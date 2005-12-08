/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.module.admin.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Notification;
import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;

import com.sitescape.ef.jobs.FolderEmailNotification;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.admin.AdminModule;

import org.quartz.Scheduler;

import com.sitescape.util.Validator;

/**
 * @author Janet McCann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AdminModuleImpl implements AdminModule {
		
	private Scheduler scheduler; 
	private CoreDao coreDao;
	private ProcessorManager processorManager;
	
    /**
     * This method is used only by the IoC container. 
     * @param commonService
     */
    public void setCoreDao(CoreDao coreDao) {
        this.coreDao = coreDao;

    }
    public void setScheduler(Scheduler scheduler) {
    	this.scheduler = scheduler;
    }
    public void setProcessorManager(ProcessorManager processorManager) {
        this.processorManager = processorManager;
    }
    /**
     * Disable email notification for this forum.
     * @param forumId
     */
    public void disableNotification(Long forumId) {
    	setDisabledNotification(forumId, true);
    }
    /**
     * Enable email notification for this forum.
     * @param forumId
     */
    public void enableNotification(Long forumId) {
       	setDisabledNotification(forumId, false);
    }
    /**
     * Do actual work to either enable or disable email notification for this forum
     * @param forumId
     * @param value
     */
    protected void setDisabledNotification(Long forumId, boolean value) {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
        Binder forum = coreDao.loadBinder(forumId, companyId); 
    	NotificationDef current = forum.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		current.setLastNotification(new Date());
    		forum.setNotificationDef(current);
    	}
    	current.setDisabled(value);
     	//Remove or add from scheduler
   		checkNotificationSchedule(forum);
    }
    /**
     * Set the notification schedule for a forum.  
     * @param forumId
     * @param definition - Use NotifcationDef to set teamOn,summaryLines,contextLevel,
     * emailAddress and schedule.  Distribution list is built 
     * by this method based on the Set of userIds passed in.
     * @param users - Set of Long userIds; Used to build the distribution list
     * @throws NoPrincipalByTheIdException
     */
    public void setNotification(Long forumId, NotificationDef definition, Set users) 
    {
        Principal p;
		Set notifyUsers = new HashSet();
		Notification n;
		
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
        Binder forum = coreDao.loadBinder(forumId, companyId); 
     	NotificationDef current = forum.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		forum.setNotificationDef(current);
    		current.setDisabled(definition.isDisabled());
    		current.setLastNotification(new Date());    		
    	}
    	
   		current.setTeamOn(definition.isTeamOn());
   		current.setSummaryLines(definition.getSummaryLines());
   		current.setContextLevel(definition.getContextLevel());
   		current.setEmailAddress(definition.getEmailAddress());
 		//	Pre-load for performance
   		coreDao.loadPrincipals(users);
   		for (Iterator iter=users.iterator(); iter.hasNext();) {
   			//	make sure user exists and is in this zone
   			p = coreDao.loadPrincipal((Long)iter.next(),companyId);
   			notifyUsers.add(new Notification(forum, p));   			
   		}

   		current.setDistribution(notifyUsers);

   		String nSched = definition.getSchedule();
   		if (!Validator.isNull(nSched)) {
   	   		current.setSchedule(nSched);
   		}

		checkNotificationSchedule(forum);
    }
    /**
     * Make sure the scheduler is in sync with forum notification definition.
     * @param forum
     */

    public void checkNotificationSchedule(Binder forum)  {
   		FolderEmailNotification process = (FolderEmailNotification)processorManager.getProcessor(forum, FolderEmailNotification.PROCESSOR_KEY);
   		process.checkSchedule(scheduler, forum);
    } 
}
