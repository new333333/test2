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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.kablink.teaming.remoting.rest.model.FolderEntry;

@Path("/folder_entry/{id}")
public class FolderEntryResource extends AbstractResource {
		
	@Context UriInfo uriInfo;
	
	// Read folder entry
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public FolderEntry getFolderEntry(
			@PathParam("id") long id,
			@QueryParam("attribute") List<String> attributes) {
		org.kablink.teaming.domain.FolderEntry hEntry = getFolderModule().getEntry(null, id);
		FolderEntry entry = new FolderEntry();
		entry.setId(hEntry.getId());
		entry.setTitle(hEntry.getTitle());
		entry.setDescription(hEntry.getDescription().getText());
		return entry;
	}
	
	// Update folder entry
	@PUT
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response putFolderEntry(@PathParam("id") long id) {
		return null;
	}

	// Delete folder entry
	@DELETE
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void deleteFolderEntry(@PathParam("id") long id) {
		getFolderModule().deleteEntry(null, id);
	}
	
	// Create a new file as an attachment to the folder entry.
	@POST
	@Path("create_file")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public  FileResource createFile() {
		// How ??
		return null;
	}

	// Controller resource for adding workflow to a folder entry
	@POST
	@Path("add_workflow")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void addWorkflow(@FormParam("def_id") Long workflowDefinitionId) {
		
	}
	
	// Controller resource for deleting workflow from a folder entry
	@POST
	@Path("delete_workflow")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void deleteWorkflow(@FormParam("def_id") Long workflowDefinitionId) {
		
	}
	
	// Controller resource for modifying workflow state for a folder entry
	@POST
	@Path("modify_workflow_state")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void modifyWorkflowState(@FormParam("state_id") Long workflowStateId,
			@FormParam("to_state") String workflowToState) {
		
	}
	
	// Controller resource for setting workflow response for a folder entry
	@POST
	@Path("set_workflow_response")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void setWorkflowResponse(@FormParam("state_id") Long workflowStateId,
			@FormParam("question_name") String workflowQuestionName,
			@FormParam("response") String workflowResponse) {
		
	}
	
	
	
	
	
	
	

	//---------------------------------------------------------
	@GET
	@Produces( { MediaType.TEXT_PLAIN })
	public String getFolderEntryBrowser() {
		return "Hello!";
	}
	
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String getFolderEntry() {
		// Actually, this implementation doesn't make sense. The returned string
		// must be formatted according to the media type.
		return "Hello!";
	}
}
