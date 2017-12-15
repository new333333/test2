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
import org.kablink.teaming.context.request.NoContextUserException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.HTMLInputFilter;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.StringPool;


public class XSSCheck implements StringCheck {

	private static final String TYPE_CHECK_STRING = "check_string"; // Checking a string
	private static final String TYPE_CHECK_FILE = "check_file"; // Checking a file
	private static final String MODE_DISALLOW = "disallow"; // default mode
	private static final String MODE_STRIP = "strip";
	private static final String MODE_TRUSTED_DISALLOW = "trusted.disallow";
	private static final String MODE_TRUSTED_STRIP = "trusted.strip";
	
	//Lists of tags and attributes used in the following patterns
	private static final String TAGS_TO_DELETE = "script|embed|object|applet|style|html|head|body|meta|xml|blink|link|iframe|frame|frameset|form|ilayer|layer|bgsound|base";
	private static final String TAGS_TO_DELETE_FILE = "script|embed|object|applet|blink|iframe|frame|frameset|form|ilayer|layer|bgsound|base";
	private static final String TAGS_TO_QUOTE = "script|embed|object|applet|style|html|head|body|meta|xml|blink|link|iframe|frame|frameset|form|ilayer|layer|bgsound|base";
	private static final String TAGS_TO_QUOTE_FILE = "script|embed|object|applet|blink|iframe|frame|frameset|form|ilayer|layer|bgsound|base";
	private static final String TAGS_TO_CHECK = "a|button|img|iframe|area|base|frame|frameset|input|link|xml|math|maction|meta|blockquote|del|ins|q|video|audio|source|track|menu|menuitem|command";
	private static final String ATTRS_TO_CHECK = "href|xlink:href|src|cite|scheme|formaction|actiontype|poster|icon|background";
	
	//Properly formatted html - these get deleted
	private static final String PATTERN_STR1 = "(?i)(<[\\s]*/?[\\s]*(?:" + TAGS_TO_DELETE + ")(?:[\\s]+[^>]*>|>))";
	private static final String PATTERN_STR1_FILE = "(?i)(<[\\s]*/?[\\s]*(?:" + TAGS_TO_DELETE_FILE + ")(?:[\\s]+[^>]*>|>))";
	//Improperly closed tags - these get quoted with &lt; and &gt;
	private static final String PATTERN_STR1a = "(?i)(<[\\s]*/?[\\s]*(?:" + TAGS_TO_QUOTE + ")(?:[\\s]+[^>]*$|$))";
	private static final String PATTERN_STR1_FILEa = "(?i)(<[\\s]*/?[\\s]*(?:" + TAGS_TO_QUOTE_FILE + ")(?:[\\s]+[^>]*$|$))";

	private static final String PATTERN_STR2 = "(?i)(<[\\s]*(?:" + TAGS_TO_CHECK + ")[\\s]+[^>]*)((?:" + ATTRS_TO_CHECK + ")[\\s]*=[\\s]*(?:([\"'])[\\s]*[^\\s]*[\u0001-\u0013]*script[\\s]*:[^>]*\\2|[^>\\s]*[\u0001-\u0013]*script[\\s]*:[^>\\s]*))([^>]*>)";
	private static final String PATTERN_STR2_FILE = "(?i)(<[\\s]*(?:" + TAGS_TO_CHECK + ")[\\s]+[^>]*)((?:" + ATTRS_TO_CHECK + ")[\\s]*=[\\s]*(?:([\"'])[\\s]*[^\\s]*https?[\\s]*:[^>]*\\2|[^>\\s]*https?[\\s]*:[^>\\s]*))([^>]*>)";
	private static final String PATTERN_STR2a = "(?i)((?:" + ATTRS_TO_CHECK + ")[\\s]*=[\\s]*(?:([\"'])[\\s]*[^\\s]*[\u0001-\u0013]*script[\\s]*:[^>]*\\2|[^>\\s]*[\u0001-\u0013]*script[\\s]*:[^>\\s]*))";
	private static final String PATTERN_STR2b1 = "(?i)(<[\\s]*(?:" + TAGS_TO_CHECK + ")[\\s]+[^>]*)((?:" + ATTRS_TO_CHECK + ")[\\s]*=[\\s]*(?:([\"'])[\\s]*[^\\s]data[\\s]*:[^>]*\\2|[^>\\s]data[\\s]*:[^>\\s]*))([^>]*>)";
	private static final String PATTERN_STR2b2 = "(?i)((?:" + ATTRS_TO_CHECK + ")[\\s]*=[\\s]*(?:([\"'])[\\s]*[^\\s]data[\\s]*:[^>]*\\2|[^>\\s]data[\\s]*:[^>\\s]*))";
	private static final String PATTERN_STR2b3 = "(?i)((?:" + ATTRS_TO_CHECK + ")[\\s]*=[\\s]*(?:([\"'])[\\s]*[^\\s]data:image[^>]*\\2|[^>\\s]data:image[^>\\s]*))";

