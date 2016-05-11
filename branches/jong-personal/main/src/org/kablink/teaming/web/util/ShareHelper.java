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
package org.kablink.teaming.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.fi.connection.acl.AclItemPermissionMapper;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.*;

import java.util.*;

/**
 * Helper class dealing with various administrative functions.
 * 
 * @author drfoster@novell.com
 */
public class ShareHelper {
	protected static Log m_logger = LogFactory.getLog(ShareHelper.class);

	private static Folder getFolderFromEntityId(EntityIdentifier entityIdentifier) {
		Folder folder = null;
		if(EntityIdentifier.EntityType.folder == entityIdentifier.getEntityType())
			folder = getFolderDao().loadFolder(entityIdentifier.getEntityId(), null);
		return folder;
	}


	public static EntityShareRights calculateHighestEntityShareRights(AllModulesInjected ami, EntityIdentifier entityId) {
		EntityShareRights highestRights;
		// Does the entity represent a folder in a net folder?
		Folder folder = getFolderFromEntityId(entityId);

		// Get the highest share rights the logged-in user has to this entity
		if(folder != null && folder.isFolderInNetFolder()){
			highestRights = calculateHighestEntityShareRightsForNetFolder( ami, folder );
		}
		else{
			highestRights = calculateHighestEntityShareRightsForEntity( ami, entityId );
		}
		return highestRights;
	}


