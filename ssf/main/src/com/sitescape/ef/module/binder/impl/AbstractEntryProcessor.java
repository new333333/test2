package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.lang.Long;
import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.folder.InputDataAccessor;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.OperationAccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.FilterHelper;
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
	
	private FileModule fileModule;
	
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}
	protected FileModule getFileModule() {
		return fileModule;
	}
	

	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	protected void getBinder_accessControl(Binder binder) {
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
	}
	//***********************************************************************************************************	
    public Long addEntry(final Binder binder, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        
        addEntry_accessControl(binder);
        
        Map entryDataAll = addEntry_toEntryData(binder, def, inputData, fileItems);
        final Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        final WorkflowControlledEntry entry = addEntry_create(clazz);
        entry.setEntryDef(def);
        
        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
                //need to set entry/binder information before generating file attachments
                //Attachments/Events need binder info for AnyOwner
                addEntry_fillIn(binder, entry, inputData, entryData);
                
                addEntry_preSave(binder, entry, inputData, entryData);      

                addEntry_save(binder, entry, inputData, entryData);      
                
                return null;
        	}
        });
        
        addEntry_postSave(binder, entry, inputData, entryData);
        
        // We must save the entry before processing files because it makes use
        // of the persistent id of the entry. 
        addEntry_processFiles(binder, entry, fileData);
        
        //After the entry is successfully added, start up any associated workflows
        addEntry_startWorkflow(entry);

        // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addEntry_indexAdd(binder, entry, inputData, fileData);
        
        cleanupFiles(fileData);
        
        return entry.getId();
    }

     
    protected void addEntry_accessControl(Binder binder) throws AccessControlException {
        getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);        
    }
    
    protected void addEntry_processFiles(Binder binder, WorkflowControlledEntry entry, List fileData) 
    	throws WriteFilesException {
    	getFileModule().writeFiles(binder, entry, fileData);
    }
    
    protected Map addEntry_toEntryData(Binder binder, Definition def, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
    protected WorkflowControlledEntry addEntry_create(Class clazz)  {
    	try {
    		return (WorkflowControlledEntry)clazz.newInstance();
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    protected void addEntry_fillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(user));
        entry.setModification(entry.getCreation());
        
        
        // The entry inherits acls from the parent by default. 
        getAclManager().doInherit(binder, (WorkflowControlledEntry) entry);
        for (Iterator iter=entryData.values().iterator(); iter.hasNext();) {
        	Object obj = iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (obj instanceof Event)
        		getCoreDao().save(obj);
        }
        EntryBuilder.buildEntry(entry, entryData);
    }

    protected void addEntry_preSave(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {
    }

    protected void addEntry_save(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {
        getCoreDao().save(entry);
    }
    
    protected void addEntry_postSave(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {
    }

    protected void addEntry_indexAdd(Binder binder, WorkflowControlledEntry entry, 
    		InputDataAccessor inputData, List fileData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(binder, entry);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
        
        /* The following skeleton is to be completed by Roy 
        
        // Create separate documents one for each attached files and index them.
        for(int i = 0; i < fileData.size(); i++) {
        	// Get a handle on the uploaded file. 
        	FileUploadItem fui = (FileUploadItem) fileData.get(i);
        	// Create a Lucene document object from the uploaded file.
        	// This involves applying additional processings such as doc
        	// conversion, etc. 
        	indexDoc = buildIndexDocumentFromFile(binder, entry, fui);
            // Register the index document for indexing.
            IndexSynchronizationManager.addDocument(indexDoc);
        }
        
        */
    }
 
    protected void cleanupFiles(List fileData) {
        for(int i = 0; i < fileData.size(); i++) {
        	// Get a handle on the uploaded file. 
        	FileUploadItem fui = (FileUploadItem) fileData.get(i);
        	try {
				fui.delete();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
        }
    }

    protected void addEntry_startWorkflow(WorkflowControlledEntry entry) {
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
    public Long modifyEntry(Binder binder, Long entryId, InputDataAccessor inputData, Map fileItems) 
    		throws AccessControlException, WriteFilesException {
		WorkflowControlledEntry entry = modifyEntry_load(binder, entryId);
	    modifyEntry_accessControl(binder, entry);
	
	    Map entryDataAll = modifyEntry_toEntryData(entry, inputData, fileItems);
	    Map entryData = (Map) entryDataAll.get("entryData");
	    List fileData = (List) entryDataAll.get("fileData");
	    
	    modifyEntry_processFiles(binder, entry, fileData);
	    
	    modifyEntry_fillIn(binder, entry, inputData, entryData);
	                
	    modifyEntry_postFillIn(binder, entry, inputData, entryData);
	    
	    modifyEntry_indexAdd(binder, entry, inputData, fileData);
	    
	    cleanupFiles(fileData);
	    
	    return entry.getId();
	}
	public Long modifyEntryData(Binder binder, Long entryId, Map entryData) 
			throws AccessControlException {
    	WorkflowControlledEntry entry = modifyEntry_load(binder, entryId);
	    modifyEntry_accessControl(binder, entry);
	
        EntryBuilder.updateEntry(entry, entryData);
		return entry.getId();
	}
    protected WorkflowControlledEntry modifyEntry_load(Binder binder, Long entryId) {
    	return entry_load(binder, entryId);
    	
    }
    protected void modifyEntry_accessControl(Binder binder, WorkflowControlledEntry entry) throws AccessControlException {
    	modifyAccessCheck(binder, entry);
   }

    protected void modifyEntry_processFiles(Binder binder, WorkflowControlledEntry entry, List fileData) 
    throws WriteFilesException {
    	getFileModule().writeFiles(binder, entry, fileData);
    }
    protected Map modifyEntry_toEntryData(WorkflowControlledEntry entry, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(entry.getEntryDef(), inputData, fileItems);
    }
    protected void modifyEntry_fillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setModification(new HistoryStamp(user));
        EntryBuilder.updateEntry(entry, entryData);

    }

    protected void modifyEntry_postFillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {
    }
    
    protected void modifyEntry_indexAdd(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, List fileData) {
    	indexEntry(entry);
    	// Take care of attached files - to be completed by Roy
    }
    
    
    
    //***********************************************************************************************************
    public void modifyWorkflowState(Binder binder, Long entryId, Long tokenId, String toState) {

    	WorkflowControlledEntry entry = entry_load(binder, entryId);
 		
		//Find the workflowState
		WorkflowState ws = entry.getWorkflowState(tokenId);
 		if (ws != null) {
			//We have the workflowState of the current state
			//See if the user is allowed to go to this state
			Map transitions = ws.getManualTransitions();
			if (transitions.containsKey(toState)) {
				//It is ok to transition to this state; go do it
				getWorkflowModule().modifyWorkflowState(ws.getTokenId(), ws.getState(), toState);
	    		indexEntry(entry);
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
   		//flush any changes so any changes don't get lost
   		getCoreDao().flush();
   		SFQuery query = indexBinder_getQuery(binder);
	   	
	   	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
       	try {       
  			List batch = new ArrayList();
  			List docs = new ArrayList();
  			int total=0;
      		while (query.hasNext()) {
       			int count=0;
       			batch.clear();
       			docs.clear();
       			// get 1000 entries, then build collections by hand 
       			//for performance
       			while (query.hasNext() && (count < 1000)) {
       				Object obj = query.next();
       				if (obj instanceof Object[])
       					obj = ((Object [])obj)[0];
       				batch.add(obj);
       				++count;
       			}
       			total += count;
       			//have 1000 entries, manually load their collections
       			getCoreDao().bulkLoadCollections(batch);
       			logger.info("Indexing at " + total + "(" + binder.getId().toString() + ")");
       			
       			for (int i=0; i<batch.size(); ++i) {
       				WorkflowControlledEntry entry = (WorkflowControlledEntry)batch.get(i);
       				// 	Create an index document from the entry object.
       				org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(binder, entry);
           			if (logger.isDebugEnabled())
           				logger.debug("Indexing (" + binder.getId().toString() + ") " + entry.toString() + ": " + indexDoc.toString());
      				getCoreDao().evict(entry);
      				docs.add(indexDoc);
       			}
	            
       			// Delete the document that's currently in the index.
 // turn back on later when don't delete everything
//       				luceneSession.deleteDocument(entry.getIndexDocumentUid());
	            
       			// Register the index document for indexing.
       			luceneSession.addDocuments(docs);
       			logger.info("Indexing done at " + total + "("+ binder.getId().toString() + ")");
       		
        	}
        	
        } finally {
        	query.close();
        	luceneSession.close();
        }
 
    }
    protected void indexBinder_accessControl(Binder binder) {
    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
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
   	public void indexEntry(WorkflowControlledEntry entry) {
		// 	Create an index document from the entry object.
   		org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(entry.getParentBinder(), entry);
        
        
        // Delete the document that's currently in the index.
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
            
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
        
   	}
   	public void indexEntry(Collection entries) {
   		for (Iterator iter=entries.iterator(); iter.hasNext();) {
   			indexEntry((WorkflowControlledEntry)iter.next());
   		}
   	}
   	
    //***********************************************************************************************************
    public Map getBinderEntries(Binder binder, String[] entryTypes, int maxChildEntries) {
    	org.dom4j.Document searchFilter = null;
    	return getBinderEntries(binder, entryTypes, maxChildEntries, searchFilter);
    }
    public Map getBinderEntries(Binder binder, String[] entryTypes, int maxChildEntries, org.dom4j.Document searchFilter) {
        int count=0;
        Field fld;
        //search engine will only return entries you have access to
         //validate entry count
        maxChildEntries = getBinderEntries_maxEntries(maxChildEntries); 
        //do actual search index query
        Hits hits = getBinderEntries_doSearch(binder, entryTypes, maxChildEntries, searchFilter);
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
    protected int getBinderEntries_maxEntries(int maxChildEntries) {
        if (maxChildEntries == 0) maxChildEntries = DEFAULT_MAX_CHILD_ENTRIES;
        return maxChildEntries;
    }
     
    protected Hits getBinderEntries_doSearch(Binder binder, String [] entryTypes, int maxResults, org.dom4j.Document searchFilter) {
       	Hits hits = null;
       	// Build the query
    	if (searchFilter == null) {
    		//If there is no search filter, assume the caller wants everything
    		searchFilter = DocumentHelper.createDocument();
    		Element rootElement = searchFilter.addElement(FilterHelper.FilterRootName);
        	rootElement.addElement(FilterHelper.FilterTerms);
    		Element filterTerms = rootElement.addElement(FilterHelper.FilterTerms);
    	}
       	org.dom4j.Document queryTree = getBinderEntries_getSearchDocument(binder, entryTypes, searchFilter);
    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder();
    	SearchObject so = qb.buildQuery(queryTree);
    	
    	//Set the sort order
    	SortField[] fields = getBinderEntries_getSortFields(binder); 
    	so.setSortBy(fields);
    	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
    	
    	logger.info("Query is: " + queryTree.asXML());
    	logger.info("Query is: " + soQuery.toString());
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        hits = luceneSession.search(soQuery,so.getSortBy(),0,maxResults);
        }
        finally {
            luceneSession.close();
        }
    	
        return hits;
     
    }
    protected org.dom4j.Document getBinderEntries_getSearchDocument(Binder binder, 
    		String [] entryTypes, org.dom4j.Document searchFilter) {
    	if (searchFilter == null) {
    		searchFilter = DocumentHelper.createDocument();
    		Element rootElement = searchFilter.addElement(FilterHelper.FilterRootName);
        	rootElement.addElement(FilterHelper.FilterTerms);
    	}
    	org.dom4j.Document qTree = FilterHelper.convertSearchFilterToSearchBoolean(searchFilter);
    	Element rootElement = qTree.getRootElement();
    	if (rootElement == null) return qTree;
    	Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
    	if (boolElement == null) return qTree;
    	boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
     	
    	//Look only for binderId=binder
    	if (binder != null) {
    		Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.BINDER_ID_FIELD);
        	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	child.setText(binder.getId().toString());
    	}
    	return qTree;
    }
   	protected SortField[] getBinderEntries_getSortFields(Binder binder) {
   		SortField[] fields = new SortField[1];
    	boolean descend = true;
    	fields[0] = new SortField(EntryIndexUtils.MODIFICATION_DATE_FIELD, descend);
    	return fields;
   	}
    

    //***********************************************************************************************************
    public WorkflowControlledEntry getEntry(Binder parentBinder, Long entryId, int type) {
    	//get the entry
    	WorkflowControlledEntry entry = getEntry_load(parentBinder, entryId);
        //check access
        getEntry_accessControl(parentBinder, entry);
        //Initialize users
        loadEntryHistory(entry);
        return entry;
    }
          
    protected WorkflowControlledEntry getEntry_load(Binder binder, Long entryId) {
    	return entry_loadFull(binder, entryId);
    }
             
    protected void getEntry_accessControl(Binder parentBinder, WorkflowControlledEntry entry) {
        readAccessCheck(parentBinder, entry);   
    }
   //***********************************************************************************************************   
    public void deleteEntry(Binder parentBinder, Long entryId) {
    	WorkflowControlledEntry entry = deleteEntry_load(parentBinder, entryId);
        deleteEntry_accessControl(parentBinder, entry);
        deleteEntry_preDelete(parentBinder, entry);
        deleteEntry_workflow(parentBinder, entry);
        deleteEntry_processFiles(parentBinder, entry);
        deleteEntry_delete(parentBinder, entry);
        deleteEntry_postDelete(parentBinder, entry);
        deleteEntry_indexDel(entry);
   	
    }
    protected WorkflowControlledEntry deleteEntry_load(Binder binder, Long entryId) {
    	//load entry and all its collections - will need them to clean up
    	return entry_loadFull(binder, entryId);
    }
        
    protected void deleteEntry_accessControl(Binder parentBinder, WorkflowControlledEntry entry) {
    	deleteAccessCheck(parentBinder, entry);
    }
    protected void deleteEntry_preDelete(Binder parentBinder, WorkflowControlledEntry entry) {
    }
        
    protected void deleteEntry_workflow(Binder parentBinder, WorkflowControlledEntry entry) {
    	getWorkflowModule().deleteEntryWorkflow(parentBinder, entry);
    }
    
    protected void deleteEntry_processFiles(Binder parentBinder, WorkflowControlledEntry entry) {
    	getFileModule().deleteFiles(parentBinder, entry);
    }
    
    protected abstract void deleteEntry_delete(Binder parentBinder, WorkflowControlledEntry entry); 
    protected void deleteEntry_postDelete(Binder parentBinder, WorkflowControlledEntry entry) {
    }

    protected void deleteEntry_indexDel(WorkflowControlledEntry entry) {
        // Delete the document that's currently in the index.
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
        
        // We must delete the index entries for attached files.
        // To be completed by Roy.
    }
    
    //***********************************************************************************************************
    /*
     * Load all principals assocated with an entry.  
     * This is a performance optimization for display.
     */
    protected abstract WorkflowControlledEntry entry_load(Binder parentBinder, Long entryId);
    
    protected abstract WorkflowControlledEntry entry_loadFull(Binder parentBinder, Long entryId);
    
    protected void loadEntryHistory(WorkflowControlledEntry entry) {
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
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, WorkflowControlledEntry entry) {
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
 
    protected void modifyAccessCheck(Binder binder, WorkflowControlledEntry entry) {
        if (!entry.hasAclSet()) {
           	try {
           		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES);
           	} catch (OperationAccessControlException ex) {
          		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) 
       				getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATOR_MODIFY);
          		else throw ex;
          	}     
        } else {         	
        	//entry has a workflow
        	//see if owner can modify
        	if (entry.checkOwner(AccessType.WRITE)) {
    		   if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) {
    			   if (binder.isWidenModify()) return;
    			   if (getAccessControlManager().testOperation(binder, WorkAreaOperation.CREATOR_MODIFY)) return;
    		   }
    	   }
		    //see if folder default is enabled.
    	   if (entry.checkWorkArea(AccessType.WRITE)) {
    		   try {
    	       		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES); 
    	       		return;
    		   } catch (OperationAccessControlException ex) {
    			   //at this point we can stop if workflow cannot widen access
    			   if (!binder.isWidenModify()) throw ex;
    		   }
    	   }
    	   //if fail this test exception is thrown
    	   getAccessControlManager().checkAcl(binder, entry, AccessType.WRITE, false, false);
    	   if (binder.isWidenModify()) return;
    	   //make sure acl list is sub-set of binder access
      		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES);     	   
        }    	
    }
    protected void readAccessCheck(Binder binder, WorkflowControlledEntry entry) {
        if (!entry.hasAclSet()) {
           	try {
           		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
           	} catch (OperationAccessControlException ex) {
          		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) 
       				getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATOR_READ);
          		else throw ex;
          	}     
        } else {         	
        	//entry has a workflow
        	//see if owner can read
        	if (entry.checkOwner(AccessType.READ)) {
    		   if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) {
    			   if (binder.isWidenRead()) return;
    			   if (getAccessControlManager().testOperation(binder, WorkAreaOperation.CREATOR_READ)) return;
    		   }
    	   }
		    //see if folder default is enabled.
    	   if (entry.checkWorkArea(AccessType.READ)) {
    		   try {
    	       		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES); 
    	       		return;
    		   } catch (OperationAccessControlException ex) {
    			   //at this point we can stop if workflow cannot widen access
    			   if (!binder.isWidenRead()) throw ex;
    		   }
    	   }
    	   //if fails this test exception is thrown
    	   getAccessControlManager().checkAcl(binder, entry, AccessType.READ, false, false);
    	   if (binder.isWidenRead()) return;
    	   //make sure acl list is sub-set of binder access
      		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);     	   
        }    	
    }
    protected void deleteAccessCheck(Binder binder, WorkflowControlledEntry entry) {
        if (!entry.hasAclSet()) {
           	try {
           		getAccessControlManager().checkOperation(binder, WorkAreaOperation.DELETE_ENTRIES);
           	} catch (OperationAccessControlException ex) {
          		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) 
       				getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATOR_DELETE);
          		else throw ex;
          	}     
        } else {         	
        	//entry has a workflow
        	//see if owner can delete
        	if (entry.checkOwner(AccessType.DELETE)) {
    		   if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) {
    			   if (binder.isWidenDelete()) return;
    			   if (getAccessControlManager().testOperation(binder, WorkAreaOperation.CREATOR_DELETE)) return;
    		   }
    	   }
		    //see if folder default is enabled.
    	   if (entry.checkWorkArea(AccessType.DELETE)) {
    		   try {
    	       		getAccessControlManager().checkOperation(binder, WorkAreaOperation.DELETE_ENTRIES); 
    	       		return;
    		   } catch (OperationAccessControlException ex) {
    			   //at this point we can stop if workflow cannot widen access
    			   if (!binder.isWidenDelete()) throw ex;
    		   }
    	   }
    	   //if fails this test exception is thrown
    	   getAccessControlManager().checkAcl(binder, entry, AccessType.DELETE, false, false);
    	   if (binder.isWidenDelete()) return;
    	   //make sure acl list is sub-set of binder access
      		getAccessControlManager().checkOperation(binder, WorkAreaOperation.DELETE_ENTRIES);     	   
        }    	
    }
}
