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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItemMember.RecipientType;
import org.kablink.teaming.domain.ShareItemMember.RightSet;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItemMember;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtShareItemMember;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SpringContextUtil;


/**
 * Helper methods for the GWT UI server code that services share requests.
 *
 * @author jwootton@novell.com
 */
public class GwtShareHelper 
{
	protected static Log m_logger = LogFactory.getLog( GwtShareHelper.class );
	private static RightSet m_viewRightSet;
	private static RightSet m_contributorRightSet;
	private static RightSet m_ownerRightSet;
	

	/**
	 * Compare the 2 RightSet objects to see if they have the same rights
	 */
	private static boolean areRightSetsEqual( RightSet rightSet1, RightSet rightSet2 )
	{
		if ( rightSet1 == null || rightSet2 == null )
		{
			m_logger.error( "In GwtShareHelper.areRightSetsEqual(), one of the RightSet parameters is null" );
			return false;
		}
	
		return rightSet1.equals( rightSet2 );
	}
	
	/**
	 * Return the RightSet that corresponds to the "Contributor" rights
	 */
	private static RightSet getContributorRightSet()
	{
		if ( m_contributorRightSet == null )
		{
			m_contributorRightSet = new RightSet();
			m_contributorRightSet.setReadEntries( true );
			m_contributorRightSet.setCreateEntries( true );
			m_contributorRightSet.setModifyEntries( true );
			m_contributorRightSet.setDeleteEntries( true );
		}
		
		return m_contributorRightSet;
	}
	
	/**
	 * Return the RightSet that corresponds to the "Owner" rights
	 */
	private static RightSet getOwnerRightSet()
	{
		if ( m_ownerRightSet == null )
		{
			m_ownerRightSet = new RightSet();
			m_ownerRightSet.setReadEntries( true );
			m_ownerRightSet.setCreateEntries( true );
			m_ownerRightSet.setModifyEntries( true );
			m_ownerRightSet.setDeleteEntries( true );
			m_ownerRightSet.setAddReplies( true );
			m_ownerRightSet.setBinderAdministration( true );
			m_ownerRightSet.setCreateEntryAcls( true );
			m_ownerRightSet.setChangeAccessControl( true );
		}
		
		return m_ownerRightSet;
	}
	
	/**
	 * Return the name of the given principal
	 */
	private static String getRecipientName( AllModulesInjected ami, ShareItemMember shareItemMember )
	{
		String name = null;
		
		if ( shareItemMember != null )
		{
			// Set the recipient's name
			try 
			{
				Principal principal;
				
				//!!! Finish.  How do we construct the name
				principal = ami.getProfileModule().getEntry( shareItemMember.getRecipientId() );
				name = principal.getName();
			}
			catch ( Exception e )
			{
				m_logger.error( "Could find the user: " + shareItemMember.getRecipientId().toString() );
			}
		}
		
		return name;
	}

	/**
	 * Return the appropriate RightSet object for the given ShareRights object
	 */
	private static RightSet getRightSetFromShareRights( ShareRights shareRights )
	{
		RightSet rightSet;
		
		switch ( shareRights )
		{
		case CONTRIBUTOR:
			rightSet = getContributorRightSet(); 
			break;
		
		case OWNER:
			rightSet = getOwnerRightSet();
			break;
		
		case VIEW:
			rightSet = getViewRightSet();
			break;
			
		case UNKNOWN:
		default:
			rightSet = new RightSet();
			m_logger.error( "In GwtShareHelper.getRightSet(), unknown share rights" );
			break;
		}
	
		return rightSet;
	}
	
	/**
	 * Get a ShareRights object that corresponds to the given RightSet
	 */
	public static ShareRights getShareRightsFromRightSet( RightSet rightSet )
	{
		if ( rightSet != null )
		{
			RightSet tmpRightSet;
			
			// Is the given RightSet equal to the "View" RightSet
			tmpRightSet = getViewRightSet();
			if ( areRightSetsEqual( rightSet, tmpRightSet ) )
			{
				// Yes
				return ShareRights.VIEW;
			}
			
			// Is the given RightSet equal to the "Contributor" RightSet
			tmpRightSet = getContributorRightSet();
			if ( areRightSetsEqual( rightSet, tmpRightSet ) )
			{
				// Yes
				return ShareRights.CONTRIBUTOR;
			}
			
			// Is the given RightSet equal to the "Owner" RightSet
			tmpRightSet = getOwnerRightSet();
			if ( areRightSetsEqual( rightSet, tmpRightSet ) )
			{
				// Yes
				return ShareRights.OWNER;
			}
			
		}
		
		// If we get here we didn't find a match.
		return ShareRights.UNKNOWN;
	}

