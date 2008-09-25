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

import java.util.TimeZone;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.util.SpringContextUtil;

public class DefaultEmailPosting extends SSStatefulJob implements EmailPosting {
	public void doExecute(final JobExecutionContext context) throws JobExecutionException {
		if (!coreDao.loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()).getMailConfig().isPostingEnabled()) {
			logger.debug("Posting is not enabled for zone " + RequestContextHolder.getRequestContext().getZoneName());
			return;
		}
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		mail.receivePostings();
    }
	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new MailJobDescription(zoneId));
	}
	public void setScheduleInfo(ScheduleInfo info) {
		setScheduleInfo(new MailJobDescription(info.getZoneId()), info);

	}

	public void enable(boolean enable, Long zoneId) {
		enable(enable, new MailJobDescription(zoneId));
 	}

	public class MailJobDescription implements JobDescription {
		private Long zoneId;
		public MailJobDescription(Long zoneId) {
			this.zoneId = zoneId;
		}
    	public  String getDescription() {
    		return SSStatefulJob.trimDescription(zoneId.toString());
    	}
    	public Long getZoneId() {
    		return zoneId;
    	}
    	public String getName() {
    		return zoneId.toString();
    	}
    	public String getGroup() {
    		return POSTING_GROUP;
    	}		
    	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = new ScheduleInfo(zoneId);
    		return info;
    	}
      	public TimeZone getTimeZone() {
    		return getDefaultTimeZone();
   	}
    	public String getCleanupListener() {
    		return getDefaultCleanupListener();
    	}
   	}

}
