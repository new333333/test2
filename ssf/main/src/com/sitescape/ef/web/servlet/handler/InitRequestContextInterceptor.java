package com.sitescape.ef.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.web.WebKeys;

public class InitRequestContextInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
	    
    	HttpSession ses = request.getSession();
    	
		RequestContextUtil.setThreadContext((String) ses.getAttribute(WebKeys.ZONE_NAME),
				(String) ses.getAttribute(WebKeys.USER_NAME));
		
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
