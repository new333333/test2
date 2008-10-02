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
package com.sitescape.team.module.zone.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.ObjectControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.ApplicationGroup;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.LdapConnectionConfig;
import com.sitescape.team.domain.NoGroupByTheNameException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.WorkflowHistory;
import com.sitescape.team.domain.WorkflowStateHistory;
import com.sitescape.team.domain.ZoneConfig;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.jobs.ZoneSchedule;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.ldap.LdapModule;
import com.sitescape.team.module.ldap.LdapSchedule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.template.TemplateModule;
import com.sitescape.team.module.zone.ZoneException;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SessionUtil;
import com.sitescape.util.Validator;
public abstract class AbstractZoneModule extends CommonDependencyInjection implements ZoneModule,InitializingBean {
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
    protected TemplateModule templateModule;
    protected TemplateModule getTemplateModule() {
    	return templateModule;
    }
    public void setTemplateModule(TemplateModule templateModule) {
    	this.templateModule = templateModule;
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
	private LdapModule ldapModule;
	public void setLdapModule(LdapModule ldapModule) {
		this.ldapModule = ldapModule;
	}
	protected LdapModule getLdapModule() {
		return ldapModule;
	}
	protected List<ZoneSchedule> startupModules;
	public void setScheduleModules(List modules) {
		startupModules = modules;		
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
			final String zoneName = SZoneConfig.getDefaultZoneName();
			//only execting one
			if (companies.size() == 0) {
				addZone(zoneName, null);
 			} else {
        		for (int i=0; i<companies.size(); ++i) {
        			final Workspace zone = (Workspace)companies.get(i);
        			if (zone.getName().equals(zoneName)) {
        				getTransactionTemplate().execute(new TransactionCallback() {
        					public Object doInTransaction(TransactionStatus status) {
        						upgradeZoneTx(zone);
        						return null;
        					}
        				});
        			}
        		}
				//make sure zone is setup correctly
				getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		for (int i=0; i<companies.size(); ++i) {
	        			Workspace zone = (Workspace)companies.get(i);
	        			if (zone.isDeleted()) continue;
	        			validateZoneTx(zone);
	    	        }
		        	return null;
	        	}
				});
    			for (ZoneSchedule zoneM:startupModules) {
	        		for (int i=0; i<companies.size(); ++i) {
	        			Workspace zone = (Workspace)companies.get(i);
	        			zoneM.startScheduledJobs(zone);
	        		}
    			}
 			
			}
		} finally {
			if (closeSession) SessionUtil.sessionStop();
		}

 		RequestContextHolder.clear();
 		
 	}
 	
	public Long getZoneIdByZoneName(String zoneName) {
		Workspace top = getCoreDao().findTopWorkspace(zoneName);
		return top.getId();
	}
	public ZoneConfig getZoneConfig(Long zoneId) throws ZoneException {
		return getCoreDao().loadZoneConfig(zoneId);
	}
 	// Must be running inside a transaction set up by the caller 
	private ZoneConfig addZoneConfigTx(Workspace zone) {
		// Make sure there is a ZoneConfig; new for v2
		ZoneConfig zoneConfig = new ZoneConfig(zone.getId());
		if (SPropsUtil.getBoolean("smtp.service.enable")) zoneConfig.getMailConfig().setSimpleUrlPostingEnabled(true);		
		//keep current if was deleted
		ScheduleInfo notify = getAdminModule().getNotificationSchedule();
		zoneConfig.getMailConfig().setSendMailEnabled(notify.isEnabled());
		ScheduleInfo posting = getAdminModule().getPostingSchedule();
		zoneConfig.getMailConfig().setPostingEnabled(posting.isEnabled());
		getCoreDao().save(zoneConfig);
		return zoneConfig;
	}
 	protected void upgradeZoneTx(Workspace zone) {
 		Integer version = zone.getUpgradeVersion(); //in future release, start using version from zoneConfig
 		if ((version == null) || version.intValue() <= 1) {
 			String superName = SZoneConfig.getAdminUserName(zone.getName());
 			//	get super user from config file - must exist or throws and error
 			User superU = getProfileDao().findUserByName(superName, zone.getName());
 			RequestContextUtil.setThreadContext(superU).resolve();
 			//TODO: setZoneId as non=null, only do based on version
			getCoreDao().executeUpdate("update com.sitescape.team.domain.AuditTrail set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.Tag set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.WorkflowState set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.Event set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.Visits set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.Subscription set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.LibraryEntry set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.Dashboard set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.Attachment set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.SeenMap set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.WorkflowResponse set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.UserProperties set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.FolderEntry set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.CustomAttribute set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.PostingDef set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.Rating set zoneId=" + zone.getId() + 
				" where zoneId is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.TemplateBinder set name=templateTitle where parentBinder is null and (name is null or name='')");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.FolderEntry set subscribed=false where subscribed is null");
			getCoreDao().executeUpdate("update com.sitescape.team.domain.FolderEntry set subscribed=true where id in (select id.entityId from com.sitescape.team.domain.Subscription where id.entityType=6)");

			//fixup user emails
	 		SFQuery query=null;
	 		List batch = new ArrayList();
	 		// Load processor class
	 		try {
	 			Class processorClass = ReflectHelper.classForName(Principal.class.getName());
	 			Field fld = processorClass.getDeclaredField("emailAddress");
	 			fld.setAccessible(true);
	 			query = getProfileDao().queryAllPrincipals(new FilterControls(), zone.getId());
	      		while (query.hasNext()) {
	       			int count=0;
	       			batch.clear();
	       			// process 1000 entries
	       			while (query.hasNext() && (count < 1000)) {
	       				Object obj = query.next();
	       				if (obj instanceof Object[])
	       					obj = ((Object [])obj)[0];
	       				Principal principal = (Principal)obj;
	       		        String email = (String)fld.get(principal);
	       		        if (Validator.isNotNull(email)) {
	       					principal.setEmailAddress(Principal.PRIMARY_EMAIL, email);
	          		        fld.set(principal, null);
	       		        }
	       				++count;
	       				batch.add(principal);
	       			}
	       			//flush updates
	       			getCoreDao().flush();
	       			//flush cache
	       			for (int i=0; i<batch.size(); ++i) {
	       				getCoreDao().evict(batch.get(i));
	       			}
	      		}
	   			//flush updates
	   			getCoreDao().flush();
	   			//flush cache
	   			for (int i=0; i<batch.size(); ++i) {
	   				getCoreDao().evict(batch.get(i));
	   			}
	
			} catch (ClassNotFoundException cn) {
	 			//this cannot happen, can it?
				logger.error(cn);
			} catch (NoSuchFieldException nf) {
	 			//this cannot happen, can it?
				logger.error(nf);
			} catch (IllegalAccessException ia) {
	 			//this cannot happen, can it?
				logger.error(ia);
	 		} finally {
	 			if (query != null) query.close();
	 			query=null;
	 		}
			
	
			//fixup styles
	 		try {
				Class processorClass = ReflectHelper.classForName(Subscription.class.getName());
				Field fld = processorClass.getDeclaredField("style");
				fld.setAccessible(true);
				String [] styles = new String[] {Principal.PRIMARY_EMAIL};
	 			query = getCoreDao().queryObjects(new ObjectControls(Subscription.class), null, zone.getId());
	      		while (query.hasNext()) {
	       			int count=0;
	       			batch.clear();
	       			// process 1000 entries
	       			while (query.hasNext() && (count < 1000)) {
	       				Object obj = query.next();
	       				if (obj instanceof Object[])
	       					obj = ((Object [])obj)[0];
	       				Subscription sub = (Subscription)obj;
	     				int style = fld.getInt(sub);
	       				if (style != 0) {
	           				fld.setInt(sub, 0);
	       					sub.addStyle(style, styles);
	       				}
	       				++count;
	       				batch.add(sub);
	       			}
	       			//flush updates
	       			getCoreDao().flush();
	       			//flush cache
	       			for (int i=0; i<batch.size(); ++i) {
	       				getCoreDao().evict(batch.get(i));
	       			}
	      		}
	   			//flush updates
	   			getCoreDao().flush();
	   			//flush cache
	   			for (int i=0; i<batch.size(); ++i) {
	   				getCoreDao().evict(batch.get(i));
	   			}
	
			} catch (ClassNotFoundException cn) {
	 			//this cannot happen, can it?
				logger.error(cn);
			} catch (NoSuchFieldException nf) {
	 			//this cannot happen, can it?
				logger.error(nf);
			} catch (IllegalAccessException ia) {
	 			//this cannot happen, can it?
				logger.error(ia);
			} finally {
	 			if (query != null) query.close();
	 			query=null;
	 		}
			//move old workflowStateHistory rows to new table
	 		try {
	 			query = getCoreDao().queryObjects(new ObjectControls(WorkflowStateHistory.class), null, zone.getId());
	      		while (query.hasNext()) {
	       			int count=0;
	       			batch.clear();
	       			// process 1000 entries
	       			while (query.hasNext() && (count < 1000)) {
	       				Object obj = query.next();
	       				if (obj instanceof Object[])
	       					obj = ((Object [])obj)[0];
	       				WorkflowStateHistory old = (WorkflowStateHistory)obj;
	       				WorkflowHistory wfHistory = new WorkflowHistory(old);
	       				getCoreDao().replicate(wfHistory);
	       				++count;
	       				batch.add(old);
	       			}
	       			//flush updates
	       			getCoreDao().flush();
	       			//flush cache
       				getCoreDao().evict(batch);
	      		}
	   			//flush updates
	   			getCoreDao().flush();
	   			//flush cache
  				getCoreDao().evict(batch);
	
			} finally {
	 			if (query != null) query.close();
	 			query=null;
	 		}
			//remove the old history
			getCoreDao().executeUpdate("delete com.sitescape.team.domain.WorkflowStateHistory where zoneId=" + zone.getId());

			//create schedule first time through
	 		ZoneConfig zoneConfig = addZoneConfigTx(zone);
			ScheduleInfo notify = getAdminModule().getNotificationSchedule();
	 		notify.getSchedule().setDaily(true);
	 		notify.getSchedule().setHours("0");
	 		notify.getSchedule().setMinutes("15");
	 		notify.setEnabled(true);
			zoneConfig.getMailConfig().setSendMailEnabled(true);
			getAdminModule().setMailConfigAndSchedules(zoneConfig.getMailConfig(), notify, null);
			
			//If not configured yet,  check old config
			if (getCoreDao().loadObjects(LdapConnectionConfig.class, null, zone.getId()).isEmpty()) {				
				LdapSchedule schedule = getLdapModule().getLdapSchedule();
				LdapSchedule.LegacyLdapConfig oldConfig = new LdapSchedule.LegacyLdapConfig(schedule.getScheduleInfo().getDetails());
				//make sure was configured already
				if (schedule.isEnabled() || Validator.isNotNull(oldConfig.getUserUrl())) {
					//upgrade
					String url = oldConfig.getUserUrl();
					String userDn = "";
					int pos = url.lastIndexOf('/');
					if (pos > 8) {
						userDn = url.substring(pos+1, url.length());
						url = url.substring(0, url.lastIndexOf('/'));
					}
					String groupDn = oldConfig.getGroupsBasedn();
					if (Validator.isNull(groupDn)) groupDn = userDn;
					List<LdapConnectionConfig.SearchInfo> userSearch = new ArrayList();
					userSearch.add(new LdapConnectionConfig.SearchInfo(userDn, SZoneConfig.getString("ldapConfiguration/userFilter"), true));						

					List<LdapConnectionConfig.SearchInfo> groupSearch = new ArrayList();
					groupSearch.add(new LdapConnectionConfig.SearchInfo(groupDn, SZoneConfig.getString("ldapConfiguration/groupFilter"), true));
					String userId = oldConfig.getUserIdMapping();
					if (Validator.isNull(userId)) userId="uid";
					LdapConnectionConfig connection = new LdapConnectionConfig(url, userId, 
							oldConfig.getUserMappings(), userSearch, groupSearch, oldConfig.getUserPrincipal(), oldConfig.getUserCredential());
					connection.setPosition(0);
					connection.setZoneId(zone.getId());
					getCoreDao().save(connection);
				}
			}
			zone.setUpgradeVersion(2);
 		}

  	}
 	// Must be running inside a transaction set up by the caller 
 	protected void validateZoneTx(Workspace zone) {
 		String superName = SZoneConfig.getAdminUserName(zone.getName());
		//	get super user from config file - must exist or throws and error
		User superU = getProfileDao().findUserByName(superName, zone.getName());
		RequestContextUtil.setThreadContext(superU).resolve();
		if (!ObjectKeys.SUPER_USER_INTERNALID.equals(superU.getInternalId())) {
			superU.setInternalId(ObjectKeys.SUPER_USER_INTERNALID);
			//force update
			getCoreDao().merge(superU);	
			getProfileModule().indexEntry(superU);
		}
 		try {
 			getZoneConfig(zone.getId());
 		} catch (NoObjectByTheIdException zx) {
			// Make sure there is a ZoneConfig; new for v2
 			addZoneConfigTx(zone);
 		}
		//make sure only one
		getCoreDao().executeUpdate(
				"update com.sitescape.team.domain.User set internalId=null where " +
				"internalId='" + ObjectKeys.SUPER_USER_INTERNALID + 
				"' and zoneId=" + zone.getId() + 
				" and not id=" + superU.getId());
		//adds user to profileDao cache
		superU = getProfileDao().getReservedUser(ObjectKeys.SUPER_USER_INTERNALID, zone.getId());
		//make sure background user exists
		try {
			getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, zone.getId());
		} catch (NoUserByTheNameException nu) {
			//need to add it
			User u = addJobProcessor(superU.getParentBinder(), new HistoryStamp(superU));
			//updates cache
			getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, zone.getId());
			getProfileModule().indexEntry(u);
		}
		//make sure posting agent exists
		try {
			getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zone.getId());
		} catch (NoUserByTheNameException nu) {
			//need to add it
			User u = addPosting(superU.getParentBinder(), new HistoryStamp(superU));
			//updates cache
			getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zone.getId());
			getProfileModule().indexEntry(u);
		}
		//make sure guest exists
		User guest=null;
		try {
			guest = getProfileDao().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zone.getId());
			// Make sure guest has password.
			if(guest.getPassword() == null) {
				guest.setPassword(guest.getName());
				getCoreDao().merge(guest);
			}
		} catch (NoUserByTheNameException nu) {
			//need to add it
			guest = addGuest(superU.getParentBinder(), new HistoryStamp(superU));
			//	updates cache
			getProfileDao().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zone.getId());
			getProfileModule().indexEntry(guest);
		}
		if (guest.getWorkspaceId() == null) {
			Workspace guestWs = getProfileModule().addUserWorkspace(guest, null);
			getAdminModule().setWorkAreaOwner(guestWs, superU.getId(), true);
		}
		//make sure allUsers exists
		try {
			getProfileDao().getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, zone.getId());
		} catch (NoGroupByTheNameException nu) {
			//need to add it
			Group g = addAllUserGroup(superU.getParentBinder(), new HistoryStamp(superU));
			//	updates cache
			getProfileDao().getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, zone.getId());
			getProfileModule().indexEntry(g);
		}
		//make sure allApplications exists
		try {
			getProfileDao().getReservedApplicationGroup(ObjectKeys.ALL_APPLICATIONS_GROUP_INTERNALID, zone.getId());
		} catch (NoGroupByTheNameException nu) {
			//need to add it
			ApplicationGroup g = addAllApplicationGroup(superU.getParentBinder(), new HistoryStamp(superU));
			//	updates cache
			getProfileDao().getReservedApplicationGroup(ObjectKeys.ALL_APPLICATIONS_GROUP_INTERNALID, zone.getId());
			getProfileModule().indexEntry(g);
		}
	}

 	// Must be running inside a transaction set up by the caller
 	protected Workspace addZoneTx(String zoneName, String zoneAdminName, String virtualHost) {
       		Workspace top = new Workspace();
    		top.setName(zoneName);
    		//temporary until have read id
    		top.setZoneId(new Long(-1));
    		top.setTitle(NLT.get("administration.initial.workspace.title", new Object[] {zoneName}, zoneName));
    		top.setPathName("/"+top.getTitle());
    		top.setInternalId(ObjectKeys.TOP_WORKSPACE_INTERNALID);
    		top.setTeamMembershipInherited(false);
    		top.setFunctionMembershipInherited(false);
    		top.setDefinitionsInherited(false);
    		//generate id for top and profiles
    		getCoreDao().save(top);
    		top.setZoneId(top.getId());
    		top.setupRoot();
    		
 
    		// some piece of code needs zone id in the context
    		RequestContextHolder.getRequestContext().setZoneId(top.getId());   		
	
    		ProfileBinder profiles = addPersonalRoot(top);

    		//build user
    		User user = new User();
    		user.setName(zoneAdminName);
    		user.setPassword(zoneAdminName);
    		user.setLastName(zoneAdminName);
    		user.setForeignName(zoneAdminName);
    		user.setZoneId(top.getId());
    		user.setInternalId(ObjectKeys.SUPER_USER_INTERNALID);
    		user.setParentBinder(profiles);
    		getCoreDao().save(user);
    		//indexing and other modules needs the user
    		RequestContextHolder.getRequestContext().setUser(user).resolve();
    		//set zone info after context is set
			ZoneConfig zoneConfig = addZoneConfigTx(top);
    		HistoryStamp stamp = new HistoryStamp(user);
    		//add reserved group for use in import templates
    		Group group = addAllUserGroup(profiles, stamp);
    		ApplicationGroup applicationGroup = addAllApplicationGroup(profiles, stamp);
	
    		Function visitorsRole = addVisitorsRole(top);
    		Function participantsRole = addParticipantsRole(top);
    		Function guestParticipantRole = addGuestParticipantRole(top);
    		Function teamMemberRole = addTeamMemberRole(top);
    		Function binderRole = 	addBinderRole(top);
    		Function adminRole = addAdminRole(top);
    		Function teamWsRole = addTeamWorkspaceRole(top);
    		//make sure allusers group and roles are defined, may be referenced by templates
    		getAdminModule().updateDefaultDefinitions(top.getId());
    		getTemplateModule().updateDefaultTemplates(top.getId());

    		//Update after import of definitions
    		getDefinitionModule().setDefaultBinderDefinition(top);
    		getDefinitionModule().setDefaultBinderDefinition(profiles);
    		getDefinitionModule().setDefaultEntryDefinition(user);

    		//fill in config for profiles
    		List defs = profiles.getDefinitions();
    		defs.add(profiles.getEntryDef());
    		defs.add(user.getEntryDef());
    		
    		defs = top.getDefinitions();
    		defs.add(top.getEntryDef());
    			        		
    		//fill in timestampes
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
    		addGuest(profiles, stamp); 
    		Workspace globalRoot = addGlobalRoot(top, stamp);		
    		Workspace teamRoot = addTeamRoot(top, stamp);
    		teamRoot.setFunctionMembershipInherited(false);
    		
    		
    		//setup allUsers & allApplications access
    		List members = new ArrayList();
    		members.add(group.getId());
    		members.add(applicationGroup.getId());
    		//all users and all applications visitors at top
    		addMembership(top, visitorsRole, top, members);
    		// all users participants at top
    		members.remove(applicationGroup.getId());
    		addMembership(top, participantsRole, top, members);
    		// all users participants at teamroot
    		addMembership(top, participantsRole, teamRoot, members);
    		// all users createWs  at teamroot
    		addMembership(top, teamWsRole, teamRoot, members);
    		//add members to participants
    		members.clear();
    		members.add(ObjectKeys.TEAM_MEMBER_ID);
    		// all team members have team member role at top
    		addMembership(top, teamMemberRole, top, members);
    		// all team members have team member role at teamroot
    		addMembership(top, teamMemberRole, teamRoot, members);
    		
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
    		getProfileDao().getReservedApplicationGroup(ObjectKeys.ALL_APPLICATIONS_GROUP_INTERNALID, top.getId());

    		ScheduleInfo info = getAdminModule().getNotificationSchedule();
   			info.getSchedule().setDaily(true);
   			info.getSchedule().setHours("0");
   			info.getSchedule().setMinutes("15");
   			info.setEnabled(true);
			zoneConfig.getMailConfig().setSendMailEnabled(true);
			getAdminModule().setMailConfigAndSchedules(zoneConfig.getMailConfig(), info, null); 
    		return top;
 	}
 	
	protected Long addZone(final String name, final String virtualHost) {
		final String adminName = SZoneConfig.getAdminUserName(name);
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(name, adminName);
		try {
  	        Workspace zone =  (Workspace) getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	    			IndexSynchronizationManager.begin();

	        		Workspace zone = addZoneTx(name, adminName, virtualHost);
	        			        		
	    			User guest = getProfileDao().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zone.getId());
	    			Workspace guestWs = getProfileModule().addUserWorkspace(guest, null);
	        		//now change owner to admin
	        		getAdminModule().setWorkAreaOwner(guestWs, zone.getOwnerId() ,true);
	        		//do now, with request context set - won't have one if here on zone startup
	        		IndexSynchronizationManager.applyChanges();
	    		
	        		return zone;
	        	}
	        });
  	        //do outside of transaction, so commited.
  	        //otherwise jobs may start and fail cause data not saved.
        	for (ZoneSchedule zoneM:startupModules) {
				zoneM.startScheduledJobs(zone);
			}
        	return zone.getId();
		} finally  {
			//leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);
		}

	}
	
	/**
	 * Remove a zone deleteing all its contents.
	 * @param zoneName
	 */
	protected abstract void removeZone(String zoneName);

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
    private ApplicationGroup addAllApplicationGroup(Binder parent, HistoryStamp stamp) {
		//build allApplications group
		ApplicationGroup group = new ApplicationGroup();
		group.setName("allApplications");
		group.setForeignName(group.getName());
		group.setTitle(NLT.get("administration.initial.applicationgroup.allapplication.title", group.getName()));
		group.setZoneId(parent.getZoneId());
		group.setParentBinder(parent);
		group.setInternalId(ObjectKeys.ALL_APPLICATIONS_GROUP_INTERNALID);
		getDefinitionModule().setDefaultEntryDefinition(group);
		getCoreDao().save(group);
		group.setCreation(stamp);
		group.setModification(stamp);
		return group;
	}
	private User addReservedUser(Binder parent, HistoryStamp stamp, String name, String password, String title, String id) {
		
		User user = new User();
		user.setName(name);
		if(password != null) // optional field
			user.setPassword(password);
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
		return addReservedUser(parent, stamp, "_postingAgent", null, NLT.get("administration.initial.postingAgent.title"), ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID);
	}
	private User addJobProcessor(Binder parent, HistoryStamp stamp) {
		return addReservedUser(parent, stamp, "_jobProcessingAgent", null, NLT.get("administration.initial.jobProcessor.title"), ObjectKeys.JOB_PROCESSOR_INTERNALID);
	}
	private User addGuest(Binder parent, HistoryStamp stamp) {
		String guestName= SZoneConfig.getString(parent.getRoot().getName(), "property[@name='guestUser']", "guest");
		return addReservedUser(parent, stamp, guestName, guestName, NLT.get("administration.initial.guestTitle"), ObjectKeys.GUEST_USER_INTERNALID);
		
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
		team.setIconName("/icons/team_workspace.gif");
		List<Definition> defs = getCoreDao().loadDefinitions(new FilterControls("type", Definition.WORKSPACE_VIEW), top.getId());
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
		global.setIconName("/icons/workspace.gif");
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
		profiles.setIconName("/icons/workspace_personal.gif");
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

	private Function addGuestParticipantRole(Workspace top) {
		Function function = new Function();
		function.setZoneId(top.getId());
		function.setName(ObjectKeys.ROLE_TITLE_GUEST_PARTICIPANT);

		function.addOperation(WorkAreaOperation.READ_ENTRIES);
		function.addOperation(WorkAreaOperation.CREATE_ENTRIES);
		function.addOperation(WorkAreaOperation.ADD_REPLIES);
		
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
