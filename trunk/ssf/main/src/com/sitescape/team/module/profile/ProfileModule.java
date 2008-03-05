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
/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.team.module.profile;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import org.dom4j.Document;

import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.ApplicationGroup;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.GroupPrincipal;
import com.sitescape.team.domain.IndividualPrincipal;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;

public interface ProfileModule {
	public enum ProfileOperation {
		addEntry,
		deleteEntry,
		modifyEntry
	}
	/**
	 * Add profile entries. 
	 * @param binderId
	 * @param doc
	 * @param options - additional processing options or null (See ObjectKeys.INPUT_OPTION_DELETE_USE_WORKSPACE)
	 * @throws AccessControlException
	 */
	 public void addEntries(Long binderId, Document doc, Map options) throws AccessControlException;
	 /**
	  * Add a new group
	  * @param binderId
	  * @param definitionId
	  * @param inputData
	  * @param fileItems - may be null
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  * @throws WriteFilesException
	  */
	 public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException;
	 /**
	  * Add a new user
	  * @param binderId
	  * @param definitionId
	  * @param inputData
	  * @param fileItems - may be null
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  * @throws WriteFilesException
	  */
	 public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException;
	 /**
	  * Add a user as a member of a group.  If username is <code>not null</code> then find the user by name,
	  * otherwise find the user by id
	  * @param userId Id of user or null if username supplied
	  * @param username Login name of user
	  * @param groupId Id of group to add user to
	  */
	 public void addUserToGroup(Long userId, String username, Long groupId);
	 
	 /**
	  * Add a user workspace from the user workspace template
	  * @param user
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  */
	 public Workspace addUserWorkspace(User user, Map options)
	 	throws AccessControlException;
	/**
	 * Create an user from information from the portal.
	 * 
	 * @param userName 
	 * @param password may be null
	 * @param updates may be null
	 * @param options - additional processing options or null
	 * @return created user object
	 */
	public User addUserFromPortal(String userName, String password, Map updates, Map options);
	/**
	 * Check access to a binder, throwing an exception if access is denied
	 * @param binder
	 * @param operation
	 * @throws AccessControlException
	 */
	public void checkAccess(ProfileBinder binder, ProfileOperation operation) throws AccessControlException;
	/**
	 * Check access to a principal, throwing an exception if access is denied
	 * @param entry
	 * @param operation
	 * @throws AccessControlException
	 */
	public void checkAccess(Principal entry, ProfileOperation operation) throws AccessControlException;
	/**
	 * Delete an entry.  If the entry is a user, will not delete the user workspace by default
	 * @param binderId
	 * @param id - group or use id
	 * @param options - additional processing options or null (See ObjectKeys.INPUT_OPTION_DELETE_USE_WORKSPACE)
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public void deleteEntry(Long binderId, Long id, Map options)
		throws AccessControlException, WriteFilesException;
	/**
	 * Delete a user.
	 * @param userName
	 * @param options - additional processing options or null (See ObjectKeys.INPUT_OPTION_DELETE_USE_WORKSPACE)
	 */
	public void deleteUserByName(String userName,  Map options);
	/**
	 * Get a principal
	 * @param binderId
	 * @param userId
	 * @return
	 * @throws AccessControlException
	 */
	public Principal getEntry(Long binderId, Long userId)
		throws AccessControlException;
	/**
	 * Get the workspace for a user
	 * @param binderId
	 * @param principaId
	 * @return
	 */
	public Long getEntryWorkspaceId(Long binderId, Long principaId);
	/**
	 * Return search results for user groups
	 * @param binderId - id of profileBinder
	 * @return
	 * @throws AccessControlException
	 */
    public Map getGroups(Long binderId) 
    	throws AccessControlException;
    /**
     * Same as {@link getGroups(Long) getGroups} except additional search options may be supplied
     * @param binderId - id of profileBinder
     * @param searchOptions
     * @return
     * @throws AccessControlException
     */
    public Map getGroups(Long binderId, Map searchOptions)
    	throws AccessControlException;
    /**
     * Return set of user groups, sorted by title
     * @param groupIds
     * @return
     * @throws AccessControlException
     */
	public SortedSet<Group> getGroups(Collection<Long> groupIds)
		throws AccessControlException;
	/**
	 * Return set of principals, sorted by title
	 * @param ids
	 * @return
	 * @throws AccessControlException
	 */
 	public SortedSet<Principal> getPrincipals(Collection<Long> ids)
 		throws AccessControlException;
 	/**
 	 * Returnt the profileBinder
 	 * @return
 	 */
	public ProfileBinder getProfileBinder();
	/**
	 * Return general user properties
	 * @param userId - null for current user
	 * @return
	 */
    public UserProperties getUserProperties(Long userId);
    /**
     * Return user properties associated with specific binder
     * @param userId - null for current user
     * @param folderId
     * @return
     */
    public UserProperties getUserProperties(Long userId, Long folderId);
    /**
	 * Return search results for users
     * @param binderId - id of profileBinder
     * @return
     */
	public Map getUsers(Long binderId);
    /**
     * Same as {@link #getUsers(Long)} except additional search options may be supplied
     * 
     * @param binderId - id of profileBinder
     * @param searchOptions
     * @return
     */
	public Map getUsers(Long binderId, Map searchOptions);
	/**
	 * Return set of users, sorted by title
	 * @param ids
	 * @return
	 * @throws AccessControlException
	 */
    public SortedSet<User> getUsers(Collection<Long> userIds);
    public User findUserByName(String username) throws NoUserByTheNameException;
   
