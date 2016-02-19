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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.util.*;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.rest.v1.model.Group;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/groups")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class GroupResource extends AbstractPrincipalResource {
	// Get all users
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<GroupBrief> getGroups(
		@QueryParam("name") String name,
        @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
		@QueryParam("first") Integer offset,
		@QueryParam("count") @DefaultValue("100") Integer maxCount) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( ObjectKeys.SEARCH_FILTER_AND, SearchUtils.buildExcludeUniversalAndContainerGroupFilter(false) );
        SearchFilter searchTermFilter = new SearchFilter();
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (name!=null) {
            searchTermFilter.addGroupNameFilter(name);
            options.put( ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter() );
            nextParams.put("name", name);
        }
        if (offset!=null) {
            options.put(ObjectKeys.SEARCH_OFFSET, offset);
        } else {
            offset = 0;
        }
        if (maxCount!=null) {
            options.put(ObjectKeys.SEARCH_MAX_HITS, maxCount);
        }
        nextParams.put("description_format", descriptionFormatStr);
        Map resultMap = getProfileModule().getGroups(options);
        SearchResultList<GroupBrief> results = new SearchResultList<GroupBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new GroupBriefBuilder(toDomainFormat(descriptionFormatStr)), resultMap, "/groups", nextParams, offset);
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
