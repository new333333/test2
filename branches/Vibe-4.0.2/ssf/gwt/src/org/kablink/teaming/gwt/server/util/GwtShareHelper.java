/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.FolderDao;
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
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.User.ExtProvState;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.gwt.client.GwtEmailPublicLinkResults;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtPublic;
import org.kablink.teaming.gwt.client.GwtRole;
import org.kablink.teaming.gwt.client.GwtSendShareNotificationEmailResults;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.ZoneShareRights;
import org.kablink.teaming.gwt.client.ZoneShareTerms;
import org.kablink.teaming.gwt.client.GwtRole.GwtRoleType;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.MailToPublicLinksRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.PublicLinksRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateShareListsRpcResponseData;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtEmailPublicLinkData;
import org.kablink.teaming.gwt.client.util.GwtPublicShareItem;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtShareLists;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.PerEntityShareRightsInfo;
import org.kablink.teaming.gwt.client.util.PublicLinkInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.gwt.client.util.PrincipalType;
import org.kablink.teaming.gwt.client.widgets.ShareSendToWidget.SendToValue;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.SendMailErrorWrapper;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.FileIconsHelper;
import org.kablink.teaming.util.IconSize;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.ShareLists;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.*;

import org.springframework.mail.MailSendException;

/**
 * Helper methods for the GWT UI server code that services share requests.
 *
 * @author jwootton@novell.com
 */
@SuppressWarnings("unchecked")
public class GwtShareHelper 
{
	protected static Log					m_logger               = LogFactory.getLog( GwtShareHelper.class );
	private   static long					MILLISEC_IN_A_DAY      = 86400000;
	private   static AccessControlManager	m_accessControlManager = null;
	
	/**
	 */
	public enum ShareOperation
	{
		SHARE_FORWARD,
		SHARE_WITH_EXTERNAL_USERS,
		SHARE_WITH_INTERNAL_USERS,
		SHARE_WITH_PUBLIC,
		SHARE_PUBLIC_LINK
	}
	
	/*
	 * Returns a GWT EntityId based on an EntitiyIdentifier. 
	 */
	private static EntityId buildEntityIdFromEntityIdentifier( AllModulesInjected bs, EntityIdentifier eid )
	{
		EntityId reply;
		
		switch ( eid.getEntityType() )
		{
		case folderEntry:
			FolderEntry entry = bs.getFolderModule().getEntry( null, eid.getEntityId() );
			reply = new EntityId( entry.getParentBinder().getId(), eid.getEntityId(), EntityId.FOLDER_ENTRY );
			break;
			
		case folder:
			reply = new EntityId( eid.getEntityId(), EntityId.FOLDER );
			break;
			
		case workspace:
			reply = new EntityId( eid.getEntityId(), EntityId.WORKSPACE );
			break;
			
		default:
			m_logger.error( "GwtShareHelper.buildEntityIdFromEntityIdentifier():  Unknown entity type: " + eid.getEntityType() );
			reply = null;
			break;
		}
		
		return reply;
	}

	private static AccessRights buildAccessRightsFromShareItemRole(ShareItem.Role role) {
		if (role == ShareItem.Role.CONTRIBUTOR) {
			return AccessRights.CONTRIBUTOR;
		} else if (role == ShareItem.Role.EDITOR) {
			return AccessRights.EDITOR;
		} else if (role == ShareItem.Role.VIEWER) {
			return AccessRights.VIEWER;
		} else {
			return AccessRights.NONE;
		}
	}
	
	/*
	 * Constructs and returns a ShareExpirationValue from a ShareItem. 
	 */
	private static ShareExpirationValue buildShareExpiratioNValueFromShareItem( ShareItem si )
	{
		ShareExpirationValue reply = new ShareExpirationValue();
		reply.setType( ShareExpirationType.NEVER );
		
		// Is there an expiration specified?
		Date endDate = si.getEndDate();
		if ( endDate != null )
		{
			int expiresAfterDays;
			
			// Do we have an "expires after" value?
			expiresAfterDays = si.getDaysToExpire();
			if ( expiresAfterDays > 0 )
			{
				long milliSecLeft;
				
				// Yes
				// Calculate how many days are left before the share expires.
				milliSecLeft = endDate.getTime() - new Date().getTime();
				expiresAfterDays = (int)(milliSecLeft / MILLISEC_IN_A_DAY);
				if ( expiresAfterDays >= 0 )
				{
					if ( (milliSecLeft % MILLISEC_IN_A_DAY) > 0 )
						++expiresAfterDays;
				}
				
				// If the share has already expired...
				if ( 0 > expiresAfterDays )
				{
					// ...let the UI handle it as an expired on date.
					reply.setType( ShareExpirationType.ON_DATE );
					reply.setValue( endDate.getTime() );
				}
				else
				{
					// ...otherwise, it can continue to handle it as an
					// ...expired after days.
					reply.setType( ShareExpirationType.AFTER_DAYS );
					reply.setValue( Long.valueOf( expiresAfterDays ) );
				}
			}
			else
			{
				// We are dealing with "expires on"
				reply.setType( ShareExpirationType.ON_DATE );
				reply.setValue( endDate.getTime() );
			}
		}
		return reply;
	}
	
	/**
	 * See if the user has rights to share with the "all external users" group.
	 * 
	 * @param ami
	 * 
	 * @return
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
	 * 
	 * @param ami
	 * 
	 * @return
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
	 * 
	 * @param ami
	 * @param entityid
	 * @param shareOperation
	 * 
	 * @return
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
			
			case SHARE_PUBLIC_LINK:
				// Is sharing a public link enabled at the zone level?
				m_accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_LINK_SHARING );
				
				binderOperation = BinderOperation.allowSharingPublicLinks;
				folderOperation = FolderOperation.allowSharingPublicLinks;
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
	 * 
	 * @param ami
	 * @param listOfEntityids
	 * @param shareOperation
	 * 
	 * @return
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
	 * See if the user can share the given entities using a File link
	 * 
	 * @param ami
	 * @param listOfEntityids
	 * 
	 * @return
	 */
	public static boolean canShareUsingFileLink(
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		return canShareWith( ami, listOfEntityIds, ShareOperation.SHARE_PUBLIC_LINK );
	}

	/**
	 * See if the user can share the given entities with external users.
	 * 
	 * @param ami
	 * @param listOfEntityids
	 * 
	 * @return
	 */
	public static boolean canShareWithExternalUsers (
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		return canShareWith( ami, listOfEntityIds, ShareOperation.SHARE_WITH_EXTERNAL_USERS );
	}

	/**
	 * See if the user can share the given entities with internal users.
	 * 
	 * @param ami
	 * @param listOfEntityids
	 * 
	 * @return
	 */
	public static boolean canShareWithInternalUsers (
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		return canShareWith( ami, listOfEntityIds, ShareOperation.SHARE_WITH_INTERNAL_USERS );
	}

	/**
	 * See if the user can share the given entities with the public.
	 * 
	 * @param ami
	 * @param listOfEntityids
	 * 
	 * @return
	 */
	public static boolean canShareWithPublic (
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		return canShareWith( ami, listOfEntityIds, ShareOperation.SHARE_WITH_PUBLIC );
	}

