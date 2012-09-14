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
import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.binderviews.EntryMenuPanel;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.DescriptionHtml;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.folderdata.GuestInfo;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper.DeletePurgeEntriesCallback;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.datatable.ActionMenuColumn;
import org.kablink.teaming.gwt.client.datatable.ApplyColumnWidths;
import org.kablink.teaming.gwt.client.datatable.AssignmentColumn;
import org.kablink.teaming.gwt.client.datatable.CommentsColumn;
import org.kablink.teaming.gwt.client.datatable.CustomColumn;
import org.kablink.teaming.gwt.client.datatable.DescriptionHtmlColumn;
import org.kablink.teaming.gwt.client.datatable.DownloadColumn;
import org.kablink.teaming.gwt.client.datatable.EmailAddressColumn;
import org.kablink.teaming.gwt.client.datatable.EntryPinColumn;
import org.kablink.teaming.gwt.client.datatable.EntryTitleColumn;
import org.kablink.teaming.gwt.client.datatable.GuestColumn;
import org.kablink.teaming.gwt.client.datatable.PresenceColumn;
import org.kablink.teaming.gwt.client.datatable.RatingColumn;
import org.kablink.teaming.gwt.client.datatable.ShareStringValueColumn;
import org.kablink.teaming.gwt.client.datatable.SizeColumnsDlg;
import org.kablink.teaming.gwt.client.datatable.SizeColumnsDlg.SizeColumnsDlgClient;
import org.kablink.teaming.gwt.client.datatable.StringColumn;
import org.kablink.teaming.gwt.client.datatable.TaskFolderColumn;
import org.kablink.teaming.gwt.client.datatable.VibeCheckboxCell;
import org.kablink.teaming.gwt.client.datatable.VibeDataGrid;
import org.kablink.teaming.gwt.client.datatable.VibeColumn;
import org.kablink.teaming.gwt.client.datatable.ViewColumn;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent.Change;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedUserWorkspacesEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.InvokeColumnResizerEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.InvokeSignGuestbookEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedUserWorkspacesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.ToggleSharedViewEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeAllEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreAllEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ViewPinnedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ViewSelectedEntryEvent;
import org.kablink.teaming.gwt.client.event.ViewWhoHasAccessEvent;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderRowsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderPinningStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderSortCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveSharedFilesStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetEntriesPinStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryPinInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.ShareStringValue;
import org.kablink.teaming.gwt.client.util.TaskFolderInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Base object of 'data table' based folder views.
 * 
 * @author drfoster@novell.com
 */
