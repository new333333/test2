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
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.NetFolderSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.NoLdapConnectionConfigByTheIdException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.exception.UncheckedCodedException;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.resource.AbstractResource;
import org.kablink.teaming.remoting.rest.v1.util.AdminLinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.admin.KeyValuePair;
import org.kablink.teaming.rest.v1.model.admin.LdapSearchInfo;
import org.kablink.teaming.rest.v1.model.admin.LdapUserSource;
import org.kablink.teaming.rest.v1.model.admin.NetFolder;
import org.kablink.teaming.rest.v1.model.admin.NetFolderServer;
import org.kablink.teaming.util.InvokeException;
import org.kablink.teaming.util.SZoneConfig;
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
import javax.xml.bind.annotation.XmlElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/admin/net_folder_servers")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AdminNetFolderServerResource extends AbstractAdminResource {

    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public SearchResultList<NetFolderServer> getNetFolderServers(@QueryParam("include_full_details") @DefaultValue("false") boolean fullDetails) {
        SearchResultList<NetFolderServer> results = new SearchResultList<NetFolderServer>();
        List<ResourceDriverConfig> configs = getResourceDriverModule().getAllNetFolderResourceDriverConfigs();
        for (ResourceDriverConfig config : configs) {
            results.append(AdminResourceUtil.buildNetFolderServer(config, fullDetails, false));
        }
        return results;
    }

    @POST
   	@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public NetFolderServer createNetFolderServer(NetFolderServer netFolderServer) {
        ResourceDriverConfig driverConfig = toResourceDriverConfig(netFolderServer);
        driverConfig = NetFolderHelper.createNetFolderRoot(getAdminModule(), getResourceDriverModule(),
                driverConfig.getName(), driverConfig.getRootPath(), driverConfig.getDriverType(),
                driverConfig.getAccountName(), driverConfig.getPassword(), null, null, false, false,
                driverConfig.getFullSyncDirOnly(), driverConfig.getAuthenticationType(),
                driverConfig.getUseDirectoryRights(), driverConfig.getCachedRightsRefreshInterval(),
                driverConfig.getIndexContent(),
                driverConfig.isJitsEnabled(),
                new Long( driverConfig.getJitsMaxAge() ),
                new Long( driverConfig.getJitsAclMaxAge() ),
                toScheduleInfo(netFolderServer.getSyncSchedule()));
        return AdminResourceUtil.buildNetFolderServer(driverConfig, true, false);
   	}

    @GET
    @Path("{id}")
    public NetFolderServer getNetFolderServer(@PathParam("id") Long id) {
        ResourceDriverConfig resourceDriverConfig = getResourceDriverModule().getResourceDriverConfig(id);
        return AdminResourceUtil.buildNetFolderServer(resourceDriverConfig, true, false);
    }

    @PUT
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("{id}")
    public NetFolderServer modifyNetFolderServer(@PathParam("id") Long id, NetFolderServer newServer) {
        ResourceDriverConfig existingConfig = getResourceDriverModule().getResourceDriverConfig(id);
        NetFolderServer existingServer = AdminResourceUtil.buildNetFolderServer(existingConfig, false, true);
        newServer.setId(id);
        newServer.replaceNullValues(existingServer);

        ResourceDriverConfig newConfig = toResourceDriverConfig(newServer);
        newConfig = NetFolderHelper.modifyNetFolderRoot(getAdminModule(), getResourceDriverModule(), getProfileModule(), getBinderModule(),
                getWorkspaceModule(), getFolderModule(), existingConfig.getName(), newConfig.getRootPath(), newConfig.getAccountName(), newConfig.getPassword(),
                newConfig.getDriverType(), null, false, false, null, newConfig.getFullSyncDirOnly(), newConfig.getAuthenticationType(),
                newConfig.getUseDirectoryRights(), newConfig.getCachedRightsRefreshInterval(),
                newConfig.getIndexContent(),
                newConfig.isJitsEnabled(),
                new Long( newConfig.getJitsMaxAge() ),
                new Long( newConfig.getJitsAclMaxAge() ),
                toScheduleInfo(newServer.getSyncSchedule()));
        return AdminResourceUtil.buildNetFolderServer(newConfig, true, false);
    }

    @DELETE
    @Path("{id}")
    public void deleteNetFolderServer(@PathParam("id") Long id) {
        getResourceDriverModule().deleteResourceDriverConfig(id);
    }

    @GET
    @Path("{id}/net_folders")
    public SearchResultList<NetFolder> getNetFolders(@PathParam("id") Long id,
                                                     @QueryParam("include_full_details") @DefaultValue("false") boolean fullDetails,
                                                     @QueryParam("type") String type) {
        ResourceDriverConfig resourceDriverConfig = getResourceDriverModule().getResourceDriverConfig(id);
        NetFolderSelectSpec selectSpec = new NetFolderSelectSpec();
        selectSpec.setRootName(resourceDriverConfig.getName());
        NetFolder.Type nfType = toEnum(NetFolder.Type.class, "type", type);
        if (nfType==NetFolder.Type.net) {
            selectSpec.setIncludeHomeDirNetFolders(false);
            selectSpec.setIncludeNonHomeDirNetFolders(true);
        } else if (nfType == NetFolder.Type.home) {
            selectSpec.setIncludeHomeDirNetFolders(true);
            selectSpec.setIncludeNonHomeDirNetFolders(false);
        } else {
            selectSpec.setIncludeHomeDirNetFolders(true);
            selectSpec.setIncludeHomeDirNetFolders(true);
        }
        List<Folder> folderList = NetFolderHelper.getAllNetFolders2(getBinderModule(), getWorkspaceModule(), selectSpec);

        SearchResultList<NetFolder> results = new SearchResultList<NetFolder>();
        for (Folder folder : folderList) {
            results.append(AdminResourceUtil.buildNetFolder(folder, this, fullDetails));
        }
        return results;
    }

    @POST
    @Path("{id}/net_folders")
    public NetFolder createNetFolder(@PathParam("id") Long id, NetFolder netFolder) throws WriteFilesException, WriteEntryDataException {
        ResourceDriverConfig resourceDriverConfig = getResourceDriverModule().getResourceDriverConfig(id);
        return _createNetFolder(netFolder, resourceDriverConfig);
    }

    private ResourceDriverConfig toResourceDriverConfig(NetFolderServer server) {
        validateMandatoryField(server, "getName");
        validateMandatoryField(server, "getDriverType");
        validateMandatoryField(server, "getRootPath");
        validateMandatoryField(server, "getAuthenticationType");
        validateMandatoryField(server, "getAccountName");
        validateMandatoryField(server, "getPassword");

        ResourceDriverConfig model = new ResourceDriverConfig();
        model.setAccountName(server.getAccountName());
        model.setAuthenticationType(toEnum(ResourceDriverConfig.AuthenticationType.class, "auth_type", server.getAuthenticationType()));
        model.setChangeDetectionMechanism(toEnum(ResourceDriverConfig.ChangeDetectionMechanism.class, "change_detection_mechanism", server.getChangeDetectionMechanism()));
        model.setDriverType(toEnum(ResourceDriverConfig.DriverType.class, "driver_type", server.getDriverType()));
        model.setFullSyncDirOnly(server.getFullSyncDirOnly());
        model.setId(server.getId());
        model.setModifiedOn(server.getModifiedOn());
        model.setName(server.getName());
        model.setPassword(server.getPassword());
        model.setRootPath(server.getRootPath());
        model.setCachedRightsRefreshInterval(server.getCachedRightsRefreshInterval());
        model.setUseDirectoryRights(server.getUseDirectoryRights());
        model.setIndexContent(server.getIndexContent());
        if (server.getJitsEnabled()!=null) {
            model.setJitsEnabled(server.getJitsEnabled());
        }
        if (server.getJitsMaxAge()!=null) {
            model.setJitsMaxAge(server.getJitsMaxAge());
        }
        if (server.getJitsMaxACLAge()!=null) {
            model.setJitsAclMaxAge(server.getJitsMaxACLAge());
        }
        return model;
    }
}
