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
package org.kablink.teaming.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.web.servlet.listener.SessionListener.ActiveSessionCounter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author jong
 *
 */
public class GangliaMonitoring extends QuartzJobBean {

	private static GangliaMonitoring instance; // A singleton instance
	
	private static final Log logger = LogFactory.getLog(GangliaMonitoring.class);
	
	private AtomicInteger uniqueLoggedInUsers = new AtomicInteger();
	private AtomicInteger fileWrites = new AtomicInteger();
	private AtomicInteger fileReads = new AtomicInteger();
	private AtomicInteger filesShared = new AtomicInteger();
	private AtomicInteger foldersShared = new AtomicInteger();
	private AtomicLong restRequests = new AtomicLong();
	
	public GangliaMonitoring() {
		instance = this;
	}
	
	public static int incrementUniqueLoggedInUsers() {
		if(instance == null) return 0; // not ready
		return instance.uniqueLoggedInUsers.addAndGet(1);
	}
	
	public static int incrementFileWrites() {
		if(instance == null) return 0; // not ready
		return instance.fileWrites.addAndGet(1);
	}
	
	public static int incrementFileReads() {
		if(instance == null) return 0; // not ready
		return instance.fileReads.addAndGet(1);
	}
	
	public static int incrementFilesShared() {
		if(instance == null) return 0; // not ready
		return instance.filesShared.addAndGet(1);
	}
	
	public static int incrementFoldersShared() {
		if(instance == null) return 0; // not ready
		return instance.foldersShared.addAndGet(1);
	}
	
	public static long incrementRestRequests() {
		if(instance == null) return 0; // not ready
		return instance.restRequests.addAndGet(1);
	}
	
	public void dump() throws IOException {
		if(instance == null) return; // not ready

		File gangliaDir = new File(DirPath.getWebappRootDirPath() + "/../../var/ganglia");
		if(!gangliaDir.exists()) {
			logger.info("Creating directory [" + gangliaDir.getAbsolutePath() + "]");
			gangliaDir.mkdirs();
		}

		File gangliaFile = new File(gangliaDir, "server_metrics.properties");
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(gangliaFile)));
		try {
			writeProperty(writer, "sessions", String.valueOf(ActiveSessionCounter.getActiveSessionCount()));
			writeProperty(writer, "peakSessions", String.valueOf(ActiveSessionCounter.getPeakActiveSessionCount()));
			writeProperty(writer, "uniqueLoggedInUsers", String.valueOf(instance.uniqueLoggedInUsers.get()));
			writeProperty(writer, "fileWrites", String.valueOf(instance.fileWrites.get()));
			writeProperty(writer, "fileReads", String.valueOf(instance.fileReads.get()));
			writeProperty(writer, "filesShared", String.valueOf(instance.filesShared.get()));
			writeProperty(writer, "foldersShared", String.valueOf(instance.foldersShared.get()));
			writeProperty(writer, "restRequests", String.valueOf(instance.restRequests));
		}
		finally {
			writer.close();
		}
	}

	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		// This task does not need to run in any user's account/context.
		try {
			dump();
		}
		catch(Exception e) {
			logger.warn("Error while writing monitoring information", e);
		}
	}

	private void writeProperty(PrintWriter writer, String key, String value) {
		writer.println(key + "=" + value);
	}

}
