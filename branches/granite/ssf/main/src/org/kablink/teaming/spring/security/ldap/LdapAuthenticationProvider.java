package org.kablink.teaming.spring.security.ldap;

import org.kablink.teaming.spring.security.AuthenticationThreadLocal;
import org.springframework.security.AccountStatusException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ldap.LdapAuthoritiesPopulator;
import org.springframework.security.providers.ldap.LdapAuthenticator;

public class LdapAuthenticationProvider extends org.springframework.security.providers.ldap.LdapAuthenticationProvider {

	private String ldapConnectionConfigId;

    public LdapAuthenticationProvider(String ldapConnectionConfigId, LdapAuthenticator authenticator) {
    	super(authenticator);
		this.ldapConnectionConfigId = ldapConnectionConfigId;
    }

    public LdapAuthenticationProvider(String ldapConnectionConfigId, LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
    	super(authenticator, authoritiesPopulator);
		this.ldapConnectionConfigId = ldapConnectionConfigId;
    }
 
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	try {
    		Authentication result = super.authenticate(authentication);
    		
    		// If still here, it means that the authentication was successful.
    		AuthenticationThreadLocal.put("ldapConnectionConfigId", ldapConnectionConfigId);
    		
    		return result;
    	}
    	catch(AuthenticationException e) {
    		if(e instanceof BadCredentialsException)
    			throw new LdapBadCredentialsException((BadCredentialsException)e);
    		else
    			throw e;
    	}
    }
    
	/**
	 * This class wraps Spring's <code>BadCredentialsException</code> conceptually, while
	 * extending physically from <code>AccountStatusException</code> class instead.
	 * This allows us to carry the same semantics as BadCredentialsException, while
	 * forcing Spring's ProviderManager to propagate this exception up the call stack
	 * immediately without proceeding to the next providers in the authentication chain
	 * (see Bug 801715).
	 * 
	 * @author jong
	 *
	 */
	public static class LdapBadCredentialsException extends AccountStatusException {
		
		private static final long serialVersionUID = 1L;

		public LdapBadCredentialsException(BadCredentialsException source) {
			super("Bad credential for LDAP user", source);
		}
	}

}
