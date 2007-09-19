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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.docconverter.ITextConverterManager;
import com.sitescape.team.docconverter.TextConverter;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.TitleException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.exception.UncheckedCodedException;
import com.sitescape.team.fi.connection.ResourceDriver;
import com.sitescape.team.fi.connection.ResourceSession;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.index.FieldBuilderUtil;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.file.FilesErrors;
import com.sitescape.team.module.file.FilterException;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.module.rss.RssModule;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.SearchUtils;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.pipeline.Pipeline;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.util.StatusTicket;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;

/**
 *
 * 
 */
public abstract class AbstractBinderProcessor extends CommonDependencyInjection 
	implements BinderProcessor {
	public static  String[] docTypes = new String[] {BasicIndexUtils.DOC_TYPE_BINDER, BasicIndexUtils.DOC_TYPE_ENTRY,BasicIndexUtils.DOC_TYPE_ATTACHMENT};
    protected DefinitionModule definitionModule;
    protected static Map fieldsOnlyIndexArgs = new HashMap();
    static {
    	fieldsOnlyIndexArgs.put(DefinitionModule.INDEX_FIELDS_ONLY, Boolean.TRUE);
	}
 
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
	
	private ReportModule reportModule;
	
	protected ReportModule getReportModule() {
		return reportModule;
	}
	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}

	private RssModule rssModule;
	protected RssModule getRssModule() {
		return rssModule;
	}
	public void setRssModule(RssModule rssModule) {
		this.rssModule = rssModule;
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
    //no transaction    
    public Binder addBinder(final Binder parent, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
      	if (parent.isZone())
      		throw new NotSupportedException("errorcode.notsupported.addbinder");
               
    	SimpleProfiler sp = new SimpleProfiler(false);
    	
    	sp.start("addBinder_toEntryData");
        final Map ctx = addBinder_setCtx(parent, null);
        Map entryDataAll = addBinder_toEntryData(parent, def, inputData, fileItems,ctx);
        sp.stop("addBinder_toEntryData");
        
        final Map entryData = (Map) entryDataAll.get("entryData");
        
         List fileUploadItems = (List) entryDataAll.get("fileData");
       
    	try {
	        sp.start("addBinder_create");
	        final Binder binder = addBinder_create(def, clazz, ctx);
	        sp.stop("addBinder_create");
	        
	    	if (def != null) {
	    		if ((parent.getDefinitionType() == null) ||
	    				(binder.getDefinitionType().intValue() != parent.getDefinitionType().intValue())) {
	    			binder.setDefinitionsInherited(false);
	    		}
	    	}
	        String title = (String)entryData.get("title");
	        if (Validator.isNull(title)) {
	        	title = (String)inputData.getSingleValue("title");
	        	entryData.put("title", title);
	        }
	        if (Validator.isNull(title)) throw new TitleException("");
    		if (Validator.containsPathCharacters(title)) throw new UncheckedCodedException("errorcode.title.pathCharacters", new Object[]{title}){};
	        
	        binder.setPathName(parent.getPathName() + "/" + title);
	        
	        sp.start("addBinder_transactionExecute");
	        // The following part requires update database transaction.
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	                //need to set entry/binder information before generating file attachments
	                //Attachments/Events need binder info for AnyOwner
	                addBinder_fillIn(parent, binder, inputData, entryData, ctx);
	                
	                addBinder_mirrored(parent, binder, inputData, entryData, ctx);
	                
	                addBinder_preSave(parent, binder, inputData, entryData, ctx);      
	
	                addBinder_save(parent, binder, inputData, entryData, ctx);      
	                
	                addBinder_postSave(parent, binder, inputData, entryData, ctx);
	                //register title for uniqueness for webdav; always ensure binder titles are unique in parent
	                getCoreDao().updateFileName(binder.getParentBinder(), binder, null, binder.getTitle());
	                if (binder.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(binder.getParentBinder(), binder, null, binder.getNormalTitle());
	                return null;
	        	}
	        });
	        sp.stop("addBinder_transactionExecute");
	           
	        sp.start("addBinder_filterFiles");
	        //Need to do filter here after binder is saved cause it makes use of
	        // the id of binder
	        FilesErrors filesErrors = addBinder_filterFiles(binder, fileUploadItems,ctx);
	        sp.stop("addBinder_filterFiles");
	        
	        sp.start("addBinder_processFiles");
	        // We must save the entry before processing files because it makes use
	        // of the persistent id of the entry. 
	        filesErrors = addBinder_processFiles(binder, fileUploadItems, filesErrors,ctx);
	        sp.stop("addBinder_processFiles");
	        
	        sp.start("addBinder_indexAdd");
	        // This must be done in a separate step after persisting the entry,
	        // because we need the entry's persistent ID for indexing. 
	        addBinder_indexAdd(parent, binder, inputData, fileUploadItems, ctx);
	        sp.stop("addBinder_indexAdd");
	        
	    	sp.print();

	    	if(filesErrors.getProblems().size() > 0) {
	    		// At least one error occured during the operation. 
	    		throw new WriteFilesException(filesErrors, binder.getId());
	    	}
	    	else {
	    		return binder;
	    	}
    	}
    	finally {
	        cleanupFiles(fileUploadItems);
    	}
    }
    //inside write transaction    
    protected Map addBinder_setCtx(Binder binder, Map ctx) {
    	return ctx;
    }

    //inside write transaction    
   protected FilesErrors addBinder_filterFiles(Binder binder, List fileUploadItems, Map ctx) throws FilterException {
    	return getFileModule().filterFiles(binder, fileUploadItems);
    }

   //inside write transaction    
    protected FilesErrors addBinder_processFiles(Binder binder, List fileUploadItems, FilesErrors filesErrors, Map ctx) {
    	return getFileModule().writeFiles(binder, binder, fileUploadItems, filesErrors);
    }
    
    //inside write transaction    
    protected Map addBinder_toEntryData(Binder parent, Definition def, InputDataAccessor inputData, Map fileItems, Map ctx) {
        //Call the definition processor to get the entry data to be stored
    	if (def != null) {
    		return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
    	} else {
    		return new HashMap();
    	}
    }
    
    //inside write transaction    
    protected Binder addBinder_create(Definition def, Class clazz, Map ctx)  {
    	try {
    		Binder binder = (Binder)clazz.newInstance();
            binder.setEntryDef(def);
            if (def != null) {
            	binder.setDefinitionType(def.getType());
            	List defs = new ArrayList();
            	defs.add(def);
            	binder.setDefinitions(defs);
   	        	String icon = DefinitionUtils.getPropertyValue(def.getDefinition().getRootElement(), "icon");
   	        	if (Validator.isNotNull(icon)) binder.setIconName(icon);
            	
            } 
            return binder;
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    //inside write transaction    
    protected void addBinder_fillIn(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        binder.setZoneId(parent.getZoneId());
        binder.setCreation(new HistoryStamp(user));
        binder.setModification(binder.getCreation());
        binder.setLogVersion(Long.valueOf(1));
        binder.setOwner(user);

    	//force a lock so contention on the sortKey is reduced
        if (Boolean.TRUE.equals(inputData.getSingleObject(ObjectKeys.INPUT_OPTION_FORCE_LOCK))) {
            getCoreDao().lock(parent);
        } 
        parent.addBinder(binder);
        
 		// not implemented
 		//getAclManager().doInherit(parent, (AclControlled) binder);

        for (Iterator iter=entryData.values().iterator(); iter.hasNext();) {
        	Object obj = iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (obj instanceof Event)
        		getCoreDao().save(obj);
        }
        doBinderFillin(parent, binder, inputData, entryData);
        //can add these fields on creation, but cannot use modifyBinder to change them
   		if (inputData.exists(ObjectKeys.FIELD_BINDER_LIBRARY) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_LIBRARY)) {
   			entryData.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_LIBRARY)));
   		}
   		if (inputData.exists(ObjectKeys.FIELD_BINDER_UNIQUETITLES) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_UNIQUETITLES)) {
   			entryData.put(ObjectKeys.FIELD_BINDER_UNIQUETITLES, Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_UNIQUETITLES)));
   		}
  		if (inputData.exists(ObjectKeys.FIELD_BINDER_INHERITTEAMMEMBERS) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_INHERITTEAMMEMBERS)) {
   			entryData.put(ObjectKeys.FIELD_BINDER_INHERITTEAMMEMBERS, Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_INHERITTEAMMEMBERS)));
   		}
  		if (inputData.exists(ObjectKeys.FIELD_BINDER_NAME) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_NAME)) {
   			entryData.put(ObjectKeys.FIELD_BINDER_NAME, inputData.getSingleValue(ObjectKeys.FIELD_BINDER_NAME));
   		}
   		
  		EntryBuilder.buildEntry(binder, entryData);
 		
 		// If binder is mirrored, ignore user's input and make it a library as well.
 		// We don't do the same in modifyBinder since changing non-library to library
 		// requires some fixup in the higher up. 
 		if(binder.isMirrored())
 			binder.setLibrary(true);
 		
 		checkConstraintMirrored(parent, binder, binder.isLibrary(), inputData);
    }

    protected void checkConstraintMirrored(Binder parent, Binder binder, boolean library, InputDataAccessor inputData) {
 		// A little more validation is necessary with respect to mirrored binder.
 		if(binder.isMirrored()) {
 			if(!library)
 				throw new IllegalArgumentException("Mirrored folder must also be a library folder");
 			if(binder.getResourceDriverName() == null) {
 				if(parent.isMirrored()) {
 					binder.setResourceDriverName(parent.getResourceDriverName());
 				}
 				else {
 					throw new IllegalArgumentException("Resource driver name must be specified for new binder");
 				}
 			}
 			else {
 				if(parent.isMirrored()) {
 					if(!binder.getResourceDriverName().equals(parent.getResourceDriverName()))
 						throw new IllegalArgumentException("Specified resource driver name [" + binder.getResourceDriverName()
 								+ "] does not match the resource driver name [" + parent.getResourceDriverName()
 								+ "] of the parent binder [" + parent.getPathName() + "]");
 				}
 			}
 			if(binder.getResourcePath() == null) {
 				if(parent.isMirrored()) {
 					binder.setResourcePath(getResourceDriverManager().normalizedResourcePath(parent.getResourceDriverName(), parent.getResourcePath(), binder.getTitle()));
 				}
 				else {
 					throw new IllegalArgumentException("Resource path must be specified for new binder");
 				}
 			}
 		}
 		else {
 			binder.setResourceDriverName(null);
 			binder.setResourcePath(null);
 		}
    }

    //inside write transaction    
    protected void addBinder_mirrored(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
		if(binder.isMirrored()) { // The newly created binder is a mirrored one.
			// First, make sure that the resource path we store is normalized.
	    	normalizeResourcePath(binder, inputData);
						
			// Second, perform outward synchronization, if requested.
			Boolean synchToSource = Boolean.TRUE;
			if(inputData.exists(ObjectKeys.PI_SYNCH_TO_SOURCE))
				synchToSource = Boolean.parseBoolean(inputData.getSingleValue(ObjectKeys.PI_SYNCH_TO_SOURCE));
			if(Boolean.TRUE.equals(synchToSource)) {
				ResourceDriver driver = getResourceDriverManager().getDriver(binder.getResourceDriverName());
				
				if(driver.isReadonly()) {
					throw new NotSupportedException("errorcode.notsupported.addMirroredBinder.readonly", 
							new String[] {binder.getPathName(), driver.getTitle()});
				}
				else {
					ResourceSession session = driver.openSession().setPath(binder.getResourcePath());
					try {
						session.createDirectory();
					}
					finally {
						session.close();
					}	
				}
			}
		}
    }

    //inside write transaction    
   protected void addBinder_preSave(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    }

   //inside write transaction    
   protected void addBinder_save(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
        getCoreDao().save(binder);
    }
    
   //inside write transaction    
    protected void addBinder_postSave(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
  		if (inputData.exists(ObjectKeys.INPUT_FIELD_FUNCTIONMEMBERSHIPS)) {
  			List<WorkAreaFunctionMembership> wfms = (List)inputData.getSingleObject(ObjectKeys.INPUT_FIELD_FUNCTIONMEMBERSHIPS);
  			if (wfms != null && !wfms.isEmpty()) { 
  				binder.setFunctionMembershipInherited(false);
  		    	for (WorkAreaFunctionMembership fm: wfms) {
  		    		WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
  			       	membership.setZoneId(binder.getZoneId());
  			       	membership.setWorkAreaId(binder.getWorkAreaId());
  			       	membership.setWorkAreaType(binder.getWorkAreaType());
  			       	membership.setFunctionId(fm.getFunctionId());
  			       	membership.setMemberIds(new HashSet(fm.getMemberIds()));
  			        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);	
  		    	}
  			}
  		}
 		if (inputData.exists(ObjectKeys.INPUT_FIELD_DEFINITIONS)) {
 			List<Definition>defs = (List)inputData.getSingleObject(ObjectKeys.INPUT_FIELD_DEFINITIONS);
  			if (defs != null && !defs.isEmpty()) { 
 		    	binder.setDefinitionsInherited(false);
 		    	binder.setDefinitions(defs);
 		    	binder.setWorkflowAssociations((Map)inputData.getSingleObject(ObjectKeys.INPUT_FIELD_WORKFLOWASSOCIATIONS));
 	    	}
 		}
 		//create history - using timestamp and version from fillIn
    	processChangeLog(binder, ChangeLog.ADDBINDER);
    	getReportModule().addAuditTrail(AuditType.add, binder);
    	
    }

    //inside write transaction    
   protected void addBinder_indexAdd(Binder parent, Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems, Map ctx) {
        //no tags typically exists on a new binder - reduce db lookups by supplying list
    	List tags = null;
    	if (ctx != null) tags = (List)ctx.get(ObjectKeys.INPUT_FIELD_TAGS);
    	if (tags == null) tags = new ArrayList();
        
    	indexBinder(binder, fileUploadItems, null, true, tags);
    }
 	//common fillin for add/modify
 	protected void doBinderFillin(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData) {  
   		if (inputData.exists(ObjectKeys.FIELD_ENTITY_DESCRIPTION) && !entryData.containsKey(ObjectKeys.FIELD_ENTITY_DESCRIPTION)) {
   			String val = inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_DESCRIPTION);
   			entryData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, new Description(val) );
   		}
    
   		if (inputData.exists(ObjectKeys.FIELD_ENTITY_ICONNAME) && !entryData.containsKey(ObjectKeys.FIELD_ENTITY_ICONNAME)) {
   			entryData.put(ObjectKeys.FIELD_ENTITY_ICONNAME, inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_ICONNAME));
   		}
  		
   		if (binder.isMirroredAllowed()) { //not supported on templates - don't know where target will be
   			Boolean mirrored = null;
   			if(parent != null && parent.isMirrored()) //parent null on templates
   				mirrored = Boolean.TRUE;
   			else {
   				if (inputData.exists(ObjectKeys.FIELD_BINDER_MIRRORED) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_MIRRORED))
   					mirrored = Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_MIRRORED));
   			}
   			if(mirrored != null) {
   				entryData.put(ObjectKeys.FIELD_BINDER_MIRRORED, mirrored);
   			}
   			if (inputData.exists(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME)) {
   				entryData.put(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, inputData.getSingleValue(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME));
   			}
   			if (inputData.exists(ObjectKeys.FIELD_BINDER_RESOURCE_PATH) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_RESOURCE_PATH)) {
   				entryData.put(ObjectKeys.FIELD_BINDER_RESOURCE_PATH, inputData.getSingleValue(ObjectKeys.FIELD_BINDER_RESOURCE_PATH));
   			}
   		}
 	}
    //***********************************************************************************************************
    //no transaction    
    public void modifyBinder(final Binder binder, final InputDataAccessor inputData, 
    		Map fileItems, final Collection deleteAttachments) 
    		throws AccessControlException, WriteFilesException {
	
    	SimpleProfiler sp = new SimpleProfiler(false);
    	
    	sp.start("modifyBinder_toEntryData");
    	final Map ctx = modifyBinder_setCtx(binder, null);
	    Map entryDataAll = modifyBinder_toEntryData(binder, inputData, fileItems, ctx);
	    sp.stop("modifyBinder_toEntryData");
	    
	    final Map entryData = (Map) entryDataAll.get("entryData");
	    List fileUploadItems = (List) entryDataAll.get("fileData");

	    try {
		    
	    	sp.start("modifyBinder_filterFiles");
		    FilesErrors filesErrors = modifyBinder_filterFiles(binder, fileUploadItems, ctx);
		    sp.stop("modifyBinder_filterFiles");
	
	    	sp.start("modifyBinder_transactionExecute");
	    	// The following part requires update database transaction.
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		String oldTitle = binder.getTitle();
	        		String oldNormalTitle = binder.getNormalTitle();
	        		modifyBinder_fillIn(binder, inputData, entryData, ctx);
	        		modifyBinder_postFillIn(binder, inputData, entryData, ctx);
	        		//if title changed, must update path info for all child folders
	        		String newTitle = binder.getTitle();
	        		if (Validator.isNull(newTitle)) throw new TitleException("");
	        		if (Validator.containsPathCharacters(newTitle)) throw new UncheckedCodedException("errorcode.title.pathCharacters", new Object[]{newTitle}){};
	        		modifyBinder_mirrored(binder, oldTitle, newTitle, inputData);
	        		//case matters here
	        		if ((oldTitle == null) || !oldTitle.equals(newTitle)) {
	        			fixupPath(binder);
	        		}
	        		if (!binder.isRoot()) {
	        			getCoreDao().updateFileName(binder.getParentBinder(), binder, oldTitle, newTitle);
	        			if (binder.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(binder.getParentBinder(), binder, oldNormalTitle, binder.getNormalTitle());

        			}
        			return null;
	        	}});
	        sp.stop("modifyBinder_transactionExecute");
	        
	        //handle outside main transaction so main changeLog doesn't reflect attactment changes
	        sp.start("modifyBinder_removeAttachments");
	    	List<FileAttachment> filesToDeindex = new ArrayList<FileAttachment>();
	    	List<FileAttachment> filesToReindex = new ArrayList<FileAttachment>();	    
	        modifyBinder_removeAttachments(binder, deleteAttachments, filesToDeindex, filesToReindex, ctx);    
	        sp.stop("modifyBinder_removeAttachments");
	        
	        sp.start("modifyBinder_processFiles");
		    filesErrors = modifyBinder_processFiles(binder, fileUploadItems, filesErrors, ctx);
		    sp.stop("modifyBinder_processFiles");
		    
	    	// Since index update is implemented as removal followed by add, 
	    	// the update requests must be added to the removal and then add
	    	// requests respectively. 
	    	filesToDeindex.addAll(filesToReindex);
	    	sp.start("modifyBinder_indexRemoveFiles");
	        modifyBinder_indexRemoveFiles(binder, filesToDeindex, ctx);
	        sp.stop("modifyBinder_indexRemoveFiles");
	        
	        sp.start("modifyBinder_indexAdd");
		    modifyBinder_indexAdd(binder, inputData, fileUploadItems, filesToReindex, ctx);
		    sp.stop("modifyBinder_indexAdd");
		    
		    sp.print();
		    
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
    //no transaction    
    protected Map modifyBinder_setCtx(Binder binder, Map ctx) {
    	if (ctx == null) ctx = new HashMap();
    	//save title before changes
		ctx.put(ObjectKeys.FIELD_ENTITY_TITLE, binder.getTitle());
    	return ctx;
    }
    //no transaction    
    protected FilesErrors modifyBinder_filterFiles(Binder binder, List fileUploadItems, Map ctx) throws FilterException {
    	return getFileModule().filterFiles(binder, fileUploadItems);
    }

    //no transaction    
    protected FilesErrors modifyBinder_processFiles(Binder binder, 
    		List fileUploadItems, FilesErrors filesErrors, Map ctx) {
    	return getFileModule().writeFiles(binder, binder, fileUploadItems, filesErrors);
    }
    //no transaction    
   protected Map modifyBinder_toEntryData(Binder binder, InputDataAccessor inputData, Map fileItems, Map ctx) {
        //Call the definition processor to get the entry data to be stored
    	Definition def = binder.getEntryDef();
    	if (def == null) {
    		//There is no definition for this binder. Get the default definition.
     		def = getDefinitionModule().setDefaultBinderDefinition(binder);
    	} 
        return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
    }
   //inside write transaction    
    protected void modifyBinder_fillIn(Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        binder.setModification(new HistoryStamp(user));
        binder.incrLogVersion();
        for (Iterator iter=entryData.entrySet().iterator(); iter.hasNext();) {
        	Map.Entry mEntry = (Map.Entry)iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (binder.getCustomAttribute((String)mEntry.getKey()) == null) {
        			Object obj = mEntry.getValue();
        			if (obj instanceof Event)
        				getCoreDao().save(obj);
        	}
        }
        doBinderFillin(binder.getParentBinder(), binder, inputData, entryData);
               
        EntryBuilder.updateEntry(binder, entryData);

        boolean library;
	   	if (inputData.exists(ObjectKeys.FIELD_BINDER_LIBRARY))
 	   			library = Boolean.parseBoolean(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_LIBRARY));
 	   	else 
 	   		library = binder.isLibrary();

 		checkConstraintMirrored(binder.getParentBinder(), binder, library, inputData);
    }
    //no transaction    
    protected void modifyBinder_removeAttachments(Binder binder, Collection deleteAttachments,
    		List<FileAttachment> filesToDeindex, List<FileAttachment> filesToReindex, Map ctx) {
    	removeAttachments(binder, binder, deleteAttachments, filesToDeindex, filesToReindex);
    }

    //inside write transaction    
    protected void modifyBinder_postFillIn(Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//create history - using timestamp and version from fillIn
    	reorderFiles(binder, inputData, entryData);
    	processChangeLog(binder, ChangeLog.MODIFYBINDER);
    	getReportModule().addAuditTrail(AuditType.modify, binder);
   }
    
    //inside write transaction    
   protected void modifyBinder_mirrored(Binder binder, String oldTitle, String newTitle, InputDataAccessor inputData) {
    	if(binder.isMirrored())
    		normalizeResourcePath(binder, inputData);
    	
    	if(isMirroredAndNotTopLevel(binder) && !oldTitle.equals(newTitle)) {
			ResourceDriver driver = getResourceDriverManager().getDriver(binder.getResourceDriverName());
    		
    		if(driver.isReadonly()) {
				throw new NotSupportedException("errorcode.notsupported.renameMirroredBinder.readonly", 
						new String[] {binder.getPathName(), driver.getTitle()});
    		}
    		else {
				ResourceSession session = driver.openSession().setPath(binder.getResourcePath());
				try {
					session.move(binder.getParentBinder().getResourcePath(), newTitle);
					
					// Do not yet update the resource path in the binder, since we 
					// need old info again shortly.
				}
				finally {
					session.close();
				}	
    		}
    	}
    }

    private void normalizeResourcePath(Binder binder, InputDataAccessor inputData) {
   		if (inputData.exists(ObjectKeys.FIELD_BINDER_RESOURCE_PATH)) {
			String normalizedResourcePath = getResourceDriverManager().normalizedResourcePath(binder.getResourceDriverName(), binder.getResourcePath());
			binder.setResourcePath(normalizedResourcePath);  			
   		}
    }
   		
   //no transaction    
   protected void modifyBinder_indexAdd(Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems,
    		Collection<FileAttachment> filesToIndex, Map ctx) {
    	indexBinder(binder, fileUploadItems, filesToIndex, false,
    			(ctx == null ? null : (List)ctx.get(ObjectKeys.INPUT_FIELD_TAGS )));
    	
    	//Also re-index all of the direct children binders to get the correct folder extended title indexed
    	if (!ctx.get(ObjectKeys.FIELD_ENTITY_TITLE).equals(binder.getTitle())) {
    		Iterator itBinders = binder.getBinders().iterator();
    		while (itBinders.hasNext()) {
    			indexBinder((Binder) itBinders.next(), false);
    		}
    	}
    }
   //no transaction    
    protected void modifyBinder_indexRemoveFiles(Binder binder, Collection<FileAttachment> filesToDeindex, Map ctx) {
    	removeFilesIndex(binder, filesToDeindex);
    }
    //***********************************************************************************************************
 
    protected void removeFilesIndex(DefinableEntity entity, Collection<FileAttachment> filesToDeindex) {
		//remove index entry
    	for(FileAttachment fa : filesToDeindex) {
    		removeFileFromIndex(fa);
    	}
    }
    protected void removeAttachments(Binder binder, DefinableEntity entity, 
    		Collection deleteAttachments, List<FileAttachment> filesToDeindex,
    		List<FileAttachment> filesToReindex) {
    	if (deleteAttachments == null) return;
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
    //inside write transaction    
    public void deleteBinder(Binder binder, boolean deleteMirroredSource) {
    	if (binder.isReserved()) 
    		throw new NotSupportedException(
    				"errorcode.notsupported.deleteBinder", new String[]{binder.getPathName()});
    	SimpleProfiler sp = new SimpleProfiler(false);
    	
    	sp.start("deleteBinder_preDelete");
    	final Map ctx = deleteBinder_setCtx(binder, null);
        deleteBinder_preDelete(binder,ctx);
        sp.stop("deleteBinder_preDelete");
        
        sp.start("deleteBinder_processFiles");
        deleteBinder_processFiles(binder, ctx);
        sp.stop("deleteBinder_processFiles");
        
        sp.start("deleteBinder_mirrored");
        deleteBinder_mirrored(binder, deleteMirroredSource, ctx);
        sp.stop("deleteBinder_mirrored");
        
       	if (!binder.isRoot()) {
   			//delete reserved names for self which is registered in parent space
    		getCoreDao().updateFileName(binder.getParentBinder(), binder, binder.getTitle(), null);
   			if (binder.getParentBinder().isUniqueTitles()) 
   				getCoreDao().updateTitle(binder.getParentBinder(), binder, binder.getNormalTitle(), null);
    	}

        sp.start("deleteBinder_delete");
        deleteBinder_delete(binder, deleteMirroredSource, ctx);
        sp.stop("deleteBinder_delete");
       
        sp.start("deleteBinder_postDelete");
        deleteBinder_postDelete(binder, ctx);
        sp.stop("deleteBinder_postDelete");
        
        sp.start("deleteBinder_indexDel");
        deleteBinder_indexDel(binder, ctx);
        sp.stop("deleteBinder_indexDel");
     
        sp.print();
    }
    //inside write transaction    
    protected Map deleteBinder_setCtx(Binder binder, Map ctx) {
    	return ctx;
    }
    
    //inside write transaction    
   protected void deleteBinder_preDelete(Binder binder, Map ctx) { 
     	//create history - using timestamp and version from fillIn
        User user = RequestContextHolder.getRequestContext().getUser();
        binder.setModification(new HistoryStamp(user));
        binder.incrLogVersion();
    	processChangeLog(binder, ChangeLog.DELETEBINDER);
    	getReportModule().addAuditTrail(AuditType.delete, binder);
    	if ((binder.getDefinitionType() != null) &&
    			binder.getDefinitionType() == Definition.USER_WORKSPACE_VIEW) {
    		//remove connection
    		if (binder.getOwner() != null) {
    			Principal owner = binder.getOwner();
    			if (binder.getId().equals(owner.getWorkspaceId()))
    				owner.setWorkspaceId(null);
    		}
    	}
     	//remove postings to this binder handled in coreDao
    	getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMemberships(
    			RequestContextHolder.getRequestContext().getZoneId(), binder);

    }
  
    
   //inside write transaction    
    protected void deleteBinder_processFiles(Binder binder, Map ctx) {
    	getFileModule().deleteFiles(binder, binder, false, null);
     }
    
    //inside write transaction    
   protected void deleteBinder_mirrored(Binder binder, boolean deleteMirroredSource, Map ctx) {
    	if(deleteMirroredSource && binder.isMirrored()) {
    		try {
				ResourceDriver driver = getResourceDriverManager().getDriver(binder.getResourceDriverName());

				if(driver.isReadonly()) {
					throw new NotSupportedException("errorcode.notsupported.deleteMirroredBinder.readonly", 
							new String[] {binder.getPathName(), driver.getTitle()});
				}
				else {
	    			ResourceSession session = driver.openSession().setPath(binder.getResourcePath());
	    			try {
	    				session.delete();
	    			}
	    			finally {
	    				session.close();
	    			}	
				}
    		}
    		catch(NotSupportedException e) {
    			logger.warn(e.getLocalizedMessage());
    		}
    		catch(Exception e) {
    			logger.error("Error deleting source resource for mirrored binder [" + binder.getPathName() + "]", e);
    		}  	   		
    	}
     }
    
   //inside write transaction    
   protected void deleteBinder_delete(Binder binder, boolean deleteMirroredSource, Map ctx) {
    	
       	if (!binder.isRoot()) {
    		binder.getParentBinder().removeBinder(binder);
    	}
       	getCoreDao().delete(binder);
    }
   //inside write transaction    
   protected void deleteBinder_postDelete(Binder binder, Map ctx) {
    }

   //inside write transaction    
    protected void deleteBinder_indexDel(Binder binder, Map ctx) {
        // Delete the document that's currently in the index.
    	indexDeleteBinder(binder);
    }
    
    //***********************************************************************************************************
    //inside write transaction    
    public void moveBinder(Binder source, Binder destination) {
    	if (source.equals(destination)) return;
    	if (source.isReserved() || source.isZone()) 
    		throw new NotSupportedException(
    				"errorcode.notsupported.moveBinder", new String[]{source.getPathName()});
    	if (destination.isZone())
      		throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
    	//cannot move a parent to a child
    	if (destination.getBinderKey().getSortKey().startsWith(source.getBinderKey().getSortKey())) {
    		throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
    	}
    	Map ctx = moveBinder_setCtx(source, destination, null);
    	moveBinder_preMove(source, destination, ctx);
    	boolean resourcePathAffected = moveBinder_mirrored(source, destination, ctx);
    	moveBinder_move(source, destination, resourcePathAffected, ctx);
    	moveBinder_postMove(source, destination, resourcePathAffected, ctx);
    	
 		moveBinder_index(source, ctx);

    }
    //inside write transaction    
    protected Map moveBinder_setCtx(Binder source, Binder destination, Map ctx) {
    	if (ctx == null) ctx = new HashMap();
    	return ctx;
    }
    //inside write transaction    
	protected void moveBinder_preMove(Binder source, Binder destination, Map ctx) {
 	}
    //inside write transaction    
   protected boolean moveBinder_mirrored(Binder source, Binder destination, Map ctx) {
    	// Post-operation condition: A binder representing a seed resource 
    	// (ie, a top-level mirrored binder whose parent binder is not a
    	// mirrored binder) must preserve that attribute after move.
    	// Likewise, a mirrored binder that is not top-level can not be
    	// a top-level binder after move. In other words, the characteristic
    	// of being top-level or not must be preserved. 
    	// Otherwise, the move is not allowed.
    	boolean resourcePathAffected=false;
    	if(source.isMirrored()) {
    		if(source.getParentBinder().isMirrored()) { // mirrored but not top-level
    			if(destination.isMirrored()) {
    				if(source.getResourceDriverName().equals(destination.getResourceDriverName())) {
    					ResourceDriver driver = getResourceDriverManager().getDriver(source.getResourceDriverName());
    					
    		    		if(driver.isReadonly()) {
    						throw new NotSupportedException("errorcode.notsupported.renameMirroredBinder.readonly", 
    								new String[] {source.getPathName(), driver.getTitle()});
    		    		}
    		    		else {
        					// We can/must move the resource.
	    					ResourceSession session = driver.openSession().setPath(source.getResourcePath()); 
	    					try {
	    						session.move(destination.getResourcePath(), source.getTitle());  	
	    						// Do not yet update the resource path in the source, it will be done by callder.
	    						resourcePathAffected=true;
	    					}
	    					finally {
	    						session.close();
	    					}	
    		    		}
    				}
    				else {
    					logger.warn("Cannot move binder [" + source.getPathName() + "] to [" + destination.getPathName()
    							+ "] because the resource driver is different");
    		      		throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
    				}
    			}
    			else {
					logger.warn("Cannot move binder [" + source.getPathName() + "] to [" + destination.getPathName()
							+ "] because the source is not top-level mirrored and the destination is not mirrored");
		      		throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});  				
    			}
    		}
    		else { // top-level mirrored
    			if(destination.isMirrored()) {
					logger.warn("Cannot move binder [" + source.getPathName() + "] to [" + destination.getPathName()
							+ "] because the source is top-level mirrored and the destination is already mirrored");
		      		throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
    			}
    			else {
    				// Does not involve resource moving.
    			}
    		}
    	}
    	else { // not mirrored at all
    		if(destination.isMirrored()) {
				logger.warn("Cannot move binder [" + source.getPathName() + "] to [" + destination.getPathName()
						+ "] because the source is not mirrored but the destination is");
	      		throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
    		}
    		else {
    			// Does not involve resource moving.
    		}
    	}
    	return resourcePathAffected;
    } 
   //inside write transaction    
	protected void moveBinder_move(Binder source, Binder destination, boolean resourcePathAffected, Map ctx) {
	   	//Only need to update this on top level of binder tree.  Children relative to the same binder
		//remove title from old parent
    	getCoreDao().updateFileName(source.getParentBinder(), source, source.getTitle(), null);
		if (source.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(source.getParentBinder(), source, source.getNormalTitle(), null);		
		source.getParentBinder().removeBinder(source);
    	destination.addBinder(source);
    	//now add name to new parent 
		if (destination.isUniqueTitles()) getCoreDao().updateTitle(destination, source, null, source.getNormalTitle());   	
		getCoreDao().updateFileName(source.getParentBinder(), source, null, source.getTitle());
		source.setPathName(destination.getPathName() + "/" + source.getTitle());
		if (resourcePathAffected) {
			String newPath = getResourceDriverManager().normalizedResourcePath
			(source.getResourceDriverName(), source.getParentBinder().getResourcePath(), source.getTitle());
			source.setResourcePath(newPath);
		}
	}
	
    //inside write transaction    
	protected void moveBinder_postMove(Binder source, Binder destination, boolean resourcePathAffected, Map ctx) {
     	//create history - using timestamp and version from fillIn
        HistoryStamp stamp = new HistoryStamp(RequestContextHolder.getRequestContext().getUser());
        moveBinder_log(source, stamp);
        moveBinderFixup(source);
        List<Binder>children = new ArrayList(source.getBinders());
        while (!children.isEmpty()) {
        	Binder b = children.get(0);
        	children.remove(0);
        	children.addAll(b.getBinders());
    		//parent has moved, just fix up path and sortKey
    		b.setPathName(b.getParentBinder().getPathName() + "/" + b.getTitle());
    		b.setBinderKey(new HKey(b.getParentBinder().getBinderKey(), b.getBinderKey().getLastNumber()));
			if(resourcePathAffected) {
				String newPath = getResourceDriverManager().normalizedResourcePath
				(b.getResourceDriverName(), b.getParentBinder().getResourcePath(), b.getTitle());
				b.setResourcePath(newPath);
			}
  	    	BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(b, b.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
   	    	processor.moveBinderFixup(b);
   	        moveBinder_log(b, stamp);

        }
	}
    //inside write transaction    
    protected void moveBinder_log(Binder binder, HistoryStamp stamp) {
    	binder.setModification(stamp);
 		binder.incrLogVersion();
 		ChangeLog changes = new ChangeLog(binder, ChangeLog.MOVEBINDER);
    	changes.getEntityRoot();
    	getCoreDao().save(changes);

    }
    //inside write transaction    
    protected void moveBinder_index(Binder binder, Map ctx) {
    	//delete tree first
		IndexSynchronizationManager.deleteDocuments(new Term(EntityIndexUtils.ENTRY_ANCESTRY, binder.getId().toString()));
		//delete actual binder
		IndexSynchronizationManager.deleteDocument(binder.getIndexDocumentUid());
    	indexTree(binder, null);
    }
    //somewhere up the parent chain we have a new parent
    //don't have to do all the work immediate parent had to do
    //inside write transaction    
	public void moveBinderFixup(Binder binder) {
		getCoreDao().move(binder);
	}

    //********************************************************************************************************
    public Map getBinders(Binder binder, Map options) {
        //search engine will only return binder you have access to
         //validate entry count
    	//do actual search index query
        Hits hits = getBinders_doSearch(binder, options);
        //iterate through results
        List childBinders = SearchUtils.getSearchEntries(hits);

       	Map model = new HashMap();
        model.put(ObjectKeys.BINDER, binder);      
        model.put(ObjectKeys.SEARCH_ENTRIES, childBinders);
        model.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits.getTotalHits()));
        //Total number of results found
        model.put(ObjectKeys.TOTAL_SEARCH_COUNT, new Integer(hits.getTotalHits()));
        //Total number of results returned
        model.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(hits.length()));
        return model;
   }

    protected int getBinders_maxEntries(int maxChildEntries) {
        return maxChildEntries;
    }
     
    protected Hits getBinders_doSearch(Binder binder, Map options) {
    	int maxResults = 0;
    	int searchOffset = 0;
    	if (options != null) {
    		if (options.containsKey(ObjectKeys.SEARCH_MAX_HITS)) 
    			maxResults = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
        
    		if (options.containsKey(ObjectKeys.SEARCH_OFFSET)) 
    			searchOffset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);       
    	}
    	maxResults = getBinders_maxEntries(maxResults); 
    

       	
       	SearchFilter searchFilter = new SearchFilter(true);

       	if ((options != null) && options.containsKey(ObjectKeys.SEARCH_SEARCH_FILTER)) {
       		org.dom4j.Document userSearchFilter = (org.dom4j.Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER);
           	if (userSearchFilter != null) {
           		searchFilter.appendFilter(userSearchFilter);
           	}       		
       	}

    	if ((options != null) && options.containsKey(ObjectKeys.SEARCH_SEARCH_DYNAMIC_FILTER)) {
    		org.dom4j.Document userDynamicSearchFilter = (org.dom4j.Document) options.get(ObjectKeys.SEARCH_SEARCH_DYNAMIC_FILTER);
        	if (userDynamicSearchFilter != null) {
        		searchFilter.appendFilter(userDynamicSearchFilter);
        	}
    	}

       	
       	
       	
       	getBinders_getSearchDocument(binder, searchFilter);
       	org.dom4j.Document queryTree = SearchUtils.getInitalSearchDocument(searchFilter.getFilter(), options);
      	
      	SearchUtils.getQueryFields(queryTree, options); 
    	if(logger.isDebugEnabled()) {
    		logger.debug("Query is: " + queryTree.asXML());
    	}
       	
       	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
    	SearchObject so = qb.buildQuery(queryTree);
    	
    	//Set the sort order
    	SortField[] fields = SearchUtils.getSortFields(options); 
    	so.setSortBy(fields);
    	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("Query is: " + soQuery.toString());
    	}
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
    	Hits hits = null;
        try {
        	hits = luceneSession.search(soQuery, so.getSortBy(), searchOffset, maxResults);
        }
        finally {
            luceneSession.close();
        }
    	
        return hits;
     
    }
    protected void getBinders_getSearchDocument(Binder binder, SearchFilter searchFilter) {
    	searchFilter.newCurrentFilterTermsBlock(true);
    	
		searchFilter.addBinderParentId(binder.getId().toString());
   		searchFilter.addDocumentType(BasicIndexUtils.DOC_TYPE_BINDER);
    
    }    
    //***********************************************************************************************************
    //not really meant to be overridden, but here to share code
    public void indexFunctionMembership(Binder binder, boolean cascade) {
    	List<Binder> binders = new ArrayList();
    	binders.add(binder);
    	
    	if (cascade) {
    		List<Binder>candidates = new ArrayList(binder.getBinders());
    		while (!candidates.isEmpty()) {
    			Binder c = candidates.get(0);
    			candidates.remove(0);
       			if (c.isFunctionMembershipInherited()) {
    				binders.add(c);
    				candidates.addAll(c.getBinders());
    			}
    			// limit list to 100 or index will be locked
    			if (binders.size() >= 100) {
    				updateIndexFolderAcl(binders);
    				//evict used binders so don't fill session cache, but don't evict starting binder
    				if (binders.get(0).equals(binder)) binders.remove(0);
    				for (int i=0; i<binders.size(); ++i) getCoreDao().evict(binders.get(i));					
    				binders.clear();
    			}
    		}
    	};
    	
    	//finish list
    	if (!binders.isEmpty()) {
    		updateIndexFolderAcl(binders);
    	}
    }
 
    protected void updateIndexFolderAcl(List<Binder> indexBinders) {
    	ArrayList<Query> updateQueries = new ArrayList();
    	ArrayList<String> updateIds = new ArrayList();
    	//make a copy so don't modify original
    	ArrayList<Binder>binders = new ArrayList(indexBinders);
		// Now, create a query which can be used by the index update method to modify all the
		// entries, replies, attachments, and binders(workspaces) in the index with this new 
		// Acl list.
		//find descendent binders with the same owner and re-index together
		//the owner  may be part of the acl set, so cannot always share
 		while (!binders.isEmpty()) {
			Binder top = binders.get(0);
			binders.remove(0);
			List ids = new ArrayList();
			ids.add(top.getId());
			List<Binder> others = new ArrayList(binders);
			for (Binder b:others) {
				//have same owner  same acl				
				if (b.getOwnerId() != null && b.getOwnerId().equals(top.getOwnerId())) {
					binders.remove(b);
					ids.add(b.getId());
				}
			}
			org.dom4j.Document qTree = buildQueryforUpdate(ids);
	    	//don't need to add access check to update of acls
			//access to entries is not required to update the folder acl
	    	QueryBuilder qb = new QueryBuilder(null);
			// add this query and list of ids to the lists we'll pass to updateDocs.
			updateQueries.add(qb.buildQuery(qTree, true).getQuery());
			updateIds.add(EntityIndexUtils.getFolderAclString(top));
		}
		
    	if (updateQueries.size() > 0) {
    		LuceneSession luceneSession = getLuceneSessionFactory().openSession();
    		try {
    			
    			luceneSession.updateDocuments(updateQueries, BasicIndexUtils.FOLDER_ACL_FIELD,
    						updateIds);
    		} finally {
    			luceneSession.close();
    		}
    	}
    		
    }
    public void indexTeamMembership(Binder binder, boolean cascade) {
       	List<Binder> binders = new ArrayList();
    	binders.add(binder);
    	
    	if (cascade) {
    		List<Binder>candidates = new ArrayList(binder.getBinders());
    		while (!candidates.isEmpty()) {
    			Binder c = candidates.get(0);
    			candidates.remove(0);
       			if (c.isTeamMembershipInherited()) {
    				binders.add(c);
    				candidates.addAll(c.getBinders());
       			} 
    			// limit list to 100 or index will be locked
    			if (binders.size() >= 100) {
    				updateIndexTeamAcl(binders);
    				if (binders.get(0).equals(binder)) binders.remove(0);
    				for (int i=0; i<binders.size(); ++i) getCoreDao().evict(binders.get(i));					
    				binders.clear();
    			}
    		}
    	};
    	
    	//finish list
   		updateIndexTeamAcl(binders);
   }
    protected void updateIndexTeamAcl(List<Binder> binders) {
    	if (binders.isEmpty()) return;
		// Now, create a query which can be used by the index update method to modify all the
		// entries, replies, attachments, and binders(workspaces) in the index with this new 
		// team list.
		List ids = new ArrayList();
		for (Binder b:binders) {
			ids.add(b.getId());
		}
		org.dom4j.Document qTree = buildQueryforUpdate(ids);
		//don't need to add access check to update of acls
		//access to entries is not required to update the team acl
		QueryBuilder qb = new QueryBuilder(null);
		// add this query and list of ids to the lists we'll pass to updateDocs.
   		LuceneSession luceneSession = getLuceneSessionFactory().openSession();
   		try {
   			luceneSession.updateDocuments(qb.buildQuery(qTree, true).getQuery(), BasicIndexUtils.TEAM_ACL_FIELD, binders.get(0).getTeamMemberString());
   		} finally {
   			luceneSession.close();
    	}    		
    }   
 
	private static org.dom4j.Document buildQueryforUpdate(List<Long> binderIds) {
		org.dom4j.Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
		Element qTreeOrElement = qTreeRootElement.addElement(QueryBuilder.OR_ELEMENT);
    	Element qTreeAndElement = qTreeOrElement.addElement(QueryBuilder.AND_ELEMENT);
 
    	Element idsOrElement = qTreeAndElement.addElement((QueryBuilder.OR_ELEMENT));
    	//get all the entrys, replies and attachments
    	// Folderid's and doctypes:{entry, binder, attachment}
    	for (Iterator iter = binderIds.iterator(); iter.hasNext();) {
    		Element field = idsOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(iter.next().toString());
    	}
    	
    	Element typeOrElement = qTreeAndElement.addElement((QueryBuilder.OR_ELEMENT));
    	for (int i =0; i < docTypes.length; i++) {
    		Element field = typeOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(docTypes[i]);
    	}
    	// Get all the binder's themselves
    	// OR Doctype=binder and binder id's
    	Element andElement = qTreeOrElement.addElement((QueryBuilder.AND_ELEMENT));
    	Element orOrElement = andElement.addElement((QueryBuilder.OR_ELEMENT));
    	Element field = andElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(BasicIndexUtils.DOC_TYPE_BINDER);
	   	for (Iterator iter = binderIds.iterator(); iter.hasNext();) {
    		field = orOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.DOCID_FIELD);
			child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(iter.next().toString());
    	}
	   	return qTree;
	}
	
    //***********************************************************************************************************
    public void indexBinder(Binder binder, boolean includeEntries) {
    	//call overloaded methods
   		indexBinder(binder, includeEntries, true, null);    	
    }
    protected void indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, List tags) {
   		indexBinder(binder, null, null, !deleteIndex, tags);    	
    	
    }
    //***********************************************************************************************************
    //It is assumed that the index has been deleted for each binder to be index
    public Collection indexTree(Binder binder, Collection exclusions) {
    	return indexTree(binder, exclusions, StatusTicket.NULL_TICKET);
    }
    public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket) {
       	TreeSet indexedIds = new TreeSet();
       	if (exclusions == null) exclusions = new TreeSet();
   		String oldStatus = statusTicket.getStatus();
   		if(oldStatus != null) {
   			statusTicket.setStatus(oldStatus + " // " + binder.getTitle());
   		} else {
   			statusTicket.setStatus(NLT.get("index.indexingBinder", new Object[] {binder.getTitle()}));
   		}
       	if (!exclusions.contains(binder.getId())) {
        	//index self.
       		indexBinder(binder, true, false, null);
        	indexedIds.add(binder.getId());
        }
       	List binders = binder.getBinders();
   		for (int i=0; i<binders.size(); ++i) {
   	    	Binder b = (Binder)binders.get(i);
   	    	if (b.isDeleted()) continue;
   	    	//index children
   	    	BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(b, b.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
   	    	indexedIds.addAll(processor.indexTree(b, exclusions, statusTicket));
   	   	 }
   		//apply after we have gathered a few
   		IndexSynchronizationManager.applyChanges(SPropsUtil.getInt("lucene.flush.threshhold", 100));
   		statusTicket.setStatus(oldStatus);

   		return indexedIds;	
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

    	indexBinderWithAttachments(binder, binder.getFileAttachments(), fileUploadItems, newEntry, tags);
    }
    protected void indexDeleteBinder(Binder binder) {
		// Since all matches will be deleted, this will also delete the attachments 
		IndexSynchronizationManager.deleteDocument(binder.getIndexDocumentUid());
   	
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
			Collection<FileAttachment> fileAttachments, List fileUploadItems, boolean newEntry, List tags) {
		if(!newEntry) {
			// This is modification. We must first delete existing document(s) from the index.
			indexDeleteBinder(binder);	        
		}
		
        // Create an index document from the entry object.
		org.apache.lucene.document.Document indexDoc;
		if (tags == null) getCoreDao().loadAllTagsByEntity(binder.getEntityIdentifier());
		indexDoc = buildIndexDocumentFromBinder(binder, tags);

        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
        
        //Create separate documents one for each attached file and index them.
        for(FileAttachment fa : fileAttachments) {
        	FileUploadItem fui = null;
        	if(fileUploadItems != null)
        		fui = findFileUploadItem(fileUploadItems, fa.getRepositoryName(), fa.getFileItem().getName());
        	try {
           		IndexSynchronizationManager.addDocument(buildIndexDocumentFromBinderFile(binder, fa, fui, tags));
           	} catch (Exception ex) {
        		logger.error("Error index file for binder " + binder + " attachment" + fa + " " + ex.getLocalizedMessage());
        	}
        }
	}

    protected org.apache.lucene.document.Document buildIndexDocumentFromBinder(Binder binder, List tags) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        boolean fieldsOnly = false;
    	fillInIndexDocWithCommonPartFromBinder(indexDoc, binder, false);
        // Add creation-date and modification date from binder
    	// Not in common part, cause files use different dates
        EntityIndexUtils.addCreation(indexDoc, binder.getCreation(), fieldsOnly);
        EntityIndexUtils.addModification(indexDoc, binder.getModification(), fieldsOnly);
        
        //index parentBinder - used to locate sub-binders - attachments shouldn't need this
        EntityIndexUtils.addParentBinder(indexDoc, binder, fieldsOnly);

    	// Add search document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.team.search.BasicIndexUtils.DOC_TYPE_BINDER, fieldsOnly);
        //used to answer what teams am I a member of
       	if (!binder.isTeamMembershipInherited()) EntityIndexUtils.addTeamMembership(indexDoc, binder.getTeamMemberIds(), fieldsOnly);

        // Add the events
        EntityIndexUtils.addEvents(indexDoc, binder, fieldsOnly);
        
        // Add the tags for this binder
        if (tags == null) tags =  getCoreDao().loadAllTagsByEntity(binder.getEntityIdentifier());
        EntityIndexUtils.addTags(indexDoc, binder, tags, fieldsOnly);
 
        // Add attached files to binder only
        EntityIndexUtils.addAttachedFileIds(indexDoc, binder, fieldsOnly);
       
        return indexDoc;
    }   
    protected org.apache.lucene.document.Document buildIndexDocumentFromBinderFile
		(Binder binder, FileAttachment fa, FileUploadItem fui, List tags) {
       	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
       	//do common part first.  Indexing a file will remove some of the items
       	fillInIndexDocWithCommonPartFromBinder(indexDoc, binder, true);
         	
  	  	buildIndexDocumentFromFile(indexDoc, binder, binder, fa, fui, tags);
       	return indexDoc;
     }

    /**
     * This should be done last.  The indexing of a file will remove override some fields
     * @param binder
     * @param entry
     * @param fa This is non-null.
     * @param fui This may be null. 
     * @return
     */
    protected void buildIndexDocumentFromFile
    	(org.apache.lucene.document.Document indexDoc, Binder binder, DefinableEntity entity, FileAttachment fa, FileUploadItem fui, List tags) {
    	ITextConverterManager textConverterManager = null;
    	TextConverter converter = null;
		String text = "";
		
		// Get the Text converter from manager
		// Abstract Class can not use Spring Injection mechanism
		textConverterManager = (ITextConverterManager)SpringContextUtil.getBean("textConverterManager");
		converter = textConverterManager.getConverter();
		
		try
		{
			text = converter.convert(binder, entity, fa);
				}
		catch (Exception e)
		{
			// Most like conversion did not succeed, nothing client can do about this
			// limitation of Software.
			logger.error(e);
		}
			
    	
    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.team.search.BasicIndexUtils.DOC_TYPE_ATTACHMENT, true);
        
        // Add file info
        EntityIndexUtils.addFileAttachment(indexDoc, fa, true);
        
        // Add creation-date from entity
        EntityIndexUtils.addCreation(indexDoc, entity.getCreation(), true);
        // Add modification-date from file
        EntityIndexUtils.addModification(indexDoc, fa.getModification(), true);
        
        if(text != null) BasicIndexUtils.addFileContents(indexDoc, text);
        
        // Add the tags for this entry
        if (tags == null) tags =  getCoreDao().loadAllTagsByEntity(entity.getEntityIdentifier());
        EntityIndexUtils.addTags(indexDoc, entity, tags, true);
        
        //needs to be last item cause removes extraneous alltext fields
   		EntityIndexUtils.addFileAttachmentAllText(indexDoc);
   
    }
    
    //add common fields from binder for binder and its attachments
    protected void fillInIndexDocWithCommonPartFromBinder(org.apache.lucene.document.Document indexDoc, 
    		Binder binder, boolean fieldsOnly) {
    	EntityIndexUtils.addReadAccess(indexDoc, binder, fieldsOnly);

    	EntityIndexUtils.addNormTitle(indexDoc, binder, fieldsOnly);
    	
    	fillInIndexDocWithCommonPart(indexDoc, binder.getParentBinder(), binder, fieldsOnly);
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
    protected void fillInIndexDocWithCommonPart(final org.apache.lucene.document.Document indexDoc, 
    		Binder binder, final DefinableEntity entity, final boolean fieldsOnly) {
        // Add uid
        BasicIndexUtils.addUid(indexDoc, entity.getIndexDocumentUid(), fieldsOnly);
                
        // Add Doc Id
        EntityIndexUtils.addDocId(indexDoc, entity, fieldsOnly);
        
        // Add Doc title
        EntityIndexUtils.addTitle(indexDoc, entity, fieldsOnly);
        
        //Add Rating
        EntityIndexUtils.addRating(indexDoc, entity, fieldsOnly);
        
        // Add EntityType
        EntityIndexUtils.addEntityType(indexDoc, entity, fieldsOnly);
        
        // Add DefinitionType
        EntityIndexUtils.addDefinitionType(indexDoc, entity, fieldsOnly);
 
        // Add command definition
        EntityIndexUtils.addCommandDefinition(indexDoc, entity, fieldsOnly);
       
        // Add definition family
        EntityIndexUtils.addFamily(indexDoc, entity, fieldsOnly);
       
        // Add ancestry 
        EntityIndexUtils.addAncestry(indexDoc, entity, fieldsOnly);
        
 
        // Add data fields driven by the entry's definition object. 
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
                if (flagElement.attributeValue("apply").equals("true")) {
                	String fieldBuilder = flagElement.attributeValue("fieldBuilder");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
                	Field[] fields = FieldBuilderUtil.buildField(entity,
                         nameValue, fieldBuilder, args);
                	if (fields != null) {
                		for (int i = 0; i < fields.length; i++) {
                			indexDoc.add(fields[i]);
                		}
                    }
                }
			}
			public String getFlagElementName() { return "index"; }
		};
		if (!fieldsOnly) {
			getDefinitionModule().walkDefinition(entity, visitor, null);
		} else {
			getDefinitionModule().walkDefinition(entity, visitor, fieldsOnlyIndexArgs);			
		}
        
    }
    	
    /*
    protected List findCorrespondingFileAttachments(DefinableEntity entity, List fileUploadItems) {
    	List fileAttachments = new ArrayList();
    	for(int i = 0; i < fileUploadItems.size(); i++) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		FileAttachment fa = entity.getFileAttachment(fui.getRepositoryName(), fui.getOriginalFilename());
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
    				logger.error(e.getLocalizedMessage(), e);
    			}
    		}
    			
        }
    }
    protected void removeFileFromIndex(FileAttachment fa) {
    	IndexSynchronizationManager.deleteDocuments(new Term(
    			EntityIndexUtils.FILE_ID_FIELD, fa.getId()));  	
    }
    protected boolean isMirroredAndNotTopLevel(Binder binder) {
    	// A mirrored binder's title represents a directory only if the binder
    	// is not a top-level mirrored binder. 
		boolean parentIsMirrored = false;
		if(binder.getParentBinder() != null && binder.getParentBinder().isMirrored())
			parentIsMirrored = true;
		if(binder.isMirrored() && parentIsMirrored)
			return true;
		else
			return false;
    }
    
    protected void fixupPath(Binder binder) {
		if (!binder.isRoot()) {
			binder.setPathName(binder.getParentBinder().getPathName() + "/" + binder.getTitle());
		} else {
			//must be top
			binder.setPathName("/" + binder.getTitle());
		}
		
		boolean resourcePathAffected = false;
		if(isMirroredAndNotTopLevel(binder)) {
			// A mirrored binder's title actually represents a directory name on the 
			// external source only when it's parent is also a mirrored folder. 
			// That is, the top-level mirrored binders' titles bear no resemblance
			// to the names of the directories they represent. Consequently changing
			// their titles do not affect the resource names.
			resourcePathAffected = true;
			String newPath = getResourceDriverManager().normalizedResourcePath
			(binder.getResourceDriverName(), binder.getParentBinder().getResourcePath(), binder.getTitle());
			binder.setResourcePath(newPath);
		}
		
		List children = new ArrayList(binder.getBinders());
		//if we index the path, need to reindex all these folders
		while (!children.isEmpty()) {
			Binder child = (Binder)children.get(0);
			child.setPathName(child.getParentBinder().getPathName() + "/" + child.getTitle());
			if(resourcePathAffected) {
				String newPath = getResourceDriverManager().normalizedResourcePath
				(child.getResourceDriverName(), child.getParentBinder().getResourcePath(), child.getTitle());
				child.setResourcePath(newPath);
			}
			children.remove(0);
			children.addAll(child.getBinders());
		}
	}
    	
    
	protected FileUploadItem findFileUploadItem(List fileUploadItems, String repositoryName, String fileName) {
		for(int i = 0; i < fileUploadItems.size(); i++) {
			FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
			if(fui.getRepositoryName().equals(repositoryName) &&
					fui.getOriginalFilename().equals(fileName))
				return fui;
		}
		return null;
	}
	protected void reorderFiles(DefinableEntity entity, InputDataAccessor inputData, Map entryData) {
        //see if request to reorder attachments is present
        Map<String, CustomAttribute> attrs = entity.getCustomAttributes();
        for (Map.Entry<String, CustomAttribute> me: attrs.entrySet()) {
        	String name = me.getKey();
		    String key = name + ObjectKeys.INPUT_FIELD_ORDER_SUFFIX;
		    if (inputData.exists(key)) {
		    	String val = inputData.getSingleValue(key);
		    	if (Validator.isNull(val)) continue;
		    	String [] vals = StringUtil.split(val, " ");
		    	//the values are the string id of an attachment
		    	CustomAttribute current = me.getValue();
		    	Object currentObj = current.getValue();
		    	if (!(currentObj instanceof Collection)) continue;
		    	Collection currentVal = (Collection)currentObj;
		    	//if not the same size, something is wrong
		    	if (currentVal.size() != vals.length) continue;
		    	List attVals = new ArrayList();
		    	boolean process = true;
		    	for (int i=0; i<vals.length; ++i) {
		    		String id = vals[i];
		    		Attachment file = entity.getAttachment(id);
		    		if (file != null) attVals.add(file);
		    		else {
		    			process = false;
		    			break;
		    		}
		    	}
		    	if (process) current.setValue(attVals);
		    }

        }
		
	}
	public ChangeLog processChangeLog(Binder binder, String operation) {
		ChangeLog changes = new ChangeLog(binder, operation);
		//any changes here should be considered to template export
		Element element = ChangeLogUtils.buildLog(changes, binder);
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_LIBRARY, binder.isLibrary());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITFUNCTIONMEMBERSHIP, binder.isFunctionMembershipInherited());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITDEFINITIONS, binder.isDefinitionsInherited());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITTEAMMEMBERS, binder.isTeamMembershipInherited());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_UNIQUETITLES, binder.isUniqueTitles());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_TEAMMEMBERS, LongIdUtil.getIdsAsString(binder.getTeamMemberIds()));
		if (!binder.isFunctionMembershipInherited()) {
			List<WorkAreaFunctionMembership> wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
					binder.getZoneId(), binder);
			for (WorkAreaFunctionMembership wfm: wfms) {
				wfm.addChangeLog(element);
			}
		}
		getCoreDao().save(changes);
		return changes;
	}
	/*************************************************************************************************/

 
    public Set getPrincipalIds(List<DefinableEntity> results) {
    	Set ids = new HashSet();
    	for (DefinableEntity entry: results) {
            if (entry.getCreation() != null)
                ids.add(entry.getCreation().getPrincipal().getId());
            if (entry.getModification() != null)
                ids.add(entry.getModification().getPrincipal().getId());
    	}
    	return ids;
    }	     
    public Set getPrincipalIds(DefinableEntity entity) {
    	Set ids = new HashSet();
    	if (entity.getCreation() != null)
    		ids.add(entity.getCreation().getPrincipal().getId());
        if (entity.getModification() != null)
        	ids.add(entity.getModification().getPrincipal().getId());
    	return ids;
    }	     
	protected String getEntryPrincipalField() {
    	return EntityIndexUtils.CREATORID_FIELD;
    }
}
