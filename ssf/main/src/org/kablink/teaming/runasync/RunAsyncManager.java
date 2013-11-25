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
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SessionUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class RunAsyncManager implements InitializingBean, DisposableBean {

	private Log logger = LogFactory.getLog(getClass());
	
	private ExecutorService executorService;
	private int executorTerminationTimeout;
	
	public void afterPropertiesSet() throws Exception {
		executorService = Executors.newCachedThreadPool();
		executorTerminationTimeout = SPropsUtil.getInt("runasync.executor.termination.timeout", 20);
	}

	public void destroy() throws Exception {
		if(executorService != null) {
			executorService.shutdown();
			try {
				executorService.awaitTermination(executorTerminationTimeout, TimeUnit.SECONDS);
			}
			catch(InterruptedException e) {
				Thread.currentThread().interrupt(); // Restore the interrupt
			}
			executorService = null;
		}
	}

	public Future execute(final RunAsyncCallback action) {
		final RequestContext parentRequestContext = RequestContextHolder.getRequestContext();
		Callable<Object> task = new Callable<Object>() {
			public Object call() throws Exception {
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
						Object result = action.doAsynchronously();
						
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
		return executorService.submit(task);
	}

}
