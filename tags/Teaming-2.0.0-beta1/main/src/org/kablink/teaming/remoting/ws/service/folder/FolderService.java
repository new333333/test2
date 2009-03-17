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
package org.kablink.teaming.remoting.ws.service.folder;

import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.FolderEntry;
import org.kablink.teaming.remoting.ws.model.FolderEntryCollection;
import org.kablink.teaming.remoting.ws.model.Subscription;
import org.kablink.teaming.remoting.ws.model.Tag;

public interface FolderService {

	public FolderEntryCollection folder_getEntries(String accessToken, long binderId);

	public FolderEntry folder_getEntry(String accessToken, long entryId, boolean includeAttachments);
	public FolderEntry folder_getEntryByFileName(String accessToken, long binderId, String fileName, boolean includeAttachments);
	public void folder_addEntryWorkflow(String accessToken, long entryId, String definitionId);
    public void folder_deleteEntryWorkflow(String accessToken, long entryId, String definitionId);
	public void folder_modifyWorkflowState(String accessToken, long entryId, long stateId, String toState);
	public void folder_setWorkflowResponse(String accessToken, long entryId, long stateId, String question, String response); 
	public void folder_removeFile(String accessToken, long entryId, String fileName);
	/**
	 * Returns information about the versions of the file. 
	 * Throws exception if the entry or the file does not exist.
	 * 
	 * @param accessToken
	 * @param entryId
	 * @param fileName
	 * @return
	 */
	public FileVersions folder_getFileVersions(String accessToken, long entryId, String fileName);
	public void folder_uploadFile(String accessToken, long entryId, 
			String fileUploadDataItemName, String fileName);
	public void folder_uploadFileStaged(String accessToken, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath);
	public void folder_synchronizeMirroredFolder(String accessToken, long binderId);
	
	public long folder_addEntry(String accessToken, FolderEntry entry, String attachedFileName);
	
	public void folder_modifyEntry(String accessToken, FolderEntry entry);
	
	public long folder_addReply(String accessToken, long parentEntryId, FolderEntry reply, String attachedFileName);
	
	public void folder_deleteEntry(String accessToken, long entryId);
    public long folder_copyEntry(String accessToken, long entryId, long destinationId);
    public void folder_moveEntry(String accessToken, long entryId, long destinationId); 
    public void folder_reserveEntry(String accessToken, long entryId);
    public void folder_unreserveEntry(String accessToken, long entryId);

    public Subscription folder_getSubscription(String accessToken, long entryId);
	public void folder_setSubscription(String accessToken, long entryId, Subscription subscription); 
	
	public void folder_deleteEntryTag(String accessToken, long entryId, String tagId); 
	public Tag[] folder_getEntryTags(String accessToken, long entryId);
	public void folder_setEntryTag(String accessToken, Tag tag);
	public void folder_setRating(String accessToken, long entryId, long value);
}
