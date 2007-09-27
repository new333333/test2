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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;

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
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		Long folderId = new Long(jobDataMap.getLong("binder"));
		Long entryId = new Long(jobDataMap.getLong("entry"));
		Date stamp = (Date)jobDataMap.get("when");
		try {
			mail.fillSubscription(folderId, entryId, stamp);
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
			context.setResult("Success");
		} catch (Exception ex) {
			//remove job
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
			throw new JobExecutionException(ex);
		}
    }

		
    public void schedule(Long folderId, Long entryId, Date changeDate) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		//each job is new = don't use verify schedule, cause this unique
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.MINUTE, 1);
		
		//add time to jobName - may have multiple 
	 	String jobName =  "fillEmailSubscription" + "-" + entryId + "-" + changeDate.getTime();
	 	String className = this.getClass().getName();
	  	try {		
			JobDetail jobDetail = new JobDetail(jobName, ENTRY_SUBSCRIPTION_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription("Fill subscription");
			JobDataMap data = new JobDataMap();
			data.put("binder", folderId);
			data.put("zoneId",RequestContextHolder.getRequestContext().getZoneId());
			data.put("entry", entryId);
			data.put("when", changeDate);
			
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(getDefaultCleanupListener());
			//retry every hour
	  		SimpleTrigger trigger = new SimpleTrigger(jobName, ENTRY_SUBSCRIPTION_GROUP, jobName, ENTRY_SUBSCRIPTION_GROUP, start.getTime(), null, 24, 1000*60*60);
  			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
  			trigger.setDescription("Fill subscription");
  			trigger.setVolatility(false);
			scheduler.scheduleJob(jobDetail, trigger);				
 		} catch (Exception e) {
   			throw new ConfigurationException("Cannot start (job:group) " + jobName 
   					+ ":" + ENTRY_SUBSCRIPTION_GROUP, e);
   		}
    }
}

