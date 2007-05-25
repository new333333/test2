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

import com.sitescape.util.ServerDetector;

/**
 * This class provides utility methods that hide minor app server-specific
 * nuances from the application.
 *  
 * @author Jong Kim
 */
public class PortabilityUtil {

	public static String getJndiName(String jndiName) {
		if(ServerDetector.isJBoss()) {
			jndiName = jndiName.replaceFirst("java:comp/env/jdbc", "java:jdbc");
			jndiName = jndiName.replaceFirst("java:comp/env/", "");
		}

		return jndiName;
	}
}
