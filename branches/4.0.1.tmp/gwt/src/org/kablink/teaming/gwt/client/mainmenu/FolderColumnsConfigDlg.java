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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
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
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Implements a dialog for configuring folder columns.
 *  
 * @author phurley@novell.com
 * @owner  drfoster@novell.com
 */
public class FolderColumnsConfigDlg extends DlgBox implements EditSuccessfulHandler {
	private BinderInfo						m_binderInfo;				// The binder the folder columns dialog is running against.
	private boolean							m_folderAdmin;				// true -> Logged in user is a folder admin.  false -> They're not.
	private Button							m_folderDefaultBtn;			// Restore default settings
	private CheckBox						m_folderDefaultCheckBox;	// Set the folder default columns.
	private FlexCellFormatter				m_folderColumnsGridCF;		//
	private FlexTable						m_folderColumnsGrid;		// Once displayed, the table of folder columns.
	private GwtTeamingMainMenuImageBundle	m_images;					//
	private GwtTeamingMessages				m_messages;					// Access to the GWT UI messages.
	private int								m_folderColumnsListCount;	// Count of items in m_folderColumnsList.
	private List<FolderColumn>				m_folderColumnsList;		// List of selected folder column items.
	private List<FolderColumn>				m_folderColumnsListAll;		// List of all folder column items.
	private RowFormatter					m_folderColumnsGridRF;		//
	private ScrollPanel						m_sp;						//
	private VerticalPanel					m_vp;						//
	private VibeMenuItem					m_moveBottom;				//
	private VibeMenuItem					m_moveDown;					//
	private VibeMenuItem					m_moveTop;					//
	private VibeMenuItem					m_moveUp;					//
	
	private final static String IDBASE				= "folderColumn_";					// Base ID for rows in the folder columns Grid.
	private final static String IDTAIL_CHECKBOX		= "_cb";							// Used for constructing the ID of a row's checkbox.
	private final static String IDTAIL_MOVE_BOTTOM	= "bottom";							// Used for constructing the ID of the move to bottom menu item.
	private final static String IDTAIL_MOVE_DOWN	= "down";							// Used for constructing the ID of the move down      menu item.
	private final static String IDTAIL_MOVE_TOP		= "top";							// Used for constructing the ID of the move to top    menu item.
	private final static String IDTAIL_MOVE_UP		= "up";								// Used for constructing the ID of the move up        menu item.
	private final static String IDTAIL_RADIO		= "_rb";							// Used for constructing the ID of a row's radio button.
	private final static String IDTAIL_TEXTBOX		= "_tb";							// Used for constructing the ID of a row's text box.
	private final static String ID_MOVE_BOTTOM		= (IDBASE + IDTAIL_MOVE_BOTTOM);	//
	private final static String ID_MOVE_DOWN		= (IDBASE + IDTAIL_MOVE_DOWN);		//
	private final static String ID_MOVE_TOP			= (IDBASE + IDTAIL_MOVE_TOP);		//
	private final static String ID_MOVE_UP			= (IDBASE + IDTAIL_MOVE_UP);		//
	
	// The following define the column indexes into the FlexTable used
	// to edit the columns.
	private final static int COL_SELECT_RB		= 0;
	private final static int COL_COLUMN			= 1;
	private final static int COL_CUSTOM_LABEL	= 2;
	private final static int COL_SHOW_CB		= 3;

	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends InlineLabel {
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param title
		 */
		public DlgLabel(String label, String title) {
			super(label);
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
			addStyleName("folderColumnsDlg_Label");
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null);
		}
		
