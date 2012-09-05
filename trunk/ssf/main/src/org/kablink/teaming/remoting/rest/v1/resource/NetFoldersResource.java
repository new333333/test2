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

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static org.kablink.util.search.Restrictions.in;

/**
 * User: david
 * Date: 9/5/12
 * Time: 3:01 PM
 */
@Path("/net_folders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class NetFoldersResource extends AbstractResource {
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getNetFolders(
                                                           @QueryParam("first") @DefaultValue("0") Integer offset,
                                                           @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Criteria crit = new Criteria();
        crit.add(in(Constants.DOC_TYPE_FIELD,            new String[]{Constants.DOC_TYPE_BINDER}));
        crit.add(in(Constants.FAMILY_FIELD,              new String[]{Definition.FAMILY_FILE}));
        crit.add(in(Constants.IS_MIRRORED_FIELD,         new String[]{Constants.TRUE}));
        crit.add(in(Constants.IS_TOP_FOLDER_FIELD,       new String[]{Constants.TRUE}));
        crit.add(in(Constants.HAS_RESOURCE_DRIVER_FIELD, new String[]{Constants.TRUE}));
        Map map = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), map, "/net_folders", null, offset);
        return results;
    }

    @GET
    @Path("/library_entities")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getLibraryEntities(@QueryParam("recursive") @DefaultValue("false") boolean recursive,
            @QueryParam("keyword") String keyword,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset);
        SearchResultList<BinderBrief> netFolders = getNetFolders(0, -1);
        if (netFolders.getCount()>0) {
            Junction criterion = Restrictions.conjunction();
            Junction searchContext = Restrictions.disjunction();
            for (BinderBrief binder : netFolders.getResults()) {
                Junction shareCrit = Restrictions.conjunction();
                if (recursive) {
                    shareCrit.add(buildSearchBinderCriterion(binder.getId(), true));
                } else {
                    shareCrit.add(buildBinderCriterion(binder.getId()));
                }
                searchContext.add(shareCrit);
            }
            criterion.add(searchContext);
            if (keyword!=null) {
                criterion.add(buildKeywordCriterion(keyword));
            }
            criterion.add(buildLibraryCriterion(true));
            Map<String, String> nextParams = new HashMap<String, String>();
            nextParams.put("recursive", Boolean.toString(recursive));
            if (keyword!=null) {
                criterion.add(buildKeywordCriterion(keyword));
                //TODO: URL encode the keyword
                nextParams.put("keyword", keyword);
            }
            Criteria crit = new Criteria();
            crit.add(criterion);
            Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
            SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(), resultsMap,
                    "/net_folders/library_entities", nextParams, offset);
        }
        return results;
    }

}
