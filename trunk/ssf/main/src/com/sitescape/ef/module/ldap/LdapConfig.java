
package com.sitescape.ef.module.ldap;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.ef.domain.Workspace;
import com.sitescape.util.Validator;
import com.sitescape.util.GetterUtil;
/**
 * @author Janet McCann
 *
 * Manage the ldap properties for a workspace. 
 */
public class LdapInfo {
	public static final int USERS=1;
	public static final int GROUPS=2;
	public static final int MEMBERSHIP=2;

	public static final String SOCKET_FACTORY = "ldap.connect.socket.factory";
	public static final String CONTEXT_FACTORY = "ldap.connect.context.factory";
	public static final String SSL = "ldap.connect.SSL";
	public static final String HOST = "ldap.connect.host";
	public static final String PORT = "ldap.connect.port";
	public static final String AUTH_PRINCIPAL = "ldap.auth.principal";
	public static final String AUTH_PASSWORD = "ldap.auth.password";
	public static final String AUTH_TYPE = "ldap.auth.type";
	public static final String ENABLE_LOGIN = "ldap.login.enable";
	public static final String ENABLE_LOGIN_SYNC = "ldap.login.sync.enable";
	public static final String SCHEDULE = "ldap.schedule";
	public static final String ENABLE_SCHEDULE = "ldap.schedule.enable";
	
	public static final String DISABLE_USERS = "ldap.users.disable";
	public static final String CREATE_USERS = "ldap.users.enable";
	public static final String SYNC_USERS = "ldap.users.sync";
	public static final String USERS_ID = "ldap.users.identity";
	public static final String USERS_DOMAIN = "ldap.users.domain";
	public static final String USERS_OBJECT_CLASS = "ldap.users.object.class";
	public static final String USERS_OBJECT_CLASS_VALUES = "ldap.users.object.class.values";
	public static final String USERS_ATTR_MAP = "ldap.users.attributes";
	
	public static final String SYNC_MEMBERSHIP = "ldap.membership.sync";
	public static final String DISABLE_GROUPS = "ldap.groups.disable";
	public static final String CREATE_GROUPS = "ldap.groups.enable";
	public static final String SYNC_GROUPS = "ldap.groups.sync";
	public static final String GROUPS_DOMAIN = "ldap.groups.domain";
	public static final String GROUPS_ATTR_MAP = "ldap.groups.attributes";
	public static final String GROUPS_OBJECT_CLASS_VALUES = "ldap.groups.object.class.values";

	public static final String MEMBERS_ID = "ldap.members.identity";
	public static final String MEMBERSHIP_IGNORECASE = "ldap.members.names.ignorecase";
	
	public static final String[] LDAPPROPS = new String[]{SOCKET_FACTORY,CONTEXT_FACTORY,SSL,HOST,PORT,ENABLE_LOGIN,ENABLE_LOGIN_SYNC,
			SCHEDULE,ENABLE_SCHEDULE,AUTH_PRINCIPAL,AUTH_PASSWORD,AUTH_TYPE,
			DISABLE_USERS,CREATE_USERS,SYNC_USERS,USERS_OBJECT_CLASS,USERS_OBJECT_CLASS_VALUES,
			USERS_ID,USERS_DOMAIN,USERS_ATTR_MAP,
			DISABLE_GROUPS,CREATE_GROUPS,SYNC_GROUPS,GROUPS_OBJECT_CLASS_VALUES,
			GROUPS_DOMAIN,GROUPS_ATTR_MAP,
			MEMBERS_ID,SYNC_MEMBERSHIP,MEMBERSHIP_IGNORECASE};
	
	private static final String HOST_DEFAULT = "localhost";
	private static final String[] USERS_ID_DEFAULT = new String[]{"uid"};
	private static final String[] MEMBERS_ID_DEFAULT = new String[]{"member", "uniqueMember"};
	private static final String OBJECT_CLASS_DEFAULT = "objectClass";
	private static final String[] USERS_OBJECT_CLASS_VALUES_DEFAULT = new String []{"person","inetOrgPerson","organizationalPerson","residentialPerson"};
	private static final String[] GROUPS_OBJECT_CLASS_VALUES_DEFAULT = new String []{"group","groupOfUniqueNames","groupOfNames"};
	    
