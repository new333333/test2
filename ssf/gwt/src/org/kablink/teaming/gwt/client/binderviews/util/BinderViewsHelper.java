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
import org.kablink.teaming.gwt.client.binderviews.MoveEntriesDlg;
import org.kablink.teaming.gwt.client.binderviews.MoveEntriesDlg.MoveEntriesDlgClient;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.LockEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Helper methods for binder views.
 *
 * @author drfoster@novell.com
 */
public class BinderViewsHelper {
	private static MoveEntriesDlg m_moveEntriesDlg;
	
	/*
	 * Constructor method. 
	 */
	private BinderViewsHelper() {
		// Inhibits this class from being instantiated.
	}

	/**
	 * Invokes the appropriate UI to change the entry type of the
	 * entries based on a List<Long> of their entry IDs.
	 *
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void changeEntryTypes(Long folderId, FolderType folderType, List<Long> entryIds) {
		// If we weren't given any entry IDs to entries whose types
		// are to be changed...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}
		
//!		...this needs to be implemented...
		Window.alert("BinderViewsHelper.changeEntryTypes():  ...this needs to be implemented...");
	}

	/**
	 * Invokes the appropriate UI to copy the entries based on a
	 * List<Long> of their entry IDs.
	 *
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void copyEntries(Long folderId, FolderType folderType, List<Long> entryIds) {
		// If we weren't given any entry IDs to be copied...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}
		
//!		...this needs to be implemented...
		Window.alert("BinderViewsHelper.copyEntries():  ...this needs to be implemented...");
	}

	/*
	 * Displays a message to the user regarding possibly multiple
	 * errors. 
	 */
	private static void displayMultipleErrors(String baseError, List<String> multiErrors) {
		StringBuffer msg = new StringBuffer(baseError);
		for (String error:  multiErrors) {
			msg.append("\n\t");
			msg.append(error);
		}
		GwtClientHelper.deferredAlert(msg.toString());
	}

	/**
	 * Invokes the appropriate UI to lock the entries based on a
	 * List<Long> of their entry IDs.
	 *
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void lockEntries(final Long folderId, final FolderType folderType, final List<Long> entryIds) {
		// If we weren't given any entry IDs to be locked...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Send a request to lock the entries.
		LockEntriesCmd cmd = new LockEntriesCmd(folderId, entryIds);
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
					displayMultipleErrors(GwtTeaming.getMessages().lockEntriesError(), errors);
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
	 * Invokes the appropriate UI to mark the entries read based on a
	 * List<Long> of their entry IDs.
	 *
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void markEntriesRead(Long folderId, FolderType folderType, List<Long> entryIds) {
		// If we weren't given any entry IDs to be marked read...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}
		
//!		...this needs to be implemented...
		Window.alert("BinderViewsHelper.markEntriesRead():  ...this needs to be implemented...");
	}

	/**
	 * Invokes the appropriate UI to move the entries based on a
	 * List<Long> of their entry IDs.
	 *
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void moveEntries(final Long folderId, final FolderType folderType, final List<Long> entryIds) {
		// If we weren't given any entry IDs to be moved...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}

		// Have we created a move entries dialog yet?
		if (null == m_moveEntriesDlg) {
			// No!  Create one now...
			MoveEntriesDlg.createAsync(new MoveEntriesDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(MoveEntriesDlg moveEntriesDlg) {
					// ...and run it with the parameters.
					m_moveEntriesDlg = moveEntriesDlg;
					moveEntriesNow(folderId, folderType, entryIds);
				}
			});
		}
		
		else {
			// Yes, we've created a move entries dialog already!  Run
			// it with the parameters.
			moveEntriesNow(folderId, folderType, entryIds);
		}
	}
	
	/*
	 * Invokes the appropriate UI to move the entries based on a
	 * List<Long> of their entry IDs.
	 */
	private static void moveEntriesNow(final Long folderId, final FolderType folderType, final List<Long> entryIds) {
		MoveEntriesDlg.initAndShow(
			m_moveEntriesDlg,
			folderId,
			folderType,
			entryIds);
	}

	/**
	 * Invokes the appropriate UI to share the entries based on a
	 * List<Long> of their entry IDs.
	 *
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void shareEntries(Long folderId, FolderType folderType, List<Long> entryIds) {
		// If we weren't given any entry IDs to be shared...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}
		
//!		...this needs to be implemented...
		Window.alert("BinderViewsHelper.shareEntries():  ...this needs to be implemented...");
	}

	/**
	 * Invokes the appropriate UI to subscribe to the entries based on
	 * a List<Long> of their entry IDs.
	 *
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void subscribeToEntries(Long folderId, FolderType folderType, List<Long> entryIds) {
		// If we weren't given any entry IDs to be subscribed to...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}
		
//!		...this needs to be implemented...
		Window.alert("BinderViewsHelper.subscribeToEntries():  ...this needs to be implemented...");
	}

	/**
	 * Invokes the appropriate UI to unlock the entries based on a
	 * List<Long> of their entry IDs.
	 *
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void unlockEntries(Long folderId, FolderType folderType, List<Long> entryIds) {
		// If we weren't given any entry IDs to be unlocked...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...bail.
			return;
		}
		
//!		...this needs to be implemented...
		Window.alert("BinderViewsHelper.unlockEntries():  ...this needs to be implemented...");
	}
}
