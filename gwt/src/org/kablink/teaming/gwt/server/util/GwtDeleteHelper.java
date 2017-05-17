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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.DeleteSelectedUsersMode;
import org.kablink.teaming.gwt.client.util.DeleteSelectionsMode;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.CloudFolderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.TrashHelper.TrashEntity;
import org.kablink.teaming.web.util.TrashHelper.TrashResponse;

/**
 * Helper methods for the GWT server code that services requests
 * dealing with deleting things.
 *
 * @author drfoster@novell.com
 */
public class GwtDeleteHelper {
	protected static Log m_logger = LogFactory.getLog(GwtDeleteHelper.class);

	// The following controls whether any net storage (Vibe mirrored
	// folders, Filr Net Folders (including a Home folder), ... get
	// purge from the net storage source when a user's workspace is
	// deleted.
	private final static boolean	PURGE_NETSTORAGE_WITH_USER_WS	= false;

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtDeleteHelper() {
		// Nothing to do.
	}

	/**
	 * Delete the given tag from the given entry.
	 * 
	 * @param fm
	 * @param entryId
	 * @param tagInfo
	 */
	public static void deleteEntryTag(FolderModule fm, Long entryId, TagInfo tagInfo) {
		// Does this tag already exist?
		String tagId = tagInfo.getTagId();
		if (MiscUtil.hasString(tagId)) {
			// Yes!  Delete it from the given entry.
			fm.deleteTag(null, entryId, tagId);
		}
	}
	
	/**
	 * Remove the given tag from the given binder.
	 * are deleted from the binder.
	 * 
	 * @param bm
	 * @param binder
	 * @param tag
	 */
	public static void deleteBinderTag(BinderModule bm, Binder binder, TagInfo tag) {
		String tagId = tag.getTagId();
		if (MiscUtil.hasString(tagId)) {
			bm.deleteTag(binder.getId(), tagId);
		}
	}
	
	/**
	 * ?
	 * 
	 * @param bs
	 * @param listOfGroups
	 * 
	 * @return
	 */
	public static Boolean deleteGroups(AllModulesInjected bs, ArrayList<GroupInfo> listOfGroups) throws GwtTeamingException {
		if (MiscUtil.hasItems(listOfGroups)) {
	   		List<Long> grpIds = new ArrayList<Long>();
	   		for (GroupInfo nextGroup:  listOfGroups) {
	   			grpIds.add(nextGroup.getId());
	   		}

       		// Remove each group's quota (by setting it to 0) before
	   		// deleting it.  This will fix up all of the user quotas
	   		// that may have been influenced by this group.
	   		bs.getProfileModule().adjustGroupDiskQuotas(grpIds, 0L);
	   		bs.getProfileModule().adjustGroupFileSizeLimits(grpIds, null);
	       		
			// Delete each groups
	   		for (Long nextGroupId:  grpIds) {
	   			try {
	   				bs.getProfileModule().deleteEntry(nextGroupId, null);
	   			}
	   			catch (Exception ex) {
	   				throw GwtLogHelper.getGwtClientException(m_logger, ex);
	   			} 
	   		}
		}
		
		return Boolean.TRUE;
	}

