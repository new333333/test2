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
package com.sitescape.team.ical.util;

import javax.portlet.PortletRequest;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.web.util.WebUrlUtil;

/**
 * iCalendar URLs util class. 
 * 
 * @author Pawel Nowicki
 *
 */
public class UrlUtil {

	/**
	 * Creates iCalendar URL for given entry.
	 * 
	 * @param req
	 * @param binderId
	 * @param entryId
	 * @return
	 */
	public static String getICalURL(PortletRequest req, String binderId, String entryId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		
		StringBuffer url = new StringBuffer();
		
		url.append(WebUrlUtil.getIcalRootURL(req)).
			append("get").
			append("?zn=").
			append(rc.getZoneName()).
			append("&bi=").
			append(binderId).
			append("&entry=").
			append(entryId).
			append("&ui=").
			append(rc.getUserId()).
			append("&pd=").
			append(rc.getUser().getPasswordDigest());
		
		return url.toString();
	}
}
