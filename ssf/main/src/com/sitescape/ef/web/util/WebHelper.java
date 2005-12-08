package com.sitescape.ef.web.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sitescape.ef.web.WebKeys;

public class WebHelper {

	public static boolean isUserLoggedIn(HttpServletRequest request) {
		if(request.getUserPrincipal() != null)
			return true;
		
		HttpSession ses = request.getSession(false);
		
		if(ses != null &&
				ses.getAttribute(WebKeys.USER_NAME) != null)
			return true;
		
		return false;
	}
	
	public static boolean isUserLoggedIn(PortletRequest request) {
		if(request.getUserPrincipal() != null)
			return true;
		
		PortletSession ses = request.getPortletSession(false);
		
		if(ses != null &&
				ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE) != null)
			return true;
		
		return false;
	}
}
