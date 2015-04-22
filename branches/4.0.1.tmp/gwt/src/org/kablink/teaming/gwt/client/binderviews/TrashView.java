/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import java.util.Map;

import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.RefreshSidebarTreeEvent;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TrashPurgeAllCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TrashPurgeSelectedEntitiesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TrashRestoreAllCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TrashRestoreSelectedEntitiesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.cellview.client.AbstractCellTable;
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
		// Initialize the super class.
		super(binderInfo, viewReady, "vibe-trashDataTable");
	}
	
	/**
	 * Resets the columns as appropriate for the trash view.
	 * 
	 * Unless otherwise specified the widths default to be a percentage
	 * value.
	 * 
	 * Overrides the DataTableFolderViewBase.adjustFixedColumnWidths() method.
	 * 
	 * @param columnWidths
	 */
	@Override
	protected void adjustFixedColumnWidths(Map<String, ColumnWidth> columnWidths) {
		columnWidths.put(FolderColumn.COLUMN_AUTHOR, new ColumnWidth(20));
		columnWidths.put(FolderColumn.COLUMN_TITLE,  new ColumnWidth(27));
	}

	/**
	 * Returns true if the entry viewer should include next/previous
	 * buttons and false otherwise. 
	 *
	 * Overrides the FolderViewBase.allowNextPrevOnEntryView() method.
	 * 
	 * @return
	 */
	@Override
	protected boolean allowNextPrevOnEntryView() {
		// By default, the trash doesn't allow next/previous.
		return false;
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
					EntityId rowEntityId = row.getEntityId();
					String trashData =
						 rowEntityId.getEntityId()                    + ":" +
						 rowEntityId.getBinderId()                    + ":" +
						(rowEntityId.isBinder() ? "binder" : "entry") + ":" +
						 rowEntityId.getEntityType();
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
		viewReady();
		populateContent();
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
				GwtClientHelper.deferredAlert(m_messages.codeSplitFailure_TrashView());
				vClient.onUnavailable();
			}
		});
	}

	/**
	 * Returns true for panels that are to be included and false
	 * otherwise.
	 * 
	 * Overrides the FolderViewBase.includePanel() method.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	@Override
	protected boolean includePanel(FolderPanels folderPanel) {
		boolean reply;

		// In the trash view, we don't show the accessories,
		// description or filter panels beyond the default.
		switch (folderPanel) {
		case ACCESSORIES:
		case DESCRIPTION:
		case BINDER_OWNER_AVATAR:
		case FILTER:  reply =  false;                                                                                                                           break;
		case FOOTER:  reply = (super.includePanel(folderPanel) && (!(getFolderInfo().isBinderProfilesRootWS())) && (!(getFolderInfo().isBinderTeamsRootWS()))); break;
		default:      reply =  super.includePanel(folderPanel);                                                                                                 break;
		}
		
		return reply;
	}

	/*
	 * Asynchronously forces the content to reload and optionally, the
	 * sidebar tree.
	 */
	private void reloadUIAsync(final boolean reloadSidebar) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				reloadUINow(reloadSidebar);
			}
		});
	}
	
	/*
	 * Synchronously forces the content to reload and optionally, the
	 * sidebar tree.
	 */
	private void reloadUINow(boolean reloadSidebar) {
		FullUIReloadEvent.fireOne();
		if (reloadSidebar) {
			RefreshSidebarTreeEvent.fireOne();
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
		final StringBuffer confirm = new StringBuffer(m_messages.vibeDataTable_TrashConfirmPurgeAll());
		if (areEntriesSelected()) {
			// If they have anything selected, we need to ensure they
			// meant to do a purge all and not simply a purge of what
			// they've selected.
			confirm.append("  ");
			confirm.append(m_messages.vibeDataTable_TrashConfirmPurgeAllWithSelections());
		}

		final boolean purgeBinders = areBindersInDataTable();
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
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
							// Perform the purge.
						    showBusySpinner();
							Long binderId = getFolderInfo().getBinderIdAsLong();
							GwtClientHelper.executeCommand(new TrashPurgeAllCmd(binderId, true), new AsyncCallback<VibeRpcResponse>() {
								@Override
								public void onFailure(Throwable t) {
								    hideBusySpinner();
									GwtClientHelper.handleGwtRPCFailure(
										t,
										m_messages.rpcFailure_TrashPurgeAll());
								}
								
								@Override
								public void onSuccess(VibeRpcResponse response) {
									// Display any messages we get back from the server.
								    hideBusySpinner();
									StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
									String messages = responseData.getStringValue();
									if (GwtClientHelper.hasString(messages)) {
										GwtClientHelper.deferredAlert(messages);
									}
									reloadUIAsync(purgeBinders);
								}
							});
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					confirm.toString());
			}
		});
	}
	
	/**
	 * Purges the selected entries from the trash.
	 * 
	 * Overrides the DataTableFolderViewBase.trashPurgeSelectedEntities()
	 * method.
	 */
	@Override
	public void trashPurgeSelectedEntities() {
		final boolean purgeBinders = areBindersInDataTable();
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
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
							// Perform the purge.
						    showBusySpinner();
							List<String> trashSelectionData = buildTrashSelectionList();
							Long binderId = getFolderInfo().getBinderIdAsLong();
							GwtClientHelper.executeCommand(new TrashPurgeSelectedEntitiesCmd(
									binderId,
									true,
									trashSelectionData),
									new AsyncCallback<VibeRpcResponse>() {
								@Override
								public void onFailure(Throwable t) {
								    hideBusySpinner();
									GwtClientHelper.handleGwtRPCFailure(
										t,
										m_messages.rpcFailure_TrashPurgeSelectedEntities());
								}
								
								@Override
								public void onSuccess(VibeRpcResponse response) {
									// Display any messages we get back from the server.
								    hideBusySpinner();
									StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
									String messages = responseData.getStringValue();
									if (GwtClientHelper.hasString(messages)) {
										GwtClientHelper.deferredAlert(messages);
									}
									reloadUIAsync(purgeBinders);
								}
							});
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.vibeDataTable_TrashConfirmPurge());
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
								// Perform the restore.
								trashRestoreAllImpl();
							}
	
							@Override
							public void rejected() {
								// No, they're not sure!
							}
						},
						m_messages.vibeDataTable_TrashConfirmRestoreAllWithSelections());
				}
			});
		}
		
		else {
			// ...otherwise, just perform the restore.
			trashRestoreAllImpl();
		}
	}
	
	/*
	 * Restores all the entries in the trash. 
	 */
	private void trashRestoreAllImpl() {
	    showBusySpinner();
		final boolean restoreBinders = areBindersInDataTable();
		Long binderId = getFolderInfo().getBinderIdAsLong();
		GwtClientHelper.executeCommand(new TrashRestoreAllCmd(binderId), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
			    hideBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_TrashRestoreAll());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display any messages we get back from the server.
			    hideBusySpinner();
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
	 * Overrides the DataTableFolderViewBase.trashRestoreSelectedEntities()
	 * method.
	 */
	@Override
	public void trashRestoreSelectedEntities() {
		// Perform the restore.
	    showBusySpinner();
		final boolean restoreBinders = areBindersInDataTable();
		List<String> trashSelectionData = buildTrashSelectionList();
		Long binderId = getFolderInfo().getBinderIdAsLong();
		GwtClientHelper.executeCommand(new TrashRestoreSelectedEntitiesCmd(binderId, trashSelectionData), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
			    hideBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_TrashRestoreSelectedEntities());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display any messages we get back from the server.
			    hideBusySpinner();
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
