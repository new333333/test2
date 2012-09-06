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
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Workspace;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/workspaces")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class WorkspaceResource extends AbstractBinderResource {

    protected String getBasePath() {
        return "/workspaces/";
    }

    @GET
    public SearchResultList<BinderBrief> getWorkspaces(@QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                       @QueryParam("first") @DefaultValue("0") Integer offset,
                                                       @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Document queryDoc = buildQueryDocument("<query/>", buildWorkspacesCriterion());
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        Map<String, String> nextParams = new HashMap<String, String>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(textDescriptions), resultsMap, "/workspaces", nextParams, offset);
        return results;
    }

    @POST
    @Path("/legacy_query")
   	public SearchResultList<BinderBrief> getWorkspacesViaLegacyQuery(@Context HttpServletRequest request,
                                                                     @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                                     @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                     @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, buildWorkspacesCriterion());
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        Map<String, String> nextParams = new HashMap<String, String>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(textDescriptions), resultsMap, "/workspaces/legacy_query", nextParams, offset);
        return results;
    }

    /**
     * Gets a list of child binders contained in the specified binder.
     * @param id The id of the parent binder
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("{id}/binders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SearchResultList<BinderBrief> getSubBinders(@PathParam("id") long id,
                                                       @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                       @QueryParam("first") @DefaultValue("0") Integer offset,
                                                       @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Map<String, String> nextParams = new HashMap<String, String>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        return getSubBinders(id, null, offset, maxCount, "/workspaces/" + id + "/binders", nextParams, textDescriptions);
    }

    @GET
    @Path("/{id}/workspaces/{title}")
    public Workspace getWorkspace(@PathParam("id") long parentId, @PathParam("title") String name) {
        org.kablink.teaming.domain.Workspace parent = _getWorkspace(parentId);
        Binder binder = getBinderModule().getBinderByParentAndTitle(parentId, name);
        if (binder instanceof org.kablink.teaming.domain.Workspace) {
            return (Workspace) ResourceUtil.buildBinder(binder, true, false);
        }
        throw new NoWorkspaceByTheNameException(name);
    }

	// Read subworkspaces
	@GET
	@Path("{id}/workspaces")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubWorkspaces(@PathParam("id") long id,
                                                          @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
			@QueryParam("first") @DefaultValue("0") Integer offset,
			@QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Map<String, String> nextParams = new HashMap<String, String>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        return getSubBinders(id, Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_WORKSPACE),
                offset, maxCount, "/workspaces/" + id + "/workspaces", nextParams, textDescriptions);
	}

    @POST
   	@Path("{id}/workspaces")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Workspace createSubWorkspace(@PathParam("id") long id, org.kablink.teaming.rest.v1.model.Workspace workspace,
                                                                          @QueryParam("template") Long templateId,
                                                                          @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId!=null) {
            TemplateBinder template = getTemplateModule().getTemplate(templateId);
            if (EntityIdentifier.EntityType.workspace != template.getEntityType()) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a workspace template.");
            }
        }
        return (Workspace) createBinder(id, workspace, templateId, textDescriptions);
    }

    // Read subfolders
	@GET
	@Path("{id}/folders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubFolders(@PathParam("id") long id,
                                                       @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
			@QueryParam("first") @DefaultValue("0") int offset,
			@QueryParam("count") @DefaultValue("-1") int maxCount) {
        Map<String, String> nextParams = new HashMap<String, String>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        return getSubBinders(id, Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER),
                offset, maxCount, "/workspaces/" + id + "/folders", nextParams, textDescriptions);
	}

    @POST
   	@Path("{id}/folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Folder createSubFolder(@PathParam("id") long id,
                                                                    org.kablink.teaming.rest.v1.model.Binder binder,
                                                                    @QueryParam("template") Long templateId,
                                                                    @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing required 'template' query string parameter.");
        }
        TemplateBinder template = getTemplateModule().getTemplate(templateId);
        if (EntityIdentifier.EntityType.folder != template.getEntityType()) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a folder template.");
        }
        return (Folder) createBinder(id, binder, templateId, textDescriptions);
    }

    @GET
    @Path("/{id}/folders/{title}")
    public Folder getFolderByTitle(@PathParam("id") long parentId, @PathParam("title") String name) {
        _getWorkspace(parentId);
        Binder binder = getBinderModule().getBinderByParentAndTitle(parentId, name);
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            return (Folder) ResourceUtil.buildBinder(binder, true, false);
        }
        throw new NoWorkspaceByTheNameException(name);
    }

    // Read entries
	@GET
	@Path("{id}/library_entities")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<SearchableObject> getLibraryFiles(@PathParam("id") long id,
                                                  @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("keyword") String keyword,
                                                  @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                  @QueryParam("first") @DefaultValue("0") Integer offset,
                                                  @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Map<String, String> nextParams = new HashMap<String, String>();
        nextParams.put("recursive", Boolean.toString(recursive));
        if (keyword!=null) {
            nextParams.put("keyword", keyword);
        }
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        return getSubEntities(id, recursive, true, keyword, offset, maxCount, "/workspaces/" + id + "/library_entities", nextParams, textDescriptions);
	}

    @GET
    @Path("{id}/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getRecentActivity(@PathParam("id") Long id,
                                                                @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                @QueryParam("first") @DefaultValue("0") Integer offset,
                @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        List<String> workspaces = new ArrayList<String>();
        workspaces.add(id.toString());
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, workspaces, null, null, true, Constants.LASTACTIVITY_FIELD);
        Map resultsMap = getBinderModule().executeSearchQuery(criteria, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset);
        Map<String, String> nextParams = new HashMap<String, String>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(textDescriptions), resultsMap,
                "/workspaces/" + id + "/recent_activity", nextParams, offset);
        return results;
    }

    @Override
    protected Binder _getBinder(long id) {
        return _getWorkspace(id);
    }

    private org.kablink.teaming.domain.Workspace _getWorkspace(long id) {
        try{
            org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(id);
            if (binder instanceof org.kablink.teaming.domain.Workspace) {
                org.kablink.teaming.domain.Workspace workspace = (org.kablink.teaming.domain.Workspace) binder;
                if (!workspace.isPreDeleted()) {
                    return workspace;
                }
            }
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.WORKSPACE_NOT_FOUND, "NOT FOUND");
    }

    @Override
    EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.workspace;
    }
}
