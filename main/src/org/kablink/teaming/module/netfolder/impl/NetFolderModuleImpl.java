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
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoNetFolderConfigByTheIdException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.NetFolderConfig.SyncScheduleOption;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.module.binder.BinderModule;
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
	
	private static final String NET_FOLDER_TOP_NAME_PREFIX = "_NFT_";

	@Override
    public NetFolderConfig createNetFolder(Long templateId, 
    		Long parentBinderId, 
    		String name, 
    		User owner, 
    		String rootName, 
    		String path, 
    		Boolean isHomeDir, 
    		boolean indexContent, 
    		Boolean inheritIndexContent, 
    		SyncScheduleOption syncScheduleOption, 
    		Boolean fullSyncDirOnly,
    		Boolean allowDesktopAppToTriggerSync,
    		Boolean inheritAllowDesktopAppToTriggerSync) throws AccessControlException, WriteFilesException, WriteEntryDataException {
		
		// Before doing anything, make sure that the specified net folder server exists and the
		// corresponding resource driver has been loaded in memory.
		ResourceDriverConfig nfs = NetFolderUtil.getNetFolderServerByName(rootName);
		ResourceDriver driver = NetFolderUtil.getResourceDriverByNetFolderServerId(nfs.getId());
		// If still here, above validation was successful.
    	
		// Create top-level folder corresponding to the net folder as data. If there is a naming
		// conflict, this operation will fail right here.
   		String folderName = NET_FOLDER_TOP_NAME_PREFIX + name;
   		if(folderName.length() > 128)
   			folderName = folderName.substring(0, 128);
		if(logger.isDebugEnabled())
			logger.debug("Creating new folder object (name='" + folderName + "') representing the top of the net folder");
    	final Folder folder = getFolderModule().createNetFolder(
    			templateId, 
    			parentBinderId, 
    			folderName,
    			name,
    			owner);
    	
    	// Create and save a new net folder config object    
    	NetFolderConfig netFolderConfig;
    	try {
			final NetFolderConfig nfc = new NetFolderConfig(nfs.getId());
	    	nfc.setName(name);
	    	nfc.setTopFolderId(folder.getId());
	    	nfc.setResourcePath(path);
	    	nfc.setHomeDir(isHomeDir);
	    	nfc.setIndexContent(indexContent);
	    	nfc.setUseInheritedIndexContent(inheritIndexContent);
	    	nfc.setSyncScheduleOption(syncScheduleOption);
	    	nfc.setFullSyncDirOnly(fullSyncDirOnly);
			if (allowDesktopAppToTriggerSync!=null) {
				nfc.setAllowDesktopAppToTriggerInitialHomeFolderSync(allowDesktopAppToTriggerSync);
			}
 			if (inheritAllowDesktopAppToTriggerSync!=null) {
				nfc.setUseInheritedDesktopAppTriggerSetting(inheritAllowDesktopAppToTriggerSync);
			}
			if(logger.isDebugEnabled())
				logger.debug("Creating new net folder config object " + nfc.toString());
			netFolderConfig = (NetFolderConfig) getTransactionTemplate().execute(new TransactionCallback<Object>() {
	        	@Override
				public Object doInTransaction(final TransactionStatus status) {
	        		getCoreDao().save(nfc);
					return nfc;
	        	}
	        });
    	}
    	catch(Exception e) {
    		// Something went wrong, which implies that the creation of the net folder config object
    		// didn't go as planned. In this case, we need to clean up by deleting the net folder top
    		// that was created in the previous step. Otherwise, subsequent attempt to recreate the
    		// net folder will fail because the name is already taken in the folder name space.
    		logger.error("Failed to create net folder config object with name [" + name + "]. Cleaning up associated net folder top with id=" + folder.getId());
    		getBinderModule().deleteBinder(folder.getId());
    		throw e; // Rethrow
    	}

    	// Modify the folder with the additional net folder config information.
    	getFolderModule().modifyNetFolder(netFolderConfig, folder.getId());
    	
    	// Synchronize the top of the net folder
    	getFolderModule().syncAclForNetFolderRoot(folder);

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
        	logger.debug("Updating folder object (id=" + netFolderConfig.getTopFolderId() + ")");
        getFolderModule().modifyNetFolder(netFolderConfig, netFolderConfig.getTopFolderId());
    }
    
	@Override
    public void deleteNetFolder(Long netFolderTopId, boolean deleteSource) {
		Binder binder;
		
		try {
			binder = getCoreDao().loadBinder(netFolderTopId, RequestContextHolder.getRequestContext().getZoneId());
		}
		catch(NoBinderByTheIdException e) {
			logger.info("Net folder top folder (id=" + netFolderTopId + ") not found. Nothing to delete.");
			return;
		}
		
        logger.info("Deleting net folder top folder [" + binder.getPathName() + "] (id=" + netFolderTopId + ") and everything in it");
        getFolderModule().deleteNetFolder(netFolderTopId, deleteSource);

		// If still here, it means that the deletion of the folder was successful.
        // Now go ahead and delete the net folder config object associated with it.
        		
		NetFolderConfig nfc;
		try {
	    	nfc = binder.getNetFolderConfig();
		}
		catch(NoNetFolderConfigByTheIdException e) {
			logger.info("Net folder config (id=" + binder.getNetFolderConfigId() + ") not found. Nothing more to delete.");
			return;
		}

		if(nfc == null) {
			logger.warn("Net folder top folder (id=" + netFolderTopId + ") is missing net folder config ID. Ignoring.");
			return;
		}
		
    	logger.info("Deleting net folder config [" + nfc.getName() + "] (id=" + nfc.getId() + ")");
    	final NetFolderConfig nfcRef = nfc;
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		getCoreDao().delete(nfcRef);
				return null;
        	}
        });
    }
    	
	protected FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}
	
	protected BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}
	
	protected TemplateModule getTemplateModule() {
		return (TemplateModule) SpringContextUtil.getBean("templateModule");
	}
	
	protected TransactionTemplate getTransactionTemplate() {
		return (TransactionTemplate) SpringContextUtil.getBean("transactionTemplate");
	}
}
