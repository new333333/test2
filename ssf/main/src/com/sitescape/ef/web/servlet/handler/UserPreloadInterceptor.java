package com.sitescape.ef.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.web.WebKeys;

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
		
		if(requestContext.getUser() == null) {
			loadUser(request, requestContext);
		}

		return true;
	}
	    
	private void loadUser(HttpServletRequest request, RequestContext reqCxt) {
		User user;
		HttpSession ses = request.getSession(false);
	   	Long userId = (Long)ses.getAttribute(WebKeys.USER_ID);
		if (userId == null) { 
			user = getCoreDao().findUserByNameOnlyIfEnabled(
					reqCxt.getUserName(), reqCxt.getZoneName());
			ses.setAttribute(WebKeys.USER_ID, user.getId());
		} else {
			user = getCoreDao().loadUserOnlyIfEnabled(userId, reqCxt.getZoneName());
		}

		reqCxt.setUser(user);
	}

}
