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
package com.sitescape.team.security.authentication;

import java.util.Map;

import com.sitescape.team.domain.User;
import com.sitescape.team.util.SpringContextUtil;

public class AuthenticationManagerUtil {

	public static User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch, Map updates, String authenticatorName)
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		AuthenticationManager am = (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
		return am.authenticate(zoneName, username, password, passwordAutoSynch, updates, authenticatorName);
	}
	
	public static User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch, String authenticatorName)
		throws UserDoesNotExistException, PasswordDoesNotMatchException {
		AuthenticationManager am = (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
		return am.authenticate(zoneName, username, password, passwordAutoSynch, authenticatorName);
	}

	public static User authenticate(String zoneName, Long userId, String passwordDigest, String authenticatorName)
	throws UserDoesNotExistException, PasswordDoesNotMatchException {
		AuthenticationManager am = (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
		return am.authenticate(zoneName, userId, passwordDigest, authenticatorName);
	}
}
