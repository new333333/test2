/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.sharing;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.ShareLists;

/**
 * <code>SharingModule</code> provides "Share with Me" related operations
 * 
 * @author Peter Hurley
 */
public interface SharingModule {
	public enum SharingOperation {
		addShareItem,
		modifyShareItem,
		deleteShareItem
	}

	/**
	 * Enumeration value used to represent the status of sharing with a
	 * specific external email address.
	 */
	public enum ExternalAddressStatus {
		failsBlacklistDomain,
		failsBlacklistEMA,
		failsWhitelist,
		
		valid;

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean failsBlackListDomain() {return failsBlacklistDomain.equals(this);                        }
		public boolean failsBlackListEMA()    {return failsBlacklistEMA.equals(   this);                        }
		public boolean failsBlackList()       {return (this.failsBlackListDomain() || this.failsBlackListEMA());}
		public boolean failsWhitelist()       {return failsWhitelist.equals(      this);                        }
		public boolean isInvalid()            {return (!(isValid()));                                           }
		public boolean isValid()              {return valid.equals(               this);                        }
	}

	public static class EntityShareRights {
		private ShareItem.Role topRole;
		private ShareItem.Role maxGrantRole;

		public EntityShareRights(ShareItem.Role topRole, ShareItem.Role maxGrantRole) {
			this.topRole = topRole;
			this.maxGrantRole = maxGrantRole;
		}

		public ShareItem.Role getMaxGrantRole() {
			return maxGrantRole;
		}

		public ShareItem.Role getTopRole() {
			return topRole;
		}
	}


	public void checkAccess(ShareItem shareItem, SharingOperation operation)
    	throws AccessControlException;
	public void checkAccess(ShareItem shareItem, EntityIdentifier entityIdentifier, SharingOperation operation)
		throws AccessControlException;
	
	public boolean testAccess(ShareItem shareItem, SharingOperation operation);
	public boolean testAccess(ShareItem shareItem, EntityIdentifier entityIdentifier, SharingOperation operation);
	
	public boolean testAddShareEntity(DefinableEntity de);
	public boolean testAddShareEntityInternal(DefinableEntity de);
	public boolean testAddShareEntityExternal(DefinableEntity de);
	public boolean testAddShareEntityPublic(DefinableEntity de);
	public boolean testAddShareEntityPublicLinks(DefinableEntity de);
	public boolean testShareEntityForward(DefinableEntity de);

	public boolean isShareForwardingEnabled();
	public boolean isSharingEnabled();
	public boolean isSharingPublicLinksEnabled();

	public EntityShareRights calculateHighestEntityShareRights(EntityIdentifier entityId);

	/**
     * Add a new share by adding a share item.
     * 
     * @param shareItem
     */
    public void addShareItem(ShareItem shareItem);

    /**
     * Returns true if there are public shares that are active and
     * false otherwise.
     * 
     * @return
     */
    public boolean arePublicSharesActive();
    
    /**
     * Modify an existing share by creating a new snapshot. The previous snapshot becomes an archive. 
     * 
     * @param latestShareItem the new snapshot being added
     * @param previousShareItemId the ID of the previous snapshot of the share
     */
    public ShareItem modifyShareItem(ShareItem latestShareItem, Long previousShareItemId);
    
    /**
     * Delete an existing share item.
     * 
     * @param shareItemId
     */
    public void deleteShareItem(Long shareItemId);
    
 	/**
 	 * Get a share item by id.
 	 * If the object is not found, it throws <code>NoShareItemByTheIdException</code>.
 	 * 
 	 * @param shareItemId
 	 * @return
 	 * @throws NoShareItemByTheIdException
 	 */
 	public ShareItem getShareItem(Long shareItemId) throws NoShareItemByTheIdException;
 	
 	/**
 	 * Get a list of share items by their ids.
 	 * Unlike <code>getShareItem</code> method, this method does not return error when
 	 * not all of the objects are found by the specified ids. Instead, it will only return
 	 * those objects successfully found.
 	 * 
 	 * @param shareItemIds
 	 * @return
 	 */
 	public List<ShareItem> getShareItems(Collection<Long> shareItemIds); 

 	/** 
 	 * Find a list of <code>ShareItem</code> meeting the specified selection criteria.
 	 * 
 	 * @param selectSpec
 	 * @return
 	 */
 	public List<ShareItem> getShareItems(ShareItemSelectSpec selectSpec);

