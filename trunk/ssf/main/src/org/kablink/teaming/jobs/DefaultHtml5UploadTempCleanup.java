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
package org.kablink.teaming.jobs;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TempFileUtil;
import org.kablink.teaming.web.util.Html5Helper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job that scans the temporary file directory and deletes any
 * 'orphaned' HTML5 uploader temporary files.
 * 
 * 'Orphaned' files are those older than the configured amount of
 * time (default is 1 hour).
 * 
 * @author drfoster@novell.com
 */
public class DefaultHtml5UploadTempCleanup extends SSCronTriggerJob implements Html5UploadTempCleanup {
	private static final Log m_logger = LogFactory.getLog(DefaultHtml5UploadTempCleanup.class);

	// The following contains the prefix and suffix of files from the
	// HTML5 uploader's temporary directory that are recognized as
	// HTML5 uploader temporary files.
	private static String ORPHANED_UPLOAD_FILENAME_PREFIX = Html5Helper.UPLOAD_FILE_PREFIX;
	private static String ORPHANED_UPLOAD_FILENAME_SUFFIX = ".tmp";

	// Callback interface used to process files in the HTML5 uploader
	// temporary directory.
	private static interface TempFileCallback {
		public void checkThisHtml5UploaderTempFile(File html5UploaderTempFile);
	}

	/*
	 * Inner class used to track how HTML5 uploader temporary files get
	 * processed.
	 */
	private static class Html5UploaderTempFileCounters {
		private int	m_deleteCount;	// Counts files that get deleted.
		private int	m_errorCount;	// Counts files that could not be deleted.
		private int	m_totalCount;	// Counts the total number of files that are looked at.
		
		/**
		 * Constructor method.
		 */
		public Html5UploaderTempFileCounters() {
			// Nothing to do.
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public int getDeleteCount() {return m_deleteCount;}
		public int getErrorCount()  {return m_errorCount; }
		public int getTotalCount()  {return m_totalCount; }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void incrDeleteCount() {m_deleteCount += 1;}
		public void incrErrorCount()  {m_errorCount  += 1;}
		public void incrTotalCount()  {m_totalCount  += 1;}
	}

	/**
	 * Called when the job is triggered to scan the files in the HTML5
	 * uploader's temporary directory for orphans and delete them.
	 * 
	 * @param context
	 */
	@Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		m_logger.debug("DefaultHtml5UploadTempCleanup.doExecute():  Cleaning orphaned HTML5 uploader temporary files...");

		// How old does a file have to be to be considered an orphan?
		final long ageInMinutes = SPropsUtil.getLong("html5.upload.temp.cleanup.age.minutes", 60);
		final long ageCheck     = (new Date().getTime() - (ageInMinutes * 60 * 1000));	// Age to check against.  If last modified is before this, the file is deleted.

		// Process the files in the HTML5 uploader's temporary
		// directory...
		final Html5UploaderTempFileCounters counters = new Html5UploaderTempFileCounters();
		processTempFiles(TempFileUtil.getHtml5UploaderTempDir(false), new TempFileCallback() {
			@Override
			public void checkThisHtml5UploaderTempFile(File html5UploaderTempFile) {
				// ...processing each we think is an HTML5 uploader
				// ...temporary file.
				counters.incrTotalCount();
				long lastMod = html5UploaderTempFile.lastModified();
				if (lastMod < ageCheck) {
					try {
						html5UploaderTempFile.delete();
						m_logger.debug("...deleted '" + html5UploaderTempFile.getName() + "'.");
						counters.incrDeleteCount();
					}
					catch (Exception ex) {
						m_logger.debug("...could not delete '" + html5UploaderTempFile.getName() + "':  ", ex);
						counters.incrErrorCount();
					}
				}
			}
		});

		// Log what we did.
		m_logger.debug("...processed " + counters.getTotalCount() + " total HTML5 uploader temporary files...");
		m_logger.debug("......" + counters.getDeleteCount() + " files were deleted..."      );
		m_logger.debug("......" + counters.getErrorCount() + " files could not be deleted.");
	}

	@Override
	public void enable(boolean enable, Long zoneId) {
		enable(enable, new CronJobDescription(zoneId, zoneId.toString(),HTML5_UPLOAD_TEMP_CLEANUP_GROUP, HTML5_UPLOAD_TEMP_CLEANUP_DESCRIPTION));
	}

	@Override
	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new CronJobDescription(zoneId, zoneId.toString(),HTML5_UPLOAD_TEMP_CLEANUP_GROUP, HTML5_UPLOAD_TEMP_CLEANUP_DESCRIPTION));
	}

	@Override
	public void setScheduleInfo(ScheduleInfo schedulerInfo) {
		setScheduleInfo(new CronJobDescription(schedulerInfo.getZoneId(), schedulerInfo.getZoneId().toString(), HTML5_UPLOAD_TEMP_CLEANUP_GROUP, HTML5_UPLOAD_TEMP_CLEANUP_DESCRIPTION), schedulerInfo);
	}

	/*
	 * Called to process the files in the HTML5 uploader's temporary
	 * directory looking for orphans.
	 */
	private void processTempFiles(File directory, TempFileCallback callback) {
		// If we weren't given a directory, or it doesn't exist or it's
		// not really a directory...
		if ((null == directory) || (!(directory.exists())) || (!(directory.isDirectory()))) {
			// ...there's nothing to process.
			return;
		}

		// Find the files that match the HTML5 uploader temporary file
		// naming criteria.
		File[] contents = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File entry) {
				// If this is a file...
				if (entry.isFile()) {
					// ...and the name matches the HTML5 uploader's
					// ...file naming pattern...
					String fName = entry.getName();
					if (fName.startsWith(ORPHANED_UPLOAD_FILENAME_PREFIX) && fName.endsWith(ORPHANED_UPLOAD_FILENAME_SUFFIX)) {
						// ...return true as we need to consider it.
						return true;
					}
				}
				
				// If we get here, this file won't be considered.
				// Return false. 
				return false;
			}
		});
		
		// Scan the directory's contents.
		for (File entry:  contents) {
			// If this entry doesn't exist...
			if (!(entry.exists())) {
				// ...skip it.
				continue;
			}

			// ...otherwise, tell the callback to process it.
			callback.checkThisHtml5UploaderTempFile(entry);
		}
	}
}
