package com.sitescape.team.context.request;

import com.sitescape.team.domain.User;

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
	public static RequestContext setThreadContext(Long zoneId, Long userId) {
		if(zoneId == null)
			throw new IllegalArgumentException("User id must be specified");
		if(userId == null)
			throw new IllegalArgumentException("Zone id must be specified");
		
		RequestContext rc = new RequestContext(zoneId, userId);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}	
	
	public static RequestContext setThreadContext(User user) {
		RequestContext rc = setThreadContext(user.getParentBinder().getParentBinder().getName(), user.getName());
		rc.setUser(user);
		return rc;		
	}
	
	public static void clearThreadContext() {
		RequestContextHolder.clear();
	}
}