		/**
		 * Constructor method.
		 */
		public DlgLabel() {
			// Always use the initial form of the method.
			this(" ", null);
		}
	}

	/*
	 * Inner class that implements the move to bottom command.
	 */
	private class DoMoveBottom implements Command {
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
			if ((2 < rows) && (!(isRowChecked(rows - 1)))) {
				// Yes!  Scan the rows.
				getDataFromDlg();	//Make sure all of the changed settings are captured first
				int checkedRow = (-1);
				for (int rowIndex = 0; rowIndex < rows; rowIndex += 1) {
					// Is this row checked?
					if (isRowChecked(rowIndex)) {
						// Yes!  Track it and break out of the loop.
						checkedRow = rowIndex;
						break;
					}
				}
				
				// Do we have the index of a checked row?
				if ((-1) == checkedRow) {
					// No!  Bail, we can't move to bottom if nothing
					// is checked.
					return;
				}
					
				// Scan the rows from the checked on down...
				for (int rowIndex = checkedRow; rowIndex < (rows - 1); rowIndex += 1) {
					// ...moving them down...
					int colIndex = (rowIndex - 1);
					renderRow( rowIndex,      m_folderColumnsListAll.get(colIndex + 1));
					renderRow((rowIndex + 1), m_folderColumnsListAll.get(colIndex    ));
					
					FolderColumn fc1 = m_folderColumnsListAll.get(colIndex    );
					FolderColumn fc2 = m_folderColumnsListAll.get(colIndex + 1);
					m_folderColumnsListAll.set( colIndex,      fc2);
					m_folderColumnsListAll.set((colIndex + 1), fc1);
				}

				// ...and then update the view.
				checkedRow = (rows - 1);
				setRowChecked(checkedRow);
				resetRowStyles();
				validateMoves(checkedRow);
				m_sp.ensureVisible(m_folderColumnsGrid.getWidget(checkedRow, 0));
			}
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
			if ((2 < rows) && (!(isRowChecked(rows - 1)))) {
				// Yes!  Scan the rows.
				getDataFromDlg();	//Make sure all of the changed settings are captured first.
				int newCheckIndex       = (-1);
				int scrollIntoViewIndex = (-1);
				for (int rowIndex = (rows - 1); rowIndex > 0; rowIndex -= 1) {
					// If this row checked...
					if (isRowChecked(rowIndex)) {
						// ...track it...
						if ((-1) == scrollIntoViewIndex) {
							scrollIntoViewIndex = rowIndex;
						}
						
						// ...and move it down.
						int colIndex = (rowIndex - 1);
						renderRow( rowIndex,      m_folderColumnsListAll.get(colIndex + 1));
						renderRow((rowIndex + 1), m_folderColumnsListAll.get(colIndex    ));
						
						newCheckIndex = (rowIndex + 1);
						setRowChecked(newCheckIndex);
						
						FolderColumn fc1 = m_folderColumnsListAll.get(colIndex    );
						FolderColumn fc2 = m_folderColumnsListAll.get(colIndex + 1);
						m_folderColumnsListAll.set( colIndex,      fc2);
						m_folderColumnsListAll.set((colIndex + 1), fc1);
						
						break;
					}
				}
				
				resetRowStyles();
				if ((-1) != newCheckIndex) {
					validateMoves(newCheckIndex);
				}
				
				if ((-1) != scrollIntoViewIndex) {
					m_sp.ensureVisible(m_folderColumnsGrid.getWidget(scrollIntoViewIndex, 0));
				}
			}
		}
	}
	
	/*
	 * Inner class that implements the move to top command.
	 */
	private class DoMoveTop implements Command {
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
			if ((2 < rows) && (!(isRowChecked(1)))) {
				// Yes!  Scan the rows.
				getDataFromDlg();	//Make sure all of the changed settings are captured first
				
				int checkedRow = (-1);
				for (int rowIndex = 0; rowIndex < rows; rowIndex += 1) {
					// Is this row checked?
					if (isRowChecked(rowIndex)) {
						// Yes!  Track it and break out of the loop.
						checkedRow = rowIndex;
						break;
					}
				}
				
				// Do we have the index of a checked row?
				if ((-1) == checkedRow) {
					// No!  Bail, we can't move to bottom if nothing
					// is checked.
					return;
				}

				// Scan the rows from the checked one up...
				for (int rowIndex = checkedRow; rowIndex > 1; rowIndex -= 1) {
					// ...moving them up....
					int colIndex = (rowIndex - 1);
					renderRow((rowIndex - 1), m_folderColumnsListAll.get(colIndex    ));
					renderRow( rowIndex,      m_folderColumnsListAll.get(colIndex - 1));
					
					FolderColumn fc1 = m_folderColumnsListAll.get(colIndex    );
					FolderColumn fc2 = m_folderColumnsListAll.get(colIndex - 1);
					m_folderColumnsListAll.set( colIndex,      fc2);
					m_folderColumnsListAll.set((colIndex - 1), fc1);
				}
				
				// ...and then update the view.
				setRowChecked(1);
				resetRowStyles();
				validateMoves(1);
				m_sp.ensureVisible(m_folderColumnsGrid.getWidget(1, 0));
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
			if ((2 < rows) && (!(isRowChecked(1)))) {
				// Yes!  Scan the rows.
				getDataFromDlg();	//Make sure all of the changed settings are captured first
				int newCheckIndex       = (-1);
				int scrollIntoViewIndex = (-1);
				for (int rowIndex = 2; rowIndex < rows; rowIndex += 1) {
					// If this row checked...
					if (isRowChecked(rowIndex)) {
						// ...track it...
						if ((-1) == scrollIntoViewIndex) {
							scrollIntoViewIndex = rowIndex;
						}
						
						// ...and move it up.
						int colIndex = (rowIndex - 1);
						renderRow((rowIndex - 1), m_folderColumnsListAll.get(colIndex    ));
						renderRow( rowIndex,      m_folderColumnsListAll.get(colIndex - 1));
						
						newCheckIndex = (rowIndex - 1);
						setRowChecked(newCheckIndex);
						
						FolderColumn fc1 = m_folderColumnsListAll.get(colIndex    );
						FolderColumn fc2 = m_folderColumnsListAll.get(colIndex - 1);
						m_folderColumnsListAll.set( colIndex,      fc2);
						m_folderColumnsListAll.set((colIndex - 1), fc1);
						
						break;
					}
				}
				
				resetRowStyles();
				if ((-1) != newCheckIndex) {
					validateMoves(newCheckIndex);
				}
				
				if ((-1) != scrollIntoViewIndex) {
					m_sp.ensureVisible(m_folderColumnsGrid.getWidget(scrollIntoViewIndex, 0));
				}
			}
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
		m_images   = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the main dialog panels.
		createAllDlgContent(
			m_messages.folderColumnsDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// null -> Data passed via global data members.
	}
	
	/*
	 * Adds a section header row to the folder options grid.
	 */
	private void addHeaderRow() {
		m_folderColumnsGridRF.setStyleName(0, "folderColumnsDlg_SectionHeaderRow");
		
		Label il = new DlgLabel();
		il.getElement().setInnerHTML("&nbsp;");
		il.addStyleName("folderColumnsDlg_SectionHeaderCell");
		m_folderColumnsGrid.setWidget(0, COL_SELECT_RB, il);
		
		il = new DlgLabel(m_messages.folderColumnsDlgColumn());
		il.addStyleName("folderColumnsDlg_SectionHeaderCell");
		m_folderColumnsGrid.setWidget(0, COL_COLUMN, il);
		
		il = new DlgLabel(m_messages.folderColumnsDlgCustomLabel());
		il.addStyleName("folderColumnsDlg_SectionHeaderCell");
		m_folderColumnsGrid.setWidget(0, COL_CUSTOM_LABEL, il);
		
		il = new DlgLabel(m_messages.folderColumnsDlgShow());
		il.addStyleName("folderColumnsDlg_SectionHeaderCell");
		m_folderColumnsGrid.setWidget(0, COL_SHOW_CB, il);
	}

	/*
	 * Adds the menu bar to the dialog.
	 */
	private void addMenu() {
		MenuBar mb = new VibeMenuBar();
		mb.setStyleName("folderColumnsDlg_MenuBar");
		
		VibeMenuItem order = new VibeMenuItem(m_messages.folderColumnsDlgOrder(), ((Command) null), "folderColumnsDlg_MenuItem_Order folderColumnsDlg_MenuItem");
		order.setEnabled(false);
		mb.addItem(order);

		FlowPanel fp = new FlowPanel();
		Image i = GwtClientHelper.buildImage(m_images.arrowUpDisabled().getSafeUri().asString());
		i.addStyleName("folderColumnsDlg_MenuImg");
		i.setTitle(m_messages.folderColumnsDlgMoveUp());
		Element iE = i.getElement();
		iE.setAttribute("align", "absmiddle");
		iE.setId(ID_MOVE_UP);
		fp.add(i);
		m_moveUp = new VibeMenuItem(fp.getElement().getInnerHTML(), true, new DoMoveUp(), "folderColumnsDlg_MenuItem");
		mb.addItem(m_moveUp);
		
		fp = new FlowPanel();
		i = GwtClientHelper.buildImage(m_images.arrowDownDisabled().getSafeUri().asString());
		i.addStyleName("folderColumnsDlg_MenuImg");
		i.setTitle(m_messages.folderColumnsDlgMoveDown());
		iE = i.getElement();
		iE.setAttribute("align", "absmiddle");
		iE.setId(ID_MOVE_DOWN);
		fp.add(i);
		m_moveDown = new VibeMenuItem(fp.getElement().getInnerHTML(), true, new DoMoveDown(), "folderColumnsDlg_MenuItem");
		mb.addItem(m_moveDown);
		
		fp = new FlowPanel();
		i = GwtClientHelper.buildImage(m_images.arrowTopDisabled().getSafeUri().asString());
		i.addStyleName("folderColumnsDlg_MenuImg padding10L");
		i.setTitle(m_messages.folderColumnsDlgMoveTop());
		iE = i.getElement();
		iE.setAttribute("align", "absmiddle");
		iE.setId(ID_MOVE_TOP);
		fp.add(i);
		m_moveTop= new VibeMenuItem(fp.getElement().getInnerHTML(), true, new DoMoveTop(), "folderColumnsDlg_MenuItem");
		mb.addItem(m_moveTop);
		
		fp = new FlowPanel();
		i = GwtClientHelper.buildImage(m_images.arrowBottomDisabled().getSafeUri().asString());
		i.addStyleName("folderColumnsDlg_MenuImg");
		i.setTitle(m_messages.folderColumnsDlgMoveTop());
		iE = i.getElement();
		iE.setAttribute("align", "absmiddle");
		iE.setId(ID_MOVE_BOTTOM);
		fp.add(i);
		m_moveBottom= new VibeMenuItem(fp.getElement().getInnerHTML(), true, new DoMoveBottom(), "folderColumnsDlg_MenuItem");
		mb.addItem(m_moveBottom);
		
		m_vp.add(mb);
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
		m_vp = new VibeVerticalPanel(null, null);
		m_vp.setStyleName("teamingDlgBoxContent");
		
		addMenu();
		
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
		if (0 < m_folderColumnsListCount) {
			// Yes!  Create a grid to contain them... 
			m_folderColumnsGrid = new VibeFlexTable();
			m_folderColumnsGrid.addStyleName("folderColumnsDlg_Grid");
			m_folderColumnsGrid.setCellPadding(0);
			m_folderColumnsGrid.setCellSpacing(0);
			m_folderColumnsGridRF = m_folderColumnsGrid.getRowFormatter();
			m_folderColumnsGridCF = m_folderColumnsGrid.getFlexCellFormatter();
			
			// ...render the folder columns into the panel...
			addHeaderRow();
			for (int i = 0; i < m_folderColumnsListCount; i += 1) {
				renderRow(m_folderColumnsGrid.getRowCount(), m_folderColumnsListAll.get(i));
			}
			resetRowStyles();

			// ...connect everything together...
			m_sp.add(m_folderColumnsGrid);
			
			// ...and adjust the scroll panel's styles as appropriate.
			if (5 < m_folderColumnsListCount)
			     m_sp.addStyleName(   "folderColumnsDlg_ScrollLimit");
			else m_sp.removeStyleName("folderColumnsDlg_ScrollLimit");
			
			// If the user's a folder administrator...
			FlexTable ft = new VibeFlexTable();
			int folderDefaultRow;
			if (m_folderAdmin) {
				// ...add the option to set the folder default.
				m_folderDefaultCheckBox = new CheckBox();
				ft.setWidget(0, 0, m_folderDefaultCheckBox);
				ft.setText(0, 1, m_messages.folderColumnsDlgSetAsDefault());
				folderDefaultRow = 1;
			}
			else {
				folderDefaultRow = 0;
			}
			
			// Add a button to restore factory defaults.
			m_folderDefaultBtn = new Button(m_messages.folderColumnsDlgRestoreDefaults());
			m_folderDefaultBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					setFactoryDefaultsAsync();
				}
			});
			
			ft.getFlexCellFormatter().setColSpan(folderDefaultRow, 0, 2);
			ft.setWidget(folderDefaultRow, 0, m_folderDefaultBtn);
			ft.addStyleName("folderColumnsDlg_OptionsGrid");
			m_vp.add(ft);			
		}
		
		else {
			// No, there weren't any folder options to display in the
			// dialog!  Put a simple no available options message.
			m_vp.add(new DlgLabel(m_messages.folderColumnsDlgNoOptions()));
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
		Boolean isDefault = ((null != m_folderDefaultCheckBox) && m_folderDefaultCheckBox.getValue());
		SaveFolderColumnsCmd cmd = new SaveFolderColumnsCmd(m_binderInfo.getBinderId(), fcList, isDefault);
		setOkEnabled(false);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveFolderColumns());
				setOkEnabled(true);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				GetBinderPermalinkCmd cmd = new GetBinderPermalinkCmd(m_binderInfo.getBinderId());
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
							m_binderInfo.getBinderId());
						setOkEnabled(true);
					}
					
					@Override
					public void onSuccess(VibeRpcResponse response) {
						StringRpcResponseData responseData = (StringRpcResponseData) response.getResponseData();
						String binderUrl = responseData.getStringValue();
						OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(m_binderInfo, binderUrl, Instigator.FORCE_FULL_RELOAD);
						if (GwtClientHelper.validateOSBI(osbInfo)) {
							GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
						}
						
						setOkEnabled(true);
						hide();
					}
				});
			}
		});

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
		// Are there any data rows in the grid?
		int rows = m_folderColumnsGrid.getRowCount();
		if (1 < rows) {
			// Yes!  Scan and update them.
			for (int i = 1; i < rows; i += 1) {
				String       rowId = getRowId(i);
				FolderColumn fc    = getFolderColumnByName(getFolderColumnNameFromRow(i));
				InputElement iE    = Document.get().getElementById(rowId + IDTAIL_CHECKBOX).getFirstChild().cast();
				fc.setColumnShown(iE.isChecked());
				
				iE = Document.get().getElementById(rowId + IDTAIL_TEXTBOX).cast();
				fc.setColumnCustomTitle(iE.getValue());
			}
		}
		
		// Return the updated list of columns.
		return m_folderColumnsListAll;
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
			}
			
			else {
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
	
	/*
	 * Returns a row's FolderColumn column name.
	 */
	private String getFolderColumnNameFromRow(int row) {
		String rowId = getRowId(row);
		return rowId.substring(rowId.indexOf('_') + 1);
	}
	
	/*
	 * Returns the ID of a row.
	 */
	private String getRowId(int row) {
		return m_folderColumnsGridRF.getElement(row).getId();
	}
	
	/*
	 * Returns true if a row's checkbox is checked. 
	 */
	private boolean isRowChecked(int row) {
		// If this is the header row...
		if (0 == row) {
			// ...it can't be checked.
			return false;
		}
		
		String       rowId = getRowId(row);
		InputElement rb    = Document.get().getElementById(rowId + IDTAIL_RADIO).getFirstChild().cast();
		return rb.isChecked();
	}
	
    /**
     * Called after the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingEnded() method.
     */
	@Override
    protected void okBtnProcessingEnded() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
    /**
     * Called before the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingStarted() method.
     */
	@Override
    protected void okBtnProcessingStarted() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
	/*
	 * Check a row's radio button. 
	 */
	private void setRowChecked(int row) {
		// If this is the header row...
		if (0 == row) {
			// ...we can't set its radio button.
			return;
		}
		
		String       rowId = getRowId(row);
		InputElement rb    = Document.get().getElementById(rowId + IDTAIL_RADIO).getFirstChild().cast();
		if (null != rb) {
			rb.setChecked(true);
		}
	}
	
	/*
	 * Renders a folder column as a row in a Grid.
	 */
	private void renderRow(final int row, FolderColumn fci) {
		// Create the row...
		String rowIdSuffix = fci.getColumnName();
		if (fci.getColumnDefId() != null && !fci.getColumnDefId().equals("")) {
			rowIdSuffix = fci.getColumnDefId()+"."+fci.getColumnEleName();
		}
		String rowId = (IDBASE + rowIdSuffix);
		if (row >= m_folderColumnsGrid.getRowCount()) {
			m_folderColumnsGrid.insertRow(row);
		}
		m_folderColumnsGridRF.getElement(row).setId(rowId);
		
		// ...create the radio button for moving the row..
		RadioButton rb = new RadioButton("MoveButton");
		rb.getElement().setId(rowId + IDTAIL_RADIO);
		rb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				validateMoves(row);
			}
		});
		m_folderColumnsGrid.setWidget(     row, COL_SELECT_RB, rb);
		m_folderColumnsGridCF.addStyleName(row, COL_SELECT_RB, "folderColumnsDlg_RowCell folderColumnsDlg_RowCellCB");
		m_folderColumnsGridCF.setAlignment(row, COL_SELECT_RB, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		
		// ...create the column title...
		String txt = fci.getColumnDefaultTitle();
		if (!(GwtClientHelper.hasString(txt))) {
			txt = fci.getColumnEleName();
		}
		m_folderColumnsGrid.setWidget(     row, COL_COLUMN, new DlgLabel(txt));
		m_folderColumnsGridCF.addStyleName(row, COL_COLUMN, "folderColumnsDlg_RowCell");
		
		// ...create the custom label input widget...
		TextBox tb = new TextBox();
		tb.setName("CustomName");
		tb.getElement().setId(rowId + IDTAIL_TEXTBOX);
		if ((GwtClientHelper.hasString(fci.getColumnCustomTitle()))) {
			tb.setValue(fci.getColumnCustomTitle());
		}
		m_folderColumnsGrid.setWidget(     row, COL_CUSTOM_LABEL, tb);
		m_folderColumnsGridCF.addStyleName(row, COL_CUSTOM_LABEL, "folderColumnsDlg_RowCell");
		
		// ...and create the checkbox to select the column for viewing.
		CheckBox cb = new CheckBox();
		cb.setName("ColumnSelected_" + rowIdSuffix);
		cb.addStyleName("folderColumnsDlg_CheckBox");
		cb.getElement().setId(rowId + IDTAIL_CHECKBOX);
		cb.setValue(fci.isColumnShown());
		m_folderColumnsGrid.setWidget(     row, COL_SHOW_CB, cb);
		m_folderColumnsGridCF.addStyleName(row, COL_SHOW_CB, "folderColumnsDlg_RowCell folderColumnsDlg_RowCellCB");
	}

	/*
	 * Walks the data rows in the grid and sets the rows for the
	 * odd/even styling.
	 */
	private void resetRowStyles() {
		// Scan the data rows in the grid...
		int rows = m_folderColumnsGrid.getRowCount(); 
		for (int i = 1; i < rows; i += 1) {
			// ...adding/removing the styles from each as appropriate.
			String addStyle;
			String removeStyle;
			if (0 == (i % 2)) {
				addStyle    = "folderColumnsDlg_Row-even";
				removeStyle = "folderColumnsDlg_Row-odd";
			}
			else {
				addStyle    = "folderColumnsDlg_Row-odd";
				removeStyle = "folderColumnsDlg_Row-even";
			}
			m_folderColumnsGridRF.addStyleName(   i, addStyle   );
			m_folderColumnsGridRF.removeStyleName(i, removeStyle);
		}
	}

	/*
	 * Asynchronously populates the dialog.
	 */
	private void populateDlgAsync(final BinderInfo binderInfo, final List<FolderColumn> folderColumnsList, final List<FolderColumn> folderColumnsListAll) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow(binderInfo, folderColumnsList, folderColumnsListAll);
			}
		});
	}

	/*
	 * Asynchronously populates the dialog.
	 */
	private void populateDlgNow(BinderInfo binderInfo, List<FolderColumn> folderColumnsList, List<FolderColumn> folderColumnsListAll) {
		// Setup the column data...
		m_binderInfo = binderInfo;
		m_folderColumnsList = folderColumnsList;
		m_folderColumnsListAll = folderColumnsListAll;
		m_folderColumnsListCount = ((null == m_folderColumnsListAll) ? 0 : m_folderColumnsListAll.size());
		for (FolderColumn fc : m_folderColumnsList) {
			for (FolderColumn fca : m_folderColumnsListAll) {
				if (fc.getColumnName().equals(fca.getColumnName())) {
					//Set the columns that are shown
					fca.setColumnShown(Boolean.TRUE);
					break;
				}
			}
		}

		// ...use it to create the dialog's content...
		createContentImpl();
		
		// ...and show the dialog.
		setCancelEnabled(true);
		setOkEnabled(    true);
		show(            true);	// true -> Display the dialog centered on the screen.
	}

	/*
	 * Asynchronously launches the binder being viewed in the content
	 * panel again.
	 */
	private void relaunchViewAsync(final boolean hideOnRelaunch) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				relaunchViewNow(hideOnRelaunch);
			}
		});
	}
	
	/*
	 * Synchronously launches the binder being viewed in the content
	 * panel again.
	 */
	private void relaunchViewNow(final boolean hideOnRelaunch) {
		GetBinderPermalinkCmd cmd = new GetBinderPermalinkCmd(m_binderInfo.getBinderId());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					m_binderInfo.getBinderId());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				StringRpcResponseData responseData = (StringRpcResponseData) response.getResponseData();
				String binderUrl = responseData.getStringValue();
				OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(m_binderInfo, binderUrl, Instigator.FORCE_FULL_RELOAD);
				if (GwtClientHelper.validateOSBI(osbInfo)) {
					GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
				}
				
				if (hideOnRelaunch) {
					hide();
				}
			}
		});
	}
	
	/*
	 * Asynchronously runs the given instance of the dialog.
	 */
	private static void runDlgAsync(final FolderColumnsConfigDlg fccDlg, final BinderInfo bi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				fccDlg.runDlgNow(bi);
			}
		});
	}
	
	/*
	 * Synchronously runs the dialog.
	 */
	private void runDlgNow(final BinderInfo bi) {
		// Issue an RPC request to get the personal preferences from the DB.
		GetFolderColumnsCmd cmd = new GetFolderColumnsCmd(bi, Boolean.TRUE);
		GwtClientHelper.executeCommand(
			cmd,
			new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolderColumns(),
						bi.getBinderIdAsLong() );
				}
		
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// We successfully retrieved the folder's columns.
					// Use it to populate the dialog.
					FolderColumnsRpcResponseData cData = ((FolderColumnsRpcResponseData)response.getResponseData());
					m_folderAdmin = cData.isFolderAdmin(); 
					populateDlgAsync(bi, cData.getFolderColumns(), cData.getFolderColumnsAll());
				}
			});
	}
	
	/*
	 * Asynchronously restores the factory default column settings.
	 */
	private void setFactoryDefaultsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				setFactoryDefaultsNow();
			}
		});
	}
	
	/*
	 * Synchronously restores the factory default column settings.
	 */
	private void setFactoryDefaultsNow() {
		// Save the folder columns...
		SaveFolderColumnsCmd cmd = new SaveFolderColumnsCmd(m_binderInfo.getBinderId(), new ArrayList<FolderColumn>(), new Boolean(Boolean.TRUE));
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveFolderColumns());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...and launch the view whose columns were being
				// ...edited.
				relaunchViewAsync(true);
			}
		});
	}
	
	/*
	 * Enables/disables one of the move buttons on the menu.
	 */
	private static void setMoveEnabled(VibeMenuItem mi, String id, boolean enabled, ImageResource imgEnabled, ImageResource imgDisabled) {
		Element e = DOM.getElementById(id);
		if (enabled) {
			e.addClassName("folderColumnsDlg_MoveEnabled");
			e.setAttribute("src", imgEnabled.getSafeUri().asString());
		}
		else {
			e.removeClassName("folderColumnsDlg_MoveEnabled");
			e.setAttribute("src", imgDisabled.getSafeUri().asString());
		}
		mi.setEnabled(enabled);
	}

	/*
	 * Called to validate the move buttons.
	 */
	private void validateMoves(int rowSelected) {
		boolean canMoveBottom = (rowSelected < m_folderColumnsListCount);
		boolean canMoveTop    = (1 < rowSelected);
		boolean canMoveDown   = (rowSelected < m_folderColumnsListCount);
		boolean canMoveUp     = (1 < rowSelected);
		
		setMoveEnabled(m_moveBottom, ID_MOVE_BOTTOM, canMoveBottom, m_images.arrowBottom(), m_images.arrowBottomDisabled());
		setMoveEnabled(m_moveDown,   ID_MOVE_DOWN,   canMoveDown,   m_images.arrowDown(),   m_images.arrowDownDisabled()  );
		setMoveEnabled(m_moveTop,    ID_MOVE_TOP,    canMoveTop,    m_images.arrowTop(),    m_images.arrowTopDisabled()   );
		setMoveEnabled(m_moveUp,     ID_MOVE_UP,     canMoveUp,     m_images.arrowUp(),     m_images.arrowUpDisabled()    );
	}


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the dialog and perform some operation on it.                  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the dialog asynchronously
	 * after it loads. 
	 */
	public interface FolderColumnsConfigDlgClient {
		void onSuccess(FolderColumnsConfigDlg fccDlg);
		void onUnavailable();
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
