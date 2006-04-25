package com.sitescape.ef.util.aopalliance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MethodInvocationTraceInterceptor implements MethodInterceptor {

	protected Log logger = LogFactory.getLog(getClass());

	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(logger.isInfoEnabled())
			logger.info(invocation.toString());
		
		return invocation.proceed();
	}
}
