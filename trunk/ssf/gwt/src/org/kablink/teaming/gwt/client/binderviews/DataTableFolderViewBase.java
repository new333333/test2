/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.binderviews.BreadCrumbPanel;
import org.kablink.teaming.gwt.client.binderviews.DescriptionPanel;
import org.kablink.teaming.gwt.client.binderviews.EntryMenuPanel;
import org.kablink.teaming.gwt.client.binderviews.FilterPanel;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.datatable.CustomColumn;
import org.kablink.teaming.gwt.client.datatable.DownloadColumn;
import org.kablink.teaming.gwt.client.datatable.EntryPinColumn;
import org.kablink.teaming.gwt.client.datatable.EntryTitleColumn;
import org.kablink.teaming.gwt.client.datatable.PresenceColumn;
import org.kablink.teaming.gwt.client.datatable.RatingColumn;
import org.kablink.teaming.gwt.client.datatable.StringColumn;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.datatable.VibeColumn;
import org.kablink.teaming.gwt.client.datatable.VibeDataGrid;
import org.kablink.teaming.gwt.client.datatable.ViewColumn;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeColumnResizerEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteFolderEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderRowsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PurgeFolderEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderSortCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
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
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
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
public abstract class DataTableFolderViewBase extends ViewBase
	implements ToolPanelReady,
	// Event handlers implemented by this class.
		ContributorIdsRequestEvent.Handler,
		DeleteSelectedEntriesEvent.Handler,
		InvokeColumnResizerEvent.Handler,
		InvokeDropBoxEvent.Handler,
		PurgeSelectedEntriesEvent.Handler
{
	private final BinderInfo				m_folderInfo;				// A BinderInfo object that describes the folder being viewed.
	private boolean							m_folderSortDescend;		// true -> The folder is sorted in descending order.  false -> It's sorted in ascending order.
	private boolean							m_viewReady;				// Set true once the view and all its components are ready.
	private int								m_folderPageSize;			// Page size as per the user's personal preferences.
	private int								m_readyComponents;			// Tracks items as they become ready.
	private EntryMenuPanel					m_entryMenuPanel;			// Panel that holds the entry menu.
	private FolderRowPager 					m_dataTablePager;			// Pager widgets at the bottom of the data table.
	private FooterPanel						m_footerPanel;				// Panel that holds the links, ... displayed at the bottom of the view.
	private List<FolderColumn>				m_folderColumnsList;		// The List<FolderColumn>' of the columns to be displayed.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private List<Long>						m_contributorIds;			//
	private List<ToolPanelBase>				m_toolPanels;				// List<ToolPanelBase>'s of the various tools panels that appear above the table.
	private Map<String, ColumnWidth>		m_columnWidths;				// Map of column names -> ColumWidth objects.
	private String							m_folderSortBy;				// Which column the view is sorted on.
	private VibeCellTable<FolderRow>		m_dataTable;				// The actual data table holding the view's information.
	private VibeFlowPanel					m_mainPanel;				// The main panel holding the content of the view.
	private VibeFlowPanel					m_flowPanel;				// The flow panel used to hold the view specific content of the view.
	private VibeVerticalPanel				m_verticalPanel;			// The vertical panel that holds all components of the view, both common and view specific.
	
	protected GwtTeamingDataTableImageBundle m_images;	//

	// The following controls whether the display data read from the
	// server is dumped as part of the content of the view.
	private final static boolean DUMP_DISPLAY_DATA	= false;
	
	private final static int MINIMUM_HEIGHT		= 150;	// The minimum height (in pixels) of a the data table widget.
	private final static int NO_VSCROLL_ADJUST	=  28;	// Height adjustment required so there's no vertical scroll bar by default.
	
	// The following define the indexes into a VibeVerticalPanel of the
	// various panels that makeup a data table based folder view.
	private final static int BREADCRUMB_PANEL_INDEX		= 0;
	private final static int ACCESSORY_PANEL_INDEX		= 1;
	private final static int DESCRIPTION_PANEL_INDEX	= 2;
	private final static int FILTER_PANEL_INDEX			= 3;
	private final static int ENTRY_MENU_PANEL_INDEX		= 4;
	@SuppressWarnings("unused")
	private final static int DATA_TABLE_PANEL_INDEX		= 5;
	private final static int FOOTER_PANEL_INDEX			= 6;

	// The following are the various predefined names used for columns
	// in the data table.
	private final static String COLUMN_AUTHOR	= "author";
	private final static String COLUMN_COMMENTS	= "comments";
	private final static String COLUMN_DATE		= "date";
	private final static String COLUMN_DOWNLOAD	= "download";
	private final static String COLUMN_HTML		= "html";
	private final static String COLUMN_LOCATION	= "location";
	private final static String COLUMN_NUMBER	= "number";
	private final static String COLUMN_RATING	= "rating";
	private final static String COLUMN_SIZE		= "size";
	private final static String COLUMN_STATE	= "state";
	private final static String COLUMN_TITLE	= "title";
	
	// The following are the various internal names used for columns in
	// the data table.
	private final static String COLUMN_SELECT	= "--select--";
	private final static String COLUMN_PIN		= "--pin--";
	private final static String COLUMN_OTHER	= "--other--";

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
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.DELETE_SELECTED_ENTRIES,
		TeamingEvents.INVOKE_COLUMN_RESIZER,
		TeamingEvents.INVOKE_DROPBOX,
		TeamingEvents.PURGE_SELECTED_ENTRIES,
	};
	
	/*
	 * Inner class used to specify column widths.
	 */
	private static class ColumnWidth {
		private int		m_width;	//
		private Unit	m_units;	//

		/**
		 * Constructor method.
		 * 
		 * @param width
		 * @param units
		 */
		public ColumnWidth(int width, Unit units) {
			super();
			
			m_width = width;
			m_units = units;
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param width
		 */
		public ColumnWidth(int width) {
			// Always use the initial form of the method.
			this(width, Unit.PCT);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public int  getWidth() {return m_width;}
		public Unit getUnits() {return m_units;}
	}

	/*
	 * Inner class used to provide list of FolderRow's.
	 */
	private class FolderRowAsyncProvider extends AsyncDataProvider<FolderRow> {
		private VibeCellTable<FolderRow> m_vdt;	// The data table we're providing data for.
		
		/**
		 * Constructor method.
		 *
		 * @param vdt
		 * @param keyProvider
		 */
		public FolderRowAsyncProvider(VibeCellTable<FolderRow> vdt, ProvidesKey<FolderRow> keyProvider) {
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
		 * Overrides AsyncDataProvider.onRowChanged()
		 */
		@Override
		protected void onRangeChanged(HasData<FolderRow> display) {
			final Long folderId = m_folderInfo.getBinderIdAsLong();
			final Range range = display.getVisibleRange();
			final int rowsRequested = range.getLength();
			GwtClientHelper.executeCommand(
					new GetFolderRowsCmd(folderId, m_folderInfo.getFolderType(), m_folderColumnsList, range.getStart(), rowsRequested),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetFolderRows(),
						folderId);
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
		 * Implements ProvidesKey.getKey()
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
	private class FolderRowSelectionModel extends MultiSelectionModel<FolderRow> {
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
		 * Implements ColumnSortEvent.Handler.onColumnSort()
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
				final boolean folderSortByChanged = (!(folderSortBy.equalsIgnoreCase(m_folderSortBy)));
				final boolean folderSortDescend   = (folderSortByChanged ? m_folderSortDescend : (!m_folderSortDescend));
				final SaveFolderSortCmd cmd = new SaveFolderSortCmd(m_folderInfo.getBinderIdAsLong(), fc.getColumnSortKey(), (!folderSortDescend));
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
		 * Overrides Header.getValue()
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
		 * Overrides Header.onBroserEvent()
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
	 */
	public DataTableFolderViewBase(BinderInfo folderInfo, ViewReady viewReady) {
		// Initialize the super class...
		super(viewReady);

		// ...store the parameters...
		m_folderInfo = folderInfo;
		
		// ...initialize any other data members...
		m_images = GwtTeaming.getDataTableImageBundle();
		
		// ...create the main content panels and initialize the
		// ...composite...
		constructCommonContent();
		initWidget(m_mainPanel);

		// ...and finally, asynchronously initialize the view.
		loadPart1Async();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	final public BinderInfo         getFolderInfo()        {return m_folderInfo;                    }	// The binder being viewed.
	final public boolean			getFolderSortDescend() {return m_folderSortDescend;             }	//
	final public int                getFolderPageSize()    {return m_folderPageSize;                }	//
	final public List<FolderColumn>	getFolderColumns()     {return m_folderColumnsList;             }	// Columns, in order, to be shown in the data table.
	final public Long               getFolderId()          {return m_folderInfo.getBinderIdAsLong();}	//
	final public String				getFolderSortBy()      {return m_folderSortBy;                  }	//
	final public VibeFlowPanel      getFlowPanel()         {return m_flowPanel;                     }	// Flow panel holding the data table content (no toolbars, ...)
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	final public void setFolderSortDescend(boolean            folderSortDescend) {m_folderSortDescend = folderSortDescend;}
	final public void setFolderPageSize(   int                folderPageSize)    {m_folderPageSize    = folderPageSize;   }
	final public void setFolderColumns(    List<FolderColumn> folderColumnsList) {m_folderColumnsList = folderColumnsList;}
	final public void setFolderSortBy(     String             folderSortBy)      {m_folderSortBy      = folderSortBy;     }

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
						m_folderInfo.getBinderIdAsLong(),
						fr.getEntryId());
			}
		};
		
		// ...and connect it all together.
	    m_dataTable.addColumn(column, pinHeader);
	    setColumnStyles(column, COLUMN_PIN, colIndex);
	    setColumnWidth(         COLUMN_PIN, column  );
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
				if (null != m_entryMenuPanel) {
					// ...tell it to update the state of the delete and purge.
					m_entryMenuPanel.setDeleteAndPurgeState(checked);
				}
			};
		});

		// ...and connect it all together.
	    m_dataTable.addColumn(column, saHeader);
	    setColumnStyles(column, COLUMN_SELECT, colIndex);
	    setColumnWidth(         COLUMN_SELECT, column  );
	}

	/*
	 * Returns true if any rows are selected and false otherwise.
	 */
	private boolean areEntriesSelected() {
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
	 * Checks how many items are ready and once everything is, calls
	 * super class' viewReady() method.
	 */
	private void checkReadyness() {
		// If everything's ready...
		int toolPanels = m_toolPanels.size();
		if ((toolPanels + 1) == m_readyComponents) {	// Count of tool panels plus 1 for the view itself.
			// ...tell the super class.
			m_viewReady = true;
			super.viewReady();
		}
	}
	
	/*
	 * Creates the content panels, ... common to all data table folder
	 * view implementations.
	 */
	private void constructCommonContent() {
		// Initialize various data members of the class...
		initDataMembers();
		
		// ...create the main panel for the content...
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName("vibe-folderViewBase vibe-dataTableFolderViewBase vibe-verticalScroll");

		// ...set the sizing adjustments the account for the padding in
		// ...the vibe-folderViewBase style...
		final int padAdjust = (2 * GwtConstants.PANEL_PADDING);
		setContentHeightAdjust(getContentHeightAdjust() - padAdjust);
		setContentWidthAdjust( getContentWidthAdjust()  - padAdjust);

		// ...create a vertical panel to holds the layout that flows
		// ...down the view...
		m_verticalPanel = new VibeVerticalPanel();
		m_verticalPanel.addStyleName("vibe-dataTableFolderVerticalPanelBase");
	
		// ...create a flow panel for the implementing class to put
		// ...its content...
		m_flowPanel = new VibeFlowPanel();
		m_flowPanel.addStyleName("vibe-dataTableFolderFlowPanelBase");
		
		// ...and finally, tie everything together.
		m_verticalPanel.add(m_flowPanel);
		m_verticalPanel.addBottomPad();
		m_mainPanel.add(m_verticalPanel);
	}

	/**
	 * Abstract methods.
	 * 
	 * Called to allow the implementing class to complete the
	 * construction of the view.
	 */
	public abstract void constructView();
	public abstract void resetView();

	/*
	 * Asynchronously tells the implementing class to construct itself.
	 */
	private void constructViewAsync() {
		ScheduledCommand doConstructView = new ScheduledCommand() {
			@Override
			public void execute() {
				constructView();
			}
		};
		Scheduler.get().scheduleDeferred(doConstructView);
	}
	
	/*
	 * Various column type detectors.
	 */
	private static boolean isColumnAssigneeInfo(String columnName) {
		return
			(columnName.equals("attendee")          ||
			 columnName.equals("attendee_groups")   ||
			 columnName.equals("attendee_teams")    ||
			 columnName.equals("assignment")        ||
			 columnName.equals("assignment_groups") ||
			 columnName.equals("assignment_teams"));
	}
	private static boolean isColumnCustom(      FolderColumn column)     {return column.isCustomColumn();           }
	private static boolean isColumnDownload(    String       columnName) {return columnName.equals(COLUMN_DOWNLOAD);}
	private static boolean isColumnRating(      String       columnName) {return columnName.equals(COLUMN_RATING);  }
	private static boolean isColumnPresence(    String       columnName) {return columnName.equals(COLUMN_AUTHOR);  }
	private static boolean isColumnTitle(       String       columnName) {return columnName.equals(COLUMN_TITLE);   }
	private static boolean isColumnView(        String       columnName) {return columnName.equals(COLUMN_HTML);    }

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
					reply.add(row.getEntryId());
				}
			}
		}
		
		// If we get here, reply refers to List<Long> of the entry IDs
		// of the selected rows from the data table.  Return it.
		return reply;
	}
	
	/*
	 * Initializes various data members for the class.
	 * 
	 * Note:  Except for the select and pin columns, the values for the
	 *    column width were extracted from the implementation of
	 *    folder_view_common2.jsp.
	 */
	private void initDataMembers() {
		// Allocate a List<ToolPanelBase> to track the tool panels
		// created for the view.
		m_toolPanels = new ArrayList<ToolPanelBase>();

		// Initialize a map of the column widths used in the data
		// table...
		m_columnWidths = new HashMap<String, ColumnWidth>();
		m_columnWidths.put(COLUMN_SELECT, new ColumnWidth(40, Unit.PX));
		m_columnWidths.put(COLUMN_PIN,    new ColumnWidth(40, Unit.PX));

		// ...and initialize the remaining data members.
		initDataMembersImpl(m_dataTable);
	}

	/*
	 * Initializes the additional data members required when the data
	 * table is implemented using a DataGrid.
	 */
	@SuppressWarnings("unused")
	private void initDataMembersImpl(VibeDataGrid<FolderRow> dg) {
		// Add the widths for predefined column names...
		m_columnWidths.put(COLUMN_AUTHOR,   new ColumnWidth(24));	// Unless otherwise specified...
		m_columnWidths.put(COLUMN_COMMENTS, new ColumnWidth( 8));	// ...the widths default to...
		m_columnWidths.put(COLUMN_DATE,     new ColumnWidth(20));	// ...be a percentage value.
		m_columnWidths.put(COLUMN_DOWNLOAD, new ColumnWidth( 8));
		m_columnWidths.put(COLUMN_HTML,     new ColumnWidth(10));
		m_columnWidths.put(COLUMN_LOCATION, new ColumnWidth(30));
		m_columnWidths.put(COLUMN_NUMBER,   new ColumnWidth( 5));
		m_columnWidths.put(COLUMN_RATING,   new ColumnWidth(10));
		m_columnWidths.put(COLUMN_SIZE,     new ColumnWidth( 8));
		m_columnWidths.put(COLUMN_STATE,    new ColumnWidth( 8));
		m_columnWidths.put(COLUMN_RATING,   new ColumnWidth(10));
		m_columnWidths.put(COLUMN_TITLE,    new ColumnWidth(28));

		// ...and then one for everything else.
		m_columnWidths.put(COLUMN_OTHER,    new ColumnWidth(20));	// All columns not otherwise listed.
	}
	
	/*
	 * Initializes the additional data members required when the data
	 * table is implemented using a CellTable.
	 */
	private void initDataMembersImpl(VibeCellTable<FolderRow> ct) {
		m_columnWidths.put(COLUMN_TITLE, new ColumnWidth(100, Unit.PCT));
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

		// For discussion folders...
		if (FolderType.DISCUSSION == m_folderInfo.getFolderType()) {
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
						     reply = fr.getEntryId();
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
				// Yes!  Create a StringColumn for it.
				column = new StringColumn<FolderRow>(fc) {
					@Override
					public String getValue(FolderRow fr) {
						List<AssignmentInfo> aiList = fr.getColumnValueAsAssignmentInfos(fc);
						StringBuffer reply = new StringBuffer("");
						if ((null != aiList) && (!(aiList.isEmpty()))) {
							for (AssignmentInfo ai:  aiList) {
								if (0 < reply.length()) {
									reply.append(", ");
								}
								reply.append(ai.getTitle());
							}
						}
						return reply.toString();
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
			column.setSortable(true);
			m_dataTable.addColumn(column, fc.getColumnTitle());
		    setColumnStyles(      column, cName, colIndex++  );
		    setColumnWidth(               cName, column      );

		    // Is this the column we're sorted on?
		    if (fc.getColumnSortKey().equalsIgnoreCase(m_folderSortBy)) {
		    	// Yes!  Tell the data table about it.
				csl.push(new ColumnSortInfo(column, (!m_folderSortDescend)));
		    }
		}
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the BreadCrumbPanel.
	 */
	private void loadPart1Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the BreadCrumbPanel.
	 */
	private void loadPart1Now() {
		BreadCrumbPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpp) {
				m_toolPanels.add(tpp);
				m_verticalPanel.insert(tpp, BREADCRUMB_PANEL_INDEX);
				loadPart2Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the AccessoriesPanel.
	 */
	private void loadPart2Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the AccessoriesPanel.
	 */
	private void loadPart2Now() {
		AccessoriesPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_toolPanels.add(tpb);
				m_verticalPanel.insert(tpb, ACCESSORY_PANEL_INDEX);
				loadPart3Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	private void loadPart3Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	private void loadPart3Now() {
		DescriptionPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_toolPanels.add(tpb);
				m_verticalPanel.insert(tpb, DESCRIPTION_PANEL_INDEX);
				loadPart4Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	private void loadPart4Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart4Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	private void loadPart4Now() {
		FilterPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_toolPanels.add(tpb);
				m_verticalPanel.insert(tpb, FILTER_PANEL_INDEX);
				loadPart5Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart5Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart5Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart5Now() {
		EntryMenuPanel.createAsync(this, m_folderInfo, true, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_entryMenuPanel = ((EntryMenuPanel) tpb);
				m_toolPanels.add(tpb);
				m_verticalPanel.insert(tpb, ENTRY_MENU_PANEL_INDEX);
				loadPart6Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
	 */
	private void loadPart6Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart6Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
	 */
	private void loadPart6Now() {
		FooterPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_footerPanel = ((FooterPanel) tpb);
				m_toolPanels.add(tpb);
				m_verticalPanel.insert(tpb, FOOTER_PANEL_INDEX);
				loadPart7Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the column information for the folder.
	 */
	private void loadPart7Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart7Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the column information for the folder.
	 */
	private void loadPart7Now() {
		final Long folderId = m_folderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetFolderColumnsCmd(folderId, m_folderInfo.getFolderType()),
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
				// Store the folder columns and continue loading.
				FolderColumnsRpcResponseData responseData = ((FolderColumnsRpcResponseData) response.getResponseData());
				m_folderColumnsList = responseData.getFolderColumns();
				loadPart8Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
	 */
	private void loadPart8Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart8Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
	 */
	private void loadPart8Now() {
		final Long folderId = m_folderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetFolderDisplayDataCmd(folderId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderDisplayData(),
					folderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the folder display data and tell the
				// implementing class to construct itself.
				FolderDisplayDataRpcResponseData responseData = ((FolderDisplayDataRpcResponseData) response.getResponseData());
				m_folderSortBy      = responseData.getFolderSortBy();
				m_folderSortDescend = responseData.getFolderSortDescend();
				m_folderPageSize    = responseData.getFolderPageSize();
				constructViewAsync();
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
		if (eventBinderId.equals(m_folderInfo.getBinderIdAsLong())) {
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
		if (eventFolderId.equals(m_folderInfo.getBinderIdAsLong())) {
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
				DeleteFolderEntriesCmd cmd = new DeleteFolderEntriesCmd(m_folderInfo.getBinderIdAsLong(), selectedIds);
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
	 * Overrides Widget.onAttach()
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
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
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
		if (evenBinderId.equals(m_folderInfo.getBinderIdAsLong())) {
			// Yes!  Invoke the column sizing dialog on the folder.
//!			...this needs to be implemented...
			Window.alert("DataTableFolderViewBase.onInvokeColumnResizer(" + evenBinderId + "):  ...this needs to be implemented...");
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
		if (eventFolderId.equals(m_folderInfo.getBinderIdAsLong())) {
			// Yes!  Invoke the add file applet on the folder.
//!			...this needs to be implemented...
			Window.alert("DataTableFolderViewBase.onInvokeDropBox(" + eventFolderId + "):  ...this needs to be implemented...");
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
		if (eventFolderId.equals(m_folderInfo.getBinderIdAsLong())) {
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
				PurgeFolderEntriesCmd cmd = new PurgeFolderEntriesCmd(m_folderInfo.getBinderIdAsLong(), selectedIds);
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
	 * Overrides ViewBase.onResize()
	 */
	@Override
	public void onResize() {
		// Pass the resize on to the super class...
		super.onResize();

		// ...and do what we need to do locally.
		onResizeImpl(m_dataTable);
	}

	/*
	 * Performs the local resizing necessary when the data table is
	 * implemented using a DataGrid.
	 */
	@SuppressWarnings("unused")
	private void onResizeImpl(VibeDataGrid<FolderRow> dg) {
		int viewHeight		= getOffsetHeight();												// Height of the view.
		int viewTop			= getAbsoluteTop();													// Absolute top of the view.		
		int dtTop			= (dg.getAbsoluteTop() - viewTop);									// Top of the data table relative to the top of the view.		
		int dtPagerHeight	= m_dataTablePager.getOffsetHeight();								// Height of the data table's pager.
		int fpHeight		= ((null == m_footerPanel) ? 0 : m_footerPanel.getOffsetHeight());	// Height of the view's footer panel.
		int totalBelow		= (dtPagerHeight + fpHeight);										// Total space on the page below the data table.

		// What's the optimum height for the data table so we don't get
		// a vertical scroll bar?
		int dataTableHeight = (((viewHeight - dtTop) - totalBelow) - NO_VSCROLL_ADJUST);
		if (MINIMUM_HEIGHT > dataTableHeight) {
			// Too small!  Use the minimum even though this will turn
			// on the vertical scroll bar.
			dataTableHeight = MINIMUM_HEIGHT;
		}
		
		// Set the height of the data table.
		dg.setHeight(dataTableHeight + "px");
	}
	
	/*
	 * Performs the local resizing necessary when the data table is
	 * implemented using a CellTable.
	 */
	private void onResizeImpl(VibeCellTable<FolderRow> ct) {
		// Nothing to do.
	}
	
	/**
	 * Called from the super class to populate the content of this
	 * folder view.
	 */
	public void populateContent(String styleName) {		
		// If we're supposed to dump the display data we're building this on...
		VibeVerticalPanel vp;
		if (DUMP_DISPLAY_DATA) {
			// ...dump it.
			vp = new VibeVerticalPanel();
			vp.add(new HTML("<br/>- - - - - Start:  Folder Display Data - - - - -<br/><br/>"));
			vp.add(new InlineLabel("Sort by:  "         + m_folderSortBy)     );
			vp.add(new InlineLabel("Sort descending:  " + m_folderSortDescend));
			vp.add(new InlineLabel("Page size:  "       + m_folderPageSize)   );
			vp.add(new HTML("<br/>"));
			for (FolderColumn fc:  m_folderColumnsList) {
				vp.add(new InlineLabel(fc.getColumnEleName() + "='" + fc.getColumnTitle() + "'"));
			}
			vp.add(new HTML("<br/>- - - - - End:  Folder Display Data - - - - -<br/>"));
			m_flowPanel.add(vp);
		}
		
		// Create a key provider that will provide a unique key for
		// each row.
		FolderRowKeyProvider keyProvider = new FolderRowKeyProvider();
		
		// Create the table.
		m_dataTable = new VibeCellTable<FolderRow>(m_folderPageSize, keyProvider);
		setDataTableWidthImpl(m_dataTable);
		m_dataTable.addStyleName("vibe-dataTableFolderDataTableBase");
		if (GwtClientHelper.hasString(styleName)) {
			m_dataTable.addStyleName(styleName);
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
		m_flowPanel.add(vp);
		
		// Finally, ensure the table gets sized correctly.
		onResizeAsync();
	}
	
	public void populateContent() {
		// Always use the initial form of the method.
		populateContent(null);
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

	/**
	 * Resets the content for the implementing class.
	 */
	public void resetContent() {
		// Clear the flow panel's content...
		m_flowPanel.clear();
		
		// ...tell the tool panels to perform any resetting they need
		// ...to do...
		for (ToolPanelBase tpb:  m_toolPanels) {
			tpb.resetPanel();
		}
		
		// ...and reset anything else that's necessary.
	}

	/*
	 * Asynchronously resets the view.
	 */
	private void resetViewAsync() {
		ScheduledCommand doResetView = new ScheduledCommand() {
			@Override
			public void execute() {
				m_readyComponents = 0;
				m_viewReady       = false;
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
	    styles.append(columnName.equals(COLUMN_SELECT) ? STYLE_COL_SELECT : columnName);
	    m_dataTable.addColumnStyleName(colIndex, styles.toString());
	}
	
	/*
	 * Sets the width of a column in the data table based on the
	 * column's name.
	 */
	private void setColumnWidth(String columnName, Column<FolderRow, ?> column) {
		// Determine the width for the column...
		ColumnWidth width = m_columnWidths.get(columnName);
		if (null == width) {
			width = m_columnWidths.get(COLUMN_OTHER);
		}
		
		// ...and if we have a one...
		if (null != width) {
			// ...put it into affect.
			m_dataTable.setColumnWidth(
				column,
				width.getWidth(),
				width.getUnits());
		}
	}
	
	/*
	 * Sets the table width required when the data table is implemented
	 * as a DataGrid.
	 */
	@SuppressWarnings("unused")
	private void setDataTableWidthImpl(VibeDataGrid<FolderRow> ct) {
		// Nothing to do.
	}

	/*
	 * Sets the table width required when the data table is implemented
	 * as a CellTable.
	 */
	private void setDataTableWidthImpl(VibeCellTable<FolderRow> ct) {
		ct.setWidth("100%", false);	// false -> Not fixed layout.
	}

	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 */
	@Override
	public void toolPanelReady(ToolPanelBase toolPanel) {
		if (!m_viewReady) {
			m_readyComponents += 1;
			checkReadyness();
		}
		
		else if (GwtClientHelper.getRequestInfo().isDebugUI()) {
			Window.alert("DataTableFolderViewBase.toolPanelReady( *Internal Error* ):  Unexpected call to toolPanelReady() method.");
		}
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
	
	/**
	 * Called by classes that extend this base class so that it can
	 * inform the world that its view is ready to go.
	 */
	@Override
	public void viewReady() {
		if (!m_viewReady) {
			m_readyComponents += 1;
			checkReadyness();
		}
		
		else if (GwtClientHelper.getRequestInfo().isDebugUI()) {
			Window.alert("DataTableFolderViewBase.viewReady( *Internal Error* ):  Unexpected call to viewReady() method.");
		}
	}
}
