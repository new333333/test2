package com.sitescape.ef.context.request;

/**
 *
 * @author Jong Kim
 */
public class RequestContextUtil {

	public static void setThreadContext(String zoneName, String userName) 
		throws RequestContextException {
		if(userName == null)
			throw new IllegalArgumentException("User name must be specified");
		if(zoneName == null)
			throw new IllegalArgumentException("Zone name must be specified");
		
		RequestContext rc = new RequestContext(zoneName, userName);
		RequestContextHolder.setRequestContext(rc);
	}
	
	public static void clearThreadContext() {
		RequestContextHolder.clear();
	}
}
