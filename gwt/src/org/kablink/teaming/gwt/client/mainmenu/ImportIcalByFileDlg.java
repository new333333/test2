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

import java.util.Set;

import com.google.gwt.user.client.Cookies;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements Vibe's import iCal by file dialog.
 *  
 * @author drfoster@novell.com
 */
public class ImportIcalByFileDlg extends DlgBox implements EditSuccessfulHandler {
	private BinderInfo			m_folderInfo;	// The folder the dialog is running against.
	private FileUpload			m_fileInput;	//
	private FormPanel			m_uploadForm;	//
	private GwtTeamingMessages	m_messages;		// Access to Vibe's messages.
	private String				m_importType;	// Type of import (calendar or task) being performed.  Used to patch strings used for both types.
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
	private ImportIcalByFileDlg() {
		// Initialize the superclass...
		super(false, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuImportIcalByFileDlgHeader("TBD"),	// Will be updated later during the construction process.
			this,													// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),								// The dialog's EditCanceledHandler.
			null);													// Create callback data.  Unused. 
	}

	/*
	 * Helper method to generate the message display with the import
	 * results.
	 */
	private static int appendMsgPart(StringBuffer msg, int msgCount, String msgPart) {
		// Increment the count...
		msgCount += 1;
		
		// ...and if we've count multiple parts...
		if (1 < msgCount) {
			// ...append a newline separator...
			msg.append("\n");
		}
		
		// ...append the message part...
		msg.append(msgPart);
		
		// ...and return the incremented count.
		return msgCount;
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
		// Submit the form and return false.  If the import is
		// successful, the submit complete handler will take care of
		// closing the dialog.
		setOkEnabled(false);
		m_uploadForm.submit();
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
		switch (m_folderInfo.getFolderType()) {
		case CALENDAR:  m_importType = m_messages.mainMenuImportIcalTypeCalendar(); break;
		case TASK:      m_importType = m_messages.mainMenuImportIcalTypeTask();     break;
		default:        m_importType = m_messages.mainMenuImportIcalTypeError();    break;
		}
		setCaption(m_messages.mainMenuImportIcalByFileDlgHeader(m_importType));
		
		// Create a panel to hold the content...
		m_vp.clear();
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-importIcalDlg_Content");
		m_vp.add(fp);
		
		// ....create a form for the file input widgets...
		m_uploadForm = new FormPanel();
		m_uploadForm.getElement().setId("ss_calendar_import_form");
		m_uploadForm.setAction(  GwtClientHelper.getRequestInfo().getBaseVibeUrl());
		m_uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART                     );
		m_uploadForm.setMethod(  FormPanel.METHOD_POST                            );
		m_uploadForm.addSubmitHandler(new SubmitHandler() {
			/**
			 * This event is fired just before the form is submitted.
			 * We can take this opportunity to perform validation.
			 * 
			 * @param event
			 */
			@Override
			public void onSubmit(SubmitEvent event) {
				// If the user hasn't selected a file...
				if (!(GwtClientHelper.hasString(m_fileInput.getFilename()))) {
					// ...tell them about the problem and cancel the submit.
					GwtClientHelper.deferredAlert(m_messages.mainMenuImportIcalByFileDlgErrorNoFile());
					event.cancel();
					setOkEnabled(true);
				}
			}
		});
		m_uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			/**
			 * When the form submission is successfully completed, this
			 * event is fired.  Assuming the service returned a
			 * response of type text/html, we can get the result text
			 * here (see the FormPanel documentation for further
			 * explanation.)
			 * 
			 * @param event
			 */
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				boolean closeDlgAndRefresh = false;
				StringBuffer msg = new StringBuffer("");
				
