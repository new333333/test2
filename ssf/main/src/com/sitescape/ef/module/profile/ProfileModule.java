/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Rating;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Visits;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

public interface ProfileModule {
   public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
   public void checkAddEntryAllowed(ProfileBinder binder) throws AccessControlException;

   public boolean checkUserSeeCommunity();

   public boolean checkUserSeeAll();

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
	public Visits getVisit(EntityIdentifier entityId);
    public void setVisit(EntityIdentifier entityId);
	public Rating getRating(EntityIdentifier entityId);
    public void setRating(EntityIdentifier entityId, long value);
    public Long addWorkspace(Long binderId, Long entryId, String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException;

}
