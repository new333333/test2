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
package com.sitescape.team.module.admin.impl;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.mail.internet.InternetAddress;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.OrderBy;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.EmailNotification;
import com.sitescape.team.jobs.EmailPosting;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.processor.BinderProcessor;
import com.sitescape.team.module.dashboard.DashboardModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.shared.AccessUtils;
import com.sitescape.team.module.shared.ObjectBuilder;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.FunctionExistsException;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Validator;

/**
 * @author Janet McCann
 *
 */
public abstract class AbstractAdminModule extends CommonDependencyInjection implements AdminModule {
	protected MailModule mailModule;
	/**
	 * Setup by spring
	 * @param mailModule
	 */
	public void setMailModule(MailModule mailModule) {
    	this.mailModule = mailModule;
    }
	protected MailModule getMailModule() {
		return mailModule;
	}
    protected DefinitionModule definitionModule;
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
   	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	protected WorkspaceModule workspaceModule;
	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
   	protected WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}

	protected FolderModule folderModule;
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
   	protected FolderModule getFolderModule() {
		return folderModule;
	}
 
	protected BinderModule binderModule;
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
   	protected BinderModule getBinderModule() {
		return binderModule;
	}
	protected DashboardModule dashboardModule;
	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
   	protected DashboardModule getDashboardModule() {
		return dashboardModule;
	}
   	protected FileModule fileModule;
   	public void setFileModule(FileModule fileModule) {
   		this.fileModule = fileModule;
   	}
   	protected FileModule getFileModule() {
   		return fileModule;
   	}	
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

   	/**
   	 * Use operation so we can keep the logic out of application
	 * and easisly change the required rights
	 */
   	public boolean testAccess(WorkArea workArea, AdminOperation operation) {
   		try {
   			checkAccess(workArea, operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
   	public void checkAccess(WorkArea workArea, AdminOperation operation) {
   		if (workArea instanceof TemplateBinder) {
			getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
   		} else {
   			switch (operation) {
   			case manageFunctionMembership:
    				getAccessControlManager().checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);
   				break;
   			default:
   				throw new NotSupportedException(operation.toString(), "checkAccess");
  					
   			}
   		}		
	}
	private BinderProcessor loadBinderProcessor(Binder binder) {
		return (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
	}

   	/**
	 * Use operation so we can keep the logic out of application
	 * and easisly change the required rights
   	 * 
   	 */
  	public boolean testAccess(AdminOperation operation) {
   		try {
   			checkAccess(operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
  	public void checkAccess(AdminOperation operation) {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
		switch (operation) {
			case manageFunction:
			case manageMail:
			case manageTemplate:
			case manageErrorLogs:
  				getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   				break;
			case report:
   				if (getAccessControlManager().testOperation(top, WorkAreaOperation.GENERATE_REPORTS)) break;
 				getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   				break;
			default:
   				throw new NotSupportedException(operation.toString(), "checkAccess");
		}
   	}

    public List<PostingDef> getPostings() {
    	return coreDao.loadPostings(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void modifyPosting(String postingId, Map updates) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	ObjectBuilder.updateObject(post, updates);
    }
    public void addPosting(Map updates) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = new PostingDef();
    	post.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
       	ObjectBuilder.updateObject(post, updates);
       	post.setEmailAddress(post.getEmailAddress().toLowerCase());
      	coreDao.save(post);   	
    }
    public void deletePosting(String postingId) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	if (post.getBinder() != null) {
    		post.getBinder().setPosting(null);
    	}
       	coreDao.delete(post);
    }
    public ScheduleInfo getPostingSchedule() {
      	//let anyone get it;
       	return getPostingObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void setPostingSchedule(ScheduleInfo config) throws ParseException {
       	checkAccess(AdminOperation.manageMail);
    	getPostingObject().setScheduleInfo(config);
    }	     
    private EmailPosting getPostingObject() {
    	String emailPostingClass = getMailModule().getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.POSTING_JOB);
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

	/**
     * Do actual work to either enable or disable digest notification.
     * @param id
     * @param value
     */
	public ScheduleInfo getNotificationSchedule() {
  		return getNotificationObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
	}
	    
    //inside write transaction    
    public void setNotificationSchedule(ScheduleInfo config) {
    	checkAccess(AdminOperation.manageMail);
        //data is stored with job
        getNotificationObject().setScheduleInfo(config);
    }    
    private EmailNotification getNotificationObject() {
    	String emailNotifyClass = getMailModule().getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.NOTIFICATION_JOB);
        try {
            Class processorClass = ReflectHelper.classForName(emailNotifyClass);
            EmailNotification job = (EmailNotification)processorClass.newInstance();
            return job;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(
                    "Invalid EmailNotification class name '" + emailNotifyClass + "'",
                    e);
        } catch (InstantiationException e) {
            throw new ConfigurationException(
                    "Cannot instantiate EmailNotification of type '"
                            + emailNotifyClass + "'");
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(
                    "Cannot instantiate EmailNotification of type '"
                            + emailNotifyClass + "'");
        }
    }    
	public void updateDefaultDefinitions(Long topId) {
		Workspace top = (Workspace)getCoreDao().loadBinder(topId, topId);
		
		//default definitions stored in separate config file
		String startupConfig = SZoneConfig.getString(top.getName(), "property[@name='startupConfig']", "config/startup.xml");
		SAXReader reader = new SAXReader(false);  
		InputStream in=null;
		try {
			in = new ClassPathResource(startupConfig).getInputStream();
			Document cfg = reader.read(in);
			in.close();
			List<Element> elements = cfg.getRootElement().selectNodes("definitionFile");
			List<String> defs = new ArrayList();
			for (Element element:elements) {
				String file = element.getTextTrim();
				reader = new SAXReader(false);  
				try {
					in = new ClassPathResource(file).getInputStream();
					defs.add(getDefinitionModule().addDefinition(in, null, null, null, true).getId());
					getCoreDao().flush();
				} catch (Exception ex) {
					logger.error("Cannot read definition from file: " + file + " " + ex.getMessage());
					return; //cannot continue, rollback is enabled
				} finally {
					if (in!=null) in.close();
				}
			}
			for (String id:defs) {
				getDefinitionModule().updateDefinitionReferences(id);
			}

		} catch (Exception ex) {
			logger.error("Cannot read startup configuration:", ex);
		}
	}	
	public void addFunction(String name, Set<WorkAreaOperation> operations) {
		checkAccess(AdminOperation.manageFunction);
		Function function = new Function();
		function.setName(name);
		function.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		function.setOperations(operations);
		
		List zoneFunctions = functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
		if (zoneFunctions.contains(function)) {
			//Role already exists
			throw new FunctionExistsException(function.getName());
		}
		functionManager.addFunction(function);
	 
    }
    public void modifyFunction(Long id, Map updates) {
		checkAccess(AdminOperation.manageFunction);
		Function function = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		if (updates.containsKey("name") && function.getName() != updates.get("name")) {
			List zoneFunctions = functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
			//make sure unqiue - do after find or hibernate will update
			zoneFunctions.remove(function);
			function.setName((String)updates.get("name"));
			if (zoneFunctions.contains(function)) {
				//Role already exists
				throw new FunctionExistsException(function.getName());
			}
			
		}
		ObjectBuilder.updateObject(function, updates);
		functionManager.updateFunction(function);			
    }
    public List deleteFunction(Long id) {
		checkAccess(AdminOperation.manageFunction);
		Function f = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		List result = functionManager.deleteFunction(f);
		if(result != null) {
			result.add(f);
			return result;
		}
		else
			return null;
    }
    public List<Function> getFunctions() {
		//let anyone read them			
        return  functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
    }
	//no transaction
    public void setWorkAreaFunctionMemberships(final WorkArea workArea, final Map<Long, Set<Long>> functionMemberships) {
		checkAccess(workArea, AdminOperation.manageFunctionMembership);
		final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		//get list of current readers to compare for indexing
		List<WorkAreaFunctionMembership>wfms = 
	       		getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.READ_ENTRIES);
       	TreeSet<Long> origional = new TreeSet();
        for (WorkAreaFunctionMembership wfm:wfms) {
        	origional.addAll(wfm.getMemberIds());
    	}
        //first remove any that are not in the new list
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		//get list of current memberships
        		List<WorkAreaFunctionMembership>wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(zoneId, workArea);
                		for( WorkAreaFunctionMembership wfm:wfms) {
        			if (!functionMemberships.containsKey(wfm.getFunctionId()))
        				getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);       	
        		}
        		for (Map.Entry<Long, Set<Long>> fm : functionMemberships.entrySet()) {
        			WorkAreaFunctionMembership membership=null;
        			//find in current list
        			for (int i=0; i<wfms.size(); ++i) {
        				WorkAreaFunctionMembership wfm = wfms.get(i);
        				if (wfm.getFunctionId().equals(fm.getKey())) {
        					membership = wfm;
        					break;	        	
        				}
        			}
        			Set members = fm.getValue();
        			if (membership == null) { 
        				membership = new WorkAreaFunctionMembership();
        				membership.setZoneId(zoneId);
        				membership.setWorkAreaId(workArea.getWorkAreaId());
        				membership.setWorkAreaType(workArea.getWorkAreaType());
        				membership.setFunctionId(fm.getKey());
        				membership.setMemberIds(members);
        				getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
        			} else if (members == null || members.isEmpty()) {
        				getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(membership);       	
        			} else {
        				Set mems = membership.getMemberIds();
        				if (!mems.equals(members)) {
        					mems.clear();
        					mems.addAll(members);
        					membership.setMemberIds(mems);
        				}
        			}
        		}
           		workArea.setFunctionMembershipInherited(false);
				processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
				return null;
        	}});
		//get new list of readers
      	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.READ_ENTRIES);
      	TreeSet<Long> current = new TreeSet();
      	for (WorkAreaFunctionMembership wfm:wfms) {
      		current.addAll(wfm.getMemberIds());
      	}
      	//only reindex if readers were affected.  Do outside transaction
		if (!origional.equals(current) && (workArea instanceof Binder)) {
			Binder binder = (Binder)workArea;
			loadBinderProcessor(binder).indexFunctionMembership(binder, true);
		}
	}


	//no transaction
    public void setWorkAreaOwner(final WorkArea workArea, final Long userId, final boolean propagate) {
    	 checkAccess(workArea, AdminOperation.manageFunctionMembership);
    	 final List<Binder>binders = new ArrayList();
       	 if (propagate) {
			if (workArea instanceof Binder) {
				Binder binder = (Binder)workArea;
				binders.addAll(binder.getBinders());
				for (int i=0; i<binders.size();) {
					Binder child = binders.get(i);
					if (!userId.equals(child.getOwnerId())) {
						checkAccess(child, AdminOperation.manageFunctionMembership);
						++i;
					} else {
						binders.remove(i);  //already set, get out of list
					}
					binders.addAll(child.getBinders());
				}
			}
    	 }
    	if (!binders.isEmpty() || !workArea.getOwnerId().equals(userId)) {
    		getTransactionTemplate().execute(new TransactionCallback() {
   		        	public Object doInTransaction(TransactionStatus status) {
   		        		User user = getProfileDao().loadUser(userId, RequestContextHolder.getRequestContext().getZoneId());
   		        		workArea.setOwner(user);
   		        		for (Binder child:binders) {
   		        			child.setOwner(user);
   		        		}
   		        		return null;
   		       }});
    		//do outside transaction
    		//need to update access, since owner has changed - assume read access is effected
			if (workArea instanceof Binder) {
				Binder binder = (Binder)workArea;
				binders.add(binder);
				loadBinderProcessor(binder).indexOwner(binders, userId);
			}
    	}
    }
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
		// open to anyone - only way to get parentMemberships
    	// checkAccess(workArea, "getWorkAreaFunctionMembership");

        return getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
       		(RequestContextHolder.getRequestContext().getZoneId(), workArea, functionId);
    }
    
	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMemberships(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		//checkAccess(workArea, "getWorkAreaFunctionMemberships");

        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
        		RequestContextHolder.getRequestContext().getZoneId(), workArea);
	}

	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMembershipsInherited(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		// checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return new ArrayList();
	    while (source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    	//template binders have this problem, cause they are not connected to a 
	    	//root until instanciated, but want to inherit from a future parent
	    	if (source == null) return new ArrayList();
	    }
 
        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), source);
	}

	//Routine to return the workarea that access control is being inherited from
	public WorkArea getWorkAreaFunctionInheritance(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		// checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return source;
	    while (source != null && source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    }
        return source;
	}
	//no transaction
	public void setWorkAreaFunctionMembershipInherited(final WorkArea workArea, final boolean inherit) 
    throws AccessControlException {
    	checkAccess(workArea, AdminOperation.manageFunctionMembership);
        Boolean index = (Boolean) getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		if (inherit) {
        			//remove them
        			List current = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), workArea);
        			for (int i=0; i<current.size(); ++i) {
	    	        WorkAreaFunctionMembership wfm = (WorkAreaFunctionMembership)current.get(i);
	    	        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
        			}
    		
        		} else if (workArea.isFunctionMembershipInherited() && !inherit) {
        			//copy parent values as beginning values
        			getWorkAreaFunctionMembershipManager().copyWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(),getWorkAreaFunctionInheritance(workArea), workArea);
        		}
        	   	//see if there is a real change
            	if (workArea.isFunctionMembershipInherited()  != inherit) {
            		workArea.setFunctionMembershipInherited(inherit);
             		processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
             		//just changed from not inheritting to inherit = need to update index
            		//if changed from inherit to not, index remains the same
              		if (inherit) return Boolean.TRUE;

            	}
        		return Boolean.FALSE;
        	}});
        //index outside of transaction
        if (index && (workArea instanceof Binder)) {
			Binder binder = (Binder)workArea;
			loadBinderProcessor(binder).indexFunctionMembership(binder, true);
		}
     } 
	private void processAccessChangeLog(WorkArea workArea, String operation) {
        if ((workArea instanceof Binder) && !(workArea instanceof TemplateBinder)) {
        	Binder binder = (Binder)workArea;
        	User user = RequestContextHolder.getRequestContext().getUser();
        	binder.incrLogVersion();
        	binder.setModification(new HistoryStamp(user));
        	loadBinderProcessor(binder).processChangeLog(binder, operation);
       	}
	}

    public Map<String, Object> sendMail(Collection<Long> ids, Collection<String> emailAddresses, String subject, Description body) throws Exception {
    	return sendMail(null, ids, emailAddresses, subject, body, false); 
    }
    public Map<String, Object> sendMail(Entry entry, Collection<Long> ids, Collection<String> emailAddresses, String subject, Description body, boolean sendAttachments) throws Exception {
    	User user = RequestContextHolder.getRequestContext().getUser();
		Set emailSet = new HashSet();
		List distribution = new ArrayList();
		List errors = new ArrayList();
		Map result = new HashMap();
		result.put(ObjectKeys.SENDMAIL_ERRORS, errors);
		result.put(ObjectKeys.SENDMAIL_DISTRIBUTION, distribution);
		//add email address listed 
		Object[] errorParams = new Object[3];
		if (emailAddresses != null) {
			for (String e: emailAddresses) {
				if (!Validator.isNull(e)) {
					try {
						emailSet.add(new InternetAddress(e.trim()));
						distribution.add(e);
					} catch (Exception ex) {
						errorParams[0] = "";
						errorParams[1] = e;
						errorParams[2] = ex.getLocalizedMessage();
						errors.add(NLT.get("errorcode.badToAddress", errorParams));							
					}
				}
			}
		}
		Set<Long> userIds = getProfileDao().explodeGroups(ids, user.getZoneId());
		List<User> users = getCoreDao().loadObjects(userIds, User.class, user.getZoneId());
		for (User e:users) {
			try {
				emailSet.add(new InternetAddress(e.getEmailAddress().trim()));
				distribution.add(e.getEmailAddress());
			} catch (Exception ex) {
				errorParams[0] = e.getTitle();
				errorParams[1] = e.getEmailAddress();
				errorParams[2] = ex.getLocalizedMessage();
				errors.add(NLT.get("errorcode.badToAddress", errorParams));							
			}
		}
		if (emailSet.isEmpty()) {
			//no-one to send tos
			errors.add(0, NLT.get("errorcode.noRecipients", errorParams));
			result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_FAILED);
			return result;			
		}
    	Map message = new HashMap();
       	try {
    		message.put(MailModule.FROM, new InternetAddress(user.getEmailAddress()));
    	} catch (Exception ex) {
			errorParams[0] = user.getTitle();
			errorParams[1] = user.getEmailAddress();
			errorParams[2] = ex.getLocalizedMessage();
			errors.add(0, NLT.get("errorcode.badFromAddress", errorParams));
			result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_FAILED);
			//cannot send without valid from address
			return result;
    	}
   		message.put(MailModule.HTML_MSG, body.getText());
    		
    	message.put(MailModule.SUBJECT, subject);
 		message.put(MailModule.TO, emailSet);
 		boolean sent;
 		if (entry != null) {
 	   		sent = getMailModule().sendMail(entry, message, user.getTitle() + " email", sendAttachments);    		
 		} else {
    		sent = getMailModule().sendMail(RequestContextHolder.getRequestContext().getZone(), message, user.getTitle() + " email");    		
    	}
		if (sent) result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_SENT);
		else result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_SCHEDULED);
		return result;
    }


   public List<ChangeLog> getChanges(Long binderId, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("owningBinderId", binderId);
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   return filterChangeLogs(getCoreDao().loadObjects(ChangeLog.class, filter, RequestContextHolder.getRequestContext().getZoneId()));
	   //need to filter for access
   }
   public List<ChangeLog> getChanges(EntityIdentifier entityIdentifier, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("entityId", entityIdentifier.getEntityId());
	   filter.add("entityType", entityIdentifier.getEntityType().name());
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   return filterChangeLogs(getCoreDao().loadObjects(ChangeLog.class, filter, RequestContextHolder.getRequestContext().getZoneId())); 
   	
   }
   
   public List<ChangeLog> getWorkflowChanges(EntityIdentifier entityIdentifier, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("entityId", entityIdentifier.getEntityId());
	   filter.add("entityType", entityIdentifier.getEntityType().name());
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   List<ChangeLog> changeLogs = getCoreDao().loadObjects(ChangeLog.class, filter, RequestContextHolder.getRequestContext().getZoneId()); 
	   
	   List<ChangeLog> wfChangeLogs = filterWorkflowChanges(changeLogs);
	   
	   return filterChangeLogs(wfChangeLogs); 
   	
   }
   
   private List<ChangeLog> filterWorkflowChanges(List<ChangeLog> changeLogs) {
	   List<ChangeLog> wfChangeLogs = new ArrayList<ChangeLog>();
	   for (ChangeLog log: changeLogs) {
		   if(log.getOperation().equals(ChangeLog.STARTWORKFLOW)
               || log.getOperation().equals(ChangeLog.MODIFYWORKFLOWSTATEONREPLY)
			   || log.getOperation().equals(ChangeLog.MODIFYWORKFLOWSTATE)
			   || log.getOperation().equals(ChangeLog.ENDWORKFLOW)
			   || log.getOperation().equals(ChangeLog.ADDWORKFLOWRESPONSE)
			   || log.getOperation().equals(ChangeLog.WORKFLOWTIMEOUT)
			   || log.getOperation().equals(ChangeLog.ADDENTRY))
			   		wfChangeLogs.add(log);
	   }
	   return wfChangeLogs;
   }
   private List<ChangeLog> filterChangeLogs(List<ChangeLog> changeLogs) {
	   User user = RequestContextHolder.getRequestContext().getUser();
	   if (user.isSuper()) return changeLogs;
	   // get the current users acl set
	   Set<Long> userAclSet = getProfileDao().getPrincipalIds(user);
	   Set userStringIds = new HashSet();
	   for (Long id:userAclSet) {
		   userStringIds.add(id.toString());
	   }
	   List<ChangeLog> result = new ArrayList();
	   for (ChangeLog log: changeLogs) {
		   try {
			   Document doc = log.getDocument();
			   if (doc == null) continue;
			   Element root = doc.getRootElement();
			   if (root == null) continue;
			   if (AccessUtils.checkAccess(root, userStringIds)) result.add(log);
		   } catch (Exception ex) {
			   logger.error("Error processing change log: " + log.getId() + " " + ex.getLocalizedMessage());
		   }
	   }
	   return result;

   }
}
