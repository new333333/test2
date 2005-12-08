package com.sitescape.ef.portletadapter.portlet;

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

import com.sitescape.ef.InternalException;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.portletadapter.support.PortletInfo;

public class PortletRequestImpl implements PortletRequest, MultipartFileSupport {

	protected HttpServletRequest req;
	protected PortletInfo portletInfo;
	protected PortletContext portletContext;
	protected PortletSessionImpl portletSession;
	
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
		throw new UnsupportedOperationException();
	}

	public PortletPreferences getPreferences() {
		throw new UnsupportedOperationException();
	}

	public PortletSession getPortletSession() {
		return getPortletSession(true);
	}

	public PortletSession getPortletSession(boolean create) {
		if(portletSession != null)
			return portletSession;
		
		HttpSession httpSes = req.getSession(create);
		
		if(httpSes == null)
			return null;
		
		if(create) {
			this.portletSession = new PortletSessionImpl(httpSes,
				portletInfo.getName(), portletContext);
		}
		
		return portletSession;
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
		return req.getSession().getId();
	}

	public boolean isRequestedSessionIdValid() {
		if (portletSession != null) {
			return portletSession.isValid();
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

	public Iterator getFileNames() {
		if(req instanceof MultipartHttpServletRequest)
			return ((MultipartHttpServletRequest) req).getFileNames();
		else
			throw new InternalException();
	}

	public MultipartFile getFile(String name) {
		if(req instanceof MultipartHttpServletRequest)
			return ((MultipartHttpServletRequest) req).getFile(name);
		else
			throw new InternalException();
	}

	public Map getFileMap() {
		if(req instanceof MultipartHttpServletRequest)
			return ((MultipartHttpServletRequest) req).getFileMap();
		else
			throw new InternalException();
	}
}
