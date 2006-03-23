/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile.impl;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.dom4j.Document;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.domain.NoBinderByTheNameException;


public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
	private static final int DEFAULT_MAX_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	private String[] userDocType = {EntryIndexUtils.ENTRY_TYPE_USER};
	private String[] groupDocType = {EntryIndexUtils.ENTRY_TYPE_GROUP};
	
	private ProfileCoreProcessor loadProcessor(ProfileBinder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
	    return (ProfileCoreProcessor) getProcessorManager().getProcessor(binder, ProfileCoreProcessor.PROCESSOR_KEY);
	}
	private ProfileBinder loadBinder(Long binderId) {
		return (ProfileBinder)getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
	}
	private ProfileBinder loadBinder() {
	   return (ProfileBinder)getCoreDao().getProfileBinder(RequestContextHolder.getRequestContext().getZoneName());
	}
	public ProfileBinder addProfileBinder() {
		ProfileBinder pf;
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		try {
			return (ProfileBinder)getCoreDao().findBinderByName("_profiles", zoneName);
			
		} catch (NoBinderByTheNameException nb) {
			pf = new ProfileBinder();
			pf.setName("_profiles");
			pf.setZoneName(zoneName);
			getCoreDao().save(pf);
			getCoreDao().findTopWorkspace(zoneName).addChild(pf);
			List users = getCoreDao().loadUsers(new FilterControls(), zoneName);
			for (int i=0; i<users.size(); ++i) {
				User u = (User)users.get(i);
				u.setParentBinder(pf);
			}
			
			List groups = getCoreDao().loadGroups(new FilterControls(), zoneName);
			for (int i=0; i<groups.size(); ++i) {
				Group g = (Group)groups.get(i);
				g.setParentBinder(pf);
			}
			return pf;
		}
	}
	public ProfileBinder getProfileBinder() {
	   ProfileBinder binder = loadBinder();
	   //todo: access check
	   return binder;
    }

	public Principal getEntry(Long binderId, Long principaId) {
        ProfileBinder binder = loadBinder(binderId);
        return getCoreDao().loadPrincipal(principaId, binder.getZoneName());        
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
   public SeenMap getUserSeenMap(Long userId) {
		User user = RequestContextHolder.getRequestContext().getUser();
		SeenMap seen = null;
		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
 			 seen = getCoreDao().loadSeenMap(user.getId());
 		}
   		return seen;
   }
   public void updateUserSeenEntry(Long userId, Entry entry) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		SeenMap seen;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
			 seen = getCoreDao().loadSeenMap(user.getId());
			 seen.setSeen(entry);
		}
   }
   public void updateUserSeenEntry(Long userId, List entries) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		SeenMap seen;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
			seen = getCoreDao().loadSeenMap(user.getId());
   			for (int i=0; i<entries.size(); i++) {
   				Entry reply = (Entry)entries.get(i);
   				seen.setSeen(reply);
   			}
   		}
   }  	
  
    public List getGroups(Long binderId) {
        ProfileBinder binder = loadBinder(binderId);
  		FilterControls filter = new FilterControls();
    	filter.setOrderBy(new OrderBy("title"));
    	List result = coreDao.loadGroups(filter, RequestContextHolder.getRequestContext().getZoneName());
    	//TODO: check access
    	return result;
    }
 
    public Map getGroups(Long binderId, int maxEntries, Document searchFilter) {
        ProfileBinder binder = loadBinder(binderId);
        return loadProcessor(binder).getBinderEntries(binder, groupDocType, maxEntries, searchFilter);        
   }
 
    //***********************************************************************************************************	
    public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        ProfileBinder binder = loadBinder(binderId);
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        return loadProcessor(binder).addEntry(binder, definition, User.class, inputData, fileItems);
    }

    public void checkAddEntryAllowed(ProfileBinder binder) {
     	loadProcessor(binder).addEntry_accessControl(binder);    	   	
    }
     
    public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData, Map fileItems) 
   		throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        loadProcessor(binder).modifyEntry(binder, id, inputData, fileItems);
     }

    public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
			throws AccessControlException, WriteFilesException {
    	modifyEntry(binderId, id, inputData, new HashMap());
    }
    public void checkModifyEntryAllowed(Principal entry) {
        loadProcessor((ProfileBinder)entry.getParentBinder()).modifyEntry_accessControl(entry.getParentBinder(), entry);    	
    	
    }

    public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        return loadProcessor(binder).addEntry(binder, definition, Group.class, inputData, fileItems);
    }


    public void deleteEntry(Long binderId, Long principalId) {
        ProfileBinder binder = loadBinder(binderId);
       loadProcessor(binder).deleteEntry(binder, principalId);    	
    }
 
    public void checkDeleteEntryAllowed(Principal entry) {
    	loadProcessor((ProfileBinder)entry.getParentBinder()).deleteEntry_accessControl(entry.getParentBinder(), entry);    	
    	
    }
    
    public void index(Long binderId) {
        ProfileBinder binder = loadBinder(binderId);
        loadProcessor(binder).indexBinder(binder);
    }

    public Map getUsers(Long binderId) {
    	return getUsers(binderId, DEFAULT_MAX_ENTRIES);
    }
    public Map getUsers(Long binderId, int maxEntries) {
        return getUsers(binderId, maxEntries, null);        
   }
 
    public Map getUsers(Long binderId, int maxEntries, Document searchFilter) {
        ProfileBinder binder = loadBinder(binderId);
        return loadProcessor(binder).getBinderEntries(binder, userDocType, maxEntries, searchFilter);
        
   }
 
    public void modifyWorkflowState(Long binderId, Long entryId, Long tokenId, String toState) throws AccessControlException {
        ProfileBinder binder = loadBinder(binderId);
        loadProcessor(binder).modifyWorkflowState(binder, entryId, tokenId, toState);
    }


}

