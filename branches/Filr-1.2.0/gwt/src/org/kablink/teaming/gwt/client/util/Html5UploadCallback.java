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
package org.kablink.teaming.gwt.client.util;

import java.util.List;

import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;

/**
 * Callback interface to interact with the HTML5 helper asynchronously
 * after it loads. 
 *  
 * @author drfoster@novell.com
 */
public interface Html5UploadCallback {
	/**
	 * Tells the caller that some folders were in the files
	 * requested to be uploaded and were skipped.
	 * 
	 * @param count
	 * @param folderNames
	 */
	public void foldersSkipped(int count, String folderNames);
	
	/**
	 * Tells the caller to advance it's progress indicator.
	 * 
	 * @param amount
	 */
	public void incrProgress(long amount);
	
	/**
	 * Tells the caller that the helper was successfully loaded and
	 * is available for uploading files.
	 * 
	 * @param uploadHelper
	 */
	public void onSuccess(Html5UploadHelper uploadHelper);
	
	/**
	 * Tell the caller if the helper fails to load for any reason.
	 * 
	 * Note that the user will have been told about the failure.
	 */
	public void onUnavailable();

	/**
	 * Tells the caller that an error occurred uploading a file.
	 * 
	 * @param fileName
	 * @param errorDescription
	 */
	public void readError(String fileName, String errorDescription);

	/**
	 * Tells the caller to set the current files progress indicator.
	 * 
	 * @param min
	 * @param max
	 */
	public void setPerItemProgress(long min, long max);
	
	/**
	 * Tells the caller to update the total progress to the
	 * specified value.
	 * 
	 * @param amount
	 */
	public void setTotalCurrentProgress(double amount);
	
	/**
	 * Tells the caller to set it's total maximum progress value. 
	 * 
	 * @param max
	 */
	public void setTotalMaxProgress(double max);
	
	/**
	 * Tells the caller what the current state of the upload is.
	 * 
	 * @param previousState
	 * @param currentState
	 */
	public void setUploadState(Html5UploadState previousState, Html5UploadState newState);
	
	/**
	 * Tells the caller that the upload has completed.
	 * 
	 * @param aborted
	 * @param completeEvent
	 */
	public void uploadComplete(boolean aborted, VibeEventBase<?> completeEvent);

	/**
	 * Tells the caller that we're now uploading the next file.
	 * 
	 * @param fileName
	 * @param thisFile
	 * @param totalFiles
	 */
	public void uploadingNextFile(String fileName, int thisFile, int totalFiles);
	
	/**
	 * Tells the caller about errors that occurred while validating
	 * the upload request.
	 * 
	 * @param errors
	 */
	public void validationErrors(List<ErrorInfo> errors);
}
