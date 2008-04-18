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
package com.sitescape.team.remoting.ws.service.folder;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import com.sitescape.team.util.SpringContextUtil;

public class FolderServiceEndpoint implements ServiceLifecycle, FolderService {

	private FolderService folderService;

	protected FolderService getFolderService() {
		return folderService;
	}
	
	public long addFolderEntry(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return getFolderService().addFolderEntry(accessToken, binderId, definitionId, inputDataAsXML, attachedFileName);
	}

	public long addReply(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML) {
		return getFolderService().addReply(accessToken, binderId, parentId, definitionId, inputDataAsXML);
	}

	public String getFolderEntriesAsXML(String accessToken, long binderId) {
		return getFolderService().getFolderEntriesAsXML(accessToken, binderId);
	}

	public String getFolderEntryAsXML(String accessToken, long binderId, long entryId, boolean includeAttachments) {
		return getFolderService().getFolderEntryAsXML(accessToken, binderId, entryId, includeAttachments);
	}

	public void modifyFolderEntry(String accessToken, long binderId, long entryId, String inputDataAsXML) {
		getFolderService().modifyFolderEntry(accessToken, binderId, entryId, inputDataAsXML);
	}
	
	public void uploadFolderFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName) {
		getFolderService().uploadFolderFile(accessToken, binderId, entryId, fileUploadDataItemName, fileName);
	}

	public void uploadFolderFileStaged(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName, String stagedFileRelativePath) {
		getFolderService().uploadFolderFileStaged(accessToken, binderId, entryId, fileUploadDataItemName, fileName, stagedFileRelativePath);
	}

	public void addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId, String startState) {
		getFolderService().addEntryWorkflow(accessToken, binderId, entryId, definitionId, startState);
	}
	
	public void init(Object context) throws ServiceException {
		this.folderService = (FolderService) SpringContextUtil.getBean("folderService");
	}
	
	public void synchronizeMirroredFolder(String accessToken, long binderId) {
		getFolderService().synchronizeMirroredFolder(accessToken, binderId);
	}
	
	public void destroy() {
	}

}
