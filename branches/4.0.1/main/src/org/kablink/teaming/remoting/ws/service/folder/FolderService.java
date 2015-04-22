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
package org.kablink.teaming.remoting.ws.service.folder;

import java.util.Calendar;

import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.ws.model.Description;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.FolderEntry;
import org.kablink.teaming.remoting.ws.model.FolderEntryCollection;
import org.kablink.teaming.remoting.ws.model.Subscription;
import org.kablink.teaming.remoting.ws.model.Tag;

public interface FolderService {

	public FolderEntryCollection folder_getEntries(String accessToken, long binderId, int firstRecord, int maxRecords);

	public FolderEntry folder_getEntry(String accessToken, long entryId, boolean includeAttachments, boolean eventAsIcalString);
	public FolderEntry folder_getEntryByFileName(String accessToken, long binderId, String fileName, boolean includeAttachments, boolean eventAsIcalString);
	public byte[] folder_getEntryAsMime(String accessToken, long entryId, boolean includeAttachments);
	public void folder_addEntryWorkflow(String accessToken, long entryId, String definitionId);
    public void folder_deleteEntryWorkflow(String accessToken, long entryId, String definitionId);
	public void folder_modifyWorkflowState(String accessToken, long entryId, long stateId, String toState);
	public void folder_setWorkflowResponse(String accessToken, long entryId, long stateId, String question, String response); 
	public void folder_removeFile(String accessToken, long entryId, String fileName);
	public void folder_removeAttachment(String accessToken, long entryId, String attachmentId);
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
	public FileVersions folder_getFileVersionsFromAttachment(String accessToken, long entryId, String attachmentId);
	public void folder_uploadFile(String accessToken, long entryId, 
			String fileUploadDataItemName, String fileName);
	public void folder_uploadFileAsByteArray(String accessToken, long entryId, 
			String fileUploadDataItemName, String fileName, byte[] fileContent);
	public String folder_validateUploadFile(String accessToken,long entryId, String fileName, long fileSize);
	public void folder_uploadAttachmentAsByteArray(String accessToken, long entryId, 
			String fileUploadDataItemName, String attachmentId, byte[] fileContent);
	public String folder_validateUploadAttachment(String accessToken,
			long entryId, String attachmentId, long fileSize);
	public boolean folder_uploadAttachmentAsByteArrayConditional(String accessToken, long entryId, 
			String fileUploadDataItemName, String attachmentId, byte[] fileContent,
			Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber);
	public String folder_validateUploadAttachmentConditional(String accessToken,
			long entryId, String attachmentId, long fileSize,
			Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber);
	public void folder_uploadFileStaged(String accessToken, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath);
	public void folder_synchronizeMirroredFolder(String accessToken, long binderId);
	
	public long folder_addEntry(String accessToken, FolderEntry entry, String attachedFileName);
	
	public long folder_addEntryAsMime(String accessToken, long binderId, byte[] mimeData);
	
	public Long folder_addMicroBlog(String accessToken, String text);
	
	public Calendar folder_modifyEntry(String accessToken, FolderEntry entry);
	
	public long folder_addReply(String accessToken, long parentEntryId, FolderEntry reply, String attachedFileName);
	
	public void folder_deleteEntry(String accessToken, long entryId);
	public void folder_preDeleteEntry(String accessToken, long entryId);
	public void folder_restoreEntry(String accessToken, long entryId);
    public long folder_copyEntry(String accessToken, long entryId, long destinationId) throws WriteFilesException;
    public void folder_moveEntry(String accessToken, long entryId, long destinationId) throws WriteFilesException; 
    public void folder_reserveEntry(String accessToken, long entryId);
    public void folder_unreserveEntry(String accessToken, long entryId);

    public Subscription folder_getSubscription(String accessToken, long entryId);
	public void folder_setSubscription(String accessToken, long entryId, Subscription subscription); 
	
