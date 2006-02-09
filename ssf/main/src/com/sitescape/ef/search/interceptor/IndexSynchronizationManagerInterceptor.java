package com.sitescape.ef.search.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.search.IndexSynchronizationManager;

/**
 * @author Jong Kim
 *
 */
public class IndexSynchronizationManagerInterceptor implements MethodInterceptor, Serializable {

    protected static final Log logger = LogFactory.getLog(IndexSynchronizationManagerInterceptor.class);

	private static final ThreadLocal depth = new ThreadLocal();
    
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(getDepth() == 0) {
			logger.debug("Begin index-synchronization session for the thread");
			
			IndexSynchronizationManager.begin();
		}
		incrDepth();
	    
		Object rval = null;
		boolean successful = false;
		
		try {
			rval = invocation.proceed();
			successful = true;
		}
		finally {
			decrDepth();
			
			if(getDepth() == 0) {
				if(successful) {
					logger.debug("Commit index-synchronization session for the thread");
			
					IndexSynchronizationManager.applyChanges();
				}
				else {
					logger.debug("Discard index-synchronization session for the thread");
					
					IndexSynchronizationManager.discardChanges();					
				}
			}
		}
		
		return rval;
	}
	
	private int getDepth() {
		Integer d = (Integer) depth.get();
		if(d == null)
			return 0;
		else
			return d.intValue();
	}
	
	private void incrDepth() {
		Integer d = (Integer) depth.get();
		if(d == null)
			depth.set(new Integer(1));
		else
			depth.set(new Integer(d.intValue() + 1));
	}
	
	private void decrDepth() {
		Integer d = (Integer) depth.get();
		if(d == null)
			throw new InternalException();
		else
			depth.set(new Integer(d.intValue() - 1));
	}	
}