				// Parse the results as JSON.
				String result = event.getResults();
				try {
					JSONValue jv  = JSONParser.parseStrict(result);
					JSONArray jva = jv.isArray();
					
					// Scan the resultant JSON values.
					String entriesAdded    = null;
					String entriesModified = null;
					int msgCount = 0;
					for (int i = 0; i < jva.size(); i += 1) {
						// Add information about each result part to
						// the message we'll display to the user. 
						JSONValue  jao = jva.get(i);
						JSONObject jo  = jao.isObject();
						if (null != jo) {
							Set<String> joKeys = jo.keySet();
							for (String key:  joKeys) {
								JSONValue  jkv  = jo.get(key);
								JSONString jkvs = jkv.isString();
								if (null != jkvs) {
									String jkvsv = jkvs.stringValue();
									if      (key.equals("parseExceptionMsg"))     msgCount = appendMsgPart(msg, msgCount, m_messages.mainMenuImportIcalByFileDlgErrorParse(jkvsv));
									else if (key.equals("entriesAddedAmount"))    entriesAdded = jkvsv;
									else if (key.equals("entriesModifiedAmount")) entriesModified = jkvsv;
									else                                          msgCount = appendMsgPart(msg, msgCount, m_messages.mainMenuImportIcalByFileDlgErrorFailed(jkvsv));
								}
								
							}
						}
					}
					
					// Do we have counts for the entries added and
					// modified?
					if ((null != entriesAdded) && (null != entriesModified)) {
						// Yes!  Generate a success message and mark
						// the dialog to be closed.
						msgCount = appendMsgPart(msg, msgCount, m_messages.mainMenuImportIcalByFileDlgSuccess(entriesAdded, entriesModified));
						closeDlgAndRefresh = true;
					}
				}
				catch (Exception ex) {
					// Ignore.  We'll display a generic error below if
					// we didn't come up with anything else to display.
				}

				// Tell the user the results of the import...
				if (0 == msg.length()) {
					msg.append(m_messages.mainMenuImportIcalByFileDlgErrorBogusJSONData(result));
				}
				GwtClientHelper.deferredAlert(msg.toString());
				
				// ...and if requested to...
				if (closeDlgAndRefresh) {
					// ...close the dialog and refresh.
					hide();
					FullUIReloadEvent.fireOneAsync();
				}
				setOkEnabled(true);
			}
		});

		// ...connect the form to the dialog...
		FlowPanel uploadPanel = new FlowPanel();
		m_uploadForm.setWidget(uploadPanel);
		fp.add(m_uploadForm);

		GwtClientHelper.consoleLog("Cookie names: " + GwtClientHelper.join(Cookies.getCookieNames()));
		// ...create the hidden input parameters for the form's URL...
		Hidden hi;
		hi = new Hidden(); hi.setName("folderId");  hi.setValue(m_folderInfo.getBinderId()); uploadPanel.add(hi);
		hi = new Hidden(); hi.setName("action");    hi.setValue("__ajax_request"); uploadPanel.add(hi);
		hi = new Hidden(); hi.setName("operation"); hi.setValue("uploadICalendarFileGWT"); uploadPanel.add(hi);
		hi = new Hidden(); hi.setName("_csrf"); hi.setValue(Cookies.getCookie("XSRF-TOKEN")); uploadPanel.add(hi);
		
		// ...create the file input widget...
		m_fileInput = new FileUpload();
		m_fileInput.setName("iCalFile");
		m_fileInput.addStyleName("vibe-importIcalDlg_Input");
		uploadPanel.add(m_fileInput);
		
		// ...and a hint as to what's expected.
		DlgLabel urlHint = new DlgLabel(m_messages.mainMenuImportIcalByFileDlgHint());
		urlHint.addStyleName("vibe-importIcalDlg_Hint");
		fp.add(urlHint);
	}
	
	/*
	 * Asynchronously runs the given instance of the import iCal
	 * dialog.
	 */
	private static void runDlgAsync(final ImportIcalByFileDlg iiFileDlg, final BinderInfo fi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				iiFileDlg.runDlgNow(fi);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the import iCal by file
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
	/* the import iCal by file dialog and perform some operation on  */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the import iCal by file
	 * dialog asynchronously after it loads. 
	 */
	public interface ImportIcalByFileDlgClient {
		void onSuccess(ImportIcalByFileDlg iiFileDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ImportIcalByFileDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ImportIcalByFileDlgClient iiFileDlgClient,
			
			// initAndShow parameters,
			final ImportIcalByFileDlg iiFileDlg,
			final BinderInfo fi) {
		GWT.runAsync(ImportIcalByFileDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ImportIcalByFileDlg());
				if (null != iiFileDlgClient) {
					iiFileDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != iiFileDlgClient) {
					// Yes!  Create it and return it via the callback.
					ImportIcalByFileDlg iiFileDlg = new ImportIcalByFileDlg();
					iiFileDlgClient.onSuccess(iiFileDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(iiFileDlg, fi);
				}
			}
		});
	}
	
	/**
	 * Loads the ImportIcalByFileDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param iiFileDlgClient
	 */
	public static void createAsync(ImportIcalByFileDlgClient iiFileDlgClient) {
		doAsyncOperation(iiFileDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the import iCal by file dialog.
	 * 
	 * @param iiFileDlg
	 * @param fi
	 */
	public static void initAndShow(ImportIcalByFileDlg iiFileDlg, BinderInfo fi) {
		doAsyncOperation(null, iiFileDlg, fi);
	}
}
