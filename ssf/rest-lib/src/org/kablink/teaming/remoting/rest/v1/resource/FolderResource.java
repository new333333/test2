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
package org.kablink.teaming.remoting.rest.v1.resource;

import com.sun.jersey.spi.resource.Singleton;
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.exc.RestExceptionWrapper;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.FolderEntryBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.Operation;
import org.kablink.teaming.rest.v1.model.Permission;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.VibeRuntimeException;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import java.util.*;

@Path("/folders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Folders")
public class FolderResource extends AbstractBinderResource {

    protected String getBasePath() {
        return "/folders/";
    }

    // Read sub-folders
    @GET
    public SearchResultList<BinderBrief> getFolders(@QueryParam("id") Set<Long> ids,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        Junction criterion = Restrictions.conjunction();
        criterion.add(SearchUtils.buildFoldersCriterion());
        if (ids!=null) {
            Junction or = Restrictions.disjunction();
            for (Long id : ids) {
                or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
            }
            criterion.add(or);
        }
        Document queryDoc = buildQueryDocument("<query/>", criterion);
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(toDomainFormat(descriptionFormatStr)), resultsMap, "/folders", nextParams, offset);
        return results;
    }


    @POST
    @Path("/legacy_query")
    @Undocumented
   	public SearchResultList<BinderBrief> getFoldersViaLegacyQuery(@Context HttpServletRequest request,
                                                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                    @QueryParam("first") @DefaultValue("0") Integer offset,
   			                                        @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, SearchUtils.buildFoldersCriterion());
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(toDomainFormat(descriptionFormatStr)), resultsMap, "/folders/legacy_query", nextParams, offset);
        return results;
    }

    /**
     * Returns a list of all access-control related operations that can be performed on a folder.
     * @return A list of Operation objects
     */
    @GET
    @Path("operations")
    @Undocumented
    public SearchResultList<Operation> getOperations() {
        SearchResultList<Operation> results = new SearchResultList<Operation>();
        for (BinderModule.BinderOperation operation : BinderModule.BinderOperation.values()) {
            results.append(ResourceUtil.buildFolderOperation(operation));
        }
        for (FolderModule.FolderOperation operation : FolderModule.FolderOperation.values()) {
            if (operation.appliesToFolders()) {
                results.append(ResourceUtil.buildFolderOperation(operation));
            }
        }
        return results;
    }

    @GET
    @Path("operations/{name}")
    @Undocumented
    public Operation getOperation(@PathParam("name") String id) {
        BinderModule.BinderOperation binderOp = getBinderOperation(id);
        if (binderOp!=null) {
            return ResourceUtil.buildFolderOperation(binderOp);
        }
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null && folderOp.appliesToFolders()) {
            return ResourceUtil.buildFolderOperation(folderOp);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, id);
    }

    /**
     * Tests whether the authenticated user has permission to perform the specified operation on one or more folders.
     * @param id    The name of the operation
     * @param folderIds One or more folder IDs to test.
     * @return A list of Permission objects
     */
    @GET
    @Path("operations/{name}/permissions")
    @Undocumented
    public SearchResultList<Permission> testPermissions(@PathParam("name") String id, @QueryParam("folder")List<Long> folderIds) {
        BinderModule.BinderOperation binderOp = getBinderOperation(id);
        if (binderOp!=null) {
            return testBinderPermissions(EntityIdentifier.EntityType.folder, binderOp, folderIds);
        }
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null && folderOp.appliesToFolders()) {
            return testFolderPermissions(EntityIdentifier.EntityType.folder, folderOp, folderIds);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, "Checking permissions is not supported for the operation: " + id);
    }

    /**
     * Gets a list of child binders contained in the specified binder.
     * @param id The id of the parent binder
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("{id}/binders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
    public Response getSubBinders(@PathParam("id") long id,
                                  @QueryParam("title") String name,
                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                  @QueryParam("first") @DefaultValue("0") Integer offset,
                                  @QueryParam("count") @DefaultValue("100") Integer maxCount,
                                  @Context HttpServletRequest request) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        Date lastModified = getLibraryModifiedDate(new Long[]{id}, false, true);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<BinderBrief> subBinders = getSubBinders(id, null, name, true, offset, maxCount, "/folders/" + id + "/binders",
                nextParams, toDomainFormat(descriptionFormatStr), ifModifiedSince);
        return Response.ok(subBinders).lastModified(lastModified).build();
    }

    @GET
    @Path("{id}/children")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
    public Response getChildren(@PathParam("id") long id,
                                @QueryParam("title") String name,
                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                @QueryParam("allow_jits") @DefaultValue("true") Boolean allowJits,
                                @QueryParam("first") @DefaultValue("0") Integer offset,
                                @QueryParam("count") @DefaultValue("100") Integer maxCount,
                                @Context HttpServletRequest request) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        Date lastModified = getLibraryModifiedDate(new Long[]{id}, false, allowJits);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SearchableObject> children = getChildren(id, null, name, true, false, true, allowJits, offset, maxCount,
                "/folders/" + id + "/children", nextParams, toDomainFormat(descriptionFormatStr), null);
        return Response.ok(children).lastModified(lastModified).build();
    }

    // Read sub-folders
	@GET
	@Path("{id}/folders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
	public Response getSubFolders(@PathParam("id") long id,
                                  @QueryParam("title") String name,
                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
			@QueryParam("first") @DefaultValue("0") Integer offset,
			@QueryParam("count") @DefaultValue("100") Integer maxCount,
            @Context HttpServletRequest request) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        Date lastModified = getLibraryModifiedDate(new Long[]{id}, false, true);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<BinderBrief> subBinders = getSubBinders(id, Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER),
                name, true, offset, maxCount, "/folders/" + id + "/folders", nextParams, toDomainFormat(descriptionFormatStr), ifModifiedSince);
        return Response.ok(subBinders).lastModified(lastModified).build();
	}

    @POST
   	@Path("{id}/folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Binder createSubFolder(@PathParam("id") long id, org.kablink.teaming.rest.v1.model.Binder binder,
                                                                    @QueryParam("template") Long templateId,
                                                                    @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId!=null) {
            TemplateBinder template = getTemplateModule().getTemplate(templateId);
            if (EntityIdentifier.EntityType.folder != template.getEntityType()) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a folder template.");
            }
        }
        return createBinder(id, binder, templateId, toDomainFormat(descriptionFormatStr));
    }

	// Read entries
	@GET
	@Path("{id}/entries")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
	public SearchResultList<FolderEntryBrief> getFolderEntries(@PathParam("id") long id,
                                                               @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                               @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                            @QueryParam("first") Integer offset,
                                                            @QueryParam("count") @DefaultValue("100") Integer maxCount,
                                                            @QueryParam("file_name") String fileName) {
        Folder folder = _getFolder(id);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(0, folder.getModificationDate());
        if (fileName!=null) {
            if (folder.isLibrary()) {
                org.kablink.teaming.domain.FolderEntry folderEntry = getLibraryFolderEntryByName(folder, fileName);
                if (folderEntry!=null) {
                    results.append(ResourceUtil.buildFolderEntryBrief(folderEntry));
                }
            } else {
                throw new InternalServerErrorException(ApiErrorCode.NOT_SUPPORTED, "Searching for folder entries with file names is only supported on library folders.");
            }
        } else {
            Map<String, Object> options = new HashMap<String, Object>();
            if (offset!=null) {
                options.put(ObjectKeys.SEARCH_OFFSET, offset);
            } else {
                offset = 0;
            }
            if (maxCount!=null) {
                options.put(ObjectKeys.SEARCH_MAX_HITS, maxCount);
            }
            Map resultMap = getFolderModule().getEntries(id, options);
            results.setFirst(offset);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("parent_binder_paths", Boolean.toString(includeParentPaths));
            params.put("description_format", descriptionFormatStr);
            SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(toDomainFormat(descriptionFormatStr)), resultMap, "/folders/" + id + "/entries", params, offset);
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
		return results;
	}

    @POST
   	@Path("{id}/entries")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
   	public FolderEntry createFolderEntry(@PathParam("id") long id,
                                         @QueryParam("file_entry") @DefaultValue("false") boolean fileEntry,
                                         @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                         FolderEntry entry) throws WriteFilesException, WriteEntryDataException {
        Folder folder = _getFolder(id);

        String defId;
        if (entry.getDefinition()==null) {
            Definition def;
            if (fileEntry) {
                def = folder.getDefaultFileEntryDef();
            } else {
                def = folder.getDefaultEntryDef();
            }
            if  (def!=null) {
                defId = def.getId();
            } else {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No definition was supplied in the POST data.");
            }
        } else {
            defId = entry.getDefinition().getId();
        }
        if (defId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No definition id was supplied in the POST data.");
        }
        SimpleProfiler.start("REST_folder_createFolderEntry");
        HashMap options = new HashMap();
        populateTimestamps(options, entry);
        org.kablink.teaming.domain.FolderEntry result = getFolderModule().addEntry(id, defId, new RestModelInputData(entry), null, options);
        SimpleProfiler.stop("REST_folder_createFolderEntry");
        return ResourceUtil.buildFolderEntry(result, true, toDomainFormat(descriptionFormatStr));
    }

	@GET
	@Path("{id}/library_entities")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<SearchableObject> getLibraryEntities(@PathParam("id") long id,
                                                  @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
                                                  @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
                                                  @QueryParam("files") @DefaultValue("true") boolean includeFiles,
                                                  @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
                                                  @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                  @QueryParam("keyword") String keyword,
                                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                  @QueryParam("first") @DefaultValue("0") Integer offset,
                                                  @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        _getFolder(id);
        return searchForLibraryEntities(keyword, SearchUtils.buildSearchBinderCriterion(id, recursive), recursive, offset, maxCount,
                includeBinders, includeFolderEntries, includeReplies, includeFiles, includeParentPaths, toDomainFormat(descriptionFormatStr),
                "/folders/" + id + "/library_entities");
	}

    /**
     * Copies a file into the specified folder.
     *
     * <p>The Content-Type must be <code>application/x-www-form-urlencoded</code>.  The parameter values in the form data should
     * be URL-encoded UTF-8 strings.  For example: <code>source_id=09c1c3fb530f562401531070137b000e&file_name=H%C3%B6wdy</code></p>.
     * @param id          The ID of the target folder.
     * @param fileName    The name of the new file.
     * @param sourceId    The ID of the source file to copy.
     * @return  The new file metadata.
     */
    @POST
    @Path("{id}/library_files")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @StatusCodes({
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) The target folder does not exist."),
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) The source file does not exist."),
            @ResponseCode(code=409, condition="(FILE_EXISTS) A file with the specified name already exists in the target folder."),
    })
    public FileProperties copyFile(@PathParam("id") long id,
                                   @FormParam("file_name") String fileName,
                                   @FormParam("source_id") String sourceId,
                                   @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        Folder folder = _getFolder(id);
        if (fileName==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No file_name parameter was supplied in the POST data.");
        }
        if (sourceId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No source_id parameter was supplied in the POST data.");
        }
        FileAttachment existing = findFileAttachment(sourceId);
        DefinableEntity origEntry = existing.getOwner().getEntity();
        org.kablink.teaming.domain.FolderEntry newEntry = getFolderModule().copyEntry(origEntry.getParentBinder().getId(),
                origEntry.getId(), id, new String[] {fileName}, null);
        Set<Attachment> attachments = newEntry.getAttachments();
        for (Attachment attachment : attachments) {
            if (attachment instanceof FileAttachment) {
                return ResourceUtil.buildFileProperties((FileAttachment) attachment);
            }
        }
        return null;
    }

    /**
     * Adds a file to the specified folder.  This is the multipart form version.  The Content-Type must be <code>multipart/form-data</code>.
     * See <a>https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2</a>.
     *
     * @param id    The ID of the folder where the file is to be added.
     * @param fileName  The name of the file to create.
     * @param modDateISO8601    The desired last modified time for the new file.
     * @param expectedMd5       The MD5 checksum of the file.  If specified, the REST interface returns an error if the
     *                          MD5 checksum of the uploaded content does not match the expected value.
     * @param overwriteExisting     If a file already exists with the specified name, this specifies whether to overwrite the file (true) or fail with an error (false).
     */
    @Undocumented
    @POST
    @Path("{id}/library_files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @StatusCodes({
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) The target folder does not exist."),
            @ResponseCode(code=409, condition="(FILE_EXISTS) A file with the specified name already exists in the target folder (if overwrite_existing is false."),
    })
    public FileProperties addLibraryFileFromMultipart(@PathParam("id") long id,
                                         @QueryParam("file_name") String fileName,
                                         @QueryParam("mod_date") String modDateISO8601,
                                         @QueryParam("md5") String expectedMd5,
                                         @QueryParam("overwrite_existing") @DefaultValue("false") Boolean overwriteExisting,
                                         @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        Folder folder = _getFolder(id);
        InputStream is = getInputStreamFromMultipartFormdata(request);
        return createEntryWithAttachment(folder, fileName, modDateISO8601, expectedMd5, overwriteExisting, is);
    }

    /**
     * Adds a file to the specified folder.  The request Content-Type can be anything except <code>x-www-form-urlencoded</code>.
     * Supports <code>multipart/form-data</code> posts (see <a href="https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2">here</a>).
     * If another Content-Type is specified (<code>application/octet-stream</code>, for example), the raw bytes of the request body
     * are read and stored as the file content.
     *
     * @param id    The ID of the folder where the file is to be added.
     * @param fileName  The name of the file to create.
     * @param modDateISO8601    The desired last modified time for the new file.
     * @param expectedMd5       The MD5 checksum of the file.  If specified, the REST interface returns an error if the
     *                          MD5 checksum of the uploaded content does not match the expected value.
     * @param overwriteExisting     If a file already exists with the specified name, this specifies whether to overwrite the file (true) or fail with an error (false).
     */
    @POST
    @Path("{id}/library_files")
    @Consumes("*/*")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @StatusCodes({
            @ResponseCode(code=404, condition="(FOLDER_NOT_FOUND) The target folder does not exist."),
            @ResponseCode(code=409, condition="(FILE_EXISTS) A file with the specified name already exists in the target folder (if overwrite_existing is false."),
    })
    public FileProperties addLibraryFile(@PathParam("id") long id,
                                         @QueryParam("file_name") String fileName,
                                         @QueryParam("mod_date") String modDateISO8601,
                                         @QueryParam("md5") String expectedMd5,
                                         @QueryParam("overwrite_existing") @DefaultValue("false") Boolean overwriteExisting,
                                         @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        Folder folder = _getFolder(id);
        InputStream is = getRawInputStream(request);
        return createEntryWithAttachment(folder, fileName, modDateISO8601, expectedMd5, overwriteExisting, is);
    }

    @Override
    protected Binder _getBinder(long id) {
        return _getFolder(id);
    }

    @Override
    EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.folder;
    }
}
