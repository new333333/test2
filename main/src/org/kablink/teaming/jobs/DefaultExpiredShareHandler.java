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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Default job that processes expired shares.
 * 
 * @author jong
 */
public class DefaultExpiredShareHandler extends SimpleTriggerJob implements ExpiredShareHandler {
	private static final Log logger = LogFactory.getLog(DefaultExpiredShareHandler.class);
	
	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
		if(logger.isDebugEnabled())
			logger.debug("doExecute() invoked on " +  getClass().getSimpleName());
		
		List<ShareItem> expiredSharesToHandle = getProfileDao().findExpiredAndNotYetHandledShareItems(RequestContextHolder.getRequestContext().getZoneId());
		
		SharingModule sharingModule = getSharingModule();
		sharingModule.validateShareItems(expiredSharesToHandle);	// Drops any shares that may have expired.
		
		for(ShareItem shareItem:expiredSharesToHandle) {
			try {
				logger.info("Handling expired share item '" + shareItem.getId() + "'");
				sharingModule.handleExpiredShareItem(shareItem);
			}
			catch(Exception e) {
				logger.warn("Failed to handle expired share item '" + shareItem.getId() + "'", e);
				continue;
			}
		}
	}

	@Override
	public void schedule(Long zoneId, int intervalInMinutes) {
		// Give it a minute delay in case this is first time scheduling this job
		// and that the system hasn't fully started yet.
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.SECOND, SPropsUtil.getInt("job.expired.share.handler.initial.delay.seconds", 60));
		schedule(new JobDescription(zoneId, start.getTime(), intervalInMinutes));
	}

	public class JobDescription extends SimpleJobDescription {
		Date startDate;
		JobDescription(Long zoneId, Date startDate, int intervalInMinutes) {
			super(zoneId, zoneId.toString(), EXPIRED_SHARE_HANDLER_GROUP, EXPIRED_SHARE_HANDLER_DESCRIPTION, intervalInMinutes*60);
			this.startDate = startDate;
		}
		@Override
		protected Date getStartDate() {
			return startDate;
		}
	}

	@Override
	public void remove(Long zoneId) {
		unscheduleJob(zoneId.toString(), EXPIRED_SHARE_HANDLER_GROUP);
	}

	protected ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}
	
	protected SharingModule getSharingModule() {
		return (SharingModule) SpringContextUtil.getBean("sharingModule");
	}
}
