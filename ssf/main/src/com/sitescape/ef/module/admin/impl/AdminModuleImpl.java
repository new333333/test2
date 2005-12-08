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
import java.util.List;
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
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.jobs.FolderEmailNotification;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.FunctionManager;
import com.sitescape.ef.security.function.FunctionExistsException;
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
	private FunctionManager functionManager;
    protected AccessControlManager accessControlManager;
    protected AclManager aclManager;

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
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
    public void setAccessControlManager(
            AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }
    public void setAclManager(AclManager aclManager) {
        this.aclManager = aclManager;
    }
   
    /**
     * Disable email notification for this forum.
     * @param forumId
     */
    public void disableNotification(Long forumId) {
    	setEnableNotification(forumId, false);
    }
    /**
     * Enable email notification for this forum.
     * @param forumId
     */
    public void enableNotification(Long forumId) {
       	setEnableNotification(forumId, true);
    }
    /**
     * Do actual work to either enable or disable email notification for this forum
     * @param forumId
     * @param value
     */
    protected void setEnableNotification(Long forumId, boolean value) {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
        Binder forum = coreDao.loadBinder(forumId, companyId); 
    	NotificationDef current = forum.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		current.setLastNotification(new Date());
    		forum.setNotificationDef(current);
    	}
    	current.setEnabled(value);
     	//Remove or add from scheduler
   		checkNotificationSchedule(forum);
    }
    /**
     * Set the notification schedule for a forum.  
     * @param forumId
     * @param definition - Use map to set teamOn,summaryLines,contextLevel,
     * emailAddress and schedule.  Distribution list is built 
     * by this method based on the Set of userIds passed in.
     * @param users - Set of Long userIds; Used to build the distribution list
     * @throws NoPrincipalByTheIdException
     */
    public void updateNotification(Long forumId, Map definition, Set principals) 
    {
        Principal p;
		Set notifyUsers = new HashSet();
		
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
        Binder forum = coreDao.loadBinder(forumId, companyId); 
     	NotificationDef current = forum.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		forum.setNotificationDef(current);
    		current.setLastNotification(new Date());    		
    	}
    	ObjectBuilder.updateObject(current, definition);
  		//	Pre-load for performance
   		coreDao.loadPrincipals(principals,companyId);
   		for (Iterator iter=principals.iterator(); iter.hasNext();) {
   			//	make sure user exists and is in this zone
   			p = coreDao.loadPrincipal((Long)iter.next(),companyId);
   			notifyUsers.add(new Notification(forum, p));   			
   		}

   		current.setDistribution(notifyUsers);
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
    public void addFunction(Function function) {
		User user = RequestContextHolder.getRequestContext().getUser();
		function.setZoneName(user.getZoneName());
		//TODO: what acl check is needed
		
		List zoneFunctions = functionManager.findFunctions(user.getZoneName());
		if (zoneFunctions.contains(function)) {
			//Role already exists
			throw new FunctionExistsException(function.getName());
		}
		functionManager.addFunction(function);
	 
    }
    public void updateFunction(Long id, Map updates) {
		User user = RequestContextHolder.getRequestContext().getUser();
		//TODO: what acl check is needed
		
		List zoneFunctions = functionManager.findFunctions(user.getZoneName());
		for (int i=0; i<zoneFunctions.size(); ++i) {
			Function function = (Function)zoneFunctions.get(i);
			if (id.equals(function.getId())) {
			   	ObjectBuilder.updateObject(function, updates);
			    
				functionManager.updateFunction(function);			
				break;
			}
		}
    }
    public List getFunctions() {
		User user = RequestContextHolder.getRequestContext().getUser();	
		//TODO: what acl check is needed
		List zoneFunctions = functionManager.findFunctions(user.getZoneName());
		return zoneFunctions;
    }

}
