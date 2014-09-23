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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author jong
 *
 */
public class DefaultNetFolderContentIndexing extends SimpleTriggerJob implements NetFolderContentIndexing {


	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
    	FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");

		Long binderId = null;
		try {
			binderId = new Long(jobDataMap.getLong("folderId"));
		} catch (Exception ex) {
			binderId = new Long(-1L);
		}
		if(binderId.longValue() == -1L) {
			logger.warn("Deleting job because binder id is not specified: " + context.toString());
			deleteJob(context);
		}
		else {
			try {
				Folder netFolderRoot = loadFolder(binderId);
				if(netFolderRoot.getComputedIndexContent()) {
					folderModule.indexFileContentForNetFolder(netFolderRoot);
				}
				else {
					// Content indexing is disabled for this folder at this time. Probably admin has changed that 
					// setting. Simply unschedule the job without physically deleting it. It will be re-scheduled
					// next time the admin enables content indexing for this net folder.
					logger.info("Unscheduling job because content indexing is currently disabled for net folder [" + netFolderRoot.getPathName() + "]");
					unscheduleJob(context);
				}
			} catch (NoFolderByTheIdException nf) {
				// Apparently the folder on which this scheduler is defined has been removed.
				// This is not an error. So simply remove the job.
				logger.info("Deleting job because folder by id " + binderId + " is not found: " + context.toString());
				deleteJob(context);
			} 
		}
	}

	@Override
	public void schedule(Long folderId, int intervalInMinutes) {
		JobDataMap data = new JobDataMap();
		data.put(ZONEID, RequestContextHolder.getRequestContext().getZoneId());
		data.put("folderId", folderId);
		
		// Do not start the job right away. Instead skip one full cycle to make sure that
		// the system is fully ready by the time this job starts executing.
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.MINUTE, intervalInMinutes);
		schedule(new JobDescription(folderId, start.getTime(), intervalInMinutes, data));
	}

	@Override
	public void unschedule(Long folderId) {
		// Actually, this has the same effect as calling deleteJob() method because this job is
		// not durable hence is automatically deleted when its (only) trigger is deleted.
		unscheduleJob(folderId.toString(), NET_FOLDER_CONTENT_INDEXING_GROUP);
	}

	@Override
	public void deleteJob(Long folderId) {
		// Even if this command failed right here, it would be OK, because, when the job runs
		// next time, it would recognize that the net folder root this job is associated
		// with is gone, and it will self-destruct itself.
		deleteJob(folderId.toString(), NET_FOLDER_CONTENT_INDEXING_GROUP);
	}

	public class JobDescription extends SimpleJobDescription {
		Date startDate;
		JobDataMap data;
		JobDescription(Long folderId, Date startDate, int intervalInMinutes, JobDataMap data) {
			super(RequestContextHolder.getRequestContext().getZoneId(), 
					folderId.toString(), 
					NET_FOLDER_CONTENT_INDEXING_GROUP, 
					NET_FOLDER_CONTENT_INDEXING_DESCRIPTION + folderId, 
					intervalInMinutes*60,
					false,
					SPropsUtil.getInt("job.net.folder.content.indexing.priority", 4));
			this.startDate = startDate;
			this.data = data;
		}
		protected Date getStartDate() {
			return startDate;
		}
		protected JobDataMap getData() {
			return data;
		}
	}

	private Folder loadFolder(Long folderId) throws NoBinderByTheIdException {
        Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneId());
		if (folder.isDeleted()) throw new NoBinderByTheIdException(folderId);
		if (folder.isPreDeleted()) throw new NoBinderByTheIdException(folderId);
		return folder;
	}

	private FolderDao getFolderDao() {
		return (FolderDao)SpringContextUtil.getBean("folderDao");
	}

	private FolderModule getFolderModule() {
		return (FolderModule)SpringContextUtil.getBean("folderModule");
	}
}
