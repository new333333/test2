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
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TempFileUtil;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job that scans the temporary file directory and deletes any unused
 * temporary files.
 * 
 * Unused files are those older than the configured amount of time
 * (default is 1 day.)
 * 
 * @author drfoster@novell.com
 */
public class DefaultTempFileCleanup extends SSCronTriggerJob implements TempFileCleanup {
	private static final Log m_logger = LogFactory.getLog(DefaultTempFileCleanup.class);

	/*
	 * Inner class used to count temporary items processed.
	 */
	private static class TempCounters {
		private long	m_deletedFileCount;		// Counts files that got deleted.
		private long	m_errorFileCount;		// Counts files that could not be deleted.
		private long	m_ignoredFileCount;		// Counts files that were ignore because they weren't old enough.
		private long	m_totalDirectoryCount;	// Counts the total number of directories that were looked at.
		private long	m_totalFileCount;		// Counts the total number of files       that were looked at.
		
		/**
		 * Constructor method.
		 */
		public TempCounters() {
			// Nothing to do.
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public long getDeletedFileCount()    {return m_deletedFileCount;   }
		public long getErrorFileCount()      {return m_errorFileCount;     }
		public long getIgnoredFileCount()    {return m_ignoredFileCount;   }
		public long getTotalDirectoryCount() {return m_totalDirectoryCount;}
		public long getTotalFileCount()      {return m_totalFileCount;     }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void incrDeletedFileCount()    {m_deletedFileCount    += 1;}
		public void incrErrorFileCount()      {m_errorFileCount      += 1;}
		public void incrIgnoredFileCount()    {m_ignoredFileCount    += 1;}
		public void incrTotalDirectoryCount() {m_totalDirectoryCount += 1;}
		public void incrTotalFileCount()      {m_totalFileCount      += 1;}

		/**
		 * If debugging is enabled, write the counts to the log. 
		 */
		public void logCounts() {
			if (m_logger.isDebugEnabled()) {
				long totalDirs  = getTotalDirectoryCount();
				long totalFiles = getTotalFileCount();
				if (0l == totalFiles) {
					m_logger.debug("...no temporary files were found scanning " + totalDirs + " temporary directories.");
				}
				else {
					m_logger.debug("...processed " + getTotalFileCount()   + " total temporary files from " + totalDirs + " temporary directories...");
					m_logger.debug("......"        + getIgnoredFileCount() + " files were ignored because they weren't old enough..."                );
					m_logger.debug("......"        + getDeletedFileCount() + " files were deleted..."                                                );
					m_logger.debug("......"        + getErrorFileCount()   + " files could not be deleted."                                          );
				}
			}
		}
	}

	/**
	 * Called when the job is triggered to scan the files in the
	 * temporary directory for unused ones and delete them.
	 * 
	 * @param context
	 */
	@Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		long begin = System.nanoTime();
		try {
			m_logger.debug("DefaultTempFileCleanup.doExecute():  Cleaning unused temporary files...");
	
			// How old does a file have to be to be considered unused?
			final long ageInMinutes = SPropsUtil.getLong("temp.file.cleanup.age.minutes", 1440);
			final long ageCheck     = (new Date().getTime() - (ageInMinutes * TempFileUtil.A_MINUTE));	// Age to check against.  If last modified is before this, the file is deleted.
	
			// Process the files starting in the root temporary
			// directory...
			final TempCounters counters = new TempCounters();
			processTempFiles(TempFileUtil.getTempFileRootDir(), counters, ageCheck);
	
			// ...and log what we did.
			counters.logCounts();
		}
		
		finally {
			// If info logging is enabled...
			if (m_logger.isInfoEnabled()) {
				// ...log the amount of time the cleanup took.
				double diff = ((System.nanoTime() - begin) / 1000000.0);
				String head;
				if (m_logger.isDebugEnabled())
				     head = "...";
				else head = "DefaultTempFileCleanup.doExecute() ";
				m_logger.info(head + "cleanup took " + diff + " ms to complete.");
			}
		}
	}

	@Override
	public void enable(boolean enable, Long zoneId) {
		enable(enable, new CronJobDescription(zoneId, zoneId.toString(),TEMP_FILE_CLEANUP_GROUP, TEMP_FILE_CLEANUP_DESCRIPTION));
	}

	@Override
	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new CronJobDescription(zoneId, zoneId.toString(),TEMP_FILE_CLEANUP_GROUP, TEMP_FILE_CLEANUP_DESCRIPTION));
	}

	@Override
	public void setScheduleInfo(ScheduleInfo schedulerInfo) {
		setScheduleInfo(new CronJobDescription(schedulerInfo.getZoneId(), schedulerInfo.getZoneId().toString(), TEMP_FILE_CLEANUP_GROUP, TEMP_FILE_CLEANUP_DESCRIPTION), schedulerInfo);
	}

	/*
	 * Recursively called to process the files in a temporary directory
	 * looking for unused files to delete.
	 */
	private void processTempFiles(File directory, TempCounters counters, long ageCheck) {
		// If we weren't given a directory, or it doesn't exist or it's
		// not really a directory...
		if ((null == directory) || (!(directory.exists())) || (!(directory.isDirectory()))) {
			// ...there's nothing to process.
			return;
		}
		
		// Count this directory.
		counters.incrTotalDirectoryCount();

		// If there's nothing in this directory...
		File[] contents = directory.listFiles();
		if ((null == contents) || (0 == contents.length)) {
			// ...there's nothing to process.
			return;
		}
		
		// Scan the directory's contents.
		for (File entry:  contents) {
			// If this entry doesn't exist...
			if (!(entry.exists())) {
				// ...skip it.
				continue;
			}
			
			// If this entry is a directory...
			if (entry.isDirectory()) {
				// ...process it recursively...
				processTempFiles(entry, counters, ageCheck);
			}

			// ...if this entry is a file...
			else if (entry.isFile()) {
				// ...and it's older than the ageCheck...
				counters.incrTotalFileCount();
				long lastMod = entry.lastModified();
				if (lastMod < ageCheck) {
					String fName = entry.getName();
					try {
						// ...delete it.
						entry.delete();
						m_logger.debug("...deleted file '" + fName + "'.");
						counters.incrDeletedFileCount();
					}
					catch (Exception ex) {
						m_logger.debug("...could not delete file '" + fName + "':  ", ex);
						counters.incrErrorFileCount();
					}
				}
				
				else {
					// ...otherwise, ignore it.
					counters.incrIgnoredFileCount();
				}
			}
		}
	}
}
