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
package org.kablink.teaming.jobs;

import java.util.Date;
import java.util.TimeZone;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 */
public class DefaultMirroredFolderSynchronization extends SSCronTriggerJob 
	implements MirroredFolderSynchronization {
	 
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
		try {
			Long binderId = null;
			try {
				binderId = new Long(jobDataMap.getLong("binder"));
			} catch (Exception ex) {
				binderId = new Long(-1);
			}
			if (binderId.equals(-1)) {
				removeJob(context);
			} else {
				folderModule.synchronize(binderId,null);
			}
		} catch (NoBinderByTheIdException nf) {
			// Apparently the folder on which this scheduler is defined has been removed.
			// This is not an error. So simply remove the job.
			removeJob(context);
		} 
    }

	public ScheduleInfo getScheduleInfo(Long zoneId, Long folderId) {
		return getScheduleInfo(new SyncJobDescription(zoneId, folderId));
	}
	public void setScheduleInfo(ScheduleInfo info, Long folderId) {
		info.getDetails().put(USERID,RequestContextHolder.getRequestContext().getUserId());
		info.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		info.getDetails().put(ZONEID,RequestContextHolder.getRequestContext().getZoneId());
		info.getDetails().put("binder", folderId);
		
		setScheduleInfo(new SyncJobDescription(info.getZoneId(),folderId), info);
	}

	public void enable(boolean enable, Long folderId) {
		enable(enable, new SyncJobDescription(zoneId, folderId));
 	}
	public class SyncJobDescription extends CronJobDescription {
		private Long folderId;
		public SyncJobDescription(Long zoneId, Long folderId) {
			super(zoneId, folderId.toString(), SYNCHRONIZATION_GROUP, SYNCHRONIZATION_DESCRIPTION + folderId);
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