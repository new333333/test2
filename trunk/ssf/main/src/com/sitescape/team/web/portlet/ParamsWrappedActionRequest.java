package com.sitescape.team.web.portlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;

public class ParamsWrappedActionRequest implements ActionRequest {

	private ActionRequest req; // the real one
	
	private Map<String, String[]> params; // new validated params map to be used

	public ParamsWrappedActionRequest(ActionRequest req, Map params) {
		this.req = req;
		this.params = params;
	}
	
	public Object getAttribute(String arg0) {
		return req.getAttribute(arg0);
	}

	public Enumeration getAttributeNames() {
		return req.getAttributeNames();
	}

	public String getAuthType() {
		return req.getAuthType();
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

	public String getContextPath() {
		return req.getContextPath();
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

	public PortalContext getPortalContext() {
		return req.getPortalContext();
	}

	public InputStream getPortletInputStream() throws IOException {
		return req.getPortletInputStream();
	}

	public PortletMode getPortletMode() {
		return req.getPortletMode();
	}

	public PortletSession getPortletSession() {
		return req.getPortletSession();
	}

	public PortletSession getPortletSession(boolean arg0) {
		return req.getPortletSession(arg0);
	}

	public PortletPreferences getPreferences() {
		return req.getPreferences();
	}

	public Enumeration getProperties(String arg0) {
		return req.getProperties(arg0);
	}

	public String getProperty(String arg0) {
		return req.getProperty(arg0);
	}

	public Enumeration getPropertyNames() {
		return req.getPropertyNames();
	}

	public BufferedReader getReader() throws UnsupportedEncodingException, IOException {
		return req.getReader();
	}

	public String getRemoteUser() {
		return req.getRemoteUser();
	}

	public String getRequestedSessionId() {
		return req.getRequestedSessionId();
	}

	public String getResponseContentType() {
		return req.getResponseContentType();
	}

	public Enumeration getResponseContentTypes() {
		return req.getResponseContentTypes();
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

	public Principal getUserPrincipal() {
		return req.getUserPrincipal();
	}

	public WindowState getWindowState() {
		return req.getWindowState();
	}

	public boolean isPortletModeAllowed(PortletMode arg0) {
		return req.isPortletModeAllowed(arg0);
	}

	public boolean isRequestedSessionIdValid() {
		return req.isRequestedSessionIdValid();
	}

	public boolean isSecure() {
		return req.isSecure();
	}

	public boolean isUserInRole(String arg0) {
		return req.isUserInRole(arg0);
	}

	public boolean isWindowStateAllowed(WindowState arg0) {
		return req.isWindowStateAllowed(arg0);
	}

	public void removeAttribute(String arg0) {
		req.removeAttribute(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
		req.setAttribute(arg0, arg1);
	}

	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		req.setCharacterEncoding(arg0);
	}
	

}
