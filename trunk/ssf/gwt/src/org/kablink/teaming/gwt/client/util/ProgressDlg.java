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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements Vibe's progress dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ProgressDlg extends DlgBox implements EditCanceledHandler {
	private boolean				m_canCancel;			// true -> The operation can be canceled.  false -> It can't be.
	private boolean				m_dialogReady;			//
	private InlineLabel			m_progressIndicator;	// Label containing the 'x of y' progress indicator.
	private int					m_totalCount;			// Tracks the total number of steps that need to be performed while.
	private int					m_totalDone;			// Tracks the number of steps that have been performed while the operation is in progress.
	private ProgressCallback	m_progressCallback;		// Interface used to interact with the caller of this dialog.
	private String				m_progressString;		//
	private VibeVerticalPanel	m_vp;					// The panel holding the dialog's content.

	public final static int CHUNK_SIZE		=  5;	// Number of operation in a chunk that are performed when doing them by chunks.
	public final static int CHUNK_THRESHOLD	= 20;	// Number of operations beyond which we send them across in chunks so that we can show a progress indicator.

	/**
	 * Interface used by the dialog to inform the caller about what's
	 * going on. 
	 */
	public interface ProgressCallback {
		public void dialogReady();
		public void operationCanceled();
		public void operationComplete();
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ProgressDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Cancel);
		
		// ...and create the dialog's content.
		createAllDlgContent(
			"",								// No caption yet.  It's set appropriately when the dialog runs.
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			this,							// The dialog's EditCancledHandler.
			null);							// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage(String style) {
		Image reply = new Image(GwtTeaming.getImageBundle().spinner16());
		reply.getElement().setAttribute("align", "absmiddle");
		if (GwtClientHelper.hasString(style)) {
			reply.addStyleName(style);
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
		// Create and return a vertical panel to hold the dialog's
		// content.
		m_vp = new VibeVerticalPanel();
		m_vp.addStyleName("vibe-progressDlg_RootPanel");
		return m_vp;
	}

	/*
	 * Marks the dialog as being ready and shows it.
	 */
	private void doDialogReady() {
		m_dialogReady = true;
		show(true);
		ScheduledCommand doReady = new ScheduledCommand() {
			@Override
			public void execute() {
				m_progressCallback.dialogReady();
			}
		};
		Scheduler.get().scheduleDeferred(doReady);
	}
	
	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled() {
		// Can the operation be canceled?
		if (!m_canCancel) {
			// No!  Ignore the call and return false to keep the dialog
			// open.
			return false;
		}
		
		// Does the user really want to cancel the operation?
		if (Window.confirm(GwtTeaming.getMessages().progressDlgConfirmCancel())) {
			// Yes!  Tell the caller...
			ScheduledCommand doCancel = new ScheduledCommand() {
				@Override
				public void execute() {
					m_progressCallback.operationCanceled();
				}
			};
			Scheduler.get().scheduleDeferred(doCancel);
			
			// ...and return true to close the dialog.
			return true;
		}
		
		// Return false to leave the dialog open since the user doesn't
		// want to cancel the operation.
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
		return null;
	}

	/*
	 * Asynchronously loads the find control.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the find control.
	 */
	private void loadPart1Now() {
		populateDlgAsync();
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
		m_vp.clear();

		// ...add a panel for displaying progress, when needed...
		VibeFlowPanel progressPanel = new VibeFlowPanel();
		progressPanel.addStyleName("vibe-progressDlg_ProgressPanel");
		m_vp.add(progressPanel);
		progressPanel.add(buildSpinnerImage("vibe-progressDlg_ProgressSpinner"));
		m_progressIndicator = new InlineLabel(
			GwtClientHelper.patchMessage(
				m_progressString,
				new String[] {
					String.valueOf(m_totalDone ),
					String.valueOf(m_totalCount)
				}));
		m_progressIndicator.addStyleName("vibe-progressDlg_ProgressLabel");
		progressPanel.add(m_progressIndicator);
		
		// ...finally, mark the dialog as being ready and show it.
		doDialogReady();
	}
	
	/*
	 * Asynchronously runs the given instance of the progress dialog.
	 */
	private static void runDlgAsync(final ProgressDlg pDlg, final ProgressCallback pCB, final boolean canCancel, final String dlgCaption, final String progressString, final int totalCount) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				pDlg.runDlgNow(
					pCB,
					canCancel,
					dlgCaption,
					progressString,
					totalCount);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the progress dialog.
	 */
	private void runDlgNow(ProgressCallback pCB, boolean canCancel, String dlgCaption, String progressString, int totalCount) {
		// Set the dialog's caption...
		setCaption(      dlgCaption);
		setCancelEnabled(canCancel );
		
		// ...store the parameters...
		m_progressCallback = pCB;
		m_canCancel        = canCancel;
		m_progressString   = progressString;
		m_totalCount       = totalCount;
		
		// ...and populate it.
		loadPart1Async();
	}

	/**
	 * Returns true if the number of operations requires a progress
	 * dialog and false otherwise.
	 * 
	 * @param totalCount
	 * 
	 * @return
	 */
	public boolean needProgressDialog(int totalCount) {
		return (totalCount > CHUNK_THRESHOLD);
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
	
	/*
	 * Called up asynchronously update the progress indicator in the
	 * dialog.
	 */
	private void updateProgressAsync(final int justCompleted) {
		ScheduledCommand doUpdate = new ScheduledCommand() {
			@Override
			public void execute() {
				updateProgressNow(justCompleted);
			}
		};
		Scheduler.get().scheduleDeferred(doUpdate);
	}
	
	/*
	 * Called up synchronously update the progress indicator in the
	 * dialog.
	 */
	private void updateProgressNow(int justCompleted) {
		// If we're done...
		m_totalDone += justCompleted;
		if (m_totalDone == m_totalCount) {
			// ...hide the dialog and tell the caller we're done.
			hide();
			ScheduledCommand doComplete = new ScheduledCommand() {
				@Override
				public void execute() {
					m_progressCallback.operationComplete();
				}
			};
			Scheduler.get().scheduleDeferred(doComplete);
		}
		
		else {
			// ...otherwise, set the number we've completed.
			m_progressIndicator.setText(
				GwtClientHelper.patchMessage(
					m_progressString,
					new String[] {
						String.valueOf(m_totalDone ),
						String.valueOf(m_totalCount)
					}));
		}
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the progress dialog and perform some operation on it.         */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the progress dialog
	 * asynchronously after it loads. 
	 */
	public interface ProgressDlgClient {
		void onSuccess(ProgressDlg pDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ProgressDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ProgressDlgClient pDlgClient,
			
			// initAndShow parameters.
			final ProgressDlg       pDlg,
			final ProgressCallback  pCB,
			final boolean           canCancel,
			final String            dlgCaption,
			final String            progressString,
			final int               totalCount,
			
			// updateProgress parameters.
//			final ProgressDlg pDlg,
			final int         justCompleted) {
		GWT.runAsync(ProgressDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ProgressDlg());
				if (null != pDlgClient) {
					pDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != pDlgClient) {
					// Yes!  Create it and return it via the callback.
					final ProgressDlg pDlg = new ProgressDlg();
					pDlg.hide();
					ScheduledCommand doSuccess = new ScheduledCommand() {
						@Override
						public void execute() {
							pDlgClient.onSuccess(pDlg);
						}
					};
					Scheduler.get().scheduleDeferred(doSuccess);
				}
				
				// No, it's not a request to create a dialog!  Is it a
				// request to run one?
				else if (null != pCB) {
					// Yes!  Run it.
					runDlgAsync(pDlg, pCB, canCancel, dlgCaption, progressString, totalCount);
				}
				
				else {
					// No, it's not a request to run one either!  It
					// must be a progress update.
					pDlg.updateProgressAsync(justCompleted);
				}
			}
		});
	}
	
	/**
	 * Loads the ProgressDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param pDlgClient
	 */
	public static void createAsync(ProgressDlgClient pDlgClient) {
		doAsyncOperation(pDlgClient, null, null, false, null, null, (-1), (-1));
	}
	
	/**
	 * Initializes and shows the progress dialog.
	 * 
	 * @param pDlg
	 * @param pCB
	 * @param canCancel
	 * @param dlgCaption
	 * @Param progressString
	 * @Param totalCount
	 */
	public static void initAndShow(ProgressDlg pDlg, ProgressCallback pCB, boolean canCancel, String dlgCaption, String progressString, int totalCount) {
		doAsyncOperation(null, pDlg, pCB, canCancel, dlgCaption, progressString, totalCount, (-1));
	}
	
	/**
	 * Updates the progress indicator in the dialog.
	 * 
	 * @param pDlg
	 * @param justCompleted
	 */
	public static void updateProgress(ProgressDlg pDlg, int justCompleted) {
		doAsyncOperation(null, pDlg, null, false, null, null, (-1), justCompleted);
	}
}
