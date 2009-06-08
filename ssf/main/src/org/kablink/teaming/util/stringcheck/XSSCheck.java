/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util.stringcheck;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.HTMLInputFilter;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.util.StringPool;


public class XSSCheck implements StringCheck {

	private static final String MODE_DISALLOW = "disallow"; // default mode
	private static final String MODE_STRIP = "strip";
	private static final String MODE_TRUSTED_DISALLOW = "trusted.disallow";
	private static final String MODE_TRUSTED_STRIP = "trusted.strip";
	
	private static final String PATTERN_STR1 = "(?i)(<[\\s]*/?[\\s]*(?:script|embed|object|applet|style|html|head|body|meta|xml|blink|link|iframe|frame|frameset|ilayer|layer|bgsound|base)(?:[\\s]+[^>]*>|>))";
	private static final String PATTERN_STR2 = "(?i)(<[\\s]*(?:a|img|iframe|area|base|frame|frameset|input|link|meta|blockquote|del|ins|q)[\\s]*[^>]*)((?:href|src|cite|scheme)[\\s]*=[\\s]*(?:([\"'])[\\s]*[^\\s]*script[\\s]*:[^>]*\\2|[^\\s]*script[\\s]*:[^>\\s]*))([^>]*>)";
	private static final String PATTERN_STR3 = "(?i)<[\\s]*[^>]+[\\s]*([^>\\s]*style[\\s]*=[\\s]*([\"'])[^>]*\\2|[^>\\s]*style[\\s]*=[^>\\s]*)[^>]*>";
	private static final String PATTERN_STR4 = "(?i)(?:[^<>\\s]*style[\\s]*=[\\s]*([\"'])[^>]*\\2|[^>\\s]*style[\\s]*=[^>\\s]*)";
	private static final String PATTERN_STR5 = "(?i)((?:url|expression))";
	private static final String PATTERN_STR5a = "(?i)(/\\*[^*]*\\*/)";
	private static final String PATTERN_STR6 = "(?i)(<[\\s]*[^>]*[\\s]+)(on[^>\\s]*[\\s]*=[\\s]*(?:\"[^\">]*\"|'[^'>]*'))([^>]*>)";
	
	private Pattern pattern1;
	private Pattern pattern2;
	private Pattern pattern3;
	private Pattern pattern4;
	private Pattern pattern5;
	private Pattern pattern5a;
	private Pattern pattern6;
	private boolean enable;
	private String mode;
	private HTMLInputFilter htmlInputFilter;
	private ConcurrentHashMap<Long, Set<String>> trustedUsersMap;
	private ConcurrentHashMap<Long, Set<String>> trustedGroupsMap;
	
