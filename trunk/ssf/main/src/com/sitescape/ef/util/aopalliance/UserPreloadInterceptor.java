package com.sitescape.ef.util.aopalliance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.User;

public class UserPreloadInterceptor implements MethodInterceptor {
	
	private ProfileDao profileDao;
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		User user = getProfileDao().findUserByNameOnlyIfEnabled
			(requestContext.getUserName(), requestContext.getZoneName());
		requestContext.setUser(user);
		
		return invocation.proceed();
	}

}
