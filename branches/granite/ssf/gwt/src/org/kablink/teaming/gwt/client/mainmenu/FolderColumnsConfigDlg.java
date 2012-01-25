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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Implements a dialog for selecting the folder columns.
 *  
 * @author phurley@novell.com
 */
public class FolderColumnsConfigDlg extends DlgBox implements EditSuccessfulHandler {
	private final static String IDBASE				= "folderColumn_";	// Base ID for rows in the folder columns Grid.
	private final static String IDTAIL_CHECKBOX		= "_cb";			// Used for constructing the ID of a row's checkbox.
	private final static String IDTAIL_RADIO		= "_rb";			// Used for constructing the ID of a row's radio button.
	private final static String IDTAIL_TEXTBOX		= "_tb";			// Used for constructing the ID of a row's text box.
	private final static String OPTION_HEADER_ID	= "optionHeader";
	
	private Grid m_folderColumnsGrid;				// Once displayed, the table of folder columns.
	private Grid m_header;
	private CheckBox m_folderDefaultCheckBox;		// Set the folder default columns.
	private Button m_folderDefaultBtn;				// Restore default settings
	private GwtTeamingMessages m_messages;			// Access to the GWT UI messages.
	private String m_binderId;						// The ID of the binder the folder columns dialog is running against.
	private int m_folderColumnsListCount;			// Count of items in m_folderColumnsList.
	private List<FolderColumn> m_folderColumnsList;		// List of selected folder column items.
	private List<FolderColumn> m_folderColumnsListAll;	// List of all folder column items.
	private ScrollPanel m_sp;
	private VerticalPanel m_vp;

	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label) {
			super(label);
			addStyleName("folderColumnsDlg_Label");
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FolderColumnsConfigDlg() {
		// Initialize the superclass...
		super(true, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the main dialog panels.
		createAllDlgContent(
			m_messages.folderColumnsDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Data passed via global data members.
	}
	
	/**
	 * Callback interface to interact with the dialog asynchronously
	 * after it loads. 
	 */
	public interface FolderColumnsConfigDlgClient {
		void onSuccess(FolderColumnsConfigDlg dlg);
		void onUnavailable();
	}

	/*
	 * Adds a section header row to the folder options grid.
	 */
	private void addHeaderRow(Grid grid, int row) {
		String columnText = m_messages.folderColumnsDlgFolderColumn();
		String customLabelText = m_messages.folderColumnsDlgFolderCustomLabel();
		Label columnHeader = new Label(columnText);
		Label customLabelHeader = new Label(customLabelText);
		MenuBar mb = new MenuBar();
		mb.addStyleName("favoritesDlg_MenuBar");
		mb.addItem(new DlgMenuItem(m_messages.mainMenuFavoritesDlgMoveUp(),   new DoMoveUp()));
		mb.addItem(new DlgMenuItem(m_messages.mainMenuFavoritesDlgMoveDown(), new DoMoveDown()));

		grid.insertRow(row);
		Element e = grid.getRowFormatter().getElement(row); 
		e.setId(OPTION_HEADER_ID);
		e.addClassName("folderColumnsDlg_SectionHeaderRow");
		e = grid.getCellFormatter().getElement(row, 0);
		e.addClassName("folderColumnsDlg_SectionHeaderCell");
		e.addClassName("folderColumnsDlg_GridCell_0and1");
		grid.setWidget(row, 0, columnHeader);
		e = grid.getCellFormatter().getElement(row, 2);
		e.addClassName("folderColumnsDlg_GridCell_2");
		GwtClientHelper.setGridColSpan(grid, row, 0, 2);
		grid.setWidget(row, 2, customLabelHeader);
		e = grid.getCellFormatter().getElement(row, 3);
		e.addClassName("folderColumnsDlg_GridCell_3");
		grid.setWidget(row, 3, mb);
	}

	/**
	 * Creates the panels that will contain the dialog's content.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param ignored
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object ignored) {
		// Create the panels to hold the dialog's content...
		m_vp = new VerticalPanel();
		m_vp.setStyleName( "teamingDlgBoxContent" );
		m_header =  new Grid(0, 4);
		m_header.addStyleName("folderColumnsDlg_Grid");
		m_vp.add(m_header);
		m_sp = new ScrollPanel();
		m_sp.addStyleName("folderColumnsDlg_ScrollPanel");
		m_vp.add(m_sp);

		// ...and return the outermost panel that will hold them.
		return m_vp;
	}

	/*
	 * Called after the folder column information has been read from
	 * the server to create all the controls that make up the dialog.
	 */
	private void createContentImpl() {
		// Are there any folder columns to display in the dialog?
		if (0 < (m_folderColumnsListCount)) {
			// Yes!  Create a grid to contain them... 
			m_folderColumnsGrid = new Grid(0, 4);
			m_folderColumnsGrid.addStyleName("folderColumnsDlg_Grid");
			m_folderColumnsGrid.setCellPadding(0);
			m_folderColumnsGrid.setCellSpacing(0);
			
			// ...render the folder columns into the panel...
			if (0 < m_folderColumnsListCount) {
				addHeaderRow(m_header, 0);
				for (int i = 0; i < m_folderColumnsListCount; i += 1) {
					renderRow(m_folderColumnsGrid, m_folderColumnsGrid.getRowCount(), 
							m_folderColumnsListCount, m_folderColumnsListAll.get(i));
				}
			}

			// ...and connect everything together.
			m_sp.add(m_folderColumnsGrid);
			
			//Add the option to set the folder default
			FlexTable ft = new FlexTable();
			m_folderDefaultCheckBox = new CheckBox();
			ft.setWidget(0, 0, m_folderDefaultCheckBox);
			ft.setText(0, 1, m_messages.folderColumnsSetAsDefault());
			
			//Add a button to restore factory defaults
			m_folderDefaultBtn = new Button(m_messages.folderColumnsRestoreDefaults());
			m_folderDefaultBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					//Go restore the defaults
					AsyncCallback<VibeRpcResponse> rpcSaveCallback;
					rpcSaveCallback = new AsyncCallback<VibeRpcResponse>() {
						/**
						 * 
						 */
						@Override
						public void onFailure( Throwable t )
						{
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_SaveFolderColumns() );
						}
						
						/**
						 * 
						 */
						@Override
						public void onSuccess( VibeRpcResponse response ) {
							GetBinderPermalinkCmd cmd;
							
							cmd = new GetBinderPermalinkCmd( m_binderId );
							GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
							{
								@Override
								public void onFailure( Throwable t ) {
									GwtClientHelper.handleGwtRPCFailure(
										t,
										GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
										m_binderId );
								}//end onFailure()
								
								@Override
								public void onSuccess( VibeRpcResponse response )
								{
									String binderUrl;
									OnSelectBinderInfo osbInfo;
									StringRpcResponseData responseData;

									responseData = (StringRpcResponseData) response.getResponseData();
									binderUrl = responseData.getStringValue();
									
									osbInfo = new OnSelectBinderInfo( m_binderId, binderUrl, false, Instigator.CONTENT_AREA_CHANGED );
									if (GwtClientHelper.validateOSBI( osbInfo ))
									{
										GwtTeaming.fireEvent( new ChangeContextEvent( osbInfo ) );
									}
									hide();
								}// end onSuccess()
							});// end AsyncCallback()
						}
					};

					SaveFolderColumnsCmd cmd = new SaveFolderColumnsCmd( m_binderId, 
							new ArrayList<FolderColumn>(), new Boolean(Boolean.TRUE));
					GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
				}
			});
			ft.getFlexCellFormatter().setColSpan(1, 0, 2);
			ft.setWidget(1, 0, m_folderDefaultBtn);
			ft.addStyleName("folderColumnsDlg_OptionsGrid");
			m_vp.add(ft);			
		}
		
		else {
			// No, there weren't any folder options to display in the
			// dialog!  Put a simple no available options message.
			m_vp.add(new DlgLabel(m_messages.folderColumnsNoOptions()));
		}
	}
	
	
	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Save the new folder column info
		@SuppressWarnings("unchecked")
		List<FolderColumn> fcList = ((List<FolderColumn>) callbackData);
		Boolean isDefault = m_folderDefaultCheckBox.getValue();
		AsyncCallback<VibeRpcResponse> rpcSaveCallback;
		rpcSaveCallback = new AsyncCallback<VibeRpcResponse>() {
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveFolderColumns() );
			}
			
			/**
			 * 
			 */
			@Override
			public void onSuccess( VibeRpcResponse response ) {
				GetBinderPermalinkCmd cmd;
				
				cmd = new GetBinderPermalinkCmd( m_binderId );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
				{
					@Override
					public void onFailure( Throwable t ) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
							m_binderId );
					}//end onFailure()
					
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						String binderUrl;
						OnSelectBinderInfo osbInfo;
						StringRpcResponseData responseData;

						responseData = (StringRpcResponseData) response.getResponseData();
						binderUrl = responseData.getStringValue();
						
						osbInfo = new OnSelectBinderInfo( m_binderId, binderUrl, false, Instigator.CONTENT_AREA_CHANGED );
						if (GwtClientHelper.validateOSBI( osbInfo ))
						{
							GwtTeaming.fireEvent( new ChangeContextEvent( osbInfo ) );
						}
						
						hide();
					}// end onSuccess()
				});// end AsyncCallback()
			}
		};

		SaveFolderColumnsCmd cmd = new SaveFolderColumnsCmd( m_binderId, fcList, isDefault );
		GwtClientHelper.executeCommand( cmd, rpcSaveCallback );

		// Return false so close the dialog doesn't close yet.  Well
		// close it only after we've successfully saved the column data
		// information.  Note the hide() in the above onSuccess().
		return false;
	}

	
	/**
	 * Returns the edited List<FolderColumn>.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Are there any rows in the grid?
		int rows = m_folderColumnsGrid.getRowCount();
		if (0 < rows) {
			// Yes!  Scan and update them.
			for (int i = 0; i < rows; i += 1) {
				String rowId = getRowId(i);
				String fcName = getFolderColumnNameFromRow(i);
				FolderColumn fc = getFolderColumnByName(fcName);
				InputElement cb = Document.get().getElementById(rowId + IDTAIL_CHECKBOX).getFirstChild().cast();
				fc.setColumnIsShown(cb.isChecked());
				InputElement tb = Document.get().getElementById(rowId + IDTAIL_TEXTBOX).cast();
				fc.setColumnCustomTitle(tb.getValue());
			}
		}
		
		//Return the updated list of columns
		return m_folderColumnsListAll;
	}

	/*
	 * Returns a row's FolderColumn column name.
	 */
	private String getFolderColumnNameFromRow(int row) {
		String rowId = getRowId(row);
		return rowId.substring(rowId.indexOf('_') + 1);
	}
	
	/*
	 * Returns a FolderColumn base on its name.
	 */
	private FolderColumn getFolderColumnByName(String name) {
		// Scan the available folder columns.
		for (int i = 0; i < m_folderColumnsListCount; i += 1) {
			// Is this the ToolbarItem in question?
			FolderColumn fci = m_folderColumnsListAll.get(i);
			if (fci.getColumnDefId() != null && !fci.getColumnDefId().equals("")) {
				if (name.equals(fci.getColumnDefId()+"."+fci.getColumnEleName())) {
					// Yes, this is the row
					return fci;
				}
			} else {
				if (fci.getColumnName().equals(name)) {
					// Yes!  Return it.
					return fci;
				}
			}
		}
		
		// If we get here, we couldn't find a FolderColumn with the
		// requested Name.  Return null.
		return null;
	}
	
	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}

	/*
	 * Returns the ID of a row.
	 */
	private String getRowId(int row) {
		return m_folderColumnsGrid.getRowFormatter().getElement(row).getId();
	}
	
	/*
	 * Returns true if a row's checkbox is checked. 
	 */
	private boolean isRowChecked(int row) {
		boolean reply;
		String rowId = getRowId(row);
		if (rowId.equals(OPTION_HEADER_ID)) {
			reply = false;
		} else {
			InputElement rb = Document.get().getElementById(rowId + IDTAIL_RADIO).getFirstChild().cast();
			reply = rb.isChecked();
		}
		return reply;
	}
	
	/*
	 * Check a row's radio button. 
	 */
	private void setRowChecked(int row) {
		String rowId = getRowId(row);
		if (!rowId.equals(OPTION_HEADER_ID)) {
			InputElement rb = Document.get().getElementById(rowId + IDTAIL_RADIO).getFirstChild().cast();
			if (rb != null) {
				rb.setChecked(true);
			}
		}
	}
	
	/*
	 * Renders a folder column as a row in a Grid.
	 */
	private void renderRow(Grid grid, int row, int gridSize, FolderColumn fci) {
		grid.insertRow(row);
		
		String rowIdSuffix = fci.getColumnName();
		if (fci.getColumnDefId() != null && !fci.getColumnDefId().equals("")) {
			rowIdSuffix = fci.getColumnDefId()+"."+fci.getColumnEleName();
		}
		String rowId = (IDBASE + rowIdSuffix);
		grid.getRowFormatter().getElement(row).setId(rowId);
		
		//Checkbox to select the column for view
		CheckBox cb = new CheckBox();
		cb.setName("ColumnSelected_" + rowIdSuffix);
		cb.addStyleName("folderColumnsDlg_CheckBox");
		cb.getElement().setId(rowId + IDTAIL_CHECKBOX);
		cb.setValue(fci.getColumnIsShown());
		grid.setWidget(row, 0, cb);
		grid.getCellFormatter().addStyleName(row, 0, "folderColumnsDlg_GridCell_0");
		
		//Column title
		String txt = fci.getColumnDefaultTitle();
		if (!(GwtClientHelper.hasString(txt))) {
			txt = fci.getColumnEleName();
		}
		grid.setWidget(row, 1, new Label(txt));
		grid.getCellFormatter().addStyleName(row, 1, "folderColumnsDlg_GridCell_1");
		
		//Custom label textbox
		TextBox tb = new TextBox();
		tb.setName("CustomName");
		tb.getElement().setId(rowId + IDTAIL_TEXTBOX);
		if ((GwtClientHelper.hasString(fci.getColumnCustomTitle()))) {
			tb.setValue(fci.getColumnCustomTitle());
		}
		grid.setWidget(row, 2, tb);
		grid.getCellFormatter().addStyleName(row, 2, "folderColumnsDlg_GridCell_2");
		
		//Radio button for moving rows
		RadioButton rb = new RadioButton("MoveButton");
		rb.getElement().setId(rowId + IDTAIL_RADIO);
		grid.setWidget(row, 3, rb);
		grid.getCellFormatter().setAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().addStyleName(row, 3, "folderColumnsDlg_GridCell_3");
	}

	/*
	 * Inner class that wraps item's on the dialog's menu.
	 */
	private class DlgMenuItem extends MenuItem {
		public DlgMenuItem(String text, Command menuCommand) {
			super(text, menuCommand);
			addStyleName("folderColumnsDlg_MenuItem");
		}
	}

	/*
	 * Inner class that implements the move down command.
	 */
	private class DoMoveDown implements Command {
		@Override
		public void execute() {
			// If the table is empty...
			if (0 == m_folderColumnsListCount) {
				// ...there's nothing to do.
				return;
			}
			
			// Do we have some rows in the table without the bottom one
			// being checked?
			int rows = m_folderColumnsGrid.getRowCount();
			if ((0 < rows) && (!(isRowChecked(rows-1)))) {
				// Yes!  Scan the rows.
				getDataFromDlg();	//Make sure all of the changed settings are captured first
				for (int i = (rows - 1); i >= 0; i -= 1) {
					// If this row checked...
					if (isRowChecked(i)) {
						// ...move it down.
						m_folderColumnsGrid.removeRow(i);
						renderRow(m_folderColumnsGrid, i+1, 
								m_folderColumnsListCount, m_folderColumnsListAll.get(i));
						setRowChecked(i+1);
						FolderColumn fc1 = m_folderColumnsListAll.get(i);
						FolderColumn fc2 = m_folderColumnsListAll.get(i+1);
						m_folderColumnsListAll.set(i, fc2);
						m_folderColumnsListAll.set(i+1, fc1);
						break;
					}
				}
			}
		}
	}
	
	/*
	 * Inner class that implements the move up command.
	 */
	private class DoMoveUp implements Command {
		@Override
		public void execute() {
			// If the table is empty...
			if (0 == m_folderColumnsListCount) {
				// ...there's nothing to do.
				return;
			}
			
			// Do we have some rows in the table without the top one
			// being checked?
			int rows = m_folderColumnsGrid.getRowCount();
			if ((0 < rows) && (!(isRowChecked(0)))) {
				// Yes!  Scan the rows.
				getDataFromDlg();	//Make sure all of the changed settings are captured first
				for (int i = 1; i < rows; i += 1) {
					// If this row checked...
					if (isRowChecked(i)) {
						// ...move it up.
						m_folderColumnsGrid.removeRow(i);
						renderRow(m_folderColumnsGrid, i-1, 
								m_folderColumnsListCount, m_folderColumnsListAll.get(i));
						setRowChecked(i-1);
						FolderColumn fc1 = m_folderColumnsListAll.get(i);
						FolderColumn fc2 = m_folderColumnsListAll.get(i-1);
						m_folderColumnsListAll.set(i, fc2);
						m_folderColumnsListAll.set(i-1, fc1);
						break;
					}
				}
			}
		}
	}
	

	/*
	 * Asynchronously runs the given instance of the dialog.
	 */
	private static void runDlgAsync(final FolderColumnsConfigDlg fccDlg, final BinderInfo bi) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				fccDlg.runDlgNow(bi);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	
	/*
	 * Synchronously runs the dialog.
	 */
	private void runDlgNow(final BinderInfo bi) {
		// Create a callback that will be called when we get the folder columns.
		AsyncCallback<VibeRpcResponse> rpcReadCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetFolderColumns(),
					bi.getBinderIdAsLong() );
			}// end onFailure()
	
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// We successfully retrieved the folder's columns.
				// Use it to populate the dialog.
				FolderColumnsRpcResponseData cData = ((FolderColumnsRpcResponseData)response.getResponseData());
				populateDlgAsync(bi.getBinderId(), cData.getFolderColumns(), cData.getFolderColumnsAll());
			} // end onSuccess()
		};
		
		// Issue an RPC request to get the personal preferences from the DB.
		GetFolderColumnsCmd cmd = new GetFolderColumnsCmd(bi.getBinderIdAsLong(), bi.getFolderType(), Boolean.TRUE);
		GwtClientHelper.executeCommand( cmd, rpcReadCallback );
	}
	

	/*
	 * Asynchronously populates the dialog.
	 */
	private void populateDlgAsync(final String binderId, final List<FolderColumn> folderColumnsList, final List<FolderColumn> folderColumnsListAll) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow(binderId, folderColumnsList, folderColumnsListAll);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}

	
	/*
	 * Asynchronously populates the dialog.
	 */
	private void populateDlgNow(String binderId, List<FolderColumn> folderColumnsList, List<FolderColumn> folderColumnsListAll) {
		// Setup the column data...
		m_binderId = binderId;
		m_folderColumnsList = folderColumnsList;
		m_folderColumnsListAll = folderColumnsListAll;
		m_folderColumnsListCount = ((null == m_folderColumnsListAll) ? 0 : m_folderColumnsListAll.size());
		for (FolderColumn fc : m_folderColumnsList) {
			for (FolderColumn fca : m_folderColumnsListAll) {
				if (fc.getColumnName().equals(fca.getColumnName())) {
					//Set the columns that are shown
					fca.setColumnIsShown(Boolean.TRUE);
					break;
				}
			}
		}

		// ...use it to create the dialog's content...
		createContentImpl();
		
		// ...and show the dialog.
		show(true);	// true -> Display the dialog centered on the screen.
	}

	
	/*
	 * Asynchronously loads the split point and performs some operation
	 * against the code.
	 */
	private static void doAsyncOperation(
			// Creation parameters.
			final FolderColumnsConfigDlgClient fccDlgClient,
			
			// initAndShow parameters,
			final FolderColumnsConfigDlg fccDlg,
			final BinderInfo bi) {
		GWT.runAsync(FolderColumnsConfigDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FolderColumnsDlg());
				if (null != fccDlgClient) {
					fccDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != fccDlgClient) {
					// Yes!  Create it and return it via the callback.
					FolderColumnsConfigDlg fccDlg = new FolderColumnsConfigDlg();
					fccDlgClient.onSuccess(fccDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(fccDlg, bi);
				}
			}
		});
	}
	
	
	/**
	 * Loads the FolderColumnsConfigDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param fccDlgClient
	 */
	public static void createAsync(FolderColumnsConfigDlgClient fccDlgClient) {
		doAsyncOperation(fccDlgClient, null, null);
	}
	
	
	/**
	 * Initializes and shows the dialog.
	 * 
	 * @param fccDlg
	 * @param bi
	 */
	public static void initAndShow(FolderColumnsConfigDlg fccDlg, BinderInfo bi) {
		doAsyncOperation(null, fccDlg, bi);
	}
}