public abstract class DataTableFolderViewBase extends FolderViewBase
	implements ApplyColumnWidths,
	// Event handlers implemented by this class.
		ChangeEntryTypeSelectedEntriesEvent.Handler,
		ContentChangedEvent.Handler,
		ContributorIdsRequestEvent.Handler,
		CopySelectedEntriesEvent.Handler,
		DeleteSelectedEntriesEvent.Handler,
		DeleteSelectedUserWorkspacesEvent.Handler,
		DisableSelectedUsersEvent.Handler,
		EnableSelectedUsersEvent.Handler,
		InvokeColumnResizerEvent.Handler,
		InvokeDropBoxEvent.Handler,
		InvokeSignGuestbookEvent.Handler,
		LockSelectedEntriesEvent.Handler,
		MarkReadSelectedEntriesEvent.Handler,
		MarkUnreadSelectedEntriesEvent.Handler,
		MoveSelectedEntriesEvent.Handler,
		PurgeSelectedEntriesEvent.Handler,
		PurgeSelectedUserWorkspacesEvent.Handler,
		PurgeSelectedUsersEvent.Handler,
		QuickFilterEvent.Handler,
		ShareSelectedEntriesEvent.Handler,
		SubscribeSelectedEntriesEvent.Handler,
		ToggleSharedViewEvent.Handler,
		TrashPurgeAllEvent.Handler,
		TrashPurgeSelectedEntriesEvent.Handler,
		TrashRestoreAllEvent.Handler,
		TrashRestoreSelectedEntriesEvent.Handler,
		UnlockSelectedEntriesEvent.Handler,
		ViewPinnedEntriesEvent.Handler,
		ViewSelectedEntryEvent.Handler,
		ViewWhoHasAccessEvent.Handler
{
	private boolean						m_fixedLayout;				//
	private ColumnWidth					m_actionMenuColumnWidth;	//
	private ColumnWidth					m_defaultColumnWidth;		//
	private FolderRowPager 				m_dataTablePager;			// Pager widgets at the bottom of the data table.
	private List<FolderColumn>			m_folderColumnsList;		// The List<FolderColumn>' of the columns to be displayed.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private List<Long>					m_contributorIds;			//
	private Map<String, ColumnWidth>	m_defaultColumnWidths;		// Map of column names -> Default ColumnWidth objects.
	private Map<String, ColumnWidth>	m_columnWidths;				// Map of column names -> Current ColumnWidth objects.
	private SizeColumnsDlg				m_sizeColumnsDlg;			//
	private String						m_folderStyles;				// Specific style(s) for the for the folders that extend this.
	private String						m_quickFilter;				// Any quick filter that's active.
	private VibeDataGrid<FolderRow>		m_dataTable;				// The actual data table holding the view's information.
	
	protected GwtTeamingDataTableImageBundle m_images;	//

	// The following controls whether the display data read from the
	// server is dumped as part of the content of the view.
	private final static boolean DUMP_DISPLAY_DATA	= false;
	
	// The following is used as the ID on the <IMG> containing the
	// pin header.
	private final static String PIN_HEADER_IMG_ID	= "pinHeaderImg";
	
	// The following are used to construct the style names applied
	// to the columns and rows of the data table.
	private final static String STYLE_COL_BASE		= "vibe-dataTableFolderColumn";
	private final static String STYLE_COL_SELECT	= "select";
	private final static String STYLE_ROW_BASE		= "vibe-dataTableFolderRow";
	private final static String STYLE_ROW_EVEN		= "even";
	private final static String STYLE_ROW_ODD		= "odd";

	// Width, in pixels for the action menu show in conjunction with an
	// EntryTitleColumn.
	private final static int ACTION_MENU_WIDTH_PX	= 30;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTRIES,
		TeamingEvents.CONTENT_CHANGED,
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.COPY_SELECTED_ENTRIES,
		TeamingEvents.DELETE_SELECTED_ENTRIES,
		TeamingEvents.DELETE_SELECTED_USER_WORKSPACES,
		TeamingEvents.DISABLE_SELECTED_USERS,
		TeamingEvents.ENABLE_SELECTED_USERS,
		TeamingEvents.INVOKE_COLUMN_RESIZER,
		TeamingEvents.INVOKE_DROPBOX,
		TeamingEvents.INVOKE_SIGN_GUESTBOOK,
		TeamingEvents.LOCK_SELECTED_ENTRIES,
		TeamingEvents.MARK_READ_SELECTED_ENTRIES,
		TeamingEvents.MARK_UNREAD_SELECTED_ENTRIES,
		TeamingEvents.MOVE_SELECTED_ENTRIES,
		TeamingEvents.PURGE_SELECTED_ENTRIES,
		TeamingEvents.PURGE_SELECTED_USER_WORKSPACES,
		TeamingEvents.PURGE_SELECTED_USERS,
		TeamingEvents.QUICK_FILTER,
		TeamingEvents.SHARE_SELECTED_ENTRIES,
		TeamingEvents.SUBSCRIBE_SELECTED_ENTRIES,
		TeamingEvents.TOGGLE_SHARED_VIEW,
		TeamingEvents.TRASH_PURGE_ALL,
		TeamingEvents.TRASH_PURGE_SELECTED_ENTRIES,
		TeamingEvents.TRASH_RESTORE_ALL,
		TeamingEvents.TRASH_RESTORE_SELECTED_ENTRIES,
		TeamingEvents.UNLOCK_SELECTED_ENTRIES,
		TeamingEvents.VIEW_PINNED_ENTRIES,
		TeamingEvents.VIEW_SELECTED_ENTRY,
		TeamingEvents.VIEW_WHO_HAS_ACCESS,
	};
	
	/*
	 * Inner class used to provide list of FolderRow's.
	 */
	private class FolderRowAsyncProvider extends AsyncDataProvider<FolderRow> {
		private AbstractCellTable<FolderRow> m_vdt;	// The data table we're providing data for.
		
		/**
		 * Constructor method.
		 *
		 * @param vdt
		 * @param keyProvider
		 */
		public FolderRowAsyncProvider(AbstractCellTable<FolderRow> vdt, ProvidesKey<FolderRow> keyProvider) {
			// Initialize the super class and keep track of the data
			// table.
			super(keyProvider);
			m_vdt = vdt;
		}

		/**
		 * Called to asynchronously page through the data.
		 * 
		 * @param display
		 * 
		 * Overrides the AsyncDataProvider.onRowChanged() method.
		 */
		@Override
		protected void onRangeChanged(HasData<FolderRow> display) {
			final Long folderId = getFolderId();
			final Range range = display.getVisibleRange();
			final int rowsRequested = range.getLength();
			GwtClientHelper.executeCommand(
					new GetFolderRowsCmd(
						getFolderInfo(),
						m_folderColumnsList,
						range.getStart(),
						rowsRequested,
						m_quickFilter),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetFolderRows(),
						folderId);
					
					// If we have an entry menu...
					EntryMenuPanel emp = getEntryMenuPanel();
					if (null != emp) {
						// ...tell it to update the state of its items that
						// ...require entries be available.
						EntryMenuPanel.setEntriesAvailable(emp, false);
					}

					// ...and display a now items message.
					List<FolderRow> folderRows = new ArrayList<FolderRow>();
					m_vdt.setRowData( 0, folderRows);
					m_vdt.setRowCount(0            );
					postProcessRowDataAsync(folderRows);
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Did we read more rows than we asked for?
					FolderRowsRpcResponseData responseData = ((FolderRowsRpcResponseData) response.getResponseData());
					m_contributorIds = responseData.getContributorIds();
					List<FolderRow> folderRows = responseData.getFolderRows();
					int rowsRead = folderRows.size();
					if (rowsRead > rowsRequested) {
						// Yes!  This should only happen with pinned
						// entries.  Assert that's the case...
						GwtClientHelper.debugAssert(
							isPinning(),
							m_messages.vibeDataTable_InternalError_UnexpectedRowCount(
								rowsRequested,
								rowsRead));
						
						// ...and use the entries read as the new page
						// ...size.
						m_vdt.setPageSize(rowsRead);
					}
					
					// Apply the rows we read.
					m_vdt.setRowData( responseData.getStartOffset(), folderRows);
					m_vdt.setRowCount(responseData.getTotalRows()              );
					
					// If we have an entry menu...
					EntryMenuPanel emp = getEntryMenuPanel();
					if (null != emp) {
						// ...tell it to update the state of its items that
						// ...require entries be available.
						EntryMenuPanel.setEntriesAvailable(emp, (0 < rowsRead));
					}

					// Allow the view's that extend this do what ever
					// they need to do once a collection of rows has
					// been rendered.
					postProcessRowDataAsync(folderRows);
				}
			});
		}
	}

	/*
	 * Inner class to provide a key to a FolderRow.
	 */
	private class FolderRowKeyProvider implements ProvidesKey<FolderRow> {
		/**
		 * Returns the key used to identity a FolderRow.
		 * 
		 * @param fr
		 * 
		 * Implements the ProvidesKey.getKey() method.
		 */
		@Override
		public Object getKey(FolderRow fr) {
			// The key to a row is its entityId.
			return fr.getEntityId();
		}
	}

	/*
	 * Inner class used to provide simple pager for FolderRow's.
	 */
	private final static SimplePager.Resources SIMPLE_PAGER_RESOURCES = GWT.create(SimplePager.Resources.class);
	private class FolderRowPager extends SimplePager {
		public FolderRowPager() {
			// Simply initialize the super class.
			super(
				TextLocation.CENTER,
				SIMPLE_PAGER_RESOURCES,
				false,	// false -> No fast forward button...
				0,		//          ...hence no fast forward rows needed.
				true);	// true -> Show last page button.
		}

		/**
		 * Set the page start index.  We override this method to
		 * fix the problem that the last page display will be
		 * weird without this.
		 * 
		 * Overrides the SimplePager.setPageStart() method.
		 * 
		 * @param index
		 */
		@Override
		public void setPageStart(int index) {
		  if (getDisplay() != null) {
		    Range range = getDisplay().getVisibleRange();
		    int pageSize = range.getLength();

		    // Removed the min to show fixed ranges.
		    // if (isRangeLimited && display.isRowCountExact()) {
		    //	   index = Math.min(index, display.getRowCount() - pageSize);
		    // }

		    index = Math.max(0, index);
		    if (index != range.getStart()) {
		      getDisplay().setVisibleRange(index, pageSize);
		    }
		  }
		}
	}
	
	/*
	 * Inner class used to provide row selection for FolderRow's.
	 */
	protected class FolderRowSelectionModel extends MultiSelectionModel<FolderRow> {
		/**
		 * Constructor method.
		 * 
		 * @param keyProvider
		 */
		public FolderRowSelectionModel(FolderRowKeyProvider keyProvider) {
			// Simply initialize the super class.
			super(keyProvider);
		}
	}

	/*
	 * Inner class used to provide column sort handling for
	 * FolderRow's.
	 */
	private class FolderRowSortHandler implements ColumnSortEvent.Handler {
		/**
		 * Called when the user clicks a column to change the sort
		 * order.
		 * 
		 * Implements the ColumnSortEvent.Handler.onColumnSort()
		 * method.
		 * 
		 * @param event
		 */
		@Override
		public void onColumnSort(ColumnSortEvent event) {
			final Column<?, ?> column = event.getColumn();
			if (column instanceof VibeColumn) {
				@SuppressWarnings("unchecked")
				final FolderColumn fc = ((VibeColumn) column).getFolderColumn();
				final String  folderSortBy        = fc.getColumnSortKey();
				final boolean folderSortByChanged = (!(folderSortBy.equalsIgnoreCase(getFolderSortBy())));
				final boolean folderSortDescend   = (folderSortByChanged ? getFolderSortDescend() : (!getFolderSortDescend()));
				final SaveFolderSortCmd cmd = new SaveFolderSortCmd(getFolderInfo(), folderSortBy, (!folderSortDescend));
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_SaveFolderSort());
					}

					@Override
					public void onSuccess(VibeRpcResponse result) {
						// Store the sort change...
						setFolderSortBy(     folderSortBy     );
						setFolderSortDescend(folderSortDescend);
						
						// ...and reset the view to redisplay things
						// ...with it.
						resetViewAsync();
					}
				});
			}
			
			else if (column instanceof EntryPinColumn) {
				// Toggle the state of the pin on the header.
				final boolean pinning = (!(isPinning()));
				setPinning(pinning);
				String imgTitle;
				String imgUrl;
				if (pinning)
				     {imgUrl = m_images.orangePin().getSafeUri().asString(); imgTitle = m_messages.vibeDataTable_Alt_PinHeader_UnpinAll();}
				else {imgUrl = m_images.grayPin().getSafeUri().asString();   imgTitle = m_messages.vibeDataTable_Alt_PinHeader_PinAll();  }
				Element img = DOM.getElementById(PIN_HEADER_IMG_ID);
				img.setAttribute("src",   imgUrl  );
				img.setAttribute("title", imgTitle);

				// Are there any entries (i.e., not binders) in the
				// data table?
				final List<EntityId> pinnedEntityIds = getAllPinnedEntityIds(!pinning);
				if (GwtClientHelper.hasItems(pinnedEntityIds)) {
					// Yes!  Pin/unpin them.
				    showBusySpinner();
					final SetEntriesPinStateCmd cmd = new SetEntriesPinStateCmd(pinnedEntityIds, pinning);
					GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onFailure(Throwable caught) {
							hideBusySpinner();
							GwtClientHelper.handleGwtRPCFailure(
								caught,
								GwtTeaming.getMessages().rpcFailure_SetEntriesPinState());
						}

						@Override
						public void onSuccess(VibeRpcResponse result) {
							// Store the pin changes in the rows...
							for (EntityId entryId:  pinnedEntityIds) {
								getRowByEntityId(entryId).setPinned(pinning);
							}
							
							// ...and reset the view to redisplay things
							// ...with changes.
							hideBusySpinner();
							resetViewAsync();
						}
					});
				}
			}
		}
	}

	/*
	 * Inner class used to represent a select all check box in a data
	 * table's header.
	 * 
	 * @author rvasudevan@novell.com
	 */
	private class SelectAllHeader extends Header<Boolean> {
		private boolean m_checked;	//

		/**
		 * Constructor method.
		 * 
		 * @param cell
		 */
		public SelectAllHeader(CheckboxCell cell) {
			super(cell);
		}

		/**
		 * Get'er methods.
		 * 
		 * Overrides the Header.getValue() method.
		 * 
		 * @return
		 */
		@Override
		public Boolean getValue() {return m_checked;}
		 
		/**
		 * Set'er methods.
		 * 
		 * Set the state of the selection.  If a row is unselected, we
		 * can call this method to deselect the header checkbox
		 * 
		 * @param checked
		 */
		public void setValue(boolean checked) {m_checked = checked;}
		 
		/**
		 * Called to handle events captured by a check box header.
		 * 
		 * @param context
		 * @param elem
		 * @param event
		 * 
		 * Overrides the Header.onBroserEvent() method.
		 */
		@Override
		public void onBrowserEvent(Context context, Element elem, NativeEvent event) {
			Event evt = Event.as(event);
			int eventType = evt.getTypeInt();
			switch (eventType) {
			case Event.ONCHANGE:
				m_checked = (!m_checked);
			}
			super.onBrowserEvent(context, elem, event);
		}
	}

	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param folderStyles
	 */
	public DataTableFolderViewBase(BinderInfo folderInfo, ViewReady viewReady, String folderStyles) {
		// Initialize the super class...
		super(folderInfo, viewReady, "vibe-dataTableFolder", true);

		// ...and initialize any other data members.
		initDataMembers(folderStyles);
	}
	
	/*
	 * Get'er methods.
	 */
	protected AbstractCellTable<FolderRow> getDataTable()          {return m_dataTable;                                   }
	private   boolean                      getFolderSortDescend()  {return getFolderDisplayData().getFolderSortDescend(); }
	private   int                          getFolderPageSize()     {return getFolderDisplayData().getFolderPageSize();    }
	private   FolderType                   getFolderType()         {return getFolderInfo().getFolderType();               }
	private   Map<String, String>          getFolderColumnWidths() {return getFolderDisplayData().getFolderColumnWidths();}
	private   String                       getFolderSortBy()       {return getFolderDisplayData().getFolderSortBy();      }
	
	/*
	 * Set'er methods.
	 */
	private void setFolderSortBy(     String  folderSortBy)      {getFolderDisplayData().setFolderSortBy(folderSortBy);          }
	private void setFolderSortDescend(boolean folderSortDescend) {getFolderDisplayData().setFolderSortDescend(folderSortDescend);}

	/*
	 * Adds a column to manage pinning entries.
	 */
	private void addPinColumn(final FolderRowSelectionModel selectionModel, int colIndex, double pctTotal) {
		// Define the pin header...
		String imgTitle;
		String imgUrl;
		if (isPinning())
	         {imgUrl = m_images.orangePin().getSafeUri().asString(); imgTitle = m_messages.vibeDataTable_Alt_PinHeader_UnpinAll();}
		else {imgUrl = m_images.grayPin().getSafeUri().asString();   imgTitle = m_messages.vibeDataTable_Alt_PinHeader_PinAll();  }
		Image i = new Image();
		i.setUrl(  imgUrl  );
		i.setTitle(imgTitle);
		i.getElement().setId(PIN_HEADER_IMG_ID);
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.add(i);
		SafeHtml pinHtml = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());

		// ...define a column for it...
		EntryPinColumn<FolderRow> column = new EntryPinColumn<FolderRow>() {
			@Override
			public EntryPinInfo getValue(FolderRow fr) {
				EntryPinInfo reply;
				if (fr.isBinder()) {
					reply = null;
				}
				else {
					reply = new EntryPinInfo(
						fr.getPinned(),
						getFolderId(),
						fr.getEntityId().getEntityId());
				}
				return reply;
			}
		};
		
		// ...and connect it all together.
		column.setSortable(   true                                             );	// Not really sorted, but enters/exits 'pin' mode. 
	    m_dataTable.addColumn(column, pinHtml                                  );
	    setColumnStyles(      column, FolderColumn.COLUMN_PIN, colIndex        );
	    setColumnWidth(               FolderColumn.COLUMN_PIN, column, pctTotal);
	}
	
	/*
	 * Adds a select column to the data table including a select all
	 * checkbox in the header.
	 * 
	 * @author rvasudevan@novell.com
	 */
	private void addSelectColumn(final FolderRowSelectionModel selectionModel, int colIndex, double pctTotal) {
		// Define the select all checkbox in the header...
		CheckboxCell cbSelectAllCell = new CheckboxCell();
		final SelectAllHeader saHeader = new SelectAllHeader(cbSelectAllCell);
		saHeader.setUpdater(new ValueUpdater<Boolean>() {
			@Override
			public void update(Boolean checked) {
				List<FolderRow> rows = m_dataTable.getVisibleItems();
				if (null != rows) {
					for (FolderRow row : rows) {
						selectionModel.setSelected(row, checked);
					}
				}
				
				// If we have an entry menu...
				EntryMenuPanel emp = getEntryMenuPanel();
				if (null != emp) {
					// ...tell it to update the state of its items that
					// ...require a selection.
					EntryMenuPanel.setEntriesSelected(emp,  checked                                   );
					EntryMenuPanel.setEntrySelected(  emp, (checked && (1 == getSelectedEntryCount())));
				}
			}
		});

		// ...define a column for it...
		VibeCheckboxCell cbRowCell = new VibeCheckboxCell();
		final Column<FolderRow, Boolean> column = new Column<FolderRow, Boolean>(cbRowCell) {
			@Override
			public Boolean getValue(FolderRow row) {
				return selectionModel.isSelected(row);
			}
		};

		// ...connect updating the contents of the table when the
		// ...check box is checked or unchecked...
		column.setFieldUpdater(new FieldUpdater<FolderRow, Boolean>() {
			@Override
			public void update(int index, FolderRow row, Boolean checked) {
				selectionModel.setSelected(row, checked);
				if (!checked) {
					saHeader.setValue(checked);
					checked = areEntriesSelected();
				}

				// If we have an entry menu...
				EntryMenuPanel emp = getEntryMenuPanel();
				if (null != emp) {
					// ...tell it to update the state of its items that
					// ...require a selection.
					EntryMenuPanel.setEntriesSelected(emp,  checked                                   );
					EntryMenuPanel.setEntrySelected(  emp, (checked && (1 == getSelectedEntryCount())));
				}
			};
		});

		// ...and connect it all together.
		column.setSortable(     false                                       ); 
	    m_dataTable.addColumn(column, saHeader);
	    setColumnStyles(column, FolderColumn.COLUMN_SELECT, colIndex        );
	    setColumnWidth(         FolderColumn.COLUMN_SELECT, column, pctTotal);
	}

	/**
	 * Allows the view's that extend this do what ever they need to
	 * to these widths for their own purposes.
	 * 
	 * @param columnWidths
	 */
	protected void adjustFixedColumnWidths(Map<String, ColumnWidth> columnWidths) {
		// By default, there are no adjustments.
	}
	
	protected void adjustFloatColumnWidths(Map<String, ColumnWidth> columnWidths) {
		// By default, there are no adjustments.
	}

	/**
	 * Applies the column widths in a column widths Map.
	 *  
	 * Implements the ApplyColumnWidths.applyColumnWidths() method.
	 * 
	 * @param folderColumns
	 * @param columnWidths
	 */
	@Override
	public void applyColumnWidths(List<FolderColumn> folderColumns, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth) {
		double pctTotal = ColumnWidth.sumPCTWidths(folderColumns, columnWidths, defaultColumnWidth);
		for (FolderColumn fc:  folderColumns) {
			String      cName = fc.getColumnName();
			ColumnWidth cw    = columnWidths.get(cName);
			setColumnWidth(
				((null == cw) ? defaultColumnWidth : cw),
				m_dataTable.getColumn(getColumnIndex(folderColumns, cName)),
				pctTotal);
		}
	}

	/**
	 * Returns true if there are binders in the data table and false
	 * otherwise.
	 * 
	 * @return
	 */
	final public boolean areBindersInDataTable() {
		// If we've got more than 1 page of entries, we'll assume there
		// will be a binder somewhere.  Do we have more than 1 page of
		// entries?
		int totalRows = m_dataTable.getRowCount();
	    int pageSize  = m_dataTablePager.getDisplay().getVisibleRange().getLength();
	    boolean reply = (totalRows > pageSize);
	    if (!reply) {
	    	// No!  We need to scan the rows in the table to see if
	    	// there any binders.
			List<FolderRow> rows = m_dataTable.getVisibleItems();
			if (null != rows) {
				for (FolderRow row:  rows) {
					if (row.isBinder()) {
						reply = true;
						break;
					}
				}
			}
	    }
	    
	    // If we get here, reply is true if there are binders in the
	    // data table and false otherwise.  Return it.
	    return reply;
	}

	/**
	 * Returns true if there are binders selected in the data table and
	 * false otherwise.
	 * 
	 * @return
	 */
	final public boolean areBindersSelectedInDataTable() {
		boolean reply = false;
		List<FolderRow> rows = m_dataTable.getVisibleItems();
		if (null != rows) {
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row:  rows) {
				if (fsm.isSelected(row) && row.isBinder()) {
					reply = true;
					break;
				}
			}
		}
		
	    // If we get here, reply is true if there are binders selected
		// in the data table and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if any rows are selected and false otherwise.
	 * 
	 * @return
	 */
	final public boolean areEntriesSelected() {
		// Are there any visible rows in the table?
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (null != rows) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Return true.  No need to look any further.
					return true;
				}
			}
		}
		
		// If we get here, no rows were selected.  Return false.
		return false;
	}

	/*
	 * Returns true if entries can be pinned in the current view and
	 * false otherwise.
	 */
	private boolean canPinEntries() {
		return getFolderDisplayData().getFolderSupportsPinning();
	}
	
	/*
	 * Returns true if entries can be selected in the current view and
	 * false otherwise.
	 */
	private boolean canSelectEntries() {
		return true;
	}

	/*
	 * Removes the selection from the rows in a List<FolderRows>.
	 */
	private void deselectRows(List<FolderRow> rows) {
		// Do we have any rows to remove the selection from?
		if (GwtClientHelper.hasItems(rows)) {
			// Yes!  Scan them...
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// ...and remove the selection from each.
				fsm.setSelected(row, false);
			}
		}
	}

	/*
	 * Returns a List<EntityIds> of the EntityId's of all the entries
	 * (excluding binders) from the data table whose pinned state
	 * matches the given boolean.
	 */
	private List<EntityId> getAllPinnedEntityIds(boolean pinned) {
		// Are there any rows in the table?
		List<EntityId>  reply      = new ArrayList<EntityId>();
		List<FolderRow> folderRows = m_dataTable.getVisibleItems();
		if (null != folderRows) {
			// Yes!  Scan them
			for (FolderRow fr : folderRows) {
				// Is this row an entry that matches the requested
				// pinned state?
				EntityId rowEID = fr.getEntityId();
				if (rowEID.isEntry() && (fr.getPinned() == pinned)) {
					// Yes!  Add its entity ID to the List<EntityId>.
					reply.add(rowEID);
				}
			}
		}
		
		// If we get here, reply refers to List<EntityId> of the
		// EntityId's of the entries from the data table that match the
		// given pinned state.  Return it.
		return reply;
	}
	
	/*
	 * Returns the index of a named FolderColumn from a
	 * List<FolderColumn>.
	 */
	private static int getColumnIndex(List<FolderColumn> folderColumns, String cName) {
		// Scan the List<FolderColumn>...
		for (FolderColumn fc:  folderColumns) {
			// ...is this the column in question?
			if (fc.getColumnName().equals(cName)) {
				// Yes!  Return its display index.
				return fc.getDisplayIndex();
			}
		}

		// If we get here, we couldn't find the column in question. 
		// Return -1.
		return (-1);
	}
	
	/*
	 * Returns a List<FolderColumn> we can use to run the column sizing
	 * dialog.
	 */
	private List<FolderColumn> getColumnsForSizing() {
		// Allocate a List<FolderColumn> we can return...
		List<FolderColumn> reply = new ArrayList<FolderColumn>();

		// ...if this view supports entry selections...
		FolderColumn fc;
		if (canSelectEntries()) {
			// ...add a column for the checkbox selector...
			fc = new FolderColumn();
			fc.setColumnName(FolderColumn.COLUMN_SELECT);
			fc.setColumnTitle(m_messages.vibeDataTable_Select());
			reply.add(fc);
		}
		
		// ...if this view supports entry pinning...
		if (canPinEntries()) {
			// ...add a column for the pin selector...
			fc = new FolderColumn();
			fc.setColumnName(FolderColumn.COLUMN_PIN);
			fc.setColumnTitle(m_messages.vibeDataTable_Pin());
			reply.add(fc);
		}

		// ...copy all the defined columns...
		for (FolderColumn fcScan:  m_folderColumnsList) {
			reply.add(fcScan);
		}
		
		// ...and return the List<FolderColumn>.
		return reply;
	}

	/**
	 * Returns the widget to use for displaying the table empty message.
	 * 
	 * Provided as a convenience method.  Classes that extend this may
	 * override to provide whatever they want displayed.
	 * 
	 * @return
	 */
	protected Widget getEmptyTableWidget() {
		return
			new Label(
				isPinning()                                  ?
					m_messages.vibeDataTable_Empty_Pinning() :
					m_messages.vibeDataTable_Empty());
	}

	/*
	 * Returns the URL to use for a row's image.
	 */
	private String getRowImageUrl(FolderRow fr) {
		// Is the row a binder?
		String reply;
		if (fr.isBinder()) {
			// Yes!  Do we have a specific image for it?
			String binderIcon = fr.getBinderIcon(BinderIconSize.getListViewIconSize());
			if (GwtClientHelper.hasString(binderIcon)) {
				// Yes!  Use it to construct the URL.
				String imagesPath = GwtClientHelper.getRequestInfo().getImagesPath();
				if (binderIcon.startsWith("/"))
				     reply = (imagesPath + binderIcon.substring(1));
				else reply = (imagesPath + binderIcon);
			}
			
			else {
				// No, we don't have a specific image for it!  Use the
				// generic folder image.
				ImageResource binderImgRes = GwtTeaming.getFilrImageBundle().folder();
				reply = binderImgRes.getSafeUri().asString();
			}
		}
		
		else {
			// No, the row isn't a binder!  Use the generic entry
			// image.
			ImageResource binderImgRes = GwtTeaming.getFilrImageBundle().entry();
			reply = binderImgRes.getSafeUri().asString();
		}
		
		// If we get here, reply refers to the URL for the row's image.
		// Return it.
		return reply;
	}
	
	/*
	 * Returns the FolderRow for the given entity ID.
	 */
	private FolderRow getRowByEntityId(EntityId entityId) {
		// Are there any rows in the table?
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (null != rows) {
			// Yes!  Scan them
			for (FolderRow row : rows) {
				// Is this row an entry?
				EntityId rowEID = row.getEntityId();
				if (rowEID.equalsEntityId(entityId)) {
					return row;
				}
			}
		}
		
		// If we get here, we couldn't find the row in question.
		// Return null.
		return null;
	}
	
	/**
	 * Returns a List<EntityIds> of the entity IDs of the selected rows
	 * from the table.
	 * 
	 * @return
	 */
	public List<EntityId> getSelectedEntityIds() {
		// Are there any selected rows in the table?
		List<EntityId>   reply = new ArrayList<EntityId>();
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (null != rows) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Add its entity ID to the List<EntityId>.
					reply.add(row.getEntityId());
				}
			}
		}
		
		// If we get here, reply refers to List<EntityId> of the entity
		// IDs of the selected rows from the data table.  Return it.
		return reply;
	}

	/*
	 * Returns a count of the selected entries (i.e., folder entries,
	 * not binders, ...)
	 */
	private int getSelectedEntryCount() {
		int reply = 0;
		List<EntityId> selectedEntities = getSelectedEntityIds();
		if (GwtClientHelper.hasItems(selectedEntities)) {
			for (EntityId selectedEntity:  selectedEntities) {
				if (selectedEntity.isEntry()) {
					reply += 1;
				}
			}
		}
		return reply;
	}
	
	/*
	 * Initializes various data members for the class.
	 */
	private void initDataMembers(String folderStyles) {
		// Store the parameters...
		m_folderStyles = folderStyles;

		// Initialize a map of the ColumnWidth's used in the data
		// table...
		m_columnWidths = new HashMap<String, ColumnWidth>();

		// ...initialize the remaining data members...
		m_images      = GwtTeaming.getDataTableImageBundle();
		m_fixedLayout = isFixedLayoutImpl(m_dataTable);
		if (m_fixedLayout)
		     initDataMembersFixed();
		else initDataMembersFloat();
		m_actionMenuColumnWidth = new ColumnWidth(ACTION_MENU_WIDTH_PX, Unit.PX);
		
		// ...and store the initial columns widths as the defaults.
		m_defaultColumnWidths = ColumnWidth.copyColumnWidths(m_columnWidths);
	}

	/*
	 * Initializes any additional data members required when using a
	 * fixed table layout.
	 * 
	 * Note:  Except for the select and pin columns, the values for the
	 *    column width were extracted from the implementation of
	 *    folder_view_common2.jsp or view_trash.jsp.
	 */
	private void initDataMembersFixed() {
		// The following defines the default width that will be used for
		// columns that don't have one specified.
		m_defaultColumnWidth = new ColumnWidth(20);

		// Add the widths for predefined column names...
		m_columnWidths.put(FolderColumn.COLUMN_AUTHOR,   new ColumnWidth(24         ));	// Unless otherwise specified...
		m_columnWidths.put(FolderColumn.COLUMN_COMMENTS, new ColumnWidth(70, Unit.PX)); // ...the widths default to...
		m_columnWidths.put(FolderColumn.COLUMN_DOWNLOAD, new ColumnWidth( 8         ));	// ...be a percentage value.
		m_columnWidths.put(FolderColumn.COLUMN_HTML,     new ColumnWidth(10         ));
		m_columnWidths.put(FolderColumn.COLUMN_LOCATION, new ColumnWidth(30         ));
		m_columnWidths.put(FolderColumn.COLUMN_NUMBER,   new ColumnWidth( 5         ));
		m_columnWidths.put(FolderColumn.COLUMN_RATING,   new ColumnWidth(10         ));
		m_columnWidths.put(FolderColumn.COLUMN_SIZE,     new ColumnWidth( 8         ));
		m_columnWidths.put(FolderColumn.COLUMN_STATE,    new ColumnWidth( 8         ));
		m_columnWidths.put(FolderColumn.COLUMN_TITLE,    new ColumnWidth(28         ));

		// ...and then add the widths for everything else.
		m_columnWidths.put(FolderColumn.COLUMN_SELECT,   new ColumnWidth(33, Unit.PX));
		m_columnWidths.put(FolderColumn.COLUMN_PIN,      new ColumnWidth(33, Unit.PX));

		// Finally, let the view's that extend this do what ever they
		// need to these widths for their own purposes.
		adjustFixedColumnWidths(m_columnWidths);
	}
	
	/*
	 * Initializes any additional data members required when using a
	 * floating table layout.
	 */
	private void initDataMembersFloat() {
		// The following defines the default width that will be used for
		// columns that don't have one specified.
		m_defaultColumnWidth = null;	// null -> Flow (no specific width.)

		// For a floating table layout, the only column whose width we
		// explicitly set is the title.
		m_columnWidths.put(FolderColumn.COLUMN_TITLE, new ColumnWidth(100, Unit.PCT));
		
		// Finally, let the view's that extend this do what ever they
		// need to these widths for their own purposes.
		adjustFloatColumnWidths(m_columnWidths);
	}
	
	/*
	 * Initializes the columns in the data table.
	 */
	private void initTableColumns(final FolderRowSelectionModel selectionModel) {
		// Clear the data table's column sort list.
		ColumnSortList csl = m_dataTable.getColumnSortList();
		csl.clear();

		// If this folder supports entry selections...
		double pctTotal = ColumnWidth.sumPCTWidths(m_folderColumnsList, m_columnWidths, m_defaultColumnWidth);
		int colIndex = 0;
		if (canSelectEntries()) {
			// ...add a column for a checkbox selector.
			addSelectColumn(selectionModel, colIndex++, pctTotal);
		}

		// If this folder supports entry pinning...
		if (canPinEntries()) {
			// ...add a column to manage pinning the entry.
			addPinColumn(selectionModel, colIndex++, pctTotal);
		}

	    // Scan the columns defined in this folder.
		for (final FolderColumn fc:  m_folderColumnsList) {
			// For some columns (e.g., entry titles), we define a 2nd,
			// support column for it.  These variables are used to
			// define that.
			ColumnWidth					supportColumnWidth    = null;
			String						supportColumnTitle    = null;
			String						supportColumnStyles   = null;
			VibeColumn<FolderRow, ?>	supportColumn         = null;
			
			// We need to define a VibeColumn<FolderRow, ?> of some
			// sort for each one.  Is this a column that should show
			// a download link for?
			VibeColumn<FolderRow, ?> column;
			final String cName = fc.getColumnEleName();
			if (FolderColumn.isColumnDownload(cName)) {
				// Yes!  Create a DownloadColumn for it.
				column = new DownloadColumn<FolderRow>(fc) {
					@Override
					public Long getValue(FolderRow fr) {
						String value = fr.getColumnValueAsString(fc);
						Long reply;
						if (GwtClientHelper.hasString(value))
						     reply = fr.getEntityId().getEntityId();
						else reply = null;
						return reply;
					}
				};
			}
			
			// No, this column doesn't show a download link!  Does it
			// show presence?
			else if (FolderColumn.isColumnPresence(cName) || FolderColumn.isColumnFullName(cName)) {
				// Yes!  Create a PresenceColumn for it.
				column = new PresenceColumn<FolderRow>(fc, showProfileEntryForPresenceWithNoWS()) {
					@Override
					public PrincipalInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsPrincipalInfo(fc);
					}
				};
			}

			// No, this column doesn't show presence either!  Does it
			// show a rating?
			else if (FolderColumn.isColumnRating(cName)) {
				// Yes!  Create a RatingColumn for it.
				column = new RatingColumn<FolderRow>(fc) {
					@Override
					public Integer getValue(FolderRow fr) {
						String value = fr.getColumnValueAsString(fc);
						if (null != value) value = value.trim();
						Integer reply;
						if (GwtClientHelper.hasString(value)) {
							reply = Math.round(Float.valueOf(value));
						}
						else reply = null;
						return reply;
					}
				};
			}
			
			// No, this column doesn't show a rating either!  Does it
			// show an entry title?
			else if (FolderColumn.isColumnTitle(cName)) {
				// Yes!  Create a EntryTitleColumn for it.
				column = new EntryTitleColumn<FolderRow>(fc) {
					@Override
					public EntryTitleInfo getValue(FolderRow fr) {
						EntryTitleInfo reply = fr.getColumnValueAsEntryTitle(fc);
						if (null != reply) {
							// Create the rows's Image widget...
							Image rowImg = new Image();
							rowImg.getElement().setAttribute("align", "absmiddle");
							rowImg.setUrl(getRowImageUrl(fr));
							
							// ...apply any scaling to the Image...
							int width  = BinderIconSize.getListViewIconSize().getBinderIconWidth();  if ((-1) != width)  rowImg.setWidth( width  + "px");
							int height = BinderIconSize.getListViewIconSize().getBinderIconHeight(); if ((-1) != height) rowImg.setHeight(height + "px");

							// ...and store the Image in the reply.
							reply.setClientItemImage(rowImg);
						}
						return reply;
					}
				};

				// Is this entry title for other than an item in a
				// trash folder? 
				if (!(isTrash())) {
					// Yes!  Create an ActionMenuColumn for it.
					supportColumn = new ActionMenuColumn<FolderRow>(fc, getFolderId()) {
						@Override
						public EntryTitleInfo getValue(FolderRow fr) {
							return fr.getColumnValueAsEntryTitle(fc);
						}
					};
					supportColumn.setSortable(false);
					supportColumnWidth  = m_actionMenuColumnWidth;
					supportColumnStyles = (STYLE_COL_BASE + " vibe-dataTableActions-column");
				}
			}
			
			// No, this column doesn't show an entry title either!
			// Does it show a view link?
			else if (FolderColumn.isColumnView(cName)) {
				// Yes!  Create a ViewColumn for it.
				column = new ViewColumn<FolderRow>(fc) {
					@Override
					public ViewFileInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsViewFile(fc);
					}
				};
			}
			
			// No, this column doesn't show a view link either!  Is it
			// a custom column?
			else if (FolderColumn.isColumnCustom(fc)) {
				// Yes!  Create a CustomColumn for it.
				column = new CustomColumn<FolderRow>(fc) {
					@Override
					public Object getValue(FolderRow fr) {
						Object             reply = fr.getColumnValueAsEntryEvent(fc);
						if (null == reply) reply = fr.getColumnValueAsEntryLink(fc);
						if (null == reply) reply = fr.getColumnValueAsString(fc);
						return reply;
					}
				};
			}
			
			// No, this column doesn't show a custom column either!
			// Does it display assignment information of some sort?
			else if (AssignmentInfo.isColumnAssigneeInfo(cName) ||
			         FolderColumn.isColumnSharedBy(      cName) ||
			         FolderColumn.isColumnSharedWith(    cName)) {
				// Yes!  Create an AssignmentColumn for it.
				column = new AssignmentColumn<FolderRow>(fc) {
					@Override
					public List<AssignmentInfo> getValue(FolderRow fr) {
						return fr.getColumnValueAsAssignmentInfos(fc);
					}
				};
			}
			
			// No, this column doesn't show an assignment either!  Is
			// it a collection of task folders?
			else if (FolderColumn.isColumnTaskFolders(cName)) {
				// Yes!  Create an TaskFolderColumn for it.
				column = new TaskFolderColumn<FolderRow>(fc) {
					@Override
					public List<TaskFolderInfo> getValue(FolderRow fr) {
						return fr.getColumnValueAsTaskFolderInfos(fc);
					}
				};
			}

			// No, this column isn't a collection of task folders
			// either!  Is it an HTML description column?
			else if (FolderColumn.isColumnDescriptionHtml(cName)) {
				// Yes!  Create a DescriptionHtmlColumn for it.
				column = new DescriptionHtmlColumn<FolderRow>(fc) {
					@Override
					public DescriptionHtml getValue(FolderRow fr) {
						return fr.getColumnValueAsDescriptionHtml(fc);
					}
				};
			}
			
			// No, this column isn't an HTML description column either!
			// Is it the signer of a guest book?
			else if (FolderColumn.isColumnGuest(cName)) {
				// Yes!  Create a GuestColumn for it.
				column = new GuestColumn<FolderRow>(fc) {
					@Override
					public GuestInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsGuestInfo(fc);
					}
				};
			}
			
			// No, this column isn't signer of a guest book either!  Is
			// it an email address?
			else if (FolderColumn.isColumnEmailAddress(cName)) {
				// Yes!  Create a EmailAddressColumn for it.
				column = new EmailAddressColumn<FolderRow>(fc) {
					@Override
					public EmailAddressInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsEmailAddress(fc);
					}
				};
			}
			
			// No, this column isn't an email address either!  Is it a
			// share string value?
			else if (FolderColumn.isColumnShareStringValue(cName)) {
				// Yes!  Create a ShareStringValueColumn for it.
				column = new ShareStringValueColumn<FolderRow>(fc) {
					@Override
					public List<ShareStringValue> getValue(FolderRow fr) {
						List<ShareStringValue> reply;
						if      (cName.equals(FolderColumn.COLUMN_SHARE_ACCESS))     reply = fr.getColumnValueAsShareAccessInfos(    fc);
						else if (cName.equals(FolderColumn.COLUMN_SHARE_DATE))       reply = fr.getColumnValueAsShareDateInfos(      fc);
						else if (cName.equals(FolderColumn.COLUMN_SHARE_EXPIRATION)) reply = fr.getColumnValueAsShareExpirationInfos(fc);
						else if (cName.equals(FolderColumn.COLUMN_SHARE_MESSAGE))    reply = fr.getColumnValueAsShareMessageInfos(   fc);
						else                                                         reply = null;
						return reply;
					}
				};
			}
			
			// No, this column isn't a shared value either!  Is it a
			// comments count?
			else if (FolderColumn.isColumnComments(cName)) {
				// Yes!  Create a CommentsColumn for it.
				column = new CommentsColumn<FolderRow>(fc) {
					@Override
					public CommentsInfo getValue(FolderRow fr) {
						CommentsInfo reply = fr.getColumnValueAsComments(fc);
						if (null != reply) {
							// Create an Image widget for the manage
							// comments dialog...
							Image rowImg = new Image();
							rowImg.addStyleName("vibe-manageCommentsDlg-captionImg");
							rowImg.getElement().setAttribute("align", "absmiddle");
							rowImg.setUrl(getRowImageUrl(fr));
							
							// ...apply any scaling to the Image...
							int width  = BinderIconSize.getDialogCaptionIconSize().getBinderIconWidth();  if ((-1) != width)  rowImg.setWidth( width  + "px");
							int height = BinderIconSize.getDialogCaptionIconSize().getBinderIconHeight(); if ((-1) != height) rowImg.setHeight(height + "px");

							// ...and store the Image in the reply.
							reply.setClientItemImage(rowImg);
						}
						return reply;
					}
				};
			}
			
			else {
				// No, this column isn't a comments count either!
				// Define a StringColumn for it.
				column = new StringColumn<FolderRow>(fc) {
					@Override
					public String getValue(FolderRow fr) {
						return fr.getColumnValueAsString(fc);
					}
				};
			}

			// Complete the initialization of the column.
			fc.setDisplayIndex(   colIndex                       );
			column.setSortable(   fc.isColumnSortable()          );
			m_dataTable.addColumn(column, fc.getColumnTitle()    );
		    setColumnStyles(      column, cName, colIndex++      );
		    setColumnWidth(               cName, column, pctTotal);

		    // Do we have a support column for the column we just
		    // added?
		    if (null != supportColumn) {
		    	// Yes!  Add it too.
		    	if (GwtClientHelper.hasString(supportColumnTitle))
			         m_dataTable.addColumn(supportColumn, supportColumnTitle                       );
		    	else m_dataTable.addColumn(supportColumn, SafeHtmlUtils.fromTrustedString("&nbsp;"));
		    	if (GwtClientHelper.hasString(supportColumnStyles)) {
		    		m_dataTable.addColumnStyleName(colIndex, supportColumnStyles);
		    	}
		    	colIndex += 1;
		    	if (null != supportColumnWidth) {
		    		m_dataTable.setColumnWidth(supportColumn, ColumnWidth.getWidthStyle(supportColumnWidth));
		    	}
		    }

		    // Is this the column we're sorted on?
		    if (fc.getColumnSortKey().equalsIgnoreCase(getFolderSortBy())) {
		    	// Yes!  Tell the data table about it.
				csl.push(new ColumnSortInfo(column, (!getFolderSortDescend())));
		    }
		}
	}

	/*
	 * Return true if the data table should used a fixed layout and
	 * false other wise. 
	 */
	@SuppressWarnings("unused")
	private boolean isFixedLayoutImpl(CellTable<FolderRow> ct) {
		// A CellTable can optionally use a fixed table layout.
		return false;
	}
	
	private boolean isFixedLayoutImpl(DataGrid<FolderRow> dg) {
		// A DataGrid must always use a fixed table layout.
		return true;
	}
	
	/*
	 * Asynchronously loads the column information for the folder.
	 */
	private void loadFolderColumnsAsync() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadFolderColumnsNow();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Synchronously loads the column information for the folder.
	 */
	private void loadFolderColumnsNow() {
		GwtClientHelper.executeCommand(
				new GetFolderColumnsCmd(getFolderInfo()),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderColumns(),
					getFolderId());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the folder columns and complete the population of the view.
				FolderColumnsRpcResponseData responseData = ((FolderColumnsRpcResponseData) response.getResponseData());
				m_folderColumnsList = responseData.getFolderColumns();
				populateViewAsync();
			}
		});
	}
	
	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Handles ChangeEntryTypeSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the ChangeEntryTypeSelectedEntriesEvent.Handler.onChangeEntryTypeSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeEntryTypeSelectedEntries(ChangeEntryTypeSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the change.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.changeEntryTypes(
				getFolderType(),
				selectedEntityIds);
		}
	}
	
	/**
	 * Handles ContentChangedEvent's received by this class.
	 * 
	 * Implements the ContentChangedEvent.Handler.onContentChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContentChanged(final ContentChangedEvent event) {
		// If a share changed in the 'Shared By Me' collection view...
		if (Change.SHARING.equals(event.getChange()) && getFolderInfo().getCollectionType().equals(CollectionType.SHARED_BY_ME)) {
			// ...force the UI to refresh.
			FullUIReloadEvent.fireOneAsync();
		}
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsRequest(ContributorIdsRequestEvent event) {
		// Is the event targeted to this folder?
		final Long eventBinderId = event.getBinderId();
		if (eventBinderId.equals(getFolderId())) {
			// Yes!  Asynchronously fire the corresponding reply event
			// with the contributor IDs.
			ScheduledCommand doReply = new ScheduledCommand() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(
						new ContributorIdsReplyEvent(
							eventBinderId,
							m_contributorIds));
				}
			};
			Scheduler.get().scheduleDeferred(doReply);
		}
	}
	
	/**
	 * Handles CopySelectedEntriesEvent's received by this class.
	 * 
	 * Implements the CopySelectedEntriesEvent.Handler.onCopySelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopySelectedEntries(CopySelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the copy.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.copyEntries(
				getFolderType(),
				selectedEntityIds);
		}
	}
	
	/**
	 * Handles DeleteSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedEntriesEvent.Handler.onDeleteSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedEntries(DeleteSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Delete the selected entries and reset the view to
			// redisplay things with the entries deleted.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			final boolean deletingBinders = EntityId.areBindersInEntityIds(selectedEntityIds);
			BinderViewsHelper.deleteFolderEntries(
					selectedEntityIds,
					new DeletePurgeEntriesCallback() {
				@Override
				public void operationCanceled() {
					if (deletingBinders) {
						GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
						FullUIReloadEvent.fireOne();
					}
					else {
						resetViewAsync();
					}
				}

				@Override
				public void operationComplete() {
					if (deletingBinders) {
						GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
						FullUIReloadEvent.fireOne();
					}
					else {
						resetViewAsync();
					}
				}
				
				@Override
				public void operationFailed() {
					// Nothing to do.  The delete call will have told
					// the user about the failure.
				}
			});
		}
	}
	
	/**
	 * Handles DeleteSelectedUserWorkspacesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedUserWorkspacesEvent.Handler.onDeleteSelectedUserWorkspaces() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedUserWorkspaces(DeleteSelectedUserWorkspacesEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the delete.
			BinderViewsHelper.deleteUserWorkspaces(
				EntityId.getLongsFromEntityIds(
					getSelectedEntityIds()));
		}
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles DisableSelectedUsersEvent's received by this class.
	 * 
	 * Implements the DisableSelectedUsersEvent.Handler.onDisableSelectedUsers() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDisableSelectedUsers(DisableSelectedUsersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the disable.
			BinderViewsHelper.disableUsers(
				EntityId.getLongsFromEntityIds(
					getSelectedEntityIds()));
		}
	}
	
	/**
	 * Handles EnableSelectedUsersEvent's received by this class.
	 * 
	 * Implements the EnableSelectedUsersEvent.Handler.onEnableSelectedUsers() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEnableSelectedUsers(EnableSelectedUsersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the enable.
			BinderViewsHelper.enableUsers(
				EntityId.getLongsFromEntityIds(
					getSelectedEntityIds()));
		}
	}
	
	/**
	 * Handles InvokeColumnResizerEvent's received by this class.
	 * 
	 * Implements the InvokeColumnResizerEvent.Handler.onInvokeColumnResizer() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeColumnResizer(InvokeColumnResizerEvent event) {
		// Is the event targeted to this folder?
		Long evenBinderId = event.getBinderId();
		if (evenBinderId.equals(getFolderId())) {
			// Yes!  Invoke the column sizing dialog on the folder.
			// Have we instantiated a size columns dialog yet?
			if (null == m_sizeColumnsDlg) {
				// No!  Instantiate one now.
				SizeColumnsDlg.createAsync(new SizeColumnsDlgClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(final SizeColumnsDlg scDlg) {
						// ...and show it.
						m_sizeColumnsDlg = scDlg;
						ScheduledCommand doShow = new ScheduledCommand() {
							@Override
							public void execute() {
								showSizeColumnsDlgNow();
							}
						};
						Scheduler.get().scheduleDeferred(doShow);
					}
				});
			}
			
			else {
				// Yes, we've instantiated a size columns dialog
				// already!  Simply show it.
				showSizeColumnsDlgNow();
			}
		}
	}
	
	/**
	 * Handles InvokeDropBoxEvent's received by this class.
	 * 
	 * Implements the InvokeDropBoxEvent.Handler.onInvokeDropBox() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeDropBox(InvokeDropBoxEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the files drop box on the folder.
			BinderViewsHelper.invokeDropBox(
				getFolderInfo(),
				getEntryMenuPanel().getAddFilesMenuItem());
		}
	}
	
	/**
	 * Handles InvokeSignGuestbookEvent's received by this class.
	 * 
	 * Implements the InvokeSignGuestbookEvent.Handler.onInvokeSignGuestbook() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeSignGuestbook(InvokeSignGuestbookEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously invoke the guest book signing UI.
			Scheduler.ScheduledCommand doSignGuestbook = new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					signGuestbook();
				}
			};
			Scheduler.get().scheduleDeferred(doSignGuestbook);
		}
	}
	
	/**
	 * Handles LockSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the LockSelectedEntriesEvent.Handler.onLockSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLockSelectedEntries(LockSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the lock.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.lockEntries(
				getFolderType(),
				selectedEntityIds);
		}
	}
	
	/**
	 * Handles MarkReadSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the MarkReadSelectedEntriesEvent.Handler.onMarkReadSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkReadSelectedEntries(MarkReadSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the mark entries read.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.markEntriesRead(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MarkUnreadSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the MarkUnreadSelectedEntriesEvent.Handler.onMarkUnreadSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkUnreadSelectedEntries(MarkUnreadSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the mark entries read.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.markEntriesUnread(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MoveSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the MoveSelectedEntriesEvent.Handler.onMoveSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMoveSelectedEntries(MoveSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the move.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.moveEntries(
				getFolderType(),
				selectedEntityIds);
		}
	}
	
	/**
	 * Handles PurgeSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the PurgeSelectedEntriesEvent.Handler.onPurgeSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onPurgeSelectedEntries(PurgeSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Purge the selected entries and reset the view to
			// redisplay things with the entries purged.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			final boolean purgingBinders = EntityId.areBindersInEntityIds(selectedEntityIds);
			BinderViewsHelper.purgeFolderEntries(
					selectedEntityIds,
					new DeletePurgeEntriesCallback() {
				@Override
				public void operationCanceled() {
					if (purgingBinders) {
						GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
						FullUIReloadEvent.fireOne();
					}
					else {
						resetViewAsync();
					}
				}

				@Override
				public void operationComplete() {
					if (purgingBinders) {
						GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
						FullUIReloadEvent.fireOne();
					}
					else {
						resetViewAsync();
					}
				}
				
				@Override
				public void operationFailed() {
					// Nothing to do.  The purge call will have told the
					// user about the failure.
				}
			});
		}
	}
	
	/**
	 * Handles PurgeSelectedUsersEvent's received by this class.
	 * 
	 * Implements the PurgeSelectedUsersEvent.Handler.onPurgeSelectedUsers() method.
	 * 
	 * @param event
	 */
	@Override
	public void onPurgeSelectedUsers(PurgeSelectedUsersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the purge.
			BinderViewsHelper.purgeUsers(
				EntityId.getLongsFromEntityIds(
					getSelectedEntityIds()));
		}
	}
	
	/**
	 * Handles PurgeSelectedUserWorkspacesEvent's received by this class.
	 * 
	 * Implements the PurgeSelectedUserWorkspacesEvent.Handler.onPurgeSelectedUserWorkspaces() method.
	 * 
	 * @param event
	 */
	@Override
	public void onPurgeSelectedUserWorkspaces(PurgeSelectedUserWorkspacesEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the purge.
			BinderViewsHelper.purgeUserWorkspaces(
				EntityId.getLongsFromEntityIds(
					getSelectedEntityIds()));
		}
	}
	
	/*
	 * Asynchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void onResizeAsync(int delay) {
		if (0 == delay) {
			ScheduledCommand doResize = new ScheduledCommand() {
				@Override
				public void execute() {
					onResize();
				}
			};
			Scheduler.get().scheduleDeferred(doResize);
		}
		
		else {
			Timer timer = new Timer() {
				@Override
				public void run() {
					onResize();
				}
			};
			timer.schedule(delay);
		}
	}

	/*
	 * Asynchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void onResizeAsync() {
		onResizeAsync(INITIAL_RESIZE_DELAY);
	}
	
	/**
	 * Synchronously sets the size of the data table based on its
	 * position in the view.
	 * 
	 * Overrides the ViewBase.onResize() method.
	 */
	@Override
	public void onResize() {
		// Pass the resize on to the super class...
		super.onResize();

		// ...and do what we need to do locally.
		onResizeImpl(m_dataTable);
	}

	/*
	 * Performs the local resizing necessary.
	 */
	@SuppressWarnings("unused")
	private void onResizeImpl(CellTable<FolderRow> ct) {
		// Nothing to do.
	}
	
	private void onResizeImpl(DataGrid<FolderRow> dg) {
		FooterPanel fp = getFooterPanel();
		
		int viewHeight		= getOffsetHeight();							// Height of the view.
		int viewTop			= getAbsoluteTop();								// Absolute top of the view.		
		int dtTop			= (dg.getAbsoluteTop() - viewTop);				// Top of the data table relative to the top of the view.		
		int dtPagerHeight	= m_dataTablePager.getOffsetHeight();			// Height of the data table's pager.
		int fpHeight		= ((null == fp) ? 0 : fp.getOffsetHeight());	// Height of the view's footer panel.
		int totalBelow		= (dtPagerHeight + fpHeight);					// Total space on the page below the data table.

		// What's the optimum height for the data table so we don't get
		// a vertical scroll bar?
		int dataTableHeight = (((viewHeight - dtTop) - totalBelow) - getNoVScrollAdjustment());
		int minHeight       = getMinimumContentHeight();
		if (minHeight > dataTableHeight) {
			// Too small!  Use the minimum even though this will turn
			// on the vertical scroll bar.
			dataTableHeight = minHeight;
		}
		
		// Set the height of the data table.
		dg.setHeight(dataTableHeight + "px");
	}

	
	/**
	 * Handles QuickFilterEvent's received by this class.
	 * 
	 * Implements the QuickFilterEvent.Handler.onQuickFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onQuickFilter(QuickFilterEvent event) {
		// Is the event is targeted to the folder we're viewing?
		if (event.getFolderId().equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Track the current quick filter and force the data
			// table to refresh with it.
			m_quickFilter = event.getQuickFilter();
			resetDataTableAsync();
		}
	}

	/**
	 * Handles ShareSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the ShareSelectedEntriesEvent.Handler.onShareSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShareSelectedEntries(ShareSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Does the user have rights to share everything
			// they've selected?
			List<EntityId> seList = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(seList))) {
				seList = getSelectedEntityIds();
			}
			final List<EntityId>	selectedEntities = seList;
			final List<FolderRow>	invalidRows      = validateSelectedRows_Sharing();
			if (!(GwtClientHelper.hasItems(invalidRows))) {
				// Yes!  Invoke the share.
				shareSelectedEntitiesAsync(selectedEntities);
			}
			
			else {
				// No, they don't have rights to share everything!  Can
				// they share any of them?
				if (selectedEntities.size() == invalidRows.size()) {
					// No!  Tell them about the problem and bail.
					GwtClientHelper.deferredAlert(m_messages.vibeDataTable_Warning_ShareNoRights());
					return;
				}
				
				// Is the user sure they want to share the selections
				// they have rights to share?
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
									// Yes, they're sure!  Remove the
									// selection from the entries they
									// don't have rights to share and
									// perform the share on the rest.
									removeRowEntities(         selectedEntities, invalidRows);
									deselectRows(                                invalidRows);
									shareSelectedEntitiesAsync(selectedEntities             );
								}

								@Override
								public void rejected() {
									// No, they're not sure!
								}
							},
							m_messages.vibeDataTable_Confirm_CantShareSomeSelections());
					}
				});
			}
		}
	}
	
	/**
	 * Handles SubscribeSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the SubscribeSelectedEntriesEvent.Handler.onSubscribeSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSubscribeSelectedEntries(SubscribeSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the subscribe to.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.subscribeToEntries(
				getFolderType(),
				selectedEntityIds);
		}
	}
	
	/**
	 * Handles ToggleSharedViewEvent's received by this class.
	 * 
	 * Implements the ToggleSharedViewEvent.Handler.onToggleSharedView() method.
	 * 
	 * @param event
	 */
	@Override
	public void onToggleSharedView(ToggleSharedViewEvent event) {
		// Is the event targeted to this view's collection?
		CollectionType eventCollectionType = event.getCollectionType();
		if (eventCollectionType.equals(getFolderInfo().getCollectionType())) {
			// Yes!  Save the toggled shared view state for the
			// collection.
			final SaveSharedFilesStateCmd cmd = new SaveSharedFilesStateCmd(getFolderInfo().getCollectionType(), (!(isSharedFiles())));
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_SaveSharedFilesState());
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// ...and reload the view to redisplay things.
					FullUIReloadEvent.fireOne();
				}
			});
		}
	}
	
	/**
	 * Handles TrashPurgeAllEvent's received by this class.
	 * 
	 * Implements the TrashPurgeAllEvent.Handler.onTrashPurgeAll() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrashPurgeAll(TrashPurgeAllEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously purge all the entries from the
			// trash.
			Scheduler.ScheduledCommand doPurge = new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					trashPurgeAll();
				}
			};
			Scheduler.get().scheduleDeferred(doPurge);
		}
	}

	/**
	 * Handles TrashPurgeSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the TrashPurgeSelectedEntriesEvent.Handler.onTrashPurgeSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrashPurgeSelectedEntries(TrashPurgeSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously purge the selected entries from the
			// trash.
			Scheduler.ScheduledCommand doPurge = new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					trashPurgeSelectedEntries();
				}
			};
			Scheduler.get().scheduleDeferred(doPurge);
		}
	}

	/**
	 * Handles TrashRestoreAllEvent's received by this class.
	 * 
	 * Implements the TrashRestoreAllEvent.Handler.onTrashRestoreAll() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrashRestoreAll(TrashRestoreAllEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously restore all the entries in the
			// trash.
			Scheduler.ScheduledCommand doRestore = new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					trashRestoreAll();
				}
			};
			Scheduler.get().scheduleDeferred(doRestore);
		}
	}

	/**
	 * Handles TrashRestoreSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the TrashRestoreSelectedEntriesEvent.Handler.onTrashRestoreSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrashRestoreSelectedEntries(TrashRestoreSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously restore the selected entries in the
			// trash.
			Scheduler.ScheduledCommand doRestore = new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					trashRestoreSelectedEntries();
				}
			};
			Scheduler.get().scheduleDeferred(doRestore);
			
		}
	}
	
	/**
	 * Handles UnlockSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the UnlockSelectedEntriesEvent.Handler.onUnlockSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onUnlockSelectedEntries(UnlockSelectedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the unlock.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.unlockEntries(
				getFolderType(),
				selectedEntityIds);
		}
	}
	
	/**
	 * Handles ViewPinnedEntriesEvent's received by this class.
	 * 
	 * Implements the ViewPinnedEntriesEvent.Handler.onViewPinnedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewPinnedEntries(ViewPinnedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Save the toggled pinning state for the folder.
			final SaveFolderPinningStateCmd cmd = new SaveFolderPinningStateCmd(getFolderId(), (!(isPinning())));
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_SaveFolderPinningState());
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// ...and reload the view to redisplay things.
					FullUIReloadEvent.fireOne();
				}
			});
		}
	}
	
	/**
	 * Handles ViewSelectedEntryEvent's received by this class.
	 * 
	 * Implements the ViewSelectedEntryEvent.Handler.onViewSelectedEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewSelectedEntry(ViewSelectedEntryEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the view.
			List<EntityId> eids = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(eids))) {
				eids = getSelectedEntityIds();
			}
			if (GwtClientHelper.hasItems(eids)) {
				for (EntityId eid:  eids) {
					if (eid.isEntry()) {
						BinderViewsHelper.viewEntry(eid);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * Handles ViewWhoHasAccessEvent's received by this class.
	 * 
	 * Implements the ViewWhoHasAccessEvent.Handler.onViewWhoHasAccess() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewWhoHasAccess(ViewWhoHasAccessEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the view.
			List<EntityId> eids = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(eids))) {
				eids = getSelectedEntityIds();
			}
			if (GwtClientHelper.hasItems(eids)) {
				for (EntityId eid:  eids) {
					BinderViewsHelper.viewWhoHasAccess(eid);
					return;
				}
			}
		}
	}
	
	/**
	 * Completes populating the data table view specific content.
	 */
	final public void populateContent() {
		// Did we get any column width overrides?
		Map<String, String> widths = getFolderColumnWidths();
		if (GwtClientHelper.hasItems(widths)) {
			// Yes!  Scan them...
			for (String cName:  widths.keySet()) {
				try {
					// ...parsing the width string...
					String cwS = widths.get(cName);
					m_columnWidths.put(cName, ColumnWidth.parseWidthStyle(cwS));
				}
				catch (Exception e) {
					// On any exception, simply ignore the override.
				}
			}
		}
		
		// Asynchronously load the remaining data table specific
		// components of a folder view.
		loadFolderColumnsAsync();
	}

	/*
	 * Asynchronously populates the data table view given the available
	 * data.
	 */
	private void populateViewAsync() {
		Scheduler.ScheduledCommand doPopulate = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				populateView();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the data table view given the available
	 * data.
	 */
	private void populateView() {
		// If we're supposed to dump the display data we're building this on...
		VibeVerticalPanel vp;
		if (DUMP_DISPLAY_DATA) {
			// ...dump it.
			vp = new VibeVerticalPanel();
			vp.add(new HTML("<br/>- - - - - Start:  Folder Display Data - - - - -<br/><br/>"));
			vp.add(new InlineLabel("Sort by:  "         + getFolderSortBy())     );
			vp.add(new InlineLabel("Sort descending:  " + getFolderSortDescend()));
			vp.add(new InlineLabel("Page size:  "       + getFolderPageSize())   );
			vp.add(new HTML("<br/>"));
			for (FolderColumn fc:  m_folderColumnsList) {
				vp.add(new InlineLabel(fc.getColumnEleName() + "='" + fc.getColumnTitle() + "'"));
			}
			vp.add(new HTML("<br/>- - - - - End:  Folder Display Data - - - - -<br/>"));
			getFlowPanel().add(vp);
		}
		
		// Create a key provider that will provide a unique key for
		// each row.
		FolderRowKeyProvider keyProvider = new FolderRowKeyProvider();
		
		// Create the table.
		m_dataTable = new VibeDataGrid<FolderRow>(getFolderPageSize(), keyProvider);
		setDataTableWidthImpl(m_dataTable);
		m_dataTable.addStyleName("vibe-dataTableFolderDataTableBase");
		if (GwtClientHelper.hasString(m_folderStyles)) {
			m_dataTable.addStyleName(m_folderStyles);
		}
		m_dataTable.setRowStyles(new RowStyles<FolderRow>() {
			@Override
			public String getStyleNames(FolderRow row, int rowIndex) {
				StringBuffer reply = new StringBuffer(STYLE_ROW_BASE);
				reply.append(" ");
				reply.append(STYLE_ROW_BASE);
				reply.append("-");
				reply.append((0 == (rowIndex % 2)) ? STYLE_ROW_EVEN : STYLE_ROW_ODD);
				return reply.toString();
			}
		});
		
		// Set a message to display when the table is empty.
		m_dataTable.setEmptyTableWidget(getEmptyTableWidget());

		// Attach a sort handler to sort the list.
	    FolderRowSortHandler sortHandler = new FolderRowSortHandler();
	    m_dataTable.addColumnSortHandler(sortHandler);
		
		// Create a pager that lets the user page through the table.
	    m_dataTablePager = new FolderRowPager();
	    m_dataTablePager.setDisplay(m_dataTable);

	    // Add a selection model so the user can select cells.
	    FolderRowSelectionModel selectionModel = new FolderRowSelectionModel(keyProvider);
	    m_dataTable.setSelectionModel(selectionModel, DefaultSelectionEventManager.<FolderRow> createCheckboxManager());

	    // Initialize the table's columns.
	    initTableColumns(selectionModel);
	    
	    // Add the provider that supplies FolderRow's for the table.
		FolderRowAsyncProvider folderRowProvider = new FolderRowAsyncProvider(m_dataTable, keyProvider);
	    folderRowProvider.addDataDisplay(m_dataTable);

	    // Add the table and pager to the view.
		vp = new VibeVerticalPanel("100%", null);
	    vp.add(m_dataTable     );
	    vp.add(m_dataTablePager);
	    vp.setCellHorizontalAlignment(m_dataTablePager, HasHorizontalAlignment.ALIGN_CENTER);
		getFlowPanel().add(vp);
		
		// Finally, ensure the table gets sized correctly.
		onResizeAsync();
	}

	/*
	 * Asynchronously allows the view's that extend this do what ever
	 * they need to do once a collection of rows have been rendered.
	 */
	private void postProcessRowDataAsync(final List<FolderRow> folderRows) {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				postProcessRowData(folderRows, m_folderColumnsList);
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/**
	 * Allows the view's that extend this do what ever they need to
	 * do once a collection of rows have been rendered.
	 * 
	 * @param columnWidths
	 */
	protected void postProcessRowData(final List<FolderRow> folderRows, final List<FolderColumn> folderColumns) {
		// By default, there is no post processing required.
	}

	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Removes the EntityId's from a List<EntityId> corresponding to
	 * the FolderRow's in a List<FolderRow>.
	 */
	private void removeRowEntities(List<EntityId> entities, List<FolderRow> rows) {
		// Do we have list to process?
		if (GwtClientHelper.hasItems(entities) && GwtClientHelper.hasItems(rows)) {
			// Yes!  Scan the rows.
			for (FolderRow row:  rows) {
				// Scan the entities.
				for (EntityId entity:  entities) {
					// Is this the entity for the row?
					if (entity.equalsEntityId(row.getEntityId())) {
						// Yes!  Remove it from the List<EntityId> and
						// skip to the next row.
						entities.remove(entity);
						break;
					}
				}
			}
		}
	}

	/*
	 * Asynchronously resets the the content of the data table.
	 * 
	 * This is different from resetViewAsync() in that only the data
	 * table is reset and not the tool panels, ...
	 */
	private void resetDataTableAsync() {
		getFlowPanel().clear();
		populateViewAsync();
	}
	
	/*
	 * Asynchronously resets the view.
	 */
	private void resetViewAsync() {
		ScheduledCommand doResetView = new ScheduledCommand() {
			@Override
			public void execute() {
				resetView();
			}
		};
		Scheduler.get().scheduleDeferred(doResetView);
	}
	
	/*
	 * Sets the style on a column in the data table based on the
	 * column's name.
	 */
	private void setColumnStyles(Column<FolderRow, ?> column, String columnName, int colIndex) {
		if (!(FolderColumn.isColumnTitle(columnName))) {
			column.setCellStyleNames("gwtUI_nowrap");
		}
	    StringBuffer styles = new StringBuffer(STYLE_COL_BASE);
	    styles.append(" ");
	    styles.append(STYLE_COL_BASE);
	    styles.append("-");
	    styles.append(columnName.equals(FolderColumn.COLUMN_SELECT) ? STYLE_COL_SELECT : columnName);
	    m_dataTable.addColumnStyleName(colIndex, styles.toString());
	}

	/*
	 * Sets the width of a column in the data table based on the
	 * column's name.
	 */
	private void setColumnWidth(String cName, Column<FolderRow, ?> column, double pctTotal) {
		// Set the width for the column.
		ColumnWidth cw = m_columnWidths.get(cName);
		setColumnWidth(
			((null == cw) ? m_defaultColumnWidth : cw),
			column,
			pctTotal);
	}
	
	/*
	 * Sets the width of a column in the data table based on a
	 * ColumnWidth object.
	 */
	private void setColumnWidth(ColumnWidth cw, Column<FolderRow, ?> column, double pctTotal) {
		// Do we have a column width to set?
		if (null == cw) {
			// No!  Remove any setting that's already there.
			m_dataTable.clearColumnWidth(column);
		}
		
		else {
			// Yes, we have a column width to set!  Is it a percent we
			// need to adjust?
			double  currentWidth  = cw.getWidth();
			boolean adjustedWidth = (cw.isPCT() && (pctTotal != ((double) 100)));
			if (adjustedWidth) {
				// Yes!  Scale the width used so that the actual values sum
				// to 100%.
				cw.setWidth(
					ColumnWidth.scalePCTWidth(
						currentWidth,
						pctTotal));
			}
			
			// Put the width into affect.
			m_dataTable.setColumnWidth(column, ColumnWidth.getWidthStyle(cw));
			if (adjustedWidth) {
				cw.setWidth(currentWidth);
			}
		}
	}
	
	/*
	 * Sets the table width.
	 */
	@SuppressWarnings("unused")
	private void setDataTableWidthImpl(CellTable<FolderRow> ct) {
		ct.setWidth("100%", m_fixedLayout);
	}

	private void setDataTableWidthImpl(DataGrid<FolderRow> dg) {
		dg.setWidth("100%");
	}

	/*
	 * Asynchronously runs the share dialog on the selected entities.
	 */
	private void shareSelectedEntitiesAsync(final List<EntityId> selectedEntities) {
		Scheduler.ScheduledCommand doShare = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				shareSelectedEntitiesNow(selectedEntities);
			}
		};
		Scheduler.get().scheduleDeferred(doShare);
	}
	
	/*
	 * Synchronously runs the share dialog on the selected entities.
	 */
	private void shareSelectedEntitiesNow(List<EntityId> selectedEntities) {
		BinderViewsHelper.shareEntities(selectedEntities);
	}
	
	/*
	 * Synchronously shows the column sizing dialog.
	 */
	private void showSizeColumnsDlgNow() {
		SizeColumnsDlg.initAndShow(
			m_sizeColumnsDlg,
			getFolderInfo(),
			getColumnsForSizing(),
			m_columnWidths,
			m_defaultColumnWidth,
			m_defaultColumnWidths,
			this,
			m_dataTable.getAbsoluteTop(),
			m_fixedLayout);
	}

	/**
	 * Returns true if the PresenceColumn should show the profile entry
	 * dialog for presence when a user has no workspace and false if it
	 * shouldn't.
	 * 
	 * Stub provided as a convenience method.  Should only be
	 * overridden by PersonalWorkspacesView so that access is provided
	 * to modify a user profile when that user has no workspace.
	 * 
	 * @return
	 */
	public boolean showProfileEntryForPresenceWithNoWS() {
		// Return false since by default, we don't show the profile
		// entry dialog if a user doesn't have a workspace.
		return false;
	}
	
	/**
	 * Invokes the sign the guest book UI. 
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide guest book services.
	 */
	public void signGuestbook() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_GuestbookInternalErrorOverrideMissing());
	}
	
	/**
	 * Purges all the entries from the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashPurgeAll() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashPurgeAll()"));
	}
	
	/**
	 * Purges the selected entries from the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashPurgeSelectedEntries() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashPurgeSelectedEntries()"));
	}
	
	/**
	 * Restores all the entries in the trash. 
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashRestoreAll() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashRestoreAll()"));
	}
	
	/**
	 * Restores the selected entries in the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashRestoreSelectedEntries() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashRestoreSelectedEntries()"));
	}
	
	/*
	 * Returns a List<FolderRow> of the selected rows that the user
	 * can't share.
	 */
	private List<FolderRow> validateSelectedRows_Sharing() {
		// Are there any selected rows in the table?
		List<FolderRow> reply = new ArrayList<FolderRow>();
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (GwtClientHelper.hasItems(rows)) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Is it sharable?
					if (!(row.getCanShare())) {
						// No!  Track it as invalid.
						reply.add(row);
					}
				}
			}
		}
		
		// If we get here, reply refers to List<FolderRow> of the rows
		// the user doesn't have rights to share.  Return it.
		return reply;
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
}
