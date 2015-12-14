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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.datatable.UploadTemplate;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.Html5UploadHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;

/**
 * Class that encapsulates a popup panel shown while an HTML5 file
 * upload is in progress.
 * 
 * @author drfoster@novell.com
 */
public class Html5UploadPopup extends TeamingPopupPanel {
	private Button							m_abortButton;	// Button on the panel that will abort the uploads.
	private GwtTeamingDataTableImageBundle	m_images;		// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;		// Access to the GWT localized string resource.
	private Html5UploadHelper				m_uploadHelper;	// The HTML5 upload APIs.
	private FlexTable						m_pbPanel;		// Panel that holds the progress bars shown while an upload is in progress.
	private InlineLabel						m_hintLabel;	// A label holding hint text about what's happening during an upload.
	private ProgressBar						m_pbPerItem;	// A 'per item' progress bar.
	private ProgressBar						m_pbTotal;		// A 'total uploads' progress bar.

	// Template used to generate the filename widgets while uploading a
	// file.
	private final static UploadTemplate UPLOAD_TEMPLATE = GWT.create(UploadTemplate.class);
	
	/**
	 * Constructor method.
	 */
	public Html5UploadPopup() {
		// Initialize the super class...
		super(false, true);	// false -> Don't auto hide.  true -> Modal.
		
		// ...enable and style that glass that shows under the popup...
		setGlassEnabled(true);
		setGlassStyleName("vibe-addFilesHtml5Popup-html5UploadPopup-glass");
		
		// ...initialize the  data members...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and construct the panel's content.
		constructContent();
	}

