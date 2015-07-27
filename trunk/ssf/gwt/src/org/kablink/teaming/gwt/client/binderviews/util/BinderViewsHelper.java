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
import org.kablink.teaming.gwt.client.event.InvokeEditInPlaceEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg.EmailNotificationDlgClient;
import org.kablink.teaming.gwt.client.mainmenu.WhoHasAccessDlg;
import org.kablink.teaming.gwt.client.mainmenu.WhoHasAccessDlg.WhoHasAccessDlgClient;
import org.kablink.teaming.gwt.client.rpc.shared.DisableUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.DownloadFolderAsCSVFileUrlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EnableUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.ForceFilesUnlockCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetDownloadFolderAsCSVFileUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMailToPublicLinksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.MailToPublicLinksRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.MarkFolderContentsReadCmd;
import org.kablink.teaming.gwt.client.rpc.shared.MarkFolderContentsUnreadCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetZipDownloadFilesUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetZipDownloadFolderUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.HideSharesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMultipleAdhocFolderSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMultipleDownloadSettingsCmd;
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
import org.kablink.teaming.gwt.client.util.PublicLinkInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.CopyFiltersDlg;
import org.kablink.teaming.gwt.client.widgets.CopyFiltersDlg.CopyFiltersDlgClient;
import org.kablink.teaming.gwt.client.widgets.CopyPublicLinkDlg;
import org.kablink.teaming.gwt.client.widgets.CopyPublicLinkDlg.CopyPublicLinkDlgClient;
import org.kablink.teaming.gwt.client.widgets.DeleteSelectedUsersDlg;
import org.kablink.teaming.gwt.client.widgets.DeleteSelectedUsersDlg.DeleteSelectedUsersDlgClient;
import org.kablink.teaming.gwt.client.widgets.DeleteSelectionsDlg;
import org.kablink.teaming.gwt.client.widgets.DeleteSelectionsDlg.DeleteSelectionsDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditPublicLinkDlg;
import org.kablink.teaming.gwt.client.widgets.EditPublicLinkDlg.EditPublicLinkDlgClient;
import org.kablink.teaming.gwt.client.widgets.EmailPublicLinkDlg;
import org.kablink.teaming.gwt.client.widgets.EmailPublicLinkDlg.EmailPublicLinkDlgClient;
import org.kablink.teaming.gwt.client.widgets.MailToMultiplePublicLinksSelectDlg;
import org.kablink.teaming.gwt.client.widgets.MailToMultiplePublicLinksSelectDlg.MailToMultiplePublicLinksSelectCallback;
import org.kablink.teaming.gwt.client.widgets.MailToMultiplePublicLinksSelectDlg.MailToMultiplePublicLinksSelectDlgClient;
import org.kablink.teaming.gwt.client.widgets.SelectCSVDelimiterDlg;
import org.kablink.teaming.gwt.client.widgets.SelectCSVDelimiterDlg.CSVDelimiterCallback;
import org.kablink.teaming.gwt.client.widgets.SelectCSVDelimiterDlg.SelectCSVDelimiterDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2.ShareThisDlg2Client;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.Window.Navigator;

/**
 * Helper methods for binder views.
 *
 * @author drfoster@novell.com
 */
public class BinderViewsHelper {
	private static AddFilesDlg							m_addFilesAppletDlg;					// An instance of the add files (via an applet) dialog.
	private static AddFilesHtml5Popup					m_addFilesHtml5Popup;					// An instance of the add files (via HTML5)     popup.
	private static CopyFiltersDlg						m_copyFiltersDlg;						// An instance of a copy filters dialog. 
	private static ChangeEntryTypesDlg					m_cetDlg;								// An instance of a change entry types dialog. 
	private static CopyMoveEntriesDlg					m_cmeDlg;								// An instance of a copy/move entries dialog.
	private static CopyPublicLinkDlg					m_copyPublicLinkDlg;					// An instance of a copy public link dialog.
	private static DeleteSelectedUsersDlg				m_dsuDlg;								// An instance of a delete selected users dialog.
	private static DeleteSelectionsDlg					m_dsDlg;								// An instance of a delete selections dialog.
	private static EditPublicLinkDlg					m_editPublicLinkDlg;					// An instance of a edit public link dialog.
	private static EmailNotificationDlg					m_enDlg;								// An instance of an email notification dialog used to subscribe to subscribe to the entries in a List<EntityId>. 
	private static EmailPublicLinkDlg					m_emailPublicLinkDlg;					// An instance of an email public link dialog.
	private static GwtTeamingMessages					m_messages = GwtTeaming.getMessages();	// Access to the GWT localized strings.
	private static ShareThisDlg2						m_shareDlg;								// An instance of a share this dialog.
	private static MailToMultiplePublicLinksSelectDlg	m_mailPLSelectDlg;						// An instance of the mail to multiple public links select dialog.
	private static SelectCSVDelimiterDlg				m_selectCSVDelimiterDlg;				// An instance of the select CSV delimiter dialog.
	private static WhoHasAccessDlg						m_whaDlg;								// An instance of a who has access dialog used to view who has access to an entity. 

