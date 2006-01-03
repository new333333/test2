package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.Long;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.WriteFilesException;
import com.sitescape.ef.module.workflow.WorkflowModule;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractEntryProcessor extends CommonDependencyInjection 
	implements EntryProcessor {
    
	private static final int DEFAULT_MAX_CHILD_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
    protected DefinitionModule definitionModule;
 
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}

	private WorkflowModule workflowModule;
    
	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	protected WorkflowModule getWorkflowModule() {
		return workflowModule;
	}


    //***********************************************************************************************************	
    public Long addEntry(Binder binder, Definition def, Class clazz, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        
        addEntry_accessControl(binder);
        
        Map entryDataAll = addEntry_toEntryData(binder, def, inputData, fileItems);
        Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        AclControlledEntry entry = addEntry_create(clazz);
        entry.setEntryDef(def);
        
        //need to set entry/binder information before generating file attachments
        //Attachments need binder info for AnyOwner
        addEntry_fillIn(binder, entry, inputData, entryData);
        
        addEntry_processFiles(binder, entry, fileData);
        
        addEntry_preSave(binder, entry, inputData, entryData);
        
        addEntry_save(entry);
        
        addEntry_postSave(binder, entry, inputData, entryData);
        
        // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addEntry_indexAdd(binder, entry, inputData);
        
        //After the entry is successfully added, start up any associated workflows
        addEntry_startWorkflow(entry);
        
        return entry.getId();
    }

     
    protected void addEntry_accessControl(Binder binder) throws AccessControlException {
        accessControlManager.checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);        
    }
    
    protected void addEntry_processFiles(Binder binder, AclControlledEntry entry, List fileData) 
    	throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileManager(), binder, entry, fileData);
    }
    
    protected Map addEntry_toEntryData(Binder binder, Definition def, Map inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
    protected AclControlledEntry addEntry_create(Class clazz)  {
    	try {
    		return (AclControlledEntry)clazz.newInstance();
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    protected void addEntry_fillIn(Binder binder, AclControlledEntry entry, Map inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(user));
        entry.setModification(entry.getCreation());
        
        
        // The entry inherits acls from the parent by default. 
        getAclManager().doInherit(binder, (AclControlledEntry) entry);
        
        EntryBuilder.buildEntry(entry, entryData);
    }
    
    protected void addEntry_preSave(Binder binder, AclControlledEntry entry, Map inputData, Map entryData) {
    }
    
    protected void addEntry_save(AclControlledEntry entry) {
        getCoreDao().save(entry);
    }
    
    protected void addEntry_postSave(Binder binder, AclControlledEntry entry, Map inputData, Map entryData) {
    }
    
    protected void addEntry_indexAdd(Binder binder, AclControlledEntry entry, Map inputData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(binder, entry);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }
    
    protected void addEntry_startWorkflow(AclControlledEntry entry) {
    	Binder binder = entry.getParentBinder();
    	Map workflowAssociations = (Map) binder.getProperty(ObjectKeys.BINDER_WORKFLOW_ASSOCIATIONS);
    	if (workflowAssociations != null) {
    		//See if the entry definition type has an associated workflow
    		Definition entryDef = entry.getEntryDef();
    		if (entryDef != null) {
	    		if (workflowAssociations.containsKey(entryDef.getId()) && 
	    				!workflowAssociations.get(entryDef.getId()).equals("")) {
	    			Definition wfDef = getDefinitionModule().getDefinition((String)workflowAssociations.get(entryDef.getId()));
	    			getWorkflowModule().startWorkflow(entry, wfDef);
	    		}
    		}
    	}
    }
 	

   //***********************************************************************************************************
    public Long modifyEntry(Binder binder, Long entryId, Map inputData, Map fileItems) 
    		throws AccessControlException, WriteFilesException {
		AclControlledEntry entry = modifyEntry_load(binder, entryId);
	    modifyEntry_accessControl(binder, entry);
	
	    Map entryDataAll = modifyEntry_toEntryData(entry, inputData, fileItems);
	    Map entryData = (Map) entryDataAll.get("entryData");
	    List fileData = (List) entryDataAll.get("fileData");
	    
	    modifyEntry_processFiles(binder, entry, fileData);
	    
	    modifyEntry_fillIn(binder, entry, inputData, entryData);
	                
	    modifyEntry_postFillIn(binder, entry, inputData, entryData);
	    
	    modifyEntry_indexAdd(binder, entry, inputData);
	    return entry.getId();
	}
	public Long modifyEntryData(Binder binder, Long entryId, Map entryData) 
			throws AccessControlException {
    	AclControlledEntry entry = modifyEntry_load(binder, entryId);
	    modifyEntry_accessControl(binder, entry);
	
        EntryBuilder.updateEntry(entry, entryData);
		return entry.getId();
	}
    protected AclControlledEntry modifyEntry_load(Binder binder, Long entryId) {
    	return entry_load(binder, entryId);
    	
    }
    protected void modifyEntry_accessControl(Binder binder, AclControlledEntry entry) throws AccessControlException {
        try {
        	//Check if the user is allowed to modify entries in the binder
        	getAccessControlManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES);
        } catch(AccessControlException ace) {
        	//The user doesn't have binder level priv's
            // Check if the user has "read" access to the particular entry.
            getAccessControlManager().checkAcl(binder, entry, AccessType.READ);
            // Check if the user has "write" access to the particular entry.
            getAccessControlManager().checkAcl(binder, entry, AccessType.WRITE);
        }
    }
    
    protected void modifyEntry_processFiles(Binder binder, AclControlledEntry entry, List fileData) 
    throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileManager(), binder, entry, fileData);
    }
    protected Map modifyEntry_toEntryData(AclControlledEntry entry, Map inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(entry.getEntryDef(), inputData, fileItems);
    }
    protected void modifyEntry_fillIn(Binder binder, AclControlledEntry entry, Map inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setModification(new HistoryStamp(user));
        EntryBuilder.updateEntry(entry, entryData);

    }

    protected void modifyEntry_postFillIn(Binder binder, AclControlledEntry entry, Map inputData, Map entryData) {
    }
    
    protected void modifyEntry_indexAdd(Binder binder, AclControlledEntry entry, Map inputData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(binder, entry);
        
        // Delete the document that's currently in the index.
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }
    
    //***********************************************************************************************************
    public void modifyWorkflowState(Binder binder, Long entryId, Long tokenId, String toState) {

    	Entry entry = entry_load(binder, entryId);
 		
		//Find the workflowState
		WorkflowState ws = entry.getWorkflowState(tokenId);
 		if (ws != null) {
			//We have the workflowState of the current state
			//See if the user is allowed to go to this state
			Map transitions = ws.getManualTransitions();
			if (transitions.containsKey(toState)) {
				//It is ok to transition to this state; go do it
				getWorkflowModule().modifyWorkflowState(ws.getTokenId(), ws.getState(), toState);
			}
		}
    }
 

    //***********************************************************************************************************
    
    public void indexBinder(Binder binder) {
    	
    	indexBinder_accessControl(binder);
     	// this is just here until we get our indexes in sync with
    	// the db.  (Early in development, they're not...
   		//iterate through results
   		indexBinder_deleteEntries(binder);
	   	SFQuery query = indexBinder_getQuery(binder);
	   	
	   	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
       	try {       
       		while (query.hasNext()) {
       			Object obj = query.next();
       			if (obj instanceof Object[])
       				obj = ((Object [])obj)[0];
       			AclControlledEntry entry = (AclControlledEntry)obj;
       			// 	Create an index document from the entry object.
       			org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(binder, entry);
	            
        		logger.info("Indexing (" + binder.getId().toString() + ") " + entry.toString() + ": " + indexDoc.toString());
	            
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
    protected void indexBinder_accessControl(Binder binder) {
    	getAccessControlManager().checkAcl(binder, AccessType.READ);
    }

    protected void indexBinder_deleteEntries(Binder binder) {
        //iterate through results
       	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        try {	            
	        logger.info("Indexing (" + binder.getId().toString() + ") ");
	        
	        // Delete the document that's currently in the index.
	        Term delTerm = indexBinder_getDeleteEntriesTerm(binder);
	        luceneSession.deleteDocuments(delTerm);
	            
        } finally {
	        luceneSession.close();
	    }
 
    }
    
    protected Term indexBinder_getDeleteEntriesTerm(Binder binder) {
        return new Term(EntryIndexUtils.BINDER_ID_FIELD, binder.getId().toString());
    }
   	protected abstract SFQuery indexBinder_getQuery(Binder binder);
 

    //***********************************************************************************************************
    public Map getBinderEntries(Binder binder, String[] entryTypes, int maxChildEntries) {
        int count=0;
        Field fld;
        
        //check access to binder - might be able to get rid of this
        getBinderEntries_accessControl(binder);
        //validate entry count
        maxChildEntries = getBinderEntries_maxEntries(maxChildEntries); 
        //do actual search index query
        Hits hits = getBinderEntries_doSearch(binder, entryTypes, maxChildEntries);
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
        	if (child.get(EntryIndexUtils.CREATORID_FIELD) != null) {
        		child.put(WebKeys.PRINCIPAL, getPrincipal(users,(String)child.get(EntryIndexUtils.CREATORID_FIELD)));
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
    protected void getBinderEntries_accessControl(Binder binder) {
        getAccessControlManager().checkAcl(binder, AccessType.READ);    	
    }
    protected int getBinderEntries_maxEntries(int maxChildEntries) {
        if (maxChildEntries == 0) maxChildEntries = DEFAULT_MAX_CHILD_ENTRIES;
        return maxChildEntries;
    }
    protected boolean getBinderEntries_accessControl(Binder binder, AclControlled obj) {
    	return getAccessControlManager().testAcl(binder, (AclControlled) obj, AccessType.READ);
    }
    
    protected Hits getBinderEntries_doSearch(Binder binder, String [] entryTypes, int maxResults) {
       	Hits hits = null;
       	// Build the query
    	org.dom4j.Document qTree = getBinderEntries_getSearchDocument(binder, entryTypes);
    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder();
    	SearchObject so = qb.buildQuery(qTree);
    	
    	//Set the sort order
    	SortField[] fields = getBinderEntries_getSortFields(binder); 
    	so.setSortBy(fields);
    	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
    	
    	System.out.println("Query is: " + qTree.asXML());
    	System.out.println("Query is: " + soQuery.toString());
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        hits = luceneSession.search(soQuery,so.getSortBy(),0,maxResults);
        }
        finally {
            luceneSession.close();
        }
    	
        return hits;
     
    }
    protected org.dom4j.Document getBinderEntries_getSearchDocument(Binder binder, String [] entryTypes) {
    	org.dom4j.Document qTree = DocumentHelper.createDocument();
    	Element rootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
    	Element boolElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
    	boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
    	
    	
    	//Look only for binderId=binder
    	Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.BINDER_ID_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(binder.getId().toString());
    	return qTree;
    }
   	protected SortField[] getBinderEntries_getSortFields(Binder binder) {
   		SortField[] fields = new SortField[1];
    	boolean descend = true;
    	fields[0] = new SortField(EntryIndexUtils.MODIFICATION_DATE_FIELD, descend);
    	return fields;
   	}
    

    //***********************************************************************************************************
    public AclControlledEntry getEntry(Binder parentBinder, Long entryId, int type) {
    	//get the entry
    	AclControlledEntry entry = getEntry_load(parentBinder, entryId);
        //check access
        getEntry_accessControl(parentBinder, entry);
        //Initialize users
        loadEntryHistory(entry);
        return entry;
    }
          
    protected AclControlledEntry getEntry_load(Binder binder, Long entryId) {
    	return entry_loadFull(binder, entryId);
    }
             
    protected void getEntry_accessControl(Binder parentBinder, AclControlledEntry entry) {
           
        // Check if the user has the privilege to view the entries in the 
        // work area
    	getBinderEntries_accessControl(parentBinder);
              
        // Check if the user has "read" access to the particular entry.
        getAccessControlManager().checkAcl(parentBinder, entry, AccessType.READ);
        
        // TODO If there is a workflow attached to the entry, we must perform
        // additional access check based on the state the entry is currently in.
    }
   //***********************************************************************************************************   
    public void deleteEntry(Binder parentBinder, Long entryId) {
    	AclControlledEntry entry = deleteEntry_load(parentBinder, entryId);
        deleteEntry_accessControl(parentBinder, entry);
        deleteEntry_preDelete(parentBinder, entry);
        deleteEntry_workflow(parentBinder, entry);
        deleteEntry_processFiles(parentBinder, entry);
        deleteEntry_delete(parentBinder, entry);
        deleteEntry_postDelete(parentBinder, entry);
        deleteEntry_indexDel(entry);
   	
    }
    protected AclControlledEntry deleteEntry_load(Binder binder, Long entryId) {
    	return entry_load(binder, entryId);
    }
        
    protected void deleteEntry_accessControl(Binder parentBinder, AclControlledEntry entry) {
        getAccessControlManager().checkOperation(parentBinder, WorkAreaOperation.DELETE_ENTRIES);
        
        getAccessControlManager().checkAcl(parentBinder, entry, AccessType.DELETE);
    }
    protected void deleteEntry_preDelete(Binder parentBinder, AclControlledEntry entry) {
    }
        
    protected void deleteEntry_workflow(Binder parentBinder, AclControlledEntry entry) {
    	getWorkflowModule().deleteEntryWorkflow(parentBinder, entry);
    }
    
    protected void deleteEntry_processFiles(Binder parentBinder, AclControlledEntry entry) {
    	getFileManager().deleteFiles(parentBinder, entry);
    }
    
    protected void deleteEntry_delete(Binder parentBinder, AclControlledEntry entry) {
        getCoreDao().delete(entry);   
    }
    protected void deleteEntry_postDelete(Binder parentBinder, AclControlledEntry entry) {
    }

    protected void deleteEntry_indexDel(AclControlledEntry entry) {
        // Delete the document that's currently in the index.
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
    }
    
    //***********************************************************************************************************
    /*
     * Load all principals assocated with an entry.  
     * This is a performance optimization for display.
     */
    protected abstract AclControlledEntry entry_load(Binder parentBinder, Long entryId);
    
    protected abstract AclControlledEntry entry_loadFull(Binder parentBinder, Long entryId);
    
    protected void loadEntryHistory(AclControlledEntry entry) {
        Set ids = new HashSet();
        if (entry.getCreation() != null)
            ids.add(entry.getCreation().getPrincipal().getId());
        if (entry.getModification() != null)
            ids.add(entry.getModification().getPrincipal().getId());
         getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     } 

    protected void loadEntryHistory(HashMap entry) {
        Set ids = new HashSet();
        if (entry.get(EntryIndexUtils.CREATORID_FIELD) != null)
    	    ids.add(entry.get(EntryIndexUtils.CREATORID_FIELD));
        if (entry.get(EntryIndexUtils.MODIFICATIONID_FIELD) != null) 
    		ids.add(entry.get(EntryIndexUtils.MODIFICATIONID_FIELD));
        getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     } 
    protected List loadEntryHistoryLuc(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        HashMap entry;
        while (iter.hasNext()) {
            entry = (HashMap)iter.next();
            if (entry.get(EntryIndexUtils.CREATORID_FIELD) != null)
        	    ids.add(entry.get(EntryIndexUtils.CREATORID_FIELD));
            if (entry.get(EntryIndexUtils.MODIFICATIONID_FIELD) != null) 
        		ids.add(entry.get(EntryIndexUtils.MODIFICATIONID_FIELD));
        }
        return getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     }   

    protected void loadEntryHistory(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        Entry entry;
        while (iter.hasNext()) {
            entry = (Entry)iter.next();
            if (entry.getCreation() != null)
                ids.add(entry.getCreation().getPrincipal().getId());
            if (entry.getModification() != null)
                ids.add(entry.getModification().getPrincipal().getId());
        }
        getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     }     
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, AclControlledEntry entry) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        
        // Add uid
        BasicIndexUtils.addUid(indexDoc, entry.getIndexDocumentUid());
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_ENTRY);
        // Add the entry type 
        EntryIndexUtils.addEntryType(indexDoc, entry);
       
        EntryIndexUtils.addBinder(indexDoc, entry);
        
        // Add creation-date
        EntryIndexUtils.addCreationDate(indexDoc, entry);
        
        // Add modification-date
        EntryIndexUtils.addModificationDate(indexDoc,entry);
        
        // Add creator id
        EntryIndexUtils.addCreationPrincipalId(indexDoc,entry);
        
        // Add Modification Principal Id
        EntryIndexUtils.addModificationPrincipalId(indexDoc,entry);
        
        // Add ReservedBy Principal Id
        EntryIndexUtils.addModificationPrincipalId(indexDoc,entry);
        
        // Add Doc Id
        EntryIndexUtils.addDocId(indexDoc, entry);
        
        // Add Doc title
        EntryIndexUtils.addTitle(indexDoc, entry);
        
        // Add command definition
        EntryIndexUtils.addCommandDefinition(indexDoc, entry); 
        
        
        // Add data fields driven by the entry's definition object. 
        getDefinitionModule().addIndexFieldsForEntry(indexDoc, binder, entry);
        
        // Add ACL field. We only need to index ACLs for read access.
        BasicIndexUtils.addReadAcls(indexDoc, binder, entry, getAclManager());
        
        // Add the events
        EntryIndexUtils.addEvents(indexDoc, entry);
        
        // Add the workflows
        EntryIndexUtils.addWorkflow(indexDoc, entry);
        
        return indexDoc;
    }
       
}
