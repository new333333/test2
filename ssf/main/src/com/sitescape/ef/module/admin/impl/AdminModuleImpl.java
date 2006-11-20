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
import java.util.Set;
import java.util.TreeSet;
import java.util.Collection;
import java.util.HashMap;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.InvalidArgumentException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EmailAlias;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.NoPrincipalByTheIdException;
import com.sitescape.ef.domain.NotificationDef;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.jobs.EmailNotification;
import com.sitescape.ef.jobs.EmailPosting;
import com.sitescape.ef.jobs.ScheduleInfo;
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
import com.sitescape.ef.security.function.WorkAreaFunctionMembershipExistsException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.util.SZoneConfig;

/**
 * @author Janet McCann
 *
 */
public class AdminModuleImpl extends CommonDependencyInjection implements AdminModule {
	private static final String[] defaultDefAttrs = new String[]{"internalId", "zoneName", "definitionType"};

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
	/**
     * Do actual work to either enable or disable email notification.
     * @param id
     * @param value
     */
    public void setEnableNotification(Long id, boolean value) {
        Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneName()); 
        checkBinderAdmin(binder); 
        //data is stored with job
   		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
   		process.enable(value, binder);
    }
    public ScheduleInfo getNotificationConfig(Long id) {
        Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneName()); 
        checkBinderAdmin(binder); 
        //data is stored with job
		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
  		return process.getScheduleInfo(binder);
    }
    
    public void setNotificationConfig(Long id, ScheduleInfo config) {
        Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneName()); 
        checkBinderAdmin(binder); 
       //data is stored with job
 		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
  		process.setScheduleInfo(config, binder);
    }
    /**
     * Set the notification definition for a folder.  
     * @param id
     * @param updates
     * @param principals - if null, don't change list.
     */
      public void modifyNotification(Long id, Map updates, Collection principals) 
    {
        Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneName()); 
        checkBinderAdmin(binder); 
    	NotificationDef current = binder.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		binder.setNotificationDef(current);
    	}
    	ObjectBuilder.updateObject(current, updates);
    	if (principals == null) return;
  		//	Pre-load for performance
    	List notifyUsers = new ArrayList();
    	getProfileDao().loadPrincipals(principals, binder.getZoneName());
   		for (Iterator iter=principals.iterator(); iter.hasNext();) {
   			//	make sure user exists and is in this zone
   			Principal p = getProfileDao().loadPrincipal((Long)iter.next(),binder.getZoneName());
   			notifyUsers.add(p);   			
   		}

   		current.setDistribution(notifyUsers);
    }
    public List getEmailAliases() {
    	return coreDao.loadEmailAliases(RequestContextHolder.getRequestContext().getZoneName());
    }
    public void modifyEmailAlias(String aliasId, Map updates) {
    	checkSiteAdmin();
    	EmailAlias alias = coreDao.loadEmailAlias(aliasId, RequestContextHolder.getRequestContext().getZoneName());
    	ObjectBuilder.updateObject(alias, updates);
    }
    public void addEmailAlias(Map updates) {
    	checkSiteAdmin();
    	String zoneName = RequestContextHolder.getRequestContext().getZoneName(); 
    	EmailAlias alias = new EmailAlias();
    	alias.setZoneName(zoneName);
       	ObjectBuilder.updateObject(alias, updates);
       	coreDao.save(alias);   	
    }
    public void deleteEmailAlias(String aliasId) {
    	checkSiteAdmin();
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
    	checkSiteAdmin();
       	getPostingObject().enable(enable, RequestContextHolder.getRequestContext().getZoneName());
    }
    public ScheduleInfo getPostingSchedule() {
    	checkSiteAdmin();
       	return getPostingObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneName());
    }
    public void setPostingSchedule(ScheduleInfo config) throws ParseException {
    	checkSiteAdmin();
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
    public void modifyPosting(Long binderId, String postingId, Map updates) {
    	//posting defs are defined by admin
    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
        checkBinderAdmin(binder); 
    	//Locate the posting
   		PostingDef post = binder.getPosting(postingId); 
    	if (post != null) ObjectBuilder.updateObject(post, updates);
    }
    public void addPosting(Long binderId, Map updates) {
    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
        checkBinderAdmin(binder); 
   		PostingDef post = new PostingDef();
   		post.setZoneName(binder.getZoneName());
		ObjectBuilder.updateObject(post, updates);
		binder.addPosting(post);
    }    	
    public void deletePosting(Long binderId, String postingId) {
    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
        checkBinderAdmin(binder); 
   		PostingDef post = binder.getPosting(postingId); 
    	if (post != null) binder.removePosting(post);
    }
 
	public BinderConfig createDefaultConfiguration(int type) {
		checkSiteAdmin();
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		String title=null;
		String internalId=null;
		switch (type) {
			case Definition.FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FOLDER_CONFIG, zoneName, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_folder";
				internalId = ObjectKeys.DEFAULT_FOLDER_CONFIG;
				break;
			}

			case Definition.WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_WORKSPACE_CONFIG, zoneName, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_workspace";
				internalId = ObjectKeys.DEFAULT_WORKSPACE_CONFIG;
				break;
			}
			case Definition.USER_WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG, zoneName, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_user_workspace";
				internalId = ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG;
				break;
			}
			case Definition.FILE_FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
					new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FILE_FOLDER_CONFIG, zoneName, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_file_folder";
				internalId = ObjectKeys.DEFAULT_FILE_FOLDER_CONFIG;
				break;
			}
		default: {
			throw new InvalidArgumentException("Invalid type:" + type);
			}
		}
		String id = addConfiguration(type, title);
		BinderConfig cfg = getCoreDao().loadConfiguration(id, zoneName);
		cfg.setInternalId(internalId);
		return cfg;

	}
	 public String addConfiguration(int type, String title) {
		checkSiteAdmin();
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
			case Definition.FILE_FOLDER_VIEW: {
				defs.add(getDefinitionModule().createDefaultDefinition(Definition.FILE_ENTRY_VIEW).getId());
				break;
			}
			default: {
				throw new InvalidArgumentException("Invalid type:" + type);
			}
		}
		
		config.setTitle(title);
		config.setZoneName(RequestContextHolder.getRequestContext().getZoneName());
		config.setDefinitionIds(defs);
		config.setDefinitionType(type);
		getCoreDao().save(config);
		return config.getId();
		
	}
	 public void deleteConfiguration(String id) {
		checkSiteAdmin();
		BinderConfig config = getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneName());
		getCoreDao().delete(config);
	}
	public void modifyConfiguration(String id, Map updates) {
		checkSiteAdmin();
		BinderConfig config = getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneName());
		ObjectBuilder.updateObject(config, updates);
	}
	public BinderConfig getConfiguration(String id) {
		checkSiteAdmin();
		return getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneName());
	}
	public List getConfigurations() {
		checkSiteAdmin();
		return getCoreDao().loadConfigurations(RequestContextHolder.getRequestContext().getZoneName());
	}
	public List getConfigurations(int type) {
		checkSiteAdmin();
		return getCoreDao().loadConfigurations( RequestContextHolder.getRequestContext().getZoneName(), type);
	}
	public void addFunction(Function function) {
		checkSiteAdmin();
		String zoneName  = RequestContextHolder.getRequestContext().getZoneName();
		function.setZoneName(zoneName);
		
		List zoneFunctions = functionManager.findFunctions(zoneName);
		if (zoneFunctions.contains(function)) {
			//Role already exists
			throw new FunctionExistsException(function.getName());
		}
		functionManager.addFunction(function);
	 
    }
    public void modifyFunction(Long id, Map updates) {
		checkSiteAdmin();		
		Function function = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneName(), id);
		functionManager.updateFunction(function);			
    }
    public void deleteFunction(Long id) {
		checkSiteAdmin();			
		Function f = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneName(), id);
		functionManager.deleteFunction(f);
    }
   public List getFunctions() {
		checkSiteAdmin();			
        return  functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneName());
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
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		
		List memberships = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(companyId, workArea);
		if (memberships.contains(membership)) {
			throw new WorkAreaFunctionMembershipExistsException(membership);
		}
        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
	}
	
	public void modifyWorkAreaFunctionMembership(WorkArea workArea, WorkAreaFunctionMembership membership) {
		
        //Check that this user is allowed to do this operation; 
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);   
        getWorkAreaFunctionMembershipManager().updateWorkAreaFunctionMembership(membership);
	}
	
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        

        WorkAreaFunctionMembership wfm = getWorkAreaFunctionMembership(workArea, functionId);
        if (wfm != null) {
	        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
        }
    }
    
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
        accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        

        return getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
       		(RequestContextHolder.getRequestContext().getZoneName(), workArea, functionId);
    }
    
	public List getWorkAreaFunctionMemberships(WorkArea workArea) {
		accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        

        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
        		RequestContextHolder.getRequestContext().getZoneName(), workArea);
	}

	public List getWorkAreaFunctionMembershipsInherited(WorkArea workArea) {
		accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return new ArrayList();
	    while (source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    }
 
        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneName(), source);
	}

	public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) 
    throws AccessControlException {
    	getAccessControlManager().checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL); 
    	if (inherit) {
    		//remove them
    	   	String companyId = RequestContextHolder.getRequestContext().getZoneName();
    	   	List current = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(companyId, workArea);
    	   	for (int i=0; i<current.size(); ++i) {
    	        WorkAreaFunctionMembership wfm = (WorkAreaFunctionMembership)current.get(i);
   		        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
    	   	}
    		
    	} else if (workArea.isFunctionMembershipInherited() && !inherit) {
    		//copy parent values as beginning values
    		List current = getWorkAreaFunctionMembershipsInherited(workArea);
    		for (int i=0; i<current.size(); ++i) {
    			WorkAreaFunctionMembership wf = (WorkAreaFunctionMembership)current.get(i);
    			addWorkAreaFunctionMembership(workArea, wf.getFunctionId(), new HashSet(wf.getMemberIds()));
    		}
    	}
    	workArea.setFunctionMembershipInherited(inherit);
    } 
	//return binders this user is a team_member of
	public List getTeamMemberships(Long userId) {
		//team membership is implemented by a reserved role
        User current = RequestContextHolder.getRequestContext().getUser();
        
        User user;
        if ((userId == null) || current.getId().equals(userId)) user = current;
        else user = getProfileDao().loadUser(userId, current.getZoneName());
        Function function = getFunctionManager().getReservedFunction(user.getZoneName(), ObjectKeys.TEAM_MEMBER_ROLE_ID);
		List<WorkAreaFunctionMembership> wfm = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(user.getZoneName(), getProfileDao().getPrincipalIds(user), function.getId());
	    Set ids = new HashSet();
	    for (WorkAreaFunctionMembership w: wfm) {
	    	ids.add(w.getWorkAreaId());
	    }
	    List wa = getCoreDao().loadObjects(ids, Binder.class, user.getZoneName());
	    
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

	public void addZone(String name) {
		
		String adminName = SZoneConfig.getString(name, "property[@name='adminUser']", "admin");
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(name, adminName);
		try {
			Workspace top = new Workspace();
			top.setName(name);
			top.setZoneName(name);
			top.setTitle(NLT.get("administration.initial.workspace.title", new Object[] {name}, name));
			top.setPathName("/"+top.getTitle());
			top.setInternalId(ObjectKeys.TOP_WORKSPACE_ID);
			getDefinitionModule().setDefaultBinderDefinition(top);
					
			ProfileBinder profiles = new ProfileBinder();
			profiles.setName("_profiles");
			profiles.setTitle(NLT.get("administration.initial.profile.title", "Users and Groups"));
			profiles.setZoneName(name);
			profiles.setInternalId(ObjectKeys.PROFILE_ROOT_ID);
			getDefinitionModule().setDefaultBinderDefinition(profiles);
			top.addBinder(profiles);
			
			//generate id for top and profiles
			getCoreDao().save(top);
		
			//build user
			User user = new User();
			user.setName(adminName);
			user.setLastName(adminName);
			user.setForeignName(adminName);
			user.setZoneName(name);
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
			Group group = addAllUserGroup(profiles, stamp);
			
			addAdminRole(name, top, user);
			addVisitorsRole(name, top, group);
			addTeamRole(name, top, user);
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
		group.setZoneName(parent.getZoneName());
		group.setParentBinder(parent);
		group.setInternalId(ObjectKeys.ALL_USERS_GROUP_ID);
		getDefinitionModule().setDefaultEntryDefinition(group);
		getCoreDao().save(group);
		group.setCreation(stamp);
		group.setModification(stamp);
		return group;
	}
	private void addAdminRole(String zoneName, WorkArea workArea, User user) {
		Function function = new Function();
		function.setZoneName(zoneName);
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
		ms.setZoneName(zoneName);
		ms.setFunctionId(function.getId());
		Set members = new HashSet();
		members.add(user.getId());
		ms.setMemberIds(members);
		getCoreDao().save(ms);
		
	}
	private void addVisitorsRole(String zoneName, WorkArea workArea, Group group) {
		Function function = new Function();
		function.setZoneName(zoneName);
		function.setName(NLT.get("administration.initial.function.visitors", "Visitors"));

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		
		//generate functionId
		getFunctionManager().addFunction(function);
		
		WorkAreaFunctionMembership ms = new WorkAreaFunctionMembership();
		ms.setWorkAreaId(workArea.getWorkAreaId());
		ms.setWorkAreaType(workArea.getWorkAreaType());
		ms.setZoneName(zoneName);
		ms.setFunctionId(function.getId());
		Set members = new HashSet();
		members.add(group.getId());
		ms.setMemberIds(members);
		getCoreDao().save(ms);				
	}
	private void addTeamRole(String zoneName, WorkArea workArea, User user) {
		Function function = new Function();
		function.setZoneName(zoneName);
		function.setName(NLT.get("administration.initial.function.team_member", "Team Members"));
		function.setInternalId(ObjectKeys.TEAM_MEMBER_ROLE_ID);

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		function.addOperation(WorkAreaOperation.MODIFY_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATE_ENTRIES);
		function.addOperation(WorkAreaOperation.ADD_REPLIES);
		
		//generate functionId
		getFunctionManager().addFunction(function);
		
		WorkAreaFunctionMembership ms = new WorkAreaFunctionMembership();
		ms.setWorkAreaId(workArea.getWorkAreaId());
		ms.setWorkAreaType(workArea.getWorkAreaType());
		ms.setZoneName(zoneName);
		ms.setFunctionId(function.getId());
		Set members = new HashSet();
		members.add(user.getId());
		ms.setMemberIds(members);
		getCoreDao().save(ms);		
	}
	private void checkSiteAdmin() {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		Binder top = getCoreDao().findTopWorkspace(zoneName);
        getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);        		
	}
	private void checkBinderAdmin(Binder binder) {
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION); 	       		
	}
	public void setZone() {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		Map params = new HashMap();
		params.put("zoneName", zoneName);
		List result = coreDao.loadObjects("from com.sitescape.ef.domain.Binder where parentBinder is null and zoneName=:zoneName", params);
		//has to exist
		Binder ws = (Binder)result.get(0);
		if (!ObjectKeys.TOP_WORKSPACE_ID.equals(ws.getInternalId())) ws.setInternalId(ObjectKeys.TOP_WORKSPACE_ID);
		if (ws.getEntryDef() == null) getDefinitionModule().setDefaultBinderDefinition(ws);
		params.put("name", "_profiles");
		result = coreDao.loadObjects("from com.sitescape.ef.domain.Binder where name=:name and zoneName=:zoneName", params);
		ProfileBinder profiles = (ProfileBinder)result.get(0);
		if (!ObjectKeys.PROFILE_ROOT_ID.equals(profiles.getInternalId())) profiles.setInternalId(ObjectKeys.PROFILE_ROOT_ID);
		if (profiles.getEntryDef() == null) getDefinitionModule().setDefaultBinderDefinition(profiles);
		try {
			Group g = getProfileDao().getReservedGroup(ObjectKeys.ALL_USERS_GROUP_ID, zoneName);
		} catch (com.sitescape.ef.domain.NoGroupByTheNameException ng) {
			addAllUserGroup(profiles, new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
		}

	}
}
