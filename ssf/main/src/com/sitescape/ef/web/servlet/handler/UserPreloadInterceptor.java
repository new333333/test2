package com.sitescape.ef.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;

public class UserPreloadInterceptor extends HandlerInterceptorAdapter {
	private CoreDao coreDao;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		
		// TODO testing for now...
		try {
		User user = getCoreDao().findUserByNameOnlyIfEnabled
			(requestContext.getUserName(), requestContext.getZoneName());
		
		requestContext.setUser(user);
		}
		catch(Exception e) {
			// TODO This should be removed. 
			User user = getCoreDao().findUserByNameOnlyIfEnabled("wf_admin", "liferay.com");
			requestContext.setUser(user);
		}

		return true;
	}
	    

}
