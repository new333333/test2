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
package org.kablink.teaming.security.jaas;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.ServerDetector;
import org.kablink.util.Validator;


public class KablinkLoginModule implements LoginModule {

	protected static Log logger = LogFactory.getLog(KablinkLoginModule.class);

	private LoginModule loginModule;

	public KablinkLoginModule() {
		String jassImplClassName = SPropsUtil.getString("security.jaas.impl.class", null);
		
		if (Validator.isNotNull(jassImplClassName)) {
			try {
				loginModule = (LoginModule) Class.forName(jassImplClassName).newInstance();
			}
			catch(Exception e) {
				logger.error(e);
			}
		}
		
		if(loginModule == null) {
			if (ServerDetector.isJBoss()) {
				loginModule =
					new org.kablink.teaming.security.jaas.jboss.KablinkLoginModule();
			}
			else if (ServerDetector.isTomcat()) {
				loginModule =
					new org.kablink.teaming.security.jaas.tomcat.KablinkLoginModule();
			}
			else {
				logger.error("Unrecognized container type");
				throw new RuntimeException("Unrecognized container type");
			}
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
		String enableKey = (String) options.get("enableKey");
		boolean enable = true;
		if(Validator.isNotNull(enableKey)) {
			enable = SPropsUtil.getBoolean(enableKey, true);
		}
		if(enable) {
			loginModule.initialize(subject, callbackHandler, sharedState, options);
		}
		else {
			if(logger.isDebugEnabled())
				logger.debug("Denying remote client login: It is disabled");
			throw new RuntimeException("The service is disabled");
		}
	}

	public boolean login() throws LoginException {
		return loginModule.login();
	}

	public boolean logout() throws LoginException {
		return loginModule.logout();
	}
}
