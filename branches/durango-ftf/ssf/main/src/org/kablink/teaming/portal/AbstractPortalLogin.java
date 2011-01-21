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
package org.kablink.teaming.portal;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SPropsUtil;


public abstract class AbstractPortalLogin implements PortalLogin {

	protected Log logger = LogFactory.getLog(getClass());

	private static final String PORTAL_LOGIN_OVERRIDE_SCHEME = "portal.login.override.scheme";
	private static final String PORTAL_LOGIN_OVERRIDE_HOST = "portal.login.override.host";
	private static final String PORTAL_LOGIN_OVERRIDE_PORT = "portal.login.override.port";

	public void loginPortal(HttpServletRequest request, HttpServletResponse response, 
			String username, String password, boolean remember) throws Exception {
		javax.servlet.http.Cookie[] clientCookies = request.getCookies();

		HttpClient httpClient = getPortalHttpClient(request);
		if(clientCookies != null)
			copyClientCookies(request, clientCookies, httpClient);
		
		org.apache.commons.httpclient.Cookie[] portalCookies = 
			logIntoPortal(request, response, httpClient, username, password, remember);
		
		copyPortalCookies(clientCookies, portalCookies, response);
	}

	public void logoutPortal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		javax.servlet.http.Cookie[] clientCookies = request.getCookies();

		HttpClient httpClient = getPortalHttpClient(request);
		if(clientCookies != null)
			copyClientCookies(request, clientCookies, httpClient);
		
		org.apache.commons.httpclient.Cookie[] portalCookies = 
			logOutFromPortal(request, response, httpClient);
		
