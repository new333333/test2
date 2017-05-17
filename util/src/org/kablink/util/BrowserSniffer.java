/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * <a href="BrowserSniffer.java.html"><b><i>View Source</i></b></a>
 *
 * See http://www.zytrax.com/tech/web/browser_ids.htm for examples.
 *
 * @author Brian Wing Shun Chan
 *
 */
public class BrowserSniffer {
	public static boolean acceptsGzip(HttpServletRequest req) {
		String acceptEncoding = req.getHeader(HttpHeaders.ACCEPT_ENCODING);

		if ((acceptEncoding != null) &&
			(acceptEncoding.indexOf(_GZIP) != -1)) {

			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_edge(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();
		if ((agent.indexOf("like gecko") != -1) && (agent.indexOf(" edge/") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ie(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (agent.indexOf("msie") != -1) {
			return true;
		}
		else {
			return is_ie_11(req);
		}
	}

	public static boolean is_ie_4(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 4") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ie_5(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 5.0") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ie_5_5(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 5.5") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ie_5_5_up(HttpServletRequest req) {
		if (is_ie(req) && !is_ie_4(req) && !is_ie_5(req)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ie_6(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 6.0") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ie_7(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 7.0") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ie_8(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 8.0") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ie_11(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if ((agent.indexOf("like gecko") != -1) && (agent.indexOf("rv:11.0") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_linux(HttpServletRequest req) {
		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (agent.matches(".*linux.*")) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_mozilla_5(HttpServletRequest req) {
		if (req == null) return false;

		String agent = req.getHeader(HttpHeaders.USER_AGENT);
		if (agent == null) return false;

		agent = agent.toLowerCase();

		if (is_mozilla(req) && (agent.indexOf("mozilla/5") != -1)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean is_mozilla(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if ((agent.indexOf("mozilla") != -1) &&
			(agent.indexOf("spoofer") == -1) &&
			(agent.indexOf("compatible") == -1) &&
			(agent.indexOf("opera") == -1) &&
			(agent.indexOf("webtv") == -1) &&
			(agent.indexOf("hotjava") == -1)) {

			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_mozilla_1_3_up(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_mozilla(req)) {
			int pos = agent.indexOf("gecko/");

			if (pos == -1) {
				return false;
			}
			else {
				String releaseDate = agent.substring(pos + 6, agent.length());

				if (releaseDate.compareTo("20030210") > 0) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean is_ns_4(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (!is_ie(req) && (agent.indexOf("mozilla/4.") != -1)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_rtf(HttpServletRequest req) {
		if (is_ie_5_5_up(req) || is_mozilla_1_3_up(req)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_safari(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (agent.indexOf("safari") != -1) {
			return !is_chrome(req);
		}
		else {
			return false;
		}
	}

	public static boolean is_chrome(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if ((agent.indexOf("chrome") != -1) && (!(is_edge(req)))) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_mobile(HttpServletRequest req, String userAgents) { 
		if (req == null) {
			return false;
		}
		if (is_iphone(req) || is_ipad(req)) return true;
		if (is_blackberry(req)) return true;
		if (is_droid(req)) return true;
		if (is_otherMobile(req, userAgents)) return true;
		if (is_wap_xhtml(req)) return true;

		return false;
	}

	public static boolean is_wap_xhtml(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String accept = req.getHeader(HttpHeaders.ACCEPT);

		if (accept == null) {
			return false;
		}

		accept = accept.toLowerCase();

		if (accept.indexOf("wap.xhtml") != -1) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_iphone(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (agent.indexOf("iphone") != -1) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_ipad(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (agent.indexOf("ipad") != -1) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_blackberry(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (agent.indexOf("blackberry") != -1) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_droid(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (agent.indexOf("android") != -1) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean is_tablet(HttpServletRequest req, String userAgentRegexp, Boolean testForAndroid) {
		if (req == null) {
			return false;
		}
		if (userAgentRegexp == null || userAgentRegexp.equals("")) {
			//If there is no regexp defined for finding tablets, then this cannot be a tablet.
			return false;
		}
		Pattern p = Pattern.compile(userAgentRegexp, Pattern.CASE_INSENSITIVE);

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		Matcher m = p.matcher(agent);
		if (m.find()) return true;
		if (testForAndroid && agent.indexOf("android") > -1 && agent.indexOf("mobile") == -1) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean is_otherMobile(HttpServletRequest req, String userAgents) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);
		if (agent == null) {
			return false;
		}
		agent = agent.toLowerCase();
		
		//See if there are user agents defined in ssf.properties (e.g., "nokia, pre/")
		String[] mobileDevices = userAgents.split(",");
		for (int i = 0; i < mobileDevices.length; i++) {
			if (mobileDevices[i].equals("")) continue;
			if (agent.indexOf(mobileDevices[i]) != -1) {
				return true;
			}
		}
		return false;
	}

	public static boolean is_wml(HttpServletRequest req) {
		if (req == null) {
			return false;
		}

		String accept = req.getHeader(HttpHeaders.ACCEPT);

		if (accept == null) {
			return false;
		}

		accept = accept.toLowerCase();

		if (accept.indexOf("wap.wml") != -1) {
			return true;
		}
		else {
			return false;
		}
	}

	public static String getOSInfo(HttpServletRequest req) {
		if (req == null) {
			return "";
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);

		if (agent == null) {
			return "";
		}

		agent = agent.toLowerCase();

		if ((agent.indexOf("windows") != -1)) return "windows";
		else if ((agent.indexOf("linux") != -1)) return "linux";
		else if ((agent.indexOf("mac") != -1)) return "mac";		
		else return "";
	}

	public static boolean is_TinyMCECapable(HttpServletRequest req, String userAgents) {
		if (req == null) {
			return false;
		}

		String agent = req.getHeader(HttpHeaders.USER_AGENT);
		if (agent == null) {
			return false;
		}
		agent = agent.toLowerCase();
		
		//See if there are user agents defined in ssf.properties (e.g., "nokia, pre/")
		String[] mobileDevices = userAgents.split(",");
		for (int i = 0; i < mobileDevices.length; i++) {
			if (mobileDevices[i].equals("")) continue;
			if (agent.indexOf(mobileDevices[i]) != -1) {
				return false;
			}
		}
		return true;
	}

	private static final String _GZIP = "gzip";

}