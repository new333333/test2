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

import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements the select CSV delimiter dialog.
 *  
 * @author drfoster@novell.com
 */
public class SelectCSVDelimiterDlg extends DlgBox implements EditCanceledHandler, EditSuccessfulHandler {
	private CSVDelimiterCallback		m_csvCallback;		// Callback when the user OK's or cancels the dialog.
	private GwtTeamingMessages			m_messages;			// Access to Vibe's messages.
	private Map<String, RadioButton>	m_csvRBMap;			// Map<String, RadioButton> of the CSV delimiter radio buttons.
	private TextBox						m_customCSVInput;	// <INPUT> widget for the user to enter a custom CSV delimiter.
	private VerticalPanel				m_vp;				// The panel that contains the dialog's contents.

	// Defines the maximum length of a custom CSV delimiter string.
	private final static int	MAX_CUSTOM_DELIMITER_LENGTH	= 5;
	
	// Defines a key into the m_csvRBMap Map that indicates a custom
	// CSV delimiter string.  This string CANNOT be one of the strings
	// defined in AVAILABLE_DELIMITERS.
	private final static String	CUSTOM_CSV_MAP_KEY	= " ";
	
	// Defines the list of CSV delimiter characters we support by
	// default. 
	private final static String[] AVAILABLE_DELIMITERS = new String[] {
		",",
		":",
		"|",
		"!",
		"@",
		"#",
		"$",
		"%",
		"^",
		"&",
		"*",
		"-",
		"_",
		"+",
		"=",
	};
	
