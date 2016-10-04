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
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.PoolThreadFactory;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SessionUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class RunAsyncManager implements InitializingBean, DisposableBean {

	public enum TaskType {
		JITS, // Just-In-Time Sync on net folder
		FULL_SYNC, // Full Sync on net folder
		MISC // All miscs
	}
	
	private Log logger = LogFactory.getLog(getClass());
	
	private int jitsMaximumPoolSize;
	private int fullSyncMaximumPoolSize;
	private int miscMaximumPoolSize;
	private ThreadPoolExecutor executorService_JITS;
	// NOTE: This pool is used exclusively by NetFolderFullSyncCoordinator. Application code MUST NOT use this pool directly!
	private ThreadPoolExecutor executorService_FULL_SYNC;
	private ThreadPoolExecutor executorService_MISC;

	private int executorTerminationTimeout;
	
	public void afterPropertiesSet() throws Exception {

		// With this pool, the caller will get an exception immediately if there's no thread available to take the request.
		// That is perfectly OK, given the nature of the JITS.
		this.jitsMaximumPoolSize = SPropsUtil.getInt("runasync.executor.jits.maximum.pool.size", 300);
		boolean jitsThreadDaemon = SPropsUtil.getBoolean("runasync.executor.jits.thread.daemon", false);
		int jitsThreadPriority = SPropsUtil.getInt("runasync.executor.jits.thread.priority", Thread.NORM_PRIORITY);
		if(logger.isDebugEnabled())
			logger.debug("Creating JITS thread pool: max pool size=" + jitsMaximumPoolSize + ", thread daemon=" + jitsThreadDaemon + ", thread priority=" + jitsThreadPriority);
		ThreadPoolExecutor jitsExecutor = new ThreadPoolExecutor(
				jitsMaximumPoolSize,
				jitsMaximumPoolSize,
                60L, 
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new PoolThreadFactory("jits", jitsThreadDaemon, jitsThreadPriority));
		jitsExecutor.allowCoreThreadTimeOut(true);
		this.executorService_JITS = jitsExecutor;
		
		
		if(SPropsUtil.getBoolean("nf.full.sync.coordinator.enable", true)) {
			// Create a thread pool that will execute full synchronization.
			// This pool is relatively small in size, and the usage context guarantees that the caller would check the
			// active size of the pool before submitting a new task to execute to ensure that submitted task will 
			// execute (nearly) immediately. Consequently, the associated queue doesn't require any capacity in theory
			// (meaning that SynchronousQueue looks like a perfect one to choose for this situation). However we
			// actually use an unbounded queue. This is because the active size that the caller uses to make the
			// determination is only an approximation rather than absolutely accurate figure, and therefore, it's
			// possible for the submission to fail in rare cases. We keep that from happening by supplying unbounded
			// queue which can temporarily retain the extra tasks submitted under that rare condition. In reality 
			// this queue will never grow beyond more than just a few elements.
			this.fullSyncMaximumPoolSize = SPropsUtil.getInt("runasync.executor.fullsync.maximum.pool.size", 5);
			boolean fullSyncThreadDaemon = SPropsUtil.getBoolean("runasync.executor.fullsync.thread.daemon", false);
			int fullSyncThreadPriority = SPropsUtil.getInt("runasync.executor.fullsync.thread.priority", Thread.NORM_PRIORITY);
			if(logger.isDebugEnabled())
				logger.debug("Creating FULL SYNC thread pool: max pool size=" + fullSyncMaximumPoolSize + ", thread daemon=" + fullSyncThreadDaemon + ", thread priority=" + fullSyncThreadPriority);

			ThreadPoolExecutor fullSyncExecutor = new ThreadPoolExecutor(
					fullSyncMaximumPoolSize,
					fullSyncMaximumPoolSize,
	                120L, 
	                TimeUnit.SECONDS,
	                new LinkedBlockingQueue<Runnable>(),
	                new PoolThreadFactory("fullsync", fullSyncThreadDaemon, fullSyncThreadPriority));
			fullSyncExecutor.allowCoreThreadTimeOut(true);
			executorService_FULL_SYNC = fullSyncExecutor;
		}
		else {
			if(logger.isDebugEnabled())
				logger.debug("Will not create FULL SYNC thread pool since net folder full sync coordinator is disabled");			
		}

		
		// If the pool is already at the core size, the executor creates a new thread only if the work queue is full.
		// With large queue size as this, it is not desirable since we don't want to add that many tasks in the
		// queue before being able to create a new thread beyond the core size. For that reason, we set the core
		// size to be the same as the max pool size, forcing the executor to create a new thread immediately as
		// soon as a new work arrives and all existing threads are busy up to the max pool size limit.
		// One down side of this approach is that the pool can contain as many idle threads as the max pool size
		// even when there are no work to execute. To remedy this, we set the allowCoreThreadTimeOut to true,
		// which will allow all the worker threads to be able to time out and eventually be torn down. 
		miscMaximumPoolSize = SPropsUtil.getInt("runasync.executor.misc.maximum.pool.size", 100);
		int miscMaximumQueueSize = SPropsUtil.getInt("runasync.executor.misc.maximum.queue.size", 10000);
		boolean miscThreadDaemon = SPropsUtil.getBoolean("runasync.executor.misc.thread.daemon", false);
		int miscThreadPriority = SPropsUtil.getInt("runasync.executor.misc.thread.priority", Thread.NORM_PRIORITY);
		if(logger.isDebugEnabled())
			logger.debug("Creating MISC thread pool: max pool size=" + miscMaximumPoolSize + ", max queue size=" + miscMaximumQueueSize + ", thread daemon=" + miscThreadDaemon + ", thread priority=" + miscThreadPriority);

		ThreadPoolExecutor miscExecutor = new ThreadPoolExecutor(
				miscMaximumPoolSize,
				miscMaximumPoolSize,
                60L, 
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(miscMaximumQueueSize),
                new PoolThreadFactory("misc", miscThreadDaemon, miscThreadPriority));
		miscExecutor.allowCoreThreadTimeOut(true);
		executorService_MISC = miscExecutor;


		executorTerminationTimeout = SPropsUtil.getInt("runasync.executor.termination.timeout", 20);
	}

	public void destroy() throws Exception {
		// Initiate shutdown
		if(executorService_FULL_SYNC != null)
			executorService_FULL_SYNC.shutdown();
		if(executorService_JITS != null)
			executorService_JITS.shutdown();
		if(executorService_MISC != null)
			executorService_MISC.shutdown();
		
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
		
		// If we still have remaining time available, wait on the MISC executor service.
		if(executorService_MISC != null) {
			long remainingTime = start + executorTerminationTimeout*1000 - System.currentTimeMillis();
			if(remainingTime > 0) {
				try {
					executorService_MISC.awaitTermination(remainingTime, TimeUnit.MILLISECONDS);
				}
				catch(InterruptedException e) {
					Thread.currentThread().interrupt(); // Restore the interrupt
				}
				executorService_MISC = null;
			}
		}
	}

	public <V> Future<V> execute(final RunAsyncCallback<V> action, TaskType taskType) throws RejectedExecutionException {
		return _execute(action, taskType, RequestContextHolder.getRequestContext());
	}

	public <V> Future<V> execute(final RunAsyncCallback<V> action, TaskType taskType, User contextUser) throws RejectedExecutionException {
		return _execute(action, taskType, new RequestContext(contextUser, null).resolve());
	}

	public int getPoolMaximumSize(TaskType taskType) {
		if(taskType == TaskType.JITS)
			return jitsMaximumPoolSize;
		else if(taskType == TaskType.FULL_SYNC)
			return fullSyncMaximumPoolSize;
		else
			return miscMaximumPoolSize;
	}
	
	public int getPoolActiveCount(TaskType taskType) {
		if(taskType == TaskType.JITS) {
			return executorService_JITS.getActiveCount();
		}
		else if(taskType == TaskType.FULL_SYNC) {
			if(executorService_FULL_SYNC != null)
				return executorService_FULL_SYNC.getActiveCount();
			else
				return 0;
		}
		else {
			return executorService_MISC.getActiveCount();
		}
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
		
		if(taskType == TaskType.JITS) {
			return executorService_JITS.submit(task);
		}
		else if(taskType == TaskType.FULL_SYNC) {
			if(executorService_FULL_SYNC != null)
				return executorService_FULL_SYNC.submit(task);
			else
				throw new ConfigurationException("No thread pool exists for full sync tasks");
		}
		else {
			return executorService_MISC.submit(task);
		}
	}
	
}
