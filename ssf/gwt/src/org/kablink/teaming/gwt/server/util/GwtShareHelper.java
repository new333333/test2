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
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.function.WorkAreaOperation.RightSet;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.User.ExtProvState;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtPublic;
import org.kablink.teaming.gwt.client.GwtRole;
import org.kablink.teaming.gwt.client.GwtSendShareNotificationEmailResults;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.ZoneShareRights;
import org.kablink.teaming.gwt.client.GwtRole.GwtRoleType;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtPublicShareItem;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtShareLists;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.gwt.client.util.UserType;
import org.kablink.teaming.gwt.client.widgets.ShareSendToWidget.SendToValue;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.SendMailErrorWrapper;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.ShareLists;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.EmailHelper;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code that services share requests.
 *
 * @author jwootton@novell.com
 */
public class GwtShareHelper 
{
	protected static Log m_logger = LogFactory.getLog( GwtShareHelper.class );
	private static long MILLISEC_IN_A_DAY = 86400000;
	private static AccessControlManager m_accessControlManager = null;

	
	/**
	 * 
	 */
	public enum ShareOperation
	{
		SHARE_FORWARD,
		SHARE_WITH_EXTERNAL_USERS,
		SHARE_WITH_INTERNAL_USERS,
		SHARE_WITH_PUBLIC,
	}
	
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
	 * See if the user has rights to share with the "all external users" group.
	 */
	public static boolean canShareWithAllExternalUsersGroup( AllModulesInjected ami )
	{
		Long zoneId;
		ZoneConfig zoneConfig;
		
		m_accessControlManager = getAccessControlManager();
		if ( m_accessControlManager == null )
		{
			m_logger.error( "In GwtShareHelper.canShareWithAllExternalUsersGroup(), unable to get the access control manager" );
			return false;
		}

    	zoneId = RequestContextHolder.getRequestContext().getZoneId();
		zoneConfig = ami.getZoneModule().getZoneConfig( zoneId );

		try
		{
			// Can the user share with the "all external users" group?
			m_accessControlManager.checkOperation( zoneConfig, WorkAreaOperation.ENABLE_SHARING_ALL_EXTERNAL );
		}
		catch ( AccessControlException acEx )
		{
			return false;
		}
		
		// If we get here, the user has rights to share with the "all external users" group.
		return true;
	}
	
	/**
	 * See if the user has rights to share with the "all internal users" group.
	 */
	public static boolean canShareWithAllInternalUsersGroup( AllModulesInjected ami )
	{
		Long zoneId;
		ZoneConfig zoneConfig;
		
		m_accessControlManager = getAccessControlManager();
		if ( m_accessControlManager == null )
		{
			m_logger.error( "In GwtShareHelper.canShareWithAllInternalUsersGroup(), unable to get the access control manager" );
			return false;
		}

    	zoneId = RequestContextHolder.getRequestContext().getZoneId();
		zoneConfig = ami.getZoneModule().getZoneConfig( zoneId );

		try
		{
			// Can the user share with the "all internal users" group?
			m_accessControlManager.checkOperation( zoneConfig, WorkAreaOperation.ENABLE_SHARING_ALL_INTERNAL );
		}
		catch ( AccessControlException acEx )
		{
			return false;
		}
		
		// If we get here, the user has rights to share with the "all internal users" group.
		return true;
	}
	
	/**
	 * See if the user can share the given entity
	 */
	public static boolean canShareWith(
		AllModulesInjected ami,
		EntityId entityId,
		ShareOperation shareOperation )
	{
		BinderModule binderModule;
		FolderModule folderModule;
		User currentUser;
    	Long zoneId;
    	ZoneConfig zoneConfig;
		
		binderModule = ami.getBinderModule();
		folderModule = ami.getFolderModule();
		currentUser = GwtServerHelper.getCurrentUser();
		
		m_accessControlManager = getAccessControlManager();
		if ( m_accessControlManager == null )
		{
			m_logger.error( "In GwtShareHelper.canShareWith(), unable to get the access control manager" );
			return false;
		}

    	zoneId = RequestContextHolder.getRequestContext().getZoneId();
		zoneConfig = ami.getZoneModule().getZoneConfig( zoneId );

		try
		{
			BinderOperation binderOperation;
			FolderOperation folderOperation;

			switch ( shareOperation )
			{
			case SHARE_FORWARD:
				// Is share forwarding enabled at the zone level?
				m_accessControlManager.checkOperation( zoneConfig, WorkAreaOperation.ENABLE_SHARING_FORWARD );

				binderOperation = BinderOperation.allowSharingForward;
				folderOperation = FolderOperation.allowSharingForward;
				break;
			
			case SHARE_WITH_EXTERNAL_USERS:
				// Is sharing with external users enabled at the zone level?
				m_accessControlManager.checkOperation( zoneConfig, WorkAreaOperation.ENABLE_SHARING_EXTERNAL );
				
				binderOperation = BinderOperation.allowSharingExternal;
				folderOperation = FolderOperation.allowSharingExternal;
				break;
			
			case SHARE_WITH_INTERNAL_USERS:
				// Is sharing with internal users enabled at the zone level?
				m_accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_INTERNAL );
				
				binderOperation = BinderOperation.allowSharing;
				folderOperation = FolderOperation.allowSharing;
				break;
			
			case SHARE_WITH_PUBLIC:
				// Is sharing with the public enabled at the zone level?
				m_accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_PUBLIC);
				
				binderOperation = BinderOperation.allowSharingPublic;
				folderOperation = FolderOperation.allowSharingPublic;
				break;
			
			default:
				m_logger.info( "InGwtShareHelper.canShareWith(), unknown share operation: " + shareOperation.toString() );
				return false;
			}

			// If we get here the given share operation is enabled at the zone level
			
