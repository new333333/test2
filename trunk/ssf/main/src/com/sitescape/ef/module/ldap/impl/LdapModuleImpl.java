
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
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.ldap.LdapConfig;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.NoGroupByTheIdException;
import com.sitescape.ef.util.XmlClassPathConfigFiles;
import org.dom4j.Document;
import org.dom4j.Element;
/**
 * @author Janet McCann
 *
 */
public class LdapModuleImpl extends CommonDependencyInjection implements LdapModule {
	protected Log logger = LogFactory.getLog(getClass());
	//pointer to self, so we can wrap scheduled sync operations in write transactions
	protected ProfileModule profileModule;
	protected String [] principalAttrs = new String[]{"name", "id", "disabled", "reserved", "foreignName"};

	protected String socketFactory=null;
	protected String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
	protected String authType = "simple";
	protected String authPrincipal = null;
	protected String authPassword = null;
	protected String userDomain,groupDomain;
	protected String connectUrl = "ldap://localhost:389";
	protected String objectClassAttribute="objectClass";
	protected List userObjectClasses = new ArrayList();
	protected List groupObjectClasses = new ArrayList();
	protected Map groupAttributes = new HashMap();
	protected Map userAttributes = new HashMap();
	protected static final String[] sample = new String[0];
	protected String [] userAttributeNames;
	protected String [] groupAttributeNames;
	protected String userIdAttribute = "uid";
	protected String ssIdAttribute = "name";
	protected List memberAttributes = new ArrayList();
	protected boolean membersIgnoreCase=true;
	protected XmlClassPathConfigFiles configDocs;
	public LdapModuleImpl () {

	}
	//called after bean properties are initialized
	public void init() {
		Document doc = configDocs.getAsDom4jDocument(0);
		Element root = doc.getRootElement();
		Element next;
		Element ctx = root.element("initial-context");
		List props = ctx.elements("property");
		for (int i=0; i<props.size(); ++i) {
			next = (Element)props.get(i);
			String name = next.attributeValue("name");
			if (name.equals("authType")) {
				authType = next.getText();
			} else if (name.equals("authPrincipal")) {
				authPrincipal = next.getText();
			} else if (name.equals("authPassword")) {
				authPassword = next.getText();
			} else if (name.equals("userDomain")) {
				userDomain = next.getText();
			} else if (name.equals("groupDomain")) {
				groupDomain = next.getText();
			} else if (name.equals("connectUrl")) {
				connectUrl = next.getText();
			} else if (name.equals("contextFactory")) {
				contextFactory = next.getText();
			} else if (name.equals("socketFactory")) {
				socketFactory = next.getText();
			} else if (name.equals("objectClassAttribute")) {
				objectClassAttribute = next.getText();
			} else if (name.equals("membersIgnoreCase")) {
				membersIgnoreCase = Boolean.getBoolean(next.getText());
			} 
		}
		
		ctx = root.element("user-mappings");
		List classes = ctx.elements("objectClass");
		for (int i=0; i<classes.size(); ++i) {
			next = (Element)classes.get(i);
			userObjectClasses.add(next.getTextTrim());
		}
		List mappings = ctx.elements("mapping");
		for (int i=0; i<mappings.size(); ++i) {
			next = (Element)mappings.get(i);
			userAttributes.put(next.attributeValue("from"), next.attributeValue("to"));
		}
		userAttributeNames = (String[])(userAttributes.keySet().toArray(sample));

		next = ctx.element("userAttribute");
		userIdAttribute = next.attributeValue("from");
		ssIdAttribute = next.attributeValue("to");
		ctx = root.element("group-mappings");
		classes = ctx.elements("objectClass");
		for (int i=0; i<classes.size(); ++i) {
			next = (Element)classes.get(i);
			groupObjectClasses.add(next.getTextTrim());
		}
		classes = ctx.elements("memberAttribute");
		for (int i=0; i<classes.size(); ++i) {
			next = (Element)classes.get(i);
			memberAttributes.add(next.getTextTrim());
		}
		
		mappings = ctx.elements("mapping");
		for (int i=0; i<mappings.size(); ++i) {
			next = (Element)mappings.get(i);
			groupAttributes.put(next.attributeValue("from"), next.attributeValue("to"));
		}
		groupAttributeNames = (String[])(groupAttributes.keySet().toArray(sample));
		
	}
	public void setConfigDocs(XmlClassPathConfigFiles configDocs) {
		this.configDocs = configDocs;
	}
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
	public LdapConfig getLdapConfig() {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		Workspace ws = (Workspace)coreDao.findTopWorkspace(zoneName);
		return ws.getLdapConfig();
	}
	/**
	 * Update ldap configuration.  Only properties specified in the props file will
	 * be modified.
	 * @param zoneId
	 * @param props
	 */
	public void modifyLdapConfig(Map props) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		Workspace ws = (Workspace)coreDao.findTopWorkspace(zoneName);
    	ObjectBuilder.updateObject(ws.getLdapConfig(), props);
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
		LdapConfig info = ws.getLdapConfig();
		Map mods = new HashMap();
		String dn;
		//make sure user exists
		dn = getUpdates(info, loginName, mods);
			
