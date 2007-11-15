package com.sitescape.team.asmodule.requestcontext;

public class RequestContextInfo {
	
    private static final ThreadLocal<String> context = new ThreadLocal<String>();

    public static void setServerName(String serverName) {
    	context.set(serverName);
    }
    
    public static String getServerName() {
    	return context.get();
    }
    
    public static void clear() {
    	context.set(null);
    }
}
