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

import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;


/**
 * @author Janet McCann
 * Do common setup for all jobs.
 * Return result objects to be handled by CleanupJobListener:
 * delete a job on unrecoverable error
 * delete a job that is complete
 * 
 */
public abstract class SSStatefulJob implements StatefulJob {
	protected Log logger = LogFactory.getLog(getClass());
	protected JobDataMap jobDataMap;
	protected FileModule fileModule;
	protected ProfileDao profileDao;
	protected CoreDao coreDao;
	protected User user;
	protected Long zoneId;
	public static int DESCRIPTION_MAX = 120; 
	public static String ZONEID="zoneId";
	public static String USERID="userId";
		
	public String trimDescription(String description) {
		return description.substring(0, Math.min(description.length(), DESCRIPTION_MAX));
	}
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		setupSession();
    	fileModule = (FileModule)SpringContextUtil.getBean("fileModule");
    	profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
    	coreDao = (CoreDao)SpringContextUtil.getBean("coreDao");
    	jobDataMap = context.getJobDetail().getJobDataMap();
		context.setResult("Success");
		try {  
	           	//zone required
           	if (!jobDataMap.containsKey(ZONEID)) {			
           		removeJobOnError(context, new SchedulerException(context.getJobDetail().getFullName() + " : zoneId missing from jobData"));
           	}
           	zoneId = jobDataMap.getLong(ZONEID);
           	//Validate user and zone are compatible
           	try {
           		if (jobDataMap.containsKey(USERID)) {
           			Long id = new Long(jobDataMap.getLong(USERID));
           			user = profileDao.loadUser(id, zoneId);
           		} else {
           			user = profileDao.getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, zoneId);
           		}
           	} catch (Exception ex) {
           		//see if zone is deleted and remove job gracefully
           		try {
           			Workspace zone = (Workspace)coreDao.loadBinder(zoneId, zoneId);
           			if (zone.isDeleted()) {
           	   			removeJob(context);
          				return;
           			}
           		} catch (NoBinderByTheIdException nb) {
       	   			removeJob(context);   
       	   			return;
           		} catch (NoFolderByTheIdException nb) {
       	   			removeJob(context);   
       	   			return;
           		}
           		throw ex;  //zone exists, other error
           	}
           	if (user.getParentBinder().getRoot().isDeleted()) {
  	   			removeJob(context); 
           	} else {
           		//	Setup thread context expected by business logic
           		RequestContextUtil.setThreadContext(user).resolve();
            	//	do the real work
           		doExecute(context);
           	}
		} catch (NoUserByTheIdException nu) {
			removeJobOnError(context, nu);
		} catch (NoUserByTheNameException nn) {
			removeJobOnError(context, nn);
		} catch (JobExecutionException je) {
			context.setResult("Failed");
			//re-throw
			throw je;
		} catch (ConfigurationException cf) {
			context.setResult("Failed");
			throw new JobExecutionException(cf);
		} catch (Exception e){
			context.setResult("Failed");
    		throw new JobExecutionException(e);
    	} finally {
    		SessionUtil.sessionStop();
    		RequestContextHolder.clear();
    	}

	}  
	protected void removeJob(JobExecutionContext context) {
		context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
		context.setResult("Success");
		return;
	}
	protected void setupSession() {
		SessionUtil.sessionStartup();		
	}
	protected Scheduler getScheduler() {		
		return (Scheduler)SpringContextUtil.getBean("scheduler");		
	}
	/**
	 * Job failed due to missing domain objects.  Return exception that will remove the
	 * job triggers and the job if durablility=false;
	 * @param context
	 * @param e
	 * @throws JobExecutionException
	 */
	protected void removeJobOnError(JobExecutionContext context, Exception e) throws JobExecutionException {
		context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
		context.setResult("Failed");
		throw new JobExecutionException(e);
	}
	/**
	 * Job failed due to missing domain objects.  Return exception that will unschedule the
	 * job triggers.  JobDetails will remain. 
	 * @param context
	 * @param e
	 * @throws JobExecutionException
	 */
	protected void unscheduleJobOnError(JobExecutionContext context, Exception e) throws JobExecutionException {
		context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.UnscheduleJobOnError);
		context.setResult("Failed");
		throw new JobExecutionException(e);
	}
	
	protected void removeJob(String jobName, String jobGroup) {
		Scheduler scheduler = getScheduler();		
		try {
			scheduler.unscheduleJob(jobName, jobGroup);
		} catch (SchedulerException se) {			
			logger.error(se.getLocalizedMessage()==null?se.getMessage():se.getLocalizedMessage());
		}
		
	}
	protected abstract void doExecute(JobExecutionContext context) throws JobExecutionException;

	protected String getDefaultCleanupListener() {
		return org.kablink.teaming.jobs.CleanupJobListener.name;
	}
	protected TimeZone getDefaultTimeZone() {
		return TimeZoneHelper.getDefault();
	}



}
