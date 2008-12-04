/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.bridge;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.security.authentication.PasswordDoesNotMatchException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.kablink.teaming.util.SZoneConfig;


public class AuthenticationBridge {

	protected static final Log logger = LogFactory.getLog(AuthenticationBridge.class);

	public static void authenticate(String zoneName, String userName, 
			String password, Map updates, String authenticator) 
	throws UserDoesNotExistException, PasswordDoesNotMatchException {
		if(zoneName != null) {
			if(!(zoneName.equals(SZoneConfig.getDefaultZoneName()) ||
					LicenseChecker.isAuthorizedByLicense("org.kablink.teaming.module.zone.MultiZone")))
				return; // don't allow it; simply return
		}
		else {
			zoneName = SZoneConfig.getDefaultZoneName();
		}

		// Authenticate the user against SSF user database.
		AuthenticationManagerUtil.authenticate(zoneName, userName, password, updates, authenticator);
	}

	public static void authenticateEasy(String zoneName, String userName, 
			String password, Map updates, String authenticator) {
		try {
			authenticate(zoneName, userName, password, updates, authenticator);
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
