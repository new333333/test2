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

import java.io.File;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;

import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;

@Path("/file/{id}")
public class FileResource extends AbstractResource {

	// Read file content
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getFileContent(@PathParam("id") String id) {
		File f = new File("C:/junk/Sunset.jpg");
		
		if(!f.exists())
			new WebApplicationException(Response.Status.NOT_FOUND);
		
		String mt = new MimetypesFileTypeMap().getContentType(f);
		return Response.ok(f, mt).build();
	}
	
	// Read file properties
	@GET
	@Path("properties")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public FileProperties getFileProperties(@PathParam("id") String id) {
		return null;
	}
	
	// Update file content
	@PUT
	public Response putFile(@PathParam("id") String id) {
		// How do I receive file from client?
		return null;
	}
	
	// There is no method for updating file properties (at least yet). 
	// File properties are modified only indirectly when file content is modified.

	// Delete file content as well as the properties associated with the file.
	@DELETE
	public void deleteFile(@PathParam("id") String id) {
		
	}
	
	@GET
	@Path("versions")
	public List<FileVersionProperties> getFileVersions(@PathParam("id") String id) {
		return null;
	}
}
