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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.widgets.ShareSendToWidget.SendToValue;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represents all of the sharing information for a given entity
 */
public class GwtSharingInfo
	implements IsSerializable, VibeRpcResponseData
{
	private ArrayList<GwtShareItem> m_listOfShareItems;
	private ArrayList<GwtShareItem> m_listOfToBeDeletedShareItems;
	private HashMap<EntityId, String> m_entityNamesMap;
	private HashMap<EntityId, ShareRights> m_entityShareRightsMap;	// Holds the "highest" access right the logged-in user has to the entity
	private SendToValue m_sendToValue;
	private boolean m_notifyRecipients;
	private boolean m_canShareWithExternalUsers;
	private boolean m_canShareWithInternalUsers;
	private boolean m_canShareWithPublic;
	private boolean m_canShareWithAllExternalUsersGroup;
	private boolean m_canShareWithAllInternalUsersGroup;
	private boolean m_canShareWithLdapGroups;
	private boolean m_canShareUsingFileLink;
	
	/**
	 * 
	 */
	public GwtSharingInfo()
	{
		m_listOfShareItems = null;
		m_listOfToBeDeletedShareItems = null;
		m_entityNamesMap = null;
		m_entityShareRightsMap = null;
		m_notifyRecipients = true;
		m_canShareWithExternalUsers = false;
		m_canShareWithInternalUsers = false;
		m_canShareWithPublic = false;
		m_canShareWithAllExternalUsersGroup = false;
		m_canShareWithAllInternalUsersGroup = false;
		m_canShareWithLdapGroups = false;
		m_canShareUsingFileLink = false;
		m_sendToValue = SendToValue.ALL_RECIPIENTS;
	}
	
	/**
	 * 
	 */
	public void addShareItem( GwtShareItem shareItem )
	{
		if ( m_listOfShareItems == null )
			m_listOfShareItems = new ArrayList<GwtShareItem>();
		
		m_listOfShareItems.add( shareItem );
	}
	
	/**
	 * 
	 */
	public void addToBeDeleted( GwtShareItem shareItem )
	{
		if ( m_listOfToBeDeletedShareItems == null )
			m_listOfToBeDeletedShareItems = new ArrayList<GwtShareItem>();
		
		// Does this ShareItem exist in the db?
		if ( shareItem.getId() != null )
		{
			// Yes, add it to the list of ShareItems to be deleted.
			shareItem.setToBeDeleted( true );
			m_listOfToBeDeletedShareItems.add( shareItem );
		}
		else
		{
			// No
			// Is this a public share?
			if ( shareItem instanceof GwtPublicShareItem )
			{
				GwtPublicShareItem publicShareItem;
				
				publicShareItem = (GwtPublicShareItem) shareItem;

				// Is this an existing public share?
				if ( publicShareItem.getAllInternalShareItem() != null &&
					 publicShareItem.getGuestShareItem() != null )
				{
					// Yes
					shareItem.setToBeDeleted( true );
					m_listOfToBeDeletedShareItems.add( shareItem );
				}
			}
		}
	}
	
	/**
	 * Return the "highest" share right the logged-in user has to the given entity
	 */
	public ShareRights getShareRights( EntityId entityId )
	{
		Set<EntityId> entityIds;
		
		if ( entityId == null || m_entityShareRightsMap == null )
			return null;
		
		entityIds = m_entityShareRightsMap.keySet();
		if ( entityIds != null )
		{
			for ( EntityId nextEntityId : entityIds )
			{
				// Is this the EntityId we are looking for?
				if ( entityId.equalsEntityId( nextEntityId ) )
				{
					ShareRights shareRights;
					
					// Yes
					shareRights = m_entityShareRightsMap.get( nextEntityId );
					return shareRights;
				}
			}
		}
		
		// If we get here we did not find the entityId
		return null;
	}
	
	/**
	 * 
	 */
	public boolean getCanShareUsingFileLink()
	{
		return m_canShareUsingFileLink;
	}
	
	/**
	 * 
	 */
	public boolean getCanShareWithAllExternalUsersGroup()
	{
		return m_canShareWithAllExternalUsersGroup;
	}
	
	/**
	 * 
	 */
	public boolean getCanShareWithAllInternalUsersGroup()
	{
		return m_canShareWithAllInternalUsersGroup;
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
	public boolean getCanShareWithLdapGroups()
	{
		return m_canShareWithLdapGroups;
	}
	
	/**
	 * 
	 */
	public boolean getCanShareWithPublic()
	{
		return m_canShareWithPublic;
	}
	
	/**
	 * Return the name of the given entity
	 */
	public String getEntityName( EntityId entityId )
	{
		Set<EntityId> entityIds;
		
		if ( entityId == null || m_entityNamesMap == null )
			return null;
		
		entityIds = m_entityNamesMap.keySet();
		if ( entityIds != null )
		{
			for ( EntityId nextEntityId : entityIds )
			{
				// Is this the EntityId we are looking for?
				if ( entityId.equalsEntityId( nextEntityId ) )
				{
					// Yes
					return m_entityNamesMap.get( nextEntityId );
				}
			}
		}
		
		// If we get here we did not find the entityId
		return null;
	}
	
	/**
	 * 
	 */
	public HashMap<EntityId, String> getEntityNamesMap()
	{
		return m_entityNamesMap;
	}
	
	/**
	 * Return the list of entities we are sharing
	 */
	public ArrayList<EntityId> getListOfEntities()
	{
		ArrayList<EntityId> listOfEntities;
		
		listOfEntities = new ArrayList<EntityId>();
		if ( m_entityNamesMap != null )
		{
			Set<EntityId> entityIds;

			entityIds = m_entityNamesMap.keySet();
			if ( entityIds != null )
			{
				for ( EntityId nextEntityId : entityIds )
				{
					listOfEntities.add( nextEntityId );
				}
			}
		}
		
		return listOfEntities;
	}
	
	/**
	 * 
	 */
	public ArrayList<GwtShareItem> getListOfShareItems()
	{
		return m_listOfShareItems;
	}
	
	/**
	 * 
	 */
	public ArrayList<GwtShareItem> getListOfToBeDeletedShareItems()
	{
		return m_listOfToBeDeletedShareItems;
	}
	
	/**
	 * 
	 */
	public boolean getNotifyRecipients()
	{
		return m_notifyRecipients;
	}
	
	/**
	 * 
	 */
	public SendToValue getSendToValue()
	{
		return m_sendToValue;
	}

	/**
	 * 
	 */
	public void setCanShareUsingFileLink( boolean canShare )
	{
		m_canShareUsingFileLink = canShare;
	}
	
	/**
	 * 
	 */
	public void setCanShareWithAllExternalUsersGroup( boolean canShare )
	{
		m_canShareWithAllExternalUsersGroup = canShare;
	}
	
	/**
	 * 
	 */
	public void setCanShareWithAllInternalUsersGroup( boolean canShare )
	{
		m_canShareWithAllInternalUsersGroup = canShare;
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
	public void setCanShareWithLdapGroups( boolean canShare )
	{
		m_canShareWithLdapGroups = canShare;
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
	public void setEntityShareRights( EntityId entityId, ShareRights shareRights )
	{
		if ( m_entityShareRightsMap == null )
			m_entityShareRightsMap = new HashMap<EntityId, ShareRights>();
		
		m_entityShareRightsMap.put( entityId, shareRights );
	}
	
	/**
	 * 
	 */
	public void setEntityName( EntityId entityId, String entityName )
	{
		if ( m_entityNamesMap == null )
			m_entityNamesMap = new HashMap<EntityId, String>();
		
		m_entityNamesMap.put( entityId, entityName );
	}
	
	/**
	 * 
	 */
	public void setEntityNamesMap( HashMap<EntityId, String> entityNamesMap )
	{
		m_entityNamesMap = entityNamesMap;
	}
	
	/**
	 * 
	 */
	public void setListOfShareItems( ArrayList<GwtShareItem> listOfShareItems )
	{
		m_listOfShareItems = listOfShareItems;
	}
	
	/**
	 * 
	 */
	public void setListOfToBeDeletedShareItems( ArrayList<GwtShareItem> listOfToBeDeletedShareItems )
	{
		m_listOfToBeDeletedShareItems = listOfToBeDeletedShareItems;
	}
	
	/**
	 * 
	 */
	public void setNotifyRecipients( boolean notifyRecipients )
	{
		m_notifyRecipients = notifyRecipients;
	}
	
	/**
	 * 
	 */
	public void setSendToValue( SendToValue sendToValue )
	{
		m_sendToValue = sendToValue;
	}
}

