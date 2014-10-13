/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a confirmation dialog that can replace GWT's
 * Windows.confirm() dialog.
 *  
 * @author drfoster@novell.com
 */
public class ConfirmDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private boolean					m_dialogReady;			// Set true once the dialog is ready for display.
	private ConfirmCallback			m_confirmCallback;		// Callback interface to let the caller know what's going on.
	private GwtTeamingMessages		m_messages;				// Access to Vibe's messages.
	private String					m_confirmationMsg;		// Base message confirmation is being requested for.
	private VibeVerticalPanel		m_vp;					// The panel holding the dialog's content.
	private Widget					m_additionalWidgets;	// Any additional widgets that the caller wants confirmation on.
	
	// The following controls whether we use a simple Window.confirm()
	// by default if we don't have any additional widgets to include.
	//		true  -> We do.
	//		false -> We always use our custom dialog.
	// Note that this can be overridden by explicitly specifying a
	// choice when calling ConfirmDlg.initAndShow().
	private final static boolean USE_SIMPLE_CONFIRM_WHEN_POSSIBLE	= false;

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ConfirmDlg(DlgButtonMode buttons) {
		// Initialize the superclass...
		super(false, true, buttons);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.confirmDlgHeader(),	// The dialog's header.
			this,							// The dialog's EditSuccessfulHandler.
			this,							// The dialog's EditCanceledHandler.
			null);							// Create callback data.  Unused. 
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
		m_vp = new VibeVerticalPanel(null, null);
		m_vp.addStyleName("vibe-confirmDlg-rootPanel");
		return m_vp;
	}

	/*
	 * Marks the dialog as being ready and shows it.
	 */
	private void doDialogReady() {
		m_dialogReady = true;
		show(true);
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_confirmCallback.dialogReady();
			}
		});
	}
	
	/**
	 * Called if the user selects the dialog's Cancel button.
	 */
	@Override
	public boolean editCanceled() {
		// Tell the caller the confirmation was rejected...
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_confirmCallback.rejected();
			}
		});
		
		// ...and return true to close the dialog.
		return true;
	}

	/**
	 * Called is the user selects the dialog's Ok button.
	 * 
	 * @param callbackData
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Tell the caller the confirmation was accepted...
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_confirmCallback.accepted();
			}
		});
		
		// ...and return true to close the dialog.
		return true;
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
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_vp.clear();

		// Add a label with the base confirmation message.
		Label l = new Label();
		l.getElement().setInnerHTML( m_confirmationMsg );
		l.addStyleName("vibe-confirmDlg-confirmationTxt");
		m_vp.add(l);

		// If the caller supplied additional widgets that require
		// confirmation...
		if (null != m_additionalWidgets) {
			// ...and them to the dialog.
			l.addStyleName("vibe-confirmDlg-confirmationTxtWithAdditional");
			m_additionalWidgets.addStyleName("vibe-confirmDlg-confirmationAdditional");
			m_vp.add(m_additionalWidgets);
		}

		// Finally, mark the dialog as being ready and show it.
		doDialogReady();
	}
	
	/*
	 * Asynchronously runs the given instance of the confirmation
	 * dialog.
	 */
	private static void runDlgAsync(final ConfirmDlg cDlg, final ConfirmCallback cCB, final String confirmationMsg, final Widget additionalWidgets, final boolean useSimpleConfirmWhenPossible) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cDlg.runDlgNow(cCB, confirmationMsg, additionalWidgets, useSimpleConfirmWhenPossible);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the confirmation
	 * dialog.
	 */
	private void runDlgNow(ConfirmCallback cCB, String confirmationMsg, Widget additionalWidgets, boolean useSimpleConfirmWhenPossible) {
		// Store the parameters.
		m_confirmCallback   = cCB;
		m_confirmationMsg   = confirmationMsg;
		m_additionalWidgets = additionalWidgets;

		// If there are no additional widgets, are we supposed to use
		// a simple Window.confirm()?
		if (useSimpleConfirmWhenPossible && (null == additionalWidgets)) {
			// Yes!  Use it.
			m_confirmCallback.dialogReady();
			if (Window.confirm(confirmationMsg))
			     m_confirmCallback.accepted();
			else m_confirmCallback.rejected();
		}
		
		else {
			// No, we can't use a simply Window.confirm().  Populate
			// and run the dialog.
			populateDlgAsync();
		}
	}

	/**
	 * Shows the dialog if it's ready.
	 * 
	 * Overrides the DlgBox.show() method.
	 */
	@Override
	public void show() {
		// If the dialog is ready to be shown...
		if (m_dialogReady) {
			// ...pass this on to the suer class.
			super.show();
		}
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the confirmation dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the confirmation dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfirmDlgClient {
		void onSuccess(ConfirmDlg cDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ConfirmDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ConfirmDlgClient	cDlgClient,
			final DlgButtonMode		buttons,
			
			// initAndShow parameters,
			final ConfirmDlg		cDlg,
			final ConfirmCallback	cCB,
			final String			confirmationMsg,
			final Widget			additionalWidgets,
			final boolean			useSimpleConfirmWhenPossible) {
		GWT.runAsync(ConfirmDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ConfirmDlg());
				if (null != cDlgClient) {
					cDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cDlgClient) {
					// Yes!  Create it and return it via the callback.
					ConfirmDlg cDlg = new ConfirmDlg(buttons);
					cDlgClient.onSuccess(cDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cDlg, cCB, confirmationMsg, additionalWidgets, useSimpleConfirmWhenPossible);
				}
			}
		});
	}
	
	/**
	 * Loads the ConfirmDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param cDlgClient
	 * @param buttons
	 */
	public static void createAsync(ConfirmDlgClient cDlgClient, DlgButtonMode buttons) {
		// Invoke the appropriate asynchronous operation.
		doAsyncOperation(cDlgClient, buttons, null, null, null, null, false);
	}
	
	public static void createAsync(ConfirmDlgClient cDlgClient) {
		// Always use the initial form of the method.
		createAsync(cDlgClient, DlgButtonMode.YesNo);
	}
	
	/**
	 * Initializes and shows the confirmation dialog.
	 * 
	 * @param cDlg
	 * @param cCB
	 * @param confirmationMsg
	 * @param additionalWidgets
	 * @param useSimpleConfirmWhenPossible
	 */
	public static void initAndShow(ConfirmDlg cDlg, ConfirmCallback cCB, String confirmationMsg, Widget additionalWidgets, boolean useSimpleConfirmWhenPossible) {
		// Invoke the appropriate asynchronous operation.
		doAsyncOperation(null, null, cDlg, cCB, confirmationMsg, additionalWidgets, useSimpleConfirmWhenPossible);
	}
	
	public static void initAndShow(ConfirmDlg cDlg, ConfirmCallback cCB, String confirmationMsg, Widget additionalWidgets) {
		// Always use the initial form of the method.
		initAndShow(cDlg, cCB, confirmationMsg, additionalWidgets, USE_SIMPLE_CONFIRM_WHEN_POSSIBLE);
	}
	
	public static void initAndShow(ConfirmDlg cDlg, ConfirmCallback cCB, String confirmationMsg) {
		// Always use the initial form of the method.
		initAndShow(cDlg, cCB, confirmationMsg, null, USE_SIMPLE_CONFIRM_WHEN_POSSIBLE);
	}
	
	public static void initAndShow(ConfirmDlg cDlg, ConfirmCallback cCB, String confirmationMsg, boolean useSimpleConfirmWhenPossible) {
		// Always use the initial form of the method.
		initAndShow(cDlg, cCB, confirmationMsg, null, useSimpleConfirmWhenPossible);
	}
}
