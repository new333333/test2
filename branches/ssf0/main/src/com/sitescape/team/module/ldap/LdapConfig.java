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

package com.sitescape.team.module.ldap;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.util.GetterUtil;
/**
 * @author Janet McCann
 *
 * Manage the ldap properties. 
 */
public class LdapConfig extends ScheduleInfo {

//	public static final String SESSION_SYNC = "ldap.session.sync";
	public static final String SCHEDULE = "ldap.schedule";
	public static final String QUARTZ_SCHEDULE = "ldap.quartz.schedule";
	public static final String ENABLE_SCHEDULE = "ldap.schedule.enable";
	
	public static final String USERS_URL = "ldap.users.url";
	public static final String USERS_PRINCIPAL = "ldap.users.princiapl";
	public static final String USERS_CREDENTIAL = "ldap.users.credentials";
	public static final String USERS_ID="ldap.users.id";
	public static final String USERS_MAPPINGS="ldap.users.mappings";
	public static final String GROUPS_BASE_DN = "ldap.groups.basedn";
//	public static final String GROUPS_PRINCIPAL = "ldap.groups.princiapl";
//	public static final String GROUPS_CREDENTIAL = "ldap.groups.credentials";
//	public static final String DISABLE_USERS = "ldap.users.disable";
	public static final String DELETE_USERS = "ldap.users.delete";
	public static final String DELETE_USERS_WORKSPACE = "ldap.users.workspace.delete";
	public static final String REGISTER_USERS = "ldap.users.register";
	public static final String SYNC_USERS = "ldap.users.sync";
	public static final String SYNC_MEMBERSHIP = "ldap.membership.sync";
//	public static final String DISABLE_GROUPS = "ldap.groups.disable";
	public static final String DELETE_GROUPS = "ldap.groups.delete";
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
	public LdapConfig (Long zoneId) {
		super(zoneId);
	}
	public String getUserUrl() {
		return GetterUtil.get((String)details.get(USERS_URL), "");
	}
	public void setUserUrl(String userUrl) {
		details.put(USERS_URL, userUrl);
	}
	
	public String getUserPrincipal() {
		return GetterUtil.get((String)details.get(USERS_PRINCIPAL), "");
	}
	public void setUserPrincipal(String userPrincipal) {
		details.put(USERS_PRINCIPAL, userPrincipal);
	}
	public String getUserCredential() {
		return GetterUtil.get((String)details.get(USERS_CREDENTIAL), "");
	}
	public void setUserCredential(String userCredential) {
		details.put(USERS_CREDENTIAL, userCredential);
	}
	public String getUserIdMapping() {
		return  GetterUtil.get((String)details.get(USERS_ID), "");
	}
	public void setUserIdMapping(String userId) {
		details.put(USERS_ID, userId);
	}
	public Map getUserMappings() {
		Map result = (Map)details.get(USERS_MAPPINGS);
		if (result == null) return new LinkedHashMap(); //maintain order
		return result;
	}
	public void setUserMappings(Map mappings) {
		details.put(USERS_MAPPINGS, mappings);
	}
	public String getGroupsBasedn() {
		return GetterUtil.get((String)details.get(GROUPS_BASE_DN), "");
	}
	public void setGroupsBasedn(String basedn) {
		details.put(GROUPS_BASE_DN, basedn);
	}
	public boolean isScheduleEnabled() {
		return GetterUtil.get((String)details.get(ENABLE_SCHEDULE), false);
	}
	public void setScheduleEnabled(boolean scheduleEnabled) {
		details.put(ENABLE_SCHEDULE, Boolean.toString(scheduleEnabled));
	}
//	public boolean isSessionSync() {
//		return GetterUtil.get((String)details.get(SESSION_SYNC), false);
//	}
//	public void setSessionSync(boolean loginSync) {
//		details.put(SESSION_SYNC, Boolean.toString(loginSync));
//	}
	public boolean isUserDelete() {
		return GetterUtil.get((String)details.get(DELETE_USERS), false);
	}
	public void setUserDelete(boolean disable) {
		details.put(DELETE_USERS, Boolean.toString(disable));
	}
	public boolean isUserWorkspaceDelete() {
		return GetterUtil.get((String)details.get(DELETE_USERS_WORKSPACE), false);
	}
	public void setUserWorkspaceDelete(boolean disable) {
		details.put(DELETE_USERS_WORKSPACE, Boolean.toString(disable));
	}
	public boolean isGroupDelete() {
		return GetterUtil.get((String)details.get(DELETE_GROUPS), false);
	}
	public void setGroupDelete(boolean disable) {
		details.put(DELETE_GROUPS, Boolean.toString(disable));			
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
		//default to true
		return  GetterUtil.get((String)details.get(SYNC_GROUPS), true);			
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
