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
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.text.ParseException;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.mail.MailManager;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.jobs.EmailNotification;
import com.sitescape.ef.jobs.EmailPosting;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;

import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.FunctionExistsException;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;
import com.sitescape.ef.security.function.WorkAreaFunctionMembershipExistsException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.EmailAlias;
import com.sitescape.ef.util.SZoneConfig;
/**
 * @author Janet McCann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AdminModuleImpl extends CommonDependencyInjection implements AdminModule {

	protected MailManager mailManager;
    public void setMailManager(MailManager mailManager) {
    	this.mailManager = mailManager;
    }
	/**
     * Do actual work to either enable or disable email notification.
     * @param id
     * @param value
     */
    public void setEnableNotification(Long id, boolean value) {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
        Binder binder = coreDao.loadBinder(id, companyId); 
   		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
   		process.enable(value, binder);
    }
    public ScheduleInfo getNotificationConfig(Long id) {
        Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneName()); 
 		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
  		return process.getScheduleInfo(binder);
    }
    
    public void setNotificationConfig(Long id, ScheduleInfo config) {
        Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneName()); 
 		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
  		process.setScheduleInfo(config, binder);
    }
    /**
     * Set the notification definition for a folder.  
     * @param id
     * @param definition - Use map to set NotifcationDef field
     * Distribution list is built 
     * by this method based on the Set of userIds passed in.
     * @throws NoPrincipalByTheIdException
     */
    public void modifyNotification(Long id, Map updates) 
    {
        Principal p;
		
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
        Binder binder = coreDao.loadBinder(id, companyId); 
     	NotificationDef current = binder.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		binder.setNotificationDef(current);
    	}
    	ObjectBuilder.updateObject(current, updates);
    }
 
 
    public List getEmailAliases() {
    	return coreDao.loadEmailAliases(RequestContextHolder.getRequestContext().getZoneName());
    }
    public void modifyEmailAlias(String aliasId, Map updates) {
    	EmailAlias alias = coreDao.loadEmailAlias(aliasId, RequestContextHolder.getRequestContext().getZoneName());
    	ObjectBuilder.updateObject(alias, updates);
    }
    public void addEmailAlias(Map updates) {
    	String zoneName = RequestContextHolder.getRequestContext().getZoneName(); 
    	EmailAlias alias = new EmailAlias();
    	alias.setZoneName(zoneName);
       	ObjectBuilder.updateObject(alias, updates);
       	coreDao.save(alias);   	
    }
    public void deleteEmailAlias(String aliasId) {
    	EmailAlias alias = coreDao.loadEmailAlias(aliasId, RequestContextHolder.getRequestContext().getZoneName());
    	List postings = alias.getPostings();
    	for (int i=0; i<postings.size(); ++i) {
    		PostingDef post = (PostingDef)postings.get(i);
    		post.setEmailAlias(null);
    		post.setEnabled(false);
    	}
       	coreDao.delete(alias);
    }
    /**
     * Enable/disable email posting.
     * @param id
     */
    public void setEnablePostings(boolean enable) {
       	getPostingObject().enable(enable, RequestContextHolder.getRequestContext().getZoneName());
    }
    public ScheduleInfo getPostingSchedule() {
       	return getPostingObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneName());
    }
    public void setPostingSchedule(ScheduleInfo config) throws ParseException {
    	getPostingObject().setScheduleInfo(config);
    }	     
    public List getPostingDefs() {
    	return getCoreDao().loadPostings(RequestContextHolder.getRequestContext().getZoneName());
    }
    private EmailPosting getPostingObject() {
    	String emailPostingClass = mailManager.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailManager.POSTING_JOB);
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
    public void modifyPosting(Long binderId, String postingId, Map updates) {
    	//posting defs are defined by admin
    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
//TODO: acl check
    	//Locate the posting
   		PostingDef post = binder.getPosting(postingId); 
    	if (post != null) ObjectBuilder.updateObject(post, updates);
    }
    public void addPosting(Long binderId, Map updates) {
       	//posting defs are defined by admin
    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
//TODO: acl check
   		PostingDef post = new PostingDef();
   		post.setZoneName(binder.getZoneName());
		ObjectBuilder.updateObject(post, updates);
		binder.addPosting(post);
    }    	
    public void deletePosting(Long binderId, String postingId) {
       	//posting defs are defined by admin
    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
//TODO: acl check
   		PostingDef post = binder.getPosting(postingId); 
    	if (post != null) binder.removePosting(post);
    }
    public void setPostings(Long binderId, List updates) {
    }
 
    public void addFunction(Function function) {
		User user = RequestContextHolder.getRequestContext().getUser();
		function.setZoneName(user.getZoneName());

        //Check that this user is allowed to do this operation
		Workspace workspace = getCoreDao().findTopWorkspace(user.getZoneName());
        accessControlManager.checkOperation(workspace, WorkAreaOperation.SITE_ADMINISTRATION);        
		
		List zoneFunctions = functionManager.findFunctions(user.getZoneName());
		if (zoneFunctions.contains(function)) {
			//Role already exists
			throw new FunctionExistsException(function.getName());
		}
		functionManager.addFunction(function);
	 
    }
    public void modifyFunction(Long id, Map updates) {
		User user = RequestContextHolder.getRequestContext().getUser();

        //Check that this user is allowed to do this operation
		Workspace workspace = getCoreDao().findTopWorkspace(user.getZoneName());
        accessControlManager.checkOperation(workspace, WorkAreaOperation.SITE_ADMINISTRATION);        
		
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

        //Check that this user is allowed to do this operation
		Workspace workspace = getCoreDao().findTopWorkspace(user.getZoneName());
        accessControlManager.checkOperation(workspace, WorkAreaOperation.SITE_ADMINISTRATION);        

        List zoneFunctions = functionManager.findFunctions(user.getZoneName());
		return zoneFunctions;
    }
    
	public void addWorkAreaFunctionMembership(WorkArea workArea, Long functionId, Set memberIds) {
       	String companyId = RequestContextHolder.getRequestContext().getZoneName();
		WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
		membership.setZoneName(companyId);
		membership.setWorkAreaId(workArea.getWorkAreaId());
		membership.setWorkAreaType(workArea.getWorkAreaType());
		membership.setFunctionId(functionId);
		membership.setMemberIds(memberIds);

        //Check that this user is allowed to do this operation; 
		// Is it SITE_ADMINISTRATION right operation for this checking?
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		
		List memberships = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(companyId, workArea);
		if (memberships.contains(membership)) {
			throw new WorkAreaFunctionMembershipExistsException(membership);
		}
        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
	}
	
	public void modifyWorkAreaFunctionMembership(WorkArea workArea, WorkAreaFunctionMembership membership) {
		
        //Check that this user is allowed to do this operation; 
		//Is it SITE_ADMINISTRATION right operation for this checking?
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);   
        
        getWorkAreaFunctionMembershipManager().updateWorkAreaFunctionMembership(membership);
	}
	
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
        //Check that this user is allowed to do this operation; 
		// Is it SITE_ADMINISTRATION right operation for this checking?
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        

        WorkAreaFunctionMembership wfm = getWorkAreaFunctionMembership(workArea, functionId);
        if (wfm != null) {
	        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
        }
    }
    
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
       	String companyId = RequestContextHolder.getRequestContext().getZoneName();
		
        //Check that this user is allowed to do this operation; 
		// Is it SITE_ADMINISTRATION right operation for this checking?
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        

        return getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
       		(companyId, workArea, functionId);
    }
    
	public List getWorkAreaFunctionMemberships(WorkArea workArea) {
	   	String companyId = RequestContextHolder.getRequestContext().getZoneName();
	    WorkArea source = workArea;
	    while (source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    }
	  	
        //Check that this user is allowed to do this operation; 
		// Is it SITE_ADMINISTRATION right operation for this checking?
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        

        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(companyId, workArea);
	}

	public List getWorkAreaFunctionMembershipsInherited(WorkArea workArea) {
	   	String companyId = RequestContextHolder.getRequestContext().getZoneName();
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return new ArrayList();
	    while (source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    }
	  	
        //Check that this user is allowed to do this operation; 
		// Is it SITE_ADMINISTRATION right operation for this checking?
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        

        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(companyId, source);
	}

	public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) 
    throws AccessControlException {
    	getAccessControlManager().checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL); 
    	if (workArea.isFunctionMembershipInherited() && !inherit) {
    		//copy parent values as beginning values
    		List current = getWorkAreaFunctionMembershipsInherited(workArea);
    		for (int i=0; i<current.size(); ++i) {
    			WorkAreaFunctionMembership wf = (WorkAreaFunctionMembership)current.get(i);
    			addWorkAreaFunctionMembership(workArea, wf.getFunctionId(), new HashSet(wf.getMemberIds()));
    		}
    	}
    	workArea.setFunctionMembershipInherited(inherit);
    } 

	public void createZone(String name) {
		Workspace top = new Workspace();
		top.setName(name);
		ProfileBinder profiles = new ProfileBinder();
		profiles.setName("_profiles");
		profiles.setZoneName(name);
		top.addBinder(profiles);
		User user = new User();
		user.setName(SZoneConfig.getString(name, "adminUser", "admin"));
		//generate id for top
		getCoreDao().save(top);
		
		Function function = new Function();
		function.setZoneName(name);
		function.setName("Site Administration");
		Set ops = new HashSet();
		for (Iterator iter = WorkAreaOperation.getWorkAreaOperations(); iter.hasNext();) {
			ops.add(((WorkAreaOperation)iter.next()).getName());
		}	
		//generate functionId
		functionManager.addFunction(function);
		WorkAreaFunctionMembership ms = new WorkAreaFunctionMembership();
		ms.setWorkAreaId(top.getWorkAreaId());
		ms.setWorkAreaType(top.getWorkAreaType());
		ms.setZoneName(name);
		ms.setFunctionId(function.getId());
		Set members = new HashSet();
		members.add(user.getId());
		ms.setMemberIds(members);		

	}

}