	/**
	 * Interface implemented by callers of this dialog for the dialog
	 * to return the selected CSV delimiter through.
	 */
	public interface CSVDelimiterCallback {
		public void onCancel();					// Called if the select CSV delimiter dialog is canceled.
		public void onSelect(String csvDelim);	// Called if the select CSV delimiter dialog has a valid selection and the user presses OK.
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private SelectCSVDelimiterDlg() {
		// Initialize the superclass...
		super(false, true);	// false -> Not auto hide, true -> Modal.

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_csvRBMap = new HashMap<String, RadioButton>();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.selectCSVDelimiterDlgHeader(),	// The dialog's caption.
			this,										// The dialog's EditSuccessfulHandler.
			this,										// The dialog's EditCanceledHandler.
			null);										// Create callback data.  Unused. 
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
		m_vp.addStyleName("vibe-selectCSVDelimiterDlg-panel");
		return m_vp;
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// Tell the caller the dialog was canceled and hide the
				// dialog.
				m_csvCallback.onCancel();
				hide();
			}
		});
		
		// Return false to leave the dialog open.  It will get closed
		// after we tell the caller about the cancel
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// Validate the user's selection...
				String csvDelim = ",";
				for (String rbKey:  m_csvRBMap.keySet()) {
					// If this radio button isn't checked...
					RadioButton rb = m_csvRBMap.get(rbKey);
					if (!(rb.getValue())) {
						// ...skip it.
						continue;
					}

					// Is this the custom CSV radio button?
					if (CUSTOM_CSV_MAP_KEY.equals(rbKey)) {
						// Yes!  Did the user enter something valid?
						csvDelim = m_customCSVInput.getValue();
						if (null == csvDelim) csvDelim = "";
						csvDelim = csvDelim.trim();
						
						String errMsg;
						if      (0 == csvDelim.length())  errMsg = m_messages.selectCSVDelimiterDlgErr_Blank(MAX_CUSTOM_DELIMITER_LENGTH);
						else if (csvDelim.contains("\\")) errMsg = m_messages.selectCSVDelimiterDlgErr_Backslash();
						else if (csvDelim.contains("/"))  errMsg = m_messages.selectCSVDelimiterDlgErr_Slash();
						else                              errMsg = null;
						if (null != errMsg) {
							// No!  Tell them about the problem and
							// bail.
							GwtClientHelper.deferredAlert(errMsg);
							return;
						}
					}
					
					else {
						// No, this isn't the custom CSV radio button!
						// The key into the Map is the delimiter.
						csvDelim = rbKey;
						break;
					}
				}
				
				// ...and tell the caller the CSV delimiter was
				// ...specified and hide the dialog.
				m_csvCallback.onSelect(csvDelim);
				hide();
			}
		});
		
		// Return false to leave the dialog open.  It will get closed
		// after we tell the caller about the selected CSV delimiter.
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
		// Create a panel to hold the content...
		m_vp.clear();
		FlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-selectCSVDelimiterDlg-content");
		m_vp.add(fp);

		// ...add a FlexTable for the CSV delimiter options...
		FlexTable csvGrid = new VibeFlexTable();
		FlexCellFormatter csvGridCellFormatter = csvGrid.getFlexCellFormatter();
		csvGrid.addStyleName("vibe-selectCSVDelimiterDlg-grid");
		csvGrid.setCellPadding(0);
		csvGrid.setCellSpacing(0);
		fp.add(csvGrid);
		
		// ...add a hint at the top...
		int row = csvGrid.getRowCount();
		Label l = new Label(m_messages.selectCSVDelimiterDlgHint1());
		l.addStyleName("vibe-selectCSVDelimiterDlg-hint");
		csvGrid.insertRow(row);
		csvGrid.setWidget(row, 0, l);
		csvGridCellFormatter.setColSpan(row, 0, 2);
		csvGridCellFormatter.setWordWrap(row, 0, true);

		// ...add the specific CSV delimiter options...
		int rbIndex = 0;
		RadioButton rb;
		m_csvRBMap.clear();
		for (String csvDelim:  AVAILABLE_DELIMITERS) {
			rb = new RadioButton("csvDelimiters", csvDelim);
			rb.addStyleName("vibe-selectCSVDelimiterDlg-radio");
			rb.setValue(0 == rbIndex);	// Default to the first one.
			row = csvGrid.getRowCount();
			csvGrid.insertRow(row);
			csvGrid.setWidget(row, 0, rb);
			csvGridCellFormatter.setColSpan(row, 0, 2);
			m_csvRBMap.put(csvDelim, rb);
			
			rbIndex += 1;
		}

		// ...add a row with a hint about a custom CSV delimiter...
		row = csvGrid.getRowCount();
		l = new Label(m_messages.selectCSVDelimiterDlgHint2(MAX_CUSTOM_DELIMITER_LENGTH));
		l.addStyleName("vibe-selectCSVDelimiterDlg-hint vibe-selectCSVDelimiterDlg-hint2");
		csvGrid.insertRow(row);
		csvGrid.setWidget(row, 0, l);
		csvGridCellFormatter.setColSpan(row, 0, 2);
		csvGridCellFormatter.setWordWrap(row, 0, true);

		// ...and add a custom CSV delimiter option.
		final RadioButton customCSVDelimiterRB = new RadioButton("csvDelimiters", m_messages.selectCSVDelimiterDlgCustom());
		customCSVDelimiterRB.addStyleName("vibe-selectCSVDelimiterDlg-radio");
		row = csvGrid.getRowCount();
		csvGrid.insertRow(row);
		csvGrid.setWidget(row, 0, customCSVDelimiterRB);
		m_customCSVInput = new TextBox();
		m_customCSVInput.setMaxLength(MAX_CUSTOM_DELIMITER_LENGTH);
		m_customCSVInput.addStyleName("vibe-selectCSVDelimiterDlg_customCSVInput");
		m_customCSVInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				// What key is being pressed?
				switch (event.getNativeEvent().getKeyCode()) {
				case KeyCodes.KEY_ESCAPE:
					// Treat escape like the user pressed Cancel.
					editCanceled();
					break;
					
				case KeyCodes.KEY_ENTER:
					// Treat enter like the user pressed OK.
					editSuccessful(null);
					break;
				}
			}
		});
		m_customCSVInput.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				// When the <INPUT> gets the focus, force its radio
				// button to be selected.
				customCSVDelimiterRB.setValue(true);
			}
		});
		csvGrid.setWidget(row, 1, m_customCSVInput);
		csvGrid.getCellFormatter().setWidth(row, 1, "100%");
		m_csvRBMap.put(CUSTOM_CSV_MAP_KEY, customCSVDelimiterRB);

		// Finally, show the dialog.
		center();
	}
	
	/*
	 * Asynchronously runs the given instance of the select CSV
	 * delimiter dialog.
	 */
	private static void runDlgAsync(final SelectCSVDelimiterDlg csvDlg, final CSVDelimiterCallback csvCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				csvDlg.runDlgNow(csvCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the select CSV
	 * delimiter dialog.
	 */
	private void runDlgNow(CSVDelimiterCallback csvCallback) {
		// Store the parameter...
		m_csvCallback = csvCallback;
		
		// ...and populate the dialog.
		populateDlgAsync();
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the select CSV delimiter dialog and perform some operation on */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the select CSV delimiter
	 * dialog asynchronously after it loads. 
	 */
	public interface SelectCSVDelimiterDlgClient {
		void onSuccess(SelectCSVDelimiterDlg csvDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the SelectCSVDelimiterDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters to create an instance of the dialog.
			final SelectCSVDelimiterDlgClient csvDlgClient,
			
			// Parameters to initialize and show the dialog.
			final SelectCSVDelimiterDlg	csvDlg,
			final CSVDelimiterCallback	csvCallback) {
		GWT.runAsync(SelectCSVDelimiterDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_SelectCSVDelimiterDlg());
				if (null != csvDlgClient) {
					csvDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != csvDlgClient) {
					// Yes!  Create it and return it via the callback.
					SelectCSVDelimiterDlg csvDlg = new SelectCSVDelimiterDlg();
					csvDlgClient.onSuccess(csvDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(csvDlg, csvCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the SelectCSVDelimiterDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param csvDlgClient
	 */
	public static void createAsync(SelectCSVDelimiterDlgClient csvDlgClient) {
		doAsyncOperation(csvDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the select CSV delimiter dialog.
	 * 
	 * @param csvDlg
	 */
	public static void initAndShow(SelectCSVDelimiterDlg csvDlg, CSVDelimiterCallback csvCallback) {
		doAsyncOperation(null, csvDlg, csvCallback);
	}
}
