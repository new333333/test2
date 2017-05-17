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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.folderdata.FileBlob;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FileBlob.ReadType;
import org.kablink.teaming.gwt.client.datatable.FileConflictsDlg;
import org.kablink.teaming.gwt.client.datatable.FileConflictsDlg.FileConflictsDlgClient;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.AbortFileUploadCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetHtml5SpecsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.Html5SpecsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UploadFileBlobCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateUploadsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateUploadsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.UploadInfo;
import org.kablink.teaming.gwt.client.widgets.AlertDlg.AlertDlgCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;

import org.vectomatic.file.Blob;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
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
import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Routines to upload files using HTML5 APIs.
 *  
 * @author drfoster@novell.com
 */
public class Html5UploadHelper
	implements
		ErrorHandler,
		LoadEndHandler
{
	private BinderInfo					m_folderInfo;		// The folder the HTML5 helper is uploading into.
	private byte[]						m_blobCache;		// A byte[] used to cache blobs as they're read and sent to the server.
	private double						m_totalFileSize;	// Total size of everything to be uploaded.
	private FileBlob					m_fileBlob;			// Contains information about blobs of a file as they're read and uploaded.
	private FileReader					m_reader;			// Used to reads files as they're being uploaded.
	private GwtTeamingMessages			m_messages;			// Access to our localized messages.
	private Html5SpecsRpcResponseData	m_html5Specs;		// Holds the specifications about how to perform HTML5 file uploads.
	private Html5UploadCallback			m_callback;			// Callback interface to keep the caller informed about what's going on.
	private Html5UploadState			m_uploadState;		// Tracks the current state of uploading.
	private int							m_readThis;			// The file number out of the total that is currently being uploaded.
	private int							m_readTotal;		// Total number of files to upload during an upload request.
	private List<File>					m_ignoredAsFolders;	// Tracks files that are dropped because they 'appear' to be folders and are hence, ignored.
	private List<File>					m_readQueue;		// List of files queued for uploading.
	private VibeEventBase<?>			m_completeEvent;	// Event to fire on completion of an upload.

	// The following is used to convert an Int8Array to a base64
	// encoded string in the method toBase64().
	private static final char[] BASE64_CHARS = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
		'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
		'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
		'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', '+', '/'
	};
	private static final char BASE64_PADDING = '=';
	
	// true -> Information about file blobs being uploaded is displayed
	// via alerts.  false -> They're not.
	private static boolean TRACE_BLOBS	= false;	//
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private Html5UploadHelper(Html5UploadCallback callback) {
		// Initialize the superclass...
		super();
		
		// ...store the parameter...
		m_callback = callback;
		
		// ...initialize the other data members...
		m_messages = GwtTeaming.getMessages();

		m_uploadState      = Html5UploadState.INACTIVE;
		m_ignoredAsFolders = new ArrayList<File>();
		m_readQueue        = new ArrayList<File>();
		
		m_reader = new FileReader();
		m_reader.addLoadEndHandler(this);
		m_reader.addErrorHandler(  this);

		// ...and load what we need from the server.
		loadPart1Async();
	}

	/*
	 * Asynchronously aborts an upload. 
	 */
	private static void abortUploadAsync(final Html5UploadHelper abortHelper) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				abortHelper.abortUploadNow();
			}
		});
	}
	
	/*
	 * Synchronously aborts an upload.
	 *  
	 * Does what's necessary to abort a file upload sequence.
	 */
	private void abortUploadNow() {
		// If the reader's loading...
		if (State.LOADING.equals(m_reader.getReadyState())) {
			// ...abort it...
			m_reader.abort();
		}
		
		// ...empty the read queue...
		m_readQueue.clear();

		// ...and if we have an active FileBlob...
		if (null != m_fileBlob) {
			// ...tell the server that we've aborted the upload so
			// ...it can clean up anything it has hanging around...
			final AbortFileUploadCmd cmd = new AbortFileUploadCmd(m_folderInfo, m_fileBlob);
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
		
		// ...and call uploadNextFile to clean up from the abort.
		uploadNextFileAsync(true);
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
	 * Continues processing pending uploads.
	 */
	private void continueUploads(boolean hasUploadError) {
		// If there are uploads pending (there won't be if the user
		// aborted the current upload)...
		if (uploadsPending()) {
			// ...continue with the next file.
			popCurrentFile();
			uploadNextFileAsync(hasUploadError);
		}
	}
	
	/*
	 * If we're in debug UI mode, displays an alert about a file blob.
	 */
	private void debugTraceBlob(String methodName, boolean traceQueueSize, String traceHead, String traceTail) {
		if (TRACE_BLOBS && GwtClientHelper.isDebugUI()) {
			String blobHash = m_fileBlob.getBlobMD5Hash();
			if (null == blobHash) {
				blobHash = "***";
			}
			String	dump  = (traceHead + ":  '" + m_fileBlob.getFileName() + "' (fSize:" + m_fileBlob.getFileSize() + ", bStart:" + m_fileBlob.getBlobStart() + ", bSize:" + m_fileBlob.getBlobSize() + ", md5Hash:" + blobHash + ")");
			if (traceQueueSize) {
				int		files = (m_readQueue.size() - 1);
				switch (files) {
				case 0:   dump += " there are no files pending";             break;
				case 1:   dump += " there is 1 file pending";                break;
				default:  dump += " there are "  + files + " files pending"; break;
				}
			}
			boolean hasTail = GwtClientHelper.hasString(traceTail);
			dump = ("Html5UploadHelper." + methodName + "( " + dump + " )" + (hasTail ? ":  " + traceTail : ""));
			int dataLen;
			if (m_fileBlob.getReadType().isArrayBuffer()) {
				byte[] data = m_fileBlob.getBlobDataBytes(); 
				dataLen =  ((null == data )? 0 : data.length);
			}
			else {
				String data = m_fileBlob.getBlobDataString();
				dataLen = ((null == data) ? 0 : data.length());
			}
			dump += ("\n\nData Read:  " + dataLen + (m_fileBlob.isBlobBase64() ? " base64 encoded" : "") + " bytes."); 
			GwtClientHelper.debugAlert(dump);
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
	 * Defines the type of HTML5 file read used to upload a file.
	 * 
	 * Note:  IE10 (the only version of IE that supports uploading
	 *    files using HTML5) doesn't support BINARY_STRING.  Setting
	 *    this to that will break uploading files using this helper
	 *    there.
	 */
	private ReadType getInitialReadType() {
		ReadType reply;
		if (m_html5Specs.isMd5HashValidate())
		     reply = ReadType.ARRAY_BUFFER;	// When we have to MD5 validate, ARRAY_BUFFER
		else reply = ReadType.DATA_URL;		// ...faster.  When we don't DATA_URL is faster.
		return reply;
	}

	/**
	 * Extracts the File's from a FileList and returns them in a
	 * List<File>.
	 * 
	 * @param fileList
	 * 
	 * @return
	 */
	public static List<File> getListFromFileList(FileList fileList) {
		List<File> reply = new ArrayList<File>();
		if ((null != fileList) && (0 < fileList.getLength())) {
			for (File file:  fileList) {
				reply.add(file);
			}
		}
		return reply;
	}
	
	/*
	 * Returns the MD5 hash key for the given string.
	 */
	private String getMd5Hash(byte[] bytes) {
		MD5Digest	sd = new MD5Digest();
		sd.update(bytes, 0, bytes.length);
		byte[] result = new byte[sd.getDigestSize()];
		sd.doFinal(result, 0);
		return byteArrayToHexString(result);
	}
	
	private String getMd5Hash(String string) {
		// Always use the initial form of the method.
		return getMd5Hash(string.getBytes());
	}

	/**
	 * Returns the total number of items being uploaded.
	 * 
	 * @return
	 */
	public int getReadTotal() {
		return m_readTotal;
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
	private void handleError(final File file) {
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
						// ...treat this as a request to uploaded
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
		
		// If we get here, we need to tell the caller about the error.
		m_callback.readError(file.getName(), errorDesc);
	}

	/*
	 * Tells the caller if if any files were ignored as folders.
	 */
	private int handleIgnoredFolders() {
		// Are we tracking any ignored folders?
		int reply = 0;
		final int count = m_ignoredAsFolders.size();
		if (0 < count) {
			// Yes!  Tell the caller...
			final StringBuffer folderNames = new StringBuffer("");
			for (File ignoredFolder:  m_ignoredAsFolders) {
				if (0 < reply) {
					folderNames.append(", ");
				}
				reply += 1;
				
				folderNames.append("'");
				folderNames.append(ignoredFolder.getName());
				folderNames.append("'");
			}
			m_callback.foldersSkipped(count, folderNames.toString());

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
	
	/*
	 * Asynchronously loads the HTML5 upload specifications.
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
	 * Synchronously loads the HTML5 upload specifications.
	 */
	private void loadPart1Now() {
		GwtClientHelper.executeCommand(
				new GetHtml5SpecsCmd(),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetHtml5Specs());
				
				m_callback.onUnavailable();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the HTML5 upload specifications and tell the
				// caller we're ready to go.
				m_html5Specs = ((Html5SpecsRpcResponseData) response.getResponseData());
				m_callback.onSuccess(Html5UploadHelper.this);
			}
		});
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
			popCurrentFile();
			uploadNextFileAsync(true);
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
				m_callback.incrProgress(m_fileBlob.getBlobSize());
				processBlobAsync();
			}
			
			else {
				// No, the blob wasn't successfully uploaded!  The
				// onError() should have handled the error.  Skip to
				// the next file to upload.
				popCurrentFile();
				m_callback.setTotalCurrentProgress(m_totalFileSize - getTotalQueueFileSize());
				uploadNextFileNow(true);
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
		byte[] blobDataBytes;
		String blobDataString;
		String blobMd5Hash;
		boolean isMd5HashValidate = m_html5Specs.isMd5HashValidate();
		ReadType readType = m_fileBlob.getReadType();
		if (readType.isArrayBuffer()) {
			blobDataString = null;
			ArrayBuffer buffer = m_reader.getArrayBufferResult();
			Int8Array   array  = Int8ArrayNative.create(buffer);
			if (m_fileBlob.isBlobBase64())
			     blobDataBytes = toBase64(array).getBytes();
			else blobDataBytes = toBytes( array);
			blobMd5Hash = (isMd5HashValidate ? getMd5Hash(blobDataBytes) : null);
		}
		else {
			blobDataBytes  = null;
			blobDataString = m_reader.getStringResult();
			if (readType.isDataUrl()) {
				m_fileBlob.setBlobBase64(true);
				if (isMd5HashValidate) {
					blobDataString = FileBlob.fixDataUrlString(blobDataString);
				}
			}
			else if (m_fileBlob.isBlobBase64()) {
				blobDataString = FileUtils.base64encode(blobDataString);
			}
			blobMd5Hash = (isMd5HashValidate ? getMd5Hash(blobDataString) : null);
		}
		m_fileBlob.setBlobDataBytes( blobDataBytes );
		m_fileBlob.setBlobDataString(blobDataString);
		m_fileBlob.setBlobMD5Hash(   blobMd5Hash   );
		
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
				uploadNextFileAsync(true);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Are we done uploading this file?
				String			uploadError    = ((StringRpcResponseData) result.getResponseData()).getStringValue();
				final boolean	hasUploadError = GwtClientHelper.hasString(uploadError); 
				if (lastBlob || hasUploadError) {
					// Yes!  If we stopped because of an error...
					if (hasUploadError) {
						// ...display the error...
						GwtClientHelper.deferredAlert(uploadError, new AlertDlgCallback() {
							@Override
							public void closed() {
								// ...and continue the uploads once the
								// ...user has closed the error.
								continueUploads(hasUploadError);
							}
						});
					}
					
					else {
						// ...otherwise, simply continue the uploads.
						continueUploads(hasUploadError);
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
						uploadNextFileAsync(true);
					}
				}
			}
		});
	}
	
	/*
	 * Asynchronously uploads the given files.
	 */
	private void processFilesAsync(final List<File> files) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				processFilesNow(files);
			}
		});
	}
	
	/*
	 * Synchronously uploads the given files.
	 */
	private void processFilesNow(List<File> files) {
		// If we're currently performing  an upload...
		if (!(m_uploadState.isInactive())) {
			// ...we can't be asked to perform another.
			GwtClientHelper.deferredAlert(m_messages.html5Uploader_InternalError_UploaderAlreadyActive());
			return;
		}
		
		// We start an upload in an inactive state.
		setUploadStateImpl(Html5UploadState.INACTIVE);
		
		// If we weren't given any files to process...
		if (!(GwtClientHelper.hasItems(files))) {
			// ...bail.
			return;
		}
		int fileCount = files.size();
		
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
			// ...otherwise, make sure we tell the caller about any
			// ...that we ignored...
			int ignored = handleIgnoredFolders();
			if (ignored == fileCount) {
				// ...and about the upload if we ignored them all.
				abortUploadAsync(this);
			}
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
	private void readNextBlobNow(final Blob readBlob) {
		switch (m_fileBlob.getReadType()) {
		case ARRAY_BUFFER:   m_reader.readAsArrayBuffer( readBlob); break;
		case BINARY_STRING:  m_reader.readAsBinaryString(readBlob); break;
		case DATA_URL:       m_reader.readAsDataURL(     readBlob); break;
		case TEXT:           m_reader.readAsText(        readBlob); break;
		}
	}
	
	/*
	 * Does what's necessary with the UI to indicate the current upload
	 * state.
	 */
	private void setUploadStateImpl(final Html5UploadState newState) {
		switch (newState) {
		case UPLOADING:
			// We're uploading a file!  If we aren't currently showing
			// an active upload...
			if (!(m_uploadState.equals(Html5UploadState.UPLOADING))) {
				m_totalFileSize = getTotalQueueFileSize();
				m_callback.setTotalMaxProgress(m_totalFileSize);
			}
			
			// ...show the per item progress bar...
			m_callback.setPerItemProgress(0, getCurrentFile().getSize());
			break;
			
		case INACTIVE:    break;
		case VALIDATING:  break;
		}

		// If the state is changing...
		if (!(newState.equals(m_uploadState))) {
			// ...tell the caller about it.
			m_callback.setUploadState(m_uploadState, newState);
			m_uploadState = newState;
		}
	}
	
	/*
	 * Manually converts an Int8Array to a base64 encoded string.
	 */
	private static String toBase64(Int8Array array) {
		StringBuilder builder = new StringBuilder();
		int length = array.length();
		if (0 < length) {
			char[] charArray = new char[4];
			int ix = 0;
			while (3 <= length) {
				int i = ((array.get(ix)   & 0xff) << 16)
				      + ((array.get(ix+1) & 0xff) << 8)
				      +  (array.get(ix+2) & 0xff);
				charArray[0] = BASE64_CHARS[ i >> 18];
				charArray[1] = BASE64_CHARS[(i >> 12) & 0x3f];
				charArray[2] = BASE64_CHARS[(i >>  6) & 0x3f];
				charArray[3] = BASE64_CHARS[ i & 0x3f];
				builder.append(charArray);
				ix     += 3;
				length -= 3;
			}
			if (1 == length) {
				int i = array.get(ix)&0xff;
				charArray[0] = BASE64_CHARS[ i >> 2];
				charArray[1] = BASE64_CHARS[(i << 4) & 0x3f];
				charArray[2] = BASE64_PADDING;
				charArray[3] = BASE64_PADDING;
				builder.append(charArray);
			}
			else if (2 == length) {
				int i = ((array.get(ix)     & 0xff) << 8)
				       + (array.get(ix + 1) & 0xff);
				charArray[0] = BASE64_CHARS[ i >> 10];
				charArray[1] = BASE64_CHARS[(i >>  4) & 0x3f];
				charArray[2] = BASE64_CHARS[(i <<  2) & 0x3f];
				charArray[3] = BASE64_PADDING;
				builder.append(charArray);
			}
		}
		return builder.toString();
	 }
	
	/*
	 * Manually converts an Int8Array to a byte[].
	 */
	private byte[] toBytes(Int8Array array) {
		int al = array.length();
		byte[] reply;
		if ((null != m_blobCache) && (m_blobCache.length == al))
		     reply = m_blobCache;
		else reply = new byte[al];
		for (int i = 0; i < al; i += 1) {
			reply[i] = array.get(i);
		}
		return reply;
	}
	
	/*
	 * Asynchronously uploads the given files.
	 */
	private static void uploadFilesAsync(final Html5UploadHelper uploadHelper, final BinderInfo fi, final List<File> files, final VibeEventBase<?> completeEvent) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				uploadHelper.uploadFilesNow(fi, files, completeEvent);
			}
		});
	}
	
	/*
	 * Synchronously uploads the given files.
	 */
	private void uploadFilesNow(BinderInfo fi, List<File> files, VibeEventBase<?> completeEvent) {
		// Store the parameters and start processing the files.
		m_folderInfo    = fi;
		m_completeEvent = completeEvent;
		processFilesAsync(files);
	}

	/*
	 * Asynchronously uploads the next file.
	 */
	private void uploadNextFileAsync(final boolean aborted) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				uploadNextFileNow(aborted);
			}
		});
	}
	
	/*
	 * Synchronously uploads the next file.
	 * 
	 * Called to read and upload the next file in the queue.
	 */
	private void uploadNextFileNow(boolean aborted) {
		// Are we done uploading files?
		if (!(uploadsPending())) {
			// Yes!  Reset the upload to indicate there are no uploads
			// in progress...
			setUploadStateImpl(Html5UploadState.INACTIVE);

			// ...and tell the caller the uploads have completed.
			if (0 < handleIgnoredFolders()) {
				aborted = true;
			}
			m_callback.uploadComplete(aborted, m_completeEvent);
		}
		
		else {
			// No, we aren't done uploading files!  Reset the upload to
			// indicate that an upload is in progress..
			setUploadStateImpl(Html5UploadState.UPLOADING);

			// Tell the caller we're on to the next file...
			final File file = getCurrentFile();
			m_callback.uploadingNextFile(file.getName(), ++m_readThis, m_readTotal);
			try {
				// ...determine the appropriate blob size...
				long fileSize = file.getSize();
				long blobSize;
				if (m_html5Specs.isFixed()) {
					blobSize = m_html5Specs.getFixedBlobSize();
				}
				else {
					blobSize = (fileSize / m_html5Specs.getVariableBlobsPerFile());
					blobSize = Math.max(blobSize, m_html5Specs.getVariableMinBlobSize());
					if (0 < m_html5Specs.getVariableMaxBlobSize()) {
						blobSize = Math.min(blobSize, m_html5Specs.getVariableMaxBlobSize());
					}
					if (fileSize < blobSize) {
						blobSize = fileSize;	// Never use a blob size that's bigger than the file.
					}
				}
				if (TRACE_BLOBS) {
					GwtClientHelper.debugAlert("BlobSize: " + blobSize + ", Encoded: " + m_html5Specs.isEncode());
				}
				
				// ...add a blob cache used to hold the bytes read from
				// ...an ArrayBuffer...
				m_blobCache = new byte[(int) blobSize];
				
				// ...and upload it by blobs.
				m_fileBlob = new FileBlob(
					getInitialReadType(),
					file.getName(),
					getFileUTC(  file),
					getFileUTCMS(file),
					fileSize,
					new Date().getTime(),
					m_html5Specs.isEncode(),
					blobSize);
				
				readNextBlobNow(
					file.slice(
						m_fileBlob.getBlobStart(),
						m_fileBlob.getBlobSize()));
			}
			
			catch (Throwable t) {
				// Necessary for FF (see bug https://bugzilla.mozilla.org/show_bug.cgi?id=701154.)
				// Standard-complying browsers will to go in this branch.
				handleError(file);
				popCurrentFile();
				uploadNextFileAsync(true);
			}
		}
	}
	
	/**
	 * Returns true if there are files pending uploading and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean uploadsPending() {
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
		
		// Change the display to reflect that the files to be uploaded
		// are being verified on the server.
		setUploadStateImpl(Html5UploadState.VALIDATING);
		
		// ...and check if things are valid.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ValidateUploads());
				setUploadStateImpl(Html5UploadState.INACTIVE);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Did we get any messages back from the validation?
				ValidateUploadsRpcResponseData	responseData = ((ValidateUploadsRpcResponseData) result.getResponseData());
				final List<ErrorInfo>			errors       = responseData.getErrorList();
				int								errorCount   = responseData.getErrorCount();
				int								totalCount   = responseData.getTotalMessageCount();
				if (0 < totalCount) {
					// Yes!  Tell the caller...
					m_callback.validationErrors(errors);

					// ...and if there were any errors (vs. just
					// ...warnings)...
					if (0 < errorCount) {
						// ...abort the uploads that were requested.
						abortUploadNow();
					}
				}

				// Did we get any errors (warnings are fine)?
				if (0 == errorCount) {
					// No!  Were any of the files duplicates that we
					// need to get confirmation from the user about?
					final List<UploadInfo>	duplicates     = responseData.getDuplicateList();
					final int				duplicateCount = responseData.getDuplicateCount();
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
											uploadNextFileAsync(false);
										}

										@Override
										public void rejected() {
											// No, they're not sure!
											// Abort the uploads that
											// were requested.
											abortUploadNow();
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
						uploadNextFileAsync(false);
					}
				}
			}
		});
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the HTML5 helper and perform some operation on it.            */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/*
	 * Asynchronously loads the Html5UploadHelper and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final Html5UploadCallback callback,
			
			// abortUpload() parameters,
			final Html5UploadHelper	abortHelper,
			
			// uploadFiles() parameters,
			final Html5UploadHelper	uploadHelper,
			final BinderInfo		fi,
			final List<File>		files,
			final VibeEventBase<?>	completeEvent) {
		GWT.runAsync(Html5UploadHelper.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_Html5UploadHelper());
				if (null != callback) {
					callback.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a helper?
				if (null != callback) {
					// Yes!  Does the browser support HTML5?
					if (!(GwtClientHelper.jsBrowserSupportsHtml5FileAPIs())) {
						// No!  Tell the user about the problem and
						// return an unavailable status.
						GwtClientHelper.deferredAlert(GwtTeaming.getMessages().html5Uploader_InternalError_NoBrowserSupport());
						callback.onUnavailable();
					}
					
					else {
						// Yes!  Create a helper and return it via the
						// callback.
						@SuppressWarnings("unused")
						Html5UploadHelper uploadHelper = new Html5UploadHelper(callback);
					}
				}
				
				// No, it's not a request to create a helper!  Is it a
				// request to abort an upload?
				else if (null != abortHelper) {
					// Yes!  Perform the abort.
					abortUploadAsync(abortHelper);
				}
				
				else {
					// No, it's not a request to abort an upload
					// either!  It must be a request to run an existing
					// helper.  Run it.
					uploadFilesAsync(uploadHelper, fi, files, completeEvent);
				}
			}
		});
	}

	/**
	 * Aborts an upload that's in progress.
	 * 
	 * @param abortHelper
	 */
	public static void abortUpload(Html5UploadHelper abortHelper) {
		// Perform the appropriate asynchronous operation.
		doAsyncOperation(null, abortHelper, null, null, null, null);
	}
	
	/**
	 * Loads the Html5UploadHelper split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param callback
	 */
	public static void createAsync(Html5UploadCallback callback) {
		// Perform the appropriate asynchronous operation.
		doAsyncOperation(callback, null, null, null, null, null);
	}
	
	/**
	 * Uploads the given files into the specified binder.
	 * 
	 * @param uploadHelper
	 * @param fi
	 * @param files
	 */
	public static void uploadFiles(Html5UploadHelper uploadHelper, BinderInfo fi, List<File> files, VibeEventBase<?> completeEvent) {
		doAsyncOperation(null, null, uploadHelper, fi, files, completeEvent);
	}
	
	public static void uploadFiles(Html5UploadHelper uploadHelper, BinderInfo fi, FileList files, VibeEventBase<?> completeEvent) {
		// Always use the initial form of the method.
		uploadFiles(uploadHelper, fi, getListFromFileList(files), completeEvent);
	}
}
