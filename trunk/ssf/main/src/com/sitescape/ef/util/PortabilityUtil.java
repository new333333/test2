package com.sitescape.ef.util;

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