	public void folder_deleteEntryTag(String accessToken, long entryId, String tagId); 
	public Tag[] folder_getEntryTags(String accessToken, long entryId);
	public void folder_setEntryTag(String accessToken, Tag tag);
	public void folder_setRating(String accessToken, long entryId, long value);
	public byte[] folder_getAttachmentAsByteArray(String accessToken, long entryId, String attachmentId);
	public byte[] folder_getFileVersionAsByteArray(String accessToken, long entryId, String fileVersionId);

	/**
	 * Return a list of IDs of the entries of the specific family type that have been added or updated between the start and end times.
	 * 
	 * @param accessToken
	 * @param family string representing a family; if null, the match is performed irrespective of family type 
	 * @param startTime the start time, inclusive; this argument is optional
	 * @param endTime the end time, exclusive; this argument is required
	 * @return
	 */
	public long[] folder_getCreatedOrUpdatedEntries(String accessToken, String family, Calendar startTime, Calendar endTime);
	
	/**
	 * Return a list of IDs of the entries of the specific family type that have been deleted between the start and end times.
	 * 
	 * @param accessToken
	 * @param family string representing a family; if null, the match is performed irrespective of family type 
	 * @param startTime the start time, inclusive; this argument is optional
	 * @param endTime the end time, exclusive; this argument is required
	 * @return
	 */
	public long[] folder_getDeletedEntries(String accessToken, String family, Calendar startTime, Calendar endTime);

	/**
	 * Return a list of IDs of the entries that have been deleted or pre-deleted between the start and end times 
	 * and whose parent folder is in the specified folder IDs.
	 * 
	 * @param accessToken
	 * @param folderIds a list of parent folder IDs; if null or empty, the match is not confined by parent folder
	 * @param family string representing a family; if null, the match is performed irrespective of family type 
	 * @param startTime the start time, inclusive; this argument is optional
	 * @param endTime the end time, exclusive; this argument is required
	 * @return
	 */
	public long[] folder_getDeletedEntriesInFolders(String accessToken, long[] folderIds, String family, Calendar startTime, Calendar endTime);

	/**
	 * Return a list of IDs of the entries that have been restored between the start and end times 
	 * and whose parent folder is in the specified folder IDs.
	 * 
	 * @param accessToken
	 * @param folderIds a list of parent folder IDs; if null or empty, the match is not confined by parent folder
	 * @param family string representing a family; if null, the match is performed irrespective of family type 
	 * @param startTime the start time, inclusive; this argument is optional
	 * @param endTime the end time, exclusive; this argument is required
	 * @return
	 */
	public long[] folder_getRestoredEntriesInFolders(String accessToken, long[] folderIds, String family, Calendar startTime, Calendar endTime);

	/**
	 * Return a list of IDs of the entries that have been moved from one folder to another
	 * between the start and end times.
	 * 
	 * @param accessToken
	 * @param startTime the start time, inclusive; this argument is optional
	 * @param endTime the end time, exclusive; this argument is required
	 * @return
	 */
	public long[] folder_getMovedEntries(String accessToken, Calendar startTime, Calendar endTime);

	/**
	 * Test if the calling user has the right to execute the specified operation on each of the folders specified.
	 * 
	 * <p>If an folder does not exist, the result will be set to <code>false</code> for that specific folder.
	 * If the operation name is an unknown value in Teaming, the result will be set to <code>false</code>
	 * for all entries.
	 * 
	 * @param accessToken Either the security token passed to your application by Teaming as part of
	 * implementing a remote application, or the null value.
	 * @param operationName The string name of a {@link org.kablink.teaming.module.folder.FolderModule.FolderOperation FolderOperation}
	 * instance. See the Java source file for the names.
	 * @param folderIds The ID of the entries against which to test the access.
	 */
	public boolean[] folder_testFolderOperation(String accessToken, String operationName, long[] folderIds);

