package com.sitescape.team.web.portlet.handler;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.team.web.UnauthenticatedAccessException;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;

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
		
		// The rest of the code assumes that the user is logged in (ie, authenticated).
		/*
		if(!WebHelper.isUserLoggedIn(request))
			throw new UnauthenticatedAccessException();
		*/
		
		String userName = WebHelper.getRequiredUserName(request);
		String zoneName = WebHelper.getRequiredZoneName(request);

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
