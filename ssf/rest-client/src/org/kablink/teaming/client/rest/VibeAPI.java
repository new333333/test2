/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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

package org.kablink.teaming.client.rest;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.kablink.teaming.rest.model.FileProperties;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @author jong
 *
 */
public class VibeAPI {
	
	private static MediaType[] DEFAULT_MEDIA_TYPES = new MediaType[] {MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE};
	
	private static final String FILE_TEMPLATE_BY_NAME = "rest/file/name/{entityType}/{entityId}/{filename}";
	
	public static FileProperties readFileProperties(VibeConnection conn, String entityType, long entityId, String filename) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_NAME).path("properties").build(entityType, entityId, filename);
		WebResource r = c.resource(resourceUri);
		return r.accept(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(FileProperties.class);
	}
	
	public static void main(String[] args) {
		VibeConnection conn = VibeConnection.getInstance("http://localhost:8079", "admin", "admin");
		FileProperties fp = readFileProperties(conn, "folderEntry", 13, "debug5.txt");
		FileProperties fp2 = readFileProperties(conn, "folderEntry", 13, "debug5.txt");
		conn.destroy();
	}

}
