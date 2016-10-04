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
package org.kablink.teaming.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 * An implementation of HttpServletRequest that only handles attributes and
 * parameters. 
 * 
 * IMPORTANT: Do NOT make this class dependent upon any other class in the
 * system. In other word, do NOT import any class other than java or
 * javax classes.
 * 
 * @author jong
 *
 */
public class AttributesAndParamsOnlyServletRequest implements HttpServletRequest {

	private Map params = new HashMap();
	private Map attrs = new HashMap();
	private String contextPath;
	
	public AttributesAndParamsOnlyServletRequest(String contextPath) {
		this.contextPath = contextPath;
		params = new HashMap();
	}
	
	public AttributesAndParamsOnlyServletRequest(String contextPath, Map params) {
		this.contextPath = contextPath;
		this.params = params;
	}
	
	@Override
	public String getAuthType() {
		return null;

	}

	@Override
	public Cookie[] getCookies() {
		return null;
	}

	@Override
	public long getDateHeader(String name) {
		return -1;

	}

	@Override
	public String getHeader(String name) {
		return null;
	}

	@Override
	public Enumeration getHeaders(String name) {
		return null;

	}

	@Override
	public Enumeration getHeaderNames() {
		return null;
	}

	@Override
	public int getIntHeader(String name) {
		return -1;

	}

	@Override
	public String getMethod() {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getPathInfo() {
		return null;
	}

	@Override
	public String getPathTranslated() {
		return null;

	}

	@Override
	public String getContextPath() {
		return contextPath;
	}
	
	@Override
	public String getQueryString() {
		return null;

	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		return null;
	}

	@Override
	public String getRequestURI() {
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getServletPath() {
		return null;
	}

	@Override
	public HttpSession getSession(boolean create) {
		throw new UnsupportedOperationException();

	}

	@Override
	public HttpSession getSession() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException();

	}

	@Override
	public Object getAttribute(String name) {
		return attrs.get(name);
	}

	@Override
	public Enumeration getAttributeNames() {
		return Collections.enumeration(attrs.keySet());
	}

	@Override
	public String getCharacterEncoding() {
		return null;

	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getContentLength() {
		return -1;

	}

	@Override
	public String getContentType() {
		return null;

	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getParameter(String name) {
		String[] values = (String[]) params.get(name);
		
		if(values != null && values.length > 0)
			return values[0];
		else
			return null;
	}

	@Override
	public Enumeration getParameterNames() {
		// Note: This implementation does not precisely follow servlet specification.
		return Collections.enumeration(params.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return (String[]) params.get(name);
	}

	@Override
	public Map getParameterMap() {
		// Note: This implementation does not precisely follow servlet specification.
		return params;
	}

	@Override
	public String getProtocol() {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getScheme() {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getServerName() {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getServerPort() {
		throw new UnsupportedOperationException();

	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getRemoteAddr() {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getRemoteHost() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setAttribute(String name, Object o) {
		attrs.put(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		attrs.remove(name);
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();

	}

	@Override
	public Enumeration getLocales() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isSecure() {
		throw new UnsupportedOperationException();

	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getRealPath(String path) {
		throw new UnsupportedOperationException();

	}

	public void setParameter(String name, String value) {
		params.put(name, new String[] {value});
	}
	
	public void setParameter(String name, String[] values) {
		params.put(name, values);
	}
	
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public String getLocalAddr() {
		return null;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public int getRemotePort() {
		return 0;
	}

	public AsyncContext getAsyncContext() {
		throw new UnsupportedOperationException();
	}

	public DispatcherType getDispatcherType() {
		return DispatcherType.REQUEST;
	}

	public ServletContext getServletContext() {
		throw new UnsupportedOperationException();
	}

	public boolean isAsyncStarted() {
		return false;
	}

	public boolean isAsyncSupported() {
		return false;
	}

	public AsyncContext startAsync() {
		throw new UnsupportedOperationException();
	}

	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) {
		throw new UnsupportedOperationException();
	}

	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		throw new UnsupportedOperationException();
	}

	public Part getPart(String arg0) throws IOException, IllegalStateException,
			ServletException {
		throw new UnsupportedOperationException();
	}

	public Collection<Part> getParts() throws IOException,
			IllegalStateException, ServletException {
		throw new UnsupportedOperationException();
	}

	public void login(String arg0, String arg1) throws ServletException {
		throw new UnsupportedOperationException();
	}

	public void logout() throws ServletException {
		throw new UnsupportedOperationException();
	}
}
