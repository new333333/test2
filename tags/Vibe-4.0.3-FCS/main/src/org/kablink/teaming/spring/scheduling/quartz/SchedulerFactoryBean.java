/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.spring.scheduling.quartz;

import org.kablink.teaming.web.servlet.listener.ContextListenerPostSpring;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.scheduling.SchedulingException;

/**
 * This class extends the original class to provide more precise lazy initialization
 * that is guaranteed to be safe for the system.
 * 
 * @author Jong
 *
 */
public class SchedulerFactoryBean extends org.springframework.scheduling.quartz.SchedulerFactoryBean {
	
	private static final int CHECK_INTERVAL = 30; // 30 seconds

	@Override
	protected void startScheduler(final Scheduler scheduler, final int startupDelay) throws SchedulerException {
		if (startupDelay <= 0) {
			logger.info("Starting Quartz Scheduler now");
			scheduler.start();
		}
		else {
			if (logger.isInfoEnabled()) {
				logger.info("Will attempt to start Quartz Scheduler [" + scheduler.getSchedulerName() +
						"] in " + startupDelay + " seconds");
			}
			Thread schedulerThread = new Thread() {
				@Override
				public void run() {
					// Initial startup delay specified in the configuration file.
					try {
						Thread.sleep(startupDelay * 1000);
					}
					catch (InterruptedException ignore) {}
					// Don't start scheduler if the system is still in the process of starting up.
					int count = 0;
					while (ContextListenerPostSpring.isStartupInProgress()) {
						if (logger.isDebugEnabled()) {
							logger.debug("Server still starting up, delaying " + CHECK_INTERVAL + " additional seconds");
						}
						try {
							Thread.sleep(CHECK_INTERVAL * 1000);
						}
						catch (InterruptedException ignore) {}
						count++;
					}
					if (logger.isInfoEnabled()) {
						logger.info("Starting Quartz Scheduler now, after delay of " + (startupDelay + CHECK_INTERVAL*count) + " seconds in total");
					}
					try {
						scheduler.start();
					}
					catch (SchedulerException ex) {
						throw new SchedulingException("Could not start Quartz Scheduler after delay", ex);
					}
				}
			};
			schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
			schedulerThread.setDaemon(true);
			schedulerThread.start();
		}
	}

}
