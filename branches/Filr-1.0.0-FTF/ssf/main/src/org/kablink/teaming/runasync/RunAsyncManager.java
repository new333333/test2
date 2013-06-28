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
package org.kablink.teaming.runasync;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SessionUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class RunAsyncManager implements InitializingBean, DisposableBean {

	public enum TaskType {
		JITS, // Just-In-Time Sync on net folder
		FULL_SYNC, // Full Sync on net folder
		OTHER // All others
	}
	
	private Log logger = LogFactory.getLog(getClass());
	
	private ExecutorService executorService_JITS;
	private ExecutorService executorService_FULL_SYNC;
	private ExecutorService executorService_OTHER;

	private int executorTerminationTimeout;
	
	public void afterPropertiesSet() throws Exception {

		// With this pool, the caller will get an exception immediately if there's no thread available to take the request.
		// That is perfectly OK, given the nature of the JITS.
		int jitsMaximumPoolSize = SPropsUtil.getInt("runasync.executor.jits.maximum.pool.size", 300);
		ThreadPoolExecutor jitsExecutor = new ThreadPoolExecutor(
				jitsMaximumPoolSize,
				jitsMaximumPoolSize,
                60L, 
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
		jitsExecutor.allowCoreThreadTimeOut(true);
		executorService_JITS = jitsExecutor;


        int fullSyncMaximumPoolSize = SPropsUtil.getInt("runasync.executor.fullsync.maximum.pool.size", 10);
		int fullSyncMaximumQueueSize = SPropsUtil.getInt("runasync.executor.fullsync.maximum.queue.size", 1000);
        ThreadPoolExecutor fullSyncExecutor = new ThreadPoolExecutor(
        		fullSyncMaximumPoolSize,
        		fullSyncMaximumPoolSize,
                60L, 
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(fullSyncMaximumQueueSize));
        fullSyncExecutor.allowCoreThreadTimeOut(true);
		executorService_FULL_SYNC = fullSyncExecutor;

		
		int otherMaximumPoolSize = SPropsUtil.getInt("runasync.executor.other.maximum.pool.size", 100);
		int otherMaximumQueueSize = SPropsUtil.getInt("runasync.executor.other.maximum.queue.size", 10000);
		ThreadPoolExecutor otherExecutor = new ThreadPoolExecutor(
				otherMaximumPoolSize,
				otherMaximumPoolSize,
                60L, 
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(otherMaximumQueueSize));
		otherExecutor.allowCoreThreadTimeOut(true);
		executorService_OTHER = otherExecutor;


		executorTerminationTimeout = SPropsUtil.getInt("runasync.executor.termination.timeout", 20);
	}

	public void destroy() throws Exception {
		// Initiate shutdown
		if(executorService_FULL_SYNC != null)
			executorService_FULL_SYNC.shutdown();
		if(executorService_JITS != null)
			executorService_JITS.shutdown();
		if(executorService_OTHER != null)
			executorService_OTHER.shutdown();
		
		// Wait on the FULL_SYNC executor service first.
		long start = System.currentTimeMillis();
		if(executorService_FULL_SYNC != null) {
			try {
				executorService_FULL_SYNC.awaitTermination(executorTerminationTimeout*1000, TimeUnit.MILLISECONDS);
			}
			catch(InterruptedException e) {
				Thread.currentThread().interrupt(); // Restore the interrupt
			}
			executorService_FULL_SYNC = null;
		}
		
		// If we still have remaining time available, wait on the JITS executor service.
		if(executorService_JITS != null) {
			long remainingTime = start + executorTerminationTimeout*1000 - System.currentTimeMillis();
			if(remainingTime > 0) {
				try {
					executorService_JITS.awaitTermination(remainingTime, TimeUnit.MILLISECONDS);
				}
				catch(InterruptedException e) {
					Thread.currentThread().interrupt(); // Restore the interrupt
				}
				executorService_JITS = null;
			}
		}
		
		// If we still have remaining time available, wait on the OTHER executor service.
		if(executorService_OTHER != null) {
			long remainingTime = start + executorTerminationTimeout*1000 - System.currentTimeMillis();
			if(remainingTime > 0) {
				try {
					executorService_OTHER.awaitTermination(remainingTime, TimeUnit.MILLISECONDS);
				}
				catch(InterruptedException e) {
					Thread.currentThread().interrupt(); // Restore the interrupt
				}
				executorService_OTHER = null;
			}
		}
	}

	public <V> Future<V> execute(final RunAsyncCallback<V> action, TaskType taskType) throws RejectedExecutionException {
		return _execute(action, taskType, RequestContextHolder.getRequestContext());
	}

	public <V> Future<V> execute(final RunAsyncCallback<V> action, TaskType taskType, User contextUser) throws RejectedExecutionException {
		return _execute(action, taskType, new RequestContext(contextUser, null).resolve());
	}

	private <V> Future<V> _execute(final RunAsyncCallback<V> action, final TaskType taskType, final RequestContext parentRequestContext) throws RejectedExecutionException {
		Callable<V> task = new Callable<V>() {
			public V call() throws Exception {
				boolean hadSession = SessionUtil.sessionActive();
				try {
					if (!hadSession)
						SessionUtil.sessionStartup();	
					try {
						// Copy parent/calling thread's request context
						RequestContextHolder.setRequestContext(parentRequestContext);
						if(logger.isDebugEnabled()) {
							if(parentRequestContext != null)
								logger.debug("Inherit parent's request context " + parentRequestContext.toString());
							else
								logger.debug("No request context to inherit from parent");
						}
						if(logger.isDebugEnabled())
							logger.debug("Executing " + action.toString());
						V result = action.doAsynchronously();
						
						if(logger.isDebugEnabled()) {
							if(result != null)
								logger.debug("Action completed successfully with a return value of type " + result.getClass().getName());
							else
								logger.debug("Action completed successfully with no return value");
						}
						return result;
					}
					finally {
						RequestContextHolder.clear();
					}
				}
				finally {
					if (!hadSession) 
						SessionUtil.sessionStop();
				}				
			}
		};
		
		if(taskType == TaskType.JITS)
			return executorService_JITS.submit(task);
		else if(taskType == TaskType.FULL_SYNC)
			return executorService_FULL_SYNC.submit(task);
		else
			return executorService_OTHER.submit(task);
	}
	
}
