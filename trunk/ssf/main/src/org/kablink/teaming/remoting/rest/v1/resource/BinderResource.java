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
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.util.api.ApiErrorCode;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/binders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class BinderResource extends AbstractResource {
    @GET
    public SearchResultList<BinderBrief> getBinders(@QueryParam("id") Set<Long> ids,
                                                    @QueryParam("library_mod_times") @DefaultValue("false") boolean libraryModTimes,
                                                    @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                    @QueryParam("first") @DefaultValue("0") Integer offset,
                                                    @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        List<Long> specialIds = new ArrayList<Long>();
        Junction criterion = Restrictions.conjunction();
        criterion.add(buildBindersCriterion());
        if (ids!=null) {
            Junction or = Restrictions.disjunction();
            for (Long id : ids) {
                if (id>0) {
                    or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
                } else {
                    specialIds.add(id);
                }
            }
            criterion.add(or);
        }
        Document queryDoc = buildQueryDocument("<query/>", criterion);
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(textDescriptions), resultsMap, "/binders", null, offset);
        for (Long id : specialIds) {
            if (id.equals(ObjectKeys.SHARED_BY_ME_ID)) {
                results.append(getFakeSharedByMe());
            } else if (id.equals(ObjectKeys.SHARED_WITH_ME_ID)) {
                results.append(getFakeSharedWithMe());
            } else if (id.equals(ObjectKeys.MY_FILES_ID)) {
                results.append(getFakeMyFileFolders());
            } else if (id.equals(ObjectKeys.NET_FOLDERS_ID)) {
                results.append(getFakeNetFolders());
            }
        }
        if (libraryModTimes) {
            populateLibraryModTimes(results);
        }
        return results;
    }

    @POST
    @Path("/legacy_query")
   	public SearchResultList<BinderBrief> getBindersViaLegacyQuery(@Context HttpServletRequest request,
                                                                  @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                                  @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                  @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, buildBindersCriterion());
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(textDescriptions), resultsMap, "/binders/legacy_query", nextParams, offset);
        return results;
   	}

    /**
     * Returns the Binder with the specified ID.
     * @param id    The ID of the binder to return.
     * @param includeAttachments    Configures whether attachments should be included in the returned Binder object.
     * @return  Returns a subclass of Binder.
     */
    @GET
    @Path("{id}")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Binder getBinder(@PathParam("id") long id,
                            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments, textDescriptions);
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
            if (id.equals(ObjectKeys.SHARED_WITH_ME_ID)) {
                binder.setLibraryModificationDate(getSharedWithLibraryModifiedDate(getLoggedInUserId(), true));
            } else if (id.equals(ObjectKeys.MY_FILES_ID)) {
                binder.setLibraryModificationDate(getMyFilesLibraryModifiedDate(true));
            } else if (id>0) {
                binder.setLibraryModificationDate(getLibraryModifiedDate(new Long[]{id}, true));
            }
        }
    }
}
