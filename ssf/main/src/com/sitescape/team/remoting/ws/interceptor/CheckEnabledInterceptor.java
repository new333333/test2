package com.sitescape.team.remoting.ws.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.sitescape.team.util.SPropsUtil;

public class CheckEnabledInterceptor implements MethodInterceptor {
	

	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(SPropsUtil.getBoolean("remoting.ws.enable", true)) {
			return invocation.proceed();			
		}
		else {
			throw new RuntimeException("Web Services is disabled");
		}
	}
}



