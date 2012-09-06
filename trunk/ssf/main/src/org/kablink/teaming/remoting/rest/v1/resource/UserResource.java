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
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.TeamInfo;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.ChainedInputData;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UserBriefBuilder;
import org.kablink.teaming.remoting.ws.util.ModelInputData;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.GroupBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.TeamBrief;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.UserBrief;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.util.api.ApiErrorCode;

@Path("/users")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class UserResource extends AbstractPrincipalResource {
	// Get all users
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<UserBrief> getUsers(
		@QueryParam("name") String name,
		@QueryParam("email") String email,
        @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
		@QueryParam("first") Integer offset,
		@QueryParam("count") Integer maxCount) {
        Map<String, Object> options = new HashMap<String, Object>();
        Map<String, String> nextParams = new HashMap<String, String>();
        SearchFilter searchTermFilter = new SearchFilter();
        String nextUrl = "/users";
        if (name!=null || email!=null) {
            String params = null;
            if (name!=null) {
                searchTermFilter.addLoginNameFilter(name);
                nextParams.put("name", name);
            }
            if (email!=null) {
                searchTermFilter.addEmailFilter(email.replace('@', '?'));
                nextParams.put("email", email);
            }
            options.put( ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter() );
            nextUrl += params;
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
        SearchResultBuilderUtil.buildSearchResults(results, new UserBriefBuilder(textDescriptions), resultMap, nextUrl, nextParams, offset);
		return results;
	}
	
	// Create a new user.
	@POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public User createUser(User user, @QueryParam ("password") String password,
                           @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
            throws WriteFilesException, WriteEntryDataException {
        if (user.getName()==null || user.getName().length()==0) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No name specified for the user to be created.");
        }
		// optionally accept initial password
        ChainedInputData inputData = new ChainedInputData();
        inputData.addAccessor(new RestModelInputData(user));
        if(password != null) {
            Map passwordMap = new HashMap();
            passwordMap.put("password", password);
            inputData.addAccessor(new MapInputData(passwordMap));
        }
        String defId = null;
        if (user.getDefinition()!=null) {
            defId = user.getDefinition().getId();
        }

        return ResourceUtil.buildUser(getProfileModule().addUser(defId, inputData, null, null), true, textDescriptions);
	}

    @GET
    @Path("/name/{name}")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User getUser(@PathParam("name") String name,
                        @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        if (name==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing name query parameter.");
        }
        return ResourceUtil.buildUser(getProfileModule().getUser(name), includeAttachments, textDescriptions);
    }

    @GET
    @Path("/{id}")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User getUser(@PathParam("id") long userId,
                        @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        return ResourceUtil.buildUser(_getUser(userId), includeAttachments, textDescriptions);
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateUser(@PathParam("id") long id, User user)
            throws WriteFilesException, WriteEntryDataException {
        _getUser(id);
        getProfileModule().modifyEntry(id, new RestModelInputData(user));
    }

    @GET
    @Path("/{id}/teams")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<TeamBrief> getTeams(@PathParam("id") long userId) {
        List<TeamInfo> binders = getProfileModule().getUserTeams(userId);
        SearchResultList<TeamBrief> results = new SearchResultList<TeamBrief>();
        for (org.kablink.teaming.domain.TeamInfo binder : binders) {
            results.append(ResourceUtil.buildTeamBrief(binder));
        }
        return results;
    }

    @GET
    @Path("/{id}/favorites")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getFavorites(@PathParam("id") long userId) {
        List<Binder> binders = getProfileModule().getUserFavorites(userId);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        for (Binder binder : binders) {
            results.append(ResourceUtil.buildBinderBrief(binder));
        }
        return results;
    }

    @GET
    @Path("/{id}/groups")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<GroupBrief> getGroups(@PathParam("id") long id) {
        _getUser(id);
        List<Group> groups = getProfileModule().getUserGroups(id);
        SearchResultList<GroupBrief> results = new SearchResultList<GroupBrief>();
        for (Group group : groups) {
            results.append(ResourceUtil.buildGroupBrief(group));
        }
        return results;
    }

    @Override
    protected EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.user;
    }

}
