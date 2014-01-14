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

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureColumnsEvent;
import org.kablink.teaming.gwt.client.event.InvokeImportIcalFileEvent;
import org.kablink.teaming.gwt.client.event.InvokeImportIcalUrlEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.rpc.shared.GetDefaultFolderDefinitionIdCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class FolderOptionsDlg extends DlgBox implements EditSuccessfulHandler {
	private final static String IDBASE				= "folderOption_";	// Base ID for rows in the folder options Grid.
	private final static String IDTAIL_RADIO		= "_rb";			// Used for constructing the ID of a row's radio button.
	private final static String OPTION_HEADER_ID	= "optionHeader";

	private Grid m_folderOptionsGrid;				// Once displayed, the table of folder options.
	private GwtTeamingMessages m_messages;			// Access to the GWT UI messages.
	private int m_calendarImportListCount;			// Count of items in m_calendarImportList.
	private int m_folderViewsListCount;				// Count of items in m_folderViewsList.
	private List<ToolbarItem> m_calendarImportList;	// List of calendar import toolbar items.
	private List<ToolbarItem> m_folderViewsList;	// List of folder view     toolbar items.
	private String m_binderId;						// The ID of the binder the folder options dialog is running against.

	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label) {
			super(label);
			addStyleName("folderOptionsDlg_Label");
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FolderOptionsDlg(boolean autoHide, boolean modal, int left, int top, String binderId, List<ToolbarItem> calendarImportsList, List<ToolbarItem> folderViewsList) {
		// Initialize the superclass...
		super(autoHide, modal, left, top);

		// ...initialize everything else...
		m_messages           = GwtTeaming.getMessages();
		m_binderId           = binderId;
		m_calendarImportList = calendarImportsList; m_calendarImportListCount = ((null == m_calendarImportList) ? 0 : m_calendarImportList.size());
		m_folderViewsList    = folderViewsList;     m_folderViewsListCount    = ((null == m_folderViewsList)    ? 0 : m_folderViewsList.size());
		if (1 == m_folderViewsListCount) {
			// We ignore the folder view options if there is only one
			// to select from.
			m_folderViewsList       = null;
			m_folderViewsListCount = 0;
		}
	
		// ...create the dialog's content...
		createAllDlgContent(
			m_messages.mainMenuFolderOptionsDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Data passed via global data members.

		// ...and check the default folder view, if there's one to be
		// ...checked.
		checkDefaultFolderView();
	}
	
	/*
	 * Adds a section header row to the folder options grid.
	 */
	private void addHeaderRow(Grid grid, int row, String headerText) {
		DlgLabel header = new DlgLabel(headerText);
		header.addStyleName("folderOptionsDlg_SectionHeader");

		grid.insertRow(row);
		Element e = grid.getRowFormatter().getElement(row); 
		e.setId(OPTION_HEADER_ID);
		e.addClassName("folderOptionsDlg_SectionHeaderRow");
		e = grid.getCellFormatter().getElement(row, 0);
		e.addClassName("folderOptionsDlg_SectionHeaderCell");
		grid.setWidget(row, 0, header);
		GwtClientHelper.setGridColSpan(grid, row, 0, 2);
	}

	/*
	 * Checks the radio button corresponding to the default folder view
	 * for the binder we're running against.
	 */
	private void checkDefaultFolderView() {
		// Did we display any folder view options in the dialog?
		if (0 < m_folderViewsListCount) {
			// Yes!  Does the folder we're working on have a default
			// view defined?
			GetDefaultFolderDefinitionIdCmd cmd = new GetDefaultFolderDefinitionIdCmd( m_binderId );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetFolderDefinitionId());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					String folderDefId;
					if (null != response.getResponseData()) {
						StringRpcResponseData responseData = (StringRpcResponseData) response.getResponseData();
						folderDefId = responseData.getStringValue();
					}
					else {
						folderDefId = null;
					}
					
					if (GwtClientHelper.hasString(folderDefId)) {
						// Yes!  Scan the folder view options.
						for (int i = 0; i < m_folderViewsListCount; i += 1) {
							// Is this the folder view option that
							// corresponds to the folder's default
							// view definition?
							ToolbarItem tbi = m_folderViewsList.get(i);
							String url = tbi.getUrl();
							if (GwtClientHelper.hasString(url) && (0 < url.indexOf(folderDefId))) {
								// Yes!  Check its radio button.
								String rbId = (IDBASE + tbi.getName() + IDTAIL_RADIO);
								InputElement rb = Document.get().getElementById(rbId).getFirstChildElement().cast();
								rb.setChecked(true);								
								break;
							}
						}
					}
				}
			});
		}		
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param ignored
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object ignored) {
		// Create a panel to hold the dialog's content...
		VerticalPanel vp = new VerticalPanel();

		// Are there any folder options to display in the dialog?
		if (0 < (m_folderViewsListCount + m_calendarImportListCount)) {
			// Yes!  Create a grid to contain them... 
			m_folderOptionsGrid = new Grid(0, 2);
			m_folderOptionsGrid.addStyleName("folderOptionsDlg_Grid");
			m_folderOptionsGrid.setCellPadding(0);
			m_folderOptionsGrid.setCellSpacing(0);
			
			// ...render the folder view options into the panel...
			if (0 < m_folderViewsListCount) {
				addHeaderRow(m_folderOptionsGrid, m_folderOptionsGrid.getRowCount(), m_messages.mainMenuFolderOptionsDlgFolderViews());
				for (int i = 0; i < m_folderViewsListCount; i += 1) {
					renderRow(m_folderOptionsGrid, m_folderOptionsGrid.getRowCount(), m_folderViewsList.get(i), false);
				}
			}
			
			// ...render the calendar import options into the panel...
			if (0 < m_calendarImportListCount) {
				String viewType = GwtClientHelper.jsGetViewType();
				String dlgHeader;
				if (GwtClientHelper.hasString(viewType) && viewType.equalsIgnoreCase("task"))
				     dlgHeader = m_messages.mainMenuFolderOptionsDlgImportTask();
				else dlgHeader = m_messages.mainMenuFolderOptionsDlgImportCalendar();				
				addHeaderRow(m_folderOptionsGrid, m_folderOptionsGrid.getRowCount(), dlgHeader);
				for (int i = 0; i < m_calendarImportListCount; i += 1) {
					renderRow(m_folderOptionsGrid, m_folderOptionsGrid.getRowCount(), m_calendarImportList.get(i), false);
				}
			}

			// ...and connect everything together.
			vp.add(m_folderOptionsGrid);
		}
		
		else {
			// No, there weren't any folder options to display in the
			// dialog!  Put a simple no available options message.
			vp.add(new DlgLabel(m_messages.mainMenuFolderOptionsNoOptions()));
		}
		
		// Finally, return the panel the with the dialog's contents.
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
	public boolean editSuccessful(Object callbackData) {
		// If we have a folder option selected, put it into effect.
		ToolbarItem tbi = ((ToolbarItem) callbackData);
		TeamingEvents event = tbi.getTeamingEvent();
		if (TeamingEvents.UNDEFINED == event) {
			String url = tbi.getUrl();
			if (GwtClientHelper.hasString(url)) {
				String jsString = tbi.getQualifierValue("onClick");
				if (GwtClientHelper.hasString(jsString)) {
					GwtClientHelper.jsEvalString(url, jsString);
				}
				else {
					GwtTeaming.fireEvent(new GotoContentUrlEvent(url));
				}
			}
		}
		
		else {
			VibeEventBase<?> vibeEvent;
			String importType = tbi.getName();
			switch (event) {			
			case INVOKE_CONFIGURE_COLUMNS:  vibeEvent = new InvokeConfigureColumnsEvent();         break;
			case INVOKE_IMPORT_ICAL_FILE:   vibeEvent = new InvokeImportIcalFileEvent(importType); break;
			case INVOKE_IMPORT_ICAL_URL:    vibeEvent = new InvokeImportIcalUrlEvent( importType); break;			
			default:
				Window.alert(m_messages.mainMenuFolderOptionsUnexpectedEvent(event.name()));
				return true;
			}
			GwtTeaming.fireEvent(vibeEvent);
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
		int rows = m_folderOptionsGrid.getRowCount();
		if (0 < rows) {
			// Yes!  Scan them.
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
		return new ToolbarItem(null);	// null -> Name will not be used.
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
	private ToolbarItem getFolderOptionById(String foId) {
		// Scan the available folder views.
		for (int i = 0; i < m_folderViewsListCount; i += 1) {
			// Is this the ToolbarItem in question?
			ToolbarItem tbi = m_folderViewsList.get(i);
			if (tbi.getName().equals(foId)) {
				// Yes!  Return it.
				return tbi;
			}
		}
		
		// If we get here, we didn't find the folder option as a folder
		// view!  Scan the available import calendar options.
		for (int i = 0; i < m_calendarImportListCount; i += 1) {
			// Is this the ToolbarItem in question?
			ToolbarItem tbi = m_calendarImportList.get(i);
			if (tbi.getName().equals(foId)) {
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
		boolean reply;
		String rowId = getRowId(row);
		if (rowId.equals(OPTION_HEADER_ID)) {
			reply = false;
		}
		else {
			InputElement rb = Document.get().getElementById(rowId + IDTAIL_RADIO).getFirstChildElement().cast();
			reply = rb.isChecked();
		}
		return reply;
	}
	
	/*
	 * Renders a folder view ToolbarItem as a row in a Grid.
	 */
	private void renderRow(Grid grid, int row, ToolbarItem tbi, boolean checked) {
		grid.insertRow(row);
		
		String rowId = (IDBASE + tbi.getName());
		grid.getRowFormatter().getElement(row).setId(rowId);
		
		RadioButton cb = new RadioButton("folderOptions");
		cb.addStyleName("folderOptionsDlg_Radio");
		cb.getElement().setId(rowId + IDTAIL_RADIO);
		cb.setValue(checked);
		grid.setWidget(row, 0, cb);
		String txt = tbi.getTitle();
		if (!(GwtClientHelper.hasString(txt))) {
			txt = tbi.getName();
		}
		grid.setWidget(row, 1, new DlgLabel(txt));
		grid.getCellFormatter().setWidth(row, 1, "100%");
	}
	
	/**
	 * Callback interface to interact with the dialog asynchronously
	 * after it loads. 
	 */
	public interface FolderOptionsDlgClient {
		void onSuccess(FolderOptionsDlg dlg);
		void onUnavailable();
	}

	/**
	 * Loads the FolderOptionsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param binderId
	 * @param calendarImportsList
	 * @param folderViewsList
	 * @param dlgClient
	 */
	public static void createAsync(
			final boolean autoHide,
			final boolean modal,
			final int left,
			final int top,
			final String binderId,
			final List<ToolbarItem> calendarImportsList,
			final List<ToolbarItem> folderViewsList,
			final FolderOptionsDlgClient dlgClient) {
		GWT.runAsync(FolderOptionsDlg.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				FolderOptionsDlg dlg = new FolderOptionsDlg(
					autoHide,
					modal,
					left,
					top,
					binderId,
					calendarImportsList,
					folderViewsList);
				
				dlgClient.onSuccess(dlg);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FolderOptionsDlg());
				dlgClient.onUnavailable();
			}
		});
	}	
}