		// Make sure the fetched user dn and password combination can bind
		// against the LDAP server
		try {
			ctx = getContext(dn, password, userDomain);
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
	 * Update a ssf user with an ldap person.  
	 * @param zoneName
	 * @param loginName
	 * @throws NoUserByTheNameException
	 * @throws NamingException
	 */
	public void syncUser(String zoneName, String loginName) 
		throws NoUserByTheNameException, NamingException {
		Workspace ws = (Workspace)coreDao.findTopWorkspace(zoneName);
		LdapConfig info = ws.getLdapConfig();
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
		LdapConfig info = ws.getLdapConfig();
   		LdapContext ctx=null;
   		String dn;
   		Object[] gRow,uRow;
   		Iterator iter;
   		try {
			ctx = getContext(null, null, userDomain);
			Map dnUsers  = syncUsers(ws, ctx, info);
			if (!userDomain.equals(groupDomain)) {
				ctx.close();
				ctx = null;
				ctx = getContext(null, null, groupDomain);
			}
			Map [] gResults = syncGroups(ws, ctx, info);
			if (info.isMembershipSync()) {
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
					Attributes lAttrs = (Attributes)entry.getValue();

					for (int i=0; i<memberAttributes.size(); i++) {
						Attribute att = lAttrs.get((String)memberAttributes.get(i));
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
		EntryBuilder.updateEntry(profile, mods);

	}
	protected Map syncUsers(Workspace ws, LdapContext ctx, LdapConfig info) 
		throws NamingException {
		String ssName;
		Object[] row;
		Map ssUsers = new HashMap();
		Map notInLdap = new HashMap();
		Map ldap_existing = new HashMap();
		Map ldap_new = new HashMap();
		boolean create = info.isUserRegister();
		boolean sync = info.isUserSync();
		Map dnUsers;
		String [] sample = new String[0];
		if (membersIgnoreCase) dnUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
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
		Set la = new HashSet(userAttributes.keySet());
		la.add(userIdAttribute);
		SearchControls sch = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 0, 0, (String [])la.toArray(sample), false, false);

		NamingEnumeration ctxSearch = ctx.search("", getFilterString(userObjectClasses), sch);
		while (ctxSearch.hasMore()) {
			Binding bd = (Binding)ctxSearch.next();
			Attributes lAttrs = ctx.getAttributes(bd.getName());
			Attribute id=null;
			id = lAttrs.get(userIdAttribute);
			if (id == null) continue;
			//map ldap id to sitescapeName
			ssName = idToName((String)id.get());
			if (ssName == null) continue;
			String dn;
			if (bd.isRelative()) {
				dn = (bd.getName().trim() + "," + userDomain);
			} else {
				dn = bd.getName().trim();
			}
			row = (Object[])ssUsers.get(ssName);
			if (row != null) {
				if (sync) {
					Map userMods = new HashMap();
					getUpdates(userAttributeNames, userAttributes, lAttrs, userMods);
					userMods.put("foreignName", dn);
					ldap_existing.put(row[1], userMods);
				} 
				//exists in ldap, remove from missing list
				notInLdap.remove(ssName);
				//setup distinquished name for group sync
				dnUsers.put(dn, row);
			} else if (create) {
				Map userMods = new HashMap();
				getUpdates(userAttributeNames, userAttributes, lAttrs, userMods);
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
		if (info.isUserDisable() && !notInLdap.isEmpty()) {
			doLog("Disabling users:", notInLdap);
			profileModule.bulkDisableUsers(notInLdap.values());
		}
		return dnUsers;
	}

	protected Map[] syncGroups(Workspace ws, LdapContext ctx, LdapConfig info) 
		throws NamingException {
		String ssName;
		Object[] row;
		//ssname=> forum info
		Map ssGroups = new HashMap();
		//ssname groups that don't exists in ldap
		Map notInLdap = new HashMap();
		Map ldap_existing = new HashMap();
		Map ldap_new = new HashMap();
		boolean create = info.isGroupRegister();
		boolean sync = info.isGroupSync();
		//ldap group dn that have forum equivalents, contains membership attrs
		Map ldapGroups = new HashMap();
		String [] sample = new String[0];
		//ldap dn => forum info
		Map dnGroups;
		if (membersIgnoreCase) dnGroups = new TreeMap(String.CASE_INSENSITIVE_ORDER);
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
		Set la = new HashSet(groupAttributes.keySet());
		la.addAll(memberAttributes);
		SearchControls sch = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 0, 0, (String [])la.toArray(sample), false, false);
	

		NamingEnumeration ctxSearch = ctx.search("", getFilterString(groupObjectClasses), sch);
		while (ctxSearch.hasMore()) {
			Binding bd = (Binding)ctxSearch.next();
			Attributes lAttrs = ctx.getAttributes(bd.getName());
			String dn;
			if (bd.isRelative()) {
				dn = bd.getName().trim() + "," + groupDomain;
			} else {
				dn = bd.getName().trim();
			}
			//see if group mapping exists
			row = (Object [])dnGroups.get(dn);
			if (row == null) { 
				if (create) {
					//see if name already exists
					ssName = dnToGroupName(dn);
					row = (Object[])ssGroups.get(ssName);
					if (row != null) {
						logger.error(dn + " Cannot create; ldap name mapped to an existing group without a foreignName mapping " + ssName);
					} else {
						Map userMods = new HashMap();
						getUpdates(groupAttributeNames, groupAttributes, lAttrs, userMods);
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
					getUpdates(groupAttributeNames, groupAttributes, lAttrs, userMods);
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
		if (info.isGroupDisable() && !notInLdap.isEmpty()) {
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
	protected LdapContext getContext(String user, String pwd, String domain) throws NamingException { 
		// Load user from ldap
		try {
			Hashtable env = new Hashtable();

			env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
			if (!Validator.isNull(user)) {
				env.put(Context.SECURITY_PRINCIPAL, authPrincipal);
				env.put(Context.SECURITY_CREDENTIALS, authPassword);			
			} else if (!Validator.isNull(authPrincipal) && !Validator.isNull(authPassword)) {
				env.put(Context.SECURITY_PRINCIPAL, authPrincipal);
				env.put(Context.SECURITY_CREDENTIALS, authPassword);
			}
			env.put(Context.SECURITY_AUTHENTICATION, authType);
			env.put(Context.PROVIDER_URL, connectUrl + "/" + domain);
			if (!Validator.isNull(socketFactory))
				env.put("java.naming.ldap.factory.socket", socketFactory);
		

			return new InitialLdapContext(env, null);
		} catch (NamingException ex) {
			logger.debug("context error:" + ex);
			throw ex;
		}
	}

	/**
	 * Connect to an ldap server and retrieve attributes corresponding to
	 * the specified loginName
	 * 
	 * @param info
	 * @param loginName
	 * @param mods
	 * @return ldap distinquished name of user; mods updated
	 * @throws NamingException
	 * @throws NoUserByTheNameException
	 */
	protected String getUpdates(LdapConfig info, String loginName, Map mods) throws NamingException, NoUserByTheNameException {
		LdapContext ctx = getContext(null, null, userDomain);
		String dn=null;
		try {
			SearchControls sch = new SearchControls(
					SearchControls.SUBTREE_SCOPE, 1, 0, (String[])(userAttributes.keySet().toArray(sample)), false, false);

			NamingEnumeration ctxSearch = ctx.search("", getFilterString(loginName), sch);
			if (!ctxSearch.hasMore()) {
				throw new NoUserByTheNameException(loginName);
			}
			Binding bd = (Binding)ctxSearch.next();
			getUpdates(userAttributeNames, userAttributes,  ctx.getAttributes(bd.getName()), mods);
			if (bd.isRelative()) {
				dn = bd.getName() + "," + userDomain;
			} else {
				dn = bd.getName();
			}

		} finally {
			ctx.close();
		}
		return dn;
	}
	protected void getUpdates(String []ldapAttrNames, Map mapping, Attributes attrs, Map mods)  throws NamingException {
			
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
	protected String getFilterString(String userId) {
		StringBuffer filter = new StringBuffer();
		filter.append("(" + userIdAttribute + "=" + userId + ")");		

		return "(&" + getFilterString(userObjectClasses) + filter.toString() + ")";		
	}
	protected String getFilterString(List values) {
		StringBuffer filter = new StringBuffer();
		if (values.size() > 1) {
			filter.append("(|");
			for (int i=0; i<values.size(); ++i) {
				filter.append("(" + objectClassAttribute + "=" + values.get(i) + ")");
			}
			filter.append(")");
		}  else {
			filter.append("(" + objectClassAttribute + "=" + values.get(0) + ")");			
		}
		return filter.toString();		
	}
	protected String nameToId(String name) {
		return name;
	}
	protected String idToName(String id) {
		return id;
	}
	protected String dnToGroupName(String dn) {
		return dn;
	}
}