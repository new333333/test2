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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.User;

public class UserReloadInterceptor implements MethodInterceptor {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private ProfileDao profileDao;
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			return invocation.proceed();
		}
		catch(Throwable t) {
			if(betterReload(t)) // Determine if we need to reload user object
				reloadUser();
			
			throw t; // Rethrow the original exception
		}
	}

	/**
	 * Default behavior is as with EJB which Springframework also follows as
	 * default: rollback on unchecked exception. Additionally attempt to 
	 * rollback on Error. Consistent with Spring's TransactionTemplate's behavior.
	 */
	protected boolean betterReload(Throwable ex) {
		// If we use customized (i.e., non-default) behavior for exception
		// handling in conjunction with Spring's transaction proxy, then
		// this implementation may need to be changed. 
		
		return (ex instanceof RuntimeException || ex instanceof Error);
	}
	
	private void reloadUser() {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		// If the interceptor was triggered by failed authentication request
		// itself, request context could be null. Simply return in that case. 
		if(requestContext == null)
			return;
		// If the request context didn't previously have an user object
		// associated with it, don't try reloading it. 
		if(requestContext.getUser() == null)
			return;
		String userName = requestContext.getUserName();
		String zoneName = requestContext.getZoneName();
		
		try {
			User user = getProfileDao().findUserByName
			(userName, zoneName);
			
			requestContext.setUser(user);
		}
		catch(Throwable t) {
			// If any error during reloading, at least clear the old user 
			// object in the request context.
			requestContext.setUser(null);
			// Do not rethrow this exception, because it will cause the 
			// interceptor to throw this exception rather than the original
			// exception that came from the invocation target. Out of the two,
			// this is less important an exception. We will log it though. 
			logger.error("Failed to reload user " + userName 
					+ " for zone " + zoneName, t);
		}
	}
}
