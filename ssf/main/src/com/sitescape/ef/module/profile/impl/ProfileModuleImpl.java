/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile.impl;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.SortField;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Attachment;
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
import com.sitescape.ef.domain.ProfileBinder;

import com.sitescape.ef.lucene.Hits;
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
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.module.workflow.WorkflowModule;


public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
	private static final int DEFAULT_MAX_ENTRIES = ObjectKeys.FOLDER_MAX_PAGE_SIZE;
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
    public Principal getPrincipal(Long principaId) {
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
        Definition def = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        addUser_accessControl(binder);
        
        Map entryDataAll = addUser_toEntryData(binder, def, inputData, fileItems);
        Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        User entry = addUser_create();
        entry.setEntryDef(def);
        
        //need to set entry/folder information before generating file attachments
        //Attachments need folder info for AnyOwner
        addUser_fillIn(binder, entry, inputData, entryData);
        
        addUser_processFiles(binder, entry, fileData);
        
        addUser_preSave(binder, entry, inputData, entryData);
        
        addUser_save(entry);
        
        addUser_postSave(binder, entry, inputData, entryData);
        
        // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addUser_indexAdd(binder, entry, inputData);
      
        return entry.getId();
    }

     
    protected void addUser_accessControl(ProfileBinder binder) throws AccessControlException {
        accessControlManager.checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);        
    }
    
    protected void addUser_processFiles(ProfileBinder binder, User user, List fileData) 
    	throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileManager(), binder, user, fileData);
    }
    
    protected Map addUser_toEntryData(ProfileBinder binder, Definition def, Map inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
     protected User addUser_create() {
    	return new User();
    }
    
    protected void addUser_fillIn(ProfileBinder binder, User user, Map inputData, Map entryData) {  
    	add_fillIn(binder, user, inputData, entryData);
     }
    
    protected void addUser_preSave(ProfileBinder binder, User user, Map inputData, Map entryData) {
    }
    
    protected void addUser_save(User user) {
        getCoreDao().save(user);
    }
    
    protected void addUser_postSave(ProfileBinder binder, User user, Map inputData, Map entryData) {
    }
    

    protected void addUser_indexAdd(ProfileBinder binder, User user, Map inputData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocument(binder, user);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }


    //***********************************************************************************************************	
    public void modifyPrincipal(Long id, Map inputData, Map fileItems) 
   		throws AccessControlException, WriteFilesException {
        ProfileBinder binder = getProfileBinder();
     	Principal entry = coreDao.loadUser(id, binder.getZoneName());

     	modifyEntry_accessControl(binder, entry);

     	Map entryDataAll = modifyEntry_toEntryData(entry, inputData, fileItems);
     	Map entryData = (Map) entryDataAll.get("entryData");
     	List fileData = (List) entryDataAll.get("fileData");
     	if (inputData.containsKey("displayStyle")) {
     		entryData.put("displayStyle", inputData.get("displayStyle"));
     	}
     	modifyEntry_processFiles(binder, entry, fileData);
     	
     	modifyEntry_fillIn(binder, entry, inputData, entryData);
                   
     	modifyEntry_postFillIn(binder, entry, inputData, entryData);
        
     	modifyEntry_indexAdd(binder, entry, inputData);
      }

     protected void modifyEntry_accessControl(ProfileBinder binder, Principal entry) throws AccessControlException {
 		// Check if the user has "read" access to the workspace.
         getAccessControlManager().checkAcl(binder, AccessType.READ);
             
         // Check if the user has "write" access to the particular entry.
         //getAccessControlManager().checkAcl(ws, user, AccessType.WRITE);
     }
     protected void modifyEntry_processFiles(ProfileBinder binder, Principal entry, List fileData) 
         throws WriteFilesException {
  	   EntryBuilder.writeFiles(getFileManager(), binder, entry, fileData);
     }
     protected Map modifyEntry_toEntryData(Principal entry, Map inputData, Map fileItems) {
  	   //Call the definition processor to get the entry data to be stored
         return getDefinitionModule().getEntryData(entry.getEntryDef(), inputData, fileItems);
     }
     protected void modifyEntry_fillIn(ProfileBinder binder, Principal entry, Map inputData, Map entryData) {  
    	 entry.setModification(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
         EntryBuilder.updateEntry(entry, entryData);
     }

     protected void modifyEntry_postFillIn(ProfileBinder binder, Principal entry, Map inputData, Map entryData) {
     }
         
     protected void modifyEntry_indexAdd(ProfileBinder binder, Principal entry, Map inputData) {
             
         // Create an index document from the entry object.
         org.apache.lucene.document.Document indexDoc = buildIndexDocument(binder, entry);
             
         // Delete the document that's currently in the index.
         IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
             
         // Register the index document for indexing.
         IndexSynchronizationManager.addDocument(indexDoc);        
     }        
         
    //***********************************************************************************************************	
    public Long addGroup(String definitionId, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        ProfileBinder binder = getProfileBinder();
        Definition def = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        addGroup_accessControl(binder);
        
        Map entryDataAll = addGroup_toEntryData(binder, def, inputData, fileItems);
        Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        Group entry = addGroup_create();
        entry.setEntryDef(def);
        
        //need to set entry/folder information before generating file attachments
        //Attachments need folder info for AnyOwner
        addGroup_fillIn(binder, entry, inputData, entryData);
        
        addGroup_processFiles(binder, entry, fileData);
        
        addGroup_preSave(binder, entry, inputData, entryData);
        
        addGroup_save(entry);
        
        addGroup_postSave(binder, entry, inputData, entryData);
        
        // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addGroup_indexAdd(binder, entry, inputData);
      
        return entry.getId();
    }

     
    protected void addGroup_accessControl(ProfileBinder binder) throws AccessControlException {
        accessControlManager.checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);        
    }
    
    protected void addGroup_processFiles(ProfileBinder binder, Group group, List fileData) 
    	throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileManager(), binder, group, fileData);
    }
    
    protected Map addGroup_toEntryData(ProfileBinder binder, Definition def, Map inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
     protected Group addGroup_create() {
    	return new Group();
    }
    
    protected void addGroup_fillIn(ProfileBinder binder, Group group, Map inputData, Map entryData) {  
    	add_fillIn(binder, group, inputData, entryData);
 
    }
    
    protected void addGroup_preSave(ProfileBinder binder, Group group, Map inputData, Map entryData) {
    }
    
    protected void addGroup_save(Group group) {
        getCoreDao().save(group);
    }
    
    protected void addGroup_postSave(ProfileBinder binder, Group group, Map inputData, Map entryData) {
    }
    

    protected void addGroup_indexAdd(ProfileBinder binder, Group group, Map inputData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocument(binder, group);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }

   //***********************************************************************************************************	
    //***********************************************************************************************************	
    public void deletePrincipal(Long principaId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        ProfileBinder pf = (ProfileBinder)getCoreDao().findBinderByName("_profiles", user.getZoneName());
           
        Principal entry = getCoreDao().loadPrincipal(principaId, user.getZoneName());        
        deleteEntry_accessControl(pf, entry);
        deleteEntry_preDelete(pf, entry);
        deleteEntry_workflow(pf, entry);
        deleteEntry_processFiles(pf, entry);
        deleteEntry_delete(pf, entry);
        deleteEntry_postDelete(pf, entry);
        deleteEntry_indexDel(pf, entry);
   	
    }
    protected void deleteEntry_accessControl(ProfileBinder binder, Principal entry) {
        getAccessControlManager().checkOperation(binder, WorkAreaOperation.DELETE_ENTRIES);
        
        getAccessControlManager().checkAcl(binder, entry, AccessType.DELETE);
    }
    protected void deleteEntry_preDelete(ProfileBinder binder, Principal entry) {
    }
        
    protected void deleteEntry_workflow(ProfileBinder binder, Principal entry) {
    	getWorkflowModule().deleteEntryWorkflow(binder, entry);
    }
    protected void deleteEntry_processFiles(ProfileBinder binder, Principal entry) {
    	getFileManager().deleteFiles(binder, entry);
    }
    
    protected void deleteEntry_delete(ProfileBinder binder, Principal entry) {
    	getCoreDao().delete(entry);   
    }
    protected void deleteEntry_postDelete(ProfileBinder binder, Principal entry) {
    }

    protected void deleteEntry_indexDel(ProfileBinder binder, Principal entry) {
        // Delete the document that's currently in the index.
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
    }

   //***********************************************************************************************************
    public void deleteIndexEntries(ProfileBinder binder) {
    	FolderEntry entry;
    	
    	index_accessControl(binder);
    	
        //iterate through results
        	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        try {	            
	        logger.info("Indexing (" + binder.getId().toString() + ") ");
	        
	        // Delete the document that's currently in the index.
	        Term delTerm = new Term(EntryIndexUtils.BINDER_ID_FIELD, binder.getId().toString());
	        luceneSession.deleteDocuments(delTerm);
	            
        } finally {
	        luceneSession.close();
	    }
 
    }
    
    public void index() {
    	Principal entry;
 	   ProfileBinder binder = getProfileBinder();

    	index_accessControl(binder);
    	deleteIndexEntries(binder);
    	SFQuery query = getCoreDao().queryUsers(new FilterControls(), binder.getZoneName());
        //iterate through results
        LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        try {
 	        while (query.hasNext()) {
	            Object obj = query.next();
	            if (obj instanceof Object[])
	                obj = ((Object [])obj)[0];
	            entry = (Principal)obj;
	            // Create an index document from the entry object.
	            org.apache.lucene.document.Document indexDoc = buildIndexDocument(binder, entry);
	            
	            logger.info("Indexing (User) " + entry.getName() + ": " + indexDoc.toString());
	            
	            // Delete the document that's currently in the index.
	            luceneSession.deleteDocument(entry.getIndexDocumentUid());
	            
	            // Register the index document for indexing.
	            luceneSession.addDocument(indexDoc);        
	        }
        	query.close();
 	        query = getCoreDao().queryGroups(new FilterControls(), binder.getZoneName());
 	        while (query.hasNext()) {
	            Object obj = query.next();
	            if (obj instanceof Object[])
	                obj = ((Object [])obj)[0];
	            entry = (Principal)obj;
	            // Create an index document from the entry object.
	            org.apache.lucene.document.Document indexDoc = buildIndexDocument(binder, entry);
	            
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
    protected void index_accessControl(ProfileBinder binder) {
    	getAccessControlManager().checkAcl(binder, AccessType.READ);
    }
    

//  ***********************************************************************************************************
    public Map getUsers() {
    	return getUsers(DEFAULT_MAX_ENTRIES);
    }
    public Map getUsers(int maxEntries) {
        int count=0;
        Field fld;
        ProfileBinder binder = getProfileBinder();
        //check access to folder - might be able to get rid of this
        getUsers_accessControl(binder);
        //validate entry count
        maxEntries = getUsers_maxEntries(maxEntries); 
        //do actual search index query
        Hits hits = getUsers_doSearch(binder, maxEntries);
        //iterate through results
        ArrayList childEntries = new ArrayList(hits.length());
        try {
 	        while (count < hits.length()) {
	            HashMap ent = new HashMap();
	            Document doc = hits.doc(count);
	            //enumerate thru all the returned fields, and add to the map object
	            Enumeration flds = doc.fields();
	            while (flds.hasMoreElements()) {
	            	fld = (Field)flds.nextElement();
	            	if (fld.name().toLowerCase().indexOf("date") > 0) 
	            		ent.put(fld.name(),DateField.stringToDate(fld.stringValue()));
	            	else
	            		ent.put(fld.name(),fld.stringValue());
	            }
	            childEntries.add(ent);
	            ++count;
	            
	        }
        } finally {
        }
        List users = loadEntryHistoryLuc(childEntries);
        // walk the entries, and stuff in the user object.
        for (int i = 0; i < childEntries.size(); i++) {
        	Principal p;
        	HashMap child = (HashMap)childEntries.get(i);
        	if (child.get("_creatorId") != null) {
        		child.put("_principal", getPrincipal(users,(String)child.get("_creatorId")));
        	}        	
        }
       	Map model = new HashMap();
        model.put(ObjectKeys.BINDER, binder);      
        model.put(ObjectKeys.ENTRIES, childEntries);
        model.put(ObjectKeys.TOTAL_SEARCH_COUNT, new Integer(hits.length()));
        return model;
   }
    private Principal getPrincipal(List users, String userId) {
    	Principal p;
    	for (int i=0; i<users.size(); i++) {
    		p = (Principal)users.get(i);
    		if (p.getId().toString().equalsIgnoreCase(userId)) return p;
    	}
    	return null;
    }
    protected List loadEntryHistoryLuc(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        HashMap entry;
        while (iter.hasNext()) {
            entry = (HashMap)iter.next();
            if (entry.get("_creatorId") != null)
        	    ids.add(entry.get("_creatorId"));
            if (entry.get("_modificationId") != null) 
        		ids.add(entry.get("_modificationId"));
        }
        return getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     }   
    protected void getUsers_accessControl(ProfileBinder binder) {
        getAccessControlManager().checkAcl(binder, AccessType.READ);    	
    }
    protected int getUsers_maxEntries(int maxEntries) {
        if (maxEntries == 0) maxEntries = DEFAULT_MAX_ENTRIES;
        return maxEntries;
    }
    
    protected Hits getUsers_doSearch(ProfileBinder binder, int maxResults) {
       	Hits hits = null;
       	// Build the query
    	org.dom4j.Document qTree = DocumentHelper.createDocument();
    	Element rootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
    	Element boolElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
    	boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
    	
    	//Look only for entryType=entry
    	Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(BasicIndexUtils.DOC_TYPE_USER);
    	
    	//Look only for entryType=entry
    	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.BINDER_ID_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(binder.getId().toString());

    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder();
    	SearchObject so = qb.buildQuery(qTree);
    	
    	//Set the sort order
    	SortField[] fields = new SortField[1];
    	boolean descend = true;
    	fields[0] = new SortField(EntryIndexUtils.MODIFICATION_DATE_FIELD, descend);
    	so.setSortBy(fields);
    	//Set the sort order
//    	SortField[] fields = new SortField[1];
 //   	boolean descend = true;
  //  	fields[0] = new SortField(EntryIndexUtils.TITLE_FIELD, descend);
   // 	so.setSortBy(fields);
    	
    	System.out.println("Query is: " + qTree.asXML());
    	System.out.println("Query is: " + so.getQuery().toString());
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        hits = luceneSession.search(so.getQuery(),so.getSortBy(),0,maxResults);
        }
        finally {
            luceneSession.close();
        }
    	
        return hits;
     
    }


     
    //***********************************************************************************************************

    private void add_fillIn(ProfileBinder binder, Principal entry, Map inputData, Map entryData) {  
        User current = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(current));
        entry.setModification(current.getCreation());
        entry.setZoneName(binder.getZoneName());
                
        EntryBuilder.buildEntry(entry, entryData);
    }   
	protected org.apache.lucene.document.Document buildIndexDocument(ProfileBinder binder, Principal entry) {
		org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
		// Add uid
		BasicIndexUtils.addUid(indexDoc, entry.getIndexDocumentUid());
    
		// Add doc type
		if (entry instanceof User) {
			User user = (User)entry;
			BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_USER);
			IndexUtils.addName(indexDoc, user);
	        IndexUtils.addFirstName(indexDoc, user);
	        IndexUtils.addMiddleName(indexDoc, user);
	        IndexUtils.addLastName(indexDoc, user);
	        IndexUtils.addEmailAddress(indexDoc, user);
		} else {
			BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_GROUP);
	        IndexUtils.addName(indexDoc, (Group)entry);
			
		}
        // Add ACL field. We only need to index ACLs for read access.
        BasicIndexUtils.addReadAcls(indexDoc, binder, entry, getAclManager());
		// Add creation-date
		EntryIndexUtils.addCreationDate(indexDoc, entry);
    
		// Add modification-date
		EntryIndexUtils.addModificationDate(indexDoc,entry);
    
		// Add creator id
		EntryIndexUtils.addCreationPrincipalId(indexDoc,entry);
    
		// Add Modification Principal Id
		EntryIndexUtils.addModificationPrincipalId(indexDoc,entry);
    
		EntryIndexUtils.addParentBinder(indexDoc, entry);
		// Add Doc Id
		EntryIndexUtils.addDocId(indexDoc, entry);
   
		// Add Doc title
		EntryIndexUtils.addTitle(indexDoc, entry);
    
		// Add command definition
		EntryIndexUtils.addCommandDefinition(indexDoc, entry); 
	       // Add data fields driven by the entry's definition object. 
//      getDefinitionModule().addIndexFieldsForEntry(indexDoc, ws, entry);
      
      
      // add the events
      EntryIndexUtils.addEvents(indexDoc, entry);
      return indexDoc;
		
	}    
}

