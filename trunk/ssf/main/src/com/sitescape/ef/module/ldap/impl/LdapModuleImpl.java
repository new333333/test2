
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

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.jobs.LdapSynchronization;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.ldap.LdapConfig;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.util.Validator;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.util.CollectionUtil;
import com.sitescape.ef.util.DirtyInterceptor;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.util.SZoneConfig;
import com.sitescape.ef.util.SessionUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.module.shared.MapInputData;

import org.dom4j.Element;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
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
public class LdapModuleImpl implements LdapModule {
	protected Log logger = LogFactory.getLog(getClass());
	protected String [] principalAttrs = new String[]{"name", "id", "disabled", "reserved", "foreignName"};
	protected CoreDao coreDao;
	
	protected static final String[] sample = new String[0];
	HashMap defaultProps = new HashMap(); 
	protected HashMap zones = new HashMap();
	protected ProcessorManager processorManager;
	protected TransactionTemplate transactionTemplate;
	protected ProfileModule profileModule;
	public LdapModuleImpl () {
		defaultProps.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
		defaultProps.put("java.naming.security.authentication", "simple");
		defaultProps.put("java.naming.provider.url", "ldap://localhost:389");
		defaultProps.put(LdapModule.OBJECT_CLASS, "objectClass");
    	defaultProps.put(LdapModule.SYNC_JOB, "com.sitescape.ef.jobs.DefaultLdapSynchronization");
	}