	/**
	 * See if the user can share the given entities with the public.
	 * 
	 * @param ami
	 * @param listOfEntityids
	 * 
	 * @return
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
	 * 
	 * @param ami
	 * @param listOfGwtShareItems
	 * 
	 * @return
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
						publicShareItem.setRecipientPrincipalType( PrincipalType.UNKNOWN );
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
	 * Create a ShareItem that can be used as a "public link" share.
	 * 
	 * @param ami
	 * @param sharedById
	 * @param entityId
	 * @param expirationValue
	 * @param comment
	 * 
	 * @return
	 */
	private static ShareItem createPublicLinkShareItem(
		AllModulesInjected ami,
		Long sharedById,
		EntityId entityId,
		ShareExpirationValue expirationValue,
		String comment )
	{
		ShareItem shareItem;
		
		shareItem = buildPublicLinkShareItem( ami, sharedById, entityId, expirationValue, comment );
		if ( shareItem != null )
		{
			try
			{
				// Create this ShareItem in the db.
				ami.getSharingModule().addShareItem( shareItem );
			}
			catch ( Exception ex )
			{
				m_logger.error( "In createPublicLinkShareItem(), addShareItem() threw an exception: " + ex.toString() );
			}
		}
		
		return shareItem;
	}
	
	/*
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
	
	/*
	 * Build a ShareItem that can be used as a "public link" share
	 */
	private static ShareItem buildPublicLinkShareItem(
		AllModulesInjected ami,
		Long sharedById,
		EntityId entityId,
		ShareExpirationValue expirationValue,
		String comment )
	{
		ShareItem shareItem;
		Date endDate = null;
		RightSet rightSet;
		EntityIdentifier entityIdentifier;
		int daysToExpire = -1;

		// Get the entity that is being shared.
		entityIdentifier = getEntityIdentifierFromEntityId( entityId );		

		// Get the share expiration value
		{
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
		
		// Create the appropriate RightSet
		{
			rightSet = ShareHelper.getViewerRightSet();
			rightSet.setAllowSharingForward( false );
			rightSet.setAllowSharing( false );
			rightSet.setAllowSharingExternal( false );
			rightSet.setAllowSharingPublic( false );
		}
		
		// Create the new ShareItem.  This does not create a new ShareItem in the db
		{
			shareItem = new ShareItem(
									sharedById,
									entityIdentifier,
									comment,
									endDate,
									RecipientType.publicLink,
									null,
									rightSet );
			
			shareItem.setDaysToExpire( daysToExpire );
			shareItem.setLatest( true );
		}		
		
		return shareItem;
	}
	
	/*
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
			
		case PUBLIC_LINK:
			recipientType = RecipientType.publicLink;
			break;
			
		case UNKNOWN:
		default:
			recipientType = RecipientType.user;
			break;
		}
		
		recipientId = getRecipientId( ami, gwtShareItem );

		// Get the appropriate RightSet
		rightSet = getRightSetFromShareRights( gwtShareItem );
		
		// Create the new ShareItem
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

	/*
	 * Returns a List<ShareItem> of all the public link shares for the
	 * given entity.
	 */
	private static List<ShareItem> getPublicLinkShareItems( AllModulesInjected bs, Long sharerId, EntityId entityId )
	{
		// Set up the search criteria to get the shares on this entity.
		ShareItemSelectSpec spec = new ShareItemSelectSpec();
		spec.setSharerId( Long.valueOf( sharerId ) );
		EntityIdentifier entityIdentifier = getEntityIdentifierFromEntityId( entityId );
		spec.setSharedEntityIdentifier( entityIdentifier );
		spec.setLatest( true );
		
		// Are there shares on it?
		List<ShareItem> shares = bs.getSharingModule().getShareItems( spec );
		List<ShareItem> reply  = new ArrayList<ShareItem>();
		if ( MiscUtil.hasItems( shares ))
		{
			// Yes!  Scan them.
			for ( ShareItem si:  shares )
			{
				// Should we return this as public link share?
				boolean ignore = (
					si.isDeleted()          ||												// Not if it's deleted...
					( ! ( si.isLatest() ) ) ||												// ...or if it's not the latest version of this share...
					( ! ( si.getRecipientType().equals( RecipientType.publicLink ) ) ) );	// ...or if it's not a public link share.
				
				if ( !ignore )
				{
					// Yes!  Add it to the reply list.
					reply.add( si );
				}
			}
		}

		// If we get here, reply refers to a List<ShareItem> of the
		// public link shares against the entity.  Return it. 
		return reply;
	}
	
	/**
	 * ?
	 * 
	 * @param ami
	 * @param request
	 * @param data
	 * 
	 * @return
	 */
	public static GwtEmailPublicLinkResults emailPublicLink(
		AllModulesInjected ami,
		HttpServletRequest request,
		GwtEmailPublicLinkData data )
	{
		GwtEmailPublicLinkResults results;
		ArrayList<String> listOfEmailAddresses;
		List<EntityId> listOfEntityIds;
		List<SendMailErrorWrapper> emailErrors = null;
		User currentUser;
		
		results = new GwtEmailPublicLinkResults();

		currentUser = GwtServerHelper.getCurrentUser();

		emailErrors = new ArrayList<SendMailErrorWrapper>();

		listOfEmailAddresses = data.getListOfEmailAddresses();
		
		listOfEntityIds = data.getListOfEntities();
		
		if ( listOfEntityIds != null && listOfEntityIds.size() > 0 &&
			 listOfEmailAddresses != null && listOfEmailAddresses.size() > 0 )
		{
			for ( EntityId nextEntityId : listOfEntityIds )
			{
				ShareItem shareItem = null;
				String fileName = null;
				
				// Get the name of the file
				try
				{
					FolderEntry entry;
					FileAttachment fileAttach;
					FileItem fileItem;

					// Get the name of the file.
					entry = ami.getFolderModule().getEntry( null, nextEntityId.getEntityId() );
					fileAttach = MiscUtil.getPrimaryFileAttachment( entry );
					fileItem = fileAttach.getFileItem();
					if ( fileItem != null )
						fileName = fileItem.getName();
				}
				catch ( Exception ex )
				{
					SendMailErrorWrapper error;
					MailSendException msEx = null;
					
					m_logger.error( "In GwtShareHelper.emailPublicLink(), unable to get file name" );
					
					error = new SendMailErrorWrapper( msEx, NLT.get( "email.public.link.cant.get.file.name" ) );
					emailErrors.add( error );
				}
				
				// Did we get a file name?
				if ( fileName != null )
				{
					List<ShareItem> listOfShareItems;

					// Yes
					// Has the user already created a "public link" share for this file?
					listOfShareItems = getPublicLinkShareItems( ami, currentUser.getId(), nextEntityId );
					if ( listOfShareItems != null && listOfShareItems.size() > 0 )
					{
						ShareItem tmpShareItem;
						
						// Yes
						// There should only be one.
						shareItem = listOfShareItems.get( 0 );

						// Build a ShareItem with the new information.  buildPublicLinksShareItem()
						// will not create a new ShareItem in the db.
						tmpShareItem = buildPublicLinkShareItem(
															ami,
															currentUser.getId(),
															nextEntityId,
															data.getExpirationValue(),
															data.getMessage() );
						
						// Modify the existing "public link" share item.
						ami.getSharingModule().modifyShareItem( tmpShareItem, shareItem.getId() );
					}
					else
					{
						// No
						// Create a "public link" ShareItem
						shareItem = createPublicLinkShareItem(
														ami,
														currentUser.getId(),
														nextEntityId,
														data.getExpirationValue(),
														data.getMessage() );
					}

					if ( shareItem != null && shareItem.getId() != null )
					{
						List<SendMailErrorWrapper> entityEmailErrors = null;
						String viewUrl = null;
						String downloadUrl = null;
						
						// Get the download file url
						downloadUrl = WebUrlUtil.getSharedPublicFileUrl(
																	request,
																	shareItem.getId(),
																	shareItem.getPassKey(),
																	WebKeys.URL_SHARE_PUBLIC_LINK,
																	fileName );

						// Can this file be rendered as html?
						if ( GwtViewHelper.supportsViewAsHtml( fileName ) )
						{
							// Yes, get the view file url.
							viewUrl = WebUrlUtil.getSharedPublicFileUrl(
																	request,
																	shareItem.getId(),
																	shareItem.getPassKey(),
																	WebKeys.URL_SHARE_PUBLIC_LINK_HTML,
																	fileName );
						}
						
						// Send an email to each recipient
						entityEmailErrors = EmailHelper.sendEmailToPublicLinkRecipients(
																					ami,
																					shareItem,
																					currentUser,
																					listOfEmailAddresses,
																					viewUrl,
																					downloadUrl);
						if ( entityEmailErrors != null )
						{
							if ( emailErrors == null )
								emailErrors = entityEmailErrors;
							else
								emailErrors.addAll( entityEmailErrors );
						}
					}
					else
					{
						SendMailErrorWrapper error;
						MailSendException msEx = null;
						
						m_logger.error( "In GwtShareHelper.emailPublicLink(), unable to create ShareItem" );
						
						error = new SendMailErrorWrapper( msEx, NLT.get( "email.public.link.cant.create.shareitem" ) );
						emailErrors.add( error );
					}
				}
			}// end for()
		}

		// Add any errors that happened to the results.
		if ( emailErrors != null )
		{
			results.addErrors( SendMailErrorWrapper.getErrorMessages( emailErrors ) );
		}

		return results;
	}
	
	/*
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
	 * ?
	 * 
	 * @return
	 */
	public static AccessControlManager getAccessControlManager()
	{
		if ( m_accessControlManager == null )
			m_accessControlManager = (AccessControlManager) SpringContextUtil.getBean( "accessControlManager" );

		return m_accessControlManager;
	}
	
	/**
	 * Return an EntityIdentifier for the given EntityId
	 * 
	 * @param entityId
	 * 
	 * @return
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
			
			//~JW:  Finish
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
	
	/*
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
	
	/*
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

	/*
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

					if ( ((Group) group).getGroupType() == Group.GroupType.team )
						name = group.getTitle();
				}
			}
			catch ( Exception e )
			{
				m_logger.error( "Could not find the group: " + shareItem.getRecipientId().toString() );
			}
		}
		
		return name;
	}

	private static Folder getFolderFromEntityId(EntityId entityId) {
		Folder folder = null;
		EntityIdentifier entityIdentifier = getEntityIdentifierFromEntityId(entityId);
		if(EntityIdentifier.EntityType.folder == entityIdentifier.getEntityType())
			folder = getFolderDao().loadFolder(entityIdentifier.getEntityId(), null);
		return folder;
	}
	
	/*
	 * Return the share rights the logged-in user has to the given entity
	 */
	private static ShareRights getEntityShareRights( AllModulesInjected ami, EntityId entityId )
	{
		ShareRights shareRights;
		boolean result;
		
		shareRights = new ShareRights();

		SharingModule.EntityShareRights highestRights = ami.getSharingModule().calculateHighestEntityShareRights(getEntityIdentifierFromEntityId(entityId));

		shareRights.setAccessRights( buildAccessRightsFromShareItemRole(highestRights.getMaxGrantRole()) );
		shareRights.setUnAlteredAccessRights(buildAccessRightsFromShareItemRole(highestRights.getTopRole()));
		
		// Determine if the user has "can share with external users" rights.
		result = canShareWith( ami, entityId, ShareOperation.SHARE_WITH_EXTERNAL_USERS );
		shareRights.setCanShareWithExternalUsers( result );

		// Determine if the user has "can share with internal users" rights.
		result = canShareWith( ami, entityId, ShareOperation.SHARE_WITH_INTERNAL_USERS );
		shareRights.setCanShareWithInternalUsers( result );

		// Determine if the user has "can share with the public" rights.
		result = canShareWith( ami, entityId, ShareOperation.SHARE_WITH_PUBLIC );
		shareRights.setCanShareWithPublic( result );
		
		// Determine if the user has "can share a Link" rights.
		result = canShareWith( ami, entityId, ShareOperation.SHARE_PUBLIC_LINK );
		shareRights.setCanSharePublicLink( result );
		
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
	
	/*
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
			
			if ( entityId == null )
			{
				// Bug 876900, we need to validate that the entity that each share item is referencing
				// still exists.
				ami.getSharingModule().validateShareItems( listOfShareItems );
			}
		}
		catch ( Exception ex )
		{
			m_logger.error( "sharingModule.getListOfGwtShareItems() failed: " + ex.toString() );
		}

		// Do we have a list of ShareItem objects for the given entity?
		if ( listOfShareItems != null )
		{
			// Yes
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
						nextEntityId = buildEntityIdFromEntityIdentifier( ami, entityIdentifier );
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
						gwtShareItem.setRecipientPrincipalType( PrincipalType.UNKNOWN );
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
							
							gwtShareItem.setRecipientPrincipalType( GwtViewHelper.getPrincipalType( user ) );
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
						
					case publicLink:
						name = NLT.get( "public.link.name" );
						gwtShareItem.setRecipientName( name );
						gwtShareItem.setRecipientType( GwtRecipientType.PUBLIC_LINK );
						break;
						
					default:
						gwtShareItem.setRecipientType( GwtRecipientType.UNKNOWN );
						m_logger.error( "unknown recipient type: " + nextShareItem.getRecipientType().toString() );
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
					ShareExpirationValue expirationValue = buildShareExpiratioNValueFromShareItem( nextShareItem );
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

	/*
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
	
	/*
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
			rightSet = ShareHelper.getContributorRightSet();
			break;
		
		case EDITOR:
			rightSet = ShareHelper.getEditorRightSet();
			break;
		
		case VIEWER:
			rightSet = ShareHelper.getViewerRightSet();
			break;
			
		case NONE:
			rightSet = new RightSet();
			break;
			
		default:
			rightSet = new RightSet();
			m_logger.error( "In GwtShareHelper.getRightSet(), unknown share rights" );
			break;
		}
		
		Folder folder = getFolderFromEntityId(gwtShareItem.getEntityId());
		
		if(folder != null && folder.isFolderInNetFolder()) {
			// Sharing a folder from a net folder. This maps to a different set of rights.
			rightSet.setAllowFolderSharingForward( shareRights.getCanShareForward() );
			rightSet.setAllowFolderSharingInternal( shareRights.getCanShareWithInternalUsers() );
			rightSet.setAllowFolderSharingExternal( shareRights.getCanShareWithExternalUsers() );
			rightSet.setAllowFolderSharingPublic( shareRights.getCanShareWithPublic() );
			rightSet.setAllowSharingForward( shareRights.getCanShareForward() );
			rightSet.setAllowSharing( shareRights.getCanShareWithInternalUsers() );
			rightSet.setAllowSharingExternal( shareRights.getCanShareWithExternalUsers() );
			rightSet.setAllowSharingPublic( shareRights.getCanShareWithPublic() );
			rightSet.setAllowSharingPublicLinks( shareRights.getCanSharePublicLink() );
		}
		else {
			// All other cases.
			rightSet.setAllowSharingForward( shareRights.getCanShareForward() );
			rightSet.setAllowSharing( shareRights.getCanShareWithInternalUsers() );
			rightSet.setAllowSharingExternal( shareRights.getCanShareWithExternalUsers() );
			rightSet.setAllowSharingPublic( shareRights.getCanShareWithPublic() );
			rightSet.setAllowSharingPublicLinks( shareRights.getCanSharePublicLink() );
		}
	
		return rightSet;
	}
	
	/**
	 * Get a ShareRights object that corresponds to the given RightSet
	 * 
	 * @param rightSet
	 * 
	 * @return
	 */
	public static ShareRights getShareRightsFromRightSet( RightSet rightSet )
	{
		ShareRights shareRights;

		shareRights = new ShareRights();
		shareRights.setAccessRights( ShareRights.AccessRights.NONE );

		if ( rightSet != null )
		{
			AccessRights accessRights;

			accessRights = buildAccessRightsFromShareItemRole(ShareHelper.getAccessRightsFromRightSet( rightSet ));
			shareRights.setAccessRights( accessRights );

			// Does the RightSet allow "share with external users"?
			// The rightSet could contain different set of rights depending on whether the rightSet
			// came from a share associated with a folder in a net folder or not. Instead of actually
			// checking for that condition, we will simply set the resulting bit on if either right
			// is present in the righSet with the assumption that the user is working with the 
			// correct entity associated with the share (hence no possibility of mis-presentation).
			shareRights.setCanShareWithExternalUsers( rightSet.isAllowSharingExternal() || rightSet.isAllowFolderSharingExternal());
			
			// Does the RightSet allow "share with internal users"?
			shareRights.setCanShareWithInternalUsers( rightSet.isAllowSharing() || rightSet.isAllowFolderSharingInternal() );
			
			// Does the RightSet allow "share with public"?
			shareRights.setCanShareWithPublic( rightSet.isAllowSharingPublic() || rightSet.isAllowFolderSharingPublic() );
			
			// Does the RightSet allow "share forward"?
			shareRights.setCanShareForward( rightSet.isAllowSharingForward() || rightSet.isAllowFolderSharingForward() );
			
			// Does the RightSet allow "share public link"?
			shareRights.setCanSharePublicLink( rightSet.isAllowSharingPublicLinks() );
		}
		
		return shareRights;
	}
	
	/**
	 * Return the sharing roles that are defined at the zone level.
	 * 
	 * @param ami
	 * 
	 * @return
	 */
	public static ZoneShareRights getZoneShareRights( AllModulesInjected ami )
	{
		ZoneShareRights shareSettings;
		ArrayList<GwtRole> listOfRoles;
		GwtRole role;
		AdminModule adminModule;
		Long zoneId;
		WorkArea workArea;

		shareSettings = new ZoneShareRights();

		// Get the "allow users to share with ldap groups" setting
		{
			ZoneConfig zoneConfig;
			
			zoneConfig = ami.getZoneModule().getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
			shareSettings.setAllowShareWithLdapGroups( zoneConfig.isSharingWithLdapGroupsEnabled() );
		}
		
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
		role = new GwtRole();
		role.setType( GwtRoleType.EnableShareLink );
		listOfRoles.add( role );
		
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
						Description desc;
						
						nextGroup = (Group) nextPrincipal;
						
						gwtGroup = new GwtGroup();
						gwtGroup.setInternal( nextGroup.getIdentityInfo().isInternal() );
						gwtGroup.setId( nextGroup.getId().toString() );
						gwtGroup.setName( nextGroup.getName() );
						gwtGroup.setTitle( nextGroup.getTitle() );
						gwtGroup.setDn( nextGroup.getForeignName() );
						desc = nextGroup.getDescription();
						if ( desc != null )
							gwtGroup.setDesc( desc.getText() );
						gwtGroup.setGroupType( GwtServerHelper.getGroupType( nextGroup ) );
						
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
						gwtUser.setEmail( user.getEmailAddress() );
	
						nextRole.addMember( gwtUser );
					}
				}
			}
		}
		
		return shareSettings;
	}
	
	/**
	 * Return the sharing roles that are defined at the zone level.
	 * 
	 * @param ami
	 * 
	 * @return
	 */
	public static ZoneShareTerms getZoneShareTerms( AllModulesInjected ami )
	{
		Long zoneId=RequestContextHolder.getRequestContext().getZoneId();
		String termsAndConditions=ami.getZoneModule().getZoneConfig( zoneId ).getExtUserTermsAndConditions();
		Boolean showTermsAndConditions=ami.getZoneModule().getZoneConfig(zoneId).isExtUserTermsAndConditionsEnabled();
		ZoneShareTerms shareTerms=new ZoneShareTerms(termsAndConditions,showTermsAndConditions);	
		return shareTerms;
	}	

	/**
	 * Return sharing information for the given entities
	 * 
	 * @param ami
	 * @param listOfEntityids
	 * @param sharedById
	 * 
	 * @return
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
				
				// Get the share rights the logged-in user has to this entity
				shareRights = getEntityShareRights( ami, nextEntityId );
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

							// Get the share rights the logged-in user has to this entity
							shareRights = getEntityShareRights( ami, entityId );
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
		
		// Get the "allow users to share with ldap groups" setting
		{
			ZoneConfig zoneConfig;
			
			zoneConfig = ami.getZoneModule().getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
			sharingInfo.setCanShareWithLdapGroups( zoneConfig.isSharingWithLdapGroupsEnabled() );
		}
		
		// See if the user has rights to share the given entities using a File link
		sharingInfo.setCanShareUsingFileLink( canShareUsingFileLink( ami, listOfEntityIds ) );

		return sharingInfo;
	}

	/*
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

	/*
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
	 * Send a notification email for each of the given share items
	 * 
	 * @param ami
	 * @param listOfShareItemIds
	 * 
	 * @return
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
	 * 
	 * @param ami
	 * @param sharingData
	 * 
	 * @return
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
					// Was it modified?
					if ( nextGwtShareItem.isDirty() )
					{
						// Yes
						// Build a new ShareItem with the new information.
						shareItem = buildShareItem( ami, nextGwtShareItem.getSharedById(), nextGwtShareItem );
						
						// Modify the share by marking existing snapshot as not being the latest
						// and persisting the new snapshot. 
						sharingModule.modifyShareItem( shareItem, shareItemId );

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
				else
				{
					// No
					// Are we supposed to send an email to all recipients?
					if ( sendEmail == true )
					{
						// Yes
						if ( shareItemId != null )
						{
							results.addSuccess( shareItemId, nextGwtShareItem, sendEmail );
						}
					}
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
	public static boolean isEntitySharable(AllModulesInjected bs, DefinableEntity de) {
		SharingModule sm = bs.getSharingModule();
		return (sm.testAddShareEntity(de) || sm.testAddShareEntityPublic(de));
	}

	/**
	 * Returns true if an entity's public link can be shared and false
	 * otherwise.
	 * 
	 * @param bs
	 * @param de
	 * 
	 * @return
	 */
	public static boolean isEntityPublicLinkSharable(AllModulesInjected bs, DefinableEntity de) {
		SharingModule sm = bs.getSharingModule();
		return sm.testAddShareEntityPublicLinks(de);
	}

	/**
	 * Returns true if share forwarding is currently enabled and false
	 * otherwise.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static boolean isShareForwardingEnabled(AllModulesInjected bs) {
		return bs.getSharingModule().isShareForwardingEnabled();
	}

	/**
	 * Returns true if sharing is currently enabled and false
	 * otherwise.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static boolean isSharingEnabled(AllModulesInjected bs) {
		return bs.getSharingModule().isSharingEnabled();
	}

	/**
	 * Returns true if sharing public links is currently enabled and
	 * false otherwise.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static boolean isSharingPublicLinksEnabled(AllModulesInjected bs) {
		return bs.getSharingModule().isSharingPublicLinksEnabled();
	}

	/**
	 * ?
	 * 
	 * @param ami
	 * @param rights
	 * 
	 * @return
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
				// It is ok if we didn't find the "EnableShareWithAllExternal" role.  That role used
				// to exist but doesn't now.
				if ( fnId == null && nextRole.getType() != GwtRoleType.EnableShareWithAllExternal )
				{
					// No
					m_logger.error( "In GwtShareHelper.saveZoneShareRights(), could not find function for role: " + nextRole.getType() );
					continue;
				}
	
				// Reset the function's membership.
				adminModule.resetWorkAreaFunctionMemberships( workArea, fnId, nextRole.getMemberIds() );
			}
		}
		
		// Save the "allow users to share with ldap groups" setting
		{
			AdminModule adminModule;
			
			adminModule = ami.getAdminModule();
			adminModule.setAllowShareWithLdapGroups( rights.getAllowShareWithLdapGroups() );
		}
		
		return Boolean.TRUE;
	}
	
	/**
	 * ?
	 * 
	 * @param ami
	 * @param terms and conditions
	 * 
	 * @return
	 */
	public static Boolean saveZoneShareTerms( AllModulesInjected ami, ZoneShareTerms terms )
	{
		if ( ami == null || terms == null || terms.getTermsAndConditions() == null || ami.getAdminModule() == null)
		{
			m_logger.error( "In GwtShareHelper.saveZoneShareTerms(), invalid parameters" );
			return Boolean.FALSE;
		}		
		ami.getAdminModule().setExtUserTermsAndConditionsSettings(terms.isShowTermsAndConditions(),terms.getTermsAndConditions());
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
	 * Saves the expiration value of a share.
	 * 
	 * @param bs
	 * @param request
	 * @param shareId
	 * @param expirationValue
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData saveShareExpirationValue( AllModulesInjected bs, HttpServletRequest request, Long shareId, ShareExpirationValue expirationValue ) throws GwtTeamingException
	{
		GwtServerProfiler gsp = GwtServerProfiler.start( m_logger, "GwtShareHelper.saveShareExpirationValue()" );
		try
		{
			boolean reply = false;

			// Do we have what we need to modify the share's
			// expiration?
			if ( ( null != shareId ) && ( null != expirationValue ) && expirationValue.isValid() )
			{
				// Yes!  Can we access the ShareItem?
				SharingModule sm = bs.getSharingModule();
				ShareItem si = sm.getShareItem( shareId );
				if ( null != si )
				{
					// Yes!  Create a modified ShareItem...
					EntityId eid = buildEntityIdFromEntityIdentifier( bs, si.getSharedEntityIdentifier() );
					ShareItem newSI = buildPublicLinkShareItem( bs, GwtServerHelper.getCurrentUserId(), eid, expirationValue, si.getComment() );
					newSI.setStartDate( si.getStartDate() );
					
					// ...and write out the change.
					sm.modifyShareItem( newSI, shareId );
					reply = true;
				}
			}
			
			// ...and return a BooleanRpcResponseData containing true.
			return new BooleanRpcResponseData( reply );
		}
		
		catch ( Exception e )
		{
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtShareHelper.saveShareExpirationValue( SOURCE EXCEPTION ):  " );
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
			// Save the ShareLists...
			ShareLists shareLists = ((null == gwtShareLists) ? new ShareLists() : gwtShareListsToShareLists(gwtShareLists));
			SharingModule sm = bs.getSharingModule();
			sm.setShareLists( shareLists );
			
			// ...and return a BooleanRpcResponseData containing true.
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
	public static ValidateShareListsRpcResponseData validateShareLists( AllModulesInjected bs, HttpServletRequest request, GwtShareLists gwtShareLists ) throws GwtTeamingException
	{
		GwtServerProfiler gsp = GwtServerProfiler.start( m_logger, "GwtShareHelper.validateShareLists()" );
		try
		{
			// Allocate response object we can return.
			ValidateShareListsRpcResponseData reply = new ValidateShareListsRpcResponseData(new ArrayList<ErrorInfo>());

			// Convert the GwtShareLists to a ShareLists that can be
			// used outside the GWT code.
			ShareLists shareLists = (
				( null == gwtShareLists ) ?
					new ShareLists()      :
					gwtShareListsToShareLists( gwtShareLists ) );

			// Can we get find any external users?
			ProfileModule    pm             = bs.getProfileModule();
			SharingModule    sm             = bs.getSharingModule();
			List<Long>       invalidUserIds = new ArrayList<Long>();
			Collection<User> extUsers       = pm.getAllExternalUsers();
			if ( MiscUtil.hasItems( extUsers ) )
			{
				// Yes!  Scan them.
				for ( User extUser:  extUsers )
				{
					// Is this external user's email address bogus,
					// given the ShareLists?
					String ema = extUser.getEmailAddress();
					if ( MiscUtil.hasString( ema ) && ( ! ( sm.isExternalAddressValid( ema, shareLists ) ) ) )
					{
						// Yes!  Track their user ID.
						invalidUserIds.add( extUser.getId() );
					}
				}
			}

			// Are we tracking the user IDs of external users that
			// can't be shared with, given the ShareLists?
			if ( ! ( invalidUserIds.isEmpty() ) )
			{
				// Yes!  Can we find any shares with those users?
				ShareItemSelectSpec	spec = new ShareItemSelectSpec();
				spec.setRecipients( invalidUserIds, null, null );	// nulls -> No groups or teams.
				List<ShareItem> shareItems = sm.getShareItems( spec );
				if ( MiscUtil.hasItems( shareItems ) )
				{
					// Yes!  Scan the shares.
					for ( ShareItem si:  shareItems )
					{
						// If this share has already been deleted...
						if ( si.isDeleted() )
						{
							// ...skip it.
							continue;
						}
						
						// Add the share's ID to the reply's invalid
						// share IDs list.
						reply.addInvalidShareId( si.getId() );
						
						// We also need to add an indication of the
						// invalid share to the reply's error list.
						// This will be used to confirm with the user
						// that this share will be deleted if the
						// ShareList is saved.
						
						// Get the title for the share...
						String shareTitle = null;
						try
						{
							DefinableEntity	siEntity = sm.getSharedEntity( si );
							if ( null != siEntity )
							{
								shareTitle = siEntity.getTitle();
							}
						}
						catch (Exception e) {}
						if ( ! ( MiscUtil.hasString( shareTitle ) ) )
						{
							shareTitle = ( "ID:  " + si.getSharedEntityIdentifier().getEntityId() );
						}
						
						// ...get the sharer's title...
						User sharer = GwtServerHelper.getResolvedUser( si.getSharerId() );
						String sharerTitle = ( ( null == sharer ) ? "" : Utils.getUserTitle( sharer ) );
						
						// ...and generate the error message.
						String   key;
						String   keyTail;
						String[] patches;
						if ( si.getSharedEntityIdentifier().getEntityType().equals( EntityType.folder ) )
						{
							keyTail = "folder";
						}
						else
						{
							if ( Utils.checkIfFilr() )
							     keyTail = "entry.filr";	// Wording uses 'file'.
							else keyTail = "entry.vibe";	// Wording uses 'entry'.
						}
						if ( MiscUtil.hasString( sharerTitle ) )
						{
							patches = new String[]{ shareTitle, sharerTitle };
							key     = "shareWillBeDeleted.hasUser";
						}
						else
						{
							patches = new String[]{ shareTitle };
							key     = "shareWillBeDeleted.noUser";
						}
						reply.addError( NLT.get( ( key + "." + keyTail ), patches ) );
					}
				}
			}
			
			// If we get here, reply refers to a
			// ValidateShareListsRpcResponseData containing the
			// validation results.  Return it.
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
					"GwtShareHelper.validateShareLists( SOURCE EXCEPTION ):  " );
		}
		
		finally
		{
			gsp.stop();
		}
	}
	
	/**
	 * Deletes shares with the specified IDs.
	 * 
	 * @param bs
	 * @param request
	 * @param shareIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData deleteShares( AllModulesInjected bs, HttpServletRequest request, List<Long> shareIds ) throws GwtTeamingException
	{
		GwtServerProfiler gsp = GwtServerProfiler.start( m_logger, "GwtShareHelper.deleteShares()" );
		try
		{
			// Allocate response object we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
			
			// Do we have any the IDs of any shares that have to be
			// deleted?
			if ( MiscUtil.hasItems( shareIds ) )
			{
				// Yes!  Scan them...
				SharingModule sm = bs.getSharingModule();
				for ( Long shareId:  shareIds ) {
					try
					{
						// ...and delete the corresponding share.
						sm.deleteShareItem( shareId );
					}
					catch (Exception e)
					{
						addDeleteShareErrorToErrorList( bs, e, reply, shareId );
						GwtLogHelper.error( m_logger, "GwtShareHelper.deleteShares( EXCEPTION ):  ", e );
					}
				}
			}
			
			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing the results of the
			// delete.  Return it.
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
					"GwtShareHelper.deleteShares( SOURCE EXCEPTION ):  " );
		}
		
		finally
		{
			gsp.stop();
		}
	}

	/*
	 * Given an exception deleting a share, adds information about the
	 * error to an ErrorListRpcResponseData.
	 */
	private static void addDeleteShareErrorToErrorList( AllModulesInjected bs, Exception deleteEx, ErrorListRpcResponseData reply, Long shareId )
	{
		// Can we access the share?
		SharingModule sm = bs.getSharingModule();
		ShareItem share;
		try
		{
			share = sm.getShareItem( shareId );
		}
		catch (Exception e)
		{
			share = null;
		}
		if ( null == share )
		{
			// No!  Generate an error that it couldn't be deleted.
			reply.addError( NLT.get( "deleteShareError.UnknownShare", new String[]{ String.valueOf( shareId ) } ) );
		}
		
		else
		{
			// Yes, we have access to the share!  Get the title for
			// the share...
			String shareTitle = null;
			try
			{
				DefinableEntity	siEntity = sm.getSharedEntity( share );
				if ( null != siEntity )
				{
					shareTitle = siEntity.getTitle();
				}
			}
			catch (Exception e) {}
			if ( ! ( MiscUtil.hasString( shareTitle ) ) )
			{
				shareTitle = ( "ID:  " + share.getSharedEntityIdentifier().getEntityId() );
			}
			
			// ...get the sharer's title...
			User sharer = GwtServerHelper.getResolvedUser( share.getSharerId() );
			String sharerTitle = ( ( null == sharer ) ? "" : Utils.getUserTitle( sharer ) );
			if ( ! ( MiscUtil.hasString( sharerTitle )))
			{
				shareTitle = NLT.get("deleteShareError.UnknownUser");
			}
			
			// ...and generate the error message.
			String key;
			if ( deleteEx instanceof AccessControlException ) key = "deleteShareError.AccssControlException";
			else                                              key = "deleteShareError.OtherException";
			reply.addError( NLT.get( key, new String[]{ shareTitle, sharerTitle } ) );
		}
	}

	/**
	 * Returns a PublicLinksRpcResponseData object containing the
	 * public links for the specified entities.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static PublicLinksRpcResponseData getPublicLinks( AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds ) throws GwtTeamingException
	{
		GwtServerProfiler gsp = GwtServerProfiler.start( m_logger, "GwtShareHelper.getPublicLinks()" );
		try
		{
			PublicLinksRpcResponseData reply = new PublicLinksRpcResponseData();

			// Do we have any entities to get the public links from?
			if ( MiscUtil.hasItems( entityIds ) )
			{
				// Yes!  Prepare the common things we need to build the
				// public links...
				FolderModule fm = bs.getFolderModule();
				SharingModule sm = bs.getSharingModule();
				Long userId = GwtServerHelper.getCurrentUserId();
				ShareExpirationValue expires = new ShareExpirationValue();
				expires.setType( ShareExpirationType.NEVER );
				
				// ...and scan them.
				for ( EntityId eid:  entityIds )
				{
					// We'll default to using the entity's ID in any
					// error we generate until we have its title.
					String feTitle = String.valueOf( eid.getEntityId() );
					
					// Is this entity an en entry?
					if ( ! ( eid.isEntry() ) )
					{
						// No!  This should never happen.  Generate an
						// error and skip it.
						reply.addError( NLT.get( "publicLink.internalError.NotAnEntry", new String[]{ feTitle } ) );
						continue;
					}

					try
					{
						// Are there any public link shares defined on
						// this entity?
						List<ShareItem> plShares = getPublicLinkShareItems( bs, userId, eid );
						if ( ! ( MiscUtil.hasItems( plShares ) ) )
						{
							// No!  Can we get the name of the primary
							// file attached to this entry?
							FolderEntry fe = fm.getEntry( null, eid.getEntityId() );
							feTitle = fe.getTitle();
							String fName = MiscUtil.getPrimaryFileName(fe);
							if ( ! ( MiscUtil.hasString( fName ) ) )
							{
								// No!  Then we can't build links for it.
								// Generate an error and skip it.
								reply.addError( NLT.get( "publicLink.error.NoFile", new String[]{ feTitle } ) );
								continue;
							}
							
							// Generate the public link 'share' on the
							// entry...
							ShareItem si = buildPublicLinkShareItem( bs, userId, eid, expires, "" );
							if ( null == si )
							{
								reply.addError( NLT.get( "publicLink.error.CantShare", new String[]{ feTitle } ) );
								continue;
							}
							sm.addShareItem( si );
							
							// ...and add it to the list of public link
							// ...shares.
							plShares.add( si );
						}

						// Scan the public link shares on this entry.
						for ( ShareItem si:  plShares )
						{
							// Can we get the name of the primary file
							// attached to this entry?
							FolderEntry fe = ((FolderEntry) sm.getSharedEntity( si ));
				            feTitle = fe.getTitle();
							String fName = MiscUtil.getPrimaryFileName(fe);
							if ( ! ( MiscUtil.hasString( fName ) ) )
							{
								// No!  Then we can't build links for it.
								// Generate an error and skip it.
								reply.addError( NLT.get( "publicLink.error.NoFile", new String[]{ feTitle } ) );
								continue;
							}
							
							// ...construct a download link URL for it...
							Long   siId = si.getId();
							String siPK = si.getPassKey();
							String downloadUrl = WebUrlUtil.getSharedPublicFileUrl( request, siId, siPK, WebKeys.URL_SHARE_PUBLIC_LINK, fName );
	
							// ...and if we support viewing it as HTML,
							// ...construct a view link URL for it...
							String viewUrl;
							if ( GwtViewHelper.supportsViewAsHtml( fName ) )
							     viewUrl = WebUrlUtil.getSharedPublicFileUrl( request, siId, siPK, WebKeys.URL_SHARE_PUBLIC_LINK_HTML, fName );
							else viewUrl = null;
							
							// ...get the date it was shared...
							Date   sharedOnDate = si.getStartDate();
							String sharedOn     = GwtServerHelper.getDateTimeString( sharedOnDate, DateFormat.MEDIUM, DateFormat.SHORT );
							
							// ...get the share's expiration...
							String expiration;
							Date expirationDate = si.getEndDate();
							if ( null == expirationDate )
							     expiration = null;
							else expiration = GwtServerHelper.getDateTimeString( expirationDate, DateFormat.MEDIUM, DateFormat.SHORT );
							
							// ...and add the public link information to
							// ...the reply.
							reply.addPublicLink(
								eid,
								si.getId(),
								feTitle,
								fe.getParentBinder().getPathName(),
								FileIconsHelper.getFileIconFromFileName(
									fName,
									IconSize.SMALL ),
								downloadUrl,
								viewUrl,
								si.getComment(),
								sharedOn,
								si.isExpired(),
								expiration,
								buildShareExpiratioNValueFromShareItem( si ) );
						}
					}
					
					catch ( Exception e )
					{
						// No!  Add an error to the error list...
						String messageKey;
						if (e instanceof AccessControlException) messageKey = "publicLink.error.AccssControlException";
						else                                     messageKey = "publicLink.error.OtherException";
						reply.addError( NLT.get( messageKey, new String[]{ feTitle } ) );
						
						// ...and log it.
						GwtLogHelper.error( m_logger, "GwtShareHelper.getPublicLinks( Entry:  '" + feTitle + "', EXCEPTION ):  ", e );
					}
				}
			}
			
			// If we get here, reply refers to a
			// PublicLinksRpcResponseData containing the results of obtaining
			// the public links for the entities.  Return it.
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
					"GwtShareHelper.getPublicLinks( SOURCE EXCEPTION ):  " );
		}
		
		finally
		{
			gsp.stop();
		}
	}
	
	/**
	 * Returns a MailToPublicLinksRpcResponseData object containing the
	 * public links for the specified entity.
	 * 
	 * @param bs
	 * @param request
	 * @param eid
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static MailToPublicLinksRpcResponseData getMailToPublicLinks( AllModulesInjected bs, HttpServletRequest request, EntityId eid ) throws GwtTeamingException
	{
		GwtServerProfiler gsp = GwtServerProfiler.start( m_logger, "GwtShareHelper.getMailToPublicLinks()" );
		try
		{
			// Create a MailToPublicLinksRpcResponseData we can return.
			MailToPublicLinksRpcResponseData reply = new MailToPublicLinksRpcResponseData();

			// Prepare the common things we need to build public links.
			User user                    = GwtServerHelper.getCurrentUser();
			Long userId                  = user.getId();
			ShareExpirationValue expires = new ShareExpirationValue();
			expires.setType( ShareExpirationType.NEVER );
			
			// We'll default to using the entity's ID in any
			// error we generate until we have its title.
			String feTitle = String.valueOf( eid.getEntityId() );
			
			// Is this entity an en entry?
			if ( ! ( eid.isEntry() ) )
			{
				// No!  This should never happen.  Generate an
				// error and skip it.
				reply.setError( NLT.get( "mailtoPublicLink.internalError.NotAnEntry", new String[]{ feTitle } ) );
				return reply;
			}

			try
			{
				// Can we get the name of the primary file
				// attached to this entry?
				FolderEntry fe      = bs.getFolderModule().getEntry( null, eid.getEntityId() );
				            feTitle = fe.getTitle();
				String      fName   = MiscUtil.getPrimaryFileName(fe);
				if ( ! ( MiscUtil.hasString( fName ) ) )
				{
					// No!  Then we can't build links for it.
					// Generate an error and skip it.
					reply.setError( NLT.get( "mailtoPublicLink.error.NoFile", new String[]{ feTitle } ) );
					return reply;
				}

				// Synthesize the subject for the mail.
				String subject = NLT.get( "mailtoPublicLink.subject", new String[] { user.getTitle(), feTitle } );
				reply.setSubject(subject);

				// Does the current user have any public shares already
				// available for this file?
				List<ShareItem> plShares = getPublicLinkShareItems( bs, userId, eid );
				if ( ! ( MiscUtil.hasItems( plShares )))
				{
					// No!  Generate a public link 'share' on the
					// entry...
					ShareItem si = buildPublicLinkShareItem( bs, userId, eid, expires, "" );
					if ( null == si )
					{
						reply.setError( NLT.get( "mailtoPublicLink.error.CantShare", new String[]{ feTitle } ) );
						return reply;
					}
					bs.getSharingModule().addShareItem( si );

					// ...and track it.
					if ( null == plShares )
					{
						plShares = new ArrayList<ShareItem>();
					}
					plShares.add( si );
				}

				// Scan the public shares on this file...
				for ( ShareItem plShare:  plShares )
				{
					// ...construct a download link URL for it...
					Long   siId = plShare.getId();
					String siPK = plShare.getPassKey();
					String downloadUrl = WebUrlUtil.getSharedPublicFileUrl( request, siId, siPK, WebKeys.URL_SHARE_PUBLIC_LINK, fName );
	
					// ...and it we support viewing it as HTML,
					// ...construct a view link URL for it...
					String viewUrl;
					if ( GwtViewHelper.supportsViewAsHtml( fName ) )
					     viewUrl = WebUrlUtil.getSharedPublicFileUrl( request, siId, siPK, WebKeys.URL_SHARE_PUBLIC_LINK_HTML, fName );
					else viewUrl = null;

					// ...get the date it was shared...
					Date   sharedOnDate = plShare.getStartDate();
					String sharedOn     = GwtServerHelper.getDateTimeString( sharedOnDate, DateFormat.MEDIUM, DateFormat.SHORT );
					
					// ...if the share expires, return its expiration
					// ...date...
					String expiration;
					Date expirationDate = plShare.getEndDate();
					if ( null == expirationDate )
					     expiration = null;
					else expiration = GwtServerHelper.getDateTimeString( expirationDate, DateFormat.MEDIUM, DateFormat.SHORT );

					// ...and add the mail to public link information
					// ...to the reply.
					PublicLinkInfo plLink = new PublicLinkInfo(
						plShare.getId(),
						feTitle,
						fe.getParentBinder().getPathName(),
						FileIconsHelper.getFileIconFromFileName(
							fName,
							IconSize.SMALL ),
						downloadUrl,
						viewUrl,
						plShare.getComment(),
						sharedOn,
						plShare.isExpired(),
						expiration,
						buildShareExpiratioNValueFromShareItem( plShare ) );
					reply.addMailToPublicLink( plLink );
				}
			}
			
			catch ( Exception e )
			{
				// No!  Add an error to the error list...
				String messageKey;
				if (e instanceof AccessControlException) messageKey = "mailtoPublicLink.error.AccssControlException";
				else                                     messageKey = "mailtoPublicLink.error.OtherException";
				reply.setError( NLT.get( messageKey, new String[]{ feTitle } ) );
				
				// ...and log it.
				GwtLogHelper.error( m_logger, "GwtShareHelper.getMailToPublicLinks( Entry:  '" + feTitle + "', EXCEPTION ):  ", e );
			}
			
			// If we get here, reply refers to a
			// MailToPublicLinksRpcResponseData containing the results
			// of obtaining the mail to public links for the entity. 
			// Return it.
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
					"GwtShareHelper.getMailToPublicLinks( SOURCE EXCEPTION ):  " );
		}
		
		finally
		{
			gsp.stop();
		}
	}
	
	/**
	 * Returns share rights the given user has been granted at the zone level.
	 * 
	 * @param ami
	 * @param princiaplId
	 * 
	 * @return
	 */
	public static PerEntityShareRightsInfo getUserZoneShareSettings(
		AllModulesInjected ami,
		Long principalId ) throws GwtTeamingException
	{
    	PerEntityShareRightsInfo shareRights;
    	Long zoneId;
    	ZoneConfig zoneConfig;

    	shareRights = new PerEntityShareRightsInfo( false, false, false, false, false,false, false, false, false );

		zoneId = RequestContextHolder.getRequestContext().getZoneId();
		zoneConfig = ami.getZoneModule().getZoneConfig( zoneId );

		m_accessControlManager = getAccessControlManager();
		if ( m_accessControlManager == null )
		{
			m_logger.error( "In GwtShareHelper.getUserZoneShareSettings(), unable to get the access control manager" );
			return shareRights;
		}

		try
		{
			ArrayList<Long> ids;
			SortedSet<Principal> principals;
			
			ids = new ArrayList<Long>();
			ids.add( principalId );
			principals = ami.getProfileModule().getPrincipals( ids );
			if ( principals != null && principals.size() == 1 )
			{
				Principal principal = null;

				principal = principals.first();
				if ( principal instanceof User )
				{
			    	User user = null;

			    	user = (User) principal;

			    	try
					{
						// Is share forwarding enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_SHARING_FORWARD );
						shareRights.setAllowForwarding( true );
					}
					catch ( AccessControlException acEx )
					{
					}
			    	
			    	try
					{
						// Is share forwarding enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_SHARING_FORWARD );
						shareRights.setAllowFolderForwarding( true );
					}
					catch ( AccessControlException acEx )
					{
					}

					try
					{
						// Is sharing with internal users enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_SHARING_INTERNAL );
						shareRights.setAllowInternal( true );
					}
					catch ( AccessControlException acEx )
					{
					}

					try
					{
						// Is sharing with external users enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_SHARING_EXTERNAL );
						shareRights.setAllowExternal( true );
					}
					catch ( AccessControlException acEx )
					{
					}

					try
					{
						// Is sharing with the public enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_SHARING_PUBLIC );
						shareRights.setAllowPublic( true );
					}
					catch ( AccessControlException acEx )
					{
					}
					
					try
					{
						// Is sharing with internal users enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_SHARING_INTERNAL );
						shareRights.setAllowFolderInternal( true );
					}
					catch ( AccessControlException acEx )
					{
					}

					try
					{
						// Is sharing with external users enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_SHARING_EXTERNAL );
						shareRights.setAllowFolderExternal( true );
					}
					catch ( AccessControlException acEx )
					{
					}

					try
					{
						// Is sharing with the public enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_SHARING_PUBLIC );
						shareRights.setAllowFolderPublic( true );
					}
					catch ( AccessControlException acEx )
					{
					}
					
					try
					{
						// Is sharing public links enabled at the zone level for this user?
						m_accessControlManager.checkOperation( user, zoneConfig, WorkAreaOperation.ENABLE_LINK_SHARING );
						shareRights.setAllowPublicLinks( true );
					}
					catch ( AccessControlException acEx )
					{
					}
				}
				else if ( principal instanceof Group )
				{
					// I spoke to Peter today (2/7/2014) and this is what he said.
					// We have no way of determining what rights a particular group has.  Therefore
					// we will allow the admin to set all rights.  This won't cause a problem because
					// if an individual hasn't been given rights at the zone level they won't be able
					// to perform the share.
					shareRights.setAllowForwarding( true );
					shareRights.setAllowFolderForwarding( true );
					shareRights.setAllowInternal( true );
					shareRights.setAllowExternal( true );
					shareRights.setAllowPublic( true );
					shareRights.setAllowPublicLinks( true );
					shareRights.setAllowFolderInternal(true);
					shareRights.setAllowFolderExternal(true);
					shareRights.setAllowFolderPublic(true);
				}
			}
		}
		catch ( AccessControlException acEx )
		{
			// Nothing to do
		}
		
		return shareRights;
	}

	/**
	 * Returns true if the given sharer has any public links defined on
	 * the entity and false otherwise.
	 * 
	 * @param bs
	 * @param sharerId
	 * @param entityId
	 * 
	 * @return
	 */
	public static boolean hasPublicLinks( AllModulesInjected bs, Long sharerId, EntityId entityId ) {
		List<ShareItem> publicLinks = getPublicLinkShareItems( bs, sharerId, entityId );
		return MiscUtil.hasItems(publicLinks);
	}
	
	/**
	 * Returns true if the user can access the given WorkArea without
	 * factoring in shares and false otherwise.
	 * 
	 * @param bs
	 * @param user
	 * @param wa
	 */
	public static boolean visibleWithoutShares( AllModulesInjected bs, User user, WorkArea wa )
	{
		return bs.getFolderModule().testReadAccess( user, wa, false );	// false -> Don't check access because of sharing.
	}
	
	private static FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}
	
	private static ResourceDriverManager getResourceDriverManager() {
		return (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
	}
}
