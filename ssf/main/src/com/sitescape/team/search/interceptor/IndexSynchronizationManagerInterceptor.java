/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.search.interceptor;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.InternalException;
import com.sitescape.team.search.IndexSynchronizationManager;

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
		catch(RuntimeException e) {
			// Upon RuntimeException, we rollback user transaction.
			// This is consistent with the way we decide whether to
			// rollback database transaction or not. 
			throw e;
		}
		catch(Exception e) {
			// Non RuntimeException does not cause database transaction
			// to rollback. Keep the same policy for user transaction as well.
			successful = true;
			throw e;
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

