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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import com.sitescape.team.InternalException;
import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.extension.ExtensionCallback;
import com.sitescape.team.extension.ZoneClassManager;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.module.workflow.jbpm.CalloutHelper;
import com.sitescape.team.module.workflow.support.WorkflowAction;
import com.sitescape.team.module.workflow.support.WorkflowCallout;
import com.sitescape.team.module.workflow.support.WorkflowScheduledAction;
import com.sitescape.team.module.workflow.support.WorkflowStatus;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SpringContextUtil;

/**
 *
 * @author Janet McCann
 */
public class DefaultWorkflowProcess extends SimpleTriggerJob implements WorkflowProcess {
	 
	protected ZoneClassManager getZoneClassManager() {
		return (ZoneClassManager)SpringContextUtil.getBean("zoneClassManager");
	};

	public void doExecute(final JobExecutionContext context) throws JobExecutionException {	
		final Long entryId = new Long(jobDataMap.getLong("entityId"));
		final FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
		FolderEntry entry = null;
    	try {
    		entry = folderModule.getEntry(null, entryId);
    		if (entry.isDeleted()) {
    			removeJob(context);
    			return;
    		}
       	} catch (NoObjectByTheIdException no) {
    		removeJob(context);
    		return;
      	} catch (AccessControlException acc) {
    		removeJobOnError(context, acc);
    		return;
    	}

       	final Long stateId = new Long(jobDataMap.getLong("stateId"));
       	WorkflowState state = entry.getWorkflowState(stateId);
       	//workflow done, remove job
       	if (state == null) {
       		removeJob(context);
       		return;
       	}
       	//remove from cache in case execution takes long
       	coreDao.evict(entry);
       	String actionName = jobDataMap.getString("class");
		try {
			getZoneClassManager().execute(new ExtensionCallback() {
				public Object execute(Object action) {
		       		WorkflowScheduledAction job = (WorkflowScheduledAction)action;
		       		WorkflowStatus status = (WorkflowStatus)jobDataMap.get("workflowStatus");
		       		if (job.execute(entryId, stateId, status)) {
		       			//reload incase execute took a long time
		       			FolderEntry entry = folderModule.getEntry(null, entryId);
		       			WorkflowState state = entry.getWorkflowState(stateId);
		       			removeJob(context);
		       			if (state != null && job instanceof WorkflowCallout) {
		       				//	could be a naming issue for variables if multiple remote apps run simultaneously for the same entry
		       				WorkflowModule wf = (WorkflowModule)SpringContextUtil.getBean("workflowModule");
		       				wf.setWorkflowVariables(entry, state, ((WorkflowCallout)job).getVariables());
		       				wf.modifyWorkflowStateOnChange(entry);
		       			}
		       		} else {
		       			//update jobdata
		       			jobDataMap.put("workflowStatus", status);
		       			SimpleTrigger trigger = (SimpleTrigger)context.getTrigger();
		       			if (status.getRetrySeconds()*1000 != trigger.getRepeatInterval()) {
		       				//	change time
		       				trigger.setRepeatInterval(status.getRetrySeconds()*1000);
		       				try {
		       					context.getScheduler().rescheduleJob(context.getJobDetail().getName(), context.getJobDetail().getGroup(), trigger);
		       				} catch (SchedulerException se) {			
		       					throw new ConfigurationException(se.getLocalizedMessage());			
		       				}
		       			}
		       		}
		       		return null;

				};
			}, actionName);
 
		} catch (ClassNotFoundException e) {
			logger.error("Invalid Workflow Action class name '" + actionName + "'");
			throw new ConfigurationException("Invalid Workflow Action class name '" + actionName + "'",
					e);
       	} catch (InternalException e) {
   			removeJob(context);			
      		throw new ConfigurationException("Cannot instantiate Workflowprocess of type '" 	+ actionName + "'");
 		} catch (NoObjectByTheIdException no) {
   			removeJob(context);			
		}
    }
	public void remove(WorkflowSupport entry, WorkflowState wfState) {
		String groupName = getGroupName(entry, wfState);
		Scheduler scheduler = getScheduler();		
		try {
			String[] jobNames = scheduler.getJobNames(groupName);
			for (int i=0; i<jobNames.length; ++i) {
				scheduler.unscheduleJob(jobNames[i], groupName);
			}
		} catch (SchedulerException se) {			
			logger.error(se.getLocalizedMessage()==null?se.getMessage():se.getLocalizedMessage());
		}
	}
	private String getGroupName(WorkflowSupport entry, WorkflowState wfState) {
		return WORKFLOW_PROCESS_GROUP + ":" + ((FolderEntry)entry).getId().toString() + ":" + wfState.getId().toString();
	}
    public void schedule(WorkflowSupport entry, WorkflowState wfState, String clazz, Map args, int seconds) {
		String groupName = getGroupName(entry, wfState);
		try {
			//since scheduling is not handled by the current transaction,
			//old jobs could be registered that were not rolledback
			//If I integrate the transaction into quartz I get deadlocks
			//so this will have to do.
			removeJob(clazz, groupName);
		} catch (Exception noexist) {};
		JobDataMap data = new JobDataMap();
		data.put(ZONEID,wfState.getZoneId());
		data.put(USERID,RequestContextHolder.getRequestContext().getUserId());
		data.put("entityId", wfState.getOwner().getEntity().getEntityIdentifier().getEntityId());
		data.put("entityType", wfState.getOwner().getEntity().getEntityIdentifier().getEntityType());
		data.put("stateId", wfState.getId());
		data.put("class", clazz);
		WorkflowStatus status = new WorkflowStatus(args, seconds);
		data.put("workflowStatus", status);
		
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.MINUTE, 1);
		GregorianCalendar end = new GregorianCalendar(); //try for 1 week
		end.add(Calendar.DATE, 7);
			
		schedule(new JobDescription(wfState.getZoneId(), clazz, groupName, start.getTime(), end.getTime(), seconds, data));
    }

	public class JobDescription extends SimpleJobDescription {
		Date startDate,endDate;
		JobDataMap data;
		JobDescription(Long zoneId, String jobName, String jobGroup, Date startDate, Date endDate, int repeatSeconds, JobDataMap data) {
			super(zoneId, jobName, jobGroup, WORKFLOW_PROCESS_DESCRIPTION, repeatSeconds);
			this.data = data;
			this.startDate = startDate;
			this.endDate = endDate;
		}
		protected Date getStartDate() {
			return startDate;
		}
		protected Date getEndDate() {
			return endDate;
		}

		protected JobDataMap getData() {
			return data;
		}
		protected int getMisfireInstruction() {
			return SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
		}

	}    
}
