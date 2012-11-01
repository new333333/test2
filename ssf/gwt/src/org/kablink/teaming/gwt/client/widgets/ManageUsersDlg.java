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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetManageUsersInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ManageUsersInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements the 'Manage Users' dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ManageUsersDlg extends DlgBox {
	public final static boolean SHOW_GWT_MANAGE_USERS	= false;	//! DRF:  Leave false on checkin until I get this working.
	
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private int								m_showX;			// The x and...
	private int								m_showY;			// ...y position to show the dialog.
	private ManageUsersInfoRpcResponseData	m_manageUsersInfo;	// Information necessary to run the manage users dialog.
	private VibeFlowPanel					m_rootPanel;		// The panel that holds the dialog's contents.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageUsersDlg(ManageUsersDlgClient muDlgClient, int x, int y, int cx, int cy) {
		// Initialize the superclass...
		super(
			false,					// false -> Not auto hide.
			true,					// true  -> Modal.
			x, y, cx, cy,			// Position and size of dialog.
			DlgButtonMode.Close);	// Forces the 'X' close button in the upper right corner.

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		addStyleName("vibe-manageUsersDlg");
		createAllDlgContent(
			m_messages.manageUserDlgCaption(),
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			muDlgClient); 
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
	public Panel createContent(Object callbackData) {
		// Can we get the information necessary to manage users?
		final ManageUsersDlg		muDlg       = this;
		final ManageUsersDlgClient	muDlgClient = ((ManageUsersDlgClient) callbackData);
		GwtClientHelper.executeCommand(new GetManageUsersInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetManageUsersInfo());

				// ...and tell the caller that the dialog will be
				// ...unavailable.
				muDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Yes!  Store it and tell the caller that the dialog
				// is available.
				m_manageUsersInfo = ((ManageUsersInfoRpcResponseData) result.getResponseData());
				muDlgClient.onSuccess(muDlg);
			}
		});
		
		// Create the main panel that will hold the dialog's content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-manageUsersDlg-rootPanel");
		
		// ...and return it.  Note that it will get populated during
		// ...the initAndShow() call.
		return m_rootPanel;
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
//!		...this needs to be implemented...
		return null;
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
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_rootPanel.clear();

		// ..create new content...
//!		...this needs to be implemented...

		// ...and position and show it.
		setPopupPosition(m_showX, m_showY);
		show();
	}
	
	/*
	 * Asynchronously runs the given instance of the manage users
	 * dialog.
	 */
	private static void runDlgAsync(final ManageUsersDlg muDlg, final int x, final int y) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				muDlg.runDlgNow(x, y);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the manage users
	 * dialog.
	 */
	private void runDlgNow(int x, int y) {
		// Store the parameters...
		m_showX = x;
		m_showY = y;
		
		// ...and start populating the dialog.
		populateDlgAsync();
	}


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the manage users dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the manage users dialog
	 * asynchronously after it loads. 
	 */
	public interface ManageUsersDlgClient {
		void onSuccess(ManageUsersDlg muDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ManageUsersDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the dialog.
			final ManageUsersDlgClient muDlgClient,
			final int					createX,
			final int					createY,
			final int					createCX,
			final int					createCY,
			
			// Parameters used to initialize and show an instance of the dialog.
			final ManageUsersDlg	muDlg,
			final int				initX,
			final int				initY) {
		GWT.runAsync(ManageUsersDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ManageUsersDlg());
				if (null != muDlgClient) {
					muDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != muDlgClient) {
					// Yes!  Create the dialog.  Note that its
					// construction flow will call the appropriate
					// method off the ManageUsersDlgClient object.
					new ManageUsersDlg(muDlgClient, createX, createY, createCX, createCY);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(muDlg, initX, initY);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageUsersDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param muDlgClient
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void createAsync(ManageUsersDlgClient muDlgClient, int x, int y, int cx, int cy) {
		doAsyncOperation(muDlgClient, x, y, cx, cy, null, (-1), (-1));
	}
	
	/**
	 * Initializes and shows the manage users dialog.
	 * 
	 * @param muDlg
	 * @param x
	 * @param y
	 */
	public static void initAndShow(ManageUsersDlg muDlg, int x, int y) {
		doAsyncOperation(null, (-1), (-1), (-1), (-1), muDlg, x, y);
	}
}