	private static final String PATTERN_STR3 = "(?i)<[\\s]*[^>]+[\\s]*([^>]*)>";
	private static final String PATTERN_STR4 = "(?i)(?:style[\\s]*=[\\s]*\"[^\">]*\"|style[\\s]*=[\\s]*'[^'>]*'|style[\\s]*=[^<>\\s\"']*)";
	private static final String PATTERN_STR5 = "(?i)((?:^url\\W|\\Wurl\\W|^expression\\W|\\Wexpression\\W))";
	private static final String PATTERN_STR5a = "(?i)(/\\*[^*]*\\*/)";

	//Pattern_str6 is used to find onxxx statements (e.g., onClick, onload, onerror)
	//It is important to realize that these can be preceeded by either a space or a "/"
	private static final String PATTERN_STR6 = "(?i)(<[\\s]*[^>]*[\\s\"'/]+)(on[^>\\s!#%*+,\\./?@_-]*[\\s]*=[\\s]*(?:\"[^\">]*\"|'[^'>]*'|[^>\\s/]*))([^>]*>|[^/]*//)";
	
	private Pattern pattern1;
	private Pattern pattern1file;
	private Pattern pattern1a;
	private Pattern pattern1filea;
	private Pattern pattern2;
	private Pattern pattern2file;
	private Pattern pattern2a;
	private Pattern pattern2b1;
	private Pattern pattern2b2;
	private Pattern pattern2b3;
	private Pattern pattern3;
	private Pattern pattern4;
	private Pattern pattern5;
	private Pattern pattern5a;
	private Pattern pattern6;
	private boolean enable;
	private String modeDefault;
	private String modeFile;
	private HTMLInputFilter htmlInputFilter;
	private ConcurrentHashMap<Long, Set<String>> trustedUsersMap;
	private ConcurrentHashMap<Long, Set<String>> trustedGroupsMap;
	
	public XSSCheck() {
		this(SPropsUtil.getBoolean("xss.check.enable"),
				SPropsUtil.getString("xss.check.mode.default"),
				SPropsUtil.getString("xss.check.mode.file"));
	}
	
