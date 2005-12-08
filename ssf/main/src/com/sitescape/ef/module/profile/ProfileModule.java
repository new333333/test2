/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile;

import java.util.Map;
import java.util.List;
import java.util.Collection;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.module.shared.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

public interface ProfileModule {
   public Long addUser(String definitionId, Map inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public Long addGroup(String definitionId, Map inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public void modifyUser(Long id, Map inputData, Map fileItems) 
   		throws AccessControlException, WriteFilesException;
   public void modifyGroup(Long id, Map inputData, Map fileItems)
		throws AccessControlException, WriteFilesException;
    /**
     * @param userId
     * @return
     */
    public Map getProfile(Long userId);

    public List getGroups();
    public List getUsers();
  
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
