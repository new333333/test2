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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Implements an alert dialog that can replace GWT's Windows.alert()
 * dialog.
 *  
 * @author drfoster@novell.com
 */
public class AlertDlg extends DlgBox {
	private FlowPanel			m_dlgPanel;			// The panel holding the dialog's content.
	private GwtTeamingMessages	m_messages;			// Access to Vibe's messages.
	private String				m_alertText;		// The text to alert the user with.
	private UIObject			m_showRelativeTo;	// UIObject to show the dialog relative to.  null -> Center it.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AlertDlg(boolean autoHide, boolean modal) {
		// Initialize the super class...
		super(autoHide, modal, DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.alertDlgHeader(GwtClientHelper.getProductName()),
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
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
		// Create and return a FlowPanel to hold the dialog's content.
		m_dlgPanel = new VibeFlowPanel();
		m_dlgPanel.addStyleName("vibe-alertDlg_Panel");
		return m_dlgPanel;
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
		// Unused.
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
		// Clear anything already in the dialog's panel.
		m_dlgPanel.clear();

		// Create a FlexTable to hold the widgets...
		VibeFlexTable grid = new VibeFlexTable();
		grid.addStyleName("vibe-alertDlg_Grid");
		m_dlgPanel.add(grid);
		FlexCellFormatter gridCellFmt = grid.getFlexCellFormatter();

		// ...and add the alert text.  The following will handle
		// ...embedded \n characters and other escapes.
		grid.setHTML(0, 0, new SafeHtmlBuilder().appendEscapedLines(m_alertText).toSafeHtml());
		gridCellFmt.addStyleName(0, 0, "vibe-alertDlg_NameLabel");
		gridCellFmt.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

		// Finally, show the dialog.
		if (null == m_showRelativeTo)
		     center();
		else showRelativeTo(m_showRelativeTo);
	}
	
	/*
	 * Asynchronously runs the given instance of the alert dialog.
	 */
	private static void runDlgAsync(final AlertDlg aDlg, final String alertText, final UIObject showRelativeTo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				aDlg.runDlgNow(alertText, showRelativeTo);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the alert dialog.
	 */
	private void runDlgNow(String alertText, final UIObject showRelativeTo) {
		// Store the parameters...
		m_alertText      = alertText;
		m_showRelativeTo = showRelativeTo;

		// ...and start populating the dialog.
		populateDlgAsync();
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the alert dialog and perform some operation on it.            */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the alert dialog
	 * asynchronously after it loads. 
	 */
	public interface AlertDlgClient {
		void onSuccess(AlertDlg aDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the AlertDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync() parameters.
			final AlertDlgClient	aDlgClient,
			final boolean			autoHide,
			final boolean			modal,
			
			// initAndShow() parameters,
			final AlertDlg	aDlg,
			final String	alertText,
			final UIObject	showRelativeTo) {
		GWT.runAsync(AlertDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_AlertDlg());
				if (null != aDlgClient) {
					aDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != aDlgClient) {
					// Yes!  Create it and return it via the callback.
					AlertDlg aDlg = new AlertDlg(autoHide, modal);
					aDlgClient.onSuccess(aDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(aDlg, alertText, showRelativeTo);
				}
			}
		});
	}
	
	/**
	 * Loads the AlertDlg split point and returns an instance of it via
	 * the callback.
	 * 
	 * @param aDlgClient
	 * @param autoHide
	 * @param modal
	 */
	public static void createAsync(AlertDlgClient aDlgClient, boolean autoHide, boolean modal) {
		doAsyncOperation(aDlgClient, autoHide, modal, null, null, null);
	}
	
	public static void createAsync(AlertDlgClient aDlgClient) {
		// Always use the initial form of the method.
		createAsync(aDlgClient, false, true);	// false -> Don't auto hide.  true -> Modal.
	}
	
	/**
	 * Initializes and shows the alert dialog.
	 * 
	 * @param aDlg
	 * @param alertText
	 * @param showRelativeTo
	 */
	public static void initAndShow(AlertDlg aDlg, String alertText, UIObject showRelativeTo) {
		doAsyncOperation(null, false, false, aDlg, alertText, showRelativeTo);
	}
	
	public static void initAndShow(AlertDlg aDlg, String alertText) {
		// Always use the initial form of the method.
		initAndShow(aDlg, alertText, null);
	}
}
