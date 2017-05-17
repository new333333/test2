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
package org.kablink.teaming.domain;

import java.util.Date;

/**
 * @author jong
 *
 */
public class BinderState extends ZonedObject {

	public enum FullSyncStatus {
		/**
		 * Full synchronization is ready to run and is waiting in a queue to be assigned to a thread for execution.
		 */
		ready (false),
		/**
		 * Full synchronization has been taken off the ready queue and is about to start its execution.
		 */
		taken (false),
		/**
		 * Full synchronization has started and is currently running. Only this status indicates running state.
		 */
		started (false),
		/**
		 * Full synchronization that was in taken or started state has stopped due to explicit request by admin to stop it.
		 */
		stopped (true), // in idle because it was stopped
		/**
		 * Full synchronization has run its course and finished. This doesn't tell how successful the sync was though.
		 */
		finished (true), // in idle because it was finished
		/**
		 * Full synchronization that was in taken or started state has been aborted due to system termination 
		 * (gracious shutdown, abrupt termination, crash) or non-intermittent error such as unreachable file server.
		 */
		aborted (true), // in idle because it was aborted
		/**
		 * Full synchronization that was in ready state has been canceled due to explicit request by admin to cancel it.
		 */
		canceled (true), // in idle because it was canceled
		/**
		 * The corresponding binder is being deleted. Unlike other status, this signals the end of life cycle for this object.
		 */
		deleting (true);

        private boolean completed;

        private FullSyncStatus(boolean completed) {
            this.completed = completed;
        }

        public boolean isCompleted() {
            return completed;
        }
	}
	
	// IMPORTANT: This enum values must be exactly one character long
	public enum SyncType {
		/**
		 * FUll sync
		 */
		F,
		/**
		 * JITS
		 */
		J
	}
	
	/*
	 * Owning binder ID
	 */
	private Long binderId;
	/*
	 * Last time sync finished on this particular folder.
	 * This does NOT tell whether the sync was full (whole branch) or JIT 
	 * (single level only), or whether the sync was successful or not.
	 * Applicable to all folders within net folder, not just net folder root.
	 */
	private Date lastSyncTime; 
	
	/*
	 * Last time full sync initiated on this binder completed successfully.
	 * This property is relevant only to those binders on which full sync is initiated
	 * which are almost always net folder roots (currently, Filr does not allow starting
	 * full sync on any folder other than net folder roots, but that may change in the future).
	 * Those binders that are synchronized indirectly from a full sync triggered on 
	 * one of their ancestor binders do not get this property value set.
	 * This property value is set if and only if full sync completes successfully,
	 * where a successful sync is defined as one where the sync process was able
	 * to enumerate all children of successfully processed folders within the data
	 * hierarchy starting from the top all the way down to leaves. Failure to process
	 * individual files and sub-folders do not count towards overall failure. 
	 * With this definition, there is NO guarantee that the sync process has actually
	 * visited and enumerated ALL files and sub-folders that exist on the file system,
	 * because a failure to process a particular folder can most likely result in 
	 * aborting that part of the tree all together. 
	 */
	private Date lastFullSyncCompletionTime;

	/*
	 * Statistics information specific to the last full synchronization instance that 
	 * either already ended or is currently in progress. This information spans no more
	 * than a single full synchronization instance, and no history is kept.
	 * This field is applicable to net folder roots only.
	 */
	private FullSyncStats fullSyncStats;
	
	/*
	 * This flag is used to request full synchronization currently in progress to be stopped as soon as possible.
	 * 
	 */
	private Boolean fullSyncStopRequested;
	
	/*
	 * Tells whether latest sync is full sync or JITS on this folder
	 * Applicable to all folders within net folder, not just net folder root.
	 */
	private SyncType syncType;
	
	/*
	 * IP address of the node executing latest sync on this folder
	 * Applicable to all folders within net folder, not just net folder root.
	 */
	private String syncIpv4Address;

