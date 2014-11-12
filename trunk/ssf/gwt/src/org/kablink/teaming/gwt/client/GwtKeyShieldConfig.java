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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to represent the KeyShield SSO configuration data
 * @author jwootton
 *
 */
public class GwtKeyShieldConfig implements IsSerializable, VibeRpcResponseData
{
	private boolean m_enabled;
	private String m_serverUrl;
	private int m_httpConnectionTimeout;	// Timeout in milliseconds
	private String m_apiAuthKey;
	private String m_authConnectorNames;
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	private GwtKeyShieldConfig()
	{
		m_enabled = false;
		m_serverUrl = null;
		m_httpConnectionTimeout = 250;
		m_apiAuthKey = null;
		m_authConnectorNames = null;
	}

	/**
	 * 
	 */
	public static GwtKeyShieldConfig getGwtKeyShieldConfig()
	{
		return new GwtKeyShieldConfig();
	}
	
	/**
	 * 
	 */
	public String getApiAuthKey()
	{
		return m_apiAuthKey;
	}
	
	/**
	 * 
	 */
	public String getAuthConnectorNames()
	{
		return m_authConnectorNames;
	}
	
	/**
	 * 
	 */
	public int getHttpConnectionTimeout()
	{
		return m_httpConnectionTimeout;
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
	public boolean isEnabled()
	{
		return m_enabled;
	}
	
	/**
	 * 
	 */
	public void setApiAuthKey( String authKey )
	{
		m_apiAuthKey = authKey;
	}
	
	/**
	 * 
	 */
	public void setAuthConnectorNames( String authConnectorNames )
	{
		m_authConnectorNames = authConnectorNames;
	}
	
	/**
	 * 
	 */
	public void setHttpConnectionTimeout( int timeout )
	{
		m_httpConnectionTimeout = timeout;
	}
	
	/**
	 * 
	 */
	public void setIsEnabled( boolean enabled )
	{
		m_enabled = enabled;
	}
	
	/**
	 * 
	 */
	public void setServerUrl( String serverUrl )
	{
		m_serverUrl = serverUrl;
	}
}
