package com.sitescape.ef.web.portlet.handler;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;

public class UserPreloadInterceptor implements HandlerInterceptor {

	private CoreDao coreDao;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	public boolean preHandle(PortletRequest request, PortletResponse response, Object handler)
    	throws Exception {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		
		// Load user only if it hasn't already been done for the current
		// requesting thread. Since this handler is called once per SSF portlet, 
		// it's possible that we end up loading the same user object multiple 
		// times when the single user interaction involves invocation of 
		// multiple SSF portlets (e.g. re-drawing of a portal page). 
		// This checking will prevent the inefficiency from happening. 
		if(requestContext.getUser() == null) {
			loadUser(requestContext);
		}
		
		return true;
	}

	public void postHandle(
			RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
	}

	public void afterCompletion(
			PortletRequest request, PortletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	private void loadUser(RequestContext reqCxt) {
		User user = getCoreDao().findUserByNameOnlyIfEnabled(
				reqCxt.getUserName(), reqCxt.getZoneName());

		reqCxt.setUser(user);
	}
}
