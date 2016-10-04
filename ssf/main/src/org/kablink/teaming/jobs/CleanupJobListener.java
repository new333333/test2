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

package org.kablink.teaming.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This listener handles post-processing of jobs. 
 * It does nothing if an exception occurs - assuming the job will run again. 
 * It will delete jobs if requested in the result.
 * 
 *  @author Janet McCann
 *
 */
public class CleanupJobListener implements JobListener {
	public static final Integer DeleteJob = new Integer(1);
	public static final Integer DeleteJobOnError = new Integer(2);
	public static final Integer UnscheduleJob = new Integer(3);
	public static final Integer UnscheduleJobOnError = new Integer(4);
	public static final String CLEANUPSTATUS="cleanupStatus";
	protected Log logger = LogFactory.getLog(getClass());
	public static final String name = "SS_CleanupJobListener";
	/* (non-Javadoc)
	 * @see org.quartz.JobListener#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
	 */
	public void jobToBeExecuted(JobExecutionContext ctx) {
		if (logger.isDebugEnabled())
			logger.debug("Starting job " + ctx.getJobDetail().getFullName());

	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
	 */
	public void jobExecutionVetoed(JobExecutionContext ctx) {
		// do nothing

	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
	 */
	public void jobWasExecuted(JobExecutionContext ctx,
			JobExecutionException exc) {
		//delete job and all triggers if successful
		Scheduler scheduler = ctx.getScheduler();
		JobDetail job = ctx.getJobDetail();
		Integer result = (Integer)ctx.get(CLEANUPSTATUS);
		
		try {
			if (exc == null) {
				if (DeleteJob.equals(result)) {
					logger.info("Removing job " + job.getFullName());
					scheduler.deleteJob(job.getName(),job.getGroup());
				} else if (UnscheduleJob.equals(result)) {
					logger.info("Unscheduling job " + job.getFullName());
					scheduler.unscheduleJob(job.getName(), job.getGroup());
				} else {
					if (logger.isDebugEnabled())
							logger.debug("Completed job " + job.getFullName());
				}
			} else {
				if (DeleteJobOnError.equals(result)) {
					logger.error("Removing job " + job.getFullName() + " after error " + exc.getCause());
					scheduler.deleteJob(job.getName(),job.getGroup());
				} else if (UnscheduleJobOnError.equals(result)) {
					logger.error("Unscheduling job " + job.getFullName() + " after error " + exc.getCause());
					scheduler.unscheduleJob(job.getName(), job.getGroup());
				} else {
					logger.error("Error running job " + job.getFullName() + " " + exc.getCause());
					
				}
			}
		} catch (SchedulerException ex) {
			//TODO??
		}

	}

}
