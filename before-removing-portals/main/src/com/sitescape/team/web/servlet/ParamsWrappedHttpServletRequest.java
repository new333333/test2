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
package com.sitescape.team.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ParamsWrappedHttpServletRequest implements HttpServletRequest {

	private HttpServletRequest req; // the real one
	
	private Map<String, String[]> params; // new validated params map to be used
	
	public ParamsWrappedHttpServletRequest(HttpServletRequest req, Map params) {
		this.req = req;
		this.params = params;
	}
	
	public String getAuthType() {
		return req.getAuthType();
	}

	public String getContextPath() {
		return req.getContextPath();
	}

	public Cookie[] getCookies() {
		return req.getCookies();
	}

	public long getDateHeader(String name) {
		return req.getDateHeader(name);
	}

	public String getHeader(String name) {
		return req.getHeader(name);
	}

	public Enumeration getHeaderNames() {
		return req.getHeaderNames();
	}

	public Enumeration getHeaders(String name) {
		return req.getHeaders(name);
	}

	public int getIntHeader(String name) {
		return req.getIntHeader(name);
	}

	public String getMethod() {
		return req.getMethod();
	}

	public String getPathInfo() {
		return req.getPathInfo();
	}

	public String getPathTranslated() {
		return req.getPathTranslated();
	}

	public String getQueryString() {
		return req.getQueryString();
	}

	public String getRemoteUser() {
		return req.getRemoteUser();
	}

	public String getRequestURI() {
		return req.getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return req.getRequestURL();
	}

	public String getRequestedSessionId() {
		return req.getRequestedSessionId();
	}

	public String getServletPath() {
		return req.getServletPath();
	}

	public HttpSession getSession() {
		return req.getSession();
	}

	public HttpSession getSession(boolean create) {
		return req.getSession(create);
	}

	public Principal getUserPrincipal() {
		return req.getUserPrincipal();
	}

	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdFromURL() {
		return req.isRequestedSessionIdFromURL();
	}

	public boolean isRequestedSessionIdFromUrl() {
		return req.isRequestedSessionIdFromUrl();
	}

	public boolean isRequestedSessionIdValid() {
		return req.isRequestedSessionIdValid();
	}

	public boolean isUserInRole(String role) {
		return req.isUserInRole(role);
	}

	public Object getAttribute(String name) {
		return req.getAttribute(name);
	}

	public Enumeration getAttributeNames() {
		return req.getAttributeNames();
	}

	public String getCharacterEncoding() {
		return req.getCharacterEncoding();
	}

	public int getContentLength() {
		return req.getContentLength();
	}

	public String getContentType() {
		return req.getContentType();
	}

	public ServletInputStream getInputStream() throws IOException {
		return req.getInputStream();
	}

	public Locale getLocale() {
		return req.getLocale();
	}

	public Enumeration getLocales() {
		return req.getLocales();
	}

	public String getParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		String[] value = params.get(name);
		if(value != null && value.length > 0)
			return value[0];
		else
			return null;
	}

	public Map getParameterMap() {
		return Collections.unmodifiableMap(params);
	}

	public Enumeration getParameterNames() {
		return Collections.enumeration(params.keySet());
	}

	public String[] getParameterValues(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return params.get(name);
	}

	public String getProtocol() {
		return req.getProtocol();
	}

	public BufferedReader getReader() throws IOException {
		return req.getReader();
	}

	public String getRealPath(String path) {
		return req.getRealPath(path);
	}

	public String getRemoteAddr() {
		return req.getRemoteAddr();
	}

	public String getRemoteHost() {
		return req.getRemoteHost();
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		return req.getRequestDispatcher(path);
	}

	public String getScheme() {
		return req.getScheme();
	}

	public String getServerName() {
		return req.getServerName();
	}

	public int getServerPort() {
		return req.getServerPort();
	}

	public boolean isSecure() {
		return req.isSecure();
	}

	public void removeAttribute(String name) {
		req.removeAttribute(name);
	}

	public void setAttribute(String name, Object o) {
		req.setAttribute(name, o);
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		req.setCharacterEncoding(env);
	}

}
