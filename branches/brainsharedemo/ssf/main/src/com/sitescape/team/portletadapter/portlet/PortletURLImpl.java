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
