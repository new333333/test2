/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.team.module.profile;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
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
	public boolean testAccess(ProfileBinder binder, String operation);
	public boolean testAccess(Principal entry, String operation);

   public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public void addEntries(Long binderId, Document doc);
   public boolean checkUserSeeCommunity();

   public boolean checkUserSeeAll();

   public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData, 
		   Map fileItems, Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo) 
		throws AccessControlException, WriteFilesException;

   public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
		throws AccessControlException, WriteFilesException;
  
   public void deleteEntry(Long binderId, Long id)
		throws AccessControlException, WriteFilesException;

   public ProfileBinder getProfileBinder();

    /**
     * @param userId
     * @return
     */
    public Principal getEntry(Long binderId, Long userId);

    public Map getGroups(Long binderId);
    public Map getGroups(Long binderId, Map options);
	public Collection getGroups(Set groupIds);
    public Map getUsers(Long binderId);
    public Map getUsers(Long binderId, Map options);
	public Collection getUsers(Set userIds);
	/**
	 * Return a collection of user.  The are either in the principal list
	 * or members of groups in the principal list.
	 * @param principalIds
	 * @return
	 */
	public Collection getUsersFromPrincipals(Set principalIds);
    	   
    public UserProperties setUserProperty(Long userId, Long folderId, String property, Object value);
    public UserProperties getUserProperties(Long userId, Long folderId);
    public UserProperties setUserProperty(Long userId, String property, Object value);
    public UserProperties getUserProperties(Long userId);
    public SeenMap getUserSeenMap(Long userId);
    public void setSeen(Long userId, Entry entry);
    public void setSeen(Long userId, List entries);
    public Workspace addUserWorkspace(User user) throws AccessControlException;
    
	/**
	 * Create an user from information from the portal.
	 * 
	 * @param zoneName 
	 * @param userName 
	 * @param password may be null
	 * @param updates may be null
	 * @return created user object
	 */
	public User addUserFromPortal(String zoneName, String userName, String password, Map updates);
	
	/**
	 * Update user from information from the portal.
	 * 
	 * @param user
	 * @param updates
	 */
	public void modifyUserFromPortal(User user, Map updates);
}
