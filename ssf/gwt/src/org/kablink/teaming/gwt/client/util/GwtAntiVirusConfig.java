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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * GWT client mapping of the domain GwtAntiVirusConfig object.
 * 
 * @author drfoster@novell.com
 */
public class GwtAntiVirusConfig implements IsSerializable {
	/**
	 * Enumeration value for the type of anti virus this configuration
	 * is for.
	 */
	public enum GwtAntiVirusType implements IsSerializable {
		gwava;

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isGWAVA() {return this.equals(gwava);}
	}

	private GwtAntiVirusType	m_type;			//
	private boolean				m_enabled;		//
	private String				m_serverUrl;	//
	private String				m_username;		//
	private String 				m_password;		//
	
	/**
	 * Constructor for Hibernate
	 */
	public GwtAntiVirusConfig() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Copies the values from a GwtAntiVirusConfig into this one.
	 * 
	 * @param config
	 */
	public void copy(GwtAntiVirusConfig config) {
		m_type      = config.getType();
		m_enabled   = config.isEnabled();
		m_serverUrl = config.getServerUrl();
		m_username  = config.getUsername();
		m_password  = config.getPassword();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean          isEnabled()    {return m_enabled;  }
	public GwtAntiVirusType getType()      {return m_type;     }
	public String           getPassword()  {return m_password; }
	public String           getServerUrl() {return m_serverUrl;}
	public String           getUsername()  {return m_username; }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEnabled(  boolean          enabled)   {m_enabled   = enabled;  }
	public void setType(     GwtAntiVirusType type)      {m_type      = type;     }
	public void setPassword( String           password)  {m_password  = password; }
	public void setServerUrl(String           serverUrl) {m_serverUrl = serverUrl;}
	public void setUsername( String           username)  {m_username  = username; }
}