	/**
	 * Test if the calling user has the right to execute each operation on the specified folder.
	 * 
	 * <p>If the folder does not exist, the result will be set to <code>false</code> for all operations.
	 * If the operation name is an unknown value in Teaming, the result will be set to <code>false</code>
	 * for that operation.
	 * 
	 * @param accessToken Either the security token passed to your application by Teaming as part of
	 * implementing a remote application, or the null value.
	 * @param operationNames The string names of {@link org.kablink.teaming.module.folder.FolderModule.FolderOperation FolderOperation}
	 * instances. See the Java source file for the names.
	 * @param folderId The ID of the entry against which to test the access.
	 */
	public boolean[] folder_testFolderOperations(String accessToken, String[] operationNames, long folderId);

	/**
	 * Test if the calling user has the right to execute the specified operation on each of the entries specified.
	 * 
	 * <p>If an entry does not exist, the result will be set to <code>false</code> for that specific entry.
	 * If the operation name is an unknown value in Teaming, the result will be set to <code>false</code>
	 * for all entries.
	 * 
	 * @param accessToken Either the security token passed to your application by Teaming as part of
	 * implementing a remote application, or the null value.
	 * @param operationName The string name of a {@link org.kablink.teaming.module.folder.FolderModule.FolderOperation FolderOperation}
	 * instance. See the Java source file for the names.
	 * @param entryIds The ID of the entries against which to test the access.
	 */
	public boolean[] folder_testEntryOperation(String accessToken, String operationName, long[] entryIds);

	/**
	 * Test if the calling user has the right to execute the specified operation on each of the entries specified.
	 * 
	 * <p>If the entry does not exist, the result will be set to <code>false</code> for all operations.
	 * If the operation name is an unknown value in Teaming, the result will be set to <code>false</code>
	 * for that operation.
	 * 
	 * @param accessToken Either the security token passed to your application by Teaming as part of
	 * implementing a remote application, or the null value.
	 * @param operationNames The string names of {@link org.kablink.teaming.module.folder.FolderModule.FolderOperation FolderOperation}
	 * instances. See the Java source file for the names.
	 * @param entryId The ID of the entry against which to test the access.
	 */
	public boolean[] folder_testEntryOperations(String accessToken, String[] operationNames, long entryId);

	/**
	 * Copy current version to a new one and set its major version number to be one higher than the
	 * major version number of the current one and its minor version number to zero. 
	 * This operation takes as input not the ID of the current file version but the ID of the file attachment.
	 * 
	 * @param accessToken
	 * @param entryId ID of the entry the file is attached to
	 * @param attachmentId ID of the file attachment
	 */
	public void folder_incrementFileMajorVersion(String accessToken, long entryId, String attachmentId);
	
	/**
	 * Set note/comment for the file version.
	 * 
	 * @param accessToken
	 * @param entryId ID of the entry the file is attached to
	 * @param fileVersionId ID of the file version
	 * @param note note text. This can contain html markup tags (e.g. <p>Adding <strong>important</strong> note</p>)
	 */
	public void folder_setFileVersionNote(String accessToken, long entryId, String fileVersionId, String note);
	
	/**
	 * Copy a previous version to a new one and set its minor version number to be one higher than
	 * the minor version number of the one being copied and its major version number to the same value. 
	 * The newly created version becomes current version.
	 * This operation is valid only on a version that is not current version.
	 *  
	 * @param accessToken
	 * @param entryId ID of the entry the file is attached to
	 * @param fileVersionId ID of the file version. This must not be current version.
	 */
	public void folder_promoteFileVersionCurrent(String accessToken, long entryId, String fileVersionId);
	
	/**
	 * Delete a file version.
	 * 
	 * @param accessToken
	 * @param entryId ID of the entry the file is attached to
	 * @param fileVersionId ID of the file version. This must not be current version.
	 */
	public void folder_deleteFileVersion(String accessToken, long entryId, String fileVersionId);
	
	/**
	 * Set status for a file version.
	 * 
	 * @param accessToken
	 * @param entryId ID of the entry the file is attached to
	 * @param fileVersionId ID of the file version. This must not be current version.
	 * @param status status code<br>
	 * 0 - no status
	 * 1 - official
	 * 2 - draft
	 * 3 - obsolete
	 */
	public void folder_setFileVersionStatus(String accessToken, long entryId, String fileVersionId, int status);

}
