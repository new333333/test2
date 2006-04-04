package com.sitescape.ef.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
	
	public String getAuthType() {
		return null;

	}

	public Cookie[] getCookies() {
		return null;
	}

	public long getDateHeader(String name) {
		return -1;

	}

	public String getHeader(String name) {
		return null;
	}

	public Enumeration getHeaders(String name) {
		return null;

	}

	public Enumeration getHeaderNames() {
		return null;
	}

	public int getIntHeader(String name) {
		return -1;

	}

	public String getMethod() {
		throw new UnsupportedOperationException();

	}

	public String getPathInfo() {
		return null;
	}

	public String getPathTranslated() {
		return null;

	}

	public String getContextPath() {
		return contextPath;
	}
	
	public String getQueryString() {
		return null;

	}

	public String getRemoteUser() {
		return null;
	}

	public boolean isUserInRole(String role) {
		return false;
	}

	public Principal getUserPrincipal() {
		return null;
	}

	public String getRequestedSessionId() {
		return null;
	}

	public String getRequestURI() {
		return null;
	}

	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException();

	}

	public String getServletPath() {
		return null;
	}

	public HttpSession getSession(boolean create) {
		throw new UnsupportedOperationException();

	}

	public HttpSession getSession() {
		throw new UnsupportedOperationException();

	}

	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException();

	}

	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException();

	}

	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException();

	}

	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException();

	}

	public Object getAttribute(String name) {
		return attrs.get(name);
	}

	public Enumeration getAttributeNames() {
		return Collections.enumeration(attrs.keySet());
	}

	public String getCharacterEncoding() {
		return null;

	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		throw new UnsupportedOperationException();

	}

	public int getContentLength() {
		return -1;

	}

	public String getContentType() {
		return null;

	}

	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException();

	}

	public String getParameter(String name) {
		String[] values = (String[]) params.get(name);
		
		if(values != null && values.length > 0)
			return values[0];
		else
			return null;
	}

	public Enumeration getParameterNames() {
		// Note: This implementation does not precisely follow servlet specification.
		return Collections.enumeration(params.keySet());
	}

	public String[] getParameterValues(String name) {
		return (String[]) params.get(name);
	}

	public Map getParameterMap() {
		// Note: This implementation does not precisely follow servlet specification.
		return params;
	}

	public String getProtocol() {
		throw new UnsupportedOperationException();

	}

	public String getScheme() {
		throw new UnsupportedOperationException();

	}

	public String getServerName() {
		throw new UnsupportedOperationException();

	}

	public int getServerPort() {
		throw new UnsupportedOperationException();

	}

	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException();

	}

	public String getRemoteAddr() {
		throw new UnsupportedOperationException();

	}

	public String getRemoteHost() {
		throw new UnsupportedOperationException();

	}

	public void setAttribute(String name, Object o) {
		attrs.put(name, o);
	}

	public void removeAttribute(String name) {
		attrs.remove(name);
	}

	public Locale getLocale() {
		throw new UnsupportedOperationException();

	}

	public Enumeration getLocales() {
		throw new UnsupportedOperationException();

	}

	public boolean isSecure() {
		throw new UnsupportedOperationException();

	}

	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();

	}

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
}
