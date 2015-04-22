/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.security.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.security.jaas.KablinkPrincipal;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.Validator;


public class BasicLoginModule implements LoginModule {

	protected static Log logger = LogFactory.getLog(BasicLoginModule.class);
			
	private Subject subject;
	private CallbackHandler callbackHandler;
	private Principal principal;
	private String password;
	private String authenticator;
	protected String roleName;
	
	public boolean abort() throws LoginException {
		return true;
	}

	public boolean commit() throws LoginException {
		if (getPrincipal() != null) {
			getSubject().getPrincipals().add(getPrincipal());

			return true;
		}
		else {
			return false;
		}
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler, 
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		authenticator = (String) options.get("authenticator");
		if(Validator.isNull(authenticator))
			authenticator = LoginAudit.AUTHENTICATOR_UNKNOWN;
		roleName = (String) options.get("role");
		if(Validator.isNull(roleName))
			roleName = "users";
	}

	public boolean login() throws LoginException {
		String[] credentials = null;

		try {
			credentials = authenticate();
		}
		catch (Exception e) {
			logger.error(e);

			throw new LoginException();
		}

		if (credentials != null && credentials.length == 2) {
			setPrincipal(getSiteScapePrincipal(credentials[0]));
			setPassword(credentials[1]);

			return true;
		}
		else {
			throw new LoginException();
		}
	}

	public boolean logout() throws LoginException {
		getSubject().getPrincipals().clear();

		return true;
	}

	protected Subject getSubject() {
		return subject;
	}

	protected Principal getPrincipal() {
		return principal;
	}

	protected void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	protected Principal getSiteScapePrincipal(String userId) {
		return new KablinkPrincipal(userId);
	}

	protected String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	protected String[] authenticate() throws IOException,
			UnsupportedCallbackException, LoginException {
		NameCallback nameCallback = new NameCallback("Username: ");
		PasswordCallback passwordCallback = new PasswordCallback("Password: ",
				false);

		callbackHandler
				.handle(new Callback[] { nameCallback, passwordCallback });

		String username = nameCallback.getName();
		if (username == null)
			throw new LoginException("No user name entered");

		char[] passwordChar = passwordCallback.getPassword();

		if (passwordChar != null)
			password = new String(passwordChar);
		// Should we allow logging in with no password?
		if (password == null)
			password = "";

		try {
			String zoneName = getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName());
			
			AuthenticationManagerUtil.authenticate
			(zoneName, username, password, false, false, authenticator);

			// If still here, the authentication was successful.
			return new String[] { username, password };
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}
	
	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

}
