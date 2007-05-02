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

import com.sitescape.team.domain.User;

import java.util.Map;
public interface AuthenticationManager {

	public User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch, Map updates, String authenticatorName)
		throws PasswordDoesNotMatchException, UserDoesNotExistException;

	public User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch, String authenticatorName)
	throws PasswordDoesNotMatchException, UserDoesNotExistException;

	public User authenticate(String zoneName, Long userId, String passwordDigest, 
			String authenticatorName) throws PasswordDoesNotMatchException, 
			UserDoesNotExistException;
	
	public void checkZone(String zoneName);
}
