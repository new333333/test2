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
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.remoting.rest.v1.util.NetFolderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.SpringContextUtil;
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
 */
@Path("/net_folders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Net Folders")
public class NetFoldersResource extends AbstractResource {
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<NetFolderBrief> getNetFolders(@QueryParam("title") String name,
                                                          @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                          @QueryParam("first") @DefaultValue("0") Integer offset,
                                                          @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        return _getNetFolders(name, toDomainFormat(descriptionFormatStr), offset, maxCount, "/net_folders", nextParams);
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
                                                                 @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                                 @QueryParam("keyword") String keyword,
                                                                 @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                 @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        SearchResultList<SearchableObject> results;
        SearchResultList<NetFolderBrief> netFolders = getNetFolders(null, descriptionFormatStr, 0, -1);
        if (netFolders.getCount()>0) {
            Junction searchContext = Restrictions.disjunction();
            for (BinderBrief binder : netFolders.getResults()) {
                Junction shareCrit = Restrictions.conjunction();
                if (recursive) {
                    shareCrit.add(SearchUtils.buildSearchBinderCriterion(binder.getId(), true));
                } else {
                    shareCrit.add(SearchUtils.buildBinderCriterion(binder.getId()));
                }
                searchContext.add(shareCrit);
            }
            results = searchForLibraryEntities(keyword, searchContext, recursive, offset, maxCount,
                    includeBinders, includeFolderEntries, includeReplies, includeFiles, includeParentPaths,
                    toDomainFormat(descriptionFormatStr), "/net_folders/library_entities");
        } else {
            results = new SearchResultList<SearchableObject>();
        }
        return results;
    }

    @GET
    @Path("/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<RecentActivityEntry> getRecentActivity(
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("description_format", descriptionFormatStr);

        SearchResultList<NetFolderBrief> folders = getNetFolders(null, descriptionFormatStr, 0, -1);
        if (folders.getCount()==0) {
            return new SearchResultList<RecentActivityEntry>();
        }
        List<String> binders = new ArrayList<String>();
        for (BinderBrief binder : folders.getResults()) {
            binders.add(binder.getId().toString());
        }
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binders, null, null, true, Constants.LASTACTIVITY_FIELD);
        return _getRecentActivity(includeParentPaths, toDomainFormat(descriptionFormatStr), offset, maxCount, criteria,
                "/net_folders/recent_activity", nextParams);
    }

}
