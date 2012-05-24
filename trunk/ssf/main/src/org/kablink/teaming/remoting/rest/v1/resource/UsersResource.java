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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UserBriefBuilder;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.UserBrief;
import org.kablink.teaming.search.filter.SearchFilter;

@Path("/v1/users")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class UsersResource extends AbstractResource {
	// Get all users
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<UserBrief> getUsers(
		@QueryParam("name") String name,
		@QueryParam("email") String email,
		@QueryParam("first") Integer offset,
		@QueryParam("count") Integer maxCount) {
        Map<String, Object> options = new HashMap<String, Object>();
        SearchFilter searchTermFilter = new SearchFilter();
        if (name!=null) {
            searchTermFilter.addLoginNameFilter(name);
            options.put( ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter() );
        }
        if (offset!=null) {
            options.put(ObjectKeys.SEARCH_OFFSET, offset);
        } else {
            offset = 0;
        }
        if (maxCount!=null) {
            options.put(ObjectKeys.SEARCH_MAX_HITS, maxCount);
        }
        Map resultMap = getProfileModule().getUsers(options);
        SearchResultList<UserBrief> results = new SearchResultList<UserBrief>();
        SearchResultBuilderUtil.buildSearchResults(results, new UserBriefBuilder(), resultMap, "/users", offset);
		return results;
	}
	
	// Create a new user.
	@POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public void createUser() {		
		// optionally accept initial password
	}
}
