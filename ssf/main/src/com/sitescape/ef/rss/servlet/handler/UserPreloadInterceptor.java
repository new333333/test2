package com.sitescape.ef.rss.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.User;

public class UserPreloadInterceptor extends HandlerInterceptorAdapter {
	private ProfileDao profileDao;
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		
		if(requestContext.getUser() == null) {
			loadUser(request, requestContext);
		}

		return true;
	}
	    
	private void loadUser(HttpServletRequest request, RequestContext rc) {
		if(rc.getUserId() == null)
			throw new InternalException("User ID must be present in request context");
		
		User user = getProfileDao().loadUserOnlyIfEnabled(rc.getUserId(), rc.getZoneName());
		rc.setUser(user);
	}

}
