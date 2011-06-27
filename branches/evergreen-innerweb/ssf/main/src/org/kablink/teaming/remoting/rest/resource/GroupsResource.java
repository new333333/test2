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
package org.kablink.teaming.remoting.rest.resource;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kablink.teaming.rest.model.Group;

@Path("/groups")
public class GroupsResource {

	// Get all groups
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Group> getGroups(
		@QueryParam("offset") Integer offset,
		@QueryParam("maxcount") Integer maxCount) {
		return null;
	}

	// Create a new group.
	@POST
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void createGroup() {		
	}

	// Get group
	@GET
	@Path("group/{id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Group getGroup(@PathParam("id") long id) {
		return null;
	}
	
	// Update group. This only updates properties/metadata.
	@PUT
	@Path("group/{id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void updateGroup(@PathParam("id") long id) {
	}
	
	// Delete group.
	@DELETE
	@Path("group/{id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void deleteGroup(@PathParam("id") long id) {
	}
	
	// Get group by name
	@GET
	@Path("group")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Group getGroupByName(@QueryParam("name") String name) {
		return null;
	}
	
	// Add member to the group
	@PUT
	@Path("group/{id}/member/{user_or_group_id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addMember(@PathParam("id") long id,
			@PathParam("user_or_group_id") long userPrincipalId) {
		// How about providing interface where client can add more than one members in a single call?
		return null;
	}
	
	// Remove member from the group
	@DELETE
	@Path("group/{id}/member/{user_or_group_id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response removeMember(@PathParam("id") long id,
			@PathParam("user_or_group_id") long userPrincipalId) {
		return null;
	}

	// Get members
	@GET
	@Path("group/{id}/members")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List getMembers(@PathParam("id") long id) {
		// TODO $$$ Can I use polymorphism here, that is, a list of "
		// Unfortunately, polymopshism won't work with a collection.
		// We should consider offering two variations - one with a list of
		// full objects, and the other with a list of brief objects (i.e, 
		// handle/reference/identity only).
		return null;
	}
}
