package com.sitescape.team.dao;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;

import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.NoGroupByTheIdException;
import com.sitescape.team.domain.NoGroupByTheNameException;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.Rating;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
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
     * Delete all principals in the ProfileBinder
     * @param profile
     */
    public void deleteEntries(ProfileBinder profile);
    /**
     * Delete a collection of principals including all their associations
     * @param entries
     */
    public void deleteEntries(List entries);
    
    public void disablePrincipals(Collection ids, Long zoneId);
    /**
     * Convert a group into its set of Users.  This includes
     * recursing through any member groups.
     * @param ids
     * @param zoneId
     * @return
     */
 	public Set explodeGroups(Set ids, Long zoneId); 
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
    
 	public Set getAllGroupMembership(Long principalId, Long zoneId);
 	public List getMembership(Long groupId, Long zoneId);
	public Set getPrincipalIds(User user);
	public ProfileBinder getProfileBinder(Long zoneId);
	public Group getReservedGroup(String internalId, Long zoneId) throws NoGroupByTheNameException;	   
	public User getReservedUser(String internalId, Long zoneId) throws NoUserByTheNameException;
      
    public Group loadGroup(Long groupId, Long zoneId) throws NoGroupByTheIdException;
    public List loadGroups(Collection groupsIds, Long zoneId);
    public List loadGroups(FilterControls filter, Long zoneId) throws DataAccessException; 
    public Principal loadPrincipal(Long prinId, Long zoneId, boolean checkActive);
    public List loadPrincipals(Collection ids, Long zoneId,  boolean checkActive);
    public Map loadPrincipalsData(Collection ids, Long zoneId,  boolean checkActive);
	public Rating loadRating(Long userId, EntityIdentifier entityId);
    public SeenMap loadSeenMap(Long userId);
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
    public List loadUsers(FilterControls filter, Long zoneId) throws DataAccessException; 
    public List loadUsers(Collection usersIds, Long zoneId);
	public Visits loadVisit(Long userId, EntityIdentifier entityId);

	public UserProperties loadUserProperties(Long userId);
    public UserProperties loadUserProperties(Long userId, Long binderId);
 
    public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId) throws DataAccessException;
    public SFQuery queryGroups(FilterControls filter, Long zoneId) throws DataAccessException; 
    public SFQuery queryUsers(FilterControls filter, Long zoneId) throws DataAccessException;    
     
 }