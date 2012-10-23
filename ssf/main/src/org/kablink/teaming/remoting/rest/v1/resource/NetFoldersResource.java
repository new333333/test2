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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.ParentBinder;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.search.SearchUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public SearchResultList<BinderBrief> getNetFolders(@QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
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
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(textDescriptions), map, "/net_folders", nextParams, offset);
        for (BinderBrief binder : results.getResults()) {
            binder.setParentBinder(new ParentBinder(ObjectKeys.NET_FOLDERS_ID, "/self/net_folders"));
        }
        return results;
    }

    @GET
    @Path("/library_entities")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getLibraryEntities(@QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                 @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
                                                                 @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
                                                                 @QueryParam("files") @DefaultValue("true") boolean includeFiles,
                                                                 @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
                                                                 @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                 @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                                 @QueryParam("keyword") String keyword,
                                                                 @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                 @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset);
        SearchResultList<BinderBrief> netFolders = getNetFolders(textDescriptions, 0, -1);
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
            criterion.add(buildDocTypeCriterion(includeBinders, includeFolderEntries, includeFiles, includeReplies));
            criterion.add(buildLibraryCriterion(true));
            Map<String, Object> nextParams = new HashMap<String, Object>();
            nextParams.put("recursive", Boolean.toString(recursive));
            nextParams.put("binders", Boolean.toString(includeBinders));
            nextParams.put("folder_entries", Boolean.toString(includeFolderEntries));
            nextParams.put("files", Boolean.toString(includeFiles));
            nextParams.put("replies", Boolean.toString(includeReplies));
            nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
            if (keyword!=null) {
                nextParams.put("keyword", keyword);
            }
            nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
            Criteria crit = new Criteria();
            crit.add(criterion);
            Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
            SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(textDescriptions), resultsMap,
                    "/net_folders/library_entities", nextParams, offset);
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    @GET
    @Path("/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getRecentActivity(
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));

        SearchResultList<BinderBrief> folders = getNetFolders(true, 0, -1);
        if (folders.getCount()==0) {
            return new SearchResultList<SearchableObject>();
        }
        List<String> binders = new ArrayList<String>();
        for (BinderBrief binder : folders.getResults()) {
            binders.add(binder.getId().toString());
        }
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binders, null, null, true, Constants.LASTACTIVITY_FIELD);
        return _getRecentActivity(includeParentPaths, textDescriptions, offset, maxCount, criteria, "/net_folders/recent_activity", nextParams);
    }

}
