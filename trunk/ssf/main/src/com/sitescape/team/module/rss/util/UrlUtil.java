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
package com.sitescape.team.module.rss.util;

import javax.portlet.PortletRequest;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.util.WebUrlUtil;

public class UrlUtil {

	public static String getFeedURL(PortletRequest req, String binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		
		boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
		if (!rssEnabled) return "";
		
		StringBuffer url = new StringBuffer();
		
		url.append(WebUrlUtil.getRssRootURL(req)).
			append("list").
			append("?zn=").
			append(rc.getZoneName()).
			append("&bi=").
			append(binderId).
			append("&ui=").
			append(rc.getUserId()).
			append("&pd=").
			append(rc.getUser().getPrivateDigest());
		
		return url.toString();
	}
}
