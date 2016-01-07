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
package org.kablink.teaming.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ?
 * 
 * @author ?
 */
public class LdapConnectionConfig extends ZonedObject {
	protected static Log logger = LogFactory.getLog(LdapConnectionConfig.class);

	private Boolean	importUsersAsExternalUsers;
	
	protected String id;

	protected String url;

	protected String userIdAttribute;
	
	protected String ldapGuidAttribute = null;	// ldap attribute used to uniquely identify a user or group.

	protected Map<String, String> mappings;

	protected List<SearchInfo> userSearches;

	protected List<SearchInfo> groupSearches;

	protected String principal;

	protected String credentials;

	protected int position;
	
	public LdapConnectionConfig() {

	}

	public LdapConnectionConfig(String url, String userIdAttribute,
			Map<String, String> mappings, List<SearchInfo> userSearches,
			List<SearchInfo> groupSearches, String principal, String credentials,
			String ldapGuidAttribute,
			Boolean importUsersAsExternalUsers) {
		setUrl(url);
		setUserIdAttribute(userIdAttribute);
		setMappings(mappings);
		setUserSearches(userSearches);
		setGroupSearches(groupSearches);
		setPrincipal(principal);
		setCredentials(credentials);
		setLdapGuidAttribute(ldapGuidAttribute);
		setImportUsersAsExternalUsers(importUsersAsExternalUsers);
	}
	
	public LdapConnectionConfig(String url, String userIdAttribute,
			Map<String, String> mappings, List<SearchInfo> userSearches,
			List<SearchInfo> groupSearches, String principal, String credentials,
			String ldapGuidAttribute) {
		this(url, userIdAttribute, mappings, userSearches, groupSearches, principal, credentials, ldapGuidAttribute, null);
	}

	public boolean getImportUsersAsExternalUsers() {
		if (null == importUsersAsExternalUsers)
		     return false;
		else return importUsersAsExternalUsers.booleanValue();
	}
	
	public void setImportUsersAsExternalUsers(boolean importUsersAsExternalUsers) {
		this.importUsersAsExternalUsers = Boolean.valueOf(importUsersAsExternalUsers);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public Map<String, String> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserIdAttribute() {
		return userIdAttribute;
	}

	public void setUserIdAttribute(String userIdAttribute) {
		this.userIdAttribute = userIdAttribute;
	}

	/**
	 * Get the ldap attribute that is used to uniquely identify a user or group.
	 */
	public String getLdapGuidAttribute()
	{
		return ldapGuidAttribute;
	}// end getGuidAttribute()
	
	
	/**
	 * Set the ldap attribute that is used to uniquely identify a user or group.
	 */
	public void setLdapGuidAttribute( String ldapGuidAttribute )
	{
		this.ldapGuidAttribute = ldapGuidAttribute;
	}// end setGuidAttribute()
	
	
	public List<SearchInfo> getUserSearches() {
		return userSearches;
	}

	public void setUserSearches(List<SearchInfo> userSearches) {
		this.userSearches = userSearches;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	
	/**
	 * 
	 */
	public enum HomeDirCreationOption
	{
		USE_CUSTOM_CONFIG,
		USE_HOME_DIRECTORY_ATTRIBUTE,
		USE_CUSTOM_ATTRIBUTE,
		DONT_CREATE_HOME_DIR_NET_FOLDER,
		UNKNOWN
	}
	
	/**
	 * This class holds all of the information needed to know how to create home directory
	 * net folders. 
	 */
	public static class HomeDirConfig implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private HomeDirCreationOption m_creationOption;
		
		// The following data members are relevant when the creation option is "formula"
		private String m_netFolderServerName;
		private String m_path;
		
		// The following data members are relevant when the creation option is "custom attribute"
		private String m_attributeName;
		
		/**
		 * 
		 */
		public HomeDirConfig()
		{
			m_creationOption = HomeDirCreationOption.UNKNOWN;
		}
		
		/**
		 * 
		 */
		public HomeDirConfig copy()
		{
			HomeDirConfig copy;
			
			copy = new HomeDirConfig();
			copy.setAttributeName( getAttributeName() );
			copy.setCreationOption( getCreationOption() );
			copy.setNetFolderServerName( getNetFolderServerName() );
			copy.setPath( getPath() );
			
			return copy;
		}
		
		/**
		 * 
		 */
		public String getAttributeName()
		{
			return m_attributeName;
		}
		
		/**
		 * 
		 */
		public HomeDirCreationOption getCreationOption()
		{
			return m_creationOption;
		}
		
		/**
		 * 
		 */
		public String getNetFolderServerName()
		{
			return m_netFolderServerName;
		}
		
		/**
		 * 
		 */
		public String getPath()
		{
			return m_path;
		}
		
		/**
		 * 
		 */
		public void setAttributeName( String attributeName )
		{
			m_attributeName = attributeName;
		}
		
		/**
		 * 
		 */
		public void setCreationOption( HomeDirCreationOption creationOption )
		{
			m_creationOption = creationOption;
		}
		
		/**
		 * 
		 */
		public void setNetFolderServerName( String name )
		{
			m_netFolderServerName = name;
		}
		
		/**
		 * 
		 */
		public void setPath( String path )
		{
			m_path = path;
		}
	}
	

	/**
	 * 
	 */
	public static class SearchInfo implements Serializable {
		private String baseDn;

		private String filter;
		// In the "configure ldap" page, the filter field is not multiline which means the user can enter a CR
		// We need to have the filter without a CR or LF
		private String m_filterWithoutCRLF;

		private boolean searchSubtree;
		
		private HomeDirConfig m_homeDirConfig;

		public SearchInfo(String baseDn, String filter, boolean searchSubtree) {
			setBaseDn(baseDn);
			setFilter(filter);
			setSearchSubtree(searchSubtree);
		}

		public String getBaseDn() {
			return baseDn;
		}

		public void setBaseDn(String baseDn) {
			this.baseDn = baseDn;
		}

		public String getFilter() {
			return filter;
		}

		/**
		 * 
		 */
		public String getFilterWithoutCRLF()
		{
			return m_filterWithoutCRLF;
		}

		public void setFilter(String query) {
			this.filter = query;
			m_filterWithoutCRLF = query;
			if ( query != null && query.length() > 0 )
			{
				m_filterWithoutCRLF = query.replaceAll( "\r", "" );
				m_filterWithoutCRLF = m_filterWithoutCRLF.replaceAll( "\n", "" );
			}
		}

		public boolean isSearchSubtree() {
			return searchSubtree;
		}

		public void setSearchSubtree(boolean searchSubtree) {
			this.searchSubtree = searchSubtree;
		}
		
		/**
		 * 
		 */
		public HomeDirConfig getHomeDirConfig()
		{
			return m_homeDirConfig;
		}
		
		/**
		 * 
		 */
		public void setHomeDirConfig( HomeDirConfig config )
		{
			m_homeDirConfig = config.copy();
		}
	}

	public List<SearchInfo> getGroupSearches() {
		return groupSearches;
	}

	public void setGroupSearches(List<SearchInfo> groupSearches) {
		this.groupSearches = groupSearches;
	}
}
