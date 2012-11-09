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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.function.WorkAreaOperation.RightSet;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.User.ExtProvState;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.gwt.client.widgets.ShareSendToWidget.SendToValue;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
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
		
		binderModule = ami.getBinderModule();
		folderModule = ami.getFolderModule();
		currentUser = GwtServerHelper.getCurrentUser();
		
		try
		{
			BinderOperation binderOperation;
			FolderOperation folderOperation;

			switch ( shareOperation )
			{
			case SHARE_FORWARD:
				binderOperation = BinderOperation.allowSharingForward;
				folderOperation = FolderOperation.allowSharingForward;
				break;
			
			case SHARE_WITH_EXTERNAL_USERS:
				binderOperation = BinderOperation.allowSharingExternal;
				folderOperation = FolderOperation.allowSharingExternal;
				break;
			
			case SHARE_WITH_INTERNAL_USERS:
				binderOperation = BinderOperation.allowSharing;
				folderOperation = FolderOperation.allowSharing;
				break;
			
			case SHARE_WITH_PUBLIC:
				binderOperation = BinderOperation.allowSharingPublic;
				folderOperation = FolderOperation.allowSharingPublic;
				break;
			
			default:
				m_logger.info( "InGwtShareHelper.canShareWith(), unknown share operation: " + shareOperation.toString() );
				return false;
			}

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
	 * Create a ShareItem from the given GwtShareItem object
	 */
	private static ShareItem createShareItem(
		AllModulesInjected ami,
		User sharer,
		GwtShareItem gwtShareItem )
	{
		ShareItem shareItem = buildShareItem(ami, sharer, gwtShareItem);
		
		ami.getSharingModule().addShareItem( shareItem );
		
		return shareItem;
	}
	
	/**
	 * Build a ShareItem from the given GwtShareItem object
	 */
	private static ShareItem buildShareItem(
		AllModulesInjected ami,
		User sharer,
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
				long milliSecToExpire;
				Date now;

				daysToExpire = expirationValue.getValue().intValue();
				milliSecToExpire = daysToExpire * MILLISEC_IN_A_DAY;

				// Calculate the end date based on the days-to-expire.
				now = new Date();
				endDate = new Date( now.getTime() + milliSecToExpire );
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
								sharer.getId(),
								entityIdentifier,
								gwtShareItem.getComments(),
								endDate,
								recipientType,
								recipientId,
								rightSet );
			
			shareItem.setDaysToExpire( daysToExpire );
			shareItem.setLatest( true );
		}		
		
		return shareItem;
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
			m_logger.error( "sharingModule.getShareItems() failed: " + ex.toString() );
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
		User currentUser,
		EntityId entityId )
	{
		EntityIdentifier entityIdentifier;
		List<ShareItem> listOfShareItems = null;
		ArrayList<GwtShareItem> listOfGwtShareItems = null;
		ShareItemSelectSpec spec;

		listOfGwtShareItems = new ArrayList<GwtShareItem>();
		
		// Get the entity type
		entityIdentifier = getEntityIdentifierFromEntityId( entityId );
		
		// Get the list of ShareItem objects for the given entity
		spec = new ShareItemSelectSpec();
		spec.setSharerId( currentUser.getId() );
		spec.setSharedEntityIdentifier( entityIdentifier );
		spec.setLatest( true );
		
		try
		{
			listOfShareItems = ami.getSharingModule().getShareItems( spec );
		}
		catch ( Exception ex )
		{
			m_logger.error( "sharingModule.getShareItems() failed: " + ex.toString() );
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
				
				gwtShareItem = new GwtShareItem();
				gwtShareItem.setEntityId( entityId );
				gwtShareItem.setEntityName( getEntityName( ami, entityId ) );
				gwtShareItem.setId( nextShareItem.getId() );
				gwtShareItem.setIsExpired( nextShareItem.isExpired() );
				gwtShareItem.setRecipientId( nextShareItem.getRecipientId() );
				gwtShareItem.setComments( nextShareItem.getComment() );

				// Set the recipient type and name.
				{
					String name;
					
					switch ( nextShareItem.getRecipientType() )
					{
					case group:
						name = getGroupName( ami, nextShareItem );
						gwtShareItem.setRecipientName( name );
						gwtShareItem.setRecipientType( GwtRecipientType.GROUP );
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
	@SuppressWarnings("unchecked")
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
				final String recipientName;
				final ProfileModule profileModule;

				// Maybe
				profileModule = ami.getProfileModule();
				
				recipientName = gwtShareItem.getRecipientName();
				
				try
				{
					User user;

					// Does a Vibe account exist with the given name?
					user = profileModule.getUser( recipientName );
					if ( user != null )
					{
						id = user.getId();
					}
				}
				catch ( Exception ex )
				{
					RunasCallback callback;
					
					// If we get here a Vibe account does not exist for the given external user.
					// Create one.
					callback = new RunasCallback()
					{
						@Override
						public Object doAs() 
						{
							HashMap updates;
							User user;

							updates = new HashMap();
							updates.put( ObjectKeys.FIELD_USER_EMAIL, recipientName );
							updates.put( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, recipientName );
							updates.put( ObjectKeys.FIELD_USER_EXT_ACCOUNT_STATE, ExtProvState.initial );
							// Do NOT set the "fromOpenid" bit on initially. We will set it when the user actually
							// logs in and binds a valid OpenID account with the email address specified during sharing.
			 				user = profileModule.addUserFromPortal(
			 													new IdentityInfo(false, false, false, false),
			 													recipientName,
			 													null,
			 													updates,
			 													null );
			 				
			 				return user.getId();
						}
					}; 

					id = (Long) RunasTemplate.runasAdmin(
														callback,
														RequestContextHolder.getRequestContext().getZoneName() );
				}
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
	 * Return sharing information for the given entities
	 */
	public static GwtSharingInfo getSharingInfo( AllModulesInjected ami, List<EntityId> listOfEntityIds )
	{
		GwtSharingInfo sharingInfo;
		User currentUser;

		sharingInfo = new GwtSharingInfo();
		
		// See if the user has rights to share the given entities with an external user.
		sharingInfo.setCanShareWithExternalUsers( canShareWithExternalUsers( ami, listOfEntityIds ) );
		
		// See if the user has rights to share the given entities with an internal user.
		sharingInfo.setCanShareWithInternalUsers( canShareWithInternalUsers( ami, listOfEntityIds ) );
		
		// See if the user has rights to share the given entities with the public.
		sharingInfo.setCanShareWithPublic( canShareWithPublic( ami, listOfEntityIds ) );

		currentUser = GwtServerHelper.getCurrentUser();

		if ( listOfEntityIds == null || listOfEntityIds.size() == 0 )
		{
			m_logger.error( "In GwtShareHelper.getSharingInfo(), listOfEntityIds is null or empty" );
			return sharingInfo;
		}
		
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
			listOfGwtShareItems = getListOfGwtShareItems( ami, currentUser, nextEntityId );

			if ( listOfGwtShareItems != null )
			{
				for ( GwtShareItem nextGwtShareItem : listOfGwtShareItems )
				{
					sharingInfo.addShareItem( nextGwtShareItem );
				}
			}
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
	 * Send an email to the given recipient
	 */
	@SuppressWarnings("rawtypes")
	private static List sendEmailToRecipient(
		AllModulesInjected ami,
		ShareItem shareItem,
		GwtShareItem gwtShareItem,
		User currentUser )
	{
		List emailErrors;
		Set<Long> principalIds;
		Set<Long> teamIds;
		Set<Long> bccIds;
		String bccEmailAddress;
		EntityId entityId;
		DefinableEntity sharedEntity;

		if ( ami == null || currentUser == null || gwtShareItem == null )
		{
			m_logger.error( "invalid parameter in sendEmailToRecipient()" );
			return null;
		}
		
		principalIds = new HashSet<Long>();
		teamIds = new HashSet<Long>();
		
		switch ( gwtShareItem.getRecipientType() )
		{
		case GROUP:
		case USER:
		case EXTERNAL_USER:
			principalIds.add( gwtShareItem.getRecipientId() );
			break;
		
		case TEAM:
			teamIds.add( gwtShareItem.getRecipientId() );
			break;
		
		default:
			m_logger.error( "unknow recipient type in sendEmailToRecipient()" );
			break;
		}
		
		entityId = gwtShareItem.getEntityId();
		if ( entityId.isBinder() )
			sharedEntity = ami.getBinderModule().getBinder( entityId.getEntityId() );
		else
			sharedEntity = ami.getFolderModule().getEntry( entityId.getBinderId(), entityId.getEntityId() );
		
		// Does this user want to be BCC'd on all mail sent out?
		bccEmailAddress = currentUser.getBccEmailAddress();
		if ( MiscUtil.hasString( bccEmailAddress ) )
		{
			// Yes!
			// Add them to a BCC list.
			bccIds = new HashSet<Long>();
			bccIds.add( currentUser.getId() );
		}
		else
		{
			bccIds = null;
		}
		
		emailErrors = null;
		try
		{
			Map<String,Object> errorMap;
			
			if ( gwtShareItem.getRecipientType() == GwtRecipientType.EXTERNAL_USER &&
				 getExternalUserAccountState( ami, gwtShareItem.getRecipientId() ) == ExtProvState.initial )
			{
				errorMap = null;
				
				errorMap = GwtEmailHelper.sendShareInviteToExternalUser(
														ami,
														shareItem,
														sharedEntity,
														gwtShareItem.getRecipientId() );
			}
			else
			{
				errorMap = GwtEmailHelper.sendShareNotification(
														ami,
														shareItem,
														sharedEntity,
														principalIds,
														teamIds,
														null,	// null -> No stand alone email addresses.
														null,	// null -> No CC'ed users.
														bccIds );
			}
			
			if ( errorMap != null )
			{
				emailErrors = (List) errorMap.get( ObjectKeys.SENDMAIL_ERRORS );
			}
		}
		catch ( Exception ex )
		{
			m_logger.error( "GwtEmailHelper.sendShareNotification() threw an exception: " + ex.toString() );
		}
		return emailErrors;
	}
		
	/**
	 * Save the given share data. 
	 */
	@SuppressWarnings({ "unchecked" })
	public static GwtShareEntryResults shareEntry(
		AllModulesInjected ami,
		GwtSharingInfo sharingData )
	{
		SharingModule sharingModule;
		GwtShareEntryResults results;
		ArrayList<GwtShareItem> listOfGwtShareItems;
		ArrayList<GwtShareItem> listOfGwtShareItemsToDelete;
		User currentUser;
		List emailErrors;

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
		
		currentUser = GwtServerHelper.getCurrentUser();
		emailErrors = null;
		
		// Delete ShareItems that the user removed.
		listOfGwtShareItemsToDelete = sharingData.getListOfToBeDeletedShareItems();
		if ( listOfGwtShareItemsToDelete != null )
		{
			for ( GwtShareItem nextShareItem : listOfGwtShareItemsToDelete )
			{
				Long shareItemId;
				
				shareItemId = nextShareItem.getId();
				if ( shareItemId != null )
				{
					sharingModule.deleteShareItem( shareItemId );
				}
			}
		}
		
		// For each GwtShareItem, make the necessary updates to the database.
		for (GwtShareItem nextGwtShareItem : listOfGwtShareItems)
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
					shareItem = createShareItem( ami, currentUser, nextGwtShareItem );
				
					// createShareItem() may have created an external user.  Get the
					// recipient id just in case.
					nextGwtShareItem.setRecipientId( shareItem.getRecipientId() );
	
					if ( sharingData.getNotifyRecipients() && (sharingData.getSendToValue() == SendToValue.ONLY_NEW_RECIPIENTS || sharingData.getSendToValue() == SendToValue.ONLY_MODIFIED_RECIPIENTS) )
						sendEmail = true;
				}
				catch ( Exception ex )
				{
					String error;
					String[] args;
					
					args = new String[3];
					args[0] = nextGwtShareItem.getEntityName();
					args[1] = nextGwtShareItem.getRecipientName();
					args[2] = ex.toString();
					error = NLT.get( "errorcode.sharing.entity", args );
					results.addError( error );
					m_logger.error( "Error creating share item: " + ex.toString() );
					continue;
				}
			}
			else
			{
				// The ShareItem exists.
				// Build a new ShareItem with the new information.
				shareItem = buildShareItem( ami, currentUser, nextGwtShareItem );
				
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
			
			// Is the recipient a newly created external user
			if ( getExternalUserAccountState( ami, shareItem.getRecipientId() ) == ExtProvState.initial )
			{
				// Yes, always send them an email.
				sendEmail = true;
			}
			
			// Send an email to this recipient
			if ( sendEmail )
			{
				List entityEmailErrors = null;
				
				// Send an email to each of the recipients
				entityEmailErrors = sendEmailToRecipient(
														ami,
														shareItem,
														nextGwtShareItem,
														currentUser );
				
				if ( emailErrors == null )
				{
					emailErrors = entityEmailErrors;
				}
				else
				{
					if ( entityEmailErrors != null )
					{
						emailErrors.addAll( entityEmailErrors );
					}
				}
			}
		}// end for()

		// Add any errors that happened to the results.
		if ( null != emailErrors )
		{
			results.addErrors( emailErrors );
		}
		
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
		return bs.getSharingModule().testAddShareEntity( de );
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
	
}
