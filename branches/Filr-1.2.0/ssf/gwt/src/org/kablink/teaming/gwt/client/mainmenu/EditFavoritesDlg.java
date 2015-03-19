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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.UpdateFavoritesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Implements a dialog for editing favorites.
 *  
 * @author drfoster@novell.com
 */
public class EditFavoritesDlg extends DlgBox implements EditSuccessfulHandler {
	private final static String IDBASE		= "favorite_";	// Base ID for rows in the favorites Grid.
	private final static String IDTAIL_CBOX	= "_cb";		// Used for constructing the ID of a row's CheckBox.

	private Grid				m_favoritesGrid;		// Once displayed, the table of favorites being edited.
	private GwtTeamingMessages	m_messages;				// Access to the GWT UI messages.
	private int					m_favoritesGridCount;	// Count of rows  in m_favoritesGrid. 
	private int					m_favoritesListCount;	// Count of items in m_favoritesList.
	private List<FavoriteInfo>	m_favoritesList;		// List of FavoriteInfo's to be edited.

	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label, String title) {
			super(label);
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
			addStyleName("favoritesDlg_Label");
		}
		public DlgLabel(String label) {
			this(label, null);
		}
	}

	/*
	 * Inner class that wraps item's on the dialog's menu.
	 */
	private class DlgMenuItem extends MenuItem {
		public DlgMenuItem(String text, Command menuCommand) {
			super(text, menuCommand);
			addStyleName("favoritesDlg_MenuItem");
		}
	}
	
	/*
	 * Inner class that implements the delete command.
	 */
	private class DoDelete implements Command {
		@Override
		public void execute() {
			// If the table is empty...
			if (0 == m_favoritesGridCount) {
				// ...there's nothing to do.
				return;
			}
			
			// Scan the rows in the table.
			int rows = m_favoritesGrid.getRowCount();
			for (int i = (rows - 1); i >= 0; i -= 1) {
				// If this row checked...
				if (isRowChecked(i)) {
					// ...delete it.
					m_favoritesGrid.removeRow(i);
				}
			}
			m_favoritesGridCount = m_favoritesGrid.getRowCount();
			if (0 == m_favoritesGridCount) {
				m_favoritesGrid.insertRow(0);
				m_favoritesGrid.setWidget(0, 1, new DlgLabel(m_messages.mainMenuFavoritesNoFavorites()));
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
			if (0 == m_favoritesGridCount) {
				// ...there's nothing to do.
				return;
			}
			
			// Do we have some rows in the table without the bottom one
			// being checked?
			int rows = m_favoritesGrid.getRowCount();
			if ((0 < rows) && (!(isRowChecked(rows - 1)))) {
				// Yes!  Scan the rows.
				for (int i = (rows - 2); i >= 0; i -= 1) {
					// If this row checked...
					if (isRowChecked(i)) {
						// ...move it down.
						FavoriteInfo fi = getFavoriteById(getFavoriteIdFromRow(i));
						m_favoritesGrid.removeRow(i);
						renderRow(m_favoritesGrid, fi, i + 1, true);
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
			if (0 == m_favoritesGridCount) {
				// ...there's nothing to do.
				return;
			}
			
			// Do we have some rows in the table without the top one
			// being checked?
			int rows = m_favoritesGrid.getRowCount();
			if ((0 < rows) && (!(isRowChecked(0)))) {
				// Yes!  Scan the rows.
				for (int i = 1; i < rows; i += 1) {
					// If this row checked...
					if (isRowChecked(i)) {
						// ...move it up.
						FavoriteInfo fi = getFavoriteById(getFavoriteIdFromRow(i));
						m_favoritesGrid.removeRow(i);
						renderRow(m_favoritesGrid, fi, i - 1, true);
					}
				}
			}
		}
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param favoritesList
	 */
	public EditFavoritesDlg(boolean autoHide, boolean modal, int left, int top, List<FavoriteInfo> favoritesList) {
		// Initialize the superclass...
		super(autoHide, modal, left, top);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuFavoritesEditDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			favoritesList); 
	}
	

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public Panel createContent(Object callbackData) {
		// Store the List<FavoriteInfo> in class global data member
		// for use through the class.
		m_favoritesList = ((List<FavoriteInfo>) callbackData);
		m_favoritesListCount = m_favoritesList.size(); 

		// Create the dialog's menu.
		VerticalPanel vp = new VerticalPanel();
		MenuBar mb = new MenuBar();
		mb.addStyleName("favoritesDlg_MenuBar");
		mb.addItem(new DlgMenuItem(m_messages.mainMenuFavoritesDlgMoveUp(),   new DoMoveUp()));
		mb.addItem(new DlgMenuItem(m_messages.mainMenuFavoritesDlgMoveDown(), new DoMoveDown()));
		mb.addItem(new DlgMenuItem(m_messages.mainMenuFavoritesDlgDelete(),   new DoDelete()));
		vp.add(mb);

		// Render the rows in the dialog.
		m_favoritesGrid = new Grid(0, 2);
		m_favoritesGrid.addStyleName("favoritesDlg_Grid");
		m_favoritesGrid.setCellPadding(0);
		m_favoritesGrid.setCellSpacing(0);
		m_favoritesGridCount = m_favoritesListCount;
		if (0 == m_favoritesGridCount) {
			m_favoritesGrid.insertRow(0);
			m_favoritesGrid.setWidget(0, 1, new DlgLabel(m_messages.mainMenuFavoritesNoFavorites()));
		}
		else {
			for (int i = 0; i < m_favoritesListCount; i += 1) {
				renderRow(m_favoritesGrid, m_favoritesList.get(i), i, false);
			}
		}
		vp.add(m_favoritesGrid);
		
		setCancelEnabled(true);
		setOkEnabled(    true);
		
		// And return the Panel the with the dialog's contents.
		return vp;
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
	@SuppressWarnings({ "unchecked" })
	public boolean editSuccessful(Object callbackData) {
		UpdateFavoritesCmd cmd;
		
		// Update the favorites.
		setOkEnabled(false);
		List<FavoriteInfo> favoritesList = ((List<FavoriteInfo>) callbackData);
		cmd = new UpdateFavoritesCmd( favoritesList );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_UpdateFavorites());
				setOkEnabled(true);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				setOkEnabled(true);
				hide();
			}
		});
		
		// Return false so that the dialog remains open.  We'll close
		// it manually if the update completes successfully.
		return false;
	}

	
	/**
	 * Returns the edited List<FavoriteInfo>.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		ArrayList<FavoriteInfo> reply =  new ArrayList<FavoriteInfo>();
		if (0 < m_favoritesGridCount) {
			int rows = m_favoritesGrid.getRowCount();
			for (int i = 0; i < rows; i += 1) {
				String fiId = getFavoriteIdFromRow(i);
				FavoriteInfo fi = getFavoriteById(fiId);
				reply.add(fi);
			}
		}
		return reply;
	}

	/*
	 * Returns a row's FavoriteInfo ID.
	 */
	private String getFavoriteIdFromRow(int row) {
		String rowId = getRowId(row);
		return rowId.substring(rowId.indexOf('_') + 1);
	}
	
	/*
	 * Returns a FavoriteInfo base on its ID.
	 */
	private FavoriteInfo getFavoriteById(String fiId) {
		// Scan the List<FavoriteInfo>'s.
		for (int i = 0; i < m_favoritesListCount; i += 1) {
			// Is this the FavoriteInfo in question?
			FavoriteInfo fi = m_favoritesList.get(i);
			if (fi.getId().equals(fiId)) {
				// Yes!  Return it.
				return fi;
			}
		}
		
		// If we get here, we couldn't find a FavoriteInfo with the
		// requested ID.  Return null.
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
		return m_favoritesGrid.getRowFormatter().getElement(row).getId();
	}
	
	/*
	 * Returns true if a row's checkbox is checked. 
	 */
	private boolean isRowChecked(int row) {
		String rowId = getRowId(row);
		InputElement cb = Document.get().getElementById(rowId + IDTAIL_CBOX).getFirstChildElement().cast();
		return cb.isChecked();
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
	 * Renders a FavoriteInfo as a row in a Grid.
	 */
	private void renderRow(Grid grid, FavoriteInfo fi, int row, boolean checked) {
		grid.insertRow(row);
		
		String rowId = (IDBASE + fi.getId());
		grid.getRowFormatter().getElement(row).setId(rowId);
		
		CheckBox cb = new CheckBox();
		cb.addStyleName("favoritesDlg_Checkbox");
		cb.getElement().setId(rowId + IDTAIL_CBOX);
		cb.setValue(checked);
		grid.setWidget(row, 0, cb);
		grid.setWidget(row, 1, new DlgLabel(fi.getName(), fi.getHover()));
	}
}
