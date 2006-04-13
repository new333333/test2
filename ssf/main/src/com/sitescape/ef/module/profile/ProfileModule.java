/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile;

import java.util.Collection;
import java.util.Map;
import java.util.List;

import org.dom4j.Document;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

public interface ProfileModule {
   public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public void checkAddEntryAllowed(ProfileBinder binder) throws AccessControlException;

   public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData, Map fileItems, Collection deleteAttachments) 
		throws AccessControlException, WriteFilesException;
   public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
		throws AccessControlException, WriteFilesException;
   public void modifyWorkflowState(Long binderId, Long entryId, Long tokenId, String toState) 
	throws AccessControlException;
   public void checkModifyEntryAllowed(Principal entry) throws AccessControlException;
  
   public void deleteEntry(Long binderId, Long id)
		throws AccessControlException, WriteFilesException;
   public void checkDeleteEntryAllowed(Principal entry) throws AccessControlException;

   public ProfileBinder addProfileBinder();
   public ProfileBinder getProfileBinder();

    /**
     * @param userId
     * @return
     */
    public Principal getEntry(Long binderId, Long userId);

    public List getGroups(Long binderId);
    public Map getGroups(Long binderId, int maxEntries, Document searchFilter);
    public Map getUsers(Long binderId);
    public Map getUsers(Long binderId, int maxEntries);
    public Map getUsers(Long binderId, int maxEntries, Document searchFilter);
    	   
    public void indexEntries(Long binderId);
    public UserProperties setUserFolderProperty(Long userId, Long folderId, String property, Object value);
    public UserProperties getUserFolderProperties(Long userId, Long folderId);
    public UserProperties setUserProperty(Long userId, String property, Object value);
    public UserProperties getUserProperties(Long userId);
    public SeenMap getUserSeenMap(Long userId);
    public void updateUserSeenEntry(Long userId, Entry entry);
    public void updateUserSeenEntry(Long userId, List entries);

}
