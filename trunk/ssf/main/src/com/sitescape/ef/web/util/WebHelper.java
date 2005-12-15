package com.sitescape.ef.web.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sitescape.ef.web.WebKeys;

public class WebHelper {

	public static boolean isUserLoggedIn(HttpServletRequest request) {
		try {
			getRequiredUserName(request);
			return true;
		}
		catch(IllegalStateException e) {
			return false;
		}
	}
	
	public static boolean isUserLoggedIn(PortletRequest request) {
		try {
			getRequiredUserName(request);
			return true;
		}
		catch(IllegalStateException e) {
			return false;
		}
	}
	
	public static String getRequiredUserName(HttpServletRequest request) 
	throws IllegalStateException {
		if(request.getRemoteUser() != null)
			return request.getRemoteUser();
		
		if(request.getUserPrincipal() != null)
			return request.getUserPrincipal().getName();
		
		HttpSession ses = request.getSession(false);
		
		if(ses == null)
			throw new IllegalStateException("No user session");
		
		if(ses.getAttribute(WebKeys.USER_NAME) == null)
			throw new IllegalStateException("No user name in the session");
			
		return (String) ses.getAttribute(WebKeys.USER_NAME);
	}
	
	public static String getRequiredUserName(PortletRequest request) 
	throws IllegalStateException {
		if(request.getRemoteUser() != null)
			return request.getRemoteUser();
		
		if(request.getUserPrincipal() != null)
			return request.getUserPrincipal().getName();
		
		PortletSession ses = request.getPortletSession(false);
		
		if(ses == null)
			throw new IllegalStateException("No user session");
		
    	// Due to bugs in some portlet containers (eg. Liferay) as well as in 
    	// our own portlet adapter, when a user's session is invalidated or
    	// expired, the associated PortletSession if any may not be properly 
    	// notified of the change in the state of the underlying HttpSession.
    	// When this occurs, application may still be able to call getAttribute
    	// method on the PortletSession without incurring an exception. 
    	// To work around that problem, I had to add the following checking 
    	// (hack) as a way of indirectly detecting whether or not the user 
    	// session has indeed expired. Yuck, but it works. 
		if(ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE) == null)
			throw new IllegalStateException("No user name in the session");
			
		return (String) ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE);		
	}
	
	public static String getRequiredZoneName(HttpServletRequest request) 
	throws IllegalStateException {
		HttpSession ses = request.getSession(false);
		
		if(ses == null)
			throw new IllegalStateException("No user session");
		
		if(ses.getAttribute(WebKeys.ZONE_NAME) == null)
			throw new IllegalStateException("No zone name in the session");
			
		return (String) ses.getAttribute(WebKeys.ZONE_NAME);
	}
	
	public static String getRequiredZoneName(PortletRequest request) 
	throws IllegalStateException {
		PortletSession ses = request.getPortletSession(false);
		
		if(ses == null)
			throw new IllegalStateException("No user session");
		
    	// Due to bugs in some portlet containers (eg. Liferay) as well as in 
    	// our own portlet adapter, when a user's session is invalidated or
    	// expired, the associated PortletSession if any may not be properly 
    	// notified of the change in the state of the underlying HttpSession.
    	// When this occurs, application may still be able to call getAttribute
    	// method on the PortletSession without incurring an exception. 
    	// To work around that problem, I had to add the following checking 
    	// (hack) as a way of indirectly detecting whether or not the user 
    	// session has indeed expired. Yuck, but it works. 
		if(ses.getAttribute(WebKeys.ZONE_NAME, PortletSession.APPLICATION_SCOPE) == null)
			throw new IllegalStateException("No zone name in the session");
			
		return (String) ses.getAttribute(WebKeys.ZONE_NAME, PortletSession.APPLICATION_SCOPE);		
	}
	
	public static PortletSession getRequiredPortletSession(PortletRequest request) 
	throws IllegalStateException {
		PortletSession ses = request.getPortletSession(false);
		if(ses == null)
			throw new IllegalStateException("No session object is in place - Illegal request sequence.");
		else
			return ses;
	}
	
	public static HttpSession getRequiredSession(HttpServletRequest request) 
	throws IllegalStateException {
		HttpSession ses = request.getSession(false);
		if(ses == null)
			throw new IllegalStateException("No session object is in place - Illegal request sequence.");
		else
			return ses;
	}
}
