/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile.impl;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.WriteFilesException;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.module.workflow.WorkflowModule;


public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
	private static final int DEFAULT_MAX_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	private String[] userDocType = {EntryIndexUtils.ENTRY_TYPE_USER};
	private String[] groupDocType = {EntryIndexUtils.ENTRY_TYPE_GROUP};
	
	protected DefinitionModule definitionModule;
    protected WorkflowModule workflowModule;
    
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
  
	protected WorkflowModule getWorkflowModule() {
		return workflowModule;
	}
	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
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
	   ProfileBinder pf = (ProfileBinder)getCoreDao().findBinderByName("_profiles", RequestContextHolder.getRequestContext().getZoneName());
	   return pf;
    }
    public Principal getEntry(Long binderId, Long principaId) {
        Principal entry;
            
        User user = RequestContextHolder.getRequestContext().getUser();
        entry = getCoreDao().loadPrincipal(principaId, user.getZoneName());        
        return entry;
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
    	List foundEntries = coreDao.loadUsers(users.keySet(), RequestContextHolder.getRequestContext().getZoneName());
    	EntryBuilder.updateEntries(foundEntries, users);

    }
    /**
     * Update users with one shared updates
     * @param ids - Collection of user ids
     * @param updates - Map indexed by attribute name, value is update value
     */
    public void bulkUpdateUsers(Collection ids, Map updates) {
    	List foundEntries = coreDao.loadUsers(ids, RequestContextHolder.getRequestContext().getZoneName());
    	EntryBuilder.applyUpdate(foundEntries, updates);

    }   
    /**
     * Update groups with their own updates
     * @param groups - Map indexed by group id, value is map of updates
     */    
    public void bulkUpdateGroups(Map groups) {
    	List foundEntries = coreDao.loadGroups(groups.keySet(), RequestContextHolder.getRequestContext().getZoneName());
       	EntryBuilder.updateEntries(foundEntries, groups);
    }
    /**
     * Update groups with one shared updates
     * @param ids - Collection of group ids
     * @param updates - Map indexed by attribute name, value is update value
     */    
    public void bulkUpdateGroups(Collection ids, Map updates) {
    	List foundEntries = coreDao.loadGroups(ids, RequestContextHolder.getRequestContext().getZoneName());
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
    	List result = coreDao.loadGroups(filter, RequestContextHolder.getRequestContext().getZoneName());
    	//TODO: check access
    	return result;
    }
 
    //***********************************************************************************************************	
    public Long addUser(String definitionId, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        ProfileBinder binder = getProfileBinder();
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	binder, ProfileCoreProcessor.PROCESSOR_KEY);
        return processor.addEntry(binder, definition, User.class, inputData, fileItems);
    }

     
    public void modifyPrincipal(Long id, Map inputData, Map fileItems) 
   		throws AccessControlException, WriteFilesException {
        ProfileBinder binder = getProfileBinder();
        ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
               	binder, ProfileCoreProcessor.PROCESSOR_KEY);
               processor.modifyEntry(binder, id, inputData, fileItems);
     }

    public Long addGroup(String definitionId, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        ProfileBinder binder = getProfileBinder();
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
               	binder, ProfileCoreProcessor.PROCESSOR_KEY);
                return processor.addEntry(binder, definition, Group.class, inputData, fileItems);
    }


    public void deletePrincipal(Long principalId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        ProfileBinder binder = (ProfileBinder)getCoreDao().findBinderByName("_profiles", user.getZoneName());
           
        ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
               	binder, ProfileCoreProcessor.PROCESSOR_KEY);
        processor.deleteEntry(binder, principalId);    	
    }
 
     
    public void index() {
    	ProfileBinder binder = getProfileBinder();
        ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
               	binder, ProfileCoreProcessor.PROCESSOR_KEY);
        processor.indexBinder(binder);

    }

    public Map getUsers() {
    	return getUsers(DEFAULT_MAX_ENTRIES);
    }
    public Map getUsers(int maxEntries) {
        ProfileBinder binder = getProfileBinder();
        ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
               	binder, ProfileCoreProcessor.PROCESSOR_KEY);
        return processor.getBinderEntries(binder, userDocType, maxEntries);
        
   }
 
    public void modifyWorkflowState(Long folderId, Long entryId, Long tokenId, String toState) throws AccessControlException {
        ProfileBinder binder = getProfileBinder();
       // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
        ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
               	binder, ProfileCoreProcessor.PROCESSOR_KEY);
       
        processor.modifyWorkflowState(binder, entryId, tokenId, toState);
    }


}

