/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portletadapter.portlet;

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