	public XSSCheck(boolean enable, String modeDefault, String modeFile) {
		pattern1 = Pattern.compile(PATTERN_STR1);
		pattern1file = Pattern.compile(PATTERN_STR1_FILE);
		pattern1a = Pattern.compile(PATTERN_STR1a);
		pattern1filea = Pattern.compile(PATTERN_STR1_FILEa);
		pattern2 = Pattern.compile(PATTERN_STR2);
		pattern2file = Pattern.compile(PATTERN_STR2_FILE);
		pattern2a = Pattern.compile(PATTERN_STR2a);
		pattern2b1 = Pattern.compile(PATTERN_STR2b1);
		pattern2b2 = Pattern.compile(PATTERN_STR2b2);
		pattern2b3 = Pattern.compile(PATTERN_STR2b3);
		pattern3 = Pattern.compile(PATTERN_STR3);
		pattern4 = Pattern.compile(PATTERN_STR4);
		pattern5 = Pattern.compile(PATTERN_STR5);
		pattern5a = Pattern.compile(PATTERN_STR5a);
		pattern6 = Pattern.compile(PATTERN_STR6);
		this.enable = enable;
		this.modeDefault = modeDefault;
		this.modeFile = modeFile;
		trustedUsersMap = new ConcurrentHashMap<Long, Set<String>>();
		trustedGroupsMap = new ConcurrentHashMap<Long, Set<String>>();

		// We do this not only to validate the input mode but also to enable
		// simple reference comparison for modes. 
		if(modeDefault.equals(MODE_TRUSTED_DISALLOW))
			modeDefault = MODE_TRUSTED_DISALLOW;
		if(modeDefault.equals(MODE_TRUSTED_STRIP))
			modeDefault = MODE_TRUSTED_STRIP;
		else if(modeDefault.equals(MODE_STRIP))
			modeDefault = MODE_STRIP;
		else
			modeDefault = MODE_DISALLOW; // default mode
		
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
			return doCheck(input, TYPE_CHECK_STRING, modeDefault);
		else
			return input;
	}
	
	public String check(String input, boolean checkOnly) throws XSSCheckException {
		if(enable || checkOnly)
			return doCheck(input, TYPE_CHECK_STRING, modeDefault, checkOnly);
		else
			return input;
	}
	
	public String checkFile(String fileContentAsString) throws XSSCheckException {
		if(enable) {
			SimpleProfiler.start("XSSCheck.checkFile");
			String result = doCheck(fileContentAsString, TYPE_CHECK_FILE, modeFile);
			SimpleProfiler.stop("XSSCheck.checkFile");
			return result;
		}
		else
			return fileContentAsString;
	}
	
	public String checkForQuotes(String value, boolean checkOnly) throws StringCheckException {
		//Remove any %27 0r %22 character representations
		value = htmlInputFilter.filter(value, true);

		if (value != null && (value.contains("\"") || value.contains("'"))) {
			value = value.replaceAll("\"", "&#34;");	 //Replace all quotation marks. There should never be any quotation marks in these strings. 
			value = value.replaceAll("'", "&#39;");		 //Replace all single quotation marks. This is intended to fix certain XSS attacks.
		}
		return value;
	}
	
	// A subclass can override this method to provide custom or enhanced implementation.
	protected String doCheck(String input, String type, String mode) throws XSSCheckException {
		return doCheck(input, type, mode, false);
	}
	protected String doCheck(String input, String type, String mode, boolean checkOnly) throws XSSCheckException {
		if(input == null || input.equals(""))
			return input;
		
		// We can use much faster reference comparison rather than string 
		// value equality test due to the way we setup above.
		
		if (!checkOnly && (mode.equals(MODE_TRUSTED_DISALLOW) || mode.equals(MODE_TRUSTED_STRIP))) {
			if (RequestContextHolder.getRequestContext() != null) {
				User user = null;
				try {
					user = RequestContextHolder.getRequestContext().getUser();
				}
				catch(NoContextUserException doNotPropogate) {}
				if(user != null) {
					if(getTrustedUserNames(user.getZoneId()).contains(user.getName()))
						return input; // match found on user list
					if(!Collections.disjoint(user.computeApplicationLevelGroupNames(), getTrustedGroupNames(user.getZoneId())))
						return input; // match found on group list
				}
			}
		}
		
		String sequence = new String(input);
		Map data = new HashMap();

		//We do not support "strip" mode for files
		if (type.equals(TYPE_CHECK_FILE) || mode == MODE_DISALLOW || mode == MODE_TRUSTED_DISALLOW) {
			data.put("sequence", sequence);
			if (!checkIfStringValid(type, data)) {
				throw new XSSCheckException();
			}
			return input;
		} else { // mode == MODE_STRIP || mode == MODE_TRUSTED_STRIP
			String cleanString = htmlInputFilter.filter(sequence.toString());
			String decodedString = cleanString;
			boolean changed = false;
			try {
				decodedString = URLDecoder.decode(cleanString, "UTF-8");
		    } catch(Exception e) {}
		    int loopDetector = 2000;
		    while (loopDetector > 0) {
				data.put("sequence", decodedString);
				String oldCleanString = decodedString;
				if (checkIfStringValid(type, data)) {
					break;
				}
				if (checkOnly) {
					//This request is to check the validity only. Since it isn't valid, throw error
					throw new XSSCheckException();
				}
				changed = true;
				decodedString = (String)data.get("sequence");
				if (decodedString.equals(oldCleanString)) {
					//The XSS checking routine thought there was an error, but the string is unchanged. 
					//  Since we don't know how to strip it, just blank out the whole string.
					decodedString = "";
					break;
				}
				loopDetector--;
		    }
		    if (loopDetector <= 0) {
		    	//The stripping code could not clean this string, so just blank it
		    	decodedString = "";
		    }
			if (changed) {
				return decodedString;
			}else {
				return cleanString;
			}
		}
	}
	
