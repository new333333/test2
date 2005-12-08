/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile.impl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;


import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
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
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.WorkAreaOperation;


public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
    protected DefinitionModule definitionModule;
    
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
    
   public Map getProfile(Long userId) {
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
    public List getUsers() {
    	FilterControls filter = new FilterControls();
    	filter.setOrderBy(new OrderBy("title"));
    	List result = coreDao.loadUsers(filter, RequestContextHolder.getRequestContext().getZoneName());
    	//TODO: check access
    	return result;
    }
    //***********************************************************************************************************	
    public Long addUser(String definitionId, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
  		User user = RequestContextHolder.getRequestContext().getUser();
        Workspace ws = getCoreDao().findTopWorkspace(user.getZoneName());
        Definition def = getCoreDao().loadDefinition(definitionId, user.getZoneName());
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
    	add_fillIn(ws, user, inputData, entryData);
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
        
    	buildIndexDocument(indexDoc, entry);
      
        IndexUtils.addName(indexDoc, entry);
        IndexUtils.addFirstName(indexDoc, entry);
        IndexUtils.addMiddleName(indexDoc, entry);
        IndexUtils.addLastName(indexDoc, entry);
        IndexUtils.addEmailAddress(indexDoc, entry);
        
        // Add data fields driven by the entry's definition object. 
//        getDefinitionModule().addIndexFieldsForEntry(indexDoc, ws, entry);
        
        // Add ACL field. We only need to index ACLs for read access.
//        IndexUtils.addReadAcls(indexDoc, folder, entry, getAclManager());
        
        // add the events
        EntryIndexUtils.addEvents(indexDoc, entry);
        
        return indexDoc;
    }
    //***********************************************************************************************************	
    public void modifyUser(Long id, Map inputData, Map fileItems) 
   		throws AccessControlException, WriteFilesException {
     	User user = coreDao.loadUser(id, RequestContextHolder.getRequestContext().getZoneName());
     	Workspace ws = getCoreDao().findTopWorkspace(user.getZoneName());

     	modifyUser_accessControl(ws,user);

     	Map entryDataAll = modifyUser_toEntryData(user, inputData, fileItems);
     	Map entryData = (Map) entryDataAll.get("entryData");
     	List fileData = (List) entryDataAll.get("fileData");
     	
     	modifyUser_processFiles(ws, user, fileData);
     	
     	modifyUser_fillIn(ws, user, inputData, entryData);
                   
     	modifyUser_postFillIn(ws, user, inputData, entryData);
        
     	modifyUser_indexAdd(ws, user, inputData);
      }

     protected void modifyUser_accessControl(Workspace ws, User user) throws AccessControlException {
         getAccessControlManager().checkOperation(ws, WorkAreaOperation.VIEW);
             
         // Check if the user has "write" access to the particular entry.
         //getAccessControlManager().checkAcl(ws, user, AccessType.WRITE);
     }
     protected void modifyUser_processFiles(Workspace ws, User user, List fileData) 
         throws WriteFilesException {
  	   EntryBuilder.writeFiles(getFileManager(), ws, user, fileData);
     }
     protected Map modifyUser_toEntryData(User user, Map inputData, Map fileItems) {
  	   //Call the definition processor to get the entry data to be stored
         return getDefinitionModule().getEntryData(user.getEntryDef(), inputData, fileItems);
     }
     protected void modifyUser_fillIn(Workspace ws, User user, Map inputData, Map entryData) {  
         user.setModification(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
         EntryBuilder.updateEntry(user, entryData);
     }

     protected void modifyUser_postFillIn(Workspace ws, User user, Map inputData, Map entryData) {
     }
         
     protected void modifyUser_indexAdd(Workspace ws, User user, Map inputData) {
             
         // Create an index document from the entry object.
         org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(ws, user);
             
         // Delete the document that's currently in the index.
         IndexSynchronizationManager.deleteDocument(user.getIndexDocumentUid());
             
         // Register the index document for indexing.
         IndexSynchronizationManager.addDocument(indexDoc);        
     }        
         
    //***********************************************************************************************************	
    public Long addGroup(String definitionId, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
  		User user = RequestContextHolder.getRequestContext().getUser();
        Workspace ws = getCoreDao().findTopWorkspace(user.getZoneName());
        Definition def = getCoreDao().loadDefinition(definitionId, user.getZoneName());
        addGroup_accessControl(ws);
        
        Map entryDataAll = addGroup_toEntryData(ws, def, inputData, fileItems);
        Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        Group entry = addGroup_create();
        entry.setEntryDef(def);
        
        //need to set entry/folder information before generating file attachments
        //Attachments need folder info for AnyOwner
        addGroup_fillIn(ws, entry, inputData, entryData);
        
        addGroup_processFiles(ws, entry, fileData);
        
        addGroup_preSave(ws, entry, inputData, entryData);
        
        addGroup_save(entry);
        
        addGroup_postSave(ws, entry, inputData, entryData);
        
        // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addGroup_indexAdd(ws, entry, inputData);
      
        return entry.getId();
    }

     
    protected void addGroup_accessControl(Workspace ws) throws AccessControlException {
        accessControlManager.checkOperation(ws, WorkAreaOperation.CREATE_ENTRIES);        
    }
    
    protected void addGroup_processFiles(Workspace ws, Group group, List fileData) 
    	throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileManager(), ws, group, fileData);
    }
    
    protected Map addGroup_toEntryData(Workspace ws, Definition def, Map inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
     protected Group addGroup_create() {
    	return new Group();
    }
    
    protected void addGroup_fillIn(Workspace ws, Group group, Map inputData, Map entryData) {  
    	add_fillIn(ws, group, inputData, entryData);
 
    }
    
    protected void addGroup_preSave(Workspace ws, Group group, Map inputData, Map entryData) {
    }
    
    protected void addGroup_save(Group group) {
        getCoreDao().save(group);
    }
    
    protected void addGroup_postSave(Workspace ws, Group group, Map inputData, Map entryData) {
    }
    

    protected void addGroup_indexAdd(Workspace ws, Group group, Map inputData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(ws, group);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }

   //***********************************************************************************************************	
   public void modifyGroup(Long id, Map inputData, Map fileItems) 
  		throws AccessControlException, WriteFilesException {
    	Group group = coreDao.loadGroup(id, RequestContextHolder.getRequestContext().getZoneName());
    	Workspace ws = getCoreDao().findTopWorkspace(group.getZoneName());

    	modifyGroup_accessControl(ws,group);

    	Map entryDataAll = modifyGroup_toEntryData(group, inputData, fileItems);
    	Map entryData = (Map) entryDataAll.get("entryData");
    	List fileData = (List) entryDataAll.get("fileData");
    	
    	modifyGroup_processFiles(ws, group, fileData);
    	
    	modifyGroup_fillIn(ws, group, inputData, entryData);
                  
    	modifyGroup_postFillIn(ws, group, inputData, entryData);
       
    	modifyGroup_indexAdd(ws, group, inputData);
         
    }

    protected void modifyGroup_accessControl(Workspace ws, Group group) throws AccessControlException {
        getAccessControlManager().checkOperation(ws, WorkAreaOperation.VIEW);
            
        // Check if the user has "write" access to the particular entry.
        //getAccessControlManager().checkAcl(ws, user, AccessType.WRITE);
    }
    protected void modifyGroup_processFiles(Workspace ws, Group group, List fileData) 
        throws WriteFilesException {
 	   EntryBuilder.writeFiles(getFileManager(), ws, group, fileData);
    }
    protected Map modifyGroup_toEntryData(Group group, Map inputData, Map fileItems) {
 	   //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(group.getEntryDef(), inputData, fileItems);
    }
    protected void modifyGroup_fillIn(Workspace ws, Group group, Map inputData, Map entryData) {  
    	group.setModification(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
        EntryBuilder.updateEntry(group, entryData);
    }

    protected void modifyGroup_postFillIn(Workspace ws, Group group, Map inputData, Map entryData) {
    }
        
    protected void modifyGroup_indexAdd(Workspace ws, Group group, Map inputData) {
            
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(ws, group);
            
        // Delete the document that's currently in the index.
        IndexSynchronizationManager.deleteDocument(group.getIndexDocumentUid());
            
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }        
    //***********************************************************************************************************	
  
    public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Workspace ws, Group entry) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        
    	buildIndexDocument(indexDoc, entry);
        
        IndexUtils.addName(indexDoc, entry);
        
        // Add data fields driven by the entry's definition object. 
//        getDefinitionModule().addIndexFieldsForEntry(indexDoc, ws, entry);
        
        // Add ACL field. We only need to index ACLs for read access.
//        IndexUtils.addReadAcls(indexDoc, folder, entry, getAclManager());
        
        // add the events
        EntryIndexUtils.addEvents(indexDoc, entry);
        
        return indexDoc;
    }
   //***********************************************************************************************************
    
    public void index() {
    	Principal entry;
    	String zoneName = RequestContextHolder.getRequestContext().getZoneName();
    	Workspace ws = getCoreDao().findTopWorkspace(zoneName);

    	index_accessControl(ws);
    	
    	SFQuery query = getCoreDao().queryUsers(new FilterControls(), zoneName);
        //iterate through results
        LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        try {
 	        while (query.hasNext()) {
	            Object obj = query.next();
	            if (obj instanceof Object[])
	                obj = ((Object [])obj)[0];
	            entry = (Principal)obj;
	            // Create an index document from the entry object.
	            org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(ws, (User)entry);
	            
	            logger.info("Indexing (User) " + entry.getName() + ": " + indexDoc.toString());
	            
	            // Delete the document that's currently in the index.
	            luceneSession.deleteDocument(entry.getIndexDocumentUid());
	            
	            // Register the index document for indexing.
	            luceneSession.addDocument(indexDoc);        
	        }
        	query.close();
 	        query = getCoreDao().queryGroups(new FilterControls(), zoneName);
 	        while (query.hasNext()) {
	            Object obj = query.next();
	            if (obj instanceof Object[])
	                obj = ((Object [])obj)[0];
	            entry = (Principal)obj;
	            // Create an index document from the entry object.
	            org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(ws, (Group)entry);
	            
	            logger.info("Indexing (Group) " + entry.getName() + ": " + indexDoc.toString());
	            
	            // Delete the document that's currently in the index.
	            luceneSession.deleteDocument(entry.getIndexDocumentUid());
	            
	            // Register the index document for indexing.
	            luceneSession.addDocument(indexDoc);        
	        }
        } finally {
	        query.close();
	        luceneSession.close();
	    }
    }
    protected void index_accessControl(Workspace ws) {
    	getAccessControlManager().checkAcl(ws, AccessType.READ);
    }
    //***********************************************************************************************************

    private void add_fillIn(Workspace ws, Principal entry, Map inputData, Map entryData) {  
        User current = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(current));
        entry.setModification(current.getCreation());
        entry.setZoneName(ws.getZoneName());
                
        EntryBuilder.buildEntry(entry, entryData);
    }   
	private void buildIndexDocument(org.apache.lucene.document.Document indexDoc, Principal entry) {
		// Add uid
		BasicIndexUtils.addUid(indexDoc, entry.getIndexDocumentUid());
    
		// Add doc type
		if (entry instanceof User)
			BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_USER);
		else
			BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_GROUP);
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
	}    
}

