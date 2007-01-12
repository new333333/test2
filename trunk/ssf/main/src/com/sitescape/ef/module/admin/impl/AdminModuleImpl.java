/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.module.admin.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import javax.mail.internet.InternetAddress;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.InvalidArgumentException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.domain.ChangeLog;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Description;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.jobs.EmailPosting;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.jobs.SendEmail;
import com.sitescape.ef.mail.MailManager;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.binder.BinderComparator;
import com.sitescape.ef.module.binder.BinderProcessor;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.FunctionExistsException;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.util.SZoneConfig;

import com.sitescape.util.Validator;

/**
 * @author Janet McCann
 *
 */
public class AdminModuleImpl extends CommonDependencyInjection implements AdminModule {
	private static final String[] defaultDefAttrs = new String[]{ObjectKeys.FIELD_INTERNALID, ObjectKeys.FIELD_ZONE, ObjectKeys.FIELD_ENTITY_DEFTYPE};

    protected DefinitionModule definitionModule;
	protected MailManager mailManager;
	/**
	 * Setup by spring
	 * @param mailManager
	 */
	public void setMailManager(MailManager mailManager) {
    	this.mailManager = mailManager;
    }
	protected MailManager getMailManager() {
		return mailManager;
	}
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

	public void checkAccess(WorkArea workArea, String operation) {
		if (operation.startsWith("setWorkAreaFunctionMembership")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else if (operation.startsWith("deleteWorkAreaFunctionMembership")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else if (operation.startsWith("getWorkAreaFunctionMembership")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else if (operation.startsWith("getWorkAreaFunctionMemberships")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else if (operation.startsWith("setWorkAreaFunctionMembershipInherited")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else if (operation.startsWith("getWorkAreaFunctionMembershipInherited")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.READ_ENTRIES);
		}
		
	}
   	public void checkAccess(String operation) {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
        getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);        		

        if ("modifyPosting".equals(operation)) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("addPosting")) {
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("deletePosting")) {
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
 		} else if (operation.startsWith("setPostingSchedule")) {
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("addConfiguration")) {
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("deleteConfiguration")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("modifyConfiguration")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("addFunction")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("modifyFunction")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("deleteFunction")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else {
			accessControlManager.checkOperation(top, WorkAreaOperation.READ_ENTRIES);
		}

		
   	}

    public List getPostings() {
    	//TODO: access check
    	return coreDao.loadPostings(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void modifyPosting(String postingId, Map updates) {
    	checkAccess("modifyPosting");
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	ObjectBuilder.updateObject(post, updates);
    }
    public void addPosting(Map updates) {
    	checkAccess("addPosting");
    	PostingDef post = new PostingDef();
    	post.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
       	ObjectBuilder.updateObject(post, updates);
       	coreDao.save(post);   	
    }
    public void deletePosting(String postingId) {
    	checkAccess("deletePosting");
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	if (post.getBinder() != null) {
    		post.getBinder().setPosting(null);
    	}
       	coreDao.delete(post);
    }
    public ScheduleInfo getPostingSchedule() {
    	//TODO: checkAccess;
       	return getPostingObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneName());
    }
    public void setPostingSchedule(ScheduleInfo config) throws ParseException {
       	checkAccess("setPostingSchedule");
    	getPostingObject().setScheduleInfo(config);
    }	     
    private EmailPosting getPostingObject() {
    	String emailPostingClass = getMailManager().getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailManager.POSTING_JOB);
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
 
	public BinderConfig createDefaultConfiguration(int type) {
       	//This is called as a side effect to bootstrap
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		String title=null;
		String internalId=null;
		switch (type) {
			case Definition.FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FOLDER_CONFIG, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_folder";
				internalId = ObjectKeys.DEFAULT_FOLDER_CONFIG;
				break;
			}

			case Definition.WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_WORKSPACE_CONFIG, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_workspace";
				internalId = ObjectKeys.DEFAULT_WORKSPACE_CONFIG;
				break;
			}
			case Definition.USER_WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_user_workspace";
				internalId = ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG;
				break;
			}
		default: {
			throw new InvalidArgumentException("Invalid type:" + type);
			}
		}
		return doAddConfig(type, title, internalId);
	 }
	 public String addConfiguration(int type, String title) {
	    checkAccess("addConfiguration");
	    return doAddConfig(type, title, null).getId();
	 }
	 protected BinderConfig doAddConfig(int type, String title, String internalId) {
		List defs = new ArrayList();
		defs.add(getDefinitionModule().createDefaultDefinition(type).getId());
		BinderConfig config = new BinderConfig();
		switch (type) {
			case Definition.WORKSPACE_VIEW: {
				break;
			}
			case Definition.USER_WORKSPACE_VIEW: {
				break;
			}
			case Definition.FOLDER_VIEW: {
				defs.add(getDefinitionModule().createDefaultDefinition(Definition.FOLDER_ENTRY).getId());
				break;
			}
			default: {
				throw new InvalidArgumentException("Invalid type:" + type);
			}
		}
		
		config.setTitle(title);
		config.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		config.setDefinitionIds(defs);
		config.setDefinitionType(type);
		config.setInternalId(internalId);
		getCoreDao().save(config);
		return config;
		
	}
	 public void deleteConfiguration(String id) {
		checkAccess("deleteConfiguration");
		BinderConfig config = getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneId());
		getCoreDao().delete(config);
	}
	public void modifyConfiguration(String id, Map updates) {
		checkAccess("modifyConfiguration");
		BinderConfig config = getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneId());
		ObjectBuilder.updateObject(config, updates);
	}
	public BinderConfig getConfiguration(String id) {
		//TODO: is there access
		return getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneId());
	}
	public List getConfigurations() {
		//TODO: is there access
		return getCoreDao().loadConfigurations(RequestContextHolder.getRequestContext().getZoneId());
	}
	public List getConfigurations(int type) {
		//TODO: is there access
		return getCoreDao().loadConfigurations( RequestContextHolder.getRequestContext().getZoneId(), type);
	}
	public void addFunction(String name, Set operations) {
		checkAccess("addFunction");
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
		checkAccess("modifyFunction");
		Function function = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		ObjectBuilder.updateObject(function, updates);
		functionManager.updateFunction(function);			
    }
    public void deleteFunction(Long id) {
		checkAccess("deleteFunction");
		Function f = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		functionManager.deleteFunction(f);
    }
   public List getFunctions() {
		//TODO: any access?			
        return  functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
    }
	
	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map functionMemberships) {
		checkAccess(workArea, "setWorkAreaFunctionMembership");
		Iterator itFunctions = functionMemberships.entrySet().iterator();
		while (itFunctions.hasNext()) {
			Map.Entry fm = (Map.Entry)itFunctions.next();
			setWorkAreaFunctionMembership(workArea, (Long)fm.getKey(), (Set)fm.getValue());
		}
	}
	
	public void setWorkAreaFunctionMembership(WorkArea workArea, Long functionId, Set memberIds) {
		checkAccess(workArea, "setWorkAreaFunctionMembership");
      	Workspace zone = RequestContextHolder.getRequestContext().getZone();
      	WorkAreaFunctionMembership membership =
      		getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership(zone.getId(), workArea, functionId);
		if (membership == null) { 
	       	membership = new WorkAreaFunctionMembership();
	       	membership.setZoneId(zone.getId());
	       	membership.setWorkAreaId(workArea.getWorkAreaId());
	       	membership.setWorkAreaType(workArea.getWorkAreaType());
	       	membership.setFunctionId(functionId);
	       	membership.setMemberIds(memberIds);
	        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
	        processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
		} else {
			Set mems = membership.getMemberIds();
			mems.clear();
			mems.addAll(memberIds);
			membership.setMemberIds(mems);
			getWorkAreaFunctionMembershipManager().updateWorkAreaFunctionMembership(membership);
    	    processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
		}
	}
	
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
		checkAccess(workArea, "deleteWorkAreaFunctionMembership");

        WorkAreaFunctionMembership wfm = getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
   				(RequestContextHolder.getRequestContext().getZoneId(), workArea, functionId);
        if (wfm != null) {
	        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
	        processAccessChangeLog(workArea, ChangeLog.ACCESSDELETE);

        }
    }
    
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
		checkAccess(workArea, "getWorkAreaFunctionMembership");

        return getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
       		(RequestContextHolder.getRequestContext().getZoneId(), workArea, functionId);
    }
    
	public List getWorkAreaFunctionMemberships(WorkArea workArea) {
		checkAccess(workArea, "getWorkAreaFunctionMemberships");

        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
        		RequestContextHolder.getRequestContext().getZoneId(), workArea);
	}

	public List getWorkAreaFunctionMembershipsInherited(WorkArea workArea) {
		checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return new ArrayList();
	    while (source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    }
 
        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), source);
	}

	//Routine to return the workarea that access control is being inherited from
	public WorkArea getWorkAreaFunctionInheritance(WorkArea workArea) {
		checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return source;
	    while (source != null && source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    }
        return source;
	}

	public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) 
    throws AccessControlException {
    	checkAccess(workArea, "setWorkAreaFunctionMembershipInherited");
    	if (inherit) {
    		//remove them
    	   	List current = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), workArea);
    	   	for (int i=0; i<current.size(); ++i) {
    	        WorkAreaFunctionMembership wfm = (WorkAreaFunctionMembership)current.get(i);
   		        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
    	   	}
    		
    	} else if (workArea.isFunctionMembershipInherited() && !inherit) {
    		//copy parent values as beginning values
    		List current = getWorkAreaFunctionMembershipsInherited(workArea);
           	for (int i=0; i<current.size(); ++i) {
    			WorkAreaFunctionMembership wf = (WorkAreaFunctionMembership)current.get(i);
    			WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
    			membership.setZoneId(wf.getZoneId());
    			membership.setWorkAreaId(workArea.getWorkAreaId());
    			membership.setWorkAreaType(workArea.getWorkAreaType());
    			membership.setFunctionId(wf.getFunctionId());
    			membership.setMemberIds(new HashSet(wf.getMemberIds()));    		
    	        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
     		}
    	}
    	if (workArea.isFunctionMembershipInherited()  != inherit) {
    		workArea.setFunctionMembershipInherited(inherit);
    		processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
    	}
    } 
	private void processAccessChangeLog(WorkArea workArea, String operation) {
        if (workArea instanceof Binder) {
        	Binder binder = (Binder)workArea;
        	User user = RequestContextHolder.getRequestContext().getUser();
        	binder.incrLogVersion();
        	binder.setModification(new HistoryStamp(user));
        	BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
        	processor.processChangeLog(binder, operation);
       	}
		
	}
	//return binders this user is a team_member of
	public List getTeamMemberships(Long userId) {
		//team membership is implemented by a reserved role
        User current = RequestContextHolder.getRequestContext().getUser();
        
        User user;
        if ((userId == null) || current.getId().equals(userId)) user = current;
        else user = getProfileDao().loadUser(userId, current.getZoneId());
		List<WorkAreaFunctionMembership> wfm = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(
				RequestContextHolder.getRequestContext().getZoneId(), WorkAreaOperation.TEAM_MEMBER, getProfileDao().getPrincipalIds(user));
	    Set ids = new HashSet();
	    for (WorkAreaFunctionMembership w: wfm) {
	    	ids.add(w.getWorkAreaId());
	    }
	    List wa = getCoreDao().loadObjects(ids, Binder.class, user.getZoneId());
	    
	    //can certainly see own memberships
	    Set result = new TreeSet(new BinderComparator(current.getLocale()));
	    if (user == current) result.addAll(wa);
	    else {
	    	for (int i=0; i<wa.size(); ++i) {
	    		Binder b = (Binder) wa.get(i);
	    		try {
	    			//TODO: is this what we want??
	    			getAccessControlManager().checkOperation(user, b, WorkAreaOperation.READ_ENTRIES);
	    			result.add(b);
	    		} catch (Exception ex) {};
	    	}
	    }
	    wa.clear();
	    wa.addAll(result);
	    return wa;
	    
    }
    public Map sendMail(Set ids, Set emailAddresses, String subject, Description body, List entries) throws Exception {
    	User user = RequestContextHolder.getRequestContext().getUser();
		Set userIds = getProfileDao().explodeGroups(ids, user.getZoneId());
		//TODO is there accesschecking on seeing email address
		List users = getCoreDao().loadObjects(userIds, User.class, user.getZoneId());
		Set emailSet = new HashSet();
		List errors = new ArrayList();
		Map result = new HashMap();
		result.put(ObjectKeys.SENDMAIL_ERRORS, errors);
		//add email address listed 
		Object[] errorParams = new Object[3];
		for (Iterator iter=emailAddresses.iterator(); iter.hasNext();) {
			String e = (String)iter.next();
			if (!Validator.isNull(e)) {
				try {
					emailSet.add(new InternetAddress(e.trim()));
				} catch (Exception ex) {
					errorParams[0] = "";
					errorParams[1] = e;
					errorParams[2] = ex.getLocalizedMessage();
					errors.add(NLT.get("errorcode.badToAddress", errorParams));							
				}
			}
		}
		for (Iterator iter=users.iterator(); iter.hasNext();) {
			User e = (User)iter.next();
			try {
				emailSet.add(new InternetAddress(e.getEmailAddress().trim()));
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
			result.put(ObjectKeys.SENDMAIL_STATUS, Boolean.FALSE);
			return result;			
		}
    	Map message = new HashMap();
       	try {
    		message.put(SendEmail.FROM, new InternetAddress(user.getEmailAddress()));
    	} catch (Exception ex) {
			errorParams[0] = user.getTitle();
			errorParams[1] = user.getEmailAddress();
			errorParams[2] = ex.getLocalizedMessage();
			errors.add(0, NLT.get("errorcode.badFromAddress", errorParams));
			result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_FAILED);
			//cannot send without valid from address
			return result;
    	}
    	if (body.getFormat() == Description.FORMAT_HTML)
    		message.put(SendEmail.HTML_MSG, body.getText());
    	else 
       		message.put(SendEmail.TEXT_MSG, body.getText());
    		
    	message.put(SendEmail.SUBJECT, subject);
 		message.put(SendEmail.TO, emailSet);
 		if (entries != null) {
 			List attachments = new ArrayList();
 			for (int i=0; i<entries.size(); ++i) {
 				DefinableEntity entry = (DefinableEntity)entries.get(i);
 				attachments.addAll(entry.getFileAttachments());
 			}
 			message.put(SendEmail.ATTACHMENTS, attachments);
 		}
		
		boolean sent = getMailManager().sendMail(RequestContextHolder.getRequestContext().getZone(), message, user.getTitle() + " email");
		if (sent) result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_SENT);
		else result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_SCHEDULED);
		return result;
    }

	public void addZone(String name) {
		
		String adminName = SZoneConfig.getString(name, "property[@name='adminUser']", "admin");
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(name, adminName);
		try {
			Workspace top = new Workspace();
			top.setName(name);
			//temporary until have read id
			top.setZoneId(new Long(-1));
			top.setTitle(NLT.get("administration.initial.workspace.title", new Object[] {name}, name));
			top.setPathName("/"+top.getTitle());
			top.setInternalId(ObjectKeys.TOP_WORKSPACE_ID);
			getDefinitionModule().setDefaultBinderDefinition(top);
			//generate id for top and profiles
			getCoreDao().save(top);
			top.setZoneId(top.getId());
					
			ProfileBinder profiles = new ProfileBinder();
			profiles.setName("_profiles");
			profiles.setTitle(NLT.get("administration.initial.profile.title", "Users and Groups"));
			profiles.setPathName(top.getPathName() + "/" + profiles.getTitle());
			profiles.setZoneId(top.getId());
			profiles.setInternalId(ObjectKeys.PROFILE_ROOT_ID);
			getDefinitionModule().setDefaultBinderDefinition(profiles);
			top.addBinder(profiles);
			
			//generate id for top and profiles
			getCoreDao().save(profiles);
			getCoreDao().updateLibraryName(profiles, profiles, null, profiles.getTitle());
		
			//build user
			User user = new User();
			user.setName(adminName);
			user.setLastName(adminName);
			user.setForeignName(adminName);
			user.setZoneId(top.getId());
			user.setParentBinder(profiles);
			getDefinitionModule().setDefaultEntryDefinition(user);
			getCoreDao().save(user);
			

			//indexing needs the user
			RequestContextHolder.getRequestContext().setUser(user);

			//fill in timestampes
			HistoryStamp stamp = new HistoryStamp(user);
			top.setCreation(stamp);
			top.setModification(stamp);
			profiles.setCreation(stamp);
			profiles.setModification(stamp);
			user.setCreation(stamp);
			user.setModification(stamp);
			addAnnonymous(profiles, stamp);
			Group group = addAllUserGroup(profiles, stamp);
			
			addAdminRole(top, top, user);
			//all users are visitors
			addVisitorsRole(top, top, group);
			BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(top, top.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
			processor.indexBinder(top);
		
			processor = (BinderProcessor)getProcessorManager().getProcessor(profiles, profiles.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
			processor.indexBinder(profiles);
			//do now, with request context set
			IndexSynchronizationManager.applyChanges();
		} finally  {
			//leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);
		}

	}
	private Group addAllUserGroup(Binder parent, HistoryStamp stamp) {
		//build allUsers group
		Group group = new Group();
		group.setName("allUsers");
		group.setForeignName(group.getName());
		group.setTitle(NLT.get("administration.initial.group.alluser.title", group.getName()));
		group.setZoneId(parent.getZoneId());
		group.setParentBinder(parent);
		group.setInternalId(ObjectKeys.ALL_USERS_GROUP_ID);
		getDefinitionModule().setDefaultEntryDefinition(group);
		getCoreDao().save(group);
		group.setCreation(stamp);
		group.setModification(stamp);
		return group;
	}
	private User addAnnonymous(Binder parent, HistoryStamp stamp) {
		//build allUsers group
		User user = new User();
		user.setName("postingAgent");
		user.setForeignName(user.getName());
		user.setZoneId(parent.getZoneId());
		user.setParentBinder(parent);
		user.setInternalId(ObjectKeys.ANONYMOUS_POSTING_USER_ID);
		getDefinitionModule().setDefaultEntryDefinition(user);
		getCoreDao().save(user);
		user.setCreation(stamp);
		user.setModification(stamp);
		return user;
	}
	private void addAdminRole(Workspace top, WorkArea workArea, User user) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(NLT.get("administration.initial.function.admin", "Administrators"));
		for (Iterator iter = WorkAreaOperation.getWorkAreaOperations(); iter.hasNext();) {
			function.addOperation(((WorkAreaOperation)iter.next()));
		}	
		function.setInternalId(ObjectKeys.ADMINISTRATOR_ROLE_ID);
		//generate functionId
		getFunctionManager().addFunction(function);
	
		WorkAreaFunctionMembership ms = new WorkAreaFunctionMembership();
		ms.setWorkAreaId(workArea.getWorkAreaId());
		ms.setWorkAreaType(workArea.getWorkAreaType());
		ms.setZoneId(top.getId());
		ms.setFunctionId(function.getId());
		Set members = new HashSet();
		members.add(user.getId());
		ms.setMemberIds(members);
		getCoreDao().save(ms);
		
	}
	private void addVisitorsRole(Workspace top, WorkArea workArea, Group group) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(NLT.get("administration.initial.function.visitors", "Visitors"));

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		
		//generate functionId
		getFunctionManager().addFunction(function);
		
		WorkAreaFunctionMembership ms = new WorkAreaFunctionMembership();
		ms.setWorkAreaId(workArea.getWorkAreaId());
		ms.setWorkAreaType(workArea.getWorkAreaType());
		ms.setZoneId(top.getId());
		ms.setFunctionId(function.getId());
		Set members = new HashSet();
		members.add(group.getId());
		ms.setMemberIds(members);
		getCoreDao().save(ms);				
	}
	public void setZone1(String zoneName) {
		getCoreDao().executeUpdate("Update com.sitescape.ef.domain.FileAttachment set uniqueName='0' where uniqueName is null");	
		getCoreDao().executeUpdate("Update com.sitescape.ef.domain.BinderConfig set library='0' where library is null");	
		getCoreDao().executeUpdate("Update com.sitescape.ef.domain.Principal set logVersion=0 where logVersion is null");	
		getCoreDao().executeUpdate("Update com.sitescape.ef.domain.FolderEntry set logVersion=0 where logVersion is null");	
		getCoreDao().executeUpdate("Update com.sitescape.ef.domain.Binder set logVersion=0 where logVersion is null");	
		
	}
	public void setZone2(String zoneName) {
		Workspace zone = getCoreDao().findTopWorkspace(zoneName);
		int count = getCoreDao().countObjects(com.sitescape.ef.domain.LibraryEntry.class, new FilterControls("binderId", zone.getId()));
		if (count == 0) {
			List<Binder> binders = new ArrayList();
			binders.addAll(zone.getBinders());
			while (!binders.isEmpty()) {
				Binder b = binders.get(0);
				binders.remove(0);
				binders.addAll(b.getBinders());
				try {
					coreDao.registerLibraryEntry(b.getParentBinder(), null, b.getTitle());
				} catch (com.sitescape.ef.domain.TitleException te) {
					logger.error("Cannot register binder name: " + b.getPathName());
				}
			}
		}
		Definition fDef = getDefinitionModule().createDefaultDefinition(Definition.FOLDER_VIEW);
		Definition eDef = getDefinitionModule().createDefaultDefinition(Definition.FOLDER_ENTRY);
		List<com.sitescape.ef.domain.Folder> folders = getCoreDao().loadObjects("from com.sitescape.ef.domain.Folder where definitionType=9", null);
		//old file folders
		for (com.sitescape.ef.domain.Folder f: folders) {
			f.setEntryDef(fDef);
			f.setDefinitionType(fDef.getType());
			f.setLibrary(true);
			if (!f.isDefinitionsInherited()) {
				List ds = f.getDefinitions();
				ds.clear();
				ds.add(fDef);
				ds.add(eDef);
			}
			//get all attachments in this binder
		   	FilterControls filter = new FilterControls("owner.owningBinderId", f.getId());
	        List<com.sitescape.ef.domain.FileAttachment> atts = getCoreDao().loadObjects(com.sitescape.ef.domain.FileAttachment.class, filter);
	 
			for (com.sitescape.ef.domain.FileAttachment fa: atts) {
				if (!(fa instanceof com.sitescape.ef.domain.VersionAttachment)) {
					try {
						getCoreDao().registerLibraryEntry(f, fa.getOwner().getEntity(), fa.getFileItem().getName());
					} catch (com.sitescape.ef.domain.TitleException te) {
						logger.error("Cannot register attachment: " + f.getPathName() + " " + fa.getFileItem().getName());
					}
				}
			}
			
		}
		List<Definition> defs = getCoreDao().loadDefinitions(zone.getId(), 9);
		for (Definition def: defs) {
			getCoreDao().delete((Object)def);
		}
		defs = getCoreDao().loadDefinitions(zone.getId(), 10);
		getCoreDao().flush();
		for (Definition def: defs) {
			getCoreDao().executeUpdate("update com.sitescape.ef.domain.FolderEntry set entryDef='" +eDef.getId() + "',definitionType=" +
					Definition.FOLDER_ENTRY + " where entryDef='" + def.getId() + "'");
			getCoreDao().flush();
			getCoreDao().delete((Object)def);
		}	
		getCoreDao().executeUpdate("delete from com.sitescape.ef.domain.BinderConfig where definitionType=9");
	}
   public List getChanges(Long binderId, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("binderId", binderId);
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("entityId");
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   return getCoreDao().loadObjects(ChangeLog.class, filter); 
   }
   public List getChanges(Long entityId, String entityType, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("entityId", entityId);
	   filter.add("entityType", entityType);
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   return getCoreDao().loadObjects(ChangeLog.class, filter); 
   	
   }

}
