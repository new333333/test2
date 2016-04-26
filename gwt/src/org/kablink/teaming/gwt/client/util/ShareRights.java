/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
 * 
 * @author ?
 */
public class ShareRights implements IsSerializable
{
	private AccessRights m_accessRights;
	private AccessRights m_unAlteredAccessRights;
	private boolean m_canShareForward;
	private boolean m_canShareWithExternalUsers;
	private boolean m_canShareWithInternalUsers;
	private boolean m_canShareWithPublic;
	private boolean m_canSharePublicLink;
	
	/**
	 * IMPORTANT: The ordinal number of the enum is significant for this class.
	 *            We use it for easy comparison of which rights are more or less.
	 *            So do NOT change the ordering!
	 */
	public enum AccessRights implements IsSerializable
	{
		NONE,
		VIEWER,
		EDITOR,
		CONTRIBUTOR
	}
	
	/**
	 * 
	 */
	public ShareRights()
	{
		m_accessRights = AccessRights.NONE;
		m_unAlteredAccessRights = AccessRights.NONE;
		m_canShareWithExternalUsers = false;
		m_canShareWithInternalUsers = false;
		m_canShareWithPublic = false;
		m_canSharePublicLink = false;
		m_canShareForward = false;
	}

	/**
	 * 
	 */
	public void copy( ShareRights rights )
	{
		if ( rights == null )
			return;
		
		setAccessRights( rights.getAccessRights() );
		setUnAlteredAccessRights( rights.getUnAlteredAccessRights() );
		setCanShareForward( rights.getCanShareForward() );
		setCanSharePublicLink( rights.getCanSharePublicLink() );
		setCanShareWithExternalUsers( rights.getCanShareWithExternalUsers() );
		setCanShareWithInternalUsers( rights.getCanShareWithInternalUsers() );
		setCanShareWithPublic( rights.getCanShareWithPublic() );
	}
	
	/**
	 * 
	 */
	public boolean equalsRights( ShareRights rights )
	{
		if ( rights == null )
			return false;
		
		if ( getAccessRights() != rights.getAccessRights() )
			return false;
		
		if ( getUnAlteredAccessRights() != rights.getUnAlteredAccessRights() )
			return false;
		
		if ( getCanShareForward() != rights.getCanShareForward() )
			return false;
		
		if ( getCanSharePublicLink() != rights.getCanSharePublicLink() )
			return false;
		
		if ( getCanShareWithExternalUsers() != rights.getCanShareWithExternalUsers() )
			return false;
		
		if ( getCanShareWithInternalUsers() != rights.getCanShareWithInternalUsers() )
			return false;
		
		if ( getCanShareWithPublic() != rights.getCanShareWithPublic() )
			return false;
		
		return true;
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
	public AccessRights getUnAlteredAccessRights()
	{
		return m_unAlteredAccessRights;
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
	public boolean getCanSharePublicLink()
	{
		return m_canSharePublicLink;
	}
	
	/**
	 * 
	 */
	public String getReshareRightsAsString()
	{
		StringBuffer sb;
		
		if ( m_canShareForward == false )
			return GwtTeaming.getMessages().shareDlg_reshareNo();
		
		sb = new StringBuffer();
		if ( m_canShareWithInternalUsers )
			sb.append( GwtTeaming.getMessages().shareDlg_reshareInternal() );
		
		if ( m_canShareWithExternalUsers )
		{
			if ( sb.length() > 0 )
				sb.append( ", " );
			
			sb.append( GwtTeaming.getMessages().shareDlg_reshareExternal() );
		}
		
		if ( m_canShareWithPublic )
		{
			if ( sb.length() > 0 )
				sb.append( ", " );
			
			sb.append( GwtTeaming.getMessages().shareDlg_resharePublic() );
		}
		
		if ( m_canSharePublicLink )
		{
			if ( sb.length() > 0 )
				sb.append( ", " );
			
			sb.append( GwtTeaming.getMessages().shareDlg_resharePublicLink() );
		}
		
		return sb.toString();
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
	public void setUnAlteredAccessRights( AccessRights unAlteredAccessRights )
	{
		m_unAlteredAccessRights = unAlteredAccessRights;
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
	
	/**
	 * 
	 */
	public void setCanSharePublicLink( boolean canSharePublicLink )
	{
		m_canSharePublicLink = canSharePublicLink;
	}
}

