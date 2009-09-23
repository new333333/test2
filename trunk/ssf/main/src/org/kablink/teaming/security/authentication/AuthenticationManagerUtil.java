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
package org.kablink.teaming.security.authentication;

import java.util.Map;

import org.kablink.teaming.domain.User;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;


public class AuthenticationManagerUtil {

	public static User authenticate(final String zoneName,
			final String username, final String password,
			final boolean createUser, final boolean passwordAutoSynch,
			final boolean ignorePassword, final Map updates,
			final String authenticatorName)
			throws PasswordDoesNotMatchException, UserDoesNotExistException {
		return (User) RunasTemplate.runasAdmin(new RunasCallback() {
			public Object doAs() {
				return getAuthenticationManager().authenticate(zoneName,
						username, password, createUser, passwordAutoSynch,
						ignorePassword, updates, authenticatorName);
			}
		}, zoneName);
	}

	public static User authenticate(final String zoneName,
			final String username, final String password,
			final Map updates, final String authenticatorName)
			throws PasswordDoesNotMatchException, UserDoesNotExistException {
		boolean passwordAutoSynch = 
			SPropsUtil.getBoolean("portal.password.auto.synchronize", false);
		boolean ignorePassword =
			SPropsUtil.getBoolean("portal.password.ignore", true);
		boolean createUser = 
			SPropsUtil.getBoolean("portal.user.auto.create", true);
		
		return authenticate(zoneName, username, password, createUser, passwordAutoSynch, ignorePassword, updates, authenticatorName);
	}
	
	public static User authenticate(final String zoneName,
			final String username, final String password,
			final boolean passwordAutoSynch, final boolean ignorePassword,
			final String authenticatorName) throws UserDoesNotExistException,
			PasswordDoesNotMatchException {
		return (User) RunasTemplate.runasAdmin(new RunasCallback() {
			public Object doAs() {
				return getAuthenticationManager().authenticate(zoneName,
						username, password, passwordAutoSynch, ignorePassword,
						authenticatorName);
			}
		}, zoneName);
	}

	public static User authenticate(final String zoneName, final Long userId,
			final String binderId, final String privateDigest,
			final String authenticatorName) throws UserDoesNotExistException,
			DigestDoesNotMatchException {
		return (User) RunasTemplate.runasAdmin(new RunasCallback() {
			public Object doAs() {
				return getAuthenticationManager().authenticate(zoneName,
						userId, binderId, privateDigest, authenticatorName);
			}
		}, zoneName);
	}
	
	protected static AuthenticationManager getAuthenticationManager() {
		return (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
	}

}
