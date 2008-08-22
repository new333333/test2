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
package com.sitescape.team.dao;


import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;

import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.ApplicationGroup;
import com.sitescape.team.domain.ApplicationPrincipal;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.GroupPrincipal;
import com.sitescape.team.domain.IndividualPrincipal;
import com.sitescape.team.domain.NoGroupByTheIdException;
import com.sitescape.team.domain.NoGroupByTheNameException;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.NoPrincipalByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.Rating;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.SharedEntity;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserPrincipal;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Visits;

/**
 * Interface to handle principals.
 */
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

 	public Principal findPrincipalByName(String name, Long zoneId) 
 		throws NoPrincipalByTheNameException;
 	public Set<Long> getAllGroupMembership(Long principalId, Long zoneId);
 	public List<Long> getMembership(Long groupId, Long zoneId);
	public Set<Long> getPrincipalIds(Principal principal);
	public ProfileBinder getProfileBinder(Long zoneId);
	public Group getReservedGroup(String internalId, Long zoneId) throws NoGroupByTheNameException;	   
	public User getReservedUser(String internalId, Long zoneId) throws NoUserByTheNameException;
      
    public Group loadGroup(Long groupId, Long zoneId) throws NoGroupByTheIdException;
    public List<Group> loadGroups(Collection<Long> groupsIds, Long zoneId);
    public List<Group> loadGroups(FilterControls filter, Long zoneId) throws DataAccessException; 
    public Principal loadPrincipal(Long prinId, Long zoneId, boolean checkActive);
    public List<Principal> loadPrincipalByEmail(final String email, final Long zoneId);
    public UserPrincipal loadUserPrincipal(Long prinId, Long zoneId, boolean checkActive);
    public List<UserPrincipal> loadUserPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
	public Rating loadRating(Long userId, EntityIdentifier entityId);
    public SeenMap loadSeenMap(Long userId);
    public List<SharedEntity> loadSharedEntities(Collection ids, Collection binderIds, Date after, Long zoneId); 	
   	public Subscription loadSubscription(Long userId, EntityIdentifier entityId);
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
    public SFQuery queryGroups(FilterControls filter, Long zoneId) throws DataAccessException; 
    public SFQuery queryUsers(FilterControls filter, Long zoneId) throws DataAccessException;    
     
	public ApplicationGroup getReservedApplicationGroup(String internalId, Long zoneId) throws NoGroupByTheNameException;	   
    public ApplicationGroup loadApplicationGroup(Long groupId, Long zoneId) throws NoGroupByTheIdException;
    public Application loadApplication(Long applicationId, Long zoneId) throws NoUserByTheIdException;
    public Application loadApplication(Long applicationId, String zoneName) throws NoUserByTheIdException;
    public List<ApplicationGroup> loadApplicationGroups(Collection<Long> groupsIds, Long zoneId);
    public ApplicationPrincipal loadApplicationPrincipal(Long prinId, Long zoneId, boolean checkActive);
    public List<ApplicationPrincipal> loadApplicationPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
    public List<GroupPrincipal> loadGroupPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
    public List<IndividualPrincipal> loadIndividualPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);
    public List<Application> loadApplications(Collection<Long> applicationIds, Long zoneId);

    public List<Principal> loadPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive);

 }