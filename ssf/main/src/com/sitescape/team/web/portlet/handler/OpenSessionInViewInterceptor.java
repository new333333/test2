package com.sitescape.team.web.portlet.handler;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.dao.DataAccessException;

/**
 *
 */
public class OpenSessionInViewInterceptor extends 
	org.springframework.web.portlet.support.hibernate3.OpenSessionInViewInterceptor {
	public void afterCompletion(
			PortletRequest request, PortletResponse response, Object handler, Exception ex)
			throws DataAccessException {
		super.afterCompletion(request, response, handler, ex);
		
	}

}
