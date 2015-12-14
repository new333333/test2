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
package org.kablink.teaming.spring.security.ldap;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.web .authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class PreAuthenticatedLdapAuthenticationProvider extends LdapAuthenticationProvider {
	
    private LdapAuthenticator authenticator;
    private UserDetailsContextMapper userDetailsContextMapper;
    
    public PreAuthenticatedLdapAuthenticationProvider(Long zoneId, String ldapConnectionConfigId, LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
    	super(zoneId, ldapConnectionConfigId, authenticator, authoritiesPopulator);
    	this.authenticator = authenticator;
    }

    public PreAuthenticatedLdapAuthenticationProvider(Long zoneId, String ldapConnectionConfigId, LdapAuthenticator authenticator) {
    	super(zoneId, ldapConnectionConfigId, authenticator);
    	this.authenticator = authenticator;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(PreAuthenticatedAuthenticationToken.class, authentication,
            "Only PreAuthenticatedAuthenticationToken is supported");

        PreAuthenticatedAuthenticationToken userToken = (PreAuthenticatedAuthenticationToken)authentication;

        String accountname = userToken.getName();

        if (!StringUtils.hasLength(accountname)) {
            throw new BadCredentialsException("Empty Username");
        }

        try {
            DirContextOperations userData = this.authenticator.authenticate(authentication);

            Collection<? extends GrantedAuthority> extraAuthorities = loadUserAuthorities(userData, accountname, null);

            UserDetails user = userDetailsContextMapper.mapUserFromContext(userData, accountname, extraAuthorities);

            return createSuccessfulAuthentication(userToken, user);

        } catch (NamingException ldapAccessFailure) {
            throw new AuthenticationServiceException(ldapAccessFailure.getMessage(), ldapAccessFailure);
        }
    }
    
    public void setUserDetailsContextMapper(UserDetailsContextMapper userDetailsContextMapper) {
    	super.setUserDetailsContextMapper(userDetailsContextMapper);
    	this.userDetailsContextMapper = userDetailsContextMapper;
    }
    
    protected Authentication createSuccessfulAuthentication(PreAuthenticatedAuthenticationToken authentication,
            UserDetails user) {
        return new PreAuthenticatedAuthenticationToken(user, null, user.getAuthorities());
    }

    public boolean supports(Class authentication) {
		return (PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

