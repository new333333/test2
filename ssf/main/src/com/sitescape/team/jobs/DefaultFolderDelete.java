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
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.util.SpringContextUtil;

/**
 *
 * @author Janet McCann
 */
public class DefaultFolderDelete extends SSStatefulJob implements FolderDelete {
	 
    /*
     * The bulk of this code is taken from org.jbpm.scheduler.impl.SchedulerThread
     * @see com.sitescape.team.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
     */

	public void doExecute(JobExecutionContext context) throws JobExecutionException {	
    	FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
    	folderModule.cleanupFolders();
    }

    public void schedule(Long zoneId, int hours) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		try {
			JobDetail jobDetail=scheduler.getJobDetail(zoneId.toString(), FOLDER_DELETE_GROUP);
			if (jobDetail == null) {
				jobDetail = new JobDetail(zoneId.toString(), FOLDER_DELETE_GROUP, 
						Class.forName(this.getClass().getName()),false, false, false);
				jobDetail.setDescription(FOLDER_DELETE_DESCRIPTION);
				JobDataMap data = new JobDataMap();
				data.put("zoneId",zoneId);
				jobDetail.setJobDataMap(data);
				jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
			}
			int milliSeconds = hours*60*60*1000;
//DEBUGGING			int milliSeconds = 2*60*1000;
			SimpleTrigger trigger = (SimpleTrigger)scheduler.getTrigger(zoneId.toString(), FOLDER_DELETE_GROUP);
			//	see if job exists
			if (trigger == null) {
				trigger = new SimpleTrigger(zoneId.toString(), FOLDER_DELETE_GROUP, zoneId.toString(), FOLDER_DELETE_GROUP, new Date(), null, 
						SimpleTrigger.REPEAT_INDEFINITELY, milliSeconds);
				trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT);
				trigger.setDescription(FOLDER_DELETE_DESCRIPTION);
				trigger.setVolatility(false);
				scheduler.scheduleJob(trigger);				
    	
			} else {
				int state = scheduler.getTriggerState(zoneId.toString(), FOLDER_DELETE_GROUP);
				if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE)) {
					scheduler.resumeJob(zoneId.toString(), FOLDER_DELETE_GROUP);
				}
				if (trigger.getRepeatInterval() != milliSeconds) {
					trigger.setRepeatInterval(milliSeconds);
					scheduler.rescheduleJob(zoneId.toString(), FOLDER_DELETE_GROUP, trigger);
				}
			} 
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
  		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
  		}
    }


}
