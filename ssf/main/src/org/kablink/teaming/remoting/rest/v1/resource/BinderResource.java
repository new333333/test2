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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Path("/binders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class BinderResource extends AbstractResource {
    @GET
    public SearchResultList<BinderBrief> getBinders(@QueryParam("id") Set<Long> ids,
                                                    @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                    @QueryParam("first") @DefaultValue("0") Integer offset,
                                                    @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Junction criterion = Restrictions.conjunction();
        criterion.add(buildBindersCriterion());
        if (ids!=null) {
            Junction or = Restrictions.disjunction();
            for (Long id : ids) {
                or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
            }
            criterion.add(or);
        }
        Document queryDoc = buildQueryDocument("<query/>", criterion);
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(textDescriptions), resultsMap, "/binders", null, offset);
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
            return getBinderModule().getBinder(id, false, true);
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.BINDER_NOT_FOUND, "NOT FOUND");
    }
}
