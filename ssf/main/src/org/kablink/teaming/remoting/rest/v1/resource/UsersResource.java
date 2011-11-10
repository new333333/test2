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

import org.kablink.teaming.rest.v1.model.Team;
import org.kablink.teaming.rest.v1.model.User;

@Path("/users")
public class UsersResource {

	// Get all users
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<User> getUsers(
		@QueryParam("offset") Integer offset,
		@QueryParam("maxcount") Integer maxCount) {
		return null;
	}
	
	// Create a new user.
	@POST
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void createUser() {		
		// optionally accept initial password
	}
	
	@GET
	@Path("byemail")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<User> getUsersByEmail(
		@QueryParam("email_address") String emailAddress,
		@QueryParam("email_type") String emailType) {
		return null;
	}

	@GET
	@Path("user")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public User getUserByName(@QueryParam("name") String name) {
		return null;
	}

	@GET
	@Path("user/{id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public User getUser(@PathParam("id") Long id) {
		return null;
	}
	
	// Update user. This only updates properties/metadata.
	@PUT
	@Path("user/{id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void updateUser(@PathParam("id") long id) {
	}
	
	// Delete user.
	@DELETE
	@Path("user/{id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void deleteUser(@PathParam("id") long id) {
	}

	// Create personal workspace for the user, if it doesn't already exist. 
	@PUT
	@Path("user/{id}/personal_workspace")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void addPersonalWorkspace(@PathParam("id") long id) {
	}

	// Change password
	@POST
	@Path("user/{id}/password")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void changePassword(@PathParam("id") long id) {
	}

	@GET
	@Path("user/{id}/favorites")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void getFavorites(@PathParam("id") long id) {
		// Return a list of favorites (i.e., a list of binders).
	}
	
	// Return my teams (i.e, a list of teams I'm a member of).
	@GET
	@Path("user/{id}/teams")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Team> getTeams(@PathParam("id") long id) {
		return null;
	}
}
