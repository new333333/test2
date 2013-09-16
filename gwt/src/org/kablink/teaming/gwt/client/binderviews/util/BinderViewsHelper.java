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
package org.kablink.teaming.gwt.client.binderviews.util;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.ChangeEntryTypesDlg;
import org.kablink.teaming.gwt.client.binderviews.ChangeEntryTypesDlg.ChangeEntryTypesDlgClient;
import org.kablink.teaming.gwt.client.binderviews.CopyMoveEntriesDlg;
import org.kablink.teaming.gwt.client.binderviews.CopyMoveEntriesDlg.CopyMoveEntriesDlgClient;
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper.DeletePurgeEntriesCallback;
import org.kablink.teaming.gwt.client.datatable.AddFilesDlg;
import org.kablink.teaming.gwt.client.datatable.AddFilesDlg.AddFilesDlgClient;
import org.kablink.teaming.gwt.client.datatable.AddFilesHtml5Popup;
import org.kablink.teaming.gwt.client.datatable.AddFilesHtml5Popup.AddFilesHtml5PopupClient;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg.EmailNotificationDlgClient;
import org.kablink.teaming.gwt.client.mainmenu.WhoHasAccessDlg;
import org.kablink.teaming.gwt.client.mainmenu.WhoHasAccessDlg.WhoHasAccessDlgClient;
import org.kablink.teaming.gwt.client.rpc.shared.DisableUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.EnableUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.HideSharesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMultipleAdhocFolderSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetUnseenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShowSharesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UnlockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg.ShareThisDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg.ShareThisDlgMode;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Helper methods for binder views.
 *
 * @author drfoster@novell.com
 */
public class BinderViewsHelper {
	private static AddFilesDlg			m_addFilesAppletDlg;					// An instance of the add files (via an applet) dialog.
	private static AddFilesHtml5Popup	m_addFilesHtml5Popup;					// An instance of the add files (via HTML5)     popup.
	private static ChangeEntryTypesDlg	m_cetDlg;								// An instance of a change entry types dialog. 
	private static CopyMoveEntriesDlg	m_cmeDlg;								// An instance of a copy/move entries dialog.
	private static EmailNotificationDlg	m_enDlg;								// An instance of an email notification dialog used to subscribe to subscribe to the entries in a List<EntityId>. 
	private static GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	// Access to the GWT localized strings.
	private static ShareThisDlg			m_shareDlg;								// An instance of a share this dialog.
	private static WhoHasAccessDlg		m_whaDlg;								// An instance of a who has access dialog used to view who has access to an entity. 

