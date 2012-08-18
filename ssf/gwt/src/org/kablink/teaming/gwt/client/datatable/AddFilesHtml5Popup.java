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
package org.kablink.teaming.gwt.client.datatable;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.folderdata.FileBlob;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UploadFileBlobCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;
import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.Blob;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.events.ErrorEvent;
import org.vectomatic.file.events.ErrorHandler;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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
		ErrorHandler,
		LoadEndHandler
{
	private BinderInfo						m_folderInfo;				// The folder the add files is running against.
	private Button							m_browseButton;				// Button that fronts the default browser 'Browse' button.
	private DropPanel						m_dndPanel;					// The drag and drop panel that holds the popup's content.
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private InlineLabel						m_hintLabel;				// The label inside the hint box.
	private int								m_readThis;					// The file out of the total that is currently being uploaded.
	private int								m_readTotal;				// Total number of files to upload during an upload event.
	private List<File>						m_readQueue;				// List of files queued for uploading.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private FileBlob						m_fileBlob;					// Contains information about blobs of a file as they're read and uploaded.
	private FileReader						m_reader;					// Reads files.
	private FileUploadExt					m_uploadButton;				// A hidden button that services the m_browseButton above.
	private SpinnerPopup					m_busy;						// Shows a spinner while uploading files.

	// Controls whether the HTML5 popup will be used vs. the Java
	// applet to upload files.
	private static boolean USE_HTML5_POPUP	= false;	//
	
	// true -> Information about files being uploaded is displayed via
	// alerts.  false -> It's not.
	private static boolean TRACE_FILES	= true;	//
	
	// The minimum height and width of the popup.
	private static int MIN_HEIGHT		= 200;	//
	private static int MIN_WIDTH		= 800;	//

	// Padding on the left/right and top/bottom of the popup.
	private static int LEFT_RIGHT_PAD	= 240;	// This number of pixels on the left and right.
	private static int TOP_BOTTOM_PAD	= 200;	// This number of pixels on the top  and bottom.
	
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
	private AddFilesHtml5Popup() {
		// Initialize the superclass...
		super(false, true);

		// ...initialize everything else...
		m_images    = GwtTeaming.getDataTableImageBundle();
		m_messages  = GwtTeaming.getMessages();
		m_readQueue = new ArrayList<File>();
		m_reader    = new FileReader();
		m_reader.addLoadEndHandler(this);
		m_reader.addErrorHandler(  this);
		m_busy = new SpinnerPopup();
	
		// ...and create the popup's content.
		addStyleName("vibe-addFilesHtml5Popup");
		createContent();
	}

	/**
	 * Returns true if the browser being used supports uploading files
	 * using HTML5 and false otherwise.
	 * 
	 * @return
	 */
	public static boolean browserSupportsHtml5() {
//!		...this needs to be implemented...
		return USE_HTML5_POPUP;
	}

	/*
	 * Creates all the controls that make up the popup.
	 */
	private void createContent() {
		// Create an main panel to hold the popup's content.
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("vibe-addFilesHtml5Popup-panel");
		add(mainPanel);
		
		// Create a close image that is positioned at the top right
		// hand corner of the popup.
		FlowPanel closePanel = new FlowPanel();
		closePanel.addStyleName("vibe-addFilesHtml5Popup-closePanel");
		Image closeImg = new Image(m_images.closeBorder());
		closeImg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If the user clicks the close, simply hide the popup.
				hide();
			}
		});
		closePanel.add(closeImg  );
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
		m_browseButton.addClickHandler(this);
		browsePanel.add(m_browseButton);
		hintPanel.add(browsePanel);
	}

	/*
	 * If we're in debug UI mode, displays an alert about a file.
	 */
	private void debugTraceFile(String methodName, File file, boolean traceQueueSize, String traceHead, String traceTail) {
		if (TRACE_FILES && GwtClientHelper.isDebugUI()) {
			String	dump  = (traceHead + ":  '" + file.getName() + "' (" + file.getSize() + ":" + m_fileBlob.getBlobStart() + ":" + m_fileBlob.getBlobSize() + ")");
			if (traceQueueSize) {
				int		files = (m_readQueue.size() - 1);
				switch (files) {
				case 0:   dump += " there are no files pending";             break;
				case 1:   dump += " there is 1 file pending";                break;
				default:  dump += " there are "  + files + " files pending"; break;
				}
			}
			boolean hasTail = GwtClientHelper.hasString(traceTail);
			dump = ("AddFilesHtml5Popup." + methodName + "( " + dump + " )" + (hasTail ? ":  " + traceTail : ""));
			String data = m_reader.getStringResult();
			if (null != data) {
				int len = data.length();
				if (2048 < len) {
					data = (data.substring(0, 2048) + "...truncated...");
				}
				dump += ("\n\nData:  " + data); 
			}
			Window.alert(dump);
		}
	}
	
	@SuppressWarnings("unused")
	private void debugTraceFile(String methodName, File file, boolean traceQueueSize, String traceHead) {
		// Always use the initial form of the method.
		debugTraceFile(methodName, file, traceQueueSize, traceHead, null);
	}
	
	/*
	 * Called if the reader encounters an error.
	 */
	private void handleError(File file) {
		FileError error = m_reader.getError();
		String errorDesc = "";
		if (error != null) {
			ErrorCode errorCode = error.getCode();
			if (null != errorCode) {
				errorDesc = ": " + errorCode.name();
			}
		}
		GwtClientHelper.deferredAlert(m_messages.addFilesHtml5PopupReadError(file.getName(), errorDesc));
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
		processFiles(m_uploadButton.getFiles());
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
		if (m_readQueue.isEmpty()) {
			// Yes!  Let the user select some files to upload.
			m_uploadButton.click();
		}
		else {
			// No, the read queue isn't empty!  Abort and read
			// in progress and clear the read queue to cancel
			// uploading.
			m_reader.abort();
			m_readQueue.clear();
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
		if (m_readQueue.isEmpty()) {
			           addStyleName("vibe-addFilesHtml5Popup-hover"        );
			m_dndPanel.addStyleName("vibe-addFilesHtml5Popup-dndPanelHover");
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
		if (m_readQueue.isEmpty()) {
			           removeStyleName("vibe-addFilesHtml5Popup-hover"        );
			m_dndPanel.removeStyleName("vibe-addFilesHtml5Popup-dndPanelHover");
		}
		event.stopPropagation();
		event.preventDefault();
	}

	/**
	 * Called when a something is being dragged over the drag and drop
	 * popup.
	 * 
	 * Implements the DragEnterHandler.onDragOver() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragOver(DragOverEvent event) {
		// Mandatory handler, otherwise the default behavior will kick
		// in and onDrop will never be called.
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
		if (m_readQueue.isEmpty()) {
	                   removeStyleName("vibe-addFilesHtml5Popup-hover"        );
			m_dndPanel.removeStyleName("vibe-addFilesHtml5Popup-dndPanelHover");
			processFiles(event.getDataTransfer().<DataTransferExt>cast().getFiles());
		}
		event.stopPropagation();
		event.preventDefault();
	}

	/**
	 * Called if the read encounters an error reading a file.
	 * 
	 * Implements the ErrorHandler.onError() method.
	 * 
	 * @param event
	 */
	@Override
	public void onError(ErrorEvent event) {
		if (!(m_readQueue.isEmpty())) {
			File file = m_readQueue.get(0);
			handleError(file);
			m_readQueue.remove(0);
			uploadNextNow();
		}
	}
	
	/**
	 * Called when the reader completes reading a file.
	 * 
	 * Implements the LoadEndHandler.onLoadEnd() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLoadEnd(LoadEndEvent event) {
		if (null == m_reader.getError()) {
			if (!(m_readQueue.isEmpty())) {
				File file = m_readQueue.get(0);
				debugTraceFile("onLoadEnd", file, true, "Just read", "...this needs to be implemented...");
				
				processBlobAsync();
			}
		}
	}

	/*
	 * Asynchronously processes the next blob read from a file.
	 */
	private void processBlobAsync() {
		Scheduler.ScheduledCommand doProcessBlob = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				processBlobNow();
			}
		};
		Scheduler.get().scheduleDeferred(doProcessBlob);
	}
	
	/*
	 * Synchronously processes the next blob read from a file.
	 */
	private void processBlobNow() {
		// Upload the blob.
		final boolean lastBlob = ((m_fileBlob.getBlobStart() + m_fileBlob.getBlobSize()) >= m_fileBlob.getFileSize());
		m_fileBlob.setBlobData(m_reader.getStringResult());
		final UploadFileBlobCmd cmd = new UploadFileBlobCmd(m_folderInfo, m_fileBlob, lastBlob);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_UploadFileBlob(),
					m_fileBlob.getFileName());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Are we done uploading this file?
				String	uploadError    = ((StringRpcResponseData) result.getResponseData()).getStringValue();
				boolean	hasUploadError = GwtClientHelper.hasString(uploadError); 
				if (lastBlob || hasUploadError) {
					// Yes!  If we stopped because of an error...
					if (hasUploadError) {
						// ...display the error...
						GwtClientHelper.deferredAlert(uploadError);
					}
					
					// ...and continue with the next file.
					m_readQueue.remove(0);
					uploadNextAsync();
				}
				
				else {
					// No, we aren't done uploading this file!
					File file = m_readQueue.get(0);
					m_fileBlob.incrBlobStart(m_fileBlob.getBlobSize());
					try {
						// Read the next blob.
						Blob readBlob = file.slice(m_fileBlob.getBlobStart(), m_fileBlob.getBlobSize());
						readNextBlobAsync(readBlob);
					}
					
					catch(Throwable t) {
						// Necessary for FF (see bug https://bugzilla.mozilla.org/show_bug.cgi?id=701154.)
						// Standard-complying browsers will to go in this branch.
						handleError(file);
						m_readQueue.remove(0);
						uploadNextAsync();
					}
				}
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the popup.
	 */
	private void populatePopupAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populatePopupNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the popup.
	 */
	private void populatePopupNow() {
		// Size...
		String h = (Math.max(MIN_HEIGHT, (Window.getClientHeight() - (TOP_BOTTOM_PAD * 2))) + "px"); 
		String w = (Math.max(MIN_WIDTH,  (Window.getClientWidth()  - (LEFT_RIGHT_PAD * 2))) + "px");
		
		setHeight(h); m_dndPanel.setHeight(h);
		setWidth( w); m_dndPanel.setWidth( w);
		
		// ...and show the popup.
		center();
	}
	
	/*
	 * Called when some files are dropped on the panel.
	 */
	private void processFiles(FileList files) {
		// If we weren't given any files to process...
		if ((null == files) || (0 >= files.getLength())) {
			// ...bail.
			return;
		}
		
		// Copy the files into the read queue...
		for (File file:  files) {
			m_readQueue.add(file);
		}

		// ...and upload them.
		m_readTotal = m_readQueue.size();
		m_readThis  = 0;
		uploadNextNow();
	}

	/*
	 * Asynchronously initiates the reading of the given blob.
	 */
	private void readNextBlobAsync(final Blob readBlob) {
		Scheduler.ScheduledCommand doRead = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				readNextBlobNow(readBlob);
			}
		};
		Scheduler.get().scheduleDeferred(doRead);
	}
	
	/*
	 * Synchronously initiates the reading of the given blob.
	 */
	private void readNextBlobNow(final Blob readBlob) {
//!		m_reader.readAsArrayBuffer( readBlob);
		m_reader.readAsDataURL(     readBlob);
//!		m_reader.readAsBinaryString(readBlob);
//!		m_reader.readAsText(        readBlob);
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
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				afPopup.runPopupNow(fi);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
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

	/*
	 * Asynchronously uploads the next file.
	 */
	private void uploadNextAsync() {
		Scheduler.ScheduledCommand doRead = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				uploadNextNow();
			}
		};
		Scheduler.get().scheduleDeferred(doRead);
	}
	
	/*
	 * Synchronously uploads the next file.
	 * 
	 * Called to read and upload the next file in the queue.
	 */
	private void uploadNextNow() {
		if (m_readQueue.isEmpty()) {
			m_browseButton.setText( m_messages.addFilesHtml5PopupBrowse()   );
			m_browseButton.setTitle(m_messages.addFilesHtml5PopupBrowseAlt());
			m_hintLabel.setText(    m_messages.addFilesHtml5PopupHint()     );
			m_busy.hide();
		}
		
		else {
			if ((!(m_busy.isAttached())) || (!(m_busy.isVisible()))) {
				m_browseButton.setText( m_messages.addFilesHtml5PopupCancel()   );
				m_browseButton.setTitle(m_messages.addFilesHtml5PopupCancelAlt());
				m_busy.center();
			}
			
			File file = m_readQueue.get(0);
			m_hintLabel.setText(m_messages.addFilesHtml5PopupBusy(file.getName(), ++m_readThis, m_readTotal));
			try {
				m_fileBlob    = new FileBlob(file.getName(), file.getLastModifiedDate().toUTCString(), file.getSize());
				Blob readBlob = file.slice(m_fileBlob.getBlobStart(), m_fileBlob.getBlobSize());
				readNextBlobNow(readBlob);
			}
			
			catch(Throwable t) {
				// Necessary for FF (see bug https://bugzilla.mozilla.org/show_bug.cgi?id=701154.)
				// Standard-complying browsers will to go in this branch.
				handleError(file);
				m_readQueue.remove(0);
				uploadNextAsync();
			}
		}
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
					AddFilesHtml5Popup afPopup = new AddFilesHtml5Popup();
					afPopupClient.onSuccess(afPopup);
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
