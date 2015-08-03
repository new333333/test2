/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.binder.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.SortField;

import org.dom4j.Element;

import org.hibernate.ReplicationMode;
import org.hibernate.exception.LockAcquisitionException;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.IllegalCharacterInNameException;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.docconverter.ITextConverterManager;
import org.kablink.teaming.docconverter.TextConverter;
import org.kablink.teaming.docconverter.TextStreamConverter;
import org.kablink.teaming.docconverter.TextStreamConverterManager;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderQuota;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DeletedBinder;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityDashboard;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.fi.connection.ResourceSession;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.jobs.BinderReindex;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.definition.index.FieldBuilderDescription;
import org.kablink.teaming.module.definition.index.FieldBuilderUtil;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.FilesErrors;
import org.kablink.teaming.module.file.FilterException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.rss.RssModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.ChangeLogUtils;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.EntryBuilder;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.relevance.Relevance;
import org.kablink.teaming.relevance.util.RelevanceUtils;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.QueryBuilder;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.util.ConcurrentStatusTicket;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.FileUploadItem;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.NetworkUtil;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.servlet.listener.ContextListenerPostSpring;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.FieldFactory;

import static org.kablink.util.search.Constants.ENTITY_ID_FIELD;
import static org.kablink.util.search.Restrictions.conjunction;
import static org.kablink.util.search.Restrictions.disjunction;
import static org.kablink.util.search.Restrictions.eq;
import static org.kablink.util.search.Restrictions.in;
import static org.kablink.util.search.Restrictions.not;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class AbstractBinderProcessor extends CommonDependencyInjection 
	implements BinderProcessor {
	private static  String[] docTypes = new String[] {Constants.DOC_TYPE_ENTRY,Constants.DOC_TYPE_ATTACHMENT};
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
	
	private BinderModule binderModule;
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	protected BinderModule getBinderModule() {
		if (binderModule != null) return binderModule;
		binderModule = (BinderModule)SpringContextUtil.getBean("binderModule");
		return binderModule;
	}

	protected AdminModule getAdminModule() {
		return (AdminModule) SpringContextUtil.getBean("adminModule");
	}

	private WorkflowModule workflowModule;
	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	protected WorkflowModule getWorkflowModule() {
		return workflowModule;
	}
	
	private FolderModule folderModule;
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	protected FolderModule getFolderModule() {
		return folderModule;
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
	
	private TemplateModule templateModule;
	protected TemplateModule getTemplateModule() {
		return templateModule;
	}
	public void setTemplateModule(TemplateModule templateModule) {
		this.templateModule = templateModule;
	}
	
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
    private ITextConverterManager textConverterManager;
    protected ITextConverterManager getTextConverterManager() {
    	return textConverterManager;
    }
    public void setTextConverterManager(ITextConverterManager textConverterManager) {
    	this.textConverterManager = textConverterManager;
    }

    private TextStreamConverterManager textStreamConverterManager;
    protected TextStreamConverterManager getTextStreamConverterManager() {
    	return textStreamConverterManager;
    }
    public void setTextStreamConverterManager(TextStreamConverterManager textStreamConverterManager) {
    	this.textStreamConverterManager = textStreamConverterManager;
    }

	//***********************************************************************************************************	
    //no transaction    
    @Override
	public Binder addBinder(final Binder parent, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems, Map options) 
    	throws AccessControlException, WriteFilesException, WriteEntryDataException {
        // This default implementation is coded after template pattern. 
      	if (parent.isZone())
      		throw new NotSupportedException("errorcode.notsupported.addbinder");
               
      	SimpleProfiler.start("addBinder_toEntryData");
        final Map ctx = new HashMap();
        if (options != null) ctx.putAll(options);
        addBinder_setCtx(parent, ctx);
        Map entryDataAll = addBinder_toEntryData(parent, def, inputData, fileItems,ctx);
        SimpleProfiler.stop("addBinder_toEntryData");
        
        final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
        List fileUploadItems = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
        List allUploadItems = new ArrayList(fileUploadItems);
        EntryDataErrors entryDataErrors = (EntryDataErrors) entryDataAll.get(ObjectKeys.DEFINITION_ERRORS);
        if (entryDataErrors.getProblems().size() > 0) {
        	//An error occurred processing the entry Data
        	throw new WriteEntryDataException(entryDataErrors);
        }
       
    	try {
    		SimpleProfiler.start("addBinder_create");
	        final Binder binder = addBinder_create(def, clazz, ctx);
	        SimpleProfiler.stop("addBinder_create");
	        	        
	    	if (def != null) {
	    		if ((parent.getDefinitionType() == null) ||
	    				(binder.getDefinitionType().intValue() != parent.getDefinitionType().intValue())) {
	    			binder.setDefinitionsInherited(false);
	    		}
	    	}
	        String title = (String)entryData.get("title");
	        if (Validator.isEmptyString(title)) {
	        	title = (String)inputData.getSingleValue("title");
	        	entryData.put("title", title);
	        }
	        if (Validator.isEmptyString(title)) throw new TitleException("");
    		if (Validator.containsPathCharacters(title)) throw new IllegalCharacterInNameException("errorcode.title.pathCharacters", new Object[]{title}); 
	        
	        binder.setPathName(parent.getPathName() + "/" + title);
	        
	        SimpleProfiler.start("addBinder_transactionExecute");
	        // The following part requires update database transaction.
	        int tryMaxCount = 1 + SPropsUtil.getInt("select.database.transaction.retry.max.count", ObjectKeys.SELECT_DATABASE_TRANSACTION_RETRY_MAX_COUNT);
	        int tryCount = 0;
	        while(true) {
	        	tryCount++;
	        	try {
			        getTransactionTemplate().execute(new TransactionCallback() {
			        	@Override
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
			        break; // successful transaction
	        	}
	        	catch(LockAcquisitionException | CannotAcquireLockException e) {
	        		if(tryCount < tryMaxCount) {
	        			if(logger.isDebugEnabled())
	        				logger.warn("(" + tryCount + ") 'add binder' failed due to lock error - Retrying in new transaction", e);
	        			else 
	        				logger.warn("(" + tryCount + ") 'add binder' failed due to lock error - Retrying in new transaction: " + e.toString());
	        			getCoreDao().refresh(parent);        		
	        		}
	        		else {
	        			logger.error("(" + tryCount + ") 'add binder' failed due to lock error - Aborting", e);
	        			throw e;
	        		}
	        	}
	        }
	        SimpleProfiler.stop("addBinder_transactionExecute");
	           
	        SimpleProfiler.start("addBinder_filterFiles");
	        //Need to do filter here after binder is saved cause it makes use of
	        // the id of binder
	        FilesErrors filesErrors = addBinder_filterFiles(binder, fileUploadItems,ctx);
	        SimpleProfiler.stop("addBinder_filterFiles");
	        
	        SimpleProfiler.start("addBinder_processFiles");
	        // We must save the entry before processing files because it makes use
	        // of the persistent id of the entry. 
	        filesErrors = addBinder_processFiles(binder, fileUploadItems, filesErrors,ctx);
	        SimpleProfiler.stop("addBinder_processFiles");
	        
	        SimpleProfiler.start("addBinder_indexAdd");
	        // This must be done in a separate step after persisting the entry,
	        // because we need the entry's persistent ID for indexing. 
	        addBinder_indexAdd(parent, binder, inputData, fileUploadItems, ctx);
	        SimpleProfiler.stop("addBinder_indexAdd");
	        
	        //SimpleProfiler.done(logger);

	    	if(filesErrors.getProblems().size() > 0) {
	    		// At least one error occured during the operation. 
	    		throw new WriteFilesException(filesErrors, binder.getId());
	    	}
	    	else {
	    		return binder;
	    	}
    	}
    	finally {
	        cleanupFiles(allUploadItems);
    	}
    }
    //inside write transaction    
    protected void addBinder_setCtx(Binder binder, Map ctx) {
    }

    //inside write transaction    
   protected FilesErrors addBinder_filterFiles(Binder binder, List fileUploadItems, Map ctx) throws FilterException {
  		FilesErrors nameErrors = new FilesErrors();
   		
   		checkInputFileNames(fileUploadItems, nameErrors);

        FilesErrors checkSumErrors = getFileModule().verifyCheckSums(fileUploadItems);
  		FilesErrors filterErrors = getFileModule().filterFiles(binder, binder, fileUploadItems);
    	filterErrors.getProblems().addAll(nameErrors.getProblems());
        filterErrors.getProblems().addAll(checkSumErrors.getProblems());
    	return filterErrors;
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
    		HashMap map = new HashMap();
    		map.put(ObjectKeys.DEFINITION_ENTRY_DATA, new HashMap());
    		map.put(ObjectKeys.DEFINITION_FILE_DATA, new LinkedList());
    		map.put(ObjectKeys.DEFINITION_ERRORS, new EntryDataErrors());
    		return map;
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
        binder.setZoneId(parent.getZoneId());
        processCreationTimestamp(binder, ctx);
        processModificationTimestamp(binder, binder.getCreation(), ctx);
        binder.setLogVersion(Long.valueOf(1));
        
        Principal owner = binder.getCreation().getPrincipal();
        if(ctx.containsKey(ObjectKeys.INPUT_OPTION_OWNER_ID)) {
        	try {
        		owner = getProfileDao().loadUser((Long)ctx.get(ObjectKeys.INPUT_OPTION_OWNER_ID), RequestContextHolder.getRequestContext().getZoneId());
        	}
        	catch(Exception e) {
        		logger.warn("Error loading owning user '" + ctx.get(ObjectKeys.INPUT_OPTION_OWNER_ID).toString() + "'. Defaulting to creator.");
        	}
        }
        binder.setOwner(owner);

       	//force a lock so contention on the sortKey is reduced
        Object lock = ctx.get(ObjectKeys.INPUT_OPTION_FORCE_LOCK);
        if (Boolean.TRUE.equals(lock)) {
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
  		if (inputData.exists(ObjectKeys.FIELD_BINDER_INHERITFUNCTIONS) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_INHERITFUNCTIONS)) {
   			entryData.put(ObjectKeys.FIELD_BINDER_INHERITFUNCTIONS, Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_INHERITFUNCTIONS)));
   		}
  		if (inputData.exists(ObjectKeys.FIELD_BINDER_INHERITDEFINITIONS) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_INHERITDEFINITIONS)) {
   			entryData.put(ObjectKeys.FIELD_BINDER_INHERITDEFINITIONS, Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_INHERITDEFINITIONS)));
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
    }

    protected void checkConstraintMirrored(Binder parent, Binder binder, String oldTitle, boolean library, InputDataAccessor inputData) {
 		// A little more validation is necessary with respect to mirrored binder.
 		if(binder.isMirrored()) {
 			if(!library)
 				throw new IllegalArgumentException("Mirrored folder must also be a library folder");
 			if(binder.getResourceDriverName() == null) {
 				if(parent.isMirrored()) {
 					binder.setResourceDriverName(parent.getResourceDriverName());
 					binder.setNetFolderConfigId(parent.getNetFolderConfigId());
 				}
 				else {
 					if(!binder.isAclExternallyControlled() && binder.getTitle() != null && !binder.getTitle().equals(oldTitle)) {
 						// This is a old legacy mirrored folder (or its sub-folder) and the user is changing the title of the folder.
 						// In this case, don't throw an exception so that we can work around the issue reported in bug #914335.
 					}
 					else {
 						throw new ConfigurationException("errorcode.mirrored.folder.requires.resource.driver." + (binder.isAclExternallyControlled() ? "net" : "mirrored"), new Object[]{});
 					}
 				}
 			}
 			else {
 				if(parent.isMirrored()) {
 					if(!binder.getResourceDriverName().equals(parent.getResourceDriverName()))
 						throw new IllegalArgumentException("Specified resource driver name [" + binder.getResourceDriverName()
 								+ "] does not match the resource driver name [" + parent.getResourceDriverName()
 								+ "] of the parent binder [" + parent.getPathName() + "]");
 					Long binderNFCId = binder.getNetFolderConfigId();
 					Long parentNFCId = parent.getNetFolderConfigId();
 					boolean equal;
 					if(binderNFCId == null) {
 						if(parentNFCId == null)
 							equal = true;
 						else
 							equal = false;
 					}
 					else {
 						if(parentNFCId == null)
 							equal = false;
 						else if(binderNFCId.equals(parentNFCId))
 							equal = true;
 						else
 							equal = false;
 					}
 					if(!equal)
						throw new IllegalArgumentException("Specified net folder config id [" + binderNFCId
 								+ "] does not match the net folder config id [" + parentNFCId
 								+ "] of the parent binder [" + parent.getPathName() + "]");
 				}
 			}
 			if(binder.getResourcePath() == null) {
 				if(parent.isMirrored()) {
 					binder.setResourcePath(getResourceDriverManager().normalizedResourcePath(parent.getResourceDriverName(), parent.getResourcePath(), binder.getTitle()));
 				}
 				else {
 					binder.setResourcePath("");
 				}
 			}
 		}
 		else {
 			binder.setResourceDriverName(null);
 			binder.setResourcePath(null);
 			binder.setNetFolderConfigId(null);
 		}
    }

    //inside write transaction    
    protected void addBinder_mirrored(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
		if(parent.isMirrored() && parent.getResourceDriverName() == null) {
			// We  allow adding sub-folder to a mirrored folder that has not been fully configured yet,
			// since it can lead to 
			throw new NotSupportedException("errorcode.notsupported.addBinder.unconfiguredParentMirroredBinder." + (parent.isAclExternallyControlled() ? "net" : "mirrored"), 
					new String[] {binder.getPathName()});
		}		
		
		if (binder.isMirrored() || parent.isMirrored()) { 
			// The newly created binder is a mirrored one or its parent is one.
			// Make sure that the resource path we store is normalized.
			if (!binder.isMirrored()) {
				//If the binder is not mirrored, force it to be mirrored since only mirrored folders are allowed in a mirrored folder.
				binder.setMirrored(true);
			}
	    	normalizeResourcePathIfInInput(binder, inputData);
	    	if(parent.isMirrored()) {
		    	binder.setResourceDriverName(parent.getResourceDriverName());
		    	binder.setNetFolderConfigId(parent.getNetFolderConfigId());
	    	}
	    	
	    	ResourceDriver driver = binder.getResourceDriver();
	    	if(driver != null) {
				ResourceSession session = null;
				try {
					if(binder.getResourcePath() == null && parent.getResourcePath() != null) {
						session = getResourceDriverManager().getSession(driver, ResourceDriverManager.FileOperation.CREATE_FOLDER, parent);
						session.setPath(parent.getResourcePath(), parent.getResourceHandle(), binder.getTitle(), binder.getResourceHandle(), Boolean.TRUE);
						binder.setResourcePath(session.getPath());
						normalizeResourcePath(binder);
					}
					
					// Perform outward synchronization, if requested and possible.
					Boolean synchToSource = Boolean.TRUE;
					if(inputData.exists(ObjectKeys.PI_SYNCH_TO_SOURCE))
						synchToSource = Boolean.parseBoolean(inputData.getSingleValue(ObjectKeys.PI_SYNCH_TO_SOURCE));
					if(Boolean.TRUE.equals(synchToSource)) {
						
						if(driver.isReadonly()) {
							throw new NotSupportedException("errorcode.notsupported.addMirroredBinder.readonly." + (binder.isAclExternallyControlled() ? "net" : "mirrored"), 
									new String[] {binder.getPathName(), driver.getTitle()});
						}
						else {
							if(session == null) {						
								session = getResourceDriverManager().getSession(driver, ResourceDriverManager.FileOperation.CREATE_FOLDER, parent);
								session.setPath(binder.getResourcePath(), binder.getResourceHandle(), Boolean.TRUE);
							}
							
							session.createDirectory();
						}
					}
				}
				finally {
					if(session != null)
						session.close();
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
		boolean skipDbLog = false;
		if(ctx != null && ctx.containsKey(ObjectKeys.INPUT_OPTION_SKIP_DB_LOG))
			skipDbLog = ((Boolean)ctx.get(ObjectKeys.INPUT_OPTION_SKIP_DB_LOG)).booleanValue();

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
 		
		//generate events uid
		Iterator<Event> it = binder.getEvents().iterator();
		while (it.hasNext()) {
			Event event = it.next();
			event.generateUid(binder);
		}
	
 		//create history - using timestamp and version from fillIn
    	processChangeLog(binder, ChangeLog.ADDBINDER, skipDbLog);

		if(!skipDbLog)
	    	getReportModule().addAuditTrail(AuditType.add, binder);
    	
    	
    	// Should have a BinderQuota for the newly created binder.
    	BinderQuota bq;
    	try {
    		// Normally, BinerQuota shouldn't already exist for a newly created binder. 
    		// However, due to the way this code was written previously, there's slight chance that
    		// inconsistency can arise between the binder and the corresponding quota object. 
    		// This code will fix the inconsistency if detected.
    		bq = getCoreDao().loadBinderQuota(binder.getZoneId(), binder.getId());
    		bq.setDiskQuota(null);
    		bq.setDiskSpaceUsed(0L);
    		bq.setDiskSpaceUsedCumulative(0L);
    	} catch(NoObjectByTheIdException e) {
    		bq = new BinderQuota();
    		bq.setZoneId(binder.getZoneId());
    		bq.setBinderId(binder.getId());
    		bq.setDiskSpaceUsed(0L);
    		bq.setDiskSpaceUsedCumulative(0L);
    		getCoreDao().save(bq);
    	}
    	
    	updateParentModTime(parent, ctx);
    }

    //inside write transaction    
   protected void addBinder_indexAdd(Binder parent, Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems, Map ctx) {
        //no tags typically exists on a new binder - reduce db lookups by supplying list
    	List tags = null;
 	   if (ctx != null && Boolean.TRUE.equals(ctx.get(ObjectKeys.INPUT_OPTION_NO_INDEX))) return;
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
   			if (inputData.exists( ObjectKeys.FIELD_NET_FOLDER_CONFIG_ID ) &&
      				 !entryData.containsKey( ObjectKeys.FIELD_NET_FOLDER_CONFIG_ID )) {
  				Long value = Long.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_NET_FOLDER_CONFIG_ID ) );
  				if ( value != null )
  					entryData.put( ObjectKeys.FIELD_NET_FOLDER_CONFIG_ID, value );
      		}
   			if (inputData.exists(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME)) {
   				entryData.put(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, inputData.getSingleValue(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME));
   			}
   			if (inputData.exists(ObjectKeys.FIELD_BINDER_RESOURCE_PATH) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_RESOURCE_PATH)) {
   				entryData.put(ObjectKeys.FIELD_BINDER_RESOURCE_PATH, inputData.getSingleValue(ObjectKeys.FIELD_BINDER_RESOURCE_PATH));
   			}
   			if (inputData.exists(ObjectKeys.FIELD_BINDER_REL_RSC_PATH) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_REL_RSC_PATH)) {
   				entryData.put(ObjectKeys.FIELD_BINDER_REL_RSC_PATH, inputData.getSingleValue(ObjectKeys.FIELD_BINDER_REL_RSC_PATH));
   			}
   			if (inputData.exists(ObjectKeys.FIELD_RESOURCE_HANDLE) && !entryData.containsKey(ObjectKeys.FIELD_RESOURCE_HANDLE)) {
   				entryData.put(ObjectKeys.FIELD_RESOURCE_HANDLE, inputData.getSingleValue(ObjectKeys.FIELD_RESOURCE_HANDLE));
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_IS_HOME_DIR ) && !entryData.containsKey( ObjectKeys.FIELD_BINDER_IS_HOME_DIR ) )
   			{
   				Boolean homeDir = null;
   				
   				homeDir = Boolean.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_IS_HOME_DIR ) );
   				if ( homeDir != null )
   					entryData.put( ObjectKeys.FIELD_BINDER_IS_HOME_DIR, homeDir );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_IS_MYFILES_DIR ) && !entryData.containsKey( ObjectKeys.FIELD_BINDER_IS_MYFILES_DIR ) )
   			{
   				Boolean myFilesDir = null;
   				
   				myFilesDir = Boolean.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_IS_MYFILES_DIR ) );
   				if ( myFilesDir != null )
   					entryData.put( ObjectKeys.FIELD_BINDER_IS_MYFILES_DIR, myFilesDir );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_ALLOW_DESKTOP_APP_TO_SYNC_DATA ) && !entryData.containsKey( ObjectKeys.FIELD_BINDER_ALLOW_DESKTOP_APP_TO_SYNC_DATA ) )
   			{
   				Boolean allow = null;
   				
   				allow = Boolean.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_ALLOW_DESKTOP_APP_TO_SYNC_DATA ) );
   				if ( allow != null )
   					entryData.put( ObjectKeys.FIELD_BINDER_ALLOW_DESKTOP_APP_TO_SYNC_DATA, allow );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_ALLOW_MOBILE_APPS_TO_SYNC_DATA ) && !entryData.containsKey( ObjectKeys.FIELD_BINDER_ALLOW_MOBILE_APPS_TO_SYNC_DATA ) )
   			{
   				Boolean allow = null;
   				
   				allow = Boolean.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_ALLOW_MOBILE_APPS_TO_SYNC_DATA ) );
   				if ( allow != null )
   					entryData.put( ObjectKeys.FIELD_BINDER_ALLOW_MOBILE_APPS_TO_SYNC_DATA, allow );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_INDEX_CONTENT ) && !entryData.containsKey( ObjectKeys.FIELD_BINDER_INDEX_CONTENT ) )
   			{
   				Boolean index = null;
   				
   				index = Boolean.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_INDEX_CONTENT ) );
   				if ( index != null )
   					entryData.put( ObjectKeys.FIELD_BINDER_INDEX_CONTENT, index );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_JITS_ENABLED ) &&
   				 !entryData.containsKey( ObjectKeys.FIELD_BINDER_JITS_ENABLED ) )
   			{
   				Boolean enabled = null;
   				
   				enabled = Boolean.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_JITS_ENABLED ) );
   				if ( enabled != null )
   					entryData.put( ObjectKeys.FIELD_BINDER_JITS_ENABLED, enabled );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_JITS_ACL_MAX_AGE ) &&
   				 !entryData.containsKey( ObjectKeys.FIELD_BINDER_JITS_ACL_MAX_AGE ) )
   			{
   				Long value;
   				
   				value = Long.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_JITS_ACL_MAX_AGE ) );
   				if ( value != null )
   					entryData.put( ObjectKeys.FIELD_BINDER_JITS_ACL_MAX_AGE, value );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_JITS_RESULTS_MAX_AGE ) &&
      			 !entryData.containsKey( ObjectKeys.FIELD_BINDER_JITS_RESULTS_MAX_AGE ) )
   			{
   				Long value;
   				
   				value = Long.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_JITS_RESULTS_MAX_AGE ) );
   				if ( value != null )
   					entryData.put( ObjectKeys.FIELD_BINDER_JITS_RESULTS_MAX_AGE, value );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_SYNC_SCHEDULE_OPTION ) &&
         		 !entryData.containsKey( ObjectKeys.FIELD_BINDER_SYNC_SCHEDULE_OPTION ) )
  			{
  				Object value;
  				
  				value = inputData.getSingleObject( ObjectKeys.FIELD_BINDER_SYNC_SCHEDULE_OPTION );
				entryData.put( ObjectKeys.FIELD_BINDER_SYNC_SCHEDULE_OPTION, value );
  			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_FULL_SYNC_DIR_ONLY ) && !entryData.containsKey( ObjectKeys.FIELD_BINDER_FULL_SYNC_DIR_ONLY ) )
   			{
   				Boolean dirOnly = null;
   				Object value;
   				
   				value = inputData.getSingleObject( ObjectKeys.FIELD_BINDER_FULL_SYNC_DIR_ONLY ); 
   				if ( value != null )
   	   				dirOnly = Boolean.valueOf( inputData.getSingleValue( ObjectKeys.FIELD_BINDER_FULL_SYNC_DIR_ONLY ) );
 
   				entryData.put( ObjectKeys.FIELD_BINDER_FULL_SYNC_DIR_ONLY, dirOnly );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_USE_INHERITED_INDEX_CONTENT ) && !entryData.containsKey( ObjectKeys.FIELD_BINDER_USE_INHERITED_INDEX_CONTENT ) )
   			{
   				Boolean inherit = null;
   				Object value;
   				
   				value = inputData.getSingleObject( ObjectKeys.FIELD_BINDER_USE_INHERITED_INDEX_CONTENT );
   				if ( value != null && value instanceof Boolean )
   	   				inherit = (Boolean)value;
   					
   				entryData.put( ObjectKeys.FIELD_BINDER_USE_INHERITED_INDEX_CONTENT, inherit );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_USE_INHERITED_JITS_SETTINGS ) && !entryData.containsKey( ObjectKeys.FIELD_BINDER_USE_INHERITED_JITS_SETTINGS ) )
   			{
   				Boolean inherit = null;
   				Object value;
   				
   				value = inputData.getSingleObject( ObjectKeys.FIELD_BINDER_USE_INHERITED_JITS_SETTINGS );
   				if ( value != null && value instanceof Boolean )
   	   				inherit = (Boolean)value;
   					
   				entryData.put( ObjectKeys.FIELD_BINDER_USE_INHERITED_JITS_SETTINGS, inherit );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_USE_INHERITED_DESKTOP_APP_TRIGGER_SETTING ) &&
   				 !entryData.containsKey( ObjectKeys.FIELD_BINDER_USE_INHERITED_DESKTOP_APP_TRIGGER_SETTING ) )
   			{
   				Boolean inherit = null;
   				Object value;
   				
   				value = inputData.getSingleObject( ObjectKeys.FIELD_BINDER_USE_INHERITED_DESKTOP_APP_TRIGGER_SETTING );
   				if ( value != null && value instanceof Boolean )
   	   				inherit = (Boolean)value;
   					
   				entryData.put( ObjectKeys.FIELD_BINDER_USE_INHERITED_DESKTOP_APP_TRIGGER_SETTING, inherit );
   			}

   			if ( inputData.exists( ObjectKeys.FIELD_BINDER_ALLOW_DESKTOP_APP_TO_TRIGGER_INITIAL_HOME_FOLDER_SYNC ) &&
   				 !entryData.containsKey( ObjectKeys.FIELD_BINDER_ALLOW_DESKTOP_APP_TO_TRIGGER_INITIAL_HOME_FOLDER_SYNC ) )
   			{
   				Boolean allow = null;
   				Object value;
   				
   				value = inputData.getSingleObject( ObjectKeys.FIELD_BINDER_ALLOW_DESKTOP_APP_TO_TRIGGER_INITIAL_HOME_FOLDER_SYNC );
   				if ( value != null && value instanceof Boolean )
   					allow = (Boolean) value;
   				
   				entryData.put( ObjectKeys.FIELD_BINDER_ALLOW_DESKTOP_APP_TO_TRIGGER_INITIAL_HOME_FOLDER_SYNC, allow );
   			}

   		}
   		Boolean library = null;
		if (inputData.exists(ObjectKeys.FIELD_BINDER_LIBRARY) && !entryData.containsKey(ObjectKeys.FIELD_BINDER_LIBRARY))
			library = Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_LIBRARY));
		if(library != null) {
			entryData.put(ObjectKeys.FIELD_BINDER_LIBRARY, library);
		}
 	}
    //***********************************************************************************************************
    //no transaction    
    @Override
	public void modifyBinder(final Binder binder, final InputDataAccessor inputData, 
    		Map fileItems, final Collection deleteAttachments, Map options) 
    		throws AccessControlException, WriteFilesException, WriteEntryDataException {
	
    	SimpleProfiler.start("modifyBinder_toEntryData");
        final Map ctx = new HashMap();
        if (options != null) ctx.putAll(options);
    	modifyBinder_setCtx(binder, ctx);
	    Map entryDataAll = modifyBinder_toEntryData(binder, inputData, fileItems, ctx);
	    SimpleProfiler.stop("modifyBinder_toEntryData");
	    
	    final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	    List fileUploadItems = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
        List allUploadItems = new ArrayList(fileUploadItems);
	    EntryDataErrors entryDataErrors = (EntryDataErrors) entryDataAll.get(ObjectKeys.DEFINITION_ERRORS);
        if (entryDataErrors.getProblems().size() > 0) {
        	//An error occurred processing the entry Data
        	throw new WriteEntryDataException(entryDataErrors);
        }

	    try {
		    
	    	SimpleProfiler.start("modifyBinder_filterFiles");
		    FilesErrors filesErrors = modifyBinder_filterFiles(binder, fileUploadItems, ctx);
		    SimpleProfiler.stop("modifyBinder_filterFiles");
	
		    SimpleProfiler.start("modifyBinder_transactionExecute");
	    	// The following part requires update database transaction.
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	@Override
				public Object doInTransaction(TransactionStatus status) {
	        		String oldTitle = binder.getTitle();
	        		String oldNormalTitle = binder.getNormalTitle();
	        		modifyBinder_fillIn(binder, oldTitle, inputData, entryData, ctx);
	        		modifyBinder_postFillIn(binder, inputData, entryData, ctx);
	        		//if title changed, must update path info for all child folders
	        		String newTitle = binder.getTitle();
	        		if (Validator.isEmptyString(newTitle)) throw new TitleException("");
	        		if (Validator.containsPathCharacters(newTitle)) throw new IllegalCharacterInNameException("errorcode.title.pathCharacters", new Object[]{newTitle});
	        		modifyBinder_mirrored(binder, oldTitle, newTitle, inputData);
	        		//case matters here
	        		if ((oldTitle == null) || !oldTitle.equals(newTitle)) {
	        			fixupPath(binder);
	        			
	        			// If there is a "team group" associated with this binder, rename the group
	        			// to match the binder's new name.
	        			getBinderModule().fixupTeamGroupName( binder );
	        		}
	        		if (!binder.isRoot()) {
	        			getCoreDao().updateFileName(binder.getParentBinder(), binder, oldTitle, newTitle);
	        			if (binder.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(binder.getParentBinder(), binder, oldNormalTitle, binder.getNormalTitle());

        			}
        			return null;
	        	}});
	        SimpleProfiler.stop("modifyBinder_transactionExecute");
	        
	        //handle outside main transaction so main changeLog doesn't reflect attactment changes
	        SimpleProfiler.start("modifyBinder_removeAttachments");
	    	List<FileAttachment> filesToDeindex = new ArrayList<FileAttachment>();
	    	List<FileAttachment> filesToReindex = new ArrayList<FileAttachment>();	    
	        modifyBinder_removeAttachments(binder, deleteAttachments, filesToDeindex, filesToReindex, ctx);    
	        SimpleProfiler.stop("modifyBinder_removeAttachments");
	        
	        SimpleProfiler.start("modifyBinder_processFiles");
		    filesErrors = modifyBinder_processFiles(binder, fileUploadItems, filesErrors, ctx);
		    SimpleProfiler.stop("modifyBinder_processFiles");
		    
	    	// Since index update is implemented as removal followed by add, 
	    	// the update requests must be added to the removal and then add
	    	// requests respectively. 
	    	filesToDeindex.addAll(filesToReindex);
	    	SimpleProfiler.start("modifyBinder_indexRemoveFiles");
	        modifyBinder_indexRemoveFiles(binder, filesToDeindex, ctx);
	        SimpleProfiler.stop("modifyBinder_indexRemoveFiles");
	        
	        SimpleProfiler.start("modifyBinder_indexAdd");
		    modifyBinder_indexAdd(binder, inputData, fileUploadItems, filesToReindex, ctx);
		    SimpleProfiler.stop("modifyBinder_indexAdd");
		    
		    //SimpleProfiler.done(logger);
		    
	    	if (filesErrors.getProblems().size() > 0) {
	    		// At least one error occured during the operation. 
	    		throw new WriteFilesException(filesErrors);
	    	}
	    	else {
	    		return;
	    	}
	    } finally {
		    cleanupFiles(allUploadItems);
	    }
	}
    //no transaction    
    protected void modifyBinder_setCtx(Binder binder, Map ctx) {
    	//save title before changes
		ctx.put(ObjectKeys.FIELD_ENTITY_TITLE, binder.getTitle());
    }
    //no transaction    
    protected FilesErrors modifyBinder_filterFiles(Binder binder, List fileUploadItems, Map ctx) throws FilterException {
  		FilesErrors nameErrors = new FilesErrors();
   		
   		checkInputFileNames(fileUploadItems, nameErrors);
        FilesErrors checkSumErrors = getFileModule().verifyCheckSums(fileUploadItems);
  		FilesErrors filterErrors = getFileModule().filterFiles(binder, binder, fileUploadItems);
    	filterErrors.getProblems().addAll(nameErrors.getProblems());
    	filterErrors.getProblems().addAll(checkSumErrors.getProblems());
    	return filterErrors;
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
    protected void modifyBinder_fillIn(Binder binder, String oldTitle, InputDataAccessor inputData, Map entryData, Map ctx) {  
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

 		checkConstraintMirrored(binder.getParentBinder(), binder, oldTitle, library, inputData);
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
    	String description = (String)ctx.get(ObjectKeys.FIELD_ENTITY_TITLE);
    	if (description == null || binder.getTitle().equals(description)) {
    		getReportModule().addAuditTrail(AuditType.modify, binder);
    	} else {
    		//We use the description field to hold the original title in case this is needed at report time
    		getReportModule().addAuditTrail(AuditType.rename, binder, description);
    	}
   }
    
    //inside write transaction    
   protected void modifyBinder_mirrored(Binder binder, String oldTitle, String newTitle, InputDataAccessor inputData) {
    	if(binder.isMirrored())
    		normalizeResourcePathIfInInput(binder, inputData);
    	
    	if(isMirroredAndNotTopLevel(binder) && !oldTitle.equals(newTitle)) {
			ResourceDriver driver = getResourceDriverManager().getDriver(binder.getResourceDriverName());
    		
    		if(driver.isReadonly()) {
				throw new NotSupportedException("errorcode.notsupported.renameMirroredBinder.readonly." + (binder.isAclExternallyControlled() ? "net" : "mirrored"), 
						new String[] {binder.getPathName(), driver.getTitle()});
    		}
    		else {
				ResourceSession session = getResourceDriverManager().getSession(driver, ResourceDriverManager.FileOperation.UPDATE, binder.getParentBinder()).setPath(binder.getResourcePath(), binder.getResourceHandle(), Boolean.TRUE);
				try {
					session.move(binder.getParentBinder().getResourcePath(), binder.getParentBinder().getResourceHandle(), newTitle);
					
					// Do not yet update the resource path in the binder, since we 
					// need old info again shortly.
				}
				finally {
					session.close();
				}	
    		}
    	}
    }

    private void normalizeResourcePathIfInInput(Binder binder, InputDataAccessor inputData) {
   		if (inputData.exists(ObjectKeys.FIELD_BINDER_RESOURCE_PATH)) {
   			normalizeResourcePath(binder);
   		}
    }
    
    private void normalizeResourcePath(Binder binder) {
		String normalizedResourcePath = getResourceDriverManager().normalizedResourcePath(binder.getResourceDriverName(), binder.getResourcePath());
		binder.setResourcePath(normalizedResourcePath);  			   	
    }
   		
    //no transaction    
    protected void modifyBinder_indexAdd(Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems,
    		Collection<FileAttachment> filesToIndex, Map ctx) {
    	// Do we need to index nested FolderEntry's?
    	AbstractEntryProcessor aep          = null;
    	boolean                isRename     = ((ctx != null) && (!(ctx.get(ObjectKeys.FIELD_ENTITY_TITLE).equals(binder.getTitle()))));
    	boolean                indexEntries = (isRename && binder.isAclExternallyControlled());
    	if (indexEntries) {
    		// Bugzilla 872468 (DRF):  We do if we're renaming a binder
    		// in Net Storage.  Otherwise, ACL checks on their comments
    		// through FAMT may fail after the rename.  To index them,
    		// we'll need an entry processor.
			BinderProcessor processor = ((BinderProcessor) getProcessorManager().getProcessor(
				binder,
				binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY)));

			indexEntries = (processor instanceof AbstractEntryProcessor);
			if (indexEntries) {
				aep = ((AbstractEntryProcessor) processor);
			}
	   }
	   
	   List tags = (ctx == null ? null : ((List) ctx.get(ObjectKeys.INPUT_FIELD_TAGS)));
	   indexBinder(binder, fileUploadItems, filesToIndex, false, tags);
	   if (indexEntries) {
		   aep.indexEntries(binder, true, true, true);
		   IndexSynchronizationManager.applyChanges(0);	// Is this needed?  AbstractEntryProcessor.indexBinderIncremental() does it so I copied it here.
	   }
    	
	   // Also re-index all of the direct children binders to get the
	   // correct folder extended title indexed
	   if (isRename) {
		   // If the title has changed for the binder, we must re-index
		   // all of the child binders recursively so that their paths
		   // get updated in the index.
		   List children = new ArrayList(binder.getBinders());
		   while (!children.isEmpty()) {
			   Binder child = (Binder)children.get(0);
			   indexBinder(child, false);
			   if (indexEntries) {
				   aep.indexEntries(child, true, true, true);
				   IndexSynchronizationManager.applyChanges(0);	// Is this needed?  AbstractEntryProcessor.indexBinderIncremental() does it so I copied it here.
			   }
			   children.remove(0);
			   children.addAll(child.getBinders());
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
    @Override
	public void deleteBinder(Binder binder, boolean deleteMirroredSource, Map options, boolean skipDbLog) {
    	if (binder.isReserved() && !binder.getRoot().isDeleted()) 
    		throw new NotSupportedException(
    				"errorcode.notsupported.deleteBinder", new String[]{binder.getPathName()});
    	
        final Map ctx = new HashMap();
        if (options != null) ctx.putAll(options);
     	deleteBinder_setCtx(binder, ctx);

     	{
	     	// Bugzilla 686906:
	     	//    Moved these calls from below deleteBinder_preDelete()
     		//    call so that we delete the mirror information, if
     		//    necessary first so in case that fails, we won't have
     		//    removed all the metadata.
	        SimpleProfiler.start("deleteBinder_processFiles");
	        deleteBinder_processFiles(binder, ctx);
	        SimpleProfiler.stop("deleteBinder_processFiles");
	        
	        SimpleProfiler.start("deleteBinder_mirrored");
	        deleteBinder_mirrored(binder, deleteMirroredSource, ctx);
	        SimpleProfiler.stop("deleteBinder_mirrored");
     	}
        
        SimpleProfiler.start("deleteBinder_indexDel");
        deleteBinder_indexDel(binder, ctx);
        SimpleProfiler.stop("deleteBinder_indexDel");
     
    	SimpleProfiler.start("deleteBinder_preDelete");
        deleteBinder_preDelete(binder,ctx, skipDbLog);
        SimpleProfiler.stop("deleteBinder_preDelete");
        
       	if (!binder.isRoot()) {
   			//delete reserved names for self which is registered in parent space
    		getCoreDao().updateFileName(binder.getParentBinder(), binder, binder.getTitle(), null);
   			if (binder.getParentBinder().isUniqueTitles()) 
   				getCoreDao().updateTitle(binder.getParentBinder(), binder, binder.getNormalTitle(), null);
    	}

        SimpleProfiler.start("deleteBinder_delete");
        deleteBinder_delete(binder, deleteMirroredSource, ctx);
        SimpleProfiler.stop("deleteBinder_delete");
       
        SimpleProfiler.start("deleteBinder_postDelete");
        deleteBinder_postDelete(binder, ctx);
        SimpleProfiler.stop("deleteBinder_postDelete");
        
        SimpleProfiler.start("deleteBinder_deleteRssFeed");
        doRssDelete(binder);
        SimpleProfiler.stop("deleteBinder_deleteRssFeed");
     
        //SimpleProfiler.done(logger);
    }
    //inside write transaction    
    protected void deleteBinder_setCtx(Binder binder, Map ctx) {
    	//save title before changes
		ctx.put(ObjectKeys.FIELD_ENTITY_TITLE, binder.getTitle());
    }
    
    //inside write transaction    
   protected void deleteBinder_preDelete(Binder binder, Map ctx, boolean skipDbLog) { 
     	//create history - using timestamp and version from fillIn
        User user = RequestContextHolder.getRequestContext().getUser();
        binder.setModification(new HistoryStamp(user));
        binder.incrLogVersion();
        
		//create a log for the binder being deleted.
		try {
			DeletedBinder deletedBinder = new DeletedBinder(binder);
			getCoreDao().replicate(deletedBinder, ReplicationMode.OVERWRITE);
		}
		catch(Exception e) {
			logger.error("Error creating DeletedBinder for binder " + binder.getId(), e);
		}		

    	processChangeLog(binder, ChangeLog.DELETEBINDER, skipDbLog);
        if(!skipDbLog) {
        	// Make sure that the audit trail's timestamp is identical to the modification time of the binder. 
        	// 12/19/2014 JK - The path of the deleted binder is no longer stored with the audit trail.
        	// Instead, the path information for deleted binders are available from another table.
         	getReportModule().addAuditTrail(AuditType.delete, binder, binder.getModification().getDate());
        }
    	if ((binder.getDefinitionType() != null) &&
    			(binder.getDefinitionType() == Definition.USER_WORKSPACE_VIEW ||
    				binder.getDefinitionType() == Definition.EXTERNAL_USER_WORKSPACE_VIEW)) {
    		//remove connection
   			Principal owner = binder.getCreation().getPrincipal(); //creator is user
   			if (binder.getId().equals(owner.getWorkspaceId()))
   				owner.setWorkspaceId(null);
    	}
     	//remove postings to this binder handled in coreDao
    	getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMemberships(
    			RequestContextHolder.getRequestContext().getZoneId(), binder);
	    
    	//remove share items associated with this binder or with any entries within this binder
    	getCoreDao().deleteShares(binder, true);
    	
    	// Unlike regular group, team group's life cycle is bound by the binder that owns it.
    	// Remove team group, if exists, owned by this binder.
    	if(binder.getTeamGroupId() != null) {
    		Principal teamGroup = getProfileDao().loadPrincipal(binder.getTeamGroupId(), RequestContextHolder.getRequestContext().getZoneId(), false);
    		ProfileCoreProcessor profileProcessor = (ProfileCoreProcessor) getProcessorManager().getProcessor(teamGroup.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
    		try {
				profileProcessor.deleteEntry(teamGroup.getParentBinder(), teamGroup, false, null);
			} catch (WriteFilesException e) {
				logger.error("Error deleting team group '" + teamGroup.getId() + "' owned by binder '" + binder.getId() + "'", e);
			}
    	}
    }
  
    
   //inside write transaction    
    protected void deleteBinder_processFiles(Binder binder, Map ctx) {
    	getFileModule().deleteFiles(binder, binder, false, null);
     }
    
    //inside write transaction    
   protected void deleteBinder_mirrored(Binder binder, boolean deleteMirroredSource, Map ctx) {
	   	// Delete the source resource, if so required.
    	if(deleteMirroredSource && binder.isMirrored() && binder.getResourceDriverName() != null) {
    		try {
				ResourceDriver driver = getResourceDriverManager().getDriver(binder.getResourceDriverName());

				if(driver.isReadonly()) {
					throw new NotSupportedException("errorcode.notsupported.deleteMirroredBinder.readonly." + (binder.isAclExternallyControlled() ? "net" : "mirrored"), 
							new String[] {binder.getPathName(), driver.getTitle()});
				}
				else {
					//Guard against deleting the whole mirrored source by accident
					boolean okToDeleteSource = true;
					if (binder.isAclExternallyControlled() && 
							binder.getParentBinder() != null && binder.getParentBinder().isAclExternallyControlled() &&
							binder.getParentBinder().getResourceDriverName().equals(binder.getResourceDriverName())) {
						
						//This is a sub-folder of a net folder. Check that it has a proper resource path
						if (binder.getResourcePath() == null || binder.getResourcePath().equals("") || 
								binder.getResourcePath().equals("/")) {
							//Don't allow deleting of this source because it looks like the configuration wasn't properly completed.
							okToDeleteSource = false;
						}
					}
					if (okToDeleteSource) {
		    			ResourceSession session = getResourceDriverManager().getSession(driver, ResourceDriverManager.FileOperation.DELETE, binder.getParentBinder()).setPath(binder.getResourcePath(), binder.getResourceHandle(), Boolean.TRUE);
		    			try {
		    				session.delete();
		    			}
		    			finally {
		    				session.close();
		    			}	
					}
				}
    		}
    		catch(NotSupportedException e) {
    			logger.warn(e.getLocalizedMessage());
    			throw e;
    		}
    		catch(UncheckedIOException e) {
    			logger.error("Error deleting source resource for mirrored binder (1) [" + binder.getPathName() + "]", e);
    			throw e;
    		}
    		catch(Exception e) {
    			logger.error("Error deleting source resource for mirrored binder (2) [" + binder.getPathName() + "]", e);
    			throw e;
    		}
    	}
     }
    
   //inside write transaction    
   protected void deleteBinder_delete(Binder binder, boolean deleteMirroredSource, Map ctx) {
    	
       	if (!binder.isRoot()) {
    		binder.getParentBinder().removeBinder(binder);
    	}
       	if (!binder.isDeleted()) {
    		//assume other code is handling the delete
    		getCoreDao().delete(binder);
    	}
    }
   //inside write transaction    
   protected void deleteBinder_postDelete(Binder binder, Map ctx) {
   }

   //inside write transaction    
    protected void deleteBinder_indexDel(Binder binder, Map ctx) {
        // Delete the document that's currently in the index.
    	indexDeleteBinder(binder);
    	// Flush out index changes immediately rather than waiting for the module interceptor to do it.
    	IndexSynchronizationManager.applyChanges();
    }
    
    //***********************************************************************************************************
    //inside write transaction    
    @Override
	public void moveBinder(Binder source, Binder destination, Map options) {
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
    	//Check to make sure the target binder has quota enough for this
    	if (!checkMoveBinderQuota(source, destination)) {
    		throw new NotSupportedException("errorcode.notsupported.moveBinderDestinationQuota", new String[] {destination.getPathName()});
    	}
    	
 		//Clear the RSS feed of the original source binder before it is moved
 		doRssUpdate(source);

        final Map ctx = new HashMap();
        if (options != null) ctx.putAll(options);
     	moveBinder_setCtx(source, destination, ctx);
    	moveBinder_preMove(source, destination, ctx);
    	boolean resourcePathAffected = moveBinder_mirrored(source, destination, ctx);
    	moveBinder_move(source, destination, resourcePathAffected, ctx);
    	moveBinder_postMove(source, destination, resourcePathAffected, ctx);
    	
 		moveBinder_index(source, ctx);
 		
 		//Clear the RSS feed of its new destination after the move
 		doRssUpdate(source);
    }
    
    //Check to see if destination folder has enough quota
    @Override
	public boolean checkMoveBinderQuota(Binder source, Binder destination) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	if (getBinderModule().isBinderDiskQuotaEnabled()) {
	    	Long minQuotaLeft = getBinderModule().getMinBinderQuotaLeft(destination);
	    	try {
		    	BinderQuota sourceBinderQuota = getCoreDao().loadBinderQuota(zoneId, source.getId());
		    	if (minQuotaLeft != null && minQuotaLeft < sourceBinderQuota.getDiskSpaceUsedCumulative()) {
		    		//There is not enough quota in the destination. See if this is a parent binder of the source
		    		Binder parentBinder = source;
		    		while (parentBinder != null) {
		    			if (parentBinder.equals(destination)) {
		    				//This binder is an ancestor, so the quota is not going to change
		    				return true;
		    			}
		    			parentBinder = parentBinder.getParentBinder();
		    		}
		    		//Destination does not have the quota for this move.
		    		return false;
		    	}
			} catch(NoObjectByTheIdException e) {
				//Skip any binders that don't have a quota set up (shouldn't happen, but...)
				//If there is no quota set up, just let the move be done
			}
    	}

    	return true;
    }

    //Check to see if destination folder has enough quota
    @Override
	public boolean checkMoveEntryQuota(Binder source, Binder destination, FolderEntry entry) {
    	SortedSet<FileAttachment> atts = entry.getFileAttachments();
    	Long totalFileSizes = 0L;
    	for (FileAttachment att : atts) {
    		Set<VersionAttachment> fileVersions = att.getFileVersions();
    		for (VersionAttachment fv : fileVersions) {
    			totalFileSizes += fv.getFileItem().getLength();
    		}
    	}
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	if (getBinderModule().isBinderDiskQuotaEnabled()) {
	    	Long minQuotaLeft = getBinderModule().getMinBinderQuotaLeft(destination);
	    	try {
		    	if (minQuotaLeft != null && minQuotaLeft < totalFileSizes) {
		    		//There is not enough quota in the destination. See if destination is a parent binder of the source
		    		Binder parentBinder = source;
		    		while (parentBinder != null) {
		    			if (parentBinder.equals(destination)) {
		    				//This binder is an ancestor, so the quota is not going to change
		    				return true;
		    			}
		    			parentBinder = parentBinder.getParentBinder();
		    		}
		    		//Also see if moving down in the hierarchy and the destination has room
		    		parentBinder = destination;
		    		BinderQuota parentBinderQuota = getCoreDao().loadBinderQuota(zoneId, destination.getId());
		    		while (parentBinder != null) {
		    			//See if the parent binder would have the room in its quota
		    			if (parentBinderQuota.getDiskQuota() == null ||
		    					totalFileSizes < parentBinderQuota.getDiskQuota() - parentBinderQuota.getDiskSpaceUsed()) {
			    			if (parentBinder.equals(source)) {
			    				//This binder is an descendant, so the quota is not going to change
			    				return true;
			    			}
		    			} else {
		    				//This parent doesn't have the room in its quota
		    				return false;
		    			}
		    			parentBinder = parentBinder.getParentBinder();
		    		}
		    		//Destination does not have the quota for this move.
		    		return false;
		    	}
			} catch(NoObjectByTheIdException e) {
				//Skip any binders that don't have a quota set up (shouldn't happen, but...)
				//If there is no quota set up, just let the move be done
			}
    	}

    	return true;
    }
    
    //inside write transaction    
    protected void moveBinder_setCtx(Binder source, Binder destination, Map ctx) {
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
    						throw new NotSupportedException("errorcode.notsupported.moveMirroredBinder.readonly." + (destination.isAclExternallyControlled() ? "net" : "mirrored"), 
    								new String[] {source.getPathName(), driver.getTitle()});
    		    		}
    		    		else {
        					// We can/must move the resource.
	    					ResourceSession session = getResourceDriverManager().getSession(driver, ResourceDriverManager.FileOperation.MOVE_FOLDER, source.getParentBinder(), destination).setPath(source.getResourcePath(), source.getResourceHandle(), Boolean.TRUE); 
	    					try {
	    						session.move(destination.getResourcePath(), destination.getResourceHandle(), source.getTitle());  	
	    						// Do not yet update the resource path in the source, it will be done by caller.
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
		      		throw new NotSupportedException("errorcode.notsupported.moveBinderMirroredToNonMirrored." + (source.isAclExternallyControlled() ? "net" : "mirrored"));  				
    			}
    		}
    		else { // top-level mirrored
    			if(destination.isMirrored()) {
					logger.warn("Cannot move binder [" + source.getPathName() + "] to [" + destination.getPathName()
							+ "] because the source is top-level mirrored and the destination is already mirrored");
		      		throw new NotSupportedException("errorcode.notsupported.moveBinderTopMirroredDestinationMirrored." + (source.isAclExternallyControlled() ? "net" : "mirrored"));
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
		// Let's check if the caller also wants to change the title of the binder while moving it.
		String newTitle = (ctx==null)?null:(String)ctx.get(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE);
		if(Validator.isNotNull(newTitle))
			source.setTitle(newTitle);

        source.copyInheritedDefinitions();
		source.move(destination);
    	//now add name to new parent 
		if (destination.isUniqueTitles()) getCoreDao().updateTitle(destination, source, null, source.getNormalTitle());   	
		getCoreDao().updateFileName(source.getParentBinder(), source, null, source.getTitle());
		if (resourcePathAffected) {
			source.setNetFolderConfigId(destination.getNetFolderConfigId());
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
        	b.move(b.getParentBinder());
			if(resourcePathAffected) {
				b.setNetFolderConfigId(destination.getNetFolderConfigId());
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
 		ChangeLog changes = ChangeLogUtils.create(binder, ChangeLog.MOVEBINDER);
 		ChangeLogUtils.save(changes);

    }
    //inside write transaction    
    protected void moveBinder_index(Binder binder, Map ctx) {
    	//delete tree first
		IndexSynchronizationManager.deleteDocuments(new Term(Constants.ENTRY_ANCESTRY, binder.getId().toString()));
		getCoreDao().flush(); //get updates out for optimized indexTree
    	indexTree(binder, null);  //binder will be evicted on return
    }
    //somewhere up the parent chain we have a new parent
    //don't have to do all the work immediate parent had to do
    //inside write transaction    
	@Override
	public void moveBinderFixup(Binder binder) {
		getCoreDao().move(binder);
		
	}
    //***********************************************************************************************************
    //no transaction    
    @Override
	public Binder copyBinder(final Binder source, final Binder destination, final Map options) {
    	//Check if moving a binder into itself or into a sub binder of itself
    	Binder parent = destination;
    	while (parent != null) {
    		if (source.equals(parent))
        		throw new NotSupportedException("errorcode.notsupported.copyBinderDestination", new String[] {destination.getPathName()});
    		parent = (Binder) parent.getParentWorkArea();
    	}
    	if (source.isReserved() || source.isZone()) 
    		throw new NotSupportedException(
    				"errorcode.notsupported.copyBinder", new String[]{source.getPathName()});
    	if (destination.isZone())
      		throw new NotSupportedException("errorcode.notsupported.copyBinderDestination", new String[] {destination.getPathName()});
    	if (ObjectKeys.PROFILE_ROOT_INTERNALID.equals(destination.getInternalId()))
         		throw new NotSupportedException("errorcode.notsupported.copyBinderDestination", new String[] {destination.getPathName()});
    	//Check to make sure the target binder has quota enough for this
    	if (!checkMoveBinderQuota(source, destination)) {
    		throw new NotSupportedException("errorcode.notsupported.moveBinderDestinationQuota", new String[] {destination.getPathName()});
    	}
        final Map ctx = new HashMap();
        if (options != null) ctx.putAll(options);
    	copyBinder_setCtx(source, destination, ctx);
		//Make sure there is no other binder with the same name in the parent.
     	String newTitle = BinderHelper.getUniqueBinderTitleInParent(source.getTitle(), destination, (options==null)?null:(String)options.get(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE));
     	final Binder binder = copyBinder_create(source, destination, newTitle, ctx);
 		return binder;
    }
    //no transaction    
    protected void copyBinder_setCtx(Binder source, Binder destination, Map ctx) {
    }
    //no transaction - should be overridden 
	protected Binder copyBinder_create(Binder source, Binder destination, String title, Map ctx) {
	   Binder sampleBinder = source;
	   Definition sampleDef = sampleBinder.getEntryDef();
	   List<Definition> sampleDefs = sampleBinder.getDefinitions();
	   Class sampleBinderClass = sampleBinder.getClass();
	   if (destination.isAclExternallyControlled() || destination.isMirrored()) {
		   //When the destination is a remote disk folder, make the new binder the same type as the destination
		   sampleBinder = destination;
		   sampleBinderClass = sampleBinder.getClass();
		   sampleDef = sampleBinder.getEntryDef();
		   sampleDefs = sampleBinder.getDefinitions();
	   }
	   if (source.isAclExternallyControlled() && !destination.isAclExternallyControlled()) {
		   //Copying from a net folder to a non-net folder, make the sample be a regular file folder
		   if (destination.isLibrary()) {
			   //The destination is a file folder, so use it as the sample
			   sampleBinder = destination;
			   sampleBinderClass = sampleBinder.getClass();
			   sampleDef = sampleBinder.getEntryDef();
			   sampleDefs = sampleBinder.getDefinitions();
		   } else {
			   //Copying this to a non-library folder, so we must use a file folder definition
			   sampleBinder = destination;
			   sampleBinderClass = Folder.class;
			   sampleDef = getDefinitionModule().getDefinitionByReservedId(ObjectKeys.DEFAULT_LIBRARY_FOLDER_DEF);
			   sampleDefs = sampleBinder.getDefinitions();
			   TemplateBinder tb = getTemplateModule().getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_LIBRARY);
			   if (tb != null) {
				   sampleDefs = tb.getDefinitions();
			   }
		   }
	   }
       Map data = new HashMap();
       data.put("title", title);
       InputDataAccessor inputData = new MapInputData(data);
       Binder binder = null;
       try {
			binder = addBinder(destination, sampleDef, sampleBinderClass, inputData, null, null);
			if (!(destination.getOwnerId().equals(binder.getOwnerId()))) {
				// Bugzilla:  926037 and 926033:
				//    We want to make the owner of the copied binder
				//    the same as the owner of the binder being copied
				//    into.
				binder.setOwner(destination.getOwner());
			}
			//Also copy the configured definitions from the sample
			binder.setDefinitions(sampleDefs);
			//If moving share items, do that now
			if (ctx.containsKey(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS) && 
					(Boolean)ctx.get(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS)) {
				List<Long> shareItemIds = getProfileDao().getShareItemIdsByEntity(source);
				if (!shareItemIds.isEmpty()) {
					getProfileDao().changeSharedEntityId(shareItemIds, binder);
				}
			}
			getCoreDao().flush();
       } catch (Exception e) {}
       
       return binder;
   }

   //inside write transaction    
   protected void copyBinder_fillIn(Binder source, Binder parent, Binder binder, Map ctx) {  
       binder.setLogVersion(Long.valueOf(1));
       binder.setPathName(parent.getPathName() + "/" + binder.getTitle());

   	//force a lock so contention on the sortKey is reduced
       Object lock = ctx.get(ObjectKeys.INPUT_OPTION_FORCE_LOCK);
       if (Boolean.TRUE.equals(lock)) {
           getCoreDao().lock(parent);
       } 
       parent.addBinder(binder);
		if(binder.isMirrored())
 			binder.setLibrary(true);
   }
   protected void copyBinder_preSave(Binder source, Binder parent, Binder binder, Map ctx) {  
   }
   protected void copyBinder_save(Binder source, Binder parent, Binder binder, Map ctx) {   
	   getCoreDao().save(binder);
   }
   protected void copyBinder_postSave(Binder source, Binder parent, Binder binder, Map ctx) {   
		EntityDashboard dashboard = getCoreDao().loadEntityDashboard(source.getEntityIdentifier());
		if (dashboard != null) {
			EntityDashboard myDashboard = new EntityDashboard(dashboard);
			myDashboard.setOwnerIdentifier(binder.getEntityIdentifier());
			getCoreDao().save(myDashboard);
		  }
		//copy all file attachments; need to do first so custom file attributes have a real object to reference
		getFileModule().copyFiles(source, source, binder, binder, null);
		EntryBuilder.copyAttributes(source, binder);
  		getWorkAreaFunctionMembershipManager().copyWorkAreaFunctionMemberships(binder.getZoneId(), source, binder);
  		List<Tag> tags = getCoreDao().loadAllTagsByEntity(source.getEntityIdentifier());
  		//copy tags
  		for (Tag t:tags) {
  			Tag tCopy = new Tag(t);
  			tCopy.setEntityIdentifier(binder.getEntityIdentifier());
  			if (source.getEntityIdentifier().equals(t.getOwnerIdentifier())) {
  				tCopy.setOwnerIdentifier(binder.getEntityIdentifier());
  			}
  			getCoreDao().save(tCopy);
  		}
  		
    }
   
    //inside write transaction    
    protected void copyBinder_index(Binder binder, Map ctx) {  
		getCoreDao().flush(); //get updates out 
		//entries should be indexed already
    	indexBinder(binder, false, false, null); 
    }
    
    //********************************************************************************************************
    @Override
	public Map getBinders(Binder binder, Map searchOptions) {
        //search engine will only return binder you have access to
         //validate entry count
    	//do actual search index query
        Hits hits = getBinders_doSearch(binder, searchOptions);
        //iterate through results
        return buildResultMap(binder, hits);
   }

    @Override
	public Map getBinders(Binder binder, List binderIds, Map searchOptions) {
        //search engine will only return binder you have access to
         //validate entry count
    	//do actual search index query 
        Hits hits = getBinders_doSearch(binder, binderIds, searchOptions);
        return buildResultsMap(hits);
    }

    private Map buildResultMap(Binder binder, Hits hits) {
        Map model = buildResultsMap(hits);
        model.put(ObjectKeys.BINDER, binder);
        return model;
    }

    private Map buildResultsMap(Hits hits) {
        //iterate through results
        List childBinders = SearchUtils.getSearchEntries(hits);

        Map model = new HashMap();
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
     
    protected Hits getBinders_doSearch(List binderIds, Map searchOptions) {
    	return getBinders_doSearch(null, binderIds, searchOptions);
    }
    protected Hits getBinders_doSearch(Binder binder, Map searchOptions) {
    	return getBinders_doSearch(binder, null, searchOptions);
    }
    protected Hits getBinders_doSearch(Binder binder, List binderIds, Map searchOptions) {
    	int maxResults = 0;
    	int searchOffset = 0;
    	if (searchOptions != null) {
    		if (searchOptions.containsKey(ObjectKeys.SEARCH_MAX_HITS)) 
    			maxResults = (Integer) searchOptions.get(ObjectKeys.SEARCH_MAX_HITS);
        
    		if (searchOptions.containsKey(ObjectKeys.SEARCH_OFFSET)) 
    			searchOffset = (Integer) searchOptions.get(ObjectKeys.SEARCH_OFFSET);       
    	}
    	maxResults = getBinders_maxEntries(maxResults); 
    

       	
       	SearchFilter searchFilter = new SearchFilter();

       	if ((searchOptions != null) && searchOptions.containsKey(ObjectKeys.SEARCH_SEARCH_FILTER)) {
       		org.dom4j.Document userSearchFilter = (org.dom4j.Document) searchOptions.get(ObjectKeys.SEARCH_SEARCH_FILTER);
           	if (userSearchFilter != null) {
           		searchFilter.appendFilter(userSearchFilter);
           	}       		
       	}

    	if ((searchOptions != null) && searchOptions.containsKey(ObjectKeys.SEARCH_SEARCH_DYNAMIC_FILTER)) {
    		org.dom4j.Document userDynamicSearchFilter = (org.dom4j.Document) searchOptions.get(ObjectKeys.SEARCH_SEARCH_DYNAMIC_FILTER);
        	if (userDynamicSearchFilter != null) {
        		searchFilter.appendFilter(userDynamicSearchFilter);
        	}
    	}

       	if (binderIds != null) {
       		searchFilter.addBinderParentIds(binderIds);
       	}
       	
       	else if (binder != null) {
            getBinders_getSearchDocument(binder, searchFilter);
        }
       	
       	org.dom4j.Document queryTree = SearchUtils.getInitalSearchDocument(searchFilter.getFilter(), searchOptions);
      	
      	SearchUtils.getQueryFields(queryTree, searchOptions); 
    	if(logger.isTraceEnabled()) {
    		logger.trace("Query is: " + queryTree.asXML());
    	}
       	
       	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder(true, false);
    	SearchObject so = qb.buildQuery(queryTree);
    	
    	//Set the sort order
    	SortField[] fields = SearchUtils.getSortFields(searchOptions); 
    	so.setSortBy(fields);

		Boolean allowJits = ((searchOptions != null) ? ((Boolean) searchOptions.get(ObjectKeys.SEARCH_ALLOW_JITS)) : null);
		if (allowJits==null) {
			allowJits = Boolean.TRUE;
		}

		LuceneReadSession luceneSession = getLuceneSessionFactory().openReadSession();
        
    	Hits hits = null;
        try {
        	hits = SearchUtils.searchFolderOneLevelWithInferredAccess(luceneSession, RequestContextHolder.getRequestContext().getUserId(),
        			so, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, searchOffset, maxResults,
        			binder, true);
        }
        finally {
            luceneSession.close();
        }
    	
        return hits;
     
    }
    protected void getBinders_getSearchDocument(Binder binder, SearchFilter searchFilter) {
    	searchFilter.newCurrentFilterTermsBlock(true);
    	
		searchFilter.addBinderParentId(binder.getId().toString());
   		searchFilter.addDocumentType(Constants.DOC_TYPE_BINDER);
    
    }    
    //***********************************************************************************************************
    //not really meant to be overridden, but here to share code
 
    @Override
	public void indexFunctionMembership(Binder binder, boolean cascade, Boolean runInBackground, boolean indexEntries) {
    	indexFunctionMembership(binder, cascade, runInBackground, indexEntries, false);
    }
    
    @Override
	public void indexFunctionMembership(Binder binder, boolean cascade, Boolean runInBackground, boolean indexEntries, boolean skipFileContentIndexing) {
		String value = EntityIndexUtils.getFolderAclString(binder);
    	if (cascade) {
    		Map params = new HashMap();
    		params.put("functionTest", Boolean.FALSE);
    		//this will return binders down the tree that may come after others that are not inheritting, 
    		//but since the upper ancestor is in the tree it won't hurt to have extras in the not phrase
    		List<Object[]> notBinders = getCoreDao().loadObjects("select x.id,x.binderKey.sortKey from org.kablink.teaming.domain.Binder x where x.binderKey.sortKey like '" +
    				binder.getBinderKey().getSortKey() + "%' and x.functionMembershipInherited=:functionTest order by x.binderKey.sortKey", params);
       		List<Long>ids = pruneUpdateList(binder, notBinders);
    		int limit=SPropsUtil.getInt("lucene.max.booleans", 10000) - 10;  //account for others in search
    		if (ids.size() <= limit) {
    			doFieldUpdate(binder, ids, Constants.FOLDER_ACL_FIELD, value, runInBackground, indexEntries, skipFileContentIndexing);
    			doRssUpdate(binder);
    		} else {
    			//revert to walking the tree
    	    	List<Binder> binders = new ArrayList();
    	    	binders.add(binder);
    	    	List<Binder>candidates = new ArrayList(binder.getBinders());
    	    	while (!candidates.isEmpty()) {
    	    		Binder c = candidates.get(0);
    	    		candidates.remove(0);
    	    		if (c.isFunctionMembershipInherited()) {
    	    			binders.add(c);
    	    			candidates.addAll(c.getBinders());
    	    		}
    	    		if (binders.size() >= limit) {
    	    			doFieldUpdate(binders, Constants.FOLDER_ACL_FIELD, value,indexEntries);
    	    			doRssUpdate(binders);
    	    			//evict used binders so don't fill session cache, but don't evict starting binder
    	    			if (binders.get(0).equals(binder)) binders.remove(0);
    	    			getCoreDao().evict(binders);					
    	    			binders.clear();
    	    		}
    	    	}
    	       	//finish list
    			doFieldUpdate(binders, Constants.FOLDER_ACL_FIELD, value, indexEntries);
    			doRssUpdate(binders);
    		}
    	} else {
       		doFieldUpdate(binder, Constants.FOLDER_ACL_FIELD, value, runInBackground, indexEntries, skipFileContentIndexing);
       		doRssUpdate(binder);
       	    		
    	}
    	
    }
     @Override
	public void indexTeamMembership(Binder binder, boolean cascade) {
    	String value = getBinderModule().getTeamMemberString( binder );
    	if (cascade) {
    		Map params = new HashMap();
    		params.put("functionTest", Boolean.FALSE);
    		//this will return binders down the tree that may come after others that are not inheritting
    		//but since the upper ancestor is in the tree it won't hurt to have extras in the not phrase
    		List<Object[]> notBinders = getCoreDao().loadObjects("select x.id,x.binderKey.sortKey from org.kablink.teaming.domain.Binder x where x.binderKey.sortKey like '" +
    				binder.getBinderKey().getSortKey() + "%' and x.teamMembershipInherited=:functionTest order by x.binderKey.sortKey", params);
       		List<Long>ids = pruneUpdateList(binder, notBinders);
       		int limit=SPropsUtil.getInt("lucene.max.booleans", 10000) - 10;  //account for others in search
    		if (ids.size() <= limit) {
          		doFieldUpdate(binder, ids, Constants.TEAM_ACL_FIELD, value, null, false);
          		doRssUpdate(binder);
       		} else {
       			List<Binder> binders = new ArrayList();
       			binders.add(binder);
        		List<Binder>candidates = new ArrayList(binder.getBinders());
        		while (!candidates.isEmpty()) {
        			Binder c = candidates.get(0);
        			candidates.remove(0);
           			if (c.isTeamMembershipInherited()) {
        				binders.add(c);
        				candidates.addAll(c.getBinders());
           			} 
       	    		if (binders.size() >= limit) {
       	    			doFieldUpdate(binders, Constants.TEAM_ACL_FIELD, value, false);
       	    			doRssUpdate(binders);
       	    			//evict used binders so don't fill session cache, but don't evict starting binder
       	    			if (binders.get(0).equals(binder)) binders.remove(0);
        				getCoreDao().evict(binders);					
        				binders.clear();
        			}
        		}
        		//finish list
        		doFieldUpdate(binders, Constants.TEAM_ACL_FIELD, value, false);
        		doRssUpdate(binders);
       		}
    	} else {
    		doFieldUpdate(binder, Constants.TEAM_ACL_FIELD, value, null, false);
    		doRssUpdate(binder);
    	}
    	
   }
    //list must be sorted by sortKey ascending order 
    private List<Long> pruneUpdateList(Binder binder, List<Object[]>notBinders) {
    	 List<Long>ids = new ArrayList();
    	 //if present this binder will be the first
    	 if (!notBinders.isEmpty() && notBinders.get(0)[0].equals(binder.getId())) notBinders.remove(0);
    	 String previousKey=null;
    	 for (int i=0; i<notBinders.size(); ++i) {
    		 Object[] row = notBinders.get(i);
    		 if (i == 0) {
    			 ids.add((Long)row[0]);
    			 previousKey = (String)row[1];
    		 } else {
    			 String key = (String)row[1];
    			 if (key.startsWith(previousKey)) {
    				 //descendant that is redundant
    				 continue;
    			 } else {
    				 //new branch
    				 previousKey = key;
    				 ids.add((Long)row[0]);   					
    			 }
    		 }
    	 }
    	 return ids;
   
     }
     @Override
	public void indexOwner(Collection<Binder>binders, Long ownerId) {
  		String value = Constants.EMPTY_ACL_FIELD;
 		if (ownerId != null) value = ownerId.toString();
 		doFieldUpdate(binders, Constants.BINDER_OWNER_ACL_FIELD, value, false);     		
     }

     private void executeUpdateQuery(Criteria crit, String field, String value, Boolean runInBackground, boolean indexEntries) {
    	 executeUpdateQuery(crit, field, value, runInBackground, indexEntries, false);
     }
     
     private void executeUpdateQuery(Criteria crit, String field, String value, Boolean runInBackground, boolean indexEntries, boolean skipFileContentIndexing) {

		Map<String,Object> doc;
		List<Long> binders = new ArrayList<Long>();
		
		// flush anything that is waiting
		IndexSynchronizationManager.applyChanges();

		// Get a list of the binders which need to be reindexed
		LuceneReadSession luceneSessionn = getLuceneSessionFactory()
				.openReadSession();
		QueryBuilder qbb = new QueryBuilder(false, false); 
		
		crit.add(conjunction().add(
				eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER)));
		
		SearchObject so = qbb.buildQuery(crit.toQuery());
		
		Hits hits = luceneSessionn.search(RequestContextHolder.getRequestContext().getUserId(), so.getBaseAclQueryStr(), so.getExtendedAclQueryStr(), Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 
				so.getLuceneQuery(), SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
		luceneSessionn.close();
		for (int i = 0; i < hits.length(); i++) {
			doc = hits.doc(i);
			String binderId = (String) doc.get(Constants.DOCID_FIELD);
			if (binderId != null) {
				try {
					binders.add(Long.valueOf(binderId));
				} catch (Exception ignore) {
				}
			}
		}
		
		if(Boolean.FALSE.equals(runInBackground)) {
	    	for (Long id:binders) {
				try {
					getBinderModule().indexBinderIncremental(id, indexEntries, skipFileContentIndexing);
				} catch (NoObjectByTheIdException ex) {
					//gone, skip it
				} catch (Exception ex) {
					//try again
					logger.error(NLT.get("profile.titlechange.index.error") + " (binder " + id.toString() + ") " + ex.toString());
					continue;
				}
	    	}
		}
		else {
			// Setup the background job for reindexing
			User user = RequestContextHolder.getRequestContext().getUser();
			String className = SPropsUtil.getString("job.binder.reindex.class", "org.kablink.teaming.jobs.DefaultBinderReindex");
			BinderReindex job = (BinderReindex)ReflectHelper.getInstance(className);
			job.scheduleNonBlocking(binders, user, indexEntries); 
		}

	}
     
     // this will update the binder, its attachments and entries, and
		// subfolders and entries that inherit
     private void doFieldUpdate(Binder binder, List<Long>notBinderIds, String field, String value, Boolean runInBackground, boolean indexEntries) {
    	 doFieldUpdate(binder, notBinderIds, field, value, runInBackground, indexEntries, false);
     }
     
      private void doFieldUpdate(Binder binder, List<Long>notBinderIds, String field, String value, Boolean runInBackground, boolean indexEntries, boolean skipFileContentIndexing) {
 		// Now, create a query which can be used by the index update method to modify all the
		// entries, replies, attachments, and binders(workspaces) in the index 
 		Criteria crit = new Criteria()
  			.add(eq(Constants.ENTRY_ANCESTRY, binder.getId().toString()));
 		
		if (!notBinderIds.isEmpty()) {	
			crit.add(not()
				.add(in(Constants.ENTRY_ANCESTRY, LongIdUtil.getIdsAsStringSet(notBinderIds)))
			);
 		}
		executeUpdateQuery(crit, field, value, runInBackground, indexEntries, skipFileContentIndexing);
    }

    //this will update just the binder, its attachments and entries only
    private void doFieldUpdate(Binder binder, String field, String value, Boolean runInBackground, boolean indexEntries) {
    	doFieldUpdate(binder, field, value, runInBackground, indexEntries, false);
    }
    
  	//this will update just the binder, its attachments and entries only
     private void doFieldUpdate(Binder binder, String field, String value, Boolean runInBackground, boolean indexEntries, boolean skipFileContentIndexing) {
  		// Now, create a query which can be used by the index update method to modify all the
 		// entries, replies, attachments, and binders(workspaces) in the index 
    	//_binderId= OR (_docId= AND (_docType=binder OR _attType=binder)) 
    	Criteria crit = new Criteria()
    	.add(disjunction()
    		.add(eq(Constants.BINDER_ID_FIELD, binder.getId().toString())) // get all the entries, replies and their attachments using parentBinder
    		.add(conjunction()	//gt binder itself (_docId= AND (_docType=binder OR _attType=binder)) 
    			.add(eq(Constants.DOCID_FIELD, binder.getId().toString()))
    			.add(disjunction()
    				.add(eq(Constants.DOC_TYPE_FIELD,Constants.DOC_TYPE_BINDER))
    				.add(eq(Constants.ATTACHMENT_TYPE_FIELD, Constants.ATTACHMENT_TYPE_BINDER))
    			)
    		)
    	);
		executeUpdateQuery(crit, field, value, runInBackground, indexEntries, skipFileContentIndexing);
     }
 
    private void doFieldUpdate(Collection<Binder>binders, String field, String value, boolean indexEntries) {
     	if (binders.isEmpty()) return;
 		// Now, create a query which can be used by the index update method to modify all the
 		// entries, replies, attachments, and binders(workspaces) in the index 
       	//_binderId= OR (_docId= AND (_docType=binder OR _attType=binder)) 
		ArrayList<String>ids = new ArrayList();
		for (Binder b:binders) {
			ids.add(b.getId().toString());
		}
	   	Criteria crit = new Criteria()
    	.add(disjunction()
    		.add(in(Constants.BINDER_ID_FIELD, ids)) // get all the entries, replies and their attachments using parentBinder
    		.add(conjunction()   	// Get all the binder's themselves  (_docType=binder OR _attType=binder) AND (_docId= OR _docId= OR ... )    				  
    			.add(in(Constants.DOCID_FIELD, ids))
    			.add(disjunction()     					  
    				.add(eq(Constants.DOC_TYPE_FIELD,Constants.DOC_TYPE_BINDER))
    				.add(eq(Constants.ATTACHMENT_TYPE_FIELD, Constants.ATTACHMENT_TYPE_BINDER))
    			)
    		)
    	);
		executeUpdateQuery(crit, field, value, null, indexEntries);
     }
    
    private void doRssDelete(Binder binder) {
  		// Delete the rss index
    	this.getRssModule().deleteRssFeed(binder);
     }
 
    private void doRssUpdate(Binder binder) {
  		// Delete the rss index since it's invalid once you change the acls
    	this.getRssModule().deleteRssFeed(binder);
     }
 
    private void doRssUpdate(Collection<Binder>binders) {
     	if (binders.isEmpty()) return;
 		// Delete the rss index since it's invalid once you change the acls
 		
		for (Binder b:binders) {
			this.getRssModule().deleteRssFeed(b);
		}
     }

    //***********************************************************************************************************
    @Override
	public IndexErrors indexBinder(Binder binder, boolean includeEntries) {
    	//call overloaded methods
    	IndexErrors errors = indexBinder(binder, includeEntries, true, null);   
   		return errors;
    }
    
    @Override
	public IndexErrors indexBinderIncremental(Binder binder, boolean includeEntries) {
    	return indexBinderIncremental(binder, includeEntries, false);
    }
    
    @Override
	public IndexErrors indexBinderIncremental(Binder binder, boolean includeEntries, boolean skipFileContentIndexing) {
    	//call overloaded methods
    	IndexErrors errors = loadIndexTreeIncremental(binder, includeEntries, skipFileContentIndexing);   
   		return errors;
    } 
    
    @Override
	public IndexErrors indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags) {
    	IndexErrors errors = indexBinder(binder, null, null, !deleteIndex, tags);    	
   		return errors;
    	
    }
    
    @Override
	public IndexErrors indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags, boolean skipFileContentIndexing) {
    	// Ignore skipFileContentIndexing arg
    	return indexBinder(binder, includeEntries, deleteIndex, tags);
    }
    //***********************************************************************************************************
    //It is assumed that the index has been deleted for each binder to be index
    @Override
	public Collection indexTree(Binder binder, Collection exclusions) {
   		IndexErrors errors = new IndexErrors();
    	return loadIndexTree(binder, exclusions, StatusTicket.NULL_TICKET, errors, false);
    }
   	@Override
	public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket) {
   		IndexErrors errors = new IndexErrors();
   		return indexTree(binder, exclusions, statusTicket, errors);
   	}
   	@Override
	public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket, IndexErrors errors) {
   		return indexTree(binder, exclusions, statusTicket, errors, false);
   	}
   	@Override
	public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket, IndexErrors errors, boolean skipFileContentIndexing) {
   		return loadIndexTree(binder, exclusions, statusTicket, errors, skipFileContentIndexing);
   	}
   	private Collection loadIndexTree(Binder binder, Collection exclusions, StatusTicket statusTicket, IndexErrors errors, boolean skipFileContentIndexing) {
   		//get all the ids of child binders. order for statusTicket to make some sense
   		if(logger.isDebugEnabled())
   			logger.debug("Fetching IDs of all binders at or below this branch [" + binder.getPathName() + "] (id=" + binder.getId() + ")");
		Map params = new HashMap();
		params.put("deleted", false);
   		List<Long> ids = getCoreDao().loadObjects("select x.id from org.kablink.teaming.domain.Binder x where x.binderKey.sortKey like '" +
				binder.getBinderKey().getSortKey() + "%' and x.deleted=:deleted order by x.binderKey.sortKey", params);
   		if(logger.isDebugEnabled())
   	   		logger.debug("Identified " + ids.size() + " binders to index within the branch [" + binder.getPathName() + "] (id=" + binder.getId() + "): " + ids.toString());
   		else
   			logger.info("Identified " + ids.size() + " binders to index within the branch [" + binder.getPathName() + "] (id=" + binder.getId() + ")");
		int inClauseLimit=SPropsUtil.getInt("db.clause.limit", 1000);
		if (exclusions != null) {
			synchronized(exclusions) {
				ids.removeAll(exclusions);
			}
		}
		int bindersIndexed = 0;
		Long lastProcessedBinderId = null;
		params.clear();
		
    	if(statusTicket instanceof ConcurrentStatusTicket)
    		((ConcurrentStatusTicket)statusTicket).incrementTotalCount(ids.size());
				
		/*
		 * In order to use this hidden setting/capability, the following conditions must be understood and met.
		 * 1) Reindexing must be single threaded. If reindexing is performed by concurrent threads (which is
		 *    the default), then this capability can NOT be used.
		 * 2) Reindexing continuation is node-specific capability. It is managed on a per node basis.
		 *    Therefore, all subsequent reindexing attempts after initial crash must execute on the same Filr Appliance.
		 * 3) Reindexing continuation can be used only in conjunction with site wide reindexing.
		 *    It should not and can not be used for partial reindexing.
		 * 4) In order to be able to continue previously aborted reindexing, BOTH the previous and current runs
		 *    must be configured with this capability. In other word, you can't pick up failed previous reindexing
		 *    task that was performed with this capability disabled.
		 * 5) The system will not automatically pick up incomplete reindexing work after restart and continue.
		 *    Instead, site wide reindexing must be kicked off again from the admin console.
		 */
		boolean supportsReindexingContinuation = false;
		if(!(statusTicket instanceof ConcurrentStatusTicket))
			supportsReindexingContinuation = SPropsUtil.getBoolean("index.tree.supports.continuation", false);
		
		if(supportsReindexingContinuation) {
			// This is not an officially supported option. Nevertheless we have it as a last resort tool primarily to aid our support colleagues.
			Long lastCheckpointBinderId = readLastCheckpointBinderIdForReindexing();
			if(lastCheckpointBinderId != null) {
				// This means that last reindexing didn't complete. Skip over past the last checkpoint binder id.
				for(int i = 0; i < ids.size(); i++) {
					if(lastCheckpointBinderId.equals(ids.get(i))) {
						ids = ids.subList(i+1, ids.size());
						break;
					}
				}
				logger.info("Continuing past last checkpoint binder ID of " + lastCheckpointBinderId + ": After adjustment there are " + ids.size() + " remaining binders to index");
			}
		}

		for (int i=0; i<ids.size(); i+=inClauseLimit) {
			List<Long> subList = ids.subList(i, Math.min(ids.size(), i+inClauseLimit));
			params.put("pList", subList);
			if(logger.isDebugEnabled())
				logger.debug("Loading " + subList.size() + " binder objects in a batch. The ID of the first binder in this batch is " + subList.get(0));
			List<Binder> binders = getCoreDao().loadObjects("from org.kablink.teaming.domain.Binder x where x.id in (:pList) order by x.binderKey.sortKey", params);
			if(logger.isTraceEnabled())
				logger.trace("Bulk loading collections for " + binders.size() + " binders");
			// Bulk load associated collections for better performance
			getCoreDao().bulkLoadCollections(binders);
			List<EntityIdentifier> folderIds = new ArrayList();
			List<EntityIdentifier> workspaceIds = new ArrayList();
			List<EntityIdentifier> otherIds = new ArrayList();
			for (Binder e: binders) {
				if(EntityIdentifier.EntityType.folder.equals(e.getEntityType()))
					folderIds.add(e.getEntityIdentifier());
				else if(EntityIdentifier.EntityType.workspace.equals(e.getEntityType()))
					workspaceIds.add(e.getEntityIdentifier());
				else 
					otherIds.add(e.getEntityIdentifier());
			}
			
			Map tagMap = new HashMap();
			if(logger.isTraceEnabled())
				logger.trace("Loading tags for " + folderIds.size() + " folders");
			try {
				tagMap.putAll(getCoreDao().loadAllTagsByEntity(folderIds));
			}
			catch(Exception e) {
				logger.error("Error loading tags for folders", e);
			}
			if(logger.isTraceEnabled())
				logger.trace("Loading tags for " + workspaceIds.size() + " workspaces");
			try {
				tagMap.putAll(getCoreDao().loadAllTagsByEntity(workspaceIds));
			}
			catch(Exception e) {
				logger.error("Error loading tags for workspaces", e);
			}			
			if(logger.isTraceEnabled())
				logger.trace("Loading tags for " + otherIds.size() + " others");
			try {
				tagMap.putAll(getCoreDao().loadAllTagsByEntity(otherIds));
			}
			catch(Exception e) {
				logger.error("Error loading tags for others", e);
			}

			for (Binder b:binders) {
	   	    	BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(b, b.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
	   	    	if(statusTicket instanceof ConcurrentStatusTicket) {
	   	    		((ConcurrentStatusTicket)statusTicket).incrementCurrentCount();
					if(logger.isDebugEnabled())
						logger.debug("(" + (bindersIndexed+1) + ") Indexing binder [" + b.getPathName() + "] (id=" + b.getId() + ") - Progress (global estimate): " + statusTicket);	    
	   	    	}
	   	    	else {
	   	    		statusTicket.setStatus(NLT.get("index.indexingBinder", new Object[] {String.valueOf(bindersIndexed), String.valueOf(ids.size())}));		
					if(logger.isDebugEnabled())
						logger.debug("(" + (bindersIndexed+1) + ") Indexing binder [" + b.getPathName() + "] (id=" + b.getId() + ")");
	   	    	}
				
	   	    	Collection tags = (Collection)tagMap.get(b.getEntityIdentifier());
	   	    	IndexErrors binderErrors = processor.indexBinder(b, true, false, tags, skipFileContentIndexing);
	   	    	errors.add(binderErrors);
	   	    	
				if(logger.isTraceEnabled())
					logger.trace("Evicting tags and binder");
				
	   	    	getCoreDao().evict(tags);
	   	    	getCoreDao().evict(b);
	   	    	bindersIndexed++;
	   	    	lastProcessedBinderId = b.getId();
			}
						
			logger.info("Indexed " + bindersIndexed + " binders so far. The ID of the last processed binder is " + lastProcessedBinderId +
					((statusTicket instanceof ConcurrentStatusTicket)? " - Progress (global estimate): " + statusTicket : ""));	
			
			if(supportsReindexingContinuation) {
				if(logger.isDebugEnabled())
					logger.debug("Writing checkpoint binder ID of " + lastProcessedBinderId);
				writeLastCheckpointBinderIdForReindexing(lastProcessedBinderId);
			}
			
			// Periodically (i.e., after processing each batch) clear the entire session to avoid OutOfMemory error
			// caused by objects kept accumulating in the Hibernate session. It appears that evicting tags and binders
			// aren't enough for long running reindexing task.
			if(StatusTicket.NULL_TICKET != statusTicket) {
				// Clear the session ONLY IF this method is being called from administrative reindexing task.
				// This is clumsy hack, but a quickest work around for the problem reported in bug #878377.
				// If this hack turns out to be insufficient, we will think about a better solution then.
				getCoreDao().clear();
			}
			
			if(logger.isTraceEnabled())
				logger.trace("Applying changes to index");
	  		IndexSynchronizationManager.applyChanges(SPropsUtil.getInt("lucene.flush.threshold", 100));
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Applying remaining changes to index if any");
		IndexSynchronizationManager.applyChanges();
		
		if(supportsReindexingContinuation) {
	    	// Now that the entire processing completed successfully, there's no need for checkpoint. Remove it.
	    	removeLastCheckpointForReindexing();
		}
		
    	if(logger.isDebugEnabled())
			logger.debug("Indexed " + ids.size() + " binders in the branch [" + binder.getPathName() + "] (id=" + binder.getId() + ")");
		
   		return ids;
   	}

   	private IndexErrors loadIndexTreeIncremental(Binder binder, boolean includeEntries, boolean skipFileContentIndexing) {
		IndexErrors errors = new IndexErrors();
		// load the binder
		Map params = new HashMap();
		params.put("pList", binder.getId());
		List<Binder> binders = getCoreDao()
				.loadObjects(
						"from org.kablink.teaming.domain.Binder x where x.id in (:pList) order by x.binderKey.sortKey",
						params);
		getCoreDao().bulkLoadCollections(binders);
		EntityIdentifier entityId = binder.getEntityIdentifier();

		Collection tags = getCoreDao().loadAllTagsByEntity(entityId);

		for (Binder b : binders) {
			if (b.isDeleted())
				continue;
			BinderProcessor processor = (BinderProcessor) getProcessorManager()
					.getProcessor(b,
							b.getProcessorKey(BinderProcessor.PROCESSOR_KEY));

			IndexErrors binderErrors = null;
			if (processor instanceof AbstractEntryProcessor) {
				binderErrors = ((AbstractEntryProcessor) processor)
						.indexBinderIncremental(b, includeEntries, false, tags, skipFileContentIndexing);
			} else {
				binderErrors = processor.indexBinder(b, includeEntries, false, tags);
			}
			errors.add(binderErrors);
			getCoreDao().evict(tags);
			// 05/30/2015 JK (bug #932689) - Do NEVER evict the loaded binder here!! 
			// It effectively causes the input binder to this method to be evicted
			// out of the session without the caller knowing about it. If the caller
			// attempts any subsequent state-changing operation around the now-detached
			// binder object, then this tends to result in the infamous "a different 
			// object with the same identifier value was already associated with the 
			// session" Hibernate object. Here's a concrete example:
			// The call to importSettingsList() in ExportHelper.binder_addBinderWithXML()
			// method eventually calls this method, and the input binder passed to this
			// call stack gets evicted from the session in secrecy. And then, the call
			// to importWorkflows() in the same method calls BinderModule.setDefinitions()
			// which then attempts to load the same binder before making modification.
			// Because the previous instance is now detached from the session, the code
			// ends up loading a different instance of the binder through the session.
			// At a later point, some change is made to the binder's parent binder, and
			// due to the defined Hibernate cascade settings, the change is cascaded to
			// the child binder (which is the binder evicted), and Hibernate finds that
			// it has another instance of the binder associated with the same session,
			// hence raising this infamous org.hibernate.NonUniqueObjectException (which
			// maps to org.springframework.dao.DuplicateKeyException). Needless to say,
			// this is one of those extremely difficult bug to track down.
			//getCoreDao().evict(b);
		}
		IndexSynchronizationManager.applyChanges(SPropsUtil.getInt(
				"lucene.flush.threshold", 100));

		return errors;

	}

   	//Routine to validate the quota data for all binders
   	//  Builds a map of every binder, its quota data, and its parent
   	//  Then walks that tree validating the data
	public class QuotaData {
		private Long diskSpaceUsed;
		private Long diskSpaceUsedCumulative;
		private Long newDiskSpaceUsedCumulative;
		private Long parentBinderId;
		public QuotaData(Long diskSpaceUsed, Long diskSpaceUsedCumulative, Binder binder) {
			this.diskSpaceUsed = diskSpaceUsed;
			this.diskSpaceUsedCumulative = diskSpaceUsedCumulative;
			this.parentBinderId = null;
			if (binder.getParentBinder() != null) {
				this.parentBinderId = binder.getParentBinder().getId();
			}
			this.newDiskSpaceUsedCumulative = null;
		}
	}
    @Override
	public Collection validateBinderQuotasTree(Binder binder, StatusTicket statusTicket, 
    		List<Long> errors) {
    	final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	final List<Long> binderQuotasToUpdate = new ArrayList<Long>();
    	final Map<Long,QuotaData> binderQuotasMap = new HashMap<Long,QuotaData>();
    	final Set<Long> binderParents = new HashSet<Long>();
    	
   		//get all the ids of child binders. order for statusTicket to make some sense
   		List<Long> ids = getCoreDao().loadObjects("select x.id from org.kablink.teaming.domain.Binder x where x.binderKey.sortKey like '" +
				binder.getBinderKey().getSortKey() + "%' order by x.binderKey.sortKey", null);
		int inClauseLimit=SPropsUtil.getInt("db.clause.limit", 1000);
		Map params = new HashMap();
		for (int i=0; i<ids.size(); i+=inClauseLimit) {
			List subList = ids.subList(i, Math.min(ids.size(), i+inClauseLimit));
			params.put("pList", subList);
			List<Binder> binders = getCoreDao().loadObjects("from org.kablink.teaming.domain.Binder x where x.id in (:pList) order by x.binderKey.sortKey", params);
			getCoreDao().bulkLoadCollections(binders);
			List<EntityIdentifier> folderIds = new ArrayList();
			List<EntityIdentifier> workspaceIds = new ArrayList();
			List<EntityIdentifier> otherIds = new ArrayList();
			for (Binder e: binders) {
				if(EntityIdentifier.EntityType.folder.equals(e.getEntityType()))
					folderIds.add(e.getEntityIdentifier());
				else if(EntityIdentifier.EntityType.workspace.equals(e.getEntityType()))
					workspaceIds.add(e.getEntityIdentifier());
				else 
					otherIds.add(e.getEntityIdentifier());
			}

			//For this chunk of binders, calculate the disk space usage
			for (Binder b:binders) {
				if (b.isDeleted()) continue;
				//Build a set of all binders that are parents. This is used later to find end nodes of the tree
				if (b.getParentBinder() != null) binderParents.add(b.getParentBinder().getId());
	   	    	statusTicket.setStatus(NLT.get("validate.binderQuota.status", 
	   	    			new Object[] {String.valueOf(binderQuotasMap.size()), String.valueOf(ids.size()), String.valueOf(errors.size())}));
	   	    	
	   	    	Long dsu = getCoreDao().computeDiskSpaceUsed(zoneId, b.getId());
	   	    	BinderQuota bq = null;
	   	    	QuotaData qd = null;
	   	    	try {
	   	    		bq = getCoreDao().loadBinderQuota(zoneId, b.getId());
	   	    		qd = new QuotaData(dsu, bq.getDiskSpaceUsedCumulative(), b);
	   	    	} catch(NoObjectByTheIdException e) {
	   	    		binderQuotasToUpdate.add(b.getId());
	   	    		qd = new QuotaData(dsu, Long.valueOf(0), b);
	   	    	}
   	    		binderQuotasMap.put(b.getId(), qd);
	   	    	if (bq != null && !dsu.equals(bq.getDiskSpaceUsed())) {
	   	    		//This disk space used value was wrong, so add it to the fix list
	   	    		binderQuotasToUpdate.add(b.getId());
		   	    	errors.add(b.getId());
	   	    	}
	   	    	getCoreDao().evict(b);
			}
			//Next, make sure each of these binders has a BinderQuota row in the database
			if (!binderQuotasToUpdate.isEmpty()) {
				SimpleProfiler.start("validateBinderQuotasTree");
		        // The following part requires update database transaction.
		        getTransactionTemplate().execute(new TransactionCallback() {
		        	@Override
					public Object doInTransaction(TransactionStatus status) {
		        		//Go through all binders to validate and update the disk space used values
		        		//  This step makes sure each binder has a BinderQuota in the database
		        		for (Long binderId : binderQuotasToUpdate) {
		        			QuotaData qd = binderQuotasMap.get(binderId);
		    	   	    	BinderQuota bq = null;
		    	   	    	try {
		    	   	    		bq = getCoreDao().loadBinderQuota(zoneId, binderId);
		    	   	    		if (!bq.getDiskSpaceUsed().equals(qd.diskSpaceUsed)) {
		    	   	    			//The disk space used value was different, so update it
		    	   	    			bq.setDiskSpaceUsed(qd.diskSpaceUsed);
		    	   	    			getCoreDao().save(bq);
		    	   	    		}
		    	   	    	} catch(NoObjectByTheIdException e) {
		    	   	    		bq = new BinderQuota();
		    	   	    		bq.setZoneId(zoneId);
		    	   	    		bq.setBinderId(binderId);
		    	   	    		bq.setDiskSpaceUsed(qd.diskSpaceUsed);
		    	   	    		getCoreDao().save(bq);
		    	   	    	}
		        		}
		                return null;
		        	}
		        });
		        SimpleProfiler.stop("validateBinderQuotasTree");
			}
		}
		//At this point, all binders in the zone have been analyzed and a complete map exists
		//Now, walk up the tree from each end node and calculate the cumulative values
        for (Long binderId : binderQuotasMap.keySet()) {
        	//Is this an end node?
        	if (!binderParents.contains(binderId)) {
        		//This is not a parent of any binder, so it must be an end node
        		QuotaData qd = binderQuotasMap.get(binderId);
        		qd.newDiskSpaceUsedCumulative = qd.diskSpaceUsed;
        		Long increment = qd.diskSpaceUsed; 
        		while (qd.parentBinderId != null) {
        			//Walk up the tree to the top adding up the space used
        			qd = binderQuotasMap.get(qd.parentBinderId);
        			if (qd.newDiskSpaceUsedCumulative == null) {
        				//This is the first time seeing this binder, so add in the binder's own usage
        				increment += qd.diskSpaceUsed;
        				qd.newDiskSpaceUsedCumulative = Long.valueOf(0);
        			}
        			qd.newDiskSpaceUsedCumulative += increment;
        		}
        	}
        }
        //Now, the map is updated with new cumulative counts, look for changes to be written to the database
        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	@Override
			public Object doInTransaction(TransactionStatus status) {
        		//Go through all binders to validate and update the disk space used values
        		//  This step makes sure each binder has a BinderQuota in the database
        		for (Long binderId : binderQuotasMap.keySet()) {
        			QuotaData qd = binderQuotasMap.get(binderId);
        			if (!qd.newDiskSpaceUsedCumulative.equals(qd.diskSpaceUsedCumulative)) {
	    	   	    	//The cumulative count was different, so this must be refected in the database
        				BinderQuota bq = null;
	    	   	    	try {
	    	   	    		bq = getCoreDao().loadBinderQuota(zoneId, binderId);
	    	   	    		bq.setDiskSpaceUsedCumulative(qd.newDiskSpaceUsedCumulative);
	    	   	    	} catch(NoObjectByTheIdException e) {
	    	   	    		bq = new BinderQuota();
	    	   	    		bq.setZoneId(zoneId);
	    	   	    		bq.setBinderId(binderId);
	    	   	    		bq.setDiskSpaceUsed(qd.diskSpaceUsed);
	    	   	    		bq.setDiskSpaceUsedCumulative(qd.newDiskSpaceUsedCumulative);
	    	   	    		getCoreDao().save(bq);
	    	   	    	}
        			}
        		}
        		return null;
        	}
        });

        return ids;

    }

    //Routine to calculate the aging date for each file in a binder
    @Override
	public void setFileAgingDates(Binder binder) {
    	//Nothing to be done here. It is all done in FolderCoreProcessor
    }
   	
    //Routine to see if this binder is empty
    @Override
	public boolean isFolderEmpty(final Binder binder) {
    	return true;
    }
   	
    // ***********************************************************************************************************
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
    protected IndexErrors indexBinder(Binder binder, List fileUploadItems, 
    		Collection<FileAttachment> filesToIndex, boolean newEntry, Collection tags) {
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

    	return indexBinderWithAttachments(binder, binder.getFileAttachments(), fileUploadItems, newEntry, tags);
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
	protected IndexErrors indexBinderWithAttachments(Binder binder,
			Collection<FileAttachment> fileAttachments, List fileUploadItems, boolean newEntry, Collection tags) {
		IndexErrors errors = new IndexErrors();
		if(SPropsUtil.getBoolean("indexing.escalate.add.to.update", true))
			newEntry = false;
		
		if(!newEntry) {
			// This is modification. We must first delete existing document(s) from the index.
			indexDeleteBinder(binder);	        
		}
		
        // Create an index document from the entry object.
		org.apache.lucene.document.Document indexDoc;
		if (tags == null) tags = getCoreDao().loadAllTagsByEntity(binder.getEntityIdentifier());
		try {
			indexDoc = buildIndexDocumentFromBinder(binder, tags);
		} catch(Exception e) {
			//An error occurred, increment the error count and return. No more can be done.
			logger.error("Error indexing binder " + binder, e);
			errors.addError(binder);
			return errors;
		}

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
        		logger.error("Error indexing file for binder " + binder + " attachment" + fa, ex);
        		errors.addError(binder);
        	}
        }
        return errors;
	}

    protected org.apache.lucene.document.Document buildIndexDocumentFromBinder(Binder binder, Collection tags) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        boolean fieldsOnly = false;
    	fillInIndexDocWithCommonPartFromBinder(indexDoc, binder, false);
        // Add creation-date and modification date from binder
    	// Not in common part, cause files use different dates
        EntityIndexUtils.addCreation(indexDoc, binder.getCreation(), fieldsOnly);
        EntityIndexUtils.addModification(indexDoc, binder.getModification(), fieldsOnly);
        EntityIndexUtils.addOwner(indexDoc, binder.getOwner(), fieldsOnly);
        
        //index parentBinder - used to locate sub-binders - attachments shouldn't need this
        EntityIndexUtils.addParentBinder(indexDoc, binder, fieldsOnly);

    	// Add search document type
        BasicIndexUtils.addDocType(indexDoc, Constants.DOC_TYPE_BINDER, fieldsOnly);
        //used to answer what teams am I a member of
       	if (!binder.isTeamMembershipInherited()) EntityIndexUtils.addTeamMembership(indexDoc, getBinderModule().getTeamMemberIds( binder ), fieldsOnly);

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
		(Binder binder, FileAttachment fa, FileUploadItem fui, Collection tags) {
       	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
       	//do common part first.  Indexing a file will remove some of the items
       	fillInIndexDocWithCommonPartFromBinder(indexDoc, binder, true);
        BasicIndexUtils.addAttachmentType(indexDoc, Constants.ATTACHMENT_TYPE_BINDER, true);

  	  	buildIndexDocumentFromFile(indexDoc, binder, binder, fa, tags, false, false);
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
    	(org.apache.lucene.document.Document indexDoc, Binder binder, DefinableEntity entity, FileAttachment fa, Collection tags, boolean isLibraryFile, boolean skipFileContentIndexing) {

		String text = "";
		//See if the file contents are supposed to be indexed
		//The root folder of a folder chain dictates if file contents are to be indexed
		Binder rootFolder = AccessUtils.getRootFolder(entity);
		boolean rootFolderIsNetFolder = false;
		if(rootFolder != null && rootFolder.isMirrored() && rootFolder.getResourceDriver() instanceof AclResourceDriver)
			rootFolderIsNetFolder = true;
		Field contentIndexTypeField;
		// We include file content in the index here IF AND ONLY IF
		// 	1. The file is associated with an entity that is not a folder entry (e.g. user or binder)
		//		OR
		//	2. The caller specifies to include file content AND (The file is adhoc file OR (The file is net folder file AND The net folder has content indexing enabled))   
		if(!(entity instanceof FolderEntry) ||
				(!skipFileContentIndexing && 
				(rootFolder == null || 
				!rootFolderIsNetFolder ||
				(rootFolderIsNetFolder && rootFolder.getComputedIndexContent())))) {
			//The file contents of files in this folder are to be added to the index
			// Get the Text converter from manager
	    	TextConverter       fileConverter   = null;
	    	TextStreamConverter streamConverter = null;
			boolean useStreamConverter = false;	//! SPropsUtil.getBoolean("use.stream.text.converter", false);
			/*
			 * *** Warning *** Warning *** Warning *** Warning *** Warning ***
			 * ***                                                         ***
			 * *** The Tika text converter as it currently stands does NOT ***
			 * *** work!  The only dependencies for it that we're bringing ***
			 * *** in are tika-core.jar and tika-parsers.jar.  In order    ***
			 * *** for it to work, we need to use tika-app.jar instead     ***
			 * *** (which brings in dependencies that break other things)  ***
			 * *** or track down and bring in all the dependencies of      ***
			 * *** tika-parsers.jar (with Tika 1.8, there were 78 of       ***
			 * *** them.)                                                  ***  
			 * ***                                                         ***  
			 * *** Warning *** Warning *** Warning *** Warning *** Warning ***  
			 */
			if (useStreamConverter)
			     streamConverter = textStreamConverterManager.getConverter();	// Uses Apache Tika.
			else fileConverter   = textConverterManager.getConverter();			// Uses Oracle OIT or OpenOffice.
			try {
				if (useStreamConverter) {
					InputStream is = getFileModule().readFile(binder, entity, fa);
					if (null != is) {
						text = streamConverter.convert(fa.getFileItem().getName(), is, "");
					}
				}
				else {
					text = fileConverter.convert(binder, entity, fa);
				}
			}
			catch (Exception e) {
				// Most likely conversion did not succeed, nothing client
				// can do about this limitation of software.
				String eMsg = e.toString();
				logger.error("AbstractBinderProcessor.buildIndexDocumentFromFile( EXCEPTION:1 ):  " + eMsg);
				if (logger.isDebugEnabled()) {
					logger.debug("AbstractBinderProcessor.buildIndexDocumentFromFile( EXCEPTION:1:STACK )", e);
				}
			}
			// Indicate that file content is being indexed.
            contentIndexTypeField = FieldFactory.createFieldStoredNotAnalyzed(Constants.CONTENT_INDEXED_FIELD, Constants.TRUE);
		} else {
			//The text contents are not added
	        contentIndexTypeField = FieldFactory.createFieldStoredNotAnalyzed(Constants.CONTENT_INDEXED_FIELD, Constants.FALSE);
		}
        indexDoc.add(contentIndexTypeField);
			
		// Is there a relevance engine engine enabled?
    	Relevance relevanceEngine = getRelevanceManager().getRelevanceEngine();
    	if (relevanceEngine.isRelevanceEnabled()) {
			try {
				// Yes!  Can it generate a relevance UUID for this
				// file?
				String relevanceUUID = relevanceEngine.addAttachment(binder, entity, fa);
				if (MiscUtil.hasString(relevanceUUID)) {
					// Yes!  Add it to the search index for the file.
					text += (" " + RelevanceUtils.getSearchableUUID(relevanceUUID));
				}
			}
			catch (Exception e) {
				// Most likely relevance generation did not succeed,
				// nothing client can do about this limitation of software.
				logger.error("AbstractBinderProcessor.buildIndexDocumentFromFile( EXCEPTION:2 ):  ", e);
			}
    	}
		
    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, Constants.DOC_TYPE_ATTACHMENT, true);
        
        // Add file info
        EntityIndexUtils.addFileAttachment(indexDoc, fa, true);

        if (isLibraryFile) {
            Field libraryField = FieldFactory.createFieldStoredNotAnalyzed(Constants.IS_LIBRARY_FIELD, Boolean.toString(true));
            indexDoc.add(libraryField);
        }

        // Add creation-date from entity
        EntityIndexUtils.addCreation(indexDoc, entity.getCreation(), true);
        // Add modification-date from file
        EntityIndexUtils.addModification(indexDoc, fa.getModification(), true);
        
        if(text != null) BasicIndexUtils.addFileContents(indexDoc, text);
        
        // Add the tags for this entry
        if (tags == null) tags =  getCoreDao().loadAllTagsByEntity(entity.getEntityIdentifier());
        EntityIndexUtils.addTags(indexDoc, entity, tags, true);
        
        //needs to be last item cause removes extraneous generaltext fields
   		EntityIndexUtils.addFileAttachmentGeneralText(indexDoc);
   
    }
    
    //add common fields from binder for binder and its attachments
    protected void fillInIndexDocWithCommonPartFromBinder(org.apache.lucene.document.Document indexDoc, 
    		Binder binder, boolean fieldsOnly) {
    	EntityIndexUtils.addReadAccess(indexDoc, binder, fieldsOnly, true);

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
        
        // Add PreDeletedField (i.e., entity is in the trash.)
        EntityIndexUtils.addPreDeletedFields(indexDoc, entity, fieldsOnly);
        
        // Add DefinitionType
        EntityIndexUtils.addDefinitionType(indexDoc, entity, fieldsOnly);
 
        // Add command definition
        EntityIndexUtils.addCommandDefinition(indexDoc, entity, fieldsOnly);
       
        // Add command definition
        EntityIndexUtils.addCreatedWithDefinition(indexDoc, entity, fieldsOnly);
       
        // Add command definition
        EntityIndexUtils.addEntryDefinitions(indexDoc, entity, fieldsOnly);
       
        // Add definition family
        EntityIndexUtils.addFamily(indexDoc, entity, fieldsOnly);
       
        // Add ancestry 
        EntityIndexUtils.addAncestry(indexDoc, entity, fieldsOnly);
        
    	EntityIndexUtils.addResourceDriverName( indexDoc, entity, fieldsOnly );

    	EntityIndexUtils.addNetFolderResourcePath( indexDoc, entity, fieldsOnly );

        if (entity instanceof Binder) {
            //Add binder path
        	EntityIndexUtils.addBinderPath(indexDoc, (Binder) entity, fieldsOnly);
        	EntityIndexUtils.addBinderIconName(indexDoc, (Binder) entity, fieldsOnly);
        	EntityIndexUtils.addBinderIsLibrary(indexDoc, (Binder) entity, fieldsOnly);
        	EntityIndexUtils.addBinderIsMirrored(indexDoc, (Binder) entity, fieldsOnly);
        	EntityIndexUtils.addBinderIsHomeDir(indexDoc, (Binder) entity, fieldsOnly);
        	EntityIndexUtils.addBinderIsMyFilesDir(indexDoc, (Binder) entity, fieldsOnly);
        	EntityIndexUtils.addBinderHasResourceDriver(indexDoc, (Binder) entity, fieldsOnly);
        	EntityIndexUtils.addBinderIsTopFolder(indexDoc, (Binder) entity, fieldsOnly);
        	EntityIndexUtils.addBinderCloudFolderInfo( indexDoc, (Binder) entity, fieldsOnly );
        	if (entity instanceof Folder) {
               	EntityIndexUtils.addFolderFileTime( indexDoc, (Folder) entity, fieldsOnly );
        	}
        }
                
        if(entity.supportsCustomFields()) {
	        // Add data fields driven by the entry's definition object. 
			DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
				@Override
				public void visit(Element entryElement, Element flagElement, Map args)
				{
	                if (flagElement.attributeValue("apply").equals("true")) {
	                	String fieldBuilder = flagElement.attributeValue("fieldBuilder");
	                	String excludeFromSearchIndex = DefinitionUtils.getPropertyValue(entryElement, "excludeFromSearchIndex");
	                	if (excludeFromSearchIndex == null || !excludeFromSearchIndex.equals("true")) {
							String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
							if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
							args.put(DefinitionModule.DEFINITION_ELEMENT, entryElement);
		                	Field[] fields = FieldBuilderUtil.buildField(entity,
		                         nameValue, fieldBuilder, args);

		                	// Did we build any Field's?
		                	if ((fields != null) && (0 < fields.length)) {
		                		// Yes!  Are we working with numeric data?
			                	String  eeType    = entryElement.attributeValue("type");
			                	boolean isNumeric = ((null != eeType) && eeType.equals("data"));
			                	if (isNumeric) {
				                	eeType    = entryElement.attributeValue("dataType");
				                	isNumeric = ((null != eeType) && eeType.equals("number"));
			                	}
			                	
		                		// Scan the Field's.
		                		for (int i = 0; i < fields.length; i++) {
		                			// If we're indexing a numeric
		                			// field...
		                			Field     field      = fields[i];
		                			Fieldable indexField = field;
		                			if (isNumeric) {
		                				// ...and we can map to a
		                				// ...NumericField equivalent...
		                				NumericField nf = FieldBuilderUtil.mapBasicFieldToNumericField(field);
		                				if (null != nf) {
		                					// ...we'll add that to the
		                					// ...index instead of the
		                					// ...basic Field.
		                					indexField = nf;
		                				}
		                			}
		                			
		                			// Add the appropriate field to
		                			// the index document.
	                				indexDoc.add(indexField);
		                		}
		                    }
	                	}
	                }
				}
				@Override
				public String getFlagElementName() { return "index"; }
			};
			if (!fieldsOnly) {
				getDefinitionModule().walkDefinition(entity, visitor, null);
			} else {
				getDefinitionModule().walkDefinition(entity, visitor, fieldsOnlyIndexArgs);			
			}
        }
        else {
        	// Short circuit the use of definition facility which is slow and expensive.
        	// No need to process title here since it is handled somewhere else.
        	// Process description field.
        	Field[] descFields = FieldBuilderUtil.buildField(entity, ObjectKeys.FIELD_ENTITY_DESCRIPTION, "org.kablink.teaming.module.definition.index.FieldBuilderDescription", Collections.EMPTY_MAP);
        	if(descFields != null)
        		for(Field descField:descFields)
        			indexDoc.add(descField);
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
    			Constants.FILE_ID_FIELD, fa.getId()));  	
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
	protected void editFileComments(DefinableEntity entity, InputDataAccessor inputData) {
		//See if there are comment fields to be edited
		Set<FileAttachment> files = entity.getFileAttachments();
		for (FileAttachment f : files) {
			if (inputData.exists("ss_attachFile" + f.getId() + ".description")) {
				String newText = inputData.getSingleValue("ss_attachFile" + f.getId() + ".description");
				if (!f.getFileItem().getDescription().getText().equals(newText)) {
					f.getFileItem().getDescription().setText(newText);
				}
			}
		}
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
	
	@Override
	public ChangeLog processChangeLog(Binder binder, String operation) {
		return processChangeLog(binder, operation, false);
	}
	
	@Override
	public ChangeLog processChangeLog(Binder binder, String operation, boolean skipDbLog) {
		ChangeLog changes = null;

		// (bug #937273) 8/3/2015 JK - If this code is called during server startup
		// (e.g. as part of system upgrade), do not create change log. 
		if (ContextListenerPostSpring.isStartupInProgress() || !getAdminModule().isChangeLogEnabled()) {
			//Don't build the ChangeLog object if it isn't going to be logged
			return null;
		}

		if(!skipDbLog) {
			//any changes here should be considered to template export
			changes = ChangeLogUtils.createAndBuild(binder, operation);
			Element element = changes.getEntityRoot();
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_BINDER_NAME, ObjectKeys.XTAG_TYPE_STRING, binder.getName());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_LIBRARY, binder.isLibrary());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITFUNCTIONMEMBERSHIP, binder.isFunctionMembershipInherited());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITDEFINITIONS, binder.isDefinitionsInherited());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITTEAMMEMBERS, binder.isTeamMembershipInherited());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_UNIQUETITLES, binder.isUniqueTitles());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_TEAMMEMBERS, LongIdUtil.getIdsAsString( getBinderModule().getTeamMemberIds( binder )));
			if (!binder.isFunctionMembershipInherited()) {
				List<WorkAreaFunctionMembership> wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
						binder.getZoneId(), binder);
				for (WorkAreaFunctionMembership wfm: wfms) {
					wfm.addChangeLog(element);
				}
			}
			if (operation.equals(ChangeLog.DELETEBINDER) || operation.equals(ChangeLog.PREDELETEBINDER)) {
				//Add the path so it can be shown in the activity reports
				XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_PATH, binder.getPathName());
			}
			ChangeLogUtils.save(changes);
		}
		
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
    	return Constants.CREATORID_FIELD;
    }
	
	protected void checkInputFileNames(List fileUploadItems, FilesErrors errors) {
		//name must be unique within DefinableEntity
		for (int i=0; i<fileUploadItems.size(); ++i) {
			FileUploadItem fui1 = (FileUploadItem)fileUploadItems.get(i);
            if (Validator.containsPathCharacters(fui1.getOriginalFilename())) {
                errors.addProblem(new FilesErrors.Problem(null,
                        fui1.getOriginalFilename(), FilesErrors.Problem.PROBLEM_ILLEGAL_CHARACTER));
            } else {
                for (int j=i+1; j<fileUploadItems.size(); ) {
                    FileUploadItem fui2 = (FileUploadItem)fileUploadItems.get(j);
                    if (fui1.getOriginalFilename().equalsIgnoreCase(fui2.getOriginalFilename()) &&
                            !fui1.getRepositoryName().equals(fui2.getRepositoryName())) {
                        fileUploadItems.remove(j);
                        errors.addProblem(new FilesErrors.Problem(null,
                                fui1.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS));
                    } else ++j;
                }
            }
		}
	}
    protected void checkRenameFileNames(Map<FileAttachment, String> fileRenamesTo) {
        if (fileRenamesTo!=null) {
            for (String newName : fileRenamesTo.values()) {
                if (Validator.containsPathCharacters(newName)) {
                    throw new IllegalCharacterInNameException("errorcode.illegalCharacterInName", new Object[]{newName});
                }
            }
        }
    }

    protected void processCreationTimestamp(DefinableEntity entity, Map options) {
		User user;
		if (options != null && (
				options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) ||
				options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_ID) ||
				options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_NAME))) {
			Calendar date = (Calendar)options.get(ObjectKeys.INPUT_OPTION_CREATION_DATE);
			Long id = (Long)options.get(ObjectKeys.INPUT_OPTION_CREATION_ID);
			String name = (String)options.get(ObjectKeys.INPUT_OPTION_CREATION_NAME);
			processCreationTimestamp(entity, date, id, name);
		} else {
			entity.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
		}
	}
	protected void processCreationTimestamp(DefinableEntity entity, Calendar date, Long id, String name) {
		User user;
		if(id != null) { // id specified
			user = getProfileDao().loadUser(id, RequestContextHolder.getRequestContext().getZoneId());
		}
		else if(Validator.isNotNull(name)) { // name specified
			user = getProfileDao().findUserByName(name, RequestContextHolder.getRequestContext().getZoneId());				
		}
		else { // neither id nor name specified
			user = RequestContextHolder.getRequestContext().getUser();				
		}
		entity.setCreation(new HistoryStamp(user, (date != null)? date.getTime():new Date()));
	}

	protected void processModificationTimestamp(DefinableEntity entity, HistoryStamp fallback, Map options) {
		User user;
		if (options != null && Boolean.TRUE.equals(options.get(ObjectKeys.INPUT_OPTION_NO_MODIFICATION_DATE))) return;
		if (options != null && options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)) {
			Calendar date = (Calendar)options.get(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE);
			Long id = (Long)options.get(ObjectKeys.INPUT_OPTION_MODIFICATION_ID);
			String name = (String)options.get(ObjectKeys.INPUT_OPTION_MODIFICATION_NAME);
			processModificationTimestamp(entity, date, id, name);
		} else if (fallback != null) {
			entity.setModification(fallback);
		} else {
			entity.setModification(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));			
		}
	}
	protected void processModificationTimestamp(DefinableEntity entity, Calendar date, Long id, String name) {
		User user;
		if(id != null) { // id specified
			user = getProfileDao().loadUser(id,  RequestContextHolder.getRequestContext().getZoneId());
		}
		else if(Validator.isNotNull(name)) { // name specified
			user = getProfileDao().findUserByName(name, RequestContextHolder.getRequestContext().getZoneId());				
		}
		else { // neither id nor name specified
			user = RequestContextHolder.getRequestContext().getUser();				
		}
		entity.setModification(new HistoryStamp(user, date.getTime()));
	}
	
    @Override
	public void updateParentModTime(final Binder parentBinder, Map options, boolean reindex) {
		if(parentBinder != null && 
				parentBinder.getInternalId() == null &&
				!(options != null && Boolean.TRUE.equals(options.get(ObjectKeys.INPUT_OPTION_SKIP_PARENT_MODTIME_UPDATE)))) {
    		// Set the modification time to the "current" time.
			parentBinder.getModification().setDate(new Date());
			if (reindex) {
				// Reindex
				indexBinder(parentBinder, false);
			}
		}
	}
    
    @Override
	public void updateParentModTime(final Binder parentBinder, Map options) {
    	// Always use the initial form of the method.
    	updateParentModTime(parentBinder, options, true);
    }
    
    private Long readLastCheckpointBinderIdForReindexing() {
    	try {
	    	File file = getLastCheckpointFile();
	    	if(!file.exists()) 
	    		return null;
	    	String str = FileHelper.readString(file.getPath(), Charset.forName("UTF-8"));
	    	if(Validator.isNull(str))
	    		return null;
	    	return Long.valueOf(str);
    	}
    	catch(Exception e) {
    		return null;
    	}
    }
    
    private void writeLastCheckpointBinderIdForReindexing(Long binderId) {
    	File file = getLastCheckpointFile();
    	try (PrintWriter out = new PrintWriter(file)) {
    		out.print(binderId.toString());
    	} catch (Exception e) {
    		logger.warn("Failed to write last checkpoint binder ID of " + binderId, e);
    	}
    }
    
    private void removeLastCheckpointForReindexing() {
    	try {
	    	File file = getLastCheckpointFile();
	    	if(!file.exists())
	    		return;
	    	FileHelper.delete(file);
    	}
    	catch(Exception e) {
    		logger.warn("Failed to remove last checkpoint", e);
    	}
    }
    
    private File getLastCheckpointFile() {
    	String filePath = DirPath.getWebinfTmpDirPath() + File.separator + "reindexing_" + NetworkUtil.getLocalHostIPv4Address() + "_" + RequestContextHolder.getRequestContext().getZoneId();
    	return new File(filePath);
    }

}
