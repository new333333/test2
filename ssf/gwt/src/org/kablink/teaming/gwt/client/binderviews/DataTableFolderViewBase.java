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

import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.binderviews.BreadCrumbPanel;
import org.kablink.teaming.gwt.client.binderviews.EntryMenuPanel;
import org.kablink.teaming.gwt.client.binderviews.FilterPanel;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.datatable.VibeDataTable;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderRowsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Base object of 'data table' based folder views.
 * 
 * @author drfoster@novell.com
 */
public abstract class DataTableFolderViewBase extends ViewBase {
	private final BinderInfo			m_folderInfo;			// A BinderInfo object that describes the folder being viewed.
	private boolean						m_folderSortDescend;	// true -> The folder is sorted in descending order.  false -> It's sorted in ascending order.
	private int							m_folderPageSize;		//
	private FolderRowPager 				m_dataTablePager;		//
	private FooterPanel					m_footerPanel;			//
	private HashMap<String, Integer>	m_columnWidths;			//
	private List<FolderColumn>			m_folderColumnsList;	// The list of columns to be displayed.
	private List<ToolPanelBase>			m_toolPanels;			//
	private String						m_folderSortBy;			// Which column the view is sorted on.
	private VibeDataTable<FolderRow>	m_dataTable;			//
	private VibeFlowPanel				m_mainPanel;			// The main panel holding the content of the view.
	private VibeFlowPanel				m_flowPanel;			// The flow panel used to hold the view specific content of the view.
	private VibeVerticalPanel			m_verticalPanel;		// The vertical panel that holds all components of the view, both common and view specific.

	// The following controls whether the display data read from the
	// server is dumped as part of the content of the view.
	private final static boolean DUMP_DISPLAY_DATA	= false;
	
	// The following define the indexes into a VibeVerticalPanel of the
	// various panels that makeup a data table based folder view.
	private final static int BREADCRUMB_PANEL_INDEX	= 0;
	private final static int ACCESSORY_PANEL_INDEX	= 1;
	private final static int FILTER_PANEL_INDEX		= 2;
	private final static int ENTRY_MENU_PANEL_INDEX	= 3;
	@SuppressWarnings("unused")
	private final static int DATA_TABLE_PANEL_INDEX	= 4;
	private final static int FOOTER_PANEL_INDEX		= 5;

	// The following the various internal names used columns in the
	// data table.
	private final static String COLUMN_SELECT	= "--select--";
	private final static String COLUMN_PIN		= "--pin--";
	private final static String COLUMN_OTHER	= "--other--";

	// The following are used to construct the style names applied
	// to the columns and rows of the data table.
	private final static String STYLE_COL_BASE		= "vibe-dataTableFolderColumn-";
	private final static String STYLE_COL_SELECT	= "select";
	private final static String STYLE_ROW_BASE		= "vibe-dataTableFolderRow-";
	private final static String STYLE_ROW_EVEN		= "even";
	private final static String STYLE_ROW_ODD		= "odd";

	/*
	 * Inner class used to provide list of FolderRow's.
	 */
	private class FolderRowAsyncProvider extends AsyncDataProvider<FolderRow> {
		private VibeDataTable<FolderRow> m_vdt;
		
		/**
		 * Constructor method.
		 * 
		 * @param keyProvider
		 */
		public FolderRowAsyncProvider(VibeDataTable<FolderRow> vdt, ProvidesKey<FolderRow> keyProvider) {
			// Initialize the super class...
			super(keyProvider);
			
			// ...and store the parameters.
			m_vdt = vdt;
		}

