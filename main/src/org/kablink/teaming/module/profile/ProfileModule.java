/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
/*
 * Created on Nov 16, 2004
 *
 */
package org.kablink.teaming.module.profile;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.dom4j.Document;

import org.kablink.teaming.dao.util.GroupSelectSpec;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.IndividualPrincipal;
import org.kablink.teaming.domain.LimitedUserView;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.SharedEntity;
import org.kablink.teaming.domain.TeamInfo;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.PrincipalDesktopAppsConfig;
import org.kablink.teaming.util.PrincipalMobileAppsConfig;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public interface ProfileModule {
	public enum ProfileOperation {
		addEntry,
		deleteEntry,
		modifyEntry,
		manageEntries
	}
	/**
	 * Add profile entries. 
	 * @param doc
	 * @param options - additional processing options or null (See ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE)
	 * @throws AccessControlException
	 */
	public void addEntries(Document doc, Map options) throws AccessControlException;
	 /**
	  * Add a new group
	  * @param definitionId
	  * @param inputData
	  * @param fileItems - may be null
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  * @throws WriteFilesException
	  */
	 public Group addGroup(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException, WriteEntryDataException;
	 /**
	  * Add a new user
	  * @param definitionId
	  * @param inputData
	  * @param fileItems - may be null
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  * @throws WriteFilesException
	  */
	 public User addUser(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException, WriteEntryDataException;
	 
	 public User addUser(InputDataAccessor inputData)
				throws AccessControlException, WriteFilesException, WriteEntryDataException;
	 
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
	  * Add a user MiniBlog folder from the MiniBlog folder template
	  * @param user
	  * @return
	  * @throws AccessControlException
	  */
	 public Folder addUserMiniBlog(User entry) 
	 	throws AccessControlException;
	 
	 /**
	  * Sets a user MiniBlog folder from the MiniBlog folder template
	  * @param user
	  * @param folderId
	  * @throws AccessControlException
	  */
	 public Folder setUserMiniBlog(User entry, Long folderId) 
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
	public User addUserFromPortal(IdentityInfo identityInfo, String userName, String password, Map updates, Map options);

    public User findOrAddExternalUser(String emailAddress);

	/**
	 * Check access to a binder, throwing an exception if access is denied
	 * @param user
	 * @param binder
	 * @param operation
	 * @throws AccessControlException
	 */
	public void checkAccess( User user, ProfileBinder binder, ProfileOperation operation) throws AccessControlException;
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
	 * Disable an entry.  
	 * @param entryId - group or use id
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public void disableEntry( Long entryId, boolean disabled)
	throws AccessControlException;
	/**
	 * Delete an entry.  If the entry is a user, will not delete the user workspace by default
	 * @param entryId - group or use id
	 * @param options - additional processing options or null (See ObjectKeys.INPUT_OPTION_DELETE_USE_WORKSPACE)
	 * @throws AccessControlException
	 */
	public void deleteEntry( Long entryId, Map options)
	throws AccessControlException, WriteFilesException, WriteEntryDataException;
	/**
	 * Execute only the phase1 (synchronous part) of the {@link #deleteEntry(Long, Map)}
	 * method. 
	 * @param entryId
	 * @param options
	 * @param phase1Only
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 * @throws WriteEntryDataException
	 */
	public void deleteEntry( Long entryId, Map options, boolean phase1Only)
	throws AccessControlException, WriteFilesException, WriteEntryDataException;	
	/**
	 * Performs phase2 (asynchronous part) of deleting an entry. Must be called after
	 * calling {@link #deleteEntry(Long, Map, boolean)} one or more times and specifying
	 * to only do phase1.
	 */
	public void deleteEntryFinish();
	/**
	 * Delete a user.
	 * @param userName
	 * @param options - additional processing options or null (See ObjectKeys.INPUT_OPTION_DELETE_USE_WORKSPACE)
	 */
	public void deleteUserByName(String userName,  Map options);
	
	/**
	 * Determine if the user has "create entry" rights to the profile binder.
	 * @return
	 */
	public boolean doesGuestUserHaveAddRightsToProfileBinder();
	
	/**
	 * Get the guest user
	 * @return
	 */
	public User getGuestUser();
	
	/**
	 * Get a principal by name
	 * @param name
	 * @return
	 */
	public Principal getEntry(String name);
	/**
	 * Get a principal
	 * @param userId
	 * @return
	 * @throws AccessControlException
	 */
	public Principal getEntry(Long userId)                      throws AccessControlException;
	public Principal getEntry(Long userId, boolean checkAccess) throws AccessControlException;
	
	/**
	 * Get the workspace for a user
	 * @param principaId
	 * @return
	 */
	public Long getEntryWorkspaceId(Long principaId);
	/**
	 * Get a group by name
	 * @param name
	 * @return
	 */
	public Group getGroup(String name);
	/**
	 * Return search results for user groups
	 * @return
	 * @throws AccessControlException
	 */
    public Map getGroups() 
    	throws AccessControlException;
    
	/**
	 * Return a list of LDAP container groups.
	 * @return
	 * @throws AccessControlException
	 */
    public List<Group> getLdapContainerGroups();
    
    /**
     * Same as {@link getGroups(Long) getGroups} except additional search options may be supplied
     * @param searchOptions
     * @return
     * @throws AccessControlException
     */
    public Map getGroups(Map searchOptions)
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
	 * Return a list of groups that meet the given filter
	 */
	public List<Group> getGroups( GroupSelectSpec groupSelectSpec );
	
	/**
	 * Return List of Binders owned by users in the list
	 * @param ids
	 * @return
	 */
	public List reindexPersonalUserOwnedBinders(Set<Principal> userIds);
	
	/**
	 * Return set of principals, sorted by title
	 * 
	 * @param ids
	 * @param checkProfilesAccess
	 * 
	 * @return
	 * 
	 * @throws AccessControlException
	 */
 	public SortedSet<Principal> getPrincipals(Collection<Long> ids, boolean checkProfilesAccess) throws AccessControlException;
 	public SortedSet<Principal> getPrincipals(Collection<Long> ids)                              throws AccessControlException;
 	
 	/**
 	 * Search for all principals
 	 * @param binderId
 	 * @param searchOptions
 	 * @return
 	 * @throws AccessControlException
 	 */
	public Map getPrincipals(Map searchOptions) throws AccessControlException;
	/**
	 * Load principals using name
	 * @param names
	 * @return
	 * @throws AccessControlException
	 */
	public Collection<Principal> getPrincipalsByName(Collection<String> names) throws AccessControlException;
	
	/**
 	 * Return the profileBinder id
 	 * @return
 	 */
	public Long getProfileBinderId();
	/**
 	 * Return the profileBinder
 	 * @return
 	 */
	public ProfileBinder getProfileBinder();
	/**
 	 * Return the map of entry definitions
 	 *   Must have read or create access
 	 * @return
 	 */
	public Map getProfileBinderEntryDefsAsMap();
	
	/**
	 * Return collection
	 * @param userId
	 * @param after
	 * @return
	 */
    public List<SharedEntity> getShares(Long userId, Date after);
	/**
	 * Return general user properties
	 * @param userId - null for current user
	 * @return
	 */
    public UserProperties getUserProperties(Long userId);
    public UserProperties getUserProperties(Long userId, boolean checkActive);
    /**
     * Return user properties associated with specific binder
     * @param userId - null for current user
     * @param folderId
     * @return
     */
    public UserProperties getUserProperties(Long userId, Long folderId);
    
	/**
	 * Return general user properties
	 * @param groupId
	 * @return
	 */
    public UserProperties getGroupProperties(Long groupId);
    
    /**
     * Get user by name
     * @param name
     * @return
     */
    public User getUser(String name);
    
    /**
     * Get user by name even deleted or disabled users.
     * @param name
     * @return
     */
    public User getUserDeadOrAlive( String name );
    
    /**
     * Get user by id even deleted or disabled users.
     * @param name
     * @return
     */
    public User getUserDeadOrAlive( Long userId );
    
    /**
	 * Return search results for users
     * @return
     */
	public Map getUsers();
    /**
     * Same as {@link #getUsers(Long)} except additional search options may be supplied
     * 
     * @param searchOptions
     * @return
     */
	public Map getUsers(Map searchOptions);
	/**
	 * Return set of users, sorted by title
	 * @param ids
	 * @return
	 * @throws AccessControlException
	 */
    public SortedSet<User> getUsers(Collection<Long> userIds);
	/**
	 * Return set of limited user views.  No access check is performed.
	 * @param userIds
	 * @return
	 */
    public Set<LimitedUserView> getLimitedUserViews(Collection<Long> userIds);
    public LimitedUserView getLimitedUserView(Long userId);
    public User findUserByName(String username) throws NoUserByTheNameException;
    public User getReservedUser(String internalId) throws NoUserByTheNameException;
    public User getReservedUser(String internalId, Long zoneId) throws NoUserByTheNameException;
    public Collection<User> getReservedUsers(Collection<String> internalId) throws NoUserByTheNameException;
   
    /**
     * 
     * @param ldapGuid
     * @return
     * @throws NoUserByTheNameException
     */
    public User findUserByLdapGuid( String ldapGuid ) throws NoUserByTheNameException;
    
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
     * Update index.  Should be handled by all other operations automatically
     * @param entry
     */
	public IndexErrors indexEntry(Principal entry);
	
	/**
	 * Index all of the entries found in the given Collection.
	 * 
	 * @param entries
	 * @param skipFileContentIndexing
	 * 
	 * @return
	 */
	public IndexErrors indexEntries(Collection<Principal> entries);
	public IndexErrors indexEntries(Collection<Principal> entries, boolean skipFileContentIndexing);
	
	public MapInputData validateUserAttributes(Long userId, Map formData);
	
	/**
	 * Modify existing principal
	 * @param entryId
	 * @param inputData
	 * @param fileItems - may be null
	 * @param deleteAttachments - may be null
	 * @param fileRenamesTo - may be null
	 * @param options - additional processing options or null 
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 * @throws WriteEntryDataException
	 */
	public void modifyEntry(Long entryId, InputDataAccessor inputData, 
			   Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
			throws AccessControlException, WriteFilesException, WriteEntryDataException;
	/**
	 * Modify existing principal
	 * @param entryId
	 * @param inputData
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 * @throws WriteEntryDataException
	 */
	public void modifyEntry(Long entryId, InputDataAccessor inputData) 
		throws AccessControlException, WriteFilesException, WriteEntryDataException;
	  
	/**
	 * Update user from information from the portal.
	 * r
	 * @param user
	 * @param updates
	 */
	public void modifyUserFromPortal(Long userId, Map updates, Map options);
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
     * Set a general user property.
     * @param groupId
     * @param property
     * @param value - null to remove value
     * @return
     */
    public UserProperties setGroupProperty(Long groupId, String property, Object value);
    /**
     * Refer to {@link #setUserProperty(Long, String, Object) setUserProperty}
     * This is an optimization to set multiple properties in 1 transaction.
     * @param groupId
     * @param values
     * @return
     */
    public UserProperties setGroupProperties(Long groupId, Map<String, Object> values);
    
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
     * Mark a collection of entries as seen.
     * @param userId
     * @param entryIds
     */
    public void setSeenIds(Long userId, Collection<Long> entryIds);
    /**
     * Mark a collection of entryIds as seen.
     * @param userId
     * @param entryIds
     */
    public void setUnseen(Long userId, Collection<Long> entryIds);
    /**
     * Share an entry 
     * @param entity
     * @param principalIds
     * @param binderIds
     */
    public void setShares(DefinableEntity entity, Collection<Long> principalIds, Collection<Long> bindersIds);
    /**
     * Set status.  Sets the status of the current user
     * @param status
     */
    public void setStatus(String status);
    /**
     * Set status.  Sets the status of the current user
     * @param status
     */
    public void setStatusDate(Date statusDate);
    /**
     * Set the disk quota for the current user (in megabytes)
     * @param megabytes
     */
    public void setDiskQuota(long megabytes);   
    
    /**
     * Get the user specific disk quota for the current user (in megabytes)
     * @return
     */
    public long getDiskQuota();

    /**
     * Check if the user's disk quota is exceeded
     * @return
     */
    public boolean isDiskQuotaExceeded();

    /**
     * Check if the user's disk quota high water mark is exceeded
     * @return
     */
    public boolean isDiskQuotaHighWaterMarkExceeded();

    /**
     * Get a user's disk quota which is either specific, or max of group membership
     * @return
     */
    public long getMaxUserQuota();
    public long getMaxUserQuota(Long userId);
    /**
     * Set the diskquota value for a set of users (in megabytes)
	 * @param entryIds
	 * @param megabytes
     */
    public void setUserDiskQuotas(Collection<Long> entryIds, long megabytes);
    /**
     * Set the diskquota value for a set of groups (in megabytes)
	 * @param entryIds
	 * @param megabytes
     */
    public void adjustGroupDiskQuotas(Collection<Long> groupIds, long megabytes);
    public void setUserGroupDiskQuotas(Collection<Long> userIds, Group group);
    public void deleteUserGroupDiskQuotas(Collection<Long> userIds, Group group);
    
    /**
     * Set the fileSizeLimit value for a set of users (in megabytes)
	 * @param entryIds
	 * @param megabytes
     */
    public void setUserFileSizeLimits(Collection<Long> userIds, Long fileSizeLimit);
    /**
     * Set the fileSizeLimit value for a set of groups (in megabytes)
	 * @param entryIds
	 * @param megabytes
     */
    public void adjustGroupFileSizeLimits(Collection<Long> groupIds, Long fileSizeLimit);
    public void setUserGroupFileSizeLimits(Collection<Long> userIds, Group group);
    public void deleteUserGroupFileSizeLimits(Collection<Long> userIds, Group group);
    
    /**
     * Reset the diskspace usage value for all users
     */
    public void resetDiskUsage();

    /**
     *this returns non-zero quotas (any quota which has been set by the admin) 
     */
    public List getNonDefaultQuotas(String type);
    
    /**
     *this returns non-zero file size limits (any limit which has been set by the admin) 
     */
    public List getNonDefaultFileSizeLimits(String type);

    /**
     *This returns a list of all disabled user account ids 
     */
    public List<Long> getDisabledUserAccounts();

    /**
	 * Test access to a binder
	 * @param user
	 * @param binder
	 * @param operation
	 * @throws AccessControlException
	 */
	public boolean testAccess(User user, ProfileBinder binder, ProfileOperation operation) throws AccessControlException;
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
	 * Get application group by name
	 * @param name
	 * @return
	 */
	public ApplicationGroup getApplicationGroup(String name);
	/**
	  * Add a new application group
	  * @param definitionId
	  * @param inputData
	  * @param fileItems - may be null
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  * @throws WriteFilesException
	  * @throws WriteEntryDataException
	  */
	 public ApplicationGroup addApplicationGroup(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException, WriteEntryDataException;
	 
	 /**
	  * Add a new application
	  * @param definitionId
	  * @param inputData
	  * @param fileItems - may be null
	  * @param options - additional processing options or null
	  * @return
	  * @throws AccessControlException
	  * @throws WriteFilesException
	  * @throws WriteEntryDataException
	  */
	 public Application addApplication(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException, WriteEntryDataException;

	/**
	 * Return search results for application groups
	 * @return
	 * @throws AccessControlException
	 */
	public Map getApplicationGroups()
			throws AccessControlException;

	/**
	 * Same as {@link getApplicationGroups(Long) getApplicationGroups} except additional search options may be supplied
	 * @param searchOptions
	 * @return
	 * @throws AccessControlException
	 */
	public Map getApplicationGroups(Map searchOptions)
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
	 * @return
	 * @throws AccessControlException
	 */
    public Map getGroupPrincipals() 
    	throws AccessControlException;
    /**
     * Same as {@link getGroupPrincipals(Long) getGroupPrincipals} except additional search options may be supplied
     * @param searchOptions
     * @return
     * @throws AccessControlException
     */
    public Map getGroupPrincipals(Map searchOptions)
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
	 * Get application by name
	 * @param name
	 * @return
	 */
	public Application getApplication(String name);
    /**
	 * Return search results for applications
     * @return
     */
	public Map getApplications();
    /**
     * Same as {@link #getApplications(Long)} except additional search options may be supplied
     * 
     * @param searchOptions
     * @return
     */
	public Map getApplications(Map searchOptions);
	/**
	 * Return set of applications, sorted by title.
	 * If <code>ids</code> is <code>null</code>, it returns all applications.
	 * @param ids
	 * @return
	 * @throws AccessControlException
	 */
    public SortedSet<Application> getApplications(Collection<Long> applicationIds);

    /**
	 * Return search results for individual principals (includes both users and applications)
      * @return
     */
	public Map getIndividualPrincipals();
    /**
     * Same as {@link #getIndividualPrincipals(Long)} except additional search options may be supplied
     * 
     * @param searchOptions
     * @return
     */
	public Map getIndividualPrincipals(Map searchOptions);
	/**
	 * Return set of individual principals, sorted by title (includes both users and applications)
	 * @param ids
	 * @return
	 * @throws AccessControlException
	 */
    public SortedSet<IndividualPrincipal> getIndividualPrincipals(Collection<Long> individualIds);

    public void changePassword(Long userId, String oldPassword, String newPassword);
    public void changePassword(Long userId, String oldPassword, String newPassword, boolean validateAgainstPolicy);

    /**
     * Returns true if the logged in user must provide the current password of the specified user in
     * order to change the user's password.  Ordinary users need to provide the old password in order
     * to change their own, but super users do not.
     * @param userId
     * @return
     */
    public boolean mustSupplyOldPasswordToSetNewPassword(Long userId);
    
    /**
     * Returns a set of users matching the email, sorted by title
     * @param emailAddress email address, required
     * @param emailType email type, optional
     * @return
     */
    public SortedSet<User> getUsersByEmail(String emailAddress, String emailType);
    
    /**
     * Returns a string array of size two where the first element is the user's
     * name and the second element the user's decrypted password. The returned
     * information is used for authentication purpose.
     * <p> 
     * The username element will be <code>null</code> if the specified user
     * is not found in the system. The password element will be <code>null</code>
     * if the user has no password or the user's password is stored using 
     * asymmetric (i.e., irreversible) encryption algorithm.
     * The entire return value (array) will be null if (a) the specified user
     * is found in the system, and (b) the user is a LDAP provisioned user,
     * and (c) the system doesn't allow LDAP authentication for certain
     * authenticator types and the current authenticator type happens to be
     * one of them.
     * 
     * @param username
     * @return
     */
    public String[] getUsernameAndDecryptedPasswordForAuth(String username);
    
    /**
     * Returns a list of Groups that the user is a member of, either directly or indirectly.
     */
	public List<Group> getUserGroups(Long userId) throws AccessControlException;

    public List<Binder> getUserFavorites(Long userId) throws AccessControlException;

    public List<TeamInfo> getUserTeams(Long userId);
    
    public void setFirstLoginDate(Long userId);
    
    /**
     * Interacts with a user's last password changed date.
     */
    public void setLastPasswordChange(User user,   Date lastPasswordChange);
    public void setLastPasswordChange(Long userId, Date lastPasswordChange);

    /**
     * Interacts with a user's workspace pre-deleted flag.
     */
    public Boolean getUserWorkspacePreDeleted(Long userId);
    public void    setUserWorkspacePreDeleted(Long userId, boolean userWorkspacePreDeleted);

    /**
     * Interacts with a user or group's download enabled flag.
     */
    public Boolean getDownloadEnabled(Long upId);
    public void    setDownloadEnabled(Long upId, Boolean downloadEnabled);

    /**
     * Interacts with a user or group's web access enabled flag.
     */
    public Boolean getWebAccessEnabled(Long upId);
    public void    setWebAccessEnabled(Long upId, Boolean webAccessEnabled);

    /**
     * Interacts with a user or group's adHoc folders enabled flag.
     */
    public Boolean getAdHocFoldersEnabled(Long upId);
    public void    setAdHocFoldersEnabled(Long upId, Boolean adHocFoldersEnabled);

    /**
     * Returns a Collection<User> of all the external user's the
     * current user has rights to see.
     * 
     * @return
     */
    public Collection<User> getAllExternalUsers();
    
    /**
     * Returns a PrincipalMobileAppsConfig for the user or group as
     * read from its UserProperties.
     * 
     * @param principalId
     * 
     * @return
     */
    public PrincipalMobileAppsConfig getPrincipalMobileAppsConfig(Long principalId);
    
    /**
     * Returns a PrincipalDesktopAppsConfig for the user or group as
     * read from its UserProperties.
     * 
     * @param principalId
     * 
     * @return
     */
    public PrincipalDesktopAppsConfig getPrincipalDesktopAppsConfig(Long principalId);

    /**
     * Writes the settings from a PrincipalMobileAppsConfig to the
     * user's or group's UserProperties.
     * 
     * @param principalId
     * @param principalsAreUsers 
     * 
     * @param pConfig
     */
    public void savePrincipalMobileAppsConfig(Long       principalId,  boolean principalIsUser,    PrincipalMobileAppsConfig config);
    public void savePrincipalMobileAppsConfig(List<Long> principalIds, boolean principalsAreUsers, PrincipalMobileAppsConfig config);
    
    /**
     * Writes the settings from a PrincipalMobileAppsConfig to the
     * user's or group's UserProperties.
     * 
     * @param principalId
     * @param principalsAreUsers 
     * 
     * @param pConfig
     */
    public void savePrincipalDesktopAppsConfig(Long       principalId,  boolean principalIsUser,    PrincipalDesktopAppsConfig config);
    public void savePrincipalDesktopAppsConfig(List<Long> principalIds, boolean principalsAreUsers, PrincipalDesktopAppsConfig config);
    
	/**
	 * Upgrade Vibe Granite external users to full Vibe Hudson external users.
	 * Vibe Hudson external users are functionally equivalent to Filr 1.1 external users.
	 */
	public void upgradeVibeGraniteExternalUsers();
	
	/**
	 * Find all groups and teams that have an external user or the guest user as a member and
	 * mark the group/team as external.
	 */
	public void upgradeExternalGroupsAndTeams();
	
	/**
	 * Mark the given group as external, meaning it can contain external users/groups
	 */
	public void markGroupAsExternal( Long groupId ) throws AccessControlException, WriteFilesException, WriteEntryDataException;
	public void markGroupAsExternal( Group group ) throws AccessControlException, WriteFilesException, WriteEntryDataException;

	/**
	 * Sets the default locale language and country for new users.
	 * 
	 * @param language
	 * @param country
	 * @param zoneId
	 */
	public void setDefaultUserLocale(   String language, String country, Long zoneId);
	public void setDefaultUserLocale(   String language, String country             );	// Default -> Current zone.
	public void setDefaultUserLocaleExt(String language, String country, Long zoneId);
	public void setDefaultUserLocaleExt(String language, String country             );	// Default -> Current zone.
	
	/**
	 * Sets the default timezone for new users.
	 * 
	 * @param tz
	 * @param zoneId
	 */
	public void setDefaultUserTimeZone(   String tz, Long zoneId);
	public void setDefaultUserTimeZone(   String tz             );	// Default -> Current zone.
	public void setDefaultUserTimeZoneExt(String tz, Long zoneId);
	public void setDefaultUserTimeZoneExt(String tz             );	// Default -> Current zone.

	/**
	 * Sets the default timezone and locale for new internal and
	 * external users.
	 * 
	 * @param timeZone
	 * @param locale
	 * @param timeZoneExt
	 * @param localeExt
	 * @param zoneId
	 */
	public void setDefaultUserSettings(String timeZone, String locale, String timeZoneExt, String localeExt, Long zoneId);
	public void setDefaultUserSettings(String timeZone, String locale, String timeZoneExt, String localeExt             );	// Default -> Current zone.
}
