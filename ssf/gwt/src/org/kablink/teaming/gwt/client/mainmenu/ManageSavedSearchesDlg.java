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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.SearchSavedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.RemoveSavedSearchCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveSearchCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements a dialog for managing saved searches.
 *  
 * @author drfoster@novell.com
 */
public class ManageSavedSearchesDlg extends DlgBox {
	private final static String IDBASE				= "savedSearch_";		// Base ID for rows in the Grid.
	private final static int	MAX_NAME_LENGTH		= Integer.MAX_VALUE;	// Is there a maximum?
	private final static String SECTION_HEADER_ID	= "sectionHeader";		// The ID used for section headers in the dialog.
	private final static int	VISIBLE_NAME_LENGTH	= 20;					// Any better guesses?

	private Grid							m_ssGrid;		// Once displayed, the Grid with the dialog's contents.
	private GwtTeamingMainMenuImageBundle	m_images;		// Access to the GWT main menu images.
	private GwtTeamingMessages				m_messages;		// Access to the GWT UI messages.
	private int								m_ssListCount;	// Count of SavedSearchInfo's in m_ssList.
	private List<SavedSearchInfo>			m_ssList;		// The saved searches currently defined.
	private String							m_searchTabId;	// If were on a search results page (i.e., the search can be saved), the tab ID of the search that can be saved.

	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label) {
			super(label);
			addStyleName("manageSavedSearchesDlg_Label");
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageSavedSearchesDlg(boolean autoHide, boolean modal, int left, int top, List<SavedSearchInfo> ssList, String searchTabId) {
		// Initialize the superclass...
		super(autoHide, modal, left, top, DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_images = GwtTeaming.getMainMenuImageBundle();
		m_searchTabId = searchTabId;
		m_ssList = new ArrayList<SavedSearchInfo>();
		for (SavedSearchInfo ssi:  ssList) {
			m_ssList.add(ssi);
		}
		m_ssListCount = m_ssList.size();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuManageSavedSearchesDlgHeader(),
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);							// Data accessed via global data members. 
	}
	
	/*
	 * Adds a section header row to the Grid.
	 */
	private void addHeaderRow(Grid grid, int row, String headerText) {
		DlgLabel header = new DlgLabel(headerText);
		header.addStyleName("manageSavedSearchesDlg_SectionHeader");

		grid.insertRow(row);
		Element e = grid.getRowFormatter().getElement(row); 
		e.setId(SECTION_HEADER_ID);
		e.addClassName("manageSavedSearchesDlg_SectionHeaderRow");
		e = grid.getCellFormatter().getElement(row, 0);
		e.addClassName("manageSavedSearchesDlg_SectionHeaderCell");
		grid.setWidget(row, 0, header);
		GwtClientHelper.setGridColSpan(grid, row, 0, 2);
	}

	/*
	 * Adds a SavedSearchInfo object to a List<SavedSearchInfo>
	 * accounting for sorting.
	 */
	private static void addSSIToList(List<SavedSearchInfo> ssiList, SavedSearchInfo ssi) {
		String s2 = ssi.getName();
		int ssiCount = ssiList.size();
		for (int i = 0; i < ssiCount; i += 1) {
			String s1 = ssiList.get(i).getName();
			int cmp = GwtClientHelper.safeSColatedCompare(s1, s2);
			if (0 < cmp) {
				ssiList.add(i, ssi);
				return;
			}
		}
		ssiList.add(ssi);
	}

	/*
	 * Returns true if a string contains punctuation characters and
	 * false otherwise.
	 * 
	 * Implementation logic is based on that in the
	 * ss_saveSearchQuery() method in ss_search.js.
	 */
	private native boolean containsInvalidData(String s) /*-{
		var pattern = new RegExp("[^\\w \\.]");
		if (pattern.test(s)) {
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param ignoreThis
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object ignoreThis) {
		// Create a panel and Grid to hold the dialog's content...
		VerticalPanel vp = new VerticalPanel();
		m_ssGrid = new Grid(0, 2);
		m_ssGrid.addStyleName("manageSavedSearchesDlg_Grid");
		m_ssGrid.setCellPadding(0);
		m_ssGrid.setCellSpacing(0);
		populateSavedSearchesGrid();
		vp.add(m_ssGrid);
		
		// ...and return the panel.
		return vp;
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
		// Return something so that the editSuccessful() method gets
		// called.  Doesn't matter what since we're passing our data
		// around via global data members.
		return Boolean.TRUE;
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
	 * Populate the Grid with the defined saved searches.
	 */
	private void populateSavedSearchesGrid() {
		// Render the saved search links into the panel...
		addHeaderRow(m_ssGrid, m_ssGrid.getRowCount(), m_messages.mainMenuManageSavedSearchesDlgLinks());
		renderSearchLinkRows(m_ssGrid, m_ssGrid.getRowCount());
		
		// ...render the saved searches into the panel.
		addHeaderRow(m_ssGrid, m_ssGrid.getRowCount(), m_messages.mainMenuManageSavedSearchesDlgSavedSearches());
		if (0 < m_ssListCount) {
			for (int i = 0; i < m_ssListCount; i += 1) {
				renderSavedSearchRow(m_ssGrid, m_ssGrid.getRowCount(), m_ssList.get(i));
			}
		}
		else {
			renderNoSavedSearchesRow(m_ssGrid, m_ssGrid.getRowCount(), "NoSavedSearches");
		}
		if (GwtClientHelper.hasString(m_searchTabId)) {
			renderSaveSearchRow(m_ssGrid, m_ssGrid.getRowCount());
		}
	}
	
	/*
	 * Renders a no saved searches row in a Grid.
	 */
	private void renderNoSavedSearchesRow(Grid grid, int row, String idExtension) {
		grid.insertRow(row);
		String rowId = (IDBASE + idExtension);
		grid.getRowFormatter().getElement(row).setId(rowId);
		grid.setWidget(row, 1, new DlgLabel(m_messages.mainMenuManageSavedSearchesDlgNoItems()));
	}
	
	/*
	 * Renders an save search row in a Grid.
	 */
	private void renderSaveSearchRow(Grid grid, int row) {
		// Yes!  Create a row for the add...
		grid.insertRow(row);
		String rowId = (IDBASE + "SaveSearch");
		grid.getRowFormatter().getElement(row).setId(rowId);
		GwtClientHelper.setGridColSpan(grid, row, 0, 2);

		// Create a panel for the save widgets...
		FlowPanel savePanel = new FlowPanel();
		savePanel.addStyleName("manageSavedSearchesDlg_Save");
		
		// ...create the save name <INPUT> widget...
		final TextBox saveName = new TextBox();
		saveName.addStyleName("manageSavedSearchesDlg_SaveInput");
		savePanel.add(saveName);
		saveName.setMaxLength(MAX_NAME_LENGTH);
		saveName.setVisibleLength(VISIBLE_NAME_LENGTH);
		
		// ...and create the save push button.
		Button saveButton = new Button(m_messages.mainMenuManageSavedSearchesDlgSave(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SaveSearchCmd cmd;
				
				// Is the name of the search to save valid?
				final String searchName = validateSearchName(saveName.getValue());
				if (!(GwtClientHelper.hasString(searchName))) {
					// No!  validateSearchName() will have told the
					// user about any problems.  Simply bail.
					return;
				}
				
				// Generate a SavedSearchInfo for the new search.
				SavedSearchInfo ssi = new SavedSearchInfo();
				ssi.setName(searchName);
				
				// Can we save it?
				cmd = new SaveSearchCmd( m_searchTabId, ssi );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							m_messages.rpcFailure_SaveSearch(),
							searchName);
					}
					@Override
					public void onSuccess(VibeRpcResponse response) {
						SavedSearchInfo savedSSI = null;
						
						if ( response.getResponseData() != null )
							savedSSI = (SavedSearchInfo) response.getResponseData();
						
						// Perhaps.  Did it really get saved?
						if (null != savedSSI) {
							// Yes!  Add it to the appropriate list...
							addSSIToList(m_ssList, savedSSI);
							m_ssListCount = m_ssList.size();
							
							// ...and regenerate the dialog's contents.
							reRenderDlg();
						}
					}
				});
			}
		});
		saveButton.addStyleName("manageSavedSearchesDlg_SaveButton teamingButton");
		saveButton.setTitle(m_messages.mainMenuManageSavedSearchesDlgSaveSearch());
		savePanel.add(saveButton);
		
		// Finally, add the save widget's panel to the Grid.
		grid.setWidget(row, 0, savePanel);
	}
	
	/*
	 * Renders a SavedSearchInfo as a row in a Grid.
	 */
	private void renderSavedSearchRow(Grid grid, int row, final SavedSearchInfo ssi) {
		// Create the row...
		grid.insertRow(row);
		String rowId = (IDBASE + ssi.getName());
		grid.getRowFormatter().getElement(row).setId(rowId);

		// ...create a delete button anchor...
		Image deleteImg = new Image(m_images.searchDelete());
		deleteImg.setTitle(m_messages.mainMenuManageSavedSearchesDlgDeleteSearch());
		deleteImg.addStyleName("manageSavedSearchesDlg_DeleteImg");
		Anchor deleteAnchor = new Anchor();
		deleteAnchor.addStyleName("manageSavedSearchesDlg_DeleteAnchor");
		deleteAnchor.getElement().appendChild(deleteImg.getElement());
		deleteAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RemoveSavedSearchCmd cmd;
				
				cmd = new RemoveSavedSearchCmd( ssi );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							m_messages.rpcFailure_RemoveSavedSearch(),
							ssi.getName());
					}
					@Override
					public void onSuccess(VibeRpcResponse response) {
						// Remove the deleted SavedSearchInfo from the
						// list...
						m_ssList.remove(ssi);
						m_ssListCount  -= 1;

						// ...and regenerate the dialog's contents.
						reRenderDlg();
					}
				});
			}
		});
		grid.setWidget(row, 0, deleteAnchor);

		// ...and create the row's label.
		grid.setWidget(row, 1, new DlgLabel(ssi.getName()));
		grid.getCellFormatter().setWidth(row, 1, "100%");
	}

	/*
	 * Renders rows for the links to saved searches in a Grid.
	 */
	private void renderSearchLinkRows(Grid grid, int row) {
		boolean searchLinksDisplayed = false;
		FlowPanel searchLinksPanel;
		
		// If we have any saved search...
		if (0 < m_ssListCount) {
			// ...render their links in a row.
			searchLinksDisplayed = true;
			grid.insertRow(row);
			String rowId = (IDBASE + "SearchLinks");
			grid.getRowFormatter().getElement(row).setId(rowId);
			searchLinksPanel = new FlowPanel();
			searchLinksPanel.addStyleName("manageSavedSearchesDlg_SearchAnchorPanel");
			for (SavedSearchInfo ssi:  m_ssList) {
				renderSearchLink(searchLinksPanel, ssi);
			}
			grid.setWidget(row, 1, searchLinksPanel);
		}
		
		// If we didn't render any saved search links...
		if (!searchLinksDisplayed) {
			// ...render a row saying that.
			renderNoSavedSearchesRow(m_ssGrid, m_ssGrid.getRowCount(), "SavedSearchLinks_Empty");
		}
	}
	
	/*
	 * Renders the link for a saved search into a FlowPanel.
	 */
	private void renderSearchLink(FlowPanel searchLinksPanel, final SavedSearchInfo ssi) {
		Anchor searchAnchor = new Anchor(ssi.getName());
		searchAnchor.addStyleName("manageSavedSearchesDlg_SearchAnchor");
		searchAnchor.addClickHandler(new ClickHandler() {
			/*
			 * Called when the user clicks on the link's Anchor.
			 */
			@Override
			public void onClick(ClickEvent event) {
				// Hide the dialog and perform the saved search.
				hide();
				GwtTeaming.fireEvent(new SearchSavedEvent(ssi.getName()));
			}
		});
		searchLinksPanel.add(searchAnchor);
	}
	
	/*
	 * Re-renders the content of the dialog.
	 */
	private void reRenderDlg() {
		// Remove any existing content from the dialog...
		for (int i = (m_ssGrid.getRowCount() - 1); i >= 0; i -= 1) {
			m_ssGrid.removeRow(i);
		}
		
		// ...and regenerate it.
		populateSavedSearchesGrid();
	}
	
	/*
	 * Does what's needed to validate searchName.  The user is informed
	 * if any errors are detected.
	 * 
	 * If the string needs to be modified to be validated, the modified
	 * string is returned.
	 * 
	 * Implementation logic is based on that in the
	 * ss_saveSearchQuery() method in ss_search.js.
	 */
	private String validateSearchName(String searchName) {
		// Do we have a search name to validate?
		String reply = ((null == searchName) ? searchName : searchName.trim());
		if (GwtClientHelper.hasString(reply)) {
			// Yes!  If the search name is too long...
			if (MAX_NAME_LENGTH < reply.length()) {
				// ...tell the user and truncate it.
				Window.alert(m_messages.mainMenuManageSavedSearchesDlgWarningNameTruncated());
				reply = reply.substring(0, (MAX_NAME_LENGTH - 1));
			}

			// If the name contains invalid characters...
			if (containsInvalidData(reply)) {
				// ...tell the user that's not valid and bail.
				Window.alert(m_messages.mainMenuManageSavedSearchesDlgErrorSearchHasInvalidData());
				return "";
			}
			
			// Scan the currently defined names.
			for (int i = 0; i < m_ssListCount; i += 1) {
				// Is the new name a duplicate of this?
				SavedSearchInfo ssi = m_ssList.get(i);
				if (ssi.getName().equals(reply)) {
					// Yes!  Tell the user that's not valid and bail.
					Window.alert(m_messages.mainMenuManageSavedSearchesDlgErrorSearchDuplicate());
					return "";
				}
			}
		}
		
		// If we get here, reply refers to the validated string or an
		// empty string if the name was not valid.  Return it.
		return reply;
	}
	
	/**
	 * Callback interface to interact with the manage saved searches
	 * dialog asynchronously after it loads. 
	 */
	public interface ManageSavedSearchesDlgClient {
		void onSuccess(ManageSavedSearchesDlg mssd);
		void onUnavailable();
	}

	/**
	 * Loads the ManageSavedSearchesDlg split point and returns an
	 * instance of it via the callback.
	 *
	 * @param autoHide
	 * @param modeal
	 * @param left
	 * @param top
	 * @param ssList
	 * @param searchTabId
	 * @param mssdClient
	 */
	public static void createAsync(
			final boolean autoHide,
			final boolean modal,
			final int left,
			final int top,
			final List<SavedSearchInfo> ssList,
			final String searchTabId,
			final ManageSavedSearchesDlgClient mssdClient) {
		GWT.runAsync(ManageSavedSearchesDlg.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				ManageSavedSearchesDlg mmp = new ManageSavedSearchesDlg(autoHide, modal, left, top, ssList, searchTabId);
				mssdClient.onSuccess(mmp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ManageSavedSearchesDlg() );
				mssdClient.onUnavailable();
			}
		});
	}
}
