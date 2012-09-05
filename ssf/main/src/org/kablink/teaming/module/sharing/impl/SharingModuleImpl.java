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
package org.kablink.teaming.module.sharing.impl;

import java.util.Collection;
import java.util.List;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * This module gives us the transaction semantics to deal with the "Shared with Me" features.  
 *
 * @author Peter Hurley
 *
 */
public class SharingModuleImpl extends CommonDependencyInjection implements SharingModule {

	private FolderModule folderModule;
	private BinderModule binderModule;
	private ProfileModule profileModule;
	
    @Override
	public void checkAccess(ShareItem shareItem, SharingOperation operation)
	    	throws AccessControlException {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		EntityIdentifier entityIdentifier = shareItem.getSharedEntityIdentifier();
		AccessControlManager accessControlManager = getAccessControlManager();
		if (accessControlManager == null) {
			accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		}
    	
		switch (operation) {
		case addShareItem:
			//Make sure sharing is enabled at the zone level for this type of user
			accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING);

			//Check that the user is either the entity owner, or has the right to share entities
			if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
				if (accessControlManager.testOperation(user, fe, WorkAreaOperation.ALLOW_SHARING)) {
					return;
				}
				//User didn't have AllowSharing, so now check if user is owner of the entity
				if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					//This is the owner of the entry. Allow the sharing.
					return;
				}
			} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
					entityIdentifier.getEntityType().equals(EntityType.workspace)) {
				Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
				if (accessControlManager.testOperation(user, binder, WorkAreaOperation.ALLOW_SHARING)) {
					return;
				}
				//User didn't have AllowSharing, so now check if user is owner of the entity
				if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
					//This is the owner of the binder. Allow the sharing.
					return;
				}
			}
			break;
		case modifyShareItem:
			//The share creator and the entity owner can modify a shareItem
			if (user.getId().equals(shareItem.getSharerId())) {
				//The user is the creator of the share
				return;
			}
			//Check if this is the owner of the entity
			if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
				if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					//This is the owner of the entry. Allow the modification.
					return;
				}
			} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
					entityIdentifier.getEntityType().equals(EntityType.workspace)) {
				Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
				if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
					//This is the owner of the binder. Allow the modification.
					return;
				}
			}
			break;
		case deleteShareItem:
			//The share creator, the entity owner, or the site admin can delete a shareItem
			if (user.getId().equals(shareItem.getSharerId())) {
				//The user is the creator of the share
				return;
			}
			//Check if this is the owner of the entity
			if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
				if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					//This is the owner of the entry. Allow the modification.
					return;
				}
			} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
					entityIdentifier.getEntityType().equals(EntityType.workspace)) {
				Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
				if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
					//This is the owner of the binder. Allow the modification.
					return;
				}
			}
			//Check for site administrator
			if (accessControlManager.testOperation(user, zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION)) {
				//This is a site administrator
				return;
			}
			break;
		default:
			throw new NotSupportedException(operation.toString(),
					"checkAccess");
		}
		//No access was found
		throw new AccessControlException();
	}
    
    @Override
	public boolean testAccess(ShareItem shareItem, SharingOperation operation) {
    	try {
    		checkAccess(shareItem, operation);
    		return true;
    	}
    	catch (AccessControlException ac) {
    		return false;
    	}
    }

    /**
     * Returns true if the current user can share the given
     * DefinableEntity and false otherwise.
     * 
     * @param de
     * 
     * @return
     */
    @Override
	public boolean testAddShareEntity(DefinableEntity de) {
		boolean reply = false;
		try {
			// Is sharing enabled at the zone level for this type of user.
	    	Long					zoneId               = RequestContextHolder.getRequestContext().getZoneId();
	    	ZoneConfig				zoneConfig           = getCoreDao().loadZoneConfig(zoneId);
			AccessControlManager	accessControlManager = getAccessControlManager();
			if (null == accessControlManager) {
				accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
			}
			accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING);

			// Is the entity a folder entry?
	    	User user = RequestContextHolder.getRequestContext().getUser();
			if (de.getEntityType().equals(EntityType.folderEntry)) {
				// Yes!  Does the user have AllowSharing rights on it?
				FolderEntry fe = ((FolderEntry) de);
				if (accessControlManager.testOperation(user, fe, WorkAreaOperation.ALLOW_SHARING)) {
					// Yes!
					reply = true;
				}
				
				// Is the user the owner of the folder entry?
				else if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					// Yes!
					reply = true;
				}
				
			}

			// No, the entity isn't a folder entry!  Is it a folder or
			// workspace (i.e., a binder)?
			else if (de.getEntityType().equals(EntityType.folder) || de.getEntityType().equals(EntityType.workspace)) {
				// Yes!  Does the user have AllowSharing rights on it?
				Binder binder = ((Binder) de);
				if (accessControlManager.testOperation(user, binder, WorkAreaOperation.ALLOW_SHARING)) {
					// Yes!
					reply = true;
				}
				
				// Is the user the owner of the binder?
				else if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
					// Yes.
					reply = true;
				}
			}
		}
		
		catch (AccessControlException ace) {
			// AccessControlException implies sharing isn't allowed.
			reply = false;
		}

		// If we get here, reply contains true if the user can add a
		// share to the given entity and false otherwise.  Return it.
		return reply;
	}
	
    
	@Override
	public boolean isSharingEnabled() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		AccessControlManager accessControlManager = getAccessControlManager();
		if (accessControlManager == null) {
			accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		}
		return accessControlManager.testOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING);
	}

    //RW transaction
	@Override
	public void addShareItem(ShareItem shareItem) {
		// Access check (throws error if not allowed)
		checkAccess(shareItem, SharingOperation.addShareItem);
		
		getCoreDao().save(shareItem);
		
		//Index the entity that is being shared
		indexSharedEntity(shareItem);
	}
	
    //RW transaction
	@Override
	public void modifyShareItem(ShareItem latestShareItem, Long previousShareItemId) {
		if(latestShareItem == null)
			throw new IllegalArgumentException("Latest share item must be specified");
		if(previousShareItemId == null)
			throw new IllegalArgumentException("Previous share item ID must be specified");
		if(latestShareItem.getId() != null)
			throw new IllegalArgumentException("Latest share item must be transient");
		
		// Access check (throws error if not allowed)
		checkAccess(latestShareItem, SharingOperation.modifyShareItem);
		
		// Update previous snapshot
		try {
			ShareItem previousShareItem = getProfileDao().loadShareItem(previousShareItemId);
			
			previousShareItem.setLatest(false);
			
			getCoreDao().update(previousShareItem);
		}
		catch(NoShareItemByTheIdException e) {
			// The previous snapshot isn't found.
			logger.warn("Previous share item with id '" + previousShareItemId + "' is not found.");
		}

		// Save new snapshot
		getCoreDao().save(latestShareItem);
		
		//Index the entity that is being shared
		indexSharedEntity(latestShareItem);		
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#deleteShareItem(org.kablink.teaming.domain.ShareItem)
	 */
    //RW transaction
	@Override
	public void deleteShareItem(Long shareItemId) {
		try {
			ShareItem shareItem = getProfileDao().loadShareItem(shareItemId);
			// Access check (throws error if not allowed)
			checkAccess(shareItem, SharingOperation.deleteShareItem);
			
			getCoreDao().delete(shareItem);
			
			//Index the entity that is being shared
			indexSharedEntity(shareItem);
		}
		catch(NoShareItemByTheIdException e) {
			// already gone, ok
		}
	}
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#getShareItem(java.lang.Long)
	 */
	@Override
	public ShareItem getShareItem(Long shareItemId)
			throws NoShareItemByTheIdException {
		// Access check
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		return getProfileDao().loadShareItem(shareItemId);
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#getShareItems(java.util.Collection)
	 */
	@Override
	public List<ShareItem> getShareItems(Collection<Long> shareItemIds) {
		// Access check?
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		return getProfileDao().loadShareItems(shareItemIds);
	}
    
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.sharing.SharingModule#getShareItems(org.kablink.teaming.dao.util.ShareItemSelectSpec)
	 */
	@Override
	public List<ShareItem> getShareItems(ShareItemSelectSpec selectSpec) {
		// Access check?
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		return getProfileDao().findShareItems(selectSpec);
	}
    
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.sharing.SharingModule#getSharedEntity(org.kablink.teaming.domain.ShareItem)
	 */
	@Override
	public DefinableEntity getSharedEntity(ShareItem shareItem) {
		EntityIdentifier.EntityType entityType = shareItem.getSharedEntityIdentifier().getEntityType();
		if(entityType == EntityIdentifier.EntityType.folderEntry) {
			return getFolderModule().getEntry(null, shareItem.getSharedEntityIdentifier().getEntityId());
		}
		else if(entityType == EntityIdentifier.EntityType.folder || entityType == EntityIdentifier.EntityType.workspace) {
			return getBinderModule().getBinder(shareItem.getSharedEntityIdentifier().getEntityId());
		}
		else {
			throw new IllegalArgumentException("Unsupported entity type '" + entityType.name() + "' for sharing");
		}
	}

	@Override
	public DefinableEntity getSharedRecipient(ShareItem shareItem) {
		ShareItem.RecipientType recipientType = shareItem.getRecipientType();
		if(recipientType == RecipientType.user || recipientType == RecipientType.group) {
			return getProfileModule().getEntry(shareItem.getRecipientId());
			
		} else if(recipientType == RecipientType.team) {
			return getBinderModule().getBinder(shareItem.getRecipientId());
			
		} else {
			throw new IllegalArgumentException("Unsupported recipient type '" + recipientType.name() + "' for sharing");
		}
	}

	protected FolderModule getFolderModule() {
		if (folderModule == null) {
			folderModule = (FolderModule) SpringContextUtil.getBean("folderModule");
		}
		return folderModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	protected BinderModule getBinderModule() {
		if (binderModule == null) {
			binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");
		}
		return binderModule;
	}

	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	
	protected ProfileModule getProfileModule() {
		if (profileModule == null) {
			profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
		}
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	//Routine to re-index an entity after a change in sharing
	protected void indexSharedEntity(ShareItem shareItem) {
		DefinableEntity entity = getSharedEntity(shareItem);
		if (entity.getEntityType() == EntityType.folderEntry) {
			folderModule.indexEntry((FolderEntry) entity, Boolean.TRUE);
		}
		else if (entity.getEntityType() == EntityIdentifier.EntityType.folder || 
				entity.getEntityType() == EntityIdentifier.EntityType.workspace) {
			getBinderModule().indexBinder(entity.getId());
		}
	}

}
