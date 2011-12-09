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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.ClipboardUsersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClipboardUsersRpcResponseData.ClipboardUser;
import org.kablink.teaming.gwt.client.rpc.shared.GetClipboardUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Implements Vibe's clipboard dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings({"unused"})
public class ClipboardDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private final static String IDBASE		= "clipboard_";	// Base ID for rows in the clipboard Grid.
	private final static String IDTAIL_CBOX	= "_cb";		// Used for constructing the ID of a row's CheckBox.

	private BinderInfo				m_binderInfo;	//
	private Grid					m_cbGrid;		// Once displayed, the table of clipboard items.
	private GwtTeamingImageBundle	m_images;		//
	private GwtTeamingMessages		m_messages;		// Access to the GWT UI messages.
	private int						m_cbGridCount;	// Count of rows  in m_cbGrid. 

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
//!			...this needs to be implemented...			
		}
	}
	
	/*
	 * Inner class that implements the add team command.
	 */
	private class DoAddTeam implements Command {
		@Override
		public void execute() {
//!			...this needs to be implemented...			
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
			int rows = m_cbGrid.getRowCount();
			for (int i = (rows - 1); i >= 0; i -= 1) {
				// If this row checked...
				if (isRowChecked(i)) {
					// ...delete it.
					m_cbGrid.removeRow(i);
				}
			}
			m_cbGridCount = m_cbGrid.getRowCount();
			if (0 == m_cbGridCount) {
				m_cbGrid.insertRow(0);
				m_cbGrid.setWidget(0, 1, new DlgLabel(m_messages.mainMenuClipboardDlgEmpty()));
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
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuClipboardDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage() {
		return new Image(m_images.spinner16());
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
		// Create the dialog's menu.
		VerticalPanel vp = new VerticalPanel();
		MenuBar mb = new MenuBar();
		mb.addStyleName("vibe-cbDlg_MenuBar");
		mb.addItem(new DlgMenuItem(m_messages.mainMenuClipboardDlgAddPeople(), new DoAddPeople()));
		mb.addItem(new DlgMenuItem(m_messages.mainMenuClipboardDlgAddTeam(),   new DoAddTeam()));
		mb.addItem(new DlgMenuItem(m_messages.mainMenuClipboardDlgDelete(),    new DoDelete()));
		vp.add(mb);

		// Render the rows in the dialog.
		m_cbGrid = new Grid(0, 2);
		m_cbGrid.addStyleName("vibe-cbDlg_Grid");
		m_cbGrid.setCellPadding(0);
		m_cbGrid.setCellSpacing(0);
		vp.add(m_cbGrid);
		
		// And return the Panel that will hold the dialog's contents.
		return vp;
	}

	/*
	 * Clears the contents of the dialog and displays a message that
	 * we're reading the contents of the clipboard.
	 */
	private void displayReading() {
		m_cbGrid.clear();
		m_cbGrid.insertRow(0);
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-cbDlgReadingPanel");
		fp.add(buildSpinnerImage());
		DlgLabel l = new DlgLabel(m_messages.mainMenuClipboardDlgReading());
		l.addStyleName("vibe-cbDlgReadingLabel");
		fp.add(l);
		m_cbGrid.setWidget(0, 1, fp);
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
		// Unused.
		return true;
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
	 * Returns true if a row is checked and false otherwise.
	 */
	private boolean isRowChecked(int i) {
//!		...this needs to be implemented...
		return false;
	}

	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
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
				// Extract the clipboard users and continue populating.
				ClipboardUsersRpcResponseData responseData = ((ClipboardUsersRpcResponseData) response.getResponseData());
				populatePart2Async(responseData.getClipboardUsers());
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populatePart2Async(final List<ClipboardUser> cbUsers) {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populatePart2Now(cbUsers);
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populatePart2Now(final List<ClipboardUser> cbUsers) {
//!		...this needs to be implemented...		
		Window.alert("ClipboardDlg.populatePart2Now():  ...this needs to be implemented...");
	}

	/*
	 * Asynchronously runs the given instance of the clipboard dialog.
	 */
	private static void runDlgAsync(final ClipboardDlg cbDlg, final BinderInfo bi) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				cbDlg.runDlgNow(bi);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Asynchronously runs the given instance of the clipboard dialog.
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
