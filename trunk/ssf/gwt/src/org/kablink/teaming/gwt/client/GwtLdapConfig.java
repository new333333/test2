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
import java.util.HashMap;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is used to represent all of the LDAP configuration data.
 * 
 * @author drfoster@novell.com
 */
public class GwtLdapConfig implements IsSerializable, VibeRpcResponseData {
	// User information.
	private boolean	m_deleteLdapUsers;						//
	private boolean	m_deleteUserWorkspace;					//
	private boolean	m_registerUserProfilesAutomatically;	//
	private boolean	m_syncUserProfiles;						//
	private String	m_defaultUserFilter;					//
	private String	m_locale;								//
	private String	m_timeZone;								//
	
	// Group information.
	private boolean	m_deleteNonLdapGroups;					//
	private boolean	m_registerGroupProfilesAutomatically;	//
	private boolean	m_syncGroupMembership;					//
	private boolean	m_syncGroupProfiles;					//
	private String	m_defaultGroupFilter;					//
	
	// LDAP schedule information.
	private GwtSchedule	m_schedule;	//
	
	// Login information.
	private boolean	m_allowLocalLogin;	//
	
	// LDAP connection information.
	private ArrayList<GwtLdapConnectionConfig>	m_listOfLdapConnections;	//
	
	// User attribute mappings.
	private HashMap<String, String>	m_defaultUserAttributeMappings;	//
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtLdapConfig() {
		// Initialize the super class.
		super();
	}

	/**
	 * Adds a default user attribute mapping to the Map of them.
	 * 
	 * @param from
	 * @param to
	 */
	public void addDefaultUserAttributeMapping(String from, String to) {
		if (null == m_defaultUserAttributeMappings) {
			m_defaultUserAttributeMappings = new HashMap<String, String>();
		}
		m_defaultUserAttributeMappings.put(from, to);
	}
	
	/**
	 * Adds a GwtLdapConnectionConfig to the list of them.
	 * 
	 * @param config.
	 */
	public void addLdapConnectionConfig(GwtLdapConnectionConfig config) {
		if (null == m_listOfLdapConnections) {
			m_listOfLdapConnections = new ArrayList<GwtLdapConnectionConfig>();
		}
		m_listOfLdapConnections.add(config);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public ArrayList<GwtLdapConnectionConfig> getListOfLdapConnections()              {return m_listOfLdapConnections;             }
	public boolean                            getAllowLocalLogin()                    {return m_allowLocalLogin;                   }
	public boolean                            getDeleteLdapUsers()                    {return m_deleteLdapUsers;                   }
	public boolean                            getDeleteNonLdapGroups()                {return m_deleteNonLdapGroups;               }
	public boolean                            getDeleteUserWorkspace()                {return m_deleteUserWorkspace;               }
	public boolean                            getRegisterGroupProfilesAutomatically() {return m_registerGroupProfilesAutomatically;}
	public boolean                            getRegisterUserProfilesAutomatically()  {return m_registerUserProfilesAutomatically; }
	public boolean                            getSyncGroupMembership()                {return m_syncGroupMembership;               }
	public boolean                            getSyncGroupProfiles()                  {return m_syncGroupProfiles;                 }
	public boolean                            getSyncUserProfiles()                   {return m_syncUserProfiles;                  }
	public GwtSchedule                        getSchedule()                           {return m_schedule;                          }
	public HashMap<String, String>            getDefaultUserAttributeMappings()       {return m_defaultUserAttributeMappings;      }
	public String                             getDefaultGroupFilter()                 {return m_defaultGroupFilter;                }
	public String                             getDefaultUserFilter()                  {return m_defaultUserFilter;                 }
	public String                             getLocale()                             {return m_locale;                            }
	public String                             getTimeZone()                           {return m_timeZone;                          }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAllowLocalLogin(                   boolean     allow)    {m_allowLocalLogin                    = allow;   }
	public void setDeleteLdapUsers(                   boolean     delete)   {m_deleteLdapUsers                    = delete;  }
	public void setDeleteNonLdapGroups(               boolean     delete)   {m_deleteNonLdapGroups                = delete;  }
	public void setDeleteUserWorkspace(               boolean     delete)   {m_deleteUserWorkspace                = delete;  }
	public void setRegisterGroupProfilesAutomatically(boolean     register) {m_registerGroupProfilesAutomatically = register;}
	public void setRegisterUserProfilesAutomatically( boolean     register) {m_registerUserProfilesAutomatically  = register;}
	public void setSyncGroupMembership(               boolean     sync)     {m_syncGroupMembership                = sync;    }
	public void setSyncGroupProfiles(                 boolean     sync)     {m_syncGroupProfiles                  = sync;    }
	public void setSyncUserProfiles(                  boolean     sync)     {m_syncUserProfiles                   = sync;    }
	public void setDefaultGroupFilter(                String      filter)   {m_defaultGroupFilter                 = filter;  }
	public void setDefaultUserFilter(                 String      filter)   {m_defaultUserFilter                  = filter;  }
	public void setLocale(                            String      locale)   {m_locale                             = locale;  }
	public void setTimeZone(                          String      timeZone) {m_timeZone                           = timeZone;}
	public void setSchedule(                          GwtSchedule schedule) {m_schedule                           = schedule;}
}
