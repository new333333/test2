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
import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.profile.index.IndexUtils;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.WriteFilesException;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.file.FileManager;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlNonCodedException;
import com.sitescape.ef.security.function.WorkAreaOperation;

public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
    protected DefinitionModule definitionModule;
    
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
    
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
    	   throw new AccessControlNonCodedException();
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
    //***********************************************************************************************************	
    public Long addUser(Definition def, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
  		User user = RequestContextHolder.getRequestContext().getUser();
        Workspace ws = getCoreDao().findTopWorkspace(user.getZoneName());
        addUser_accessControl(ws);
        
        Map entryDataAll = addUser_toEntryData(ws, def, inputData, fileItems);
        Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        User entry = addUser_create();
        entry.setEntryDef(def);
        
        //need to set entry/folder information before generating file attachments
        //Attachments need folder info for AnyOwner
        addUser_fillIn(ws, entry, inputData, entryData);
        
        addUser_processFiles(ws, entry, fileData);
        
        addUser_preSave(ws, entry, inputData, entryData);
        
        addUser_save(entry);
        
        addUser_postSave(ws, entry, inputData, entryData);
        
        // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addUser_indexAdd(ws, entry, inputData);
      
        return entry.getId();
    }

     
    protected void addUser_accessControl(Workspace ws) throws AccessControlException {
        accessControlManager.checkOperation(ws, WorkAreaOperation.CREATE_ENTRIES);        
    }
    
    protected void addUser_processFiles(Workspace ws, User user, List fileData) 
    	throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileManager(), ws, user, fileData);
    }
    
    protected Map addUser_toEntryData(Workspace ws, Definition def, Map inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
     protected User addUser_create() {
    	return new User();
    }
    
    protected void addUser_fillIn(Workspace ws, User user, Map inputData, Map entryData) {  
        User current = RequestContextHolder.getRequestContext().getUser();
        user.setCreation(new HistoryStamp(current));
        user.setModification(current.getCreation());
        user.setZoneName(ws.getZoneName());
                
        EntryBuilder.buildEntry(user, entryData);
    }
    
    protected void addUser_preSave(Workspace ws, User user, Map inputData, Map entryData) {
    }
    
    protected void addUser_save(User user) {
        getCoreDao().save(user);
    }
    
    protected void addUser_postSave(Workspace ws, User user, Map inputData, Map entryData) {
    }
    

    protected void addUser_indexAdd(Workspace ws, User user, Map inputData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(ws, user);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }

    public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Workspace ws, User entry) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        
        // Add uid
        BasicIndexUtils.addUid(indexDoc, entry.getIndexDocumentUid());
        
        // Add doc type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_USER);
               
        // Add creation-date
        EntryIndexUtils.addCreationDate(indexDoc, entry);
        
        // Add modification-date
        EntryIndexUtils.addModificationDate(indexDoc,entry);
        
        // Add creator id
        EntryIndexUtils.addCreationPrincipleId(indexDoc,entry);
        
        // Add Modification Principle Id
        EntryIndexUtils.addModificationPrincipleId(indexDoc,entry);
        
        // Add Doc Id
        EntryIndexUtils.addDocId(indexDoc, entry);
       
        // Add Doc title
        EntryIndexUtils.addTitle(indexDoc, entry);
        
        // Add command definition
        EntryIndexUtils.addCommandDefinition(indexDoc, entry); 
        
        IndexUtils.addName(indexDoc, entry);
        // Add data fields driven by the entry's definition object. 
        getDefinitionModule().addIndexFieldsForEntry(indexDoc, ws, entry);
        
        // Add ACL field. We only need to index ACLs for read access.
//        IndexUtils.addReadAcls(indexDoc, folder, entry, getAclManager());
        
        // add the events
        EntryIndexUtils.addEvents(indexDoc, entry);
        
        return indexDoc;
    }
       
}

