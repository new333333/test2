/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.module.ldap;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.jobs.Schedule;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.util.GetterUtil;

/**
 * @author Janet McCann
 *
 * Manage the ldap properties. 
 */
public class LdapSchedule {

//	public static final String SESSION_SYNC = "ldap.session.sync";
	public static final String SCHEDULE = "ldap.schedule";
	public static final String QUARTZ_SCHEDULE = "ldap.quartz.schedule";
	public static final String ENABLE_SCHEDULE = "ldap.schedule.enable";
	
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
	
    private ScheduleInfo scheduleInfo;
    public LdapSchedule(ScheduleInfo scheduleInfo) {
		this.scheduleInfo = new ScheduleInfo(scheduleInfo.getDetails());
		this.scheduleInfo.setSchedule(scheduleInfo.getSchedule());
		this.scheduleInfo.setEnabled(scheduleInfo.isEnabled());
	}
	public LdapSchedule(Map details) {
		this.scheduleInfo = new ScheduleInfo(details);
	}
	public LdapSchedule (Long zoneId) {
		this.scheduleInfo = new ScheduleInfo(zoneId);
	}
	
	protected Map getDetails()
	{
		return scheduleInfo.getDetails();
	}

	public ScheduleInfo getScheduleInfo()
	{
		return scheduleInfo;
	}

    public void setScheduleInfo(ScheduleInfo scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }

    public boolean isScheduleEnabled() {
		return GetterUtil.get((String)getDetails().get(ENABLE_SCHEDULE), false);
	}
	public void setScheduleEnabled(boolean scheduleEnabled) {
		getDetails().put(ENABLE_SCHEDULE, Boolean.toString(scheduleEnabled));
	}
//	public boolean isSessionSync() {
//		return GetterUtil.get((String)getDetails().get(SESSION_SYNC), false);
//	}
//	public void setSessionSync(boolean loginSync) {
//		getDetails().put(SESSION_SYNC, Boolean.toString(loginSync));
//	}
	public boolean isUserDelete() {
		return GetterUtil.get((String)getDetails().get(DELETE_USERS), false);
	}
	public void setUserDelete(boolean disable) {
		getDetails().put(DELETE_USERS, Boolean.toString(disable));
	}
	public boolean isUserWorkspaceDelete() {
		return GetterUtil.get((String)getDetails().get(DELETE_USERS_WORKSPACE), false);
	}
	public void setUserWorkspaceDelete(boolean disable) {
		getDetails().put(DELETE_USERS_WORKSPACE, Boolean.toString(disable));
	}
	public boolean isGroupDelete() {
		return GetterUtil.get((String)getDetails().get(DELETE_GROUPS), false);
	}
	public void setGroupDelete(boolean disable) {
		getDetails().put(DELETE_GROUPS, Boolean.toString(disable));			
	}
	public boolean isUserRegister() {
		return  GetterUtil.get( (String)getDetails().get(REGISTER_USERS), true );
	}
	public void setUserRegister(boolean create) {
		getDetails().put(REGISTER_USERS, Boolean.toString(create));
	}
	public boolean isGroupRegister() {
		return  GetterUtil.get((String)getDetails().get(REGISTER_GROUPS), true );			
	}
	public void setGroupRegister(boolean create) {
		getDetails().put(REGISTER_GROUPS, Boolean.toString(create));
	}

	public boolean isUserSync() {
		return  GetterUtil.get((String)getDetails().get(SYNC_USERS), true );			
	}
	public void setUserSync(boolean create) {
		getDetails().put(SYNC_USERS, Boolean.toString(create));
	}
	public boolean isGroupSync() {
		//default to true
		return  GetterUtil.get((String)getDetails().get(SYNC_GROUPS), true);			
	}
	public void setGroupSync(boolean create) {
		getDetails().put(SYNC_GROUPS, Boolean.toString(create));
	}
	public boolean isMembershipSync() {
		return  GetterUtil.get((String)getDetails().get(SYNC_MEMBERSHIP), true );			
	}
	public void setMembershipSync(boolean create) {
		getDetails().put(SYNC_MEMBERSHIP, Boolean.toString(create));
	}
	
	public boolean isEnabled()
	{
		return scheduleInfo.isEnabled();
	}

	public Schedule getSchedule()
	{
		return scheduleInfo.getSchedule();
	}
	
	public static class LegacyLdapConfig
	{
		public static final String USERS_URL = "ldap.users.url";
		public static final String USERS_PRINCIPAL = "ldap.users.princiapl";
		public static final String USERS_CREDENTIAL = "ldap.users.credentials";
		public static final String USERS_ID="ldap.users.id";
		public static final String USERS_MAPPINGS="ldap.users.mappings";
		public static final String GROUPS_BASE_DN = "ldap.groups.basedn";
//		public static final String GROUPS_PRINCIPAL = "ldap.groups.princiapl";
//		public static final String GROUPS_CREDENTIAL = "ldap.groups.credentials";
		Map details;
		
		public LegacyLdapConfig(Map details)
		{
			setDetails(details);
		}

		public Map getDetails() {
			return details;
		}

		public void setDetails(Map details) {
			this.details = details;
		}

		public String getUserUrl() {
			return GetterUtil.get((String)getDetails().get(USERS_URL), "");
		}
		public void setUserUrl(String userUrl) {
			getDetails().put(USERS_URL, userUrl);
		}
		
		public String getUserPrincipal() {
			return GetterUtil.get((String)getDetails().get(USERS_PRINCIPAL), "");
		}
		public void setUserPrincipal(String userPrincipal) {
			getDetails().put(USERS_PRINCIPAL, userPrincipal);
		}
		public String getUserCredential() {
			return GetterUtil.get((String)getDetails().get(USERS_CREDENTIAL), "");
		}
		public void setUserCredential(String userCredential) {
			getDetails().put(USERS_CREDENTIAL, userCredential);
		}
		public String getUserIdMapping() {
			return  GetterUtil.get((String)getDetails().get(USERS_ID), "");
		}
		public void setUserIdMapping(String userId) {
			getDetails().put(USERS_ID, userId);
		}
		public Map getUserMappings() {
			Map result = (Map)getDetails().get(USERS_MAPPINGS);
			if (result == null) return new LinkedHashMap(); //maintain order
			return result;
		}
		public void setUserMappings(Map mappings) {
			getDetails().put(USERS_MAPPINGS, mappings);
		}
		public String getGroupsBasedn() {
			return GetterUtil.get((String)getDetails().get(GROUPS_BASE_DN), "");
		}
		public void setGroupsBasedn(String basedn) {
			getDetails().put(GROUPS_BASE_DN, basedn);
		}

	}
}
