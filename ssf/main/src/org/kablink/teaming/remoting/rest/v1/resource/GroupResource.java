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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoGroupByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.util.LinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.ws.RemotingException;
import org.kablink.teaming.rest.v1.model.Group;
import org.kablink.teaming.rest.v1.model.GroupMember;
import org.kablink.teaming.rest.v1.model.PrincipalBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: david
 * Date: 5/18/12
 * Time: 11:46 AM
 */
@Path("/v1/group/{id}")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class GroupResource extends AbstractPrincipalResource{
    @Override
    protected EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.group;
    }

    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Group getGroup(@PathParam("id") long id,
                        @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments) {
        return ResourceUtil.buildGroup(_getGroup(id), includeAttachments);
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateGroup(@PathParam("id") long id, Group group)
            throws WriteFilesException, WriteEntryDataException {
        _getGroup(id);
        getProfileModule().modifyEntry(id, new RestModelInputData(group));
    }

    @GET
    @Path("/members")
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
            Principal member = (Principal) members.get(offset+i);
            results.append(ResourceUtil.buildGroupMember(id, member));
        }
        results.setTotal(members.size());
        results.setNextIfNecessary(LinkUriUtil.getGroupLinkUri(id) + "/members");
        return results;
    }

    @POST
    @Path("/members")
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
    @Path("/member/{memberId}")
    public void removeMember(@PathParam("id") long id, @PathParam("memberId") long memberId) throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Group group = _getGroup(id);
        Principal member = getProfileModule().getEntry(memberId);
        Map updates = new HashMap();
        List members = new ArrayList(group.getMembers());
        members.remove(member);
        updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
        getProfileModule().modifyEntry(group.getId(), new MapInputData(updates));
    }

    private org.kablink.teaming.domain.Group _getGroup(long id) {
        Principal entry = getProfileModule().getEntry(id);

        if(!(entry instanceof org.kablink.teaming.domain.Group))
            throw new NoGroupByTheIdException(id);
        return (org.kablink.teaming.domain.Group) entry;
    }
}