	/*
	 * Tells whether or not sync (of any type) is in progress on this folder.
	 * Applicable to all folders within net folder, not just net folder root.
	 */
	private Boolean syncInProgress;
	
	/*
	 * The date up to which a net folder in Filr is updated with respect to the 
	 * source file system. This is an indicator of how up to date a net folder is.
	 * 
	 * With successful full initial sync, this value is set to the time at which 
	 * the full initial sync started, which means we do know that the data in
	 * Filr is at least that much up-to-date. The same is true with successful
	 * full subsequent sync that uses brute-force scanning. This is based on the
	 * fact that both types of full sync always work with the latest states of 
	 * data by going to the very source (hence, no latency in data freshness), 
	 * AND that they do not work with change list and therefore there is no reliable
	 * way to find out precisely when was the last time anything changed on the
	 * file system prior to the sync.
	 * 
	 * On the other hand, with successful full subsequent sync that uses some
	 * sort of change list (delta) mechanism, this value is set to the time of
	 * the last change item in the change list processed during the sync cycle.
	 * The reason why we do not set the value to the time at which the sync 
	 * cycle started is because the underlying mechanism providing this service
	 * may not necessarily work 100% real-time. For example, consider an example
	 * where there are changes c2, c3, and c4 at time t2, t3, and t4 respectively,
	 * and a sync cycle starts at time t5. Then the sync process asks the service
	 * for a change list since t1 (which is the value of this field from the
	 * last sync cycle). The service returns {c2, c3} and misses c4 in response
	 * to this request, because this service is not 100% real-time based and 
	 * its internal change list is maintained by getting notified of the file 
	 * system changes in a batch every 30 seconds (hence up to 30 seconds delay).
	 * So the sync process has no opportunity to process c4 during the particular
	 * sync cycle. In this case, if the sync cycle sets this value to t5 (which
	 * is the time at which the sync cycle started), then when the next sync
	 * cycle kicks in and use the value (t5) to obtain a change list from the
	 * service, the response will miss t4 in the result because it comes before
	 * t5. For this reason, this value has to be set to the time of the last
	 * change item processed by the sync cycle.
	 */
	private Date updatedToDate;
	
	protected BinderState() {
		// Use by Hibernate only
	}
	
	public BinderState(Long binderId) {
		setBinderId(binderId);
	}
	
