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

import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

/**
 * Composite that uploads a file for site branding.
 * 
 * @author drfoster@novell.com
 */
public class UploadSiteBrandingFile implements ConfirmDlgClient {
	private FileUpload				m_fileInput;	//
	private FormPanel				m_uploadForm;	//
	private	SiteBrandingDescriptor	m_descriptor;	//

	/**
	 * Inner class used to tell the composite about what it's uploading
	 * and how to upload it.
	 */
	public static class SiteBrandingDescriptor {
		private boolean                 m_hasExistingFile;			// true -> Uploading will overwrite an existing file.  false -> It won't.
		private FlexTable               m_grid;                // The table to contain the widgets that are created.
		private GwtTeamingMessages		m_messages;					// Access to Vibe's messages.
		private String					m_noFileError;				// Error to display if the user clicks the upload button without selecting a file.
		private String					m_overwriteConfirmationMsg;	// Confirmation message to display asking the user to verify the want to overwrite an existing file.
		private String					m_overwriteHint;			// Hint to display saying that the file will be overwritten.
		private String					m_uploadAlt;				// ALT text for the 'Upload File' button.
		private String					m_uploadId;					// The ID to use for this upload composite so that all of them in a dialog are unique.
		private String					m_uploadOperation;			// The operation to submit with the upload form's URL. 
		private String					m_widgetLabel;				// Caption for this upload composite.

		/**
		 * Constructor method.
		 */
		public SiteBrandingDescriptor() {
			// Simply initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean            hasExistingFile()             {return m_hasExistingFile;         }
		public FlexTable          getGrid()                     {return m_grid;                    }
		public GwtTeamingMessages getMessages()                 {return m_messages;                }
		public String             getNoFileError()              {return m_noFileError;             }
		public String             getOverwriteConfirmationMsg() {return m_overwriteConfirmationMsg;}
		public String             getOverwriteHint()            {return m_overwriteHint;           }
		public String             getUploadAlt()                {return m_uploadAlt;               }
		public String             getUploadId()                 {return m_uploadId;                }
		public String             getUploadOperation()          {return m_uploadOperation;         }
		public String             getWidgetLabel()              {return m_widgetLabel;             }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setHasExistingFile(         boolean            hasExistingFile)          {m_hasExistingFile          = hasExistingFile;         }
		public void setGrid(                    FlexTable          grid)                     {m_grid                     = grid;                    }
		public void setMessages(                GwtTeamingMessages messages)                 {m_messages                 = messages;                }
		public void setNoFileError(             String             noFileError)              {m_noFileError              = noFileError;             }
		public void setOverwriteConfirmationMsg(String             overwriteConfirmationMsg) {m_overwriteConfirmationMsg = overwriteConfirmationMsg;}
		public void setOverwriteHint(           String             overwriteHint)            {m_overwriteHint            = overwriteHint;           }
		public void setUploadAlt(               String             uploadAlt)                {m_uploadAlt                = uploadAlt;               }
		public void setUploadId(                String             uploadId)                 {m_uploadId                 = uploadId;                }
		public void setUploadOperation(         String             uploadOperation)          {m_uploadOperation          = uploadOperation;         }
		public void setWidgetLabel(             String             widgetLabel)              {m_widgetLabel              = widgetLabel;             }
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
		m_uploadForm.getElement().setId("siteBrandingUpload_form_" + m_descriptor.getUploadId());
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
				// Nothing to do.
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
		m_fileInput = new FileUpload();
		m_fileInput.setName("siteBrandingUpload_input_" + m_descriptor.getUploadId());
		m_fileInput.addStyleName("vibe-uploadSiteBranding-input");
		uploadPanel.add(m_fileInput);

		// ...and create the 'Upload File' <INPUT>.
		Button uploadButton = new Button(m_descriptor.getMessages().uploadSiteBranding_Upload(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If the user hasn't selected a file...
				if (!(GwtClientHelper.hasString(m_fileInput.getFilename()))) {
					// ...tell them about the problem and don't submit
					// ...the form.
					GwtClientHelper.deferredAlert(m_descriptor.getNoFileError());
					return;
				}
				
				// If there's an existing file, confirm the user wants
				// to overwrite it, otherwise, simply submit the form.
				if (m_descriptor.hasExistingFile())
				     ConfirmDlg.createAsync(UploadSiteBrandingFile.this);
				else submitFormAsync();
			}
		});
		uploadButton.setTitle(m_descriptor.getUploadAlt());
		ft.setWidget(row, 2, uploadButton);
		
		// Will this overwrite an existing file? 
		if (m_descriptor.hasExistingFile()) {
			// Yes!  Add a hint to the table telling the user.
			row += 1;
			fcf.setColSpan(row, 1, 2);
			InlineLabel hint = new InlineLabel(m_descriptor.getOverwriteHint());
			hint.addStyleName("vibe-uploadSiteBranding-overwriteHint");
			ft.setWidget(row, 1, hint);
		}
	}

	/**
	 * Called if the ConfirmDlg cannot be instantiated.
	 * 
	 * Implements the ConfirmDlgCallback.onUnavailable method.
	 */
	@Override
	public void onUnavailable() {
		// Nothing to do.  Error handled in
		// asynchronous provider.
	}
	
	/**
	 * Called when the ConfirmDlg is instantiated.
	 * 
	 * Implements the ConfirmDlgCallback.onSuccess() method.
	 */
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
			m_descriptor.getOverwriteConfirmationMsg());
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
