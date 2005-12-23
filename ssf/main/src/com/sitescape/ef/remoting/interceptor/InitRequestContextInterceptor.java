package com.sitescape.ef.remoting.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.sitescape.ef.context.request.RequestContextUtil;

/**
 * AOP Alliance MethodInterceptor responsible for setting up request context
 * for the thread executing remote request. 
 * 
 * @author jong
 *
 */
public class InitRequestContextInterceptor implements MethodInterceptor {

	public Object invoke(MethodInvocation invocation) throws Throwable {
		String zoneName = "liferay.com"; // TODO Use real one
		String userName = "liferay.com.1"; // TODO 
		
		RequestContextUtil.setThreadContext(zoneName, userName);
		
		try {
			return invocation.proceed();
		}
		finally {
	    	RequestContextUtil.clearThreadContext();
		}
	}

}
