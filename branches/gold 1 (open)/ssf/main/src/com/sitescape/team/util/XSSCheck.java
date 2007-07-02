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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.util.StringPool;

public class XSSCheck {

	private static final String MODE_DISALLOW = "disallow"; // default mode
	private static final String MODE_GROUPS = "groups";
	private static final String MODE_STRIP = "strip";
	
	private static final String PATTERN_STR = SPropsUtil.getString("xss.regexp.pattern");
	private static final Pattern PATTERN = Pattern.compile(PATTERN_STR);
	private static final boolean ENABLE = SPropsUtil.getBoolean("xss.check.enable");
	private static String MODE = SPropsUtil.getString("xss.check.mode");
	private static final String[] GROUPS = SPropsUtil.getStringArray("xss.groups", ",");
	
	static {
		// We do this not only to validate the input mode but also to enable
		// simple reference comparison for modes. 
		if(MODE.equals(MODE_GROUPS))
			MODE = MODE_GROUPS;
		else if(MODE.equals(MODE_STRIP))
			MODE = MODE_STRIP;
		else
			MODE = MODE_DISALLOW;
	}
	
	/**
	 * Detect potential XSS threat. The exact behavior of this method
	 * is determined by the configuration settings in ssf.properties. 
	 * 
	 * @param input input data as string
	 * @return output data as string
	 * @throws XSSCheckException
	 */
	public static String check(String input) throws XSSCheckException {
		if(ENABLE)
			return doCheck(input);
		else
			return input;
	}
	
	/**
	 * Detect potential XSS threat. The exact behavior of this method
	 * is determined by the configuration settings in ssf.properties. 
	 * 
	 * @param input input map
	 * @return output map, this map may or may not be identical to the input map
	 * @throws XSSCheckException
	 */
	public static Map<String,String[]> check(Map<String,String[]> input) throws XSSCheckException {
		Map output;
		if(ENABLE) {
			output = new TreeMap<String, String[]>();
			
			String[] value=null,newValue=null;
			for(String key : input.keySet()) {
				value = input.get(key);
				if(value != null) {
					newValue = new String[value.length];
					for(int i = 0; i < value.length; i++) {
						newValue[i] = check(value[i]);
					}
				}
				output.put(key, newValue);
			}
		}
		else {
			output = input;
		}
		
		return output;
	}
	
	private static String doCheck(String input) throws XSSCheckException {
		CharSequence sequence = input.subSequence(0, input.length());

		Matcher matcher = PATTERN.matcher(sequence);

		// We can use much faster reference comparison rather than string 
		// value equality test due to the way we setup above.
		if(MODE == MODE_DISALLOW) {
			if(matcher.find())
				throw new XSSCheckException();
			else
				return input;
		}
		else if(MODE == MODE_GROUPS) {
			User user = RequestContextHolder.getRequestContext().getUser();
			Set groupNames = user.computeGroupNames();
			for(int i = 0; i < GROUPS.length; i++) {
				if(groupNames.contains(GROUPS[i]))
					return input;
			}
			if(matcher.find())
				throw new XSSCheckException();
			else
				return input;			
		}
		else { // MODE == MODE_STRIP
			return matcher.replaceAll(StringPool.BLANK);
		}
	}
}
