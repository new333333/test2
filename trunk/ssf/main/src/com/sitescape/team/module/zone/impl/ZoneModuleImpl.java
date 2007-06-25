/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.zone.impl;

import java.util.ArrayList;
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
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SessionUtil;

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
		boolean closeSession = false;
		if (!SessionUtil.sessionActive()) {
			SessionUtil.sessionStartup();	
			closeSession = true;
		}
		try {
			final List companies = getCoreDao().findCompanies();
			//only execting one
			if (companies.size() == 0) {
				String zoneName = SZoneConfig.getDefaultZoneName();
				addZone(zoneName);
			} else {
				//make sure zone is setup correctly
				getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		for (int i=0; i<companies.size(); ++i) {
	        			Workspace zone = (Workspace)companies.get(i);
	        			String superName = SZoneConfig.getString(zone.getName(), "property[@name='adminUser']", "administrator");
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
		} finally {
			if (closeSession) SessionUtil.sessionStop();
		}

 		RequestContextHolder.clear();
 		
 	}

	public void addZone(final String name) {
		final String adminName = SZoneConfig.getString(name, "property[@name='adminUser']", "admin");
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(name, adminName);
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
    	try {
    		luceneSession.clearIndex();
    	} finally {
    		luceneSession.close();
    	}
		try {
  			IndexSynchronizationManager.begin();

 	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	           		Workspace top = new Workspace();
	        		top.setName(name);
	        		//temporary until have read id
	        		top.setZoneId(new Long(-1));
	        		top.setTitle(NLT.get("administration.initial.workspace.title", new Object[] {name}, name));
	        		top.setPathName("/"+top.getTitle());
	        		top.setInternalId(ObjectKeys.TOP_WORKSPACE_INTERNALID);
	        		top.setTeamMembershipInherited(false);
	        		top.setFunctionMembershipInherited(false);
	        		top.setDefinitionsInherited(false);
	        		//generate id for top and profiles
	        		getCoreDao().save(top);
	        		top.setZoneId(top.getId());
	        		top.setupRoot();
			
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
	        		Function visitorsRole = addVisitorsRole(top);
	        		Function participantsRole = addParticipantsRole(top);
	        		Function teamMemberRole = addTeamMemberRole(top);
	        		Function binderRole = 	addBinderRole(top);
	        		Function adminRole = addAdminRole(top);
	        		Function teamWsRole = addTeamWorkspaceRole(top);

	        		importDefaultDefs(name);

	        		//need request context
	        		getDefinitionModule().setDefaultBinderDefinition(top);
	        		getDefinitionModule().setDefaultBinderDefinition(profiles);
	        		getDefinitionModule().setDefaultEntryDefinition(user);

	        		//fill in config form profiles
	        		List defs = profiles.getDefinitions();
	        		defs.add(profiles.getEntryDef());
	        		defs.add(user.getEntryDef());
	        		
	        		defs = top.getDefinitions();
	        		defs.add(top.getEntryDef());
	        		
	        		
	        		//fill in timestampes
	        		HistoryStamp stamp = new HistoryStamp(user);
	        		top.setCreation(stamp);
	        		top.setModification(stamp);
	        		profiles.setCreation(stamp);
	        		profiles.setModification(stamp);
	        		user.setCreation(stamp);
	        		user.setModification(stamp);
	        		//flush these changes, other reads may re-load
	        		getCoreDao().flush();
			
	        		addPosting(profiles, stamp);
	        		addJobProcessor(profiles, stamp); 
	        		Group group = addAllUserGroup(profiles, stamp);
	        		Workspace globalRoot = addGlobalRoot(top, stamp);		
	        		Workspace teamRoot = addTeamRoot(top, stamp);
	        		teamRoot.setFunctionMembershipInherited(false);
	        		
	        		
	        		//setup allUsers access
	        		List members = new ArrayList();
	        		members.add(group.getId());
	        		
	        		addMembership(top, visitorsRole, top, members);
	        		addMembership(top, participantsRole, teamRoot, members);
	        		addMembership(top, teamWsRole, teamRoot, members);
	        		//add members to participants
	        		members.add(ObjectKeys.TEAM_MEMBER_ID);
	        		addMembership(top, participantsRole, top, members);
	        		addMembership(top, participantsRole, teamRoot, members);
	        		
	        		members.clear();
	        		members.add(ObjectKeys.OWNER_USER_ID);
	        		addMembership(top, binderRole, top, members);
	        		addMembership(top, binderRole, teamRoot, members);
			
	        		members.clear();
	        		members.add(user.getId());
	        		addMembership(top, adminRole, top, members);
	        		//use module instead of processor directly so index synchronziation works correctly
	        		//index flushes entries from session - don't make changes without reload
	        		getBinderModule().indexTree(top.getId());
	        		//this will force the Ids to be cached 
	        		getProfileDao().getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, top.getId());
	        		getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, top.getId());
	        		//reload user as side effect after index flush
	        		user = getProfileDao().getReservedUser(ObjectKeys.SUPER_USER_INTERNALID, top.getId());
	        		getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, top.getId());

	        		//do now, with request context set - won't have one if here on zone startup
	        		IndexSynchronizationManager.applyChanges();
	        		return null;
	        	}
	        });
		} finally  {
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
			//Now setup configurations
			elements = cfg.getRootElement().selectNodes("templateFile");
			for (int i=0; i<elements.size(); ++i) {
				Element element = (Element)elements.get(i);
				String file = element.getTextTrim();
				reader = new SAXReader(false);  
				try {
					Document doc = reader.read(new ClassPathResource(file).getInputStream());
					getAdminModule().addTemplate(doc);
					//TODO:if support multiple zones, database and replyIds may have to be changed
				} catch (Exception ex) {
					logger.error("Cannot add template:", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot read startup configuration:", ex);
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
		team.setIconName("icons/team_workspace.gif");
		List<Definition> defs = getCoreDao().loadDefinitions(top.getId(), Definition.WORKSPACE_VIEW);
		//find the definition for a root team space
		Definition teamDef=null;
		for (Definition d:defs) {
			String viewType = DefinitionUtils.getViewType(d.getDefinition());
			if (!Definition.VIEW_STYLE_TEAM_ROOT.equals(viewType)) continue;
			teamDef = d;
			break;
		}
		if (teamDef == null) getDefinitionModule().setDefaultBinderDefinition(team);
		else team.setEntryDef(teamDef);
		top.addBinder(team);
		team.setDefinitionsInherited(false);
		defs = team.getDefinitions();
		defs.add(team.getEntryDef());
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
		global.setIconName("icons/workspace.gif");
		getDefinitionModule().setDefaultBinderDefinition(global);
		top.addBinder(global);
		global.setDefinitionsInherited(false);
		List defs = global.getDefinitions();
		defs.add(global.getEntryDef());
		
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
		profiles.setIconName("icons/workspace_personal.gif");
		top.addBinder(profiles);
		profiles.setDefinitionsInherited(false);
		//generate id for top and profiles
		getCoreDao().save(profiles);
		getCoreDao().updateFileName(top, profiles, null, profiles.getTitle());
		return profiles;
	}
	private Function addVisitorsRole(Workspace top) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(ObjectKeys.ROLE_TITLE_VISITOR);

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		function.addOperation(WorkAreaOperation.ADD_REPLIES);
		
		//generate functionId
		getFunctionManager().addFunction(function);		
		return function;
	}
	private Function addParticipantsRole(Workspace top) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(ObjectKeys.ROLE_TITLE_PARTICIPANT);

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATE_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATOR_MODIFY);
		function.addOperation(WorkAreaOperation.CREATOR_DELETE);
		function.addOperation(WorkAreaOperation.ADD_REPLIES);
//		function.addOperation(WorkAreaOperation.USER_SEE_ALL);
		
		//generate functionId
		getFunctionManager().addFunction(function);
		return function;
	}

	private Function addTeamMemberRole(Workspace top) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(ObjectKeys.ROLE_TITLE_TEAM_MEMBER);
		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATE_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATOR_MODIFY);
		function.addOperation(WorkAreaOperation.CREATOR_DELETE);
		function.addOperation(WorkAreaOperation.ADD_REPLIES);
		function.addOperation(WorkAreaOperation.ADD_COMMUNITY_TAGS);
		function.addOperation(WorkAreaOperation.CREATE_FOLDERS);
		function.addOperation(WorkAreaOperation.CREATE_WORKSPACES);
		function.addOperation(WorkAreaOperation.DELETE_ENTRIES);
		function.addOperation(WorkAreaOperation.MODIFY_ENTRIES);
		function.addOperation(WorkAreaOperation.GENERATE_REPORTS);

		//generate functionId
		getFunctionManager().addFunction(function);
		return function;
	}
	private Function addBinderRole(Workspace top) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(ObjectKeys.ROLE_TITLE_BINDER_ADMIN);
		//add all them remove a few
		for (Iterator iter=WorkAreaOperation.getWorkAreaOperations(); iter.hasNext();) {
			function.addOperation((WorkAreaOperation)iter.next());			
		}
