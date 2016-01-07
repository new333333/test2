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

import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GwtBrandingFileInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

/**
 * Composite that uploads a file for site branding.
 * 
 * @author drfoster@novell.com
 */
public class UploadSiteBrandingFile {
	private Button					m_removeButton;		//
	private Button					m_uploadButton;		//
	private FileUpload				m_fileUpload;		//
	private FormPanel				m_uploadForm;		//
	private Label					m_overwriteHint;	//
	private	SiteBrandingDescriptor	m_descriptor;		//
	private String					m_fileUploadId;		//

	/**
	 * Interface used to interact with the container about site
	 * branding file uploads.
	 */
	public interface SiteBrandingDescriptor {
		public boolean            hasExistingFile();
		public FlexTable          getGrid();
		public GwtTeamingMessages getMessages();
		public String             getFileDateTime();
		public String             getFileName();
		public String             getNoFileError();
		public String             getOverwriteConfirmationMsg(String fName);
		public String             getOverwriteHint();
		public String             getRemoveAlt();
		public String             getRemoveConfirmationMsg();
		public String             getUploadAlt();
		public String             getUploadId();
		public String             getUploadOperation();
		public String             getWidgetLabel();
		public void               refreshBrandingInfo(SiteBrandingRefreshCallback refreshCallback);
		public void               removeFile();
		public void               setFileInfo(GwtBrandingFileInfo fi);
	}

	/**
	 * Interface used for a container to tell the site branding
	 * widget that its branding refresh has completed.
	 */
	public interface SiteBrandingRefreshCallback {
		public void refreshComplete();
	}
	
	/**
	 * Constructor method.
	 */
	public UploadSiteBrandingFile(SiteBrandingDescriptor descriptor) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_descriptor = descriptor;
		
