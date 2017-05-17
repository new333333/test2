/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.jobs.DefaultAuditTrailMigration.JobDescription;
import org.kablink.teaming.jobs.SimpleTriggerJob.SimpleJobDescription;
import org.kablink.teaming.module.zone.ZoneUtil;
import org.kablink.teaming.telemetry.TelemetryService;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Jong
 *
 */
public class DefaultTelemetryProcess extends SimpleTriggerJob implements TelemetryProcess {

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
		// First, collect and save telemetry data for the current period.
		try {
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(ZoneUtil.getDefaultZoneId());
			Boolean tier2Enabled = zoneConfig.getTelemetryTier2Enabled();
			// If tier2Enabled flag is null, it means that the user has had no chance to respond to the system's
			// question about whether to allow or deny the tier2 part yet. In that case, we default to collecting them. 
			getTelemetryService().collectAndSaveTelemetryData((tier2Enabled != null)? tier2Enabled.booleanValue() : true);
		}
		catch(Exception e) {
			logger.error("Failed to collect and save telemetry data", e);
		}
		
		// Next, upload telemetry data collected during the current period as well as during previous periods if any.
		try {
			getTelemetryService().uploadTelemetryData();
		}
		catch(Exception e) {
			logger.error("Failed to upload telemetry data", e);
		}		
		
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.TelemetryProcess#remove()
	 */
	@Override
	public void remove() {
		Long zoneId = ZoneUtil.getDefaultZoneId();
		unscheduleJob(zoneId.toString(), TELEMETRY_PROCESS_GROUP);
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.TelemetryProcess#schedule(int)
	 */
	@Override
	public void schedule(int repeatIntervalInSeconds, int delayInSeconds) {
		Long zoneId = ZoneUtil.getDefaultZoneId();
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.SECOND, delayInSeconds);
		schedule(new JobDescription(zoneId, start.getTime(), repeatIntervalInSeconds));
	}
	
	TelemetryService getTelemetryService() {
		return (TelemetryService)SpringContextUtil.getBean("telemetryService");
	}

	public class JobDescription extends SimpleJobDescription {
		Date startDate;
		JobDescription(Long zoneId, Date startDate, int repeatIntervalInSeconds) {
			super(zoneId, zoneId.toString(), TELEMETRY_PROCESS_GROUP, TELEMETRY_PROCESS_DESCRIPTION, repeatIntervalInSeconds);
			this.startDate = startDate;
		}
		@Override
		protected Date getStartDate() {
			return startDate;
		}
	}

}
