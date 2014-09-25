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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.ClipboardUsersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClipboardUsersRpcResponseData.ClipboardUser;
import org.kablink.teaming.gwt.client.rpc.shared.GetClipboardTeamUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetClipboardUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetClipboardUsersFromListCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveClipboardUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.ContributorsHelper;
import org.kablink.teaming.gwt.client.util.ContributorsHelper.ContributorsCallback;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements Vibe's clipboard dialog.
 *  
 * @author drfoster@novell.com
 */
public class ClipboardDlg extends DlgBox {
	private final static String IDBASE		= "clipboard_";	// Base ID for rows in the clipboard Grid.
	private final static String IDTAIL_CBOX	= "_cb";		// Used for constructing the ID of a row's CheckBox.
	private final static int    SCROLL_WHEN	= 5;			// Count of items in the ScrollPanel when scroll bars are enabled.

	private BinderInfo						m_binderInfo;		// The binder the clipboard is running against.
	private FlowPanel 						m_selectPanel;		// Panel containing the select/clear all widgets.
	private Grid							m_cbGrid;			// Once displayed, the table of clipboard items.
	private GwtTeamingMainMenuImageBundle	m_images;			// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private List<ClipboardUser>				m_cbUserList;		// Current list of users on the clipboard.
	private int								m_cbUserListCount;	// Number of items in m_cbUserList.
	private int								m_cbGridCount;		// Count of rows  in m_cbGrid.
	private ScrollPanel						m_sp;				// The ScrollPanel holding m_cbGrid.  Scroll bars are enabled/disabled based on the count of items.