	/*
	 * Constructs the panel's content.
	 */
	private void constructContent() {
		// Add the panel's base style.
		addStyleName("vibe-addFilesHtml5Popup-html5UploadPopup-panel");

		// Create a FlowPanel to hold the popup's content.
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.addStyleName("vibe-addFilesHtml5Popup-html5UploadPopup-root");
		add(rootPanel);
		
		// Add a busy spinner that shows while the upload is
		// active.
		Image busyImage = GwtClientHelper.buildImage(m_images.busyAnimation_medium());
		busyImage.addStyleName("vibe-addFilesHtml5Popup-innerHintBusy");
		rootPanel.add(busyImage);
		
		// Add a label that shows the status of the upload.
		m_hintLabel = new InlineLabel(m_messages.addFilesHtml5PopupHint());
		m_hintLabel.addStyleName("vibe-addFilesHtml5Popup-innerHintLabel");
		rootPanel.add(m_hintLabel);

		// Create an abort button panel in the panel.
		FlowPanel abortPanel = new FlowPanel();
		abortPanel.addStyleName("vibe-addFilesHtml5Popup-innerHintBrowsePanel");
		m_abortButton = new Button(m_messages.addFilesHtml5PopupAbort());
		m_abortButton.addStyleName("vibe-addFilesHtml5Popup-innerHintBrowseButton");
		m_abortButton.setTitle(m_messages.addFilesHtml5PopupAbortAlt());
		m_abortButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Is the read queue is empty?
				if (m_uploadHelper.uploadsPending()) {
					// No!  Abort the uploads that are in progress.
					Html5UploadHelper.abortUpload(m_uploadHelper);
				}
				
				else {
					// Yes, the read queue is empty!  Simply hide
					// this panel.
					setActive(false);
				}
			}
		});
		abortPanel.add(m_abortButton);
		rootPanel.add(abortPanel);

		// Create an initially hidden panel containing progress bars
		// used to show upload progress.
		m_pbPanel = new VibeFlexTable();
		m_pbPanel.addStyleName("vibe-addFilesHtml5Popup-progressBar-panel");
		m_pbPanel.setVisible(false);
		rootPanel.add(m_pbPanel);
		
		InlineLabel il = new InlineLabel(m_messages.addFilesHtml5PopupProgressItem());
		il.addStyleName("vibe-addFilesHtml5Popup-progressBar-perItem-label");
		m_pbPanel.setWidget(0, 0, il);
		m_pbPerItem = new ProgressBar(0, 0, 0);
		m_pbPerItem.addStyleName("vibe-addFilesHtml5Popup-progressBar-perItem-bar");
		m_pbPanel.setWidget(0, 1, m_pbPerItem);
		
		il = new InlineLabel(m_messages.addFilesHtml5PopupProgressTotal());
		il.addStyleName("vibe-addFilesHtml5Popup-progressBar-total-label");
		m_pbPanel.setWidget(1, 0, il);
		m_pbTotal = new ProgressBar(0, 0, 0);
		m_pbTotal.addStyleName("vibe-addFilesHtml5Popup-progressBar-total-bar");
		m_pbPanel.setWidget(1, 1, m_pbTotal);
	}

	/**
	 * Increments the 'Per Item' progress bar.
	 * 
	 * @param amount
	 */
	public void incrPerItemProgress(long amount) {
		m_pbPerItem.incrProgress(amount);
	}
	
	/**
	 * Increments the 'Total' progress bar.
	 * 
	 * @param amount
	 */
	public void incrTotalProgress(long amount) {
		if (m_pbTotal.isVisible()) {
			m_pbTotal.incrProgress(amount);
		}
	}
	
	/**
	 * Shows/hides the 'Abort' push button.
	 * 
	 * @param visible
	 */
	public void setAbortVisible(boolean visible) {
		m_abortButton.setVisible(visible);
	}
	
	/**
	 * Activates or inActivates the panel.
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		if (active)
		     center();
		else hide();
	}

	/**
	 * Stores an HTML5 helper in the panel.
	 * 
	 * @param uploadHelper
	 */
	public void setHtml5UploadHelper(Html5UploadHelper uploadHelper) {
		m_uploadHelper = uploadHelper;
	}
	
	/**
	 * Sets the progress values in the 'Per Item' progress bar.
	 * 
	 * @param current
	 * @param min
	 * @param max
	 */
	public void setPerItemProgress(double current, double min, double max) {
		m_pbPerItem.setMaxProgress(max    );
		m_pbPerItem.setMinProgress(min    );
		m_pbPerItem.setProgress(   current);
	}
	
	/**
	 * Shows/hides the progress bars panel.
	 * 
	 * @param visible
	 */
	public void setProgressBarsVisible(boolean visible) {
		m_pbPanel.setVisible(visible);
	}

	/**
	 * Sets the 'Total' progress bar's current progress.
	 * 
	 * @param amount
	 */
	public void setTotalCurrentProgress(double amount) {
		if (m_pbTotal.isVisible()) {
			m_pbTotal.setProgress(amount);
		}
	}

	/**
	 * Sets the progress values in the 'Total' progress bar.
	 * 
	 * @param current
	 * @param min
	 * @param max
	 */
	public void setTotalProgress(double current, double min, double max) {
		if (m_pbTotal.isVisible()) {
			m_pbTotal.setMaxProgress(max    );
			m_pbTotal.setMinProgress(min    );
			m_pbTotal.setProgress(   current);
		}
	}
	
	/**
	 * Shows/hides the 'Total' progress bar.
	 * 
	 * @param visible
	 */
	public void setTotalProgressBarVisible(boolean visible) {
		m_pbPanel.getRowFormatter().setVisible(1, visible);
	}
	
	/**
	 * Sets the hint label to a specific string.
	 * 
	 * @param hint
	 */
	public void setUploadHint(String hint) {
		m_hintLabel.setText(hint);
	}
	
	/**
	 * Sets the hint label for uploading a file.
	 * 
	 * @param fileName
	 * @param thisFile
	 * @param totalFiles
	 */
	public void setUploadHint(String fileName, int thisFile, int totalFiles) {
		String preText  = m_messages.addFilesHtml5PopupBusyPre();
		String postText = m_messages.addFilesHtml5PopupBusyPost(thisFile, totalFiles);
		m_hintLabel.getElement().setInnerSafeHtml(UPLOAD_TEMPLATE.uploadHtml(preText, fileName, postText));
	}
}
