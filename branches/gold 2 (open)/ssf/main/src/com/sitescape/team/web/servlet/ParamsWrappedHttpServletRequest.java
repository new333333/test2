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
		req.setAttribute(name, 0);
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		req.setCharacterEncoding(env);
	}

}
