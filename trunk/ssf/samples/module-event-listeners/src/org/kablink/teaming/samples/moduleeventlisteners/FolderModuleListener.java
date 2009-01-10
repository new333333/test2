/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.samples.moduleeventlisteners;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.InputDataAccessor;

public class FolderModuleListener {

	protected Log logger = LogFactory.getLog(getClass());
	
	
	/****************** Listener for AddEntry method ********************/
	
	public void preAddEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options) {
		logger.info("preAddEntry: About to add an entry to the folder " + folderId + " with definition " + definitionId);
	}
	
	public void postAddEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options, FolderEntry entry) {
		// This method illustrates accepting the result of the module event 
		// as an object (FolderEntry). 
		
		logger.info("postAddEntry: A new entry is created with ID " + entry.getId());
		
		// Figure out who added this entry.
		String userName = RequestContextHolder.getRequestContext().getUserName();
		
		// Given the user name and the newly added entry, we can do something useful
		// here. For example, making a web services call to a remote system to 
		// synchronize the data, or posting this data to another web site, etc. 
	}
	public void afterCompletionAddEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options, Throwable ex) {
		logger.info("afterCompletionAddEntry: " + ((ex==null)? "Successful" : ex.toString()));
	}
	
	
	/****************** Listener for ReserveEntry method ********************/
	
	public boolean preReserveEntry(Long folderId, Long entryId) {
		// Get the name of the user who is requesting to reserve this entry.
		String userName = RequestContextHolder.getRequestContext().getUserName();
		
		// If the requestor is not me (ie, admin) and the ID of the entry that 
		// the user is trying to reserve is 12, then keep the request from
		// proceeding by returning false from here. The entry 12 means so much
		// to me that I do not want anyone but me to be able to reserve it!
		if(!userName.equals("admin") && entryId.longValue() == 12) {
			logger.info("preReserveEntry: No, you can't do this");
			return false;
		}
		else {
			logger.info("preReserveEntry: OK, that's fine");
			return true;
		}
	}
	public void postReserveEntry(Long folderId, Long entryId) {
		// This method does not append an extra argument because the corresponding 
		// module method (reserveEntry) does not return anything (ie, void type). 
		
		logger.info("postReserveEntry");
	}
	public void afterCompletionReserveEntry(Long folderId, Long entryId, Throwable ex) {
		logger.info("afterCompletionReserveEntry");
	}

	
	/****************** Listener for TestAccess method ********************/
	
	public void preTestAccess(Folder folder, FolderOperation operation) {
		logger.info("preTestAccess");

	}
	public void postTestAccess(Folder folder, FolderOperation operation, boolean testResult) {
		// This method illustrates accepting the result of the module event 
		// as a primitive type (boolean).
	
		logger.info("postTestAccess");
	}
	public void afterCompletionTestAccess(Folder folder, FolderOperation operation, Throwable ex) {
		logger.info("afterCompletionTestAccess");
	}

}