	/*
	 * Inner class used to compare two ClipboardUser's by their title.
	 */
	private static class ClipboardUserComparator implements Comparator<ClipboardUser> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public ClipboardUserComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two ClipboardUser's by their names.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param cbUser1
		 * @param cbUser2
		 * 
		 * @return
		 */
		@Override
		public int compare(ClipboardUser cbUser1, ClipboardUser cbUser2) {
			String name1 = cbUser1.getTitle();
			String name2 = cbUser2.getTitle();

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(name1, name2);
			else reply = GwtClientHelper.safeSColatedCompare(name2, name1);
			return reply;
		}
	}
	
	/*
	 * Inner class that wraps items displayed in the dialog's content.
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
			addStyleName("vibe-cbDlg_Label");
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
	}

	/*
	 * Inner class that wraps item's on the dialog's menu.
	 */
	private class DlgMenuItem extends MenuItem {
		/**
		 * Constructor method.
		 * 
		 * @param text
		 * @param menuCommand
		 */
		public DlgMenuItem(String text, Command menuCommand) {
			super(text, menuCommand);
			addStyleName("vibe-cbDlg_MenuItem");
		}
	}
	
	/*
	 * Inner class that implements the add people command.
	 */
	private class DoAddPeople implements Command {
		@Override
		public void execute() {
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					ContributorsHelper.getContributors(m_binderInfo.getBinderIdAsLong(), new ContributorsCallback() {
						@Override
						public void onFailure() {
							// Nothing to do.  Simply ignore.
						}

						@Override
						public void onSuccess(List<Long> contributorIds) {
							// Asynchronously process the list of
							// contributor IDs.
							processContributorIdsAsync(contributorIds);
						}
					});
				}
			});
		}
	}
	
	/*
	 * Inner class that implements the add team command.
	 */
	private class DoAddTeam implements Command {
		@Override
		public void execute() {
			GwtClientHelper.executeCommand(
					new GetClipboardTeamUsersCmd(m_binderInfo.getBinderIdAsLong()),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetClipboardTeamUsers());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Extract the clipboard users from the response
					// data and merge it into the global list...
					ClipboardUsersRpcResponseData responseData = ((ClipboardUsersRpcResponseData) response.getResponseData());
					mergeCBUserLists(responseData.getClipboardUsers());

					// ...save the global list as the new contents of
					// ...the clipboard....
					saveCBUserListAsync();
					
					// ...and use the global list to populate the
					// ...dialog.
					populateFromCBUserListAsync();
				}
			});
		}
	}
	
	/*
	 * Inner class that implements the delete command.
	 */
	private class DoDelete implements Command {
		@Override
		public void execute() {
			// If the table is empty...
			if (0 == m_cbGridCount) {
				// ...there's nothing to do.
				return;
			}
			
			// Scan the rows in the table.
			boolean rowsRemoved = false;
			int rows = m_cbGrid.getRowCount();
			for (int i = (rows - 1); i >= 0; i -= 1) {
				// If this row checked...
				if (isRowChecked(i)) {
					// ...delete it.
					removeUserFromCBList(Long.parseLong(getRowId(i).substring(IDBASE.length())));
					m_cbGrid.removeRow(i);
					rowsRemoved = true;
				}
			}
			
			// Did we remove any rows?
			m_cbGridCount = m_cbGrid.getRowCount();
			if ((m_cbGridCount != m_cbUserListCount) && GwtClientHelper.isDebugUI()) {
				Window.alert("ClipboardDlg.DoDelete.execute( *Internal Error* ):  Grid count does not match list count!");
			}
			if (rowsRemoved) {
				// Yes!  If there aren't any left in the grid...
				if (0 == m_cbGridCount) {
					// ...add a string saying that.
					m_cbGrid.insertRow(0);
					m_cbGrid.setWidget(0, 1, new DlgLabel(m_messages.mainMenuClipboardDlgEmpty()));
					m_selectPanel.setVisible(false);
				}
				
				// ...and save the global List<ClipboardUser> as the
				// ...new contents of the clipboard.
				saveCBUserListAsync();
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
	private ClipboardDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_images   = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuClipboardDlgHeader(),
			getSimpleSuccessfulHandler(),	// The dialog's EditCanceledHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);							// Create callback data.  Unused. 
	}

	/*
	 * Adds a ClipboardUser to a List<ClipboardUser> if its user ID is
	 * not already in the list.
	 */
	private void addCBUserToListIfUnique(ClipboardUser cbUser, List<ClipboardUser> cbUserList) {
		// Scan the List<ClipboardUser>.
		Long id = cbUser.getUserId();
		for (ClipboardUser cbUserScan:  cbUserList) {
			// Is this the ID in question?
			if (id.equals(cbUserScan.getUserId())) {
				// Yes!  Then we won't add it again.
				return;
			}
		}
		
		// If we get here, the ID is not already in the list.  Add the
		// new ClipboardUser to it now.
		cbUserList.add(cbUser);
	}
	
	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage() {
		return new Image(m_images.spinner());
	}

	/*
	 * Clears the contents of the ClipboardUser Grid.
	 */
	private void clearGrid() {
		m_cbGrid.clear();
		m_cbGrid.resize(0, 2);
		m_cbGridCount = 0;
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create a panel to hold the dialog's content...
		VerticalPanel vp = new VerticalPanel();
		
		// ...create the dialog's menu...
		MenuBar mb = new MenuBar();
		mb.addStyleName("vibe-cbDlg_MenuBar");
		mb.addItem(new DlgMenuItem(m_messages.mainMenuClipboardDlgAddPeople(), new DoAddPeople()));
		mb.addItem(new DlgMenuItem(m_messages.mainMenuClipboardDlgAddTeam(),   new DoAddTeam()));
		mb.addItem(new DlgMenuItem(m_messages.mainMenuClipboardDlgDelete(),    new DoDelete()));
		vp.add(mb);

		// ...create a Grid to render the rows in the dialog...
		m_cbGrid = new Grid(0, 2);
		m_cbGrid.addStyleName("vibe-cbDlg_Grid");
		m_cbGrid.setCellPadding(2);
		m_cbGrid.setCellSpacing(2);

		// ...create a ScrollPanel to hold that Grid...
		m_sp = new ScrollPanel();
		m_sp.addStyleName("vibe-cbDlg_ScrollPanel");
		m_sp.add(m_cbGrid);
		vp.add(m_sp);
		
		// ...create a FlowPanel and widgets for mass selections...
		m_selectPanel = new FlowPanel();
		m_selectPanel.addStyleName("vibe-cbDlg_SelectPanel");
		InlineLabel selectAll = new InlineLabel(m_messages.mainMenuClipboardDlgSelectAll());
		selectAll.setWordWrap(false);
		selectAll.addStyleName("vibe-cbDlg_Anchor vibe-cbDlg_SelectAll");
		selectAll.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doSelectAllAsync(true);
			}
		});
		m_selectPanel.add(selectAll);
		InlineLabel clearAll = new InlineLabel(m_messages.mainMenuClipboardDlgClearAll());
		clearAll.setWordWrap(false);
		clearAll.addStyleName("vibe-cbDlg_Anchor vibe-cbDlg_ClearAll");
		clearAll.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doSelectAllAsync(false);
			}
		});
		m_selectPanel.add(clearAll);
		m_selectPanel.setVisible(false);
		vp.add(m_selectPanel);
		
		// ...and return the Panel that holds the dialog's contents.
		return vp;
	}

	/*
	 * Clears the contents of the dialog and displays a message that
	 * we're reading the contents of the clipboard.
	 */
	private void displayReading() {
		clearGrid();
		m_cbGrid.insertRow(0);
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-cbDlg_ReadingPanel");
		fp.add(buildSpinnerImage());
		DlgLabel l = new DlgLabel(m_messages.mainMenuClipboardDlgReading());
		l.addStyleName("vibe-cbDlg_ReadingLabel");
		fp.add(l);
		m_cbGrid.setWidget(0, 1, fp);
	}

	/*
	 * Asynchronously checks or unChecks the check boxes.
	 */
	private void doSelectAllAsync(final boolean select) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doSelectAllNow(select);
			}
		});
	}
	
	/*
	 * Asynchronously checks or unChecks the check boxes.
	 */
	private void doSelectAllNow(boolean select) {
		// If the table is empty...
		if (0 == m_cbGridCount) {
			// ...there's nothing to do.
			return;
		}
		
		// Scan the rows in the table...
		int rows = m_cbGrid.getRowCount();
		for (int i = 0; i < rows; i += 1) {
			// ...checking/unChecking each.
			String rowId = getRowId(i);
			InputElement cb = Document.get().getElementById(rowId + IDTAIL_CBOX).getFirstChildElement().cast();
			cb.setChecked(select);
		}
	}
	
	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
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
		return m_cbGrid.getRowFormatter().getElement(row).getId();
	}
	
	/*
	 * Returns true if a row is checked and false otherwise.
	 */
	private boolean isRowChecked(int row) {
		String rowId = getRowId(row);
		InputElement cb = Document.get().getElementById(rowId + IDTAIL_CBOX).getFirstChildElement().cast();
		return cb.isChecked();
	}

	/*
	 * Merges the given List<ClipboardUser> into the list stored
	 * globally and updates the global data members accordingly.
	 */
	private void mergeCBUserLists(List<ClipboardUser> cbUsers) {
		// If we don't have a global list...
		if (null == m_cbUserList) {
			// ...allocate one now.
			m_cbUserList = new ArrayList<ClipboardUser>();
		}

		// Do we have a List<ClipboardUser> to merge into the global
		// list?
		if ((null != cbUsers) && (!(cbUsers.isEmpty()))) {
			// Yes!  Scan the ClipboardUser's in that list...
			for (ClipboardUser cbUser:  cbUsers) {
				// ...adding them to the global list if they're not
				// ...already there...
				addCBUserToListIfUnique(cbUser, m_cbUserList);
			}
			
			// ...and sort the resultant global list.
			sortCBUserList(m_cbUserList);
		}

		// Finally, update the global count of ClipboardUser's in the
		// global list.
		m_cbUserListCount = m_cbUserList.size();
	}
	
	/*
	 * Asynchronously process the contributor IDs.
	 */
	private void processContributorIdsAsync(final List<Long> contributorIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				processContributorIdsNow(contributorIds);
			}
		});
	}
	
	/*
	 * Synchronously process the contributor IDs.
	 */
	private void processContributorIdsNow(List<Long> contributorIds) {
		GwtClientHelper.executeCommand(
				new GetClipboardUsersFromListCmd(m_binderInfo.getBinderIdAsLong(), contributorIds),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetClipboardUsersFromList());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the clipboard users from the response
				// data and merge it into the global list...
				ClipboardUsersRpcResponseData responseData = ((ClipboardUsersRpcResponseData) response.getResponseData());
				mergeCBUserLists(responseData.getClipboardUsers());

				// ...save the global list as the new contents of
				// ...the clipboard....
				saveCBUserListAsync();
				
				// ...and use the global list to populate the
				// ...dialog.
				populateFromCBUserListAsync();
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		GwtClientHelper.executeCommand(
				new GetClipboardUsersCmd(),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetClipboardUsers());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the clipboard users from the response
				// data...
				ClipboardUsersRpcResponseData responseData = ((ClipboardUsersRpcResponseData) response.getResponseData());
				m_cbUserList      = responseData.getClipboardUsers();
				m_cbUserListCount = ((null == m_cbUserList) ? 0: m_cbUserList.size());
				
				// ...and use that to populate the dialog.
				populateFromCBUserListAsync();
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the dialog from the
	 * global List<ClipboardUser>.
	 */
	private void populateFromCBUserListAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateFromCBUserListNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog from the
	 * global List<ClipboardUser>.
	 */
	private void populateFromCBUserListNow() {
		clearGrid();
		m_cbGrid.resize(0, 2);
		m_cbGridCount = m_cbUserListCount;
		
		if (m_cbGridCount >= SCROLL_WHEN)
		     m_sp.addStyleName(   "vibe-cbDlg_ScrollPanelLimt");
		else m_sp.removeStyleName("vibe-cbDlg_ScrollPanelLimt");
		
		if (0 == m_cbGridCount) {
			m_cbGrid.insertRow(0);
			m_cbGrid.setWidget(0, 1, new DlgLabel(m_messages.mainMenuClipboardDlgEmpty()));
			m_selectPanel.setVisible(false);
		}
		else {
			m_selectPanel.setVisible(true);
			for (int i = 0; i < m_cbGridCount; i += 1) {
				renderRow(m_cbGrid, m_cbUserList.get(i), i, false);
			}
		}
	}

	/*
	 * Removes the ClipboardUser with the given ID from the global
	 * List<ClipboardUser>.
	 */
	private void removeUserFromCBList(Long userId) {
		// Scan the ClipboardUser's in the global list.
		for (ClipboardUser cbUser:  m_cbUserList) {
			// Is this the user in question?
			if (userId.equals(cbUser.getUserId())) {
				// Yes!  Remove it from the list and update the count.
				m_cbUserList.remove(cbUser);
				m_cbUserListCount -= 1;
				return;
			}
		}
	}
	
	/*
	 * Renders a ClipboardUser as a row in a Grid.
	 */
	private void renderRow(Grid grid, ClipboardUser cbUser, int row, boolean checked) {
		grid.insertRow(row);
		
		String rowId = (IDBASE + cbUser.getUserId());
		grid.getRowFormatter().getElement(row).setId(rowId);
		
		CheckBox cb = new CheckBox();
		cb.addStyleName("vibe-cbDlg_Checkbox");
		cb.getElement().setId(rowId + IDTAIL_CBOX);
		cb.setValue(checked);
		grid.setWidget(row, 0, cb);
		grid.setWidget(row, 1, new DlgLabel(cbUser.getTitle()));
	}
	
	/*
	 * Asynchronously runs the given instance of the clipboard dialog.
	 */
	private static void runDlgAsync(final ClipboardDlg cbDlg, final BinderInfo bi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cbDlg.runDlgNow(bi);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the clipboard dialog.
	 */
	private void runDlgNow(BinderInfo bi) {
		// Store the parameter...
		m_binderInfo = bi;

		// ...and display a reading message, start populating the
		// ...dialog and show it.
		displayReading();
		populateDlgAsync();
		show(true);
	}

	/*
	 * Asynchronously saves the global List<ClipboardUser> as the new
	 * contents of the clipboard.
	 */
	private void saveCBUserListAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				saveCBUserListNow();
			}
		});
	}
	
	/*
	 * Synchronously saves the global List<ClipboardUser> as the new
	 * contents of the clipboard.
	 */
	private void saveCBUserListNow() {
		GwtClientHelper.executeCommand(
				new SaveClipboardUsersCmd(m_cbUserList),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveClipboardUsers());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Data saved.  Nothing else to do.
			}
		});
	}

	/*
	 * Sorts the ClipboardUser's in a List<ClipboardUser>.
	 */
	private void sortCBUserList(List<ClipboardUser> cbUserList) {
		Comparator<ClipboardUser> comparator = new ClipboardUserComparator(true);	// true -> Ascending.
		Collections.sort(cbUserList, comparator);
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the clipboard dialog and perform some operation on it.        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the clipboard dialog
	 * asynchronously after it loads. 
	 */
	public interface ClipboardDlgClient {
		void onSuccess(ClipboardDlg cbDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ClipboardDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ClipboardDlgClient cbDlgClient,
			
			// initAndShow parameters,
			final ClipboardDlg cbDlg,
			final BinderInfo bi) {
		GWT.runAsync(ClipboardDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ClipboardDlg());
				if (null != cbDlgClient) {
					cbDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cbDlgClient) {
					// Yes!  Create it and return it via the callback.
					ClipboardDlg cbDlg = new ClipboardDlg();
					cbDlgClient.onSuccess(cbDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cbDlg, bi);
				}
			}
		});
	}
	
	/**
	 * Loads the ClipboardDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param cbDlgClient
	 */
	public static void createAsync(ClipboardDlgClient cbDlgClient) {
		doAsyncOperation(cbDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the clipboard dialog.
	 * 
	 * @param cbDlg
	 * @param bi
	 */
	public static void initAndShow(ClipboardDlg cbDlg, BinderInfo bi) {
		doAsyncOperation(null, cbDlg, bi);
	}
}
