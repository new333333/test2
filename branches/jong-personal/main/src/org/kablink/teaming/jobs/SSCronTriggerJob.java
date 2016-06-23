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

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.kablink.teaming.ConfigurationException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import static org.quartz.JobKey.*;
import static org.quartz.TriggerKey.*;

public abstract class SSCronTriggerJob extends SSStatefulJob {
	/**
	 * Sync quartz with new scheduler information..  Always register job, even if disabled.
	 * @param info
	 * @throws ParseException
	 */
	public void setScheduleInfo(CronJobDescription job, ScheduleInfo info) {
		try {
			Scheduler scheduler = getScheduler();		
		 	JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey(job.getJobName(), job.getJobGroup()));
		 	//never been scheduled -start now
		 	if (jobDetail == null) {
 				//volitility(not stored in db),durablilty(remains after trigger removed),recover(after recover or fail-over)
		 		jobDetail = new JobDetailImpl(job.getJobName(), job.getJobGroup(),
		 				this.getClass(), job.getDurability(), false);
				jobDetail.setDescription(job.getJobDescription());
				jobDetail.setJobDataMap((JobDataMap)info.getDetails());
				//jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
		 	} else {
		 		//update data if necessary
		 		if (!jobDetail.getJobDataMap().equals(info.getDetails())) {
			 		jobDetail.setJobDataMap((JobDataMap)info.getDetails());	 			
		 			scheduler.addJob(jobDetail, true);
		 		}
		 	}
  			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey(job.getTriggerName(), job.getTriggerGroup()));
  			//see if stopped
  			if (trigger == null) {
  				if (info.isEnabled()) {
  					trigger = buildCronTrigger(job, info.getSchedule());
  					scheduler.scheduleJob(trigger);
  				} 
  			} else {
  				//make sure schedule is the same
  				if (info.isEnabled()) {
  					String cSched = trigger.getCronExpression();
  					String nSched = info.getSchedule().getQuartzSchedule();
  					if (!nSched.equals(cSched)) {
  						trigger = buildCronTrigger(job, info.getSchedule());
  				 		scheduler.rescheduleJob(new TriggerKey(job.getJobName(), job.getJobGroup()), trigger);
 	 				} else {
 	 					TriggerState state = scheduler.getTriggerState(new TriggerKey(job.getTriggerName(), job.getTriggerGroup()));
 	 					if ((state == Trigger.TriggerState.PAUSED) || (state == Trigger.TriggerState.NONE)) {
 	 						scheduler.resumeJob(new JobKey(job.getJobName(), job.getJobGroup()));
 	 					}
 	 				}
  				} else {
			 		scheduler.unscheduleJob(new TriggerKey(job.getJobName(), job.getJobGroup()));  					
  				}
				
  			}
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());
		} catch (ParseException pe) {
			throw new ConfigurationException("Error parsing schedule", pe);
		}
		
	}
	/**
	 * Get scheduler information for a job
	 * @param job
	 * @return
	 */
	public ScheduleInfo getScheduleInfo(CronJobDescription job) {
		try {
			Scheduler scheduler = getScheduler();		
			JobDetail jobDetail=scheduler.getJobDetail(new JobKey(job.getJobName(), job.getJobGroup()));
			if (jobDetail == null) {
				return job.getDefaultScheduleInfo();
			}
			
			ScheduleInfo info = new ScheduleInfo(job.getZoneId());
			TriggerState state = scheduler.getTriggerState(new TriggerKey(job.getTriggerName(), job.getTriggerGroup()));
			if ((state == Trigger.TriggerState.PAUSED) || (state == Trigger.TriggerState.NONE))
				info.setEnabled(false);
			else
				info.setEnabled(true);
			info.setDetails(jobDetail.getJobDataMap());
			return info;
		} catch (SchedulerException se) {
			return job.getDefaultScheduleInfo();
		}
	}


	public CronTrigger buildCronTrigger(CronJobDescription job, Schedule schedule) throws ParseException{
    	
   		CronTriggerImpl trigger = new CronTriggerImpl(job.getJobName(), job.getJobGroup(), job.getTriggerName(), 
   					job.getTriggerGroup(), schedule.getQuartzSchedule(), job.getTimeZone());
   		trigger.setMisfireInstruction(job.getMisfireInstruction());
   		//trigger.setVolatility(false);
   		trigger.setPriority(job.getPriority());
 
		return trigger;    	
    }
	/**
	 * Enable or disable a job
	 * @param job
	 */
	public void enable(boolean enable, CronJobDescription job) {
		ScheduleInfo info = getScheduleInfo(job); 
		if (enable && info.isEnabled()) return;
		if (!enable && !info.isEnabled()) return;
		info.setEnabled(enable);
		setScheduleInfo(job,info);
 	}

 	public class CronJobDescription {
		protected Long zoneId;
		protected String jobName, jobGroup, jobDescription;
		boolean durability = true;
		int priority = 5;
		CronJobDescription(Long zoneId) {
			this.zoneId = zoneId;
		}
		CronJobDescription(Long zoneId, String jobName, String jobGroup, String jobDescription) {
			this.zoneId = zoneId;
			this.jobName = jobName;
			this.jobGroup = jobGroup;
			this.jobDescription = trimDescription(jobDescription);
		}
		CronJobDescription(Long zoneId, String jobName, String jobGroup, String jobDescription, boolean durability) {
			this(zoneId, jobName, jobGroup, jobDescription);
			this.durability = durability;
		}
		CronJobDescription(Long zoneId, String jobName, String jobGroup, String jobDescription, boolean durability, int priority) {
			this(zoneId, jobName, jobGroup, jobDescription);
			this.durability = durability;
			this.priority = priority;
		}
		protected Long getZoneId() {
			return zoneId;
		}
		protected String getJobName() {
			return jobName;
		}
		protected String getJobGroup() {
			return jobGroup;
		}
		protected String getJobDescription() {
			return jobDescription;
		}
     	public TimeZone getTimeZone() {
    		return getDefaultTimeZone();
     	}
 		
		protected String getTriggerName() {
			return getJobName();
		}
		protected String getTriggerGroup() {
			return getJobGroup();
		}
		protected String getTriggerDescription() {
			return getJobDescription();
		}
		protected Date getStartDate() {
			return new Date();
		}
		protected Date getEndDate() {
			return null;
		}
		protected int getMisfireInstruction() {
			return CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
		}
		protected ScheduleInfo getDefaultScheduleInfo() {
	   		return new ScheduleInfo(zoneId);   	 
		}
		protected boolean getDurability() {
			return durability;
		}
		protected int getPriority() {
			return priority;
		}
	}
}