	public Long getBinderId() {
		return binderId;
	}

	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}

	public Date getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(Date lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public SyncType getSyncType() {
		return syncType;
	}
	
	public void setSyncType(SyncType syncType) {
		this.syncType = syncType;
	}
	
	// Used by Hibernate only
	private String getSyncTypeStr() {
		if(syncType == null)
			return null;
		else
			return syncType.name();
	}
	
	// Used by Hibernate only
	private void setSyncTypeStr(String syncTypeStr) {
		if(syncTypeStr == null) {
			syncType = null;
		}
		else {
			try {
				syncType = SyncType.valueOf(syncTypeStr);
			}
			catch(Exception e) {
				syncType = null;
			}
		}
	}
	
	public String getSyncIpv4Address() {
		return syncIpv4Address;
	}
	
	public void setSyncIpv4Address(String syncIpv4Address) {
		this.syncIpv4Address = syncIpv4Address;
	}
		
	public boolean isSyncInProgress() {
		if(syncInProgress == null)
			return false; // default to false
		else 
			return syncInProgress.booleanValue();
	}

	public void setSyncInProgress(boolean syncInProgress) {
		this.syncInProgress = syncInProgress;
	}

	public Date getUpdatedToDate() {
		return updatedToDate;
	}

	public void setUpdatedToDate(Date updatedToDate) {
		this.updatedToDate = updatedToDate;
	}

	public Date getLastFullSyncCompletionTime() {
		return lastFullSyncCompletionTime;
	}

	public void setLastFullSyncCompletionTime(Date lastFullSyncCompletionTime) {
		this.lastFullSyncCompletionTime = lastFullSyncCompletionTime;
	}

	public FullSyncStats getFullSyncStats() {
		if(fullSyncStats == null)
			fullSyncStats = new FullSyncStats();
		return fullSyncStats;
	}
	
	public void setFullSyncStats(FullSyncStats fullSyncStats) {
		this.fullSyncStats = fullSyncStats;
	}
	
	public Boolean getFullSyncStopRequested() {
		return fullSyncStopRequested;
	}

	public void setFullSyncStopRequested(Boolean fullSyncStopRequested) {
		this.fullSyncStopRequested = fullSyncStopRequested;
	}

	// This class is used only for net folder roots.
	public static class FullSyncStats {
		/*
		 * Full sync status
		 */
		FullSyncStatus status;
		
		/*
		 * The time 'status' value was set or cleared.
		 */
		Date statusDate;
		
		/*
		 * The IPv4 address of the node from which 'status' value was set or cleared.
		 */
		String statusIpv4Address;
		
		/*
		 * The time full sync started on this binder.
		 */
		Date startDate;
		
		/*
		 * The time full sync ended on this binder.
		 */
		Date endDate;
		
		/*
		 * Whether or not the full sync was directory only 
		 */
		Boolean dirOnly;

		/*
		 * Whether or not directory enumeration failed
		 */
		Boolean enumerationFailed;
		
		/*
		 * Count of files encountered as result of enumerating source file system
		 */
		Integer countFiles;
		/*
		 * Count of files added
		 */
		Integer countFileAdd;
		/*
		 * Count of files expunged
		 */
		Integer countFileExpunge;
		/*
		 * Count of files modified
		 */
		Integer countFileModify;
		/*
		 * Count of files on which ACLs are set/updated
		 */
		Integer countFileSetAcl;
		/*
		 * Count of files on which ownership are explicitly set/updated
		 */
		Integer countFileSetOwnership;
		/*
		 * Count of folders encountered as result of enumerating source file system
		 */
		Integer countFolders;
		/*
		 * Count of folders added
		 */
		Integer countFolderAdd;
		/*
		 * Count of folders expunged
		 */
		Integer countFolderExpunge;
		/*
		 * Count of folders on which ACLs are set/updated
		 */
		Integer countFolderSetAcl;
		/*
		 * Count of folders on which ownership are explicitly set/updated
		 */
		Integer countFolderSetOwnership;
		/*
		 * Count of dangling entries expunged
		 */
		Integer countEntryExpunge;
		/*
		 * Count of failure. Note that this does NOT count the number of unique folders
		 * and files failed to process. That is actually hard number to obtain due to
		 * recursive nature of the processing. Instead, this count simply denotes how
		 * many operations failed during the sync without clearing defining what those
		 * operations are and at what granularity. 
		 */
		Integer countFailure;
		/*
		 * Count of folders synchronized. This includes both newly created folders and
		 * existing folders that have been synchronized with the source. This number
		 * does not include expunged folders.
		 */
		Integer countFolderProcessed;
		
		/*
		 * Maximum number of folders that were found in the queue at once. In other word,
		 * an indication of how big the queue has grown at any time during the sync.
		 */
		Integer countFolderMaxQueue;
		
		public FullSyncStatus getStatus() {
			return status;
		}

		private void setStatus(FullSyncStatus status) {
			this.status = status;
		}

		// Used by Hibernate only
		private String getStatusStr() {
			if(status == null)
				return null;
			else
				return status.name();
		}
		
		// Used by Hibernate only
		private void setStatusStr(String statusStr) {
			if(statusStr == null) {
				status = null;
			}
			else {
				try {
					status = FullSyncStatus.valueOf(statusStr);
				}
				catch(Exception e) {
					status = null;
				}
			}
		}
		
		public Date getStatusDate() {
			return statusDate;
		}

		// For use by Hibernately only
		private void setStatusDate(Date statusDate) {
			this.statusDate = statusDate;
		}

		public Date getStartDate() {
			return startDate;
		}

		public String getStatusIpv4Address() {
			return statusIpv4Address;
		}

		// For use by Hibernate only
		private void setStatusIpv4Address(String statusIpv4Address) {
			this.statusIpv4Address = statusIpv4Address;
		}

		public void setStatus(FullSyncStatus status, String statusIpv4Address) {
			setStatus(status); 
			setStatusDate(new Date());
			setStatusIpv4Address(statusIpv4Address);
		}
		
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public Boolean getDirOnly() {
			return dirOnly;
		}

		public void setDirOnly(Boolean dirOnly) {
			this.dirOnly = dirOnly;
		}

		public Integer getCountFiles() {
			return countFiles;
		}

		public Boolean getEnumerationFailed() {
			return enumerationFailed;
		}

		public void setEnumerationFailed(Boolean enumerationFailed) {
			this.enumerationFailed = enumerationFailed;
		}

		public void setCountFiles(Integer countFiles) {
			this.countFiles = countFiles;
		}

		public Integer getCountFileAdd() {
			return countFileAdd;
		}

		public void setCountFileAdd(Integer countFileAdd) {
			this.countFileAdd = countFileAdd;
		}

		public Integer getCountFileExpunge() {
			return countFileExpunge;
		}

		public void setCountFileExpunge(Integer countFileExpunge) {
			this.countFileExpunge = countFileExpunge;
		}

		public Integer getCountFileModify() {
			return countFileModify;
		}

		public void setCountFileModify(Integer countFileModify) {
			this.countFileModify = countFileModify;
		}

		public Integer getCountFileSetAcl() {
			return countFileSetAcl;
		}

		public void setCountFileSetAcl(Integer countFileSetAcl) {
			this.countFileSetAcl = countFileSetAcl;
		}

		public Integer getCountFileSetOwnership() {
			return countFileSetOwnership;
		}

		public void setCountFileSetOwnership(Integer countFileSetOwnership) {
			this.countFileSetOwnership = countFileSetOwnership;
		}

		public Integer getCountFolders() {
			return countFolders;
		}

		public void setCountFolders(Integer countFolders) {
			this.countFolders = countFolders;
		}

		public Integer getCountFolderAdd() {
			return countFolderAdd;
		}

		public void setCountFolderAdd(Integer countFolderAdd) {
			this.countFolderAdd = countFolderAdd;
		}

		public Integer getCountFolderExpunge() {
			return countFolderExpunge;
		}

		public void setCountFolderExpunge(Integer countFolderExpunge) {
			this.countFolderExpunge = countFolderExpunge;
		}

		public Integer getCountFolderSetAcl() {
			return countFolderSetAcl;
		}

		public void setCountFolderSetAcl(Integer countFolderSetAcl) {
			this.countFolderSetAcl = countFolderSetAcl;
		}

		public Integer getCountFolderSetOwnership() {
			return countFolderSetOwnership;
		}

		public void setCountFolderSetOwnership(Integer countFolderSetOwnership) {
			this.countFolderSetOwnership = countFolderSetOwnership;
		}

		public Integer getCountEntryExpunge() {
			return countEntryExpunge;
		}

		public void setCountEntryExpunge(Integer countEntryExpunge) {
			this.countEntryExpunge = countEntryExpunge;
		}

		public Integer getCountFailure() {
			return countFailure;
		}

		public void setCountFailure(Integer countFailure) {
			this.countFailure = countFailure;
		}

		public Integer getCountFolderProcessed() {
			return countFolderProcessed;
		}

		public void setCountFolderProcessed(Integer countFolderProcessed) {
			this.countFolderProcessed = countFolderProcessed;
		}

		public Integer getCountFolderMaxQueue() {
			return countFolderMaxQueue;
		}

		public void setCountFolderMaxQueue(Integer countFolderMaxQueue) {
			this.countFolderMaxQueue = countFolderMaxQueue;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("status=")
			.append(status)
			.append(",statusDate=")
			.append(statusDate)
			.append(",IP=")
			.append(statusIpv4Address)
			.append(",countFiles=")
			.append(countFiles)
			.append(",countFolders=")
			.append(countFolders);
			return sb.toString();
		}
	}
}
