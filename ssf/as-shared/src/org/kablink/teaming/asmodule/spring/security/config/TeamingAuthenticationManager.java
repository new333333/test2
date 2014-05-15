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
package org.kablink.teaming.asmodule.spring.security.config;

import java.util.List;

import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

//public class TeamingAuthenticationManager extends NamespaceAuthenticationManager {
public class TeamingAuthenticationManager extends ProviderManager {

	protected String authenticator = "unknown";
	protected String enableKey = null;
	
    public TeamingAuthenticationManager(List<AuthenticationProvider> providers) {
    	super(providers);
    }

	public void setAuthenticator(String authenticator) {
		this.authenticator = authenticator;
	}

	public void setEnableKey(String enableKey) {
		this.enableKey = enableKey;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		AuthenticationContextHolder.setAuthenticationContext(authenticator, enableKey);
		// Additionally, store the authenticator information in the zone context holder so that the
		// information be available to application during the entire life cycle of the request, not 
		// just during authentication phase.
		ZoneContextHolder.setProperty("authenticator", authenticator);
		try {
			return super.authenticate(authentication);
		}
		finally {
			AuthenticationContextHolder.clear();
			// Do not clear zone context holder. It will be cleared by another component.
		}
    }
    
}
