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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.rpc.shared.ImportIcalByUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ImportIcalByUrlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ImportIcalByUrlRpcResponseData.FailureReason;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements Vibe's import iCal by URL dialog.
 *  
 * @author drfoster@novell.com
 */
public class ImportIcalByUrlDlg extends DlgBox implements EditSuccessfulHandler {
	private BinderInfo			m_folderInfo;	// The folder the dialog is running against.
	private GwtTeamingMessages	m_messages;		// Access to Vibe's messages.
	private String				m_importType;	// Type of import (calendar or task) being performed.  Used to patch strings used for both types.
	private TextBox				m_url;			//
	private VerticalPanel		m_vp;			//

	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param title
		 */
		public DlgLabel(String label, String title) {
			super(label);
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
			addStyleName("vibe-importIcalDlg_Label");
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null);
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ImportIcalByUrlDlg() {
		// Initialize the superclass...
		super(false, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuImportIcalByUrlDlgHeader("TBD"),	// Will be updated later during the construction process.
			this,												// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),							// The dialog's EditCanceledHandler.
			null);												// Create callback data.  Unused. 
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
		m_vp = new VerticalPanel();
		m_vp.addStyleName("vibe-importIcalDlg_Panel");
		return m_vp;
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
		// Do we have a URL to import?
		String url = m_url.getValue();
		if (null != url) {
			url = url.trim();
		}
		if (GwtClientHelper.hasString(url)) {
			// Yes!  Invoke the import method with it.
			setOkEnabled(false);
			importIcalUrlAsync(url);
		}
		else {
			// No, we don't have a URL to import!  Tell the user about
			// the problem.
			GwtClientHelper.deferredAlert(m_messages.mainMenuImportIcalByUrlDlgErrorNoUrl());
		}
		
		// Return false.  If the import is successful, the dialog will
		// be closed after the import completes.
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
	 * Asynchronously imports the given iCal URL.
	 */
	private void importIcalUrlAsync(final String url) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				importIcalUrlNow(url);
			}
		});
	}
	
	/*
	 * Synchronously imports the given iCal URL.
	 */
	private void importIcalUrlNow(String url) {
		GwtClientHelper.executeCommand(
				new ImportIcalByUrlCmd(m_folderInfo.getBinderIdAsLong(), url),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_ImportIcalByUrl());
				setOkEnabled(true);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Was the import successful?
				ImportIcalByUrlRpcResponseData responseData = ((ImportIcalByUrlRpcResponseData) response.getResponseData());
				if (responseData.hasErrors()) {
					// No!  Tell the user about the problem(s).
					Map<FailureReason, String> errors = responseData.getErrors();
					Set<FailureReason> reasons = errors.keySet();
					StringBuffer msgs = new StringBuffer("");
					int count = 0;
					for (FailureReason reason:  reasons) {
						count += 1;
						String detail = errors.get(reason);
						String msg;
						switch (reason) {
						case IMPORT_FAILED:    msg = m_messages.mainMenuImportIcalByUrlDlgErrorFailed(detail); break;
						case PARSE_EXCEPTION:  msg = m_messages.mainMenuImportIcalByUrlDlgErrorParse( detail); break;
						case URL_EXCEPTION:    msg = m_messages.mainMenuImportIcalByUrlDlgErrorUrl(   detail); break;
						default:               msg = m_messages.mainMenuImportIcalByUrlDlgErrorUnknown();      break;
						}
						if (1 < count) {
							msgs.append("\n\n");
						}
						msgs.append(msg);
					}
					GwtClientHelper.deferredAlert(msgs.toString());
				}
				
				else {
					// Yes, the import was successful!  Close the
					// dialog...
					hide();
					
					// ...tell the user what changed...
					int added    = responseData.getAddedEntryIds().size();
					int modified = responseData.getModifiedEntryIds().size();
					GwtClientHelper.deferredAlert(
						m_messages.mainMenuImportIcalByUrlDlgSuccess(
							String.valueOf(added),
							String.valueOf(modified)));
					
					// ...and if anything changed...
					if (0 < (added + modified)) {
						// ...force the UI to reload.
						FullUIReloadEvent.fireOneAsync();
					}
				}
				setOkEnabled(true);
			}
		});
	}
	
    /**
     * Called after the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingEnded() method.
     */
	@Override
    protected void okBtnProcessingEnded() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
    /**
     * Called before the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingStarted() method.
     */
	@Override
    protected void okBtnProcessingStarted() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
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
		// Update the caption with the correct string based on the
		// folder that we're importing into.
		String hint;
		switch (m_folderInfo.getFolderType()) {
		case CALENDAR:  m_importType = m_messages.mainMenuImportIcalTypeCalendar(); hint = m_messages.mainMenuImportIcalByUrlDlgHintCalendar(); break;
		case TASK:      m_importType = m_messages.mainMenuImportIcalTypeTask();     hint = m_messages.mainMenuImportIcalByUrlDlgHintTask();     break;
		default:        m_importType = m_messages.mainMenuImportIcalTypeError();    hint = null;                                                break;
		}
		setCaption(m_messages.mainMenuImportIcalByUrlDlgHeader(m_importType));

		// Create a panel to hold the content...
		m_vp.clear();
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-importIcalDlg_Content");
		m_vp.add(fp);
		
		// ....add the URL input widgets...
		m_url = new TextBox();
		m_url.addStyleName("vibe-importIcalDlg_Input");
		fp.add(m_url);
		
		// ...and if we have one, a hint as to what's expected.
		if (GwtClientHelper.hasString(hint)) {
			DlgLabel urlHint = new DlgLabel(hint);
			urlHint.addStyleName("vibe-importIcalDlg_Hint");
			fp.add(urlHint);
		}
	}
	
	/*
	 * Asynchronously runs the given instance of the import iCal
	 * dialog.
	 */
	private static void runDlgAsync(final ImportIcalByUrlDlg iiUrlDlg, final BinderInfo fi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				iiUrlDlg.runDlgNow(fi);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the import iCal by URL
	 * dialog.
	 */
	private void runDlgNow(BinderInfo fi) {
		// Store the parameter...
		m_folderInfo = fi;

		// ...and display a reading message, start populating the
		// ...dialog and show it.
		populateDlgAsync();
		setCancelEnabled(true);
		setOkEnabled(    true);
		show(            true);
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the import iCal by URL dialog and perform some operation on   */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the import iCal by URL
	 * dialog asynchronously after it loads. 
	 */
	public interface ImportIcalByUrlDlgClient {
		void onSuccess(ImportIcalByUrlDlg iiUrlDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ImportIcalByUrlDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ImportIcalByUrlDlgClient iiUrlDlgClient,
			
			// initAndShow parameters,
			final ImportIcalByUrlDlg iiUrlDlg,
			final BinderInfo fi) {
		GWT.runAsync(ImportIcalByUrlDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ImportIcalByUrlDlg());
				if (null != iiUrlDlgClient) {
					iiUrlDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != iiUrlDlgClient) {
					// Yes!  Create it and return it via the callback.
					ImportIcalByUrlDlg iiUrlDlg = new ImportIcalByUrlDlg();
					iiUrlDlgClient.onSuccess(iiUrlDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(iiUrlDlg, fi);
				}
			}
		});
	}
	
	/**
	 * Loads the ImportIcalByUrlDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param iiUrlDlgClient
	 */
	public static void createAsync(ImportIcalByUrlDlgClient iiUrlDlgClient) {
		doAsyncOperation(iiUrlDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the import iCal by URL dialog.
	 * 
	 * @param iiUrlDlg
	 * @param fi
	 */
	public static void initAndShow(ImportIcalByUrlDlg iiUrlDlg, BinderInfo fi) {
		doAsyncOperation(null, iiUrlDlg, fi);
	}
}