	/*
	 * Get the highest share rights the logged-in user has to this entity
	 */
	public static EntityShareRights calculateHighestEntityShareRightsForEntity(AllModulesInjected ami, EntityIdentifier entityId) {
		List<ShareItem> listOfShareItems = null;
		ShareItem.Role accessRights;
		ShareItemSelectSpec spec;

		// Get the current user's acl rights to the given entity.
		accessRights = getHighestEntityAccessRightsFromACLs( ami, entityId );

		spec = new ShareItemSelectSpec();
		spec.setRecipients( RequestContextHolder.getRequestContext().getUserId(), null, null );
		spec.setSharedEntityIdentifier( entityId );
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
				ShareItem.Role nextAccessRights;

				nextAccessRights = ShareHelper.getAccessRightsFromRightSet( nextShareItem.getRightSet() );

				switch( accessRights )
				{
					case EDITOR:
						if ( nextAccessRights == ShareItem.Role.CONTRIBUTOR )
							accessRights = nextAccessRights;
						break;

					case VIEWER:
					case NONE:
						accessRights = nextAccessRights;
						break;
				}
			}
		}

		return new EntityShareRights(accessRights, accessRights);
	}


	/*
	 * Get the highest share rights the logged-in user has to this entity
	 */
	public static EntityShareRights calculateHighestEntityShareRightsForNetFolder(AllModulesInjected ami, Folder folder) {
		User currentUser = RequestContextHolder.getRequestContext().getUser();

		ShareItem.Role role;

		// If admin, don't bother computing it. He is all mighty.
		if(currentUser.isSuper()) {
			return new EntityShareRights(ShareItem.Role.CONTRIBUTOR, ShareItem.Role.CONTRIBUTOR);
		}

		Map<String, List<String>> groupIds = null;
		AclItemPermissionMapper permissionMapper = null;
		AclResourceSession session = null;

		boolean internalLdapUser = currentUser.getIdentityInfo().isInternal() && currentUser.getIdentityInfo().isFromLdap();
		if(internalLdapUser) {
			AclResourceDriver driver = (AclResourceDriver) folder.getResourceDriver();
			groupIds = AccessUtils.getFileSystemGroupIds(driver);
			permissionMapper = driver.getAclItemPermissionMapper();
			session = (AclResourceSession) getResourceDriverManager().getSession(driver);
		}

		BinderNode topNode, minNode;

		ShareItem.Role result;

		try {
			LinkedList<BinderNode> list = new LinkedList<BinderNode>(); // queue
			ShareItem.Role nativeAccessRights;
			if(session != null)
				nativeAccessRights = getNativeAccessRights(folder, session, permissionMapper, groupIds);
			else
				nativeAccessRights = ShareItem.Role.NONE;
			ShareItem.Role shareGrantedAccessRights = getShareGrantedAccessRights(ami, folder);
			BinderNode node = new BinderNode(folder, nativeAccessRights, shareGrantedAccessRights);
			if(m_logger.isTraceEnabled())
				m_logger.trace("User='" + currentUser.getName() + "', Top folder=" + folder.getId() + ", Folder access=" + node);
			topNode = minNode = node;
			result = node.combinedAccessRights;
			// If the user has NO access at the current node, there's no need to look further
			// because there's no lesser access than NO access.
			// Additionally, if the user's highest access rights  to the current node was granted by
			// a sharing (instead of or in conjunction with native access), there's no need to look
			// further down the tree because the user's access will never decrease due to the nature
			// of the share-granted access.
			if(result != ShareItem.Role.NONE && result != shareGrantedAccessRights)
				list.add(node);
			boolean stop = false;
			while(!stop && !list.isEmpty()) {
				node = list.removeFirst();
				List<Binder> children = node.binder.getBinders();
				for(Binder child:children) {
					if(session != null)
						nativeAccessRights = getNativeAccessRights(child, session, permissionMapper, groupIds);
					else
						nativeAccessRights = ShareItem.Role.NONE;
					shareGrantedAccessRights = getShareGrantedAccessRights(ami, node.shareGrantedAccessRights, child);
					node = new BinderNode(child, nativeAccessRights, shareGrantedAccessRights);
					if(m_logger.isTraceEnabled())
						m_logger.trace("User='" + currentUser.getName() + "', Top folder=" + folder.getId() + ", Folder access=" + node);
					result = minAccessRights(result, node.combinedAccessRights);
					if(result == node.combinedAccessRights)
						minNode = node;
					if(result != ShareItem.Role.NONE && result != shareGrantedAccessRights) {
						list.add(node);
					}
					else {
						stop = true;
						break;
					}
				}
			}
		}
		finally {
			if(session != null)
				session.close();
		}

		if(topNode.combinedAccessRights != result) {
			// This means the user has higher access rights at the top of the tree (= the folder
			// being considered for sharing) than over the entire tree. Since this is non-typical
			// situation, we want to log the information here in case Admin needs that info to
			// trouble shoot or respond to user inquiry.
			m_logger.info("User '" + currentUser.getName() + "' with diminishing access: Top access=" + topNode + ", Least access=" + minNode);
		}
		else {
			// The user has constant level of access on the entire tree.
			if(m_logger.isDebugEnabled())
				m_logger.debug("User '" + currentUser.getName() + "' has constant access on the folder tree: Top access=" + topNode);
		}

		return new EntityShareRights(topNode.combinedAccessRights, result);
	}

	/*
	 * Return the "highest" access rights based on acls the logged-in user has to the given entity
	 */
	private static ShareItem.Role getHighestEntityAccessRightsFromACLs(
			AllModulesInjected ami,
			EntityIdentifier entityId )
	{
		ShareItem.Role accessRights;

		accessRights = ShareItem.Role.VIEWER;

		if ( entityId.getEntityType().isBinder() )
		{
			boolean access;
			WorkArea workArea;
			AccessControlManager accessControlManager;
			User currentUser = RequestContextHolder.getRequestContext().getUser();

			accessControlManager = AccessUtils.getAccessControlManager();

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
				accessRights = ShareItem.Role.EDITOR;
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
				accessRights = ShareItem.Role.CONTRIBUTOR;
			}
		}
		else
		{
			FolderModule folderModule;
			FolderEntry folderEntry;

			folderModule = ami.getFolderModule();
			folderEntry = ami.getFolderModule().getEntry( null, entityId.getEntityId() );

			// Does the user have "editor" rights?
			if ( folderModule.testAccess( folderEntry, FolderModule.FolderOperation.readEntry ) &&
					folderModule.testAccess( folderEntry, FolderModule.FolderOperation.modifyEntry ) &&
					folderModule.testAccess( folderEntry, FolderModule.FolderOperation.addReply ) )
			{
				accessRights = ShareItem.Role.EDITOR;
			}
		}
