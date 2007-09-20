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
package com.sitescape.team.util.stringcheck;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.util.StringPool;

public class XSSCheck implements StringCheck {

	private static final String MODE_DISALLOW = "disallow"; // default mode
	private static final String MODE_GROUPS = "groups";
	private static final String MODE_STRIP = "strip";
	
	private String patternStr;
	private Pattern pattern;
	private boolean enable;
	private String mode;
	private String[] groups;
	
	public XSSCheck() {
		patternStr = SPropsUtil.getString("xss.regexp.pattern");
		pattern = Pattern.compile(patternStr);
		enable = SPropsUtil.getBoolean("xss.check.enable");
		mode = SPropsUtil.getString("xss.check.mode");
		groups = SPropsUtil.getStringArray("xss.groups", ";");

		// We do this not only to validate the input mode but also to enable
		// simple reference comparison for modes. 
		if(mode.equals(MODE_GROUPS))
			mode = MODE_GROUPS;
		else if(mode.equals(MODE_STRIP))
			mode = MODE_STRIP;
		else
			mode = MODE_DISALLOW;
	}
	
	/**
	 * Detect potential XSS threat. The exact behavior of this method
	 * is determined by the configuration settings in ssf.properties. 
	 * 
	 * @param input input data as string
	 * @return output data as string
	 * @throws XSSCheckException
	 */
	public String check(String input) throws XSSCheckException {
		if(enable)
			return doCheck(input);
		else
			return input;
	}
	
	private String doCheck(String input) throws XSSCheckException {
		if(input == null || input.equals(""))
			return input;
		
		CharSequence sequence = input.subSequence(0, input.length());

		Matcher matcher = pattern.matcher(sequence);

		// We can use much faster reference comparison rather than string 
		// value equality test due to the way we setup above.
		if(mode == MODE_DISALLOW) {
			if(matcher.find())
				throw new XSSCheckException();
			else
				return input;
		}
		else if(mode == MODE_GROUPS) {
			User user = RequestContextHolder.getRequestContext().getUser();
			Set groupNames = user.computeGroupNames();
			for(int i = 0; i < groups.length; i++) {
				if(groupNames.contains(groups[i]))
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
