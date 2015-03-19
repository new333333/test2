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
package org.kablink.teaming.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SPropsUtil;

/**
 * Helper class dealing with the HTML5 file uploader.
 * 
 * @author drfoster@novell.com
 */
public class Html5Helper {
	protected static Log m_logger = LogFactory.getLog(Html5Helper.class);

	// Base filename for the temporary file used to upload files
	// using the HTML5 file uploader.
	public static final String UPLOAD_FILE_PREFIX	= "uploadFile.";
	
	private static final boolean HTML5_UPLOAD_BLOBS_VARIABLE               = SPropsUtil.getString( "html5.upload.blob.mode",                    "variable").equalsIgnoreCase("variable");
	private static final boolean HTML5_UPLOAD_ENCODE                       = SPropsUtil.getBoolean("html5.upload.encode",                       false     );
	private static final boolean HTML5_UPLOAD_MD5_HASH_VALIDATE            = SPropsUtil.getBoolean("html5.upload.md5.hash.validate",            false     );
	private static final long    HTML5_UPLOAD_FIXED_BLOB_SIZE              = SPropsUtil.getLong(   "html5.upload.fixed.blob.size",              16384l    );
	private static final int     HTML5_UPLOAD_VARIABLE_BLOBS_PER_FILE      = SPropsUtil.getInt(    "html5.upload.variable.blobs.per.file",      25        );
	private static final long    HTML5_UPLOAD_VARIABLE_BLOBS_MIN_BLOB_SIZE = SPropsUtil.getLong(   "html5.upload.variable.blobs.min.blob.size", 16384l    );
	private static final long    HTML5_UPLOAD_VARIABLE_BLOBS_MAX_BLOB_SIZE = SPropsUtil.getLong(   "html5.upload.variable.blobs.max.blob.size", 0l        );

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private Html5Helper() {
		// Nothing to do.
	}

	/**
	 * Returns true if blobs are base64 encode for transfer to the
	 * server and false otherwise.
	 * 
	 * @return 
	 */
	public static boolean isHtml5UploadEncode() {
		return HTML5_UPLOAD_ENCODE;
	}
	
	/**
	 * Returns true if blobs are MD5 hash validated during a transfer
	 * to the server and false otherwise.
	 * 
	 * @return 
	 */
	public static boolean isHtml5UploadMd5HashValidate() {
		return HTML5_UPLOAD_MD5_HASH_VALIDATE;
	}
	
	/**
	 * Returns true if we uploading using a variable blob sized or
	 * false if we uploading a fixed blob size.
	 * 
	 * @return
	 */
	public static boolean isHtml5UploadVariableBlobs() {
		return HTML5_UPLOAD_BLOBS_VARIABLE;
	}
	
	/**
	 * If we're uploading used a fixed blob size, returns the fixed
	 * size to use.  Otherwise, returns (-1);
	 * 
	 * For values less than 1024, 1024 will be used.
	 * 
	 * @return
	 */
	public static long getHtml5FixedBlobSize() {
		long reply;
		if (isHtml5UploadVariableBlobs())
		     reply = (-1);
		else reply = Math.max(1024l, HTML5_UPLOAD_FIXED_BLOB_SIZE);
		return reply;
	}

	/**
	 * If we're uploading using a variable blob size, returns the
	 * number of blobs per file.  Otherwise, returns (-1).
	 *
	 * The file size is divided by this to arrive at the blob size.
	 * For values less than 1, 1 will be used.
	 * 
	 * @return
	 */
	public static int getHtml5VariableBlobsPerFile() {
		int reply;
		if (isHtml5UploadVariableBlobs())
		     reply = Math.max(1, HTML5_UPLOAD_VARIABLE_BLOBS_PER_FILE);
		else reply = (-1);
		return reply;
	}

	/**
	 * If we're uploading using a variable blob size, returns the
	 * minimum blob size that can be used.  Otherwise, returns (-1).
	 * 
	 * For values less than 1024, 1024 will be used.
	 * 
	 * @return
	 */
	public static long getHtml5VariableBlobsMinBlobSize() {
		long reply;
		if (isHtml5UploadVariableBlobs())
		     reply = Math.max(1024l, HTML5_UPLOAD_VARIABLE_BLOBS_MIN_BLOB_SIZE);
		else reply = (-1);
		return reply;
	}
	
	/**
	 * If we're uploading using a variable blob size, returns the
	 * maximum blob size that can be used.  Otherwise, returns (-1).
	 * 
	 * A value of 0 implies no maximum (i.e., the maximum is
	 * unbounded.)  Otherwise, for values less than the minimum, the
	 * minimum will be used.  For values greater than Integer.MAXINT,
     * Integer.MAXINT will be used.
	 * 
	 * @return
	 */
	public static long getHtml5VariableBlobsMaxBlobSize() {
		long reply;
		if (isHtml5UploadVariableBlobs()) {
			if (0 == HTML5_UPLOAD_VARIABLE_BLOBS_MAX_BLOB_SIZE)
			     reply = HTML5_UPLOAD_VARIABLE_BLOBS_MAX_BLOB_SIZE;
			else reply = Math.max(getHtml5VariableBlobsMinBlobSize(), HTML5_UPLOAD_VARIABLE_BLOBS_MAX_BLOB_SIZE);
			reply = Math.min(reply, Integer.MAX_VALUE);
		}
		else {
			reply = (-1);
		}
		return reply;
	}
}
