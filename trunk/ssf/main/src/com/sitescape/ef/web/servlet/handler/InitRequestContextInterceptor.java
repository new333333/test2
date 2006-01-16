package com.sitescape.ef.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.web.NoValidUserSessionException;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebHelper;

public class InitRequestContextInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
	    
		if(!WebHelper.isUserLoggedIn(request))
			throw new NoValidUserSessionException();
		
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
