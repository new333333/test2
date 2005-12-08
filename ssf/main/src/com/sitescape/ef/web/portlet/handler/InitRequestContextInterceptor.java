package com.sitescape.ef.web.portlet.handler;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.web.WebKeys;

public class InitRequestContextInterceptor implements HandlerInterceptor {

	public boolean preHandle(PortletRequest request, PortletResponse response, Object handler) throws Exception {
	    
    	PortletSession ses = request.getPortletSession();
    	
		RequestContextUtil.setThreadContext((String) ses.getAttribute(WebKeys.ZONE_NAME, PortletSession.APPLICATION_SCOPE),
				(String) ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE));
		
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
