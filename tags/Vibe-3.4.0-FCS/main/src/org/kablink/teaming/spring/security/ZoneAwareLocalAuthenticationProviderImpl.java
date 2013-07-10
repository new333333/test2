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
package org.kablink.teaming.spring.security;

import java.util.HashMap;

import org.kablink.teaming.domain.User;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.security.authentication.PasswordDoesNotMatchException;
import org.kablink.teaming.security.authentication.UserAccountNotActiveException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

public class ZoneAwareLocalAuthenticationProviderImpl implements ZoneAwareLocalAuthenticationProvider {
	
	protected String zoneName;
	
	public void setZoneName(String zoneName) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		this.zoneName = zoneName;
	}

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		return doAuthenticate(authentication);
	}

	public boolean supports(Class authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

	protected Authentication doAuthenticate(Authentication authentication) throws AuthenticationException {
		try {
			User user = AuthenticationManagerUtil.authenticate(zoneName,
					(String) authentication.getName(), (String) authentication.getCredentials(),
					false, false, false, new HashMap(), null);

			return outputAuthentication(authentication, user, null);
		} catch(PasswordDoesNotMatchException e) {
			throw new BadCredentialsException("Bad credentials", e);
		} catch(UserDoesNotExistException e) {
			throw new UsernameNotFoundException("No such user", e);
		} catch(UserAccountNotActiveException e) {
			throw new UsernameNotFoundException("User account disabled or deleted", e);
		}	
	}

	protected Authentication outputAuthentication(Authentication authentication, User user, Object extraInfo) {
		UserDetails details = new SsfContextMapper.SsfUserDetails(user.getName());
		UsernamePasswordAuthenticationToken result = newUsernamePasswordAuthenticationToken(user, extraInfo, details, authentication.getCredentials(), new GrantedAuthority[0]);
		result.setDetails(details);
		return result;	
	}
	
	protected UsernamePasswordAuthenticationToken newUsernamePasswordAuthenticationToken
	(User user, Object extraInfo, Object principal, Object credentials, GrantedAuthority[] authorities) {
		return new SynchNotifiableAuthenticationImpl(principal, credentials, authorities);
	}
	
	public static class SynchNotifiableAuthenticationImpl extends UsernamePasswordAuthenticationToken implements SynchNotifiableAuthentication {
	    public SynchNotifiableAuthenticationImpl(Object principal, Object credentials, GrantedAuthority[] authorities) {
	    	super(principal, credentials, authorities);
	    }
		public void synchDone() {}
	}
}
