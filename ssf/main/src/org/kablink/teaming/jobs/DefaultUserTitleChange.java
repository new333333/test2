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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 *
 * @author Janet McCann
 */
public class DefaultUserTitleChange extends SimpleTriggerJob implements UserTitleChange {
	 
	protected static int MAX_IDS=1000;
	public void doExecute(JobExecutionContext context) throws JobExecutionException {	
	   	CoreDao coreDao = (CoreDao)SpringContextUtil.getBean("coreDao");
	    BinderModule binderModule = (BinderModule)SpringContextUtil.getBean("binderModule");
    	List<Long> binderIds = (List)jobDataMap.get("binderIds");
    	List<Long>retryBinderIds = new ArrayList();
    	for (Long id:binderIds) {
    		//index binder only
			try {
				binderModule.indexBinder(id, false);
			} catch (NoObjectByTheIdException ex) {
				//gone, skip it
			} catch (Exception ex) {
				//try again
				logger.error(NLT.get("profile.titlechange.index.error") + " (binder " + id.toString() + ") " +
						ex.getLocalizedMessage());
				retryBinderIds.add(id);
			}
    	}
    	
      	FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
      	List<Long> entryIds = (List)jobDataMap.get("entryIds");
    	List<Long>retryEntryIds = new ArrayList();
    	for (Long id:entryIds) {
			try {
				//get entry directly, don't have parent folder
				FolderEntry entry = (FolderEntry)coreDao.load(FolderEntry.class, id);
				if (entry != null) folderModule.indexEntry(entry, false);
			} catch (NoObjectByTheIdException ex) {
				//gone, skip it
			} catch (Exception ex) {
				logger.error(NLT.get("profile.titlechange.index.error") + " (entry " + id.toString() + ") " +
						ex.getLocalizedMessage());
				//try again
				retryEntryIds.add(id);
			}
    	}
    	if (retryBinderIds.isEmpty() && retryEntryIds.isEmpty()) {
    		context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
    		context.setResult("Success");
    	} else {
    		jobDataMap.put("binderIds", retryBinderIds);
    		jobDataMap.put("entryIds", retryEntryIds);
    		//will be rescheduled
    		context.setResult("Failed");
    	}

	}

    public void schedule(User user, List<Long> binderIds, List<Long> entryIds) {
		//the number of changes could be large, and some databases won't accept it (mssql packet size 1M)
		int count = 0;
		int binderIndex=0;
		int entryIndex=0;
		while (binderIndex < binderIds.size() || entryIndex < entryIds.size()) {
			String userIdString = user.getId().toString() + " " + user.getModification().getDate().toString() + " " + String.valueOf(count);
			//each job is new
			JobDataMap data = new JobDataMap();
			data.put(ZONEID,user.getZoneId());
			if (binderIndex < binderIds.size()) {
				data.put("entryIds", Collections.EMPTY_LIST);
				data.put("binderIds", new ArrayList(binderIds.subList(binderIndex, Math.min(binderIndex+MAX_IDS, binderIds.size()))));
				binderIndex+= MAX_IDS;
					
			} else {
				data.put("binderIds", Collections.EMPTY_LIST);
				data.put("entryIds", new ArrayList(entryIds.subList(entryIndex, Math.min(entryIndex+MAX_IDS, entryIds.size()))));
				entryIndex+= MAX_IDS;					
			}
			//wait 3 minutes so user title is committed.  Otherwise we end up with
			// the previously commited title and the index is wrong
			GregorianCalendar start = new GregorianCalendar();
			start.add(Calendar.MINUTE, 3+count);
			schedule(new JobDescription(user.getZoneId(), userIdString, start.getTime(), data));
		
			++count;
		}
    }
	public class JobDescription extends SimpleJobDescription {
		Date startDate;
		JobDataMap data;
		JobDescription(Long zoneId, String jobName, Date startDate, JobDataMap data) {
			super(zoneId, jobName, USER_TITLE_GROUP, USER_TITLE_DESCRIPTION, 5*60);
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

}
