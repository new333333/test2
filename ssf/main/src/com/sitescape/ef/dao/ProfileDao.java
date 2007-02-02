package com.sitescape.ef.dao;


import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.NoGroupByTheIdException;
import com.sitescape.team.domain.NoGroupByTheNameException;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.Rating;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Visits;

/**
 * @author Jong Kim
 *
 */
public interface ProfileDao {
	public void delete(ProfileBinder profile);
    public void delete(Principal entry);
    public void deleteEntries(ProfileBinder profile);
    public void deleteEntries(List entries);
    public ProfileBinder getProfileBinder(Long zoneId);
      
    /**
     * 
     * @param id
     * @return
     */
    public Principal loadPrincipal(Long prinId, Long zoneId);
     /**
     * 
     * @param ids
     * @return
     */
    public List loadPrincipals(Collection ids, Long zoneId);
    public void disablePrincipals(Collection ids, Long zoneId);
   /**
     * Check that user is in zone
     * @param userId
     * @param zoneId
     * @return
     * @throws DataAccessException
     * @throws NoUserByTheIdException
     */
    public User loadUser(Long userId, Long zoneId) throws NoUserByTheIdException;
    /**
     * Same as <code>loadUser</code> except that this throws
     * NoUserByTheIdException if the user object is disabled.
     * @param userId
     * @param zoneId
     * @return
     */
    public User loadUserOnlyIfEnabled(Long userId, Long zoneId) throws NoUserByTheIdException;
    public User loadUserOnlyIfEnabled(Long userId, String zoneName) throws NoUserByTheIdException;
    /**
     * @param userName
     * @param zoneName
     * @return User
     * @throws DataAccessException
     * @throws NoUserByTheNameException
     * @throws NoZoneByTheIdException
     */
    public User findUserByName(String principalName, String zoneName) throws NoUserByTheNameException;
    
    /**
     * Same as <code>findUserByName</code> except that this throws 
     * NoUserByTheNameException if the user object is disabled. 
     * @param userName
     * @param zoneName
     * @return
     * @throws DataAccessException
     * @throws NoUserByTheNameException
     * @throws NoZoneByTheIdException
     */
    public User findUserByNameOnlyIfEnabled(final String userName, final String zoneName) throws NoUserByTheNameException;

    public List loadUsers(Collection usersIds, Long zoneId);
    public List loadEnabledUsers (Collection usersIds, Long zoneId);
    public SFQuery queryUsers(FilterControls filter, Long zoneId) throws DataAccessException; 
    public List loadUsers(FilterControls filter, Long zoneId) throws DataAccessException; 
    public User getReservedUser(String internalId, Long zoneId) throws NoUserByTheNameException;
    public void bulkLoadCollections(final Collection<Principal> entries);

    public UserProperties loadUserProperties(Long userId);
    public UserProperties loadUserProperties(Long userId, Long binderId);
    
    public Group loadGroup(Long groupId, Long zoneId) throws NoGroupByTheIdException;
    public List loadGroups(Collection groupsIds, Long zoneId);
    public SFQuery queryGroups(FilterControls filter, Long zoneId) throws DataAccessException; 
    public List loadGroups(FilterControls filter, Long zoneId) throws DataAccessException; 
 	public Set explodeGroups(Set ids, Long zoneId); 
	public List getMembership(Long groupId, Long zoneId);
	public Set getAllGroupMembership(Long principalId, Long zoneId);
	public Set getPrincipalIds(User user);
	public Group getReservedGroup(String internalId, Long zoneId) throws NoGroupByTheNameException;	   

    public SeenMap loadSeenMap(Long userId);
	public Visits loadVisit(Long userId, EntityIdentifier entityId);
	public Rating loadRating(Long userId, EntityIdentifier entityId);
	public Subscription loadSubscription(Long userId, EntityIdentifier entityId);
     
    public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId) throws DataAccessException;
 }