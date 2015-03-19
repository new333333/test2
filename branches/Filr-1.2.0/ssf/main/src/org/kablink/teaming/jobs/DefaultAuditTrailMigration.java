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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.AuditTrailMigrationUtil;
import org.kablink.teaming.util.AuditTrailMigrationUtil.MigrationStatus;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Jong
 *
 */
public class DefaultAuditTrailMigration extends SimpleTriggerJob implements AuditTrailMigration {
	
	private static final Log logger = LogFactory.getLog(DefaultAuditTrailMigration.class);

	private static Long GLOBAL_ZONE_ID = 0L; // Fake zone id used to signal global scope

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			AuditTrailMigrationUtil.migrateAll();
			// If still here, it means that the above operation completed successfully without error.
			// Mark migration as completed.
			AuditTrailMigrationUtil.setMigrationStatus(MigrationStatus.allCompleted);
			// We can now self-destroy (i.e., delete) this job.
			logger.info("Self-destroying audit trail migration job as it is no longer needed");
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
			context.setResult("Success");
		}
		catch(Exception e) {
			logger.error("Failed to migrate all remaining audit trail records across all zones - It will be retried in the next cycle", e);
    		context.setResult("Failed"); // Will be rescheduled according to the repeat interval
		}		
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.AuditTrailMigration#schedule(int)
	 */
	@Override
	public void schedule(int repeatIntervalInSeconds, int delayInSeconds) {
		// Since this job is submitted during server start, give it 30 seconds delay so that the job won't kick in
		// until the server is fully up and running (not because early kick-in will break anything, but because it
		// can further slow down server startup).
		// Note that the repeatIntervalInSeconds value is used only if a job execution fails or gets interrupted and
		// another one needs to be rescheduled. If a job execution runs to completion, it self-destroys and never
		// repeats itself.
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.SECOND, delayInSeconds);
		schedule(new JobDescription(start.getTime(), repeatIntervalInSeconds));
	}

	public class JobDescription extends SimpleJobDescription {
		Date startDate;
		JobDescription(Date startDate, int repeatIntervalInSeconds) {
			// Unlike all other jobs in the system, this job is unique in that it doesn't operate in the context of a specific zone.
			// Instead, this job is global and cuts across ALL zones in the system. In other word, it is a singleton job.
			// Because our job infrastructure requires a zone ID, we will use a fake zone id (ZERO) signaling global scope so that 
			// we can reuse the existing infrastructure without modification.
			super(GLOBAL_ZONE_ID, GLOBAL_ZONE_ID.toString(), AUDIT_TRAIL_MIGRATIION_GROUP, AUDIT_TRAIL_MIGRATIION_DESCRIPTION, repeatIntervalInSeconds);
			this.startDate = startDate;
		}
		@Override
		protected Date getStartDate() {
			return startDate;
		}
	}
}
