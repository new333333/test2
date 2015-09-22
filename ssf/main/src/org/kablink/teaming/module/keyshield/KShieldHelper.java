/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.keyshield;

import javax.servlet.http.HttpSession;

import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.domain.LoginAudit;

/**
 * @author Jong
 *
 */
public class KShieldHelper {
	// WARNING: This variable exists solely for the purpose of testing/simulation.
	// NOT to be used on production system.
	public static volatile boolean pretendHardwareTokenIsPresent = false;

	/*
	 * Transfer KeyShield SSO related state information from the request scope to session scope.
	 */
	public static void transferStateFromRequestContextToHttpSession(HttpSession session) {
		if(Boolean.TRUE.equals(KShieldContextHolder.get(KShieldContextHolder.HARDWARE_TOKEN_MISSING)))
			session.setAttribute(KShieldContextHolder.HARDWARE_TOKEN_MISSING, Boolean.TRUE);
		
		if(Boolean.TRUE.equals(KShieldContextHolder.get(KShieldContextHolder.FORCE_LDAP_LOGIN)))
			session.setAttribute(KShieldContextHolder.FORCE_LDAP_LOGIN, Boolean.TRUE);
	}
	
	/*
	 * Return whether KeyShield has expressed that the context user is missing a hardware token.
	 */
	public static boolean isHardwareTokenMissing() {
		HttpSession session = ZoneContextHolder.getHttpSession();
		return Boolean.TRUE.equals(session.getAttribute(KShieldContextHolder.HARDWARE_TOKEN_MISSING));
	}
	
	/*
	 * Return whether the user should be forced to do LDAP login using username and password, 
	 * regardless of other SSO settings.
	 */
	public static boolean shouldLdapLoginBeForced() {
		HttpSession session = ZoneContextHolder.getHttpSession();
		return Boolean.TRUE.equals(session.getAttribute(KShieldContextHolder.FORCE_LDAP_LOGIN));
	}
	
	/*
	 * Return whether current authenticator supports KeyShield SSO or not.
	 */
	public static boolean isAuthenticatorSubjectToSso() {
		String authenticator = AuthenticationContextHolder.getAuthenticator();
		
		if(authenticator != null) {
			// Currently, only Web and WebDAV clients support KeyShield SSO.
			if(LoginAudit.AUTHENTICATOR_WEB.equals(authenticator) ||
					LoginAudit.AUTHENTICATOR_WEBDAV.equals(authenticator))
				return true;
		}
		
		return false;
	}
}
