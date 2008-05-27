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
package com.sitescape.team.servlet.portal.liferay;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.web.bind.RequestUtils;

import com.sitescape.team.servlet.portal.PortalLoginController;
import com.sitescape.team.servlet.portal.PortalLoginException;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.util.WebHelper;

public class LiferayLoginController extends PortalLoginController {

	private static final String PORTAL_LOGIN_FORCENEW_ALLOWED = "portal.login.forcenew.allowed";
	private static final String SESSION_COOKIE_NAME = "JSESSIONID";
	private static final String LOGIN_PATH = "/c/portal/login";
	private static final String PASSWORD_PATTERN = "name=\".*_password";

	@Override
	protected Cookie[] logIntoPortal(HttpServletRequest request, HttpServletResponse response, String portalBaseUrl, String username, String password) throws Exception {
		
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

		HttpClient httpClient = getHttpClient(portalBaseUrl);
		
		GetMethod getMethod = new GetMethod(LOGIN_PATH);
		String body = null;
		int statusCode;
		try {
			statusCode = httpClient.executeMethod(getMethod);
			if(statusCode != HttpStatus.SC_OK) {
				logger.warn("Failed to log into portal as " + username + " - " + getMethod.getStatusLine().toString());
				throw new PortalLoginException();
			}
			
			body = getMethod.getResponseBodyAsString();
		}
		finally {
			getMethod.releaseConnection();
		}
		
		String passwordFieldName = getPasswordFieldName(body);
		if(passwordFieldName == null) {
			logger.warn("Failed to log into portal as " + username + " - Cannot obtain password field name");
			throw new PortalLoginException();
		}
		
		PostMethod postMethod = new PostMethod(LOGIN_PATH);
		String location = null;
		try {
			postMethod.addParameter("cmd", "already-registered");
			//postMethod.addParameter("tabs1", "already-registered");
			postMethod.addParameter("rememberMe", "false");
			postMethod.addParameter("login", username);
			postMethod.addParameter(passwordFieldName, password);
			
			statusCode = httpClient.executeMethod(postMethod);
			if(statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
				logger.warn("Failed to log into portal as " + username + " - Unexpected status: " + postMethod.getStatusLine().toString());
				throw new PortalLoginException();
			}
			Header locationHeader = postMethod.getResponseHeader("Location");
			if(locationHeader != null)
				location = locationHeader.getValue();
		}
		finally {
			postMethod.releaseConnection();
		}
	
		if(location == null) {
			logger.warn("Failed to log into portal as " + username + " - Location header is missing");
			throw new PortalLoginException();
		}
			
		getMethod = new GetMethod(location);
		
		try {
			statusCode = httpClient.executeMethod(getMethod);
			if(statusCode != HttpStatus.SC_OK) {
				logger.warn("Failed to log into portal as " + username + " - " + getMethod.getStatusLine().toString());
				throw new PortalLoginException();
			}
		}
		finally {
			getMethod.releaseConnection();
		}
		
		return httpClient.getState().getCookies();
	}
	
	/*
	private String getSessionID(HttpClient httpClient) {
		Cookie[] cookies = httpClient.getState().getCookies();
		if(cookies == null) return null;
		for(int i = 0; i < cookies.length; i++) {
			if(cookies[i].getName().equals(SESSION_COOKIE_NAME))
				return cookies[i].getValue();
		}
		return null;
	}

	private String getSessionID(HttpServletRequest request) {
		javax.servlet.http.Cookie[] cookies = request.getCookies();
		if(cookies == null) return null;
		for(int i = 0; i < cookies.length; i++) {
			if(cookies[i].getName().equals(SESSION_COOKIE_NAME))
				return cookies[i].getValue();
		}
		return null;
	}
	*/
	
	private HttpClient getHttpClient(String portalBaseUrl) throws URIException {
		HttpURL hrl = new HttpURL(portalBaseUrl);
		HttpClient httpClient = new HttpClient();
		HostConfiguration hostConfig = httpClient.getHostConfiguration();
		hostConfig.setHost(hrl);
		return httpClient;
	}
	
	private String getPasswordFieldName(String body) {
		String passwordFieldName = null;
		if(body != null) {
			Pattern p = Pattern.compile(PASSWORD_PATTERN);
			Matcher m = p.matcher(body);
			boolean b = m.find();
			if(b) {
				passwordFieldName = body.substring(m.start() + 6, m.end());
			}
		}
		return passwordFieldName;
	}
	
	/*
	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod("http://localhost:8079/ssf/s/portalLogin");
		method.addParameter("username", "admin");
		method.addParameter("password", "test");
		httpClient.executeMethod(method);
	}*/

}
