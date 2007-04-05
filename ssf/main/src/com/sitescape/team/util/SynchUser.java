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
package com.sitescape.team.util;

import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;

/**
 * IMPORTANT: This class must NOT have any dependency on other classes in
 * the project. Keep it completely self-contained.  
 *
 */
public class SynchUser {

	/**
	 * Update, if necessary and allowed, Aspen user database with the user 
	 * information passed in.
	 * 
	 * @param zoneName may be null
	 * @param username 
	 * @param password
	 */
	public void synch(String zoneName, String username, String password) {
		if(zoneName == null)
			zoneName = SZoneConfig.getDefaultZoneName();
	
		boolean passwordAutoSynch = 
			SPropsUtil.getBoolean("portal.password.auto.synchronize", false);

		// The functionality we need happens to be implemented in the 
		// authentication manager. So we re-use it although this has nothing
		// to do with authentication of the user.
		try {
			AuthenticationManagerUtil.authenticate(zoneName, username, password,
				passwordAutoSynch, null);
		}
		catch(UserDoesNotExistException e) {
			// This means that the user doesn't exist in Aspen and the
			// configuration does not allow automatic creation of user.
			// This is not an error. 
		}
		catch(PasswordDoesNotMatchException e) {
			// This means that the user exists in Aspen but the password
			// does not match and the configuration does not allow automatic
			// update of password.
			// This is not an error. 
		}
	}
}
