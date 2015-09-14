/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.spring.security.ldap;

import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.KeyShieldConfig;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

/**
 * @author jong
 *
 */
public class LdapAuthenticationProvider extends org.springframework.security.ldap.authentication.LdapAuthenticationProvider {

	private Long zoneId;
	private String ldapConnectionConfigId;
	
	/**
	 * @param authenticator
	 */
	public LdapAuthenticationProvider(Long zoneId, String ldapConnectionConfigId, LdapAuthenticator authenticator) {
		super(authenticator);
		this.zoneId = zoneId;
		this.setHideUserNotFoundExceptions(false);
		this.ldapConnectionConfigId = ldapConnectionConfigId;
	}

    public LdapAuthenticationProvider(Long zoneId, String ldapConnectionConfigId, LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
    	super(authenticator, authoritiesPopulator);
    	this.zoneId = zoneId;
    	this.setHideUserNotFoundExceptions(false);
    	this.ldapConnectionConfigId = ldapConnectionConfigId;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	try {
    		KeyShieldConfig ksc = getCoreDao().loadKeyShieldConfig(zoneId);
    		if(ksc != null && !ksc.getNonSsoAllowedForLdapUser())
    			throw new UsernameNotFoundException("Username/password authentication not allowed for LDAP users");
    			
    		LdapModule ldapModule;

    		ldapModule = WebHelper.getLdapModule();
    		if ( ldapModule != null )
    		{
    			// Has the user's password expired?
    			if ( ldapModule.hasPasswordExpired( authentication.getName(), ldapConnectionConfigId ) )
    			{
    				throw new BadCredentialsException( "Expired password" );
    			}
    		}

    		Authentication result = super.authenticate(authentication);
    		
    		// If still here, it means that the authentication was successful.
    		AuthenticationContextHolder.putLdapAuthenticationInfo("ldapConnectionConfigId", ldapConnectionConfigId);
    		
    		return result;
    	}
    	catch(BadCredentialsException e) {
    		throw new LdapBadCredentialsException((BadCredentialsException)e);
    	}
    }
    
	public String getLdapConnectionConfigId() {
		return ldapConnectionConfigId;
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
	
	private CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
}
