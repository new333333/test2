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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.module.zone.ZoneUtil;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import static org.quartz.TriggerKey.*;

/**
 * @author Jong
 *
 */
public class TelemetryProcessUtil {

	private static Log logger = LogFactory.getLog(TelemetryProcessUtil.class);
	
	public static void manageTelemetryProcess(boolean telemetryEnabled) {
		String className = SPropsUtil.getString("job.telemetry.process.class", "org.kablink.teaming.jobs.DefaultTelemetryProcess");
		TelemetryProcess telemetryProcess = (TelemetryProcess) ReflectHelper.getInstance(className);
		if(telemetryEnabled) {
			// Telemetry is enabled
			Scheduler scheduler = getScheduler();
			Long defaultZoneId = ZoneUtil.getDefaultZoneId();
			Trigger trigger = null;
			try {
				trigger = scheduler.getTrigger(triggerKey(defaultZoneId.toString(), TelemetryProcess.TELEMETRY_PROCESS_GROUP));
			} catch (SchedulerException e) {
				logger.warn("Failed to get trigger information on the telemetry process");
			}
			if(trigger == null) {
				// Telemetry process doesn't exist in the system. Set up one.
				int repeatIntervalInSeconds = SPropsUtil.getInt("job.telemetry.process.repeat.interval", 7*24*60*60); // default is 7 days (= once a week)
				// There are situations where we don't want telemetry process to start collecting
				// and uploading data immediately after being enabled. For instance, kicking off
				// telemetry process right after new install or upgrade may not only interfere
				// with the work user is doing but also may not return meaningful data because
				// the system is empty or something. For that reason, we give it one day of
				// grace period each time telemetry process is (re)enabled.
				int initialDelayInSeconds = SPropsUtil.getInt("job.telemetry.process.initial.delay", 24*60*60); // default is 1 day
	   			if(logger.isDebugEnabled())
	   				logger.debug("Scheduling telemetry process with repeat interval of " + repeatIntervalInSeconds + " seconds and initial delay of " + initialDelayInSeconds);
				telemetryProcess.schedule(repeatIntervalInSeconds, initialDelayInSeconds);
			}
			else {
				// Telemetry process already exists in the system. No further work is necessary.
				if(logger.isDebugEnabled())
					logger.debug("Telemetry process is already present.");
			}
		}
		else {
			// Telemetry is disabled.
			// If this feature is not enabled, remove the job, if exists, from the system altogether, rather than just disabling it.
			if(logger.isDebugEnabled())
				logger.debug("Making sure that telemetry process doesn't exist");
			telemetryProcess.remove();
		}
	}
	
	private static Scheduler getScheduler() {
		return (Scheduler)SpringContextUtil.getBean("scheduler");
	}
}
