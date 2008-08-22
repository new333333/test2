/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.web.upload;

import org.apache.commons.fileupload.ProgressListener;
import org.apache.log4j.Logger;

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

	public FileUploadProgressListener() {
		this.uploadStartTime = System.currentTimeMillis();
		this.lastUpdateTime = System.currentTimeMillis();
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
