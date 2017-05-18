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
public class DefaultEmailNotification extends SSCronTriggerJob implements EmailNotification {
	 
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
		if (!getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()).getMailConfig().isSendMailEnabled()) {
			logger.debug("Sending mail is not enabled for zone " + RequestContextHolder.getRequestContext().getZoneName());
			return;
		}
		MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		try {
			Long binderId = null;
			try {
				binderId = new Long(jobDataMap.getLong("binder"));
			} catch (Exception ex) {
				binderId = zoneId;
			}
			Date end = mail.sendNotifications(binderId, (Date)jobDataMap.get("lastNotification") );
			//In v1 top level folders had their own schedules.  In v.1.X they don't
			if (!zoneId.equals(binderId)) {
				deleteJob(context);
			} else {
				jobDataMap.put("lastNotification", end);
			}
		} catch (NoBinderByTheIdException nf) {
			deleteJobOnError(context,nf);
		} 
    }


	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new JobDescription(zoneId));
	}
	public void setScheduleInfo(ScheduleInfo info) {
		setScheduleInfo(new JobDescription(info.getZoneId()), info);
	}

	public void enable(boolean enable, Long zoneId) {
		enable(enable, new JobDescription(zoneId));
 	}
	public class JobDescription extends CronJobDescription {
		JobDescription(Long zoneId) {
			super(zoneId, zoneId.toString(), NOTIFICATION_GROUP, NOTIFICATION_DESCRIPTION);
		}
     	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = super.getDefaultScheduleInfo();
    		info.getDetails().put("lastNotification", new Date());
    		info.getDetails().put("binder", zoneId);
    		return info;
    	}
       	
	}

}