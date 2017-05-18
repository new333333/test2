/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.ldapbrowser;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class that represents an LDAP server for connecting the LDAP browser
 * to an LDAP source.
 *  
 * @author rvasudevan
 * @author drfoster@novell.com
 */
public final class DirectoryServer extends LdapServer implements IsSerializable {
	private Boolean m_syncEnabled;		//
	private String	m_authPassword;		//
	private String	m_authUser;			//
	private String	m_baseDn;			//
	private String  m_guidAttribute;	//
	private String	m_syncDomain;		//
	private String	m_syncPassword;		//
	private String	m_syncUser;			//
	private String	m_treeName;			//
	
	public static final String GUID_ATTRIBUTE            = "GUID";
	public static final String OBJECT_GUID_ATTRIBUTE     = "objectGUID";
	public static final String ENTRY_UUID_ATTRIBUTE		 = "entryUUID";
	public static final String OBJECT_SID_ATTRIBUTE      = "objectSid";
	public static final String NDS_HOME_DIR_ATTRIBUTE    = "ndsHomeDirectory";
	public static final String HOME_DIR_ATTRIBUTE        = "homeDirectory";
	public static final String NETWORK_ADDRESS_ATTRIBUTE = "networkAddress";

	/**
	 * Constructor method.
	 * 
	 * Zero parameters as per GWT serialization requirements.
	 */
	public DirectoryServer() {
		// Initialize the super class.
		super();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Boolean getSyncEnabled()   {return m_syncEnabled;  }
	public String  getAuthPassword()  {return m_authPassword; }
	public String  getAuthUser()      {return m_authUser;     }
	public String  getBaseDn()        {return m_baseDn;       }
	public String  getGuidAttribute() {return m_guidAttribute;}
	public String  getSyncDomain()    {return m_syncDomain;   }
	public String  getSyncPassword()  {return m_syncPassword; }
	public String  getSyncUser()      {return m_syncUser;     }
	public String  getTreeName()      {return m_treeName;     }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setSyncEnabled(  Boolean syncEnabled)   {m_syncEnabled   = syncEnabled;  }
	public void setAuthPassword( String  authPassword)  {m_authPassword  = authPassword; }
	public void setAuthUser(     String  authUser)      {m_authUser      = authUser;     }
	public void setBaseDn(       String  baseDn)        {m_baseDn        = baseDn;       }
	public void setGuidAttribute(String  guidAttribute) {m_guidAttribute = guidAttribute;}
	public void setSyncDomain(   String  syncDomain)    {m_syncDomain    = syncDomain;   }
	public void setSyncPassword( String  syncPassword)  {m_syncPassword  = syncPassword; }
	public void setSyncUser(     String  syncUser)      {m_syncUser      = syncUser;     }
	public void setTreeName(     String  treeName)      {m_treeName      = treeName;     }

	/**
	 * Returns the DirectoryType of this DirectoryServer based on its
	 * GUID.
	 * 
	 * @return
	 */
	public DirectoryType getDirectoryType() {
		if (null != m_guidAttribute) {
			if (m_guidAttribute.equalsIgnoreCase(OBJECT_GUID_ATTRIBUTE)) {
				return DirectoryType.ACTIVE_DIRECTORY;
			}
			
			if (m_guidAttribute.equalsIgnoreCase(GUID_ATTRIBUTE)) {
				return DirectoryType.EDIRECTORY;
			}
			
			if (m_guidAttribute.equalsIgnoreCase(ENTRY_UUID_ATTRIBUTE)){
				return DirectoryType.GROUPWISE;
			}
		}
	
		return DirectoryType.UNKNOWN;
	}

	/**
	 * Sets the GUID based on a DirectoryType.
	 * 
	 * @param dt
	 */
	public void setGuidForDirectoryType(DirectoryType dt) {
		String guid;
		switch (dt) {
		default:
		case UNKNOWN:           guid = null;                  break;
		case ACTIVE_DIRECTORY:  guid = OBJECT_GUID_ATTRIBUTE; break;
		case EDIRECTORY:        guid = GUID_ATTRIBUTE;        break;
		case GROUPWISE:			guid = ENTRY_UUID_ATTRIBUTE;  break;
		}
		setGuidAttribute(guid);
	}
	
	/*
	 * Returns true is s refers to a non null, non 0 length String and
	 * false otherwise.
	 */
	private static boolean hasString(String s) {
		return ((null != s) && (0 < s.length()));
	}

	/**
	 * Returns true if this DirectoryServer describes an Active
	 * Directory LDAP server and false otherwise.
	 * 
	 * @return
	 */
	public boolean isActiveDirectory() {
		return getDirectoryType().isActiveDirectory();
	}
	
	/**
	 * Returns true if this DirectoryServer describes an eDirectory
	 * LDAP server and false otherwise.
	 * 
	 * @return
	 */
	public boolean isEDirectory() {
		return getDirectoryType().isEDirectory();
	}
	
	/**
	 * Returns true if this DirectoryServer describes an GroupWise
	 * LDAP server and false otherwise.
	 * 
	 * @return
	 */
	public boolean isGroupWise() {
		return getDirectoryType().isGroupWise();
	}
	
	/**
	 * Returns true if this DirectoryServer has enough information to
	 * attempt to connect as anonymous and false otherwise.
	 * 
	 * @return
	 */
	public boolean isEnoughToConnectAnonymous() {
		return (
			hasString(getAddress()));
	}
	
	/**
	 * Returns true if this DirectoryServer has enough information to
	 * attempt to connect with full authentication and false otherwise.
	 * 
	 * @return
	 */
	public boolean isEnoughToConnectAuthenticated() {
		return (
			hasString(getAddress()      ) &&
			hasString(getSyncUser()     ) &&
			hasString(getSyncPassword() ) &&
			hasString(getGuidAttribute()));
	}
	
	/**
	 * Returns true if this DirectoryServer describes an eDirectory
	 * LDAP server and false otherwise.
	 * 
	 * @return
	 */
	public boolean isUnknown() {
		return getDirectoryType().isUnknown();
	}
}
