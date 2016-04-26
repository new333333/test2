/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 *
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 *
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 *
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 *
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.remoting.rest.v1.resource.admin;

import com.sun.jersey.spi.resource.Singleton;
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import com.webcohesion.enunciate.metadata.rs.ResourceLabel;
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import org.kablink.teaming.dao.util.NetFolderSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderState;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.resource.AbstractResource;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.admin.NetFolder;
import org.kablink.teaming.rest.v1.model.admin.NetFolderServer;
import org.kablink.teaming.rest.v1.model.admin.NetFolderSyncStatus;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Resources for managing Net Folders
 */
@Path("/admin/net_folders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Net Folders")
@ResourceLabel("Net Folder Resource")
public class AdminNetFolderResource extends AbstractAdminResource {

    /**
     * Lists Net Folders.
     * <p>By default, all Net Folders are returned.  To only get home directory Net Folders use the type=home query parameter.
     * To exclude home directory Net Folders, use the type=net query parameter.</p>
     * @param fullDetails  If true, the NetFolder objects will include the sync_schedule and assigned_rights fields.  Otherwise, those fields are not included.
     * @param type  Can be <code>net</code> or <code>home</code>.
     * @return A SearchResultList of NetFolder objects.
     */
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public SearchResultList<NetFolder> getNetFolders(@QueryParam("include_full_details") @DefaultValue("false") boolean fullDetails,
                                                     @QueryParam("type") String type) {
        NetFolderSelectSpec selectSpec = new NetFolderSelectSpec();
        NetFolder.Type nfType = toEnum(NetFolder.Type.class, "type", type);
        if (nfType==NetFolder.Type.net) {
            selectSpec.setIncludeHomeDirNetFolders(false);
            selectSpec.setIncludeNonHomeDirNetFolders(true);
        } else if (nfType == NetFolder.Type.home) {
            selectSpec.setIncludeHomeDirNetFolders(true);
            selectSpec.setIncludeNonHomeDirNetFolders(false);
        } else {
            selectSpec.setIncludeHomeDirNetFolders(true);
            selectSpec.setIncludeNonHomeDirNetFolders(true);
        }
        List<NetFolderConfig> folderList = NetFolderHelper.getAllNetFolders2(getBinderModule(), getWorkspaceModule(), selectSpec);

        SearchResultList<NetFolder> results = new SearchResultList<NetFolder>();
        for (NetFolderConfig folder : folderList) {
            results.append(AdminResourceUtil.buildNetFolder(folder, this, fullDetails));
        }
        return results;
    }

    /**
     * Creates a new Net Folder.
     * <p>
     * The following Net Folder fields are mandatory in the request body:
     * <ul>
     *     <li>name</li>
     *     <li>server</li>
     * </ul>
     * </p>
     * @param netFolder The Net Folder object to create.
     * @return  The full newly-created Net Folder object.
     */
    @POST
   	@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @StatusCodes({
            @ResponseCode(code=404, condition="(NET_FOLDER_SERVER_NOT_FOUND) No Net Folder Server exists with the specified ID."),
            @ResponseCode(code=409, condition="(TITLE_EXISTS) A Net Folder already exists with the specified name.")
    })
   	public NetFolder createNetFolder(NetFolder netFolder) throws WriteFilesException, WriteEntryDataException {
        validateMandatoryField(netFolder, "getServer", "getId");
        ResourceDriverConfig driverConfig = getResourceDriverModule().getResourceDriverConfig(netFolder.getServer().getId());
        return _createNetFolder(netFolder, driverConfig);
   	}

