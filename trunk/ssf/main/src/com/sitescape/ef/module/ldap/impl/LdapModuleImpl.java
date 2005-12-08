
package com.sitescape.ef.module.ldap.impl;
import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.TreeMap;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;

import com.sitescape.ef.jobs.LdapSynchronization;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.ldap.LdapInfo;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.module.ldap.LdapNameMapper;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.NoGroupByTheIdException;
/**
 * @author Janet McCann
 *
 */
public class LdapModuleImpl implements LdapModule {
	protected Log logger = LogFactory.getLog(getClass());
	private CoreDao coreDao;
	private Scheduler scheduler; 
	private ProcessorManager processorManager;
	//pointer to self, so we can wrap scheduled sync operations in write transactions
	private ProfileModule profileModule;
	private String [] principalAttrs = new String[]{"name", "id", "disabled", "reserved", "foreignName"};

	public void setCoreDao(CoreDao coreDao) {
	    this.coreDao = coreDao;
	}
	public CoreDao getCoreDao() {
		return coreDao;
	}
	public void setScheduler(Scheduler scheduler) {
    	this.scheduler = scheduler;
    }	
	public void setProcessorManager(ProcessorManager processorManager) {
	    this.processorManager = processorManager;
	} 	
	public void setProfileModule(ProfileModule profileModule) {
	    this.profileModule = profileModule;
	}	
	//methods protected by transaction in config file
	/**
	 * Update ldap configuration.  Only properties specified in the props file will
	 * be modified.
	 * @param zoneId
	 * @param props
	 */
	public void setLdapConfig(Map props) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		Workspace ws = (Workspace)coreDao.findTopWorkspace(zoneName);
		ws.getLdapInfo().updateProperties(props);
   		LdapSynchronization process = (LdapSynchronization)processorManager.getProcessor(ws, LdapSynchronization.PROCESSOR_KEY);
   		process.checkSchedule(scheduler, ws);
	}
	/**
	 * Authenticate user against ldap
	 * @param zoneName
	 * @param loginName
	 * @param password
	 * @return <code>true</code> on success; <code>false\</false>authentication failed
	 * @throws NoUserByTheNameException
	 * @throws NamingException
	 */
	public boolean authenticate(String zoneName, String loginName, String password) 
		throws NamingException, NoUserByTheNameException {
		Workspace ws = (Workspace)coreDao.findTopWorkspace(zoneName);

		LdapContext ctx = null;
		LdapInfo info = ws.getLdapInfo();
		Map mods = new HashMap();
		String dn;
		//make sure user exists
		dn = getUpdates(info, loginName, mods);
			
		// Make sure the fetched user dn and password combination can bind
		// against the LDAP server
		try {
			ctx = getContext(info, dn, password, info.getDomain(LdapInfo.USERS));
			ctx.close();
		} catch (Exception e) {
			return false;
		}
		if (!mods.isEmpty()) {
			try {
				syncUser(zoneName, loginName, mods);
			} catch (NoUserByTheNameException nu) {
				//do nothing - liferay will catch it
			}
		}
		return true;
	}
		
	/**
	 * Update a ssf user with an ldap person.  Attributes synchronized come from LdapInfo 
	 * associated with the company 
	 * @param zoneName
	 * @param loginName
	 * @throws NoUserByThenameException
	 * @throws NamingException
	 */
	public void syncUser(String zoneName, String loginName) 
		throws NoUserByTheNameException, NamingException {
		Workspace ws = (Workspace)coreDao.findTopWorkspace(zoneName);
		LdapInfo info = ws.getLdapInfo();
		Map mods = new HashMap();
		String dn = getUpdates(info, loginName, mods);
		syncUser(zoneName, loginName, mods);

	}
	/**
	 * This routine alters group membership without updateing the local caches.
	 * Need to flush cache after use
	 */
	public void syncAll(String zoneName)throws NamingException {
		Workspace ws = (Workspace)coreDao.findTopWorkspace(zoneName);
		LdapInfo info = ws.getLdapInfo();
   		LdapNameMapper mapper = (LdapNameMapper)processorManager.getProcessor(ws, LdapNameMapper.PROCESSOR_KEY);
   		LdapContext ctx=null;
   		String dn;
   		Object[] gRow,uRow;
   		Iterator iter;
   		try {
			ctx = getContext(info, null, null, info.getDomain(LdapInfo.USERS));
			Map dnUsers  = syncUsers(ws, ctx, info, mapper);
			ctx.close();
			ctx = null;
			ctx = getContext(info, null, null, info.getDomain(LdapInfo.GROUPS));
			Map [] gResults = syncGroups(ws, ctx, info, mapper);
			if (info.isAutoSync(LdapInfo.MEMBERSHIP)) {
				Map dnGroups = (Map)gResults[0];
				Map ldapGroups = (Map)gResults[1];
				//Get map indexed by id
				Set reservedIds = new HashSet();
				buildReserved(dnUsers.values(), reservedIds);
				buildReserved(dnGroups.values(), reservedIds);
				//loop threw each ldapGroup that is now in forum
				for (iter=ldapGroups.entrySet().iterator(); iter.hasNext();) {
					Map.Entry entry = (Map.Entry)iter.next();
					dn = (String)entry.getKey();
					gRow = (Object [])dnGroups.get(dn);
					Long groupId = (Long)gRow[1];
					List membership = new ArrayList();
					String [] attrs = info.getMemberIdAttrs();
					Attributes lAttrs = (Attributes)entry.getValue();

					for (int i=0; i<attrs.length; i++) {
						Attribute att = lAttrs.get(attrs[i]);
						if (att == null) continue;
						Object val = att.get();
						if (val == null) {
							continue;
						} else if (att.size() == 0) {
							continue;
						} else {
							//build new membership
							for (NamingEnumeration valEnum=att.getAll(); valEnum.hasMoreElements();) {
								String mDn = ((String)valEnum.nextElement()).trim();
								uRow = (Object[])dnUsers.get(mDn);
								if (uRow == null) uRow = (Object[])dnGroups.get(mDn);
								if (uRow == null) continue;
								membership.add(new Membership(groupId, (Long)uRow[1]));
							}
							//have a list of users, now compare with what exists already
							List current = getCoreDao().getMembership(groupId);
							for (int j =0; j<current.size(); ++j) {
								Membership c = (Membership)current.get(j);
								if (!membership.contains(c) && !reservedIds.contains(c.getUserId())) {
									getCoreDao().delete(c);
								}
							}
							for (int j=0; j<membership.size(); ++j) {
								Membership c = (Membership)membership.get(j);
								if (!current.contains(c)) getCoreDao().save(c);
							}
						}
					}		
				}
			}
		} finally {
			if (ctx != null) {
				ctx.close();			
			}
		}
	}
	private void buildReserved(Collection rows, Set reservedIds) {
		Object [] row;
		for (Iterator iter=rows.iterator(); iter.hasNext();) {
			row = (Object[])iter.next();
			if ((Boolean)row[3] == Boolean.TRUE) {
				reservedIds.add(row[1]);
			}
		}
		
	}
	
	// No transaction specified in config file
	// Will run under existing
	/**
	 * Update a ssf user with attributes specified in the map.
	 * Must be contained in another transaction
	 *
	 * @param zoneName
	 * @param loginName
	 * @param mods
	 * @throws NoUserByTheNameException
	 */
	private void syncUser(String zoneName, String loginName, Map mods) throws NoUserByTheNameException {
		Workspace ws = (Workspace)coreDao.findTopWorkspace(zoneName);
		User profile = coreDao.findUserByName(loginName, zoneName); 
		EntryBuilder.buildEntry(profile, mods);

	}
	protected Map syncUsers(Workspace ws, LdapContext ctx, LdapInfo info, LdapNameMapper mapper) 
		throws NamingException {
		String ssName;
		Object[] row;
		Map ssUsers = new HashMap();
		Map notInLdap = new HashMap();
		Map ldap_existing = new HashMap();
		Map ldap_new = new HashMap();
		boolean create = info.isAutoCreate(LdapInfo.USERS);
		boolean sync = info.isAutoSync(LdapInfo.USERS);
		Map dnUsers;
		String [] sample = new String[0];
		if (info.isMembersIgnoreCase()) dnUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		else dnUsers = new HashMap();
		//get list of users.
		List attrs = coreDao.loadObjects(new ObjectControls(User.class, principalAttrs), new FilterControls("zoneName", ws.getZoneName()));
		//convert list of objects to a Map of forumNames 
		for (int i=0; i<attrs.size(); ++i) {
			row = (Object [])attrs.get(i);
			ssName = (String)row[0];
			ssUsers.put(ssName, row);
			//initialize all users as not found unless already disabled or reserved
			if (((Boolean)row[2] == Boolean.FALSE) && ((Boolean)row[3] == Boolean.FALSE)) {
				notInLdap.put(ssName, row[1]);
			}
		}
		Set la = new HashSet(info.getLdapAttrs(LdapInfo.USERS));
		String[] userIdAttrs = info.getUserIdAttrs();
		for (int i=0; i<userIdAttrs.length; ++i) {
			la.add(userIdAttrs[i]);
		}
		SearchControls sch = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 0, 0, (String [])la.toArray(sample), false, false);

		NamingEnumeration ctxSearch = ctx.search("", info.getFilterString(LdapInfo.USERS), sch);
		while (ctxSearch.hasMore()) {
			Binding bd = (Binding)ctxSearch.next();
			Attributes lAttrs = ctx.getAttributes(bd.getName());
			Attribute id=null;
			for (int i=0; i<userIdAttrs.length; ++i) {
				id = lAttrs.get(userIdAttrs[i]);
				if (id == null) continue;
			}
			if (id == null) continue;
			//map ldap id to sitescapeName
			ssName = mapper.idToName((String)id.get());
			if (ssName == null) continue;
			String dn;
			if (bd.isRelative()) {
				dn = (bd.getName().trim() + "," + info.getDomain(LdapInfo.USERS));
			} else {
				dn = bd.getName().trim();
			}
			row = (Object[])ssUsers.get(ssName);
			if (row != null) {
				if (sync) {
					Map userMods = new HashMap();
					getUpdates(info, LdapInfo.USERS, lAttrs, userMods);
					userMods.put("foreignName", dn);
					ldap_existing.put(row[1], userMods);
				} 
				//exists in ldap, remove from missing list
				notInLdap.remove(ssName);
				//setup distinquished name for group sync
				dnUsers.put(dn, row);
			} else if (create) {
				Map userMods = new HashMap();
				getUpdates(info, LdapInfo.USERS, lAttrs, userMods);
				userMods.put("name", ssName);
				userMods.put("foreignName", dn);
				userMods.put("zoneName", ws.getZoneName());
				ldap_new.put(ssName, userMods); 
				dnUsers.put(dn, new Object[]{ssName, null, new Boolean(false), new Boolean(false), dn});
			}
			//do updates after every 100 users
			if (sync && (ldap_existing.size()%100 == 0) && !ldap_existing.isEmpty()) {
				//doLog("Updating users:", ldap_existing);
				profileModule.bulkUpdateUsers(ldap_existing);
				ldap_existing.clear();
			}
			//do creates after every 100 users
			if (create && (ldap_new.size()%100 == 0) && !ldap_new.isEmpty()) {
				doLog("Creating users:", ldap_new);
				List results = profileModule.bulkCreateUsers(ldap_new);
				ldap_new.clear();
				// fill in mapping from distinquished name to id
				for (int i=0; i<results.size(); ++i) {
					User user = (User)results.get(i);
					row = (Object[])dnUsers.get(user.getForeignName());
					row[1] = user.getId();
				}
			}
		}
		if (sync && !ldap_existing.isEmpty()) {
			//doLog("Updating users:", ldap_existing);
			profileModule.bulkUpdateUsers(ldap_existing);
		}
		if (create && !ldap_new.isEmpty()) {
			doLog("Creating users:", ldap_new);
			List results = profileModule.bulkCreateUsers(ldap_new);
			for (int i=0; i<results.size(); ++i) {
				User user = (User)results.get(i);
				row = (Object[])dnUsers.get(user.getForeignName());
				row[1] = user.getId();
			}
		}
		
		//if disable is enabled, remove users that were not found in ldap
		if (info.isAutoDisable(LdapInfo.USERS) && !notInLdap.isEmpty()) {
			doLog("Disabling users:", notInLdap);
			profileModule.bulkDisableUsers(notInLdap.values());
		}
		return dnUsers;
	}

	protected Map[] syncGroups(Workspace ws, LdapContext ctx, LdapInfo info, LdapNameMapper mapper) 
		throws NamingException {
		String ssName;
		Object[] row;
		//ssname=> forum info
		Map ssGroups = new HashMap();
		//ssname groups that don't exists in ldap
		Map notInLdap = new HashMap();
		Map ldap_existing = new HashMap();
		Map ldap_new = new HashMap();
		boolean create = info.isAutoCreate(LdapInfo.GROUPS);
		boolean sync = info.isAutoSync(LdapInfo.GROUPS);
		//ldap group dn that have forum equivalents, contains membership attrs
		Map ldapGroups = new HashMap();
		String [] sample = new String[0];
		//ldap dn => forum info
		Map dnGroups;
		if (info.isMembersIgnoreCase()) dnGroups = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		else dnGroups = new HashMap();

		//get list of existing groups.
		List attrs = coreDao.loadObjects(new ObjectControls(Group.class, principalAttrs), new FilterControls("zoneName", ws.getZoneName()));
		//convert list of objects to a Map of forumNames 
		for (int i=0; i<attrs.size(); ++i) {
			row = (Object [])attrs.get(i);
			ssName = (String)row[0];
			ssGroups.put(ssName, row);
			if (!Validator.isNull((String)row[4])) {
				dnGroups.put(row[4], row);
			}
			//initialize all groups as not found unless already disabled or reserved
			if (((Boolean)row[2] == Boolean.FALSE) && ((Boolean)row[3] == Boolean.FALSE)) {
				notInLdap.put(ssName, row[1]);
			}
		}
		Set la = new HashSet(info.getLdapAttrs(LdapInfo.GROUPS));
		String[] memberIdAttrs = info.getMemberIdAttrs();
		for (int i=0; i<memberIdAttrs.length; ++i) {
			la.add(memberIdAttrs[i]);
		}
		SearchControls sch = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 0, 0, (String [])la.toArray(sample), false, false);
	

		NamingEnumeration ctxSearch = ctx.search("", info.getFilterString(LdapInfo.GROUPS), sch);
		while (ctxSearch.hasMore()) {
			Binding bd = (Binding)ctxSearch.next();
			Attributes lAttrs = ctx.getAttributes(bd.getName());
			String dn;
			if (bd.isRelative()) {
				dn = bd.getName().trim() + "," + info.getDomain(LdapInfo.GROUPS);
			} else {
				dn = bd.getName().trim();
			}
			//see if group mapping exists
			row = (Object [])dnGroups.get(dn);
			if (row == null) { 
				if (create) {
					//see if name already exists
					ssName = mapper.dnToGroupName(dn);
					row = (Object[])ssGroups.get(ssName);
					if (row != null) {
						logger.error(dn + " Cannot create; ldap name mapped to an existing group without a foreignName mapping " + ssName);
					} else {
						Map userMods = new HashMap();
						getUpdates(info, LdapInfo.GROUPS, lAttrs, userMods);
						ldap_new.put(ssName, userMods);
						userMods.put("foreignName", dn);
						userMods.put("name",ssName);
						userMods.put("zoneName", ws.getZoneName());
						dnGroups.put(dn, new Object[]{ssName, null, new Boolean(false), new Boolean(false), dn});
						ldapGroups.put(dn, lAttrs);
					}
				}
			} else {
				ssName = (String)row[0];
				if (sync) {
					Map userMods = new HashMap();
					getUpdates(info, LdapInfo.GROUPS, lAttrs, userMods);
					ldap_existing.put(row[1], userMods);
				} 
				//exists in ldap, remove from missing list
				notInLdap.remove(ssName);
				ldapGroups.put(dn, lAttrs);
			}
			//do updates after every 100 users
			if (sync && (ldap_existing.size()%100 == 0) && !ldap_existing.isEmpty()) {
				//doLog("Updating groups:", ldap_existing);
				profileModule.bulkUpdateGroups(ldap_existing);
				ldap_existing.clear();
			}
			//do creates after every 100 users
			if (create && (ldap_new.size()%100 == 0) && !ldap_new.isEmpty()) {
				doLog("Creating Groups:", ldap_new);
				List results = profileModule.bulkCreateGroups(ldap_new);
				ldap_new.clear();
				// fill in mapping from distinquished name to id
				for (int i=0; i<results.size(); ++i) {
					Group group = (Group)results.get(i);
					row = (Object[])dnGroups.get(group.getForeignName());
					row[1] = group.getId();
				}
			}
		}
		if (sync && !ldap_existing.isEmpty()) {
			//doLog("Updating groups:", ldap_existing);
			profileModule.bulkUpdateGroups(ldap_existing);
		}
		if (create && !ldap_new.isEmpty()) {
			doLog("Creating Groups:", ldap_new);
			List results = profileModule.bulkCreateGroups(ldap_new);
			for (int i=0; i<results.size(); ++i) {
				Group group = (Group)results.get(i);
				row = (Object[])dnGroups.get(group.getForeignName());
				row[1] = group.getId();
			}
		}
	
		//if disable is enabled, remove groups that were not found in ldap
		if (info.isAutoDisable(LdapInfo.GROUPS) && !notInLdap.isEmpty()) {
			doLog("Disabling groups:", notInLdap);
			profileModule.bulkDisableGroups(notInLdap.values());
		}
		return new Map[]{dnGroups,ldapGroups};
	}
	private void doLog(String caption, Map names) {
		if (logger.isInfoEnabled()) {
			logger.info(caption);
			for (Iterator iter=names.keySet().iterator(); iter.hasNext();) {
				logger.info(iter.next());
			}
		}
	}

	/**
	 * Initialize context to an ldap server using 
	 * 
	 * @param info
	 * @param user if null use security attributes from info
	 * @param pwd
	 * @param domain
	 * @return LdapContext
	 * @throws NamingException
	 */
	protected LdapContext getContext(LdapInfo info, String user, String pwd, String domain) throws NamingException { 
		// Load user from ldap
		try {
			Hashtable env = new Hashtable();

			env.put(Context.INITIAL_CONTEXT_FACTORY, info.getContextFactoryName());
			if (!Validator.isNull(user)) {
				env.put(Context.SECURITY_PRINCIPAL, info.getAuthPrincipal());
				env.put(Context.SECURITY_CREDENTIALS, info.getAuthPassword());			
			} else if (!Validator.isNull(info.getAuthPrincipal()) && !Validator.isNull(info.getAuthPassword())) {
				env.put(Context.SECURITY_PRINCIPAL, info.getAuthPrincipal());
				env.put(Context.SECURITY_CREDENTIALS, info.getAuthPassword());
			}
			env.put(Context.SECURITY_AUTHENTICATION, info.getAuthType());
			env.put(Context.PROVIDER_URL, info.getURL(domain));
			if (!Validator.isNull(info.getSocketFactoryName()))
				env.put("java.naming.ldap.factory.socket", info.getSocketFactoryName());
		
			return new InitialLdapContext(env, null);
		} catch (NamingException ex) {
			logger.debug("context error:" + ex);
			throw ex;
		}
	}

	/**
	 * Connect to an ldap server and retrieve attributes corresponding the
	 * the specified loginName
	 * 
	 * @param info
	 * @param loginName
	 * @param mods
	 * @return ldap distinquished name of user; mods updated
	 * @throws NamingException
	 * @throws NoUserByTheNameException
	 */
	protected String getUpdates(LdapInfo info, String loginName, Map mods) throws NamingException, NoUserByTheNameException {
		LdapContext ctx = getContext(info, null, null, info.getDomain(LdapInfo.USERS));
		String dn=null;
		try {
			SearchControls sch = new SearchControls(
					SearchControls.SUBTREE_SCOPE, 1, 0, info.getLdapAttrsArray(LdapInfo.USERS), false, false);

			NamingEnumeration ctxSearch = ctx.search("", info.getFilterString(loginName), sch);
			if (!ctxSearch.hasMore()) {
				throw new NoUserByTheNameException(loginName);
			}
			Binding bd = (Binding)ctxSearch.next();
			getUpdates(info, LdapInfo.USERS, ctx.getAttributes(bd.getName()), mods);
			if (bd.isRelative()) {
				dn = bd.getName() + "," + info.getDomain(LdapInfo.USERS);
			} else {
				dn = bd.getName();
			}

		} finally {
			ctx.close();
		}
		return dn;
	}
	protected void getUpdates(LdapInfo info, int type, Attributes attrs, Map mods)  throws NamingException {
		String []ldapAttrNames = info.getLdapAttrsArray(type);
		Map mapping = info.getAttrMap(type);
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
				mods.put(mapping.get(ldapAttrNames[i]), vals);
			}
		}		
	}

}