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

package org.kablink.teaming.module.ldap.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.hibernate.SessionFactory;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.Membership;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.jobs.LdapSynchronization;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.CollectionUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.util.GetterUtil;
import org.kablink.util.Validator;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This implementing class utilizes transactional demarcation strategies that 
 * are finer granularity than typical module implementations, in an effort to
 * avoid lengthy transaction duration that could have occured if the ldap
 * database is large and many updates are made during the transaction. To support that,
 * the public methods exposed by this implementation are not transaction 
 * demarcated. Instead, this implementation uses helper methods 
 * for the part of the operations that might
 * require interaction with the database (that is, those operations that 
 * change the state of one or more domain objects) and put the transactional 
 * support around those methods hence reducing individual transaction duration.
 * Of course, this finer granularity transactional control will be of no effect
 * if the caller of this service was already transactional (i.e., it controls
 * transaction boundary that is more coarse). Whenever possible, this practise 
 * is discouraged for obvious performance/scalability reasons.  
 *   
 * @author Janet McCann
 *
 */
public class LdapModuleImpl extends CommonDependencyInjection implements LdapModule {
	protected Log logger = LogFactory.getLog(getClass());
	protected String [] principalAttrs = new String[]{ObjectKeys.FIELD_PRINCIPAL_NAME, ObjectKeys.FIELD_ID, ObjectKeys.FIELD_PRINCIPAL_DISABLED, 
			ObjectKeys.FIELD_INTERNALID, ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME};

	protected static final String[] sample = new String[0];
	HashMap defaultProps = new HashMap(); 
	protected HashMap zones = new HashMap();
	protected TransactionTemplate transactionTemplate;
	protected ProfileModule profileModule;
	protected DefinitionModule definitionModule;
	protected SessionFactory sessionFactory;
	protected static String GROUP_ATTRIBUTES="groupAttributes";
	protected static String MEMBER_ATTRIBUTES="memberAttributes";
	protected static String SYNC_JOB="ldap.job"; //properties in xml file need a unique name
	public LdapModuleImpl () {
		defaultProps.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		defaultProps.put(Context.SECURITY_AUTHENTICATION, "simple");
	}
	
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	public DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public boolean testAccess(LdapOperation operation) {
		try {
			checkAccess(operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	protected void checkAccess(LdapOperation operation) {
		getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
	}
	protected String getLdapProperty(String zoneName, String name) {
		String val = SZoneConfig.getString(zoneName, "ldapConfiguration/property[@name='" + name + "']");
		if (Validator.isNull(val)) {
			val = (String)defaultProps.get(name);
		}
		return val;
	}
	/**
	 * Get the ldap configuration.  This object is stored in the scheduling database.  
	 */
	public LdapSchedule getLdapSchedule() {		
		checkAccess(LdapOperation.manageLdap);
		LdapSchedule cfg = new LdapSchedule(getSyncObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId()));
		return cfg;
	}
	/**
	 * Update ldap configuration with scheduler. Only properties specified in the props file will
	 * be modified.
	 * @param zoneId
	 * @param props
	 */
	public void setLdapSchedule(LdapSchedule schedule) {
		checkAccess(LdapOperation.manageLdap);
		getSyncObject().setScheduleInfo(schedule.getScheduleInfo());
	}
    protected LdapSynchronization getSyncObject() {
    	String jobClass = getLdapProperty(RequestContextHolder.getRequestContext().getZoneName(), SYNC_JOB);
       	if (Validator.isNotNull(jobClass)) {
    		try {
    			return (LdapSynchronization)ReflectHelper.getInstance(jobClass);
    		} catch (Exception e) {
 			   logger.error("Cannot instantiate LdapSynchronization custom class", e);
    		}
    	}
    	return (LdapSynchronization)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultLdapSynchronization.class);		   		
 
    }	
	
    protected Map getZoneMap(String zoneName)
    {
    	if(zones.containsKey(zoneName)) { return (Map) zones.get(zoneName); }
    	Map zone = new HashMap();
    	List memberAttributes = new ArrayList();
    	List classes = SZoneConfig.getElements("ldapConfiguration/groupMapping/memberAttribute");
    	for(int i=0; i < classes.size(); i++) {
    		Element next = (Element) classes.get(i);
    		memberAttributes.add(next.getTextTrim());
    	}
    	zone.put(MEMBER_ATTRIBUTES, memberAttributes);
    	
    	Map attributes = new LinkedHashMap();
    	List mappings  = SZoneConfig.getElements("ldapConfiguration/groupMapping/mapping");
    	for(int i=0; i < mappings.size(); i++) {
    		Element next = (Element) mappings.get(i);
    		attributes.put(next.attributeValue("from"), next.attributeValue("to"));
    	}
    	zone.put(GROUP_ATTRIBUTES, attributes);
    	
    	zones.put(zoneName, zone);
    	return zone;
    }

	/**
	 * Update a ssf user with an ldap person.  
	 * @param zoneName
	 * @param loginName
	 * @throws NoUserByTheNameException
	 * @throws NamingException
	 */
	public void syncUser(Long userId) 
		throws NoUserByTheNameException, NamingException {
		Workspace zone = RequestContextHolder.getRequestContext().getZone();

		LdapSchedule schedule = new LdapSchedule(getSyncObject().getScheduleInfo(zone.getId()));
		User user = getProfileDao().loadUser(userId, schedule.getScheduleInfo().getZoneId());
		Map mods = new HashMap();
		for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs(zone.getZoneId())) {
			LdapContext ctx = getUserContext(zone.getId(), config);
			String dn=null;
			Map userAttributes = config.getMappings();
			String [] userAttributeNames = 	(String[])(userAttributes.keySet().toArray(sample));
	
			for(LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches()) {
				try {
					int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
					SearchControls sch = new SearchControls(scope, 1, 0, userAttributeNames, false, false);
		
					String search = "(" + config.getUserIdAttribute() + "=" + user.getName() + ")";
					String filter = searchInfo.getFilter();
					if(!Validator.isNull(filter)) {
						search = "(&"+search+filter+")";
					}
					NamingEnumeration ctxSearch = ctx.search(searchInfo.getBaseDn(), search, sch);
					if (!ctxSearch.hasMore()) {
						continue;
					}
					Binding bd = (Binding)ctxSearch.next();
					getUpdates(userAttributeNames, userAttributes,  ctx.getAttributes(bd.getNameInNamespace()), mods);
					if (bd.isRelative() && Validator.isNotNull(ctx.getNameInNamespace())) {
						dn = bd.getNameInNamespace() + "," + ctx.getNameInNamespace();
					} else {
						dn = bd.getNameInNamespace();
					}
					mods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
				} finally {
					ctx.close();
				}
				updateUser(zone, user.getName(), mods);
				return;
			}
		}
		throw new NoUserByTheNameException(user.getName());
	}
	
