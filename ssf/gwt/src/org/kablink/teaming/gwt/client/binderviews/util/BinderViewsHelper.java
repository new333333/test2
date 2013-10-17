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
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.ChangeEntryTypesDlg;
import org.kablink.teaming.gwt.client.binderviews.ChangeEntryTypesDlg.ChangeEntryTypesDlgClient;
import org.kablink.teaming.gwt.client.binderviews.CopyMoveEntriesDlg;
import org.kablink.teaming.gwt.client.binderviews.CopyMoveEntriesDlg.CopyMoveEntriesDlgClient;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteEntitiesHelper.DeleteEntitiesCallback;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteUsersHelper.DeleteUsersCallback;
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
import org.kablink.teaming.gwt.client.rpc.shared.GetZipDownloadFilesUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetZipDownloadFolderUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.HideSharesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMultipleAdhocFolderSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMultipleDownloadSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMultiplePublicCollectionSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMultipleWebAccessSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetUnseenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShowSharesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UnlockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ZipDownloadUrlRpcResponseData;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntityRights;
import org.kablink.teaming.gwt.client.util.EntityRights.ShareRight;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.CopyPublicLinkDlg;
import org.kablink.teaming.gwt.client.widgets.CopyPublicLinkDlg.CopyPublicLinkDlgClient;
import org.kablink.teaming.gwt.client.widgets.DeleteSelectedUsersDlg;
import org.kablink.teaming.gwt.client.widgets.DeleteSelectedUsersDlg.DeleteSelectedUsersDlgClient;
import org.kablink.teaming.gwt.client.widgets.DeleteSelectionsDlg;
import org.kablink.teaming.gwt.client.widgets.DeleteSelectionsDlg.DeleteSelectionsDlgClient;
import org.kablink.teaming.gwt.client.widgets.EmailPublicLinkDlg;
import org.kablink.teaming.gwt.client.widgets.EmailPublicLinkDlg.EmailPublicLinkDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2.ShareThisDlg2Client;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Helper methods for binder views.
 *
 * @author drfoster@novell.com
 */
