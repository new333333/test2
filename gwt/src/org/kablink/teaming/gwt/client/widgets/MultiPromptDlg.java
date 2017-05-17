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

import java.util.ArrayList;
import java.util.List;

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
 * Implements a multiple prompt dialog.
 *  
 * @author drfoster@novell.com
 */
public class MultiPromptDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private FlowPanel			m_dlgPanel;				// The panel holding the dialog's content.
	private GwtTeamingMessages	m_messages;				// Access to Vibe's messages.
	private List<String>		m_promptTextStrings;	// The text strings to prompt the user with. 
	private List<String>		m_initialValues;		// The values to initialize the <input> widgets with.
	private List<TextBox>		m_inputWidgets;			// The <input> widgets.
	private MultiPromptCallback	m_mpCallback;			// The callback interface used to interact with the caller.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MultiPromptDlg() {
		// Initialize the super class...
		super(false, true);	// false -> Don't auto hide.  true -> Modal.

		// ...initialize everything else...
		m_messages     = GwtTeaming.getMessages();
		m_inputWidgets = new ArrayList<TextBox>();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.promptDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Create callback data.  Unused. 
	}

	/*
	 * Returns an ordered List<String> of the responses to the prompts
	 * supplied by the user.
	 */
	private List<String> collectInputValues() {
		List<String> reply = new ArrayList<String>();
		for (TextBox input:  m_inputWidgets) {
			reply.add(input.getValue());
		}
		return reply;
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
		m_dlgPanel.addStyleName("vibe-multiPromptDlg_Panel");
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
				m_mpCallback.canceled();
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
				int errorIndex = m_mpCallback.applied(collectInputValues());
				if (MultiPromptCallback.NO_ERROR == errorIndex) {
					hide();
				}
				else {
					GwtClientHelper.setFocusDelayed(m_inputWidgets.get(errorIndex));
					setButtonsEnabled(true);
				}
			}
		});
		return false;
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
		return (GwtClientHelper.hasItems(m_inputWidgets) ? m_inputWidgets.get(0) : null);
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
		grid.addStyleName("vibe-multiPromptDlg_Grid");
		m_dlgPanel.add(grid);
		FlexCellFormatter gridCellFmt = grid.getFlexCellFormatter();

		// Clear an previous list of <INPUT> widgets that we're
		// tracking.
		m_inputWidgets.clear();
		
		int count = m_promptTextStrings.size();
		int row = (-1);
		for (int i = 0; i < count; i += 1) {
			// ...add a label...
			InlineLabel il = new InlineLabel(m_promptTextStrings.get(i));
			il.addStyleName("vibe-multiPromptDlg_NameLabel");
			row += 1;
			grid.setWidget(row, 0, il);
			gridCellFmt.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
	
			// ...and add an input widget.
			TextBox input = new TextBox();
			m_inputWidgets.add(input);
			input.addStyleName("vibe-multiPromptDlg_NameInput");
			input.addKeyDownHandler(new KeyDownHandler() {
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
			input.setValue(m_initialValues.get(i));
			input.selectAll();
			grid.setWidget(row, 1, input);
			gridCellFmt.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		}

		// Start with the input focus in the first <INPUT>.
		GwtClientHelper.setFocusDelayed(m_inputWidgets.get(0));
		
		// Finally, show the dialog centered on the screen.
		setButtonsEnabled(true);
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the multiple prompt
	 * dialog.
	 */
	private static void runDlgAsync(final MultiPromptDlg mpDlg, final MultiPromptCallback mpCallback, final List<String> promptTextStrings, final List<String> initialValues) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				mpDlg.runDlgNow(mpCallback, promptTextStrings, initialValues);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the multiple prompt
	 * dialog.
	 */
	private void runDlgNow(MultiPromptCallback mpCallback, List<String> promptTextStrings, List<String> initialValues) {
		// Store the parameters...
		m_mpCallback        = mpCallback;
		m_promptTextStrings = promptTextStrings;
		m_initialValues     = initialValues;

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
	/* the multiple prompt dialog and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the multiple prompt dialog
	 * asynchronously after it loads. 
	 */
	public interface MultiPromptDlgClient {
		void onSuccess(MultiPromptDlg mpDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the MultiPromptDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final MultiPromptDlgClient mpDlgClient,
			
			// initAndShow parameters,
			final MultiPromptDlg		mpDlg,
			final MultiPromptCallback	mpCallback,
			final List<String>			promptTextStrings,
			final List<String>			initialValues) {
		GWT.runAsync(MultiPromptDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_MultiPromptDlg());
				if (null != mpDlgClient) {
					mpDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != mpDlgClient) {
					// Yes!  Create it and return it via the callback.
					MultiPromptDlg mpDlg = new MultiPromptDlg();
					mpDlgClient.onSuccess(mpDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(mpDlg, mpCallback, promptTextStrings, initialValues);
				}
			}
		});
	}
	
	/**
	 * Loads the MultiPromptDlg split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param mpDlgClient
	 */
	public static void createAsync(MultiPromptDlgClient mpDlgClient) {
		doAsyncOperation(mpDlgClient, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the multiple prompt dialog.
	 * 
	 * @param mpDlg
	 * @param mpCallback
	 * @param promptTextStrings
	 * @param initialValues
	 */
	public static void initAndShow(MultiPromptDlg mpDlg, MultiPromptCallback mpCallback, List<String> promptTextStrings, List<String> initialValues) {
		doAsyncOperation(null, mpDlg, mpCallback, promptTextStrings, initialValues);
	}
}
