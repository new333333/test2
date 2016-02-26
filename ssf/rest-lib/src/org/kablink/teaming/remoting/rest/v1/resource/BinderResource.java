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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.fi.AccessDeniedException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.RestExceptionWrapper;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderChildren;
import org.kablink.teaming.rest.v1.model.ErrorInfo;
import org.kablink.teaming.rest.v1.model.LibraryInfo;
import org.kablink.teaming.rest.v1.model.NetFolderBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.util.VibeRuntimeException;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.api.ApiErrorCodeSupport;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/binders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Binders")
public class BinderResource extends AbstractResource {
    /**
     * Get binders by ID.
     *
     * <p>This resource supports top level folder IDs, such as -100 (My Files)</p>
     *
     * <p>Example: <code>GET /rest/binders?id=-100&id=46&id=48</code></p>
     *
     * @param ids   The ID of a binder.  Can be specified multiple times.
     * @param libraryInfo   Whether to calculate and return additional folder statistics for the binders.  These calculations can be very expensive.
     * @param descriptionFormatStr The desired format for the binder descriptions.  Can be "html" or "text".
     * @return A SearchResultList of BinderBrief objects.
     */
    @GET
    public SearchResultList<BinderBrief> getBinders(@QueryParam("id") Set<Long> ids,
                                                    @Undocumented @QueryParam("library_mod_times") @DefaultValue("false") boolean libraryModTimes,
                                                    @QueryParam("library_info") @DefaultValue("false") boolean libraryInfo,
                                                    @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                    @Undocumented @QueryParam("first") @DefaultValue("0") Integer offset,
                                                    @Undocumented @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        boolean skipSearch = true;
        List<Long> specialIds = new ArrayList<Long>();
        Set<Long> missingIds = new HashSet<Long>();
        Junction criterion = Restrictions.conjunction();
        criterion.add(SearchUtils.buildBindersCriterion());
        if (ids!=null) {
            Junction or = Restrictions.disjunction();
            for (Long id : ids) {
                if (id>0) {
                    or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
                    missingIds.add(id);
                    skipSearch = false;
                } else {
                    specialIds.add(id);
                }
            }
            criterion.add(or);
        }
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        if (!skipSearch) {
            Document queryDoc = buildQueryDocument("<query/>", criterion);
            Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
            SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(toDomainFormat(descriptionFormatStr)), resultsMap, "/binders", null, offset);
            Set<Long> foundIds = new HashSet<Long>();
            for (BinderBrief binder : results.getResults()) {
                missingIds.remove(binder.getId());
            }

            // The user might have inferred access to binders that did not come back in the search results.
            for (Long id : missingIds) {
                try{
                    org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(id, false, true);
                    if (binder!=null) {
                        results.append(ResourceUtil.buildBinderBrief(binder));
                    }
                } catch (NoBinderByTheIdException e) {
                } catch (AccessControlException e) {
                }
            }
        }
        for (Long id : specialIds) {
            if (id.equals(ObjectKeys.SHARED_BY_ME_ID)) {
                results.append(getFakeSharedByMe());
            } else if (id.equals(ObjectKeys.SHARED_WITH_ME_ID)) {
                results.append(getFakeSharedWithMe());
            } else if (id.equals(ObjectKeys.MY_FILES_ID)) {
                BinderBrief binder = getFakeMyFileFolders(true);
                if (binder!=null) {
                    results.append(binder);
                }
            } else if (id.equals(ObjectKeys.NET_FOLDERS_ID)) {
                results.append(getFakeNetFolders());
            } else if (id.equals(ObjectKeys.PUBLIC_SHARES_ID)) {
                BinderBrief binder = getFakePublicShares(true);
                if (binder!=null) {
                    results.append(binder);
                }
            } else if (id.equals(ObjectKeys.MY_TEAMS_ID)) {
                results.append(getFakeMyTeams());
            } else if (id.equals(ObjectKeys.My_FAVORITES_ID)) {
                results.append(getFakeMyFavorites());
            }
        }

