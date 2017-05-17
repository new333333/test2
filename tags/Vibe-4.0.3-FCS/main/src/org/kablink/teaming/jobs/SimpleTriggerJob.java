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

import java.util.Date;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.runasync.RunAsyncCallback;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import static org.quartz.JobKey.*;
import static org.quartz.TriggerKey.*;

public abstract class SimpleTriggerJob extends SSStatefulJob {
	
	public void schedule(SimpleJobDescription job) {
		Scheduler scheduler = getScheduler();		
		try {
			JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey(job.getJobName(), job.getJobGroup()));
			if (jobDetail == null) {
				jobDetail = new JobDetailImpl(job.getJobName(), job.getJobGroup(), 
						(Class<? extends Job>) Class.forName(this.getClass().getName()), job.getDurability(), false);
				jobDetail.setDescription(job.getJobDescription());
				jobDetail.setJobDataMap(job.getData());
				//jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
			} else {
				boolean changed = false;
		 		//update data if necessary
		 		if (!jobDetail.getJobDataMap().equals(job.getData())) {
			 		jobDetail.setJobDataMap(job.getData());	 			
			 		changed = true;
		 		}
		 		if (!jobDetail.isDurable()) {
			 		jobDetail.setDurability(true); // Required for Quartz 2.x.
			 		changed = true;
		 		}
		 		if(changed)
		 			scheduler.addJob(jobDetail, true);
			}
			SimpleTriggerImpl trigger = (SimpleTriggerImpl)scheduler.getTrigger(triggerKey(job.getTriggerName(), job.getTriggerGroup()));
			//	see if job exists
			if (trigger == null) {
				trigger = new SimpleTriggerImpl(job.getJobName(), job.getJobGroup(), job.getTriggerName(), job.getTriggerGroup(), job.getStartDate(), job.getEndDate(), 
						job.getRepeatCount(),  job.getRepeatSeconds()*1000);
				trigger.setMisfireInstruction(job.getMisfireInstruction());
				trigger.setDescription(job.getTriggerDescription());
				//trigger.setVolatility(false);
				trigger.setPriority(job.getPriority());
				scheduler.scheduleJob(trigger);				
		
			} else {
				if (trigger.getRepeatInterval() != job.getRepeatSeconds()*1000) {
					trigger.setRepeatInterval(job.getRepeatSeconds()*1000);
					scheduler.rescheduleJob(triggerKey(job.getJobName(), job.getJobGroup()), trigger);
				}
			} 
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
		}
	}
	
	public void scheduleNonBlocking(final SimpleJobDescription job) {
		getRunAsyncManager().execute(new RunAsyncCallback<Object>() {
			@Override
			public Object doAsynchronously() throws Exception {
				schedule(job);
				return null;
			}
		}, RunAsyncManager.TaskType.MISC);
	}
	
	public class SimpleJobDescription {
		protected Long zoneId;
		protected String jobName, jobGroup, jobDescription;
		int seconds;
		// 09/12/2016 JK - Quartz 2.x throws SchedulerException("Jobs added with no trigger must be durable.")
		// exception when attempting to save a job with durability set to false when there's no
		// associated trigger in the database. However, if you try to create a trigger before 
		// creating associated job, it throws an error saying that associated job isn't found. 
		// So, basically, you're stuck, and the only option seems to be to create all jobs
		// with durability set to true whether you want it or not. So, here we go.
		boolean durability = true;
		int priority = 5;
		SimpleJobDescription(Long zoneId) {
			this.zoneId = zoneId;
		}
		public SimpleJobDescription(Long zoneId, String jobName, String jobGroup, String jobDescription, int seconds) {
			this.zoneId = zoneId;
			this.jobName = jobName;
			this.jobGroup = jobGroup;
			this.jobDescription = trimDescription(jobDescription);

			this.seconds = seconds;
		}
		public SimpleJobDescription(Long zoneId, String jobName, String jobGroup, String jobDescription, int seconds, boolean durability) {
			this(zoneId, jobName, jobGroup, jobDescription, seconds);
			//this.durability = durability;
		}
		public SimpleJobDescription(Long zoneId, String jobName, String jobGroup, String jobDescription, int seconds, boolean durability, int priority) {
			this(zoneId, jobName, jobGroup, jobDescription, seconds);
			//this.durability = durability;
			this.priority = priority;
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
		protected int getRepeatSeconds() {
			return seconds;
		}
		
		protected JobDataMap getData() {
			JobDataMap data = new JobDataMap();
			data.put("zoneId",zoneId);
			return data;
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
		protected int getRepeatCount() {
			return SimpleTrigger.REPEAT_INDEFINITELY;
		}
		protected int getMisfireInstruction() {
			return SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT;
		}
		protected boolean getDurability() {
			return durability;
		}
		protected int getPriority() {
			return priority;
		}
	}
	
	private RunAsyncManager getRunAsyncManager() {
		return (RunAsyncManager) SpringContextUtil.getBean("runAsyncManager");
	}
}
