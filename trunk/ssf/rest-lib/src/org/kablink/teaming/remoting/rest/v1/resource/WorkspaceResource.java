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
import org.dom4j.Document;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Workspace;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction;
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
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/workspaces")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Workspaces")
public class WorkspaceResource extends AbstractBinderResource {

    protected String getBasePath() {
        return "/workspaces/";
    }

    /**
     * Get workspaces by ID.
     *
     * @param ids   The ID of a folder.  Can be specified multiple times.
     * @param descriptionFormatStr The desired format for the binder descriptions.  Can be "html" or "text".
     * @return A SearchResultList of BinderBrief objects.
     */
    @GET
    public SearchResultList<BinderBrief> getWorkspaces(@QueryParam("id") Set<Long> ids,
                                                       @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                       @QueryParam("first") @DefaultValue("0") Integer offset,
                                                       @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        Junction criterion = Restrictions.conjunction();
        criterion.add(SearchUtils.buildWorkspacesCriterion());
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
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(toDomainFormat(descriptionFormatStr)),
                resultsMap, "/workspaces", nextParams, offset);
        return results;
    }

    @POST
    @Path("/legacy_query")
    @Undocumented
   	public SearchResultList<BinderBrief> getWorkspacesViaLegacyQuery(@Context HttpServletRequest request,
                                                                     @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                                     @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                     @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, SearchUtils.buildWorkspacesCriterion());
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(toDomainFormat(descriptionFormatStr)),
                resultsMap, "/workspaces/legacy_query", nextParams, offset);
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
        SearchResultList<BinderBrief> subBinders = getSubBinders(id, null, name, true, offset, maxCount,
                "/workspaces/" + id + "/binders", nextParams, toDomainFormat(descriptionFormatStr),
                ifModifiedSince);
        return Response.ok(subBinders).lastModified(lastModified).build();
    }

    @GET
    @Path("{id}/children")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
    public Response getChildren(@PathParam("id") long id,
                                @QueryParam("allow_jits") @DefaultValue("true") Boolean allowJits,
                                @QueryParam("title") String name,
                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
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
                "/workspaces/" + id + "/children", nextParams, toDomainFormat(descriptionFormatStr), null);
        return Response.ok(children).lastModified(lastModified).build();
    }

    @GET
    @Path("/{id}/workspaces/{title}")
    @Undocumented
    public Workspace getWorkspace(@PathParam("id") long parentId, @PathParam("title") String name) {
        org.kablink.teaming.domain.Workspace parent = _getWorkspace(parentId);
        Binder binder = getFolderByName(parentId, name);
        if (binder instanceof org.kablink.teaming.domain.Workspace) {
            return (Workspace) ResourceUtil.buildBinder(binder, true, Description.FORMAT_NONE);
        }
        throw new NoWorkspaceByTheNameException(name);
    }

	// Read subworkspaces
	@GET
	@Path("{id}/workspaces")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
	public Response getSubWorkspaces(@PathParam("id") long id,
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
        SearchResultList<BinderBrief> subBinders = getSubBinders(id, Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_WORKSPACE),
                null, true, offset, maxCount, "/workspaces/" + id + "/workspaces", nextParams, toDomainFormat(descriptionFormatStr), ifModifiedSince);
        return Response.ok(subBinders).lastModified(lastModified).build();
	}

    @POST
   	@Path("{id}/workspaces")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
   	public org.kablink.teaming.rest.v1.model.Workspace createSubWorkspace(@PathParam("id") long id, org.kablink.teaming.rest.v1.model.Workspace workspace,
                                                                          @QueryParam("template") Long templateId,
                                                                          @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId!=null) {
            TemplateBinder template = getTemplateModule().getTemplate(templateId);
            if (EntityIdentifier.EntityType.workspace != template.getEntityType()) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a workspace template.");
            }
        }
        return (Workspace) createBinder(id, workspace, templateId, toDomainFormat(descriptionFormatStr));
    }

    // Read subfolders
	@GET
	@Path("{id}/folders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
	public Response getSubFolders(@PathParam("id") long id,
                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
			                      @QueryParam("first") @DefaultValue("0") int offset,
			                      @QueryParam("count") @DefaultValue("100") int maxCount,
                                  @Context HttpServletRequest request) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);

        Date lastModified = getLibraryModifiedDate(new Long[]{id}, false, true);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<BinderBrief> subBinders = getSubBinders(id, Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER),
                null, true, offset, maxCount, "/workspaces/" + id + "/folders", nextParams, toDomainFormat(descriptionFormatStr), ifModifiedSince);
        return Response.ok(subBinders).lastModified(lastModified).build();
	}

    @POST
   	@Path("{id}/folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
   	public org.kablink.teaming.rest.v1.model.Folder createSubFolder(@PathParam("id") long id,
                                                                    org.kablink.teaming.rest.v1.model.Binder binder,
                                                                    @QueryParam("template") Long templateId,
                                                                    @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing required 'template' query string parameter.");
        }
        TemplateBinder template = getTemplateModule().getTemplate(templateId);
        if (EntityIdentifier.EntityType.folder != template.getEntityType()) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a folder template.");
        }
        return (Folder) createBinder(id, binder, templateId, toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/{id}/folders/{title}")
    @Undocumented
    public Folder getFolderByTitle(@PathParam("id") long parentId, @PathParam("title") String name) {
        _getWorkspace(parentId);
        Binder binder = getFolderByName(parentId, name);
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            return (Folder) ResourceUtil.buildBinder(binder, true, Description.FORMAT_NONE);
        }
        throw new NoWorkspaceByTheNameException(name);
    }

    // Read entries
	@GET
	@Path("{id}/library_entities")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<SearchableObject> getLibraryFiles(@PathParam("id") long id,
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
        _getWorkspace(id);
        return searchForLibraryEntities(keyword, SearchUtils.buildSearchBinderCriterion(id, recursive), recursive, offset, maxCount,
                includeBinders, includeFolderEntries, includeReplies, includeFiles, includeParentPaths, toDomainFormat(descriptionFormatStr),
                "/workspaces/" + id + "/library_entities");
	}

    @Override
    protected Binder _getBinder(long id) {
        return _getWorkspace(id);
    }

    @Override
    EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.workspace;
    }
}
