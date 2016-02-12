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
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.resource.Singleton;
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import org.dom4j.Document;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.LimitedUserView;
import org.kablink.teaming.domain.TeamInfo;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.ChainedInputData;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.util.PrincipalBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UserBriefBuilder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.GroupBrief;
import org.kablink.teaming.rest.v1.model.PrincipalBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.TeamBrief;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.UserBrief;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

@Path("/users")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Users and Groups")
public class UserResource extends AbstractPrincipalResource {
	// Get all users
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<UserBrief> getUsers(
            @QueryParam("id") Set<Long> ids,
            @QueryParam("keyword") String keyword,
            @QueryParam("name") String name,
            @QueryParam("email") String email,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        SearchResultList<UserBrief> results;
        if (canViewUsers()) {
            Map<String, Object> nextParams = new HashMap<String, Object>();
            boolean allowExternal = false;
            Junction criterion = Restrictions.conjunction();
            if (ids!=null) {
                Junction or = Restrictions.disjunction();
                for (Long id : ids) {
                    or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
                    allowExternal = true;
                }
                criterion.add(or);
                nextParams.put("id", ids);
            }
            criterion.add(SearchUtils.buildUsersCriterion(allowExternal));
            if (name!=null) {
                criterion.add(Restrictions.like(Constants.LOGINNAME_FIELD, name));
                nextParams.put("name", name);
            }
            if (email!=null) {
                criterion.add(Restrictions.like(Constants.EMAIL_FIELD, email.replace('@', '?')));
                nextParams.put("email", email);
            }
            if (keyword!=null) {
                Junction or = Restrictions.disjunction();
                keyword = SearchUtils.modifyQuickFilter(keyword);
                or.add(Restrictions.like(Constants.TITLE_FIELD, keyword));
                or.add(Restrictions.like(Constants.EMAIL_FIELD, keyword));
                or.add(Restrictions.like(Constants.EMAIL_DOMAIN_FIELD, keyword));
                or.add(Restrictions.like(Constants.LOGINNAME_FIELD, keyword));
                criterion.add(or);
                nextParams.put("keyword", keyword);
            }

            String nextUrl = "/users";
            nextParams.put("description_format", descriptionFormatStr);
            Document queryDoc = buildQueryDocument("<query/>", criterion);
            Map resultMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
            results = new SearchResultList<UserBrief>();
            SearchResultBuilderUtil.buildSearchResults(results, new UserBriefBuilder(toDomainFormat(descriptionFormatStr)), resultMap, nextUrl, nextParams, offset);
        } else {
            Set<LimitedUserView> views = searchForPrincipalsLimited(ids);
            results = new SearchResultList<UserBrief>();
            for (LimitedUserView view : views) {
                results.append(ResourceUtil.buildUserBrief(view));
            }
        }
        return results;
	}
	
	// Create a new user.
	@POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public User createUser(User user, @QueryParam ("password") String password,
                           @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
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

        return ResourceUtil.buildUser(getProfileModule().addUser(defId, inputData, null, null), true, toDomainFormat(descriptionFormatStr));
	}

    @GET
    @Path("/name/{name}")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User getUserByName(@PathParam("name") String name,
                        @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (name==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing name query parameter.");
        }
        return ResourceUtil.buildUser(getProfileModule().getUser(name), includeAttachments, toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/{id}")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User getUser(@PathParam("id") long userId,
                        @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (canViewUsers()) {
            return ResourceUtil.buildUser(_getUser(userId), includeAttachments, toDomainFormat(descriptionFormatStr));
        } else {
            return ResourceUtil.buildLimitedUser(getLimitedUser(userId));
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public User updateUser(@PathParam("id") long id, User user,
                           @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                           @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.User existing = _getUser(id);
        if (user.isDisabled()!=null && user.isDisabled()!=existing.isDisabled()) {
            getProfileModule().disableEntry(id, user.isDisabled());
        }
        getProfileModule().modifyEntry(id, new RestModelInputData(user));
        return getUser(id, includeAttachments, descriptionFormatStr);
    }

    @POST
    @Path("/{id}/password")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response changePassword(@PathParam("id") long id,
                           @FormParam("old_password") String oldPassword,
                           @FormParam("new_password") String newPassword) {
        org.kablink.teaming.domain.User existing = _getUser(id);
        if (newPassword==null || newPassword.length()==0) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing new_password form parameter");
        }
        if (getProfileModule().mustSupplyOldPasswordToSetNewPassword(id) && (oldPassword==null || oldPassword.length()==0)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing old_password form parameter");
        }
        getProfileModule().changePassword(id, oldPassword, newPassword);
        return Response.ok().build();
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
