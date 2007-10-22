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
package com.sitescape.team.ssfs.wck;

public class Util {
	
	static final String URI_DELIM = "/";

	private static final String SSF_CONTEXT_PATH_DEFAULT = "/ssf";
	
	private static final String USERNAME_DELIM = ";";
	
	private static String contextPath = SSF_CONTEXT_PATH_DEFAULT;
	
	private static String defaultZoneName = null;
	
	public static void setSsfContextPath(String ctxtPath) {
		Util.contextPath = ctxtPath;
	}
	
	public static String getSsfContextPath() {
		return contextPath;
	}
	
	public static void setDefaultZoneName(String defZoneName) {
		Util.defaultZoneName = defZoneName;
	}
	
	public static String getDefaultZoneName() {
		return defaultZoneName;
	}
	
	public static String makeExtendedUserName(String zoneName, String userName) {
		return zoneName + USERNAME_DELIM + userName;
	}
	
	public static String getZoneNameFromExtendedUserName(String extendedUserName) {
		String[] v = extendedUserName.split(USERNAME_DELIM);
		return v[0];
	}
	
	public static String getUserNameFromExtendedUserName(String extendedUserName) {
		String[] v = extendedUserName.split(USERNAME_DELIM);
		return v[1];
	}
	
	public static String makeSubject(String zoneName, String userName) {
		// Subject -> /users/<extended user name>

		return "/users/" + makeExtendedUserName(zoneName, userName);
	}
		
	/**
	 * Parse the user id input. If the input is invalid, it throws
	 * IllegalArgumentException.
	 * 
	 * @param userId
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String[] parseUserIdInput(String userId) throws IllegalArgumentException {
		// User id = <zonename>;</username> OR <username>
		
		// Parse the user-specified input string into zone and user names.
		String[] id = Util.parseUserIdInput2(userId);
		
		if(id[1] == null)
			throw new IllegalArgumentException("Enter user name"); // user name unspecified
		
		if(id[0] == null) { // zone name unspecified
			if(Util.getDefaultZoneName() != null) {
				// Default zone name is configured. 
				id[0] = Util.getDefaultZoneName();
			}
			else {
				// No default zone name. 
				throw new IllegalArgumentException("Enter user id in the format <zonename>;<username>");
			}
		}
		
		return id;
	}

	/**
	 * It is expected that this method is only called with valid subject 
	 * string (cf. parseUserIdInput).
	 * 
	 * @param subject
	 * @return
	 */
	public static String[] parseSubject(String subject) {
		// Subject = /users/<user id>
		String userId = subject.substring(7);
		return parseUserIdInput(userId);
	}
	
	/**
	 * Parse the user input and return a String array of size two - first 
	 * element for zone name and the second for user name. 
	 * Either or both elements can be null.
	 *  
	 * @param input
	 * @return
	 */
	private static String[] parseUserIdInput2(String input) {
		if(input == null)
			throw new IllegalArgumentException("Input must not be null");
		
		String zoneName = null;
		String userName = null;
		
		int index = input.indexOf(USERNAME_DELIM);
		if(index == -1) { // No delimiter
			userName = input.trim();
		}
		else { // Delimiter found
			zoneName = input.substring(0, index).trim();
			if(index < input.length() - 1)
				userName = input.substring(index + 1).trim();
		}
		
		if(zoneName != null && zoneName.length() == 0)
			zoneName = null;
		
		if(userName != null && userName.length() == 0)
			userName = null;
		
		return new String[] {zoneName, userName};
	}
}
