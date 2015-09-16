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
package org.kablink.teaming.gwt.client;

import java.util.ArrayList;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtLdapSyncResults.GwtLdapSyncStatus;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is used to represent the data in an LDAP connection.
 * This class should mirror the data found in LdapConnectionConfig.
 * 
 * @author drfoster@novell.com
 */
public class GwtLdapConnectionConfig implements IsSerializable {
	private boolean	m_importUsersAsExternalUsers;	//
	private boolean	m_isDirty;						//
	private String	m_id;							//
	private String	m_serverUrl;					//
	private String	m_proxyDn;						//
	private String	m_proxyPwd;						//
	private String	m_ldapGuidAttribute;			//
	private String	m_origLdapGuidAttrib;			//
	private String	m_userIdAttribute;				//
	
	// User attribute mappings.
	private Map<String, String>				m_userAttributeMappings;		//
	
	// User search criteria.
	private ArrayList<GwtLdapSearchInfo>	m_listOfUserSearchCriteria;		//
	
	// Group search criteria.
	private ArrayList< GwtLdapSearchInfo>	m_listOfGroupSearchCriteria;	//
	
	private GwtLdapSyncStatus				m_syncStatus;					//
	

	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtLdapConnectionConfig() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public ArrayList<GwtLdapSearchInfo> getListOfGroupSearchCriteria() {return m_listOfGroupSearchCriteria; }
	public ArrayList<GwtLdapSearchInfo> getListOfUserSearchCriteria()  {return m_listOfUserSearchCriteria;  }
	public boolean                      isDirty()                      {return m_isDirty;                   }
	public boolean                      isImportUsersAsExternalUsers() {return m_importUsersAsExternalUsers;}
	public GwtLdapSyncStatus            getLdapSyncStatus()            {return m_syncStatus;                }
	public Map<String,String>           getUserAttributeMappings()     {return m_userAttributeMappings;     }
	public String                       getId()                        {return m_id;                        }
	public String                       getLdapGuidAttribute()         {return m_ldapGuidAttribute;         }
	public String                       getOrigLdapGuidAttribute()     {return m_origLdapGuidAttrib;        }
	public String                       getProxyDn()                   {return m_proxyDn;                   }
	public String                       getProxyPwd()                  {return m_proxyPwd;                  }
	public String                       getServerUrl()                 {return m_serverUrl;                 }
	public String                       getUserIdAttribute()           {return m_userIdAttribute;           }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setIsDirty(                   boolean            isDirty)   {m_isDirty                    = isDirty;  }
	public void setImportUsersAsExternalUsers(boolean            external)  {m_importUsersAsExternalUsers = external; }
	public void setLdapSyncStatus(            GwtLdapSyncStatus  status)    {m_syncStatus                 = status;   }
	public void setUserAttributeMappings(     Map<String,String> mappings)  {m_userAttributeMappings      = mappings; }
	public void setId(                        String             id)        {m_id                         = id;       }
	public void setLdapGuidAttribute(         String             attrib)    {m_ldapGuidAttribute          = attrib;   }
	public void setOrigLdapGuidAttribute(     String             attrib)    {m_origLdapGuidAttrib         = attrib;   }
	public void setProxyDn(                   String             proxyDn)   {m_proxyDn                    = proxyDn;  }
	public void setProxyPwd(                  String             proxyPwd)  {m_proxyPwd                   = proxyPwd; }
	public void setServerUrl(                 String             serverUrl) {m_serverUrl                  = serverUrl;}
	public void setUserIdAttribute(           String             attrib)    {m_userIdAttribute            = attrib;   }
	
	/**
	 * Adds a GwtLdapSearchInfo to the list of group search criteria.
	 * 
	 * @param searchCriteria
	 */
	public void addGroupSearchCriteria(GwtLdapSearchInfo searchCriteria) {
		if (null == m_listOfGroupSearchCriteria) {
			m_listOfGroupSearchCriteria = new ArrayList<GwtLdapSearchInfo>();
		}
		m_listOfGroupSearchCriteria.add(searchCriteria);
	}
	
	/**
	 * Adds a GwtLdapSearchInfo to the list of user search criteria.
	 * 
	 * @param searchCriteria
	 */
	public void addUserSearchCriteria(GwtLdapSearchInfo searchCriteria) {
		if (null == m_listOfUserSearchCriteria) {
			m_listOfUserSearchCriteria = new ArrayList<GwtLdapSearchInfo>();
		}
		m_listOfUserSearchCriteria.add(searchCriteria);
	}
	
	/**
	 * See if the value of the LDAP GUID attribute changed.
	 * 
	 * @return
	 */
	public boolean didLdapGuidAttributeChange() {
		if ((null != m_origLdapGuidAttrib) && m_origLdapGuidAttrib.equalsIgnoreCase(m_ldapGuidAttribute)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Empties the list of group search criteria. 
	 */
	public void emptyListOfGroupSearchCriteria() {
		if (null != m_listOfGroupSearchCriteria) {
			m_listOfGroupSearchCriteria.clear();
		}
	}
	
	/**
	 * Empties the list of user search criteria. 
	 */
	public void emptyListOfUserSearchCriteria() {
		if (null != m_listOfUserSearchCriteria) {
			m_listOfUserSearchCriteria.clear();
		}
	}
	
	/**
	 * Returns the user attribute mappings as a single String.
	 * 
	 * @return
	 */
	public String getUserAttributeMappingsAsString() {
		StringBuffer strBuff = new StringBuffer();
		if (null != m_userAttributeMappings) {
			for (Map.Entry<String, String> nextMapping:  m_userAttributeMappings.entrySet()) {
				strBuff.append(nextMapping.getValue());
				strBuff.append("=");
				strBuff.append(nextMapping.getKey());
				strBuff.append("\n");
			}
		}
		return strBuff.toString();
	}
	
	/**
	 * Marks the group and user search objects as being dirty.
	 * 
	 * @param isDirty
	 */
	public void setIsDirtySearchInfo(boolean isDirty) {
		if (!isDirty) {
			if (null != m_listOfGroupSearchCriteria) {
				for (GwtLdapSearchInfo nextSearchInfo:  m_listOfGroupSearchCriteria) {
					nextSearchInfo.setIsDirty(false);
				}
			}

			if (null != m_listOfUserSearchCriteria) {
				for (GwtLdapSearchInfo nextSearchInfo:  m_listOfUserSearchCriteria) {
					nextSearchInfo.setIsDirty(false);
				}
			}
		}
	}
}
