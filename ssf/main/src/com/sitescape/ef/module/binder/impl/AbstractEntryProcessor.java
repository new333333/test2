package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.InternalException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.search.SearchFieldResult;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.OperationAccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.TempFileUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.Pipeline;
import com.sitescape.ef.pipeline.PipelineException;
import com.sitescape.ef.pipeline.impl.RAMConduit;

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
	
	private Pipeline pipeline;
	
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}
	protected Pipeline getPipeline() {
		return pipeline;
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
        List fileUploadItems = (List) entryDataAll.get("fileData");
        
        FilesErrors filesErrors = addEntry_filterFiles(binder, fileUploadItems);

        final Entry entry = addEntry_create(clazz);
        entry.setEntryDef(def);
        
        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
                //need to set entry/binder information before generating file attachments
                //Attachments/Events need binder info for AnyOwner
                addEntry_fillIn(binder, entry, inputData, entryData);
                
                addEntry_preSave(binder, entry, inputData, entryData);      

                addEntry_save(binder, entry, inputData, entryData);      
                
                addEntry_postSave(binder, entry, inputData, entryData);
                
                return null;
        	}
        });
        
        
        // We must save the entry before processing files because it makes use
        // of the persistent id of the entry. 
        filesErrors = addEntry_processFiles(binder, entry, fileUploadItems, filesErrors);
        
        //After the entry is successfully added, start up any associated workflows
        addEntry_startWorkflow(entry);

        // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addEntry_indexAdd(binder, entry, inputData, fileUploadItems);
        
        cleanupFiles(fileUploadItems);
        
    	if(filesErrors.getProblems().size() > 0) {
    		// At least one error occured during the operation. 
    		throw new WriteFilesException(filesErrors);
    	}
    	else {
    		return entry.getId();
    	}
    }

     
    public void addEntry_accessControl(Binder binder) throws AccessControlException {
        getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);        
    }
    
    protected FilesErrors addEntry_filterFiles(Binder binder, List fileUploadItems) throws FilterException {
    	return getFileModule().filterFiles(binder, fileUploadItems);
    }

    protected FilesErrors addEntry_processFiles(Binder binder, 
    		Entry entry, List fileUploadItems, FilesErrors filesErrors) {
    	return getFileModule().writeFiles(binder, entry, fileUploadItems, filesErrors);
    }
    
    protected Map addEntry_toEntryData(Binder binder, Definition def, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
    protected Entry addEntry_create(Class clazz)  {
    	try {
    		return (Entry)clazz.newInstance();
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(user));
        entry.setModification(entry.getCreation());
        entry.setParentBinder(binder);
        
        
        // The entry inherits acls from the parent by default. 
        if (entry instanceof AclControlled) {
        	getAclManager().doInherit(binder, (AclControlled) entry);
        }
        for (Iterator iter=entryData.values().iterator(); iter.hasNext();) {
        	Object obj = iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (obj instanceof Event)
        		getCoreDao().save(obj);
        }
        EntryBuilder.buildEntry(entry, entryData);
    }

    protected void addEntry_preSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {
    }

    protected void addEntry_save(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {
        getCoreDao().save(entry);
    }
    
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {
    }

    protected void addEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems) {
        
    	indexEntry(binder, entry, fileUploadItems, true);
    }
 
    protected void cleanupFiles(List fileUploadItems) {
        for(int i = 0; i < fileUploadItems.size(); i++) {
        	// Get a handle on the uploaded file. 
        	FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
        	try {
				fui.delete();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
        }
    }

    protected void addEntry_startWorkflow(Entry entry) {
    	if (!(entry instanceof WorkflowSupport)) return;
    	Binder binder = entry.getParentBinder();
    	Map workflowAssociations = (Map) binder.getWorkflowAssociations();
    	if (workflowAssociations != null) {
    		//See if the entry definition type has an associated workflow
    		Definition entryDef = entry.getEntryDef();
    		if (entryDef != null) {
	    		if (workflowAssociations.containsKey(entryDef.getId()) && 
	    				!workflowAssociations.get(entryDef.getId()).equals("")) {
	    			Definition wfDef = getDefinitionModule().getDefinition((String)workflowAssociations.get(entryDef.getId()));
	    			getWorkflowModule().addEntryWorkflow((WorkflowSupport)entry, entry.getEntityIdentifier(), wfDef);
	    		}
    		}
    	}
    }
 	

   //***********************************************************************************************************
    public Long modifyEntry(final Binder binder, Long entryId, final InputDataAccessor inputData, Map fileItems) 
    		throws AccessControlException, WriteFilesException {
		final Entry entry = modifyEntry_load(binder, entryId);
	    modifyEntry_accessControl(binder, entry);
	
	    Map entryDataAll = modifyEntry_toEntryData(entry, inputData, fileItems);
	    final Map entryData = (Map) entryDataAll.get("entryData");
	    List fileUploadItems = (List) entryDataAll.get("fileData");
	    
        FilesErrors filesErrors = modifyEntry_filterFiles(binder, fileUploadItems);

	    filesErrors = modifyEntry_processFiles(binder, entry, fileUploadItems, filesErrors);
	    
        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		modifyEntry_fillIn(binder, entry, inputData, entryData);
	                
        		modifyEntry_postFillIn(binder, entry, inputData, entryData);
        		return null;
        	}});
	    modifyEntry_indexAdd(binder, entry, inputData, fileUploadItems);
	    
	    cleanupFiles(fileUploadItems);
	    
    	if(filesErrors.getProblems().size() > 0) {
    		// At least one error occured during the operation. 
    		throw new WriteFilesException(filesErrors);
    	}
    	else {
    		return entry.getId();
    	}
	}

    protected Entry modifyEntry_load(Binder binder, Long entryId) {
    	return entry_load(binder, entryId);
    	
    }
    public void modifyEntry_accessControl(Binder binder, Entry entry) throws AccessControlException {
    	if (entry instanceof WorkflowSupport)
    		modifyAccessCheck(binder, (WorkflowSupport)entry);
    	else if (entry instanceof AclControlled)
    		modifyAccessCheck(binder, (AclControlled)entry);
    		
   }

    protected FilesErrors modifyEntry_filterFiles(Binder binder, List fileUploadItems) throws FilterException {
    	return getFileModule().filterFiles(binder, fileUploadItems);
    }

    protected FilesErrors modifyEntry_processFiles(Binder binder, 
    		Entry entry, List fileUploadItems, FilesErrors filesErrors) {
    	return getFileModule().writeFiles(binder, entry, fileUploadItems, filesErrors);
    }
    protected Map modifyEntry_toEntryData(Entry entry, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(entry.getEntryDef(), inputData, fileItems);
    }
    protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setModification(new HistoryStamp(user));
        for (Iterator iter=entryData.entrySet().iterator(); iter.hasNext();) {
        	Map.Entry mEntry = (Map.Entry)iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (entry.getCustomAttribute((String)mEntry.getKey()) == null) {
        			Object obj = mEntry.getValue();
        			if (obj instanceof Event)
        				getCoreDao().save(obj);
        	}
        }
        
        EntryBuilder.updateEntry(entry, entryData);

    }

    protected void modifyEntry_postFillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {
    }
    
    protected void modifyEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems) {
    	indexEntry(binder, entry, fileUploadItems, false);
    }
    
    
    
    //***********************************************************************************************************
    public void modifyWorkflowState(Binder binder, Long entryId, Long tokenId, String toState) {

    	Entry entry = entry_load(binder, entryId);
 		if (!(entry instanceof WorkflowSupport)) return;
 		WorkflowSupport wEntry = (WorkflowSupport)entry;
 		modifyEntry_accessControl(binder, entry);
		//Find the workflowState
		WorkflowState ws = wEntry.getWorkflowState(tokenId);
 		if (ws != null) {
			//We have the workflowState of the current state
			//See if the user is allowed to go to this state
			Map transitions = ws.getManualTransitions();
			if (transitions.containsKey(toState)) {
				//It is ok to transition to this state; go do it
				getWorkflowModule().modifyWorkflowState(ws.getTokenId(), ws.getState(), toState);
				// Do NOT use reindexEntry(entry) since it reindexes attached
				// files as well. We want workflow state change to be lightweight
				// and reindexing all attachments will be unacceptably costly.
				// TODO (Roy, I believe this was your design idea, so please 
				// verify that this strategy will indeed work). 
				
				indexEntry(binder, entry, new ArrayList(), null, false);
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
   		indexBinder_preIndex(binder);
   		//flush any changes so any exiting changes don't get lost on the evict
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
       				Entry entry = (Entry)batch.get(i);
       				// 	Create an index document from the entry object.
       				org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(binder, entry);
           			if (logger.isDebugEnabled())
           				logger.debug("Indexing entry: " + entry.toString() + ": " + indexDoc.toString());
      				getCoreDao().evict(entry);
      				docs.add(indexDoc);
      				indexBinder_postIndex(binder, entry);
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
    protected void indexBinder_preIndex(Binder binder) {
    	
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
   	protected void indexBinder_postIndex(Binder binder, Entry entry) {
   	}
 
    //***********************************************************************************************************
   	public void reindexEntry(Entry entry) {
   		indexEntry(entry.getParentBinder(), entry, null, false);
   	}
   	public void reindexEntries(Collection entries) {
   		for (Iterator iter=entries.iterator(); iter.hasNext();) {
   			Entry entry = (Entry)iter.next();
   			reindexEntry(entry);
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
	            	//This hack needs to go.
	            	if (fld.name().toLowerCase().indexOf("date") > 0) 
	            		ent.put(fld.name(),DateField.stringToDate(fld.stringValue()));
	            	else if (!ent.containsKey(fld.name())) {
	            		ent.put(fld.name(), fld.stringValue());
	            	} else {
	            		Object obj = ent.get(fld.name());
	            		SearchFieldResult val;
	            		if (obj instanceof String) {
	            			val = new SearchFieldResult();
	            			//replace
	            			ent.put(fld.name(), val);
	            			val.addValue((String)obj);
	            		} else {
	            			val = (SearchFieldResult)obj;
	            		}
	            		val.addValue(fld.stringValue());
	            	} 
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
        	if (child.get(getEntryPrincipalField()) != null) {
        		child.put(WebKeys.PRINCIPAL, getPrincipal(users,child.get(getEntryPrincipalField()).toString()));
        	}        	
        }
       	Map model = new HashMap();
        model.put(ObjectKeys.BINDER, binder);      
        model.put(ObjectKeys.ENTRIES, childEntries);
        model.put(ObjectKeys.TOTAL_SEARCH_COUNT, new Integer(hits.length()));
        return model;
   }
 
    protected abstract String getEntryPrincipalField();
    
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
     	
    	//Look only for binderId=binder and doctype = entry (not attachement)
    	if (binder != null) {
    		Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.BINDER_ID_FIELD);
        	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	child.setText(binder.getId().toString());
        	
        	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
        	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);
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
    public Entry getEntry(Binder parentBinder, Long entryId, EntityIdentifier.EntityType type) {
    	//get the entry
    	Entry entry = getEntry_load(parentBinder, entryId);
        //check access
        getEntry_accessControl(parentBinder, entry);
        //Initialize users
        loadEntryHistory(entry);
        return entry;
    }
          
    protected Entry getEntry_load(Binder binder, Long entryId) {
    	return entry_loadFull(binder, entryId);
    }
             
    protected void getEntry_accessControl(Binder binder, Entry entry) {
       	if (entry instanceof WorkflowSupport)
       		readAccessCheck(binder, (WorkflowSupport)entry);
    	else if (entry instanceof AclControlled)
    		readAccessCheck(binder, (AclControlled)entry);
     }
   //***********************************************************************************************************   
    public void deleteEntry(Binder parentBinder, Long entryId) {
    	Entry entry = deleteEntry_load(parentBinder, entryId);
        deleteEntry_accessControl(parentBinder, entry);
        deleteEntry_preDelete(parentBinder, entry);
        deleteEntry_workflow(parentBinder, entry);
        deleteEntry_processFiles(parentBinder, entry);
        deleteEntry_delete(parentBinder, entry);
        deleteEntry_postDelete(parentBinder, entry);
        deleteEntry_indexDel(entry);
   	
    }
    protected Entry deleteEntry_load(Binder binder, Long entryId) {
    	//load entry and all its collections - will need them to clean up
    	return entry_loadFull(binder, entryId);
    }
        
    public void deleteEntry_accessControl(Binder binder, Entry entry) {
      	if (entry instanceof WorkflowSupport)
      		deleteAccessCheck(binder, (WorkflowSupport)entry);
    	else if (entry instanceof AclControlled)
    		deleteAccessCheck(binder, (AclControlled)entry);
    }
    protected void deleteEntry_preDelete(Binder parentBinder, Entry entry) {
    }
        
    protected void deleteEntry_workflow(Binder parentBinder, Entry entry) {
    	if (entry instanceof WorkflowSupport)
    	getWorkflowModule().deleteEntryWorkflow((WorkflowSupport)entry);
    }
    
    protected void deleteEntry_processFiles(Binder parentBinder, Entry entry) {
    	getFileModule().deleteFiles(parentBinder, entry);
    }
    
    protected abstract void deleteEntry_delete(Binder parentBinder, Entry entry); 
    protected void deleteEntry_postDelete(Binder parentBinder, Entry entry) {
    }

    protected void deleteEntry_indexDel(Entry entry) {
        // Delete the document that's currently in the index.
    	// Since all matches will be deleted, this will also delete the attachments
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
    }
    
    //***********************************************************************************************************
    /*
     * Load all principals assocated with an entry.  
     * This is a performance optimization for display.
     */
    protected abstract Entry entry_load(Binder parentBinder, Long entryId);
    
    protected abstract Entry entry_loadFull(Binder parentBinder, Long entryId);
    
    protected void loadEntryHistory(Entry entry) {
        Set ids = new HashSet();
        if (entry.getCreation() != null)
            ids.add(entry.getCreation().getPrincipal().getId());
        if (entry.getModification() != null)
            ids.add(entry.getModification().getPrincipal().getId());
         getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     } 

    protected List loadEntryHistoryLuc(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        HashMap entry;
        while (iter.hasNext()) {
            entry = (HashMap)iter.next();
            if (entry.get(EntryIndexUtils.CREATORID_FIELD) != null)
            	try {ids.add(new Long(entry.get(EntryIndexUtils.CREATORID_FIELD).toString()));
        	    } catch (Exception ex) {}
            if (entry.get(EntryIndexUtils.MODIFICATIONID_FIELD) != null) 
        		try {ids.add(new Long(entry.get(EntryIndexUtils.MODIFICATIONID_FIELD).toString()));
        		} catch (Exception ex) {}
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
    
    /**
     * Fill in the Lucene document object with information that is common between
     * entry doc and attachment docs. We duplicate data so that we won't have to
     * perform multiple queries or run our own filtering in multiple steps. 
     * 
     * @param indexDoc
     * @param binder
     * @param entry
     */
    private void fillInIndexDocWithCommonPartFromEntry(org.apache.lucene.document.Document indexDoc, 
    		Binder binder, Entry entry) {
        // Add uid
        BasicIndexUtils.addUid(indexDoc, entry.getIndexDocumentUid());

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
        
        // Add data fields driven by the entry's definition object. 
        getDefinitionModule().addIndexFieldsForEntry(indexDoc, binder, entry);
        
        // Add ACL field. We only need to index ACLs for read access.
        EntryIndexUtils.addReadAcls(indexDoc, binder, entry, getReadAclIds(entry));
    }
    
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        
    	fillInIndexDocWithCommonPartFromEntry(indexDoc, binder, entry);
    	
    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_ENTRY);
        
        // Add command definition
        EntryIndexUtils.addCommandDefinition(indexDoc, entry); 
        
        // Add the events
        EntryIndexUtils.addEvents(indexDoc, entry);
        
        // Add the workflows
        EntryIndexUtils.addWorkflow(indexDoc, entry);
        
        return indexDoc;
    }
    
    /**
     * 
     * @param binder
     * @param entry
     * @param fa This is non-null.
     * @param fui This may be null. 
     * @return
     */
    protected org.apache.lucene.document.Document buildIndexDocumentFromFile
    	(Binder binder, Entry entry, FileAttachment fa, FileUploadItem fui) {
    	// Prepare for pipeline execution.
    	
    	String text = null;
    	
    	// In this case, initial input into pipeline always comes in the form
    	// of a local file (this is because "we" know the first handler in the
    	// pipeline is a converter that expects input as a file... just details).
    	Conduit firstConduit = new RAMConduit();
    	
    	try {
	    	if(fui != null) {
	    		// Use FileUploadItem object for the file content. Potentially this
	    		// provides more efficient mechanism than fetching from repository.
		    	try {
					firstConduit.getSink().setFile(fui.getFile(), false, false, null);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
	    	}
	    	else {
	    		// We must retrieve the file content from repository and create a
	    		// temporary file. 
	    		File tempFile = TempFileUtil.createTempFile("repositoryfile", SPropsUtil.getFile("temp.dir"));
	    		
				firstConduit.getSink().setFile(tempFile, true, false, null);    		

				try {
	    			getFileModule().readFile(binder, entry, fa, new BufferedOutputStream(new FileOutputStream(tempFile)));
	    		}
	    		catch(IOException e) {
	    			throw new UncheckedIOException(e);
	    		}
	    	}
    	
	    	Conduit lastConduit = new RAMConduit();
	    	
	    	try {
		    	// Invoke pipeline.
		    	// TODO For now all pipeline executions are synchronous.
		    	// Asynchronous execution support to be added later.
	    		try {
	    			getPipeline().invoke(firstConduit.getSource(), lastConduit.getSink());
	    		}
	    		catch(PipelineException e) {
	    			// Error during pipeline execution. In this case do not propogate
	    			// the exception up the stakc. Instead we return null. It allows
	    			// application to proceed with the remaining files. 
	    			return null;
	    		}
		    	
		        // Get the resulting data of the pipeline execution as a string.
		        text = lastConduit.getSource().getDataAsString();
	    	}
	    	finally {
	    		lastConduit.close();
	    	}
    	}
    	finally {
    		firstConduit.close();
    	}
        
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
    	
    	fillInIndexDocWithCommonPartFromEntry(indexDoc, binder, entry);
    	
    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_ATTACHMENT);
        
        // Add UID of attachment file (FUID)
        EntryIndexUtils.addFileAttachmentUid(indexDoc, fa);
        
        // Add the filename
        EntryIndexUtils.addFileAttachmentName(indexDoc,fui.getOriginalFilename());        
        
        if(text != null)
        	BasicIndexUtils.addAllText(indexDoc, text);
        
        // TBD Add the filetype and Extension
        //EntryIndexUtils.addFileType(indexDoc,tempFile);

        EntryIndexUtils.addFileExtension(indexDoc,fui.getOriginalFilename());
                
        return indexDoc;
    }
    protected void modifyAccessCheck(Binder binder, AclControlled entry) {
       	try {
       		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) 
   				getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATOR_MODIFY);
      		else throw ex;
      	}
    }
     protected void modifyAccessCheck(Binder binder, WorkflowSupport entry) {
        if (!entry.hasAclSet()) {
        	modifyAccessCheck(binder, (AclControlled)entry);
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
    	   getAccessControlManager().checkAcl(binder, (AclControlled)entry, AccessType.WRITE, false, false);
    	   if (binder.isWidenModify()) return;
    	   //make sure acl list is sub-set of binder access
      		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES);     	   
        }    	
    }
     protected Set getReadAclIds(Entry entry) {
     	if (!(entry instanceof AclControlled)) return null;
        List readMemberIds = getAccessControlManager().getWorkAreaAccessControl(entry.getParentBinder(), WorkAreaOperation.READ_ENTRIES);
		Set<Long> ids = new HashSet<Long>();
		if (entry instanceof WorkflowSupport) {
			WorkflowSupport wEntry = (WorkflowSupport)entry;
			if (wEntry.hasAclSet()) {
				//index binders access
				if (wEntry.checkWorkArea(AccessType.READ)) {
					ids.addAll(readMemberIds);
				}
				//index workflow access - ignore widen for search engine - prune results later
				ids.addAll(wEntry.getAclSet().getMemberIds(AccessType.READ));
				if (wEntry.checkOwner(AccessType.READ)) {
					ids.add(wEntry.getCreatorId());
				}
				//	no access specified, add binder default
				if (ids.isEmpty())
					ids.addAll(readMemberIds);
        		
			} else {
				ids.addAll(readMemberIds);
				//TODO: this doesn't make sense on an index-need to get creator
				if (getAccessControlManager().testOperation(entry.getParentBinder(), WorkAreaOperation.CREATOR_READ))
					ids.add(wEntry.getCreatorId());
			}
		} else {
			ids.addAll(readMemberIds);
			//TODO: this doesn't make sense on an index-need to get creator
			if (getAccessControlManager().testOperation(entry.getParentBinder(), WorkAreaOperation.CREATOR_READ))
				ids.add(((AclControlled)entry).getCreatorId());			
		}
		return ids;
    	 
     }
     protected void readAccessCheck(Binder binder, AclControlled entry) {
       	try {
       		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
       	} catch (OperationAccessControlException ex) {
       		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) 
    				getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATOR_READ);
       		else throw ex;
       	}
    }
    protected void readAccessCheck(Binder binder, WorkflowSupport entry) {
        if (!entry.hasAclSet()) {
        	readAccessCheck(binder, (AclControlled)entry);
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
    protected void deleteAccessCheck(Binder binder, AclControlled entry) {
      	try {
       		getAccessControlManager().checkOperation(binder, WorkAreaOperation.DELETE_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) 
   				getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATOR_DELETE);
      		else throw ex;
      	}   
    }
    protected void deleteAccessCheck(Binder binder, WorkflowSupport entry) {
        if (!entry.hasAclSet()) {
        	deleteAccessCheck(binder, (AclControlled)entry);
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
    
    /**
     * Index entry and optionally its attached files.
     * 
     * @param biner
     * @param entry
     * @param fileUploadItems If this is null, all attached files currently in
     * the entry are indexed as well. If this is non-null, only those files
     * in the list are indexed. 
     * @param newEntry
     */
    protected void indexEntry(Binder binder, Entry entry,
    		List fileUploadItems, boolean newEntry) {
    	if(fileUploadItems != null) {
    		indexEntry(binder, entry, findCorrespondingFileAttachments(entry, fileUploadItems), fileUploadItems, newEntry);
    	}
    	else {
    		indexEntry(binder, entry, entry.getFileAttachments(), null, newEntry);
    	}
    }
    
    /**
     * Index entry along with its file attachments. 
     * 
     * @param binder
     * @param entry
     * @param fileAttachments list of FileAttachments that need to be (re)indexed.
     * Only those files explicitly listed in this list are indexed. 
     * @param fileUploadItems This may be <code>null</code> in which case the 
     * contents of the files must be obtained from repositories. If non-null,
     * the files in this list are used for indexing and the elements positionally
     * correspond to the elements in fileAttachments list. 
     * @param newEntry
     */
	protected void indexEntry(Binder binder, Entry entry,
			List fileAttachments, List fileUploadItems, boolean newEntry) {
		if(!newEntry) {
			// This is modification. We must first delete existing document(s) from the index.
			
			// Since all matches will be deleted, this will also delete the attachments 
	        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
		}
		
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(binder, entry);
            
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
        
        //Create separate documents one for each attached file and index them.
        for(int i = 0; i < fileAttachments.size(); i++) {
        	FileAttachment fa = (FileAttachment) fileAttachments.get(i);
        	FileUploadItem fui = null;
        	if(fileUploadItems != null)
        		fui = (FileUploadItem) fileUploadItems.get(i);
        	indexDoc = buildIndexDocumentFromFile(binder, entry, fa, fui);
        	if(indexDoc != null) {
        		// Register the index document for indexing.
        		IndexSynchronizationManager.addDocument(indexDoc);
        	}
        }
	}
	
    protected List findCorrespondingFileAttachments(Entry entry, List fileUploadItems) {
    	List fileAttachments = new ArrayList();
    	for(int i = 0; i < fileUploadItems.size(); i++) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		FileAttachment fa = entry.getFileAttachment(fui.getRepositoryServiceName(), fui.getOriginalFilename());
    		if(fa == null) 
    			throw new InternalException("No FileAttachment corresponding to FileUploadItem");
    		fileAttachments.add(i, fa);
    	}
    	return fileAttachments;
    }
    

}
