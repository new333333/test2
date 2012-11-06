/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represents the different share rights
 */
public class ShareRights implements IsSerializable
{
	private AccessRights m_accessRights;
	private boolean m_canShareForward;
	private boolean m_canShareWithExternalUsers;
	private boolean m_canShareWithInternalUsers;
	private boolean m_canShareWithPublic;
	
	/**
	 * 
	 */
	public enum AccessRights implements IsSerializable
	{
		VIEWER,
		EDITOR,
		CONTRIBUTOR,
		
		UNKNOWN
	}
	
	/**
	 * 
	 */
	public ShareRights()
	{
		m_accessRights = AccessRights.UNKNOWN;
		m_canShareWithExternalUsers = false;
		m_canShareWithInternalUsers = false;
		m_canShareWithPublic = false;
		m_canShareForward = false;
	}
	
	/**
	 * 
	 */
	public AccessRights getAccessRights()
	{
		return m_accessRights;
	}
	
	/**
	 * 
	 */
	public boolean getCanShareForward()
	{
		return m_canShareForward;
	}
	
	/**
	 * 
	 */
	public boolean getCanShareWithExternalUsers()
	{
		return m_canShareWithExternalUsers;
	}
	
	/**
	 * 
	 */
	public boolean getCanShareWithInternalUsers()
	{
		return m_canShareWithInternalUsers;
	}
	
	/**
	 * 
	 */
	public boolean getCanShareWithPublic()
	{
		return m_canShareWithPublic;
	}
	
	/**
	 * 
	 */
	public String getShareRightsAsString()
	{
		if ( m_accessRights == ShareRights.AccessRights.VIEWER )
			return GwtTeaming.getMessages().shareDlg_viewer();
		
		if ( m_accessRights == ShareRights.AccessRights.CONTRIBUTOR )
			return GwtTeaming.getMessages().shareDlg_contributor();
		
		if ( m_accessRights == ShareRights.AccessRights.EDITOR )
			return GwtTeaming.getMessages().shareDlg_editor();
		
		return "Unknown";
	}
	
	/**
	 * 
	 */
	public void setAccessRights( AccessRights accessRights )
	{
		m_accessRights = accessRights;
	}
	
	/**
	 * 
	 */
	public void setCanShareForward( boolean canShareForward )
	{
		m_canShareForward = canShareForward;
	}
	
	/**
	 * 
	 */
	public void setCanShareWithExternalUsers( boolean canShareWithExternalUsers )
	{
		m_canShareWithExternalUsers = canShareWithExternalUsers;
	}
	
	/**
	 * 
	 */
	public void setCanShareWithInternalUsers( boolean canShareWithInternalUsers )
	{
		m_canShareWithInternalUsers = canShareWithInternalUsers;
	}
	
	/**
	 * 
	 */
	public void setCanShareWithPublic( boolean canShareWithPublic )
	{
		m_canShareWithPublic = canShareWithPublic;
	}
}

