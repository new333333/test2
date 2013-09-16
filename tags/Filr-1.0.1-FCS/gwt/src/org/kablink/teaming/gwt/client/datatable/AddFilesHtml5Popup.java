/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import java.util.Date;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.folderdata.FileBlob;
import org.kablink.teaming.gwt.client.datatable.FileConflictsDlg.FileConflictsDlgClient;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.AbortFileUploadCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UploadFileBlobCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateUploadsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateUploadsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.UploadInfo;
import org.kablink.teaming.gwt.client.widgets.ProgressBar;
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;

import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.Blob;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.FileUtils;
import org.vectomatic.file.FileReader.State;
import org.vectomatic.file.events.ErrorEvent;
import org.vectomatic.file.events.ErrorHandler;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

import com.googlecode.gwt.crypto.bouncycastle.digests.MD5Digest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsDate;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
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
		ErrorHandler,
		KeyDownHandler,
		LoadEndHandler
{
	private BinderInfo						m_folderInfo;				// The folder the add files is running against.
	private Button							m_browseButton;				// Button that fronts the default browser 'Browse' button.
	private Button							m_closeButton;				// Button next to the Browse to close the popup.
	private DropPanel						m_dndPanel;					// The drag and drop panel that holds the popup's content.
	private FlexTable						m_pbPanel;					// Panel that will hold the progress bars.
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private Image							m_busyImage;				// Image holding a spinner that's show while an upload is happening.
	private Image							m_closeX;					// The 'X' in the upper right corner of the upload popup.
	private InlineLabel						m_hintLabel;				// The label inside the hint box.
	private int								m_readThis;					// The file out of the total that is currently being uploaded.
	private int								m_readTotal;				// Total number of files to upload during an upload event.
	private List<File>						m_ignoredAsFolders;			// Tracks files that are dropped that 'appear' to be folders and are hence, ignored.
	private List<File>						m_readQueue;				// List of files queued for uploading.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private FileBlob						m_fileBlob;					// Contains information about blobs of a file as they're read and uploaded.
	private FileReader						m_reader;					// Reads files.
	private FileUploadExt					m_uploadButton;				// A hidden button that services the m_browseButton above.
	private ProgressBar						m_pbPerItem;				// Displays a per item progress bar as items are uploaded to the server.
	private ProgressBar						m_pbTotal;					// Displays a total    progress bar as items are uploaded to the server.

	// true -> Information about file blobs being uploaded is displayed
	// via alerts.  false -> They're not.
	private static boolean TRACE_BLOBS	= false;	//
	
	// true -> Hide the popup after an upload.  false -> Don't.
	private static boolean AUTOHIDE_ON_COMPLETE	= true;	//
	
	// Defines the default type of HTML5 file read used to upload
	// files.
	private static ReadType DEFAULT_READ_TYPE	= ReadType.BINARY_STRING;
	
	// The minimum height and width of the popup.
	private static int MIN_HEIGHT	= 250;	//
	private static int MIN_WIDTH	= 800;	//

	// Padding on the left/right and top/bottom of the popup.
	private static int LEFT_RIGHT_PAD	= 50;	// This number of pixels on the left and right.
	private static int TOP_BOTTOM_PAD	= 50;	// This number of pixels on the top  and bottom.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
	};

	/*
	 * Used to specify how to read files for streaming to the server.
	 */
	private enum ReadType {
		ARRAY_BUFFER,
		BINARY_STRING,
		DATA_URL,
		TEXT,
	}

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
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		m_ignoredAsFolders = new ArrayList<File>();
		m_readQueue        = new ArrayList<File>();
		
		m_reader = new FileReader();
		m_reader.addLoadEndHandler(this);
		m_reader.addErrorHandler(  this);
		
		// ...and create the popup's content.
		addStyleName("vibe-addFilesHtml5Popup");
		createContent();
	}

	/*
	 * Does what's necessary to abort a file upload sequence.
	 */
	private void abortUpload() {
		// If the reader's loading...
		if (State.LOADING == m_reader.getReadyState()) {
			// ...abort it...
			m_reader.abort();
		}
		
		// ...empty the read queue...
		m_readQueue.clear();

		// ...and tell the server that we've aborted the upload so
		// ...it can clean up anything it has hanging around.
		final AbortFileUploadCmd cmd = new AbortFileUploadCmd(m_folderInfo);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Ignored.
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Ignored.  Nothing to do.
			}
		});
	}
	
	/**
	 * Returns true if the browser being used supports uploading files
	 * using HTML5 and false otherwise.
	 * 
	 * @return
	 */
	public static boolean browserSupportsHtml5() {
		return GwtClientHelper.jsBrowserSupportsHtml5FileAPIs();
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

	/*
	 * If we're in debug UI mode, displays an alert about a file blob.
	 */
	private void debugTraceBlob(String methodName, boolean traceQueueSize, String traceHead, String traceTail) {
		if (TRACE_BLOBS && GwtClientHelper.isDebugUI()) {
			String	dump  = (traceHead + ":  '" + m_fileBlob.getFileName() + "' (fSize:" + m_fileBlob.getFileSize() + ", bStart:" + m_fileBlob.getBlobStart() + ", bSize:" + m_fileBlob.getBlobSize() + ", md5Hash:" + m_fileBlob.getBlobMD5Hash() + ")");
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
			String data = m_fileBlob.getBlobData();
			dump += ("\n\nData Read:  " + ((null == data) ? 0 : data.length()) + (m_fileBlob.isBlobBase64Encoded() ? " base64 encoded" : "") + " bytes."); 
			Window.alert(dump);
		}
	}
	
	private void debugTraceBlob(String methodName, boolean traceQueueSize, String traceHead) {
		// Always use the initial form of the method.
		debugTraceBlob(methodName, traceQueueSize, traceHead, null);
	}

	/*
	 * Returns the current File being operation on.
	 */
	private File getCurrentFile() {
		File reply;
		if (uploadsPending())
		     reply = m_readQueue.get(0);
		else reply = null;
		return reply;
	}

	/*
	 * Returns the string to use for file's date.
	 */
	private String getFileUTC(File file) {
		JsDate fileDate = ((null == file) ? null : file.getLastModifiedDate());
		return ((null == fileDate) ? null : fileDate.toUTCString());
	}

	/*
	 * Returns the Long to use for file's date.
	 */
	private Long getFileUTCMS(File file) {
		JsDate fileDate = ((null == file) ? null : file.getLastModifiedDate());
		return ((null == fileDate) ? null : new Long((long) fileDate.getTime()));
	}

	/*
	 * Returns the MD5 hash key for the given string.
	 */
	private String getMD5Hash(String text) {
		byte[]		bs = text.getBytes();
		MD5Digest	sd = new MD5Digest();
		sd.update(bs, 0, bs.length);
		byte[] result = new byte[sd.getDigestSize()];
		sd.doFinal(result, 0);
		return byteArrayToHexString(result);
	}

	/*
	 * Converts a byte[] to a string of hex characters.
	 */
	private String byteArrayToHexString(final byte[] b) {
		final StringBuffer sb = new StringBuffer(b.length * 2);
		final int baLen = b.length;
		for (int i = 0; i < baLen; i += 1) {
			int v = (b[i] & 0xff);
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}
	
	/*
	 * Returns the total size of the files pending upload.
	 */
	private double getTotalQueueFileSize() {
		double reply = 0;
		for (File file:  m_readQueue) {
			reply += file.getSize();
		}
		return reply;
	}
	
	/*
	 * Called if the reader encounters an error.
	 */
	private void handleError(File file) {
		FileError	error     = m_reader.getError();
		String		errorDesc = "";
		if (null != error) {
			ErrorCode errorCode = error.getCode();
			if (null != errorCode) {
				switch (errorCode) {
				case ABORT_ERR:
					// We ignore abort errors since these are user
					// driven.  No need to tell them they just aborted
					// the upload.
					return;
					
				case NOT_FOUND_ERR:
					// If we're running on WebKit...
					if (GwtClientHelper.jsIsWebkit()) {
						// ...treat this as the user having uploaded
						// ...a folder, which is not supported.
						m_ignoredAsFolders.add(getCurrentFile());
						return;
					}
					
					// ...otherwise, fall through and handle with the
					// ...other errors.
					
				default:
					errorDesc = errorCode.name();
					break;
				}
			}
		}
		
		// If we get here, we need to tell the user about the error.
		GwtClientHelper.deferredAlert(
			m_messages.addFilesHtml5PopupReadError(
				file.getName(),
				errorDesc));
	}

	/*
	 * Displays an error to the user if any files were ignored as
	 * folders.
	 */
	private int handleIgnoredFolders() {
		// Are we tracking any ignored folders?
		int reply = 0;
		if (0 < m_ignoredAsFolders.size()) {
			// Yes!  Display them to the user...
			StringBuffer folderNames = new StringBuffer("");
			for (File ignoredFolder:  m_ignoredAsFolders) {
				if (0 < reply) {
					folderNames.append(", ");
				}
				reply += 1;
				
				folderNames.append("'");
				folderNames.append(ignoredFolder.getName());
				folderNames.append("'");
			}
			
			GwtClientHelper.deferredAlert(
				m_messages.addFilesHtml5PopupFoldersSkipped(
					folderNames.toString()));

			// ...and clear the list.
			m_ignoredAsFolders.clear();
		}
		
		// If we get here, reply contains a count of the ignored
		// folders.  Return it.
		return reply;
	}

	/*
	 * Returns true if a file is a folder and false otherwise.
	 * 
	 * There's currently no reliable check for a File being a folder
	 * in a WebKit browser.
	 */
	private boolean isFirefoxFolder(File file) {
		return ((0 == file.getSize()) && (!(GwtClientHelper.hasString(file.getType()))) && GwtClientHelper.jsIsFirefox());
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
		if (uploadsPending()) {
			// No!  Abort the uploads that are in progress.  We call
			// the uploadNext to clean up the display from the canceled
			// uploads.
			abortUpload();
			uploadNextNow(true);
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
		if (!(uploadsPending())) {
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
		if (!(uploadsPending())) {
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
		if (!(uploadsPending())) {
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
		// If we've got an upload going...
		if (uploadsPending()) {
			// ...process the error for the file.
			handleError(getCurrentFile());
		}
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
		if (!(uploadsPending())) {
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
	 * Called when the reader completes reading a file.
	 * 
	 * Implements the LoadEndHandler.onLoadEnd() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLoadEnd(LoadEndEvent event) {
		// Do we have an upload going?
		if (uploadsPending()) {
			// Yes!  Was this blob successfully uploaded?
			if (null == m_reader.getError()) {
				// Yes!  Process it.
				m_pbPerItem.incrProgress(m_fileBlob.getBlobSize());
				if (m_pbTotal.isVisible()) {
					m_pbTotal.incrProgress(  m_fileBlob.getBlobSize());
				}
				processBlobAsync();
			}
			
			else {
				// No, the blob wasn't successfully uploaded!  The
				// onError() should have handled the error.  Skip to
				// the next file to upload.
				popCurrentFile();
				if (m_pbTotal.isVisible()) {
					m_pbTotal.setProgress(m_pbTotal.getMaxProgress() - getTotalQueueFileSize());
				}
				uploadNextNow(true);
			}
		}
	}

	/*
	 * Removes the current file from the read queue.
	 */
	private void popCurrentFile() {
		// If we have files pending to be uploaded...
		if (uploadsPending()) {
			// ...remove the first entry.
			m_readQueue.remove(0);
		}
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
	
	/*
	 * Asynchronously processes the next blob read from a file.
	 */
	private void processBlobAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				processBlobNow();
			}
		});
	}
	
	/*
	 * Synchronously processes the next blob read from a file.
	 */
	private void processBlobNow() {
		// Extract the data for the blob we just read...
		final boolean lastBlob = ((m_fileBlob.getBlobStart() + m_fileBlob.getBlobSize()) >= m_fileBlob.getFileSize());
		String blobData = m_reader.getStringResult();
		if (m_fileBlob.isBlobBase64Encoded()) {
			blobData = FileUtils.base64encode(blobData);
		}
		m_fileBlob.setBlobData(              blobData );
		m_fileBlob.setBlobMD5Hash(getMD5Hash(blobData));
		
		// ...and trace it, if necessary.
		debugTraceBlob("processBlobNow", true, "Just read");
		
		// Upload the blob.
		final UploadFileBlobCmd cmd = new UploadFileBlobCmd(m_folderInfo, m_fileBlob, lastBlob);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_UploadFileBlob(),
					m_fileBlob.getFileName());
				
				// ...and continue with the next file.
				popCurrentFile();
				uploadNextAsync(true);
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

					// ...and if there are uploads pending (there won't
					// ...be if the use aborted the current upload)...
					if (uploadsPending()) {
						// ...continue with the next file.
						popCurrentFile();
						uploadNextAsync(hasUploadError);
					}
				}
				
				else {
					// No, we aren't done uploading this file!
					m_fileBlob.incrBlobStart(m_fileBlob.getBlobSize());
					File file = getCurrentFile();
					try {
						// Read the next blob.
						Blob readBlob = file.slice(m_fileBlob.getBlobStart(), (m_fileBlob.getBlobStart() + m_fileBlob.getBlobSize()));
						readNextBlobAsync(readBlob);
					}
					
					catch (Throwable t) {
						// Necessary for FF (see bug https://bugzilla.mozilla.org/show_bug.cgi?id=701154.)
						// Standard-complying browsers will to go in this branch.
						handleError(file);
						popCurrentFile();
						uploadNextAsync(true);
					}
				}
			}
		});
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
		
		// Scan the file list...
		m_ignoredAsFolders.clear();
		for (File file:  files) {
			// Is this file actually a folder?
			if (isFirefoxFolder(file)) {
				// Yes!  Add it to the ignore list and skip it.
				m_ignoredAsFolders.add(file);
				continue;
			}
			
			// Add the files that are actually files to the read queue.
			m_readQueue.add(file);
		}
		
		//  If we have any actual files to upload...
		if (uploadsPending()) {
			// ...start upload them...
			m_readTotal = m_readQueue.size();
			m_readThis  = 0;
			validateFileListAsync();
		}
		
		else {
			// ...otherwise, make sure we tell the user about any that
			// ...we ignored.
			handleIgnoredFolders();
		}
	}

	/*
	 * Asynchronously initiates the reading of the given blob.
	 */
	private void readNextBlobAsync(final Blob readBlob) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				readNextBlobNow(readBlob);
			}
		});
	}
	
	/*
	 * Synchronously initiates the reading of the given blob.
	 */
	private void readNextBlobNow(final ReadType readType, final Blob readBlob) {
		switch (readType) {
		case ARRAY_BUFFER:   m_reader.readAsArrayBuffer( readBlob); break;
		case BINARY_STRING:  m_reader.readAsBinaryString(readBlob); break;
		case DATA_URL:       m_reader.readAsDataURL(     readBlob); break;
		case TEXT:           m_reader.readAsText(        readBlob); break;
		}
	}
	
	private void readNextBlobNow(final Blob readBlob) {
		// Always use the initial form of the method.
		readNextBlobNow(DEFAULT_READ_TYPE, readBlob);
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
	 * Does what's necessary with the UI to indicate an upload is active or not.
	 */
	private void showUploadsActive(boolean active) {
		// Are we showing uploads as being active?
		if (active) {
			// Yes!  If we aren't current showing an active upload...
			if (!(m_busyImage.isVisible())) {
				// ...update the text displayed...
				m_browseButton.setText( m_messages.addFilesHtml5PopupAbort()   );
				m_browseButton.setTitle(m_messages.addFilesHtml5PopupAbortAlt());
				
				// ...hide the close button...
				m_closeButton.setVisible(false);
				m_closeX.setVisible(     false);
				
				// ...show the progress panel and if necessary...
				m_busyImage.setVisible(true);
				m_pbPanel.setVisible(  true);
				boolean showTotal = (1 < m_readQueue.size()); 
				if (showTotal) {
					// ...the total progress bar...
					m_pbTotal.setMaxProgress(getTotalQueueFileSize());
					m_pbTotal.setMinProgress(0                      );
					m_pbTotal.setProgress(   0                      );
				}
				m_pbPanel.getRowFormatter().setVisible(1, showTotal);
			}
			
			// ...and show the per item progress bar.
			m_pbPerItem.setMaxProgress(getCurrentFile().getSize());
			m_pbPerItem.setMinProgress(0                         );
			m_pbPerItem.setProgress(   0                         );
		}
		
		else {
			// No, we aren't showing uploads as being active!  They
			// must changing to inactive.  Update the text displayed...
			m_browseButton.setText( m_messages.addFilesHtml5PopupBrowse()   );
			m_browseButton.setTitle(m_messages.addFilesHtml5PopupBrowseAlt());
			m_hintLabel.setText(    m_messages.addFilesHtml5PopupHint()     );
			
			// ..restore the close button...
			m_closeButton.setVisible(true);
			m_closeX.setVisible(     true);
			
			// ...clear out any value in the upload widget so the the
			// ...same file can be reselected, if desired...
			m_uploadButton.getElement().setPropertyString("value", "");

			// ...and hide any necessary widgets.
			m_busyImage.setVisible(false);
			m_pbPanel.setVisible(  false);
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

	/*
	 * Asynchronously uploads the next file.
	 */
	private void uploadNextAsync(final boolean aborted) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				uploadNextNow(aborted);
			}
		});
	}
	
	/*
	 * Synchronously uploads the next file.
	 * 
	 * Called to read and upload the next file in the queue.
	 */
	private void uploadNextNow(boolean aborted) {
		// Are we done uploading files?
		if (!(uploadsPending())) {
			// Yes!  Reset the popup to indicate there are no uploads
			// in progress...
			showUploadsActive(false);

			// ...tell the user about any folders that were ignored...
			if (0 < handleIgnoredFolders()) {
				aborted = true;
			}
			
			// ...and if we're supposed...
			if ((!aborted) && AUTOHIDE_ON_COMPLETE) {
				// ...close the upload popup...
				hide();
			}
			
			// ...and force the folder to refresh.
			GwtTeaming.fireEventAsync(new FullUIReloadEvent());
		}
		
		else {
			// No, we aren't done uploading files!  Reset the popup to
			// indicate that an upload is in progress..
			showUploadsActive(true);

			// Change the hint to reflect the current file...
			File file = getCurrentFile();
			m_hintLabel.setText(m_messages.addFilesHtml5PopupBusy(file.getName(), ++m_readThis, m_readTotal));
			try {
				// ...and upload it by blobs.
				m_fileBlob = new FileBlob(file.getName(), getFileUTC(file), getFileUTCMS(file), file.getSize(), new Date().getTime());
				readNextBlobNow(file.slice(m_fileBlob.getBlobStart(), m_fileBlob.getBlobSize()));
			}
			
			catch (Throwable t) {
				// Necessary for FF (see bug https://bugzilla.mozilla.org/show_bug.cgi?id=701154.)
				// Standard-complying browsers will to go in this branch.
				handleError(file);
				popCurrentFile();
				uploadNextAsync(true);
			}
		}
	}
	
	/*
	 * Returns true if there are files pending uploading and false
	 * otherwise.
	 */
	private boolean uploadsPending() {
		return (!(m_readQueue.isEmpty()));
	}

	/*
	 * Asynchronously validates the file list to be uploaded and starts
	 * the upload if everything is valid.
	 */
	private void validateFileListAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				validateFileListNow();
			}
		});
	}
	
	/*
	 * Synchronously validates the file list to be uploaded and starts
	 * the upload if everything is valid.
	 */
	private void validateFileListNow() {
		// Create a command to validate what's to be uploaded...
		final ValidateUploadsCmd cmd = new ValidateUploadsCmd(m_folderInfo);
		for (File file:  m_readQueue) {
			cmd.addFile(file.getName(), file.getSize());
		}
		
		// ...and check if things are valid.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ValidateUploads());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Did we get any messages back from the validation?
				ValidateUploadsRpcResponseData	responseData = ((ValidateUploadsRpcResponseData) result.getResponseData());
				List<ErrorInfo>					errors       = responseData.getErrorList();
				int								errorCount   = responseData.getErrorCount();
				int								totalCount   = responseData.getTotalMessageCount();
				if (0 < totalCount) {
					// Yes!  Display them to the user...
					GwtClientHelper.displayMultipleErrors(m_messages.addFilesHtml5PopupUploadValidationError(), errors);

					// ...and if there were any errors (vs. just
					// ...warnings)...
					if (0 < errorCount) {
						// ...abort the uploads that were requested.
						// ...We call uploadNext to clean up the
						// ...display from the failed uploads.
						abortUpload();
						uploadNextNow(true);
					}
				}

				// Did we get any errors (warnings are fine)?
				if (0 == errorCount) {
					// No!  We're any of the files duplicates that we
					// need to get confirmation from the user about?
					final List<UploadInfo>	duplicates     = responseData.getDuplicateList();
					final int					duplicateCount = responseData.getDuplicateCount();
					if (0 < duplicateCount) {
						// Yes!  Does the user want to overwrite the duplicates?
						FileConflictsDlg.createAsync(new FileConflictsDlgClient() {
							@Override
							public void onUnavailable() {
								// Nothing to do.  Error handled in
								// asynchronous provider.
							}
							
							@Override
							public void onSuccess(FileConflictsDlg cDlg) {
								FileConflictsDlg.initAndShow(
									cDlg,
									new ConfirmCallback() {
										@Override
										public void dialogReady() {
											// Ignored.  We don't
											// really care when the
											// dialog is ready.
										}

										@Override
										public void accepted() {
											// The user has accepted
											// the duplicates.  We can
											// start the override.
											uploadNextAsync(false);
										}

										@Override
										public void rejected() {
											// No, they're not sure!
											// Abort the uploads that
											// were requested.  We call
											// uploadNext to clean up
											// the display from the
											// failed uploads.
											abortUpload();
											uploadNextNow(true);
										}
									},
									m_folderInfo,
									duplicates);
							}
						});
					}
					
					else {
						// No, there were no duplicates!  We can start
						// the upload.
						uploadNextAsync(false);
					}
				}
			}
		});
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