	/**
	 * Return sharing information for the given entities
	 */
	public static GwtSharingInfo getSharingInfo( AllModulesInjected ami, List<EntityId> listOfEntityIds )
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
				// For each given entity, get the sharing information.
				for (EntityId nextEntityId : listOfEntityIds)
				{
					List<ShareItem> listOfShareItems;
					EntityIdentifier entityIdentifier;
					
					// Get the entity type
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
									
									sharingInfo.addShareItemMember( gwtShareItemMember );
									
									gwtShareItemMember.setShareItem( gwtShareItem );
									gwtShareItemMember.setRecipientId( nextMember.getRecipientId() );
									
									// Set the recipient type and name.
									{
										String name;
										
										switch ( nextMember.getRecipientType() )
										{
										case group:
											name = getRecipientName( ami, nextMember );
											gwtShareItemMember.setRecipientName( name );
											gwtShareItemMember.setRecipientType( GwtRecipientType.GROUP );
											break;
											
										case user:
											name = getRecipientName( ami, nextMember );
											gwtShareItemMember.setRecipientName( name );
											gwtShareItemMember.setRecipientType( GwtRecipientType.USER );
											break;
										
										case team:
											//!!! Finish
											gwtShareItemMember.setRecipientType( GwtRecipientType.TEAM );
											break;
											
										default:
											gwtShareItemMember.setRecipientType( GwtRecipientType.UNKNOWN );
											m_logger.error( "unknown recipient type for user: " + nextMember.getRecipientId().toString() );
											break;
										}
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
										ShareRights shareRights;
										
										shareRights = getShareRightsFromRightSet( nextMember.getRightSet() );
										gwtShareItemMember.setShareRights( shareRights );
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


	/**
	 * Return the RightSet that corresponds to the "View" rights
	 */
	private static RightSet getViewRightSet()
	{
		if ( m_viewRightSet == null )
		{
			m_viewRightSet = new RightSet();
			m_viewRightSet.setReadEntries( true );
		}
		
		return m_viewRightSet;
	}
	
	/**
	 * Save the given share data. 
	 */
	public static GwtShareEntryResults shareEntry(
					AllModulesInjected ami,
					String comments,
					GwtSharingInfo sharingData )
	{
		ProfileModule profileModule;
		BinderModule binderModule;
		FolderModule folderModule;
		ProfileDao profileDao;
		GwtShareEntryResults results;
		ArrayList<EntityId> listOfEntityIds;

		profileDao = (ProfileDao)SpringContextUtil.getBean( "profileDao" );
		binderModule = ami.getBinderModule();
		folderModule = ami.getFolderModule();
		profileModule = ami.getProfileModule();

		if ( sharingData == null )
		{
			m_logger.error( "In GwtShareHelper.shareEntry(), sharingData is null." );
			return new GwtShareEntryResults();
		}

		listOfEntityIds = sharingData.getListOfEntityIds();
		if ( listOfEntityIds != null )
		{
			User currentUser;
			Description desc;
			ArrayList<ShareItemMember> members;

			currentUser = GwtServerHelper.getCurrentUser();

			// Get the list of members for this share
			{
				ArrayList<GwtShareItemMember> listOfMembers;

				members = new ArrayList<ShareItemMember>();
				
				//!!! Get the list of members this entity has already been shared with.
				listOfMembers = sharingData.getListOfShareItemMembers();
				if ( listOfMembers != null )
				{
					for (GwtShareItemMember nextMember : listOfMembers)
					{
						ShareItemMember shareItemMember;
						Date endDate = null;
						RecipientType recipientType;
						Long recipientId;
						RightSet rightSet;

						// Get the share expiration value
						{
							//!!! Finish
						}
						
						// Get the recipient type
						switch ( nextMember.getRecipientType() )
						{
						case EXTERNAL_USER:
							recipientType = RecipientType.user;
							break;
							
						case GROUP:
							recipientType = RecipientType.group;
							break;
							
						case TEAM:
							recipientType = RecipientType.team;
							break;
						
						case USER:
							recipientType = RecipientType.user;
							break;
							
						case UNKNOWN:
						default:
							recipientType = RecipientType.user;
							break;
						}
						
						recipientId = nextMember.getRecipientId();
						
						// Get the appropriate RightSet
						rightSet = getRightSetFromShareRights( nextMember.getShareRights() );
						
						shareItemMember = new ShareItemMember(
														endDate,
														recipientType,
														recipientId,
														rightSet );
						members.add( shareItemMember );
					}
				}
			}
			
			// Get the comments for the share
			{
				if ( comments == null )
					comments = "";
				
				desc = new Description( comments );
			}
			
			for (EntityId nextEntityId : listOfEntityIds)
			{
				GwtShareItem gwtShareItem;
				DefinableEntity sharedEntity;
				
				// Get the entity that is being shared.
				if ( nextEntityId.isBinder() )
					sharedEntity = binderModule.getBinder( nextEntityId.getEntityId() );
				else
					sharedEntity = folderModule.getEntry( nextEntityId.getBinderId(), nextEntityId.getEntityId() );

				// Get the GwtShareItem for the given entity
				gwtShareItem = sharingData.getShareItem( nextEntityId );

				// Does a ShareItem exist for the given EntityId?
				if ( gwtShareItem == null )
				{
					ShareItem shareItem;

					// No, create one.
					shareItem = new ShareItem(
										currentUser,
										sharedEntity,
										desc,
										members );
	
					profileModule.addShareItem( shareItem );
				}
				else
				{
					ShareItem shareItem;
					
					// Yes, update it.
					try
					{
						shareItem = profileDao.loadShareItem( gwtShareItem.getId() );
						
						shareItem.setDescription( desc );
						shareItem.setMembers( members );
						profileModule.modifyShareItem( shareItem );
					}
					catch ( NoShareItemByTheIdException e )
					{
					}
				}
			}
		}
		
		results = new GwtShareEntryResults();
		
		return results;
	}
}
