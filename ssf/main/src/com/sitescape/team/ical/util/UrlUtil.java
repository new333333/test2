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
import com.sitescape.team.mail.MailHelper;
import com.sitescape.team.web.util.WebUrlUtil;

/**
 * iCalendar URLs util class. 
 * 
 * @author Pawel Nowicki
 *
 */
public class UrlUtil {

	/**
	 * Creates iCalendar URL for given entry or forum.
	 * 
	 * @param req
	 * @param binderId
	 * @param entryId
	 * @return
	 */
	public static String getICalURL(PortletRequest req, String binderId, String entryId) {
		if (binderId == null) {
			throw new IllegalArgumentException("'binderId' is required.");
		}
		RequestContext rc = RequestContextHolder.getRequestContext();
		
		StringBuffer url = new StringBuffer();
		
		url.append(WebUrlUtil.getIcalRootURL(req)).
			append("basic").
			append(MailHelper.ICAL_FILE_EXTENSION).
			append("?zn=").
			append(rc.getZoneName()).
			append("&bi=").
			append(binderId);
		
		if (entryId != null) {
			url.append("&entry=").append(entryId);
		}
		
		url.append("&ui=").
			append(rc.getUserId()).
			append("&pd=").
			append(rc.getUser().getPrivateDigest(binderId));
		
		return url.toString();
	}
}
