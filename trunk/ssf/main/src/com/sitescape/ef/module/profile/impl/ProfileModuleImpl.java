/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile.impl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;


import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.security.AccessControlException;

public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
    
   public Map showProfile(Long userId, boolean securityInfo, boolean signature) {
   		Map model = new HashMap();
        Principal entry;
            
        User user = RequestContextHolder.getRequestContext().getUser();
        entry = getCoreDao().loadPrincipal(userId, user.getZoneName());        
        model.put("entry", entry);
        if (entry instanceof Group) {
            Group group = (Group)entry;
            Iterator iter = group.getMembers().iterator();
            ArrayList groupList = new ArrayList();
            ArrayList memberList = new ArrayList();
            while (iter.hasNext()) {
                entry = (Principal)iter.next();
                if (entry instanceof User) {
                    memberList.add(entry);
                } else {
                   groupList.add(entry);
                }
            }
            model.put("groupList", groupList);
            model.put("memberList", memberList);
                
        }
       
        model.put(ObjectKeys.USER, user);

        return model;
    }
   public User modifyUser(Long id, Map updates) {
       User user = RequestContextHolder.getRequestContext().getUser();
       User modUser = coreDao.loadUser(id, user.getZoneName());
       if (user.equals(modUser)) {
           modUser.setModification(new HistoryStamp(user));
           EntryBuilder.updateEntry(modUser, updates);
       } else {
    	   throw new AccessControlException();
       }
       return modUser;
       
	   
   }
   public Group modifyGroup(Long id, Map updates) {
       User user = RequestContextHolder.getRequestContext().getUser();
       Group group = coreDao.loadGroup(id, user.getZoneName());
       //TODO: access check
       group.setModification(new HistoryStamp(user));
       EntryBuilder.updateEntry(group, updates);
       return group;
	   
   }
   
   public UserProperties setUserFolderProperty(Long userId, Long folderId, String property, Object value) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
  		if (userId == null) userId = user.getId();
   		//TODO: probably need access checks, but how?
  		if (user.getId().equals(userId)) {
    		uProps = getFolderDao().loadUserFolderProperties(userId, folderId);
			uProps.setProperty(property, value); 	
  		}
  		return uProps;
   }
   public UserProperties getUserFolderProperties(Long userId, Long folderId) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
  		if (userId == null) userId = user.getId();
		//TODO: probably need access checks, but how?
  		if (user.getId().equals(userId)) {
			uProps = getFolderDao().loadUserFolderProperties(userId, folderId);
		}
		return uProps;
}

   public UserProperties setUserProperty(Long userId, String property, Object value) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
 		//TODO: probably need access checks, but how?
  		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
 			uProps = getCoreDao().loadUserProperties(user.getId());
			uProps.setProperty(property, value);			
  		}
		return uProps;
    }
   public UserProperties getUserProperties(Long userId) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
 		if (userId == null) userId = user.getId();
 		  		//TODO: probably need access checks, but how?
  		if (user.getId().equals(userId)) {
    		uProps = getCoreDao().loadUserProperties(userId);
  		}
  		return uProps;
   }
   public SeenMap getUserSeenMap(Long userId, Long folderId) {
		User user = RequestContextHolder.getRequestContext().getUser();
		SeenMap seen = null;
		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
 			 seen = getFolderDao().loadSeenMap(user.getId(), folderId);
 		}
   		return seen;
   }
   public void updateUserSeenEntry(Long userId, Long folderId, Entry entry) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		SeenMap seen;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
			 seen = getFolderDao().loadSeenMap(user.getId(), folderId);
			 seen.setSeen(entry);
		}
   }
   public void updateUserSeenEntry(Long userId, Long folderId, List entries) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		SeenMap seen;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
  			seen = getFolderDao().loadSeenMap(user.getId(), folderId);
   			for (int i=0; i<entries.size(); i++) {
   				Entry reply = (Entry)entries.get(i);
   				seen.setSeen(reply);
   			}
   		}
   }  	
   public HistoryMap getUserHistory(Long userId, Long folderId) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		HistoryMap history = null;
   		if (userId == null) userId = user.getId();
		if (user.getId().equals(userId)) {
			history = getFolderDao().loadHistoryMap(user.getId(), folderId);
		}
		return history;
}
   public void updateUserHistory(Long userId, Long folderId, Entry entry) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		HistoryMap history;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
   			history = getFolderDao().loadHistoryMap(user.getId(), folderId);
   			history.setSeen(entry);
   		}
   }
   public void updateUserHistory(HistoryMap history) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		if (history.getId().getPrincipalId().equals(user.getId())) {
   			getCoreDao().update(history);
   		}  		
   }
   
   /**
    * Update users with their own updates
    * @param users - Map indexed by user id, value is map of updates
    */
    public void bulkUpdateUsers(Map users) {
    	List foundEntries = coreDao.loadUsers(users.keySet());
    	EntryBuilder.updateEntries(foundEntries, users);

    }
    /**
     * Update users with one shared updates
     * @param ids - Collection of user ids
     * @param updates - Map indexed by attribute name, value is update value
     */
    public void bulkUpdateUsers(Collection ids, Map updates) {
    	List foundEntries = coreDao.loadUsers(ids);
    	EntryBuilder.applyUpdate(foundEntries, updates);

    }   
    /**
     * Update groups with their own updates
     * @param groups - Map indexed by group id, value is map of updates
     */    
    public void bulkUpdateGroups(Map groups) {
    	List foundEntries = coreDao.loadGroups(groups.keySet());
       	EntryBuilder.updateEntries(foundEntries, groups);
    }
    /**
     * Update groups with one shared updates
     * @param ids - Collection of group ids
     * @param updates - Map indexed by attribute name, value is update value
     */    
    public void bulkUpdateGroups(Collection ids, Map updates) {
    	List foundEntries = coreDao.loadUsers(ids);
    	EntryBuilder.applyUpdate(foundEntries, updates);

    }     
    public List bulkCreateUsers(Map users) {
    	try {
    		List result = EntryBuilder.buildEntries(ReflectHelper.classForName("com.sitescape.ef.domain.User"), users.values());
    		getCoreDao().save(result);
    		return result;
    	} catch (ClassNotFoundException ce) {
    		throw new ConfigurationException(ce);
    	}
    }
    public List bulkCreateGroups(Map groups) {
       	try {
       		List result = EntryBuilder.buildEntries(ReflectHelper.classForName("com.sitescape.ef.domain.Group"), groups.values());
       		getCoreDao().save(result);
       		return result;
    	} catch (ClassNotFoundException ce) {
    		throw new ConfigurationException(ce);
    	}
    }
    public void bulkDisableUsers(Collection ids) {
    	coreDao.disablePrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
    }
    public void bulkDisableGroups(Collection ids) {
    	coreDao.disablePrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
   }
    public List getGroups() {
    	FilterControls filter = new FilterControls();
    	filter.setOrderBy(new OrderBy("title"));
    	List result = coreDao.filterGroups(filter);
    	//TODO: check access
    	return result;
    }
    public List getUsers() {
    	FilterControls filter = new FilterControls();
    	filter.setOrderBy(new OrderBy("title"));
    	List result = coreDao.filterUsers(filter);
    	//TODO: check access
    	return result;
    }
}
