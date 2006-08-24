package com.sitescape.ef.module.binder.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.dom4j.Document;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.TitleException;
import com.sitescape.ef.module.binder.AccessUtils;
import com.sitescape.ef.module.binder.BinderProcessor;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntityIndexUtils;
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
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.util.Validator;
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
                
        Map entryDataAll = addBinder_toEntryData(parent, def, inputData, fileItems);
        final Map entryData = (Map) entryDataAll.get("entryData");
        List fileUploadItems = (List) entryDataAll.get("fileData");
        
        final Binder binder = addBinder_create(def, clazz);
    	if (def != null) {
    		if ((parent.getDefinitionType() == null) ||
    				(binder.getDefinitionType().intValue() != parent.getDefinitionType().intValue())) {
    			binder.setDefinitionsInherited(false);
    		}
    	}
        String title = (String)entryData.get("title");
        getCoreDao().validateTitle(parent, title);
        binder.setPathName(parent.getPathName() + "/" + title);
        
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
           
        //Need to do filter here after binder is saved cause it makes use of
        // the id of binder
        FilesErrors filesErrors = addBinder_filterFiles(binder, fileUploadItems);
        // We must save the entry before processing files because it makes use
        // of the persistent id of the entry. 
        filesErrors = addBinder_processFiles(binder, fileUploadItems, filesErrors);
        
       // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addBinder_indexAdd(parent, binder, inputData, fileUploadItems);
        
        cleanupFiles(fileUploadItems);
        
    	if(filesErrors.getProblems().size() > 0) {
    		// At least one error occured during the operation. 
    		throw new WriteFilesException(filesErrors);
    	}
    	else {
    		return binder;
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
        
    	indexBinder(binder, fileUploadItems, true);
    }
 	
 	
    //***********************************************************************************************************
    public void modifyBinder(final Binder binder, final InputDataAccessor inputData, 
    		Map fileItems, final Collection deleteAttachments) 
    		throws AccessControlException, WriteFilesException {
	
	    Map entryDataAll = modifyBinder_toEntryData(binder, inputData, fileItems);
	    final Map entryData = (Map) entryDataAll.get("entryData");
	    List fileUploadItems = (List) entryDataAll.get("fileData");

	    if (entryData.containsKey("title")) {
	    	String newTitle = (String)entryData.get("title");
	    	if (!newTitle.equalsIgnoreCase(binder.getTitle())) getCoreDao().validateTitle(binder.getParentBinder(), newTitle);
	    		
	    }
	    FilesErrors filesErrors = modifyBinder_filterFiles(binder, fileUploadItems);

	    filesErrors = modifyBinder_processFiles(binder, fileUploadItems, filesErrors);
	    
        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		String oldTitle = binder.getTitle();
        		modifyBinder_fillIn(binder, inputData, entryData);
	            modifyBinder_removeAttachments(binder, deleteAttachments);    
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
        modifyBinder_indexRemoveFiles(binder, deleteAttachments);
	    modifyBinder_indexAdd(binder, inputData, fileUploadItems);
	    
	    cleanupFiles(fileUploadItems);
	    
    	if (filesErrors.getProblems().size() > 0) {
    		// At least one error occured during the operation. 
    		throw new WriteFilesException(filesErrors);
    	}
    	else {
    		return;
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
    protected void modifyBinder_removeAttachments(Binder binder, Collection deleteAttachments) {
    	removeAttachments(binder, binder, deleteAttachments);
    }

    protected void modifyBinder_postFillIn(Binder binder, InputDataAccessor inputData, Map entryData) {
    }
    
    protected void modifyBinder_indexAdd(Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems) {
    	indexBinder(binder, fileUploadItems, false);
    }
    protected void modifyBinder_indexRemoveFiles(Binder binder, Collection attachments) {
    	removeFilesIndex(binder, attachments);
    }
 
    protected void removeFilesIndex(DefinableEntity entity, Collection attachments) {
		//remove index entry
    	for (Iterator iter=attachments.iterator(); iter.hasNext();) {
    		Attachment a = (Attachment)iter.next();
    		if (a instanceof FileAttachment) {
    			removeFileFromIndex((FileAttachment)a);
    		}
    	}    	
    }
    protected void removeAttachments(Binder binder, DefinableEntity entity, Collection deleteAttachments) {
    	for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    		Attachment a = (Attachment)iter.next();
    		//see if associated with a customAttribute
    		if (a instanceof FileAttachment) {
    			FileAttachment fa = (FileAttachment)a;
    			getFileModule().deleteFile(binder, entity, fa, null);
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
        Object ctx = deleteBinder_preDelete(binder);
        ctx = deleteBinder_processFiles(binder, ctx);
        ctx = deleteBinder_delete(binder, ctx);
        ctx = deleteBinder_postDelete(binder, ctx);
        deleteBinder_indexDel(binder, ctx);   	
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
     * @param fileUploadItems If this is null, all attached files currently in
     * the entry are indexed as well. If this is non-null, only those files
     * in the list are indexed. 
     * @param newEntry
     */
    protected void indexBinder(Binder binder,
    		List fileUploadItems, boolean newEntry) {
    	if(fileUploadItems != null) {
    		indexBinder(binder, findCorrespondingFileAttachments(binder, fileUploadItems), fileUploadItems, newEntry);
    	}
    	else {
    		indexBinder(binder, binder.getFileAttachments(), null, newEntry);
    	}
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
	protected void indexBinder(Binder binder,
			List fileAttachments, List fileUploadItems, boolean newEntry) {
		if(!newEntry) {
			// This is modification. We must first delete existing document(s) from the index.
			
			// Since all matches will be deleted, this will also delete the attachments 
	        IndexSynchronizationManager.deleteDocument(binder.getIndexDocumentUid());
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
        		fui = (FileUploadItem) fileUploadItems.get(i);
        	indexDoc = buildIndexDocumentFromBinderFile(binder, fa, fui);
        	if(indexDoc != null) {       		
        		// Register the index document for indexing.
        		IndexSynchronizationManager.addDocument(indexDoc);
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
        
        
        return indexDoc;
    }   
    protected org.apache.lucene.document.Document buildIndexDocumentFromBinderFile
		(Binder binder, FileAttachment fa, FileUploadItem fui) {
    	org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromFile(binder, binder, fa, fui);
    	if (indexDoc != null)
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
    	
    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_ATTACHMENT);
        
        // Add UID of attachment file (FUID)
        EntityIndexUtils.addFileAttachmentUid(indexDoc, fa);
        
        // Add the filename
        EntityIndexUtils.addFileAttachmentName(indexDoc,fui.getOriginalFilename());        
        
        if(text != null)
        	BasicIndexUtils.addAllText(indexDoc, text);
        
        // TBD Add the filetype and Extension
        //EntryIndexUtils.addFileType(indexDoc,tempFile);

        EntityIndexUtils.addFileExtension(indexDoc,fui.getOriginalFilename());
                
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
    protected void removeFileFromIndex(FileAttachment fa) {
    	IndexSynchronizationManager.deleteDocuments(new Term(
    			EntityIndexUtils.FILE_ID_FIELD, fa.getId()));  	
    }
}
