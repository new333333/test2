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

import java.util.Date;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 */
public class DefaultFolderNotification extends SSCronTriggerJob implements FolderNotification {
	 
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
		if (!getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()).getMailConfig().isSendMailEnabled()) {
			logger.debug("Sending mail is not enabled for zone " + RequestContextHolder.getRequestContext().getZoneName());
			return;
		}
		MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		try {
			Long folderId = null;
			try {
				folderId = new Long(jobDataMap.getLong("binder"));
			} catch (Exception ex) {
				logger.debug("Binder not known for folder notifications");
				return;
			}
			Date end = mail.sendNotifications(folderId, (Date)jobDataMap.get("lastNotification") );
			jobDataMap.put("lastNotification", end);
		} catch (NoBinderByTheIdException nf) {
			deleteJobOnError(context,nf);
		} 
    }


	public ScheduleInfo getScheduleInfo(Long zoneId, Long folderId) {
		return getScheduleInfo(new NotificationJobDescription(zoneId, folderId));
	}
	public void setScheduleInfo(ScheduleInfo info, Long folderId) {
		info.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		info.getDetails().put(ZONEID,RequestContextHolder.getRequestContext().getZoneId());
		info.getDetails().put("binder", folderId);
		
		setScheduleInfo(new NotificationJobDescription(info.getZoneId(), folderId), info);
	}

	public void enable(boolean enable, Long zoneId, Long folderId) {
		enable(enable, new NotificationJobDescription(zoneId, folderId));
 	}
	public class NotificationJobDescription extends CronJobDescription {
		private Long folderId;
		public NotificationJobDescription(Long zoneId, Long folderId) {
			super(zoneId, folderId.toString(), NOTIFICATION_GROUP, NOTIFICATION_DESCRIPTION + folderId, false);
			this.folderId = folderId;
		}
		public Long getFolderId() {
    		return folderId;
    	}
    	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = super.getDefaultScheduleInfo();
    		info.getDetails().put("lastNotification", new Date());
    		info.getDetails().put("binder", folderId);
    		info.getDetails().put(USERID,RequestContextHolder.getRequestContext().getUserId());
    		info.getDetails().put(ZONEID, zoneId);
    		return info;
    	}
	}
}