        if (libraryInfo) {
            populateLibraryInfo(results);
        } else if (libraryModTimes) {
            populateLibraryModTimes(results);
        }
        return results;
    }

    @Undocumented
    @POST
    @Path("/legacy_query")
   	public SearchResultList<BinderBrief> getBindersViaLegacyQuery(@Context HttpServletRequest request,
                                                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                                  @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                  @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, SearchUtils.buildBindersCriterion());
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(toDomainFormat(descriptionFormatStr)), resultsMap, "/binders/legacy_query", nextParams, offset);
        return results;
   	}

    /**
     * Lists the children of the specified binders.
     *
     * <p><code>count</code> specifies the total number of children to return.  For example, <code>id=-100&id=48&id=49&count=10</code>
     * might return all 6 children of folder 48, the first 4 children of folder 49 and no results for folder -100.  The order that the binder IDs are
     * processed in is non-deterministic.</p>
     *
     * <p>The <code>first_id</code> and <code>first</code> parameters can be used to continue retrieving results from binders
     * whose children are only partially listed in the previous request. The children of the binder specified by
     * <code>first_id</code> are included first, beginning with the <code>first + 1</code> child. </p>
     *
     * <p>For example, <code>id=-100&id=48&first_id=48&first=4&count=10</code> is will return results 5-14 of
     * folder 48.  If folder 48 has fewer than 14 children, then the first few children of folder -100 will also be included
     * in the response.</p>
     *
     * <p>Paging the results of the Shared with Me (-101), Shared by Me (-102) and Public (-104) top level folders is not
     * supported.  This resource will return all children of those folders, even if that means that more than <code>count</code>
     * children are returned.</p>
     *
     * <p>If an error occurs listing the children of a particular binder, a BinderChildren object for that binder will be included
     * in the results with information about the error that occurred.</p>
     *
     * <p>This resource supports top level folder IDs, such as -100 (My Files)</p>
     * @param ids   The ID of a binder whose children are to be listed.  Can be specified multiple times.
     * @param firstId   The binder to start with.
     * @param descriptionFormatStr The desired format for the binder descriptions.  Can be "html" or "text".
     * @param offset    Specifies the first child to return.  Ignored if <code>first_id</code> is not specified.
     * @param maxCount  The maximum number of children to return.
     * @return  A list of BinderChildren resources.
     */
    @GET
    @Path("library_children")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<BinderChildren> getLibraryChildren(@QueryParam("id") Set<Long> ids,
                                                   @QueryParam("first_id") Long firstId,
                                       @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                       @QueryParam("first") @DefaultValue("0") Integer offset,
                                       @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        int remainingCount = maxCount;
        Set<Long> processedIds = new HashSet<Long>();
        List<BinderChildren> childrenList = new ArrayList<BinderChildren>();
        int domainFormat = toDomainFormat(descriptionFormatStr);

        if (firstId!=null) {
            BinderChildren bc = getBinderChildren(firstId, offset, remainingCount, domainFormat);
            childrenList.add(bc);
            remainingCount -= bc.getChildrenCount();
            processedIds.add(firstId);
        }

        for (Long id : ids) {
            if (remainingCount>0 && !processedIds.contains(id)) {
                BinderChildren bc = getBinderChildren(id, 0, remainingCount, domainFormat);
                childrenList.add(bc);
                remainingCount -= bc.getChildrenCount();
                processedIds.add(firstId);
            }
        }
        return childrenList;
    }

    private BinderChildren getBinderChildren(Long id, int offset, int maxCount, int domainFormat) {
        ErrorInfo error = null;
        SearchResultList<SearchableObject> children = null;
        try {
            if (id.equals(ObjectKeys.MY_FILES_ID)) {
                children = _getMyFilesLibraryChildren(null, null, true, false, true, false, domainFormat, offset, maxCount, null);
            } else if (id.equals(ObjectKeys.NET_FOLDERS_ID)) {
                SearchResultList<NetFolderBrief> childrenList = _getNetFolders(null, domainFormat, 0, -1, null, null);
                children = new SearchResultList<SearchableObject>();
                for (NetFolderBrief nf : childrenList.getResults()) {
                    children.append(nf);
                }
            } else if (id.equals(ObjectKeys.SHARED_WITH_ME_ID)) {
                List<SearchableObject> childrenList = getSharedWithChildren(getSharedWithShareItems(getLoggedInUserId(), false), null, true, true, false, true);
                children = new SearchResultList<SearchableObject>();
                children.appendAll(childrenList);
            } else if (id.equals(ObjectKeys.SHARED_BY_ME_ID)) {
                List<SearchableObject> childrenList = getSharedByChildren(getSharedByShareItems(getLoggedInUserId(), false), null, true, true, false, true);
                children = new SearchResultList<SearchableObject>();
                children.appendAll(childrenList);
            } else if (id.equals(ObjectKeys.PUBLIC_SHARES_ID)) {
                List<SearchableObject> childrenList = getPublicChildren(getPublicShareItems(false), null, true, true, false, true);
                children = new SearchResultList<SearchableObject>();
                children.appendAll(childrenList);
            } else {
                children = getChildren(id, SearchUtils.buildLibraryCriterion(true, Boolean.TRUE), null, true, false, true,
                        false, offset, maxCount, null, null, domainFormat, null);
            }
        } catch (RestExceptionWrapper e) {
            error = new ErrorInfo(e.getApiErrorCode().name(), e.getLocalizedMessage(), e.getData());
        } catch (WebApplicationException e) {
            if (e.getResponse().getEntity() instanceof ErrorInfo) {
                error = (ErrorInfo) e.getResponse().getEntity();
            } else {
                error = new ErrorInfo(ApiErrorCode.SERVER_ERROR.name(), e.getLocalizedMessage());
            }
        } catch (RuntimeException e) {
            if (e instanceof ApiErrorCodeSupport) {
                error = new ErrorInfo(((ApiErrorCodeSupport)e).getApiErrorCode().name(), e.getLocalizedMessage());
            } else {
                error = new ErrorInfo(ApiErrorCode.SERVER_ERROR.name(), e.getLocalizedMessage());
            }
        }
        return new BinderChildren(id, children, error);

    }

    /**
     * Returns the Binder with the specified ID.
     * @param id    The ID of the binder to return.
     * @param includeAttachments    Configures whether attachments should be included in the returned Binder object.
     * @param descriptionFormatStr The desired format for the binder description.  Can be "html" or "text".
     * @return  Returns a subclass of Binder (Folder or Workspace).
     */
    @GET
    @Path("{id}")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Binder getBinder(@PathParam("id") long id,
                            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments, toDomainFormat(descriptionFormatStr));
    }

    protected org.kablink.teaming.domain.Binder _getBinder(long id) {
        try{
            org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(id, false, true);
            if (!isBinderPreDeleted(binder)) {
                return binder;
            }
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.BINDER_NOT_FOUND, "NOT FOUND");
    }

    protected void populateLibraryModTimes(SearchResultList<BinderBrief> results) {
        for (BinderBrief binder : results.getResults()) {
            Long id = binder.getId();
            LibraryInfo info = new LibraryInfo();
            if (id.equals(ObjectKeys.SHARED_WITH_ME_ID)) {
                info.setModifiedDate(getSharedWithLibraryModifiedDate(getLoggedInUserId(), true));
            } else if (id.equals(ObjectKeys.SHARED_BY_ME_ID)) {
                info.setModifiedDate(getSharedByLibraryModifiedDate(getLoggedInUserId(), true));
            } else if (id.equals(ObjectKeys.MY_FILES_ID)) {
                info.setModifiedDate(getMyFilesLibraryModifiedDate(true, false));
            } else if (id.equals(ObjectKeys.PUBLIC_SHARES_ID)) {
                info.setModifiedDate(getPublicSharesLibraryModifiedDate(true));
            } else if (id>0) {
                info.setModifiedDate(getLibraryModifiedDate(new Long[]{id}, true, false));
            }
            if (info.getModifiedDate()!=null) {
                binder.setLibraryInfo(info);
            }
        }
    }

    protected void populateLibraryInfo(SearchResultList<BinderBrief> results) {
        for (BinderBrief binder : results.getResults()) {
            Long id = binder.getId();
            if (id.equals(ObjectKeys.SHARED_WITH_ME_ID)) {
                binder.setLibraryInfo(getSharedWithLibraryInfo(getLoggedInUserId()));
            } else if (id.equals(ObjectKeys.SHARED_BY_ME_ID)) {
                binder.setLibraryInfo(getSharedByLibraryInfo(getLoggedInUserId()));
            } else if (id.equals(ObjectKeys.MY_FILES_ID)) {
                binder.setLibraryInfo(getMyFilesLibraryInfo());
            } else if (id.equals(ObjectKeys.PUBLIC_SHARES_ID)) {
                binder.setLibraryInfo(getPublicSharesLibraryInfo());
            } else if (id>0) {
                binder.setLibraryInfo(getLibraryInfo(new Long[]{id}, binder.getMirrored()));
            }
        }
    }
}
