package com.sitescape.ef.util.aopalliance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;

import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.User;

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
		String userName = requestContext.getUserName();
		String zoneName = requestContext.getZoneName();
		
		try {
			User user = getProfileDao().findUserByNameOnlyIfEnabled
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
