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

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements the user share rights dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class UserShareRightsDlg extends DlgBox implements EditSuccessfulHandler {
	private BinderInfo			m_binderInfo;	// The profiles root workspace the dialog is running against.
	private GwtTeamingMessages	m_messages;		// Access to Vibe's messages.
	private List<Long>			m_userList;		// The List<Long> of user IDs whose sharing rights are being set.
	private VibeFlowPanel		m_rootPanel;	// The panel holding the dialog's content.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private UserShareRightsDlg() {
		// Initialize the superclass...
		super(false, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.userShareRightsDlgHeader(),	// The dialog's caption.
			this,									// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),				// The dialog's EditCanceledHandler.
			null);									// Create callback data.  Unused. 
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
		// Create and return a panel to hold the dialog's content.
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-userShareRightsDlg-panel");
		return m_rootPanel;
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
//!		...this needs to be implemented...
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
		// Clear the panel that holds the content...
		m_rootPanel.clear();
		
		// ...and recreate it and show the dialog.
//!		...this needs to be implemented...
		m_rootPanel.add(new InlineLabel("...this needs to be implemented..."));
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the user share rights
	 * dialog.
	 */
	private static void runDlgAsync(final UserShareRightsDlg usrDlg, final BinderInfo bi, final List<Long> userList) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				usrDlg.runDlgNow(bi, userList);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the user share rights
	 * dialog.
	 */
	private void runDlgNow(BinderInfo bi, List<Long> userList) {
		// Store the parameter...
		m_binderInfo = bi;
		m_userList   = userList;

		// ...and populate the dialog.
		populateDlgAsync();
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the user share rights dialog and perform some operation on    */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the user share rights dialog
	 * asynchronously after it loads. 
	 */
	public interface UserShareRightsDlgClient {
		void onSuccess(UserShareRightsDlg usrDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the UserShareRightsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters to create an instance of the dialog.
			final UserShareRightsDlgClient usrDlgClient,
			
			// Parameters to initialize and show the dialog.
			final UserShareRightsDlg	usrDlg,
			final BinderInfo			bi,
			final List<Long>			userList) {
		GWT.runAsync(UserShareRightsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_UserShareRightsDlg());
				if (null != usrDlgClient) {
					usrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != usrDlgClient) {
					// Yes!  Create it and return it via the callback.
					UserShareRightsDlg usrDlg = new UserShareRightsDlg();
					usrDlgClient.onSuccess(usrDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(usrDlg, bi, userList);
				}
			}
		});
	}
	
	/**
	 * Loads the UserShareRightsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param usrDlgClient
	 */
	public static void createAsync(UserShareRightsDlgClient usrDlgClient) {
		doAsyncOperation(usrDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the user share rights dialog.
	 * 
	 * @param usrDlg
	 * @param bi
	 * @param userList
	 */
	public static void initAndShow(UserShareRightsDlg usrDlg, BinderInfo bi, List<Long> userList) {
		doAsyncOperation(null, usrDlg, bi, userList);
	}
}