 	/**
 	 * Get a shared entity associated with the share item.
 	 * 
 	 * @param shareItem
 	 * @return
 	 */
 	public DefinableEntity getSharedEntity(ShareItem shareItem);
 	
 	/**
 	 * Get a shared entity associated with the share item without checking ACLs.  Useful when we need to
     * look up an entity for a user in order to provide share information after the share has expired.
 	 *
 	 * @param shareItem
 	 * @return
 	 */
 	public DefinableEntity getSharedEntityWithoutAccessCheck(ShareItem shareItem);

 	/**
 	 * Get the recipient associated with the share item.
 	 * 
 	 * @param shareItem
 	 * @return
 	 */
 	public DefinableEntity getSharedRecipient(ShareItem shareItem);

 	/**
 	 * Handles expired share item.
 	 * 
 	 * IMPORTANT: This is used by the background job only. 
 	 * 
 	 * @param shareItem
 	 */
 	public void handleExpiredShareItem(ShareItem shareItem);

    /**
     * Hides the binders or folder entries from the logged in user's Shared With Me or Shared By Me list.
     * @param ids       The IDs of the shared entities
     * @param recipient true if the user is the recipient of the share (Shared With Me),
     *                  false if the user is the sharer (Shared By Me).
     */
    public void hideSharedEntitiesForCurrentUser(Collection<EntityIdentifier> ids, boolean recipient);

    public void unhideSharedEntitiesForCurrentUser(Collection<EntityIdentifier> ids, boolean recipient);

    /**
     * Returns the date when the user last hid or unhid a shared entity in his/her Shared With Me or Shared By Me.
     * @param recipient true for where the user is the recipient of the share (Shared With Me),
     *                  false for where the user is the sharer (Shared By Me).
     * @return
     */
    public Date getHiddenShareModTimeForCurrentUser(boolean recipient);
    
    /**
	 * Returns true if a DefinableEnity is tagged as a hidden share and
	 * false otherwise.
	 * 
     * @param siEntity
     * @param recipient
     * 
     * @return
     */
	public boolean isSharedEntityHidden(DefinableEntity siEntity, boolean recipient);


    /**
     * Returns true if the given email address is valid for sharing
     * with based on the current sharing blacklist/whitelist.
     * 
     * @param ema
     * 
     * @return
     */
    public boolean isExternalAddressValid(String ema);
    public boolean isExternalAddressValid(String ema, ShareLists shareLists);
    
    /**
     * Returns an ExternalAddressStatus value for the status of sharing
     * with the given email address based on the current sharing
     * blacklist/whitelist.
     * 
     * @param ema
     * 
     * @return
     */
    public ExternalAddressStatus getExternalAddressStatus(String ema);
    public ExternalAddressStatus getExternalAddressStatus(String ema, ShareLists shareLists);

    /**
     * Returns the ShareLists object stored in the ZoneConfig.
     * 
     * @return
     */
    public ShareLists getShareLists();

    /**
     * Stores/updates a ShareLists object in the ZoneConfig.
     * 
     * @param shareLists
     */
    public void setShareLists(ShareLists shareLists);
    
    /**
     * Validates that the ShareItem's in a List<ShareItem> refer to a
     * valid definable entity.  If they don't, they are deleted from
     * the SS_ShareItem table and removed from the list.
     * 
     * Will Optionally:
     * 1) Populate a List<DefinableEntity> of the valid shared
     *    entities.
     * 2) Validate the user has access to the shared entities
     *    that the List<DefinableEntity> is populated with.
     * 
     * @param shares
     * @param sharedEntities (optional)
     */
    public void validateShareItems(                  List<ShareItem> shares);
    public void validateShareItems(                  List<ShareItem> shares, List<DefinableEntity> sharedEntities);
    public void validateShareItemsWithoutAccessCheck(List<ShareItem> shares, List<DefinableEntity> sharedEntities);

    /**
     * Scans a List<DefinableEntity> for the one matching an
     * EntityIdentifier.  If found, it's returned.  Otherwise, null is
     * returned.
     * 
     * @param sharedEntities
     * @param eid
     * 
     * @return
     */
    public DefinableEntity findSharedEntityInList(List<DefinableEntity> sharedEntities, EntityIdentifier eid);
}
