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
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.util.GroupBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.LinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.PrincipalBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.Group;
import org.kablink.teaming.rest.v1.model.GroupBrief;
import org.kablink.teaming.rest.v1.model.GroupMember;
import org.kablink.teaming.rest.v1.model.PrincipalBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Order;
import org.kablink.util.search.Restrictions;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/principals")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class PrincipalResource extends AbstractPrincipalResource {
	// Get all users
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<PrincipalBrief> getPrincipals(
        @QueryParam("id") Set<Long> ids,
		@QueryParam("keyword") String keyword,
		@QueryParam("included_groups") @DefaultValue("all") String groups,
		@QueryParam("included_users") @DefaultValue("all") String users,
		@QueryParam("include_all_users_group") @DefaultValue("true") boolean includeAllUsers,
        @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
		@QueryParam("first") @DefaultValue("0") Integer offset,
		@QueryParam("count") @DefaultValue("25") Integer maxCount) {
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
        PrincipalOptions userOption = toEnum(PrincipalOptions.class, "included_users", users);
        PrincipalOptions groupOption = toEnum(PrincipalOptions.class, "included_groups", groups);
        Junction orJunction = Restrictions.disjunction();
        orJunction.add(SearchUtils.getFalseCriterion());
        if (userOption!=PrincipalOptions.none) {
            Criterion userCrit = SearchUtils.buildUsersCriterion(allowExternal);
            // TODO: support local-only and ldap-only searches (not currently supported by the index)
            orJunction.add(userCrit);
        }
        if (groupOption!=PrincipalOptions.none) {
            Criterion groupCrit;
            if (groupOption==PrincipalOptions.local) {
                groupCrit = SearchUtils.buildGroupsCriterion(Boolean.FALSE, includeAllUsers);
            } else if (groupOption==PrincipalOptions.ldap) {
                groupCrit = SearchUtils.buildGroupsCriterion(Boolean.TRUE, includeAllUsers);
            } else {
                groupCrit = SearchUtils.buildGroupsCriterion(null, includeAllUsers);
            }
            orJunction.add(groupCrit);
        }
        criterion.add(orJunction);
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
        nextParams.put("description_format", descriptionFormatStr);
        Criteria criteria = new Criteria();
        criteria.add(criterion);
        criteria.addOrder(new Order(Constants.SORT_TITLE_FIELD, true));

        Map resultMap = getBinderModule().executeSearchQuery(criteria, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<PrincipalBrief> results = new SearchResultList<PrincipalBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new PrincipalBriefBuilder(toDomainFormat(descriptionFormatStr)), resultMap, "/principals", nextParams, offset);
		return results;
	}
	
	// Create a new user.
	@POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Group createGroup(Group group,
                             @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
		// optionally accept initial password
        String defId = null;
        if (group.getDefinition()!=null) {
            defId = group.getDefinition().getId();
        }

        return ResourceUtil.buildGroup(getProfileModule().addGroup(defId, new RestModelInputData(group), null, null),
                true, toDomainFormat(descriptionFormatStr));
	}

    @GET
    @Path("/name/{name}")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Group getGroup(@PathParam("name") String name,
                          @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                          @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (name==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing name query parameter.");
        }
        return ResourceUtil.buildGroup(getProfileModule().getGroup(name), includeAttachments, toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/{id}")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Group getGroup(@PathParam("id") long id,
                        @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        return ResourceUtil.buildGroup(_getGroup(id), includeAttachments, toDomainFormat(descriptionFormatStr));
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateGroup(@PathParam("id") long id, Group group)
            throws WriteFilesException, WriteEntryDataException {
        _getGroup(id);
        getProfileModule().modifyEntry(id, new RestModelInputData(group));
    }

    @GET
    @Path("/{id}/members")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<GroupMember> getMembers(@PathParam("id") long id,
                                                       @QueryParam("first") @DefaultValue("0") int offset,
                                                       @QueryParam("count") @DefaultValue("-1") int maxCount) {
        org.kablink.teaming.domain.Group group = _getGroup(id);
        List members = group.getMembers();
        int length = members.size();
        if(maxCount > 0)
            length = Math.min(length - offset, maxCount);
        if(length < 0)
            length = 0;
        SearchResultList<GroupMember> results = new SearchResultList<GroupMember>(offset);
        for(int i=0; i<length; ++i) {
            org.kablink.teaming.domain.Principal member = (org.kablink.teaming.domain.Principal) members.get(offset+i);
            results.append(ResourceUtil.buildGroupMember(id, member));
        }
        results.setTotal(members.size());
        results.setNextIfNecessary(LinkUriUtil.getGroupLinkUri(id) + "/members", null);
        return results;
    }

    @POST
    @Path("/{id}/members")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void addMembers(@PathParam("id") long id, PrincipalBrief principal)
            throws WriteFilesException, WriteEntryDataException {
        if (principal ==null || principal.getId()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No user or group id was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Group group = _getGroup(id);
        UserPrincipal member = (UserPrincipal)getProfileModule().getEntry(principal.getId());

        Map updates = new HashMap();
        List members = new ArrayList(group.getMembers());
        members.add(member);
        updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
        getProfileModule().modifyEntry(group.getId(), new MapInputData(updates));
    }

    @DELETE
    @Path("/{id}/members/{memberId}")
    public void removeMember(@PathParam("id") long id, @PathParam("memberId") long memberId) throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Group group = _getGroup(id);
        org.kablink.teaming.domain.Principal member = getProfileModule().getEntry(memberId);
        Map updates = new HashMap();
        List members = new ArrayList(group.getMembers());
        members.remove(member);
        updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
        getProfileModule().modifyEntry(group.getId(), new MapInputData(updates));
    }

    @Override
    protected EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.group;
    }
}
