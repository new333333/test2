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
			// Do NOT clear the thread context because some of the interceptors
			// that come before this intereptor may still depend upon the
			// thread context being there. For example logging interceptor
			// uses the user's locale information for error message).
			// Since thread context is reset at the beginning of every new
			// request, this omission shouldn't cause a serious problem. 
	    	//RequestContextUtil.clearThreadContext();
		}
	}

}
