package com.sitescape.ef.search.interceptor;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.search.IndexSynchronizationManager;

/**
 * @author Jong Kim
 *
 */
public class IndexSynchronizationManagerInterceptor implements MethodInterceptor, Serializable {

    protected static final Log logger = LogFactory.getLog(IndexSynchronizationManagerInterceptor.class);

	public Object invoke(MethodInvocation invocation) throws Throwable {
	    logger.debug("Begin index-synchronization session for the thread");

		IndexSynchronizationManager.begin();
	    
		Object rval = invocation.proceed();
		
	    logger.debug("Commit index-synchronization session for the thread");
		
		IndexSynchronizationManager.commit();
		
		return rval;
	}
}
