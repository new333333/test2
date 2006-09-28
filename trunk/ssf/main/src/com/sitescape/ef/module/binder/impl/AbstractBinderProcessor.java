package com.sitescape.ef.module.binder.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.module.binder.AccessUtils;
import com.sitescape.ef.module.binder.BinderProcessor;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.Pipeline;
import com.sitescape.ef.pipeline.PipelineException;
import com.sitescape.ef.pipeline.impl.RAMConduit;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.SimpleProfiler;
/**
 *
 * 
 */
public abstract class AbstractBinderProcessor extends CommonDependencyInjection 
	implements BinderProcessor {
    
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
	
	//***********************************************************************************************************	
    public Binder addBinder(final Binder parent, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
                
    	SimpleProfiler sp = new SimpleProfiler(false);
    	
    	sp.reset("addBinder_toEntryData").begin();
        Map entryDataAll = addBinder_toEntryData(parent, def, inputData, fileItems);
        sp.end().print();
        
        final Map entryData = (Map) entryDataAll.get("entryData");
        List fileUploadItems = (List) entryDataAll.get("fileData");
        
    	try {
	        sp.reset("addBinder_create").begin();
	        final Binder binder = addBinder_create(def, clazz);
	        sp.end().print();
	        
	    	if (def != null) {
	    		if ((parent.getDefinitionType() == null) ||
	    				(binder.getDefinitionType().intValue() != parent.getDefinitionType().intValue())) {
	    			binder.setDefinitionsInherited(false);
	    		}
	    	}
	        String title = (String)entryData.get("title");
	        sp.reset("addBinder_validateTitle").begin();
	        getCoreDao().validateTitle(parent, title);
	        sp.end().print();
	        binder.setPathName(parent.getPathName() + "/" + title);
	        
	        sp.reset("addBinder_transactionExecute").begin();
	        // The following part requires update database transaction.
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	                //need to set entry/binder information before generating file attachments
	                //Attachments/Events need binder info for AnyOwner
	                addBinder_fillIn(parent, binder, inputData, entryData);
	                
	                addBinder_preSave(parent, binder, inputData, entryData);      
	
	                addBinder_save(parent, binder, inputData, entryData);      
	                
	                addBinder_postSave(parent, binder, inputData, entryData);
	                
	                return null;
	        	}
	        });
	        sp.end().print();
	           
	        sp.reset("addBinder_filterFiles").begin();
	        //Need to do filter here after binder is saved cause it makes use of
	        // the id of binder
	        FilesErrors filesErrors = addBinder_filterFiles(binder, fileUploadItems);
	        sp.end().print();
	        
	        sp.reset("addBinder_processFiles").begin();
	        // We must save the entry before processing files because it makes use
	        // of the persistent id of the entry. 
	        filesErrors = addBinder_processFiles(binder, fileUploadItems, filesErrors);
	        sp.end().print();
	        
	        sp.reset("addBinder_indexAdd").begin();
	        // This must be done in a separate step after persisting the entry,
	        // because we need the entry's persistent ID for indexing. 
	        addBinder_indexAdd(parent, binder, inputData, fileUploadItems);
	        sp.end().print();
	        
	    	if(filesErrors.getProblems().size() > 0) {
	    		// At least one error occured during the operation. 
	    		throw new WriteFilesException(filesErrors);
	    	}
	    	else {
	    		return binder;
	    	}
    	}
    	finally {
	        cleanupFiles(fileUploadItems);
    	}
    }

         
    protected FilesErrors addBinder_filterFiles(Binder binder, List fileUploadItems) throws FilterException {
    	return getFileModule().filterFiles(binder, fileUploadItems);
    }

    protected FilesErrors addBinder_processFiles(Binder binder, List fileUploadItems, FilesErrors filesErrors) {
    	return getFileModule().writeFiles(binder, binder, fileUploadItems, filesErrors);
    }
    
    protected Map addBinder_toEntryData(Binder parent, Definition def, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
    	if (def != null) {
    		return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
    	} else {
    		return new HashMap();
    	}
    }
    
    protected Binder addBinder_create(Definition def, Class clazz)  {
    	try {
    		Binder binder = (Binder)clazz.newInstance();
            binder.setEntryDef(def);
            if (def != null) {
            	binder.setDefinitionType(Integer.valueOf(def.getType()));
            	List defs = new ArrayList();
            	defs.add(def);
            	binder.setDefinitions(defs);
            } 
            return binder;
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    protected void addBinder_fillIn(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        binder.setZoneName(parent.getZoneName());
        binder.setCreation(new HistoryStamp(user));
        binder.setModification(binder.getCreation());
    	//Since parent collection is a list we can add the binder without an id
    	getCoreDao().refresh(parent);
      	parent.addBinder(binder);
 
       	getAclManager().doInherit(parent, (AclControlled) binder);

        for (Iterator iter=entryData.values().iterator(); iter.hasNext();) {
        	Object obj = iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (obj instanceof Event)
        		getCoreDao().save(obj);
        }
        EntryBuilder.buildEntry(binder, entryData);
    }

    protected void addBinder_preSave(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData) {
    }

    protected void addBinder_save(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData) {
        getCoreDao().save(binder);
    }
    
    protected void addBinder_postSave(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData) {
    }

    protected void addBinder_indexAdd(Binder parent, Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems) {
        
    	indexBinder(binder, fileUploadItems, null, true);
    }
 	
 	
    //***********************************************************************************************************
    public void modifyBinder(final Binder binder, final InputDataAccessor inputData, 
    		Map fileItems, final Collection deleteAttachments) 
    		throws AccessControlException, WriteFilesException {
	
    	SimpleProfiler sp = new SimpleProfiler(false);
    	
    	sp.reset("modifyBinder_toEntryData").begin();
	    Map entryDataAll = modifyBinder_toEntryData(binder, inputData, fileItems);
	    sp.end().print();
	    
	    final Map entryData = (Map) entryDataAll.get("entryData");
	    List fileUploadItems = (List) entryDataAll.get("fileData");

	    try {
		    if (entryData.containsKey("title")) {
		    	String newTitle = (String)entryData.get("title");
		    	if (!newTitle.equalsIgnoreCase(binder.getTitle())) { 
		    		sp.reset("modifyBinder_validateTitle").begin();
		    		getCoreDao().validateTitle(binder.getParentBinder(), newTitle);
		    		sp.end().print();
		    	}	
		    }
		    
	    	sp.reset("modifyBinder_filterFiles").begin();
		    FilesErrors filesErrors = modifyBinder_filterFiles(binder, fileUploadItems);
		    sp.end().print();
	
	    	final List<FileAttachment> filesToDeindex = new ArrayList<FileAttachment>();
	    	final List<FileAttachment> filesToReindex = new ArrayList<FileAttachment>();	    

	    	sp.reset("modifyBinder_transactionExecute").begin();
	    	// The following part requires update database transaction.
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		String oldTitle = binder.getTitle();
	        		modifyBinder_fillIn(binder, inputData, entryData);
		            modifyBinder_removeAttachments(binder, deleteAttachments, filesToDeindex, filesToReindex);    
	        		modifyBinder_postFillIn(binder, inputData, entryData);
	        		//if title changed, must update path infor for all child folders
	        		String newTitle = binder.getTitle();
	        		//case matters here
	        		if ((oldTitle == null) || !oldTitle.equals(newTitle)) {
	        			if (binder.getParentBinder() != null) {
	        				binder.setPathName(binder.getParentBinder().getPathName() + "/" + newTitle);
	        			} else {
	        				//must be top
	        				binder.setPathName("/" + newTitle);
	        			}
	         			List children = new ArrayList(binder.getBinders());
	        			//if we index the path, need to reindex all these folders
	        			while (!children.isEmpty()) {
	        				Binder child = (Binder)children.get(0);
	        				child.setPathName(child.getParentBinder().getPathName() + "/" + child.getTitle());
	        				children.remove(0);
	        				children.addAll(child.getBinders());
	        			}
	        		}
	        		return null;
	        	}});
	        sp.end().print();
	        
	        sp.reset("modifyBinder_processFiles").begin();
		    filesErrors = modifyBinder_processFiles(binder, fileUploadItems, filesErrors);
		    sp.end().print();
		    
	    	// Since index update is implemented as removal followed by add, 
	    	// the update requests must be added to the removal and then add
	    	// requests respectively. 
	    	filesToDeindex.addAll(filesToReindex);
	    	sp.reset("modifyBinder_indexRemoveFiles").begin();
	        modifyBinder_indexRemoveFiles(binder, filesToDeindex);
	        sp.end().print();
	        
	        sp.reset("modifyBinder_indexAdd").begin();
		    modifyBinder_indexAdd(binder, inputData, fileUploadItems, filesToReindex);
		    sp.end().print();
		    
	    	if (filesErrors.getProblems().size() > 0) {
	    		// At least one error occured during the operation. 
	    		throw new WriteFilesException(filesErrors);
	    	}
	    	else {
	    		return;
	    	}
	    } finally {
		    cleanupFiles(fileUploadItems);
	    }
	}

    protected FilesErrors modifyBinder_filterFiles(Binder binder, List fileUploadItems) throws FilterException {
    	return getFileModule().filterFiles(binder, fileUploadItems);
    }

    protected FilesErrors modifyBinder_processFiles(Binder binder, 
    		List fileUploadItems, FilesErrors filesErrors) {
    	return getFileModule().writeFiles(binder, binder, fileUploadItems, filesErrors);
    }
    protected Map modifyBinder_toEntryData(Binder binder, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
    	Definition def = binder.getEntryDef();
    	if (def == null) {
    		//There is no definition for this binder. Get the default definition.
     		def = getDefinitionModule().setDefaultBinderDefinition(binder);
    	} 
        return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
    }
    protected void modifyBinder_fillIn(Binder binder, InputDataAccessor inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        binder.setModification(new HistoryStamp(user));
        for (Iterator iter=entryData.entrySet().iterator(); iter.hasNext();) {
        	Map.Entry mEntry = (Map.Entry)iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (binder.getCustomAttribute((String)mEntry.getKey()) == null) {
        			Object obj = mEntry.getValue();
        			if (obj instanceof Event)
        				getCoreDao().save(obj);
        	}
        }
        
        EntryBuilder.updateEntry(binder, entryData);

    }
    protected void modifyBinder_removeAttachments(Binder binder, Collection deleteAttachments,
    		List<FileAttachment> filesToDeindex, List<FileAttachment> filesToReindex) {
    	removeAttachments(binder, binder, deleteAttachments, filesToDeindex, filesToReindex);
    }

    protected void modifyBinder_postFillIn(Binder binder, InputDataAccessor inputData, Map entryData) {
    }
    
    protected void modifyBinder_indexAdd(Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems,
    		Collection<FileAttachment> filesToIndex) {
    	indexBinder(binder, fileUploadItems, filesToIndex, false);
    }
    protected void modifyBinder_indexRemoveFiles(Binder binder, Collection<FileAttachment> filesToDeindex) {
    	removeFilesIndex(binder, filesToDeindex);
    }
 
    protected void removeFilesIndex(DefinableEntity entity, Collection<FileAttachment> filesToDeindex) {
		//remove index entry
    	for(FileAttachment fa : filesToDeindex) {
    		removeFileFromIndex(fa);
    	}
    }
    protected void removeAttachments(Binder binder, DefinableEntity entity, 
    		Collection deleteAttachments, List<FileAttachment> filesToDeindex,
    		List<FileAttachment> filesToReindex) {
    	for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    		Attachment a = (Attachment)iter.next();
    		//see if associated with a customAttribute
    		if (a instanceof FileAttachment) {
    			if(a instanceof VersionAttachment) {
    				// Check if we are removing the highest version of the file.
    				// This check must be done _before_ calling deleteVersion on
    				// the file module, since it modifies the metadata.
    				VersionAttachment va = (VersionAttachment) a;
    				FileAttachment fa = va.getParentAttachment();
    				if(fa.getHighestVersionNumber() == va.getVersionNumber()) {
    					// Since we're removing the highest version, we need to re-index
    					// the file with the content from the next highest version.
    					filesToReindex.add(fa);
    				}
    				// Delete from the repository the file associated with the version.
    				getFileModule().deleteVersion(binder, entity, va);
    			}
    			else {
    				filesToDeindex.add((FileAttachment) a);
        			getFileModule().deleteFile(binder, entity, (FileAttachment) a, null);    				
    			}
    		} else {
    			entity.removeAttachment(a);
    		}
    	}    	
    }
    //***********************************************************************************************************
    /**
     * The default behavior is to delete the binder and all its entries
     * There shouldn't be any sub-folders
     */
    public void deleteBinder(Binder binder) {
    	SimpleProfiler sp = new SimpleProfiler(false);
    	
    	sp.reset("deleteBinder_preDelete").begin();
        Object ctx = deleteBinder_preDelete(binder);
        sp.end().print();
        
        sp.reset("deleteBinder_processFiles").begin();
        ctx = deleteBinder_processFiles(binder, ctx);
        sp.end().print();
        
        sp.reset("deleteBinder_delete").begin();
        ctx = deleteBinder_delete(binder, ctx);
        sp.end().print();
        
        sp.reset("deleteBinder_postDelete").begin();
        ctx = deleteBinder_postDelete(binder, ctx);
        sp.end().print();
        
        sp.reset("deleteBinder_indexDel").begin();
        deleteBinder_indexDel(binder, ctx);
        sp.end().print();
    }
    
    protected Object deleteBinder_preDelete(Binder binder) { 
    	return null;
    }
  
    //TODO: delete all files under binder
    protected Object deleteBinder_processFiles(Binder binder, Object ctx) {
//    	getFileModule().deleteFiles(binder);
    	return ctx;
    }
    
    protected Object deleteBinder_delete(Binder binder, Object ctx) {
    	getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMemberships(binder.getZoneName(), binder);
    	getCoreDao().delete(binder);
       	return ctx;
    }
    protected Object deleteBinder_postDelete(Binder binder, Object ctx) {
    	if (binder.getParentBinder() != null) binder.getParentBinder().removeBinder(binder);
       	return ctx;
    }

    protected Object deleteBinder_indexDel(Binder binder, Object ctx) {
        // Delete the document that's currently in the index.
    	// Since all matches will be deleted, this will also delete the attachments
        IndexSynchronizationManager.deleteDocument(binder.getIndexDocumentUid());
       	return ctx;
    }
    
    //***********************************************************************************************************
    public void moveBinder(Binder source, Binder destination) {
    	source.getParentBinder().removeBinder(source);
    	destination.addBinder(source);
 		// The path changes since its parent changed.
 		source.setPathName(destination.getPathName() + "/" + source.getTitle());
    }

    
    //***********************************************************************************************************
    public void indexBinder(Binder binder) {
   		indexBinder(binder, null, null, false);    	
    }
    //***********************************************************************************************************
 
    protected Principal getPrincipal(List users, String userId) {
    	Principal p;
    	for (int i=0; i<users.size(); i++) {
    		p = (Principal)users.get(i);
    		if (p.getId().toString().equalsIgnoreCase(userId)) return p;
    	}
    	return null;
    }

    /**
     * Index binder and optionally its attached files.
     * 
     * @param biner
     * @param fileUploadItems uploaded files or <code>null</code>. 
     * At minimum, those files in the list must be indexed.  
     * @param filesToIndex a list of FileAttachments or <code>null</code>. 
     * At minimum, those files in the list must be indexed. 
     * @param newEntry
     */
    protected void indexBinder(Binder binder, List fileUploadItems, 
    		Collection<FileAttachment> filesToIndex, boolean newEntry) {
    	// Logically speaking, the only files we need to index are the ones
    	// that have been uploaded (fileUploadItems) and the ones explicitly
    	// specified (in the filesToIndex). In ideal world, indexing only
    	// those subset will yield better performance. However, the way 
    	// file indexing currently works is that, whenever some aspect of its 
    	// enclosing entry changes, it deletes not only the entry but also all 
    	// file attachments from the index, because each file index entry 
    	// actually duplicates the common data from the entry itself. (see 
    	// overloaded indexEntry method in this class). Therefore, we must 
    	// (re)index "all" the file attachments in the entry regardless of 
    	// whether a particular file content has changed or not. 
    	// Consequently we obtain and pass "all" the attachments to the 
    	// following method and ignore the filesToIndex list (for now).

    	indexBinderWithAttachments(binder, binder.getFileAttachments(), fileUploadItems, newEntry);
    }
    
    /**
     * Index binder along with its file attachments. Doesn't index the binder entries.
     * 
     * @param binder
      * @param fileAttachments list of FileAttachments that need to be (re)indexed.
     * Only those files explicitly listed in this list are indexed. 
     * @param fileUploadItems This may be <code>null</code> in which case the 
     * contents of the files must be obtained from repositories. If non-null,
     * the files in this list are used for indexing and the elements positionally
     * correspond to the elements in fileAttachments list. 
     * @param newEntry
     */
	protected void indexBinderWithAttachments(Binder binder,
			List fileAttachments, List fileUploadItems, boolean newEntry) {
		if(!newEntry) {
			// This is modification. We must first delete existing document(s) from the index.
			
			// Since all matches will be deleted, this will also delete the attachments 
			IndexSynchronizationManager.deleteDocument(binder.getIndexDocumentUid());
			IndexSynchronizationManager.deleteDocuments(new Term("_binderId",binder.getIndexDocumentUid()));
	        
		}
		
        // Create an index document from the entry object.
		org.apache.lucene.document.Document indexDoc;
		indexDoc = buildIndexDocumentFromBinder(binder);

        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
        
        //Create separate documents one for each attached file and index them.
        for(int i = 0; i < fileAttachments.size(); i++) {
        	FileAttachment fa = (FileAttachment) fileAttachments.get(i);
        	FileUploadItem fui = null;
        	if(fileUploadItems != null)
        		fui = findFileUploadItem(fileUploadItems, fa.getRepositoryServiceName(), fa.getFileItem().getName());
        	try {
        		indexDoc = buildIndexDocumentFromBinderFile(binder, fa, fui);
           		// Register the index document for indexing.
           		IndexSynchronizationManager.addDocument(indexDoc);
           	} catch (Exception ex) {
        		logger.error("Error index file for binder " + binder + " attachment" + fa + " " + ex.getLocalizedMessage());
        	}
        }
	}

    protected org.apache.lucene.document.Document buildIndexDocumentFromBinder(Binder binder) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        
    	fillInIndexDocWithCommonPartFromBinder(indexDoc, binder);

    	// Add search document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_BINDER);
        
        // Add command definition
        EntityIndexUtils.addCommandDefinition(indexDoc, binder); 
        
        // Add the events
        EntityIndexUtils.addEvents(indexDoc, binder);
        
        // Add the tags for this entry
        EntityIndexUtils.addTags(indexDoc, binder, getCoreDao().loadAllTagsByEntity(binder.getEntityIdentifier()));
       
        return indexDoc;
    }   
    protected org.apache.lucene.document.Document buildIndexDocumentFromBinderFile
		(Binder binder, FileAttachment fa, FileUploadItem fui) {
   		org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromFile(binder, binder, fa, fui);
   	    fillInIndexDocWithCommonPartFromBinder(indexDoc, binder);
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
    	(Binder binder, DefinableEntity entity, FileAttachment fa, FileUploadItem fui) {
    	// Prepare for pipeline execution.
    	String text = null;
    	
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
		    	firstConduit.getSink().setInputStream(getFileModule().readFile(binder, entity, fa), false, null);
	    	}
    	
	    	Conduit lastConduit = new RAMConduit();
	    	
	    	try {
		    	// Invoke pipeline.
		    	// TODO For now all pipeline executions are synchronous.
		    	// Asynchronous execution support to be added later.
	    		getPipeline().invoke(firstConduit.getSource(), lastConduit.getSink());
		    	
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
    	
    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_ATTACHMENT);
        
        // Add UID of attachment file (FUID)
        EntityIndexUtils.addFileAttachmentUid(indexDoc, fa);
        
        // Add the filename
        EntityIndexUtils.addFileAttachmentName(indexDoc, fa.getFileItem().getName());        
        
        if(text != null)
        	BasicIndexUtils.addAllText(indexDoc, text);
        
        // TBD Add the filetype and Extension
        //EntryIndexUtils.addFileType(indexDoc,tempFile);

        EntityIndexUtils.addFileExtension(indexDoc, fa.getFileItem().getName());
             
        return indexDoc;
    }
    protected void fillInIndexDocWithCommonPartFromBinder(org.apache.lucene.document.Document indexDoc, 
    		Binder binder) {
    	EntityIndexUtils.addReadAcls(indexDoc,AccessUtils.getReadAclIds(binder));
    	fillInIndexDocWithCommonPart(indexDoc, binder.getParentBinder(), binder);
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
    protected void fillInIndexDocWithCommonPart(org.apache.lucene.document.Document indexDoc, 
    		Binder binder, DefinableEntity entity) {
        // Add uid
        BasicIndexUtils.addUid(indexDoc, entity.getIndexDocumentUid());

        // Add creation-date
        EntityIndexUtils.addCreationDate(indexDoc, entity);
        
        // Add modification-date
        EntityIndexUtils.addModificationDate(indexDoc,entity);
        
        // Add creator id
        EntityIndexUtils.addCreationPrincipalId(indexDoc,entity);
        
        // Add Modification Principal Id
        EntityIndexUtils.addModificationPrincipalId(indexDoc,entity);
        
        // Add ReservedBy Principal Id
        EntityIndexUtils.addModificationPrincipalId(indexDoc,entity);
        
        // Add Doc Id
        EntityIndexUtils.addDocId(indexDoc, entity);
        
        // Add Doc title
        EntityIndexUtils.addTitle(indexDoc, entity);
        
        //Add Rating
        EntityIndexUtils.addRating(indexDoc, entity);
        
        // Add EntityType
        EntityIndexUtils.addEntityType(indexDoc, entity);
        
       // Add data fields driven by the entry's definition object. 
        getDefinitionModule().addIndexFieldsForEntity(indexDoc, entity);
        
    }
    	
    /*
    protected List findCorrespondingFileAttachments(DefinableEntity entity, List fileUploadItems) {
    	List fileAttachments = new ArrayList();
    	for(int i = 0; i < fileUploadItems.size(); i++) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		FileAttachment fa = entity.getFileAttachment(fui.getRepositoryServiceName(), fui.getOriginalFilename());
    		if(fa == null) 
    			throw new InternalException("No FileAttachment corresponding to FileUploadItem");
    		fileAttachments.add(i, fa);
    	}
    	return fileAttachments;
    }*/
    
    protected void cleanupFiles(List fileUploadItems) {
    	if (fileUploadItems != null) {
    		for(int i = 0; i < fileUploadItems.size(); i++) {
    			// 	Get a handle on the uploaded file. 
    			FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    			try {
    				fui.delete();
    			} catch (IOException e) {
    				logger.error(e.getMessage(), e);
    			}
    		}
    			
        }
    }
    protected void removeFileFromIndex(FileAttachment fa) {
    	IndexSynchronizationManager.deleteDocuments(new Term(
    			EntityIndexUtils.FILE_ID_FIELD, fa.getId()));  	
    }
    
	protected FileUploadItem findFileUploadItem(List fileUploadItems, String repositoryName, String fileName) {
		for(int i = 0; i < fileUploadItems.size(); i++) {
			FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
			if(fui.getRepositoryServiceName().equals(repositoryName) &&
					fui.getOriginalFilename().equals(fileName))
				return fui;
		}
		return null;
	}
}
