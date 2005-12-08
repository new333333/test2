package com.sitescape.ef.context.request;

import com.sitescape.ef.domain.User;

/**
 *
 * @author Jong Kim
 */
public class RequestContextUtil {

	public static void setThreadContext(String zoneId) throws RequestContextException {
		if (zoneId == null) {
			RequestContextHolder.clear();
		} else {
			try {
				RequestContext rc = new RequestContext(zoneId);
				RequestContextHolder.setRequestContext(rc);
			} 	catch (Exception e) {
				RequestContextHolder.clear();
				throw new RequestContextException(e);
			}
		}
	}
	public static void setThreadContext(User user) throws RequestContextException {
		if (user == null) {
			RequestContextHolder.clear();
		} else {
			try {
				RequestContext rc = new RequestContext(user);
				RequestContextHolder.setRequestContext(rc);
			} 	catch (Exception e) {
				RequestContextHolder.clear();
				throw new RequestContextException(e);
			}
		}
	}
	public static void clearThreadContext() {
		RequestContextHolder.clear();
	}
}
