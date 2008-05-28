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
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.web.bind.RequestUtils;

import com.sitescape.team.servlet.portal.PortalLoginController;
import com.sitescape.team.servlet.portal.PortalLoginException;

public class LiferayLoginController extends PortalLoginController {

	private static final String SESSION_COOKIE_NAME = "JSESSIONID";
	private static final String ID_COOKIE_NAME = "ID";
	private static final String PASSWORD_COOKIE_NAME = "PASSWORD";
	private static final String LOGIN_PATH = "/c/portal/login";
	private static final String LOGOUT_PATH = "/c/portal/logout";
	private static final String PASSWORD_PATTERN = "name=\".*_password";

	@Override
	protected Cookie[] logIntoPortal(HttpServletRequest request, HttpServletResponse response, HttpClient httpClient, String username, String password) throws Exception {

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

	@Override
	protected Cookie[] logOutFromPortal(HttpServletRequest request, HttpServletResponse response, HttpClient httpClient) throws Exception {
		GetMethod getMethod = new GetMethod(LOGOUT_PATH);
		// There really is no simple way for us to find out whether the previous
		// logoff indeed invalidated an active session for the user or not (for
		// example, think about the situation where the user session has already
		// expired or didn't even exist prior to the attempt to log off).
		// But the only thing that really matters is the post-condition, that is,
		// the state in which the user is NOT logged in (possibly except as a guest).
		// It appears that the HTTP 200 status value is a good indication that
		// this post-condition is met. However, even when we receive different
		// status code, there really isn't anything we can do about it. 
		// Throwing an exception during log-off brings more problem than solution.
		// So, in this particular scenario, we will simply ignore the status code
		// from the portal and return normally. This means that the cookies from
		// the portal, if any, will be copied upstream.
		try {
			httpClient.executeMethod(getMethod);
		}
		finally {
			getMethod.releaseConnection();
		}	
		
		// Before returning a list of cookies to copy, we need to first destroy
		// a couple of cookies that came from the client. This is because, copying
		// cookies can account only for new and modified cookies, but not deleted
		// ones. One minor problem is that, we don't really know here which cookies
		// have been destroyed as the result of logging off from the portal 
		// (that is, those deleted cookies don't exist by the time this line of
		// code gets executed). Instead of trying to figure it out by comparing
		// pre and post states, we will simply use our knowledge of which cookies
		// Liferay destroys when user logs out of the system: They are ID and 
		// PASSWORD cookies (at least in the version of Liferay we're currently using).
		javax.servlet.http.Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			javax.servlet.http.Cookie cookie;
			for(int i = 0; i < cookies.length; i++) {
				cookie = cookies[i];
				if(cookie.getName().equalsIgnoreCase(ID_COOKIE_NAME) ||
						cookie.getName().equalsIgnoreCase(PASSWORD_COOKIE_NAME)) {
					cookie.setValue("");
					cookie.setMaxAge(0);
					cookie.setPath(getCookiePath());
					response.addCookie(cookie);
				}
			}
		}
		
		return httpClient.getState().getCookies();
	}

	protected String getCookiePath() {
		return "/";
	}
	
	/*
	private String getSessionID(HttpClient httpClient) {
		Cookie[] cookies = httpClient.getState().getCookies();
		if(cookies == null) return null;
		for(int i = 0; i < cookies.length; i++) {
			if(cookies[i].getName().equalsIgnoreCase(SESSION_COOKIE_NAME))
				return cookies[i].getValue();
		}
		return null;
	}

	private String getSessionID(HttpServletRequest request) {
		javax.servlet.http.Cookie[] cookies = request.getCookies();
		if(cookies == null) return null;
		for(int i = 0; i < cookies.length; i++) {
			if(cookies[i].getName().equalsIgnoreCase(SESSION_COOKIE_NAME))
				return cookies[i].getValue();
		}
		return null;
	}
	*/
	
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
