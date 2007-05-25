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

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.team.portletadapter.AdaptedPortletURL;

public class PortletURLImpl extends AdaptedPortletURL implements PortletURL {

	public PortletURLImpl(HttpServletRequest req, String portletName, boolean action) {
		super(req, portletName, action);
	}
	
	public void setWindowState(WindowState windowState) throws WindowStateException {
		// Non-standard: simply ignore
		//throw new UnsupportedOperationException();
	}

	public void setPortletMode(PortletMode portletMode) throws PortletModeException {
		// Non-standard: simply ignore
		//throw new UnsupportedOperationException();
	}
}
