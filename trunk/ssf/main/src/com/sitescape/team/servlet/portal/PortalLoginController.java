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
package com.sitescape.team.servlet.portal;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.util.WebHelper;

public abstract class PortalLoginController extends SAbstractController {

	private static final String PORTAL_LOGIN_OVERRIDE_SCHEME = "portal.login.override.scheme";
	private static final String PORTAL_LOGIN_OVERRIDE_HOST = "portal.login.override.host";
	private static final String PORTAL_LOGIN_OVERRIDE_PORT = "portal.login.override.port";
	private static final String PORTAL_LOGIN_SUPPORTED_METHOD = "portal.login.supported.method";
	private static final String PORTAL_LOGIN_FORCENEW_ALLOWED = "portal.login.forcenew.allowed";
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		javax.servlet.http.Cookie[] clientCookies = request.getCookies();
		
		org.apache.commons.httpclient.Cookie[] portalCookies = null;
		
		if("/portalLogin".equalsIgnoreCase(request.getPathInfo())) { // login request
			if(!isLoginAllowed(request)) {
				// Portal login is not allowed. Return silently, rather than throwing an exception.
				return null;
			}
			
			String username = RequestUtils.getStringParameter(request, "username", "");
			String password = RequestUtils.getStringParameter(request, "password", "");			
			boolean forceNew = RequestUtils.getBooleanParameter(request, "_forcenew", false);

			if(!(forceNew && SPropsUtil.getBoolean(PORTAL_LOGIN_FORCENEW_ALLOWED, false))) {
				if(WebHelper.isUserLoggedIn(request) && username.equalsIgnoreCase(WebHelper.getRequiredUserName(request))) {
					// This code is executing in the context of a session that belogs to a
					// user with the same name as the specified username (case insensitively).
					// This doesn't necessarily mean that the specified password is valid.
					// But regardless, this code will not attempt to obtain a new session
					// in this case. Set _forcenew parameter to true to force creation of 
					// a new session.
					logger.info("The user " + username + " is already logged in.");
					return null;
				}
			}
			
			HttpClient httpClient = getPortalHttpClient(request);
			if(clientCookies != null)
				copyCookies(clientCookies, httpClient);
			
			portalCookies = logIntoPortal(request, response, httpClient, username, password);
		}
		else { // logout request
			HttpClient httpClient = getPortalHttpClient(request);
			if(clientCookies != null)
				copyCookies(clientCookies, httpClient);
			
			portalCookies = logOutFromPortal(request, response, httpClient);
		}
		
		if(portalCookies != null)
			copyCookies(portalCookies, response);
		
		return null;
	}
	
	protected void copyCookies(javax.servlet.http.Cookie[] cookies, HttpClient httpClient) {
		javax.servlet.http.Cookie sourceCookie;
		org.apache.commons.httpclient.Cookie targetCookie;
		for(int i = 0; i < cookies.length; i++) {
			sourceCookie = cookies[i];
			// Do NOT copy the domain information. Instead, use localhost as the domain.
			targetCookie = new org.apache.commons.httpclient.Cookie("localhost", sourceCookie.getName(), sourceCookie.getValue());
			targetCookie.setComment(sourceCookie.getComment());
			int maxAge = sourceCookie.getMaxAge();
			if(maxAge < 0) {
				targetCookie.setExpiryDate(null);
			}
			else {
				targetCookie.setExpiryDate(new Date(System.currentTimeMillis() + maxAge*1000L));
			}
			targetCookie.setComment("_client");
			String path = sourceCookie.getPath();
			if(path == null)
				path = getCookiePath();
			targetCookie.setPath(path);
			targetCookie.setSecure(sourceCookie.getSecure());
			targetCookie.setVersion(sourceCookie.getVersion());
			httpClient.getState().addCookie(targetCookie);				
		}
	}
	
	protected abstract String getCookiePath();
	
	protected HttpClient getPortalHttpClient(HttpServletRequest request) throws URIException {
		String portalBaseUrl = getPortalBaseUrl(request);
		
		return getHttpClient(portalBaseUrl);
	}
	
	protected HttpClient getHttpClient(String portalBaseUrl) throws URIException {
		HttpURL hrl = new HttpURL(portalBaseUrl);
		HttpClient httpClient = new HttpClient();
		HostConfiguration hostConfig = httpClient.getHostConfiguration();
		hostConfig.setHost(hrl);
		return httpClient;
	}
	
	protected boolean isLoginAllowed(HttpServletRequest request) {
		String allowedMethod = SPropsUtil.getString(PORTAL_LOGIN_SUPPORTED_METHOD, "post");
		if(allowedMethod.equalsIgnoreCase("post")) {
			if("post".equalsIgnoreCase(request.getMethod()))
				return true;
			else
				return false;
		}
		else if(allowedMethod.equalsIgnoreCase("all")) {
			return true;
		}
		else if(allowedMethod.equalsIgnoreCase("none")) {
			return false;
		}
		else {
			logger.warn("Illegal value " + allowedMethod + " for " + PORTAL_LOGIN_SUPPORTED_METHOD + " property");
			return false;
		}		
	}
	
	protected abstract org.apache.commons.httpclient.Cookie[] logIntoPortal(HttpServletRequest request,
		    HttpServletResponse response, HttpClient httpClient, String username, String password) throws Exception;
			
	protected abstract org.apache.commons.httpclient.Cookie[] logOutFromPortal(HttpServletRequest request,
		    HttpServletResponse response, HttpClient httpClient) throws Exception;
			
	protected String getPortalBaseUrl(HttpServletRequest request) {
		String scheme = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_SCHEME, "");
		if(scheme.equals(""))
			scheme = "http";
		String host = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_HOST, "");
		if(host.equals(""))
			host = request.getServerName();
		String port = SPropsUtil.getString(PORTAL_LOGIN_OVERRIDE_PORT, "");
		if(port.equals(""))
			port = String.valueOf(request.getServerPort());
		
		return scheme + "://" + host + ":" + port;
	}
	
	protected void copyCookies(org.apache.commons.httpclient.Cookie[] cookies, HttpServletResponse response) {
		org.apache.commons.httpclient.Cookie sourceCookie;
		javax.servlet.http.Cookie targetCookie;
		
		for(int i = 0; i < cookies.length; i++) {
			sourceCookie = cookies[i];
			if(sourceCookie.getComment() != null && sourceCookie.getComment().equals("_client")) {
				// This cookie is a copy of the client cookie passed through to the portal.
				// We should not copy it back. Skip it.
				continue;
			}
			targetCookie = new javax.servlet.http.Cookie(sourceCookie.getName(), sourceCookie.getValue());
			// Copy the information from the source cookie to the target cookie.
			// Do NOT ever copy the domain information. 
			targetCookie.setComment(sourceCookie.getComment());
			// Due to the lack of method on javax.servlet.http.Cookie that takes
			// absolute expiry date, we lose some precision while translating the
			// cookie expiration attribute between the source and target cookies.
			// But that discrepency should be no more than a few seconds.
			int maxAge = -1;
			if(sourceCookie.getExpiryDate() != null) {
				maxAge = (int) ((sourceCookie.getExpiryDate().getTime() - System.currentTimeMillis()) / 1000L);
				if(maxAge < 0)
					maxAge = 0;
			}
			targetCookie.setMaxAge(maxAge);
			targetCookie.setPath(sourceCookie.getPath());
			targetCookie.setSecure(sourceCookie.getSecure());
			targetCookie.setVersion(sourceCookie.getVersion());
			response.addCookie(targetCookie);				
		}
	}
}