/**
 if ( accessControlManager.testRightsGrantedBySharing(
 GwtServerHelper.getCurrentUser(),
 workArea,
 ShareItem.Role.EDITOR.getWorkAreaOperations() ) )
 {
 accessRights = ShareItem.Role.EDITOR;
 }

 if ( entityId.isBinder() )
 {
 if ( accessControlManager.testRightsGrantedBySharing(
 GwtServerHelper.getCurrentUser(),
 workArea,
 ShareItem.Role.CONTRIBUTOR.getWorkAreaOperations() ) )
 {
 accessRights = ShareItem.Role.CONTRIBUTOR;
 }
 }
 */
		return accessRights;
	}

	private static ShareItem.Role getNativeAccessRights(Binder binder, AclResourceSession session, AclItemPermissionMapper permissionMapper, Map<String, List<String>> groupIds) {
		session.setPath(binder.getResourcePath(), binder.getResourceHandle(), Boolean.TRUE);
		ShareItem.Role accessRights;
		String permissionName = session.getPermissionName(groupIds);
		String vibeRoleName;
		if(permissionName != null)
			vibeRoleName = permissionMapper.toVibeRoleName(permissionName);
		else
			vibeRoleName = null;
		if(ObjectKeys.ROLE_TITLE_FILR_CONTRIBUTOR.equals(vibeRoleName))
			accessRights = ShareItem.Role.CONTRIBUTOR;
		else if(ObjectKeys.ROLE_TITLE_FILR_EDITOR.equals(vibeRoleName))
			accessRights = ShareItem.Role.EDITOR;
		else if(ObjectKeys.ROLE_TITLE_FILR_VIEWER.equals(vibeRoleName))
			accessRights = ShareItem.Role.VIEWER;
		else
			accessRights = ShareItem.Role.NONE;
		return accessRights;
	}

	/*
	 * Return share-granted access rights that the logged-in user has at the level
	 * represented by this binder. This is determined by all the binders shared with
	 * the user at this level or above in the ancestry hierarchy.
	 */
	private static ShareItem.Role getShareGrantedAccessRights(AllModulesInjected ami, Binder binder) {
		// Whether a sharing on a folder applies down recursively or not is controlled
		// by the regular Vibe-side inheritance flag. The inheritance flag associated
		// with the external ACLs is nothing but an implementation-level optimization
		// (to avoid storing same ACLs multiple times) and has NO effect when it comes
		// to the scope of sharing.
		List<EntityIdentifier> chain = new ArrayList<EntityIdentifier>();
		chain.add(binder.getEntityIdentifier());
		while(binder.isFunctionMembershipInherited()) {
			binder = binder.getParentBinder();
			if(binder != null)
				chain.add(binder.getEntityIdentifier());
		}

		User currentUser = RequestContextHolder.getRequestContext().getUser();
		ShareItemSelectSpec spec = new ShareItemSelectSpec();
		spec.setRecipientsFromUserMembership(currentUser.getId());
		spec.setSharedEntityIdentifiers(chain);
		spec.setLatest(true);

		ShareItem.Role result = ShareItem.Role.NONE;

		List<ShareItem> listOfShareItems = ami.getSharingModule().getShareItems(spec);
		if(listOfShareItems != null && listOfShareItems.size() > 0) {
			ShareItem.Role accessRights;
			for(ShareItem shareItem:listOfShareItems) {
				accessRights = getAccessRightsFromRightSet(shareItem.getRightSet());
				if(accessRights.ordinalCode() > result.ordinalCode()) {
					result = accessRights;
					if(ShareItem.Role.CONTRIBUTOR == result)
						break; // Maximum reached. No need for further checking
				}
			}
		}

		return result;
	}

	private static ShareItem.Role getShareGrantedAccessRights(AllModulesInjected ami, ShareItem.Role shareGrantedAccessRightsForParent, Binder binder) {
		if(ShareItem.Role.CONTRIBUTOR == shareGrantedAccessRightsForParent)
			return shareGrantedAccessRightsForParent; // Already have maximum. No need for further checking

		User currentUser = RequestContextHolder.getRequestContext().getUser();
		ShareItemSelectSpec spec = new ShareItemSelectSpec();
		spec.setRecipientsFromUserMembership(currentUser.getId());
		spec.setSharedEntityIdentifier(binder.getEntityIdentifier());
		spec.setLatest(true);

		ShareItem.Role result = shareGrantedAccessRightsForParent;

		List<ShareItem> listOfShareItems = ami.getSharingModule().getShareItems(spec);
		if(listOfShareItems != null && listOfShareItems.size() > 0) {
			ShareItem.Role accessRights;
			for(ShareItem shareItem:listOfShareItems) {
				accessRights = getAccessRightsFromRightSet(shareItem.getRightSet());
				if(accessRights.ordinalCode() > result.ordinalCode()) {
					result = accessRights;
					if(ShareItem.Role.CONTRIBUTOR == result)
						break; // Maximum reached. No need for further checking
				}
			}
		}

		return result;
	}

	/**
	 * Get AccessRights that corresponds to the given RightSet
	 *
	 * @param rightSet
	 *
	 * @return
	 */
	public static ShareItem.Role getAccessRightsFromRightSet( WorkAreaOperation.RightSet rightSet )
	{
		ShareItem.Role accessRights;

		accessRights = ShareItem.Role.NONE;

		if ( rightSet != null )
		{
			WorkAreaOperation.RightSet viewerRightSet;
			WorkAreaOperation.RightSet editorRightSet;
			WorkAreaOperation.RightSet contributorRightSet;
			boolean shareInternal;
			boolean shareExternal;
			boolean sharePublic;
			boolean shareForward;
			boolean folderShareInternal;
			boolean folderShareExternal;
			boolean folderSharePublic;
			boolean folderShareForward;

			// areRightSetsEqual() compares "share internal", "share external", "share public" and "share forward".
			// That is why we are setting them to false.
			shareInternal = rightSet.isAllowSharing();
			shareExternal = rightSet.isAllowSharingExternal();
			sharePublic = rightSet.isAllowSharingPublic();
			shareForward = rightSet.isAllowSharingForward();
			folderShareInternal = rightSet.isAllowFolderSharingInternal();
			folderShareExternal = rightSet.isAllowFolderSharingExternal();
			folderSharePublic = rightSet.isAllowFolderSharingPublic();
			folderShareForward = rightSet.isAllowFolderSharingForward();
			rightSet.setAllowSharing( false );
			rightSet.setAllowSharingExternal( false );
			rightSet.setAllowSharingPublic( false );
			rightSet.setAllowSharingForward( false );
			rightSet.setAllowFolderSharingInternal( false );
			rightSet.setAllowFolderSharingExternal( false );
			rightSet.setAllowFolderSharingPublic( false );
			rightSet.setAllowFolderSharingForward( false );

			viewerRightSet = getViewerRightSet();
			editorRightSet = getEditorRightSet();
			contributorRightSet = getContributorRightSet();

			// Is the given RightSet equal to the "View" RightSet
			if ( areRightSetsGreaterOrEqual( rightSet, viewerRightSet ) )
			{
				// Yes
				accessRights = ShareItem.Role.VIEWER;
			}
			// Is the given RightSet equal to the "Editor" RightSet
			if ( areRightSetsGreaterOrEqual( rightSet, editorRightSet ) )
			{
				// Yes
				accessRights = ShareItem.Role.EDITOR;
			}
			// Is the given RightSet equal to the "Contributor" RightSet
			if ( areRightSetsGreaterOrEqual( rightSet, contributorRightSet ) )
			{
				// Yes
				accessRights = ShareItem.Role.CONTRIBUTOR;
			}

			// Restore the values we set to false.
			rightSet.setAllowSharing( shareInternal );
			rightSet.setAllowSharingExternal( shareExternal );
			rightSet.setAllowSharingPublic( sharePublic );
			rightSet.setAllowSharingForward( shareForward );
			rightSet.setAllowFolderSharingInternal( folderShareInternal );
			rightSet.setAllowFolderSharingExternal( folderShareExternal );
			rightSet.setAllowFolderSharingPublic( folderSharePublic );
			rightSet.setAllowFolderSharingForward( folderShareForward );
		}

		return accessRights;
	}

	/*
	 * Return the RightSet that corresponds to the "Contributor" rights
	 */
	public static WorkAreaOperation.RightSet getContributorRightSet()
	{
		WorkAreaOperation.RightSet rightSet;
		List<WorkAreaOperation> operations;

		operations = ShareItem.Role.CONTRIBUTOR.getRightSet().getRights();
		rightSet = new WorkAreaOperation.RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );

		return rightSet;
	}

	/*
	 * Return the RightSet that corresponds to the "Editor" rights
	 */
	public static WorkAreaOperation.RightSet getEditorRightSet()
	{
		WorkAreaOperation.RightSet rightSet;
		List<WorkAreaOperation> operations;

		operations = ShareItem.Role.EDITOR.getRightSet().getRights();
		rightSet = new WorkAreaOperation.RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );

		return rightSet;
	}

	/*
	 * Return the RightSet that corresponds to the "Viewer" rights
	 */
	public static WorkAreaOperation.RightSet getViewerRightSet()
	{
		WorkAreaOperation.RightSet rightSet;
		List<WorkAreaOperation> operations;

		operations = ShareItem.Role.VIEWER.getRightSet().getRights();
		rightSet = new WorkAreaOperation.RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );

		return rightSet;
	}

	private static ShareItem.Role maxAccessRights(ShareItem.Role ar1, ShareItem.Role ar2) {
		if(ar1 == null || ar2 == null)
			throw new IllegalArgumentException("Access rights must be specified");
		if(ar1.ordinalCode() >= ar2.ordinalCode())
			return ar1;
		else
			return ar2;
	}

	private static ShareItem.Role minAccessRights(ShareItem.Role ar1, ShareItem.Role ar2) {
		if(ar1 == null || ar2 == null)
			throw new IllegalArgumentException("Access rights must be specified");
		if(ar1.ordinalCode() <= ar2.ordinalCode())
			return ar1;
		else
			return ar2;
	}

	/*
	 * Compare the 2 RightSet objects to see if they have the same or
	 * greater rights.
	 */
	private static boolean areRightSetsGreaterOrEqual(WorkAreaOperation.RightSet rightSet1, WorkAreaOperation.RightSet rightSet2 )
	{
		if ( rightSet1 == null || rightSet2 == null )
		{
			m_logger.error( "In GwtShareHelper.areRightSetsGreaterOrEqual(), one of the RightSet parameters is null" );
			return false;
		}

		return rightSet1.greaterOrEqual( rightSet2 );
	}

	private static class BinderNode {
		private Binder binder;
		// The level of native access rights that the logged-in user has on this binder
		private ShareItem.Role nativeAccessRights;
		// The share-granted access rights that the logged-in user has on this binder.
		// This is determined by all the binders shared with the user at this level or
		// above in the ancestry tree (i.e., it's not just one level thing).
		private ShareItem.Role shareGrantedAccessRights;
		// Computed
		private ShareItem.Role combinedAccessRights;

		BinderNode(Binder binder, ShareItem.Role nativeAccessRights, ShareItem.Role shareGrantedAccessRights) {
			this.binder = binder;
			this.nativeAccessRights = nativeAccessRights;
			this.shareGrantedAccessRights = shareGrantedAccessRights;
			this.combinedAccessRights = maxAccessRights(nativeAccessRights, shareGrantedAccessRights);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("{");
			sb.append("folderId=")
					.append(binder.getId())
					.append(", nativeAccessRights=")
					.append(nativeAccessRights)
					.append(", shareGrantedAccessRights=")
					.append(shareGrantedAccessRights)
					.append(", combinedAccessRights=")
					.append(combinedAccessRights)
					.append("}");
			return sb.toString();
		}
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

	private static FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}

	private static ResourceDriverManager getResourceDriverManager() {
		return (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
	}
}