	public XSSCheck() {
		pattern1 = Pattern.compile(PATTERN_STR1);
		pattern2 = Pattern.compile(PATTERN_STR2);
		pattern3 = Pattern.compile(PATTERN_STR3);
		pattern4 = Pattern.compile(PATTERN_STR4);
		pattern5 = Pattern.compile(PATTERN_STR5);
		pattern5a = Pattern.compile(PATTERN_STR5a);
		pattern6 = Pattern.compile(PATTERN_STR6);
		enable = SPropsUtil.getBoolean("xss.check.enable");
		mode = SPropsUtil.getString("xss.check.mode");
		trustedUsersMap = new ConcurrentHashMap<Long, Set<String>>();
		trustedGroupsMap = new ConcurrentHashMap<Long, Set<String>>();

		// We do this not only to validate the input mode but also to enable
		// simple reference comparison for modes. 
		if(mode.equals(MODE_TRUSTED_DISALLOW))
			mode = MODE_TRUSTED_DISALLOW;
		if(mode.equals(MODE_TRUSTED_STRIP))
			mode = MODE_TRUSTED_STRIP;
		else if(mode.equals(MODE_STRIP))
			mode = MODE_STRIP;
		else
			mode = MODE_DISALLOW; // default mode
		
		htmlInputFilter = new HTMLInputFilter();
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
	
	// A subclass can override this method to provide custom or enhanced implementation.
	protected String doCheck(String input) throws XSSCheckException {
		if(input == null || input.equals(""))
			return input;
		
		// We can use much faster reference comparison rather than string 
		// value equality test due to the way we setup above.
		
		if (mode == MODE_TRUSTED_DISALLOW || mode == MODE_TRUSTED_STRIP) {
			if (RequestContextHolder.getRequestContext() != null) {
				User user = RequestContextHolder.getRequestContext().getUser();
				if(getTrustedUserNames(user.getZoneId()).contains(user.getName()))
					return input; // match found on user list
				if(!Collections.disjoint(user.computeGroupNames(), getTrustedGroupNames(user.getZoneId())))
					return input; // match found on group list
			}
		}
		
		String sequence = new String(input);
		Map data = new HashMap();

		if (mode == MODE_DISALLOW || mode == MODE_TRUSTED_DISALLOW) {
			data.put("sequence", sequence);
			if (!checkIfStringValid(data)) {
				throw new XSSCheckException();
			}
			return input;
		} else { // mode == MODE_STRIP || mode == MODE_TRUSTED_STRIP
			String cleanString = htmlInputFilter.filter(sequence.toString());
			String decodedString = cleanString;
			try {
				decodedString = URLDecoder.decode(cleanString, "UTF-8");
		    } catch(Exception e) {}
			data.put("sequence", decodedString);
			if (!checkIfStringValid(data)) {
				cleanString = (String)data.get("sequence");
			}
			return cleanString;			
		}
	}
	
	protected boolean checkIfStringValid(Map data) {
		boolean result = true;
		String sequence = (String)data.get("sequence");
		//Check for scrip, embed, iframe, ...
		Matcher matcher1 = pattern1.matcher(sequence);
		if (matcher1.find()) {
			sequence = matcher1.replaceAll(StringPool.BLANK);
			result = false;
		}
		//Check for href="javascript:..." or any *script as src or href, etc
		Matcher matcher2 = pattern2.matcher(sequence);
		String lastValue = "";
		while (matcher2.find()) {
			String tagStart = matcher2.group(1);
			String scriptString = matcher2.group(2);
			String tagEnd = matcher2.group(4);
			sequence = tagStart + StringPool.BLANK + tagEnd;
			matcher2 = pattern2.matcher(sequence);
			result = false;
			if (lastValue.equals(sequence)) break;
			lastValue = sequence;
		}
		//Check for style="..." in any tag
		StringBuffer buf = new StringBuffer();
		Matcher matcher3 = pattern3.matcher(sequence);
		while (matcher3.find()) {
			String tagString = matcher3.group(0);
			if (matcher3.groupCount() >= 1) tagString = matcher3.group(1);
			Matcher matcher4 = pattern4.matcher(tagString);
			if (matcher4.find()) {
				String styleString = matcher4.group(0);
				Matcher matcher5 = pattern5.matcher(styleString);
				Matcher matcher5a = pattern5a.matcher(styleString);
				if (matcher5.find() || matcher5a.find()) {
					tagString = matcher4.replaceAll(StringPool.BLANK);
					result = false;
				}
			}
			matcher3.appendReplacement( buf, tagString );
		}
		matcher3.appendTail(buf);
		sequence = buf.toString();
		
		//Check for onClick="..." or any on*="..." 
		Matcher matcher6 = pattern6.matcher(sequence);
		while (matcher6.find()) {
			buf = new StringBuffer();
			String start = matcher6.group(1);
			String end = matcher6.group(3);
			matcher6.appendReplacement(buf, matcher6.quoteReplacement(start + StringPool.BLANK + end));
			result = false;
			matcher6.appendTail(buf);
			sequence = buf.toString();
			matcher6 = pattern6.matcher(sequence);
		}

		data.put("sequence", sequence);
		return result;
	}
	
	private Set<String> getTrustedUserNames(Long zoneId) {
		Set<String> users = trustedUsersMap.get(zoneId);
		if(users == null) {
			users = new TreeSet<String>();
			List<Element> trustedUsers = SZoneConfig.getElements("xssConfiguration/trustedUsers/user");
			for(Element elem:trustedUsers) {
				users.add(elem.attributeValue("name"));
			}
            trustedUsersMap.put(zoneId, users);
		}
		return users;
	}
	
	private Set<String> getTrustedGroupNames(Long zoneId) {
		Set<String> groups = trustedGroupsMap.get(zoneId);
		if(groups == null) {
			groups = new TreeSet<String>();
			List<Element> trustedGroups = SZoneConfig.getElements("xssConfiguration/trustedGroups/group");
			for(Element elem:trustedGroups) {
				groups.add(elem.attributeValue("name"));
			}
            trustedGroupsMap.put(zoneId, groups);
		}
		return groups;
	}

}
