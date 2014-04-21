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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NoNetFolderConfigByTheIdException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.NetFolderConfig.SyncScheduleOption;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.netfolder.NetFolderModule;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SpringContextUtil;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author jong
 *
 */
public class NetFolderModuleImpl extends CommonDependencyInjection implements NetFolderModule {

	private Log logger  = LogFactory.getLog(getClass());
	
	private static final String NET_FOLDER_NAME_PREFIX = "_netfolder_";
	
	@Override
    public NetFolderConfig createNetFolder(Long templateId, Long parentBinderId, final String name, User owner, final String netFolderServerName, final String path, final Boolean isHomeDir, final boolean indexContent, final Boolean inheritIndexContent, final SyncScheduleOption syncScheduleOption, final Boolean fullSyncDirOnly ) 
    		throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	
    	// Create and save a new net folder config object        		
		final NetFolderConfig nfc = new NetFolderConfig(NetFolderUtil.getNetFolderServerByName(netFolderServerName).getId());
    	nfc.setName(name);
    	nfc.setFolderId(0L); // temporary value
    	nfc.setResourcePath(path);
    	nfc.setHomeDir(isHomeDir);
    	nfc.setIndexContent(indexContent);
    	nfc.setUseInheritedIndexContent(inheritIndexContent);
    	nfc.setSyncScheduleOption(syncScheduleOption);
    	nfc.setFullSyncDirOnly(fullSyncDirOnly);		
		if(logger.isDebugEnabled())
			logger.debug("Creating new net folder config object " + nfc.toString());
		final NetFolderConfig netFolderConfig = (NetFolderConfig) getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		getCoreDao().save(nfc);
				return nfc;
        	}
        });

		// Create top-level folder corresponding to the net folder as data.
   		String folderName = NET_FOLDER_NAME_PREFIX + netFolderConfig.getId();
		if(logger.isDebugEnabled())
			logger.debug("Creating new folder object (name='" + folderName + "') representing the top of the net folder");
    	final Folder folder = getFolderModule().createNetFolder(netFolderConfig.getId(), templateId, parentBinderId, name, folderName, owner, isHomeDir, indexContent, inheritIndexContent, syncScheduleOption, fullSyncDirOnly);
    	
    	// Finish linking them (Note: This association is managed by application rather than by database foreign key constraint)
    	if(logger.isDebugEnabled())
    		logger.debug("Linking net folder config object (id=" + netFolderConfig.getId() + ") with folder object (id=" + folder.getId() + ")");
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		netFolderConfig.setFolderId(folder.getId());
				return null;
        	}
        });

   		return netFolderConfig;
    }
    
	@Override
    public void modifyNetFolder(final NetFolderConfig netFolderConfig) throws AccessControlException, WriteFilesException, WriteEntryDataException {
		if(logger.isDebugEnabled())
			logger.debug("Updating net folder config object " + netFolderConfig.toString());
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		getCoreDao().update(netFolderConfig);
				return null;
        	}
        });
        
        if(logger.isDebugEnabled())
        	logger.debug("Updating folder object (id=" + netFolderConfig.getFolderId() + ")");
        getFolderModule().modifyNetFolder(netFolderConfig.getFolderId(), netFolderConfig.getId(), netFolderConfig.getIndexContent(), netFolderConfig.getUseInheritedIndexContent());
    }
    
	@Override
    public void deleteNetFolder(Long netFolderConfigId, boolean deleteSource) {
		NetFolderConfig nfc;
		try {
	    	nfc = getCoreDao().loadNetFolderConfig(netFolderConfigId);
		}
		catch(NoNetFolderConfigByTheIdException e) {
			if(logger.isDebugEnabled())
				logger.debug("Net folder config object (id=" + netFolderConfigId + ") not found. Nothing to delete.");
			return;
		}
		
		try {
	        if(logger.isDebugEnabled())
	        	logger.debug("Deleting folder object (id=" + nfc.getFolderId() + ") and everything in it");
	        getFolderModule().deleteNetFolder(nfc.getFolderId(), deleteSource);
		}
		catch(Exception e) {
			logger.warn("Error deleting folder object (id=" + nfc.getFolderId() + ") and everything in it", e);
		}
		
    	if(logger.isDebugEnabled())
    		logger.debug("Deleting net folder config object " + nfc.toString());
    	final NetFolderConfig nfcRef = nfc;
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		getCoreDao().delete(nfcRef);
				return null;
        	}
        });
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
