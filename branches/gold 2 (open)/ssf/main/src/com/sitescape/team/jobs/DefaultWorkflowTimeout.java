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

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.util.SpringContextUtil;

/**
 *
 */
public class DefaultWorkflowTimeout extends SSStatefulJob implements WorkflowTimeout {
	 
    /*
     * The bulk of this code is taken from org.jbpm.scheduler.impl.SchedulerThread
     * @see com.sitescape.team.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
     */

	public void doExecute(JobExecutionContext context) throws JobExecutionException {	
		WorkflowModule work = (WorkflowModule)SpringContextUtil.getBean("workflowModule");
		work.processTimers();
	}

	
    public void schedule(Long zoneId, int seconds) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		try {
			JobDetail jobDetail=scheduler.getJobDetail(zoneId.toString(), WORKFLOW_TIMER_GROUP);
			if (jobDetail == null) {
				jobDetail = new JobDetail(zoneId.toString(), WORKFLOW_TIMER_GROUP, 
						Class.forName(this.getClass().getName()),false, false, false);
				jobDetail.setDescription(WORKFLOW_TIMER_DESCRIPTION);
				JobDataMap data = new JobDataMap();
				data.put("zoneId",zoneId);
				jobDetail.setJobDataMap(data);
				jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
			}
			SimpleTrigger trigger = (SimpleTrigger)scheduler.getTrigger(zoneId.toString(), WORKFLOW_TIMER_GROUP);
			//	see if job exists
			if (trigger == null) {
				trigger = new SimpleTrigger(zoneId.toString(), WORKFLOW_TIMER_GROUP, zoneId.toString(), WORKFLOW_TIMER_GROUP, new Date(), null, 
						SimpleTrigger.REPEAT_INDEFINITELY, seconds*1000);
				trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
				trigger.setDescription(WORKFLOW_TIMER_DESCRIPTION);
				trigger.setVolatility(false);
				scheduler.scheduleJob(trigger);				
    	
			} else {
				int state = scheduler.getTriggerState(zoneId.toString(), WORKFLOW_TIMER_GROUP);
				if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE)) {
					scheduler.resumeJob(zoneId.toString(), WORKFLOW_TIMER_GROUP);
				}
				if (trigger.getRepeatInterval() != seconds*1000) {
					trigger.setRepeatInterval(seconds*1000);
					scheduler.rescheduleJob(zoneId.toString(), WORKFLOW_TIMER_GROUP, trigger);
				}
			} 
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
  		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
  		}
    }


}
