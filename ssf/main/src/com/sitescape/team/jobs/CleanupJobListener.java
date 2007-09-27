/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */

package com.sitescape.team.jobs;

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
	public void jobToBeExecuted(JobExecutionContext arg0) {
		// do nothing

	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
	 */
	public void jobExecutionVetoed(JobExecutionContext arg0) {
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
