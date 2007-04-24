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
package com.sitescape.team.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.web.UnauthenticatedAccessException;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;

public class InitRequestContextInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
	    
		if(!WebHelper.isUserLoggedIn(request))
			throw new UnauthenticatedAccessException();
		
		String userName = WebHelper.getRequiredUserName(request);
		String zoneName = WebHelper.getRequiredZoneName(request);
		
		RequestContextUtil.setThreadContext(zoneName, userName);
		
	    return true;
	}
	
    public void afterCompletion(javax.servlet.http.HttpServletRequest request,
            javax.servlet.http.HttpServletResponse response,
            java.lang.Object handler,
            java.lang.Exception ex)
     throws java.lang.Exception {
        
    	RequestContextUtil.clearThreadContext();
    }

}