	// Controls whether a prompt is included in the purge user
	// workspace confirmation dialog allowing for the purging of the
	// source information for net folders.  true -> The prompt is
	// included.  false -> It's not.
	private static boolean PROMPT_PURGE_USER_WORKSPACE_NET_FOLDERS	= false;
	
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
	 * @param entityIds
	 * @param reloadEvent
	 */
	public static void changeEntryTypes(final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to entry whose types
		// are to be changed...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}
		
		// If there aren't any entries in the entity list...
		if (!(validateEntriesInEntityIds(entityIds))) {
			// ...bail.  (Note that validateEntriesInEntityIds() will
			// ...have told the user about any errors.)
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
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							changeEntryTypesAsync(entityIds, reloadEvent);
						}
					});
				}
			});
		}
		
		else {
			// Yes, we've instantiated a change entry types dialog
			// already!  Simply show it.
			changeEntryTypesAsync(entityIds, reloadEvent);
		}
	}
	
	public static void changeEntryTypes(final List<EntityId> entityIds) {
		// Always use the initial form of the method.
		changeEntryTypes(entityIds, null);
	}

	/*
	 * Asynchronously invokes the appropriate UI to change the entry
	 * type of the entries based on a List<EntityId> of the entries.
	 */
	private static void changeEntryTypesAsync(final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				changeEntryTypesNow(entityIds, reloadEvent);
			}
		});
	}
	
	/*
	 * Synchronously invokes the appropriate UI to change the entry
	 * type of the entries based on a List<EntityId> of the entries.
	 */
	private static void changeEntryTypesNow(List<EntityId> entityIds, VibeEventBase<?> reloadEvent) {
		ChangeEntryTypesDlg.initAndShow(m_cetDlg, entityIds, reloadEvent);
	}
	
	/**
	 * Clears the user's adHoc folders setting based on a List<Long> of
	 * their user IDs.
	 *
	 * @param userIds
	 */
	public static void clearUsersAdHocFolders(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be cleared...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we clear the adHoc folder
		// settings.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to clear the adHoc folders.
		SaveMultipleAdhocFolderSettingsCmd cmd = new SaveMultipleAdhocFolderSettingsCmd(userIds, null);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DisableUsersAdHocFolders());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  Simply hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void clearUsersAdHocFolders(final List<Long> userIds) {
		// Always use the initial form of the method.
		clearUsersAdHocFolders(userIds, null);
	}
	
	public static void clearUsersAdHocFolders(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		clearUsersAdHocFolders(userIds, reloadEvent);
	}

	public static void clearUsersAdHocFolders(final Long userId) {
		// Always use the previous form of the method.
		clearUsersAdHocFolders(userId, null);
	}

	/**
	 * Invokes the appropriate UI to copy the entries based on a
	 * List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void copyEntries(final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be copied...
		if (!(GwtClientHelper.hasItems(entityIds))) {
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
					showCMEDlgAsync(m_cmeDlg, true, entityIds, reloadEvent);
				}
			});
		}
		
		else {
			// Yes, we've created a copy/move entries dialog already!
			// Run it to copy.
			showCMEDlgAsync(m_cmeDlg, true, entityIds, reloadEvent);
		}
	}
	
	public static void copyEntries(final List<EntityId> entityIds) {
		// Always use the initialize form of the method.
		copyEntries(entityIds, null);
	}

	/**
	 * Deletes the folder entries based on a folder ID and List<Long>
	 * of their entity IDs.
	 *
	 * @param entityIds
	 */
	public static void deleteFolderEntries(final List<EntityId> entityIds, final DeletePurgeEntriesCallback dpeCallback) {
		// If we weren't given any entity IDs to be deleted...
		if (!(GwtClientHelper.hasItems(entityIds))) {
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
				String confirmationMsg;
				switch (entityIds.size()) {
				case 1:
					EntityId	eid     = entityIds.get(0);
					String		eidType = eid.getEntityType();
					if      (eidType.equals(EntityId.FOLDER))    confirmationMsg = m_messages.binderViewsConfirmDeleteFolder();
					else if (eidType.equals(EntityId.WORKSPACE)) confirmationMsg = m_messages.binderViewsConfirmDeleteWorkspace();
					else                                         confirmationMsg = m_messages.binderViewsConfirmDeleteEntry();
					break;
					
				default:
					confirmationMsg = m_messages.binderViewsConfirmDeleteEntries();
					break;
				}
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
					confirmationMsg);
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
		if (!(GwtClientHelper.hasItems(userIds))) {
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
		if (!(GwtClientHelper.hasItems(userIds))) {
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
					m_messages.rpcFailure_GetViewFolderEntryUrl());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get disabled?
				busy.hide();
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<ErrorInfo> errors = responseData.getErrorList();
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
	 * Disables the users based on a List<Long> of their user IDs.
	 *
	 * @param userIds
	 */
	public static void disableUsersAdHocFolders(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be disabled...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we disable adHoc folders.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to disable the adHoc folders.
		SaveMultipleAdhocFolderSettingsCmd cmd = new SaveMultipleAdhocFolderSettingsCmd(userIds, false);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DisableUsersAdHocFolders());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  Simply hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void disableUsersAdHocFolders(final List<Long> userIds) {
		// Always use the initial form of the method.
		disableUsersAdHocFolders(userIds, null);
	}
	
	public static void disableUsersAdHocFolders(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		disableUsersAdHocFolders(userIds, reloadEvent);
	}

	public static void disableUsersAdHocFolders(final Long userId) {
		// Always use the previous form of the method.
		disableUsersAdHocFolders(userId, null);
	}

	/**
	 * Enables the users based on a List<Long> of their user IDs.
	 *
	 * @param userIds
	 */
	public static void enableUsers(final List<Long> userIds) {
		// If we weren't given any user IDs to be enabled...
		if (!(GwtClientHelper.hasItems(userIds))) {
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
				List<ErrorInfo> errors = responseData.getErrorList();
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
	 * Enables adHoc folders for the users based on a List<Long> of
	 * their user IDs.
	 *
	 * @param userIds
	 */
	public static void enableUsersAdHocFolders(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be enable adHoc folders
		// on...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we enable adHoc folders.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to enable the adHoc folders.
		SaveMultipleAdhocFolderSettingsCmd cmd = new SaveMultipleAdhocFolderSettingsCmd(userIds, true);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_EnableUsersAdHocFolders());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  Simply hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void enableUsersAdHocFolders(final List<Long> userIds) {
		// Always use the initial form of the method.
		enableUsersAdHocFolders(userIds, null);
	}
	
	public static void enableUsersAdHocFolders(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		enableUsersAdHocFolders(userIds, reloadEvent);
	}
	
	public static void enableUsersAdHocFolders(final Long userId) {
		// Always use the previous form of the method.
		enableUsersAdHocFolders(userId, null);
	}

	/**
	 * Marks the shares hidden based on a List<Long> of their entity
	 * IDs.
	 *
	 * @param ct
	 * @param entityIds
	 * @param reloadEvent
	 */
	public static void hideSelectedShares(CollectionType ct, List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be hidden...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we hide shares.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to hide the shares.
		HideSharesCmd cmd = new HideSharesCmd(ct, entityIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_HideShares());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Simply force the content to refresh.
				busy.hide();
				if (null == reloadEvent)
				     FullUIReloadEvent.fireOneAsync();
				else GwtTeaming.fireEventAsync(reloadEvent);
			}
		});
	}
	
	public static void hideSelectedShares(CollectionType ct, List<EntityId> entityIds) {
		// Always use the initial form of the method.
		hideSelectedShares(ct, entityIds, null);
	}

	/**
	 * Invokes the 'Add Files' interface.
	 * 
	 * For browsers that support it, that will be the HTML5 file upload
	 * facility.  For all others, that will be the Java Applet.
	 * 
	 * For browsers that support HTML5, the applet can still be used by
	 * holding down the control key when this event is fired.
	 * Typically, the user would hold it down while click 'Add Files'
	 * on the entry menu.
	 * 
	 * @param folderInfo
	 * @param showRelativeWidget
	 */
	public static void invokeDropBox(final BinderInfo folderInfo, final UIObject showRelativeWidget) {
		// Are we running in a browser that support file uploads using
		// HTML5 and is the control key not pressed?
		if (AddFilesHtml5Popup.browserSupportsHtml5() && (!(GwtClientHelper.isControlKeyDown()))) {
			// Yes!  Have we instantiated an HTML5 add files popup yet?
			if (null == m_addFilesHtml5Popup) {
				// No!  Instantiate one now...
				AddFilesHtml5Popup.createAsync(new AddFilesHtml5PopupClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(final AddFilesHtml5Popup afPopup) {
						// ...and show it.
						m_addFilesHtml5Popup = afPopup;
						GwtClientHelper.deferCommand(new ScheduledCommand() {
							@Override
							public void execute() {
								showAddFilesPopupNow(folderInfo);
							}
						});
					}
				});
			}
			
			else {
				// Yes, we've instantiated an HTML5 add files popup
				// already!  Simply show it.
				showAddFilesPopupNow(folderInfo);
			}
		}
		
		else {
			// No, we aren't running in a browser that support file
			// uploads using HTML5!  Have we instantiated an applet
			// based add files dialog yet?
			if (null == m_addFilesAppletDlg) {
				// No!  Instantiate one now...
				AddFilesDlg.createAsync(new AddFilesDlgClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(final AddFilesDlg afDlg) {
						// ...and show it.
						m_addFilesAppletDlg = afDlg;
						GwtClientHelper.deferCommand(new ScheduledCommand() {
							@Override
							public void execute() {
								showAddFilesDlgNow(folderInfo, showRelativeWidget);
							}
						});
					}
				});
			}
			
			else {
				// Yes, we've instantiated an applet based add files
				// dialog already!  Simply show it.
				showAddFilesDlgNow(folderInfo, showRelativeWidget);
			}
		}
	}
	
	/**
	 * Invokes the Share dialog in administrative mode based on a
	 * List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void invokeManageSharesDlg(final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be shared...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}

		// Have we created a share dialog yet?
		if (null == m_shareDlg) {
			// No!  Create one now...
			ShareThisDlg.createAsync(new ShareThisDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(ShareThisDlg stDlg) {
					// ...and show it with the given entity IDs.
					m_shareDlg = stDlg;
					showManageSharesDlgAsync(entityIds);
				}
			});
		}
		else {
			// Yes, we've already create a share dialog!  Simply show
			// it with the given entry IDs.
			showManageSharesDlgAsync(entityIds);
		}
	}
	
	/**
	 * Locks the entries based on a List<EntityId> of their entity IDs.
	 *
	 * @param entityIds
	 * @param reloadEvent
	 */
	public static void lockEntries(final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be locked...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}

		// If there aren't any entries in the entity list...
		if (!(validateEntriesInEntityIds(entityIds))) {
			// ...bail.  (Note that validateEntriesInEntityIds() will
			// ...have told the user about any errors.)
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
				List<ErrorInfo> errors = responseData.getErrorList();
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
					if (null == reloadEvent)
					     FullUIReloadEvent.fireOneAsync();
					else GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void lockEntries(final List<EntityId> entityIds) {
		// Always use the initial form of the method.
		lockEntries(entityIds, null);
	}

	/**
	 * Marks the entries read based on a List<Long> of their entity
	 * IDs.
	 *
	 * @param entityIds
	 * @param reloadEvent
	 */
	public static void markEntriesRead(List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be marked read...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}
		
		// If there aren't any entries in the entity list...
		if (!(validateEntriesInEntityIds(entityIds))) {
			// ...bail.  (Note that validateEntriesInEntityIds() will
			// ...have told the user about any errors.)
			return;
		}
		
		// Show a busy spinner while we mark entries read.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to mark the entries read.
		SetSeenCmd cmd = new SetSeenCmd(EntityId.getEntryLongsFromEntityIds(entityIds));
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
				if (null == reloadEvent)
				     FullUIReloadEvent.fireOneAsync();
				else GwtTeaming.fireEventAsync(reloadEvent);
			}
		});
	}
	
	public static void markEntriesRead(List<EntityId> entityIds) {
		// Always use the initial form of the method.
		markEntriesRead(entityIds, null);
	}

	/**
	 * Marks the entries unread based on a List<Long> of their entry
	 * IDs.
	 *
	 * @param entityIds
	 * @param reloadEvent
	 */
	public static void markEntriesUnread(List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be marked unread...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}
		
		// If there aren't any entries in the entity list...
		if (!(validateEntriesInEntityIds(entityIds))) {
			// ...bail.  (Note that validateEntriesInEntityIds() will
			// ...have told the user about any errors.)
			return;
		}

		// Show a busy spinner while we mark entries unread.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to mark the entries unread.
		SetUnseenCmd cmd = new SetUnseenCmd(EntityId.getEntryLongsFromEntityIds(entityIds));
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
				if (null == reloadEvent)
				     FullUIReloadEvent.fireOneAsync();
				else GwtTeaming.fireEventAsync(reloadEvent);
			}
		});
	}
	
	public static void markEntriesUnread(List<EntityId> entityIds) {
		// Always use the initial form of the method.
		markEntriesUnread(entityIds, null);
	}

	/**
	 * Invokes the appropriate UI to move the entries based on a
	 * List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void moveEntries(final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be moved...
		if (!(GwtClientHelper.hasItems(entityIds))) {
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
					showCMEDlgAsync(m_cmeDlg, false, entityIds, reloadEvent);
				}
			});
		}
		
		else {
			// Yes, we've created a copy/move entries dialog already!
			// Run it to move.
			showCMEDlgAsync(m_cmeDlg, false, entityIds, reloadEvent);
		}
	}
	
	public static void moveEntries(final List<EntityId> entityIds) {
		// Always use the initial form of the method.
		moveEntries(entityIds, null);
	}
	
	/**
	 * Purges the folder entries based on a folder ID and List<Long> of
	 * the entity IDs.
	 *
	 * @param entityIds
	 */
	public static void purgeFolderEntries(final List<EntityId> entityIds, final DeletePurgeEntriesCallback dpeCallback) {
		// If we weren't given any entity IDs to be purged...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}
		
    	// If we need to, add a checkbox about purging mirrored sources.
		final CheckBox cb;
		final boolean purgeBinders = EntityId.areBindersInEntityIds(entityIds);
	    if (purgeBinders) {
	    	String caption;
	    	if (GwtClientHelper.isLicenseFilr())
	    	     caption = m_messages.vibeDataTable_TrashConfirmPurgeDeleteSourceOnMirroredSubFolders_Filr();
	    	else caption = m_messages.vibeDataTable_TrashConfirmPurgeDeleteSourceOnMirroredSubFolders_Vibe();
	    	cb = new CheckBox(caption);
	    }
	    else {
	    	cb = null;
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
				String confirmationMsg;
				switch (entityIds.size()) {
				case 1:
					EntityId	eid     = entityIds.get(0);
					String		eidType = eid.getEntityType();
					if      (eidType.equals(EntityId.FOLDER))    confirmationMsg = m_messages.binderViewsConfirmPurgeFolder();
					else if (eidType.equals(EntityId.WORKSPACE)) confirmationMsg = m_messages.binderViewsConfirmPurgeWorkspace();
					else                                         confirmationMsg = m_messages.binderViewsConfirmPurgeEntry();
					break;
					
				default:
					confirmationMsg = m_messages.binderViewsConfirmPurgeEntries();
					break;
				}
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
								((null == cb) ? false : cb.getValue()),
								dpeCallback);
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					confirmationMsg,
					cb);
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
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}

		// Is the user sure they want to the selected user workspaces
		// and user objects?
		CheckBox cb;
		if (PROMPT_PURGE_USER_WORKSPACE_NET_FOLDERS) {
			String caption;
			if (GwtClientHelper.isLicenseFilr())
			     caption = m_messages.binderViewsPromptPurgeMirroredFolders_Filr();
			else caption = m_messages.binderViewsPromptPurgeMirroredFolders_Vibe();
			cb = new CheckBox(caption);
		}
		else {
			cb = null;
		}
		final CheckBox finalCB = cb;
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
							DeletePurgeUsersHelper.purgeUsersAsync(
								userIds,
								((null == finalCB) ?
									false          :
									finalCB.getValue()));
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.binderViewsConfirmPurgeUsers(),
					finalCB);
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
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Is the user sure they want to purge the selected user
		// workspaces?
		CheckBox cb;
		if (PROMPT_PURGE_USER_WORKSPACE_NET_FOLDERS) {
			String caption;
			if (GwtClientHelper.isLicenseFilr())
			     caption = m_messages.binderViewsPromptPurgeMirroredFolders_Filr();
			else caption = m_messages.binderViewsPromptPurgeMirroredFolders_Vibe();
			cb = new CheckBox(caption);
		}
		else {
			cb = null;
		}
		final CheckBox finalCB = cb;
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
							DeletePurgeUsersHelper.purgeUserWorkspacesAsync(
								userIds,
								((null == finalCB) ?
									false          :
									finalCB.getValue()));
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.binderViewsConfirmPurgeUserWS(),
					finalCB);
			}
		});
	}

	/**
	 * Invokes the appropriate UI to share the entities based on a
	 * List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void shareEntities(final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be shared...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}

		// Have we created a copy/move entries dialog yet?
		if (null == m_shareDlg) {
			// No!  Create one now...
			ShareThisDlg.createAsync(new ShareThisDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ShareThisDlg stDlg) {
					// ...and show it with the given entity IDs.
					m_shareDlg = stDlg;
					showShareDlgAsync(entityIds);
				}
			});
		}
		
		else {
			// Yes, we've already create a share dialog!  Simply show
			// it with the given entry IDs.
			showShareDlgAsync(entityIds);
		}
	}
	
	/**
	 * Invokes the appropriate UI to share an entity based on an
	 * EntityId.
	 *
	 * @param entityId
	 */
	public static void shareEntity(EntityId entityId) {
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add(entityId);
		shareEntities(entityIds);
	}

	/*
	 * Synchronously shows the add files dialog.
	 */
	private static void showAddFilesDlgNow(final BinderInfo folderInfo, final UIObject showRelativeWidget) {
		AddFilesDlg.initAndShow(
			m_addFilesAppletDlg,
			folderInfo,
			showRelativeWidget);
	}
	
	/*
	 * Synchronously shows the HTML5 add files popup.
	 */
	private static void showAddFilesPopupNow(final BinderInfo folderInfo) {
		AddFilesHtml5Popup.initAndShow(
			m_addFilesHtml5Popup,
			folderInfo);
	}
	
	/*
	 * Asynchronously initializes and shows the copy/move entries
	 * dialog.
	 */
	private static void showCMEDlgAsync(final CopyMoveEntriesDlg cmeDlg, final boolean invokeToCopy, final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showCMEDlgNow(cmeDlg, invokeToCopy, entityIds, reloadEvent);
			}
		});
	}
	
	/*
	 * Synchronously initializes and shows the copy/move entries
	 * dialog.
	 */
	private static void showCMEDlgNow(final CopyMoveEntriesDlg cmeDlg, final boolean invokeToCopy, final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		CopyMoveEntriesDlg.initAndShow(
			cmeDlg,			// The dialog to show.
			invokeToCopy,	// true -> Run it do a copy.  false -> Run it to do a move.
			entityIds,		// The List<EntityId> to be copied/moved.
			reloadEvent);	// Event to fire to reload things after a successful operation.
	}
	
	/*
	 * Asynchronously shows the share dialog in administrative mode.
	 */
	private static void showManageSharesDlgAsync(final List<EntityId> entityIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showManageSharesDlgNow(entityIds);
			}
		});
	}
	
	/*
	 * Synchronously shows the share dialog in administrative mode.
	 */
	private static void showManageSharesDlgNow(List<EntityId> entityIds) {
		String caption = GwtClientHelper.patchMessage( m_messages.manageShares(), String.valueOf(entityIds.size()));
		ShareThisDlg.initAndShow(m_shareDlg, null, caption, null, entityIds, ShareThisDlgMode.MANAGE_SELECTED);
	}

	/*
	 * Asynchronously shows the share dialog.
	 */
	private static void showShareDlgAsync(final List<EntityId> entityIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showShareDlgNow(entityIds);
			}
		});
	}
	
	/*
	 * Synchronously shows the share dialog.
	 */
	private static void showShareDlgNow(List<EntityId> entityIds) {
		String caption = GwtClientHelper.patchMessage(m_messages.shareTheseItems(), String.valueOf(entityIds.size()));
		ShareThisDlg.initAndShow(m_shareDlg, null, caption, null, entityIds);
	}

	/**
	 * Marks the shares as not being hidden based on a List<Long> of
	 * their entity IDs.
	 *
	 * @param ct
	 * @param entityIds
	 * @param reloadEvent
	 */
	public static void showSelectedShares(CollectionType ct, List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be hidden...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we show shares.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to show the shares.
		ShowSharesCmd cmd = new ShowSharesCmd(ct, entityIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ShowShares());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Simply force the content to refresh.
				busy.hide();
				if (null == reloadEvent)
				     FullUIReloadEvent.fireOneAsync();
				else GwtTeaming.fireEventAsync(reloadEvent);
			}
		});
	}
	
	public static void showSelectedShares(CollectionType ct, List<EntityId> entityIds) {
		// Always use the initial form of the method.
		showSelectedShares(ct, entityIds, null);
	}

	/*
	 * Asynchronously invokes the who has access dialog on an entity.
	 */
	private static void showWhoHasAccessAsync(final EntityId entityId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showWhoHasAccessNow(entityId);
			}
		});
	}
	
	/*
	 * Synchronously invokes the who has access dialog on an entity.
	 */
	private static void showWhoHasAccessNow(final EntityId entityId) {
		WhoHasAccessDlg.initAndShow(m_whaDlg, entityId);
	}
	
	/**
	 * Invokes the appropriate UI to subscribe to the entries based on
	 * a List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void subscribeToEntries(final List<EntityId> entityIds, final UIObject showRelativeTo) {
		// If we weren't given any entity IDs to be subscribed to...
		if (!(GwtClientHelper.hasItems(entityIds))) {
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
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							subscribeToEntriesAsync(entityIds, showRelativeTo);
						}
					});
				}
			});
		}
		
		else {
			// Yes, we've instantiated an email notification dialog
			// already!  Simply show it.
			subscribeToEntriesAsync(entityIds, showRelativeTo);
		}
	}
	
	public static void subscribeToEntries(final List<EntityId> entityIds) {
		// Always use the initial form of the method.
		subscribeToEntries(entityIds, null);
	}

	/*
	 * Asynchronously invokes the appropriate UI to subscribe to the
	 * entries based on a List<EntityId> of the entries.
	 */
	private static void subscribeToEntriesAsync(final List<EntityId> entityIds, final UIObject showRelativeTo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				subscribeToEntriesNow(entityIds, showRelativeTo);
			}
		});
	}
	
	/*
	 * Synchronously invokes the appropriate UI to subscribe to the
	 * entries based on a List<EntityId> of the entries.
	 */
	private static void subscribeToEntriesNow(final List<EntityId> entityIds, final UIObject showRelativeTo) {
		EmailNotificationDlg.initAndShow(m_enDlg, entityIds, showRelativeTo);
	}
	
	/**
	 * Unlocks the entries based on a List<EntityId> of the entries.
	 *
	 * @param entityIds
	 * @param reloadEvent
	 */
	public static void unlockEntries(final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be unlocked...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}

		// If there aren't any entries in the entity list...
		if (!(validateEntriesInEntityIds(entityIds))) {
			// ...bail.  (Note that validateEntriesInEntityIds() will
			// ...have told the user about any errors.)
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
				List<ErrorInfo> errors = responseData.getErrorList();
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
					if (null == reloadEvent)
					     FullUIReloadEvent.fireOneAsync();
					else GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void unlockEntries(final List<EntityId> entityIds) {
		// Always use the initial form of the method.
		unlockEntries(entityIds, null);
	}

	/*
	 * Validates a List<EntityId> for containing entry references.
	 */
	private static boolean validateEntriesInEntityIds(List<EntityId> entityIds) {
		// If the list contains no entries...
		boolean hasEntries = EntityId.areEntriesInEntityIds(entityIds);
		if (!hasEntries) {
			// ...tell the user about the problem and return false.
			GwtClientHelper.deferredAlert(m_messages.vibeEntryMenu_Warning_OnlyFolders());
			return false;
		}

		// If the list contains any binders...
		boolean hasBinders = EntityId.areBindersInEntityIds(entityIds);
		if (hasBinders) {
			// ...tell the user they'll be ignored and remove them from
			// ...the list.
			GwtClientHelper.deferredAlert(m_messages.vibeEntryMenu_Warning_FoldersIgnored());
			EntityId.removeBindersFromEntityIds(entityIds);
		}

		// If we get here, the list contained entry references.  Return
		// true.
		return true;
	}

	/**
	 * Runs the entry viewer on the given entity.
	 * 
	 * @param entityId
	 */
	public static void viewEntry(final EntityId entityId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				viewEntryNow(entityId);
			}
		});
	}
	
	public static void viewEntry(Long folderId, Long entryId) {
		// Always use the initialize form of the method.
		viewEntry(new EntityId(folderId, entryId, EntityId.FOLDER_ENTRY));
	}
	
	/*
	 * Runs the entry viewer on the given entity.
	 * 
	 * @param entityId
	 */
	private static void viewEntryNow(final EntityId entityId) {
		// Get the URL to view the entry...
		GetViewFolderEntryUrlCmd cmd = new GetViewFolderEntryUrlCmd(entityId.getBinderId(), entityId.getEntityId());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetViewFolderEntryUrl(),
					String.valueOf(entityId.getEntityId()));
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...and fire the event to run the viewer.
				StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
				String viewUrl = responseData.getStringValue();
				if (GwtClientHelper.hasString(viewUrl)) {
					GwtTeaming.fireEventAsync(new ViewForumEntryEvent(viewUrl));
				}
			}
		});
	}
	
	/**
	 * Asynchronously runs the who has access viewer on the given
	 * entity.
	 * 
	 * @param entityId
	 */
	public static void viewWhoHasAccess(final EntityId entityId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				viewWhoHasAccessNow(entityId);
			}
		});
	}
	
	/*
	 * Synchronously runs the who has access viewer on the given
	 * entity.
	 */
	private static void viewWhoHasAccessNow(final EntityId entityId) {
		// Have we instantiated a who has access dialog yet?
		if (null == m_whaDlg) {
			// No!  Instantiate one now.
			WhoHasAccessDlg.createAsync(new WhoHasAccessDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final WhoHasAccessDlg whaDlg) {
					// ...and show it.
					m_whaDlg = whaDlg;
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							showWhoHasAccessAsync(entityId);
						}
					});
				}
			});
		}
		
		else {
			// Yes, we've instantiated a who has access dialog already!
			// Simply show it.
			showWhoHasAccessAsync(entityId);
		}
	}
}