			if ( entityId.isBinder() )
			{
				Binder binder;
				
				binder = binderModule.getBinder( entityId.getEntityId() );
				binderModule.checkAccess( currentUser, binder, binderOperation );
			}
			else
			{
				FolderEntry folderEntry;
				
				folderEntry = folderModule.getEntry( entityId.getBinderId(), entityId.getEntityId() );
				folderModule.checkAccess( folderEntry, folderOperation );
			}
		}
		catch ( AccessControlException acEx )
		{
			return false;
		}

		// If we get here the user has rights to the given share operation
		return true;
	}
	
	/**
	 * See if the user can share the given entities for the given operation
	 */
	public static boolean canShareWith(
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds,
		ShareOperation shareOperation )
	{
		// Go through the list of entities and see if the user has the right to the given share operation
		if ( listOfEntityIds != null )
		{
			try
			{
				for ( EntityId entityId : listOfEntityIds )
				{
					if ( canShareWith( ami, entityId, shareOperation ) == false )
						return false;
				}
			}
			catch ( AccessControlException acEx )
			{
				return false;
			}
		}
		
		// If we get here the user has rights to the given share operation
		return true;
	}
			
	/**
	 * See if the user can share the given entities with external users.
	 */
	public static boolean canShareWithExternalUsers (
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		return canShareWith( ami, listOfEntityIds, ShareOperation.SHARE_WITH_EXTERNAL_USERS );
	}

	/**
	 * See if the user can share the given entities with internal users.
	 */
	public static boolean canShareWithInternalUsers (
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		return canShareWith( ami, listOfEntityIds, ShareOperation.SHARE_WITH_INTERNAL_USERS );
	}

	/**
	 * See if the user can share the given entities with the public.
	 */
	public static boolean canShareWithPublic (
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		return canShareWith( ami, listOfEntityIds, ShareOperation.SHARE_WITH_PUBLIC );
	}

	/**
	 * See if the user can share the given entities with the public.
	 */
	public static boolean canShareForward (
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		return canShareWith( ami, listOfEntityIds, ShareOperation.SHARE_FORWARD );
	}

	/**
	 * Take the given list of GwtShareItems and find shares with "all users" and "guest" and 
	 * combine them into 1 "public" share.
	 */
	private static ArrayList<GwtShareItem> consolidatePublicShareItems(
		AllModulesInjected ami,
		ArrayList<GwtShareItem> listOfGwtShareItems )
	{
		if ( listOfGwtShareItems != null )
		{
			ArrayList<GwtShareItem> listOfPartOfPublicShareItems;
			
			// Get a list of all shares that are part of a public share.
			listOfPartOfPublicShareItems = new ArrayList<GwtShareItem>();
			for ( GwtShareItem nextShareItem : listOfGwtShareItems )
			{
				if ( nextShareItem.getIsPartOfPublicShare() )
				{
					listOfPartOfPublicShareItems.add( nextShareItem );
				}
			}
			
			// Do we have any shares that are part of a public share?
			if ( listOfPartOfPublicShareItems.size() > 0 )
			{
				Long guestId;
				Long allUsersGroupId;
				int i;
				
				// Yes
				guestId = Utils.getGuestId( ami );
				allUsersGroupId = Utils.getAllUsersGroupId();
				
				// There are two shares that are part of a "public" share; 1 share with "all users"
				// and 1 share with "guest".  Find the two shares and replace them with 1
				// "public" share item.
				for ( i = 0; i < listOfPartOfPublicShareItems.size(); ++i )
				{
					GwtShareItem nextShareItem;
					GwtShareItem allInternalUsersShareItem = null;
					GwtShareItem guestShareItem = null;
					Long recipientId;
					Long sharedById;
					EntityId entityId;

					nextShareItem = listOfPartOfPublicShareItems.get( i );
					
					recipientId = nextShareItem.getRecipientId();
					sharedById = nextShareItem.getSharedById();
					entityId = nextShareItem.getEntityId();

					// Is the recipient the "all internal users" group?
					if ( recipientId.equals( allUsersGroupId ) )
					{
						// Yes
						allInternalUsersShareItem = nextShareItem;
						
						// Find the corresponding share with "guest"
						guestShareItem = findPartOfPublicShareItem(
													listOfPartOfPublicShareItems,
													guestId,
													sharedById,
													entityId );
						
						// Remove the "guest" share item from our list
						if ( guestShareItem != null )
							listOfPartOfPublicShareItems.remove( guestShareItem );
					}
					// Is the recipient the "guest" user?
					else if ( recipientId.equals( guestId ) )
					{
						// Yes
						guestShareItem = nextShareItem;
						
						// Find the corresponding share with "all users"
						allInternalUsersShareItem = findPartOfPublicShareItem(
																listOfPartOfPublicShareItems,
																allUsersGroupId,
																sharedById,
																entityId );
						
						// Remove the "all users" share item from our list
						if ( allInternalUsersShareItem != null )
							listOfPartOfPublicShareItems.remove( allInternalUsersShareItem );
					}
					
					// Did we find the 2 parts of the "public" share?
					if ( allInternalUsersShareItem != null && guestShareItem != null )
					{
						GwtPublicShareItem	publicShareItem;
						GwtPublic gwtPublic;
						
						// Yes
						// This means we are sharing with the public.
						// Replace these 2 shares with one share public item.
						publicShareItem = new GwtPublicShareItem();
						gwtPublic = new GwtPublic();
						gwtPublic.setName( NLT.get( "share.recipientType.title.public" ) );
						publicShareItem.setRecipientName( gwtPublic.getName() );
						publicShareItem.setRecipientType( GwtRecipientType.PUBLIC_TYPE );
						publicShareItem.setRecipientUserType( UserType.UNKNOWN );
						publicShareItem.setRecipientId( gwtPublic.getIdLong() );

						// Remember the 2 share items that make up "share public"
						publicShareItem.setAllInternalShareItem( allInternalUsersShareItem );
						publicShareItem.setGuestShareItem( guestShareItem );
						
						// Get the various share values from the "all internal users" group share.
						// All 2 shares will have the same values so it doesn't matter which one
						// we copy from.
						publicShareItem.setEntityId( allInternalUsersShareItem.getEntityId() );
						publicShareItem.setEntityName( allInternalUsersShareItem.getEntityName() );
						publicShareItem.setIsExpired( allInternalUsersShareItem.isExpired() );
						publicShareItem.setComments( allInternalUsersShareItem.getComments() );
						publicShareItem.setShareExpirationValue( allInternalUsersShareItem.getShareExpirationValue() );
						publicShareItem.setShareRights( allInternalUsersShareItem.getShareRights() );
						publicShareItem.setSharedById( allInternalUsersShareItem.getSharedById() );
						publicShareItem.setSharedByName( allInternalUsersShareItem.getSharedByName() );
						
						// Add the "share public" item.
						listOfGwtShareItems.add( publicShareItem );
						
						listOfGwtShareItems.remove( allInternalUsersShareItem );
						listOfGwtShareItems.remove( guestShareItem );
					}
				}// end for()
			}
		}

		return listOfGwtShareItems;
	}
	
	/**
	 * Create a ShareItem from the given GwtShareItem object
	 */
	private static ShareItem createShareItem(
		AllModulesInjected ami,
		Long sharedById,
		GwtShareItem gwtShareItem )
	{
		ShareItem shareItem = buildShareItem( ami, sharedById, gwtShareItem );
		
		ami.getSharingModule().addShareItem( shareItem );
		
		return shareItem;
	}
	
	/**
	 * Build a ShareItem from the given GwtShareItem object
	 */
	private static ShareItem buildShareItem(
		AllModulesInjected ami,
		Long sharedById,
		GwtShareItem gwtShareItem )
	{
		ShareItem shareItem;
		Date endDate = null;
		RecipientType recipientType;
		Long recipientId;
		RightSet rightSet;
		EntityId entityId;
		EntityIdentifier entityIdentifier;
		int daysToExpire = -1;

		if ( ami == null || gwtShareItem == null )
		{
			m_logger.error( "invalid parameter passed to buildShareItem()" );
			return null;
		}
		
		// Get the entity that is being shared.
		entityId = gwtShareItem.getEntityId();
		entityIdentifier = getEntityIdentifierFromEntityId( entityId );		

		// Get the share expiration value
		{
			ShareExpirationValue expirationValue;
			
			expirationValue = gwtShareItem.getShareExpirationValue();
			switch ( expirationValue.getExpirationType() )
			{
			case AFTER_DAYS:
			{
                // Don't calculate the expiration date (endDate) here.  Let the SharingModule do that.
				daysToExpire = expirationValue.getValue().intValue();
				break;
			}

			case NEVER:
				break;

			case ON_DATE:
				endDate = new Date( expirationValue.getValue() );
				break;
				
			case UNKNOWN:
			default:
				break;
			}
		}
		
		// Get the recipient type
		switch ( gwtShareItem.getRecipientType() )
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
		
		recipientId = getRecipientId( ami, gwtShareItem );

		// Get the appropriate RightSet
		rightSet = getRightSetFromShareRights( gwtShareItem );
		
		// Create the new ShareItem in the db
		{
			shareItem = new ShareItem(
								sharedById,
								entityIdentifier,
								gwtShareItem.getComments(),
								endDate,
								recipientType,
								recipientId,
								rightSet );
			shareItem.setIsPartOfPublicShare( gwtShareItem.getIsPartOfPublicShare() );
			
			shareItem.setDaysToExpire( daysToExpire );
			shareItem.setLatest( true );
		}		
		
		return shareItem;
	}
	
	/**
	 * Take the given list of GwtShareItems and find the share item that has the given recipientId,
	 * sharedById and entityId 
	 */
	private static GwtShareItem findPartOfPublicShareItem(
		ArrayList<GwtShareItem> listOfGwtShareItems,
		Long recipientId,
		Long sharedById,
		EntityId entityId )
	{
		if ( listOfGwtShareItems != null && recipientId != null && sharedById != null && entityId != null )
		{
			for ( GwtShareItem nextShareItem : listOfGwtShareItems )
			{
				if ( nextShareItem.getIsPartOfPublicShare() &&
					 recipientId.equals( nextShareItem.getRecipientId() ) &&
					 sharedById.equals( nextShareItem.getSharedById() ) &&
					 entityId.equalsEntityId( nextShareItem.getEntityId() ) )
				{
					return nextShareItem;
				}
			}
		}
		
		// If we get here we didn't find the item.
		return null;
	}
	
	/**
	 * 
	 */
	public static AccessControlManager getAccessControlManager()
	{
		if ( m_accessControlManager == null )
			m_accessControlManager = (AccessControlManager) SpringContextUtil.getBean( "accessControlManager" );

		return m_accessControlManager;
	}
	
	/**
	 * Get AccessRights that corresponds to the given RightSet
	 */
	public static AccessRights getAccessRightsFromRightSet( RightSet rightSet )
	{
		AccessRights accessRights;
		
		accessRights = AccessRights.UNKNOWN;
		
		if ( rightSet != null )
		{
			RightSet viewerRightSet;
			RightSet editorRightSet;
			RightSet contributorRightSet;
			boolean shareInternal;
			boolean shareExternal;
			boolean sharePublic;
			boolean shareForward;

			// areRightSetsEqual() compares "share internal", "share external", "share public" and "share forward".
			// That is why we are setting them to false.
			shareInternal = rightSet.isAllowSharing();
			shareExternal = rightSet.isAllowSharingExternal();
			sharePublic = rightSet.isAllowSharingPublic();
			shareForward = rightSet.isAllowSharingForward();
			rightSet.setAllowSharing( false );
			rightSet.setAllowSharingExternal( false );
			rightSet.setAllowSharingPublic( false );
			rightSet.setAllowSharingForward( false );

			viewerRightSet = getViewerRightSet();
			editorRightSet = getEditorRightSet();
			contributorRightSet = getContributorRightSet();

			// Is the given RightSet equal to the "View" RightSet
			if ( areRightSetsEqual( rightSet, viewerRightSet ) )
			{
				// Yes
				accessRights = AccessRights.VIEWER;
			}
			// Is the given RightSet equal to the "Editor" RightSet
			else if ( areRightSetsEqual( rightSet, editorRightSet ) )
			{
				// Yes
				accessRights = AccessRights.EDITOR;
			}
			// Is the given RightSet equal to the "Contributor" RightSet
			else if ( areRightSetsEqual( rightSet, contributorRightSet ) )
			{
				// Yes
				accessRights = AccessRights.CONTRIBUTOR;
			}
			
			// Restore the values we set to false.
			rightSet.setAllowSharing( shareInternal );
			rightSet.setAllowSharingExternal( shareExternal );
			rightSet.setAllowSharingPublic( sharePublic );
			rightSet.setAllowSharingForward( shareForward );
		}
		
		return accessRights;
	}

	/**
	 * Return the RightSet that corresponds to the "Contributor" rights
	 */
	private static RightSet getContributorRightSet()
	{
		RightSet rightSet;
		List<WorkAreaOperation> operations;
		
		operations = ShareItem.Role.CONTRIBUTOR.getRightSet().getRights();
		rightSet = new RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );

		return rightSet;
	}
	
	/**
	 * Return the RightSet that corresponds to the "Editor" rights
	 */
	private static RightSet getEditorRightSet()
	{
		RightSet rightSet;
		List<WorkAreaOperation> operations;
		
		operations = ShareItem.Role.EDITOR.getRightSet().getRights();
		rightSet = new RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );
		
		return rightSet;
	}
	
	/**
	 * Return an EntityIdentifier for the given EntityId
	 * 
	 * @param entityId
	 */
	public static EntityIdentifier getEntityIdentifierFromEntityId( EntityId entityId )
	{
		EntityIdentifier entityIdentifier;
		EntityType entityType;
		Long entityIdL;
	
		if ( entityId.isEntry() )
		{
			entityIdL = entityId.getEntityId();
			entityType = EntityType.folderEntry;
		}
		else
		{
			String entityTypeS;

			entityTypeS = entityId.getEntityType();
			
			//!!! Finish
			if ( entityTypeS.equalsIgnoreCase( EntityType.folder.toString() ) )
				entityType = EntityType.folder;
			else if ( entityTypeS.equalsIgnoreCase( EntityType.workspace.toString() ) )
				entityType = EntityType.workspace;
			else
				entityType = EntityType.none;
				
			entityIdL = entityId.getEntityId();
		}
		
		entityIdentifier = new EntityIdentifier( entityIdL, entityType );
		
		return entityIdentifier;
	}
	
	/**
	 * Return the name of the given entity
	 */
	private static String getEntityName( AllModulesInjected ami, EntityId entityId )
	{
		DefinableEntity entity;
		
		if ( entityId.isBinder() )
			entity = ami.getBinderModule().getBinder( entityId.getEntityId() );
		else
			entity = ami.getFolderModule().getEntry( entityId.getBinderId(), entityId.getEntityId() );
		
		return entity.getTitle();
	}
	
	/**
	 * Return the state of the external user account.  Possible values are, initial, bound and verified.
	 * This method will return null if the given user is not an external user.
	 */
	private static ExtProvState getExternalUserAccountState(
			AllModulesInjected ami,
			Long userId )
		{
			try
			{
				ArrayList<Long> ids;
				SortedSet<Principal> principals;
				
				ids = new ArrayList<Long>();
				ids.add( userId );
				principals = ami.getProfileModule().getPrincipals( ids );
				if ( principals != null && principals.size() == 1 )
				{
					Principal principal;
					
					// 
					principal = principals.first();
					if ( principal instanceof User )
					{
						User user;

						user = (User) principal;
						if ( user.getIdentityInfo().isInternal() == false )
							return user.getExtProvState();
					}
				}
			}
			catch ( AccessControlException acEx )
			{
				// Nothing to do
			}
			
			return null;
		}

	/**
	 * Return the name of the given group
	 */
	private static String getGroupName( AllModulesInjected ami, ShareItem shareItem )
	{
		String name = null;
		
		if ( shareItem != null )
		{
			// Set the recipient's name
			try 
			{
				List<Long> groupId = new ArrayList<Long>();
				SortedSet<Principal> groupPrincipals;

				groupId.add( shareItem.getRecipientId() );
				groupPrincipals = ami.getProfileModule().getPrincipals( groupId );
				
				if ( groupPrincipals.size() == 1  )
				{
					Principal group;
					
					group = groupPrincipals.first();
					
					name = group.getName();
				}
			}
			catch ( Exception e )
			{
				m_logger.error( "Could not find the group: " + shareItem.getRecipientId().toString() );
			}
		}
		
		return name;
	}

	/**
	 * Return the "highest" access rights based on acls the logged-in user has to the given entity
	 */
	private static AccessRights getHighestEntityAccessRightsFromACLs(
		AllModulesInjected ami,
		EntityId entityId )
	{
		AccessRights accessRights;
		
		accessRights = AccessRights.VIEWER;

		if ( entityId.isBinder() )
		{
			boolean access;
			WorkArea workArea;
			AccessControlManager accessControlManager;
			User currentUser;

			accessControlManager = AccessUtils.getAccessControlManager();
			currentUser = GwtServerHelper.getCurrentUser();

			workArea = ami.getBinderModule().getBinder( entityId.getEntityId() );

			// See if the user has editor rights
			access = true;
			for ( WorkAreaOperation nextOperation : ShareItem.Role.EDITOR.getWorkAreaOperations() )
			{
				if ( accessControlManager.testOperation( currentUser, workArea, nextOperation ) == false )
				{
					access = false;
					break;
				}
			}
			
			if ( access )
			{
				accessRights = AccessRights.EDITOR;
			}

			// Does the user have "contributor" rights?
			access = true;
			for ( WorkAreaOperation nextOperation : ShareItem.Role.CONTRIBUTOR.getWorkAreaOperations() )
			{
				if ( accessControlManager.testOperation( currentUser, workArea, nextOperation ) == false )
				{
					access = false;
					break;
				}
			}
			
			if ( access )
			{
				accessRights = AccessRights.CONTRIBUTOR;
			}
		}
		else
		{
			FolderModule folderModule;
			FolderEntry folderEntry;
			
			folderModule = ami.getFolderModule();
			folderEntry = ami.getFolderModule().getEntry( null, entityId.getEntityId() );
			
			// Does the user have "editor" rights?
			if ( folderModule.testAccess( folderEntry, FolderOperation.readEntry ) &&
				 folderModule.testAccess( folderEntry, FolderOperation.modifyEntry ) && 
				 folderModule.testAccess( folderEntry, FolderOperation.addReply ) )
			{
				accessRights = AccessRights.EDITOR;
			}
		}
/**
		if ( accessControlManager.testRightsGrantedBySharing(
														GwtServerHelper.getCurrentUser(),
														workArea,
														ShareItem.Role.EDITOR.getWorkAreaOperations() ) )
		{
			accessRights = AccessRights.EDITOR;
		}
		
		if ( entityId.isBinder() )
		{
			if ( accessControlManager.testRightsGrantedBySharing(
														GwtServerHelper.getCurrentUser(),
														workArea,
														ShareItem.Role.CONTRIBUTOR.getWorkAreaOperations() ) )
			{
				accessRights = AccessRights.CONTRIBUTOR;
			}
		}
*/
		return accessRights;
	}
	
	/**
	 * Return the "highest" share rights the logged-in user has to the given entity
	 */
	private static ShareRights getHighestEntityShareRights( AllModulesInjected ami, EntityId entityId )
	{
		ShareItemSelectSpec spec;
		EntityIdentifier entityIdentifier;
		List<ShareItem> listOfShareItems = null;
		AccessRights accessRights;
		ShareRights shareRights;
		boolean result;
		
		shareRights = new ShareRights();
		
		// Get the current user's acl rights to the given entity.
		accessRights = getHighestEntityAccessRightsFromACLs( ami, entityId );
		
		// Get the entity type
		entityIdentifier = getEntityIdentifierFromEntityId( entityId );

		spec = new ShareItemSelectSpec();
		spec.setRecipients( GwtServerHelper.getCurrentUser().getId(), null, null );
		spec.setSharedEntityIdentifier( entityIdentifier );
		spec.setLatest( true );

		try
		{
			listOfShareItems = ami.getSharingModule().getShareItems( spec );
		}
		catch ( Exception ex )
		{
			m_logger.error( "In getHighestEntityShareRights(), sharingModule.getShareItems() failed: " + ex.toString() );
		}

		// Has the given entity been shared with the current user?
		if ( listOfShareItems != null && listOfShareItems.size() > 0 )
		{
			// Yes
			// Get the "highest" rights that have been given to the current user via a share.
			for ( ShareItem nextShareItem : listOfShareItems )
			{
				AccessRights nextAccessRights;
				
				nextAccessRights = getAccessRightsFromRightSet( nextShareItem.getRightSet() );
				
				switch( accessRights )
				{
				case EDITOR:
					if ( nextAccessRights == AccessRights.CONTRIBUTOR )
						accessRights = nextAccessRights;
					break;
					
				case VIEWER:
				case UNKNOWN:
					accessRights = nextAccessRights;
					break;
				}
			}
		}
		
		shareRights.setAccessRights( accessRights );
		
		// Determine if the user has "can share with external users" rights.
		result = canShareWith( ami, entityId, ShareOperation.SHARE_WITH_EXTERNAL_USERS );
		shareRights.setCanShareWithExternalUsers( result );

		// Determine if the user has "can share with internal users" rights.
		result = canShareWith( ami, entityId, ShareOperation.SHARE_WITH_INTERNAL_USERS );
		shareRights.setCanShareWithInternalUsers( result );

		// Determine if the user has "can share with the public" rights.
		result = canShareWith( ami, entityId, ShareOperation.SHARE_WITH_PUBLIC );
		shareRights.setCanShareWithPublic( result );
		
		// Determine if the user has "can share forward" rights.
		{
			DefinableEntity entity;
			
			if ( entityId.isBinder() )
				entity = ami.getBinderModule().getBinder( entityId.getEntityId() );
			else
				entity = ami.getFolderModule().getEntry( entityId.getBinderId(), entityId.getEntityId() );
			
			result = ami.getSharingModule().testShareEntityForward( entity );
			shareRights.setCanShareForward( result );
		}
		
		return shareRights;
	}
	
	/**
	 * Get the list of GwtShareItems for the given EntityId
	 */
	private static ArrayList<GwtShareItem> getListOfGwtShareItems(
		AllModulesInjected ami,
		String sharedById,
		EntityId entityId )
	{
		List<ShareItem> listOfShareItems = null;
		ArrayList<GwtShareItem> listOfGwtShareItems = null;
		ShareItemSelectSpec spec;

		listOfGwtShareItems = new ArrayList<GwtShareItem>();
		
		// Set up the search criteria
		{
			spec = new ShareItemSelectSpec();
			
			if ( sharedById != null )
				spec.setSharerId( Long.valueOf( sharedById ) );
			
			if ( entityId != null )
			{
				EntityIdentifier entityIdentifier;
	
				entityIdentifier = getEntityIdentifierFromEntityId( entityId );
				spec.setSharedEntityIdentifier( entityIdentifier );
			}
			
			spec.setLatest( true );
		}
		
		try
		{
			// Get the list of ShareItem objects for the given entity
			listOfShareItems = ami.getSharingModule().getShareItems( spec );
		}
		catch ( Exception ex )
		{
			m_logger.error( "sharingModule.getListOfGwtShareItems() failed: " + ex.toString() );
		}

		// Do we have a list of ShareItem objects for the given entity?
		if ( listOfShareItems != null )
		{
			Date today;

			// Yes
			today = new Date();

			for ( ShareItem nextShareItem : listOfShareItems )
			{
				GwtShareItem gwtShareItem;
				Long recipientId;
				EntityId nextEntityId;
				
				recipientId = nextShareItem.getRecipientId();
				
				gwtShareItem = new GwtShareItem();
				
				// Were we passed an EntityId
				if ( entityId == null )
				{
					EntityIdentifier entityIdentifier;
					
					// No.
					nextEntityId = null;
					
					entityIdentifier = nextShareItem.getSharedEntityIdentifier();
					if ( entityIdentifier != null )
					{
						nextEntityId = new EntityId();

						switch ( entityIdentifier.getEntityType() )
						{
						case folderEntry:
							FolderEntry entry;

							entry = ami.getFolderModule().getEntry( null, entityIdentifier.getEntityId() );
							
							nextEntityId.setEntityId( entityIdentifier.getEntityId() );
							nextEntityId.setBinderId( entry.getParentBinder().getId() );
							nextEntityId.setEntityType( EntityId.FOLDER_ENTRY );
							break;
							
						case folder:
							nextEntityId.setEntityId( entityIdentifier.getEntityId() );
							nextEntityId.setEntityType( EntityId.FOLDER );
							break;
							
						case workspace:
							nextEntityId.setEntityId( entityIdentifier.getEntityId() );
							nextEntityId.setEntityType( EntityId.WORKSPACE );
							break;
							
						default:
							m_logger.error( "sharingModule.getListOfGwtShareItems(), unknown entity type: " + entityIdentifier.getEntityType() );
							nextEntityId = null;
							break;
						}
					}
				}
				else
				{
					// Yes, use it.
					nextEntityId = entityId;
				}
				
				if ( nextEntityId == null )
					continue;
				
				gwtShareItem.setEntityId( nextEntityId );
				gwtShareItem.setEntityName( getEntityName( ami, nextEntityId ) );
				gwtShareItem.setId( nextShareItem.getId() );
				gwtShareItem.setIsExpired( nextShareItem.isExpired() );
				gwtShareItem.setRecipientId( recipientId );
				gwtShareItem.setComments( nextShareItem.getComment() );
				gwtShareItem.setIsPartOfPublicShare( nextShareItem.getIsPartOfPublicShare() );
				
				// Set the recipient type and name.
				{
					String name;
					
					switch ( nextShareItem.getRecipientType() )
					{
					case group:
						name = getGroupName( ami, nextShareItem );
						gwtShareItem.setRecipientName( name );
						gwtShareItem.setRecipientType( GwtRecipientType.GROUP );
						gwtShareItem.setRecipientUserType( UserType.UNKNOWN );
						break;
						
					case user:
					{
						User user;
						
						user = getUser( ami, nextShareItem );
						
						if ( user != null )
						{
							name = user.getTitle();
							gwtShareItem.setRecipientName( name );
							
							if ( !user.getIdentityInfo().isInternal() )
								gwtShareItem.setRecipientType( GwtRecipientType.EXTERNAL_USER );
							else
								gwtShareItem.setRecipientType( GwtRecipientType.USER );
							
							gwtShareItem.setRecipientUserType( GwtViewHelper.getUserType( user ) );
						}
						else
							m_logger.error( "could not find the user: " + nextShareItem.getRecipientId().toString() );
						
						break;
					}
					
					case team:
						name = getTeamName( ami, nextShareItem );
						gwtShareItem.setRecipientName( name );
						gwtShareItem.setRecipientType( GwtRecipientType.TEAM );
						break;
						
					default:
						gwtShareItem.setRecipientType( GwtRecipientType.UNKNOWN );
						m_logger.error( "unknown recipient type for user: " + nextShareItem.getRecipientId().toString() );
						break;
					}
				}
				
				// Set the "shared by" info
				{
					Long sharedByIdL;
					
					sharedByIdL = nextShareItem.getSharerId();
					
					gwtShareItem.setSharedById( sharedByIdL );

					try 
					{
						User user;
						
						user = ami.getProfileModule().getUserDeadOrAlive( sharedByIdL );
						gwtShareItem.setSharedByName( user.getTitle() );
					}
					catch ( Exception e )
					{
						m_logger.error( "Could not find the sharer: " + nextShareItem.getSharerId().toString() );
					}
				}
				
				// Set the expiration value
				{
					ShareExpirationValue expirationValue;
					Date endDate;
					
					expirationValue = new ShareExpirationValue();
					expirationValue.setType( ShareExpirationType.NEVER );
					
					// Is there an expiration specified?
					endDate = nextShareItem.getEndDate();
					if ( endDate != null )
					{
						int expiresAfterDays;
						
						// Do we have an "expires after" value?
						expiresAfterDays = nextShareItem.getDaysToExpire();
						if ( expiresAfterDays > 0 )
						{
							long milliSecLeft;
							
							// Yes
							// Calculate how many days are left before the share expires.
							milliSecLeft = endDate.getTime() - today.getTime();
							expiresAfterDays = (int)(milliSecLeft / MILLISEC_IN_A_DAY);
							if ( expiresAfterDays >= 0 )
							{
								if ( (milliSecLeft % MILLISEC_IN_A_DAY) > 0 )
									++expiresAfterDays;
							}
							expirationValue.setType( ShareExpirationType.AFTER_DAYS );
							expirationValue.setValue( Long.valueOf( expiresAfterDays ) );
						}
						else
						{
							// We are dealing with "expires on"
							expirationValue.setType( ShareExpirationType.ON_DATE );
							expirationValue.setValue( endDate.getTime() );
						}
					}
					
					gwtShareItem.setShareExpirationValue( expirationValue );
				}
				
				// Set the share rights
				{
					ShareRights shareRights;
					
					shareRights = getShareRightsFromRightSet( nextShareItem.getRightSet() );
					gwtShareItem.setShareRights( shareRights );
				}

				listOfGwtShareItems.add( gwtShareItem );
			}
		}

		return listOfGwtShareItems;
	}

	/**
	 * Return the id of the given user.  If the user is an external user we will see if their
	 * user account has been created.  If it hasn't we will create it.
	 */
	private static Long getRecipientId( AllModulesInjected ami, GwtShareItem gwtShareItem )
	{
		Long id;
		
		id = gwtShareItem.getRecipientId();

		// Are we dealing with an external user?
		if ( gwtShareItem.getRecipientType() == GwtRecipientType.EXTERNAL_USER )
		{
			// Yes
			// Does the external user have a Vibe account?
			if ( id == null )
			{
                User user = ami.getProfileModule().findOrAddExternalUser(gwtShareItem.getRecipientName());
                id = user.getId();
			}
		}
		
		return id;
	}
	
	/**
	 * Return the appropriate RightSet object for the given ShareRights object
	 */
	private static RightSet getRightSetFromShareRights( GwtShareItem gwtShareItem )
	{
		ShareRights shareRights;
		RightSet rightSet;
		
		shareRights = gwtShareItem.getShareRights();
		
		switch ( shareRights.getAccessRights() )
		{
		case CONTRIBUTOR:
			rightSet = getContributorRightSet(); 
			break;
		
		case EDITOR:
			rightSet = getEditorRightSet();
			break;
		
		case VIEWER:
			rightSet = getViewerRightSet();
			break;
			
		case UNKNOWN:
		default:
			rightSet = new RightSet();
			m_logger.error( "In GwtShareHelper.getRightSet(), unknown share rights" );
			break;
		}
		
		rightSet.setAllowSharingForward( shareRights.getCanShareForward() );
		rightSet.setAllowSharing( shareRights.getCanShareWithInternalUsers() );
		rightSet.setAllowSharingExternal( shareRights.getCanShareWithExternalUsers() );
		rightSet.setAllowSharingPublic( shareRights.getCanShareWithPublic() );
	
		return rightSet;
	}
	
	/**
	 * Get a ShareRights object that corresponds to the given RightSet
	 */
	public static ShareRights getShareRightsFromRightSet( RightSet rightSet )
	{
		ShareRights shareRights;

		shareRights = new ShareRights();
		shareRights.setAccessRights( ShareRights.AccessRights.UNKNOWN );

		if ( rightSet != null )
		{
			AccessRights accessRights;

			accessRights = getAccessRightsFromRightSet( rightSet );
			shareRights.setAccessRights( accessRights );

			// Does the RightSet allow "share with external users"?
			shareRights.setCanShareWithExternalUsers( rightSet.isAllowSharingExternal() );
			
			// Does the RightSet allow "share with internal users"?
			shareRights.setCanShareWithInternalUsers( rightSet.isAllowSharing() );
			
			// Does the RightSet allow "share with public"?
			shareRights.setCanShareWithPublic( rightSet.isAllowSharingPublic() );
			
			// Does the RightSet allow "share forward"?
			shareRights.setCanShareForward( rightSet.isAllowSharingForward() );
		}
		
		return shareRights;
	}
	
	/**
	 * Return the sharing roles that are defined at the zone level.
	 */
	@SuppressWarnings("rawtypes")
	public static ZoneShareRights getZoneShareRights( AllModulesInjected ami )
	{
		ZoneShareRights shareSettings;
		ArrayList<GwtRole> listOfRoles;
		GwtRole role;
		AdminModule adminModule;
		Long zoneId;
		WorkArea workArea;
		
		listOfRoles = new ArrayList<GwtRole>();
		role = new GwtRole();
		role.setType( GwtRoleType.EnableShareExternal );
		listOfRoles.add( role );
		role = new GwtRole();
		role.setType( GwtRoleType.EnableShareForward );
		listOfRoles.add( role );
		role = new GwtRole();
		role.setType( GwtRoleType.EnableShareInternal );
		listOfRoles.add( role );
		role = new GwtRole();
		role.setType( GwtRoleType.EnableSharePublic );
		listOfRoles.add( role );
		if (Utils.checkIfKablink() || Utils.checkIfVibe()) {
			role = new GwtRole();
			role.setType( GwtRoleType.EnableShareWithAllExternal );
			listOfRoles.add( role );
		}
		role = new GwtRole();
		role.setType( GwtRoleType.EnableShareWithAllInternal );
		listOfRoles.add( role );
		
		shareSettings = new ZoneShareRights();
		shareSettings.setRoles( listOfRoles );
		
		adminModule = ami.getAdminModule();
		
    	zoneId = RequestContextHolder.getRequestContext().getZoneId();
		workArea = ami.getZoneModule().getZoneConfig( zoneId );
		
		for ( GwtRole nextRole : listOfRoles )
		{
			Long fnId = null;
			WorkAreaFunctionMembership membership;
			Set<Long> memberIds;
			List principals = null;
			
			// Get the Function id for the given role
			fnId = GwtServerHelper.getFunctionIdFromRole( ami, nextRole );

			// Did we find the function for the given role?
			if ( fnId == null )
			{
				// No
				continue;
			}

			// Get the role's membership
			membership = adminModule.getWorkAreaFunctionMembership( workArea, fnId );
			if ( membership == null )
				continue;
			
			// Get the member ids
			memberIds = membership.getMemberIds();
			if ( memberIds == null )
				continue;
			
			try 
			{
				principals = ResolveIds.getPrincipals( memberIds );
			}
			catch ( Exception ex )
			{
				// Nothing to do
			}
			
			if ( MiscUtil.hasItems( principals ) == false )
				continue;

			for ( Object nextObj :  principals )
			{
				if ( nextObj instanceof Principal )
				{
					Principal nextPrincipal;
					
					nextPrincipal = (Principal) nextObj;

					if ( nextPrincipal instanceof Group )
					{
						Group nextGroup;
						GwtGroup gwtGroup;
						
						nextGroup = (Group) nextPrincipal;
						
						gwtGroup = new GwtGroup();
						gwtGroup.setInternal( nextGroup.getIdentityInfo().isInternal() );
						gwtGroup.setId( nextGroup.getId().toString() );
						gwtGroup.setName( nextGroup.getName() );
						gwtGroup.setTitle( nextGroup.getTitle() );
						
						nextRole.addMember( gwtGroup );
					}
					else if ( nextPrincipal instanceof User )
					{
						User user;
						GwtUser gwtUser;
						
						user = (User) nextPrincipal;
	
						gwtUser = new GwtUser();
						gwtUser.setInternal( user.getIdentityInfo().isInternal() );
						gwtUser.setUserId( user.getId() );
						gwtUser.setName( user.getName() );
						gwtUser.setTitle( Utils.getUserTitle( user ) );
						gwtUser.setWorkspaceTitle( user.getWSTitle() );
	
						nextRole.addMember( gwtUser );
					}
				}
			}
		}
		
		return shareSettings;
	}

	/**
	 * Return sharing information for the given entities
	 */
	public static GwtSharingInfo getSharingInfo(
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds,
		String sharedById )
	{
		GwtSharingInfo sharingInfo;

		sharingInfo = new GwtSharingInfo();
		
		// See if the user has rights to share with the "all external users" group.
		sharingInfo.setCanShareWithAllExternalUsersGroup( canShareWithAllExternalUsersGroup( ami ) );
		
		// See if the user has rights to share with the "all internal users" group.
		sharingInfo.setCanShareWithAllInternalUsersGroup( canShareWithAllInternalUsersGroup( ami ) );

		// Were we passed a list of entity ids?
		if ( listOfEntityIds != null && listOfEntityIds.size() > 0 )
		{
			// Yes
			// For each given entity, get the sharing information.
			for (EntityId nextEntityId : listOfEntityIds)
			{
				ArrayList<GwtShareItem> listOfGwtShareItems;
				String entityName;
				ShareRights shareRights;
	
				// Get the name of the entity
				entityName = getEntityName( ami, nextEntityId );
				sharingInfo.setEntityName( nextEntityId, entityName );
				
				// Get the highest share rights the logged-in user has to this entity
				shareRights = getHighestEntityShareRights( ami, nextEntityId );
				sharingInfo.setEntityShareRights( nextEntityId, shareRights );
				
				// Get the list of GwtShareItem objects for the given user/entity
				listOfGwtShareItems = getListOfGwtShareItems( ami, sharedById, nextEntityId );
				
				// Consolidate shares with "guest" and "all users" into 1 "public" share.
				listOfGwtShareItems = consolidatePublicShareItems( ami, listOfGwtShareItems );
	
				if ( listOfGwtShareItems != null )
				{
					for ( GwtShareItem nextGwtShareItem : listOfGwtShareItems )
					{
						sharingInfo.addShareItem( nextGwtShareItem );
					}
				}
			}
		}
		else
		{
			ArrayList<GwtShareItem> listOfGwtShareItems;

			// No, get a list of all share items.
			listOfGwtShareItems = getListOfGwtShareItems( ami, sharedById, null );

			if ( listOfGwtShareItems != null )
			{
				HashMap<String,EntityId> entityIdMap;
				
				entityIdMap = new HashMap<String,EntityId>();
				
				// Consolidate shares with "guest" and "all users" into 1 "public" share.
				listOfGwtShareItems = consolidatePublicShareItems( ami, listOfGwtShareItems );

				// Add each share to the results
				for ( GwtShareItem nextGwtShareItem : listOfGwtShareItems )
				{
					EntityId entityId;
					
					// Add the share to the results
					sharingInfo.addShareItem( nextGwtShareItem );
					
					// Get the id of the entity being shared.
					entityId = nextGwtShareItem.getEntityId();
					if ( entityId != null )
					{
						String key;
						
						key = entityId.getEntityIdString();
						if ( entityIdMap.containsKey( key ) == false )
						{
							ShareRights shareRights;
							
							entityIdMap.put( key, entityId );

							// Get the highest share rights the logged-in user has to this entity
							shareRights = getHighestEntityShareRights( ami, entityId );
							sharingInfo.setEntityShareRights( entityId, shareRights );
						}
					}
				}

				listOfEntityIds = new ArrayList<EntityId>( entityIdMap.values() );
			}
		}
		
		// See if the user has rights to share the given entities with an external user.
		sharingInfo.setCanShareWithExternalUsers( canShareWithExternalUsers( ami, listOfEntityIds ) );
		
		// See if the user has rights to share the given entities with an internal user.
		sharingInfo.setCanShareWithInternalUsers( canShareWithInternalUsers( ami, listOfEntityIds ) );
		
		// See if the user has rights to share the given entities with the public.
		{
			AuthenticationConfig authConfig;
			boolean canShareWithPublic;

			canShareWithPublic = false;
			
			// Is guest access turned on?
			authConfig = ami.getAuthenticationModule().getAuthenticationConfig();
			if ( authConfig.isAllowAnonymousAccess() )
			{
				// Yes
				// See if the user has the "can share with public" right
				canShareWithPublic = canShareWithPublic( ami, listOfEntityIds );
			}
			sharingInfo.setCanShareWithPublic( canShareWithPublic );
		}
		
		return sharingInfo;
	}

	/**
	 * Return the name of the given team
	 */
	private static String getTeamName( AllModulesInjected ami, ShareItem shareItem )
	{
		String name = null;
		
		if ( shareItem != null )
		{
			try 
			{
				DefinableEntity entity;
				
				entity = ami.getBinderModule().getBinder( shareItem.getRecipientId() );
				
				name = entity.getTitle();
			}
			catch ( Exception e )
			{
				m_logger.error( "Could not find the team: " + shareItem.getRecipientId().toString() );
			}
		}
		
		return name;
	}

	/**
	 * Return the given user
	 */
	private static User getUser( AllModulesInjected ami, ShareItem shareItem )
	{
		if ( shareItem != null )
		{
			// Set the recipient's name
			try 
			{
				User user;
				
				user = ami.getProfileModule().getUserDeadOrAlive( shareItem.getRecipientId() );
				return user;
			}
			catch ( Exception e )
			{
				m_logger.error( "Could not find the user: " + shareItem.getRecipientId().toString() );
			}
		}
		
		// If we get here we did not find the user
		return null;
	}

	/**
	 * Return the RightSet that corresponds to the "Viewer" rights
	 */
	private static RightSet getViewerRightSet()
	{
		RightSet rightSet;
		List<WorkAreaOperation> operations;
		
		operations = ShareItem.Role.VIEWER.getRightSet().getRights();
		rightSet = new RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );

		return rightSet;
	}

	/**
	 * Send a notification email for each of the given share items
	 */
	public static GwtSendShareNotificationEmailResults sendShareNotificationEmail(
		AllModulesInjected ami,
		ArrayList<Long> listOfShareItemIds )
	{
		List<SendMailErrorWrapper> emailErrors;
		GwtSendShareNotificationEmailResults results;
		User currentUser;
		
		results = new GwtSendShareNotificationEmailResults();
		emailErrors = null;
		
		if ( ami == null || listOfShareItemIds == null || listOfShareItemIds.size() == 0 )
			return results;
		
		currentUser = GwtServerHelper.getCurrentUser();
		
		for ( Long nextShareItemId : listOfShareItemIds )
		{
			try
			{
				ShareItem shareItem;
				List<SendMailErrorWrapper> entityEmailErrors = null;
				boolean isExternal;

				// Get the ShareItem.
				shareItem = ami.getSharingModule().getShareItem( nextShareItemId );
				
				// See if the recipient is an external user.
				isExternal = false;
				{
			        try
			        {
			            ArrayList<Long> ids;
			            SortedSet<Principal> principals;

			            ids = new ArrayList<Long>();
			            ids.add( shareItem.getRecipientId() );
			            principals = ami.getProfileModule().getPrincipals( ids );
			            if ( principals != null && principals.size() == 1 )
			            {
			                Principal principal;

			                principal = principals.first();
			                if ( principal instanceof User )
			                {
			                    User user;

			                    user = (User) principal;
			                    if ( user.getIdentityInfo().isInternal() == false )
			                    	isExternal = true;
			                }
			            }
			        }
			        catch ( AccessControlException acEx )
			        {
			            // Nothing to do
			        }
				}

				// Send an email to the recipient of this share.
				entityEmailErrors = EmailHelper.sendEmailToRecipient(
														ami,
														shareItem,
														isExternal,
														currentUser );
				
				if ( entityEmailErrors != null )
				{
					if ( emailErrors == null )
					{
						emailErrors = entityEmailErrors;
					}
					else
					{
						emailErrors.addAll( entityEmailErrors );
					}
				}
			}
			catch ( NoShareItemByTheIdException ex )
			{
				m_logger.info( "In GwtShareHelper.sendShareNotificationEmail(), could not find share item: " + nextShareItemId );
			}
		}
		
		// Add any errors that happened to the results.
		if ( null != emailErrors )
		{
			results.addErrors( SendMailErrorWrapper.getErrorMessages( emailErrors ) );
		}

		return results;
	}
		
	/**
	 * Save the given share data. 
	 */
	public static GwtShareEntryResults shareEntry(
		AllModulesInjected ami,
		GwtSharingInfo sharingData )
	{
		SharingModule sharingModule;
		GwtShareEntryResults results;
		ArrayList<GwtShareItem> listOfGwtShareItems;
		ArrayList<GwtShareItem> listOfGwtShareItemsToDelete;

		sharingModule = ami.getSharingModule();

		results = new GwtShareEntryResults();
		
		if ( sharingData == null )
		{
			m_logger.error( "In GwtShareHelper.shareEntry(), sharingData is null." );
			return results;
		}

		// Get the list of GwtShareItem objects
		listOfGwtShareItems = sharingData.getListOfShareItems();
		if ( listOfGwtShareItems == null )
		{
			return results;
		}
		
		// Delete ShareItems that the user removed.
		listOfGwtShareItemsToDelete = sharingData.getListOfToBeDeletedShareItems();
		if ( listOfGwtShareItemsToDelete != null )
		{
			for ( GwtShareItem nextShareItem : listOfGwtShareItemsToDelete )
			{
				if ( nextShareItem instanceof GwtPublicShareItem )
				{
					GwtShareItem gwtShareItem;
					GwtPublicShareItem gwtPublicShareItem;
					
					gwtPublicShareItem = (GwtPublicShareItem) nextShareItem;
					
					// Delete the share with the "all internal users" group.
					gwtShareItem = gwtPublicShareItem.getAllInternalShareItem();
					if ( gwtShareItem != null )
					{
						Long shareItemId;
						
						shareItemId = gwtShareItem.getId();
						if ( shareItemId != null )
						{
							sharingModule.deleteShareItem( shareItemId );
						}
					}

					// Delete the share with the guest.
					gwtShareItem = gwtPublicShareItem.getGuestShareItem();
					if ( gwtShareItem != null )
					{
						Long shareItemId;
						
						shareItemId = gwtShareItem.getId();
						if ( shareItemId != null )
						{
							sharingModule.deleteShareItem( shareItemId );
						}
					}
				}
				else
				{
					Long shareItemId;
					
					shareItemId = nextShareItem.getId();
					if ( shareItemId != null )
					{
						sharingModule.deleteShareItem( shareItemId );
					}
				}
			}
		}
		
		// For each GwtShareItem, make the necessary updates to the database.
		for (GwtShareItem nextGwtShareItem : listOfGwtShareItems)
		{
			// Are we dealing with a public share?
			if ( nextGwtShareItem instanceof GwtPublicShareItem )
			{
				GwtPublicShareItem publicShareItem;
				
				// Yes
				publicShareItem = (GwtPublicShareItem) nextGwtShareItem;
				
				// Does this share public already exist.
				if ( publicShareItem.isExisting() )
				{
					// Yes
					// Has the share been modified?
					if ( publicShareItem.isDirty() )
					{
						// Yes
						// Update the share item used for the "all internal users" group
						{
							GwtShareItem gwtShareItem;
							
							gwtShareItem = publicShareItem.getAllInternalShareItem();
							if ( gwtShareItem != null )
							{
								ShareItem shareItem;

								gwtShareItem.setShareRights( publicShareItem.getShareRights() );
								gwtShareItem.setShareExpirationValue( publicShareItem.getShareExpirationValue() );
								gwtShareItem.setComments( publicShareItem.getComments() );
								
								shareItem = buildShareItem( ami, gwtShareItem.getSharedById(), gwtShareItem );

								// Modify the share by marking existing snapshot as not being the latest
								// and persisting the new snapshot. 
								sharingModule.modifyShareItem( shareItem, gwtShareItem.getId() );
							}
						}

						// Update the share item used for the guest
						{
							GwtShareItem gwtShareItem;
							
							gwtShareItem = publicShareItem.getGuestShareItem();
							if ( gwtShareItem != null )
							{
								ShareItem shareItem;

								gwtShareItem.setShareRights( publicShareItem.getShareRights() );
								gwtShareItem.setShareExpirationValue( publicShareItem.getShareExpirationValue() );
								gwtShareItem.setComments( publicShareItem.getComments() );
								
								shareItem = buildShareItem( ami, gwtShareItem.getSharedById(), gwtShareItem );

								// Modify the share by marking existing snapshot as not being the latest
								// and persisting the new snapshot. 
								sharingModule.modifyShareItem( shareItem, gwtShareItem.getId() );
							}
						}
					}
				}
				else
				{
					GwtShareItem gwtShareItem;
					
					// No
					// Create a share item for "all internal users"
					{
						gwtShareItem = new GwtShareItem();
						gwtShareItem.setEntityId( publicShareItem.getEntityId() );
						gwtShareItem.setEntityName( publicShareItem.getEntityName() );
						gwtShareItem.setComments( publicShareItem.getComments() );
						gwtShareItem.setRecipientId( Utils.getAllUsersGroupId() );
						gwtShareItem.setRecipientType( GwtRecipientType.GROUP );
						gwtShareItem.setSharedById( publicShareItem.getSharedById() );
						gwtShareItem.setShareRights( publicShareItem.getShareRights() );
						gwtShareItem.setShareExpirationValue( publicShareItem.getShareExpirationValue() );
						gwtShareItem.setIsPartOfPublicShare( true );
						
						createShareItem( ami, gwtShareItem.getSharedById(), gwtShareItem );
					}


					// Create a share item for "guest"
					{
						gwtShareItem = new GwtShareItem();
						gwtShareItem.setEntityId( publicShareItem.getEntityId() );
						gwtShareItem.setEntityName( publicShareItem.getEntityName() );
						gwtShareItem.setComments( publicShareItem.getComments() );
						gwtShareItem.setRecipientId( Utils.getGuestId( ami ) );
						gwtShareItem.setRecipientType( GwtRecipientType.EXTERNAL_USER );
						gwtShareItem.setSharedById( publicShareItem.getSharedById() );
						gwtShareItem.setShareRights( publicShareItem.getShareRights() );
						gwtShareItem.setShareExpirationValue( publicShareItem.getShareExpirationValue() );
						gwtShareItem.setIsPartOfPublicShare( true );
						
						createShareItem( ami, gwtShareItem.getSharedById(), gwtShareItem );
					}
				}
			}
			else
			{
				ShareItem shareItem;
				Long shareItemId;
				boolean sendEmail;
				
				shareItem = null;
				sendEmail = false;
				
				if ( sharingData.getNotifyRecipients() && sharingData.getSendToValue() == SendToValue.ALL_RECIPIENTS )
					sendEmail = true;

				// Does this ShareItem exists?
				shareItemId = nextGwtShareItem.getId();
				if ( shareItemId == null )
				{
					// No, create a ShareItem object
					try
					{
						shareItem = createShareItem( ami, nextGwtShareItem.getSharedById(), nextGwtShareItem );
						
						// createShareItem() may have created an external user.  Get the
						// recipient id just in case.
						nextGwtShareItem.setRecipientId( shareItem.getRecipientId() );
		
						if ( sharingData.getNotifyRecipients() && (sharingData.getSendToValue() == SendToValue.ONLY_NEW_RECIPIENTS || sharingData.getSendToValue() == SendToValue.ONLY_MODIFIED_RECIPIENTS) )
							sendEmail = true;

						if ( getExternalUserAccountState( ami, shareItem.getRecipientId() ) == ExtProvState.initial )
						{
							// Yes, always send them an email.
							sendEmail = true;
						}
					}
					catch ( Exception ex )
					{
						String error;
						String[] args;

						m_logger.error( "Error creating share item: " + ex.toString() );
						
						if ( ex instanceof OperationAccessControlExceptionNoName )
						{
							args = new String[3];
							args[0] = nextGwtShareItem.getEntityName();
							args[1] = nextGwtShareItem.getRecipientName();
							error = NLT.get( "errorcode.sharing.entity.insufficient.rights", args );
							results.addError( error );
						}
						else
						{
							args = new String[3];
							args[0] = nextGwtShareItem.getEntityName();
							args[1] = nextGwtShareItem.getRecipientName();
							args[2] = ex.toString();
							error = NLT.get( "errorcode.sharing.entity", args );
							results.addError( error );
						}
						
						continue;
					}
				}
				else
				{
					// The ShareItem exists.
					// Build a new ShareItem with the new information.
					shareItem = buildShareItem( ami, nextGwtShareItem.getSharedById(), nextGwtShareItem );
					
					// Was it modified?
					if ( nextGwtShareItem.isDirty() )
					{
						// Yes
						// Modify the share by marking existing snapshot as not being the latest
						// and persisting the new snapshot. 
						sharingModule.modifyShareItem(shareItem, shareItemId);

						if ( sharingData.getNotifyRecipients() && sharingData.getSendToValue() == SendToValue.ONLY_MODIFIED_RECIPIENTS )
							sendEmail = true;
					}
				}
				
				// Did we successfully create/modify a share?
				if ( shareItem != null )
				{
					
					// Yes
					results.addSuccess( shareItem.getId(), nextGwtShareItem, sendEmail );
				}
			}
		}// end for()

		return results;
	}

	/**
	 * Returns true if an entity can be shared and false otherwise.
	 * 
	 * @param bs
	 * @param de
	 * 
	 * @return
	 */
	public static boolean isEntitySharable( AllModulesInjected bs, DefinableEntity de )
	{
		SharingModule sm = bs.getSharingModule();
		return ( sm.testAddShareEntity( de ) || sm.testAddShareEntityPublic( de ) );
	}

	/**
	 * Returns true if sharing is currently enabled and false
	 * otherwise.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static boolean isSharingEnabled( AllModulesInjected bs )
	{
		return bs.getSharingModule().isSharingEnabled();
	}

	/**
	 * 
	 */
	public static Boolean saveZoneShareRights( AllModulesInjected ami, ZoneShareRights rights )
	{
		ArrayList<GwtRole> roles;

		if ( ami == null || rights == null )
		{
			m_logger.error( "In GwtShareHelper.saveZoneShareRights(), invalid parameters" );
		}
		
		// Get the roles
		roles = rights.getRoles();
		
		if ( roles != null )
		{
			AdminModule adminModule;
			Long zoneId;
			WorkArea workArea;

			adminModule = ami.getAdminModule();
			
	    	zoneId = RequestContextHolder.getRequestContext().getZoneId();
			workArea = ami.getZoneModule().getZoneConfig( zoneId );

			for ( GwtRole nextRole : roles )
			{
				Long fnId = null;
				
				// Get the Function id for the given role
				fnId = GwtServerHelper.getFunctionIdFromRole( ami, nextRole );
	
				// Did we find the function for the given role?
				if ( fnId == null )
				{
					// No
					m_logger.error( "In GwtShareHelper.saveZoneShareRights(), could not find function for role: " + nextRole.getType() );
					continue;
				}
	
				// Reset the function's membership.
				adminModule.resetWorkAreaFunctionMemberships( workArea, fnId, nextRole.getMemberIds() );
			}
		}
		
		return Boolean.TRUE;
	}

	/*
	 * Converts a ShareList to a GwtShareList.
	 */
	private static GwtShareLists shareListsToGwtShareLists( ShareLists shareLists )
	{
		GwtShareLists reply = new GwtShareLists();
		
		switch ( shareLists.getShareListMode() )
		{
		case BLACKLIST:  reply.setShareListMode( GwtShareLists.ShareListMode.BLACKLIST ); break;
		case DISABLED:   reply.setShareListMode( GwtShareLists.ShareListMode.DISABLED  ); break;
		case WHITELIST:  reply.setShareListMode( GwtShareLists.ShareListMode.WHITELIST ); break;
		}
		
		List<String> list = shareLists.getEmailAddresses();
		if ( MiscUtil.hasItems( list ) )
		{
			for (String ema:  list)
			{
				reply.addEmailAddress( ema );
			}
		}
		
		list = shareLists.getDomains();
		if ( MiscUtil.hasItems( list ) )
		{
			for ( String domain:  list )
			{
				reply.addDomain( domain );
			}
		}
		
		return reply;
	}
	
	/*
	 * Converts a GwtShareList to a ShareList.
	 */
	private static ShareLists gwtShareListsToShareLists( GwtShareLists gwtShareLists )
	{
		ShareLists reply = new ShareLists();
		
		switch ( gwtShareLists.getShareListMode() )
		{
		case BLACKLIST:  reply.setShareListMode( ShareLists.ShareListMode.BLACKLIST ); break;
		case DISABLED:   reply.setShareListMode( ShareLists.ShareListMode.DISABLED  ); break;
		case WHITELIST:  reply.setShareListMode( ShareLists.ShareListMode.WHITELIST ); break;
		}
		
		List<String> list = gwtShareLists.getEmailAddresses();
		if ( MiscUtil.hasItems( list ) )
		{
			for ( String ema:  list )
			{
				reply.addEmailAddress( ema );
			}
		}
		
		list = gwtShareLists.getDomains();
		if ( MiscUtil.hasItems( list ) )
		{
			for ( String domain:  list )
			{
				reply.addDomain( domain );
			}
		}
		
		return reply;
	}

	/**
	 * Returns a GwtShareLists object that represents the current state
	 * of the share whitelist/blacklist.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GwtShareLists getShareLists( AllModulesInjected bs, HttpServletRequest request ) throws GwtTeamingException
	{
		GwtServerProfiler gsp = GwtServerProfiler.start( m_logger, "GwtShareHelper.getShareLists()" );
		try
		{
			ShareLists shareLists = bs.getSharingModule().getShareLists();
			GwtShareLists reply = ( ( null == shareLists ) ? new GwtShareLists() : shareListsToGwtShareLists( shareLists ) );
			return reply;
		}
		
		catch ( Exception e )
		{
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtShareHelper.getShareLists( SOURCE EXCEPTION ):  " );
		}
		
		finally
		{
			gsp.stop();
		}
	}

	/**
	 * Saves the current state of the share whitelist/blacklist.
	 * 
	 * @param bs
	 * @param request
	 * @param gwtShareLists
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData saveShareLists( AllModulesInjected bs, HttpServletRequest request, GwtShareLists gwtShareLists ) throws GwtTeamingException
	{
		GwtServerProfiler gsp = GwtServerProfiler.start( m_logger, "GwtShareHelper.saveShareLists()" );
		try
		{
			ShareLists shareLists = ((null == gwtShareLists) ? new ShareLists() : gwtShareListsToShareLists(gwtShareLists));
			bs.getSharingModule().setShareLists(shareLists);
			return new BooleanRpcResponseData( true );
		}
		
		catch ( Exception e )
		{
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtShareHelper.saveShareLists( SOURCE EXCEPTION ):  " );
		}
		
		finally
		{
			gsp.stop();
		}
	}
}
