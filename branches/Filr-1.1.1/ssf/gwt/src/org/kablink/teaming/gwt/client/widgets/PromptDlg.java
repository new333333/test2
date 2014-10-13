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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Implements a prompt dialog that can replace GWT's Windows.prompt()
 * dialog.
 *  
 * @author drfoster@novell.com
 */
public class PromptDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private FlowPanel			m_dlgPanel;		// The panel holding the dialog's content.
	private GwtTeamingMessages	m_messages;		// Access to Vibe's messages.
	private PromptCallback		m_pCallback;	// The callback interface used to interact with the caller.
	private String				m_promptText;	// The text to prompt the user with. 
	private String				m_initialValue;	// The value to initialize the <input> widget with.
	private TextBox 			m_input;		// The <input> widget.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private PromptDlg() {
		// Initialize the super class...
		super(false, true);	// false -> Don't auto hide.  true -> Modal.

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.promptDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Create callback data.  Unused. 
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
		m_dlgPanel.addStyleName("vibe-promptDlg_Panel");
		return m_dlgPanel;
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
		setButtonsEnabled(false);
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_pCallback.canceled();
			}
		});
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
	@Override
	public boolean editSuccessful(Object callbackData) {
		setButtonsEnabled(false);
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_pCallback.applied(m_input.getValue());
			}
		});
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
		return m_input;
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
		// Clear anything already in the dialog's panel.
		m_dlgPanel.clear();

		// Create a horizontal panel to hold the name input widgets...
		VibeFlexTable grid = new VibeFlexTable();
		grid.addStyleName("vibe-promptDlg_Grid");
		m_dlgPanel.add(grid);
		FlexCellFormatter gridCellFmt = grid.getFlexCellFormatter();

		// ...add a label...
		InlineLabel il = new InlineLabel(m_promptText);
		il.addStyleName("vibe-promptDlg_NameLabel");
		grid.setWidget(0, 0, il);
		gridCellFmt.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

		// ...and add an input widget.
		m_input = new TextBox();
		m_input.addStyleName("vibe-promptDlg_NameInput");
		m_input.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				// What key is being pressed?
				switch (event.getNativeEvent().getKeyCode()) {
				case KeyCodes.KEY_ESCAPE:
					// Escape!  Treat it like the user pressed Cancel.
					if (editCanceled()) {
						hide();
					}
					break;
					
				case KeyCodes.KEY_ENTER:
					// Enter!  Treat it like the user pressed OK.
					if (editSuccessful(null)) {
						hide();
					}
					break;
				}
			}
		});
		m_input.setValue(m_initialValue);
		m_input.selectAll();
		grid.setWidget(0, 1, m_input);
		gridCellFmt.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		GwtClientHelper.setFocusDelayed(m_input);
		
		// Finally, show the dialog centered on the screen.
		setButtonsEnabled(true);
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the prompt dialog.
	 */
	private static void runDlgAsync(final PromptDlg pDlg, final PromptCallback pCallback, final String promptText, final String initialValue) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				pDlg.runDlgNow(pCallback, promptText, initialValue);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the prompt dialog.
	 */
	private void runDlgNow(PromptCallback pCallback, String promptText, String initialValue) {
		// Store the parameters...
		m_pCallback    = pCallback;
		m_promptText   = promptText;
		m_initialValue = ((null == initialValue) ? "" : initialValue);

		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Enables/disables the buttons on the dialog.
	 */
	private void setButtonsEnabled(boolean enabled) {
		setOkEnabled(    enabled);
		setCancelEnabled(enabled);
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the prompt dialog and perform some operation on it.           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the prompt dialog
	 * asynchronously after it loads. 
	 */
	public interface PromptDlgClient {
		void onSuccess(PromptDlg pDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the PromptDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final PromptDlgClient pDlgClient,
			
			// initAndShow parameters,
			final PromptDlg			pDlg,
			final PromptCallback	pCallback,
			final String			promptText,
			final String			initialValue) {
		GWT.runAsync(PromptDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_PromptDlg());
				if (null != pDlgClient) {
					pDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != pDlgClient) {
					// Yes!  Create it and return it via the callback.
					PromptDlg pDlg = new PromptDlg();
					pDlgClient.onSuccess(pDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(pDlg, pCallback, promptText, initialValue);
				}
			}
		});
	}
	
	/**
	 * Loads the PromptDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param pDlgClient
	 */
	public static void createAsync(PromptDlgClient pDlgClient) {
		doAsyncOperation(pDlgClient, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the prompt dialog.
	 * 
	 * @param pDlg
	 * @param pCallback
	 * @param promptText
	 * @param initialValue
	 */
	public static void initAndShow(PromptDlg pDlg, PromptCallback pCallback, String promptText, String initialValue) {
		doAsyncOperation(null, pDlg, pCallback, promptText, initialValue);
	}
	
	public static void initAndShow(PromptDlg pDlg, PromptCallback pCallback, String promptText) {
		// Always use the initial form of the method.
		initAndShow(pDlg, pCallback, promptText, null);
	}
}
