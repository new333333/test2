/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
