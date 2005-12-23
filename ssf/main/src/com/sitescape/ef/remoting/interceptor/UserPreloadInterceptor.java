package com.sitescape.ef.remoting.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;

public class UserPreloadInterceptor implements MethodInterceptor {
	
	private CoreDao coreDao;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		User user = getCoreDao().findUserByNameOnlyIfEnabled
			(requestContext.getUserName(), requestContext.getZoneName());
		requestContext.setUser(user);
		
		return invocation.proceed();
	}

}
