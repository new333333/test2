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

import java.util.HashMap;
import java.util.Map;

public class UserIdMapping {

	// Shall we limit the size of this mapping?
	private static Map userIds = new HashMap();
	
	private static final String DELIM = "#";
	
	public static Long getUserId(String zoneName, String userName) {
		return (Long) userIds.get(key(zoneName, userName));
	}
	
	public static void addEntry(String zoneName, String userName, Long userId) {
		userIds.put(key(zoneName, userName), userId);
	}
	
	public static void removeEntry(String zoneName, String userName) {
		userIds.remove(key(zoneName, userName));
	}
	
	private static String key(String zoneName, String userName) {
		return zoneName + DELIM + userName;
	}
}