	/**
	 * Loaded by Spring context
	 */
    protected CoreDao getCoreDao() {
		return coreDao;
	}
	public void setCoreDao(CoreDao coreDao) {
	    this.coreDao = coreDao;
	}
	/**
	 * Loaded by Spring context
	 */
	protected ProcessorManager getProcessorManager() {
		return processorManager;
	}
	public void setProcessorManager(ProcessorManager processorManager) {
	    this.processorManager = processorManager;
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
	
	public String getLdapProperty(String zoneName, String name) {
		String val = SZoneConfig.getString(zoneName, "ldapConfiguration/property[@name='" + name + "']");
		if (Validator.isNull(val)) {
			val = (String)defaultProps.get(name);
		}
		return val;
	}
	/**
	 * Get the ldap configuration.  This object is stored in the scheduling database.  
	 */
	public LdapConfig getLdapConfig() {		
		return new LdapConfig(getSyncObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneName()));
	}
	/**
	 * Update ldap configuration with scheduler. Only properties specified in the props file will
	 * be modified.
	 * @param zoneId
	 * @param props
	 */
	public void setLdapConfig(final LdapConfig config) {
	       getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	            	getSyncObject().setScheduleInfo(config);
	        		return null;
	        	}});
	}
    protected LdapSynchronization getSyncObject() {
    	String jobClass = getLdapProperty(RequestContextHolder.getRequestContext().getZoneName(), LdapModule.SYNC_JOB);
    	try {
            Class processorClass = ReflectHelper.classForName(jobClass);
            LdapSynchronization job = (LdapSynchronization)processorClass.newInstance();
            return job;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(
                    "Invalid LdapSynchronization class name '" + jobClass + "'",
                    e);
        } catch (InstantiationException e) {
            throw new ConfigurationException(
                    "Cannot instantiate LdapSynchronization of type '"
                            + jobClass + "'");
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(
                    "Cannot instantiate LdapSynchronization of type '"
                            + jobClass + "'");
        }
    }	
	/**
	 * Internal routine that caches ldap properties in more useful form
	 * @param zoneName
	 * @return
	 */
	protected Map getZoneMap(String zoneName) {
		if (zones.containsKey(zoneName)) return (Map)zones.get(zoneName);
		Map zone = new HashMap();
		zone.put("java.naming.factory.initial", getLdapProperty(zoneName, "java.naming.factory.initial"));
		zone.put("java.naming.security.authentication", getLdapProperty(zoneName, "java.naming.security.authentication"));
		zone.put("java.naming.provider.url", getLdapProperty(zoneName, "java.naming.provider.url"));
		zone.put("java.naming.security.principal", getLdapProperty(zoneName, "java.naming.security.principal"));
		zone.put("java.naming.security.credentials", getLdapProperty(zoneName, "java.naming.security.credentials"));
		zone.put("java.naming.ldap.factory.socket", getLdapProperty(zoneName, "java.naming.ldap.factory.socket"));
		zone.put(LdapModule.USER_DOMAIN, getLdapProperty(zoneName, LdapModule.USER_DOMAIN));
		zone.put(LdapModule.GROUP_DOMAIN, getLdapProperty(zoneName, LdapModule.GROUP_DOMAIN));
		zone.put(LdapModule.OBJECT_CLASS, getLdapProperty(zoneName, LdapModule.OBJECT_CLASS));
		
		//get lists from zone config so each set can be overridden
		List objectClasses = new ArrayList();
		Element next;
		List classes = SZoneConfig.getElements("ldapConfiguration/userMapping/objectClass");		
		for (int i=0; i<classes.size(); ++i) {
			next = (Element)classes.get(i);
			objectClasses.add(next.getTextTrim());
		}
		zone.put("userObjectClasses", objectClasses);
		
		Map attributes = new HashMap();
		List mappings = SZoneConfig.getElements("ldapConfiguration/userMapping/mapping");
		for (int i=0; i<mappings.size(); ++i) {
			next = (Element)mappings.get(i);
			attributes.put(next.attributeValue("from"), next.attributeValue("to"));
		}
		
		zone.put("userAttributes", attributes);
		zone.put("userAttributeNames", (String[])(attributes.keySet().toArray(sample)));

		next = SZoneConfig.getElement("ldapConfiguration/userMapping/userAttribute");
		if (next != null) {
			zone.put("userIdAttribute", next.attributeValue("from"));
			zone.put("ssIdAttribute", next.attributeValue("to"));
		} else {
			zone.put("userIdAttribute", "uid");
			zone.put("ssIdAttribute", "loginName");
		}

		classes = SZoneConfig.getElements("ldapConfiguration/groupMapping/objectClass");
		objectClasses = new ArrayList();
		for (int i=0; i<classes.size(); ++i) {
			next = (Element)classes.get(i);
			objectClasses.add(next.getTextTrim());
		}
		zone.put("groupObjectClasses", objectClasses);
		objectClasses = new ArrayList();
		classes = SZoneConfig.getElements("ldapConfiguration/groupMapping/memberAttribute");
		for (int i=0; i<classes.size(); ++i) {
			next = (Element)classes.get(i);
			objectClasses.add(next.getTextTrim());
		}
		zone.put("memberAttributes", objectClasses);

		attributes = new HashMap();
		mappings = SZoneConfig.getElements("ldapConfiguration/groupMapping/mapping");
		for (int i=0; i<mappings.size(); ++i) {
			next = (Element)mappings.get(i);
			attributes.put(next.attributeValue("from"), next.attributeValue("to"));
		}
		zone.put("groupAttributes", attributes);
		zone.put("groupAttributeNames", (String[])(attributes.keySet().toArray(sample)));
		
		zones.put(zoneName, zone);
		return zone;
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

		LdapContext ctx = null;
		LdapConfig info = new LdapConfig(getSyncObject().getScheduleInfo(zoneName));
		Map mods = new HashMap();
		String dn;
		//make sure user exists
		dn = getUpdates(info, loginName, mods);
			
		// Make sure the fetched user dn and password combination can bind
		// against the LDAP server
		try {
			ctx = getContext(zoneName, dn, password, (String)getZoneMap(zoneName).get(LdapModule.USER_DOMAIN));
			ctx.close();
		} catch (Exception e) {
			return false;
		}
		if (!mods.isEmpty()) {
			try {
				updateUser(zoneName, loginName, mods);
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
		LdapConfig info = new LdapConfig(getSyncObject().getScheduleInfo(zoneName));

		Map mods = new HashMap();
		getUpdates(info, loginName, mods);
		updateUser(zoneName, loginName, mods);

	}
	/**
	 * This routine alters group membership without updateing the local caches.
	 * Need to flush cache after use
	 */
	public void syncAll(String zoneName)throws NamingException {
		LdapConfig info = new LdapConfig(getSyncObject().getScheduleInfo(zoneName));
   		LdapContext ctx=null;
   		String dn;
   		Object[] gRow,uRow;
   		Iterator iter;
   		try {
   			String userDomain = getLdapProperty(zoneName, USER_DOMAIN);
   			String groupDomain = getLdapProperty(zoneName, GROUP_DOMAIN);
			ctx = getContext(zoneName, userDomain);
			Map dnUsers  = syncUsers(zoneName, ctx, info, userDomain);
			if (!userDomain.equals(groupDomain)) {
				ctx.close();
				ctx = null;
				ctx = getContext(zoneName, groupDomain);
			}
			Map [] gResults = syncGroups(zoneName, ctx, info, groupDomain);
			if (info.isMembershipSync()) {
				Map dnGroups = (Map)gResults[0];
				Map ldapGroups = (Map)gResults[1];
				//Get map indexed by id
				Set reservedIds = new HashSet();
				buildReserved(dnUsers.values(), reservedIds);
				buildReserved(dnGroups.values(), reservedIds);
				//loop through each ldapGroup that is now in forum
				for (iter=ldapGroups.entrySet().iterator(); iter.hasNext();) {
					Map.Entry entry = (Map.Entry)iter.next();
					dn = (String)entry.getKey();
					gRow = (Object [])dnGroups.get(dn);
					Long groupId = (Long)gRow[1];
					List membership = new ArrayList();
					Attributes lAttrs = (Attributes)entry.getValue();
					List memberAttributes = (List)getZoneMap(zoneName).get("memberAttributes");
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
							//do inside a transaction
							updateMembership(groupId, membership, reservedIds);
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
	protected void buildReserved(Collection rows, Set reservedIds) {
		Object [] row;
		for (Iterator iter=rows.iterator(); iter.hasNext();) {
			row = (Object[])iter.next();
			if ((Boolean)row[3] == Boolean.TRUE) {
				reservedIds.add(row[1]);
			}
		}
		
	}
	

	protected Map syncUsers(String zoneName, LdapContext ctx, LdapConfig info, String userDomain) 
		throws NamingException {
		String ssName;
		Object[] row;
		Map ssUsers = new HashMap();
		Map notInLdap = new HashMap();
		Map ldap_existing = new HashMap();
		Map ldap_new = new HashMap();
		boolean create = info.isUserRegister();
		boolean sync = info.isUserSync();
		Map dnUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		String [] sample = new String[0];
	 
		//get list of users.
		List attrs = coreDao.loadObjects(new ObjectControls(User.class, principalAttrs), new FilterControls("zoneName", zoneName));
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
		Map zoneMap = getZoneMap(zoneName);
		Map userAttributes = (Map)zoneMap.get("userAttributes");
		Set la = new HashSet(userAttributes.keySet());
		String userIdAttribute = (String)zoneMap.get("userIdAttribute");
		String [] userAttributeNames = (String [])zoneMap.get("userAttributeNames");
		la.add(userIdAttribute);
		SearchControls sch = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 0, 0, (String [])la.toArray(sample), false, false);

		NamingEnumeration ctxSearch = ctx.search("", getFilterString(zoneName, (List)zoneMap.get("userObjectClasses")), sch);
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
				userMods.put("zoneName", zoneName);
				ldap_new.put(ssName, userMods); 
				dnUsers.put(dn, new Object[]{ssName, null, new Boolean(false), new Boolean(false), dn});
			}
			//do updates after every 100 users
			if (sync && (ldap_existing.size()%100 == 0) && !ldap_existing.isEmpty()) {
				//doLog("Updating users:", ldap_existing);
				updateUsers(zoneName, ldap_existing);
				ldap_existing.clear();
			}
			//do creates after every 100 users
			if (create && (ldap_new.size()%100 == 0) && !ldap_new.isEmpty()) {
				doLog("Creating users:", ldap_new);
				List results = createUsers(zoneName, ldap_new);
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
			updateUsers(zoneName, ldap_existing);
		}
		if (create && !ldap_new.isEmpty()) {
			doLog("Creating users:", ldap_new);
			List results = createUsers(zoneName, ldap_new);
			for (int i=0; i<results.size(); ++i) {
				User user = (User)results.get(i);
				row = (Object[])dnUsers.get(user.getForeignName());
				row[1] = user.getId();
			}
		}
		
		//if disable is enabled, remove users that were not found in ldap
		if (info.isUserDisable() && !notInLdap.isEmpty()) {
			doLog("Disabling users:", notInLdap);
			disableUsers(zoneName, notInLdap.values());
		}
		return dnUsers;
	}

	protected Map[] syncGroups(String zoneName, LdapContext ctx, LdapConfig info, String groupDomain) 
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
		dnGroups = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		//get list of existing groups.
		List attrs = coreDao.loadObjects(new ObjectControls(Group.class, principalAttrs), new FilterControls("zoneName", zoneName));
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
		Map zoneMap = getZoneMap(zoneName);
		Map groupAttributes = (Map)zoneMap.get("groupAttributes");
		Set la = new HashSet(groupAttributes.keySet());
		String [] groupAttributeNames = (String [])zoneMap.get("groupAttributeNames");
		la.addAll((List)zoneMap.get("memberAttributes"));
		SearchControls sch = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 0, 0, (String [])la.toArray(sample), false, false);
	

		NamingEnumeration ctxSearch = ctx.search("", getFilterString(zoneName, (List)zoneMap.get("groupObjectClasses")), sch);
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
						userMods.put("zoneName", zoneName);
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
				updateGroups(zoneName, ldap_existing);
				ldap_existing.clear();
			}
			//do creates after every 100 users
			if (create && (ldap_new.size()%100 == 0) && !ldap_new.isEmpty()) {
				doLog("Creating Groups:", ldap_new);
				List results = createGroups(zoneName, ldap_new);
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
			updateGroups(zoneName, ldap_existing);
		}
		if (create && !ldap_new.isEmpty()) {
			doLog("Creating Groups:", ldap_new);
			List results = createGroups(zoneName, ldap_new);
			for (int i=0; i<results.size(); ++i) {
				Group group = (Group)results.get(i);
				row = (Object[])dnGroups.get(group.getForeignName());
				row[1] = group.getId();
			}
		}
	
		//if disable is enabled, remove groups that were not found in ldap
		if (info.isGroupDisable() && !notInLdap.isEmpty()) {
			doLog("Disabling groups:", notInLdap);
			disableGroups(zoneName,notInLdap.values());
		}
		return new Map[]{dnGroups,ldapGroups};
	}
	protected void doLog(String caption, Map names) {
		if (logger.isInfoEnabled()) {
			logger.info(caption);
			for (Iterator iter=names.keySet().iterator(); iter.hasNext();) {
				logger.info(iter.next());
			}
		}
	}

	protected LdapContext getContext(String zoneName, String domain) throws NamingException { 
		String user = getLdapProperty(zoneName, Context.SECURITY_PRINCIPAL);
		String pwd = getLdapProperty(zoneName,	Context.SECURITY_CREDENTIALS);
		return getContext(zoneName, user, pwd, domain);
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
	protected LdapContext getContext(String zoneName, String user, String pwd, String domain) throws NamingException { 
		// Load user from ldap
		try {
			Hashtable env = new Hashtable();

			env.put(Context.INITIAL_CONTEXT_FACTORY, getLdapProperty(zoneName, Context.INITIAL_CONTEXT_FACTORY));
			if (!Validator.isNull(user) && !Validator.isNull(pwd)) {
				env.put(Context.SECURITY_PRINCIPAL, user);
				env.put(Context.SECURITY_CREDENTIALS, pwd);		
			} 
			env.put(Context.SECURITY_AUTHENTICATION, getLdapProperty(zoneName, Context.SECURITY_AUTHENTICATION));
			env.put(Context.PROVIDER_URL, getLdapProperty(zoneName, Context.PROVIDER_URL) + "/" + domain);
			String socketFactory = getLdapProperty(zoneName, "java.naming.ldap.factory.socket"); 
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
		String userDomain = (String)getZoneMap(info.getZoneName()).get(LdapModule.USER_DOMAIN);
		LdapContext ctx = getContext(info.getZoneName(), userDomain);
		String dn=null;
		Map userAttributes = (Map)getZoneMap(info.getZoneName()).get("userAttributes");
		String [] userAttributeNames = (String [])getZoneMap(info.getZoneName()).get("userAttributeNames");

		try {
			SearchControls sch = new SearchControls(
					SearchControls.SUBTREE_SCOPE, 1, 0, (String[])(userAttributes.keySet().toArray(sample)), false, false);

			NamingEnumeration ctxSearch = ctx.search("", getFilterString(info.getZoneName(), loginName), sch);
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
				mods.put(mapping.get(ldapAttrNames[i]), vals.toArray(sample));
			}
		}		
	}
	protected String getFilterString(String zoneName, String userId) {
		StringBuffer filter = new StringBuffer();
		filter.append("(" + (String)getZoneMap(zoneName).get("userIdAttribute") + "=" + userId + ")");		

		return "(&" + getFilterString(zoneName, (List)getZoneMap(zoneName).get("userObjectClasses")) + filter.toString() + ")";		
	}
	protected String getFilterString(String zoneName, List values) {
		StringBuffer filter = new StringBuffer();
		String objectClassAttribute = (String)getZoneMap(zoneName).get(LdapModule.OBJECT_CLASS);
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
	protected void updateUser(String zoneName, String loginName, Map mods) throws NoUserByTheNameException {

		User profile = coreDao.findUserByName(loginName, zoneName); 
 		IndexSynchronizationManager.begin();
		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	profile.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
		processor.syncEntry(profile, new MapInputData(mods));
		IndexSynchronizationManager.applyChanges();
	}	
	/**
	 * Update users with their own map of updates
	 * @param users - Map indexed by user id, value is map of updates for a user
	 */
	protected void updateUsers(String zoneName, final Map users) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
	   	List foundEntries = coreDao.loadUsers(users.keySet(), zoneName);
	   	Map entries = new HashMap();
	   	for (int i=0; i<foundEntries.size(); ++i) {
	   		User u = (User)foundEntries.get(i);
	   		entries.put(u, new MapInputData((Map)users.get(u.getId())));
	   	}
	   	IndexSynchronizationManager.begin();
	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    processor.syncEntries(entries);
		IndexSynchronizationManager.applyChanges();
	}

    /**
     * Update groups with their own updates
     * @param groups - Map keyed by group id, value is map of updates for a group
     */    
	protected void updateGroups(String zoneName, final Map groups) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
		List foundEntries = coreDao.loadGroups(groups.keySet(), RequestContextHolder.getRequestContext().getZoneName());
	   	Map entries = new HashMap();
	   	for (int i=0; i<foundEntries.size(); ++i) {
	   		Group g = (Group)foundEntries.get(i);
	   		entries.put(g, new MapInputData((Map)groups.get(g.getId())));
	   	}
	   	IndexSynchronizationManager.begin();
	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    processor.syncEntries(entries);
		IndexSynchronizationManager.applyChanges();
    }
    protected void updateMembership(Long groupId, Collection newMembers, final Collection reservedIds) {
		//have a list of users, now compare with what exists already
		List oldMembers = getCoreDao().getMembership(groupId);
		final Set newM = CollectionUtil.differences(newMembers, oldMembers);
		final Set remM = CollectionUtil.differences(oldMembers, newMembers);

        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		//only remove entries that are not reserved
        		for (Iterator iter=remM.iterator(); iter.hasNext();) {
        			Membership c = (Membership)iter.next();
        			if (!reservedIds.contains(c.getUserId())) {
        				getCoreDao().delete(c);
        			}
        		}
		
        		getCoreDao().save(newM);
        		return null;
        	}});
		SessionFactory sF = (SessionFactory)SpringContextUtil.getBean("sessionFactory");
		sF.evictCollection("com.sitescape.ef.domain.Principal.HMemberOf");
		
    }
    /**
     * Create users.  
     * @param zoneName
     * @param users - Map keyed by user id, value is map of attributes
     * @return
     */
    protected List createUsers(String zoneName, Map users) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
		List newUsers = new ArrayList();
		for (Iterator i=users.values().iterator(); i.hasNext();) {
			newUsers.add(new MapInputData((Map)i.next()));
		}
	   	IndexSynchronizationManager.begin();
	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    newUsers = processor.syncNewEntries(pf, null, User.class, newUsers);
		IndexSynchronizationManager.applyChanges();
		return newUsers;
     }
    /**
     * Create groups.
     * @param zoneName
     * @param groups - Map keyed by user id, value is map of attributes
     * @return
     */
    protected List createGroups(String zoneName, Map groups) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
		List newGroups = new ArrayList();
		for (Iterator i=groups.values().iterator(); i.hasNext();) {
			newGroups.add(new MapInputData((Map)i.next()));
		}
	   	IndexSynchronizationManager.begin();
	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    newGroups = processor.syncNewEntries(pf, null, Group.class, newGroups);
		IndexSynchronizationManager.applyChanges();
		return newGroups;
    }
    protected void disableUsers(final String zoneName, final Collection ids) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		coreDao.disablePrincipals(ids, zoneName);
        		return null;
        	}});
        //remove from index
   		IndexSynchronizationManager.begin();
   		for (Iterator i=ids.iterator(); i.hasNext();) {
   	   	    IndexSynchronizationManager.deleteDocument(BasicIndexUtils.makeUid("com.sitescape.ef.domain.User", (Long)i.next()));  			
   		}
   		IndexSynchronizationManager.applyChanges();
   	    
    }
    protected void disableGroups(final String zoneName, final Collection ids) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		coreDao.disablePrincipals(ids, zoneName);
        		return null;
        	}});
        //remove from index
   		IndexSynchronizationManager.begin();
   		for (Iterator i=ids.iterator(); i.hasNext();) {
   	   	    IndexSynchronizationManager.deleteDocument(BasicIndexUtils.makeUid("com.sitescape.ef.domain.Group", (Long)i.next()));  			
   		}
   		IndexSynchronizationManager.applyChanges();
   }
 }