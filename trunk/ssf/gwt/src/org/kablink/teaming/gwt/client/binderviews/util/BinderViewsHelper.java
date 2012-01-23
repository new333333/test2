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
import org.kablink.teaming.gwt.client.binderviews.ChangeEntryTypesDlg;
import org.kablink.teaming.gwt.client.binderviews.ChangeEntryTypesDlg.ChangeEntryTypesDlgClient;
import org.kablink.teaming.gwt.client.binderviews.CopyMoveEntriesDlg;
import org.kablink.teaming.gwt.client.binderviews.CopyMoveEntriesDlg.CopyMoveEntriesDlgClient;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg.EmailNotificationDlgClient;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.LockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UnlockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntryId;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Helper methods for binder views.
 *
 * @author drfoster@novell.com
 */
public class BinderViewsHelper {
	private static ChangeEntryTypesDlg	m_cetDlg;	// An instance of a change entry types dialog. 
	private static CopyMoveEntriesDlg	m_cmeDlg;	// An instance of a copy/move entries dialog.
	private static EmailNotificationDlg	m_enDlg;	// An instance of an email notification dialog used to subscribe to subscribe to the entries in a List<EntryId>. 
	
	/*
	 * Constructor method. 
	 */
	private BinderViewsHelper() {
		// Inhibits this class from being instantiated.
	}

