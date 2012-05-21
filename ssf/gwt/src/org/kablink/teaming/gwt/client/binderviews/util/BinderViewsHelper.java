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

package org.kablink.teaming.gwt.client.binderviews.util;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.ChangeEntryTypesDlg;
import org.kablink.teaming.gwt.client.binderviews.ChangeEntryTypesDlg.ChangeEntryTypesDlgClient;
import org.kablink.teaming.gwt.client.binderviews.CopyMoveEntriesDlg;
import org.kablink.teaming.gwt.client.binderviews.CopyMoveEntriesDlg.CopyMoveEntriesDlgClient;
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper.DeletePurgeEntriesCallback;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg.EmailNotificationDlgClient;
import org.kablink.teaming.gwt.client.rpc.shared.DisableUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.EnableUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.LockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetUnseenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UnlockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * Helper methods for binder views.
 *
 * @author drfoster@novell.com
 */
public class BinderViewsHelper {
	private static ChangeEntryTypesDlg	m_cetDlg;								// An instance of a change entry types dialog. 
	private static CopyMoveEntriesDlg	m_cmeDlg;								// An instance of a copy/move entries dialog.
	private static EmailNotificationDlg	m_enDlg;								// An instance of an email notification dialog used to subscribe to subscribe to the entries in a List<EntityId>. 
	private static GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	// Access to the GWT localized strings.
	private static ShareThisDlg			m_shareDlg;								// An instance of a share this dialog.
	
	/*
	 * Constructor method. 
	 */
	private BinderViewsHelper() {
		// Inhibits this class from being instantiated.
	}

