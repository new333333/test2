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

import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

/**
 * @author jong
 *
 */
public class VibeClient {

	private static final MediaType[] ACCEPTABLE_MEDIA_TYPES_DEFAULT = new MediaType[] {MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE};
	
	private String baseUrl;
	private Client client;
	private MediaType[] acceptableMediaTypes = ACCEPTABLE_MEDIA_TYPES_DEFAULT;
	
	public VibeClient(String baseUrl, String username, String password)
	{
		if(!baseUrl.endsWith("/"))
			baseUrl += "/";
		
	     DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
	     Map<String,Object> props = config.getProperties();
	     props.put(ApacheHttpClientConfig.PROPERTY_HANDLE_COOKIES, Boolean.TRUE);
	     props.put(ApacheHttpClientConfig.PROPERTY_PREEMPTIVE_AUTHENTICATION, Boolean.TRUE);
	     //props.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, Boolean.TRUE);
	     
	     config.getState().setCredentials(null, null, -1, username, password);
	     
	     ApacheHttpClient c = ApacheHttpClient.create(config);
	     
	     this.baseUrl = baseUrl;
	     this.client = c;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public Client getClient() {
		return client;
	}
	
	/**
	 * Return an array of acceptable media types. The returned array should never be modified.
	 * @return
	 */
	public MediaType[] getAcceptableMediaTypes() {
		return acceptableMediaTypes;
	}

	public void setAcceptableMediaTypes(MediaType[] acceptableMediaTypes) {
		this.acceptableMediaTypes = acceptableMediaTypes;
	}

	public void destroy() {
		client.destroy();
	}

	public VibeApiImpl createClient() {
		return new VibeApiImpl(this);
	}
}
