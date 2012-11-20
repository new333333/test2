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
package org.kablink.teaming.remoting.rest.v1.resource;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.remoting.rest.v1.util.LinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.teaming.rest.v1.model.ParentBinder;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.util.api.ApiErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * User: david
 * Date: 5/22/12
 * Time: 11:02 AM
 */
@Path("/files/{id}")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FileResource extends AbstractFileResource {
    @POST
   	@Consumes(MediaType.MULTIPART_FORM_DATA)
   	public FileProperties writeFileContentById_MultipartFormData(@PathParam("id") String fileId,
   			@QueryParam("data_name") String dataName,
   			@QueryParam("mod_date") String modDateISO8601,
            @QueryParam("md5") String expectedMd5,
   			@QueryParam("force_overwrite") @DefaultValue("false") boolean forceOverwrite,
   			@QueryParam("last_version") Integer lastVersionNumber,
   			@QueryParam("last_major_version") Integer lastMajorVersionNumber,
   			@QueryParam("last_minor_version") Integer lastMinorVersionNumber,
               @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        if (!forceOverwrite && lastVersionNumber==null && (lastMajorVersionNumber==null || lastMinorVersionNumber==null)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "You must specify one of the following query parameters: force_overwrite, last_version, or last_major_version and last_minor_version.");
        }
   		FileAttachment fa = findFileAttachment(fileId);
   		DefinableEntity entity = fa.getOwner().getEntity();
   		InputStream is = getInputStreamFromMultipartFormdata(request);
   		try {
   			return updateExistingFileContent(entity, fa, dataName, modDateISO8601, expectedMd5, forceOverwrite,
                       lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber, is);
   		}
   		finally {
   			try {
   				is.close();
   			}
   			catch(IOException ignore) {}
   		}
   	}

   	@POST
   	public FileProperties writeFileContentById_Raw(@PathParam("id") String fileId,
               @QueryParam("data_name") String dataName,
               @QueryParam("mod_date") String modDateISO8601,
               @QueryParam("md5") String expectedMd5,
               @QueryParam("force_overwrite") @DefaultValue("false") boolean forceOverwrite,
               @QueryParam("last_version") Integer lastVersionNumber,
               @QueryParam("last_major_version") Integer lastMajorVersionNumber,
               @QueryParam("last_minor_version") Integer lastMinorVersionNumber,
               @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        if (!forceOverwrite && lastVersionNumber==null && (lastMajorVersionNumber==null || lastMinorVersionNumber==null)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "You must specify one of the following query parameters: force_overwrite, last_version, or last_major_version and last_minor_version.");
        }
   		FileAttachment fa = findFileAttachment(fileId);
   		DefinableEntity entity = fa.getOwner().getEntity();
   		InputStream is = getRawInputStream(request);
   		try {
            return updateExistingFileContent(entity, fa, dataName, modDateISO8601, expectedMd5, forceOverwrite,
                    lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber, is);
   		}
   		finally {
   			try {
   				is.close();
   			}
   			catch(IOException ignore) {}
   		}
   	}

   	@POST
   	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   	public FileProperties writeFileContentById_ApplicationFormUrlencoded(@PathParam("id") String fileId) {
   		throw new UnsupportedMediaTypeException("'" + MediaType.APPLICATION_FORM_URLENCODED + "' format is not supported by this method. Use '" + MediaType.MULTIPART_FORM_DATA + "' or raw type");
   	}

   	@GET
   	public Response readFileContentById(@PathParam("id") String fileId,
   			@Context HttpServletRequest request) {
   		FileAttachment fa = findFileAttachment(fileId);
   		DefinableEntity entity = fa.getOwner().getEntity();
   		return readFileContent(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), getIfModifiedSinceDate(request));
   	}

   	@GET
    @Path("thumbnail")
   	public Response readThumbnailFileContentById(@PathParam("id") String fileId,
   			@Context HttpServletRequest request) {
   		FileAttachment fa = findFileAttachment(fileId);
   		DefinableEntity entity = fa.getOwner().getEntity();
   		return readFileContent(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), getIfModifiedSinceDate(request), FileType.thumbnail);
   	}

   	@GET
    @Path("scaled")
   	public Response readScaledFileContentById(@PathParam("id") String fileId,
   			@Context HttpServletRequest request) {
   		FileAttachment fa = findFileAttachment(fileId);
   		DefinableEntity entity = fa.getOwner().getEntity();
   		return readFileContent(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), getIfModifiedSinceDate(request), FileType.scaled);
   	}

    @DELETE
    public void deleteFileContent(@PathParam("id") String fileId) throws WriteFilesException, WriteEntryDataException {
        FileAttachment fa = findFileAttachment(fileId);
        DefinableEntity entity = fa.getOwner().getEntity();
        deleteFile(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName());
    }

    @POST
    @Path("/name")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties renameFile(@PathParam("id") String fileId,
                                     @FormParam("name") String name) throws WriteFilesException, WriteEntryDataException {
        FileAttachment fa = findFileAttachment(fileId);
        if (name==null || name.length()==0) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'name' form parameter");
        }
        DefinableEntity entity = fa.getOwner().getEntity();

        Map<FileAttachment,String> renamesTo = new HashMap<FileAttachment,String>();
        renamesTo.put(fa, name);

        InputDataAccessor inputData = null;

        if(entity instanceof FolderEntry && fa.getFileItem().getName().equals(entity.getTitle())) {
            // This entry's title is identical to the current name of the file.
            // In this case, it's reasonable to change the title to match the new name as well.
            Map data = new HashMap();
            data.put("title", name);
            inputData = new MapInputData(data);
        }
        else {
            inputData = new EmptyInputData();
        }

        getFolderModule().modifyEntry(entity.getParentBinder().getId(),
                entity.getId(), inputData, null, null, renamesTo, null);

        return ResourceUtil.buildFileProperties(fa);
    }

    @POST
    @Path("/parent_folder")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties moveFile(@PathParam("id") String fileId,
                                     @FormParam("folder_id") Long newFolderId) throws WriteFilesException, WriteEntryDataException {
        FileAttachment fa = findFileAttachment(fileId);
        if (newFolderId ==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'folder_id' form parameter");
        }
        Long finalParentId = null;
        if (newFolderId.equals(ObjectKeys.MY_FILES_ID)) {
            finalParentId = newFolderId;
            newFolderId = SearchUtils.getMyFilesFolderId(this, this.getLoggedInUser().getWorkspaceId(), true);
        }
        Binder binder = getBinderModule().getBinder(newFolderId, false, true);
        if (!(binder instanceof Folder)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The binder with the specified id is not a valid folder: " + newFolderId);
        }
        DefinableEntity entity = fa.getOwner().getEntity();
        if (!(entity instanceof FolderEntry)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Only files that are contained in an entry can be moved.");
        }
        getFolderModule().moveEntry(null, entity.getId(), newFolderId, null, null);

        FileProperties fileProperties = ResourceUtil.buildFileProperties(fa);
        if (finalParentId!=null) {
            fileProperties.setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        }
        return fileProperties;
    }

    @GET
    @Path("/metadata")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties getMetaData(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        return ResourceUtil.buildFileProperties(fa);
    }

    @GET
    @Path("/major_version")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties getMajorVersion(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        FileProperties props = new FileProperties();
        props.setMajorVersion(fa.getMajorVersion());
        return props;
    }

    @POST
    @Path("/major_version")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties incrementMajorVersion(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        getBinderModule().incrementFileMajorVersion(fa.getOwner().getEntity(), fa);
        return ResourceUtil.buildFileProperties(fa);
    }

    @GET
    @Path("/versions")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SearchResultList<FileVersionProperties> getVersions(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        List<FileVersionProperties> versions = fileVersionsFromFileAttachment(fa);
        SearchResultList<FileVersionProperties> results = new SearchResultList<FileVersionProperties>();
        results.appendAll(versions);
        return results;
    }

    @GET
    @Path("/versions/current")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileVersionProperties getCurrent(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        FileVersionProperties props = new FileVersionProperties();
        props.setId(fa.getHighestVersion().getId());
        LinkUriUtil.populateFileVersionLinks(props);
        return props;
    }

    @POST
    @Path("/versions/current")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileVersionProperties setCurrent(@PathParam("id") String fileId,
                                     FileVersionProperties properties) {
        FileAttachment fa = findFileAttachment(fileId);
        VersionAttachment va = FileUtils.findVersionAttachment(properties.getId());
        if (va==null || !va.getParentAttachment().getId().equals(fa.getId())) {
            throw new NotFoundException(ApiErrorCode.FILE_VERSION_NOT_FOUND, "File version not found");
        }
        try {
            FileUtils.promoteFileVersionCurrent(va);
        } catch (UnsupportedOperationException e) {
            throw new ConflictException(ApiErrorCode.NOT_SUPPORTED, e.getLocalizedMessage());
        }
        return ResourceUtil.fileVersionFromFileAttachment(fa.getHighestVersion());
    }

}