	// The following is the ID/name of the <IFRAME> used to run the
	// edit-in-place editor via an applet.
	private final static String EDIT_IN_PLACE_DIV_ID	= "ss_div_fileopen_GWT";
	private final static String EDIT_IN_PLACE_FRAME_ID	= "ss_iframe_fileopen_GWT";
	
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
	 * Creates and returns a flow panel create the IFRAME, ... required
	 * to run the edit-in-place editor on a file using the applet.
	 * 
	 * @return
	 */
	public static VibeFlowPanel createEditInPlaceFrame() {
		// Create the outer <DIV>...
		VibeFlowPanel reply = new VibeFlowPanel();
		reply.addStyleName("vibe-editInPlaceOuter");
		reply.getElement().setId(               EDIT_IN_PLACE_DIV_ID);
		reply.getElement().setAttribute("name", EDIT_IN_PLACE_DIV_ID);
		
		// ...create the inner <DIV>...
		VibeFlowPanel inner = new VibeFlowPanel();
		inner.addStyleName("vibe-editInPlaceInner");
		inner.getElement().setAttribute("align", "right");

		// ...create the <IFRAME>...
		int eipFrameSize = (GwtClientHelper.jsIsIE() ? 1 : 0); 
		NamedFrame eipFrame = new NamedFrame(EDIT_IN_PLACE_FRAME_ID);
		eipFrame.getElement().setId(         EDIT_IN_PLACE_FRAME_ID);
		eipFrame.setPixelSize(eipFrameSize, eipFrameSize);
		eipFrame.setUrl(GwtClientHelper.getRequestInfo().getJSPath() + "forum/null.html");
		eipFrame.setTitle(GwtClientHelper.isLicenseFilr() ? m_messages.novellFilr() : m_messages.novellTeaming());

		// ...tie it all together...
		inner.add(eipFrame);
		reply.add(inner   );
		
		// ...and return the containing panel.
		return reply;
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
	 * Downloads a folder as a CSV file.
	 *
	 * @param downloadForm
	 * @param folderId
	 * @param reloadEvent
	 */
	public static void downloadFolderAsCSVFile(final FormPanel downloadForm, final Long folderId, final VibeEventBase<?> reloadEvent) {
		// Have we created a Select CSV Delimiter dialog yet?
		if (null == m_selectCSVDelimiterDlg) {
			// No!  Create one now...
			SelectCSVDelimiterDlg.createAsync(new SelectCSVDelimiterDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(SelectCSVDelimiterDlg csvDlg) {
					// ...and run it and do the download.
					m_selectCSVDelimiterDlg = csvDlg;
					selectCSVDelimiterAndDownloadAsync(downloadForm, folderId, reloadEvent);
				}
			});
		}
		
		else {
			// Yes, we already have a Select CSV Delimiter dialog!  Run
			// it and do the download.
			selectCSVDelimiterAndDownloadAsync(downloadForm, folderId, reloadEvent);
		}
	}
	
	public static void downloadFolderAsCSVFile(FormPanel downloadForm, Long folderId) {
		// Always use the initial form of the method.
		downloadFolderAsCSVFile(downloadForm, folderId, null);
	}

