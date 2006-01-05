package com.sitescape.ef.dao;


import java.util.Set;
import java.util.List;
import java.util.Collection;
import org.springframework.dao.DataAccessException;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Group;
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
public interface CoreDao {
	/**
	 * 
	 * @parm obj
	 * @throws DataAccessException
	 */
	public void save(Object obj);
	public void save(Collection objs);
	public void saveNewSession(Object obj);
	public void update(Object obj); 
	public Object merge(Object obj); 
	/**
	 * 
	 * @param obj
	 */
	public void delete(Object obj);
	public void delete(Collection objs);
	public Object load(Class className, String id);
	public Object load(Class className, Long id);
	public List loadObjects(ObjectControls objs, FilterControls filter);
              

	public Workspace findTopWorkspace(String zoneName);
	/**
     * 
     * @param binderId
     * @param zoneName
     * @return
     * @throws DataAccessException
     * @throws NoFolderByTheIdException
     */
    public Binder loadBinder(Long binderId, String zoneName);
    public Binder findBinderByName(String binderName, String zoneName);
      
    /**
     * 
     * @param id
     * @return
     */
    public Principal loadPrincipal(Long prinId, String zoneName);
    public Principal loadFullPrincipal(Long prinId, String zoneName);
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

    public int countUsers(FilterControls filter);
    public UserProperties loadUserProperties(Long userId);
    
    public Group loadGroup(Long groupId, String zoneName);
    public List loadGroups(Collection groupsIds, String zoneName);
    public int countGroups(FilterControls filter);
    public SFQuery queryGroups(FilterControls filter, String zoneName) throws DataAccessException; 
    public List loadGroups(FilterControls filter, String zoneName) throws DataAccessException; 
 	public Set explodeGroups(Set ids); 
	public List getMembership(Long groupId);
	public Set getAllGroupMembership(Long principalId);

	public Definition loadDefinition(String defId, String zoneName);   
    public List loadDefinitions(Binder binder, ObjectControls objectDesc, FilterControls filter);       
    public List loadDefinitions(String ZoneName);
    
	public List loadPostings(String zoneName);
	public List loadEmailAliases(String zoneName);
	public EmailAlias loadEmailAlias(String aliasId, String zoneName);
    public SeenMap loadSeenMap(Long userId);
	
 }