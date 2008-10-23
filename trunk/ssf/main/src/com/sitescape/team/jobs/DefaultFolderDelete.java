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

	public void remove(Long zoneId) {
		Scheduler scheduler = getScheduler();		
		try {
			scheduler.unscheduleJob(zoneId.toString(), FOLDER_DELETE_GROUP);
		} catch (SchedulerException se) {			
			logger.error(se.getLocalizedMessage()==null?se.getMessage():se.getLocalizedMessage());
		}
		
	}
    public void schedule(Long zoneId, int hours) {
		Scheduler scheduler = getScheduler();		
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