	/**
	 * Invokes the appropriate UI to change the entry type of the
	 * entries based on a List<EntryId> of the entries.
	 *
	 * @param folderType
	 * @param entryIds
	 */
	public static void changeEntryTypes(final FolderType folderType, final List<EntryId> entryIds) {
		// If we weren't given any entry IDs to entries whose types
		// are to be changed...
		if ((null == entryIds) || entryIds.isEmpty()) {
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
							changeEntryTypesNow(entryIds);
						}
					};
					Scheduler.get().scheduleDeferred(doSubscribe);
				}
			});
		}
		
		else {
			// Yes, we've instantiated a change entry types dialog
			// already!  Simply show it.
			changeEntryTypesNow(entryIds);
		}
	}

	/*
	 * Synchronously invokes the appropriate UI to change the entry
	 * type of the entries based on a List<EntryId> of the entries.
	 */
	private static void changeEntryTypesNow(List<EntryId> entryIds) {
		ChangeEntryTypesDlg.initAndShow(m_cetDlg, entryIds);
	}
	
	/**
	 * Invokes the appropriate UI to copy the entries based on a
	 * List<EntryId> of the entries.
	 *
	 * @param folderType
	 * @param entryIds
	 */
	public static void copyEntries(final FolderType folderType, final List<EntryId> entryIds) {
		// If we weren't given any entry IDs to be copied...
		if ((null == entryIds) || entryIds.isEmpty()) {
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
					showCMEDlgNow(m_cmeDlg, true, folderType, entryIds);
				}
			});
		}
		
		else {
			// Yes, we've created a copy/move entries dialog already!
			// Run it to copy.
			showCMEDlgNow(m_cmeDlg, true, folderType, entryIds);
		}
	}

	/**
	 * Locks the entries based on a List<EntryId> of their entry IDs.
	 *
	 * @param folderType
	 * @param entryIds
	 */
	public static void lockEntries(final FolderType folderType, final List<EntryId> entryIds) {
		// If we weren't given any entry IDs to be locked...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Send a request to lock the entries.
		LockEntriesCmd cmd = new LockEntriesCmd(entryIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_LockEntries());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get locked?
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> errors = responseData.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// No!  Tell the user about the problem.
					GwtClientHelper.displayMultipleErrors(GwtTeaming.getMessages().lockEntriesError(), errors);
				}

				// If anything was locked...
				if (count != entryIds.size()) {
					// ...force the content to refresh just in case its
					// ...got something displayed that depends on
					// ...locks.
					FullUIReloadEvent.fireOne();
				}
			}
		});
	}

	/**
	 * Marks the entries read based on a List<Long> of their entry IDs.
	 *
	 * @param entryIds
	 */
	public static void markEntriesRead(List<Long> entryIds) {
		// If we weren't given any entry IDs to be marked read...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}
		
		// Send a request to mark the entries read.
		SetSeenCmd cmd = new SetSeenCmd(entryIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SetSeen());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Simply force the content to refresh just in case its
				// got something displayed that depends based on an
				// entry's read/unread state.
				FullUIReloadEvent.fireOne();
			}
		});
	}

	/**
	 * Invokes the appropriate UI to move the entries based on a
	 * List<EntryId> of the entries.
	 *
	 * @param folderType
	 * @param entryIds
	 */
	public static void moveEntries(final FolderType folderType, final List<EntryId> entryIds) {
		// If we weren't given any entry IDs to be moved...
		if ((null == entryIds) || entryIds.isEmpty()) {
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
					showCMEDlgNow(m_cmeDlg, false, folderType, entryIds);
				}
			});
		}
		
		else {
			// Yes, we've created a copy/move entries dialog already!
			// Run it to move.
			showCMEDlgNow(m_cmeDlg, false, folderType, entryIds);
		}
	}
	
	/**
	 * Invokes the appropriate UI to share the entries based on a
	 * List<EntryId> of the entries.
	 *
	 * @param folderType
	 * @param entryIds
	 */
	public static void shareEntries(final FolderType folderType, final List<EntryId> entryIds) {
		// If we weren't given any entry IDs to be shared...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}
		
//!		...this needs to be implemented...
		Window.alert("BinderViewsHelper.shareEntries():  ...this needs to be implemented...");
	}

	/*
	 * Initializes and shows and instance of the copy/move entries
	 * dialog.
	 */
	private static void showCMEDlgNow(final CopyMoveEntriesDlg cmeDlg, final boolean invokeToCopy, final FolderType folderType, final List<EntryId> entryIds) {
		CopyMoveEntriesDlg.initAndShow(
			cmeDlg,			// The dialog to show.
			invokeToCopy,	// true -> Run it do a copy.  false -> Run it to do a move.
			folderType,		// The type of folder that we're dealing with.
			entryIds);		// The List<EntryId> to be copied/moved.
	}

	/**
	 * Invokes the appropriate UI to subscribe to the entries based on
	 * a List<EntryId> of the entries.
	 *
	 * @param folderType
	 * @param entryIds
	 */
	public static void subscribeToEntries(final FolderType folderType, final List<EntryId> entryIds) {
		// If we weren't given any entry IDs to be subscribed to...
		if ((null == entryIds) || entryIds.isEmpty()) {
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
							subscribeToEntriesNow(entryIds);
						}
					};
					Scheduler.get().scheduleDeferred(doSubscribe);
				}
			});
		}
		
		else {
			// Yes, we've instantiated an email notification dialog
			// already!  Simply show it.
			subscribeToEntriesNow(entryIds);
		}
	}

	/*
	 * Synchronously invokes the appropriate UI to subscribe to the
	 * entries based on a List<EntryId> of the entries.
	 */
	private static void subscribeToEntriesNow(List<EntryId> entryIds) {
		EmailNotificationDlg.initAndShow(m_enDlg, entryIds);
	}
	
	/**
	 * Unlocks the entries based on a List<EntryId> of the entries.
	 *
	 * @param folderType
	 * @param entryIds
	 */
	public static void unlockEntries(final FolderType folderType, final List<EntryId> entryIds) {
		// If we weren't given any entry IDs to be unlocked...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Send a request to unlock the entries.
		UnlockEntriesCmd cmd = new UnlockEntriesCmd(entryIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_UnlockEntries());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get unlocked?
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> errors = responseData.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// No!  Tell the user about the problem.
					GwtClientHelper.displayMultipleErrors(GwtTeaming.getMessages().unlockEntriesError(), errors);
				}

				// If anything was unlocked...
				if (count != entryIds.size()) {
					// ...force the content to refresh just in case its
					// ...got something displayed that depends on
					// ...locks.
					FullUIReloadEvent.fireOne();
				}
			}
		});
	}
}
