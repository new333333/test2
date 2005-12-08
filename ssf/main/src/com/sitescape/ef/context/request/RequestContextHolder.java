package com.sitescape.ef.context.request;

import org.springframework.util.ClassLoaderUtils;

/**
 * @author Jong Kim
 *
 */
public class RequestContextHolder {
    private static final ThreadLocal requestContextTL = new ThreadLocal();

    public static void setRequestContext(RequestContext requestContext) {
    	//System.out.println("### Class loader hierarchy of " + RequestContextHolder.class.getName());
		//System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(RequestContextHolder.class.getClassLoader()));		

		requestContextTL.set(requestContext);
    }
    public static RequestContext getRequestContext() {
    	//System.out.println("### Class loader hierarchy of " + RequestContextHolder.class.getName());
		//System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(RequestContextHolder.class.getClassLoader()));		

		return (RequestContext) requestContextTL.get();
    }
    public static void clear() {
        setRequestContext(null);
    }
}
