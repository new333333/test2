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
package org.kablink.teaming.gwt.client;

import java.util.ArrayList;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtLdapSyncResults.GwtLdapSyncStatus;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to represent the data in an ldap connection.  This class should mirror
 * the data found in LdapConnectionConfig
 * @author jwootton
 *
 */
public class GwtLdapConnectionConfig implements IsSerializable
{
	private String m_id;
	private String m_serverUrl;
	private String m_proxyDn;
	private String m_proxyPwd;
	private String m_ldapGuidAttribute;
	private String m_userIdAttribute;
	
	// User attribute mappings
	private Map<String, String> m_userAttributeMappings;
	
	// User search criteria
	private ArrayList<GwtLdapSearchInfo> m_listOfUserSearchCriteria;
	
	// Group search criteria
	private ArrayList< GwtLdapSearchInfo> m_listOfGroupSearchCriteria;
	
	private boolean m_isDirty;
	
	private GwtLdapSyncStatus m_syncStatus;
	

	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtLdapConnectionConfig()
	{
		m_isDirty = false;
	}
	
	/**
	 * 
	 */
	public void addGroupSearchCriteria( GwtLdapSearchInfo searchCriteria )
	{
		if ( m_listOfGroupSearchCriteria == null )
			m_listOfGroupSearchCriteria = new ArrayList<GwtLdapSearchInfo>();
		
		m_listOfGroupSearchCriteria.add( searchCriteria );
	}
	
	/**
	 * 
	 */
	public void addUserSearchCriteria( GwtLdapSearchInfo searchCriteria )
	{
		if ( m_listOfUserSearchCriteria == null )
			m_listOfUserSearchCriteria = new ArrayList<GwtLdapSearchInfo>();
		
		m_listOfUserSearchCriteria.add( searchCriteria );
	}
	
	/**
	 * 
	 */
	public void emptyListOfGroupSearchCriteria()
	{
		if ( m_listOfGroupSearchCriteria != null )
			m_listOfGroupSearchCriteria.clear();
	}
	
	/**
	 * 
	 */
	public void emptyListOfUserSearchCriteria()
	{
		if ( m_listOfUserSearchCriteria != null )
			m_listOfUserSearchCriteria.clear();
	}
	
	/**
	 * 
	 */
	public String getId()
	{
		return m_id;
	}
	
	/**
	 * 
	 */
	public String getLdapGuidAttribute()
	{
		return m_ldapGuidAttribute;
	}
	
	/**
	 * 
	 */
	public GwtLdapSyncStatus getLdapSyncStatus()
	{
		return m_syncStatus;
	}
	
	/**
	 * 
	 */
	public ArrayList<GwtLdapSearchInfo> getListOfGroupSearchCriteria()
	{
		return m_listOfGroupSearchCriteria;
	}
	
	/**
	 * 
	 */
	public ArrayList<GwtLdapSearchInfo> getListOfUserSearchCriteria()
	{
		return m_listOfUserSearchCriteria;
	}
	
	/**
	 * 
	 */
	public String getProxyDn()
	{
		return m_proxyDn;
	}
	
	/**
	 * 
	 */
	public String getProxyPwd()
	{
		return m_proxyPwd;
	}
	
	/**
	 * 
	 */
	public String getServerUrl()
	{
		return m_serverUrl;
	}
	
	/**
	 * 
	 */
	public Map<String,String> getUserAttributeMappings()
	{
		return m_userAttributeMappings;
	}
	
	/**
	 * 
	 */
	public String getUserAttributeMappingsAsString()
	{
		StringBuffer strBuff;
		
		strBuff = new StringBuffer();
		if ( m_userAttributeMappings != null )
		{
			for ( Map.Entry<String, String> nextMapping : m_userAttributeMappings.entrySet() )
			{
				strBuff.append( nextMapping.getValue() );
				strBuff.append( "=" );
				strBuff.append( nextMapping.getKey() );
				strBuff.append( "\n" );
			}
		}
		
		return strBuff.toString();
	}
	
	/**
	 * 
	 */
	public String getUserIdAttribute()
	{
		return m_userIdAttribute;
	}
	
	/**
	 * 
	 */
	public boolean isDirty()
	{
		return m_isDirty;
	}
	
	/**
	 * 
	 */
	public void setId( String id )
	{
		m_id = id;
	}

	/**
	 * 
	 */
	public void setIsDirty( boolean isDirty )
	{
		m_isDirty = isDirty;
	}
	
	/**
	 * 
	 */
	public void setIsDirtySearchInfo( boolean isDirty )
	{
		if ( isDirty == false )
		{
			if ( m_listOfGroupSearchCriteria != null )
			{
				for ( GwtLdapSearchInfo nextSearchInfo : m_listOfGroupSearchCriteria )
				{
					nextSearchInfo.setIsDirty( false );
				}
			}

			if ( m_listOfUserSearchCriteria != null )
			{
				for ( GwtLdapSearchInfo nextSearchInfo : m_listOfUserSearchCriteria )
				{
					nextSearchInfo.setIsDirty( false );
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void setLdapGuidAttribute( String attrib )
	{
		m_ldapGuidAttribute = attrib;
	}
	
	/**
	 * 
	 */
	public void setLdapSyncStatus( GwtLdapSyncStatus status )
	{
		m_syncStatus = status;
	}
	
	/**
	 * 
	 */
	public void setProxyDn( String proxyDn )
	{
		m_proxyDn = proxyDn;
	}
	
	/**
	 * 
	 */
	public void setProxyPwd( String proxyPwd )
	{
		m_proxyPwd = proxyPwd;
	}
	
	/**
	 * 
	 */
	public void setServerUrl( String serverUrl )
	{
		m_serverUrl = serverUrl;
	}
	
	/**
	 * 
	 */
	public void setUserAttributeMappings( Map<String,String> mappings )
	{
		m_userAttributeMappings = mappings;
	}
	
	/**
	 * 
	 */
	public void setUserIdAttribute( String attrib )
	{
		m_userIdAttribute = attrib;
	}
}
