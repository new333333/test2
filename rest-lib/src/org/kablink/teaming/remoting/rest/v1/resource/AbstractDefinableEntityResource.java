/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.rest.v1.resource;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.BaseFileProperties;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.Permission;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: david
 * Date: 6/1/12
 * Time: 11:11 AM
 */
public abstract class AbstractDefinableEntityResource extends AbstractFileResource {
    @GET
    @Path("{id}/ancestry")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BinderBrief [] getAncestry(@PathParam("id") long id) {
        Criterion criterion = null;
        DefinableEntity entity = getDefinableEntity(id);
        if (entity instanceof Entry) {
            criterion = SearchUtils.buildEntryCriterion(id);
        } else if (entity instanceof Binder) {
            criterion = SearchUtils.buildBinderCriterion(id);
        }

        if (criterion==null) {
            throw new BadRequestException(ApiErrorCode.SERVER_ERROR, "Unsupported entity type: " + entity.getEntityTypedId());
        }
        Criteria crit = new Criteria();
        crit.add(criterion);
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, 1,
        		org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.ENTRY_ANCESTRY));
        List<Map> entries = (List<Map>)resultMap.get(ObjectKeys.SEARCH_ENTRIES);
        Map entry = entries.iterator().next();
        if (entry!=null) {
            return SearchResultBuilderUtil.getAncestryFromEntryMap(this, new BinderBriefBuilder(), id, entity.getEntityType(), entry);
        }
        return new BinderBrief[0];
    }

    @GET
    @Path("{id}/attachments")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SearchResultList<BaseFileProperties> getAttachments(@PathParam("id") long id) {
        DefinableEntity entity = getDefinableEntity(id);

        Set<Attachment> attachments = entity.getAttachments();
        SearchResultList<BaseFileProperties> props = new SearchResultList<BaseFileProperties>();
        for (Attachment attachment : attachments) {
            if (attachment instanceof VersionAttachment) {
                props.append(ResourceUtil.fileVersionFromFileAttachment((VersionAttachment) attachment));
            } else if (attachment instanceof FileAttachment) {
                props.append(ResourceUtil.buildFileProperties((FileAttachment) attachment));
            }
        }
		return props;
    }

    @POST
    @Path("{id}/attachments")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties postAttachment_Multipart(@PathParam("id") long id,
                               @QueryParam("file_name") String fileName,
                               @QueryParam("data_name") String dataName,
       			               @QueryParam("mod_date") String modDateISO8601,
       			               @QueryParam("md5") String expectedMd5,
                               @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        //TODO: make sure a file with that name doesn't exist
        InputStream is = getInputStreamFromMultipartFormdata(request);
        try {
            return writeNewFileContent(EntityIdentifier.EntityType.folderEntry, id, fileName, dataName, modDateISO8601, expectedMd5, is);
        }
        finally {
            try {
                is.close();
            }
            catch(IOException ignore) {}
        }
    }

    @POST
    @Path("{id}/attachments")
    @Consumes("*/*")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties postAttachment_Raw(@PathParam("id") long id,
                               @QueryParam("file_name") String fileName,
                               @QueryParam("data_name") String dataName,
       			               @QueryParam("mod_date") String modDateISO8601,
                               @QueryParam("md5") String expectedMd5,
                               @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        //TODO: make sure a file with that name doesn't exist
        InputStream is = getRawInputStream(request);
        try {
            return writeNewFileContent(EntityIdentifier.EntityType.folderEntry, id, fileName, dataName, modDateISO8601, expectedMd5, is);
        }
        finally {
            try {
                is.close();
            }
            catch(IOException ignore) {}
        }
    }

    @POST
    @Path("{id}/attachments")
   	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   	public FileProperties postAttachment_ApplicationFormUrlencoded(@PathParam("id") String id) {
   		throw new UnsupportedMediaTypeException("'" + MediaType.APPLICATION_FORM_URLENCODED + "' format is not supported by this method. Use '" + MediaType.MULTIPART_FORM_DATA + "' or raw type");
   	}

    protected DefinableEntity getDefinableEntity(Long id) {
        return findDefinableEntity(_getEntityType(), id);
    }

    abstract EntityIdentifier.EntityType _getEntityType();

    protected SearchResultList<Permission> testBinderPermissions(EntityIdentifier.EntityType entityType, BinderModule.BinderOperation operation, List<Long> binderIds) {
        SearchResultList<Permission> permissions = new SearchResultList<Permission>();
        for(Long binderId : binderIds) {
            Permission permission = new Permission(ResourceUtil.buildEntityId(entityType, binderId), null);
            try {
                // Do not use BinderModule.getBinder() method to load the folder, since it will
                // fail if the caller doesn't already have the appropriate right to load it.
                Binder binder = getBinderModule().getBinderWithoutAccessCheck(binderId);
                if (entityType==binder.getEntityType()) {
                    permission.setPermission(getBinderModule().testAccess(binder, operation));
                } else {
                    permission.setPermission(false);
                    permission.setFound(false);
                }
            } catch(NoBinderByTheIdException e) {
                // The specified folder does not exist. Instead of throwing an exception (and
                // aborting this operation all together), simply set the result to false for
                // this folder, and move on to the next folder.
                permission.setPermission(false);
                permission.setFound(false);
            }
            permissions.append(permission);
        }
        return permissions;
    }

    protected SearchResultList<Permission> testFolderPermissions(EntityIdentifier.EntityType entityType, FolderModule.FolderOperation operation, List<Long> folderIds) {
        SearchResultList<Permission> permissions = new SearchResultList<Permission>();
        for(Long folderId : folderIds) {
            Permission permission = new Permission(ResourceUtil.buildEntityId(entityType, folderId), null);
            try {
                // Do not use FolderModule.getFolder() method to load the folder, since it will
                // fail if the caller doesn't already have the appropriate right to load it.
                if (entityType==EntityIdentifier.EntityType.folder) {
                    Folder folder = getFolderModule().getFolderWithoutAccessCheck(folderId);
                    permission.setPermission(getFolderModule().testAccess(folder, operation));
                } else if (entityType==EntityIdentifier.EntityType.folderEntry) {
                    FolderEntry folderEntry = getFolderModule().getEntryWithoutAccessCheck(null, folderId);
                    permission.setPermission(getFolderModule().testAccess(folderEntry, operation));
                }
            } catch(NoObjectByTheIdException e) {
                // The specified folder does not exist. Instead of throwing an exception (and
                // aborting this operation all together), simply set the result to false for
                // this folder, and move on to the next folder.
                permission.setPermission(false);
                permission.setFound(false);
            }
            permissions.append(permission);
        }
        return permissions;
    }

    protected BinderModule.BinderOperation getBinderOperation(String name) {
        try {
            return BinderModule.BinderOperation.valueOf(name);
        } catch (IllegalArgumentException e) {
        }
        return null;
    }

    protected FolderModule.FolderOperation getFolderOperation(String name) {
        try {
            return FolderModule.FolderOperation.valueOf(name);
        } catch (IllegalArgumentException e) {
        }
        return null;
    }
}
