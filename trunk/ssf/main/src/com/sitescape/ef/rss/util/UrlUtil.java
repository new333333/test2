package com.sitescape.ef.rss.util;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.web.util.WebUrlUtil;

public class UrlUtil {

	public static String getFeedURL(String binderId) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		
		StringBuffer url = new StringBuffer();
		
		url.append(WebUrlUtil.getRssRootURL()).
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
	// NOTE: This is just here until we're done testing, then it'll be ripped out.
	public static String getFeedURL() {
		RequestContext rc = RequestContextHolder.getRequestContext();
		
		StringBuffer url = new StringBuffer();
		
		url.append(WebUrlUtil.getRssRootURL()).
			append("list").
			append("?zn=").
			append(rc.getZoneName()).
			append("&ui=").
			append(rc.getUserId()).
			append("&pd=").
			append(rc.getUser().getPasswordDigest());
		
		return url.toString();
	}
}
