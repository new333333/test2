/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LdapSyncException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.ldap.impl.LdapModuleImpl.HomeDirInfo;

/**
 * ?
 * 
 * @author Janet McCann
 */
@SuppressWarnings("unchecked")
public interface LdapModule {
	public enum LdapOperation {
		manageLdap,
		manageAuthentication
	}
	
	/**
	 * 
	 */
	public enum LdapSyncMode
	{
		PERFORM_SYNC,
		PREVIEW_ONLY
	}
	
	public boolean testAccess(LdapOperation operation);

	public HashSet<User> getDynamicGroupMembers( String baseDn, String filter, boolean searchSubtree ) throws LdapSyncException;
	
	public LdapSchedule getLdapSchedule();

	public boolean hasPasswordExpired( String userName, String ldapConfigId );
	
	public boolean isGuidConfigured();
	
	public void setLdapSchedule(LdapSchedule schedule);

	public HomeDirInfo getHomeDirInfo( String teamingUserName, String ldapUserName, boolean logErrors ) throws NamingException;
	
	public Map getLdapUserAttributes(User user) throws NamingException;
	
    public String readLdapGuidFromDirectory( String userName, Long zoneId );

    public String readLdapGuidFromDirectory(String userName, Long zoneId, LdapConnectionConfig config);

    public void syncAll( boolean syncUsersAndGroups, String[] listOfLdapConfigsToSyncGuid, LdapSyncMode mode, LdapSyncResults syncResults ) throws LdapSyncException;

	public void syncUser( String teamingUserName, String ldapUserName ) throws NoUserByTheNameException,NamingException;
	
	public void syncUser(Long userId) throws NoUserByTheNameException,NamingException;

	public Integer testGroupMembershipCriteria( String baseDn, String filter, boolean searchSubtree ) throws LdapSyncException;

    public String getDefaultLocaleId();
    public String getDefaultTimeZone();
    public void setDefaultLocale(String localeId);
    public void setDefaultTimeZone(String timeZoneId);
    public void updateHomeDirectoryIfNecessary(User user, String userName, boolean logErrors);
    
	public List<LdapConnectionConfig> getConfigsReadOnlyCache(Long zoneId);
	
	public void setConfigsReadOnlyCache(Long zoneId, List<LdapConnectionConfig> configs);
	
	public LdapConnectionConfig getConfigReadOnlyCache(Long zoneId, String configId);
	
	public ADLdapObject getLdapObjectFromAD( String fqdn ) throws NamingException;
	
	public boolean getLdapSupportsExternalUserImport();
}