	/*
	 * Asynchronously downloads the folder as a CSV file using the
	 * given delimiter.
	 */
	private static void downloadFolderAsCSVFileImplAsync(final FormPanel downloadForm, final Long folderId, final VibeEventBase<?> reloadEvent, final String csvDelim) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				downloadFolderAsCSVFileImplNow(downloadForm, folderId, reloadEvent, csvDelim);
			}
		});
	}
	
	/*
	 * Synchronously downloads the folder as a CSV file using the
	 * given delimiter.
	 */
	private static void downloadFolderAsCSVFileImplNow(final FormPanel downloadForm, final Long folderId, final VibeEventBase<?> reloadEvent, final String csvDelim) {
		// Show a busy spinner while we build the information for
		// downloading the folder.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request for the download folder as a CSV file URL.
		GetDownloadFolderAsCSVFileUrlCmd cmd = new GetDownloadFolderAsCSVFileUrlCmd(folderId, csvDelim);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetDownloadFolderAsCSVFileUrl());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// If we got any errors creating the URL to download
				// the folder as a CSV file...
				busy.hide();
				DownloadFolderAsCSVFileUrlRpcResponseData	downloadFolderAsCSVFileInfo = ((DownloadFolderAsCSVFileUrlRpcResponseData) response.getResponseData());
				ErrorListRpcResponseData					errorList                   = downloadFolderAsCSVFileInfo.getErrors();
				List<ErrorInfo>								errors                      = errorList.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				boolean hasErrors = (0 < count);
				if (hasErrors) {
					// ...tell the user.
					GwtClientHelper.displayMultipleErrors(m_messages.downloadFolderAsCSVFileUrlError(), errors);
					hasErrors = (0 < errorList.getErrorCount());	// The error list has errors and warnings.  Did we find any actual errors?
				}

				// Did we get any actual errors (not just warnings)?
				if (!hasErrors) {
					// No!  If we get the URL to download the folder as
					// a CSV file...
					String downloadFolderAsCSVFileUrl = downloadFolderAsCSVFileInfo.getUrl();
					if (GwtClientHelper.hasString(downloadFolderAsCSVFileUrl)) {
						// ...start it downloading...
						downloadForm.setAction(downloadFolderAsCSVFileUrl);
						downloadForm.submit();
					}
				}
				
				// ...and if we have a reload event...
				if (null != reloadEvent) {
					// ...fire it.
					GwtTeaming.fireEventAsync(reloadEvent);
				}
			}
		});
	}
	
	/**
	 * Invokes the appropriate UI to edit the public link of the
	 * entities based on a List<EntityId> of the entries.
	 *
	 * @param entityIds
	 */
	public static void editEntitiesPublicLink(final List<EntityId> entityIds) {
		// If we weren't given any entity IDs to be shared...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}

		// Have we created a edit public link dialog yet?
		if (null == m_editPublicLinkDlg) {
			// No!  Create one now...
			EditPublicLinkDlg.createAsync(new EditPublicLinkDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(EditPublicLinkDlg cplDlg) {
					// ...and show it with the given entity IDs.
					m_editPublicLinkDlg = cplDlg;
					showEditPublicLinkDlgAsync(entityIds);
				}
			});
		}
		
		else {
			// Yes, we've already create a edit public link dialog!
			// Simply show it with the given entry IDs.
			showEditPublicLinkDlgAsync(entityIds);
		}
	}
	
	/**
	 * Invokes the appropriate UI to edit the public link of an entity
	 * based on an EntityId.
	 *
	 * @param entityId
	 */
	public static void editEntityPublicLink(EntityId entityId) {
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add(entityId);
		editEntitiesPublicLink(entityIds);
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
	 * Forces the selected files to be unlocked.
	 *
	 * @param entityIds
	 * @param reloadEvent
	 */
	public static void forceUnlockSelectedFiles(List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		// If we weren't given any entity IDs to be unlocked...
		if (!(GwtClientHelper.hasItems(entityIds))) {
			// ...bail.
			return;
		}
		
		// Show a busy spinner while we unlock the files.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send a request to unlock the files.
		ForceFilesUnlockCmd cmd = new ForceFilesUnlockCmd(entityIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ForceFilesUnlock());
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
	 * Invokes the copy filters dialog on the given folder.
	 * 
	 * @param folderInfo
	 */
	public static void invokeCopyFiltersDlg(final BinderInfo folderInfo) {
		// Have we created a copy filters dialog yet?
		if (null == m_copyFiltersDlg) {
			// No!  Create one now...
			CopyFiltersDlg.createAsync(new CopyFiltersDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(CopyFiltersDlg cfDlg) {
					// ...and show it with the given folder ID.
					m_copyFiltersDlg = cfDlg;
					showCopyFiltersDlgAsync(folderInfo);
				}
			});
		}
		
		else {
			// Yes, we've already create a copy filters dialog!  Simply
			// show it with the given folder ID.
			showCopyFiltersDlgAsync(folderInfo);
		}
	}
	
	/**
	 * Invokes the 'Add Files' interface.
	 * 
	 * For browsers that support it, that will be the HTML5 file upload
	 * facility.  For all others, that will be the Java Applet.
	 * 
	 * For browsers that support HTML5, the applet can still be used by
	 * holding down the control or meta key when this event is fired.
	 * Typically, the user would hold it down while click 'Add Files'
	 * on the entry menu.
	 * 
	 * @param folderInfo
	 * @param showRelativeWidget
	 */
	public static void invokeDropBox(final BinderInfo folderInfo, final UIObject showRelativeWidget) {
		// Are we trying to upload a customized email template with a
		// browser that doesn't support HTML5?
		boolean emailTemplates       = folderInfo.isBinderEmailTemplates();
		boolean browserSupportsHtml5 = GwtClientHelper.jsBrowserSupportsHtml5FileAPIs();
		if (emailTemplates && (!(browserSupportsHtml5))) {
			// Yes!  We should never have gotten here!  Tell the user
			// about the problem and bail.
			GwtClientHelper.deferredAlert(m_messages.binderViewsHelper_internalErrorEmailTemplatesWithoutHtml5());
			return;
		}
		
		// Are we running in a browser that support file uploads using
		// HTML5 and is the user not overriding that by keystroke?
		if (browserSupportsHtml5 && (emailTemplates || (!(keyForcedAppletUpload())))) {
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
	 * Asynchronously handles editing the folder entry based on an
	 * InvokeEditInPlaceEvent.
	 * 
	 * @param event
	 */
	public static void invokeEditInPlace(final InvokeEditInPlaceEvent event) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				invokeEditInPlaceNow(event);
			}
		});
	}
	
	/*
	 * Synchronously handles editing the folder entry.
	 */
	private static void invokeEditInPlaceNow(InvokeEditInPlaceEvent event) {
		// How are we launching the edit-in-place editor?
		String   et  = event.getEditorType(); if (null == et) et = "";
		EntityId eid = event.getEntityid();
		if ("applet".equals(et)) {
			// Via an applet!  Launch it.
			GwtClientHelper.jsEditInPlace_Applet(
				eid.getBinderId(),
				eid.getEntityId(),
				"_GWT",
				event.getOperatingSystem(),
				event.getAttachmentId());
		}
		
		else if ("webdav".equals(et)) {
			// Via a WebDAV URL!  Launch it.
			GwtClientHelper.jsEditInPlace_WebDAV(event.getAttachmentUrl());
		}
		
		else {
			// Unknown!  Tell the user about the problem.
			GwtClientHelper.deferredAlert(m_messages.eventHandling_UnknownEditInPlaceEditorType(et));
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
			ShareThisDlg2.createDlg(
								false,
								true,
								0,
								0,
								null,
								null,
								ShareThisDlg2.ShareThisDlgMode.MANAGE_SELECTED,
								new ShareThisDlg2Client() {
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

	/*
	 * Returns true if a key is pressed that forces us to use the
	 * applet to upload files and false otherwise.
	 */
	private static boolean keyForcedAppletUpload() {
		// If the browser can't run Java applets...
		if (!(GwtClientHelper.browserSupportsNPAPI())) {
			// ...we never let a keystroke force it to be invoked.
			return false;
		}
		
		// Is the control key down?
		boolean reply = GwtClientHelper.isControlKeyDown();
		if (!reply) {
			// No!  If this is a Mac, is the Apple/command key down?
			String platform = Navigator.getPlatform();
			if (null == platform)
			     platform = "";
			else platform = platform.toLowerCase();
			boolean isMac = platform.contains("mac");
			reply = (isMac && GwtClientHelper.isMetaKeyDown());
		}
		
		// If we get here, reply contains true if there's a key pressed
		// that forces us to use the applet and false otherwise.
		// Return it.
		return reply;
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
	 * Mails the public link of a folder entry using a 'mailto://...'
	 * URL.
	 * 
	 * @param entityId
	 */
	public static void mailToPublicLink(final EntityId entityId) {
		// ...and request the links.
		GetMailToPublicLinksCmd cmd = new GetMailToPublicLinksCmd(entityId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetMailToPublicLinks());
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the link information from the response.
				MailToPublicLinksRpcResponseData plData = ((MailToPublicLinksRpcResponseData) response.getResponseData());
				
				// Did we get any messages (errors, ...) from the
				// request?
				if (plData.hasError()) {
					// Yes!  Display them.
					GwtClientHelper.deferredAlert(
						m_messages.binderViewsHelper_failureMailToPublicLink(
							plData.getError()));
				}

				// How many links to we have for the entity?
				List<PublicLinkInfo> plList = plData.getMailToPublicLinks();
				switch ((null == plList) ? 0 : plList.size()) {
				case 0:
					// None!  Nothing to do.
					break;
					
				case 1:
					// One!  Simply mail it.
					mailToPublicLinkAsync(plData.getSubject(), plList.get(0));
					break;
					
				default:
					// More than one!  We need to ask the user which
					// one to mail.
					mailToMultiplePublicLinksAsync(entityId, plData.getSubject(), plList);
					break;
				}
			}
		});
	}

	/*
	 * Asynchronously runs the mail to multiple public links select
	 * dialog for the user to select which public link is to be mailed.
	 */
	private static void mailToMultiplePublicLinksAsync(final EntityId entityId, final String subject, final List<PublicLinkInfo> plInfoList) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mailToMultiplePublicLinksNow(entityId, subject, plInfoList);
			}
		});
	}
	
	/*
	 * Synchronously runs the mail to multiple public links select
	 * dialog for the user to select which public link is to be mailed.
	 */
	private static void mailToMultiplePublicLinksNow(final EntityId entityId, final String subject, final List<PublicLinkInfo> plInfoList) {
		if (null == m_mailPLSelectDlg) {
			MailToMultiplePublicLinksSelectDlg.createAsync(new MailToMultiplePublicLinksSelectDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(MailToMultiplePublicLinksSelectDlg mtmplsDlg) {
					m_mailPLSelectDlg = mtmplsDlg;
					mailToMultiplePublicLinksImplAsync(entityId, subject, plInfoList);
				}
			});
		}
		
		else {
			mailToMultiplePublicLinksImplNow(entityId, subject, plInfoList);
		}
	}

	/*
	 * Asynchronously runs the mail to multiple public links select
	 * dialog for the user to select which public link is to be mailed.
	 */
	private static void mailToMultiplePublicLinksImplAsync(final EntityId entityId, final String subject, final List<PublicLinkInfo> plInfoList) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mailToMultiplePublicLinksImplNow(entityId, subject, plInfoList);
			}
		});
	}
	
	/*
	 * Synchronously runs the mail to multiple public links select
	 * dialog for the user to select which public link is to be mailed.
	 */
	private static void mailToMultiplePublicLinksImplNow(final EntityId entityId, final String subject, final List<PublicLinkInfo> plInfoList) {
		MailToMultiplePublicLinksSelectDlg.initAndShow(
			m_mailPLSelectDlg,
			entityId,
			plInfoList,
			new MailToMultiplePublicLinksSelectCallback() {
				@Override
				public void onCancel() {
					// If the dialog gets cancelled, we don't do
					// anything.
				}

				@Override
				public void onSelect(PublicLinkInfo plInfo) {
					// Mail the public link the user selected.
					mailToPublicLinkAsync(subject, plInfo);
				}
			});
	}

	/*
	 * Asynchronously mails the public link using a 'mailto://...' URL.
	 */
	private static void mailToPublicLinkAsync(final String subject, final PublicLinkInfo plLink) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mailToPublicLinkNow(subject, plLink);
			}
		});
	}
	
	/*
	 * Synchronously mails the public link using a 'mailto://...' URL.
	 */
	private static void mailToPublicLinkNow(final String subject, final PublicLinkInfo plLink) {
		// Construct the body text using the public link URLs...
		StringBuilder body    = new StringBuilder(); 
		String        url     = plLink.getViewUrl();
		boolean       hasView = GwtClientHelper.hasString(url);
		if (hasView) {
			body.append(m_messages.binderViewsHelper_view());
			body.append(" ");
			body.append(url);
		}
		url = plLink.getDownloadUrl();
		if (GwtClientHelper.hasString(url)) {
			if (hasView) {
				body.append("\r\n\r\n");
			}
			body.append(m_messages.binderViewsHelper_download());
			body.append(" ");
			body.append(url);
		}

		// ...and send the email.
		sendEmail(subject, body.toString());
	}
	
	/**
	 * Marks the contents of a folder as having been read.
	 *
	 * @param folderId
	 * @param reloadEvent
	 */
	public static void markFolderContentsRead(Long folderId, final VibeEventBase<?> reloadEvent) {
		// Show a busy spinner while we mark the contents of the folder
		// as having been read.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to mark the folder contents as having been
		// read.
		MarkFolderContentsReadCmd cmd = new MarkFolderContentsReadCmd(folderId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_MarkFolderContentsRead());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Hide the busy spinner...
				busy.hide();
				
				// ...and fire a reload event.
				if (null == reloadEvent)
				     FullUIReloadEvent.fireOneAsync();
				else GwtTeaming.fireEventAsync(reloadEvent);
			}
		});
	}
	
	public static void markFolderContentsRead(Long folderId) {
		// Always use the initial form of the method.
		markFolderContentsRead(folderId, null);
	}
	
	/**
	 * Marks the contents of a folder as having been unread.
	 *
	 * @param folderId
	 * @param reloadEvent
	 */
	public static void markFolderContentsUnread(Long folderId, final VibeEventBase<?> reloadEvent) {
		// Show a busy spinner while we mark the contents of the folder
		// as having been unread.
		final SpinnerPopup busy = new SpinnerPopup();
		busy.center();

		// Send the request to mark the folder contents as having been
		// unread.
		MarkFolderContentsUnreadCmd cmd = new MarkFolderContentsUnreadCmd(folderId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				busy.hide();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_MarkFolderContentsUnread());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Hide the busy spinner...
				busy.hide();
				
				// ...and fire a reload event.
				if (null == reloadEvent)
				     FullUIReloadEvent.fireOneAsync();
				else GwtTeaming.fireEventAsync(reloadEvent);
			}
		});
	}
	
	public static void markFolderContentsUnread(Long folderId) {
		// Always use the initial form of the method.
		markFolderContentsUnread(folderId, null);
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

	/*
	 * Asynchronously runs the select a CSV delimiter dialog and does
	 * the download.
	 */
	private static void selectCSVDelimiterAndDownloadAsync(final FormPanel downloadForm, final Long folderId, final VibeEventBase<?> reloadEvent) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				selectCSVDelimiterAndDownloadNow(downloadForm, folderId, reloadEvent);
			}
		});
	}
	
	/*
	 * Synchronously runs the select a CSV delimiter dialog and does
	 * the download.
	 */
	private static void selectCSVDelimiterAndDownloadNow(final FormPanel downloadForm, final Long folderId, final VibeEventBase<?> reloadEvent) {
		SelectCSVDelimiterDlg.initAndShow(m_selectCSVDelimiterDlg, new CSVDelimiterCallback() {
			@Override
			public void onCancel() {
				// If the user cancels the dialog, we simply do
				// nothing.
			}
			
			@Override
			public void onSelect(String csvDelim) {
				downloadFolderAsCSVFileImplAsync(downloadForm, folderId, reloadEvent, csvDelim);
			}
		});
	}
	
	/*
	 * Sends an email with the given subject and body.
	 * 
	 * See the following for the algorithm implemented:
	 *		http://shadow2531.com/opera/testcases/mailto/modern_mailto_uri_scheme.html
	 */
	private static void sendEmail(String subjectIn, String bodyIn) {
	    String subject = GwtClientHelper.jsUTF8PercentEncodeWithNewlinesStripped(  subjectIn);
	    String body    = GwtClientHelper.jsUTF8PercentEncodeWithNormalizedNewlines(bodyIn   );
	    GwtClientHelper.jsWindowOpen("mailto:?subject=" + subject + "&body=" + body);
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
			ShareThisDlg2.createDlg(
								new Boolean( false ),
								new Boolean( true ),
								0,
								0,
								null,
								null,
								ShareThisDlg2.ShareThisDlgMode.NORMAL,
								new ShareThisDlg2Client() {
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
	 * Asynchronously shows the copy filters dialog.
	 */
	private static void showCopyFiltersDlgAsync(final BinderInfo folderInfo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showCopyFiltersDlgNow(folderInfo);
			}
		});
	}
	
	/*
	 * Synchronously shows the copy filters dialog.
	 */
	private static void showCopyFiltersDlgNow(BinderInfo folderInfo) {
		CopyFiltersDlg.initAndShow(m_copyFiltersDlg, folderInfo);
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
			m_messages.copyPublicLinkTheseItems(),
			String.valueOf(entityIds.size()));
		CopyPublicLinkDlg.initAndShow(m_copyPublicLinkDlg, caption, entityIds);
	}

	/*
	 * Asynchronously shows the edit public link dialog.
	 */
	private static void showEditPublicLinkDlgAsync(final List<EntityId> entityIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showEditPublicLinkDlgNow(entityIds);
			}
		});
	}
	
	/*
	 * Synchronously shows the edit public link dialog.
	 */
	private static void showEditPublicLinkDlgNow(List<EntityId> entityIds) {
		String caption = GwtClientHelper.patchMessage(
			m_messages.editPublicLinkTheseItems(),
			String.valueOf(entityIds.size()));
		EditPublicLinkDlg.initAndShow(m_editPublicLinkDlg, caption, entityIds);
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
		if (null != m_emailPublicLinkDlg) {
			String caption = GwtClientHelper.patchMessage(m_messages.emailPublicLinkTheseItems(), String.valueOf(entityIds.size()));
			m_emailPublicLinkDlg.init(caption, entityIds);
			m_emailPublicLinkDlg.show(true);
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
		String caption;

		caption = GwtClientHelper.patchMessage(m_messages.manageShares(), String.valueOf(entityIds.size()));

		// Run the async command to show the dialog
		ShareThisDlg2.initAndShow(
								m_shareDlg,
								caption,
								entityIds,
								ShareThisDlg2.ShareThisDlgMode.MANAGE_SELECTED,
								null,
								null,
								null,
								null,
								new Boolean( true ),
								null );
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
		String caption;
	
		caption = GwtClientHelper.patchMessage(m_messages.shareTheseItems(), String.valueOf(entityIds.size()));

		// Run the async command to show the dialog
		ShareThisDlg2.initAndShow(
								m_shareDlg,
								caption,
								entityIds,
								ShareThisDlg2.ShareThisDlgMode.NORMAL,
								null,
								null,
								null,
								null,
								new Boolean( true ),
								null );
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
				((requiresFiles || GwtClientHelper.isLicenseFilr())      ?
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
	 * @param downloadForm
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
				boolean hasErrors = (0 < count);
				if (hasErrors) {
					// ...tell the user.
					GwtClientHelper.displayMultipleErrors(m_messages.zipDownloadUrlError(), errors);
					hasErrors = (0 < errorList.getErrorCount());	// The error list has errors and warnings.  Did we find any actual errors?
				}
				
				// Did we get any actual errors (not just warnings)?
				if (!hasErrors) {
					// No!  If we get the URL to download the zip...
					String zipDownloadUrl = zipDownloadInfo.getUrl();
					if (GwtClientHelper.hasString(zipDownloadUrl)) {
						// ...start it downloading...
						downloadForm.setAction(zipDownloadUrl);
						downloadForm.submit();
					}
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
	 * @param downloadForm
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
				boolean hasErrors = (0 < count);
				if (hasErrors) {
					// ...tell the user.
					GwtClientHelper.displayMultipleErrors(m_messages.zipDownloadUrlError(), errors);
					hasErrors = (0 < errorList.getErrorCount());	// The error list has errors and warnings.  Did we find any actual errors?
				}

				// Did we get any actual errors (not just warnings)?
				if (!hasErrors) {
					// No!  If we get the URL to download the zip...
					String zipDownloadUrl = zipDownloadInfo.getUrl();
					if (GwtClientHelper.hasString(zipDownloadUrl)) {
						// ...start it downloading...
						downloadForm.setAction(zipDownloadUrl);
						downloadForm.submit();
					}
				}
			}
		});
	}
	
	public static void zipAndDownloadFolder(FormPanel downloadForm, Long folderId, boolean recursive) {
		// Always use the initial form of the method.
		zipAndDownloadFolder(downloadForm, folderId, recursive, null);
	}
}
