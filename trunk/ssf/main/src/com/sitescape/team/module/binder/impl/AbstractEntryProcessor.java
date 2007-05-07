/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.binder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.TitleException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowResponse;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.binder.EntryProcessor;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.file.FilesErrors;
import com.sitescape.team.module.file.FilterException;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.SearchUtils;
import com.sitescape.team.module.workflow.WorkflowUtils;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.security.acl.AclContainer;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;
/**
 *
 * Add entries to the binder
 * @author Jong Kim
 */
public abstract class AbstractEntryProcessor extends AbstractBinderProcessor 
	implements EntryProcessor {
    
	private static final int DEFAULT_MAX_CHILD_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	//***********************************************************************************************************	
    
	public Entry addEntry(final Binder binder, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems) 
    	throws WriteFilesException {
		Boolean filesFromApplet = new Boolean (false);
		return addEntry(binder, def, clazz, inputData, fileItems, filesFromApplet);
	}
	
	public Entry addEntry(final Binder binder, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems, Boolean filesFromApplet) 
    	throws WriteFilesException {
        // This default implementation is coded after template pattern. 
        
    	SimpleProfiler sp = new SimpleProfiler(false);
        final Map ctx = addEntry_setCtx(binder, null);
    	Map entryDataAll;
    	if (!filesFromApplet) {
        	sp.start("addEntry_toEntryData");
            entryDataAll = addEntry_toEntryData(binder, def, inputData, fileItems, ctx);
            sp.stop("addEntry_toEntryData");
    	}
    	else {
	    	sp.start("createNewEntryWithAttachmentAndTitle");
	    	entryDataAll = createNewEntryWithAttachmentAndTitle(def, inputData, fileItems, ctx);
		    sp.stop("createNewEntryWithAttachmentAndTitle");
    	}        
        
        final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
        List fileUploadItems = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
        
        try {
        	
        	sp.start("addEntry_create");
        	final Entry entry = addEntry_create(def, clazz, ctx);
        	sp.stop("addEntry_create");
        
        	sp.start("addEntry_transactionExecute");
        	// 	The following part requires update database transaction.
        	getTransactionTemplate().execute(new TransactionCallback() {
        		public Object doInTransaction(TransactionStatus status) {
        			//need to set entry/binder information before generating file attachments
        			//Attachments/Events need binder info for AnyOwner
        			addEntry_fillIn(binder, entry, inputData, entryData, ctx);
        			addEntry_preSave(binder, entry, inputData, entryData, ctx);      
        			addEntry_save(binder, entry, inputData, entryData,ctx);      
                   	//After the entry is successfully added, start up any associated workflows
                	addEntry_startWorkflow(entry, ctx);
         			addEntry_postSave(binder, entry, inputData, entryData, ctx);
       			return null;
        		}
        	});
        	sp.stop("addEntry_transactionExecute");
        	
           	// We must save the entry before processing files because it makes use
        	// of the persistent id of the entry. 
            sp.start("addEntry_filterFiles");
        	FilesErrors filesErrors = addEntry_filterFiles(binder, entry, entryData, fileUploadItems, ctx);
        	sp.stop("addEntry_filterFiles");

        	sp.start("addEntry_processFiles");
        	// We must save the entry before processing files because it makes use
        	// of the persistent id of the entry. 
        	filesErrors = addEntry_processFiles(binder, entry, fileUploadItems, filesErrors, ctx);
        	sp.stop("addEntry_processFiles");
        
 
        	sp.start("addEntry_indexAdd");
        	// This must be done in a separate step after persisting the entry,
        	// because we need the entry's persistent ID for indexing. 
        	addEntry_indexAdd(binder, entry, inputData, fileUploadItems, ctx);
        	sp.stop("addEntry_indexAdd");
        	
        	sp.start("addEntry_done");
        	addEntry_done(binder, entry, inputData, ctx);
        	sp.stop("addEntry_done");
        	
        	sp.print();
        	
         	if(filesErrors.getProblems().size() > 0) {
        		// At least one error occured during the operation. 
        		throw new WriteFilesException(filesErrors);
        	}
        	else {
        		return entry;
        	}
        } finally {
           	cleanupFiles(fileUploadItems);       	
        }
    }
	
    //Method Used to get the files uploaded by the Applet and title information from the entry
    protected Map createNewEntryWithAttachmentAndTitle(Definition def, InputDataAccessor inputData, Map fileItems, Map ctx)
    {
    	List fileData = new ArrayList();
    	String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;

    	Map entryDataAll = new HashMap();
    	Map entryData = new HashMap();
        entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA,  entryData);    	

		if (inputData.exists(ObjectKeys.FIELD_ENTITY_TITLE)) entryData.put(ObjectKeys.FIELD_ENTITY_TITLE, inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_TITLE));
        
        if (def != null) {
        	boolean blnCheckForAppletFile = true;
        	int intFileCount = 1;

        	while (blnCheckForAppletFile) {
        		String fileEleName = nameValue + Integer.toString(intFileCount);
        		if (fileItems.containsKey(fileEleName)) {
        	    	MultipartFile myFile = (MultipartFile)fileItems.get(fileEleName);
        	    	String fileName = myFile.getOriginalFilename();
        	    	
        	    	if (fileName != null && !fileName.equals("")) {
            	    	// Different repository can be specified for each file uploaded.
            	    	// If not specified, use the statically selected one.  
            	    	String repositoryName = null;
            	    	if (inputData.exists(nameValue + "_repos" + Integer.toString(intFileCount))) 
            	    		repositoryName = inputData.getSingleValue(nameValue + "_repos" + Integer.toString(intFileCount));
            	    	if (repositoryName == null) {
            		    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
            	    	}
            	    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
            	    	fileData.add(fui);
        	    	}
        	    	intFileCount++;
        		}
        		else {
        			blnCheckForAppletFile = false;
        		}
        	}
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  fileData);
            
        } else {
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  new ArrayList());
        }
    	
        return entryDataAll;
    }
    protected Map addEntry_setCtx(Binder binder, Map ctx) {
    	return ctx;
    }

    private void checkInputFileNames(List fileUploadItems, FilesErrors errors) {
   		//name must be unique within Entry
   		for (int i=0; i<fileUploadItems.size(); ++i) {
			FileUploadItem fui1 = (FileUploadItem)fileUploadItems.get(i);
			for (int j=i+1; j<fileUploadItems.size(); ) {
    			FileUploadItem fui2 = (FileUploadItem)fileUploadItems.get(j);
    			if (fui1.getOriginalFilename().equalsIgnoreCase(fui2.getOriginalFilename()) &&
    				!fui1.getRepositoryName().equals(fui2.getRepositoryName())) {
    				fileUploadItems.remove(j);
    				errors.addProblem(new FilesErrors.Problem(fui1.getRepositoryName(), 
       	   				fui1.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, new TitleException(fui1.getOriginalFilename())));
    			} else ++j;
			}
   		}
    }
    
    private void checkInputFilesForNonMirroredBinder(Binder nonMirroredBinder,
			List fileUploadItems, FilesErrors errors) {
		for (int i = 0; i < fileUploadItems.size();) {
			FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
			if (fui.getRepositoryName().equals(ObjectKeys.FI_ADAPTER)) {
				fileUploadItems.remove(i);
				errors.addProblem(new FilesErrors.Problem(
								fui.getRepositoryName(),
								fui.getOriginalFilename(),
								FilesErrors.Problem.PROBLEM_MIRRORED_FILE_IN_REGULAR_FOLDER,
								new IllegalArgumentException("Binder ["
										+ nonMirroredBinder.getPathName()
										+ "] is not a mirrored folder")));
			} else {
				i++;
			}
		}
	}
    
    private void checkInputFilesForMirroredBinder(List fileUploadItems, FilesErrors errors) {
		String mirroredFileName = null;
		for(int i = 0; i < fileUploadItems.size();) {
			FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
			if(fui.getRepositoryName().equals(ObjectKeys.FI_ADAPTER)) {
				if(mirroredFileName == null) {
					mirroredFileName = fui.getOriginalFilename();
					i++;
				}
				else {
					if(mirroredFileName.equals(fui.getOriginalFilename())) {
						i++;
					}
					else {
	   					fileUploadItems.remove(i);
	    				errors.addProblem(new FilesErrors.Problem
								(fui.getRepositoryName(), fui.getOriginalFilename(), 
										FilesErrors.Problem.PROBLEM_MIRRORED_FILE_MULTIPLE, 
										new IllegalArgumentException("The entry already mirrors another file [" + mirroredFileName + "]")));
					}
				}
			}
		}
    }
    
    protected FilesErrors addEntry_filterFiles(Binder binder, Entry entry, 
    		Map entryData, List fileUploadItems, Map ctx) throws FilterException {
   		FilesErrors nameErrors = new FilesErrors();
   		
   		checkInputFileNames(fileUploadItems, nameErrors);
    			 
   		if(!binder.isMirrored()) {
   			checkInputFilesForNonMirroredBinder(binder, fileUploadItems, nameErrors);
   		}
   		else {
   			checkInputFilesForMirroredBinder(fileUploadItems, nameErrors);
   		}
   		
   		if (binder.isLibrary()) {
    		// 	Make sure the file name is unique if requested		
    		for (int i=0; i<fileUploadItems.size(); ) {
    			FileUploadItem fui = (FileUploadItem)fileUploadItems.get(i);
    			try {
    				getCoreDao().registerFileName(binder, entry, fui.getOriginalFilename());
    				fui.setRegistered(true);
    				++i;
    			} catch (TitleException te) {
    				fileUploadItems.remove(i);
    				nameErrors.addProblem(new FilesErrors.Problem(fui.getRepositoryName(), 
       	   					fui.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, te));
    			}
    		}
   		}
   		FilesErrors filterErrors = getFileModule().filterFiles(binder, fileUploadItems);
    	filterErrors.getProblems().addAll(nameErrors.getProblems());
    	return filterErrors;
    }

    protected FilesErrors addEntry_processFiles(Binder binder, 
    		Entry entry, List fileUploadItems, FilesErrors filesErrors, Map ctx) {
    	return getFileModule().writeFiles(binder, entry, fileUploadItems, filesErrors);
    }
    
    protected Map addEntry_toEntryData(Binder binder, Definition def, InputDataAccessor inputData, Map fileItems, Map ctx) {
        //Call the definition processor to get the entry data to be stored
        if (def != null) {
        	return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
        } else {
        	//handle basic fields only without definition
        	Map entryDataAll = new HashMap();
	        Map entryData = new HashMap();
			List fileData = new ArrayList();
			entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA, entryData);
			entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA, fileData);
 			if (inputData.exists(ObjectKeys.FIELD_ENTITY_TITLE)) entryData.put(ObjectKeys.FIELD_ENTITY_TITLE, inputData.getSingleValue("title"));
			if (inputData.exists(ObjectKeys.FIELD_ENTITY_DESCRIPTION)) {
				Description description = new Description();
				description.setText(inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_DESCRIPTION));
				WebHelper.scanDescriptionForUploadFiles(description, fileData);
				WebHelper.scanDescriptionForAttachmentFileUrls(description);
				description.setFormat(Description.FORMAT_HTML);
				entryData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
			}
      	
        	return entryDataAll;
        }
    }
    
    protected Entry addEntry_create(Definition def, Class clazz, Map ctx)  {
    	try {
    		Entry entry = (Entry)clazz.newInstance();
           	entry.setEntryDef(def);
        	if (def != null) {
        		entry.setDefinitionType(new Integer(def.getType()));
  	        	String icon = DefinitionUtils.getPropertyValue(def.getDefinition().getRootElement(), "icon");
   	        	if (Validator.isNotNull(icon)) entry.setIconName(icon);
         	}
        	return entry;
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(user));
        entry.setModification(entry.getCreation());
        entry.setParentBinder(binder);
        entry.setLogVersion(Long.valueOf(1));
        
        //initialize collections, or else hibernate treats any new 
        //empty collections as a change and attempts a version update which
        //may happen outside the transaction
        entry.getAttachments();
        entry.getEvents();
        entry.getCustomAttributes();
        
        // The entry inherits acls from the parent by default, except workflow
        if ((entry instanceof AclControlled) && (binder instanceof AclContainer)) {
        	getAclManager().doInherit((AclContainer)binder, (AclControlled) entry);
        }
        for (Iterator iter=entryData.values().iterator(); iter.hasNext();) {
        	Object obj = iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (obj instanceof Event)
        		getCoreDao().save(obj);
        }
        EntryBuilder.buildEntry(entry, entryData);
        takeCareOfLastModDate(entry, inputData);
        
    }

    //inside write transaction
    protected void addEntry_preSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    }

    //inside write transaction
    protected void addEntry_save(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx){
        getCoreDao().save(entry);
    }
    
    //inside write transaction
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//create history - using timestamp and version from fillIn
		if (binder.isUniqueTitles()) getCoreDao().updateTitle(binder, entry, null, entry.getNormalTitle());
    	processChangeLog(entry, ChangeLog.ADDENTRY);
    	getReportModule().addAuditTrail(AuditType.add, entry);
    }

    protected void addEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems, Map ctx){
        //no tags typically exists on a new entry - reduce db lookups by supplying list
    	List tags = null;
    	if (ctx != null) tags = (List)ctx.get(ObjectKeys.INPUT_FIELD_TAGS);
    	if (tags == null) tags = new ArrayList();
    	indexEntry(binder, entry, fileUploadItems, null, true, tags);
    }
 
    protected void addEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
    }
 
    //inside write transaction
    protected void addEntry_startWorkflow(Entry entry, Map ctx){
    	if (!(entry instanceof WorkflowSupport)) return;
    	Binder binder = entry.getParentBinder();
    	Map workflowAssociations = (Map) binder.getWorkflowAssociations();
    	if (workflowAssociations != null) {
    		//See if the entry definition type has an associated workflow
    		Definition entryDef = entry.getEntryDef();
    		if (entryDef != null) {
    			Definition wfDef = (Definition)workflowAssociations.get(entryDef.getId());
    			if (wfDef != null)	getWorkflowModule().addEntryWorkflow((WorkflowSupport)entry, entry.getEntityIdentifier(), wfDef);
    		}
    	}
    }
 	
   //***********************************************************************************************************
    public void modifyEntry(final Binder binder, final Entry entry, 
    		final InputDataAccessor inputData, Map fileItems, 
    		final Collection deleteAttachments, final Map<FileAttachment,String> fileRenamesTo)  
    		throws WriteFilesException {
    	
    	Boolean filesFromApplet = new Boolean(false);
    	modifyEntry(binder, entry, inputData, fileItems, deleteAttachments, fileRenamesTo, filesFromApplet);
    }
    
    public FilesErrors modifyEntry(final Binder binder, final Entry entry, 
    		final InputDataAccessor inputData, Map fileItems, 
    		final Collection deleteAttachments, final Map<FileAttachment,String> fileRenamesTo, Boolean filesFromApplet)  
    		throws WriteFilesException {
    	SimpleProfiler sp = new SimpleProfiler(false);
        final Map ctx = modifyEntry_setCtx(entry, null);

    	Map entryDataAll;
    	if (!filesFromApplet) {
	    	sp.start("modifyEntry_toEntryData");
	    	entryDataAll = modifyEntry_toEntryData(entry, inputData, fileItems, ctx);
		    sp.stop("modifyEntry_toEntryData");
    	}
    	else {
	    	sp.start("getFilesUploadedByApplet");
	    	entryDataAll = getFilesUploadedByApplet(entry, inputData, fileItems, ctx);
		    sp.stop("getFilesUploadedByApplet");
    	}
	    
	    final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	    List fileUploadItems = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
        
	    try {	    	
	    	sp.start("modifyEntry_transactionExecute");
	    	// The following part requires update database transaction.
	    	//ctx can be used by sub-classes to pass info
	    	getTransactionTemplate().execute(new TransactionCallback() {
	    		public Object doInTransaction(TransactionStatus status) {
	    			modifyEntry_fillIn(binder, entry, inputData, entryData, ctx);
	    	    	modifyEntry_startWorkflow(entry, ctx);
	    			modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo, ctx);
 	    			return null;
	    		}});
	    	sp.stop("modifyEntry_transactionExecute");
	        //handle outside main transaction so main changeLog doesn't reflect attactment changes
	        sp.start("modifyBinder_removeAttachments");
	    	List<FileAttachment> filesToDeindex = new ArrayList<FileAttachment>();
	    	List<FileAttachment> filesToReindex = new ArrayList<FileAttachment>();	    
            modifyEntry_removeAttachments(binder, entry, deleteAttachments, filesToDeindex, filesToReindex, ctx);
	        sp.stop("modifyBinder_removeAttachments");
	    	
	    	sp.start("modifyEntry_filterFiles");
	    	FilesErrors filesErrors = modifyEntry_filterFiles(binder, entry, entryData, fileUploadItems, ctx);
	    	sp.stop("modifyEntry_filterFiles");

           	sp.start("modifyEntry_processFiles");
	    	filesErrors = modifyEntry_processFiles(binder, entry, fileUploadItems, filesErrors, ctx);
	    	sp.stop("modifyEntry_processFiles");


	    	// Since index update is implemented as removal followed by add, 
	    	// the update requests must be added to the removal and then add
	    	// requests respectively. 
	    	filesToDeindex.addAll(filesToReindex);
	    	sp.start("modifyEntry_indexRemoveFiles");
	    	modifyEntry_indexRemoveFiles(binder, entry, filesToDeindex, ctx);
	    	sp.stop("modifyEntry_indexRemoveFiles");
	    	
	    	sp.start("modifyEntry_indexAdd");
	    	modifyEntry_indexAdd(binder, entry, inputData, fileUploadItems, filesToReindex,ctx);
	    	sp.stop("modifyEntry_indexAdd");
	    	
	    	sp.start("modifyEntry_done");
	    	modifyEntry_done(binder, entry, inputData,ctx);
	    	sp.stop("modifyEntry_done");
	    		   
	    	sp.print();
	    	
	    	if(filesErrors.getProblems().size() > 0) {
	    		// At least one error occured during the operation. 
	    		//throw new WriteFilesException(filesErrors);
	    		return filesErrors;
	    	}
	    	else {
	    		return null;
	    	} 
	    }finally {
		    cleanupFiles(fileUploadItems);
	    }
	}

    //Method Used to get the files uploaded by the Applet
    protected Map getFilesUploadedByApplet(Entry entry, InputDataAccessor inputData, Map fileItems, Map ctx)
    {
    	List fileData = new ArrayList();
    	String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;
    	Map entryDataAll = new HashMap();
    	
    	//No Definition Related Information - So it is set to empty HashMap
        entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA,  new HashMap());    	
    	
        //Call the definition processor to get the entry data to be stored
        Definition def = entry.getEntryDef();
        if (def != null) {
        	boolean blnCheckForAppletFile = true;
        	int intFileCount = 1;

        	while (blnCheckForAppletFile) {
        		String fileEleName = nameValue + Integer.toString(intFileCount);
        		if (fileItems.containsKey(fileEleName)) {
        	    	MultipartFile myFile = (MultipartFile)fileItems.get(fileEleName);
        	    	String fileName = myFile.getOriginalFilename();
        	    	
        	    	if (fileName != null && !fileName.equals("")) {
            	    	// Different repository can be specified for each file uploaded.
            	    	// If not specified, use the statically selected one.  
            	    	String repositoryName = null;
            	    	if (inputData.exists(nameValue + "_repos" + Integer.toString(intFileCount))) 
            	    		repositoryName = inputData.getSingleValue(nameValue + "_repos" + Integer.toString(intFileCount));
            	    	if (repositoryName == null) {
            	    		repositoryName = RepositoryUtil.getDefaultRepositoryName();
            		    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
            	    	}
            	    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
            	    	fileData.add(fui);
        	    	}
        	    	intFileCount++;
        		}
        		else {
        			blnCheckForAppletFile = false;
        		}
        	}
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  fileData);
            
        } else {
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  new ArrayList());
        }
    	
        return entryDataAll;
    }
    protected Map modifyEntry_setCtx(Entry entry, Map ctx) {
    	if (ctx == null) ctx = new HashMap();
    	//save normalized title and title before changes
		ctx.put(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE, entry.getNormalTitle());
		ctx.put(ObjectKeys.FIELD_ENTITY_TITLE, entry.getTitle());
    	return ctx;
    }
   protected FilesErrors modifyEntry_filterFiles(Binder binder, Entry entry,
    		Map entryData, List fileUploadItems, Map ctx) throws FilterException, TitleException {
   		FilesErrors nameErrors = new FilesErrors();
   		
   		checkInputFileNames(fileUploadItems, nameErrors);
   		
   		if(!binder.isMirrored()) {
   			checkInputFilesForNonMirroredBinder(binder, fileUploadItems, nameErrors);
   		}
   		else {
   			List<FileAttachment> fas = entry.getFileAttachments(ObjectKeys.FI_ADAPTER); // should be at most 1 in size
   			if(fas.size() > 1)
   				logger.warn("Integrity error: Entry " + entry.getId() + " in binder [" + binder.getPathName() + "] mirrors multiple files");
   			if(fas.isEmpty()) {
   	   			checkInputFilesForMirroredBinder(fileUploadItems, nameErrors);
   			}
   			else {
   	  			for(int i = 0; i < fileUploadItems.size(); i++) {
   	   				FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
   	   				if(fui.getRepositoryName().equals(ObjectKeys.FI_ADAPTER)) {
	   					for(FileAttachment fa : fas) {
	   						if(!fui.getOriginalFilename().equals(fa.getFileItem().getName())) {
	   		   					fileUploadItems.remove(i);
	   							nameErrors.addProblem(new FilesErrors.Problem
	   									(fui.getRepositoryName(), fui.getOriginalFilename(), 
	   											FilesErrors.Problem.PROBLEM_MIRRORED_FILE_MULTIPLE, 
	   											new IllegalArgumentException("The entry " + entry.getId() + 
	   													" already mirrors another file [" + fa.getFileItem().getName() + "]")));
	   							i--;
	   							break;					
	   						}
	   					}
   	   				}
   	  			}   				
   			}
   		}
   		
    	// 	Make sure the file name is unique if requested		
    	for (int i=0; i<fileUploadItems.size(); ) {
    		FileUploadItem fui = (FileUploadItem)fileUploadItems.get(i);
       		String name = fui.getOriginalFilename();
    		try {
    			//check if name exists
    			FileAttachment fa = entry.getFileAttachment(name);
    			if (fa == null) {
    		   		if (binder.isLibrary()) {
    		   			getCoreDao().registerFileName(binder, entry, fui.getOriginalFilename());
    		   			fui.setRegistered(true);
    		   		}
    		   		++i;
       			} else if (!fui.getRepositoryName().equals(fa.getRepositoryName())) {
       	   			fileUploadItems.remove(i);
       	   			nameErrors.addProblem(new FilesErrors.Problem(fa.getRepositoryName(), 
       	  					fui.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, new TitleException(fui.getOriginalFilename())));
       			} else 	++i;      				
    		} catch (TitleException te) {
    			fileUploadItems.remove(i);
    			nameErrors.addProblem(new FilesErrors.Problem(fui.getRepositoryName(), 
       	  					fui.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, te));
    		}
    	}
   		
    	FilesErrors filterErrors = getFileModule().filterFiles(binder, fileUploadItems);
    	filterErrors.getProblems().addAll(nameErrors.getProblems());
    	return filterErrors;
    }

    protected FilesErrors modifyEntry_processFiles(Binder binder, 
    		Entry entry, List fileUploadItems, FilesErrors filesErrors, Map ctx) {
    	return getFileModule().writeFiles(binder, entry, fileUploadItems, filesErrors);
    }
    protected void modifyEntry_removeAttachments(Binder binder, Entry entry, 
    		Collection deleteAttachments, List<FileAttachment> filesToDeindex,
    		List<FileAttachment> filesToReindex, Map ctx) {
       	removeAttachments(binder, entry, deleteAttachments, filesToDeindex, filesToReindex);
       	
    }
    protected void modifyEntry_indexRemoveFiles(Binder binder, Entry entry, Collection<FileAttachment> filesToDeindex, Map ctx) {
    	removeFilesIndex(entry, filesToDeindex);
    }
   
    protected Map modifyEntry_toEntryData(Entry entry, InputDataAccessor inputData, Map fileItems, Map ctx) {
        //Call the definition processor to get the entry data to be stored
        Definition def = entry.getEntryDef();
        if (def != null) {
            return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
        } else {
           	Map entryDataAll = new HashMap();
	        entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA,  new HashMap());
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  new ArrayList());
	        return entryDataAll;
        }
    }
    //inside write transaction
    protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setModification(new HistoryStamp(user));
        entry.incrLogVersion();
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
 	   takeCareOfLastModDate(entry, inputData);
 
    }
    //inside write transaction
    protected void modifyEntry_startWorkflow(Entry entry, Map ctx) {
    	if (!(entry instanceof WorkflowSupport)) return;
    	WorkflowSupport wEntry = (WorkflowSupport)entry;
    	//see if updates to entry, trigger transitions in workflow
    	if (!wEntry.getWorkflowStates().isEmpty()) getWorkflowModule().modifyWorkflowStateOnUpdate(wEntry);
     }   
    //inside write transaction
    protected void modifyEntry_postFillIn(Binder binder, Entry entry, InputDataAccessor inputData, 
    		Map entryData, Map<FileAttachment,String> fileRenamesTo, Map ctx) {
    	//create history - using timestamp and version from fillIn
  		if (entry.isTop() && binder.isUniqueTitles()) getCoreDao().updateTitle(binder, entry, (String)ctx.get(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE), entry.getNormalTitle());		
    	processChangeLog(entry, ChangeLog.MODIFYENTRY);
    	getReportModule().addAuditTrail(AuditType.modify, entry);

    	if(fileRenamesTo != null)
	    	for(FileAttachment fa : fileRenamesTo.keySet()) {
	    		String toName = fileRenamesTo.get(fa);
	    		getFileModule().renameFile(binder, entry, fa, toName);
	    	}
    }
    
    protected void modifyEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems, 
    		Collection<FileAttachment> filesToIndex, Map ctx) {
    	//tags will be null for now
    	indexEntry(binder, entry, fileUploadItems, filesToIndex, false, 
    			(ctx == null ? null : (List)ctx.get(ObjectKeys.INPUT_FIELD_TAGS )));
    }

    protected void modifyEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
    }
    protected void takeCareOfLastModDate(Entry entry, InputDataAccessor inputData) {
 	   Date lastModDate = (Date) inputData.getSingleObject(ObjectKeys.PI_LAST_MODIFIED);
 	   if(lastModDate != null) {
 		   // We have a caller-supplied last-modified date.
 	        User user = RequestContextHolder.getRequestContext().getUser();
 	        entry.setModification(new HistoryStamp(user, lastModDate));
 	   }
    }
    //***********************************************************************************************************   
    public void deleteEntry(Binder parentBinder, Entry entry, boolean deleteMirroredSource) {
    	SimpleProfiler sp = new SimpleProfiler(false);
    	Map ctx = deleteEntry_setCtx(entry, null);
    	sp.start("deleteEntry_preDelete");
        deleteEntry_preDelete(parentBinder, entry, ctx);
        sp.stop("deleteEntry_preDelete");
        
        sp.start("deleteEntry_workflow");
        deleteEntry_workflow(parentBinder, entry, ctx);
        sp.stop("deleteEntry_workflow");
        
        sp.start("deleteEntry_processFiles");
        deleteEntry_processFiles(parentBinder, entry, deleteMirroredSource, ctx);
        sp.stop("deleteEntry_processFiles");
         
        sp.start("deleteEntry_delete");
        deleteEntry_delete(parentBinder, entry, ctx);
        sp.stop("deleteEntry_delete");
        
        sp.start("deleteEntry_postDelete");
        deleteEntry_postDelete(parentBinder, entry, ctx);
        sp.stop("deleteEntry_postDelete");
        
        sp.start("deleteEntry_indexDel");
        deleteEntry_indexDel(parentBinder, entry, ctx);
        sp.stop("deleteEntry_indexDel");
        
        sp.print();
    }
    protected Map deleteEntry_setCtx(Entry entry, Map ctx) {
    	return ctx;
    }
    protected void deleteEntry_preDelete(Binder parentBinder, Entry entry, Map ctx) {
   		if (entry.isTop() && parentBinder.isUniqueTitles()) 
   			getCoreDao().updateTitle(parentBinder, entry, entry.getNormalTitle(), null);		
    	//create history - using timestamp and version from fillIn
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setModification(new HistoryStamp(user));
        entry.incrLogVersion();
        processChangeLog(entry, ChangeLog.DELETEENTRY);
    	getReportModule().addAuditTrail(AuditType.delete, entry);
    }
        
    protected void deleteEntry_workflow(Binder parentBinder, Entry entry, Map ctx) {
    	if (entry instanceof WorkflowSupport)
    		getWorkflowModule().deleteEntryWorkflow((WorkflowSupport)entry);
    }
    
    protected void deleteEntry_processFiles(Binder parentBinder, Entry entry, boolean deleteMirroredSource, Map ctx) {
    	//attachment meta-data not deleted.  Done in optimized delete entry
    	getFileModule().deleteFiles(parentBinder, entry, deleteMirroredSource, null);
    }
    
    protected void deleteEntry_delete(Binder parentBinder, Entry entry, Map ctx) {
    	//use the optimized deleteEntry or hibernate deletes each collection entry one at a time
    	getCoreDao().delete(entry);   
    }
    protected void deleteEntry_postDelete(Binder parentBinder, Entry entry, Map ctx) {
   }

    protected void deleteEntry_indexDel(Binder parentBinder, Entry entry, Map ctx) {
        // Delete the document that's currently in the index.
    	// Since all matches will be deleted, this will also delete the attachments
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
   }
    //***********************************************************************************************************
    public void moveEntry(Binder binder, Entry entry, Binder destination) {
		throw new NotSupportedException(
				NLT.get("errorcode.notsupported.moveEntry", new String[]{entry.getTitle()}));
    }
    
    //***********************************************************************************************************
    /*
     * classes must provide code to delete files belonging to entries
     */
    protected abstract void deleteBinder_processFiles(Binder binder, Map ctx);
    /*
     * classes must provide code to delete entries in the binder
     */   
    protected abstract void deleteBinder_delete(Binder binder, boolean deleteMirroredSource, Map ctx);
    protected void deleteBinder_indexDel(Binder binder, Map ctx) {
        // Delete the document that's currently in the index.
    	// Since all matches will be deleted, this will also delete the attachments
    	indexDeleteEntries(binder);
    	super.deleteBinder_indexDel(binder, ctx);
    }
	    
    //***********************************************************************************************************
     protected void indexDeleteEntries(Binder binder) {
		// Since all matches will be deleted, this will also delete the attachments 
		IndexSynchronizationManager.deleteDocuments(new Term(EntityIndexUtils.BINDER_ID_FIELD, binder.getId().toString()));
   	
    }
    //***********************************************************************************************************
    public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState) {

 		if (!(entry instanceof WorkflowSupport)) return;
 		WorkflowSupport wEntry = (WorkflowSupport)entry;
		//Find the workflowState
		WorkflowState ws = wEntry.getWorkflowState(tokenId);
 		if (ws != null) {
 	   		entry.incrLogVersion();
 	        getWorkflowModule().modifyWorkflowState(wEntry, ws, toState);
			processChangeLog(entry, ChangeLog.MODIFYWORKFLOWSTATE);
			indexEntry(entry);
		}
    }
 
    public void setWorkflowResponse(Binder binder, Entry entry, Long stateId, InputDataAccessor inputData)  {
		if (!(entry instanceof WorkflowSupport)) return;
 		WorkflowSupport wEntry = (WorkflowSupport)entry;
        User user = RequestContextHolder.getRequestContext().getUser();

 		WorkflowState ws = wEntry.getWorkflowState(stateId);
		Definition def = ws.getDefinition();
		boolean changes = false;
		//TODO: what access check belongs here??
		Map questions = WorkflowUtils.getQuestions(def, ws.getState());
		for (Iterator iter=questions.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			String question = (String)me.getKey();
			if (!inputData.exists(question)) continue;
			String response = inputData.getSingleValue(question);
			Map qData = (Map)me.getValue();
			Map rData = (Map)qData.get(WebKeys.WORKFLOW_QUESTION_RESPONSES);
			if (!rData.containsKey(response)) {
				throw new IllegalArgumentException("Illegal workflow response: " + response);
			}
			//now see if response to this question from this user exists
			Set responses = wEntry.getWorkflowResponses();
			boolean found=false;
			WorkflowResponse wr=null;
			for (Iterator iter2=responses.iterator(); iter2.hasNext();) {
				wr = (WorkflowResponse)iter2.next();
				if (def.getId().equals(wr.getDefinitionId()) && 
						question.equals(wr.getName()) &&
						user.getId().equals(wr.getResponderId())) {
					found = true;
					break;
				}			
			}
			if (found) {
				if (!response.equals(wr.getResponse())) {
					//if no changes have been made update timestamp
					if (!changes) {
						entry.setModification(new HistoryStamp(user));
						entry.incrLogVersion();	
						changes = true;
					}
					wr.setResponse(response);
					wr.setResponseDate(entry.getModification().getDate());
				}
			} else {
				//if no changes have been made update timestamp
				if (!changes) {
					entry.setModification(new HistoryStamp(user));
					entry.incrLogVersion();				
					changes = true;
				}
				wr = new WorkflowResponse();
				wr.setResponderId(user.getId());
				wr.setResponseDate(entry.getModification().getDate());
				wr.setDefinitionId(def.getId());
				wr.setName(question);
				wr.setResponse(response);
				wr.setOwner(entry);
				getCoreDao().save(wr);
				wEntry.addWorkflowResponse(wr);
			}
		}
		if (changes) {
			getWorkflowModule().modifyWorkflowStateOnResponse(wEntry);
			processChangeLog(entry, ChangeLog.ADDWORKFLOWRESPONSE);
	    	getReportModule().addAuditTrail(AuditType.modify, entry);
			indexEntry(entry);
		}
    	
    }

    //***********************************************************************************************************
    /**
     * Index binder and its entries
     */
    protected void indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, List tags) {
    	super.indexBinder(binder, includeEntries, deleteIndex, tags);
    	if (includeEntries == false) return;
    	indexEntries(binder, deleteIndex);
    }
    protected void indexEntries(Binder binder, boolean deleteIndex) {
    	//may already have been handled with an optimized query
    	if (deleteIndex) indexEntries_preIndex(binder);
  		//flush any changes so any exiting changes don't get lost on the evict
    	getCoreDao().flush();
  		SFQuery query = indexEntries_getQuery(binder);
  		int threshhold = SPropsUtil.getInt("lucene.flush.threshhold", 100);
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
       			indexEntries_load(binder, batch);
       			logger.info("Indexing at " + total + "(" + binder.getPathName() + ")");
       			Map tags = indexEntries_loadTags(binder, batch);
       			for (int i=0; i<batch.size(); ++i) {
       				Entry entry = (Entry)batch.get(i);
       				if (indexEntries_validate(binder, entry)) {
       					List entryTags = (List)tags.get(entry.getEntityIdentifier());
       					// 	Create an index document from the entry object. 
       					// Entry already deleted from index, so pretend we are new
       				   	indexEntryWithAttachments(binder, entry, entry.getFileAttachments(), null, true, entryTags);
       				   	indexEntries_postIndex(binder, entry);
       				}
       				getCoreDao().evict(entry);
          	  		//apply after we have gathered a few
           	   		IndexSynchronizationManager.applyChanges(threshhold);
           			}
       	 	            	            
       			// Register the index document for indexing.
       			logger.info("Indexing done at " + total + "("+ binder.getPathName() + ")");
       		
        	}
        	
        } finally {
        	//clear out anything remaining
   	   		IndexSynchronizationManager.applyChanges();
        	query.close();
        }
 
    }
    protected void indexEntries_preIndex(Binder binder) {
    	indexDeleteEntries(binder); 
    }
 
   	protected abstract SFQuery indexEntries_getQuery(Binder binder);
   	protected boolean indexEntries_validate(Binder binder, Entry entry) {
   		return true;
   	}
   	protected void indexEntries_postIndex(Binder binder, Entry entry) {
   	}
   	protected void indexEntries_load(Binder binder, List entries)  {
   		// bulkd load any collections that neeed to be indexed
   		getCoreDao().bulkLoadCollections(entries);
   	}
	protected Map indexEntries_loadTags(Binder binder, List<Entry> entries) {
		List<EntityIdentifier> ids = new ArrayList();
		for (Entry e: entries) {
			ids.add(e.getEntityIdentifier());
		}
		return getCoreDao().loadAllTagsByEntity(ids);
	}
 
    //***********************************************************************************************************
   	public void indexEntries(Collection entries) {
   		for (Iterator iter=entries.iterator(); iter.hasNext();) {
   			Entry entry = (Entry)iter.next();
   			indexEntry(entry);
   		}
   	}
   	public void moveFiles(Binder binder, Collection entries, Binder destination) {
   		for (Iterator iter=entries.iterator(); iter.hasNext();) {
   			Entry entry = (Entry)iter.next();
   			moveFiles(binder, entry, destination);
   		}
   	}
   	
    protected void moveFiles(Binder binder, Entry entry, Binder destination) {
    	List atts = entry.getFileAttachments();
    	for(int i = 0; i < atts.size(); i++) {
    		FileAttachment fa = (FileAttachment) atts.get(i);
    		getFileModule().moveFile(binder, entry, fa, destination);
    	}
    }
   	
    //***********************************************************************************************************
    public Map getBinderEntries(Binder binder, String[] entryTypes, Map options) {
        //search engine will only return entries you have access to
         //validate entry count
    	//do actual search index query
        Hits hits = getBinderEntries_doSearch(binder, entryTypes, options);
        //iterate through results
        List childEntries = SearchUtils.getSearchEntries(hits);
        SearchUtils.extendPrincipalsInfo(childEntries, getProfileDao(), getEntryPrincipalField());
       	Map model = new HashMap();
        model.put(ObjectKeys.BINDER, binder);      
        model.put(ObjectKeys.SEARCH_ENTRIES, childEntries);
        model.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits.getTotalHits()));
        //Total number of results found
        model.put(ObjectKeys.TOTAL_SEARCH_COUNT, new Integer(hits.getTotalHits()));
        //Total number of results returned
        model.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(hits.length()));
        return model;
   }
    
    protected int getBinderEntries_maxEntries(int maxChildEntries) {
        if (maxChildEntries == 0 || maxChildEntries == Integer.MAX_VALUE) maxChildEntries = DEFAULT_MAX_CHILD_ENTRIES;
        return maxChildEntries;
    }
     
    protected Hits getBinderEntries_doSearch(Binder binder, String [] entryTypes, 
    		Map options) {
    	int maxResults = 0;
       	int searchOffset = 0;
        if (options != null) {
        	if (options.containsKey(ObjectKeys.SEARCH_MAX_HITS)) 
        		maxResults = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
        
    	if (options.containsKey(ObjectKeys.SEARCH_OFFSET)) 
    			searchOffset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);       
        }
    	maxResults = getBinderEntries_maxEntries(maxResults); 
       	Hits hits = null;
       	org.dom4j.Document queryTree = null;
       	if ((options != null) && options.containsKey(ObjectKeys.FOLDER_ENTRY_TO_BE_SHOWN)) {
	   		SearchFilter searchFilter = new SearchFilter(true);
	   		searchFilter.addEntryId((String) options.get(ObjectKeys.FOLDER_ENTRY_TO_BE_SHOWN));
	   		getBinderEntries_getSearchDocument(binder, entryTypes, searchFilter);

	   		queryTree = SearchUtils.getInitalSearchDocument(searchFilter.getFilter(), null);
       	} else {

       		org.dom4j.Document userSearchFilter = null;
        	if ((options != null) && options.containsKey(ObjectKeys.SEARCH_SEARCH_FILTER)) 
        		userSearchFilter = (org.dom4j.Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER);
       		
        	SearchFilter searchFilter = null;
        	if (userSearchFilter != null) {
        		searchFilter = new SearchFilter(userSearchFilter);
        	} else {
        		searchFilter = new SearchFilter(true);
        	}
        	getBinderEntries_getSearchDocument(binder, entryTypes, searchFilter);
	   		queryTree = SearchUtils.getInitalSearchDocument(searchFilter.getFilter(), null);
        	SearchUtils.getQueryFields(queryTree, options); 
       	}       	
       	//System.out.println(queryTree.asXML());
       	
       	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
    	SearchObject so = qb.buildQuery(queryTree);
    	
    	//Set the sort order
    	SortField[] fields = SearchUtils.getSortFields(options); 
    	so.setSortBy(fields);
    	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("Query is: " + queryTree.asXML());
    		logger.debug("Query is: " + soQuery.toString());
    	}
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        hits = luceneSession.search(soQuery, so.getSortBy(), searchOffset, maxResults);
        }
        finally {
            luceneSession.close();
        }
    	
        return hits;
     
    }
    protected void getBinderEntries_getSearchDocument(Binder binder, 
    		String [] entryTypes, SearchFilter searchFilter) {
    	
    	searchFilter.newFiltersBlock(true);
    	
   		searchFilter.addFolderId(binder.getId().toString());
   		searchFilter.addDocumentType(BasicIndexUtils.DOC_TYPE_ENTRY);
   		searchFilter.addEntryTypes(entryTypes);
    }

    //***********************************************************************************************************
    public Entry getEntry(Binder parentBinder, Long entryId) {
    	//get the entry
    	Entry entry = entry_load(parentBinder, entryId);
        //Initialize users
    	getProfileDao().loadPrincipals(getPrincipalIds(entry), RequestContextHolder.getRequestContext().getZoneId(), false);
        return entry;
    }
          
    
    //***********************************************************************************************************
    /*
     * Load all principals assocated with an entry.  
     * This is a performance optimization for display.
     */
    protected abstract Entry entry_load(Binder parentBinder, Long entryId);
            

    public void indexEntry(Entry entry) {
    	indexEntry(entry.getParentBinder(), entry, null, null, false, null);
    }
    /**
     * Index entry and optionally its attached files.
     * 
     * @param biner
     * @param entry
     * @param fileUploadItems uploaded files or <code>null</code>. 
     * At minimum, those files in the list must be indexed.  
     * @param filesToIndex a list of FileAttachments or <code>null</code>. 
     * At minimum, those files in the list must be indexed. 
     * @param newEntry
     */
    protected void indexEntry(Binder binder, Entry entry, List fileUploadItems, 
    		Collection<FileAttachment> filesToIndex, boolean newEntry, List tags) {
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
    	
    	indexEntryWithAttachments(binder, entry, entry.getFileAttachments(), fileUploadItems, newEntry, tags);
    }
    
    /**
     * Index entry along with its file attachments. 
     * 
     * @param binder
     * @param entry
     * @param fileAttachments list of FileAttachments that need to be (re)indexed.
     * Only those files explicitly listed in this list are indexed. 
     * @param fileUploadItems a list of uploaded files or a <code>null</code>.
     * If each FileAttachment in fileAttachments list finds a corresponding
     * uploaded file in this list, the content of the uploaded file can be
     * used for indexing. Otherwise, the file content must be obtained from
     * the repository. Note that the elements in this list do not positionally
     * correspond to the elements in fileAttachments list. 
     * @param newEntry
     */
	protected void indexEntryWithAttachments(Binder binder, Entry entry,
			List fileAttachments, List fileUploadItems, boolean newEntry, List tags) {
		if(!newEntry) {
			// This is modification. We must first delete existing document(s) from the index.
			
			// Since all matches will be deleted, this will also delete the attachments 
	        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
		}
		
        // Create an index document from the entry object.
       // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(buildIndexDocumentFromEntry(entry.getParentBinder(), entry, tags));
        //Create separate documents one for each attached file and index them.
        for(int i = 0; i < fileAttachments.size(); i++) {
        	FileAttachment fa = (FileAttachment) fileAttachments.get(i);
        	FileUploadItem fui = null;
        	if(fileUploadItems != null)
        		fui = findFileUploadItem(fileUploadItems, fa.getRepositoryName(), fa.getFileItem().getName());
        	try {
        		IndexSynchronizationManager.addDocument(buildIndexDocumentFromEntryFile(binder, entry, fa, fui, tags));
           		// Register the index document for indexing.
	        } catch (Exception ex) {
		       		//log error but continue
		       		logger.error("Error indexing file for entry " + entry.getId() + " attachment " + fa, ex);
        	}
         }
 	}

    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, List tags) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        
    	fillInIndexDocWithCommonPartFromEntry(indexDoc, binder, entry);
    	
    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.team.search.BasicIndexUtils.DOC_TYPE_ENTRY);
                
        // Add the events - special indexing for calendar view
        EntityIndexUtils.addEvents(indexDoc, entry);
        
        // Add the tags for this entry
        if (tags == null) tags =  getCoreDao().loadAllTagsByEntity(entry.getEntityIdentifier());
        EntityIndexUtils.addTags(indexDoc, entry, tags);
        
        // Add the workflows
        EntityIndexUtils.addWorkflow(indexDoc, entry);
        
        // Add attached file ids
        EntityIndexUtils.addAttachedFileIds(indexDoc, entry);
        
        return indexDoc;
    }
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntryFile
	(Binder binder, Entry entry, FileAttachment fa, FileUploadItem fui, List tags) {
   		org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromFile(binder, entry, fa, fui, tags);
        // Add the workflows - different for files
        EntityIndexUtils.addWorkflow(indexDoc, entry);
   		fillInIndexDocWithCommonPartFromEntry(indexDoc, binder, entry);
   		// Add attached file id
        EntityIndexUtils.addFileAttachmentUid(indexDoc, fa);
        EntityIndexUtils.addFileAttachmentModificationDate(indexDoc, fa);
        EntityIndexUtils.addFileAttachmentCreationDate(indexDoc, fa);
        
        
   		indexDoc = EntityIndexUtils.addFileAttachmentAllText(indexDoc);
   		return indexDoc;
 
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
    protected void fillInIndexDocWithCommonPartFromEntry(org.apache.lucene.document.Document indexDoc, 
    		Binder binder, Entry entry) {
      	EntityIndexUtils.addEntryType(indexDoc, entry);       
        // Add ACL field. We only need to index ACLs for read access.
   		EntityIndexUtils.addReadAccess(indexDoc, binder, entry);
      		
        EntityIndexUtils.addParentBinder(indexDoc, entry);

        fillInIndexDocWithCommonPart(indexDoc, binder, entry);
    }
	public ChangeLog processChangeLog(DefinableEntity entry, String operation) {
		if (entry instanceof Binder) return processChangeLog((Binder)entry, operation);
		ChangeLog changes = new ChangeLog(entry, operation);
		ChangeLogUtils.buildLog(changes, entry);
		getCoreDao().save(changes);
		return changes;
	}

}