	/**
	 * This routine alters group membership without updateing the local caches.
	 * Need to flush cache after use
	 */
	public void syncAll() throws NamingException {
		Workspace zone = RequestContextHolder.getRequestContext().getZone();
		LdapSchedule info = new LdapSchedule(getSyncObject().getScheduleInfo(zone.getId()));
    		UserCoordinator userCoordinator = new UserCoordinator(zone,info.isUserSync(),info.isUserRegister(),
   															  info.isUserDelete(), info.isUserWorkspaceDelete());
		for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs(zone.getId())) {
	   		LdapContext ctx=null;
	  		try {
				ctx = getUserContext(zone.getId(), config);
				logger.info("InitialContext: " + ctx.getNameInNamespace());
				syncUsers(zone, ctx, config, userCoordinator);
			} finally {
				if (ctx != null) {
					ctx.close();			
				}
			}
		}
		Map dnUsers = userCoordinator.wrapUp();

   		GroupCoordinator groupCoordinator = new GroupCoordinator(zone, dnUsers, info.isGroupSync(), info.isGroupRegister(), info.isGroupDelete());
   		for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs(zone.getId())) {
	   		LdapContext ctx=null;
	  		try {
				ctx = getGroupContext(zone.getId(), config);
				logger.info("InitialContext: " + ctx.getNameInNamespace());
				syncGroups(zone, ctx, config, groupCoordinator, info.isMembershipSync());
			} finally {
				if (ctx != null) {
					ctx.close();			
				}
							
			}
		}
   		groupCoordinator.deleteObsoleteGroups();
   		
	}

	
	class UserCoordinator
	{
		Map<String, Object[]> ssUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		Map<String, Object[]> ssDnUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		Map<Long, String> notInLdap = new TreeMap();
		Map<Long, Map> ldap_existing = new HashMap();
		Map<String, Map> ldap_new = new HashMap();
		Map<String, Object[]> dnUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		//Keep names that have been processed.  Use case_insensitive match cause 
		//mysql treats them as equal
		//This will catch 2 ldap names that differ by case
		Map<String, String> foundNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		Map userAttributes;
		String [] userAttributeNames;
		Long zoneId;
		boolean sync;
		boolean create;
		boolean delete;
		boolean deleteWorkspace;
		
		long createSyncSize;
		long modifySyncSize;

		public UserCoordinator(Binder zone, boolean sync, boolean create, boolean delete, boolean deleteWorkspace)
		{
			this.zoneId = zone.getId();
			this.sync = sync;
			this.create = create;
			this.delete = delete;
			this.deleteWorkspace = deleteWorkspace;
			
			createSyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "create.flush.threshhold"), 100);
			modifySyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "modify.flush.threshhold"), 100);

			//get list of users in this zone and not deleted
			List attrs = coreDao.loadObjects(new ObjectControls(User.class, principalAttrs), 
					new FilterControls(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE), zoneId);
			//convert list of objects to a Map of forumNames 
			for (int i=0; i<attrs.size(); ++i) {
				Object[] row = (Object [])attrs.get(i);
				String ssName = (String)row[0];
				//map existing names to row
				ssUsers.put(ssName, row);
				//map existing DN to row
				ssDnUsers.put((String)row[4], row);
				//initialize all users as not found unless already disabled or reserved
				if (((Boolean)row[2] == Boolean.FALSE) && (Validator.isNull((String)row[3]))) {
					notInLdap.put((Long)row[1], ssName);
				}
			}			
		}
		
		public void setAttributes(Map userAttributes)
		{
			this.userAttributes = userAttributes;
			this.userAttributeNames = 	(String[])(userAttributes.keySet().toArray(sample));
		}

		public boolean isDuplicate(String dn)
		{
			return dnUsers.containsKey(dn);
		}

		public void record(String dn, String ssName, Attributes lAttrs) throws NamingException
		{
			if (logger.isDebugEnabled()) logger.debug("Retrieved user: '" + dn + "'");

			//use DN as 1st priority match.  This will catch changes in USER_ID_ATTRIBUTE
			Object[] row = ssDnUsers.get(dn); 
			if (row != null) notInLdap.remove(row[1]);
			Object[] row2 = (Object[])ssUsers.get(ssName);
			if (row2 != null) notInLdap.remove(row2[1]);
			if (row != null || row2 != null) {
				//user exists somewhere
				if (sync) {
					Map userMods = new HashMap();
					getUpdates(userAttributeNames, userAttributes, lAttrs, userMods);
					//remove this incase a mapping exists that is different than the uid attribute
					userMods.remove(ObjectKeys.FIELD_PRINCIPAL_NAME);
					if (row != null && row2 == null) {
						//user_id_attribute must have changed, just changing the name
						if (!foundNames.containsKey(ssName)) { //if haven't just added it
							if (logger.isDebugEnabled()) logger.debug("id changed: " + row[0] + "->" + ssName);
							userMods.put(ObjectKeys.FIELD_PRINCIPAL_NAME, ssName);
							row[0] = ssName;							
						} //otherwise update the other fields, just leave old name
					} else if (row == null && row2 != null) {
						//name exists, DN will be updated
						if (logger.isDebugEnabled()) logger.debug("dn changed: " + row2[4] + "->" + dn);
						userMods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
						row = row2;
						row[4] = dn;
					} else if (row != row2) {
						//have 2 rows that want the same loginName
						//this could only happen if the user_id_attribute has changed and the new name is already taken
						logger.error(NLT.get("errorcode.ldap.duplicate", new Object[] {ssName, dn}));
						//but apply updates to row anyway, just leave loginName unchanged
						
					}
					//otherwise equal and all is well
					ldap_existing.put((Long)row[1], userMods);				
				}
				//setup distinquished name for group sync
				dnUsers.put(dn, row);
			} else 	if (foundNames.containsKey(ssName)) {
				//name just created - skip duplicate
				logger.error(NLT.get("errorcode.ldap.duplicate", new Object[] {ssName, dn}));
				return;
			} else {
				if (create) {
					Map userMods = new HashMap();
					getUpdates(userAttributeNames, userAttributes, lAttrs, userMods);
					userMods.put(ObjectKeys.FIELD_PRINCIPAL_NAME, ssName);
					userMods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
					userMods.put(ObjectKeys.FIELD_ZONE, zoneId);
					ldap_new.put(ssName, userMods); 
					dnUsers.put(dn, new Object[]{ssName, null, Boolean.FALSE, null, dn});
				}
			}
			//keep track of users we have processed from ldap
			foundNames.put(ssName, ssName); 
			//do updates after every 100 users
			if (!ldap_existing.isEmpty() && (ldap_existing.size()%modifySyncSize == 0)) {
				//doLog("Updating users:", ldap_existing);
				updateUsers(zoneId, ldap_existing);
				ldap_existing.clear();
			}
			//do creates after every 100 users
			if (!ldap_new.isEmpty() && (ldap_new.size()%createSyncSize == 0)) {				
				doLog("Creating users:", ldap_new);
				List results = createUsers(zoneId, ldap_new);
				ldap_new.clear();
				// fill in mapping from distinquished name to id
				for (int i=0; i<results.size(); ++i) {
					User user = (User)results.get(i);
					row = (Object[])dnUsers.get(user.getForeignName());
					row[1] = user.getId();
				}
			}
		}
		
		public Map wrapUp()
		{
			if (!ldap_existing.isEmpty()) {
				//doLog("Updating users:", ldap_existing);
				updateUsers(zoneId, ldap_existing);
			}
			if (!ldap_new.isEmpty()) {
				doLog("Creating users:", ldap_new);
				List results = createUsers(zoneId, ldap_new);
				for (int i=0; i<results.size(); ++i) {
					User user = (User)results.get(i);
					Object[] row = (Object[])dnUsers.get(user.getForeignName());
					row[1] = user.getId();
				}
			}
			//if disable is enabled, remove users that were not found in ldap
			if (delete && !notInLdap.isEmpty()) {
				if (logger.isInfoEnabled()) {
					logger.info("Deleting users:");
					for (String name:notInLdap.values()) {
						logger.info("'" + name + "'");
					}
				}

				deletePrincipals(zoneId, notInLdap.keySet(), deleteWorkspace);
			}
			//Set foreign names of users to self; needed to recognize synced names and mark attributes read-only
			if (!delete && !notInLdap.isEmpty()) {
		    	Map users = new HashMap();
				if (logger.isDebugEnabled()) logger.debug("Users not found in ldap:");
				for (Map.Entry<Long, String>me:notInLdap.entrySet()) {
					Long id = me.getKey();
					String name = me.getValue();
					if (logger.isDebugEnabled()) logger.debug("'"+name+"'");
					Object row[] = (Object[])ssUsers.get(name);
					if (!name.equalsIgnoreCase((String)row[4])) {//was synched from somewhere else	
						Map updates = new HashMap();
				    	updates.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, name);
			    		users.put(id, updates);
			     	}
				}
				if (!users.isEmpty()) updateUsers(zoneId, users);
			}
			return dnUsers;

		}
	}
	
	protected void syncUsers(Binder zone, LdapContext ctx, LdapConnectionConfig config, UserCoordinator userCoordinator) 
		throws NamingException {
		String ssName;
		String [] sample = new String[0];
	 
		Map userAttributes = config.getMappings();

		userCoordinator.setAttributes(userAttributes);

		Set la = new HashSet(userAttributes.keySet());
		String userIdAttribute = config.getUserIdAttribute();
		if (Validator.isNull(userIdAttribute)) userIdAttribute = config.getUserIdAttribute();
		la.add(userIdAttribute);

		for(LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches()) {
			if(Validator.isNotNull(searchInfo.getFilter())) {
				int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
				SearchControls sch = new SearchControls(scope, 0, 0, (String [])la.toArray(sample), false, false);
	
				NamingEnumeration ctxSearch = ctx.search(searchInfo.getBaseDn(), searchInfo.getFilter(), sch);
				while (ctxSearch.hasMore()) {
					Binding bd = (Binding)ctxSearch.next();
					Attributes lAttrs = ctx.getAttributes(bd.getNameInNamespace());
					Attribute id=null;
					id = lAttrs.get(userIdAttribute);
					if (id == null) continue;
					//map ldap id to sitescapeName
					ssName = idToName((String)id.get());
					if (ssName == null) continue;
					String relativeName = bd.getNameInNamespace().trim();
					String dn;
					if (bd.isRelative() && !"".equals(ctx.getNameInNamespace())) {
						dn = relativeName + "," + ctx.getNameInNamespace().trim();
					} else {
						dn = relativeName;
					}
					if (userCoordinator.isDuplicate(dn)) {
						logger.error(NLT.get("errorcode.ldap.duplicate", new Object[] {ssName, dn}));
						continue;
					}
					userCoordinator.record(dn, ssName, lAttrs);
				}
			}
		}
	}

	class GroupCoordinator
	{
		Map ssGroups = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		//ssname groups that don't exists in ldap
		Map<Long, String> notInLdap = new TreeMap();
		//ldap group dn that have forum equivalents, contains membership attrs
		Map ldapGroups = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		Map DnToRelative = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		Map dnGroups = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		Map groupAttributes;
		String [] groupAttributeNames;
		

		Long zoneId;
		Map dnUsers;
		boolean sync;
		boolean create;
		boolean delete;
	
		long createSyncSize;
		long modifySyncSize;

		public GroupCoordinator(Binder zone, Map dnUsers, boolean sync, boolean create, boolean delete)
		{
			this.zoneId = zone.getId();
			this.dnUsers = dnUsers;
			this.create = create;
			this.delete = delete;
			createSyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "create.flush.threshhold"), 100);
			modifySyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "modify.flush.threshhold"), 100);

			//get list of existing groups.
			List attrs = coreDao.loadObjects(new ObjectControls(Group.class, principalAttrs), 
					new FilterControls(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE), zone.getId());
			//convert list of objects to a Map of loginNames 
			for (int i=0; i<attrs.size(); ++i) {
				Object[] row = (Object [])attrs.get(i);
				String ssName = (String)row[0];
				ssGroups.put(ssName, row);
				if (!Validator.isNull((String)row[4])) {
					dnGroups.put(row[4], row);
				}
				//initialize all groups as not found unless already disabled or reserved
				if (((Boolean)row[2] == Boolean.FALSE) && (Validator.isNull((String)row[3]))) {
					notInLdap.put((Long)row[1], ssName);
				}
			}
		}
		
		public void setAttributes(Map groupAttributes)
		{
			this.groupAttributes = groupAttributes;
			this.groupAttributeNames = 	(String[])(groupAttributes.keySet().toArray(sample));
		}

		boolean record(String dn, String relativeName, Attributes lAttrs) throws NamingException
		{
			boolean isSSGroup = false;
			if (logger.isDebugEnabled()) logger.debug("Retrieved group: '" + dn + "'");
			
			DnToRelative.put(dn, relativeName);
			//see if group mapping exists
			Object[] row = (Object [])dnGroups.get(dn);
			String ssName;
			if (row == null) { 
				if (create) {
					//see if name already exists
					ssName = dnToGroupName(dn);
					row = (Object[])ssGroups.get(ssName);
					if (row != null) {
						logger.error(NLT.get("errorcode.ldap.groupexists", new Object[]{dn}));
					} else {
						Map userMods = new HashMap();
						//mapping may change the name and title
						userMods.put(ObjectKeys.FIELD_PRINCIPAL_NAME,ssName);
						userMods.put(ObjectKeys.FIELD_ENTITY_TITLE, dn);
						getUpdates(groupAttributeNames, groupAttributes, lAttrs, userMods);
						if (logger.isDebugEnabled()) logger.debug("Creating group:" + ssName);
						Group group = createGroup(zoneId, ssName, userMods); 
						if(group != null) {
							userMods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
							userMods.put(ObjectKeys.FIELD_ZONE, zoneId);
							dnGroups.put(dn, new Object[]{ssName, group.getId(), Boolean.FALSE, null, dn});
							ldapGroups.put(dn, lAttrs);
							isSSGroup = true;
						}
					}
				}
			} else {
				ssName = (String)row[0];
				if (sync) {
					Map userMods = new HashMap();
					if (logger.isDebugEnabled()) logger.debug("Updating group:" + ssName);
					getUpdates(groupAttributeNames, groupAttributes, lAttrs, userMods);
					updateGroup(zoneId, (Long)row[1], userMods);
				} 
				//exists in ldap, remove from missing list
				notInLdap.remove(row[1]);
				ldapGroups.put(dn, lAttrs);
				isSSGroup = true;
			}
			
			return isSSGroup;
		}
		
	    /**
	     * Create groups.
	     * @param zoneName
	     * @param groups - Map keyed by user id, value is map of attributes
	     * @return
	     */
	    protected Group createGroup(Long zoneId, String ssName, Map groupData) {
	    	MapInputData groupMods = new MapInputData(groupData);
			ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
			//get default definition to use
			Group temp = new Group();
			getDefinitionModule().setDefaultEntryDefinition(temp);
			Definition groupDef = temp.getEntryDef();
			try {
		    	ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
		    	List newGroups = processor.syncNewEntries(pf, groupDef, Group.class, Arrays.asList(new MapInputData[] {groupMods}), null);
		    	IndexSynchronizationManager.applyChanges(); //apply now, syncNewEntries will commit
		    	//flush from cache
		    	getCoreDao().evict(newGroups);
		    	return (Group) newGroups.get(0);		    	
			} catch (Exception ex) {
				logger.error("Error adding group: " + ex.getLocalizedMessage());
				logger.error("'" + ssName + "':'" + groupData.get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) + "'");
			}
			return null;
	    }

	    /**
	     * Update group with their own updates
	     */    
		protected void updateGroup(Long zoneId, final Long groupId, final Map groupMods) {
			ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
			List collections = new ArrayList();
			collections.add("customAttributes");
		   	List foundEntries = getCoreDao().loadObjects(Arrays.asList(new Long[] {groupId}), Group.class, zoneId, collections);
		   	Map entries = new HashMap();
		   	Group g = (Group)foundEntries.get(0);
		   	entries.put(g, new MapInputData(groupMods));
		    try {
		    	ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
		    	processor.syncEntries(entries, null);
		    	IndexSynchronizationManager.applyChanges(); //apply now, syncEntries will commit
		    	//flush from cache
		    	getCoreDao().evict(g);
		    } catch (Exception ex) {
		    	//continue 
		    	logger.error("Error updating groups: " + ex.getLocalizedMessage());	   		
		    }
	    }

		protected void syncMembership(Long groupId, Enumeration valEnum)
		throws NamingException
		{
			Object[] uRow;
			List membership = new ArrayList();
			//build new membership
			while(valEnum.hasMoreElements()) {
				String mDn = ((String)valEnum.nextElement()).trim();
				uRow = (Object[])dnUsers.get(mDn);
				if (uRow == null) uRow = (Object[])dnGroups.get(mDn);
				if (uRow == null || uRow[1] == null) continue; //never got created
				membership.add(new Membership(groupId, (Long)uRow[1]));
			}
			//do inside a transaction
			updateMembership(groupId, membership);	
		}

		public void deleteObsoleteGroups()
		{
			//if disable is enabled, remove groups that were not found in ldap
			if (delete && !notInLdap.isEmpty()) {
				if (logger.isInfoEnabled()) {
					logger.info("Deleting groups:");
					for (String name:notInLdap.values()) {
						logger.info("'" + name + "'");
					}
				}
				deletePrincipals(zoneId,notInLdap.keySet(), false);
			} else if (!delete && !notInLdap.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Groups not found in ldap:");
					for (String name:notInLdap.values()) {
						logger.debug("'" + name + "'");
					}
					
				}
			}
		}
		
		public Object[] getGroup(String dn)
		{
			return (Object [])dnGroups.get(dn);
		}
	}

	protected void syncGroups(Binder zone, LdapContext ctx, LdapConnectionConfig config, GroupCoordinator groupCoordinator,
							  boolean syncMembership) 
		throws NamingException {
		//ssname=> forum info
		String [] sample = new String[0];
		//ldap dn => forum info
		for(LdapConnectionConfig.SearchInfo searchInfo : config.getGroupSearches()) {
			if(Validator.isNotNull(searchInfo.getFilter())) {
				Map groupAttributes = (Map) getZoneMap(zone.getName()).get(GROUP_ATTRIBUTES);
				groupCoordinator.setAttributes(groupAttributes);
				Set la = new HashSet(groupAttributes.keySet());
				la.addAll((List) getZoneMap(zone.getName()).get(MEMBER_ATTRIBUTES));
				int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
				SearchControls sch = new SearchControls(scope, 0, 0, (String [])la.toArray(sample), false, false);
	
				NamingEnumeration ctxSearch = ctx.search(searchInfo.getBaseDn(), searchInfo.getFilter(), sch);
				while (ctxSearch.hasMore()) {
					Binding bd = (Binding)ctxSearch.next();
					Attributes lAttrs = ctx.getAttributes(bd.getNameInNamespace());
					String relativeName = bd.getNameInNamespace().trim();
					String dn;
					if (bd.isRelative() && !"".equals(ctx.getNameInNamespace())) {
						if(!"".equals(relativeName)) {
							dn = relativeName + "," + ctx.getNameInNamespace().trim();
						} else {
							dn = ctx.getNameInNamespace().trim();
						}
					} else {
						dn = relativeName;
					}
					//doing this one at a time is going to be slow for lots of groups
					//not sure why it was changed for v2
					if(groupCoordinator.record(dn, relativeName, lAttrs) && syncMembership) { 
						//Get map indexed by id
						Object[] gRow = groupCoordinator.getGroup(dn);
						if (gRow == null) continue; //not created
						Long groupId = (Long)gRow[1];
						if (groupId == null) continue; // never got created
						List memberAttributes = (List) getZoneMap(zone.getName()).get(MEMBER_ATTRIBUTES);
						Attribute att = null;
						for (int i=0; i<memberAttributes.size(); i++) {
							att = lAttrs.get((String)memberAttributes.get(i));
							if(att != null && att.get() != null && att.size() != 0) {
								break;
							}
							att = null;
						}
						Enumeration members = null;
						if(att != null) {
							members = att.getAll();
						} else {
							NamingEnumeration<NameClassPair> e = ctx.list(relativeName);
	
							LinkedList membersList = new LinkedList();
							while(e.hasMore()) {
								NameClassPair pair = e.next();
								membersList.add(pair.getNameInNamespace());
							}
							members = Collections.enumeration(membersList);
						}
						if(members != null) {
							groupCoordinator.syncMembership(groupId, members);
						}
					}
				}
			}
		}
	}
	
	protected void doLog(String caption, Map<String, Map> names) {
		if (logger.isInfoEnabled()) {
			logger.info(caption);
			for (Map.Entry<String, Map> me:names.entrySet()) {
				logger.info("'" + me.getKey() + "':'" + me.getValue().get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) + "'");
			}
		}
	}
	/**
	 * Initialize context to an ldap server using 
	 * 
	 * @param info
	 * @return LdapContext
	 * @throws NamingException
	 */
	protected LdapContext getUserContext(Long zoneId, LdapConnectionConfig config) throws NamingException {
		return getContext(zoneId, config, false);
	}
	protected LdapContext getGroupContext(Long zoneId, LdapConnectionConfig config) throws NamingException {
		return getContext(zoneId, config, true);
	}
	protected LdapContext getContext(Long zoneId, LdapConnectionConfig config, boolean replaceDn) throws NamingException { 
		// Load user from ldap
		String user = config.getPrincipal();
		String pwd = config.getCredentials();
		try {
			Hashtable env = new Hashtable();
			Workspace zone = (Workspace)getCoreDao().load(Workspace.class, zoneId);
			env.put(Context.INITIAL_CONTEXT_FACTORY, getLdapProperty(zone.getName(), Context.INITIAL_CONTEXT_FACTORY));
			if (!Validator.isNull(user) && !Validator.isNull(pwd)) {
				env.put(Context.SECURITY_PRINCIPAL, user);
				env.put(Context.SECURITY_CREDENTIALS, pwd);		
				env.put(Context.SECURITY_AUTHENTICATION, getLdapProperty(zone.getName(), Context.SECURITY_AUTHENTICATION));
			} 
			String url = config.getUrl();
			/*
			if(replaceDn && !Validator.isNull(config.getGroupsBasedn())) {
				if(url.lastIndexOf('/') > 8) {
					url = url.substring(0, url.lastIndexOf('/'));
				}
				url = url + "/" + config.getGroupsBasedn();
				logger.debug("Using " + url + " for groups");
			}
			*/
			env.put(Context.PROVIDER_URL, url);
			String socketFactory = getLdapProperty(zone.getName(), "java.naming.ldap.factory.socket"); 
			if (!Validator.isNull(socketFactory))
				env.put("java.naming.ldap.factory.socket", socketFactory);
		
			return new InitialLdapContext(env, null);
		} catch (NamingException ex) {
			logger.error(NLT.get("errorcode.ldap.context") + " " + ex.getLocalizedMessage());
			throw ex;
		}
	}


	protected static void getUpdates(String []ldapAttrNames, Map mapping, Attributes attrs, Map mods)  throws NamingException {
			
		for (int i=0; i<ldapAttrNames.length; i++) {
			Attribute att = attrs.get(ldapAttrNames[i]);
			if (att == null) continue;
			Object val = att.get();
			if (val == null) {
				mods.put(mapping.get(ldapAttrNames[i]), null);
			} else if (att.size() == 0) {
				continue;
			} else if (att.size() == 1) {
				mods.put(mapping.get(ldapAttrNames[i]), val);					
			} else {
				Set vals = new HashSet();
				for (NamingEnumeration valEnum=att.getAll(); valEnum.hasMoreElements();) {
					vals.add(valEnum.nextElement());
				}
				mods.put(mapping.get(ldapAttrNames[i]), vals.toArray(sample));
			}
		}		
	}


	protected String nameToId(String name) {
		return name;
	}
	protected String idToName(String id) {
		return id.trim();
	}
	protected String dnToGroupName(String dn) {
		//already trimmed
		return dn;
	}
	
	/**
	 * Transaction enabled methods for finer granularity.  We load the ldap attributes
	 * as string values.  We are not using the definition builder.
	 * 
	 */
	/**
	 * Update a user with attributes specified in the map.
	 *
	 * @param zoneName
	 * @param loginName
	 * @param mods
	 * @throws NoUserByTheNameException
	 */
	protected void updateUser(Binder zone, String loginName, Map mods) throws NoUserByTheNameException {

		User profile = getProfileDao().findUserByName(loginName, zone.getName()); 
 		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	profile.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
		processor.syncEntry(profile, new MapInputData(mods), null);
	}	
	/**
	 * Update users with their own map of updates
	 * @param users - Map indexed by user id, value is map of updates for a user
	 */
	protected void updateUsers(Long zoneId, final Map users) {
		if (users.isEmpty()) return;
		ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
		List collections = new ArrayList();
		collections.add("customAttributes");
	   	List foundEntries = getCoreDao().loadObjects(users.keySet(), User.class, zoneId, collections);
	   	Map entries = new HashMap();
	   	for (int i=0; i<foundEntries.size(); ++i) {
	   		User u = (User)foundEntries.get(i);
	   		entries.put(u, new MapInputData((Map)users.get(u.getId())));
	   	}

	   	try {
	   		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	   		processor.syncEntries(entries, null);
	   		IndexSynchronizationManager.applyChanges(); //apply now, syncEntries will commit
	   		//flush from cache
	   		for (int i=0; i<foundEntries.size(); ++i) getCoreDao().evict(foundEntries.get(i));
	   	} catch (Exception ex) {
	   		//continue 
			logger.error("Error updating users: " + ex.getLocalizedMessage());	   		
	   	}
	}

    protected void updateMembership(Long groupId, Collection newMembers) {
		//have a list of users, now compare with what exists already
		List oldMembers = getProfileDao().getMembership(groupId, RequestContextHolder.getRequestContext().getZoneId());
		final Set newM = CollectionUtil.differences(newMembers, oldMembers);
		final Set remM = CollectionUtil.differences(oldMembers, newMembers);

        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		for (Iterator iter=remM.iterator(); iter.hasNext();) {
        			Membership c = (Membership)iter.next();
       				getCoreDao().delete(c);
        		}
		
        		getCoreDao().save(newM);
        		return null;
        	}});
		sessionFactory.evictCollection("org.kablink.teaming.domain.UserPrincipal.memberOf");
		
    }
    /**
     * Create users.  
     * @param zoneName
     * @param users - Map keyed by user id, value is map of attributes
     * @return
     */
    protected List<User> createUsers(Long zoneId, Map<String, Map> users) {
		//SimpleProfiler.setProfiler(new SimpleProfiler(false));
		
		ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
		List newUsers = new ArrayList();
		for (Iterator i=users.values().iterator(); i.hasNext();) {
			newUsers.add(new MapInputData((Map)i.next()));
		}
		//get default definition to use
		Definition userDef = pf.getDefaultEntryDef();		
		if (userDef == null) {
			User temp = new User();
			getDefinitionModule().setDefaultEntryDefinition(temp);
			userDef = temp.getEntryDef();
		}
		try {
			ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
			if(logger.isDebugEnabled())
				logger.debug("Synchronizing user objects");
			newUsers = processor.syncNewEntries(pf, userDef, User.class, newUsers, null);    
//	  Takes to long to addWorkspaces - they will get added as needed
//	   	for (int i =0; i<newUsers.size(); ++i) {
//	   		User u = (User)newUsers.get(i);
//	   		SimpleProfiler.startProfiler("createUsers:addUserWorkspace");
//	   		try {
//	   			if(logger.isDebugEnabled())
//	   				logger.debug("Adding personal workspace for user [" + u.getName() + "]");
//	   			getProfileModule().addUserWorkspace(u);
//	   		} catch (Exception ex) {
//	   			logger.error(NLT.get("errorcode.ldap.createworkspace", new Object[] {u.getName()}) + " " + ex.getLocalizedMessage());
//	   		}
//	   		SimpleProfiler.stopProfiler("createUsers:addUserWorkspace");	
//	   	}
			if(logger.isDebugEnabled())
				logger.debug("Applying index changes");
			IndexSynchronizationManager.applyChanges();  //apply now, syncNewEntries will commit
			//SimpleProfiler.printProfiler();
		   	//SimpleProfiler.clearProfiler();
			//flush from cache
			getCoreDao().evict(newUsers);
		} catch (Exception ex) {
			logger.error("Error adding users: " + ex.getLocalizedMessage());
			for (Map.Entry<String, Map> me:users.entrySet()) {
				logger.error("'" + me.getKey() + "':'" + me.getValue().get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) + "'");
			}

			//continue, returning empty list
			newUsers.clear();
		}
	   	return newUsers;
     }
    /**
     * Create groups.
     * @param zoneName
     * @param groups - Map keyed by user id, value is map of attributes
     * @return
     */
    protected List<Group> createGroups(Binder zone, Map<String, Map> groups) {
		ProfileBinder pf = getProfileDao().getProfileBinder(zone.getZoneId());
		List newGroups = new ArrayList();
		for (Iterator i=groups.values().iterator(); i.hasNext();) {
			newGroups.add(new MapInputData((Map)i.next()));
		}
		//get default definition to use
		Group temp = new Group();
		getDefinitionModule().setDefaultEntryDefinition(temp);
		Definition groupDef = temp.getEntryDef();

	    try {
	    	ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    	newGroups = processor.syncNewEntries(pf, groupDef, Group.class, newGroups, null);
	    	IndexSynchronizationManager.applyChanges(); //apply now, syncNewEntries will commit
	    	//flush from cache
	    	getCoreDao().evict(newGroups);
		} catch (Exception ex) {
			logger.error("Error adding groups: " + ex.getLocalizedMessage());
			for (Map.Entry<String, Map> me:groups.entrySet()) {
				logger.error("'" + me.getKey() + "':'" + me.getValue().get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) + "'");
			}
			//continue, returning empty list
			newGroups.clear();
		}
    	return newGroups;
	    	
    }
     public void deletePrincipals(Long zoneId, Collection ids, boolean deleteWS) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, Boolean.valueOf(deleteWS));

		for (Iterator iter=ids.iterator(); iter.hasNext();) {
    		Long id = (Long)iter.next();
    		try {
    			getProfileModule().deleteEntry(id, options);
    		} catch (Exception ex) {
    			logger.error(NLT.get("errorcode.ldap.delete", new Object[]{id.toString()}) + " " + ex.getLocalizedMessage());
    		}
    	}
		IndexSynchronizationManager.applyChanges();
    }
    //have not implement re-enable so only support delete
/*    protected void disableUsers(final Binder zone, final Collection ids) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
       		getProfileDao().disablePrincipals(ids, zone.getZoneId());
        		
        		return null;
        	}});
        //remove from index
   		for (Iterator i=ids.iterator(); i.hasNext();) {
   	   	    IndexSynchronizationManager.deleteDocument(BasicIndexUtils.makeUid("org.kablink.teaming.domain.User", (Long)i.next()));  			
   		}
   		IndexSynchronizationManager.applyChanges();
   	    
    }
    protected void disableGroups(final Binder zone, final Collection ids) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		getProfileDao().disablePrincipals(ids, zone.getZoneId());
        		return null;
        	}});
        //remove from index
   		for (Iterator i=ids.iterator(); i.hasNext();) {
   	   	    IndexSynchronizationManager.deleteDocument(BasicIndexUtils.makeUid("org.kablink.teaming.domain.Group", (Long)i.next()));  			
   		}
   		IndexSynchronizationManager.applyChanges();
   }
 */
}