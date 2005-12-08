/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile;

import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.UserProperties;
public interface ProfileModule {
    /**
     * @param userId
     * @return
     */
    public Map showProfile(Long userId, boolean securityInfo, boolean signature);
    public void bulkUpdateUsers(Map users);
    public void bulkUpdateUsers(Collection ids, Map update);
    public List bulkCreateUsers(Map users);
    public void bulkDisableUsers(Collection users);
    public void bulkUpdateGroups(Map groups);
    public void bulkUpdateGroups(Collection ids, Map update);
    public List bulkCreateGroups(Map groups);
    public void bulkDisableGroups(Collection groups);
    public List getGroups();
    
    public UserProperties setUserFolderProperty(Long userId, Long folderId, String property, Object value);
    public UserProperties getUserFolderProperties(Long userId, Long folderId);
    public UserProperties setUserProperty(Long userId, String property, Object value);
    public UserProperties getUserProperties(Long userId);
    public List getUsers();
    public SeenMap getUserSeenMap(Long userId, Long folderId);
    public void updateUserSeenEntry(Long userId, Long folderId, Entry entry);
    public void updateUserSeenEntry(Long userId, Long folderId, List entries);
    public HistoryMap getUserHistory(Long userId, Long folderId);
    public void updateUserHistory(Long userId, Long folderId, Entry entry);
    public void updateUserHistory(HistoryMap history);
    
}