	/**
	 * Return a collection of user ids  They are either in the principal list
	 * or members of groups in the principal list.
	 * @param principalIds
	 * @return
	 */
	public SortedSet<User> getUsersFromPrincipals(Collection<Long> principalIds);
	/**
	 * Return seenMap
	 * @param userId - null for current user
	 * @return
	 */
    public SeenMap getUserSeenMap(Long userId);
    /**
     * Set status.  Sets the status of the current user
     * @param status
     */
    public void setStatus(String status);
    /**
     * Update index.  Should be handled by all other operations automatically
     * @param entry
     */
	public void indexEntry(Principal entry);
	/**
	 * Modify existing principal
	 * @param binderId
	 * @param id
	 * @param inputData
	 * @param fileItems - may be null
	 * @param deleteAttachments - may be null
	 * @param fileRenamesTo - may be null
	 * @param options - additional processing options or null 
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData, 
			   Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
			throws AccessControlException, WriteFilesException;
	/**
	 * Modify existing principal
	 * @param binderId
	 * @param id
	 * @param inputData
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
		throws AccessControlException, WriteFilesException;
	  
	/**
	 * Update user from information from the portal.
	 * 
	 * @param user
	 * @param updates
	 */
	public void modifyUserFromPortal(User user, Map updates, Map options);
	/**
	 * Set a property for a binder
	 * @param userId - null for current user
	 * @param binderId
	 * @param property
	 * @param value- null to remove value
	 * @return
	 */
    public UserProperties setUserProperty(Long userId, Long binderId, String property, Object value);
     /**
     * Refer to {@link #setUserProperty(Long, Long, String, Object) setUserProperty}
     * This is an optimization to set multiple properties in 1 transaction.
     * @param userId
     * @param binderId
     * @param values
     * @return
     */
    public UserProperties setUserProperties(Long userId, Long binderId, Map<String, Object> values);
    /**
     * Set a general user property.
     * @param userId - null for current user
     * @param property
     * @param value - null to remove value
     * @return
     */
    public UserProperties setUserProperty(Long userId, String property, Object value);
    /**
     * Refer to {@link #setUserProperty(Long, String, Object) setUserProperty}
     * This is an optimization to set multiple properties in 1 transaction.
     * @param userId
     * @param values
     * @return
     */
    public UserProperties setUserProperties(Long userId, Map<String, Object> values);
    /**
     * Mark an entry as seen
     * @param userId
     * @param entry
     */
    public void setSeen(Long userId, Entry entry);
    /**
     * Mark a collection of entries as seen.
     * @param userId
     * @param entries
     */
    public void setSeen(Long userId, Collection<Entry> entries);
    /**
     * Test access to a binder
     * @param binder
     * @param operation
     * @return
     */
	public boolean testAccess(ProfileBinder binder, ProfileOperation operation);
	/**
	 * Test access to an entry
	 * @param entry
	 * @param operation
	 * @return
	 */
	public boolean testAccess(Principal entry, ProfileOperation operation);

