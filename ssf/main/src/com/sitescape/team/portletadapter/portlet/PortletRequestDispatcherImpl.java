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

import java.io.IOException;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

public class PortletRequestDispatcherImpl implements PortletRequestDispatcher {

	private RequestDispatcher rd;
	private PortletContext portletCtx;
	private String path;
	
	public PortletRequestDispatcherImpl(RequestDispatcher rd, 
			PortletContext portletCtx, String path) {
		this.rd = rd;
		this.portletCtx = portletCtx;
		this.path = path;
	}
	
	public void include(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		try {
			rd.include(((RenderRequestImpl)request).getHttpServletRequest(),
					((RenderResponseImpl) response).getHttpServletResponse());
		} catch (ServletException e) {
			throw new PortletException(e);
		} 
	}
}
