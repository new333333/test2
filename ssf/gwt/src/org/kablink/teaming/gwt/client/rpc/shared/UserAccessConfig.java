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
package org.kablink.teaming.gwt.client.rpc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get user access config' GWT RPC command.
 * 
 * @author jwootton@novell.com
 */
public class UserAccessConfig
	implements IsSerializable, VibeRpcResponseData
{
	private boolean m_allowGuestAccess;
	private boolean m_guestReadOnly;
	private boolean m_allowSelfReg;
	private boolean m_allowExternalUsers;
	private boolean m_allowExternalUsersSelfReg;
	private boolean m_allowDownload;
	private boolean m_allowWebAccess;
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public UserAccessConfig()
	{
		// Initialize the super class.
		super();
		
		m_allowExternalUsers = false;
		m_allowExternalUsersSelfReg = false;
		m_allowGuestAccess = false;
		m_allowSelfReg = false;
		m_allowDownload = false;
		m_allowWebAccess = false;
	}

	/**
	 * 
	 */
	public boolean getAllowExternalUsers()
	{
		return m_allowExternalUsers;
	}

	/**
	 * 
	 */
	public boolean getAllowExternalUsersSelfReg()
	{
		return m_allowExternalUsersSelfReg;
	}

	/**
	 * 
	 */
	public boolean getAllowGuestAccess()
	{
		return m_allowGuestAccess;
	}

	/**
	 * 
	 */
	public boolean getGuestReadOnly()
	{
		return m_guestReadOnly;
	}

	/**
	 * 
	 */
	public boolean getAllowSelfReg()
	{
		return m_allowSelfReg;
	}

	/**
	 * 
	 */
	public boolean getAllowDownload()
	{
		return m_allowDownload;
	}

	/**
	 * 
	 */
	public boolean getAllowWebAccess()
	{
		return m_allowWebAccess;
	}

	/**
	 * 
	 */
	public void setAllowExternalUsers( boolean allow )
	{
		m_allowExternalUsers = allow;
	}

	/**
	 * 
	 */
	public void setAllowExternalUsersSelfReg( boolean allow )
	{
		m_allowExternalUsersSelfReg = allow;
	}

	/**
	 * 
	 */
	public void setAllowGuestAccess( boolean allow )
	{
		m_allowGuestAccess = allow;
	}

	/**
	 * 
	 */
	public void setGuestReadOnly( boolean readOnly )
	{
		m_guestReadOnly = readOnly;
	}

	/**
	 * 
	 */
	public void setAllowSelfReg( boolean allow )
	{
		m_allowSelfReg = allow;
	}
	
	/**
	 * 
	 */
	public void setAllowDownload( boolean allow )
	{
		m_allowDownload = allow;
	}
	
	/**
	 * 
	 */
	public void setAllowWebAccess( boolean allow )
	{
		m_allowWebAccess = allow;
	}
}
