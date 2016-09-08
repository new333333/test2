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
import org.kablink.teaming.gwt.client.rpc.shared.SetTelemetryTier2EnabledCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements the telemetry tier 2 dialog.
 *  
 * @author drfoster@novell.com
 */
public class TelemetryTier2Dlg extends DlgBox implements EditCanceledHandler, EditSuccessfulHandler {
	private GwtTeamingMessages	m_messages;	// Access to localized strings.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TelemetryTier2Dlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Ok);	// false -> Not auto hide, true -> Modal.

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.telemetryTier2DlgHeader(),	// The dialog's caption.
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
		vp.addStyleName("vibe-telemetryTier2Dlg-panel");
		
		// ...add the hints about what's happening to the dialog...
		Label hint = new Label(m_messages.telemetryTier2DlgHint1(GwtClientHelper.getProductName()));
		hint.addStyleName("vibe-telemetryTier2Dlg-hint");
		vp.add(hint);

		hint = new Label(m_messages.telemetryTier2DlgHint2());
		hint.addStyleName("vibe-telemetryTier2Dlg-hint marginTop15px");
		vp.add(hint);

		hint = new Label(m_messages.telemetryTier2DlgHint3());
		hint.addStyleName("vibe-telemetryTier2Dlg-hint marginTop15px");
		vp.add(hint);

		hint = new Label(m_messages.telemetryTier2DlgHint4a(GwtClientHelper.getProductName()));
		hint.addStyleName("vibe-telemetryTier2Dlg-hint margintop3px");
		vp.add(hint);

		hint = new Label(m_messages.telemetryTier2DlgHint4b());
		hint.addStyleName("vibe-telemetryTier2Dlg-hint");
		vp.add(hint);

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
		GwtClientHelper.deferredAlert(m_messages.telemetryTier2Dlg_InternalError_CantCancel());
		
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
		// Save the state of the telemetry tier 2 setting.
		SetTelemetryTier2EnabledCmd cmd = new SetTelemetryTier2EnabledCmd(true);	// true -> Tier 2 telemetry is ALWAYS enabled.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetTelemetryTier2Enabled());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Saved!  Simply hide the dialog.
				hide();
			}
		});
		
		// Return false to leave the dialog open.  It will get closed
		// after we save the state of the tier 2 data capture.
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
		// Simply show the dialog.
		center();
	}
	
	/*
	 * Asynchronously runs the given instance of the select CSV
	 * delimiter dialog.
	 */
	private static void runDlgAsync(final TelemetryTier2Dlg tt2Dlg) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				tt2Dlg.runDlgNow();
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
	/* the telemetry tier 2 dialog and perform some operation on it. */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the telemetry tier 2
	 * dialog asynchronously after it loads. 
	 */
	public interface TelemetryTier2DlgClient {
		void onSuccess(TelemetryTier2Dlg tt2Dlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the TelemetryTier2Dlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters to create an instance of the dialog.
			final TelemetryTier2DlgClient tt2DlgClient,
			
			// Parameters to initialize and show the dialog.
			final TelemetryTier2Dlg	tt2Dlg) {
		GWT.runAsync(TelemetryTier2Dlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_TelemetryTier2Dlg());
				if (null != tt2DlgClient) {
					tt2DlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != tt2DlgClient) {
					// Yes!  Create it and return it via the callback.
					TelemetryTier2Dlg tt2Dlg = new TelemetryTier2Dlg();
					tt2DlgClient.onSuccess(tt2Dlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(tt2Dlg);
				}
			}
		});
	}
	
	/**
	 * Loads the TelemetryTier2Dlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param tt2DlgClient
	 */
	public static void createAsync(TelemetryTier2DlgClient tt2DlgClient) {
		doAsyncOperation(tt2DlgClient, null);
	}
	
	/**
	 * Initializes and shows the telemetry tier 2 dialog.
	 * 
	 * @param tt2Dlg
	 */
	public static void initAndShow(TelemetryTier2Dlg tt2Dlg) {
		doAsyncOperation(null, tt2Dlg);
	}
}
