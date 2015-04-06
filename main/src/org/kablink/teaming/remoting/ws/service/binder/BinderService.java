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
package org.kablink.teaming.remoting.ws.service.binder;

import org.kablink.teaming.remoting.ws.model.Binder;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.FolderCollection;
import org.kablink.teaming.remoting.ws.model.FunctionMembership;
import org.kablink.teaming.remoting.ws.model.Subscription;
import org.kablink.teaming.remoting.ws.model.Tag;
import org.kablink.teaming.remoting.ws.model.TeamMemberCollection;
import org.kablink.teaming.remoting.ws.model.TrashCollection;

public interface BinderService {
	/**
	 * Add a new binder, without using a template
	 * @param accessToken
	 * @param binder
	 * @return
	 */
	public long binder_addBinder(String accessToken, Binder binder);
	public long binder_copyBinder(String accessToken, long sourceId, long destinationId, boolean cascade);
	public void binder_deleteBinder(String accessToken, long binderId, boolean deleteMirroredSource); 
	public void binder_preDeleteBinder(String accessToken, long binderId); 
	public void binder_restoreBinder(String accessToken, long binderId); 
	public Binder binder_getBinder(String accessToken, long binderId, boolean includeAttachments);
	public Binder binder_getBinderByPathName(String accessToken, String pathName, boolean includeAttachments);
	public void binder_moveBinder(String accessToken, long binderId, long destinationId);
	public void binder_modifyBinder(String accessToken, Binder binder);
	public void binder_uploadFile(String accessToken, long binderId, String fileUploadDataItemName, String fileName);
	public void binder_uploadFileAsByteArray(String accessToken, long binderId, String fileUploadDataItemName, String fileName, byte[] fileContent);
	public void binder_removeFile(String accessToken, long binderId, String fileName);
	/**
	 * Returns information about the versions of the file. 
	 * Throws exception if the binder or the file does not exist.
	 * 
	 * @param accessToken
	 * @param binderId
	 * @param fileName
	 * @return
	 */
	public FileVersions binder_getFileVersions(String accessToken, long binderId, String fileName);

	public void binder_deleteTag(String accessToken, long binderId, String tagId); 
	public Tag[] binder_getTags(String accessToken, long binderId);
	public void binder_setTag(String accessToken, Tag tag);

	public void binder_indexBinder(String accessToken, long binderId);
    public Long[] binder_indexTree(String accessToken, long binderId);

	/**
	 * 
	 * @param accessToken
	 * @param binderId
	 * @param definitionIds
	 * @param workflowAssociations <Pairs of entryDefinitionId,workflowDefinitionId>
	 */
	public void binder_setDefinitions(String accessToken, long binderId, String[] definitionIds, String[] workflowAssociations);
	public void binder_setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit);
	public void binder_setOwner(String accessToken, long binderId, long userId);
	
	
	public FunctionMembership[] binder_getFunctionMembership(String accessToken, long binderId);
	public void binder_setFunctionMembership(String accessToken, long binderId, FunctionMembership[] functionMemberships);

	public TeamMemberCollection binder_getTeamMembers(String accessToken, long binderId, boolean explodeGroups, int firstRecord, int maxRecords);	
	public void binder_setTeamMembers(String accessToken, long binderId, String[] memberNames);

	public Subscription binder_getSubscription(String accessToken, long binderId); 
	public void binder_setSubscription(String accessToken, long binderId, Subscription subscription); 
	public FolderCollection binder_getFolders(String accessToken, long binderId, int firstRecord, int maxRecords);
	public FolderCollection binder_getAllFoldersOfMatchingFamily(String accessToken, long[] startingBinderIds, String[] families, boolean restrictByTeamMembership, int firstRecord, int maxRecords);
	public TrashCollection binder_getTrashEntries(String accessToken, long binderId, int firstRecord, int maxRecords);
	public byte[] binder_getAttachmentAsByteArray(String accessToken, long binderId, String attachmentId);

	/**
	 * Test if the calling user has the specified access right on each of the binders specified.
	 * 
	 * <p>If a binder does not exist, the result will be set to <code>false</code> for that specific binder.
	 * If the access right is an unknown value in Teaming, the result will be set to <code>false</code>
	 * for all binders.
	 * 
	 * @param accessToken Either the security token passed to your application by Teaming as part of
	 * implementing a remote application, or the null value.
	 * @param workAreaOperationName The string name of a {@link org.kablink.teaming.security.function.WorkAreaOperation WorkAreaOperation}
	 * instance. See the Java source file for the names.
	 * @param binderIds The ID of the binders against which to test the access.
	 */
	public boolean[] binder_testAccess(String accessToken, String workAreaOperationName, long[] binderIds);

	/**
	 * Test if the calling user has the right to execute the specified operation on each of the binders specified.
	 * 
	 * <p>If a binder does not exist, the result will be set to <code>false</code> for that specific binder.
	 * If the operation name is an unknown value in Teaming, the result will be set to <code>false</code>
	 * for all binders.
	 * 
	 * @param accessToken Either the security token passed to your application by Teaming as part of
	 * implementing a remote application, or the null value.
	 * @param binderOperationName The string name of a {@link org.kablink.teaming.module.binder.BinderModule.BinderOperation BinderOperation}
	 * instance. See the Java source file for the names.
	 * @param binderIds The ID of the binders against which to test the access.
	 */
	public boolean[] binder_testOperation(String accessToken, String binderOperationName, long[] binderIds);

	/**
	 * Test if the calling user has the right to execute each operation on the specified binder.
	 * 
	 * <p>If the binder does not exist, the result will be set to <code>false</code> for all operations.
	 * If the operation name is an unknown value in Teaming, the result will be set to <code>false</code>
	 * for that operation.
	 * 
	 * @param accessToken Either the security token passed to your application by Teaming as part of
	 * implementing a remote application, or the null value.
	 * @param binderOperationNames The string names of {@link org.kablink.teaming.module.binder.BinderModule.BinderOperation BinderOperation}
	 * instances. See the Java source file for the names.
	 * @param binderId The ID of the binder against which to test the access.
	 */
	public boolean[] binder_testOperations(String accessToken, String[] binderOperationNames, long binderId);

	public long binder_getTopWorkspaceId(String accessToken);
	
    public void binder_setDefinitionsInherited(String accessToken, long binderId, boolean inheritFromParent);

    public int binder_checkQuotaAndFileSizeLimit(String accessToken, Long userId, long binderId, long fileSize, String fileName);

}