	private static final String AUTH_TYPE_DEFAULT = "simple";
	private static final String CONTEXT_FACTORY_DEFAULT = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String SCHEDULE_DEFAULT = "0 15 2 ? * mon-fri *";
	private Workspace workspace;
	
	private static final String[] sample = new String[0];

	public LdapInfo (Workspace workspace) {
		//initialize with workspace properties
		this.workspace = workspace;
	}

	public String[] getLdapAttrsArray(int type) {
		return (String[])(getAttrMap(type).keySet().toArray(sample));
		
	}
	public Set getLdapAttrs(int type) {
		return getAttrMap(type).keySet();
		
	}
	public Map getAttrMap(int type) {
		
		Map attrMap;
		if (type == USERS) {
			attrMap = (Map)workspace.getProperty(USERS_ATTR_MAP);
		
			if (attrMap == null) {
				attrMap = new HashMap();
				attrMap.put("cn", "title");
				attrMap.put("commonName", "title");
				attrMap.put("mail", "emailAddress");
				attrMap.put("gn", "firstName");
				attrMap.put("givenName", "firstName");
				attrMap.put("sn", "lastName");
				attrMap.put("surname", "lastName");
				attrMap.put("telephoneNumber", "phone");
				attrMap.put("description", "description");
			}
		} else {
			attrMap = (Map)workspace.getProperty(GROUPS_ATTR_MAP);
			
			if (attrMap == null) {
				attrMap = new HashMap();
				attrMap.put("cn", "title");
				attrMap.put("commonName", "title");
				attrMap.put("description", "description");
			}
		}
		return attrMap;
	}
	public void setAttrMap(int type, Map attrMap) {
		if (type == USERS) {
			workspace.setProperty(USERS_ATTR_MAP, attrMap);
		} else {
			workspace.setProperty(GROUPS_ATTR_MAP, attrMap);
		}
	}

	public String getDomain(int type) {
		if (type == USERS) {
			return GetterUtil.getString((String)workspace.getProperty(USERS_DOMAIN));
		} else {
			String val = GetterUtil.getString((String)workspace.getProperty(GROUPS_DOMAIN));
			if (Validator.isNull(val)) return getDomain(USERS);
			return val;
		}
	}
	public void setDomain(int type, String domain) {
		if (type == USERS) {
			workspace.setProperty(USERS_DOMAIN, domain);
		} else {
			workspace.setProperty(GROUPS_DOMAIN, domain);			
		}
	}
	public String getHost() {
		return GetterUtil.get((String)workspace.getProperty(HOST), HOST_DEFAULT);
	}
	public void setHost(String host) {
		workspace.setProperty(HOST, host);
	}
	public int getPort() {	
		return GetterUtil.get((String)workspace.getProperty(PORT), 389);
	}
	public void setPort(int port) {
		workspace.setProperty(PORT, Integer.toString(port));
	}
	public boolean isSSL() {
		return GetterUtil.get((String)workspace.getProperty(SSL), false);
	}
	public void setSSL(boolean ssl) {
		workspace.setProperty(SSL, Boolean.toString(ssl));
	}
	public String getAuthPrincipal() {
		return GetterUtil.getString((String)workspace.getProperty(AUTH_PRINCIPAL));
	}
	public void setAuthPrincipal(String authPrincipal) {
		workspace.setProperty(AUTH_PRINCIPAL, authPrincipal);
	}
	public String getAuthPassword() {
		return GetterUtil.getString((String)workspace.getProperty(AUTH_PASSWORD));
	}
	public void setAuthPassword(String authPassword) {
		workspace.setProperty(AUTH_PASSWORD, authPassword);
	}
	public String[] getUserIdAttrs() {
		String [] ids = (String[])workspace.getProperty(USERS_ID);
		if ((ids == null)|| (ids.length == 0))  return USERS_ID_DEFAULT;			
		return ids;
	}
	public void setUserIdAttrs(String[] idAttrs) {
		if ((idAttrs == null) || (idAttrs.length == 0)) {
			workspace.setProperty(USERS_ID, USERS_ID_DEFAULT);		
		} else {
			workspace.setProperty(USERS_ID, idAttrs);
		}
	}
	public String[] getMemberIdAttrs() {
		String [] ids = (String[])workspace.getProperty(MEMBERS_ID);
		if ((ids == null)|| (ids.length == 0))  return MEMBERS_ID_DEFAULT;			
		return ids;
	}
	public void setMemberIdAttrs(String []memberIdAttrs) {
		if ((memberIdAttrs == null) || (memberIdAttrs.length == 0)) {
			workspace.setProperty(MEMBERS_ID, MEMBERS_ID_DEFAULT);		
		} else {
			workspace.setProperty(MEMBERS_ID, memberIdAttrs);
		}				
	}
	public String getObjectClassName() {
		return GetterUtil.get((String)workspace.getProperty(USERS_OBJECT_CLASS), OBJECT_CLASS_DEFAULT);
	}
	public void setObjectClassName(String objectClassName) {
		if (Validator.isNull(objectClassName)) {
			workspace.setProperty(USERS_OBJECT_CLASS, OBJECT_CLASS_DEFAULT);
		} else {
			workspace.setProperty(USERS_OBJECT_CLASS, objectClassName);
		}
	}
	public String[] getObjectClassValues(int type) {
		String []objectClassValues;
		if (type == USERS) {
			objectClassValues = (String[])workspace.getProperty(USERS_OBJECT_CLASS_VALUES);
			if ((objectClassValues == null) || (objectClassValues.length == 0)) return USERS_OBJECT_CLASS_VALUES_DEFAULT;
		} else {
			objectClassValues = (String[])workspace.getProperty(GROUPS_OBJECT_CLASS_VALUES);
			if ((objectClassValues == null) || (objectClassValues.length == 0)) return GROUPS_OBJECT_CLASS_VALUES_DEFAULT;
		}
		return objectClassValues;
	}
	public void setObjectClassValues(int type, String []objectClassValues) {
		if (type == USERS) {
			if ((objectClassValues == null) || (objectClassValues.length == 0)) {
				workspace.setProperty(USERS_OBJECT_CLASS_VALUES, USERS_OBJECT_CLASS_VALUES_DEFAULT);
			} else {
				workspace.setProperty(USERS_OBJECT_CLASS_VALUES, objectClassValues);
			}
		} else {
			if ((objectClassValues == null) || (objectClassValues.length == 0)) {
				workspace.setProperty(GROUPS_OBJECT_CLASS_VALUES, GROUPS_OBJECT_CLASS_VALUES_DEFAULT);
			} else {
				workspace.setProperty(GROUPS_OBJECT_CLASS_VALUES, objectClassValues);
			}			
		}
	}