		// ...and create the composite's content.
		createContent();
	}
	
	/*
	 * Constructs and returns an InlineLabel, optionally truncated to a
	 * given length and optionally assigned styles.
	 */
	private InlineLabel buildInlineLabel(String data, int length, String styles) {
		if (0 < length) {
			if (data.length() > length) {
				data = (data.substring(0, length) + "...");
			}
		}
		InlineLabel reply = new InlineLabel(data);
		if (GwtClientHelper.hasString(styles)) {
			reply.addStyleName(styles);
		}
		return reply;
	}
	
	@SuppressWarnings("unused")
	private InlineLabel buildInlineLabel(String data) {
		// Always use the initial form of the method.
		return buildInlineLabel(data, (-1), null);
	}

	@SuppressWarnings("unused")
	private InlineLabel buildInlineLabel(String data, int length) {
		// Always use the initial form of the method.
		return buildInlineLabel(data, length, null);
	}

	private InlineLabel buildInlineLabel(String data, String styles) {
		// Always use the initial form of the method.
		return buildInlineLabel(data, (-1), styles);
	}

	/*
	 * Creates the content for the composite.
	 */
	private void createContent() {
		// Extract the table and row we'll create the widgets in...
		FlexTable         ft  = m_descriptor.getGrid();
		FlexCellFormatter fcf = ft.getFlexCellFormatter();
		int               row = ft.getRowCount();
		
		// ...add the caption for the content...
		InlineLabel il = buildInlineLabel(m_descriptor.getWidgetLabel(), "vibe-uploadSiteBranding-label");
		ft.setWidget(   row, 0, il   );
		fcf.setWordWrap(row, 0, false);

		// ...create the <FORM> for the upload widgets...
		m_uploadForm = new FormPanel();
		m_uploadForm.getElement().setId("uploadSiteBranding_form_" + m_descriptor.getUploadId());
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
				// Nothing to do.
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
				// Store the name of the file we just uploaded in the
				// descriptor...
				m_descriptor.refreshBrandingInfo(new SiteBrandingRefreshCallback() {
					@Override
					public void refreshComplete() {
						// ...show the hint with the new name...
						m_overwriteHint.getElement().setInnerText(m_descriptor.getOverwriteHint());
						m_overwriteHint.setVisible(true);
						
						// ...and enable the 'Remove' button.
						m_removeButton.setEnabled(true);
					}
				});
			}
		});
		
		// ...connect the form to the dialog...
		FlowPanel uploadPanel = new VibeFlowPanel();
		uploadPanel.addStyleName("vibe-uploadSiteBranding-uploadPanel");
		m_uploadForm.setWidget(uploadPanel);
		ft.setWidget(row, 1, m_uploadForm);
		
		// ...create the hidden input parameters for the form's URL...
		Hidden hi;
		hi = new Hidden(); hi.setName("action");    hi.setValue("__ajax_request"                 ); uploadPanel.add(hi);
		hi = new Hidden(); hi.setName("operation"); hi.setValue(m_descriptor.getUploadOperation()); uploadPanel.add(hi);
		
		// ...create the file <INPUT> widget...
		m_fileUpload = new FileUpload();
		m_fileUploadId = ("uploadSiteBranding_input_" + m_descriptor.getUploadId());
		m_fileUpload.setName(m_fileUploadId);
		m_fileUpload.getElement().setId(m_fileUploadId);
		m_fileUpload.addStyleName("vibe-uploadSiteBranding-input");
		uploadPanel.add(m_fileUpload);
		m_fileUpload.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				m_uploadButton.setEnabled(GwtClientHelper.hasString(m_fileUpload.getFilename())); 
			}
		});

		// ...and create the 'Upload File' <INPUT>.
		m_uploadButton = new Button(m_descriptor.getMessages().uploadSiteBranding_Upload(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If the user hasn't selected a file...
				if (!(GwtClientHelper.hasString(m_fileUpload.getFilename()))) {
					// ...tell them about the problem and don't submit
					// ...the form.
					GwtClientHelper.deferredAlert(m_descriptor.getNoFileError());
					return;
				}
				
				// If there's an existing file, confirm the user wants
				// to overwrite it, otherwise, simply submit the form.
				if (m_descriptor.hasExistingFile()) {
					ConfirmDlg.createAsync(new ConfirmDlgClient() {
						@Override
						public void onUnavailable() {
							// Nothing to do.  Error handled in
							// asynchronous provider.
						}
						
						@Override
						public void onSuccess(ConfirmDlg cDlg) {
							ConfirmDlg.initAndShow(
								cDlg,
								new ConfirmCallback() {
									@Override
									public void dialogReady() {
										// Ignored.  We don't really care when the dialog
										// is ready.
									}

									@Override
									public void accepted() {
										// Yes, they're sure!  Overwrite it.
										submitFormAsync();
									}

									@Override
									public void rejected() {
										// No, they're not sure!
									}
								},
								m_descriptor.getOverwriteConfirmationMsg(m_descriptor.getFileName()));
						}
					});
				}
				else {
					submitFormAsync();
				}
			}
		});
		m_uploadButton.setTitle(m_descriptor.getUploadAlt());
		ft.setWidget(row, 2, m_uploadButton);
		m_uploadButton.setEnabled(false); 
		
		m_removeButton = new Button(m_descriptor.getMessages().uploadSiteBranding_Remove(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If the user hasn't selected a file...
				if (m_descriptor.hasExistingFile()) {
					// If there's an existing file, confirm the user wants
					// to overwrite it, otherwise, simply submit the form.
					if (m_descriptor.hasExistingFile()) {
					     ConfirmDlg.createAsync(new ConfirmDlgClient() {
							@Override
							public void onUnavailable() {
								// Nothing to do.  Error handled in
								// asynchronous provider.
							}
							
							@Override
							public void onSuccess(ConfirmDlg cDlg) {
								ConfirmDlg.initAndShow(
									cDlg,
									new ConfirmCallback() {
										@Override
										public void dialogReady() {
											// Ignored.  We don't really care when the dialog
											// is ready.
										}

										@Override
										public void accepted() {
											// Yes, they're sure!  Remove it.
											removeFileAsync();
										}

										@Override
										public void rejected() {
											// No, they're not sure!
										}
									},
									m_descriptor.getRemoveConfirmationMsg());
							}
						});
					}
					else {
						removeFileAsync();
					}
				}
			}
		});
		m_removeButton.setTitle(m_descriptor.getRemoveAlt());
		ft.setWidget(row, 3, m_removeButton);
		m_removeButton.setEnabled(m_descriptor.hasExistingFile());
		
		// Add a hint to the table telling the user what happens if
		// they upload a new file.
		row += 1;
		fcf.setColSpan(row, 1, 3);
		m_overwriteHint = new Label(m_descriptor.getOverwriteHint());
		m_overwriteHint.addStyleName("vibe-uploadSiteBranding-overwriteHint");
		ft.setWidget(row, 1, m_overwriteHint);
		m_overwriteHint.setVisible(m_descriptor.hasExistingFile());
	}

	/*
	 * Asynchronously removes the current file.
	 */
	private void removeFileAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				removeFileNow();
			}
		});
	}
	
	/*
	 * Synchronously removes the current file.
	 */
	private void removeFileNow() {
		m_descriptor.removeFile();
	}
	
	/*
	 * Asynchronously submits the given upload form.
	 */
	private void submitFormAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				submitFormNow();
			}
		});
	}
	
	/*
	 * Synchronously submits the given upload form.
	 */
	private void submitFormNow() {
		m_uploadForm.submit();
	}
}
