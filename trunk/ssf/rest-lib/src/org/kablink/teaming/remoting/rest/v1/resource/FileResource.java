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
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.*;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.remoting.rest.v1.util.FilePropertiesBuilder;
import org.kablink.teaming.remoting.rest.v1.util.LinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.teaming.rest.v1.model.ParentBinder;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

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
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * File-related endpoints.
 */
@Path("/files")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Files")
public class FileResource extends AbstractFileResource {

    /**
     * Lists files.
     * @param ids   A file ID to search for.  Can be specified multiple times (<code>id=0909938984230942380432&id=971239483127492384732914</code>
     * @param fileName  List files matching this file name pattern.  The first characted in the pattern cannot be * or ?.
     * @param includeParentPaths    Whether to include the parent folder path with each result.
     * @param onlyLibraryFiles  Limit the search to files in library folder.  A library folder is one that requires unique file names.
     * @param offset    The first index to return (for paging).
     * @param maxCount  The maximum number of results to return.
     * @return  A SearchResultList of FileProperties objects.
     */
    @GET
    public SearchResultList<FileProperties> getFiles(
            @QueryParam("id") Set<String> ids,
            @QueryParam("file_name") String fileName,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("library") @DefaultValue("false") boolean onlyLibraryFiles,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("library", Boolean.toString(onlyLibraryFiles));

        Junction criterion = Restrictions.conjunction()
            .add(SearchUtils.buildEntriesCriterion());

        if (ids!=null && ids.size()>0) {
            Junction or = Restrictions.disjunction();
            for (String id : ids) {
                or.add(Restrictions.eq(Constants.FILE_ID_FIELD, id));
            }
            criterion.add(or);
            nextParams.put("id", ids);
        }
        if (onlyLibraryFiles) {
            criterion.add(SearchUtils.buildLibraryCriterion(onlyLibraryFiles, Boolean.FALSE));
        }
        if (fileName!=null) {
            nextParams.put("file_name", fileName);
            criterion.add(SearchUtils.buildFileNameCriterion(fileName));
        }
        Criteria crit = new Criteria();
        crit.add(criterion);
        Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new FilePropertiesBuilder(this), resultsMap, "/files", nextParams, offset);
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }

        return results;

    }

    /**
     * Upload a new version of a file.  The request Content-Type can be anything except <code>x-www-form-urlencoded</code>.
     * Supports <code>multipart/form-data</code> posts (see <a href="https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2">here</a>).
     * If another Content-Type is specified (<code>application/octet-stream</code>, for example), the raw bytes of the request body
     * are read and stored as the file content.
     *
     * <p>You must specify either <code>force_overwrite</code> or <code>last_version</code>.</p>
     *
     * @param fileId    The ID of the file to update.
     * @param dataName  Reserved.
     * @param modDateISO8601    The desired last modified time for the file.
     * @param expectedMd5       The MD5 checksum of the file.  If specified, the REST interface returns an error if the
     *                          MD5 checksum of the uploaded content does not match the expected value.
     * @param forceOverwrite    Overwrite the current version of the file, regardless of the current file version.
     * @param lastVersionNumber Only update the file if the current version matches this value.
     * @param lastMajorVersionNumber    Reserved.
     * @param lastMinorVersionNumber    Reserved.
     * @return  The updated FileProperties resource.
     */
    @Undocumented
    @POST
    @Path("{id}")
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
        catch (NoFolderEntryByTheIdException e) {
            // updateExistingFileContent triggers JITS which could result in a NoFolderEntryByTheIdException.  Translate
            // that into a NoFileByTheIdException.
            throw new NoFileByTheIdException(fileId);
        }
   		finally {
   			try {
   				is.close();
   			}
   			catch(IOException ignore) {}
   		}
   	}

    /**
     * Upload a new version of a file.  The request Content-Type can be anything except <code>x-www-form-urlencoded</code>.
     * Supports <code>multipart/form-data</code> posts (see <a href="https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2">here</a>).
     * If another Content-Type is specified (<code>application/octet-stream</code>, for example), the raw bytes of the request body
     * are read and stored as the file content.
     *
     * <p>You must specify either <code>force_overwrite</code> or <code>last_version</code>.</p>
     *
     * @param fileId    The ID of the file to update.
     * @param dataName  Reserved.
     * @param modDateISO8601    The desired last modified time for the file.
     * @param expectedMd5       The MD5 checksum of the file.  If specified, the REST interface returns an error if the
     *                          MD5 checksum of the uploaded content does not match the expected value.
     * @param forceOverwrite    Overwrite the current version of the file, regardless of the current file version.
     * @param lastVersionNumber Only update the file if the current version matches this value.
     * @param lastMajorVersionNumber    Reserved.
     * @param lastMinorVersionNumber    Reserved.
     * @return  The updated FileProperties resource.
     */
   	@POST
    @Path("{id}")
    @StatusCodes({
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) No file exists with the specified ID."),
            @ResponseCode(code=409, condition="(FILE_VERSION_CONFLICT) The current file version does not match the last_version value."),
    })
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
        catch (NoFolderEntryByTheIdException e) {
            // updateExistingFileContent triggers JITS which could result in a NoFolderEntryByTheIdException.  Translate
            // that into a NoFileByTheIdException.
            throw new NoFileByTheIdException(fileId);
        }
   		finally {
   			try {
   				is.close();
   			}
   			catch(IOException ignore) {}
   		}
   	}

    @Undocumented
   	@POST
    @Path("{id}")
   	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @StatusCodes({
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) No file exists with the specified ID."),
    })
   	public FileProperties writeFileContentById_ApplicationFormUrlencoded(@PathParam("id") String fileId) {
   		throw new UnsupportedMediaTypeException("'" + MediaType.APPLICATION_FORM_URLENCODED + "' format is not supported by this method. Use '" + MediaType.MULTIPART_FORM_DATA + "' or raw type");
   	}

    /**
     * Download the specified file.
     * @param fileId    The file to download.
     * @return  The content of the file.
     */
   	@GET
    @Path("{id}")
    @StatusCodes({
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) No file exists with the specified ID."),
    })
   	public Response readFileContentById(@PathParam("id") String fileId,
   			@Context HttpServletRequest request) {
   		FileAttachment fa = findFileAttachment(fileId);
   		DefinableEntity entity = fa.getOwner().getEntity();
   		return readFileContent(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), getIfModifiedSinceDate(request));
   	}

    /**
     * Download a square thumbnail image of the specified file.
     * @param fileId    The ID of the file.
     */
   	@GET
    @Path("{id}/thumbnail")
    @StatusCodes({
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) No file exists with the specified ID."),
    })
   	public Response readThumbnailFileContentById(@PathParam("id") String fileId,
   			@Context HttpServletRequest request) {
   		FileAttachment fa = findFileAttachment(fileId);
   		DefinableEntity entity = fa.getOwner().getEntity();
   		return readFileContent(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), getIfModifiedSinceDate(request), FileType.thumbnail);
   	}

    /**
     * Download a scaled image of the specified file.
     * @param fileId    The ID of the file.
     */
   	@GET
    @Path("{id}/scaled")
    @StatusCodes({
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) No file exists with the specified ID."),
    })
   	public Response readScaledFileContentById(@PathParam("id") String fileId,
   			@Context HttpServletRequest request) {
   		FileAttachment fa = findFileAttachment(fileId);
   		DefinableEntity entity = fa.getOwner().getEntity();
   		return readFileContent(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), getIfModifiedSinceDate(request), FileType.scaled);
   	}

    /**
     * Delete the specified file.
     * @param fileId    The ID of the file.
     * @param purge     Whether to purge the file (delete permanently) or move to the trash.  Only applies to files in
     *                  personal storage.  Files in external storage are always purged.
     * @param lastVersionNumber Only delete the file if the current version matches this value.
     */
    @DELETE
    @Path("{id}")
    @StatusCodes({
            @ResponseCode(code=204, condition="The file is deleted successfully."),
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) No file exists with the specified ID."),
            @ResponseCode(code=409, condition="(FILE_VERSION_CONFLICT) The current file version does not match the last_version value."),
    })
    public void deleteFileContent(@PathParam("id") String fileId,
                                  @QueryParam("purge") @DefaultValue("false") boolean purge,
                                  @QueryParam("version") Integer lastVersionNumber) throws WriteFilesException, WriteEntryDataException {
        FileAttachment fa = findFileAttachment(fileId);
        if (lastVersionNumber!=null && !isFileVersionCorrect(fa, lastVersionNumber, null, null)) {
            throw new ConflictException(ApiErrorCode.FILE_VERSION_CONFLICT, "Specified version number does not reflect the current state of the file",
                    ResourceUtil.buildFileProperties(fa));
        }
        DefinableEntity entity = fa.getOwner().getEntity();
        if (entity instanceof FolderEntry) {
            FolderUtils.deleteFileInFolderEntry(this, (FolderEntry)entity, fa, !purge);
        } else if (entity instanceof Binder) {
            deleteFile(entity.getEntityType(), entity.getId(), fa.getFileItem().getName());
        } else {
            deleteFile(entity.getEntityType(), entity.getId(), fa.getFileItem().getName());
        }
    }

    /**
     * Get the access that the authenticated user has to the specified file.
     * @param fileId    The ID of the file.
     * @return  An Access resource.
     */
    @GET
    @Path("{id}/access")
    public Access getAccessRole(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        DefinableEntity entity = fa.getOwner().getEntity();
        if (entity instanceof FolderEntry) {
            return getAccessRole((FolderEntry) entity);
        }
        Access role = new Access();
        role.setRole(ShareItem.Role.NONE.name());
        return role;
    }

    /**
     * Rename the specified file.  The Content-Type must be <code>application/x-www-form-urlencoded</code>.  The value of the file_name
     * form parameter in the request body should be a UTF-8 string that has been URL encoded.
     * @param fileId    The ID of the file.
     * @param name  The new name of the file.
     * @return  The updated FileProperties resource.
     */
    @POST
    @Path("{id}/name")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @StatusCodes({
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) No file exists with the specified ID."),
            @ResponseCode(code=409, condition="(FILE_EXISTS) A file with the specified name already exists."),
    })
    public FileProperties renameFile(@PathParam("id") String fileId,
                                     @FormParam("name") String name) throws WriteFilesException, WriteEntryDataException {
        FileAttachment fa = findFileAttachment(fileId);
        if (name==null || name.length()==0) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'name' form parameter");
        }
        name = Normalizer.normalize(name, Normalizer.Form.NFC);
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

    /**
     * Move the specified file to another folder.  The Content-Type must be <code>application/x-www-form-urlencoded</code>.
     * The value of the file_name form parameter in the request body should be a UTF-8 string that has been URL encoded.
     * @param fileId    The ID of the file.
     * @param newFolderId The ID of the target folder.
     * @param name  The new name of the file (optional).
     * @return  The updated FileProperties resource.
     */
    @POST
    @Path("{id}/parent_folder")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @StatusCodes({
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) No file exists with the specified ID."),
            @ResponseCode(code=409, condition="(FILE_EXISTS) A file with the same name already exists in the target folder."),
    })
    public FileProperties moveFile(@PathParam("id") String fileId,
                                     @FormParam("folder_id") Long newFolderId,
                                     @FormParam("name") String name) throws WriteFilesException, WriteEntryDataException {
        FileAttachment fa = findFileAttachment(fileId);
        if (newFolderId ==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'folder_id' form parameter");
        }
        Long finalParentId = null;
        if (newFolderId.equals(ObjectKeys.MY_FILES_ID)) {
            finalParentId = newFolderId;
            if (SearchUtils.useHomeAsMyFiles(this)) {
                newFolderId = SearchUtils.getHomeFolderId(this);
            } else {
                newFolderId = SearchUtils.getMyFilesFolderId(this, this.getLoggedInUser(), true);
            }
        }
        Binder binder = getBinderModule().getBinder(newFolderId, false, true);
        if (!(binder instanceof Folder)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The binder with the specified id is not a valid folder: " + newFolderId);
        }
        DefinableEntity entity = fa.getOwner().getEntity();
        if (!(entity instanceof FolderEntry)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Only files that are contained in an entry can be moved.");
        }

        FileAttachment newAttachment;
        if (binder.isLibrary()) {
            if (name!=null) {
                name = Normalizer.normalize(name, Normalizer.Form.NFC);
            }
            newAttachment = FolderUtils.moveLibraryFile(fa, (Folder) binder, name);
        } else {
            FolderEntry newEntry = getFolderModule().moveEntry(null, entity.getId(), newFolderId, null, null);
            newAttachment = newEntry.getFileAttachment(fa.getFileItem().getName());
        }

        FileProperties fileProperties = ResourceUtil.buildFileProperties(newAttachment);
        if (finalParentId!=null) {
            fileProperties.setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        }
        return fileProperties;
    }

    /**
     * Get the file metadata.
     * @param fileId    The ID of the file.
     * @return  The FileProperties resource.
     */
    @GET
    @Path("{id}/metadata")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties getMetaData(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        return ResourceUtil.buildFileProperties(fa);
    }

    /**
     * Request that the server synchronize the metadata of the specified file.  This means the server will check the file content in the
     * file's storage area and update the metadata if it is out of date.
     * <p>The Content-Type must be <code>application/x-www-form-urlencoded</code>.
     * The value of the file_name form parameter in the request body should be a UTF-8 string that has been URL encoded.</p>
     * @param fileId    The ID of the file.
     * @param sync  If true, the file metadata will be synced with the file in the storage area.
     * @return  The updated FileProperties resource.
     */
    @POST
    @Path("{id}/metadata")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public FileProperties synchronize(@PathParam("id") String fileId,
                                      @FormParam("synchronize") Boolean sync,
                                      @FormParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        FileAttachment fa = findFileAttachment(fileId);
        DefinableEntity entity = fa.getOwner().getEntity();
        if (Boolean.TRUE.equals(sync)) {
            if (entity instanceof FolderEntry) {
                FolderEntry entry = synchronizeFolderEntry((FolderEntry) entity, false);
                fa = (FileAttachment) entry.getAttachment(fileId);
                return ResourceUtil.buildFileProperties(fa);
            }
        }
        return null;
    }

    @Undocumented
    @GET
    @Path("{id}/major_version")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties getMajorVersion(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        FileProperties props = new FileProperties();
        props.setMajorVersion(fa.getMajorVersion());
        return props;
    }

    @POST
    @Path("{id}/major_version")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
    public FileProperties incrementMajorVersion(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        getBinderModule().incrementFileMajorVersion(fa.getOwner().getEntity(), fa);
        return ResourceUtil.buildFileProperties(fa);
    }

    @GET
    @Path("{id}/versions")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
    public SearchResultList<FileVersionProperties> getVersions(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        List<FileVersionProperties> versions = fileVersionsFromFileAttachment(fa);
        SearchResultList<FileVersionProperties> results = new SearchResultList<FileVersionProperties>();
        results.appendAll(versions);
        return results;
    }

    @GET
    @Path("{id}/versions/current")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
    public FileVersionProperties getCurrent(@PathParam("id") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        FileVersionProperties props = new FileVersionProperties();
        props.setId(fa.getHighestVersion().getId());
        LinkUriUtil.populateFileVersionLinks(props);
        return props;
    }

    @POST
    @Path("{id}/versions/current")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
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
