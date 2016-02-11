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

import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Implements Vibe's multi-error alert dialog.
 *  
 * @author drfoster@novell.com
 */
public class MultiErrorAlertDlg extends DlgBox implements EditCanceledHandler, EditSuccessfulHandler {
	private boolean					m_dialogReady;		// Set true once the dialog is ready for display.
	private ConfirmCallback			m_confirmCallback;	// Callback interface to let the caller know what's going on when in confirmation mode.
	private GwtTeamingImageBundle	m_images;			// Access to Vibe's images.
	private GwtTeamingMessages		m_messages;			// Access to Vibe's messages.
	private List<ErrorInfo>			m_errors;			//
	private String					m_baseError;		//
	private VibeFlowPanel			m_fp;				// The panel holding the dialog's content.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MultiErrorAlertDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.OkCancel);

		// ...initialize everything else...
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",		// The dialog's header is set when the dialog is shown.
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
		// Create and return a panel to hold the dialog's content.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-multiErrorAlertDlg-rootPanel");
		return m_fp;
	}

	/*
	 * Marks the dialog as being ready and shows it.
	 */
	private void doDialogReady() {
		m_dialogReady = true;
		show(true);
		if (null != m_confirmCallback) {
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					m_confirmCallback.dialogReady();
				}
			});
		}
	}
	
	/**
	 * Called if the user selects the dialog's Cancel button.
	 */
	@Override
	public boolean editCanceled() {
		// If we're running in confirmation mode...
		if (null != m_confirmCallback) {
			// ...tell the caller the confirmation was rejected...
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					m_confirmCallback.rejected();
				}
			});
		}
		
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
		// If we're running in confirmation mode...
		if (null != m_confirmCallback) {
			// ...tell the caller the confirmation was accepted...
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					m_confirmCallback.accepted();
				}
			});
		}
		
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
		m_fp.clear();

		// ...create a grid for the dialog's contents...
		FlexTable grid = new FlexTable();
		grid.addStyleName("vibe-multiErrorAlertDlg-grid");
		FlexCellFormatter fcf = grid.getFlexCellFormatter();
		m_fp.add(grid);

		// ...add a warning image...
		Image warnImg = GwtClientHelper.buildImage(m_images.warningIcon16());
		warnImg.addStyleName("vibe-multiErrorAlertDlg-warnImg");
		grid.setWidget(          0, 0, warnImg);
		fcf.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

		// ...add the base error message...
		InlineLabel il = new InlineLabel(m_baseError);
		il.addStyleName("vibe-multiErrorAlertDlg-baseError");
		grid.setWidget(  0, 1, il);
		fcf.setColSpan(  0, 1, 2 );
		fcf.addStyleName(0, 1, "vibe-multiErrorAlertDlg-baseErrorCell");

		// ...scan the individual errors...
		for (ErrorInfo error:  m_errors) {
			// ...adding a spacer...
			int row = grid.getRowCount();
			Image spacerImg = new Image(m_images.spacer1px());
			spacerImg.addStyleName("vibe-multiErrorAlertDlg-spacer");
			spacerImg.setWidth("8px");
			grid.setWidget(row, 1, spacerImg);

			// ...and label for each.
			il = new InlineLabel(error.getMessage());
			il.addStyleName("vibe-multiErrorAlertDlg-eachError");
			il.setWordWrap(false);
			grid.setWidget(row, 2, il);
		}

		// Finally, mark the dialog as being ready and show it.
		doDialogReady();
	}
	
	/*
	 * Asynchronously runs the given instance of the multi-error alert
	 * dialog.
	 */
	private static void runDlgAsync(final MultiErrorAlertDlg meaDlg, final String baseError, final List<ErrorInfo> errors, final ConfirmCallback confirmCallback, final DlgButtonMode confirmButtons) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				meaDlg.runDlgNow(baseError, errors, confirmCallback, confirmButtons);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the multi-error alert
	 * dialog.
	 */
	private void runDlgNow(String baseError, List<ErrorInfo> errors, ConfirmCallback confirmCallback, DlgButtonMode confirmButtons) {
		// Store the parameters...
		m_confirmCallback = confirmCallback;
		m_baseError 	  = baseError;
		m_errors          = errors;

		// ...set the DlgBox contents...
		Button cancelButton = getCancelButton();
		Button okButton     = getOkButton();
		if (null == m_confirmCallback) {
			setCaption(m_messages.multiErrorAlertDlgHeaderError());
			cancelButton.setText(m_messages.close());
			okButton.setVisible( false             );
		}
		else {
			if (null == confirmButtons) {
				confirmButtons = DlgButtonMode.YesNo;
			}
			String cancelText;
			String okText;
			switch (confirmButtons) {
			default:
			case YesNo:    okText = m_messages.yes(); cancelText = m_messages.no();     break;
			case OkCancel: okText = m_messages.ok();  cancelText = m_messages.cancel(); break;
			}
			setCaption(m_messages.multiErrorAlertDlgHeaderConfirm());
			cancelButton.setText(cancelText);
			okButton.setText(    okText    );
			okButton.setVisible( true      );
		}
		
		// ...and populate the dialog.
		populateDlgAsync();
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
	/* the multi-error alert dialog and perform some operation on    */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the multi-error alert dialog
	 * asynchronously after it loads. 
	 */
	public interface MultiErrorAlertDlgClient {
		void onSuccess(MultiErrorAlertDlg meaDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the MultiErrorAlertDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final MultiErrorAlertDlgClient meaDlgClient,
			
			// initAndShow parameters,
			final MultiErrorAlertDlg	meaDlg,
			final String				baseError,
			final List<ErrorInfo>		errors,
			final ConfirmCallback		confirmCallback,
			final DlgButtonMode			confirmButtons) {
		GWT.runAsync(MultiErrorAlertDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_MultiErrorAlertDlg());
				if (null != meaDlgClient) {
					meaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != meaDlgClient) {
					// Yes!  Create it and return it via the callback.
					MultiErrorAlertDlg meaDlg = new MultiErrorAlertDlg();
					meaDlgClient.onSuccess(meaDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(meaDlg, baseError, errors, confirmCallback, confirmButtons);
				}
			}
		});
	}
	
	/**
	 * Loads the MultiErrorAlertDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param meaDlgClient
	 */
	public static void createAsync(MultiErrorAlertDlgClient meaDlgClient) {
		doAsyncOperation(meaDlgClient, null, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the multi-error alert dialog.
	 * 
	 * @param meaDlg
	 * @param baseError
	 * @param errors
	 */
	public static void initAndShow(MultiErrorAlertDlg meaDlg, String baseError, List<ErrorInfo> errors) {
		doAsyncOperation(null, meaDlg, baseError, errors, null, null);
	}
	
	/**
	 * Initializes and shows the multi-error alert dialog.
	 * 
	 * @param meaDlg
	 * @param baseError
	 * @param errors
	 * @param confirmCallback
	 */
	public static void initAndShow(MultiErrorAlertDlg meaDlg, String baseError, List<ErrorInfo> errors, ConfirmCallback confirmCallback, DlgButtonMode confirmButtons) {
		doAsyncOperation(null, meaDlg, baseError, errors, confirmCallback, confirmButtons);
	}
}
