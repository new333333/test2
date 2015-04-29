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
package org.kablink.teaming.module.folder.impl;

import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.BinderState.FullSyncStats;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.StatusTicket;

/**
 * ?
 * 
 * @author ?
 */
public class BaseFolderModule extends AbstractFolderModule implements BaseFolderModuleMBean {
	@Override
	public boolean fullSynchronize(Long folderId, Boolean dirOnly, StatusTicket statusTicket) throws FIException, UncheckedIOException {
		return true;
	}
	
	public ScheduleInfo getSynchronizationSchedule(Long folderId) {
		return null;
	}
	
	@Override
	public void setSynchronizationSchedule(ScheduleInfo config, Long folderId) {
    }

	@Override
    public void indexFileContentForNetFolder(Folder netFolderRoot) {
	}

	@Override
	public boolean isSyncStatsEnabled() {
		return false;
	}

	@Override
	public void setSyncStatsEnabled(boolean syncStatsEnabled) {
	}

	@Override
	public void clearSyncStats() {
	}

	@Override
	public void dumpSyncStatsToLog() {
	}

	@Override
	public String dumpSyncStatsAsString() {
		return "";
	}  

	@Override
	public boolean jitSynchronize(Folder folder) {
		return true;
	}

	@Override
	public void dumpJitsStatsToLog() {
	}

	@Override
	public String dumpJitsStatsAsString() {
		return "";
	}
	
	@Override
	public void modifyNetFolder(
					NetFolderConfig netFolderConfig,
					Long folderId )
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
	}

	@Override
	public void deleteNetFolder(Long folderId, boolean deleteSource) {
	}

	@Override
	public void syncAclForNetFolderRoot(Folder netFolderRoot) {
	}

	@Override
	public Folder createNetFolder(
					Long templateId,
					Long parentBinderId,
					String name,
					String title,
					User owner ) throws AccessControlException, WriteFilesException, WriteEntryDataException {
		return null;
	}

	@Override
	public FullSyncStats getNetFolderFullSyncStats(Long netFolderId) {
		return null;
	}

	@Override
	public boolean requestNetFolderFullSyncStop(Long netFolderId) {
		return false;
	}
	
	@Override
	public Map getNetFolderAccessData(Folder netFolder) {
		return new HashMap();
	}

	@Override
	public boolean enqueueFullSynchronize(Long folderId) {
		return false;
	}
	
	@Override
	public Folder createCloudFolder(Long templateId, Long parentBinderId,
			String name, User owner, String rootName) throws AccessControlException, WriteFilesException, WriteEntryDataException {
		// Open source stub.  See PlusFolderModule for full
		// implementation.
		return null;
	}
	
	@Override
	public void deleteCloudFolder(Long folderId, boolean deleteSource) {
		// Open source stub.  See PlusFolderModule for full
		// implementation.
	}

	@Override
	public void modifyCloudFolder(Long folderId, String netFolderName, String rootName)
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
		// Open source stub.  See PlusFolderModule for full
		// implementation.
	}

	@Override
	public boolean dequeueFullSynchronize(Long netFolderId) {
		return false;
	}

	@Override
	public FileSyncStatus fileSynchronize(FolderEntry fileEntry)
			throws FIException, UncheckedIOException, ConfigurationException {
		return FileSyncStatus.nochange;
	}

	@Override
	public void netFolderContentIndexingJobSchedule(Long folderId) {
	}

	@Override
	public void netFolderContentIndexingJobUnschedule(Long folderId) {
	}

	@Override
	public void netFolderContentIndexingJobDelete(Long folderId) {
	}

    @Override
    public boolean enqueueInitialNetFolderSync(Long folderId) {
        return false;
    }
}
