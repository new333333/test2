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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kablink.teaming.client.rest.model.Subscription;
import org.kablink.teaming.client.rest.model.Tag;
import org.kablink.teaming.remoting.ws.model.Binder;
import org.kablink.teaming.remoting.ws.model.FunctionMembership;
import org.kablink.teaming.remoting.ws.model.TeamMemberCollection;

public class BinderResource {

	// Copy binder
	//public long binder_copyBinder(String accessToken, long sourceId, long destinationId, boolean cascade);
	
	// Get binder by path name
	//public Binder binder_getBinderByPathName(String accessToken, String pathName, boolean includeAttachments);

	
	// Move binder
	//public void binder_moveBinder(String accessToken, long binderId, long destinationId);

	// Index binder
	//public void binder_indexBinder(String accessToken, long binderId);

	
	// Index tree
    //public Long[] binder_indexTree(String accessToken, long binderId);

	// Set definitions
	@POST
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void setDefinitions() {
		
	}
	
	// Set whether to inherit ACL (role membership) or not
	@PUT
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("inherit_acl/{inherit_acl}")
	public void setInheritAcl(@PathParam("inherit_acl") boolean inheritRoleMembership) {
		
	}
	
	// Set owner
	@PUT
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("owner/{id}")
	public void setOwner(@PathParam("id") long userId) {
		
	}

	// Set function membership
	// public void binder_setFunctionMembership(String accessToken, long binderId, FunctionMembership[] functionMemberships);

	// Get team members
	//public TeamMemberCollection binder_getTeamMembers(String accessToken, long binderId, boolean explodeGroups, int firstRecord, int maxRecords);
	
	// Set team members 
	//public void binder_setTeamMembers(String accessToken, long binderId, String[] memberNames);

	// Get subscription for the folder
	@GET
	@Path("subscription")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Subscription getSubscription(@PathParam("id") long id) {
		return null;
	}
	
	// Read a list of tags associated with the folder
	@GET
	@Path("tags")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Tag> getTags(@PathParam("id") long id) {
		return null;
	}
	
	// Add a tag to the folder
	@POST
	@Path("add_tag")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addTag(@PathParam("id") long id) {
		return null;
	}

	// Test if the user has the right to execute the specified operation on the folder
	@GET
	@Path("test_operation/{operation_name}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public boolean testOperation(@PathParam("id") long id,
			@PathParam("operation_name") String operationName) {
		return false;
	}
	
	// Get top workspace ID
	//public long binder_getTopWorkspaceId(String accessToken);

}
