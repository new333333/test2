package com.sitescape.ef.web.portlet.handler;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.web.NoValidUserSessionException;
import com.sitescape.ef.web.WebKeys;

public class InitRequestContextInterceptor implements HandlerInterceptor {
	
	public boolean preHandle(PortletRequest request, PortletResponse response, 
			Object handler) throws Exception {
		RequestContextUtil.clearThreadContext();
		
		Boolean unathenticatedRequest = (Boolean) request.getAttribute
			(WebKeys.UNAUTHENTICATED_REQUEST);
	
		if(Boolean.TRUE.equals(unathenticatedRequest)) {
			// The framework says that this request is being made unauthenticated,
			// that is, in no particular user's context. 
			// In this case we simply pass up in the interceptor chain. 
			return true;
		}
		
    	PortletSession ses = request.getPortletSession(false);
    	
    	if(ses == null)
    		throw new NoValidUserSessionException();
    	
    	String zoneName = null;
    	String userName = null;
    	
    	try {
    		zoneName = (String) ses.getAttribute(WebKeys.ZONE_NAME, PortletSession.APPLICATION_SCOPE);
    		userName = (String) ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE);
    	}
    	catch(IllegalStateException e) {
    		// The PortletSession is invalidated.
    		throw new NoValidUserSessionException(e);
    	}

    	// Due to bugs in some portlet containers (eg. Liferay) as well as in 
    	// our own portlet adapter, when a user's session is invalidated or
    	// expired, the associated PortletSession if any may not be properly 
    	// notified of the change in the state of the underlying HttpSession.
    	// When this occurs, application may still be able to call getAttribute
    	// method on the PortletSession without incurring an exception. 
    	// To work around that problem, I had to add the following checking 
    	// (hack) as a way of indirectly detecting whether or not the user 
    	// session has indeed expired. Yuck, but it works. 
    	
    	if(zoneName == null || userName == null)
    		throw new NoValidUserSessionException();
    	
    	RequestContextUtil.setThreadContext(zoneName, userName);
		
	    return true;
	}

	public void postHandle(RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(PortletRequest request, PortletResponse response, Object handler, Exception ex) throws Exception {
		// Do not clear the thread context here to allow re-use of the context
		// for other portlets being executed as part of the single user request
		// carried out by this thread. Specifically, this prevents user objects
		// from being refetched repeatedly. The only potential danger occurs when
		// the container re-uses the same thread (which it surely does) for
		// execution of next user request. But since the thread context will
		// be reinitialized at the start of each user request, I don't think
		// it imposes a serious problem. 
    	//RequestContextUtil.clearThreadContext();
	}

}