	/**
	 * Deletes the specified users.
	 *
	 * @param bs
	 * @param request
	 * @param userIds
	 * @param dsuMode
	 * @param purgeUsersWithWS
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData deleteSelectedUsers(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, DeleteSelectedUsersMode dsuMode, boolean purgeUsersWithWS) throws GwtTeamingException {
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		deleteSelectedUsersImpl(bs, request, userIds, dsuMode, purgeUsersWithWS, reply);
		return reply;
	}
	
	@SuppressWarnings("unchecked")
	private static void deleteSelectedUsersImpl(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, DeleteSelectedUsersMode dsuMode, boolean purgeUsersWithWS, ErrorListRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtDeleteHelper.deleteSelectedUsersImpl()");
		try {
			// Decide what's to be trashed and what's to be purged.
			List<Long> trashIds;
			List<Long> purgeIds;
			switch (dsuMode) {
			default:
				reply.addError(NLT.get("deleteSelectedUsers.InternalError.BogusDeleteMode", new String[]{dsuMode.name()}));
				return;
				
			case PURGE_ALL_WORKSPACES:  purgeIds = userIds; trashIds = null;    break;
			case TRASH_ALL_WORKSPACES:  purgeIds = null;    trashIds = userIds; break;
				
			case TRASH_ADHOC_WORKSPACES_PURGE_OTHERS:
				purgeIds = new ArrayList<Long>();
				trashIds = new ArrayList<Long>();
				List resolvedList = ResolveIds.getPrincipals(userIds, false);
				if (MiscUtil.hasItems(resolvedList)) {
					for (Object userO: resolvedList) {
						User user   = ((User) userO);
						Long userId = user.getId();
						if (TrashHelper.canTrashUserWorkspace(bs, user))
						     trashIds.add(userId);
						else purgeIds.add(userId);
						userIds.remove(userId);
					}
				}
				if (purgeIds.isEmpty()) purgeIds = null;
				if (trashIds.isEmpty()) trashIds = null;

				// Did we handle all the users we were given?
				if (!(userIds.isEmpty())) {
					// No!  Then something unexpected happened!  Tell
					// the user about the problem.
					StringBuffer buf = new StringBuffer();
					boolean firstId = true;
					for (Long uid:  userIds) {
						if (!firstId) buf.append(", ");
						buf.append(String.valueOf(uid));
					}
					reply.addWarning(NLT.get("deleteSelectedUsers.InternalError.UnhandledUsers", new String[]{buf.toString()}));
				}
				
				break;
			}
			
			// Purge the purge items...
			if (null != purgeIds) {
				if (purgeUsersWithWS)
				     doPurgeUsers(         bs, request, purgeIds, PURGE_NETSTORAGE_WITH_USER_WS, reply);
				else doPurgeUserWorkspaces(bs, request, purgeIds, PURGE_NETSTORAGE_WITH_USER_WS, reply);
			}
			
			// ...and delete the trash items.
			if (null != trashIds) {
				deleteUserWorkspacesImpl(bs, request, trashIds, reply);
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Deletes the specified entities.
	 *
	 * @param bs
	 * @param request
	 * @param entityIds
	 * @param dsMode
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData deleteSelections(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, DeleteSelectionsMode dsMode) throws GwtTeamingException {
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		deleteSelectionsImpl(bs, request, entityIds, dsMode, reply);
		return reply;
	}
	
	private static void deleteSelectionsImpl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, DeleteSelectionsMode dsMode, ErrorListRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtDeleteHelper.deleteSelectionsImpl()");
		try {
			// Decide what's to be trashed and what's to be purged.
			List<EntityId> purgeIds;
			List<EntityId> trashIds;
			switch (dsMode) {
			default:
				reply.addError(NLT.get("deleteSelections.InternalError.BogusDeleteMode", new String[]{dsMode.name()}));
				return;
				
			case PURGE_ALL:  purgeIds = entityIds; trashIds = null;      break;
			case TRASH_ALL:  purgeIds = null;      trashIds = entityIds; break;
			
			case TRASH_ADHOC_PURGE_OTHERS:
				purgeIds = new ArrayList<EntityId>();
				trashIds = new ArrayList<EntityId>();
				for (EntityId eid:  entityIds) {
					if (GwtServerHelper.isEntityRemote(bs, eid, reply))
					     purgeIds.add(eid);
					else trashIds.add(eid);
				}
				if (purgeIds.isEmpty()) purgeIds = null;
				if (trashIds.isEmpty()) trashIds = null;
				
				break;
			}

			// Purge the purge items and delete the trash items.
			if (null != purgeIds) doPurgeSelections( bs, request, purgeIds, true, reply);	// true -> purge from remote source.
			if (null != trashIds) doDeleteSelections(bs, request, trashIds,       reply);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Delete user workspaces.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData deleteUserWorkspaces(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) throws GwtTeamingException {
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		deleteUserWorkspacesImpl(bs, request, userIds, reply);
		return reply;
	}
	
	public static void deleteUserWorkspacesImpl(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, ErrorListRpcResponseData reply) throws GwtTeamingException {
		try {
			// Were we given the IDs of any users to delete?
			Long currentUserId = GwtServerHelper.getCurrentUserId(); 
			if (MiscUtil.hasItems(userIds)) {
				// Yes!  Scan them.
				ProfileModule pm                          = bs.getProfileModule();
				boolean       isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being delete?
					User user = GwtServerHelper.getResolvedUser(userId);
					if (null != user) {
						// Yes!  Is it the user that's logged in?
						if (user.getId().equals(currentUserId)) {
							// Yes!  They can't delete their own
							// workspace.  Ignore it.
							reply.addError(NLT.get("deleteUserWorkspaceError.self"));
							continue;
						}

						// Is it a reserved user?
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						if (user.isReserved()) {
							// Yes!  They can't do that.  Ignore it.
							reply.addError(NLT.get("deleteUserWorkspaceError.reserved", new String[]{userTitle}));
							continue;
						}
						
						// Was this user provisioned from an LDAP
						// source?
						if (user.getIdentityInfo().isFromLdap()) {
							// Yes!  Their workspace can't be trashed.
							// Ignore it.
							reply.addError(NLT.get("deleteUserWorkspaceError.ldap", new String[]{userTitle}));
							continue;
						}
					}
					
					try {
						// Does this user have a workspace ID?
						Long wsId = user.getWorkspaceId();
						if (null != wsId) {
							// Yes!  Delete the workspace and disable
							// the user.
							TrashHelper.preDeleteBinder(bs, wsId);
							pm.disableEntry(userId, true);	// true -> Disable.
						}
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						String messageKey;
						if      (e instanceof AccessControlException) messageKey = "deleteUserWorkspaceError.AccssControlException";
						else                                          messageKey = "deleteUserWorkspaceError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtDeleteHelper.deleteUserWorkspaces( User:  '" + user.getTitle() + "', EXCEPTION ):  ", e);
					}
				}
			}
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtDeleteHelper.deleteUserWorkspaces( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Deletes the specified entities.
	 */
	private static void doDeleteSelections(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, ErrorListRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtDeleteHelper.doDeleteSelections()");
		try {
			// Scan the entry IDs...
			BinderModule bm = bs.getBinderModule();
			for (EntityId entityId:  entityIds) {
				try {
					// ...deleting each entity...
					if (entityId.isBinder()) {
						Long binderId = entityId.getEntityId();
						if (BinderHelper.isBinderHomeFolder(bm.getBinder(binderId))) {
							// ...except Home folders which cannot...
							// ...be deleted...
							String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
							reply.addError(NLT.get("deleteEntryError.AccssControlException", new String[]{entryTitle}));
						}
						else {
							TrashHelper.preDeleteBinder(bs, entityId.getEntityId());
						}
					}
					
					else {
						TrashHelper.preDeleteEntry(bs, entityId.getBinderId(), entityId.getEntityId());
					}
				}

				catch (Exception e) {
					// ...tracking any that we couldn't delete.
					String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
					String messageKey;
					if      (e instanceof AccessControlException)         messageKey = "deleteEntryError.AccssControlException";
					else if (e instanceof ReservedByAnotherUserException) messageKey = "deleteEntryError.ReservedByAnotherUserException";
					else                                                  messageKey = "deleteEntryError.OtherException";
					reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
					
					GwtLogHelper.error(m_logger, "GwtDeleteHelper.doDeleteSelections( EntryTitle:  '" + entryTitle + "', EXCEPTION ):  ", e);
				}
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Purges the specified entities.
	 */
	private static void doPurgeSelections(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, boolean deleteMirroredSource, ErrorListRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtDeleteHelper.doPurgeSelections()");
		try {
			// Access the modules we need to purge things.
			BinderModule bm = bs.getBinderModule();
			FolderModule fm = bs.getFolderModule();
			
			// Scan the entities...
			List<Long> entryIds  = new ArrayList<Long>();
			List<Long> binderIds = new ArrayList<Long>();
			for (EntityId entityId:  entityIds) {
				// ...collecting the binder and entry IDs separately.
				Long eId = entityId.getEntityId();
				if (entityId.isEntry())
				     entryIds.add( eId);
				else binderIds.add(eId);
			}
			
			// Do we have any entry IDs?
			Map<Long, FolderEntry> entryMap = new HashMap<Long, FolderEntry>();
			boolean hasEntries = (!(entryIds.isEmpty())); 
			if (hasEntries) {
				// Yes!  Access and scan the entries...
				Set<FolderEntry> entrySet = fm.getEntries(entryIds);
				for (FolderEntry fe:  entrySet) {
					// ...mapping the ID to the FolderEntry for future
					// ...easy access. 
					entryMap.put(fe.getId(), fe);
				}
			}
			
			// Do we have any binder IDs?
			Map<Long, Binder> binderMap = new HashMap<Long, Binder>();
			boolean hasBinders = (!(binderIds.isEmpty())); 
			if (hasBinders) {
				// Yes!  Access and scan the binders...
				Set<Binder> binderSet = bm.getBinders(binderIds);
				for (Binder binder:  binderSet) {
					// ...mapping the ID to the Binder for future
					// ...easy access. 
					binderMap.put(binder.getId(), binder);
				}
			}

			// Allocate the option maps we need if we're purging any
			// entries.
            Map<String, Boolean> adHocOptions;
            Map<String, Boolean> mfOptions;
            if (hasEntries) {
	            adHocOptions = new HashMap<String, Boolean>();
	            mfOptions    = new HashMap<String, Boolean>();
	            mfOptions.put(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS, Boolean.TRUE);
            }
            else {
	            adHocOptions =
	            mfOptions    = null;
            }

            // Scan the entities...
			for (EntityId entityId:  entityIds) {
				// ...extracting the containing binder and entity ID.
				Long bId = entityId.getBinderId();
				Long eId = entityId.getEntityId();
				
				try {
					// Is this entity a binder?
					if (entityId.isBinder()) {
						// Yes!  Can we find the binder to purge?
						Binder binder = binderMap.get(eId);
						if (null == binder) {
							// No!  Tell the user about the problem.
							String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
							reply.addError(NLT.get("purgeEntryError.CantFindEntity", new String[]{entryTitle}));
							continue;
						}

						// We never allow a top level mirrored folder
						// (in Vibe) to have its mirrored source
						// deleted. 
						if (deleteMirroredSource && BinderHelper.isVibeMirroredFolder(binder)) {
							// Note that we don't depend on the 'top
							// folder' relationship here since in Vibe,
							// it's possible to create a mirrored
							// folder inside another folder.  In that
							// case, we only want to delete the
							// mirrored source if the container of the
							// folder being deleted is a mirrored
							// folder as well.
							deleteMirroredSource = BinderHelper.isVibeMirroredFolder(binder.getParentBinder());
						}
						
						// What type of binder is it?
						if (BinderHelper.isBinderHomeFolder(binder)) {
							// A Home folder!  We don't allow them to
							// be purged.  Tell the user about the problem.
							String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
							reply.addError(NLT.get("purgeEntryError.AccssControlException", new String[]{entryTitle}));
						}
						else if (CloudFolderHelper.isCloudFolder(binder)) {
							// A Cloud folder!  Purge it.
							CloudFolderHelper.deleteCloudFolder(
								bs,
								((Folder) binder),
								deleteMirroredSource);
						}
						else {
							// Some other binder type!  Purge it.
							bm.deleteBinder(
								eId,
								deleteMirroredSource,
								null);
						}
					}
					
					else {
						// No, the entity isn't a binder!  It must be
						// and entry.  Can we find it?
						FolderEntry fe = entryMap.get(eId);
						if (null == fe) {
							// No!  Tell the user about the problem.
							String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
							reply.addError(NLT.get("purgeEntryError.CantFindEntity", new String[]{entryTitle}));
							continue;
						}
						
						// Purge the entry using the appropriate option
						// map.
						fm.deleteEntry(
							bId,
							eId,
							deleteMirroredSource,
							(fe.getParentBinder().isMirrored() ?
								mfOptions                      :
								adHocOptions));
					}
				}
				
				catch (Exception e) {
					// Tracking the errors for things we couldn't
					// purge.
					String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
					String msgKey;
					if      (e instanceof AccessControlException)         msgKey = "purgeEntryError.AccssControlException";
					else if (e instanceof ReservedByAnotherUserException) msgKey = "purgeEntryError.ReservedByAnotherUserException";
					else if (e instanceof UncheckedIOException)           msgKey = "purgeEntryError.UncheckedIOException";
					else if (e instanceof WriteFilesException)            msgKey = "purgeEntryError.WriteFilesException";
					else                                                  msgKey = "purgeEntryError.OtherException";
					reply.addError(NLT.get(msgKey, new String[]{entryTitle}));
					
					GwtLogHelper.error(m_logger, "GwtDeleteHelper.doPurgeSelections( EntryTitle:  '" + entryTitle + "', EXCEPTION ):  ", e);
				}
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Purge users and their workspaces.
	 */
	@SuppressWarnings("unchecked")
	private static void doPurgeUsers(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, boolean purgeMirrored, ErrorListRpcResponseData reply) throws GwtTeamingException {
		try {
			// Were we given the IDs of any users to purge?
			Long currentUserId = GwtServerHelper.getCurrentUserId(); 
			if (MiscUtil.hasItems(userIds)) {
				// Yes!  Scan them.
				boolean isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being purge?
					User user = GwtServerHelper.getResolvedUser(userId);
					if (null != user) {
						// Yes!  Is it the user that's logged in?
						if (user.getId().equals(currentUserId)) {
							// Yes!  They can't purge their own
							// user object or workspace.  Ignore it.
							reply.addError(NLT.get("purgeUserError.self"));
							continue;
						}

						// Is it a reserved user?
						if (user.isReserved()) {
							// Yes!  They can't do that.  Ignore it.
							String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
							reply.addError(NLT.get("purgeUserError.reserved", new String[]{userTitle}));
							continue;
						}
					}
					
					try {
						// Purge the user and their workspace, if they have one.
						Map options = new HashMap();
						options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE,         new Boolean(null != user.getWorkspaceId()));
						options.put(ObjectKeys.INPUT_OPTION_DELETE_MIRRORED_FOLDER_SOURCE, new Boolean(purgeMirrored                ));
						bs.getProfileModule().deleteEntry(userId, options);
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						String messageKey;
						if      (e instanceof AccessControlException) messageKey = "purgeUserError.AccssControlException";
						else                                          messageKey = "purgeUserError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtDeleteHelper.doPurgeUsers( User:  '" + user.getTitle() + "', EXCEPTION ):  ", e);
					}
				}
			}
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtDeleteHelper.doPurgeUsers( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Purge user workspaces.
	 */
	private static void doPurgeUserWorkspaces(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, boolean purgeMirrored, ErrorListRpcResponseData reply) throws GwtTeamingException {
		try {
			// Were we given the IDs of any users to purge?
			Long currentUserId = GwtServerHelper.getCurrentUserId(); 
			if (MiscUtil.hasItems(userIds)) {
				// Yes!  Scan them.
				boolean isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being purge?
					User user = GwtServerHelper.getResolvedUser(userId);
					if (null != user) {
						// Yes!  Is it the user that's logged in?
						if (user.getId().equals(currentUserId)) {
							// Yes!  They can't purge their own
							// workspace.  Ignore it.
							reply.addError(NLT.get("purgeUserWorkspaceError.self"));
							continue;
						}

						// Is it a reserved user?
						if (user.isReserved()) {
							// Yes!  They can't do that.  Ignore it.
							String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
							reply.addError(NLT.get("purgeUserWorkspaceError.reserved", new String[]{userTitle}));
							continue;
						}
					}
					
					try {
						// Does this user have a workspace ID?
						Long wsId = user.getWorkspaceId();
						if (null != wsId) {
							// Yes!  Purge the workspace.
							bs.getBinderModule().deleteBinder(wsId, purgeMirrored, null);
						}
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						String messageKey;
						if      (e instanceof AccessControlException) messageKey = "purgeUserWorkspaceError.AccssControlException";
						else                                          messageKey = "purgeUserWorkspaceError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtDeleteHelper.doPurgeUserWorkspaces( User:  '" + userTitle + "', EXCEPTION ):  ", e);
					}
				}
			}
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtDeleteHelper.doPurgeUserWorkspaces( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Returns a StringRpcResponseData containing the URL for viewing
	 * the trash on the given BinderInfo.
	 * 
	 * @param bs
	 * @param request
	 * @param bi
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData getTrashUrl(AllModulesInjected bs, HttpServletRequest request, BinderInfo bi) throws GwtTeamingException {
		try {
			// Construct the URL for viewing the trash on this BinderInfo...
			Binder binder    = bs.getBinderModule().getBinderWithoutAccessCheck(bi.getBinderIdAsLong());
			String binderUrl = PermaLinkUtil.getPermalink(request, binder);
			String trashUrl  = GwtUIHelper.getTrashPermalink(binderUrl);
			
			// ...and wrap it in a StringRpcResponseData.
			StringRpcResponseData reply = new StringRpcResponseData(trashUrl);
			
			// If we get here, reply refers to the
			// StringRpcResponseData object containing the URL for
			// viewing the trash on the given BinderInfo.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtDeleteHelper.getTrashUrl( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Returns true if a DefinableEntity is in the trash and false
	 * otherwise.
	 * 
	 * @param item
	 * 
	 * @return
	 */
	public static boolean isEntityPreDeleted(DefinableEntity item) {
		boolean reply;
		if      (item instanceof Binder)      reply = GwtUIHelper.isBinderPreDeleted((Binder) item);
		else if (item instanceof FolderEntry) reply = ((FolderEntry) item).isPreDeleted();
		else                                  reply = false;
		return reply;
	}

	/**
	 * Returns true if a user's workspace is in the trash and false
	 * otherwise.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static boolean isUserWSInTrash(User user) {
		Workspace userWS = GwtServerHelper.getUserWorkspace(user);
		return ((null != userWS) && userWS.isPreDeleted());
	}
	
	/**
	 * Called to purge all the entities in the trash.
	 * 
	 * @param bs
	 * @param reqeust
	 * @param binderId
	 * @param purgeMirroredSources
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData trashPurgeAll(AllModulesInjected bs, HttpServletRequest reqeust, Long binderId, boolean purgeMirroredSources) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Purge the entities in the trash...
			TrashEntity[] trashEntities = TrashHelper.getAllTrashEntities(bs, binderId);
			TrashResponse tr = TrashHelper.purgeSelectedEntities(bs, trashEntities, purgeMirroredSources);
			if (tr.isError() || tr.m_rd.hasRenames()) {
				// ...and return any messages we get in response.
				reply.setStringValue(tr.getTrashMessage(bs));
			}

			// If we get here, reply refers to a StringRpcResponseData
			// containing any errors we encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtDeleteHelper.trashPurgeAll( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Called to purge the selected entities in the trash.
	 * 
	 * @param bs
	 * @param reqeust
	 * @param binderId
	 * @param purgeMirroredSources
	 * @param trashSelectionData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData trashPurgeSelectedEntities(AllModulesInjected bs, HttpServletRequest reqeust, Long binderId, boolean purgeMirroredSources, List<String> trashSelectionData) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Do we have any selections to purge?
			int count = ((null == trashSelectionData) ? 0 : trashSelectionData.size());
			if (0 < count) {
				// Yes!  Convert them to a TrashEntity[]...
				TrashEntity[] trashEntities = new TrashEntity[count];
				for (int i = 0; i < count; i += 1) {
					trashEntities[i] = new TrashEntity(trashSelectionData.get(i));
				}
				
				// ...purge those...
				TrashResponse tr = TrashHelper.purgeSelectedEntities(bs, trashEntities, purgeMirroredSources);
				if (tr.isError() || tr.m_rd.hasRenames()) {
					// ...and return any messages we get in response.
					reply.setStringValue(tr.getTrashMessage(bs));
				}
			}
			
			// If we get here, reply refers to a StringRpcResponseData
			// containing any errors we encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtDeleteHelper.trashPurgeSelectedEntities( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Called to restore all the entities in the trash.
	 * 
	 * @param bs
	 * @param reqeust
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData trashRestoreAll(AllModulesInjected bs, HttpServletRequest reqeust, Long binderId) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Restore the entities in the trash...
			TrashEntity[] trashEntities = TrashHelper.getAllTrashEntities(bs, binderId);
			TrashResponse tr = TrashHelper.restoreSelectedEntities(bs, trashEntities);
			if (tr.isError() || tr.m_rd.hasRenames()) {
				// ...and return any messages we get in response.
				reply.setStringValue(tr.getTrashMessage(bs));
			}

			// If we get here, reply refers to a StringRpcResponseData
			// containing any errors we encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtDeleteHelper.trashRestoreAll( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Called to restore the selected entities in the trash.
	 * 
	 * @param bs
	 * @param reqeust
	 * @param binderId
	 * @param trashSelectionData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData trashRestoreSelectedEntities(AllModulesInjected bs, HttpServletRequest reqeust, Long binderId, List<String> trashSelectionData) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Do we have any selections to restore?
			int count = ((null == trashSelectionData) ? 0 : trashSelectionData.size());
			if (0 < count) {
				// Yes!  Convert them to a TrashEntity[]...
				TrashEntity[] trashEntities = new TrashEntity[count];
				for (int i = 0; i < count; i += 1) {
					trashEntities[i] = new TrashEntity(trashSelectionData.get(i));
				}
				
				// ...restore those...
				TrashResponse tr = TrashHelper.restoreSelectedEntities(bs, trashEntities);
				if (tr.isError() || tr.m_rd.hasRenames()) {
					// ...and return any messages we get in response.
					reply.setStringValue(tr.getTrashMessage(bs));
				}
			}
			
			// If we get here, reply refers to a StringRpcResponseData
			// containing any errors we encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtDeleteHelper.trashRestoreSelectedEntities( SOURCE EXCEPTION ):  ");
		}
	}
}
