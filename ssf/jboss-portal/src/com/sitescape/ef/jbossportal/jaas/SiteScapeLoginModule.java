package com.sitescape.ef.jbossportal.jaas;

import com.sitescape.ef.portalmodule.web.security.AuthenticationManager;

import org.jboss.portal.identity.auth.IdentityLoginModule;

import javax.security.auth.login.LoginException;

/**
 * A login module that wraps JBoss Portal's default login module 
 * <code>org.jboss.portal.identity.auth.IdentityLoginModule</code>.
 * <p>
 * The side effect of using this login module is the possible automatic
 * user synchronization from the Portal user database to Aspen user database.
 *
 */
public class SiteScapeLoginModule extends IdentityLoginModule {
	
	public boolean commit() throws LoginException {
		boolean result = super.commit();
		if(result) {
			String username = getUsername();
			String password = new String((char[]) getCredentials());
			
			System.out.println("*** [" + username + "], [" + password + "]");
			
			try {
				AuthenticationManager.authenticate(null, username, password, null);
			} 
			catch (Exception e) {
				// It's unclear whether we should abort the user login or let
				// the user continue in this case. For now, we will abort it.
				throw new LoginException(e.toString());
			}
		}
		return result;
	}
}
