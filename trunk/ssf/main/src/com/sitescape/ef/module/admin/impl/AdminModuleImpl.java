/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.module.admin.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.text.ParseException;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Notification;
import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.jobs.EmailNotification;
import com.sitescape.ef.jobs.EmailPosting;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.FunctionExistsException;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.jobs.Schedule;
import com.sitescape.ef.module.admin.PostingConfig;
/**
 * @author Janet McCann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AdminModuleImpl extends CommonDependencyInjection implements AdminModule {
	protected String emailPostingClass;
	public void setEmailPostingJobClass(String className) {
		this.emailPostingClass = className;
	}

	/**
     * Disable email notification.
     * @param id
     */
    public void disableNotification(Long id) {
    	setEnableNotification(id, false);
    }
    /**
     * Enable email notification.
     * @param id
     */
    public void enableNotification(Long id) {
       	setEnableNotification(id, true);
    }
    /**
     * Do actual work to either enable or disable email notification.
     * @param id
     * @param value
     */
    protected void setEnableNotification(Long id, boolean value) {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
        Binder binder = coreDao.loadBinder(id, companyId); 
    	NotificationDef current = binder.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		current.setLastNotification(new Date());
    		binder.setNotificationDef(current);
    	}
    	current.setEnabled(value);
     	//Remove or add from scheduler
   		checkNotificationSchedule(binder);
    }
    /**
     * Set the notification schedule.  
     * @param id
     * @param definition - Use map to set teamOn,summaryLines,contextLevel,
     * emailAddress and schedule.  Distribution list is built 
     * by this method based on the Set of userIds passed in.
     * @param users - Set of Long userIds; Used to build the distribution list
     * @throws NoPrincipalByTheIdException
     */
    public void modifyNotification(Long id, Map updates, Set principals) 
    {
        Principal p;
		Set notifyUsers = new HashSet();
		
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
        Binder binder = coreDao.loadBinder(id, companyId); 
     	NotificationDef current = binder.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		binder.setNotificationDef(current);
    		current.setLastNotification(new Date());    		
    	}
    	ObjectBuilder.updateObject(current, updates);
  		//	Pre-load for performance
   		coreDao.loadPrincipals(principals,companyId);
   		for (Iterator iter=principals.iterator(); iter.hasNext();) {
   			//	make sure user exists and is in this zone
   			p = coreDao.loadPrincipal((Long)iter.next(),companyId);
   			notifyUsers.add(new Notification(binder, p));   			
   		}

   		current.setDistribution(notifyUsers);
		checkNotificationSchedule(binder);
    }
    /**
     * Make sure the scheduler is in sync with notification definition.
     * @param binder
     */

    public void checkNotificationSchedule(Binder binder)  {
   		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
   		process.checkSchedule(scheduler, binder);
    } 
 
    /**
     * Enable/disable email posting.
     * @param id
     */
    public void setEnablePostings(boolean enable) {
       	getPostingObject().enable(enable);
    }
 
    public void setPostingConfig(PostingConfig postingConfig) throws ParseException {
    	getPostingObject().setScheduleInfo(postingConfig);
    	
    }
    public PostingConfig getPostingConfig() {	
       	return new PostingConfig(getPostingObject().getScheduleInfo());
    }
    public List getPostingDefs() {
    	return getCoreDao().loadPostings();
    }
    private EmailPosting getPostingObject() {
        try {
            Class processorClass = ReflectHelper.classForName(emailPostingClass);
            EmailPosting job = (EmailPosting)processorClass.newInstance();
            return job;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(
                    "Invalid EmailPosting class name '" + emailPostingClass + "'",
                    e);
        } catch (InstantiationException e) {
            throw new ConfigurationException(
                    "Cannot instantiate EmailPosting of type '"
                            + emailPostingClass + "'");
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(
                    "Cannot instantiate EmailPosting of type '"
                            + emailPostingClass + "'");
        }
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
    public void modifyFunction(Long id, Map updates) {
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
