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
package com.sitescape.team.security.jaas;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.util.ServerDetector;

public class SiteScapeLoginModule implements LoginModule {

	protected static Log logger = LogFactory.getLog(SiteScapeLoginModule.class);

	private LoginModule loginModule;

	public SiteScapeLoginModule() {
		if (ServerDetector.isJBoss()) {
			loginModule =
				new com.sitescape.team.security.jaas.jboss.SiteScapeLoginModule();
		}
		else if (ServerDetector.isTomcat()) {
			loginModule =
				new com.sitescape.team.security.jaas.tomcat.SiteScapeLoginModule();
		}
		else {
			logger.warn("Unrecognized container type");
			loginModule = 
				new com.sitescape.team.security.jaas.SiteScapeLoginModule();
		}
		
		if(logger.isDebugEnabled())
			logger.debug(loginModule.getClass().getName());
	}
	
	public boolean abort() throws LoginException {
		return loginModule.abort();
	}

	public boolean commit() throws LoginException {
		return loginModule.commit();
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler, 
			Map<String, ?> sharedState, Map<String, ?> options) {
		loginModule.initialize(subject, callbackHandler, sharedState, options);
	}

	public boolean login() throws LoginException {
		return loginModule.login();
	}

	public boolean logout() throws LoginException {
		return loginModule.logout();
	}
}
