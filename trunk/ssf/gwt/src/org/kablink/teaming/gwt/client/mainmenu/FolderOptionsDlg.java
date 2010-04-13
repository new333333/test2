/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Implements a dialog for selecting a folder option.
 *  
 * @author drfoster@novell.com
 */
public class FolderOptionsDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private final static String IDBASE			= "folderOption_";	// Base ID for rows in the folder options Grid.
	private final static String IDTAIL_RADIO	= "_rb";			// Used for constructing the ID of a row's radio button.

	private Grid m_folderOptionsGrid;				// Once displayed, the table of folder options being displayed.
	private GwtTeamingMessages m_messages;			// Access to the GWT UI messages.
	private int m_folderOptionsGridCount;			// Count of rows  in m_folderOptionsGrid. 
	private int m_folderOptionsListCount;			// Count of items in m_folderOptionsList.
	private List<ToolbarItem> m_folderOptionsList;	// List of ToolbarItem's to be edited.

	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label) {
			super(label);
			addStyleName("folderOptionsDlg_Label");
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param folderOptionsList
	 */
	public FolderOptionsDlg(boolean autoHide, boolean modal, int left, int top, List<ToolbarItem> folderOptionsList) {
		// Initialize the superclass...
		super(autoHide, modal, left, top);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuFolderOptionsDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			folderOptionsList); 
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
		// Store the List<ToolbarItem> in class global data member
		// for use through the class.
		m_folderOptionsList = ((List<ToolbarItem>) callbackData);
		m_folderOptionsListCount = m_folderOptionsList.size(); 

		// Render the rows in the dialog.
		VerticalPanel vp = new VerticalPanel();
		m_folderOptionsGrid = new Grid(0, 2);
		m_folderOptionsGrid.addStyleName("folderOptionsDlg_Grid");
		m_folderOptionsGrid.setCellPadding(0);
		m_folderOptionsGrid.setCellSpacing(0);
		m_folderOptionsGridCount = m_folderOptionsListCount;
		if (0 == m_folderOptionsGridCount) {
			m_folderOptionsGrid.insertRow(0);
			m_folderOptionsGrid.setWidget(0, 1, new DlgLabel(m_messages.mainMenuFolderOptionsNoOptions()));
		}
		else {
			for (int i = 0; i < m_folderOptionsListCount; i += 1) {
				renderRow(m_folderOptionsGrid, m_folderOptionsList.get(i), i, false);
			}
		}
		vp.add(m_folderOptionsGrid);
		
		// And return the Panel the with the dialog's contents.
		return vp;
	}
	
	
	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	public boolean editCanceled() {
		// Simply return true to allow the dialog to close.
		return true;
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
	public boolean editSuccessful(Object callbackData) {
		// If we have a folder option selected...
		ToolbarItem tbi = ((ToolbarItem) callbackData);
		String url = tbi.getUrl();
		if (GwtClientHelper.hasString(url)) {
			// ...put it into effect.
			jsLoadUrlInContentFrame(url);
		}
		
		// Return true to close the dialog.
		return true;
	}

	
	/**
	 * Returns the edited List<ToolbarItem>.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Are there any rows in the grid?
		if (0 < m_folderOptionsGridCount) {
			// Yes!  Scan them.
			int rows = m_folderOptionsGrid.getRowCount();
			for (int i = 0; i < rows; i += 1) {
				// Is this row checked?
				if (isRowChecked(i)) {
					// Yes!  Return its ToolbarItem.
					String foId = getFolderOptionIdFromRow(i);
					ToolbarItem tbi = getFolderOptionById(foId);
					return tbi;
				}
			}
		}
		
		// If we get here, there wasn't anything selected.  Return an
		// empty toolbar item which will flag the dialog to simply
		// close.
		return new ToolbarItem();
	}

	/*
	 * Returns a row's ToolbarItem ID.
	 */
	private String getFolderOptionIdFromRow(int row) {
		String rowId = getRowId(row);
		return rowId.substring(rowId.indexOf('_') + 1);
	}
	
	/*
	 * Returns a ToolbarItem base on its ID.
	 */
	private ToolbarItem getFolderOptionById(String fiId) {
		// Scan the List<ToolbarItem>'s.
		for (int i = 0; i < m_folderOptionsListCount; i += 1) {
			// Is this the ToolbarItem in question?
			ToolbarItem tbi = m_folderOptionsList.get(i);
			if (tbi.getName().equals(fiId)) {
				// Yes!  Return it.
				return tbi;
			}
		}
		
		// If we get here, we couldn't find a ToolbarItem with the
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
		return m_folderOptionsGrid.getRowFormatter().getElement(row).getId();
	}
	
	/*
	 * Returns true if a row's checkbox is checked. 
	 */
	private boolean isRowChecked(int row) {
		String rowId = getRowId(row);
		InputElement rb = Document.get().getElementById(rowId + IDTAIL_RADIO).getFirstChildElement().cast();
		return rb.isChecked();
	}
	
	/*
	 * Loads a URL into the GWT UI's content frame.
	 */
	private native void jsLoadUrlInContentFrame(String url) /*-{
		window.top.gwtContentIframe.location.href = url;
	}-*/;
	
	/*
	 * Renders a ToolbarItem as a row in a Grid.
	 */
	private void renderRow(Grid grid, ToolbarItem tbi, int row, boolean checked) {
		grid.insertRow(row);
		
		String rowId = (IDBASE + tbi.getName());
		grid.getRowFormatter().getElement(row).setId(rowId);
		
		RadioButton cb = new RadioButton("folderOptions");
		cb.addStyleName("folderOptionsDlg_Radio");
		cb.getElement().setId(rowId + IDTAIL_RADIO);
		cb.setValue(checked);
		grid.setWidget(row, 0, cb);
		grid.setWidget(row, 1, new DlgLabel(tbi.getName()));
	}
}
