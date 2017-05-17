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
package org.kablink.teaming.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.GroupSelectSpec;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.ApplicationPrincipal;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.IndividualPrincipal;
import org.kablink.teaming.domain.NoApplicationByTheIdException;
import org.kablink.teaming.domain.NoGroupByTheIdException;
import org.kablink.teaming.domain.NoGroupByTheNameException;
import org.kablink.teaming.domain.NoPrincipalByTheNameException;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.Rating;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.SharedEntity;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Visits;

import org.springframework.dao.DataAccessException;

/**
 * Interface to handle principals.
 * 
 * @author Jong Kim
 */
@SuppressWarnings("unchecked")
public interface ProfileDao {
	/**
	 * Optional optimization used to bulk load principal collections.  Used to
	 * pre-load objects for indexing
	 * @param entries
	 */
	public void bulkLoadCollections(final Collection<Principal> entries);
	/**
	 * Delete the ProfileBinder and all its associations.  
	 * Assume binder is empty
	 * @param profile
	 */
	public void delete(ProfileBinder profile);
	/**
	 * Delete a principal, removing all collections and associations
	 * @param entry
	 */
    public void delete(Principal entry);
     /**
     * Delete a collection of principals including all their associations
     * @param entries
     */
    public void deleteEntries(Collection<Principal> entries);
    
    public void disablePrincipals(Collection<Long> ids, Long zoneId);
    /**
     * Convert a group into its set of Users.  This includes
     * recursing through any member groups.
     * @param ids
     * @param zoneId
     * @return
     */
 	public Set<Long> explodeGroups(Collection<Long> ids, Long zoneId); 
 	public Set<Long> explodeGroups(Collection<Long> ids, Long zoneId, boolean allowAllUsersGroup); 
 	/**
 	 * Locate a user by name.  This is used for login
 	 * @param principalName
 	 * @param zoneName
 	 * @return
 	 * @throws NoUserByTheNameException
 	 * @throws NoWorkspaceByTheNameException
 	 */
 	public User findUserByName(String principalName, String zoneName) 
		throws NoUserByTheNameException, NoWorkspaceByTheNameException;

 	public User findUserByName(String principalName, Long zoneId) 
		throws NoUserByTheNameException;

 	public User findUserByForeignName(String foreignName, Long zoneId) 
		throws NoUserByTheNameException;

 	public User findUserByLdapGuid( String ldapGuid, Long zoneId ) 
 	 		throws NoUserByTheNameException;
 	 	
 	public Long findPrincipalIdByLdapGuid(String ldapGuid, Long zoneId);
 	 	 	
 	public Long findPrincipalIdByObjectSid(String objectSid, Long zoneId);
	 	
 	public Long findUserIdByObjectSid(String objectSid, Long zoneId);
	 	
 	public Long findPrincipalIdByForeignName(String foreignName, Long zoneId);
 	
 	public Long findUserIdByForeignName(String foreignName, Long zoneId);
 	
 	public Long findPrincipalIdByTypelessDN(String typelessDN, Long zoneId);
 	
 	public Long findUserIdByTypelessDN(String typelessDN, Long zoneId);
 	
 	public Long findPrincipalIdByName(String name, Long zoneId);

 	public Long findUserIdByName(String name, Long zoneId);

 	public Long findPrincipalIdByDomainAndSamaccount(String domainName, String samaccountName, Long zoneId);

 	public Long findUserIdByDomainAndSamaccount(String domainName, String samaccountName, Long zoneId);

 	public Principal findPrincipalByName(String name, Long zoneId) 
 		throws NoPrincipalByTheNameException;
 	public Set<Long> getApplicationLevelGroupMembership(Long principalId, Long zoneId);
 	public Set<Long> getAllGroupMembership(Long principalId, Long zoneId);
 	public List<Long> getMembership(Long groupId, Long zoneId);
 	public List<Long> getOwnedBinders(final Set<Principal> users);
	public Set<Long> getApplicationLevelPrincipalIds(Principal principal);
	public Set<Long> getAllPrincipalIds(Principal principal);
	public ProfileBinder getProfileBinder(Long zoneId);
	public Group getReservedGroup(String internalId, Long zoneId) throws NoGroupByTheNameException;	   
	public Long getReservedGroupId(String internalId, Long zoneId) throws NoGroupByTheNameException;	   
	public User getReservedUser(String internalId, Long zoneId) throws NoUserByTheNameException;
	public Collection<User> getReservedUsers(Collection<String> internalIds, Long zoneId) throws NoUserByTheNameException;
      
