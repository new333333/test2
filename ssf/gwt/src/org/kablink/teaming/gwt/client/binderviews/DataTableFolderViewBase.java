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
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.datatable.AddFilesDlg;
import org.kablink.teaming.gwt.client.datatable.AddFilesDlg.AddFilesDlgClient;
import org.kablink.teaming.gwt.client.datatable.AssignmentCell.AssigneeType;
import org.kablink.teaming.gwt.client.datatable.AssignmentColumn;
import org.kablink.teaming.gwt.client.datatable.CustomColumn;
import org.kablink.teaming.gwt.client.datatable.DownloadColumn;
import org.kablink.teaming.gwt.client.datatable.EntryPinColumn;
import org.kablink.teaming.gwt.client.datatable.EntryTitleColumn;
import org.kablink.teaming.gwt.client.datatable.PresenceColumn;
import org.kablink.teaming.gwt.client.datatable.RatingColumn;
import org.kablink.teaming.gwt.client.datatable.SizeColumnsDlg;
import org.kablink.teaming.gwt.client.datatable.SizeColumnsDlg.SizeColumnsDlgClient;
import org.kablink.teaming.gwt.client.datatable.StringColumn;
import org.kablink.teaming.gwt.client.datatable.VibeDataGrid;
import org.kablink.teaming.gwt.client.datatable.VibeColumn;
import org.kablink.teaming.gwt.client.datatable.ViewColumn;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeColumnResizerEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.TrashPurgeAllEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreAllEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteFolderEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderRowsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PurgeFolderEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderSortCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntryId;
import org.kablink.teaming.gwt.client.util.EntryPinInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
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
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ProvidesKey;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Base object of 'data table' based folder views.
 * 
 * @author drfoster@novell.com
 */
