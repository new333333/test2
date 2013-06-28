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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.jobs.SimpleTriggerJob.SimpleJobDescription;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;


public class DefaultLicenseMonitor extends SimpleTriggerJob implements
		LicenseMonitor {

	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException
	{
		LicenseModule licenseModule = (LicenseModule) SpringContextUtil.getBean("licenseModule");
		licenseModule.recordCurrentUsage();
	}

	public void remove(Long zoneId) {
		unscheduleJob(zoneId.toString(), LICENSE_MONITOR_GROUP);
	}
	public void schedule(Long zoneId, int hour)
	{

		schedule(new JobDescription(zoneId, hour));
	}
	protected class JobDescription extends SimpleJobDescription {
		int hour;
		JobDescription(Long zoneId, int hour) {
			super(zoneId, zoneId.toString(), LICENSE_MONITOR_GROUP, LICENSE_MONITOR_DESCRIPTION, 24*60*60);
			this.hour = hour;
		}
		protected Date getStartDate() {
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, 0);
			cal.add(Calendar.DATE, 1);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}
	}
}
