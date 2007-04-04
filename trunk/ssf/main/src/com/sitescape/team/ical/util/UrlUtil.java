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