public abstract class DataTableFolderViewBase extends FolderViewBase
	implements
	// Event handlers implemented by this class.
		ChangeEntryTypeSelectedEntriesEvent.Handler,
		ContributorIdsRequestEvent.Handler,
		CopySelectedEntriesEvent.Handler,
		DeleteSelectedEntriesEvent.Handler,
		InvokeColumnResizerEvent.Handler,
		InvokeDropBoxEvent.Handler,
		LockSelectedEntriesEvent.Handler,
		MarkReadSelectedEntriesEvent.Handler,
		MoveSelectedEntriesEvent.Handler,
		PurgeSelectedEntriesEvent.Handler,
		ShareSelectedEntriesEvent.Handler,
		SubscribeSelectedEntriesEvent.Handler,
		TrashPurgeAllEvent.Handler,
		TrashPurgeSelectedEntriesEvent.Handler,
		TrashRestoreAllEvent.Handler,
		TrashRestoreSelectedEntriesEvent.Handler,
		UnlockSelectedEntriesEvent.Handler
{
	private AddFilesDlg					m_addFilesDlg;				//
	private boolean						m_fixedLayout;				//
	private ColumnWidth					m_defaultColumnWidth;		//
	private FolderRowPager 				m_dataTablePager;			// Pager widgets at the bottom of the data table.
	private List<FolderColumn>			m_folderColumnsList;		// The List<FolderColumn>' of the columns to be displayed.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private List<Long>					m_contributorIds;			//
	private Map<String, ColumnWidth>	m_defaultColumnWidths;		// Map of column names -> Default ColumnWidth objects.
	private Map<String, ColumnWidth>	m_columnWidths;				// Map of column names -> Current ColumnWidth objects.
	private SizeColumnsDlg				m_sizeColumnsDlg;			//
	private String						m_folderStyles;				// Specific style(s) for the for the folders that extend this.
	private VibeDataGrid<FolderRow>		m_dataTable;				// The actual data table holding the view's information.
	
	protected GwtTeamingDataTableImageBundle m_images;	//

	// The following controls whether the display data read from the
	// server is dumped as part of the content of the view.
	private final static boolean DUMP_DISPLAY_DATA	= false;
	
	// The following are used to construct the style names applied
	// to the columns and rows of the data table.
	private final static String STYLE_COL_BASE		= "vibe-dataTableFolderColumn";
	private final static String STYLE_COL_SELECT	= "select";
	private final static String STYLE_ROW_BASE		= "vibe-dataTableFolderRow";
	private final static String STYLE_ROW_EVEN		= "even";
	private final static String STYLE_ROW_ODD		= "odd";
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTRIES,
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.COPY_SELECTED_ENTRIES,
		TeamingEvents.DELETE_SELECTED_ENTRIES,
		TeamingEvents.INVOKE_COLUMN_RESIZER,
		TeamingEvents.INVOKE_DROPBOX,
		TeamingEvents.LOCK_SELECTED_ENTRIES,
		TeamingEvents.MARK_READ_SELECTED_ENTRIES,
		TeamingEvents.MOVE_SELECTED_ENTRIES,
		TeamingEvents.PURGE_SELECTED_ENTRIES,
		TeamingEvents.SHARE_SELECTED_ENTRIES,
		TeamingEvents.SUBSCRIBE_SELECTED_ENTRIES,
		TeamingEvents.TRASH_PURGE_ALL,
		TeamingEvents.TRASH_PURGE_SELECTED_ENTRIES,
		TeamingEvents.TRASH_RESTORE_ALL,
		TeamingEvents.TRASH_RESTORE_SELECTED_ENTRIES,
		TeamingEvents.UNLOCK_SELECTED_ENTRIES,
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
			final Long folderId = getFolderInfo().getBinderIdAsLong();
			final Range range = display.getVisibleRange();
			final int rowsRequested = range.getLength();
			GwtClientHelper.executeCommand(
					new GetFolderRowsCmd(
						folderId,
						getFolderType(),
						m_folderColumnsList,
						range.getStart(),
						rowsRequested),
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
						emp.setEntriesAvailable(false);
					}
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Did we read more rows than we asked for?
					FolderRowsRpcResponseData responseData = ((FolderRowsRpcResponseData) response.getResponseData());
					m_contributorIds = responseData.getContributorIds();
					List<FolderRow> folderRows = responseData.getFolderRows();
					int rowsRead = folderRows.size();
					if (rowsRead > rowsRequested) {
						// Yes!  This can happen with pinned entries. 
						// When it does, we end up not displaying all
						// the entries.  As of yet, I don't have a fix.
//!						...this needs to be implemented...
					}
					
					// Apply the rows we read.
					m_vdt.setRowData( responseData.getStartOffset(), folderRows);
					m_vdt.setRowCount(responseData.getTotalRows()              );
					
					// If we have an entry menu...
					EntryMenuPanel emp = getEntryMenuPanel();
					if (null != emp) {
						// ...tell it to update the state of its items that
						// ...require entries be available.
						emp.setEntriesAvailable(0 < rowsRead);
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
			// The key to a row is its entryId.
			return fr.getEntryId();
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
				final SaveFolderSortCmd cmd = new SaveFolderSortCmd(getFolderInfo().getBinderIdAsLong(), fc.getColumnSortKey(), (!folderSortDescend));
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
	protected AbstractCellTable<FolderRow> getDataTable()          {return m_dataTable;                                                     }
	private   boolean                      getFolderSortDescend()  {return getFolderDisplayData().getFolderSortDescend();                   }
	private   int                          getFolderPageSize()     {return getFolderDisplayData().getFolderPageSize();                      }
	private   FolderType                   getFolderType()         {return (isTrash() ? FolderType.TRASH : getFolderInfo().getFolderType());}
	private   Map<String, String>          getFolderColumnWidths() {return getFolderDisplayData().getFolderColumnWidths();                  }
	private   String                       getFolderSortBy()       {return getFolderDisplayData().getFolderSortBy();                        }
	
	/*
	 * Set'er methods.
	 */
	private void setFolderSortBy(     String  folderSortBy)      {getFolderDisplayData().setFolderSortBy(folderSortBy);          }
	private void setFolderSortDescend(boolean folderSortDescend) {getFolderDisplayData().setFolderSortDescend(folderSortDescend);}

	/*
	 * Adds a column to manage pinning entries.
	 */
	private void addPinColumn(final FolderRowSelectionModel selectionModel, int colIndex) {
		// Define the pin header...
		VibeFlowPanel fp = new VibeFlowPanel();
		Image i = new Image(m_images.grayPin());
		i.setTitle(m_messages.vibeDataTable_Alt_PinHeader());
		fp.add(i);
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		final SafeHtmlHeader pinHeader = new SafeHtmlHeader(rendered);

		// ...define a column for it...
		EntryPinColumn<FolderRow> column = new EntryPinColumn<FolderRow>() {
			@Override
			public EntryPinInfo getValue(FolderRow fr) {
				return
					new EntryPinInfo(
						fr.getPinned(),
						getFolderInfo().getBinderIdAsLong(),
						fr.getEntryId().getEntryId());
			}
		};
		
		// ...and connect it all together.
	    m_dataTable.addColumn(column, pinHeader);
	    setColumnStyles(column, ColumnWidth.COLUMN_PIN, colIndex);
	    setColumnWidth(         ColumnWidth.COLUMN_PIN, column  );
	}
	
	/*
	 * Adds a select column to the data table including a select all
	 * checkbox in the header.
	 * 
	 * @author rvasudevan@novell.com
	 */
	private void addSelectColumn(final FolderRowSelectionModel selectionModel, int colIndex) {
		// Define the select all checkbox in the header...
		CheckboxCell cbCell = new CheckboxCell();
		final SelectAllHeader saHeader = new SelectAllHeader(cbCell);
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
					emp.setEntriesSelected(checked);
				}
			}
		});

		// ...define a column for it...
		final Column<FolderRow, Boolean> column = new Column<FolderRow, Boolean>(cbCell) {
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
					emp.setEntriesSelected(checked);
				}
			};
		});

		// ...and connect it all together.
	    m_dataTable.addColumn(column, saHeader);
	    setColumnStyles(column, ColumnWidth.COLUMN_SELECT, colIndex);
	    setColumnWidth(         ColumnWidth.COLUMN_SELECT, column  );
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
	 * Returns true if entries can be pinned in the current folder and
	 * false otherwise.
	 */
	private boolean canPinEntries() {
		return (FolderType.DISCUSSION == getFolderType());
	}
	
	/*
	 * Returns a List<FolderColumn> we can use to run the column sizing
	 * dialog.
	 */
	private List<FolderColumn> getColumnsForSizing() {
		// Allocate a List<FolderColumn> we can return...
		List<FolderColumn> reply = new ArrayList<FolderColumn>();

		// ...add a column for the checkbox selector...
		FolderColumn fc = new FolderColumn();
		fc.setColumnName(ColumnWidth.COLUMN_SELECT);
		fc.setColumnTitle(m_messages.vibeDataTable_Select());
		reply.add(fc);
		
		// ...if this folder supports entry pinning...
		if (canPinEntries()) {
			// ...add a column for the pin selector...
			fc = new FolderColumn();
			fc.setColumnName(ColumnWidth.COLUMN_PIN);
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

	/*
	 * Various column type detectors.
	 */
	private static AssigneeType getAssigneeType(String columnName) {
		AssigneeType reply;
		if      (isColumnAssignee(     columnName)) reply = AssigneeType.INDIVIDUAL;
		else if (isColumnAssigneeGroup(columnName)) reply = AssigneeType.GROUP;
		else if (isColumnAssigneeTeam( columnName)) reply = AssigneeType.TEAM;
		else                                        reply = AssigneeType.UNKNOWN;
		return reply;
	}
	private static boolean isColumnAssigneeInfo(String columnName) {
		return
			(isColumnAssignee(     columnName) ||
			 isColumnAssigneeGroup(columnName) ||
			 isColumnAssigneeTeam( columnName));
	}
	private static boolean isColumnAssignee(String columnName) {
		return
			(columnName.equals("attendee")           ||	// Calendar entry attendee.
			 columnName.equals("assignment")         ||	// Task assignee.
			 columnName.equals("responsible"));			// Milestone responsible.
	}
	private static boolean isColumnAssigneeGroup(String columnName) {
		return
			(columnName.equals("attendee_groups")    ||	// Calendar entry attendee.
			 columnName.equals("assignment_groups")  ||	// Task assignee.
			 columnName.equals("responsible_groups"));	// Milestone responsible.
	}
	private static boolean isColumnAssigneeTeam(String columnName) {
		return
			(columnName.equals("attendee_teams")     ||	// Calendar entry attendee.
			 columnName.equals("assignment_teams")   ||	// Task assignee.
			 columnName.equals("responsible_teams"));	// Milestone responsible.
	}
	private static boolean isColumnCustom(      FolderColumn column)     {return column.isCustomColumn();                       }
	private static boolean isColumnDownload(    String       columnName) {return columnName.equals(ColumnWidth.COLUMN_DOWNLOAD);}
	private static boolean isColumnRating(      String       columnName) {return columnName.equals(ColumnWidth.COLUMN_RATING);  }
	private static boolean isColumnPresence(    String       columnName) {return columnName.equals(ColumnWidth.COLUMN_AUTHOR);  }
	private static boolean isColumnTitle(       String       columnName) {return columnName.equals(ColumnWidth.COLUMN_TITLE);   }
	private static boolean isColumnView(        String       columnName) {return columnName.equals(ColumnWidth.COLUMN_HTML);    }

	/*
	 * Given a List<Long> of entry IDs from the current folder, returns
	 * a corresponding List<EntryId> of them.
	 */
	private List<EntryId> getEntryIdListFromEntryIdLongs(List<Long> entryIds) {
		List<EntryId> reply = new ArrayList<EntryId>();
		Long folderId = getFolderId();
		for (Long entryId:  entryIds) {
			reply.add(new EntryId(folderId, entryId));
		}
		return reply;
	}
	
	/**
	 * Returns a List<Long> of the IDs of the selected rows from the
	 * table.
	 * 
	 * @return
	 */
	public List<Long> getSelectedEntryIds() {
		// Are there any visible rows in the table?
		List<Long>      reply = new ArrayList<Long>();
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (null != rows) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Add its entry ID to the List<Long>.
					reply.add(row.getEntryId().getEntryId());
				}
			}
		}
		
		// If we get here, reply refers to List<Long> of the entry IDs
		// of the selected rows from the data table.  Return it.
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
		m_columnWidths.put(ColumnWidth.COLUMN_AUTHOR,   new ColumnWidth(24));	// Unless otherwise specified...
		m_columnWidths.put(ColumnWidth.COLUMN_COMMENTS, new ColumnWidth( 8));	// ...the widths default to...
		m_columnWidths.put(ColumnWidth.COLUMN_DATE,     new ColumnWidth(20));	// ...be a percentage value.
		m_columnWidths.put(ColumnWidth.COLUMN_DOWNLOAD, new ColumnWidth( 8));
		m_columnWidths.put(ColumnWidth.COLUMN_HTML,     new ColumnWidth(10));
		m_columnWidths.put(ColumnWidth.COLUMN_LOCATION, new ColumnWidth(30));
		m_columnWidths.put(ColumnWidth.COLUMN_NUMBER,   new ColumnWidth( 5));
		m_columnWidths.put(ColumnWidth.COLUMN_RATING,   new ColumnWidth(10));
		m_columnWidths.put(ColumnWidth.COLUMN_SIZE,     new ColumnWidth( 8));
		m_columnWidths.put(ColumnWidth.COLUMN_STATE,    new ColumnWidth( 8));
		m_columnWidths.put(ColumnWidth.COLUMN_RATING,   new ColumnWidth(10));
		m_columnWidths.put(ColumnWidth.COLUMN_TITLE,    new ColumnWidth(28));

		// ...and then add the widths for everything else.
		m_columnWidths.put(ColumnWidth.COLUMN_SELECT,   new ColumnWidth(40, Unit.PX));
		m_columnWidths.put(ColumnWidth.COLUMN_PIN,      new ColumnWidth(40, Unit.PX));

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
		m_columnWidths.put(ColumnWidth.COLUMN_TITLE, new ColumnWidth(100, Unit.PCT));
		
		// Finally, let the view's that extend this do what ever they
		// need to these widths for their own purposes.
		adjustFloatColumnWidths(m_columnWidths);
	}
	
	/*
	 * Initializes the columns in the data table.
	 */
	private void initTableColumns(final FolderRowSelectionModel selectionModel, FolderRowSortHandler sortHandler) {
		// Clear the data table's column sort list.
		ColumnSortList csl = m_dataTable.getColumnSortList();
		csl.clear();
		
		// Add a column for a checkbox selector.
		int colIndex = 0;
		addSelectColumn(selectionModel, colIndex++);

		// If this folder supports entry pinning...
		if (canPinEntries()) {
			// ...add a column to manage pinning the entry.
			addPinColumn(selectionModel, colIndex++);
		}

	    // Scan the columns defined in this folder.
		for (final FolderColumn fc:  m_folderColumnsList) {
			// We need to define a VibeColumn<FolderRow, ?> of some
			// sort for each one.  Is this a column that should show
			// a download link for? 
			VibeColumn<FolderRow, ?> column;
			String cName = fc.getColumnEleName();
			if (isColumnDownload(cName)) {
				// Yes!  Create a DownloadColumn for it.
				column = new DownloadColumn<FolderRow>(fc) {
					@Override
					public Long getValue(FolderRow fr) {
						String value = fr.getColumnValueAsString(fc);
						Long reply;
						if (GwtClientHelper.hasString(value))
						     reply = fr.getEntryId().getEntryId();
						else reply = null;
						return reply;
					}
				};
			}
			
			// No, this column doesn't show a download link!  Does it
			// show presence?
			else if (isColumnPresence(cName)) {
				// Yes!  Create a PresenceColumn for it.
				column = new PresenceColumn<FolderRow>(fc) {
					@Override
					public PrincipalInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsPrincipalInfo(fc);
					}
				};
			}

			// No, this column doesn't show presence either!  Does it
			// show a rating?
			else if (isColumnRating(cName)) {
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
			else if (isColumnTitle(cName)) {
				// Yes!  Create a EntryTitleColumn for it.
				column = new EntryTitleColumn<FolderRow>(fc) {
					@Override
					public EntryTitleInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsEntryTitle(fc);
					}
				};
			}
			
			// No, this column doesn't show an entry title either!
			// Does it show a view link?
			else if (isColumnView(cName)) {
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
			else if (isColumnCustom(fc)) {
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
			
			// No, this column doesn't show a custom column either!  Is
			// it an assignment of some sort?
			else if (isColumnAssigneeInfo(cName)){
				// Yes!  Create an AssignmentColumn for it.
				column = new AssignmentColumn<FolderRow>(fc, getAssigneeType(cName)) {
					@Override
					public List<AssignmentInfo> getValue(FolderRow fr) {
						return fr.getColumnValueAsAssignmentInfos(fc);
					}
				};
			}
			
			else {
				// No, this column doesn't show a view link either!
				// Define a StringColumn for it.
				column = new StringColumn<FolderRow>(fc) {
					@Override
					public String getValue(FolderRow fr) {
						return fr.getColumnValueAsString(fc);
					}
				};
			}

			// Complete the initialization of the column.
			fc.setDisplayIndex(   colIndex                   );
			column.setSortable(   true                       );
			m_dataTable.addColumn(column, fc.getColumnTitle());
		    setColumnStyles(      column, cName, colIndex++  );
		    setColumnWidth(               cName, column      );

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
		final Long folderId = getFolderInfo().getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetFolderColumnsCmd(folderId, getFolderType()),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderColumns(),
					folderId);
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
		if (eventBinderId.equals(getFolderInfo().getBinderIdAsLong())) {
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Asynchronously delete the selected entries.
			ScheduledCommand doDelete = new ScheduledCommand() {
				@Override
				public void execute() {
					onDeleteSelectedEntriesNow();
				}
			};
			Scheduler.get().scheduleDeferred(doDelete);
		}
	}
	
	/*
	 * Synchronously deletes the selected entries.
	 */
	private void onDeleteSelectedEntriesNow() {
		// Are there any entries to selected to delete?  
		List<Long> selectedIds = getSelectedEntryIds();
		if (!(selectedIds.isEmpty())) {
			// Yes!  Is the user sure they want to delete them?
			if (Window.confirm(m_messages.vibeDataTable_Confirm_Delete())) {
				// Yes!  Delete them from the folder.
				DeleteFolderEntriesCmd cmd = new DeleteFolderEntriesCmd(getFolderInfo().getBinderIdAsLong(), selectedIds);
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_DeleteFolderEntries());
					}
	
					@Override
					public void onSuccess(VibeRpcResponse result) {
						// Reset the view to redisplay things with the entries
						// deleted.
						resetViewAsync();
					}
				});
			}
		}
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the change.
			BinderViewsHelper.changeEntryTypes(
				getFolderType(),
				getEntryIdListFromEntryIdLongs(getSelectedEntryIds()));
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the copy.
			BinderViewsHelper.copyEntries(
				getFolderType(),
				getEntryIdListFromEntryIdLongs(getSelectedEntryIds()));
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
		if (evenBinderId.equals(getFolderInfo().getBinderIdAsLong())) {
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the add file dialog on the folder.
			// Have we instantiated an add files dialog yet?
			if (null == m_addFilesDlg) {
				// No!  Instantiate one now.
				AddFilesDlg.createAsync(new AddFilesDlgClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(final AddFilesDlg afDlg) {
						// ...and show it.
						m_addFilesDlg = afDlg;
						ScheduledCommand doShow = new ScheduledCommand() {
							@Override
							public void execute() {
								showAddFilesDlgNow();
							}
						};
						Scheduler.get().scheduleDeferred(doShow);
					}
				});
			}
			
			else {
				// Yes, we've instantiated an add files dialog already!
				// Simply show it.
				showAddFilesDlgNow();
			}
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the lock.
			BinderViewsHelper.lockEntries(
				getFolderType(),
				getEntryIdListFromEntryIdLongs(getSelectedEntryIds()));
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the mark entries read.
			BinderViewsHelper.markEntriesRead(getSelectedEntryIds());
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the move.
			BinderViewsHelper.moveEntries(
				getFolderType(),
				getEntryIdListFromEntryIdLongs(getSelectedEntryIds()));
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Asynchronously purge the selected entries.
			ScheduledCommand doPurge = new ScheduledCommand() {
				@Override
				public void execute() {
					onPurgeSelectedEntriesNow();
				}
			};
			Scheduler.get().scheduleDeferred(doPurge);
		}
	}
	
	/*
	 * Synchronously purges the selected entries.
	 */
	private void onPurgeSelectedEntriesNow() {
		// Are there any entries selected to purge?
		List<Long> selectedIds = getSelectedEntryIds();
		if (!(selectedIds.isEmpty())) {
			// Yes!  Is the user sure they want to purge them?
			if (Window.confirm(m_messages.vibeDataTable_Confirm_Purge())) {
				// Yes!  Purge them from the folder.
				PurgeFolderEntriesCmd cmd = new PurgeFolderEntriesCmd(getFolderInfo().getBinderIdAsLong(), selectedIds);
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_PurgeFolderEntries());
					}
	
					@Override
					public void onSuccess(VibeRpcResponse result) {
						// Reset the view to redisplay things with the entries
						// purged.
						resetViewAsync();
					}
				});
			}
		}
	}
	
	/*
	 * Asynchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void onResizeAsync() {
		ScheduledCommand doResize = new ScheduledCommand() {
			@Override
			public void execute() {
				onResize();
			}
		};
		Scheduler.get().scheduleDeferred(doResize);
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
		int dataTableHeight = (((viewHeight - dtTop) - totalBelow) - NO_VSCROLL_ADJUST);
		if (MINIMUM_CONTENT_HEIGHT > dataTableHeight) {
			// Too small!  Use the minimum even though this will turn
			// on the vertical scroll bar.
			dataTableHeight = MINIMUM_CONTENT_HEIGHT;
		}
		
		// Set the height of the data table.
		dg.setHeight(dataTableHeight + "px");
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the share.
			BinderViewsHelper.shareEntries(
				getFolderType(),
				getEntryIdListFromEntryIdLongs(getSelectedEntryIds()));
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the subscribe to.
			BinderViewsHelper.subscribeToEntries(
				getFolderType(),
				getEntryIdListFromEntryIdLongs(getSelectedEntryIds()));
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
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
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the unlock.
			BinderViewsHelper.unlockEntries(
				getFolderType(),
				getEntryIdListFromEntryIdLongs(getSelectedEntryIds()));
		}
	}
	
	/**
	 * Completes populating the data table view specific content.
	 */
	final public void populateContent() {
		// Did we get any column width overrides?
		Map<String, String> widths = getFolderColumnWidths();
		if ((null != widths) && (!(widths.isEmpty()))) {
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
		m_dataTable.setEmptyTableWidget(new Label(m_messages.discussionFolder_Empty()));

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
	    initTableColumns(selectionModel, sortHandler);
	    
	    // Add the provider that supplies FolderRow's for the table.
		FolderRowAsyncProvider folderRowProvider = new FolderRowAsyncProvider(m_dataTable, keyProvider);
	    folderRowProvider.addDataDisplay(m_dataTable);

	    // Add the table and pager to the view.
		vp = new VibeVerticalPanel();
	    vp.add(m_dataTable);
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
		// By default, there post processing required.
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
		if (!(isColumnTitle(columnName))) {
			column.setCellStyleNames("gwtUI_nowrap");
		}
	    StringBuffer styles = new StringBuffer(STYLE_COL_BASE);
	    styles.append(" ");
	    styles.append(STYLE_COL_BASE);
	    styles.append("-");
	    styles.append(columnName.equals(ColumnWidth.COLUMN_SELECT) ? STYLE_COL_SELECT : columnName);
	    m_dataTable.addColumnStyleName(colIndex, styles.toString());
	}
	
	/*
	 * Sets the width of a column in the data table based on the
	 * column's name.
	 */
	private void setColumnWidth(String cName, Column<FolderRow, ?> column) {
		// Determine the width for the column...
		ColumnWidth cw = m_columnWidths.get(cName);
		if (null == cw) {
			cw = m_defaultColumnWidth;
		}
		
		// ...and put it into affect.
		m_dataTable.setColumnWidth(column, ColumnWidth.getWidthStyle(cw));
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
	 * Synchronously shows the add files dialog.
	 */
	private void showAddFilesDlgNow() {
		AddFilesDlg.initAndShow(
			m_addFilesDlg,
			getFolderInfo(),
			getEntryMenuPanel().getAddFilesMenuItem());
	}
	
	/*
	 * Synchronously shows the clipboard dialog.
	 */
	private void showSizeColumnsDlgNow() {
		SizeColumnsDlg.initAndShow(
			m_sizeColumnsDlg,
			getFolderInfo(),
			getColumnsForSizing(),
			m_columnWidths,
			m_defaultColumnWidth,
			m_defaultColumnWidths,
			m_dataTable,
			m_fixedLayout);
	}
	
	/**
	 * Purges all the entries from the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashPurgeAll() {
		Window.alert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashPurgeAll()"));
	}
	
	/**
	 * Purges the selected entries from the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashPurgeSelectedEntries() {
		Window.alert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashPurgeSelectedEntries()"));
	}
	
	/**
	 * Restores all the entries in the trash. 
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashRestoreAll() {
		Window.alert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashRestoreAll()"));
	}
	
	/**
	 * Restores the selected entries in the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashRestoreSelectedEntries() {
		Window.alert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashRestoreSelectedEntries()"));
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
}
