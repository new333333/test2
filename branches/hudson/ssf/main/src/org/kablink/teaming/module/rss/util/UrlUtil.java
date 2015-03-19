/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.rss.util;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.WebUrlUtil;


public class UrlUtil {

	public static String getFeedURL(PortletRequest req, String binderId) {
		return getFeedURLImpl(WebUrlUtil.getRssRootURL(req), binderId);
	}
	
	public static String getFeedURLHttp(HttpServletRequest req, String binderId) {
		return getFeedURLImpl(WebUrlUtil.getRssRootURL(req), binderId);
	}
	
	private static String getFeedURLImpl(String rssRootUrl, String binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		
		boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
		if (!rssEnabled) return "";
		
		StringBuffer url = new StringBuffer();
		
		url.append(rssRootUrl).
			append("list").
			append("?bi=").
			append(binderId).
			append("&ui=").
			append(rc.getUserId()).
			append("&pd=").
			append(rc.getUser().getPrivateDigest(binderId)).
			append("&v=1");		//Append an unused string at the end in case a space is added to the url;
		
		return url.toString();
	}
	
	public static String getAtomURL(PortletRequest req, String binderId) {
		return getAtomURLImpl(WebUrlUtil.getAtomRootURL(req), binderId);
	}
	
	public static String getAtomURLHttp(HttpServletRequest req, String binderId) {
		return getAtomURLImpl(WebUrlUtil.getAtomRootURL(req), binderId);
	}
	
	private static String getAtomURLImpl(String atomRootURL, String binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		
		boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
		if (!rssEnabled) return "";
		
		StringBuffer url = new StringBuffer();
		
		url.append(atomRootURL).
			append("list").
			append("?bi=").
			append(binderId).
			append("&ui=").
			append(rc.getUserId()).
			append("&pd=").
			append(rc.getUser().getPrivateDigest(binderId)).
			append("&v=1");		//Append an unused string at the end in case a space is added to the url;
		
		return url.toString();
	}
}
