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
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.util.SPropsUtil;
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
				binderId = new Long(-1L);
			}
			if(binderId.longValue() == -1L) {
				deleteJob(context);
			} else {
				folderModule.enqueueFullSynchronize(binderId);
			}
		} catch (NoFolderByTheIdException nf) {
			// Apparently the folder on which this scheduler is defined has been removed.
			// This is not an error. So simply remove the job.
			deleteJob(context);
		} 
    }

	public ScheduleInfo getScheduleInfo(Long folderId) {
		return getScheduleInfo(new SyncJobDescription(RequestContextHolder.getRequestContext().getZoneId(), folderId));
	}
	public void setScheduleInfo(ScheduleInfo info, Long folderId) {
		info.getDetails().put(USERID,RequestContextHolder.getRequestContext().getUserId());
		info.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		info.getDetails().put(ZONEID,RequestContextHolder.getRequestContext().getZoneId());
		info.getDetails().put("binder", folderId);
		
		setScheduleInfo(new SyncJobDescription(info.getZoneId(),folderId), info);
	}

	@Override
	public void deleteJob(Long folderId) {
		// Try deleting the job directly
		if(super.deleteJob(folderId.toString(), SYNCHRONIZATION_GROUP))
			return; // no error
		
		// For whatever reason, could not delete the job (probably because the job is currently executing or something)
		ScheduleInfo si = this.getScheduleInfo(folderId);
		// Now, here's a backup strategy - Enable the job if currently disabled. This way, the job will
		// run, realize that the binder is gone, and self-clean itself by removing the job.
		if (si != null && !si.isEnabled()) {
			si.setEnabled(true);
			this.setScheduleInfo(si, folderId);
		}
 	}
	
	public class SyncJobDescription extends CronJobDescription {
		private Long folderId;
		public SyncJobDescription(Long zoneId, Long folderId) {
			super(zoneId, folderId.toString(), SYNCHRONIZATION_GROUP, SYNCHRONIZATION_DESCRIPTION + folderId, true, 
					SPropsUtil.getInt("job.mirrored.folder.synchronization.priority", 4));
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