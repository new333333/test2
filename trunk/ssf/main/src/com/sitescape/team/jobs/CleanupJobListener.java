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
