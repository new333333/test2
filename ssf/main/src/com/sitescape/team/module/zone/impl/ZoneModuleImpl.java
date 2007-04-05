package com.sitescape.team.module.zone.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoGroupByTheNameException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SessionUtil;
import com.sitescape.util.Validator;

public class ZoneModuleImpl extends CommonDependencyInjection implements ZoneModule,InitializingBean {
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
	protected AdminModule adminModule;
	/**
	 * Setup by spring
	 * @param adminModule
	 */
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	protected AdminModule getAdminModule() {
		return adminModule;
	}
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	private ProfileModule profileModule;	
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	private BinderModule binderModule;	
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	protected BinderModule getBinderModule() {
		return binderModule;
	}
	/**
     * Called after bean is initialized.  
     * Check on zones
     */
 	public void afterPropertiesSet() {
 		final List companies = getCoreDao().findCompanies();
 		//only execting one
 		if (companies.size() == 0) {
 			String zoneName = SZoneConfig.getDefaultZoneName();
 			IndexSynchronizationManager.begin();
			addZone(zoneName);
 		} else {
 			//make sure super user is set correctly
 			//there should not be any session opened, so auto-commit should be in effect
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		for (int i=0; i<companies.size(); ++i) {
	        			Workspace zone = (Workspace)companies.get(i);
	        			String superName = SZoneConfig.getString(zone.getName(), "property[@name='adminUser']", "admin");
	        			//	get super user from config file - must exist or throws and error
	        			User superU = getProfileDao().findUserByName(superName, zone.getName());
	        			if (!ObjectKeys.SUPER_USER_INTERNALID.equals(superU.getInternalId())) {
	        				superU.setInternalId(ObjectKeys.SUPER_USER_INTERNALID);
	        				//force update
	        				getCoreDao().merge(superU);				   
	        			}
	        			//make sure only one
	        			getCoreDao().executeUpdate(
	        					"update com.sitescape.team.domain.User set internalId=null where " +
	        					"internalId='" + ObjectKeys.SUPER_USER_INTERNALID + "' and not id=" + superU.getId());
	        			RequestContextUtil.setThreadContext(superU);
	        			//TODO: temporary to reload
	        			importDefaultDefs(zone.getName());
	        			//adds user to profileDao cache
	        			superU = getProfileDao().getReservedUser(ObjectKeys.SUPER_USER_INTERNALID, zone.getId());
	        			//make sure posting agent and background user exist
	        			try {
	        				getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, zone.getId());
	        			} catch (NoUserByTheNameException nu) {
	        				//need to add it
	        				addJobProcessor(superU.getParentBinder(), new HistoryStamp(superU));
	        				//updates cache
	        				getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, zone.getId());
	        			}
	        			//make sure posting agent and background user exist
	        			try {
	        				getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zone.getId());
	        			} catch (NoUserByTheNameException nu) {
	        				//need to add it
	        				addPosting(superU.getParentBinder(), new HistoryStamp(superU));
	        				//updates cache
	        				getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zone.getId());
	        			}
	        			//make sure allUsers exists
	        			try {
	        				getProfileDao().getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, zone.getId());
	        			} catch (NoGroupByTheNameException nu) {
	        				//need to add it
	        				addAllUserGroup(superU.getParentBinder(), new HistoryStamp(superU));
	        				//	updates cache
	        				getProfileDao().getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, zone.getId());
	        			}
	    	        }
		        	return null;
	        	}
       	   });
 			
	        }
 		RequestContextHolder.clear();
 		
 	}

	public void addZone(final String name) {
		
		final String adminName = SZoneConfig.getString(name, "property[@name='adminUser']", "admin");
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(name, adminName);
		boolean closeSession = false;
		try {
 			if (!SessionUtil.sessionActive()) {
 				SessionUtil.sessionStartup();	
 				closeSession = true;
 			}
    		final Workspace top = new Workspace();
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		top.setName(name);
	        		//temporary until have read id
	        		top.setZoneId(new Long(-1));
	        		top.setTitle(NLT.get("administration.initial.workspace.title", new Object[] {name}, name));
	        		top.setPathName("/"+top.getTitle());
	        		top.setInternalId(ObjectKeys.TOP_WORKSPACE_INTERNALID);
	        		//generate id for top and profiles
	        		getCoreDao().save(top);
	        		top.setZoneId(top.getId());
			
	        		ProfileBinder profiles = addPersonalRoot(top);
		
	        		//build user
	        		User user = new User();
	        		user.setName(adminName);
	        		user.setLastName(adminName);
	        		user.setForeignName(adminName);
	        		user.setZoneId(top.getId());
	        		user.setInternalId(ObjectKeys.SUPER_USER_INTERNALID);
	        		user.setParentBinder(profiles);
	        		getCoreDao().save(user);
			
	        		//indexing needs the user
	        		RequestContextHolder.getRequestContext().setUser(user);
	        		importDefaultDefs(name);

	        		//need request context
	        		getDefinitionModule().setDefaultBinderDefinition(top);
	        		getDefinitionModule().setDefaultBinderDefinition(profiles);
	        		getDefinitionModule().setDefaultEntryDefinition(user);
			
	        		//fill in timestampes
	        		HistoryStamp stamp = new HistoryStamp(user);
	        		top.setCreation(stamp);
	        		top.setModification(stamp);
	        		profiles.setCreation(stamp);
	        		profiles.setModification(stamp);
	        		user.setCreation(stamp);
	        		user.setModification(stamp);
			
	        		addPosting(profiles, stamp);
	        		addJobProcessor(profiles, stamp); 
	        		Group group = addAllUserGroup(profiles, stamp);
	        		addRoles(top, top, user, group);
	        		addGlobalRoot(top, stamp);		
	        		addTeamRoot(top, stamp);		
			
	        		//use module instead of processor directly so index synchronziation works correctly
	        		//index flushes entries from session - don't make changes without reload
	        		getBinderModule().indexTree(top.getId());
	        		//this will force the Ids to be cached 
	        		getProfileDao().getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, top.getId());
	        		getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, top.getId());
	        		//reload user as side effect after index flush
	        		user = getProfileDao().getReservedUser(ObjectKeys.SUPER_USER_INTERNALID, top.getId());
	        		getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, top.getId());

	        		//add user workspace	        		
	        		getProfileModule().addUserWorkspace(user);
	        		//do now, with request context set - won't have one if here on zone startup
	        		IndexSynchronizationManager.applyChanges();
	        		return null;
	        	}
	        });
		} finally  {
			if (closeSession) SessionUtil.sessionStop();
			//leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);
		}

	}
	private void importDefaultDefs(String zoneName) {
		//default definitions stored in separate config file
		String startupConfig = SZoneConfig.getString(zoneName, "property[@name='startupConfig']", "config/startup.xml");
		SAXReader reader = new SAXReader(false);  
		try {
			Document cfg = reader.read(new ClassPathResource(startupConfig).getInputStream());
			
			List elements = cfg.getRootElement().selectNodes("definitionFile");
			for (int i=0; i<elements.size(); ++i) {
				Element element = (Element)elements.get(i);
				String file = element.getTextTrim();
				reader = new SAXReader(false);  
				try {
					Document doc = reader.read(new ClassPathResource(file).getInputStream());
					getDefinitionModule().addDefinition(doc, true);
					//TODO:if support multiple zones, database and replyIds may have to be changed
				} catch (Exception ex) {
	        	logger.error("Cannot read definition from file: " + file);
				}
			}
			//TODO:: temp to reload everyone
			List templates = getAdminModule().getTemplates();
			if (!templates.isEmpty()) return;
			//Now setup configurations
			elements = cfg.getRootElement().selectNodes("template");
			for (int i=0; i<elements.size(); ++i) {
				Element element = (Element)elements.get(i);
				try {
					getAdminModule().addTemplate(element);
				} catch (Exception ex) {
					logger.error("Cannot add template:" + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot read startup configuration:" + ex.getMessage());
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
		group.setInternalId(ObjectKeys.ALL_USERS_GROUP_INTERNALID);
		getDefinitionModule().setDefaultEntryDefinition(group);
		getCoreDao().save(group);
		group.setCreation(stamp);
		group.setModification(stamp);
		return group;
	}
	private User addReservedUser(Binder parent, HistoryStamp stamp, String name, String title, String id) {
		
		User user = new User();
		user.setName(name);
		user.setForeignName(name);
		user.setLastName(title);
		user.setZoneId(parent.getZoneId());
		user.setParentBinder(parent);
		user.setInternalId(id);
		getDefinitionModule().setDefaultEntryDefinition(user);
		user.setCreation(stamp);
		user.setModification(stamp);
		getCoreDao().save(user);
		return user;
	}
	private User addPosting(Binder parent, HistoryStamp stamp) {
		return addReservedUser(parent, stamp, "_postingAgent", NLT.get("administration.initial.postingAgent.title"), ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID);
	}
	private User addJobProcessor(Binder parent, HistoryStamp stamp) {
		return addReservedUser(parent, stamp, "_jobProcessingAgent", NLT.get("administration.initial.jobProcessor.title"), ObjectKeys.JOB_PROCESSOR_INTERNALID);
	}
	private Workspace addTeamRoot(Workspace top, HistoryStamp stamp) {
		Workspace team = new Workspace();
		team.setCreation(stamp);
		team.setModification(stamp);
		team.setName("_teams");
		team.setTitle(NLT.get("administration.initial.team.title", "Teams"));
		team.setPathName(top.getPathName() + "/" + team.getTitle());
		team.setZoneId(top.getId());
		team.setInternalId(ObjectKeys.TEAM_ROOT_INTERNALID);
		getDefinitionModule().setDefaultBinderDefinition(team);
		top.addBinder(team);
		
		//generate id for top and profiles
		getCoreDao().save(team);
		getCoreDao().updateFileName(top, team, null, team.getTitle());
		return team;
		
	}
	private Workspace addGlobalRoot(Workspace top, HistoryStamp stamp) {
		Workspace global = new Workspace();
		global.setCreation(stamp);
		global.setModification(stamp);
		
		global.setName("_global");
		global.setTitle(NLT.get("administration.initial.global.title", "Global"));
		global.setPathName(top.getPathName() + "/" + global.getTitle());
		global.setZoneId(top.getId());
		global.setInternalId(ObjectKeys.GLOBAL_ROOT_INTERNALID);
		getDefinitionModule().setDefaultBinderDefinition(global);
		top.addBinder(global);
		
		//generate id for top and profiles
		getCoreDao().save(global);
		getCoreDao().updateFileName(top, global, null, global.getTitle());
		return global;
	}
	private ProfileBinder addPersonalRoot(Workspace top) {
		ProfileBinder profiles = new ProfileBinder();
		profiles.setName("_profiles");
		profiles.setTitle(NLT.get("administration.initial.profile.title", "Personal"));
		profiles.setPathName(top.getPathName() + "/" + profiles.getTitle());
		profiles.setZoneId(top.getId());
		profiles.setInternalId(ObjectKeys.PROFILE_ROOT_INTERNALID);
		top.addBinder(profiles);
		
		//generate id for top and profiles
		getCoreDao().save(profiles);
		getCoreDao().updateFileName(top, profiles, null, profiles.getTitle());
		return profiles;
	}
	private void addRoles(Workspace top, WorkArea workArea, User user, Group allGroup) {
		addVisitorsRole(top, workArea, allGroup);
		addParticipantsRole(top, workArea, allGroup);
		addTeamRole(top, workArea, allGroup);
		addBinderRole(top, workArea, user);
		addAdminRole(top, workArea, user);
	}
	private void addVisitorsRole(Workspace top, WorkArea workArea, Group group) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(NLT.get("administration.initial.function.visitor", "Visitor"));

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		function.addOperation(WorkAreaOperation.ADD_REPLIES);
//		function.addOperation(WorkAreaOperation.USER_SEE_ALL);
		
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
	private void addParticipantsRole(Workspace top, WorkArea workArea, Group group) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(NLT.get("administration.initial.function.particpant", "Participant"));

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATE_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATOR_MODIFY);
		function.addOperation(WorkAreaOperation.CREATOR_DELETE);
		function.addOperation(WorkAreaOperation.ADD_REPLIES);
//		function.addOperation(WorkAreaOperation.USER_SEE_ALL);
		
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
	private void addTeamRole(Workspace top, WorkArea workArea, Group group) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(NLT.get("administration.initial.function.teammember", "Team member"));

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATE_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATOR_MODIFY);
		function.addOperation(WorkAreaOperation.CREATOR_DELETE);
		function.addOperation(WorkAreaOperation.ADD_REPLIES);
//		function.addOperation(WorkAreaOperation.USER_SEE_ALL);
		function.addOperation(WorkAreaOperation.TEAM_MEMBER);
		function.addOperation(WorkAreaOperation.GENERATE_REPORTS);
		
		//generate functionId
		getFunctionManager().addFunction(function);
		
		WorkAreaFunctionMembership ms = new WorkAreaFunctionMembership();
		ms.setWorkAreaId(workArea.getWorkAreaId());
		ms.setWorkAreaType(workArea.getWorkAreaType());
		ms.setZoneId(top.getId());
		ms.setFunctionId(function.getId());
		Set members = new HashSet();
		members.add(group.getId());
		members.add(ObjectKeys.OWNER_USER_ID);
		ms.setMemberIds(members);
		getCoreDao().save(ms);				
	}
	private void addBinderRole(Workspace top, WorkArea workArea, User user) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(NLT.get("administration.initial.function.binderadmin", "Workspace and folder administration"));
		//add all them remove a few
		for (Iterator iter=WorkAreaOperation.getWorkAreaOperations(); iter.hasNext();) {
			function.addOperation((WorkAreaOperation)iter.next());			
		}
//		function.removeOperation(WorkAreaOperation.USER_SEE_COMMUNITY);
		function.removeOperation(WorkAreaOperation.SITE_ADMINISTRATION);
		
		//generate functionId
		getFunctionManager().addFunction(function);
		
		WorkAreaFunctionMembership ms = new WorkAreaFunctionMembership();
		ms.setWorkAreaId(workArea.getWorkAreaId());
		ms.setWorkAreaType(workArea.getWorkAreaType());
		ms.setZoneId(top.getId());
		ms.setFunctionId(function.getId());
		Set members = new HashSet();
		members.add(user.getId());
		members.add(ObjectKeys.OWNER_USER_ID);
		ms.setMemberIds(members);
		getCoreDao().save(ms);				
	}
	private void addAdminRole(Workspace top, WorkArea workArea, User user) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(NLT.get("administration.initial.function.siteadmin", "Site administration"));
		for (Iterator iter = WorkAreaOperation.getWorkAreaOperations(); iter.hasNext();) {
			function.addOperation(((WorkAreaOperation)iter.next()));
		}	
//		function.removeOperation(WorkAreaOperation.USER_SEE_COMMUNITY);
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


}
