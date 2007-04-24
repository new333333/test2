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

import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.util.SZoneConfig;

public class BasicLoginModule implements LoginModule {

	protected static Log logger = LogFactory.getLog(BasicLoginModule.class);

	private Subject subject;
	private CallbackHandler callbackHandler;
	private Principal principal;
	private String password;
	
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
		return new SiteScapePrincipal(userId);
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
			AuthenticationManagerUtil.authenticate(SZoneConfig
					.getDefaultZoneName(), username, password, false);

			// If still here, the authentication was successful.
			return new String[] { username, password };
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}

		return null;
	}

}