	protected boolean checkIfStringValid(String type, Map data) {
		boolean result = true;
		String sequence = (String)data.get("sequence");
		//Check for script, embed, iframe, ...
		Matcher matcher1 = pattern1.matcher(sequence);
		if (type.equals(TYPE_CHECK_FILE)) matcher1 = pattern1file.matcher(sequence);
		if (matcher1.find()) {
			sequence = matcher1.replaceAll(StringPool.BLANK);
			result = false;
		}
		//Check for ill formed script, embed, iframe, ...
		Matcher matcher1a = pattern1a.matcher(sequence);
		if (type.equals(TYPE_CHECK_FILE)) matcher1a = pattern1filea.matcher(sequence);
		if (matcher1a.find()) {
			sequence = sequence.replaceAll("<", "&lt;");
			sequence = sequence.replaceAll(">", "&gt;");
			result = false;
		}
		//Check files for href="http:..." 
		if (type.equals(TYPE_CHECK_FILE)) {
			Matcher matcher2file = pattern2file.matcher(sequence);
			if (matcher2file.find()) result = false;
		}
		//Check for href="javascript:..." or any *script as src or href, etc
		Matcher matcher2 = pattern2.matcher(sequence);
		StringBuffer buf = new StringBuffer();
		while (matcher2.find()) {
			String tagString = matcher2.group(0);
			Matcher matcher2a = pattern2a.matcher(tagString);
			if (matcher2a.find()) {
				String scriptString = matcher2a.group(0);
				tagString = matcher2a.replaceAll(StringPool.BLANK);
				result = false;
			}
			matcher2.appendReplacement( buf, Matcher.quoteReplacement(tagString) );
		}
		matcher2.appendTail(buf);
		sequence = buf.toString();

		//Check for href="data:..." (except data:image is allowed)
		Matcher matcher2b1 = pattern2b1.matcher(sequence);
		buf = new StringBuffer();
		while (matcher2b1.find()) {
			String tagString = matcher2b1.group(0);
			Matcher matcher2b2 = pattern2b2.matcher(tagString);
			if (matcher2b2.find()) {
				String scriptString = matcher2b2.group(0);
				Matcher matcher2b3 = pattern2b3.matcher(scriptString);
				if (!matcher2b3.find()) {
					tagString = matcher2b2.replaceAll(StringPool.BLANK);
					result = false;
				}
			}
			matcher2b1.appendReplacement( buf, Matcher.quoteReplacement(tagString) );
		}
		matcher2b1.appendTail(buf);
		sequence = buf.toString();

		//Check for style="..." in any tag
		buf = new StringBuffer();
		Matcher matcher3 = pattern3.matcher(sequence);
		while (matcher3.find()) {
			String tagString = matcher3.group(0);
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
			matcher3.appendReplacement( buf, Matcher.quoteReplacement(tagString) );
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
