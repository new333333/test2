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
package com.sitescape.team.portletadapter.portlet;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.portletadapter.support.KeyNames;
import com.sitescape.team.portletadapter.support.PortletInfo;

public class PortletRequestImpl implements PortletRequest, MultipartFileSupport {

	public static final WindowState ADAPTED = new WindowState("adapted");
	
	protected HttpServletRequest req;
	protected PortletInfo portletInfo;
	protected PortletContext portletContext;
	
	public PortletRequestImpl(HttpServletRequest req, PortletInfo portletInfo,
			PortletContext portletContext) {
		this.req = req;
		this.portletInfo = portletInfo;
		this.portletContext = portletContext;
	}
	
	public boolean isWindowStateAllowed(WindowState windowState) {
		throw new UnsupportedOperationException();
	}

	public boolean isPortletModeAllowed(PortletMode arg0) {
		throw new UnsupportedOperationException();
	}

	public PortletMode getPortletMode() {
		// Return same fake value. 
		return PortletMode.VIEW;
	}

	public WindowState getWindowState() {
		return ADAPTED;
	}

	public PortletPreferences getPreferences() {
		throw new UnsupportedOperationException();
	}

	public PortletSession getPortletSession() {
		return getPortletSession(true);
	}

	public PortletSession getPortletSession(boolean create) {
		PortletSession pses = null;
		
		HttpSession httpSes = req.getSession(create);

		if(create || httpSes != null) {
			pses = (PortletSession) httpSes.getAttribute(KeyNames.SESSION);
			if(pses == null) {
				pses = new PortletSessionImpl(httpSes,
						portletInfo.getName(), portletContext);
				httpSes.setAttribute(KeyNames.SESSION, pses);
			}
		}
		
		return pses;
	}

	public String getProperty(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Enumeration getProperties(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Enumeration getPropertyNames() {
		throw new UnsupportedOperationException();
	}

	public PortalContext getPortalContext() {
		throw new UnsupportedOperationException();
	}

	public String getAuthType() {
		return req.getAuthType();
	}

	public String getContextPath() {
		return req.getContextPath();
	}

	public String getRemoteUser() {
		return req.getRemoteUser();
	}

	public Principal getUserPrincipal() {
		return req.getUserPrincipal();
	}

	public boolean isUserInRole(String role) {
		return req.isUserInRole(role);
	}

	public Object getAttribute(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		
		if(name.equals(PortletRequest.USER_INFO)) {
			// Non-standard: Whether the portlet deployment descriptor contains 
			// user attributes or not, this always returns null. 
			return null;
		}
		
		return req.getAttribute(name);
	}

	public Enumeration getAttributeNames() {
		return req.getAttributeNames();
	}

	public String getParameter(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		
		return req.getParameter(name);
	}

	public Enumeration getParameterNames() {
		return req.getParameterNames();
	}

	public String[] getParameterValues(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		
		return req.getParameterValues(name);
	}

	public Map getParameterMap() {
		return req.getParameterMap();
	}

	public boolean isSecure() {
		return req.isSecure();
	}

	public void setAttribute(String name, Object obj) {
		if(name == null)
			throw new IllegalArgumentException();
		
		if(obj == null)
			removeAttribute(name);
		else
			req.setAttribute(name, obj);
	}

	public void removeAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		req.removeAttribute(name);
	}

	public String getRequestedSessionId() {
		HttpSession ses = req.getSession(false);
		if(ses != null)
			return ses.getId();
		else
			return null;
	}

	public boolean isRequestedSessionIdValid() {
		PortletSessionImpl pses = (PortletSessionImpl) this.getPortletSession(false);
		
		if(pses != null) {
			return pses.isValid();
		}
		else {
			return req.isRequestedSessionIdValid();
		}
	}

	public String getResponseContentType() {
		// Simply grab the first one (which may not be the best way). 
		return (String) portletInfo.getMimeTypes().get(0);
	}

	public Enumeration getResponseContentTypes() {
		return portletInfo.getMimeTypes().elements();
	}

	public Locale getLocale() {
		// TODO I'm not sure if this implementation is correct. 
		// I need to have better understanding of the exact mechanics of
		// how locale is supposed to be set up and used in the context of
		// real portal before rewritting this...
		
		Locale locale = req.getLocale();
		
		if(locale == null)
			locale = Locale.getDefault();
		
		return locale;
	}

	public Enumeration getLocales() {
		return req.getLocales();
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

	public HttpServletRequest getHttpServletRequest() {
		return req;
	}

	/**
	 * Return iterator over file names if they exist.
	 * Otherwise, returns <code>null</code>.
	 */
	public Iterator getFileNames() {
		if(req instanceof MultipartHttpServletRequest)
			return ((MultipartHttpServletRequest) req).getFileNames();
		else
			return null;
	}

	/**
	 * Returns Spring's MultipartFile datastsructure if relevant. 
	 * Otherwise, returns <code>null</code>.
	 */
	public MultipartFile getFile(String name) {
		if(req instanceof MultipartHttpServletRequest)
			return ((MultipartHttpServletRequest) req).getFile(name);
		else
			return null;
	}

	/**
	 * Returns file map if they exist. Otherwise returns <code>null</code>.
	 */
	public Map getFileMap() {
		if(req instanceof MultipartHttpServletRequest)
			return ((MultipartHttpServletRequest) req).getFileMap();
		else
			return null;
	}
	
	public PortletContext getPortletContext() {
		return portletContext;
	}
}
