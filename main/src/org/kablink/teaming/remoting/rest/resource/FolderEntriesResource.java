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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.kablink.teaming.rest.model.FolderEntry;

@Path("/folder_entries")
public class FolderEntriesResource extends AbstractResource {

	@Context UriInfo uriInfo;

	// Resource method for browser
	/*
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<FolderEntry> getFolderEntriesBrowser() {
		List<FolderEntry> result = new ArrayList<FolderEntry>();
		FolderEntry entry = new FolderEntry();
		entry.setId(10L);
		entry.setTitle("My title");
		entry.setDescription("My description");
		result.add(entry);
		entry = new FolderEntry();
		entry.setId(20L);
		entry.setTitle("Your title");
		entry.setDescription("Your description");
		result.add(entry);
		return result;
	}
	*/
	
	// Resource method for application
	@GET
	@Produces( { MediaType.APPLICATION_XML })
	public List<FolderEntry> getFolderEntriesXML(
			@QueryParam("folderid") Long folderId,
			@QueryParam("offset") Integer offset,
			@QueryParam("maxcount") Integer maxCount) {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		
		List<FolderEntry> result = new ArrayList<FolderEntry>();
		FolderEntry entry = new FolderEntry();
		entry.setId(10L);
		entry.setTitle("My title");
		entry.setDescription("My description");
		result.add(entry);
		
		if(maxCount != null && maxCount.intValue() > 1) {
			entry = new FolderEntry();
			entry.setId(20L);
			entry.setTitle("Your title");
			entry.setDescription("Your description");
			result.add(entry);
		}
		return result;
	}
	
	@GET
	@Path("entry")
	@Produces( { MediaType.APPLICATION_XML })
	public FolderEntry getFolderEntryXML() {
		FolderEntry entry = new FolderEntry();
		entry.setId(100L);
		entry.setTitle("Nice title");
		entry.setDescription("Nice description");
		return entry;
	}
	
	
	@GET
	@Produces( { MediaType.APPLICATION_JSON })
	public List<FolderEntry> getFolderEntriesJSON(
			@QueryParam("folderid") Long folderId,
			@QueryParam("offset") Integer offset,
			@QueryParam("maxcount") Integer maxCount) {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		
		List<FolderEntry> result = new ArrayList<FolderEntry>();
		FolderEntry entry = new FolderEntry();
		entry.setId(10L);
		entry.setTitle("My title");
		entry.setDescription("My description");
		result.add(entry);
		
		if(maxCount != null && maxCount.intValue() > 1) {
			entry = new FolderEntry();
			entry.setId(20L);
			entry.setTitle("Your title");
			entry.setDescription("Your description");
			result.add(entry);
		}
		return result;
	}
	
	@GET
	@Path("entry")
	@Produces( { MediaType.APPLICATION_JSON })
	public FolderEntry getFolderEntryJSON() {
		FolderEntry entry = new FolderEntry();
		entry.setId(200L);
		entry.setTitle("Cool title");
		entry.setDescription("Cool description");
		return entry;
	}

	/*
	// Sub-resource locator
	@Path("{folderEntryId}")
	public FolderEntryResource getFolderEntryResource(@PathParam("folderEntryId") long id) {
		return new FolderEntryResource();
	}*/
}
