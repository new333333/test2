package com.sitescape.ef.portletadapter.portlet;

import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

public class PortletResponseImpl implements PortletResponse {

	protected HttpServletResponse res;
	protected String portletName;
	
	public PortletResponseImpl(HttpServletResponse res, String portletName) {
		this.res = res;
		this.portletName = portletName;
	}
	
	public void addProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

	public void setProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

	public String encodeURL(String path) {
		if ((path == null) ||
				(!path.startsWith("/") && (path.indexOf("://") == -1))) {

				throw new IllegalArgumentException(path);
		}
		
		return path;
	}
	
	public HttpServletResponse getHttpServletResponse() {
		return res;
	}
}
