package com.sitescape.ef.context.request;

import com.sitescape.ef.domain.User;

/**
 *
 * @author Jong Kim
 */
public class RequestContextUtil {

	public static RequestContext setThreadContext(String zoneName, String userName) {
		if(userName == null)
			throw new IllegalArgumentException("User name must be specified");
		if(zoneName == null)
			throw new IllegalArgumentException("Zone name must be specified");
		
		RequestContext rc = new RequestContext(zoneName, userName);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}
	
	public static RequestContext setThreadContext(String zoneName, String userName,
			Long userId) {
		RequestContext rc = setThreadContext(zoneName, userName);
		rc.setUserId(userId);
		return rc;
	}
	
	public static RequestContext setThreadContext(User user) {
		RequestContext rc = setThreadContext(user.getZoneName(), user.getName(), user.getId());
		rc.setUser(user);
		return rc;		
	}
	
	public static void clearThreadContext() {
		RequestContextHolder.clear();
	}
}
