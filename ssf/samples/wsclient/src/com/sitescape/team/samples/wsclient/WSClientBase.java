/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.samples.wsclient;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.configuration.FileProvider;

import com.sitescape.team.client.ws.WebServiceClientUtil;

public abstract class WSClientBase {

	protected static String host; // optional - default to localhost
	protected static String port; // optional - default to 8080
	protected static String username; // required
	protected static String password; // required
	protected static boolean authBasic; // optional - default to basic (the other available value is wss)
	
	protected static void getSystemProperties() {
		host = System.getProperty("host", "localhost");
		
		port = System.getProperty("port", "8080");
		
		username = System.getProperty("username");
		if(username == null)
			throw new IllegalArgumentException("username must be specified with -D switch");
		
		password = System.getProperty("password");
		if(password == null)
			throw new IllegalArgumentException("password must be specified with -D switch");
		
		String authMethod = System.getProperty("authmethod", "basic");
		if(authMethod.equalsIgnoreCase("basic"))
			authBasic = true;
		else if(authMethod.equalsIgnoreCase("wss"))
			authBasic = false;
		else
			throw new IllegalArgumentException("Illegal authmethod value: " + authMethod);			
	}
	
	protected static EngineConfiguration getEngineConfiguration() {
		// If using basic auth, there is no custom engine configuration to use.
		// If using WS-Security, the engine must be configured with appropriate handlers
		// specified in the config file.
		if(authBasic) {
			return null;
		}
		else {
			// Make sure that the config file is accessible to this program.
			return  new FileProvider("client_deploy-wss.wsdd");
		}
	}
	
	protected static String getEndpointAddress(String serviceName) {
		// The endpoint is different depending on the authentication mechanism to be used.
		StringBuilder sb = new StringBuilder("http://");
		sb.append(host);
		if(port != null)
			sb.append(":").append(port);
		if(authBasic)
			sb.append("/ssf/ws/");
		else
			sb.append("/ssr/secure/ws/");
		sb.append(serviceName);
		return sb.toString();
	}
	
	protected static void setUserCredential(Call call) {
		if(authBasic) {
			WebServiceClientUtil.setUserCredentialBasicAuth(call, username, password);
		}
		else {
			WebServiceClientUtil.setUserCredentialWSSecurity(call, username, password, true);
		}
	}
}
