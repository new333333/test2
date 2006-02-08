
package com.sitescape.ef.ldap;

import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.sitescape.ef.domain.Workspace;
import com.sitescape.util.Validator;
import com.sitescape.util.GetterUtil;
import com.sitescape.ef.jobs.Schedule;
import com.sitescape.ef.jobs.ScheduleInfo;
/**
 * @author Janet McCann
 *
 * Manage the ldap properties. 
 */
public class LdapConfig extends ScheduleInfo {

	public static final String SESSION_REGISTER_USERS = "ldap.session.register";
	public static final String SESSION_SYNC = "ldap.session.sync";
	public static final String SCHEDULE = "ldap.schedule";
	public static final String QUARTZ_SCHEDULE = "ldap.quartz.schedule";
	public static final String ENABLE_SCHEDULE = "ldap.schedule.enable";
	
	public static final String DISABLE_USERS = "ldap.users.disable";
	public static final String REGISTER_USERS = "ldap.users.register";
	public static final String SYNC_USERS = "ldap.users.sync";
	public static final String SYNC_MEMBERSHIP = "ldap.membership.sync";
	public static final String DISABLE_GROUPS = "ldap.groups.disable";
	public static final String REGISTER_GROUPS = "ldap.groups.register";
	public static final String SYNC_GROUPS = "ldap.groups.sync";
	
    
	public LdapConfig(ScheduleInfo scheduleInfo) {
		super(scheduleInfo.getDetails());
		setSchedule(scheduleInfo.getSchedule());
		setEnabled(scheduleInfo.isEnabled());
	}
	public LdapConfig(Map details) {
		super(details);
	}
	public LdapConfig (String zoneName) {
		super(zoneName);
	}

	public boolean isScheduleEnabled() {
		return GetterUtil.get((String)details.get(ENABLE_SCHEDULE), false);
	}
	public void setScheduleEnabled(boolean scheduleEnabled) {
		details.put(ENABLE_SCHEDULE, Boolean.toString(scheduleEnabled));
	}
	public boolean isSessionRegister() {
		return GetterUtil.get((String)details.get(SESSION_REGISTER_USERS), false);
	}
	public void setSessionRegister(boolean autoLogin) {
		details.put(SESSION_REGISTER_USERS, Boolean.toString(autoLogin));
	}
	public boolean isSessionSync() {
		return GetterUtil.get((String)details.get(SESSION_SYNC), false);
	}
	public void setSessionSync(boolean loginSync) {
		details.put(SESSION_SYNC, Boolean.toString(loginSync));
	}
	public boolean isUserDisable() {
		return GetterUtil.get((String)details.get(DISABLE_USERS), false);
	}
	public void setUserDisable(boolean disable) {
		details.put(DISABLE_USERS, Boolean.toString(disable));
	}
	public boolean isGroupDisable() {
		return GetterUtil.get((String)details.get(DISABLE_GROUPS), false);
	}
	public void setGroupDisable(boolean disable) {
		details.put(DISABLE_GROUPS, Boolean.toString(disable));			
	}
	public boolean isUserRegister() {
		return  GetterUtil.get((String)details.get(REGISTER_USERS), false);
	}
	public void setUserRegister(boolean create) {
		details.put(REGISTER_USERS, Boolean.toString(create));
	}
	public boolean isGroupRegister() {
		return  GetterUtil.get((String)details.get(REGISTER_GROUPS), false);			
	}
	public void setGroupRegister(boolean create) {
		details.put(REGISTER_GROUPS, Boolean.toString(create));
	}

	public boolean isUserSync() {
		return  GetterUtil.get((String)details.get(SYNC_USERS), false);			
	}
	public void setUserSync(boolean create) {
		details.put(SYNC_USERS, Boolean.toString(create));
	}
	public boolean isGroupSync() {
		return  GetterUtil.get((String)details.get(SYNC_GROUPS), false);			
	}
	public void setGroupSync(boolean create) {
		details.put(SYNC_GROUPS, Boolean.toString(create));
	}
	public boolean isMembershipSync() {
		return  GetterUtil.get((String)details.get(SYNC_MEMBERSHIP), false);			
	}
	public void setMembershipSync(boolean create) {
		details.put(SYNC_MEMBERSHIP, Boolean.toString(create));
	}
	
}
