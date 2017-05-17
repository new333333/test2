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
 * ?
 * 
 * @author rvasudevan
 * @author drfoster@novell.com
 */
public class LdapServer implements IsSerializable {
	private Boolean	m_hasSslCertificateBin;	//
	private Boolean	m_sslEnabled;			//
	private byte[]	m_sslCertificateBin;	//
	private int		m_ldapPort;				//
	private String	m_address;				//
	private String	m_description;			//
	private String	m_name;					//
	private String	m_sslCertificateFile;	//
	private String	m_url;					//

	// Used on the client side for uploading SSL certificate into the
	// back end.
	private String 	m_directoryId;			//
	private String	m_tempSslCertBinDir;	//

	/**
	 * Enumeration the defines the type of an LDAP server, if known.
	 */
	public enum DirectoryType implements IsSerializable {
		EDIRECTORY,
		ACTIVE_DIRECTORY,
		GROUPWISE,
		UNKNOWN;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isActiveDirectory() {return this.equals(ACTIVE_DIRECTORY);}
		public boolean isEDirectory()      {return this.equals(EDIRECTORY      );}
		public boolean isGroupWise()	   {return this.equals(GROUPWISE       );}
		public boolean isUnknown()         {return this.equals(UNKNOWN         );}
	}

	/**
	 * Constructor method.
	 * 
	 * Zero parameters as per GWT serialization requirements.
	 */
	public LdapServer() {
		// Initialize the super class.
		super();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isSslEnabled()            {return ((null != m_sslEnabled) && m_sslEnabled);}
	public Boolean getHasSslCertificateBin() {return m_hasSslCertificateBin;                  }
	public Boolean getSslEnabled()           {return m_sslEnabled;                            }
	public byte[]  getSslCertificateBin()    {return m_sslCertificateBin;                     }
	public int     getLdapPort()             {return m_ldapPort;                              }
	public String  getAddress()              {return m_address;                               }
	public String  getDescription()          {return m_description;                           }
	public String  getDirectoryId()          {return m_directoryId;                           }
	public String  getName()                 {return m_name;                                  }
	public String  getSslCertificateFile()   {return m_sslCertificateFile;                    }
	public String  getTempSslCertBinDir()    {return m_tempSslCertBinDir;                     }
	public String  getUrl()                  {return m_url;                                   }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setHasSslCertificateBin(Boolean hasSslCertificateBin) {m_hasSslCertificateBin = hasSslCertificateBin;         }
	public void setSslEnabled(          Boolean sslEnabled)           {m_sslEnabled           = sslEnabled;                   }
	public void setSslCertificateBin(   byte[]  sslCertificateBin)    {m_sslCertificateBin    = sslCertificateBin;            }
	public void setLdapPort(            int     ldapPort)             {m_ldapPort             = ldapPort;                     }
	public void setAddress(             String  address)              {m_address              = address; checkForSslAddress();}
	public void setDescription(         String  description)          {m_description          = description;                  }
	public void setDirectoryId(         String  directoryId)          {m_directoryId          = directoryId;                  }
	public void setName(                String  name)                 {m_name                 = name;                         }
	public void setSslCertificateFile(  String  sSLKeyFile)           {m_sslCertificateFile   = sSLKeyFile;                   }
	public void setTempSslCertBinDir(   String  tempSslCertBinDir)    {m_tempSslCertBinDir    = tempSslCertBinDir;            }
	public void setUrl(                 String  url)                  {m_url                  = url;                          }

	/*
	 * Sets the LdapServer's m_sslEnabled flag it it's not already set
	 * based on the LDAP server address stored in m_address.
	 */
	private void checkForSslAddress() {
		// If we already have an SSL enabled flag stored or we don't
		// have an address to check...
		if ((null != m_sslEnabled) || (null == m_address) || (0 == m_address.length())) {
			// ...bail.
			return;
		}

		// If the LDAP server address starts with 'ldaps://', consider
		// it to be SSL.
		String normalizedAddr = m_address.toLowerCase().trim();
		if (normalizedAddr.startsWith("ldaps://"))
		     setSslEnabled(Boolean.TRUE );
		else setSslEnabled(Boolean.FALSE);
	}
}
