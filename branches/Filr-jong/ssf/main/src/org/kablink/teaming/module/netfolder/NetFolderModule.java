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
package org.kablink.teaming.module.netfolder;

import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.NetFolderConfig.SyncScheduleOption;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.security.AccessControlException;

/**
 * @author jong
 *
 */
public interface NetFolderModule {
	
	/**
	 * Create a net folder from the supplied configuration settings.
	 * This creates a new <code>NetFolderConfig</code> object and persists it in the database.
	 * It also creates a new persistent <code>Folder</code> object representing the top of the net folder 
	 * hierarchy and these two pieces of information are linked together.
	 * 
	 * @param templateId
	 * @param parentBinderId
	 * @param name
	 * @param owner
	 * @param rootName
	 * @param path
	 * @param isHomeDir
	 * @param indexContent
	 * @param inheritIndexContent
	 * @param syncScheduleOption
	 * @param fullSyncDirOnly
	 * @return newly created net folder config object
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 * @throws WriteEntryDataException
	 */
    public NetFolderConfig createNetFolder(Long templateId, Long parentBinderId, String name, User owner, String rootName, String path, Boolean isHomeDir, boolean indexContent, Boolean inheritIndexContent, SyncScheduleOption syncScheduleOption, Boolean fullSyncDirOnly ) throws AccessControlException, WriteFilesException, WriteEntryDataException;
	
    /**
     * Persists the net folder config object containing modified configuration settings.
     * 
     * @param netFolderConfig
     */
    public void modifyNetFolder(NetFolderConfig netFolderConfig);
	
    /**
     * Delete the net folder. This deletes BOTH the configuration/definition of the net folder AND the entire data associated with it.
     * 
     * @param netFolderConfigId
     */
    public void deleteNetFolder(Long netFolderConfigId, boolean deleteSource);
    
	/**
	 * Returns the current <code>FolderEntry</code> associated with the specified
	 * file in the specified net folder or, if there is no current entry and
	 * <code>create</code> is true, returns a new persistent entry object.
	 * 
	 * If <code>create</code> is <code>false</code> and the system has no corresponding
	 * <code>FolderEntry</code> for the file, this method returns <code>null</code>.
	 * 
	 * Creating a new persistent entry object is an expensive operation, and 
	 * should be used ONLY WHEN it is absolutely necessary (e.g. to share the file
	 * or to comment on the file).
	 * 
	 * @param netFolderConfigId
	 * @param filePath
	 * @param create
	 * @return
	 */
	public FolderEntry obtainFolderEntry(Long netFolderConfigId, String filePath, boolean create);
	
	/**
	 * Returns the current <code>Folder</code> associated with the specified
	 * folder in the specified net folder or, if there is no current folder object
	 * and <code>create</code> is true, returns a new persistent folder object.
	 * 
	 * If <code>create</code> is <code>false</code> and the system has no corresponding
	 * <code>Folder</code> for the folder, this method returns <code>null</code>.
	 * 
	 * Creating a new persistent folder object is an expensive operation, and 
	 * should be used ONLY WHEN it is absolutely necessary (e.g. to share the folder).
	 * 
	 * @param netFolderConfigId
	 * @param folderPath
	 * @param create
	 * @return
	 */
	public Folder obtainFolder(Long netFolderConfigId, String folderPath, boolean create);

}