    /**
     * Retrieves a Net Folder.
     * @param id
     * @return  The Net Folder with the specified ID.
     */
    @GET
    @Path("{id}")
    @StatusCodes({
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) No Net Folder exists with the specified ID."),
    })
    public NetFolder getNetFolder(@PathParam("id") Long id) {
        Folder folder = lookupNetFolder(id);
        return AdminResourceUtil.buildNetFolder(folder.getNetFolderConfig(), this, true);
    }

    /**
     * Updates a Net Folder.  Only the fields that are included in the request body are updated.
     * @param id
     * @param netFolder
     * @return  The updated Net Folder.
     */
    @PUT
    @Path("{id}")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @StatusCodes({
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) No Net Folder exists with the specified ID."),
            @ResponseCode(code=404, condition="(NET_FOLDER_SERVER_NOT_FOUND) No Net Folder Server exists with the specified ID."),
    })
    public NetFolder updateNetFolder(@PathParam("id") Long id, NetFolder netFolder) throws WriteFilesException, WriteEntryDataException {
        Folder folder = lookupNetFolder(id);
        netFolder.setId(id);
        NetFolder existing = AdminResourceUtil.buildNetFolder(folder.getNetFolderConfig(), this, false);
        netFolder.replaceNullValues(existing);
        ResourceDriverConfig driverConfig = getResourceDriverModule().getResourceDriverConfig(netFolder.getServer().getId());
        return _modifyNetFolder(netFolder, driverConfig);
    }

    /**
     * Deletes the Net Folder.
     * @param id
     */
    @DELETE
    @Path("{id}")
    @StatusCodes({
            @ResponseCode(code=204, condition="The Net Folder is deleted successfully"),
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) No Net Folder exists with the specified ID."),
    })
    public void deleteNetFolderServer(@PathParam("id") Long id) {
        Folder folder = lookupNetFolder(id);
        NetFolderHelper.deleteNetFolder(getNetFolderModule(), id, false);
    }

    /**
     * Retrieves the Net Folder sync status.
     * @param id
     * @return The sync status.
     */
    @GET
    @Path("{id}/sync")
    @StatusCodes({
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) No Net Folder exists with the specified ID."),
    })
    public NetFolderSyncStatus getSyncStatus(@PathParam("id") Long id) {
        Folder netFolder = lookupNetFolder(id);
        BinderState binderState = getBinderState(id);
        if ( binderState != null ) {
            BinderState.FullSyncStats syncStats;
            syncStats = binderState.getFullSyncStats();
            if ( syncStats != null ) {
                return AdminResourceUtil.buildNetFolderSyncStatus(syncStats);
            }
        }
        NetFolderSyncStatus status = new NetFolderSyncStatus();
        status.setStatus("none");
        return status;
    }

    /**
     * Kick off a sync of the Net Folder.
     * @param id
     * @param waitForCompletion     If true, the HTTP request will block until the sync completes.
     * @return  The Net Folder sync status.
     */
    @POST
    @Path("{id}/sync")
    @Consumes({"*/*"})
    @StatusCodes({
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) No Net Folder exists with the specified ID."),
    })
    public NetFolderSyncStatus syncNetFolder(@PathParam("id") Long id,
                                             @QueryParam("wait") @DefaultValue("false") boolean waitForCompletion)
            throws InterruptedException {
        Folder netFolder = lookupNetFolder(id);
        getFolderModule().enqueueFullSynchronize(netFolder.getId());
        if (waitForCompletion) {
            waitForSyncToComplete(id);
        }
        return getSyncStatus(id);
    }

    /**
     * Cancel the in-progess sync of the Net Folder.
     *
     * <p>By default, the in-progress sync is notified and the HTTP request completes without waiting for the sync job to exit.</p>
     *
     * @param id
     * @param waitForCompletion If true, the HTTP request will block until the in-progress sync cancellation is complete.
     * @return The Net Folder sync status.
     */
    @DELETE
    @Path("{id}/sync")
    @Consumes({"*/*"})
    @StatusCodes({
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) No Net Folder exists with the specified ID."),
    })
    public NetFolderSyncStatus cancelNetFolderSync(@PathParam("id") Long id,
                                                   @QueryParam("wait") @DefaultValue("false") boolean waitForCompletion) throws InterruptedException {
        Folder netFolder = lookupNetFolder(id);
        getFolderModule().requestNetFolderFullSyncStop(netFolder.getId());
        if (waitForCompletion) {
            waitForSyncToComplete(id);
        }
        return getSyncStatus(id);
    }

    private void waitForSyncToComplete(Long id) throws InterruptedException {
        boolean waitForCompletion = true;
        while (waitForCompletion) {
            BinderState state = getBinderState(id);
            if (state!=null) {
                waitForCompletion = !state.getFullSyncStats().getStatus().isCompleted();
                if (waitForCompletion) {
                    Thread.sleep(2000);
                }
            } else {
                waitForCompletion = false;
            }
        }
    }

}
