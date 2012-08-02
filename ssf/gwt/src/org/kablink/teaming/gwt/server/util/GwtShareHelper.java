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

import java.text.DateFormat;
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
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.domain.ShareItem.RightSet;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.PermaLinkUtil;


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
	private static long MILLISEC_IN_A_DAY = 86400000; 
	

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
	 * See if the user can share the given entities with external users.
	 */
	public static boolean canShareWithExternalUsers (
		AllModulesInjected ami,
		List<EntityId> listOfEntityIds )
	{
		ZoneConfig zoneConfig;
		
		// Is sharing with external users turned on at the zone level?
		zoneConfig = ami.getZoneModule().getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
		return zoneConfig.isExternalUserEnabled();
	}

	/**
	 * Return the RightSet that corresponds to the "Contributor" rights
	 */
	private static RightSet getContributorRightSet()
	{
		if ( m_contributorRightSet == null )
		{
			m_contributorRightSet = ShareItem.Role.CONTRIBUTOR.getRightSet();
		}
		
		return m_contributorRightSet;
	}
	
	/**
	 * Return an EntityIdentifier for the given EntityId
	 */
	private static EntityIdentifier getEntityIdentifierFromEntityId( EntityId entityId )
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
				gwtShareItem.setComments( nextShareItem.getComment() );
				gwtShareItem.setEntityId( entityId );
				gwtShareItem.setEntityName( getEntityName( ami, entityId ) );
				gwtShareItem.setId( nextShareItem.getId() );
				gwtShareItem.setIsExpired( nextShareItem.isExpired() );
				gwtShareItem.setRecipientId( nextShareItem.getRecipientId() );

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
						
						name = user.getTitle();
						gwtShareItem.setRecipientName( name );
						
						if ( user.getIdentitySource() == User.IDENTITY_SOURCE_EXTERNAL )
							gwtShareItem.setRecipientType( GwtRecipientType.EXTERNAL_USER );
						else
							gwtShareItem.setRecipientType( GwtRecipientType.USER );
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
	 * Return the RightSet that corresponds to the "Owner" rights
	 */
	private static RightSet getOwnerRightSet()
	{
		if ( m_ownerRightSet == null )
		{
			m_ownerRightSet = ShareItem.Role.OWNER.getRightSet();
		}
		
		return m_ownerRightSet;
	}
	
	/**
	 * Return the id of the given user.  If the user is an external user we will see if their
	 * user account has been created.  If it hasn't we will create it.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Long getRecipientId( AllModulesInjected ami, GwtShareItem gwtShareItem )
	{
		Long id;
		
		id = null;
		
		if ( gwtShareItem.getRecipientType() == GwtRecipientType.EXTERNAL_USER )
		{
			User user;
			String recipientName;
			ProfileModule profileModule;

			profileModule = ami.getProfileModule();
			
			recipientName = gwtShareItem.getRecipientName();
			
			try
			{
				// Does this external user already have an account in Vibe?
				user = profileModule.getUser( recipientName );
				if ( user != null )
				{
					id = user.getId();
				}
			}
			catch ( Exception ex )
			{
				HashMap updates;
				
				// If we get here the external user does not have a Vibe account yet.
				// Create one.
				updates = new HashMap();
				updates.put( ObjectKeys.FIELD_USER_EMAIL, recipientName );
				updates.put( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, recipientName );
 				user = profileModule.addUserFromPortal(
 													User.IDENTITY_SOURCE_EXTERNAL,
 													recipientName,
 													null,
 													updates,
 													null );
 				
 				id = user.getId();
			}
		}
		else
		{
			id = gwtShareItem.getRecipientId();
		}
		
		return id;
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
	 * Copy the information from the given GwtShareItem object into a
	 * ShareItem object and return that object.
	 */
	private static ShareItem getShareItemInfo(
		AllModulesInjected ami,
		User sharer,
		ShareItem shareItem,
		GwtShareItem gwtShareItem )
	{
		Date endDate = null;
		RecipientType recipientType;
		Long recipientId;
		RightSet rightSet;
		String comments;
		EntityId entityId;
		EntityIdentifier entityIdentifier;
		int daysToExpire = -1;

		if ( ami == null || gwtShareItem == null )
		{
			m_logger.error( "invalid parameter passed to getShareItemInfo()" );
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
				HistoryStamp historyStamp = null;
				long milliSecToExpire;
				Date creationDate;

				daysToExpire = expirationValue.getValue().intValue();
				milliSecToExpire = daysToExpire * MILLISEC_IN_A_DAY;

				// Calculate the end date based on the days-to-expire.
				if ( shareItem != null )
					historyStamp = shareItem.getCreation();
				
				if ( historyStamp != null )
					creationDate = historyStamp.getDate();
				else
					creationDate = new Date();

				endDate = new Date( creationDate.getTime() + milliSecToExpire );
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

		comments = gwtShareItem.getComments();
		
		// Get the appropriate RightSet
		rightSet = getRightSetFromShareRights( gwtShareItem.getShareRights() );
		
		// Should we create a new ShareItem object?
		if ( shareItem == null )
		{
			// Yes
			shareItem = new ShareItem(
								sharer,
								entityIdentifier,
								comments,
								endDate,
								recipientType,
								recipientId,
								rightSet );
			
			shareItem.setDaysToExpire( daysToExpire );
		}
		else
		{
			// No, just update the given ShareItem.
			shareItem.setComment( comments );
			shareItem.setRightSet( rightSet );
			shareItem.setEndDate( endDate );
			shareItem.setDaysToExpire( daysToExpire );
		}
		
		return shareItem;
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
		SharingModule sharingModule;

		sharingInfo = new GwtSharingInfo();
		
		// See if the user has rights to share the given entities with an external user.
		sharingInfo.setCanShareWithExternalUsers( canShareWithExternalUsers( ami, listOfEntityIds ) );

		currentUser = GwtServerHelper.getCurrentUser();

		if ( listOfEntityIds == null || listOfEntityIds.size() == 0 )
		{
			m_logger.error( "In GwtShareHelper.getSharingInfo(), listOfEntityIds is null or empty" );
			return sharingInfo;
		}
		
		sharingModule = (SharingModule)SpringContextUtil.getBean( "sharingModule" );
		if ( sharingModule == null )
		{
			m_logger.error( "In GwtShareHelper.getSharingInfo(), sharingModule is null" );
			return sharingInfo;
		}
		
		// For each given entity, get the sharing information.
		for (EntityId nextEntityId : listOfEntityIds)
		{
			ArrayList<GwtShareItem> listOfGwtShareItems;
			String entityName;
			
			// Get the name of the entity
			entityName = getEntityName( ami, nextEntityId );
			sharingInfo.setEntityName( nextEntityId, entityName );
			
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
				ArrayList<Long> userAL;
				Set<User> userSet;
				User[] users;
				
				userAL = new ArrayList<Long>();
				userAL.add( shareItem.getRecipientId() );
				userSet = ami.getProfileModule().getUsers( userAL );
				users = userSet.toArray( new User[0] );
				if ( users.length == 1 )
				{
					User user;
					
					user = users[0];
					return user;
				}
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
	 * Return the RightSet that corresponds to the "View" rights
	 */
	private static RightSet getViewRightSet()
	{
		if ( m_viewRightSet == null )
		{
			m_viewRightSet = ShareItem.Role.VIEW.getRightSet();
		}
		
		return m_viewRightSet;
	}

	/**
	 * Send an email to the given recipient
	 */
	@SuppressWarnings("rawtypes")
	private static List sendEmailToRecipient(
		AllModulesInjected ami,
		GwtShareItem shareItem,
		User currentUser )
	{
		List emailErrors;
		Set<Long> principalIds;
		Set<Long> teamIds;
		String comments;
		String title;
		String shortTitle;
		String desc;
		String mailTitle;
		Description body;
		HashSet<String> emailAddress;
		String bccEmailAddress;
		EntityId entityId;
		DefinableEntity sharedEntity;

		if ( ami == null || currentUser == null || shareItem == null )
		{
			m_logger.error( "invalid parameter in sendEmailToRecipient()" );
			return null;
		}
		
		principalIds = new HashSet<Long>();
		teamIds = new HashSet<Long>();
		
		switch ( shareItem.getRecipientType() )
		{
		case GROUP:
		case USER:
			principalIds.add( shareItem.getRecipientId() );
			break;
		
		case TEAM:
			teamIds.add( shareItem.getRecipientId() );
			break;
		
		default:
			m_logger.error( "unknow recipient type in sendEmailToRecipient()" );
			break;
		}
		
		comments = shareItem.getComments();

		entityId = shareItem.getEntityId();
		if ( entityId.isBinder() )
			sharedEntity = ami.getBinderModule().getBinder( entityId.getEntityId() );
		else
			sharedEntity = ami.getFolderModule().getEntry( entityId.getBinderId(), entityId.getEntityId() );
		
		title = sharedEntity.getTitle();
		shortTitle = title;
		
		if ( sharedEntity.getParentBinder() != null )
			title = sharedEntity.getParentBinder().getPathName() + "/" + title;

		// Do NOT use interactive context when constructing permalink for email. See Bug 536092.
		desc = "<a href=\"" + PermaLinkUtil.getPermalinkForEmail( sharedEntity ) + "\">" + title + "</a><br/><br/>" + comments;
		desc += "<br/>";
		// Append when the share expires
		{
			ShareExpirationValue expirationValue;
			
			expirationValue = shareItem.getShareExpirationValue();
			switch ( expirationValue.getExpirationType() )
			{
			case NEVER:
				desc += NLT.get( "share.expires.never" );
				break;
			
			case AFTER_DAYS:
			{
				int daysToExpire;

				daysToExpire = expirationValue.getValue().intValue();
				desc += NLT.get( "share.expires.after", new Object[]{ daysToExpire } );
				break;
			}
			
			case ON_DATE:
			{
				Long endDate;
				
				endDate = expirationValue.getValue();
				if ( endDate != null )
				{
					Date date;
					DateFormat dateFmt;
					String dateText;
					
					date = new Date( endDate );
					dateFmt = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.SHORT, NLT.getTeamingLocale() );
					dateText = dateFmt.format( date );
					
					desc += NLT.get( "share.expires.on", new Object[]{ dateText } );
				}
				break;
			}
			}
		}
		body = new Description( desc );

		mailTitle = NLT.get( "relevance.mailShared", new Object[]{Utils.getUserTitle( currentUser )} );
		mailTitle += " (" + shortTitle +")";

		emailAddress = new HashSet<String>();
		
		//See if this user wants to be BCC'd on all mail sent out
		bccEmailAddress = currentUser.getBccEmailAddress();
		if ( bccEmailAddress != null && !bccEmailAddress.equals("") )
		{
			if ( !emailAddress.contains( bccEmailAddress.trim() ) )
			{
				//Add the user's chosen bcc email address
				emailAddress.add( bccEmailAddress.trim() );
			}
		}
		
		emailErrors = null;
		try
		{
			Map<String,Object> errorMap;
			
			// Send the email notification
			errorMap = ami.getAdminModule().sendMail(
													principalIds,
													teamIds,
													emailAddress,
													null,
													null,
													mailTitle,
													body );
			if ( errorMap != null )
			{
				emailErrors = (List) errorMap.get( ObjectKeys.SENDMAIL_ERRORS );
			}
		}
		catch ( Exception ex )
		{
			m_logger.error( "adminModule.sendMail() threw an exception: " + ex.getMessage() );
		}

		return emailErrors;
	}
		
	/**
	 * Save the given share data. 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
			
			// Does this ShareItem exists?
			shareItemId = nextGwtShareItem.getId();
			if ( shareItemId == null )
			{
				// No, create a ShareItem object
				shareItem = getShareItemInfo( ami, currentUser, null, nextGwtShareItem );

				sharingModule.addShareItem( shareItem );
				sendEmail = true;
			}
			else
			{
				// The ShareItem exists.
				// Was it modified?
				if ( nextGwtShareItem.isDirty() )
				{
					// Yes
					shareItem = sharingModule.getShareItem( shareItemId );
					
					getShareItemInfo( ami, currentUser, shareItem, nextGwtShareItem );
					
					sharingModule.modifyShareItem( shareItem );
					sendEmail = true;
				}
			}
			
			// Are we suppose to send an email to everyone and not just new or modified shares?
			if ( sharingData.getSendEmailToAll() )
			{
				// Yes
				sendEmail = true;
			}

			// Send an email to this recipient
			if ( sendEmail )
			{
				List entityEmailErrors = null;
				
				// Send an email to each of the recipients
				entityEmailErrors = sendEmailToRecipient(
														ami,
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
			results.setErrors( (String[])emailErrors.toArray( new String[0]) );
		}
		
		return results;
	}
}
