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
package org.kablink.teaming.asmodule.spring.security.providers;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AuthenticationProvider;

public class DummyAuthenticationProvider implements AuthenticationProvider {

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		return new DummyAuthentication(authentication);
	}

	public boolean supports(Class authentication) {
		return true;
	}

	/*
	 * This class delegates all calls to the original authentication, except that
	 * <code>getAuthorities</code> method returns an empty array instead of null
	 * when the original value is null. This is to avoid NPE in RoleVoter class.
	 */
	public static class DummyAuthentication implements Authentication {

		private static final long serialVersionUID = 1L;
		
		private Authentication authentication; // original input
		
		private Collection<? extends GrantedAuthority> grantedAuthorities;
		
		public DummyAuthentication(Authentication authentication) {
			this.authentication = authentication;

			grantedAuthorities = authentication.getAuthorities();
			if(grantedAuthorities == null)
				grantedAuthorities = new ArrayList<GrantedAuthority>();
		}

		public boolean equals(Object another) {
			return authentication.equals(another);
		}

		public String toString() {
			return authentication.toString();
		}

		public int hashCode() {
			return authentication.hashCode();
		}

		public Collection<? extends GrantedAuthority> getAuthorities() {
			return grantedAuthorities;
		}

		public String getName() {
			return authentication.getName();
		}

		public Object getCredentials() {
			return authentication.getCredentials();
		}

		public Object getDetails() {
			return authentication.getDetails();
		}

		public Object getPrincipal() {
			return authentication.getPrincipal();
		}

		public boolean isAuthenticated() {
			return authentication.isAuthenticated();
		}

		public void setAuthenticated(boolean isAuthenticated)
				throws IllegalArgumentException {
			authentication.setAuthenticated(isAuthenticated);
		}

	}
}
