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
package org.kablink.teaming.web.upload;

import org.apache.commons.fileupload.ProgressListener;
import org.apache.log4j.Logger;

import org.kablink.teaming.util.SPropsUtil;

/**
 * ?
 * 
 * @author ?
 */
public class FileUploadProgressListener implements ProgressListener {
	Logger logger = Logger.getLogger(FileUploadProgressListener.class);

	private long bytesRead = 0;

	private long contentLength = 0;
	
	private float uploadSpeed = 0;
	
	private int items = 0;
	
	private long uploadStartTime = 0;

	private boolean multipartFinished = false;
	
	private long lastUpdateTime = 0;
	
	private long lastUpdateBytesRead = 0;
	
	private int timeLeft = 0;
	
	private long updatesPerTrace;
	private long updateCount;

	public FileUploadProgressListener() {
		this.uploadStartTime = System.currentTimeMillis();
		this.lastUpdateTime = System.currentTimeMillis();

		if (logger.isDebugEnabled()) {
			this.updatesPerTrace = SPropsUtil.getLong("upload.multipart.updates.per.trace", 1L);
			if (1 > updatesPerTrace) {
				updatesPerTrace = 1L;
			}
			this.updateCount = 0L;
		}
	}
	
	public boolean isFinished() {
		return multipartFinished;
	}

	public int getPercentDone() {
		if (contentLength == -1) {
			// -1 if content lenght is unknown
			// ensure we never reach 100% but show progress
			return (int) Math.abs(bytesRead * 100.0 / (bytesRead + 10000));
		}
		return (int) Math.abs(bytesRead * 100.0 / contentLength);
	}

	@Override
	public void update(long bytesRead, long contentLength, int items) {
		if ((this.lastUpdateTime + 1200) < System.currentTimeMillis()) {
			float b =(bytesRead - this.lastUpdateBytesRead);
			float kB = b / 1024;
			long t = System.currentTimeMillis() - this.lastUpdateTime;
			long s = t / 1000;
			
			this.uploadSpeed = (this.uploadSpeed + kB / s) / 2;
			this.lastUpdateTime = System.currentTimeMillis();
			
			this.timeLeft = (int)((contentLength - bytesRead) / 1024 / this.uploadSpeed);
			this.lastUpdateBytesRead = bytesRead;
		}
		this.bytesRead = bytesRead;
		this.contentLength = contentLength;
		this.items = items;
		if (this.bytesRead == this.contentLength) {
			this.multipartFinished = true;
		}
		
		if (logger.isDebugEnabled()) {
			if (0L == ((this.updateCount++) % this.updatesPerTrace)) {
				logger.debug("update():");
				if ((-1) != contentLength) {
					logger.debug("...contentLength:  "               + this.contentLength);
					logger.debug("...percentDone:  "                 + getPercentDone());
					logger.debug("...multipartFinished:  "           + this.multipartFinished);
				}
				logger.debug("...update()'s:  "                      + this.updateCount);
				logger.debug("...items:  "                           + this.items);
				logger.debug("...bytesRead (lastUpdateBytesRead):  " + this.bytesRead + " (" + this.lastUpdateBytesRead + ")");
			}
		}
	}

	public int getItems() {
		return items;
	}

	public float getReadMB() {
		return ((int) Math.abs(bytesRead * 100 / (1024 * 1024))) / 100;
	}

	public float getContentLengthMB() {
		return ((int) Math.abs(contentLength * 100 / (1024 * 1024))) / 100;
	}
	
	public int getRunnigSeconds() {
		return (int)((System.currentTimeMillis() - this.uploadStartTime) / 1000);
	}
	
	public int getTimeLeftSeconds() {
		if (this.getPercentDone() == 100) {
			return 0;
		}
		return this.timeLeft;
	}
	
	public float getUploadSpeedKBproSec() {
		return this.uploadSpeed;
	}
}
