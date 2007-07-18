/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util.aopalliance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.User;

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
		User user = getProfileDao().findUserByName
			(requestContext.getUserName(), requestContext.getZoneName());
		requestContext.setUser(user);
		
		return invocation.proceed();
	}

}
