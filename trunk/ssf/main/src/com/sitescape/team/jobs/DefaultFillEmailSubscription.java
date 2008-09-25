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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.util.SpringContextUtil;

/**
 * @author Janet McCann
 *
 */
public class DefaultFillEmailSubscription extends SSStatefulJob implements FillEmailSubscription {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.team.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
		//assume old job from v1 where each changed entry registered a job
		if (!zoneId.toString().equals(context.getTrigger().getJobName())) {
				removeJob(context);
		} else {			
			if (!coreDao.loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()).getMailConfig().isSendMailEnabled()) {
				logger.debug("Sending mail is not enabled for zone " + RequestContextHolder.getRequestContext().getZoneName());
				return;
			}
			MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
			Date begin = (Date)jobDataMap.get("lastNotification");
			Date end = mail.fillSubscriptions(begin);
			jobDataMap.put("lastNotification", end);
		}
    }

	public void remove(Long zoneId) {
		Scheduler scheduler = getScheduler();		
		try {
			scheduler.unscheduleJob(zoneId.toString(), SUBSCRIPTION_GROUP);
		} catch (SchedulerException se) {			
			logger.error(se.getLocalizedMessage()==null?se.getMessage():se.getLocalizedMessage());
		}
		
	}
	
    public void schedule(Long zoneId, Date changeDate, int minutes) {
		Scheduler scheduler = getScheduler();		
		
	 	String className = this.getClass().getName();
	  	try {		
		 	JobDetail jobDetail=scheduler.getJobDetail(zoneId.toString(), SUBSCRIPTION_GROUP);
			if (jobDetail == null) {
				jobDetail = new JobDetail(zoneId.toString(), SUBSCRIPTION_GROUP, 
						Class.forName(className),false, false, false);
				jobDetail.setDescription(SUBSCRIPTION_DESCRIPTION);
				JobDataMap data = new JobDataMap();
				data.put("zoneId",zoneId);
				if (changeDate != null) {
					data.put("lastNotification", changeDate);
				} else {
					data.put("lastNotification", new Date());					
				}
			
				jobDetail.setJobDataMap(data);
				jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
			}
			
			SimpleTrigger trigger = (SimpleTrigger)scheduler.getTrigger(zoneId.toString(), SUBSCRIPTION_GROUP);
			//	see if job exists
			if (trigger == null) {
				trigger = new SimpleTrigger(zoneId.toString(), SUBSCRIPTION_GROUP, zoneId.toString(), SUBSCRIPTION_GROUP, new Date(), null, 
						SimpleTrigger.REPEAT_INDEFINITELY, minutes*60*1000);
				trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
				trigger.setDescription(SUBSCRIPTION_DESCRIPTION);
				trigger.setVolatility(false);
				scheduler.scheduleJob(trigger);				
    	
			} else {
				int state = scheduler.getTriggerState(zoneId.toString(), SUBSCRIPTION_GROUP);
				if ((state == Trigger.STATE_PAUSED) || (state == Trigger.STATE_NONE)) {
					scheduler.resumeJob(zoneId.toString(), SUBSCRIPTION_GROUP);
				}
				if (trigger.getRepeatInterval() != minutes*60*1000) {
					trigger.setRepeatInterval(minutes*60*1000);
					scheduler.rescheduleJob(zoneId.toString(), SUBSCRIPTION_GROUP, trigger);
				}
			} 
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
  		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
  		}
    }
}

