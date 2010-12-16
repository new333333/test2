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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kablink.teaming.remoting.rest.model.Folder;
import org.kablink.teaming.remoting.rest.model.FolderEntry;
import org.kablink.teaming.remoting.rest.model.Subscription;
import org.kablink.teaming.remoting.rest.model.Tag;
import org.kablink.teaming.remoting.rest.model.Workspace;

@Path("/workspace/{id}")
public class WorkspaceResource {

	// Read workspace (meaning returning workspace properties, not including children list)
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Workspace getWorkspace(@PathParam("id") long id) {
		return null;
	}
	
	// Update workspace (meaning updating workspace properties)
	@PUT
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response putWorkspace(@PathParam("id") long id) {
		return null;
	}
	
	// Delete workspace (meaning not only the properties but also the workspace itself and everything in it recursively)
	@DELETE
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void deleteWorkspace(@PathParam("id") long id) {
		
	}
	
	// Add subworkspace
	@POST
	@Path("add_subworkspace")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addSubWorkspace(@PathParam("id") long id) {
		return null;
	}
	
	// Add subfolder
	@POST
	@Path("add_subfolder")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addSubFolder(@PathParam("id") long id) {
		return null;
	}

	// Read subworkspaces
	@GET
	@Path("subworkspaces")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Workspace> getSubWorkspaces(@PathParam("id") long id) {
		return null;
	}
	
	// Read subfolders
	@GET
	@Path("subfolders")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Folder> getSubFolders(@PathParam("id") long id) {
		return null;
	}
	
	// Test if the user has the right to execute the specified operation on the workspace
	@GET
	@Path("test_operation/{operation_name}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public boolean testOperation(@PathParam("id") long id,
			@PathParam("operation_name") String operationName) {
		return false;
	}
	
	// Read a list of tags associated with the workspace
	@GET
	@Path("tags")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Tag> getTags(@PathParam("id") long id) {
		return null;
	}
	
	// Add a tag to the workspace
	@POST
	@Path("add_tag")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addTag(@PathParam("id") long id) {
		return null;
	}

	// Get subscription for the workspace
	@GET
	@Path("subscription")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Subscription getSubscription(@PathParam("id") long id) {
		return null;
	}
	

}
