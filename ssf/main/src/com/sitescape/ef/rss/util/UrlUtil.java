package com.sitescape.ef.rss.util;

import javax.portlet.PortletRequest;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.web.util.WebUrlUtil;

public class UrlUtil {

	public static String getFeedURL(PortletRequest req, String binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		
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
			append(rc.getUser().getPasswordDigest());
		
		return url.toString();
	}
}