    public Group loadGroup(Long groupId, Long zoneId) throws NoGroupByTheIdException;
    public List<Group> loadGroups(Collection<Long> groupsIds, Long zoneId);
    public List<Group> loadGroups(FilterControls filter, Long zoneId) throws DataAccessException; 
    public Principal loadPrincipal(Long prinId, Long zoneId, boolean checkActive);
    public List<Principal> loadPrincipalByEmail(final String email, String emailType, final Long zoneId);
    public UserPrincipal loadUserPrincipal(Long prinId, Long zoneId, boolean checkActive);
    public List<UserPrincipal> loadUserPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
	public Rating loadRating(Long userId, EntityIdentifier entityId);
    public SeenMap loadSeenMap(Long userId);
	public List<SharedEntity> loadSharedEntities(Collection ids, Collection binderIds, Date after, Long zoneId); 	
   	public Subscription loadSubscription(Long userId, EntityIdentifier entityId);
   	public List<Subscription> loadSubscriptions(Collection<Long> userIds, Long entityId, Long zoneId);
   	public List<Subscription> loadSubscriptions(Collection<Long> userIds,                Long zoneId);
   	public List<Subscription> loadSubscriptions(           Long  userId,  Long entityId, Long zoneId);
   	public List<Subscription> loadSubscriptions(           Long  userId,                 Long zoneId);
  /**
     * Load a user that is neither deleted or disabled. Check that user is in zone.
     * @param userId
     * @param zoneId
     * @return
     * @throws DataAccessException
     * @throws NoUserByTheIdException
     */
    public User loadUser(Long userId, Long zoneId) throws NoUserByTheIdException;
    public User loadUser(Long userId, String zoneName) throws NoUserByTheIdException;
    public List<User> loadUsers(FilterControls filter, Long zoneId) throws DataAccessException; 
    public List<User> loadUsers(Collection<Long> usersIds, Long zoneId);
	public Visits loadVisit(Long userId, EntityIdentifier entityId);

	public UserProperties loadUserProperties(Long userId);
    public UserProperties loadUserProperties(Long userId, Long binderId);
	public void markEntriesDeleted(ProfileBinder binder, Collection<Principal> entries);

