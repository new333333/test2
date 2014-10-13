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
package org.kablink.teaming.gwt.client.datatable;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.Html5UploadHelper;
import org.kablink.teaming.gwt.client.util.Html5UploadCallback;
import org.kablink.teaming.gwt.client.util.Html5UploadState;
import org.kablink.teaming.gwt.client.widgets.ProgressBar;
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileUploadExt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's 'add files' using HTML5 popup.
 *  
 * @author drfoster@novell.com
 */
public class AddFilesHtml5Popup extends TeamingPopupPanel
	implements
		ChangeHandler,
		ClickHandler,
		DragEnterHandler,
		DragLeaveHandler,
		DragOverHandler,
		DropHandler,
		Html5UploadCallback,
		KeyDownHandler
{
	private AddFilesHtml5PopupClient		m_afPopupClient;			// Client callback into the code that loaded the popup's split point.
	private BinderInfo						m_folderInfo;				// The folder the add files is running against.
	private Button							m_browseButton;				// Button that fronts the default browser 'Browse' button.
	private Button							m_closeButton;				// Button next to the Browse to close the popup.
	private DropPanel						m_dndPanel;					// The drag and drop panel that holds the popup's content.
	private FlexTable						m_pbPanel;					// Panel that will hold the progress bars.
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private Html5UploadHelper				m_uploadHelper;				// The HTML5 upload APIs.
	private Image							m_busyImage;				// Image holding a spinner that's show while an upload is happening.
	private Image							m_closeX;					// The 'X' in the upper right corner of the upload popup.
	private InlineLabel						m_hintLabel;				// The label inside the hint box.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private FileUploadExt					m_uploadButton;				// A hidden button that services the m_browseButton above.
	private ProgressBar						m_pbPerItem;				// Displays a per item progress bar as items are uploaded to the server.
	private ProgressBar						m_pbTotal;					// Displays a total    progress bar as items are uploaded to the server.

	// true -> Hide the popup after an upload.  false -> Don't.
	private static boolean AUTOHIDE_ON_COMPLETE	= true;	//
	
	// The minimum height and width of the popup.
	private static int MIN_HEIGHT	= 250;	//
	private static int MIN_WIDTH	= 800;	//

	// Padding on the left/right and top/bottom of the popup.
	private static int LEFT_RIGHT_PAD	= 50;	// This number of pixels on the left and right.
	private static int TOP_BOTTOM_PAD	= 50;	// This number of pixels on the top  and bottom.
	
	// Template used to generate the filename widgets while uploading a
	// file.
	private final static UploadTemplate UPLOAD_TEMPLATE = GWT.create(UploadTemplate.class);
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
	};

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AddFilesHtml5Popup(AddFilesHtml5PopupClient afPopupClient) {
		// Initialize the superclass...
		super(false, true);
		
		// ...store the parameter...
		m_afPopupClient = afPopupClient;
		
		// ...initialize the  data members...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		// ...create the popup's content...
		addStyleName("vibe-addFilesHtml5Popup");
		createContent();

		// ...and create an Html5UploadHelper to do the uploads with.
		loadPart1Async();
	}

	/*
	 * Creates all the controls that make up the popup.
	 */
	private void createContent() {
		// Create a main panel to hold the popup's content.
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("vibe-addFilesHtml5Popup-panel");
		add(mainPanel);
		
		// Create a close image that is positioned at the top right
		// hand corner of the popup.
		FlowPanel closePanel = new FlowPanel();
		closePanel.addStyleName("vibe-addFilesHtml5Popup-closePanel");
		m_closeX = new Image(m_images.closeBorder());
		m_closeX.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If the user clicks the close, simply hide the popup.
				hide();
			}
		});
		closePanel.add(m_closeX  );
		mainPanel.add( closePanel);
		
		// Create a drag and drop panel for dropping into...
		m_dndPanel = new DropPanel();
		m_dndPanel.addStyleName("vibe-addFilesHtml5Popup-dndPanel");

		// ...connect the various drag and drop handlers to it...
		m_dndPanel.addDragEnterHandler(this);
		m_dndPanel.addDragLeaveHandler(this);
		m_dndPanel.addDragOverHandler( this);
		m_dndPanel.addDropHandler(     this);
		
		// ...and add it to the main panel.
		mainPanel.add(m_dndPanel);
		
		// Create a hint panel in the center of the drag and drop panel.
		FlowPanel hintPanel = new FlowPanel();
		hintPanel.addStyleName("vibe-addFilesHtml5Popup-innerHintPanel");
		m_dndPanel.add(hintPanel);
		
		m_busyImage = GwtClientHelper.buildImage(m_images.busyAnimation_medium());
		m_busyImage.addStyleName("vibe-addFilesHtml5Popup-innerHintBusy");
		m_busyImage.setVisible(false);
		hintPanel.add(m_busyImage);
		
		m_hintLabel = new InlineLabel(m_messages.addFilesHtml5PopupHint());
		m_hintLabel.addStyleName("vibe-addFilesHtml5Popup-innerHintLabel");
		hintPanel.add(m_hintLabel);

		// Create a browse button panel in the hint panel.
		FlowPanel browsePanel = new FlowPanel();
		browsePanel.addStyleName("vibe-addFilesHtml5Popup-innerHintBrowsePanel");
		m_uploadButton = new FileUploadExt();
		m_uploadButton.addStyleName("vibe-addFilesHtml5Popup-innerHintBrowseUpload");
		m_uploadButton.addChangeHandler(this);
		browsePanel.add(m_uploadButton);
		m_browseButton = new Button(m_messages.addFilesHtml5PopupBrowse());
		m_browseButton.addStyleName("vibe-addFilesHtml5Popup-innerHintBrowseButton");
		m_browseButton.setTitle(m_messages.addFilesHtml5PopupBrowseAlt());
		m_browseButton.addClickHandler(  this);
		m_browseButton.addKeyDownHandler(this);
		browsePanel.add(m_browseButton);
		m_closeButton = new Button(m_messages.addFilesHtml5PopupClose());
		m_closeButton.addStyleName("vibe-addFilesHtml5Popup-innerHintCloseButton");
		m_browseButton.setTitle(m_messages.addFilesHtml5PopupCloseAlt());
		m_closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		browsePanel.add(m_closeButton);
		hintPanel.add(browsePanel);

		// Create an initially hidden panel containing progress bars
		// used to show upload progress.
		m_pbPanel = new VibeFlexTable();
		m_pbPanel.addStyleName("vibe-addFilesHtml5Popup-progressBar-panel");
		m_pbPanel.setVisible(false);
		hintPanel.add(m_pbPanel);
		
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
	 * Some folders were in the files requested to be uploaded and
	 * were skipped.
	 * 
	 * @param count
	 * @param folderNames
	 * 
	 * Implements the Html5UploadCallback.foldersSkipped() method.
	 */
	@Override
	public void foldersSkipped(int count, String folderNames) {
		GwtClientHelper.deferredAlert(
			m_messages.addFilesHtml5PopupFoldersSkipped(
				folderNames));
	}

	/**
	 * Advance the progress indicator.
	 * 
	 * @param amount
	 * 
	 * Implements the Html5UploadCallback.incrProgress() method.
	 */
	@Override
	public void incrProgress(long amount) {
		m_pbPerItem.incrProgress(amount);
		if (m_pbTotal.isVisible()) {
			m_pbTotal.incrProgress(amount);
		}
	}

	/*
	 * Asynchronously create an Html5UploadHelper to do the uploads
	 * with.
	 */
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously create an Html5UploadHelper to do the uploads
	 * with.
	 */
	private void loadPart1Now() {
		// Create the Html5UploadHelper.
		Html5UploadHelper.createAsync(this);
	}
	
	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides Widget.onAttach()
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}

	/**
	 * Called when the file selection off the 'Browse' button has
	 * changed.
	 * 
	 * Implements the ChangeHandler.onChange() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChange(ChangeEvent event) {
		Html5UploadHelper.uploadFiles(
			m_uploadHelper,
			m_folderInfo,
			m_uploadButton.getFiles(),
			new FullUIReloadEvent());
	}
	

	/**
	 * Called when the 'Browse' button is clicked.
	 * 
	 * Implements the ClickHandler.onClick() method.
	 * 
	 * @param event.
	 */
	@Override
	public void onClick(ClickEvent event) {
		// Is the read queue is empty?
		if (m_uploadHelper.uploadsPending()) {
			// No!  Abort the uploads that are in progress.
			Html5UploadHelper.abortUpload(m_uploadHelper);
		}
		
		else {
			// Yes, the read queue is empty!  Let the user select some
			// files to upload.
			m_uploadButton.click();
		}
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Called when a something is dragged into the drag and drop popup.
	 * 
	 * Implements the DragEnterHandler.onDragEnter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragEnter(DragEnterEvent event) {
		if (!(m_uploadHelper.uploadsPending())) {
			setDnDHighlight(true);
		}
		event.stopPropagation();
		event.preventDefault();
	}
	
	/**
	 * Called when a something is dragged out of the drag and drop
	 * popup.
	 * 
	 * Implements the DragEnterHandler.onDragLeave() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragLeave(DragLeaveEvent event) {
		if (!(m_uploadHelper.uploadsPending())) {
			setDnDHighlight(false);
		}
		event.stopPropagation();
		event.preventDefault();
	}

	/**
	 * Called when a something is being dragged over the drag and drop
	 * popup.
	 *
	 * Mandatory handler, otherwise the default behavior will kick in
	 * in and onDrop will never be called.
	 * 
	 * Implements the DragEnterHandler.onDragOver() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragOver(DragOverEvent event) {
		if (!(m_uploadHelper.uploadsPending())) {
			setDnDHighlight(true);
		}
		event.stopPropagation();
		event.preventDefault();
	}

	/**
	 * Called when a something is dropped on the drag and drop popup.
	 * 
	 * Implements the DragEnterHandler.onDrop() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDrop(DropEvent event) {
		if (!(m_uploadHelper.uploadsPending())) {
			setDnDHighlight(false);
			
			// If the drop data doesn't contain any files...
			FileList fileList = event.getDataTransfer().<DataTransferExt>cast().getFiles();
			int files = ((null == fileList) ? 0 : fileList.getLength());
			if (0 == files) {
				// ...tell the user about the problem...
				String warning;
				if (GwtClientHelper.jsIsAnyIE())
				     warning = m_messages.html5Uploader_Warning_NoFilesIE();
				else warning = m_messages.html5Uploader_Warning_NoFiles();
				GwtClientHelper.deferredAlert(warning);
			}
			else {
				// ...otherwise, upload the files that were dropped...
				Html5UploadHelper.uploadFiles(
					m_uploadHelper,
					m_folderInfo,
					fileList,
					new FullUIReloadEvent());
			}
		}
		event.stopPropagation();
		event.preventDefault();
	}

	/**
	 * Called when the user presses a key in the popup.
	 * 
	 * Implements the KeyDownHandler.onKeyDown() method.
	 * 
	 * @param event
	 */
	@Override
	public void onKeyDown(KeyDownEvent event) {
		// Is there an upload in progress?
		if (!(m_uploadHelper.uploadsPending())) {
			// No!  What key is being pressed?
			switch (event.getNativeEvent().getKeyCode()) {
			case KeyCodes.KEY_ENTER:
				// Enter!  Let the user select some files to upload.
				m_uploadButton.click();
				break;
				
			case KeyCodes.KEY_ESCAPE:
				// Escape!  Simply hide the popup.
				hide();
				break;
			}
		}
	}
	
	/**
	 * The helper was successfully loaded and is available for
	 * uploading files.
	 * 
	 * @param uploadHelper
	 * 
	 * Implements the Html5UploadCallback.onSuccess() method.
	 */
	@Override
	public void onSuccess(Html5UploadHelper uploadHelper) {
		m_uploadHelper = uploadHelper;
		m_afPopupClient.onSuccess(this);
	}

	/**
	 * The helper failed to load.
	 * 
	 * Note that the user will have been told about the failure.
	 * 
	 * Implements the Html5UploadCallback.onUnavailable() method.
	 */
	@Override
	public void onUnavailable() {
		// Nothing to do but hide the popup.  The user will have been
		// told about the error.
		hide();
	}

	/*
	 * Asynchronously populates the contents of the popup.
	 */
	private void populatePopupAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populatePopupNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the popup.
	 */
	private void populatePopupNow() {
		// Size the popup...
		String h = (Math.max(MIN_HEIGHT, (Window.getClientHeight() - (TOP_BOTTOM_PAD * 2))) + "px"); 
		String w = (Math.max(MIN_WIDTH,  (Window.getClientWidth()  - (LEFT_RIGHT_PAD * 2))) + "px");
		
		setHeight(h); m_dndPanel.setHeight(h);
		setWidth( w); m_dndPanel.setWidth( w);

		// ...show the gray glass under it to express modality...
		setGlassEnabled(  true                           );
		setGlassStyleName("vibe-addFilesHtml5Popup-glass");
		
		// ...show it...
		center();

		// ...and leave the input focus in the browse button.
		GwtClientHelper.setFocusDelayed(m_browseButton);
	}
	
	/**
	 * An error occurred uploading a file.
	 * 
	 * @param fileName
	 * @param errorDescription
	 * 
	 * Implements the Html5UploadCallback.readError() method.
	 */
	@Override
	public void readError(String fileName, String errorDescription) {
		GwtClientHelper.deferredAlert(
			m_messages.addFilesHtml5PopupReadError(
				fileName,
				errorDescription));
	}

	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously runs the given instance of the add files popup.
	 */
	private static void runPopupAsync(final AddFilesHtml5Popup afPopup, final BinderInfo fi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				afPopup.runPopupNow(fi);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the add files popup.
	 */
	private void runPopupNow(BinderInfo fi) {
		// Store the parameter and start populating the popup.
		m_folderInfo = fi;
		populatePopupAsync();
	}

	/*
	 * Sets or clears the drag and drop highlighting on the popup.
	 */
	private void setDnDHighlight(boolean highlight) {
		if (highlight) {
			           addStyleName("vibe-addFilesHtml5Popup-hover"        );
			m_dndPanel.addStyleName("vibe-addFilesHtml5Popup-dndPanelHover");
		}
		
		else {
			           removeStyleName("vibe-addFilesHtml5Popup-hover"        );
			m_dndPanel.removeStyleName("vibe-addFilesHtml5Popup-dndPanelHover");
		}
	}
	
	/**
	 * Sets the current files progress indicator.
	 * 
	 * @param min
	 * @param max
	 * 
	 * Implements the Html5UploadCallback.setPerItemProgress() method.
	 */
	@Override
	public void setPerItemProgress(long min, long max) {
		m_pbPerItem.setMaxProgress(max);
		m_pbPerItem.setMinProgress(min);
		m_pbPerItem.setProgress(   0  );
	}

	/**
	 * Update the total progress to the specified value.
	 * 
	 * @param amount
	 * 
	 * Implements the Html5UploadCallback.setTotalCurrentProgress() method.
	 */
	@Override
	public void setTotalCurrentProgress(double amount) {
		if (m_pbTotal.isVisible()) {
			m_pbTotal.setProgress(amount);
		}
	}

	/**
	 * Set the total maximum progress value. 
	 * 
	 * @param max
	 * 
	 * Implements the Html5UploadCallback.setTotalMaxProgress() method.
	 */
	@Override
	public void setTotalMaxProgress(double max) {
		if (m_pbTotal.isVisible()) {
			m_pbTotal.setMaxProgress(max);
			m_pbTotal.setMinProgress(0  );
			m_pbTotal.setProgress(   0  );
		}
	}

	/**
	 * Sets the current state of the upload.
	 * 
	 * @param previousState
	 * @param newState
	 * 
	 * Implements the Html5UploadCallback.setUploadState() method.
	 */
	@Override
	public void setUploadState(Html5UploadState previousState, Html5UploadState newState) {
		switch (newState) {
		case UPLOADING:
			// We're uploading a file!  If we weren't previously
			// performing an upload...
			if (!(previousState.equals(Html5UploadState.UPLOADING))) {
				// ...update the text displayed...
				m_browseButton.setVisible(true                                   );
				m_browseButton.setText(   m_messages.addFilesHtml5PopupAbort()   );
				m_browseButton.setTitle(  m_messages.addFilesHtml5PopupAbortAlt());
				
				// ...hide the close button...
				m_closeButton.setVisible(true );
				m_closeButton.setVisible(false);
				m_closeX.setVisible(     false);
				
				// ...and show the progress panel and if necessary.
				m_busyImage.setVisible(true);
				m_pbPanel.setVisible(  true);
				m_pbPanel.getRowFormatter().setVisible(1, (1 < m_uploadHelper.getReadTotal()));
			}
			
			break;
			
		case INACTIVE:
			// We're waiting for user input!  Restore the browse
			// button...
			m_browseButton.setVisible(true                                    );
			m_browseButton.setText(   m_messages.addFilesHtml5PopupBrowse()   );
			m_browseButton.setTitle(  m_messages.addFilesHtml5PopupBrowseAlt());
			
			// ..restore the close button...
			m_closeButton.setVisible(true);
			m_closeX.setVisible(     true);
			
			// ...update the text displayed...
			m_hintLabel.setText(m_messages.addFilesHtml5PopupHint());
			
			// ...clear out any value in the upload widget so the the
			// ...same file can be reselected, if desired...
			m_uploadButton.getElement().setPropertyString("value", "");

			// ...and hide any necessary widgets.
			m_busyImage.setVisible(false);
			m_pbPanel.setVisible(  false);
			break;
			
		case VALIDATING:
			// We're validating the selections before uploading them!
			// Hide the browse and close buttons...
			m_browseButton.setVisible(false);
			m_closeButton.setVisible( false);
			m_closeX.setVisible(      false);

			// ...show the busy spinner...
			m_busyImage.setVisible(true);

			// ...and set the hint to show that we're validating.
			m_hintLabel.setText(m_messages.addFilesHtml5PopupValidating());
			break;
		}
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}

	/**
	 * The upload has completed.
	 * 
	 * @param aborted
	 * @param completeEvent
	 * 
	 * Implements the Html5UploadCallback.uploadComplete() method.
	 */
	@Override
	public void uploadComplete(boolean aborted, VibeEventBase<?> completeEvent) {
		// If we're supposed...
		if ((!aborted) && AUTOHIDE_ON_COMPLETE) {
			// ...close the upload popup...
			hide();
		}
		
		// ...and if we were given an event to fire upon completion...
		if (null != completeEvent) {
			// ...fire it.
			GwtTeaming.fireEventAsync(completeEvent);
		}
	}

	/**
	 * We're now uploading the next file.
	 * 
	 * @param fileName
	 * @param thisFile
	 * @param totalFiles
	 * 
	 * Implements the Html5UploadCallback.uploadingNextFile() method.
	 */
	@Override
	public void uploadingNextFile(String fileName, int thisFile, int totalFiles) {
		String preText  = m_messages.addFilesHtml5PopupBusyPre();
		String postText = m_messages.addFilesHtml5PopupBusyPost(thisFile, totalFiles);
		m_hintLabel.getElement().setInnerSafeHtml(UPLOAD_TEMPLATE.uploadHtml(preText, fileName, postText));
	}
	
	/**
	 * Errors occurred while validating the upload request.
	 * 
	 * @param errors
	 * 
	 * Implements the Html5UploadCallback.validationErrors() method.
	 */
	@Override
	public void validationErrors(List<ErrorInfo> errors) {
		GwtClientHelper.displayMultipleErrors(
			m_messages.addFilesHtml5PopupUploadValidationError(),
			errors);
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the add files using HTML5 popup and perform some operation on */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the add files popup
	 * asynchronously after it loads. 
	 */
	public interface AddFilesHtml5PopupClient {
		void onSuccess(AddFilesHtml5Popup afPopup);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the AddFilesHtml5Popup and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final AddFilesHtml5PopupClient afPopupClient,
			
			// initAndShow parameters,
			final AddFilesHtml5Popup afPopup,
			final BinderInfo fi) {
		GWT.runAsync(AddFilesHtml5Popup.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_AddFilesHtml5Popup());
				if (null != afPopupClient) {
					afPopupClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a popup?
				if (null != afPopupClient) {
					// Yes!  Create it and return it via the callback.
					@SuppressWarnings("unused")
					AddFilesHtml5Popup afPopup = new AddFilesHtml5Popup(afPopupClient);
				}
				
				else {
					// No, it's not a request to create a popup!  It
					// must be a request to run an existing one.  Run
					// it.
					runPopupAsync(afPopup, fi);
				}
			}
		});
	}
	
	/**
	 * Loads the AddFilesHtml5Popup split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param afPopupClient
	 */
	public static void createAsync(AddFilesHtml5PopupClient afPopupClient) {
		doAsyncOperation(afPopupClient, null, null);
	}
	
	/**
	 * Initializes and shows the add files popup.
	 * 
	 * @param afPopup
	 * @param fi
	 */
	public static void initAndShow(AddFilesHtml5Popup afPopup, BinderInfo fi) {
		doAsyncOperation(null, afPopup, fi);
	}
}
