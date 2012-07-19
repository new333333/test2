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
import java.util.List;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represents all of the sharing information for a given entity
 */
public class GwtSharingInfo
	implements IsSerializable, VibeRpcResponseData
{
	private ArrayList<EntityId> m_listOfEntityIds;
	private ArrayList<GwtShareItem> m_listOfShareItems;
	private ArrayList<GwtShareItemMember> m_listOfShareItemMembers;
	private boolean m_sendEmailToAll;
	
	/**
	 * 
	 */
	public GwtSharingInfo()
	{
		m_listOfEntityIds = null;
		m_listOfShareItems = null;
		m_listOfShareItemMembers = null;
		m_sendEmailToAll = false;
	}
	
	/**
	 * 
	 */
	public void addEntityId( EntityId entityId )
	{
		if ( m_listOfEntityIds == null )
			m_listOfEntityIds = new ArrayList<EntityId>();
		
		m_listOfEntityIds.add( entityId );
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
	public void addShareItemMember( GwtShareItemMember shareItemMember )
	{
		if ( m_listOfShareItemMembers == null )
			m_listOfShareItemMembers = new ArrayList<GwtShareItemMember>();
		
		m_listOfShareItemMembers.add( shareItemMember );
	}
	
	/**
	 * 
	 */
	public ArrayList<EntityId> getListOfEntityIds()
	{
		return m_listOfEntityIds;
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
	public ArrayList<GwtShareItemMember> getListOfShareItemMembers()
	{
		return m_listOfShareItemMembers;
	}
	
	/**
	 * 
	 */
	public boolean getSendEmailToAll()
	{
		return m_sendEmailToAll;
	}
	
	/**
	 * Get the GwtShareItem for the given EntityId
	 */
	public GwtShareItem getShareItem( EntityId entityId )
	{
		if ( m_listOfShareItems != null )
		{
			for (GwtShareItem nextShareItem : m_listOfShareItems)
			{
				// Does this GwtShareItem belong to the given EntityId
				if ( nextShareItem.entityIdEquals( entityId ) )
				{
					// Yes
					return nextShareItem;
				}
			}
		}
		
		// If we get here we did not find the a GwtShareItem for the given EntityId
		return null;
	}
	
	/**
	 * 
	 */
	public void setListOfEntityIds( List<EntityId> entityIds )
	{
		if ( entityIds != null )
		{
			m_listOfEntityIds = new ArrayList<EntityId>();
			for (EntityId nextEntityId : entityIds)
			{
				m_listOfEntityIds.add( nextEntityId );
			}
		}
		else
			m_listOfEntityIds = null;
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
	public void setListOfShareItemMembers( ArrayList<GwtShareItemMember> listOfShareItemMembers )
	{
		m_listOfShareItemMembers = listOfShareItemMembers;
	}
	
	/**
	 * 
	 */
	public void setSendEmailToAll( boolean sendEmailToAll )
	{
		m_sendEmailToAll = sendEmailToAll;
	}
}