    public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId) throws DataAccessException;
    public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId, boolean includeDisabled) throws DataAccessException;
    public SFQuery queryGroups(FilterControls filter, Long zoneId) throws DataAccessException; 
    public SFQuery queryUsers(FilterControls filter, Long zoneId) throws DataAccessException;    
     
	public ApplicationGroup getReservedApplicationGroup(String internalId, Long zoneId) throws NoGroupByTheNameException;	   
    public ApplicationGroup loadApplicationGroup(Long groupId, Long zoneId) throws NoGroupByTheIdException;
    public Application loadApplication(Long applicationId, Long zoneId) throws NoApplicationByTheIdException;
    public Application loadApplication(Long applicationId, String zoneName) throws NoApplicationByTheIdException;
    public List<ApplicationGroup> loadApplicationGroups(Collection<Long> groupsIds, Long zoneId);
    public ApplicationPrincipal loadApplicationPrincipal(Long prinId, Long zoneId, boolean checkActive);
    public List<ApplicationPrincipal> loadApplicationPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
    public List<GroupPrincipal> loadGroupPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
    public List<IndividualPrincipal> loadIndividualPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
    public List<Application> loadApplications(Collection<Long> applicationIds, Long zoneId);

    public List<Principal> loadPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
    /**
     * IMPORTANT: This method is for internal/special use only - specifically
     * for error recovery situation only. Application should NEVER use this method
     * directly. For regular usage, loadUser() method must be used instead.
     * 
     * @param userId
     * @param zoneId
     * @return
     */
    public User loadUserDeadOrAlive(Long userId, Long zoneId);
    /**
     * IMPORTANT: This method is for internal/special use only - specifically
     * for error recovery situation only. Application should NEVER use this method
     * directly. For regular usage, getReservedUser() method must be used instead.
     * 
     * @param internalId
     * @param zoneId
     * @return user
     */
    public User getReservedUserDeadOrAlive(String internalId, Long zoneId) throws NoUserByTheIdException;
    /**
     * IMPORTANT: This method is for internal/special use only - specifically
     * for error recovery situation only. Application should NEVER use this method
     * directly. For regular usage, findUserByName() method must be used instead.
     * 
     * @param principalName
     * @param zoneName
     * @return
     * @throws NoUserByTheNameException
     * @throws NoWorkspaceByTheNameException
     */
 	public User findUserByNameDeadOrAlive(String principalName, String zoneName) 
	throws NoUserByTheNameException, NoWorkspaceByTheNameException;

    /**
     * Remove any principals that the user is not allowed to see
     * 
     * @param List of principals
     * @return Filtered list of principals
     */
 	public List filterInaccessiblePrincipals(List principals);
    /**
     * Check that the user is allowed to see this Principal
     * 
     * @param Principal
     * @return Principal or null
     */
 	public Principal filterInaccessiblePrincipal(Principal principal);
 	
 	/**
 	 * Initialize (or reset) the disk space used by each user
 	 */
 	public void resetDiskUsage(Long zone);
 	
 	/**
 	 * Get the non-default value quotas for either groups or users.
 	 */
 	public List getNonDefaultQuotas(String type, final long zoneId);
 	
 	/**
 	 * Get the non-default value quotas for either groups or users.
 	 */
 	public List getNonDefaultFileSizeLimits(String type, final long zoneId);
 	
 	/**
 	 * Get the list of disabled user accounts.
 	 */
 	public List<Long> getDisabledUserAccounts(final long zoneId);
 	
 	/**
 	 * Load a <code>ShareItem</code> given its id.
 	 * If the object is not found, it throws <code>NoShareItemByTheIdException</code>.
 	 * 
 	 * @param shareItemId
 	 * @return
 	 * @throws NoShareItemByTheIdException
 	 */
 	public ShareItem loadShareItem(Long shareItemId) throws NoShareItemByTheIdException;
 	
 	/**
 	 * Load a list of <code>ShareItem</code> given their ids.
 	 * Unlike <code>loadShareItem</code> method, this method does not return error when
 	 * not all of the objects are found by the specified ids. Instead, it will only return
 	 * those objects successfully found.
 	 * 
 	 * @param shareItemIds
 	 * @return
 	 */
 	public List<ShareItem> loadShareItems(Collection<Long> shareItemIds); 
 	
 	/**
 	 * Get a list of <code>Long</code> shareItem ids for a specified entity.
 	 * 
 	 * @param entity
 	 * @return
 	 */
 	public List<Long> getShareItemIdsByEntity(DefinableEntity entity);
 	
 	/**
 	 * Change a list of <code>Long</code> shareItem ids to point to a new entity.
 	 * 
 	 * @param entity
 	 * @return
 	 */
 	public void changeSharedEntityId(final Collection<Long> shareItemIds, final DefinableEntity entity);
 	
 	/**
 	 * Return IDs of users, groups, and teams that are granted specified right to the specified entity.
 	 * 
 	 * @param sharedEntityIdentifier
 	 * @param rightName
 	 * @return
 	 */
 	public Map<ShareItem.RecipientType, Set<Long>> getRecipientIdsWithGrantedRightToSharedEntity(EntityIdentifier sharedEntityIdentifier, String rightName, Long zoneId);
 	
 	/**
 	 * Return IDs of users, groups, and teams that are granted specified right to at least one of the specified entities.
 	 * 
 	 * @param sharedEntityIdentifier
 	 * @param rightName
 	 * @return
 	 */
 	public Map<ShareItem.RecipientType, Set<Long>> getRecipientIdsWithGrantedRightToSharedEntities(Collection<EntityIdentifier> sharedEntityIdentifiers, String rightName);
 	
 	/**
 	 * Return IDs of users, groups, and teams that are granted at least one of the specified
 	 * rights to at least one of the specified entities.
 	 * 
 	 * @param sharedEntityIdentifier
 	 * @param rightName
 	 * @return
 	 */
 	public Map<ShareItem.RecipientType, Set<Long>> getRecipientIdsWithGrantedRightsToSharedEntities(Collection<EntityIdentifier> sharedEntityIdentifiers, String[] rightNames);
 	
 	/** 
 	 * Find a list of <code>Group</code> meeting the specified selection criteria.
 	 * 
 	 * @return
 	 */
 	public List<Group> findGroups( GroupSelectSpec groupSelectSpec, Long zoneId);

 	/** 
 	 * Find a list of <code>ShareItem</code> meeting the specified selection criteria.
 	 * 
 	 * @param selectSpec
 	 * @return
 	 */
 	public List<ShareItem> findShareItems(final ShareItemSelectSpec selectSpec, Long zoneId);

 	/**
 	 * Find a list <code>ShareItem</code> that have been expired but yet to be handled.
 	 * 
 	 * @return
 	 */
 	public List<ShareItem> findExpiredAndNotYetHandledShareItems(final Long zoneId);
 	
 	/**
 	 * Get a list of LDAP users and groups that are members of the specified LDAP container group.
 	 * The result contains all descendants recursively, not just immediate children. 
 	 * 
 	 * @param containerGroup
 	 * @return
 	 */
 	public List<Principal> getLdapContainerGroupMembers(Group containerGroup);

 	/**
 	 * Get a list of IDs of the LDAP container groups that the specified principal is a member
 	 * of either directly (i.e., immediate child) or indirectly via recursion (i.e., descendant).
 	 * 
 	 * @param principalId
 	 * @param zoneId
 	 * @return
 	 */
 	public List<Long> getMemberOfLdapContainerGroupIds(Long principalId, Long zoneId);
 	
 	/**
 	 * Load LDAP container groups.
 	 * 
 	 * @param filter
 	 * @param zoneId
 	 * @return
 	 * @throws DataAccessException
 	 */
    public List<Group> loadLdapContainerGroups(Long zoneId); 

    /**
     * Update the various db tables to necessary to support a user being renamed.
     */
    public void renameUser( User user );
    
    
 	/**
 	 * Return IDs of sharers of the specified entities.
 	 * 
 	 * @param sharedEntityIdentifier
 	 * @return
 	 */
	public Set<Long> getSharerIdsToSharedEntities(
			Collection<EntityIdentifier> sharedEntityIdentifiers);
	
    public List<Long> getAllPrincipalIds(Long zoneId, boolean includeDisabled);
}