	public String getURL(String domain) {
		if (!isSSL()) {
			return "ldap://" + getHost() + ":" + getPort() + "/" + domain;
		} else {
			return "ldaps://" + getHost() + ":" + getPort() + "/" + domain;
		}
	}
	public String getFilterString(String userId) {
		StringBuffer filter = new StringBuffer();
		String [] values = getUserIdAttrs();
		if (values.length > 1) {
			filter.append("(|");
			for (int i=0; i<values.length; ++i) {
				filter.append("(" + values[i] + "=" + userId + ")");
			}
			filter.append(")");
		}  else {
			filter.append("(" + values[0] + "=" + userId + ")");		
		}

		return "(&" + getFilterString(USERS) + filter.toString() + ")";		
	}
	public String getFilterString(int type) {
		StringBuffer filter = new StringBuffer();
		String [] values = getObjectClassValues(type);
		if (values.length > 1) {
			filter.append("(|");
			for (int i=0; i<values.length; ++i) {
				filter.append("(" + getObjectClassName() + "=" + values[i] + ")");
			}
			filter.append(")");
		}  else {
			filter.append("(" + getObjectClassName() + "=" + values[0] + ")");			
		}
		return filter.toString();		
	}
	public String getAuthType() {
		return GetterUtil.get((String)workspace.getProperty(AUTH_TYPE), AUTH_TYPE_DEFAULT);
	}
	public void setAuthType(String authType) {
		if (authType == null) {
			workspace.setProperty(AUTH_TYPE, AUTH_TYPE_DEFAULT);
		} else {
			workspace.setProperty(AUTH_TYPE, authType);
		}
	}
	public boolean isScheduleEnabled() {
		return GetterUtil.get((String)workspace.getProperty(ENABLE_SCHEDULE), false);
	}
	public void setScheduleEnabled(boolean scheduleEnabled) {
		workspace.setProperty(ENABLE_SCHEDULE, Boolean.toString(scheduleEnabled));
	}
	public boolean isAutoLogin() {
		return GetterUtil.get((String)workspace.getProperty(ENABLE_LOGIN), false);
	}
	public void setAutoLogin(boolean autoLogin) {
		workspace.setProperty(ENABLE_LOGIN, Boolean.toString(autoLogin));
	}
	public boolean isLoginSync() {
		return GetterUtil.get((String)workspace.getProperty(ENABLE_LOGIN_SYNC), false);
	}
	public void setLoginSync(boolean loginSync) {
		workspace.setProperty(ENABLE_LOGIN_SYNC, Boolean.toString(loginSync));
	}
	public boolean isMembersIgnoreCase() {
		return GetterUtil.get((String)workspace.getProperty(MEMBERSHIP_IGNORECASE), true);
	}
	public void setMembersIgnoreCase(boolean ignoreCase) {
		workspace.setProperty(MEMBERSHIP_IGNORECASE, Boolean.toString(ignoreCase));
	}
	public boolean isAutoDisable(int type) {
		if (type == USERS) {
			return GetterUtil.get((String)workspace.getProperty(DISABLE_USERS), false);
		} else {
			return GetterUtil.get((String)workspace.getProperty(DISABLE_GROUPS), false);
		}
	}
	public void setAutoDisable(int type, boolean disable) {
		if (type == USERS) {
			workspace.setProperty(DISABLE_USERS, Boolean.toString(disable));
		} else {
			workspace.setProperty(DISABLE_GROUPS, Boolean.toString(disable));			
		}
	}
	public boolean isAutoCreate(int type) {
		if (type == USERS) {
			return  GetterUtil.get((String)workspace.getProperty(CREATE_USERS), false);
		} else {
			return  GetterUtil.get((String)workspace.getProperty(CREATE_GROUPS), false);			
		}
	}
	public void setAutoCreate(int type, boolean create) {
		if (type == USERS) {
			workspace.setProperty(CREATE_USERS, Boolean.toString(create));
		} else if (type == GROUPS) {
			workspace.setProperty(CREATE_GROUPS, Boolean.toString(create));
		} else if (type == MEMBERSHIP) {
			workspace.setProperty(SYNC_MEMBERSHIP, Boolean.toString(create));
		}

	}
	public boolean isAutoSync(int type) {
		if (type == USERS) {
			return GetterUtil.get((String)workspace.getProperty(SYNC_USERS), false);
		} else if (type == GROUPS) {
			return GetterUtil.get((String)workspace.getProperty(SYNC_GROUPS), false);			
		} else if (type == MEMBERSHIP) {
			return GetterUtil.get((String)workspace.getProperty(SYNC_MEMBERSHIP), false);			
			
		} else return false;
	}
	public void setAutoSync(int type, boolean sync) {
		if (type == USERS) {
			workspace.setProperty(SYNC_USERS, Boolean.toString(sync));
		} else {
			workspace.setProperty(SYNC_GROUPS, Boolean.toString(sync));
		}
	}
	public String getSchedule() {
		return GetterUtil.get((String)workspace.getProperty(SCHEDULE), SCHEDULE_DEFAULT);
	}
	public void setSchedule(String schedule) {
		if (Validator.isNull(schedule)) {
			workspace.setProperty(SCHEDULE, SCHEDULE_DEFAULT);
		} else {
			workspace.setProperty(SCHEDULE, schedule);
		}
	}
	public String getContextFactoryName() {
		return GetterUtil.get((String)workspace.getProperty(CONTEXT_FACTORY), CONTEXT_FACTORY_DEFAULT);
	}
	public void setContextFactoryName(String contextFactoryName) {
		if (Validator.isNull(contextFactoryName)) {
			workspace.setProperty(CONTEXT_FACTORY, CONTEXT_FACTORY_DEFAULT);
		} else {
			workspace.setProperty(CONTEXT_FACTORY, contextFactoryName);
		}
	}
	public String getSocketFactoryName() {
		return GetterUtil.getString((String)workspace.getProperty(SOCKET_FACTORY));
	}
	public void setSocketFactoryName(String socketFactoryName) {
		workspace.setProperty(SOCKET_FACTORY, socketFactoryName);
	}
	public void updateProperties(Map props) {
		Map current = workspace.getProperties();
		if (current == null) workspace.setProperties(new HashMap(props));
		else {
			current.putAll(props);
			workspace.setProperties(current);
		}
	}
}
