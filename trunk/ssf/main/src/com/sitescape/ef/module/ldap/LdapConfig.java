
package com.sitescape.ef.module.ldap;

import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.sitescape.ef.domain.Workspace;
import com.sitescape.util.Validator;
import com.sitescape.util.GetterUtil;
import com.sitescape.ef.jobs.Schedule;
/**
 * @author Janet McCann
 *
 * Manage the ldap properties for a workspace. 
 */
public class LdapConfig {

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
	
    
	//seconds minutes hours dayOfMonth months days year"
	private static final String SCHEDULE_DEFAULT = "0 15 2 ? * mon-fri *";
	private Workspace workspace;
	

	public LdapConfig (Workspace workspace) {
		//initialize with workspace properties
		this.workspace = workspace;
	}



	public boolean isScheduleEnabled() {
		return GetterUtil.get((String)workspace.getProperty(ENABLE_SCHEDULE), false);
	}
	public void setScheduleEnabled(boolean scheduleEnabled) {
		workspace.setProperty(ENABLE_SCHEDULE, Boolean.toString(scheduleEnabled));
	}
	public boolean isSessionRegister() {
		return GetterUtil.get((String)workspace.getProperty(SESSION_REGISTER_USERS), false);
	}
	public void setSessionRegister(boolean autoLogin) {
		workspace.setProperty(SESSION_REGISTER_USERS, Boolean.toString(autoLogin));
	}
	public boolean isSessionSync() {
		return GetterUtil.get((String)workspace.getProperty(SESSION_SYNC), false);
	}
	public void setSessionSync(boolean loginSync) {
		workspace.setProperty(SESSION_SYNC, Boolean.toString(loginSync));
	}
	public boolean isUserDisable() {
		return GetterUtil.get((String)workspace.getProperty(DISABLE_USERS), false);
	}
	public void setUserDisable(boolean disable) {
		workspace.setProperty(DISABLE_USERS, Boolean.toString(disable));
	}
	public boolean isGroupDisable() {
		return GetterUtil.get((String)workspace.getProperty(DISABLE_GROUPS), false);
	}
	public void setGroupDisable(boolean disable) {
		workspace.setProperty(DISABLE_GROUPS, Boolean.toString(disable));			
	}
	public boolean isUserRegister() {
		return  GetterUtil.get((String)workspace.getProperty(REGISTER_USERS), false);
	}
	public void setUserRegister(boolean create) {
		workspace.setProperty(REGISTER_USERS, Boolean.toString(create));
	}
	public boolean isGroupRegister() {
		return  GetterUtil.get((String)workspace.getProperty(REGISTER_GROUPS), false);			
	}
	public void setGroupRegister(boolean create) {
		workspace.setProperty(REGISTER_GROUPS, Boolean.toString(create));
	}

	public boolean isUserSync() {
		return  GetterUtil.get((String)workspace.getProperty(SYNC_USERS), false);			
	}
	public void setUserSync(boolean create) {
		workspace.setProperty(SYNC_USERS, Boolean.toString(create));
	}
	public boolean isGroupSync() {
		return  GetterUtil.get((String)workspace.getProperty(SYNC_GROUPS), false);			
	}
	public void setGroupSync(boolean create) {
		workspace.setProperty(SYNC_GROUPS, Boolean.toString(create));
	}
	public boolean isMembershipSync() {
		return  GetterUtil.get((String)workspace.getProperty(SYNC_MEMBERSHIP), false);			
	}
	public void setMembershipSync(boolean create) {
		workspace.setProperty(SYNC_MEMBERSHIP, Boolean.toString(create));
	}
	public Schedule getSchedule() {
		String sched = GetterUtil.get((String)workspace.getProperty(QUARTZ_SCHEDULE), SCHEDULE_DEFAULT);
		return new Schedule(sched);
	}
	public void setSchedule(Schedule schedule) {
		workspace.setProperty(QUARTZ_SCHEDULE, schedule.getQuartzSchedule());
	}

}
