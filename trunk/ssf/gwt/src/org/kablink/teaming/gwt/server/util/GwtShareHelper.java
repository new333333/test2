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
package org.kablink.teaming.gwt.server.util;

import java.util.Collection;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItemMember;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtShareItemMember;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code that services share requests.
 *
 * @author jwootton@novell.com
 */
public class GwtShareHelper 
{
	protected static Log m_logger = LogFactory.getLog( GwtShareHelper.class );

	/**
	 * Return sharing information for the given entities
	 */
	public static GwtSharingInfo getSharingInfo( List<EntityId> listOfEntityIds )
	{
		GwtSharingInfo sharingInfo;
		User currentUser;

		sharingInfo = new GwtSharingInfo();

		currentUser = GwtServerHelper.getCurrentUser();

		if ( listOfEntityIds != null && listOfEntityIds.size() > 0 )
		{
			ProfileDao profileDao;

			profileDao = (ProfileDao)SpringContextUtil.getBean( "profileDao" );
			if ( profileDao != null )
			{
				for (EntityId nextEntityId : listOfEntityIds)
				{
					List<ShareItem> listOfShareItems;
					EntityIdentifier entityIdentifier;
					
					// Set the entity type
					{
						EntityType entityType;
						Long entityId;
					
						if ( nextEntityId.isEntry() )
						{
							entityId = nextEntityId.getEntityId();
							entityType = EntityType.folderEntry;
						}
						else
						{
							String entityTypeS;

							entityTypeS = nextEntityId.getEntityType();
							
							//!!! Finish
							if ( entityTypeS.equalsIgnoreCase( EntityType.folder.toString() ) )
								entityType = EntityType.folder;
							else if ( entityTypeS.equalsIgnoreCase( EntityType.workspace.toString() ) )
								entityType = EntityType.workspace;
							else
								entityType = EntityType.none;
								
							entityId = nextEntityId.getBinderId();
						}
						
						entityIdentifier = new EntityIdentifier( entityId, entityType );
					}
					
					// Get the ShareItem for the given entity
					listOfShareItems = profileDao.findShareItemsBySharerAndSharedEntity(
															currentUser.getId(),
															entityIdentifier );
					if ( listOfShareItems != null )
					{
						for (ShareItem nextShareItem : listOfShareItems)
						{
							GwtShareItem gwtShareItem;
							Collection<ShareItemMember> listOfMembers;
							
							gwtShareItem = new GwtShareItem();
							gwtShareItem.setDesc( nextShareItem.getDescription().getText() );
							gwtShareItem.setEntityId( nextEntityId );
							gwtShareItem.setId( nextShareItem.getId() );
							
							sharingInfo.addShareItem( gwtShareItem );
							
							// Get all of the information about how this entity is shared.
							listOfMembers = nextShareItem.getMembers();
							if ( listOfMembers != null )
							{
								for (ShareItemMember nextMember : listOfMembers)
								{
									GwtShareItemMember gwtShareItemMember;
									
									gwtShareItemMember = new GwtShareItemMember();
									gwtShareItemMember.setRecipientId( nextMember.getRecipientId() );
									
									// Set the recipient's name
									{
										User user;
										
										try 
										{
											ZoneInfo zoneInfo;
											String zoneId;
											
											// Get the id of the zone we are running in.
											zoneInfo = MiscUtil.getCurrentZone();
											zoneId = zoneInfo.getId();
											if ( zoneId == null )
												zoneId = "";

											//!!! Finish.  How do we construct the name
											user = profileDao.loadUser( nextMember.getRecipientId(), zoneId );
											gwtShareItemMember.setRecipientName( user.getName() );
										}
										catch ( Exception e )
										{
										}
									}

									// Set the recipient type
									{
										if ( nextMember.getRecipientType() == org.kablink.teaming.domain.ShareItemMember.RecipientType.group )
											gwtShareItemMember.setRecipientType( GwtRecipientType.GROUP );
										else if ( nextMember.getRecipientType() == org.kablink.teaming.domain.ShareItemMember.RecipientType.team )
											gwtShareItemMember.setRecipientType( GwtRecipientType.TEAM );
										else if ( nextMember.getRecipientType() == org.kablink.teaming.domain.ShareItemMember.RecipientType.user )
											gwtShareItemMember.setRecipientType( GwtRecipientType.USER );
										else
											gwtShareItemMember.setRecipientType( GwtRecipientType.UNKNOWN );
									}
									
									// Set the expiration value
									{
										ShareExpirationValue expirationValue;
										
										//!!! Finish
										expirationValue = new ShareExpirationValue();
										expirationValue.setType( ShareExpirationType.NEVER );
										gwtShareItemMember.setShareExpirationValue( expirationValue );
									}
									
									// Set the share rights
									{
										//!!! Finish
										gwtShareItemMember.setShareRights( ShareRights.VIEW );
									}
								}
							}
						}
					}
					
				}
			}
		}
		
		return sharingInfo;
	}
}
