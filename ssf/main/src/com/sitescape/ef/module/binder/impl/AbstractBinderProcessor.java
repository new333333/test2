package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
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

import org.dom4j.Document;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.InternalException;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.module.binder.BinderProcessor;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.TempFileUtil;
import com.sitescape.ef.web.util.DefinitionUtils;
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
	
	protected void getBinder_accessControl(Binder binder) {
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
	}
	//***********************************************************************************************************	
    public Long addBinder(final Binder parent, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        
        addBinder_accessControl(parent);
        
        Map entryDataAll = addBinder_toEntryData(parent, def, inputData, fileItems);
        final Map entryData = (Map) entryDataAll.get("entryData");
        List fileUploadItems = (List) entryDataAll.get("fileData");
        
        FilesErrors filesErrors = addBinder_filterFiles(parent, fileUploadItems);

        final Binder binder = addBinder_create(clazz);
        binder.setEntryDef(def);
        
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
        
        
        // We must save the entry before processing files because it makes use
        // of the persistent id of the entry. 
        filesErrors = addBinder_processFiles(parent, binder, fileUploadItems, filesErrors);
        
       // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addBinder_indexAdd(parent, binder, inputData, fileUploadItems);
        
        cleanupFiles(fileUploadItems);
        
    	if(filesErrors.getProblems().size() > 0) {
    		// At least one error occured during the operation. 
    		throw new WriteFilesException(filesErrors);
    	}
    	else {
    		return binder.getId();
    	}
    }

     
    public void addBinder_accessControl(Binder parent) throws AccessControlException {
        getAccessControlManager().checkOperation(parent, WorkAreaOperation.CREATE_BINDERS);        
    }
    
    protected FilesErrors addBinder_filterFiles(Binder parent, List fileUploadItems) throws FilterException {
    	return getFileModule().filterFiles(parent, fileUploadItems);
    }

    protected FilesErrors addBinder_processFiles(Binder parent, 
    		Binder binder, List fileUploadItems, FilesErrors filesErrors) {
    	return getFileModule().writeFiles(parent, binder, fileUploadItems, filesErrors);
    }
    
    protected Map addBinder_toEntryData(Binder parent, Definition def, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
    	if (def != null) {
    		return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
    	} else {
    		return new HashMap();
    	}
    }
    
    protected Binder addBinder_create(Class clazz)  {
    	try {
    		return (Binder)clazz.newInstance();
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
    public Long modifyBinder(final Binder binder, final InputDataAccessor inputData, Map fileItems) 
    		throws AccessControlException, WriteFilesException {
	    modifyBinder_accessControl(binder);
	
	    Map entryDataAll = modifyBinder_toEntryData(binder, inputData, fileItems);
	    final Map entryData = (Map) entryDataAll.get("entryData");
	    List fileUploadItems = (List) entryDataAll.get("fileData");
	    
        FilesErrors filesErrors = modifyBinder_filterFiles(binder, fileUploadItems);

	    filesErrors = modifyBinder_processFiles(binder, fileUploadItems, filesErrors);
	    
        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		modifyBinder_fillIn(binder, inputData, entryData);
	                
        		modifyBinder_postFillIn(binder, inputData, entryData);
        		return null;
        	}});
	    modifyBinder_indexAdd(binder, inputData, fileUploadItems);
	    
	    cleanupFiles(fileUploadItems);
	    
    	if (filesErrors.getProblems().size() > 0) {
    		// At least one error occured during the operation. 
    		throw new WriteFilesException(filesErrors);
    	}
    	else {
    		return binder.getId();
    	}
	}

     public void modifyBinder_accessControl(Binder binder) throws AccessControlException {
    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
   }

    protected FilesErrors modifyBinder_filterFiles(Binder binder, List fileUploadItems) throws FilterException {
    	return getFileModule().filterFiles(binder, fileUploadItems);
    }

    protected FilesErrors modifyBinder_processFiles(Binder binder, 
    		List fileUploadItems, FilesErrors filesErrors) {
    	return getFileModule().writeFiles(binder.getParentBinder(), binder, fileUploadItems, filesErrors);
    }
    protected Map modifyBinder_toEntryData(Binder binder, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
    	Definition def = binder.getEntryDef();
    	Document defDoc = null;
    	if (def == null) {
    		//There is no definition for this binder. Get the default definition.
    		Map model = new HashMap();
    		defDoc = DefinitionUtils.getDefaultBinderDefinition(binder, model, "//item[@type='form']");
    	} else {
    		defDoc = def.getDefinition();
    	}
        return getDefinitionModule().getEntryData(defDoc, inputData, fileItems);
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

    protected void modifyBinder_postFillIn(Binder binder, InputDataAccessor inputData, Map entryData) {
    }
    
    protected void modifyBinder_indexAdd(Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems) {
    	indexBinder(binder, fileUploadItems, false);
    }
 
    //***********************************************************************************************************
    
    public void deleteBinder(Binder binder) {
        deleteBinder_accessControl(binder);
        deleteBinder_preDelete(binder);
        deleteBinder_processFiles(binder);
        deleteBinder_delete(binder);
        deleteBinder_postDelete(binder);
        deleteBinder_indexDel(binder);   	
    }
    
    public void deleteBinder_accessControl(Binder binder) {
    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
     }
    protected void deleteBinder_preDelete(Binder binder) {    	
    }
  
    //TODO: delete all files under binder
    protected void deleteBinder_processFiles(Binder binder) {
    	getFileModule().deleteFiles(binder.getParentBinder(), binder);
    }
    
    protected void deleteBinder_delete(Binder binder) {
    	getCoreDao().delete(binder);
    }
    protected void deleteBinder_postDelete(Binder binder) {
    }

    protected void deleteBinder_indexDel(Binder binder) {
        // Delete the document that's currently in the index.
    	// Since all matches will be deleted, this will also delete the attachments
        IndexSynchronizationManager.deleteDocument(binder.getIndexDocumentUid());
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

 
    protected Set getReadAclIds(Binder binder) {
         List readMemberIds = getAccessControlManager().getWorkAreaAccessControl(binder, WorkAreaOperation.READ_ENTRIES);
 		Set<Long> ids = new HashSet<Long>();
 		ids.addAll(readMemberIds);
 		//TODO: this doesn't make sense on an index-need to get creator
 		if (getAccessControlManager().testOperation(binder, WorkAreaOperation.CREATOR_READ))
 			ids.add(binder.getCreatorId());			
 		return ids;
     	 
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

    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_BINDER);
        
        // Add command definition
        EntryIndexUtils.addCommandDefinition(indexDoc, binder); 
        
        // Add the events
        EntryIndexUtils.addEvents(indexDoc, binder);
        
        
        return indexDoc;
    }   
    protected org.apache.lucene.document.Document buildIndexDocumentFromBinderFile
		(Binder binder, FileAttachment fa, FileUploadItem fui) {
    	org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromFile(binder.getParentBinder(), binder, fa, fui);
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
	    			getFileModule().readFile(binder, entity, fa, new BufferedOutputStream(new FileOutputStream(tempFile)));
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
    protected void fillInIndexDocWithCommonPartFromBinder(org.apache.lucene.document.Document indexDoc, 
    		Binder binder) {
    	EntryIndexUtils.addReadAcls(indexDoc,getReadAclIds(binder));
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

        //add parent binder
        EntryIndexUtils.addBinder(indexDoc, binder);
        // Add creation-date
        EntryIndexUtils.addCreationDate(indexDoc, entity);
        
        // Add modification-date
        EntryIndexUtils.addModificationDate(indexDoc,entity);
        
        // Add creator id
        EntryIndexUtils.addCreationPrincipalId(indexDoc,entity);
        
        // Add Modification Principal Id
        EntryIndexUtils.addModificationPrincipalId(indexDoc,entity);
        
        // Add ReservedBy Principal Id
        EntryIndexUtils.addModificationPrincipalId(indexDoc,entity);
        
        // Add Doc Id
        EntryIndexUtils.addDocId(indexDoc, entity);
        
        // Add Doc title
        EntryIndexUtils.addTitle(indexDoc, entity);
        
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

}
