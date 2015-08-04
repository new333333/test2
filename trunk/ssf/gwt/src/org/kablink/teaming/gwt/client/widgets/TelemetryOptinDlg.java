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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.SetTelemetryOptinEnabledCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements the telemetry optin dialog.
 *  
 * @author drfoster@novell.com
 */
public class TelemetryOptinDlg extends DlgBox implements EditCanceledHandler, EditSuccessfulHandler {
	private CheckBox			m_telemetryOptinEnabledCB;	// The optin checkbox.
	private GwtTeamingMessages	m_messages;					// Access to Vibe's messages.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TelemetryOptinDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Ok);	// false -> Not auto hide, true -> Modal.

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.telemetryOptinDlgHeader(),	// The dialog's caption.
			this,									// The dialog's EditSuccessfulHandler.
			this,									// The dialog's EditCanceledHandler.
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
		// Create a panel to hold the dialog's content...
		VerticalPanel vp = new VibeVerticalPanel(null, null);
		vp.addStyleName("vibe-telemetryOptinDlg-panel");
		
		// ...add a hint about what's happening at the top of the
		// ...dialog...
		Label hint = new Label(m_messages.telemetryOptinDlgHint(m_messages.companyNovell(), GwtClientHelper.getProductName()));
		hint.addStyleName("vibe-telemetryOptinDlg-hint");
		vp.add(hint);

		// ...add the checkbox for them to optin...
		m_telemetryOptinEnabledCB = new CheckBox(m_messages.telemetryOptinDlgCheckBoxLabel());
		m_telemetryOptinEnabledCB.addStyleName("vibe-telemetryOptinDlg-checkbox");
		vp.add(m_telemetryOptinEnabledCB);

		// ...and return the panel.
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
	@Override
	public boolean editCanceled() {
		// Should never get here as there should be no way to simply
		// close the dialog.
		GwtClientHelper.deferredAlert(m_messages.telemetryOptinDlg_InternalError_CantCancel());
		
		// Return false to leave the dialog open.
		return false;
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
		// Save the state of the telemetry optin setting.
		SetTelemetryOptinEnabledCmd cmd = new SetTelemetryOptinEnabledCmd(m_telemetryOptinEnabledCB.getValue());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetTelemetryOptinEnabled());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Saved!  Simply hide the dialog.
				hide();
			}
		});
		
		// Return false to leave the dialog open.  It will get closed
		// after we save the state of the optin.
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
		return m_telemetryOptinEnabledCB;
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
		// Clear the telemetry optin checkbox and show the dialog.
		m_telemetryOptinEnabledCB.setValue(false);
		center();
	}
	
	/*
	 * Asynchronously runs the given instance of the select CSV
	 * delimiter dialog.
	 */
	private static void runDlgAsync(final TelemetryOptinDlg toDlg) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				toDlg.runDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the select CSV
	 * delimiter dialog.
	 */
	private void runDlgNow() {
		// Make sure the dialog can't be closed (Ok only.)
		hideCloseImg();
		
		// ...and populate the dialog.
		populateDlgAsync();
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the telemetry optin dialog and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the telemetry optin
	 * dialog asynchronously after it loads. 
	 */
	public interface TelemetryOptinDlgClient {
		void onSuccess(TelemetryOptinDlg toDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the TelemetryOptinDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters to create an instance of the dialog.
			final TelemetryOptinDlgClient toDlgClient,
			
			// Parameters to initialize and show the dialog.
			final TelemetryOptinDlg	toDlg) {
		GWT.runAsync(TelemetryOptinDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_TelemetryOptinDlg());
				if (null != toDlgClient) {
					toDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != toDlgClient) {
					// Yes!  Create it and return it via the callback.
					TelemetryOptinDlg toDlg = new TelemetryOptinDlg();
					toDlgClient.onSuccess(toDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(toDlg);
				}
			}
		});
	}
	
	/**
	 * Loads the TelemetryOptinDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param toDlgClient
	 */
	public static void createAsync(TelemetryOptinDlgClient toDlgClient) {
		doAsyncOperation(toDlgClient, null);
	}
	
	/**
	 * Initializes and shows the telemetry optin dialog.
	 * 
	 * @param toDlg
	 */
	public static void initAndShow(TelemetryOptinDlg toDlg) {
		doAsyncOperation(null, toDlg);
	}
}