		/**
		 * Overrides AsyncDataProvider.onRowChanged()
		 */
		@Override
		protected void onRangeChanged(HasData<FolderRow> display) {
			final Long folderId = m_folderInfo.getBinderIdAsLong();
			final Range range = display.getVisibleRange();
			GwtClientHelper.executeCommand(
					new GetFolderRowsCmd(folderId, m_folderColumnsList, range.getStart(), range.getLength()),
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
					// Apply the rows we read...
					FolderRowsRpcResponseData responseData = ((FolderRowsRpcResponseData) response.getResponseData());
					m_vdt.setRowData( responseData.getStartOffset(), responseData.getFolderRows());
					m_vdt.setRowCount(responseData.getTotalRows()                                );
					
					// ...and ensure the table has been sized.
					setSizeAsync();
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
//!			...this needs to be implemented...
			Window.alert("DataTableFolderViewBase.FolderRowSortHandler.onColumnSort():  ...this needs to be implemented...");
		}
	}

	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public DataTableFolderViewBase(BinderInfo folderInfo, ViewReady viewReady) {
		// Initialize the base class...
		super(viewReady);

		// ...store the parameters...
		m_folderInfo = folderInfo;
		
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
	final public BinderInfo         getFolderInfo()        {return m_folderInfo;                    }
	final public boolean			getFolderSortDescend() {return m_folderSortDescend;             }
	final public int                getFolderPageSize()    {return m_folderPageSize;                }
	final public List<FolderColumn>	getFolderColumns()     {return m_folderColumnsList;             }
	final public Long               getFolderId()          {return m_folderInfo.getBinderIdAsLong();}
	final public String				getFolderSortBy()      {return m_folderSortBy;                  }
	final public VibeFlowPanel      getFlowPanel()         {return m_flowPanel;                     }
	
	/*
	 * Creates the content panels, ... common to all data table folder
	 * view implementations.
	 */
	private void constructCommonContent() {
		// Initialize various data members of the class.
		initDataMembers();
		
		// Create the main panel for the content...
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName("vibe-folderViewBase vibe-dataTableFolderViewBase");

		// ...set the sizing adjustments the account for the padding in
		// ...the vibe-folderViewBase style...
		final int padAdjust = (2 * GwtConstants.PANEL_PADDING);
		setContentHeightAdjust(getContentHeightAdjust() - padAdjust);
		setContentWidthAdjust( getContentWidthAdjust()  - padAdjust);

		// ...create the vertical panel that holds the layout that
		// ...flows down the view...
		m_verticalPanel = new VibeVerticalPanel();
		m_verticalPanel.addStyleName("vibe-dataTableFolderVerticalPanelBase");
	
		// ...create a flow panel for the implementing class to put
		// ...its content.
		m_flowPanel = new VibeFlowPanel();
		m_flowPanel.addStyleName("vibe-dataTableFolderFlowPanelBase");
		
		// ...and tie everything together.
		m_verticalPanel.add(m_flowPanel);
		m_verticalPanel.addBottomPad();
		m_mainPanel.add(m_verticalPanel);
	}
	
	/**
	 * Called to allow the implementing class to complete the
	 * construction of the view.
	 * 
	 * @param folderColumnsList
	 * @param folderSortBy
	 * @param folderSortDescend
	 */
	public abstract void constructView(List<FolderColumn> folderColumnsList, String folderSortBy, boolean folderSortDescend, int folderPageSize);
	public abstract void resetView(    List<FolderColumn> folderColumnsList, String folderSortBy, boolean folderSortDescend, int folderPageSize);

	/*
	 * Asynchronously tells the implementing class to construct itself.
	 */
	private void constructViewAsync() {
		ScheduledCommand doConstructView = new ScheduledCommand() {
			@Override
			public void execute() {
				constructView(
					m_folderColumnsList,
					m_folderSortBy,
					m_folderSortDescend,
					m_folderPageSize);
			}
		};
		Scheduler.get().scheduleDeferred(doConstructView);
	}
	
	private void initDataMembers() {
		// Allocate a List<ToolPanelBase> to track the tool panels
		// created for the view.
		m_toolPanels = new ArrayList<ToolPanelBase>();

		// Initialize a map of the column widths used in the data
		// table.  The values were extracted from the implementation
		// of folder_view_common2.jsp.
		m_columnWidths = new HashMap<String, Integer>();
		m_columnWidths.put("author",     24);
		m_columnWidths.put("comments",    8);
		m_columnWidths.put("date",       20);
		m_columnWidths.put("download",    8);
		m_columnWidths.put("html",       10);
		m_columnWidths.put("rating",     10);
		m_columnWidths.put("size",        8);
		m_columnWidths.put("state",       8);
		m_columnWidths.put("title",      28);
		m_columnWidths.put("_sortNum",    5);
		m_columnWidths.put(COLUMN_SELECT, 4);
		m_columnWidths.put(COLUMN_PIN,    2);
		m_columnWidths.put(COLUMN_OTHER, 20);
	}
	
	/*
	 * Add the columns to the table.
	 */
	private void initTableColumns(final FolderRowSelectionModel selectionModel, FolderRowSortHandler sortHandler) {
		// Add a column for a checkbox selector.
		int colIndex = 0;
	    Column<FolderRow, Boolean> cc =
	        new Column<FolderRow, Boolean>(new CheckboxCell(true, false)) {
	          @Override
	          public Boolean getValue(FolderRow fr) {
	            return selectionModel.isSelected(fr);
	          }
	        };
	    m_dataTable.addColumn(cc, SafeHtmlUtils.fromSafeConstant("<br/>"));
	    m_dataTable.addColumnStyleName(colIndex++, (STYLE_COL_BASE + STYLE_COL_SELECT));
	    setColumnWidth(COLUMN_SELECT, cc);

	    // Scan the columns defined in this folder.
		for (final FolderColumn fc:  m_folderColumnsList) {
			// Define a TextColumn for each column.
			TextColumn<FolderRow> tc = new TextColumn<FolderRow>() {
				@Override
				public String getValue(FolderRow fr) {
					return fr.getColumnValue(fc);
				}
			};
			tc.setSortable(true);
			String fcTitle = fc.getColumnTitle();
			m_dataTable.addColumn(tc, fcTitle);
		    m_dataTable.addColumnStyleName(colIndex++, (STYLE_COL_BASE + fcTitle));
		    setColumnWidth(fc.getColumnTitle(), tc);
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
		BreadCrumbPanel.createAsync(m_folderInfo, new ToolPanelClient() {			
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
		AccessoriesPanel.createAsync(m_folderInfo, new ToolPanelClient() {			
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
		FilterPanel.createAsync(m_folderInfo, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_toolPanels.add(tpb);
				m_verticalPanel.insert(tpb, FILTER_PANEL_INDEX);
				loadPart4Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
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
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart4Now() {
		EntryMenuPanel.createAsync(m_folderInfo, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_toolPanels.add(tpb);
				m_verticalPanel.insert(tpb, ENTRY_MENU_PANEL_INDEX);
				loadPart5Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
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
	 * Loads the FooterPanel.
	 */
	private void loadPart5Now() {
		FooterPanel.createAsync(m_folderInfo, new ToolPanelClient() {			
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
				loadPart6Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the column information for the folder.
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
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the column information for the folder.
	 */
	private void loadPart6Now() {
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
				loadPart7Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
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
	 * Loads the display data information for the folder.
	 */
	private void loadPart7Now() {
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
	 * Sets the size of the data table based on its position in the
	 * view.
	 * 
	 * Overrides ViewBase.onResize()
	 */
	@Override
	public void onResize() {
		super.onResize();

		int viewHeight		= getOffsetHeight();												// Height of the view.
		int viewTop			= getAbsoluteTop();													// Absolute top of the view.		
		int dtTop			= (m_dataTable.getAbsoluteTop() - viewTop);							// Top of the data table relative to the top of the view.		
		int dtPagerHeight	= m_dataTablePager.getOffsetHeight();								// Height of the data table's pager.
		int fpHeight		= ((null == m_footerPanel) ? 0 : m_footerPanel.getOffsetHeight());	// Height of the view's footer panel.
		int totalBelow		= (dtPagerHeight + fpHeight);										// Total space on the page below the data table.
		
		int dataTableHeight = ((viewHeight - dtTop) - totalBelow);								// How tall we can make the data table.
		m_dataTable.setHeight(dataTableHeight + "px");
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
				vp.add(new InlineLabel(fc.getColumnName() + "='" + fc.getColumnTitle() + "'"));
			}
			vp.add(new HTML("<br/>- - - - - End:  Folder Display Data - - - - -<br/>"));
			m_flowPanel.add(vp);
		}
		
		// Create a key provider that will provide a unique key for
		// each row.
		FolderRowKeyProvider keyProvider = new FolderRowKeyProvider();
		
		// Create the table.
		m_dataTable = new VibeDataTable<FolderRow>(m_folderPageSize, keyProvider);
		m_dataTable.addStyleName("vibe-dataTableFolderDataTableBase");
		if (GwtClientHelper.hasString(styleName)) {
			m_dataTable.addStyleName(styleName);
		}
		m_dataTable.setWidth( "100%");
		m_dataTable.setRowStyles(new RowStyles<FolderRow>() {
			@Override
			public String getStyleNames(FolderRow row, int rowIndex) {
				boolean even = (0 == (rowIndex / 2));
				return STYLE_ROW_BASE + (even ? STYLE_ROW_EVEN : STYLE_ROW_ODD);
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

	    // Finally, add the table and pager to the view.
		vp = new VibeVerticalPanel();
	    vp.add(m_dataTable);
	    vp.add(m_dataTablePager);
	    vp.setCellHorizontalAlignment(m_dataTablePager, HasHorizontalAlignment.ALIGN_CENTER);
		m_flowPanel.add(vp);
	}
	
	public void populateContent() {
		// Always use the initial form of the method.
		populateContent(null);
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
	 * Sets the width of a column in the data table based on the
	 * column's title.
	 */
	private void setColumnWidth(String columnTitle, Column<FolderRow, ?> column) {
		Integer width = m_columnWidths.get(columnTitle);
		if (null == width) {
			width = m_columnWidths.get(COLUMN_OTHER);
			if (null == width) {
				width = 20;
			}
		}
		m_dataTable.setColumnWidth(column, width, Unit.PCT);
	}
	
	/*
	 * Asynchronously forces the data table to be resized.
	 */
	private void setSizeAsync() {
		ScheduledCommand doResize = new ScheduledCommand() {
			@Override
			public void execute() {
				onResize();
			}
		};
		Scheduler.get().scheduleDeferred(doResize);
	}
}