public class BinderViewsHelper {
	private static AddFilesDlg				m_addFilesAppletDlg;					// An instance of the add files (via an applet) dialog.
	private static AddFilesHtml5Popup		m_addFilesHtml5Popup;					// An instance of the add files (via HTML5)     popup.
	private static ChangeEntryTypesDlg		m_cetDlg;								// An instance of a change entry types dialog. 
	private static CopyMoveEntriesDlg		m_cmeDlg;								// An instance of a copy/move entries dialog.
	private static CopyPublicLinkDlg		m_copyPublicLinkDlg;					// An instance of a copy public link dialog.
	private static DeleteSelectedUsersDlg	m_dsuDlg;								// An instance of a delete selected users dialog.
	private static DeleteSelectionsDlg		m_dsDlg;								// An instance of a delete selections dialog.
	private static EmailNotificationDlg		m_enDlg;								// An instance of an email notification dialog used to subscribe to subscribe to the entries in a List<EntityId>. 
	private static EmailPublicLinkDlg		m_emailPublicLinkDlg;					// An instance of an email public link dialog.
	private static GwtTeamingMessages		m_messages = GwtTeaming.getMessages();	// Access to the GWT localized strings.
	private static ShareThisDlg2			m_shareDlg;								// An instance of a share this dialog.
	private static WhoHasAccessDlg			m_whaDlg;								// An instance of a who has access dialog used to view who has access to an entity. 

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
					m_messages.rpcFailure_ClearUsersAdHocFolders());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingAdHocFolders(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
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
	 * Clears the user's download files setting based on a List<Long>
	 * of their user IDs.
	 *
	 * @param userIds
	 */
	public static void clearUsersDownload(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be cleared...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we clear the download settings.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to clear the download setting.
		SaveMultipleDownloadSettingsCmd cmd = new SaveMultipleDownloadSettingsCmd(userIds, null);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ClearUsersDownload());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingDownload(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void clearUsersDownload(final List<Long> userIds) {
		// Always use the initial form of the method.
		clearUsersDownload(userIds, null);
	}
	
	public static void clearUsersDownload(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		clearUsersDownload(userIds, reloadEvent);
	}

	public static void clearUsersDownload(final Long userId) {
		// Always use the previous form of the method.
		clearUsersDownload(userId, null);
	}

	/**
	 * Clears the user's public collection setting based on a
	 * List<Long> of their user IDs.
	 *
	 * @param userIds
	 */
	public static void clearUsersPublicCollection(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be cleared...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we clear the web access settings.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to clear the public collection setting.
		SaveMultiplePublicCollectionSettingsCmd cmd = new SaveMultiplePublicCollectionSettingsCmd(userIds, null);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ClearUsersPublicCollection());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingPublicCollection(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void clearUsersPublicCollection(final List<Long> userIds) {
		// Always use the initial form of the method.
		clearUsersPublicCollection(userIds, null);
	}
	
	public static void clearUsersPublicCollection(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		clearUsersPublicCollection(userIds, reloadEvent);
	}

	public static void clearUsersPublicCollection(final Long userId) {
		// Always use the previous form of the method.
		clearUsersPublicCollection(userId, null);
	}

	/**
	 * Clears the user's web access setting based on a List<Long>
	 * of their user IDs.
	 *
	 * @param userIds
	 */
	public static void clearUsersWebAccess(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be cleared...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we clear the web access settings.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to clear the web access setting.
		SaveMultipleWebAccessSettingsCmd cmd = new SaveMultipleWebAccessSettingsCmd(userIds, null);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ClearUsersWebAccess());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingWebAccess(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void clearUsersWebAccess(final List<Long> userIds) {
		// Always use the initial form of the method.
		clearUsersWebAccess(userIds, null);
	}
	
	public static void clearUsersWebAccess(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		clearUsersWebAccess(userIds, reloadEvent);
	}

	public static void clearUsersWebAccess(final Long userId) {
		// Always use the previous form of the method.
		clearUsersWebAccess(userId, null);
	}

	/**
	 * Invokes the appropriate UI to copy the public link of the
	 * entities based on a List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void copyEntitiesPublicLink(final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be shared...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}

		// Have we created a copy public link dialog yet?
		if (null == m_copyPublicLinkDlg) {
			// No!  Create one now...
			CopyPublicLinkDlg.createAsync(new CopyPublicLinkDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(CopyPublicLinkDlg cplDlg) {
					// ...and show it with the given entity IDs.
					m_copyPublicLinkDlg = cplDlg;
					showCopyPublicLinkDlgAsync(entityIds);
				}
			});
		}
		
		else {
			// Yes, we've already create a copy public link dialog!
			// Simply show it with the given entry IDs.
			showCopyPublicLinkDlgAsync(entityIds);
		}
	}
	
	/**
	 * Invokes the appropriate UI to copy the public link of an entity
	 * based on an EntityId.
	 *
	 * @param entityId
	 */
	public static void copyEntityPublicLink(EntityId entityId) {
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add(entityId);
		copyEntitiesPublicLink(entityIds);
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
	 * Deletes the users based on a List<Long> of their IDs.
	 *
	 * @param userIds
	 */
	public static void deleteSelectedUsers(final List<Long> userIds, final DeleteUsersCallback dpuCallback) {
		// If we weren't given any user IDs to be deleted...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}

		// Have we created an instance of the delete selection users
		// dialog yet?
		if (null == m_dsuDlg) {
			// No!  Create one now...
			DeleteSelectedUsersDlg.createAsync(new DeleteSelectedUsersDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(DeleteSelectedUsersDlg dsuDlg) {
					// ...and run it.
					m_dsuDlg = dsuDlg;
					DeleteSelectedUsersDlg.initAndShow(m_dsuDlg, userIds, dpuCallback);
				}
			});
			
		}
		
		else {
			// Yes, we already have instance of one!  Simply run
			// it.
			DeleteSelectedUsersDlg.initAndShow(m_dsuDlg, userIds, dpuCallback);
		}
	}
	
	/**
	 * Deletes the entities based on a List<EntityId> of the entities.
	 *
	 * @param entityIds
	 */
	public static void deleteSelections(final List<EntityId> entityIds, final DeleteEntitiesCallback deCallback) {
		// If we weren't given any entity IDs to be deleted...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}

		// Have we created an instance of the delete selections dialog
		// yet?
		if (null == m_dsDlg) {
			// No!  Create one now...
			DeleteSelectionsDlg.createAsync(new DeleteSelectionsDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(DeleteSelectionsDlg dsDlg) {
					// ...and run it.
					m_dsDlg = dsDlg;
					DeleteSelectionsDlg.initAndShow(m_dsDlg, entityIds, deCallback);
				}
			});
			
		}
		
		else {
			// Yes, we already have instance of one!  Simply run
			// it.
			DeleteSelectionsDlg.initAndShow(m_dsDlg, entityIds, deCallback);
		}
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
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingAdHocFolders(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
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
	 * Disables the user's download files ability based on a List<Long>
	 * of their user IDs.
	 *
	 * @param userIds
	 */
	public static void disableUsersDownload(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be disabled...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we disable the download setting.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to disable the download setting.
		SaveMultipleDownloadSettingsCmd cmd = new SaveMultipleDownloadSettingsCmd(userIds, false);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DisableUsersDownload());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingDownload(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void disableUsersDownload(final List<Long> userIds) {
		// Always use the initial form of the method.
		disableUsersDownload(userIds, null);
	}
	
	public static void disableUsersDownload(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		disableUsersDownload(userIds, reloadEvent);
	}

	public static void disableUsersDownload(final Long userId) {
		// Always use the previous form of the method.
		disableUsersDownload(userId, null);
	}

	/**
	 * Disables the user's public collection based on a List<Long>
	 * of their user IDs.
	 *
	 * @param userIds
	 */
	public static void disableUsersPublicCollection(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be disabled...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we disable the web access setting.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to disable the public collection setting.
		SaveMultiplePublicCollectionSettingsCmd cmd = new SaveMultiplePublicCollectionSettingsCmd(userIds, false);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DisableUsersPublicCollection());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingPublicCollection(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void disableUsersPublicCollection(final List<Long> userIds) {
		// Always use the initial form of the method.
		disableUsersPublicCollection(userIds, null);
	}
	
	public static void disableUsersPublicCollection(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		disableUsersPublicCollection(userIds, reloadEvent);
	}

	public static void disableUsersPublicCollection(final Long userId) {
		// Always use the previous form of the method.
		disableUsersPublicCollection(userId, null);
	}

	/**
	 * Disables the user's web access based on a List<Long>
	 * of their user IDs.
	 *
	 * @param userIds
	 */
	public static void disableUsersWebAccess(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be disabled...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we disable the web access setting.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to disable the web access setting.
		SaveMultipleWebAccessSettingsCmd cmd = new SaveMultipleWebAccessSettingsCmd(userIds, false);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DisableUsersWebAccess());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingWebAccess(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void disableUsersWebAccess(final List<Long> userIds) {
		// Always use the initial form of the method.
		disableUsersWebAccess(userIds, null);
	}
	
	public static void disableUsersWebAccess(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		disableUsersWebAccess(userIds, reloadEvent);
	}

	public static void disableUsersWebAccess(final Long userId) {
		// Always use the previous form of the method.
		disableUsersWebAccess(userId, null);
	}

	/**
	 * Invokes the appropriate UI to email the public link of the
	 * entities based on a List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void emailEntitiesPublicLink(final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be shared...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}

		// Have we created a email public link dialog yet?
		if (null == m_emailPublicLinkDlg) {
			// No!  Create one now...
			EmailPublicLinkDlg.createAsync( false, true, new EmailPublicLinkDlgClient( ) {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(EmailPublicLinkDlg eplDlg) {
					// ...and show it with the given entity IDs.
					m_emailPublicLinkDlg = eplDlg;
					showEmailPublicLinkDlgAsync(entityIds);
				}
			});
		}
		
		else {
			// Yes, we've already create a email public link dialog!
			// Simply show it with the given entry IDs.
			showEmailPublicLinkDlgAsync(entityIds);
		}
	}
	
	/**
	 * Invokes the appropriate UI to email the public link of an entity
	 * based on an EntityId.
	 *
	 * @param entityId
	 */
	public static void emailEntityPublicLink(EntityId entityId) {
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add(entityId);
		emailEntitiesPublicLink(entityIds);
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
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingAdHocFolders(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
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
	 * Enables download files for the users based on a List<Long> of
	 * their user IDs.
	 *
	 * @param userIds
	 */
	public static void enableUsersDownload(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be enable downloads
		// on...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we enable the download setting.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to enable the download setting.
		SaveMultipleDownloadSettingsCmd cmd = new SaveMultipleDownloadSettingsCmd(userIds, true);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_EnableUsersDownload());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingDownload(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void enableUsersDownload(final List<Long> userIds) {
		// Always use the initial form of the method.
		enableUsersDownload(userIds, null);
	}
	
	public static void enableUsersDownload(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		enableUsersDownload(userIds, reloadEvent);
	}
	
	public static void enableUsersDownload(final Long userId) {
		// Always use the previous form of the method.
		enableUsersDownload(userId, null);
	}

	/**
	 * Enables public collection for the users based on a List<Long> of
	 * their user IDs.
	 *
	 * @param userIds
	 */
	public static void enableUsersPublicCollection(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be enable web access
		// on...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we enable the web access setting.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to enable public collection setting.
		SaveMultiplePublicCollectionSettingsCmd cmd = new SaveMultiplePublicCollectionSettingsCmd(userIds, true);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_EnableUsersPublicCollection());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingPublicCollection(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void enableUsersPublicCollection(final List<Long> userIds) {
		// Always use the initial form of the method.
		enableUsersPublicCollection(userIds, null);
	}
	
	public static void enableUsersPublicCollection(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		enableUsersPublicCollection(userIds, reloadEvent);
	}
	
	public static void enableUsersPublicCollection(final Long userId) {
		// Always use the previous form of the method.
		enableUsersPublicCollection(userId, null);
	}

	/**
	 * Enables web access for the users based on a List<Long> of
	 * their user IDs.
	 *
	 * @param userIds
	 */
	public static void enableUsersWebAccess(final List<Long> userIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any user IDs to be enable web access
		// on...
		if (!(GwtClientHelper.hasItems(userIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we enable the web access setting.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to enable web access setting.
		SaveMultipleWebAccessSettingsCmd cmd = new SaveMultipleWebAccessSettingsCmd(userIds, true);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_EnableUsersWebAccess());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				ErrorListRpcResponseData erList = ((ErrorListRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them.
					GwtClientHelper.displayMultipleErrors(m_messages.binderViewsHelper_failureSettingWebAccess(), erList.getErrorList());
				}
				
				// ...and hide the busy spinner.
				busy.hide();
				if (null != reloadEvent) {
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	public static void enableUsersWebAccess(final List<Long> userIds) {
		// Always use the initial form of the method.
		enableUsersWebAccess(userIds, null);
	}
	
	public static void enableUsersWebAccess(final Long userId, final VibeEventBase<?> reloadEvent) {
		// Always use the initial form of the method.
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		enableUsersWebAccess(userIds, reloadEvent);
	}
	
	public static void enableUsersWebAccess(final Long userId) {
		// Always use the previous form of the method.
		enableUsersWebAccess(userId, null);
	}

	/**
	 * Returns a count of the entities that can't be shared because
	 * they're Net Folders.
	 * 
	 * @param entities
	 * @param entityRightsMap
	 * 
	 * @return
	 */
	public static int getNetFolderShareFailureCount(final List<EntityId> entities, final Map<String, EntityRights> entityRightsMap) {
		int reply = 0;
		if (GwtClientHelper.hasItems(entities) && GwtClientHelper.hasItems(entityRightsMap)) {
			for (EntityId eid:  entities) {
				if (eid.isFolder()) {
					EntityRights er = entityRightsMap.get(EntityRights.getEntityRightsKey(eid));
					ShareRight   sr = ((null == er) ? null : er.getShareRight());
					if ((null != sr) && sr.cantShareNetFolder()) {
						reply += 1;
					}
				}
			}
		}
		return reply;
	}
	
	/**
	 * Returns a count of the entities that can't have their public
	 * link copied or e-mailed because they're Folders.
	 * 
	 * @param entities
	 * 
	 * @return
	 */
	public static int getFolderPublicLinkFailureCount(final List<EntityId> entities) {
		int reply = 0;
		if (GwtClientHelper.hasItems(entities)) {
			for (EntityId eid:  entities) {
				if (eid.isFolder()) {
					reply += 1;
				}
			}
		}
		return reply;
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
		if (GwtClientHelper.jsBrowserSupportsHtml5FileAPIs() && (!(GwtClientHelper.isControlKeyDown()))) {
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
			ShareThisDlg2.createAsync( false, true, new ShareThisDlg2Client() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(ShareThisDlg2 stDlg) {
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
			ShareThisDlg2.createAsync( false, true, new ShareThisDlg2Client() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ShareThisDlg2 stDlg) {
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
	 * Asynchronously shows the copy public link dialog.
	 */
	private static void showCopyPublicLinkDlgAsync(final List<EntityId> entityIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showCopyPublicLinkDlgNow(entityIds);
			}
		});
	}
	
	/*
	 * Synchronously shows the copy public link dialog.
	 */
	private static void showCopyPublicLinkDlgNow(List<EntityId> entityIds) {
		String caption = GwtClientHelper.patchMessage(
			m_messages.copyPublicLinkTheseItems(GwtClientHelper.getProductName()),
			String.valueOf(entityIds.size()));
		CopyPublicLinkDlg.initAndShow(m_copyPublicLinkDlg, caption, entityIds);
	}

	/*
	 * Asynchronously shows the email public link dialog.
	 */
	private static void showEmailPublicLinkDlgAsync(final List<EntityId> entityIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showEmailPublicLinkDlgNow(entityIds);
			}
		});
	}
	
	/*
	 * Synchronously shows the email public link dialog.
	 */
	private static void showEmailPublicLinkDlgNow(List<EntityId> entityIds) {
		if ( m_emailPublicLinkDlg != null )
		{
			String caption;

			caption = GwtClientHelper.patchMessage(
											m_messages.emailPublicLinkTheseItems(GwtClientHelper.getProductName()),
											String.valueOf(entityIds.size()));
			m_emailPublicLinkDlg.init( caption, entityIds );
			m_emailPublicLinkDlg.show( true );
		}
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
		String caption = GwtClientHelper.patchMessage(m_messages.manageShares(), String.valueOf(entityIds.size()));
		m_shareDlg.init(caption, entityIds, ShareThisDlg2.ShareThisDlgMode.MANAGE_SELECTED);
		m_shareDlg.showDlg(null);
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
		m_shareDlg.init(caption, entityIds, ShareThisDlg2.ShareThisDlgMode.NORMAL);
		m_shareDlg.showDlg(null);
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
	private static boolean validateEntriesInEntityIds(List<EntityId> entityIds, boolean requiresFiles) {
		// If the list contains no entries...
		boolean hasEntries = EntityId.areEntriesInEntityIds(entityIds);
		if (!hasEntries) {
			// ...tell the user about the problem and return false.
			GwtClientHelper.deferredAlert(
				(requiresFiles                                           ?
					m_messages.vibeEntryMenu_Warning_OnlyFolders_Files() :
					m_messages.vibeEntryMenu_Warning_OnlyFolders_Entries()));
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
	
	private static boolean validateEntriesInEntityIds(List<EntityId> entityIds) {
		// Always use the initial form of the method.
		return validateEntriesInEntityIds(entityIds, false);
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
	
	/**
	 * Zips and downloads the selected files and folder based on a
	 * List<EntityId> of their entity IDs.
	 *
	 * @param entityIds
	 * @param recursive
	 * @param reloadEvent
	 */
	public static void zipAndDownloadFiles(final FormPanel downloadForm, List<EntityId> entityIds, boolean recursive, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be downloaded...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we build the information for
		// downloading the files.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request for the zip download URL.
		GetZipDownloadFilesUrlCmd cmd = new GetZipDownloadFilesUrlCmd(entityIds, recursive);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetZipDownloadUrl());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// If we got any errors creating the URL to download
				// the zip...
				busy.hide();
				ZipDownloadUrlRpcResponseData	zipDownloadInfo = ((ZipDownloadUrlRpcResponseData) response.getResponseData());
				ErrorListRpcResponseData		errorList       = zipDownloadInfo.getErrors();
				List<ErrorInfo>					errors          = errorList.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// ...tell the user.
					GwtClientHelper.displayMultipleErrors(m_messages.zipDownloadUrlError(), errors);
				}

				// If we get the URL to download the zip...
				String zipDownloadUrl = zipDownloadInfo.getUrl();
				if (GwtClientHelper.hasString(zipDownloadUrl)) {
					// ...start it downloading...
					downloadForm.setAction(zipDownloadUrl);
					downloadForm.submit();
				}
			}
		});
	}
	
	public static void zipAndDownloadFiles(FormPanel downloadForm, List<EntityId> entityIds, boolean recursive) {
		// Always use the initial form of the method.
		zipAndDownloadFiles(downloadForm, entityIds, recursive, null);
	}
	
	/**
	 * Zips and downloads the files in a folder.
	 *
	 * @param folderId
	 * @param recursive
	 * @param reloadEvent
	 */
	public static void zipAndDownloadFolder(final FormPanel downloadForm, Long folderId, boolean recursive, final VibeEventBase<?> reloadEvent) {
		// Show a busy spinner while we build the information for
		// downloading the files.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request for the zip download URL.
		GetZipDownloadFolderUrlCmd cmd = new GetZipDownloadFolderUrlCmd(folderId, recursive);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetZipDownloadUrl());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// If we got any errors creating the URL to download
				// the zip...
				busy.hide();
				ZipDownloadUrlRpcResponseData	zipDownloadInfo = ((ZipDownloadUrlRpcResponseData) response.getResponseData());
				ErrorListRpcResponseData		errorList       = zipDownloadInfo.getErrors();
				List<ErrorInfo>					errors          = errorList.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// ...tell the user.
					GwtClientHelper.displayMultipleErrors(m_messages.zipDownloadUrlError(), errors);
				}

				// If we get the URL to download the zip...
				String zipDownloadUrl = zipDownloadInfo.getUrl();
				if (GwtClientHelper.hasString(zipDownloadUrl)) {
					// ...start it downloading...
					downloadForm.setAction(zipDownloadUrl);
					downloadForm.submit();
				}
			}
		});
	}
	
	public static void zipAndDownloadFolder(FormPanel downloadForm, Long folderId, boolean recursive) {
		// Always use the initial form of the method.
		zipAndDownloadFolder(downloadForm, folderId, recursive, null);
	}
}
