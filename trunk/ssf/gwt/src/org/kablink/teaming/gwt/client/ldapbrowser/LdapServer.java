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
	 */
	public enum DirectoryType {
		EDIRECTORY,
		ACTIVE_DIRECTORY,
		UNKNOWN;
	}

	public LdapServer()
	{
	}

	/**
	 * @return the m_address
	 */
	public String getAddress()
	{
		return m_address;
	}

	/**
	 * @param m_address
	 *            the m_address to set
	 */
	public void setAddress(String address)
	{
		m_address = address;
	}

	/**
	 * @return the sSLKeyFile
	 */
	public String getSslCertificateFile()
	{
		return m_sslCertificateFile;
	}

	/**
	 * @param sSLKeyFile
	 *            the sSLKeyFile to set
	 */
	public void setSslCertificateFile(String sSLKeyFile)
	{
		m_sslCertificateFile = sSLKeyFile;
	}

	/**
	 * @return the m_description
	 */
	public String getDescription()
	{
		return m_description;
	}

	/**
	 * @param m_description
	 *            the m_description to set
	 */
	public void setDescription(String description)
	{
		m_description = description;
	}

	/**
	 * @return the m_ldapPort
	 */
	public int getLdapPort()
	{
		return m_ldapPort;
	}

	/**
	 * @param m_ldapPort
	 *            the m_ldapPort to set
	 */
	public void setLdapPort(int ldapPort)
	{
		m_ldapPort = ldapPort;
	}

	public Boolean getSslEnabled()
	{
		return m_sslEnabled;
	}

	public void setSslEnabled(Boolean sslEnabled)
	{
		m_sslEnabled = sslEnabled;
	}

	public byte[] getSslCertificateBin()
	{
		return m_sslCertificateBin;
	}

	public void setSslCertificateBin(byte[] sslCertificateBin)
	{
		m_sslCertificateBin = sslCertificateBin;
	}

	public Boolean getHasSslCertificateBin()
	{
		return m_hasSslCertificateBin;
	}

	public void setHasSslCertificateBin(Boolean hasSslCertificateBin)
	{
		m_hasSslCertificateBin = hasSslCertificateBin;
	}

	public String getDirectoryId()
	{
		return m_directoryId;
	}

	public void setDirectoryId(String directoryId)
	{
		m_directoryId = directoryId;
	}

	public String getTempSslCertBinDir()
	{
		return m_tempSslCertBinDir;
	}

	public void setTempSslCertBinDir(String tempSslCertBinDir)
	{
		m_tempSslCertBinDir = tempSslCertBinDir;
	}

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public String getUrl()
	{
		return m_url;
	}

	public void setUrl(String url)
	{
		m_url = url;
	}
}
