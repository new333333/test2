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
import org.kablink.teaming.util.SPropsUtil;
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
    	List<Long> tryBinderIds = new ArrayList<Long>();
    	List<Integer> binderTryCounts = (List)jobDataMap.get("binderTryCounts");
    	List<Integer> tryBinderTryCounts = new ArrayList<Integer>();
    	for(int i = 0; i < binderIds.size(); i++) {
    		Long id = binderIds.get(i);
    		//index binder only
			try {
				binderModule.indexBinder(id, false);
			} catch (NoObjectByTheIdException ex) {
				//gone, skip it
				if(logger.isDebugEnabled())
					logger.debug("The binder " + id + " is no longer found.");
			} catch (Exception ex) {
				int tryCount = 0;
				if(binderTryCounts != null && binderTryCounts.get(i) != null)
					tryCount = binderTryCounts.get(i).intValue();
				if(++tryCount < getTryMaxCount()) {
					//try again
					logger.error(NLT.get("profile.titlechange.index.error") + " (binder " + id.toString() + ") " +
							ex.toString());
					tryBinderIds.add(id);
					tryBinderTryCounts.add(Integer.valueOf(tryCount));
				}
				else {
					//tried enough. Log the problem and discard this item.
					logger.error(NLT.get("profile.titlechange.index.error") + " (binder " + id.toString() + ") - Discarding the item", ex);
				}
			}
    	}
    	
      	FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
      	List<Long> entryIds = (List)jobDataMap.get("entryIds");
    	List<Long>tryEntryIds = new ArrayList();
    	List<Integer> entryTryCounts = (List)jobDataMap.get("entryTryCounts");
    	List<Integer> tryEntryTryCounts = new ArrayList<Integer>();
    	for(int i = 0; i < entryIds.size(); i++) {
    		Long id = entryIds.get(i);
			try {
				//get entry directly, don't have parent folder
				FolderEntry entry = (FolderEntry)coreDao.load(FolderEntry.class, id);
				if (entry != null) folderModule.indexEntry(entry, false);
			} catch (NoObjectByTheIdException ex) {
				//gone, skip it
				if(logger.isDebugEnabled())
					logger.debug("The entry " + id + " is no longer found.");
			} catch (Exception ex) {
				int tryCount = 0;
				if(entryTryCounts != null && entryTryCounts.get(i) != null)
					tryCount = entryTryCounts.get(i).intValue();
				if(++tryCount < getTryMaxCount()) {
					//try again
					logger.error(NLT.get("profile.titlechange.index.error") + " (entry " + id.toString() + ") " +
							ex.toString());
					tryEntryIds.add(id);
					tryEntryTryCounts.add(Integer.valueOf(tryCount));
				}
				else {
					//tried enough. Log the problem and discard this item.
					logger.error(NLT.get("profile.titlechange.index.error") + " (entry " + id.toString() + ") - Discarding the item", ex);
				}
			}
    	}
    	if (tryBinderIds.isEmpty() && tryEntryIds.isEmpty()) {
    		context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
    		context.setResult("Success");
    	} else {
    		jobDataMap.put("binderIds", tryBinderIds);
    		if(!tryBinderTryCounts.isEmpty())
    			jobDataMap.put("binderTryCounts", tryBinderTryCounts);
    		jobDataMap.put("entryIds", tryEntryIds);
    		if(!tryEntryTryCounts.isEmpty())
    			jobDataMap.put("entryTryCounts", tryEntryTryCounts);
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

	private int getTryMaxCount() {
		return SPropsUtil.getInt("default.user.title.change.try.max.count", 3);
	}
}
