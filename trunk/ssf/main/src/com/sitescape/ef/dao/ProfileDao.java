package com.sitescape.ef.dao;


import java.util.Set;
import java.util.List;
import java.util.Collection;
import org.springframework.dao.DataAccessException;

import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.EmailAlias;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.dao.util.SFQuery;

/**
 * @author Jong Kim
 *
 */
public interface ProfileDao {
	public void delete(ProfileBinder profile);
    public void delete(Principal entry);
    public void deleteEntries(ProfileBinder profile);
    public void deleteEntries(List entries);
    public void deleteEntryWorkflows(ProfileBinder profile);
    public void deleteEntryWorkflows(List entries);
     public ProfileBinder getProfileBinder(String zoneName);
      
    /**
     * 
     * @param id
     * @return
     */
    public Principal loadPrincipal(Long prinId, String zoneName);
     /**
     * 
     * @param ids
     * @return
     */
    public List loadPrincipals(Collection ids, String zoneName);
    public void disablePrincipals(Collection ids, String zoneName);
   /**
     * Check that user is in zone
     * @param userId
     * @param zoneName
     * @return
     * @throws DataAccessException
     * @throws NoUserByTheIdException
     */
    public User loadUser(Long userId, String zoneName);
    /**
     * Same as <code>loadUser</code> except that this throws
     * NoUserByTheIdException if the user object is disabled.
     * @param userId
     * @param zoneName
     * @return
     */
    public User loadUserOnlyIfEnabled(Long userId, String zoneName);
    /**
     * @param userName
     * @param zoneName
     * @return User
     * @throws DataAccessException
     * @throws NoUserByTheNameException
     * @throws NoZoneByTheIdException
     */
    public User findUserByName(String principalName, String zoneName);
    
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
    public User findUserByNameOnlyIfEnabled(final String userName, final String zoneName);

    public List loadUsers(Collection usersIds, String zoneName);
    public List loadEnabledUsers (Collection usersIds, String zoneName);
    public SFQuery queryUsers(FilterControls filter, String zoneName) throws DataAccessException; 
    public List loadUsers(FilterControls filter, String zoneName) throws DataAccessException; 

    public int countUsers(FilterControls filter, String zoneName);
    public UserProperties loadUserProperties(Long userId);
    
    public Group loadGroup(Long groupId, String zoneName);
    public List loadGroups(Collection groupsIds, String zoneName);
    public int countGroups(FilterControls filter, String zoneName);
    public SFQuery queryGroups(FilterControls filter, String zoneName) throws DataAccessException; 
    public List loadGroups(FilterControls filter, String zoneName) throws DataAccessException; 
 	public Set explodeGroups(Set ids); 
	public List getMembership(Long groupId);
	public Set getAllGroupMembership(Long principalId);

    public SeenMap loadSeenMap(Long userId);
     
    public SFQuery queryAllPrincipals(FilterControls filter, String zoneName) throws DataAccessException;
 }