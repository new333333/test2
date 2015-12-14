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

import java.util.Set;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements the import profiles dialog.
 *  
 * @author drfoster@novell.com
 */
public class ImportProfilesDlg extends DlgBox implements EditSuccessfulHandler {
	private BinderInfo			m_binderInfo;	// The profiles root workspace the dialog is running against.
	private FileUpload			m_fileInput;	// The <INPUT> widget used to select the file to import.
	private FormPanel			m_uploadForm;	// The <FORM> that contains m_fileInput used to upload the selected file.
	private GwtTeamingMessages	m_messages;		// Access to Vibe's messages.
	private VerticalPanel		m_vp;			// The panel that contains the dialog's contents.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ImportProfilesDlg() {
		// Initialize the superclass...
		super(true, false);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.importProfilesDlgHeader(),	// The dialog's caption.
			this,									// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),				// The dialog's EditCanceledHandler.
			null);									// Create callback data.  Unused. 
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
		m_vp = new VibeVerticalPanel(null, null);
		m_vp.addStyleName("vibe-importProfilesDlg-panel");
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
		// Create a panel to hold the content...
		m_vp.clear();
		FlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-importProfilesDlg-content");
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
					GwtClientHelper.deferredAlert(m_messages.importProfilesDlgErrorNoFile());
					event.cancel();
					setOkEnabled(true);
				}
			}
		});
		m_uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			/**
			 * When the form submission is successfully completed, this
			 * event is fired.  Assuming the service returned a
			 * response of type text/HTML, we can get the result text
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
									if      (key.equals("parseExceptionMsg"))     msgCount = appendMsgPart(msg, msgCount, m_messages.importProfilesDlgErrorParse( jkvsv));
									else                                          msgCount = appendMsgPart(msg, msgCount, m_messages.importProfilesDlgErrorFailed(jkvsv));
								}
								
							}
						}
					}

					// If we didn't get any error messages...
					if (0 == msgCount) {
						// ...generate a success message and mark the
						// ...dialog to be closed.
						msgCount = appendMsgPart(msg, msgCount, m_messages.importProfilesDlgSuccess());
						closeDlgAndRefresh = true;
					}
				}
				catch (Exception ex) {
					// Ignore.  We'll display a generic error below if
					// we didn't come up with anything else to display.
				}

				// Tell the user the results of the import...
				if (0 == msg.length()) {
					msg.append(m_messages.importProfilesDlgErrorBogusJSONData(result));
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
		FlowPanel uploadPanel = new VibeFlowPanel();
		m_uploadForm.setWidget(uploadPanel);
		fp.add(m_uploadForm);
		
		// ...create the hidden input parameters for the form's URL...
		Hidden hi;
		hi = new Hidden(); hi.setName("binderId");  hi.setValue(m_binderInfo.getBinderId()); uploadPanel.add(hi);
		hi = new Hidden(); hi.setName("action");    hi.setValue("import_profiles"         ); uploadPanel.add(hi);
		hi = new Hidden(); hi.setName("operation"); hi.setValue("importProfilesGWT"       ); uploadPanel.add(hi);
		hi = new Hidden(); hi.setName("okBtn");     hi.setValue("okBtn"                   ); uploadPanel.add(hi);
		
		// ...create the file input widget...
		m_fileInput = new FileUpload();
		m_fileInput.setName("profiles");
		m_fileInput.addStyleName("vibe-importProfilesDlg-input");
		uploadPanel.add(m_fileInput);
		
		// ...and a hint as to what's expected.
		Anchor hintA = new Anchor();
		hintA.addStyleName("vibe-importProfilesDlg-hint");
		hintA.setTarget("_blank");
		hintA.setHref(GwtClientHelper.getRequestInfo().getSSFPath() + "html/sample_users.txt");
		hintA.getElement().setInnerText(m_messages.importProfilesDlgViewSample());
		m_vp.add(hintA);
	}
	
	/*
	 * Asynchronously runs the given instance of the import profiles
	 * dialog.
	 */
	private static void runDlgAsync(final ImportProfilesDlg ipDlg, final BinderInfo bi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				ipDlg.runDlgNow(bi);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the import profiles
	 * dialog.
	 */
	private void runDlgNow(BinderInfo bi) {
		// Store the parameter...
		m_binderInfo = bi;

		// ...and populate and show the dialog.
		populateDlgAsync();
		setCancelEnabled(true);
		setOkEnabled(    true);
		show(true);
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the import profiles dialog and perform some operation on      */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the import profiles dialog
	 * asynchronously after it loads. 
	 */
	public interface ImportProfilesDlgClient {
		void onSuccess(ImportProfilesDlg ipDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ImportProfilesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters to create an instance of the dialog.
			final ImportProfilesDlgClient ipDlgClient,
			
			// Parameters to initialize and show the dialog.
			final ImportProfilesDlg	ipDlg,
			final BinderInfo		bi) {
		GWT.runAsync(ImportProfilesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ImportProfilesDlg());
				if (null != ipDlgClient) {
					ipDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != ipDlgClient) {
					// Yes!  Create it and return it via the callback.
					ImportProfilesDlg ipDlg = new ImportProfilesDlg();
					ipDlgClient.onSuccess(ipDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(ipDlg, bi);
				}
			}
		});
	}
	
	/**
	 * Loads the ImportProfilesDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param ipDlgClient
	 */
	public static void createAsync(ImportProfilesDlgClient ipDlgClient) {
		doAsyncOperation(ipDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the import profiles dialog.
	 * 
	 * @param ipDlg
	 * @param bi
	 */
	public static void initAndShow(ImportProfilesDlg ipDlg, BinderInfo bi) {
		doAsyncOperation(null, ipDlg, bi);
	}
}
