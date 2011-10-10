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
package org.kablink.teaming.remoting.ws.service.profile;

import org.kablink.teaming.remoting.ws.model.BinderBrief;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.Group;
import org.kablink.teaming.remoting.ws.model.GroupCollection;
import org.kablink.teaming.remoting.ws.model.PrincipalCollection;
import org.kablink.teaming.remoting.ws.model.User;
import org.kablink.teaming.remoting.ws.model.UserCollection;

public interface ProfileService {

	public PrincipalCollection profile_getPrincipals(String accessToken, int firstRecord, int maxRecords);
	
	public UserCollection profile_getUsers(String accessToken, Boolean captive, int firstRecord, int maxRecords);

	public User profile_getUser(String accessToken, long userId, boolean includeAttachments);
	public User profile_getUserByName(String accessToken, String userName, boolean includeAttachments);
	/**
	 * Return an array of User objects that match the specified email address.
	 * 
	 * @param accessToken
	 * @param emailAddress email address to match on, required
	 * @param emailType optional email type - could be one of the following values - "_primary", "_mobile", "_text", "_bcc"
	 * If empty string or null, the match will be performed on all email types, that is, regardless of email type 
	 * @return
	 */
	public User[] profile_getUsersByEmail(String accessToken, String emailAddress, String emailType);

	public Group profile_getGroup(String accessToken, long groupId, boolean includeAttachments);
	public Group profile_getGroupByName(String accessToken, String groupName, boolean includeAttachments);
	
	public void profile_addGroupMember(String accessToken, String groupName, String username);
	public void profile_removeGroupMember(String accessToken, String groupName, String username);
	public PrincipalCollection profile_getGroupMembers(String accessToken, String groupName, int firstRecord, int maxRecords);
	public void profile_deletePrincipal(String accessToken, long principalId, boolean deleteWorkspace);
	
	public long profile_addUserWorkspace(String accessToken, long userId);
	
	public long profile_addUser(String accessToken, User user, String password);
	
	public long profile_addGroup(String accessToken, Group group);
		
	public void profile_modifyUser(String accessToken, User user);
	
	public void profile_modifyGroup(String accessToken, Group group);
	
	public void profile_removeFile(String accessToken, long principalId, String fileName);
	
	/**
	 * Returns information about the versions of the file. 
	 * Throws exception if the principal or the file does not exist.
	 * 
	 * @param accessToken
	 * @param principalId
	 * @param fileName
	 * @return
	 */
	public FileVersions profile_getFileVersions(String accessToken, long principalId, String fileName);
	
	public void profile_uploadFile(String accessToken, long principalId, String fileUploadDataItemName, String fileName);
	
	public void profile_uploadFileAsByteArray(String accessToken, long principalId, String fileUploadDataItemName, String fileName, byte[] fileContent);
	
	public void profile_changePassword(String accessToken, Long userId, String oldPassword, String newPassword);
	
	public byte[] profile_getAttachmentAsByteArray(String accessToken, long userId, String attachmentId);

	public BinderBrief[] profile_getFavorites(String accessToken);

	public GroupCollection profile_getUserGroups(String accessToken, long userId);
	
	public BinderBrief[] profile_getFollowedPlaces(String accessToken, Long userId, String[] families, Boolean library);

	/**
	 * Get a user's disk quota in bytes which is either specific, or max of group membership
	 * @param accessToken
	 * @param userId
	 * @return
	 */
	public long profile_getMaxUserQuota(String accessToken, Long userId);
}
