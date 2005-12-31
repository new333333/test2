package com.sitescape.ef.remoting.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingInterceptor implements MethodInterceptor {

	protected Log logger = LogFactory.getLog(getClass());

	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			return invocation.proceed();
		}
		catch(Throwable t) {
			logger.error(t.getMessage(), t);
			throw t;
		}
	}

}
