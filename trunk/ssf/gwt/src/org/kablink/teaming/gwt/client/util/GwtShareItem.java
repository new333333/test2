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

import java.util.Comparator;

import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is used to hold information about a GwtShareItem.
 * When an item is shared, a row is created in the SS_ShareItem table.  This class
 * is the GWT representation of a row in that table.
 */
public class GwtShareItem
	implements IsSerializable
{
	private Long m_id;
	private EntityId m_entityId;	// Id of the item being shared.
	private String m_entityName;	// Name of the item being shared.
	private Long m_recipientId;
	private String m_recipientName;
	private GwtRecipientType m_recipientType;
	private ShareRights m_shareRights;
	private ShareExpirationValue m_shareExpirationValue;
	private boolean m_isDirty;
	private boolean m_isExpired;
	private boolean m_toBeDeleted;

	/**
	 * Inner class used to compare two GwtShareItem objects
	 */
	public static class GwtShareItemComparator implements Comparator<GwtShareItem> 
	{
		/**
		 * Class constructor.
		 */
		public GwtShareItemComparator() 
		{
		}

		/**
		 * Compares two GwtShareItem objects by the recipient's name.
		 * 
		 * Implements the Comparator.compare() method.
		 */
		@Override
		public int compare( GwtShareItem gwtShareItem1, GwtShareItem gwtShareItem2 ) 
		{
			int reply;
			String name1;
			String name2;

			name1 = gwtShareItem1.getRecipientName();
			name2 = gwtShareItem2.getRecipientName();
			reply = GwtClientHelper.safeSColatedCompare( name2, name1 );

			return reply;
		}
	}

	/**
	 * 
	 */
	public GwtShareItem()
	{
		m_id = null;
		m_entityId = null;
		m_entityName = null;
		m_recipientName = null;
		m_recipientId = null;
		m_recipientType = GwtRecipientType.UNKNOWN;
		m_shareRights = ShareRights.VIEW;
		m_shareExpirationValue = null;
		m_isDirty = false;
		m_isExpired = false;
		m_toBeDeleted = false;
	}

	/**
	 * Does this GwtShareItem belong to the given EntityId
	 */
	public boolean entityIdEquals( EntityId entityId )
	{
		if ( entityId != null && m_entityId != null )
		{
			return m_entityId.equalsEntityId( entityId );
		}
		
		return false;
	}
	
	/**
	 * Compare the given GwtShareItem object to this object.
	 */
	public boolean equals( GwtShareItem shareItem )
	{
		Long recipientId;
		EntityId entityId;
		String name;
		GwtRecipientType type;
		
		if ( shareItem == null )
			return false;
		
		name = shareItem.getRecipientName();
		type = shareItem.getRecipientType();
		entityId = shareItem.getEntityId();
		recipientId = shareItem.getRecipientId();

		// Are the entities the same?
		if ( entityIdEquals( entityId ) )
		{
			// Yes
			// Do we have a recipient id?
			if ( recipientId != null )
			{
				Long nextRecipientId;
				
				// Yes
				nextRecipientId = getRecipientId();
				if ( nextRecipientId != null && recipientId.compareTo( nextRecipientId ) == 0 )
				{
					// We found the recipient
					return true;
				}
			}
			else
			{
				if ( type == getRecipientType() &&
					 name != null && name.equalsIgnoreCase( getRecipientName() ) )
				{
					// We found the recipient.
					return true;
				}
			}
		}
		
		// If we get here the two GwtShareItem objects are not the same.
		return false;
	}
	
	/**
	 * 
	 */
	public EntityId getEntityId()
	{
		return m_entityId;
	}
	
	/**
	 * 
	 */
	public String getEntityName()
	{
		return m_entityName;
	}
	
	/**
	 * 
	 */
	public Long getId()
	{
		return m_id;
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
	public boolean getToBeDeleted()
	{
		return m_toBeDeleted;
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
	public boolean isExpired()
	{
		return m_isExpired;
	}
	
	/**
	 * 
	 */
	public void setEntityId( EntityId id )
	{
		m_entityId = id;
	}
	
	/**
	 * 
	 */
	public void setEntityName( String name )
	{
		m_entityName = name;
	}
	
	/**
	 * 
	 */
	public void setId( Long id)
	{
		m_id = id;
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
	public void setIsExpired( boolean isExpired )
	{
		m_isExpired = isExpired;
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
	public void setRecipientType( GwtRecipientType type )
	{
		m_recipientType = type;
	}
	
	/**
	 * 
	 */
	public void setShareExpirationValue( ShareExpirationValue value )
	{
		m_shareExpirationValue = new ShareExpirationValue( value );
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
	public void setToBeDeleted( boolean toBeDeleted )
	{
		m_toBeDeleted = toBeDeleted;
	}
}