		copyPortalCookies(clientCookies, portalCookies, response);
	}

	public void touchPortal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		javax.servlet.http.Cookie[] clientCookies = request.getCookies();

		HttpClient httpClient = getPortalHttpClient(request);
		if(clientCookies != null)
			copyClientCookies(request, clientCookies, httpClient);
		
		org.apache.commons.httpclient.Cookie[] portalCookies = 
			touchIntoPortal(request, response, httpClient);
		
		copyPortalCookies(clientCookies, portalCookies, response);
	}

	protected HttpClient getPortalHttpClient(HttpServletRequest request) throws URIException {
		String portalBaseUrl = getPortalBaseUrl(request);
		
		return getHttpClient(portalBaseUrl);
	}
	
	protected void copyClientCookies(HttpServletRequest request, javax.servlet.http.Cookie[] cookies, HttpClient httpClient) {
		javax.servlet.http.Cookie sourceCookie;
		org.apache.commons.httpclient.Cookie targetCookie;
		for(int i = 0; i < cookies.length; i++) {
			sourceCookie = cookies[i];
			// Usually when the client sends cookies back to the server, they only send
			// the name and the value pairs. All other information associated with the 
			// cookies (eg. expiration date, domain, path, etc.) are retained and used
			// only by the client. We need to take it into account as we make copies
			// of those cookies here. 
			
			// Apache HttpClient requires domain name to be set even for inbound cookies.
			String domain = sourceCookie.getDomain();
			if(domain == null)
				domain = request.getServerName();
			targetCookie = new org.apache.commons.httpclient.Cookie(domain, sourceCookie.getName(), sourceCookie.getValue());

			// This is our internal mark that this particular cookie was copied from a client cookie.
			targetCookie.setComment("_client");

			int maxAge = sourceCookie.getMaxAge();
			if(maxAge < 0) {
				targetCookie.setExpiryDate(null);
			}
			else {
				targetCookie.setExpiryDate(new Date(System.currentTimeMillis() + maxAge*1000L));
			}
			
			String path = sourceCookie.getPath();
			if(path == null)
				path = getCookiePath();
			targetCookie.setPath(path);
			
			//targetCookie.setSecure(sourceCookie.getSecure());
			//targetCookie.setVersion(sourceCookie.getVersion());
			
			httpClient.getState().addCookie(targetCookie);				
		}
	}

	protected abstract org.apache.commons.httpclient.Cookie[] logIntoPortal(HttpServletRequest request,
		    HttpServletResponse response, HttpClient httpClient, String username, String password, boolean remember) throws Exception;
			
	protected abstract org.apache.commons.httpclient.Cookie[] logOutFromPortal(HttpServletRequest request,
		    HttpServletResponse response, HttpClient httpClient) throws Exception;
			
	protected abstract org.apache.commons.httpclient.Cookie[] touchIntoPortal(HttpServletRequest request,
		    HttpServletResponse response, HttpClient httpClient) throws Exception;
			
	protected String getPortalBaseUrl(HttpServletRequest request) {
		String scheme = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_SCHEME, "");
		if(scheme.equals(""))
			scheme = "http";
		
		String host = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_HOST, "");
		if(host.equals(""))
			host = "127.0.0.1"; // use local loopback address

		String port = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_PORT, "");
		if(port.equals(""))
			port = String.valueOf(request.getServerPort());
		
		String url = scheme + "://" + host + ":" + port;
		if(logger.isTraceEnabled())
			logger.trace("portal base url: " + url);
		
		return url;
	}
	
	protected void copyPortalCookies(javax.servlet.http.Cookie[] clientCookies, 
			org.apache.commons.httpclient.Cookie[] portalCookies, 
			HttpServletResponse response) {
		org.apache.commons.httpclient.Cookie portalCookie;
		javax.servlet.http.Cookie targetCookie;
		
		// Step 1: Copy portal cookies into the response. This accounts for both
		// newly added and modified cookies.
		if(portalCookies != null) {
			for(int i = 0; i < portalCookies.length; i++) {
				portalCookie = portalCookies[i];
				
				if(portalCookie.getComment() != null && portalCookie.getComment().equals("_client")) {
					// What this means is that, this particular cookie was sent by the client
					// to the portal AND the portal did not modify it. Therefore we must not
					// include this cookie in the response (ie, leave it as is on the client side).
					continue;
				}
				
				targetCookie = new javax.servlet.http.Cookie(portalCookie.getName(), portalCookie.getValue());
				
				// Copy everything from the portal cookie to the target cookie, EXCEPT
				// for the domain information.
				
				targetCookie.setComment(portalCookie.getComment());
				
				// Due to the lack of method on javax.servlet.http.Cookie that takes
				// absolute expiry date, we lose some precision while translating the
				// cookie expiration attribute between the source and target cookies.
				// But that discrepency should be no more than a few seconds.
				int maxAge = -1;
				if(portalCookie.getExpiryDate() != null) {
					maxAge = (int) ((portalCookie.getExpiryDate().getTime() - System.currentTimeMillis()) / 1000L);
					if(maxAge < 0)
						maxAge = 0;
				}
				targetCookie.setMaxAge(maxAge);
				
				targetCookie.setPath(portalCookie.getPath());
				
				targetCookie.setSecure(portalCookie.getSecure());
				
				targetCookie.setVersion(portalCookie.getVersion());
				
				response.addCookie(targetCookie);				
			}
		}
		
		// Step 2: For each client cookie, check if it still exists in the list of portal
		// cookies. If not, it means that the particular cookie was destroyed as result
		// of the interaction with the portal, and we need to reflect that in the response.
		if(clientCookies != null) {
			javax.servlet.http.Cookie clientCookie;
			outer:
			for(int i = 0; i < clientCookies.length; i++) {
				clientCookie = clientCookies[i];
				// check if the client cookie is still found in the portal cookies.
				if(portalCookies != null) {
					for(int j = 0; j < portalCookies.length; j++) {
						if(clientCookie.getName().equalsIgnoreCase(portalCookies[j].getName()))
							continue outer; // found it
					}
				}
				// no match
				targetCookie = new javax.servlet.http.Cookie(clientCookie.getName(), "");
				targetCookie.setMaxAge(0);
				targetCookie.setPath(getCookiePath());
				response.addCookie(targetCookie);
			}
		}
	}

	protected abstract String getCookiePath();
	
	protected HttpClient getHttpClient(String portalBaseUrl) throws URIException {
		HttpURL hrl = new HttpURL(portalBaseUrl);
		HttpClient httpClient = new HttpClient();
		HostConfiguration hostConfig = httpClient.getHostConfiguration();
		hostConfig.setHost(hrl);
		return httpClient;
	}
	
}
