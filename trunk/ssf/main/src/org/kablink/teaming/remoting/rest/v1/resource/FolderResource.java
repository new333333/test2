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
import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.FolderEntryBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.Operation;
import org.kablink.teaming.rest.v1.model.Permission;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/folders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FolderResource extends AbstractBinderResource {

	// Read sub-folders
    @GET
    public SearchResultList<BinderBrief> getFolders(@QueryParam("first") @DefaultValue("0") Integer offset,
                                                    @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Document queryDoc = buildQueryDocument("<query/>", buildFoldersCriterion());
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), resultsMap, "/folders", null, offset);
        return results;
    }


    @POST
    @Path("/legacy_query")
   	public SearchResultList<BinderBrief> getFoldersViaLegacyQuery(@Context HttpServletRequest request,
                                                    @QueryParam("first") @DefaultValue("0") Integer offset,
   			                                        @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
           String query = getRawInputStreamAsString(request);
           Document queryDoc = buildQueryDocument(query, buildFoldersCriterion());
           Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
           SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
           SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), resultsMap, "/folders/legacy_query", null, offset);
           return results;
   	}

    /**
     * Returns a list of all access-control related operations that can be performed on a folder.
     * @return A list of Operation objects
     */
    @GET
    @Path("operations")
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


	// Read sub-folders
	@GET
	@Path("{id}/folders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubFolders(@PathParam("id") long id,
			@QueryParam("first") @DefaultValue("0") Integer offset,
			@QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        return getSubBinders(id, Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER),
                offset, maxCount, "/folders/" + id + "/folders", null);
	}

    @POST
   	@Path("{id}/folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Binder createSubFolder(@PathParam("id") long id, org.kablink.teaming.rest.v1.model.Binder binder,
                                                                    @QueryParam("template") Long templateId,
                                                                    @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId!=null) {
            TemplateBinder template = getTemplateModule().getTemplate(templateId);
            if (EntityIdentifier.EntityType.folder != template.getEntityType()) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a folder template.");
            }
        }
        return createBinder(id, binder, templateId, textDescriptions);
    }

	// Read entries
	@GET
	@Path("{id}/entries")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<FolderEntryBrief> getFolderEntries(@PathParam("id") long id,
                                                            @QueryParam("first") Integer offset,
                                                            @QueryParam("count") Integer maxCount,
                                                            @QueryParam("file_name") String fileName) {
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>();
        Folder folder = _getFolder(id);
        if (fileName!=null) {
            if (folder.isLibrary()) {
                org.kablink.teaming.domain.FolderEntry folderEntry = getFolderModule().getLibraryFolderEntryByFileName(folder, fileName);
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
            SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(), resultMap, "/folders/" + id + "/entries", null, offset);
        }
		return results;
	}

    @POST
   	@Path("{id}/entries")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public FolderEntry createFolderEntry(@PathParam("id") long id,
                                         @QueryParam("file_entry") @DefaultValue("false") boolean fileEntry,
                                         @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
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
        return ResourceUtil.buildFolderEntry(result, true, textDescriptions);
    }

// Read entries
	@GET
	@Path("{id}/library_entities")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<SearchableObject> getLibraryFiles(@PathParam("id") long id,
                                                  @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("keyword") String keyword,
                                                  @QueryParam("first") @DefaultValue("0") Integer offset,
                                                  @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Map<String, String> nextParams = new HashMap<String, String>();
        nextParams.put("recursive", Boolean.toString(recursive));
        if (keyword!=null) {
            nextParams.put("keyword", keyword);
        }
        return getSubEntities(id, recursive, true, keyword, offset, maxCount, "/folders/" + id + "/library_entities", nextParams);
	}

    @Override
    protected Binder _getBinder(long id) {
        return _getFolder(id);
    }

    private org.kablink.teaming.domain.Folder _getFolder(long id) {
        try{
            org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(id);
            if (binder instanceof org.kablink.teaming.domain.Folder) {
                Folder folder = (Folder) binder;
                if (!folder.isPreDeleted()) {
                    return folder;
                }
            }
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.FOLDER_NOT_FOUND, "NOT FOUND");
    }

    @Override
    EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.folder;
    }
}