//		function.removeOperation(WorkAreaOperation.USER_SEE_COMMUNITY);
		function.removeOperation(WorkAreaOperation.SITE_ADMINISTRATION);
		
		//generate functionId
		getFunctionManager().addFunction(function);
		return function;
	}
	private Function addAdminRole(Workspace top) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(ObjectKeys.ROLE_TITLE_SITE_ADMIN);
		for (Iterator iter = WorkAreaOperation.getWorkAreaOperations(); iter.hasNext();) {
			function.addOperation(((WorkAreaOperation)iter.next()));
		}	
//		function.removeOperation(WorkAreaOperation.USER_SEE_COMMUNITY);
		//generate functionId
		getFunctionManager().addFunction(function);
		return function;
	}
	private Function addTeamWorkspaceRole(Workspace top) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(ObjectKeys.ROLE_TITLE_WORKSPACE_CREATOR);
		function.addOperation(WorkAreaOperation.CREATE_WORKSPACES);
		//generate functionId
		getFunctionManager().addFunction(function);
		return function;
	}
	private void addMembership(Workspace top, Function function, WorkArea workArea, List ids) {
		WorkAreaFunctionMembership ms = new WorkAreaFunctionMembership();
		ms.setWorkAreaId(workArea.getWorkAreaId());
		ms.setWorkAreaType(workArea.getWorkAreaType());
		ms.setZoneId(top.getId());
		ms.setFunctionId(function.getId());
		Set members = new HashSet(ids);
		ms.setMemberIds(members);
		getCoreDao().save(ms);
		
	}


}
