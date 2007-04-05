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
package com.sitescape.team.context.request;

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