	 /**
	  * Add a new application group
	  * @param binderId
	  * @param definitionId
	  * @param inputData
	  * @param fileItems - may be null
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  * @throws WriteFilesException
	  */
	 public Long addApplicationGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException;
	 
	 /**
	  * Add a new application
	  * @param binderId
	  * @param definitionId
	  * @param inputData
	  * @param fileItems - may be null
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  * @throws WriteFilesException
	  */
	 public Long addApplication(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException;

	/**
	 * Return search results for application groups
	 * @param binderId - id of profileBinder
	 * @return
	 * @throws AccessControlException
	 */
	public Map getApplicationGroups(Long binderId)
			throws AccessControlException;

	/**
	 * Same as {@link getApplicationGroups(Long) getApplicationGroups} except additional search options may be supplied
	 * @param binderId - id of profileBinder
	 * @param searchOptions
	 * @return
	 * @throws AccessControlException
	 */
	public Map getApplicationGroups(Long binderId, Map searchOptions)
			throws AccessControlException;

	/**
	 * Return set of application groups, sorted by title
	 * @param groupIds
	 * @return
	 * @throws AccessControlException
	 */
	public SortedSet<ApplicationGroup> getApplicationGroups(
			Collection<Long> groupIds) throws AccessControlException;

	/**
	 * Return search results for group principals (includes both user groups and application groups)
	 * @param binderId - id of profileBinder
	 * @return
	 * @throws AccessControlException
	 */
    public Map getGroupPrincipals(Long binderId) 
    	throws AccessControlException;
    /**
     * Same as {@link getGroupPrincipals(Long) getGroupPrincipals} except additional search options may be supplied
     * @param binderId - id of profileBinder
     * @param searchOptions
     * @return
     * @throws AccessControlException
     */
    public Map getGroupPrincipals(Long binderId, Map searchOptions)
    	throws AccessControlException;
    /**
     * Return set of group principals, sorted by title (includes both user groups and application groups)
     * @param groupIds
     * @return
     * @throws AccessControlException
     */
	public SortedSet<GroupPrincipal> getGroupPrincipals(Collection<Long> groupIds)
		throws AccessControlException;

    /**
	 * Return search results for applications
     * @param binderId - id of profileBinder
     * @return
     */
	public Map getApplications(Long binderId);
    /**
     * Same as {@link #getApplications(Long)} except additional search options may be supplied
     * 
     * @param binderId - id of profileBinder
     * @param searchOptions
     * @return
     */
	public Map getApplications(Long binderId, Map searchOptions);
	/**
	 * Return set of applications, sorted by title
	 * @param ids
	 * @return
	 * @throws AccessControlException
	 */
    public SortedSet<Application> getApplications(Collection<Long> applicationIds);

    /**
	 * Return search results for individual principals (includes both users and applications)
     * @param binderId - id of profileBinder
     * @return
     */
	public Map getIndividualPrincipals(Long binderId);
    /**
     * Same as {@link #getIndividualPrincipals(Long)} except additional search options may be supplied
     * 
     * @param binderId - id of profileBinder
     * @param searchOptions
     * @return
     */
	public Map getIndividualPrincipals(Long binderId, Map searchOptions);
	/**
	 * Return set of individual principals, sorted by title (includes both users and applications)
	 * @param ids
	 * @return
	 * @throws AccessControlException
	 */
    public SortedSet<IndividualPrincipal> getIndividualPrincipals(Collection<Long> individualIds);

}
