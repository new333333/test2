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

package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.SidebarReloadEvent;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TrashPurgeAllCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TrashPurgeSelectedEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TrashRestoreAllCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TrashRestoreSelectedEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntryId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Trash view.
 * 
 * @author drfoster@novell.com
 */
public class TrashView extends DataTableFolderViewBase {
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TrashView(BinderInfo binderInfo, ViewReady viewReady) {
		// Simply initialize the base class.
		super(binderInfo, viewReady, "vibe-trashDataTable");
	}
	
	/*
	 * Returns a List<String> of trash selection information.  The
	 * format of the strings duplicates that used by the JSP version
	 * of the trash.  See the serialize() method in ss_trash.js
	 * 
	 * Examples of the format used:
	 *		9337:18704:entry:folderEntry
	 *		19160:18704:binder:folder
	 */
	private List<String> buildTrashSelectionList() {
		// Allocate a List<String> to return with the trash selection information.
		List<String> reply = new ArrayList<String>();

		// Scan the rows in the data table.
		AbstractCellTable<FolderRow> dt = getDataTable();
		List<FolderRow> rows = dt.getVisibleItems();
		if (null != rows) {
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) dt.getSelectionModel());
			for (FolderRow row:  rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  We need to add trash information about it
					// the reply list. 
					EntryId rowEntryId = row.getEntryId();
					String trashData =
						 rowEntryId.getEntryId()              + ":" +
						 rowEntryId.getBinderId()             + ":" +
						(row.isBinder() ? "binder" : "entry") + ":" +
						 row.getEntityType();
					reply.add(trashData);
				}
			}
		}
		
		// If we get here, reply refers to a List<String> of the trash
		// selection data.  Return it.
		return reply;
	}
	
	/**
	 * Called from the base class to complete the construction of this
	 * trash view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Setup the appropriate styles for viewing the trash, populate
		// the view's contents and tell the base class that we're done
		// with the construction.
		getFlowPanel().addStyleName("vibe-trashFlowPanel");
		populateContent();
		super.viewReady();
	}
	
	/**
	 * Loads the TrashView split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param binderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo binderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(TrashView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				TrashView dfView = new TrashView(binderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_TrashView());
				vClient.onUnavailable();
			}
		});
	}

	/*
	 * Asynchronously forces the content to reload and optionally, the
	 * sidebar tree.
	 */
	private void reloadUIAsync(final boolean reloadSidebar) {
		ScheduledCommand doReload = new ScheduledCommand() {
			@Override
			public void execute() {
				reloadUINow(reloadSidebar);
			}
		};
		Scheduler.get().scheduleDeferred(doReload);
	}
	
	/*
	 * Synchronously forces the content to reload and optionally, the
	 * sidebar tree.
	 */
	private void reloadUINow(boolean reloadSidebar) {
		FullUIReloadEvent.fireOne();
		if (reloadSidebar) {
			SidebarReloadEvent.fireOne();
		}
	}

	/**
	 * Called from the base class to reset the content of this trash
	 * view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView() {
		// Clear any existing content from the view and repopulate it.
		resetContent();
		populateContent();
	}
	
	/**
	 * Called from the base class to resize the content of this trash
	 * view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Nothing to do.
	}
	
	/**
	 * Purges all the entries from the trash.
	 * 
	 * Overrides the DataTableFolderViewBase.trashPurgeAll()
	 * method.
	 */
	@Override
	public void trashPurgeAll() {
		// Is the user sure they want to purge everything?
		String confirm = m_messages.vibeDataTable_TrashConfirmPurgeAll();
		if (areEntriesSelected()) {
			// If they have anything selected, we need to ensure they
			// meant to do a purge all and not simply a purge of what
			// they've selected.
			confirm += "\n\n";
			confirm += m_messages.vibeDataTable_TrashConfirmPurgeAllWithSelections();
		}
		if (!(Window.confirm(confirm))) {
			// No!  Bail.
			return;
		}
		
	    // If there are any binders being purged...
		final boolean purgeBinders = areBindersInDataTable();
	    boolean purgeMirroredSources = purgeBinders;
	    if (purgeMirroredSources) {
	    	// ...ask the user about purging mirrored sources.
			purgeMirroredSources = Window.confirm(m_messages.vibeDataTable_TrashConfirmPurgeDeleteSourceOnMirroredSubFolders());
	    }

		// Perform the purge.
		Long binderId = getFolderInfo().getBinderIdAsLong();
		GwtClientHelper.executeCommand(new TrashPurgeAllCmd(binderId, purgeMirroredSources), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_TrashPurgeAll());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display any messages we get back from the server.
				StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
				String messages = responseData.getStringValue();
				if (GwtClientHelper.hasString(messages)) {
					GwtClientHelper.deferredAlert(messages);
				}
				reloadUIAsync(purgeBinders);
			}
		});
	}
	
	/**
	 * Purges the selected entries from the trash.
	 * 
	 * Overrides the DataTableFolderViewBase.trashPurgeSelectedEntries()
	 * method.
	 */
	@Override
	public void trashPurgeSelectedEntries() {
		// Is the user sure they want to purge the selected items?
		if (!(Window.confirm(m_messages.vibeDataTable_TrashConfirmPurge()))) {
			// No!  Bail.
			return;
		}
		
	    // If there are any binders being purged...
		final boolean purgeBinders = areBindersSelectedInDataTable();
		boolean purgeMirroredSources = purgeBinders;
		if (purgeMirroredSources) {
	    	// ...ask the user about purging mirrored sources.
			purgeMirroredSources = Window.confirm(m_messages.vibeDataTable_TrashConfirmPurgeDeleteSourceOnMirroredSubFolders());
		}
		
		// Perform the purge.
		List<String> trashSelectionData = buildTrashSelectionList();
		Long binderId = getFolderInfo().getBinderIdAsLong();
		GwtClientHelper.executeCommand(new TrashPurgeSelectedEntriesCmd(binderId, purgeMirroredSources, trashSelectionData), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_TrashPurgeSelectedEntries());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display any messages we get back from the server.
				StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
				String messages = responseData.getStringValue();
				if (GwtClientHelper.hasString(messages)) {
					GwtClientHelper.deferredAlert(messages);
				}
				reloadUIAsync(purgeBinders);
			}
		});
	}
	
	/**
	 * Restores all the entries in the trash. 
	 * 
	 * Overrides the DataTableFolderViewBase.trashRestoreAll()
	 * method.
	 */
	@Override
	public void trashRestoreAll() {
		// If the user has made selections...
		if (areEntriesSelected()) {
			// ...make sure they know that we'll restore everything and
			// ...not just what they selected.
			if (!(Window.confirm(m_messages.vibeDataTable_TrashConfirmRestoreAllWithSelections()))) {
				return;
			}
		}

		// Perform the restore.
		final boolean restoreBinders = areBindersInDataTable();
		Long binderId = getFolderInfo().getBinderIdAsLong();
		GwtClientHelper.executeCommand(new TrashRestoreAllCmd(binderId), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_TrashRestoreAll());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display any messages we get back from the server.
				StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
				String messages = responseData.getStringValue();
				if (GwtClientHelper.hasString(messages)) {
					GwtClientHelper.deferredAlert(messages);
				}
				reloadUIAsync(restoreBinders);
			}
		});
	}
	
	/**
	 * Restores the selected entries in the trash.
	 * 
	 * Overrides the DataTableFolderViewBase.trashRestoreSelectedEntries()
	 * method.
	 */
	@Override
	public void trashRestoreSelectedEntries() {
		// Perform the restore.
		final boolean restoreBinders = areBindersInDataTable();
		List<String> trashSelectionData = buildTrashSelectionList();
		Long binderId = getFolderInfo().getBinderIdAsLong();
		GwtClientHelper.executeCommand(new TrashRestoreSelectedEntriesCmd(binderId, trashSelectionData), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_TrashRestoreSelectedEntries());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display any messages we get back from the server.
				StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
				String messages = responseData.getStringValue();
				if (GwtClientHelper.hasString(messages)) {
					GwtClientHelper.deferredAlert(messages);
				}
				reloadUIAsync(restoreBinders);
			}
		});
	}
}
