package com.sitescape.ef.util;

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
