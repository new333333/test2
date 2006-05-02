package com.sitescape.ef.ssfs.wck;

public class Util {
	
	static final String CONTEXT_PATH = "/ssfs"; // hard-coded...
	
	static final String URI_DELIM = "/";

	private static final String USERNAME_DELIM = ";";
	
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
	
	public static String getZoneNameFromSubject(String subject) {
		// Subject -> /users/<extended user name>
		String extendedUserName = subject.substring(7);
		return getZoneNameFromExtendedUserName(extendedUserName);
	}
	
	public static String getUserNameFromSubject(String subject) {
		// Subject -> /users/<extended user name>
		String extendedUserName = subject.substring(7);
		return getUserNameFromExtendedUserName(extendedUserName);
	}
		
	/**
	 * Parse the user input and return a String array of size two - first 
	 * element for zone name and the second for user name. 
	 * Either or both elements can be null.
	 *  
	 * @param input
	 * @return
	 */
	public static String[] parseExtendedUserNameInput(String input) {
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
