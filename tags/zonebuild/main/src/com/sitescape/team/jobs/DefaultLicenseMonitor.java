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

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.domain.LicenseStats;
import com.sitescape.team.module.license.LicenseModule;
import com.sitescape.team.util.SpringContextUtil;

public class DefaultLicenseMonitor extends SSStatefulJob implements
		LicenseMonitor {

	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException
	{
		LicenseModule licenseModule = (LicenseModule) SpringContextUtil.getBean("licenseModule");
		licenseModule.recordCurrentUsage();
	}

	public void remove(Long zoneId) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		try {
			scheduler.unscheduleJob(zoneId.toString(), LICENSE_MONITOR_GROUP);
		} catch (SchedulerException se) {			
			logger.error(se.getLocalizedMessage()==null?se.getMessage():se.getLocalizedMessage());
		}
		
	}
	public void schedule(Long zoneId, int hour)
	{
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		try {
			JobDetail jobDetail=scheduler.getJobDetail(zoneId.toString(), LICENSE_MONITOR_GROUP);
			if (jobDetail == null) {
				jobDetail = new JobDetail(zoneId.toString(), LICENSE_MONITOR_GROUP, 
						Class.forName(this.getClass().getName()),false, false, false);
				jobDetail.setDescription(LICENSE_MONITOR_DESCRIPTION);
				JobDataMap data = new JobDataMap();
				data.put("zoneId",zoneId);
				jobDetail.setJobDataMap(data);
				jobDetail.addJobListener(getDefaultCleanupListener());
				scheduler.addJob(jobDetail, true);
			}
			int milliSeconds = 24*60*60*1000;

			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, 0);
			cal.add(Calendar.DATE, 1);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			/* DEBUG
			 * Uncomment the next line (which has two statements) to change the schedule
			 *  to every 2 minutes, starting immediately
			 */
			// milliSeconds = 2*60*1000; cal = GregorianCalendar.getInstance();
			

			SimpleTrigger trigger =
				new SimpleTrigger(zoneId.toString(), LICENSE_MONITOR_GROUP, zoneId.toString(), LICENSE_MONITOR_GROUP, cal.getTime(), null, 
						SimpleTrigger.REPEAT_INDEFINITELY, milliSeconds);
			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT);
			trigger.setDescription(LICENSE_MONITOR_DESCRIPTION);
			trigger.setVolatility(false);
			if(scheduler.getTrigger(zoneId.toString(), LICENSE_MONITOR_GROUP) == null) {
				scheduler.scheduleJob(trigger);				
			} else {
				scheduler.rescheduleJob(zoneId.toString(), LICENSE_MONITOR_GROUP, trigger);
			} 
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
  		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
  		}
	}
}
