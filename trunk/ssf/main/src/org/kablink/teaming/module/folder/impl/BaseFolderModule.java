/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import java.util.List;
import java.util.Map;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.StatusTicket;


public class BaseFolderModule extends AbstractFolderModule implements BaseFolderModuleMBean {

	public boolean synchronize(Long folderId, StatusTicket statusTicket) throws FIException, UncheckedIOException {
		throw new UnsupportedOperationException("synchronize operation is not supported in the base edition");
	}
	
	public ScheduleInfo getSynchronizationSchedule(Long zoneId, Long folderId) {
		throw new UnsupportedOperationException("getSynchronizationSchedule operation is not supported in the base edition");
	}
	
	public void setSynchronizationSchedule(ScheduleInfo config, Long folderId) {
		throw new UnsupportedOperationException("setSynchronizationSchedule operation is not supported in the base edition");
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
    public List<FolderEntry> _addNetFolderEntries(Long folderId, String definitionId, List<InputDataAccessor> inputDataList, 
    		List<Map> fileItemsList, List<Map> optionsList) 
    	throws AccessControlException, WriteFilesException, WriteEntryDataException {
		throw new UnsupportedOperationException("addNetFolderEntries operation is not supported in the base edition");
	}

}