	/**
	 * Invokes the appropriate UI to change the entry type of the
	 * entries based on a List<EntityId> of the entries.
	 *
	 * @param folderType
	 * @param entityIds
	 */
	public static void changeEntryTypes(final FolderType folderType, final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to entry whose types
		// are to be changed...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Have we instantiated a change entry types dialog yet?
		if (null == m_cetDlg) {
			// No!  Instantiate one now.
			ChangeEntryTypesDlg.createAsync(new ChangeEntryTypesDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final ChangeEntryTypesDlg cetDlg) {
					// ...and show it.
					m_cetDlg = cetDlg;
					ScheduledCommand doSubscribe = new ScheduledCommand() {
						@Override
						public void execute() {
							changeEntryTypesNow(entityIds);
						}
					};
					Scheduler.get().scheduleDeferred(doSubscribe);
				}
			});
		}
		
		else {
			// Yes, we've instantiated a change entry types dialog
			// already!  Simply show it.
			changeEntryTypesNow(entityIds);
		}
	}

	/*
	 * Synchronously invokes the appropriate UI to change the entry
	 * type of the entries based on a List<EntityId> of the entries.
	 */
	private static void changeEntryTypesNow(List<EntityId> entityIds) {
		ChangeEntryTypesDlg.initAndShow(m_cetDlg, entityIds);
	}
	
	/**
	 * Invokes the appropriate UI to copy the entries based on a
	 * List<EntityId> of the entries.
	 *
	 * @param folderType
	 * @param entityIds
	 */
	public static void copyEntries(final FolderType folderType, final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be copied...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Have we created a copy/move entries dialog yet?
		if (null == m_cmeDlg) {
			// No!  Create one now...
			CopyMoveEntriesDlg.createAsync(new CopyMoveEntriesDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(CopyMoveEntriesDlg cmeDlg) {
					// ...and run it to copy.
					m_cmeDlg = cmeDlg;
					showCMEDlgNow(m_cmeDlg, true, folderType, entityIds);
				}
			});
		}
		
		else {
			// Yes, we've created a copy/move entries dialog already!
			// Run it to copy.
			showCMEDlgNow(m_cmeDlg, true, folderType, entityIds);
		}
	}

	/**
	 * Deletes the folder entries based on a folder ID and List<Long>
	 * of their entity IDs.
	 *
	 * @param entityIds
	 */
	public static void deleteFolderEntries(final List<EntityId> entityIds, final DeletePurgeEntriesCallback dpeCallback) {
		// If we weren't given any entity IDs to be deleted...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Is the user sure they want to delete the folder entries?
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes, they're sure!  Perform the delete.
							DeletePurgeEntriesHelper.deleteSelectedEntriesAsync(
								entityIds,
								dpeCallback);
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.binderViewsConfirmDeleteEntries());
			}
		});
	}
	
	/**
	 * Deletes the user workspaces based on a List<Long> of their user
	 * IDs.
	 *
	 * @param userIds
	 */
	public static void deleteUserWorkspaces(final List<Long> userIds) {
		// If we weren't given any user IDs to be deleted...
		if ((null == userIds) || userIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Is the user sure they want to delete the selected user
		// workspaces?
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes, they're sure!  Perform the delete.
							DeletePurgeUsersHelper.deleteUserWorkspacesAsync(userIds);
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.binderViewsConfirmDeleteUserWS());
			}
		});
	}
	
	/**
	 * Disables the users based on a List<Long> of their user IDs.
	 *
	 * @param userIds
	 */
	public static void disableUsers(final List<Long> userIds) {
		// If we weren't given any user IDs to be disabled...
		if ((null == userIds) || userIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Show a busy spinner while we disable users.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to disable the users.
		DisableUsersCmd cmd = new DisableUsersCmd(userIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DisableUsers());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get disabled?
				busy.hide();
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> errors = responseData.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// No!  Tell the user about the problem.
					GwtClientHelper.displayMultipleErrors(m_messages.disableUsersError(), errors);
				}

				// If anything was disabled...
				if (count != userIds.size()) {
					// ...force the content to refresh just in case its
					// ...got something displayed that depends on it.
					FullUIReloadEvent.fireOne();
				}
			}
		});
	}

	/**
	 * Enables the users based on a List<Long> of their user IDs.
	 *
	 * @param userIds
	 */
	public static void enableUsers(final List<Long> userIds) {
		// If we weren't given any user IDs to be enabled...
		if ((null == userIds) || userIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Show a busy spinner while we enable users.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to enable the users.
		EnableUsersCmd cmd = new EnableUsersCmd(userIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_EnableUsers());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get enabled?
				busy.hide();
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> errors = responseData.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// No!  Tell the user about the problem.
					GwtClientHelper.displayMultipleErrors(m_messages.enableUsersError(), errors);
				}

				// If anything was enabled...
				if (count != userIds.size()) {
					// ...force the content to refresh just in case its
					// ...got something displayed that depends on it.
					FullUIReloadEvent.fireOne();
				}
			}
		});
	}

	/**
	 * Locks the entries based on a List<EntityId> of their entity IDs.
	 *
	 * @param folderType
	 * @param entityIds
	 */
	public static void lockEntries(final FolderType folderType, final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be locked...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Show a busy spinner while we lock entries.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to lock the entries.
		LockEntriesCmd cmd = new LockEntriesCmd(entityIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_LockEntries());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get locked?
				busy.hide();
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> errors = responseData.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// No!  Tell the user about the problem.
					GwtClientHelper.displayMultipleErrors(m_messages.lockEntriesError(), errors);
				}

				// If anything was locked...
				if (count != entityIds.size()) {
					// ...force the content to refresh just in case its
					// ...got something displayed that depends on
					// ...locks.
					FullUIReloadEvent.fireOne();
				}
			}
		});
	}

	/**
	 * Marks the entries read based on a List<Long> of their entity
	 * IDs.
	 *
	 * @param entityIds
	 */
	public static void markEntriesRead(List<Long> entityIds) {
		// If we weren't given any entity IDs to be marked read...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we mark entries read.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to mark the entries read.
		SetSeenCmd cmd = new SetSeenCmd(entityIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetSeen());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Simply force the content to refresh just in case its
				// got something displayed that depends based on an
				// entry's read/unread state.
				busy.hide();
				FullUIReloadEvent.fireOne();
			}
		});
	}

	/**
	 * Marks the entries unread based on a List<Long> of their entry
	 * IDs.
	 *
	 * @param entityIds
	 */
	public static void markEntriesUnread(List<Long> entityIds) {
		// If we weren't given any entity IDs to be marked unread...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we mark entries unread.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to mark the entries unread.
		SetUnseenCmd cmd = new SetUnseenCmd(entityIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetUnseen());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Simply force the content to refresh just in case its
				// got something displayed that depends based on an
				// entry's read/unread state.
				busy.hide();
				FullUIReloadEvent.fireOne();
			}
		});
	}

	/**
	 * Invokes the appropriate UI to move the entries based on a
	 * List<EntityId> of the entries.
	 *
	 * @param folderType
	 * @param entityIds
	 */
	public static void moveEntries(final FolderType folderType, final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be moved...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Have we created a copy/move entries dialog yet?
		if (null == m_cmeDlg) {
			// No!  Create one now...
			CopyMoveEntriesDlg.createAsync(new CopyMoveEntriesDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(CopyMoveEntriesDlg cmeDlg) {
					// ...and run it to move.
					m_cmeDlg = cmeDlg;
					showCMEDlgNow(m_cmeDlg, false, folderType, entityIds);
				}
			});
		}
		
		else {
			// Yes, we've created a copy/move entries dialog already!
			// Run it to move.
			showCMEDlgNow(m_cmeDlg, false, folderType, entityIds);
		}
	}
	
	/**
	 * Purges the folder entries based on a folder ID and List<Long> of
	 * the entity IDs.
	 *
	 * @param entityIds
	 */
	public static void purgeFolderEntries(final List<EntityId> entityIds, final DeletePurgeEntriesCallback dpeCallback) {
		// If we weren't given any entity IDs to be purged...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Is the user sure they want to purge the folder entries?
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes, they're sure!  Perform the purge.
							DeletePurgeEntriesHelper.purgeSelectedEntriesAsync(
								entityIds,
								dpeCallback);
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.binderViewsConfirmPurgeEntries());
			}
		});
	}

	/**
	 * Purges the user workspaces and user objects based on a
	 * List<Long> of their user IDs.
	 *
	 * @param userIds
	 */
	public static void purgeUsers(final List<Long> userIds) {
		// If we weren't given any user IDs to be purged...
		if ((null == userIds) || userIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Is the user sure they want to the selected user workspaces
		// and user objects?
		final CheckBox cb = new CheckBox(m_messages.binderViewsPromptPurgeMirroredFolders());
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes, they're sure!  Perform the purge.
							DeletePurgeUsersHelper.purgeUsersAsync(userIds, cb.getValue());
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.binderViewsConfirmPurgeUsers(),
					cb);
			}
		});
	}

	/**
	 * Purges the user workspaces based on a List<Long> of their user
	 * IDs.
	 *
	 * @param userIds
	 */
	public static void purgeUserWorkspaces(final List<Long> userIds) {
		// If we weren't given any user IDs to be purged...
		if ((null == userIds) || userIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Is the user sure they want to purge the selected user
		// workspaces?
		final CheckBox cb = new CheckBox(m_messages.binderViewsPromptPurgeMirroredFolders());
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes, they're sure!  Perform the purge.
							DeletePurgeUsersHelper.purgeUserWorkspacesAsync(userIds, cb.getValue());
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.binderViewsConfirmPurgeUserWS(),
					cb);
			}
		});
	}

	/**
	 * Invokes the appropriate UI to share the entries based on a
	 * List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void shareEntries(final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be shared...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}

		// If we've already instantiated a share dialog...
		if (null != m_shareDlg) {
			// ...simply invoke it.
			showShareDlgAsync(entityIds);
			return;
		}
		
		// Otherwise, we need to instantiate one now!  Note that the
		// 'Share This' dialog requires the FindCtrl be loaded before
		// it loads in order to function.  Prefetch the FindControl...
		ScheduledCommand doPrefetch = new ScheduledCommand() {
			@Override
			public void execute() {
				FindCtrl.prefetch(new FindCtrlClient()
				{
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(FindCtrl findCtrl) {
						// ...and create and show the dialog.
						m_shareDlg = new ShareThisDlg(false, true, 0, 0, m_messages.shareCaption());
						showShareDlgAsync(entityIds);
					}
				});
			}
		};
		Scheduler.get().scheduleDeferred( doPrefetch );
	}

	/*
	 * Initializes and shows and instance of the copy/move entries
	 * dialog.
	 */
	private static void showCMEDlgNow(final CopyMoveEntriesDlg cmeDlg, final boolean invokeToCopy, final FolderType folderType, final List<EntityId> entityIds) {
		CopyMoveEntriesDlg.initAndShow(
			cmeDlg,			// The dialog to show.
			invokeToCopy,	// true -> Run it do a copy.  false -> Run it to do a move.
			folderType,		// The type of folder that we're dealing with.
			entityIds);		// The List<EntityId> to be copied/moved.
	}
	
	/*
	 * Asynchronously shows the share dialog.
	 */
	private static void showShareDlgAsync(final List<EntityId> entityIds) {
		ScheduledCommand doShow = new ScheduledCommand() {
			@Override
			public void execute() {
				showShareDlgNow(entityIds);
			}
		};
		Scheduler.get().scheduleDeferred( doShow );
	}
	
	/*
	 * Synchronously shows the share dialog.
	 */
	private static void showShareDlgNow(List<EntityId> entityIds) {
		int entities = entityIds.size();
		m_shareDlg.setCaption(
			(1 < entities)                   ?
				m_messages.shareTheseItems() :
				m_messages.shareThisItem());
		m_shareDlg.showDlg(null, null, entityIds);
	}

	/**
	 * Invokes the appropriate UI to subscribe to the entries based on
	 * a List<EntityId> of the entries.
	 *
	 * @param folderType
	 * @param entityIds
	 */
	public static void subscribeToEntries(final FolderType folderType, final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be subscribed to...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Have we instantiated an email notification dialog yet?
		if (null == m_enDlg) {
			// No!  Instantiate one now.
			EmailNotificationDlg.createAsync(new EmailNotificationDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final EmailNotificationDlg enDlg) {
					// ...and show it.
					m_enDlg = enDlg;
					ScheduledCommand doSubscribe = new ScheduledCommand() {
						@Override
						public void execute() {
							subscribeToEntriesNow(entityIds);
						}
					};
					Scheduler.get().scheduleDeferred(doSubscribe);
				}
			});
		}
		
		else {
			// Yes, we've instantiated an email notification dialog
			// already!  Simply show it.
			subscribeToEntriesNow(entityIds);
		}
	}

	/*
	 * Synchronously invokes the appropriate UI to subscribe to the
	 * entries based on a List<EntityId> of the entries.
	 */
	private static void subscribeToEntriesNow(List<EntityId> entityIds) {
		EmailNotificationDlg.initAndShow(m_enDlg, entityIds);
	}
	
	/**
	 * Unlocks the entries based on a List<EntityId> of the entries.
	 *
	 * @param folderType
	 * @param entityIds
	 */
	public static void unlockEntries(final FolderType folderType, final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be unlocked...
		if ((null == entityIds) || entityIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Show a busy spinner while we unlock entries.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to unlock the entries.
		UnlockEntriesCmd cmd = new UnlockEntriesCmd(entityIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_UnlockEntries());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get unlocked?
				busy.hide();
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> errors = responseData.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// No!  Tell the user about the problem.
					GwtClientHelper.displayMultipleErrors(m_messages.unlockEntriesError(), errors);
				}

				// If anything was unlocked...
				if (count != entityIds.size()) {
					// ...force the content to refresh just in case its
					// ...got something displayed that depends on
					// ...locks.
					FullUIReloadEvent.fireOne();
				}
			}
		});
	}
}
