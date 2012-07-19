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
 * This class is used to hold information about a share
 */
public class GwtShareItemMember
	implements IsSerializable
{
	private GwtShareItem m_shareItem;
	private Long m_recipientId;
	private String m_recipientName;
	private GwtRecipientType m_recipientType;
	private ShareRights m_shareRights;
	private ShareExpirationValue m_shareExpirationValue;
	private boolean m_isDirty;
	
	/**
	 * 
	 */
	public GwtShareItemMember()
	{
		m_shareItem = null;
		m_recipientName = null;
		m_recipientId = null;
		m_recipientType = GwtRecipientType.UNKNOWN;
		m_shareRights = ShareRights.VIEW;
		m_shareExpirationValue = null;
		m_isDirty = false;
	}
	
	/**
	 * 
	 */
	public boolean isDirty()
	{
		return m_isDirty;
	}
	
	/**
	 * 
	 */
	public Long getRecipientId()
	{
		return m_recipientId;
	}
	
	/**
	 * 
	 */
	public String getRecipientName()
	{
		return m_recipientName;
	}

	/**
	 * 
	 */
	public GwtRecipientType getRecipientType()
	{
		return m_recipientType;
	}
	
	/**
	 * 
	 */
	public String getRecipientTypeAsString()
	{
		if ( m_recipientType == GwtRecipientType.USER )
			return GwtTeaming.getMessages().shareRecipientTypeUser();
		
		if ( m_recipientType == GwtRecipientType.GROUP )
			return GwtTeaming.getMessages().shareRecipientTypeGroup();
		
		if ( m_recipientType == GwtRecipientType.EXTERNAL_USER )
			return GwtTeaming.getMessages().shareRecipientTypeExternalUser();
		
		if ( m_recipientType == GwtRecipientType.TEAM )
			return GwtTeaming.getMessages().shareRecipientTypeTeam();
		
		return GwtTeaming.getMessages().unknownShareType();
	}
	
	/**
	 * 
	 */
	public ShareExpirationValue getShareExpirationValue()
	{
		return m_shareExpirationValue;
	}
	
	/**
	 * 
	 */
	public String getShareExpirationValueAsString()
	{
		if ( m_shareExpirationValue != null )
			return m_shareExpirationValue.getValueAsString();
		
		return "";
	}
	
	/**
	 * Return the GwtShareItem this object is associated with.
	 */
	public GwtShareItem getShareItem()
	{
		return m_shareItem;
	}
	
	/**
	 * 
	 */
	public ShareRights getShareRights()
	{
		return m_shareRights;
	}
	
	/**
	 * 
	 */
	public String getShareRightsAsString()
	{
		if ( m_shareRights == ShareRights.VIEW )
			return GwtTeaming.getMessages().shareDlg_view();
		
		if ( m_shareRights == ShareRights.CONTRIBUTOR )
			return GwtTeaming.getMessages().shareDlg_contributor();
		
		if ( m_shareRights == ShareRights.OWNER )
			return GwtTeaming.getMessages().shareDlg_owner();
		
		return "Unknown";
	}
	
	/**
	 * 
	 */
	public void setIsDirty( boolean isDirty )
	{
		m_isDirty = isDirty;
	}
	
	/**
	 * 
	 */
	public void setRecipientId( Long id )
	{
		m_recipientId = id;
	}
	
	/**
	 * 
	 */
	public void setRecipientName( String name )
	{
		m_recipientName = name;
	}
	
	/**
	 * 
	 */
	public void setShareExpirationValue( ShareExpirationValue value )
	{
		m_shareExpirationValue = new ShareExpirationValue( value );
	}
	
	/**
	 * Set the GwtShareItem this member is associated with. 
	 */
	public void setShareItem( GwtShareItem shareItem )
	{
		m_shareItem = shareItem;
	}
	
	/**
	 * 
	 */
	public void setShareRights( ShareRights shareRights )
	{
		m_shareRights = shareRights;
	}
	
	/**
	 * 
	 */
	public void setRecipientType( GwtRecipientType type )
	{
		m_recipientType = type;
	}
}


