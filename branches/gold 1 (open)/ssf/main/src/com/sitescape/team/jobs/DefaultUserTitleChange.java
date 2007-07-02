/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */

package com.sitescape.team.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;

/**
 *
 * @author Janet McCann
 */
public class DefaultUserTitleChange extends SSStatefulJob implements UserTitleChange {
	 
 
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

    public void schedule(User user, List binderIds, List entryIds) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		String userIdString = user.getId().toString() + user.getModification().getDate().toString();
		try {
			//each job is new
			JobDetail jobDetail = new JobDetail(userIdString, USER_TITLE_GROUP, 
						Class.forName(this.getClass().getName()),false, false, false);
			jobDetail.setDescription(USER_TITLE_DESCRIPTION);
			JobDataMap data = new JobDataMap();
			data.put(ZONEID,user.getZoneId());
			data.put("binderIds", binderIds);
			data.put("entryIds", entryIds);
			
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(getDefaultCleanupListener());
			scheduler.addJob(jobDetail, true);
			//wait 3 minutes so user title is committed.  Otherwise we end up with
			// the previously commited title and the index is wrong
			GregorianCalendar start = new GregorianCalendar();
			start.add(Calendar.MINUTE, 3);
		
			//repeats every 5 minutes
			SimpleTrigger trigger = new SimpleTrigger(userIdString, USER_TITLE_GROUP, userIdString, USER_TITLE_GROUP, start.getTime(), null, 
						SimpleTrigger.REPEAT_INDEFINITELY, 5*60*1000);
			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
			trigger.setDescription(USER_TITLE_DESCRIPTION);
			trigger.setVolatility(false);
			scheduler.scheduleJob(trigger);				
		} catch (SchedulerException se) {			
			throw new ConfigurationException(se.getLocalizedMessage());			
  		} catch (ClassNotFoundException cf) {
			throw new ConfigurationException(cf.getLocalizedMessage());			
  		}
    }


}
