/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile;

import java.util.Map;
import java.util.List;
import java.util.Collection;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.module.shared.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

public interface ProfileModule {
   public Long addUser(String definitionId, Map inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public Long addGroup(String definitionId, Map inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public void modifyPrincipal(Long id, Map inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public void modifyPrincipalData(Long id, Map entryData) 
		throws AccessControlException;
   public void deletePrincipal(Long id)
		throws AccessControlException, WriteFilesException;
   public ProfileBinder addProfileBinder();
   public ProfileBinder getProfileBinder();
   public void modifyWorkflowState(Long binderId, Long entryId, Long tokenId, String toState) 
	throws AccessControlException;

    /**
     * @param userId
     * @return
     */
    public Principal getEntry(Long binderId, Long userId);

    public List getGroups();
    public Map getUsers();
    public Map getUsers(int maxEntries);
    	   
    public void index();
    public UserProperties setUserFolderProperty(Long userId, Long folderId, String property, Object value);
    public UserProperties getUserFolderProperties(Long userId, Long folderId);
    public UserProperties setUserProperty(Long userId, String property, Object value);
    public UserProperties getUserProperties(Long userId);
    public SeenMap getUserSeenMap(Long userId);
    public void updateUserSeenEntry(Long userId, Entry entry);
    public void updateUserSeenEntry(Long userId, List entries);
    public HistoryMap getUserHistory(Long userId, Long folderId);
    public void updateUserHistory(Long userId, Long folderId, Entry entry);
    public void updateUserHistory(HistoryMap history);

    public void bulkUpdateUsers(Map users);
    public void bulkUpdateUsers(Collection ids, Map update);
    public List bulkCreateUsers(Map users);
    public void bulkDisableUsers(Collection users);
    public void bulkUpdateGroups(Map groups);
    public void bulkUpdateGroups(Collection ids, Map update);
    public List bulkCreateGroups(Map groups);
    public void bulkDisableGroups(Collection groups);
    
}
