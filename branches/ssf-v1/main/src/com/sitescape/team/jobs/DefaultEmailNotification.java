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

import java.util.Date;
import java.util.TimeZone;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.util.SpringContextUtil;
/**
 *
 */
public class DefaultEmailNotification extends SSStatefulJob implements EmailNotification {
	 
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		try {
			Date end = mail.sendNotifications(new Long(jobDataMap.getLong("binder")), (Date)jobDataMap.get("lastNotification") );
			jobDataMap.put("lastNotification", end);
		} catch (NoBinderByTheIdException nf) {
			removeJobOnError(context,nf);
		} 
    }


	public ScheduleInfo getScheduleInfo(Binder binder) {
		return getScheduleInfo(new MailJobDescription(binder));
	}
	public void setScheduleInfo(ScheduleInfo info, Binder binder) {
		info.getDetails().put("binder", binder.getId());
		setScheduleInfo(new MailJobDescription(binder), info);
	}

	public void enable(boolean enable, Binder binder) {
		enable(enable, new MailJobDescription(binder));
 	}
	public class MailJobDescription implements JobDescription {
		private Binder binder;
		private Long zoneId;
		public MailJobDescription(Binder binder) {
			this.binder = binder;
			this.zoneId = binder.getZoneId();
		}
    	public  String getDescription() {
    		return SSStatefulJob.trimDescription(binder.getTitle());
    	}
    	public Long getZoneId() {
    		return zoneId;
    	}
    	public String getName() {
    		return binder.getId().toString();
    	}
    	public String getGroup() {
    		return EmailNotification.NOTIFICATION_GROUP;
    	}		
       	public TimeZone getTimeZone() {
    		return getDefaultTimeZone();
     	}
       	public String getCleanupListener() {
    		return getDefaultCleanupListener();
    	}
    	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = new ScheduleInfo(zoneId);
    		info.getDetails().put("binder", binder.getId());
    		info.getDetails().put("lastNotification", new Date());
    		return info;
    	}
       	
	}

}