/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.search.interceptor;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.exception.NoStackTrace;
import org.kablink.teaming.search.IndexSynchronizationManager;


/**
 * @author Jong Kim
 *
 */
public class IndexSynchronizationManagerInterceptor implements MethodInterceptor, Serializable {

	private static final long serialVersionUID = 1L;

	protected static final Log logger = LogFactory.getLog(IndexSynchronizationManagerInterceptor.class);

	private static final ThreadLocal<Integer> depth = new ThreadLocal<Integer>();
	
	private static final ThreadLocal<Integer> threshold = new ThreadLocal<Integer>();
    
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(getDepth() == 0) {
			if(logger.isTraceEnabled())
				logger.trace("Begin index-synchronization session for the thread");
			
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
			if(logger.isTraceEnabled()) {
				if(e instanceof NoStackTrace)
					logger.trace("Unchecked exception during (" + invocation.toString() + "): " +  e.toString());
				else
					logger.trace("Unchecked exception during (" + invocation.toString() + ")", e);				
			}
			else if(logger.isDebugEnabled()) {
				logger.debug("Unchecked exception during (" + invocation.toString() + "): " +  e.toString());
			}
			throw e;
		}
		catch(Exception e) {
			// Non RuntimeException does not cause database transaction
			// to rollback. Keep the same policy for user transaction as well.
			successful = true;
			if(logger.isTraceEnabled()) {
				if(e instanceof NoStackTrace)
					logger.trace("Checked exception during (" + invocation.toString() + "): " +  e.toString());
				else
					logger.trace("Checked exception during (" + invocation.toString() + ")", e);				
			}
			else if(logger.isDebugEnabled()) {
				logger.debug("Unchecked exception during (" + invocation.toString() + "): " +  e.toString());
			}
			throw e;
		}
		finally {
			decrDepth();
			
			if(getDepth() == 0) {
				if(successful) {
					if(logger.isTraceEnabled())
						logger.trace("Commit index-synchronization session for the thread");
			
					IndexSynchronizationManager.applyChanges(getThreshold());
				}
				else {
					if(logger.isTraceEnabled())
						logger.trace("Discard index-synchronization session for the thread", new Exception("Just to show where we are in the code"));
					else if(logger.isDebugEnabled())
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
	
	/*
	 * Disable auto-apply for the current thread only
	 */
	public static int getThreshold() {
		Integer t = threshold.get();
		if(t == null)
			return 1;
		else
			return t.intValue();
	}
	
	public static void setThreshold(int t) {
		threshold.set(Integer.valueOf(t));
	}
	
	public static void clearThreshold() {
		threshold.set(null);
	}
}

