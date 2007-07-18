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
			// milliSeconds = 2*60*1000; cal = GregorianCalendar().getInstance();
			

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
