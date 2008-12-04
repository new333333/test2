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
package org.kablink.teaming.web.portlet;

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
