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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Implements Vibe's multi-error alert dialog.
 *  
 * @author drfoster@novell.com
 */
public class MultiErrorAlertDlg extends DlgBox {
	private boolean					m_dialogReady;	// Set true once the dialog is ready for display.
	private GwtTeamingImageBundle	m_images;		// Access to Vibe's images.
	private GwtTeamingMessages		m_messages;		// Access to Vibe's messages.
	private List<ErrorInfo>			m_errors;		//
	private String					m_baseError;	//
	private VibeFlowPanel			m_fp;			// The panel holding the dialog's content.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MultiErrorAlertDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.multiErrorAlertDlgHeader(),	// The dialog's header.
			getSimpleSuccessfulHandler(),			// The dialog's EditSuccessfulHandler.
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
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-multiErrorAlertDlg-rootPanel");
		return m_fp;
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
		m_fp.clear();

		// ...create a grid for the dialog's contents...
		FlexTable grid = new FlexTable();
		grid.addStyleName("vibe-multiErrorAlertDlg-grid");
		m_fp.add(grid);
		FlexCellFormatter fcf = grid.getFlexCellFormatter();

		// ...add a warning image...
		Image warnImg = new Image();
		warnImg.addStyleName("vibe-multiErrorAlertDlg-warnImg");
		warnImg.setUrl(m_images.warning32().getSafeUri());
		warnImg.getElement().setAttribute("align", "absmiddle");
		grid.setWidget(0, 0, warnImg);

		// ...add the base error message...
		InlineLabel il = new InlineLabel(m_baseError);
		il.addStyleName("vibe-multiErrorAlertDlg-baseError");
		grid.setWidget(0, 1, il);
		fcf.setColSpan(0, 1, 2);

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

		// Finally, show the dialog.
		m_dialogReady = true;
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the multi-error alert
	 * dialog.
	 */
	private static void runDlgAsync(final MultiErrorAlertDlg meaDlg, final String baseError, final List<ErrorInfo> errors) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				meaDlg.runDlgNow(baseError, errors);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the multi-error alert
	 * dialog.
	 */
	private void runDlgNow(String baseError, List<ErrorInfo> errors) {
		// Store the parameter and populate the dialog.
		m_baseError = baseError;
		m_errors    = errors;
		
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
			final List<ErrorInfo>		errors) {
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
					runDlgAsync(meaDlg, baseError, errors);
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
		doAsyncOperation(meaDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the multi-error alert dialog.
	 * 
	 * @param meaDlg
	 * @param baseError
	 * @param errors
	 */
	public static void initAndShow(MultiErrorAlertDlg meaDlg, String baseError, List<ErrorInfo> errors) {
		doAsyncOperation(null, meaDlg, baseError, errors);
	}
}
