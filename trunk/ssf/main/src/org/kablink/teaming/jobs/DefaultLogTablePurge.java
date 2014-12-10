/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author jong
 *
 */
public class DefaultLogTablePurge extends SSCronTriggerJob implements LogTablePurge {

	private static final Log logger = LogFactory.getLog(DefaultLogTablePurge.class);
	
	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
  		Date now = new Date();
  		
  		//See if the audit trail and change log tables need to be pruned
  		if (zoneConfig.getAuditTrailKeepDays() > 0) {
  			Date purgeBeforeDate = getCoreDao().getAuditTrailPurgeDate(zoneId);
	  		int auditTrailPurgeCount = getCoreDao().purgeAuditTrail(zoneId, purgeBeforeDate);
	  		logger.info("Purged " + auditTrailPurgeCount + " records from the SS_AuditTrail table");
	  		int loginAuditPurgeCount = getCoreDao().purgeLoginAudit(zoneId, purgeBeforeDate);
	  		logger.info("Purged " + loginAuditPurgeCount + " records from the SS_LoginAudit table");
  		}
  		
  		if (zoneConfig.getChangeLogsKeepDays() > 0) {
  			Date purgeBeforeDate = new Date(now.getTime() - ((long)zoneConfig.getChangeLogsKeepDays())*1000L*60L*60L*24L);
			int changeLogsPurgeCount = getCoreDao().purgeChangeLogs(zoneId, purgeBeforeDate);
			logger.info("Purged " + changeLogsPurgeCount + " records from the SS_ChangeLogs table");
  		}
	}

	@Override
	public void enable(boolean enable, Long zoneId) {
		enable(enable, new CronJobDescription(zoneId, zoneId.toString(),LOG_TABLE_PURGE_GROUP, LOG_TABLE_PURGE_DESCRIPTION));
	}

	@Override
	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new CronJobDescription(zoneId, zoneId.toString(),LOG_TABLE_PURGE_GROUP, LOG_TABLE_PURGE_DESCRIPTION));
	}

	@Override
	public void setScheduleInfo(ScheduleInfo schedulerInfo) {
		setScheduleInfo(new CronJobDescription(schedulerInfo.getZoneId(), schedulerInfo.getZoneId().toString(),LOG_TABLE_PURGE_GROUP, LOG_TABLE_PURGE_DESCRIPTION), schedulerInfo);
	}

	private CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
}
