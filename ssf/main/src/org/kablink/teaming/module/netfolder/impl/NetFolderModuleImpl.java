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

package org.kablink.teaming.module.netfolder.impl;

import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Binder.SyncScheduleOption;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.netfolder.NetFolderModule;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SpringContextUtil;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author jong
 *
 */
public class NetFolderModuleImpl extends CommonDependencyInjection implements NetFolderModule {

    public NetFolderConfig createNetFolder(Long templateId, Long parentBinderId, String name, User owner, String rootName, String path, Boolean isHomeDir, boolean indexContent, Boolean inheritIndexContent, SyncScheduleOption syncScheduleOption, Boolean fullSyncDirOnly ) 
    		throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	
    	// Create and save a new net folder
    	NetFolderConfig nf = new NetFolderConfig();
    	nf.setName(name);
    	nf.setNetFolderServerId(NetFolderUtil.getNetFolderServerByName(rootName).getId());
    	nf.setHomeDir(isHomeDir);
    	nf.setIndexContent(indexContent);
    	nf.setUseInheritedIndexContent(inheritIndexContent);
    	nf.setSyncScheduleOption(syncScheduleOption);
    	nf.setFullSyncDirOnly(fullSyncDirOnly);
    	getCoreDao().saveNewSessionWithoutUpdate(nf);

    	
		// Create top-level folder corresponding to the net folder.
    	getFolderModule().createNetFolder(nf.getId(), templateId, parentBinderId, name, owner, rootName, path, isHomeDir, indexContent, inheritIndexContent, syncScheduleOption, fullSyncDirOnly);

   		return nf;
    }
    
	/*
	 * @see org.kablink.teaming.module.netfolder.NetFolderModule#obtainFolderEntry(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public FolderEntry obtainFolderEntry(Long netFolderId, String filePath,
			boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.kablink.teaming.module.netfolder.NetFolderModule#obtainFolder(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public Folder obtainFolder(Long netFolderId, String folderPath,
			boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	
	protected FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}
	
	protected TemplateModule getTemplateModule() {
		return (TemplateModule) SpringContextUtil.getBean("templateModule");
	}
	
	protected TransactionTemplate getTransactionTemplate() {
		return (TransactionTemplate) SpringContextUtil.getBean("transactionTemplate");
	}
}
