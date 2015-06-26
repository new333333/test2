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
package org.kablink.teaming.module.folder.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;

import org.hibernate.exception.LockAcquisitionException;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.FolderHierarchyException;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NotifyStatus;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.AbstractEntryProcessor;
import org.kablink.teaming.module.file.FilesErrors;
import org.kablink.teaming.module.file.FilterException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.folder.index.IndexUtils;
import org.kablink.teaming.module.folder.processor.FolderCoreProcessor;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.ChangeLogUtils;
import org.kablink.teaming.module.shared.EntryBuilder;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.CollectionUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ServerTaskLinkage;
import org.kablink.util.Validator;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.sitescape.team.domain.Statistics;

/**
 * ?
 * 
 * @author Jong Kim
 */
@SuppressWarnings("unchecked")
public abstract class AbstractFolderCoreProcessor extends AbstractEntryProcessor 
	implements FolderCoreProcessor {
  //***********************************************************************************************************	
    //inside write transaction
    @Override
	protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
    	Folder folder = (Folder)binder;
    	FolderEntry fEntry = (FolderEntry)entry;
    	//lock the parent binder = to reduce optimistic lock exceptions
    	//this is needed to setup sortkey
        getCoreDao().lock(binder);
        folder.addEntry((FolderEntry)entry);
    	if (inputData.exists(ObjectKeys.INPUT_FIELD_POSTING_FROM)) {
    		fEntry.setPostedBy(inputData.getSingleValue(ObjectKeys.INPUT_FIELD_POSTING_FROM)); 
    	}
    	super.addEntry_fillIn(folder, entry, inputData, entryData, ctx);
    	fEntry.updateLastActivity(fEntry.getModification().getDate());
    	if (!folder.isMirrored() && fEntry.isTop()) {
    		Statistics statistics = getFolderStatistics(folder);
	    	statistics.addStatistics(entry.getEntryDefId(), entry.getEntryDefDoc(), entry.getCustomAttributes());
	    	setFolderStatistics(folder, statistics);
    	}
    }
    //inside write transaction
    @Override
	protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	super.addEntry_postSave(binder, entry, inputData, entryData, ctx);
    }
    //no transaction
    @Override
	protected void addEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
       	super.addEntry_done(binder, entry, inputData, ctx);
   		getRssModule().updateRssFeed(entry); 
     }
 
    //***********************************************************************************************************
    //no transaction    
    @Override
	public FolderEntry addReply(final FolderEntry parent, Definition def, final InputDataAccessor inputData, Map fileItems, Map options) 
   		throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
               
    	final Map ctx = new HashMap();
        if (options != null) ctx.putAll(options);
        ctx.put(ObjectKeys.INPUT_OPTION_SKIP_PARENT_MODTIME_UPDATE, Boolean.TRUE);

    	Map entryDataAll = addReply_toEntryData(parent, def, inputData, fileItems, ctx);
        final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
        List fileData = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
        List allUploadItems = new ArrayList(fileData);
        FolderEntry newEntry = null;
        try {
         	final FolderEntry entry = addReply_create(def, ctx);
         	newEntry = entry;
        	// The following part requires update database transaction.
	        int tryMaxCount = 1 + SPropsUtil.getInt("select.database.transaction.retry.max.count", ObjectKeys.SELECT_DATABASE_TRANSACTION_RETRY_MAX_COUNT);
	        int tryCount = 0;
	        while(true) {
	        	tryCount++;
	        	try {
		        	getTransactionTemplate().execute(new TransactionCallback() {
		        	@Override
					public Object doInTransaction(TransactionStatus status) {
		        		addReply_fillIn(parent, entry, inputData, entryData, ctx);
		        		addReply_preSave(parent, entry, inputData, entryData, ctx);
		        		addReply_save(parent, entry, inputData, entryData, ctx);
		               	addReply_startWorkflow(entry, ctx);
		           		addReply_postSave(parent, entry, inputData, entryData, ctx);
		           		return null;
		        	}});
			        break; // successful transaction
	        	}
	        	catch(LockAcquisitionException | CannotAcquireLockException e) {
	        		if(tryCount < tryMaxCount) {
	        			if(logger.isDebugEnabled())
	        				logger.warn("(" + tryCount + ") 'add reply' failed due to lock error - Retrying in new transaction", e);
	        			else 
	        				logger.warn("(" + tryCount + ") 'add reply' failed due to lock error - Retrying in new transaction: " + e.toString());
	        			getCoreDao().refresh(parent.getParentBinder());        		
	        		}
	        		else {
        				logger.error("(" + tryCount + ") 'add reply' failed due to lock error - Aborting", e);
	        			throw e;
	        		}
	        	}
	        }
	        
	        // (Bug #879800) JK - Separated the process of updating parent binder's mod time from the above main
	        // transaction to eliminate or significantly reduce the possibility of dead lock around the binder.
	        // Because the above main transaction is lengthier and involves a few other rows within the 
	        // transaction, there is much higher chance of failing to obtain a lock on the parent binder by
	        // the time it attempts to modify the binder record. Instead, we separated binder update in its
	        // own shorter transaction here which doesn't require obtaining previous lock on any other rows,
	        // hence avoiding deadlock.
	        tryCount = 0;
	        while(true) {
	        	tryCount++;
	        	try {
		        	getTransactionTemplate().execute(new TransactionCallback() {
		        	@Override
					public Object doInTransaction(TransactionStatus status) {
		    	        updateParentModTime(parent.getParentBinder(), null);
		    	        return null;
		        	}});
			        break; // successful transaction
	        	}
	        	catch(LockAcquisitionException | CannotAcquireLockException e) {
	        		if(tryCount < tryMaxCount) {
	        			if(logger.isDebugEnabled())
	        				logger.warn("(" + tryCount + ") 'update parent mod time' failed due to lock error - Retrying in new transaction", e);
	        			else 
	        				logger.warn("(" + tryCount + ") 'update parent mod time' failed due to lock error - Retrying in new transaction: " + e.toString());
	        			getCoreDao().refresh(parent.getParentBinder());        		
	        		}
	        		else {
        				logger.error("(" + tryCount + ") 'update parent mod time' failed due to lock error - Aborting", e);
	        			throw e;
	        		}
	        	}
	        	catch(HibernateOptimisticLockingFailureException e) {
	        		if(tryCount < tryMaxCount) {
	        			if(logger.isDebugEnabled())
	        				logger.warn("(" + tryCount + ") 'update parent mod time' failed due to optimistic locking failure - Retrying in new transaction", e);
	        			else 
	        				logger.warn("(" + tryCount + ") 'update parent mod time' failed due to optimistic locking failure - Retrying in new transaction: " + e.toString());
	        			getCoreDao().refresh(parent);
	        			getCoreDao().refresh(parent.getParentBinder());        		
	        		}
	        		else {
        				logger.error("(" + tryCount + ") 'update parent mod time' failed due to optimistic locking failure - Aborting", e);
	        			throw e;
	        		}
	        	}
	        }
	        
           	// Need entry id before filtering 
            FilesErrors filesErrors = addReply_filterFiles(parent.getParentFolder(), entry, entryData, fileData, ctx);
        	filesErrors = addReply_processFiles(parent, entry, fileData, filesErrors, ctx);
                 
        	addReply_indexAdd(parent, entry, inputData, fileData, ctx);
                
        	addReply_done(parent, entry, inputData, ctx);

        	if(filesErrors.getProblems().size() > 0) {
        		// 	At least one error occurred during the operation. 
        		throw new WriteFilesException(filesErrors, entry.getId());
        	}
        	else {
        		return entry;
        	}
        } catch(WriteFilesException ex) {
	       	//See if there was an entry created. If so, delete it.
        	if (ex.getEntityId() != null && newEntry != null && ex.getEntityId().equals(newEntry.getId())) {
        		try {
        			deleteEntry(parent.getParentBinder(), newEntry, false, new HashMap());
        		} catch(Exception e) {
        			//Any further errors while trying to delete the entry are ignored
        		}
        		ex.setEntityId(null);
        	}
        	throw ex;
    	} finally {
        	cleanupFiles(allUploadItems);
    		
    	}
    }
    //no transaction
    protected Map addReply_toEntryData(FolderEntry parent, Definition def, InputDataAccessor inputData, Map fileItems, Map ctx) {
     	return addEntry_toEntryData(parent.getParentBinder(), def, inputData, fileItems, ctx);
    }
    
    /**
     * Subclass must implement this.
     * @return
     */
    //no transaction
    protected FolderEntry addReply_create(Definition def, Map ctx) {
    	return (FolderEntry)addEntry_create(def, FolderEntry.class, ctx);    	
    }

    //no transaction
    protected FilesErrors addReply_filterFiles(Binder binder, Entry reply, 
    		Map entryData, List fileUploadItems, Map ctx) throws FilterException, TitleException {
    	return addEntry_filterFiles(binder, reply, entryData, fileUploadItems, ctx);
    }
    //no transaction
    protected FilesErrors addReply_processFiles(FolderEntry parent, FolderEntry entry, 
    		List fileData, FilesErrors filesErrors, Map ctx) {
    	return addEntry_processFiles(parent.getParentBinder(), entry, fileData, filesErrors, ctx);
    }
    //inside write transaction
    protected void addReply_fillIn(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//lock the top entry = to reduce optimistic lock exceptions
    	//locking the top should handle all the children as a side effect
    	//this is needed to setup sortkey and to set lastActivity,total Reply count on top Entry
    	getCoreDao().lock(parent.isTop() ? parent : parent.getTopEntry());
        parent.addReply(entry);         
    	if (inputData.exists(ObjectKeys.INPUT_FIELD_POSTING_FROM)) {
    		entry.setPostedBy(inputData.getSingleValue(ObjectKeys.INPUT_FIELD_POSTING_FROM)); 
    	}
    	super.addEntry_fillIn(entry.getParentBinder(), entry, inputData, entryData, ctx);
    	entry.updateLastActivity(entry.getModification().getDate());
    }
    
    //inside write transaction
    protected void addReply_preSave(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	addEntry_preSave(parent.getParentBinder(), entry, inputData, entryData, ctx);
    }
    
    //inside write transaction
    protected void addReply_save(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	addEntry_save(parent.getParentBinder(), entry, inputData, entryData, ctx);
    }
    //inside write transaction
    protected void addReply_startWorkflow(FolderEntry entry, Map ctx) {
       	if (ctx != null && Boolean.TRUE.equals(ctx.get(ObjectKeys.INPUT_OPTION_NO_WORKFLOW))) return;
       	FolderEntry parent = entry.getParentEntry();
   		if (getWorkflowModule().modifyWorkflowStateOnReply(parent)) {
   	   		parent.incrLogVersion();
   			processChangeLog(parent, ChangeLog.MODIFYWORKFLOWSTATEONREPLY);
   	    	getReportModule().addAuditTrail(AuditType.modify, parent);
   		}
    	//Starting a workflow on a reply works the same as for the entry
    	addEntry_startWorkflow(entry, ctx);
    }
    
    //inside write transaction
    protected void addReply_postSave(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//will log addEntry
    	addEntry_postSave(parent.getParentBinder(), entry, inputData, entryData, ctx);

    }
    
    //no transaction
    protected void addReply_done(Entry parent, Entry entry, InputDataAccessor inputData, Map ctx) {
    	addEntry_done(parent.getParentBinder(), entry, inputData, ctx);
    }
    //no transaction
    protected void addReply_indexAdd(FolderEntry parent, FolderEntry entry, 
    		InputDataAccessor inputData, List fileData, Map ctx) {
  	   if (ctx != null && Boolean.TRUE.equals(ctx.get(ObjectKeys.INPUT_OPTION_NO_INDEX))) return;
  	   addEntry_indexAdd(entry.getParentFolder(), entry, inputData, fileData, ctx);
    	//Also re-index the top entry (to catch the change in lastActivity and total reply count)
    	indexEntry(entry.getTopEntry());
    }
    
     //***********************************************************************************************************
    //inside write transaction
    @Override
	protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
    	Statistics statistics = getFolderStatistics((Folder)binder);
    	statistics.deleteStatistics(entry.getEntryDefId(), entry.getEntryDefDoc(), entry.getCustomAttributes());
    	super.modifyEntry_fillIn(binder, entry, inputData, entryData, ctx);
    }
    //inside write transaction
	@Override
	protected void modifyEntry_postFillIn(Binder binder, Entry entry, 
 			InputDataAccessor inputData, Map entryData, Map<FileAttachment,String> fileRenamesTo, Map ctx) {
 		super.modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo, ctx);
    	FolderEntry fEntry = (FolderEntry)entry;
		fEntry.updateLastActivity(fEntry.getModification().getDate());
		if(!binder.isMirrored()) {
	    	Statistics statistics = getFolderStatistics((Folder)binder);
		    statistics.addStatistics(entry.getEntryDefId(), entry.getEntryDefDoc(), entry.getCustomAttributes());
		    setFolderStatistics((Folder)binder, statistics);
		}
  }
    //no transaction
	@Override
	protected void modifyEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
    	getRssModule().updateRssFeed(entry);
 	}
    @Override
	protected void modifyEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems, 
    		Collection<FileAttachment> filesToIndex, Map ctx) {
  	   if (ctx != null && Boolean.TRUE.equals(ctx.get(ObjectKeys.INPUT_OPTION_NO_INDEX))) return;
  	   super.modifyEntry_indexAdd(binder, entry, inputData, fileUploadItems, filesToIndex, ctx);
  	   
  	   FolderEntry fEntry = (FolderEntry)entry;
  	   boolean isReply = (!(fEntry.isTop())); 
  	   if (isReply) {
  	       // Re-index the top entry (to catch the change in
  	  	   // lastActivity for comments.)
  		   indexEntry(fEntry.getTopEntry());
  	   }
  	   
  	   boolean isRename = ((ctx != null) && (!(ctx.get(ObjectKeys.FIELD_ENTITY_TITLE).equals(entry.getTitle()))));
  	   if (isRename && (fEntry.isAclExternallyControlled() || (isReply && fEntry.getTopEntry().isAclExternallyControlled()))) {
  		   // Bugzilla 869821 (DRF):  For entries in Net Folders that
  		   // are being renamed, we need re-index their comments too.
  		   // Otherwise, ACL checks on them through FAMT may not work
  		   // after the rename.
  		   Map<FolderEntry, Integer> allReplies = new HashMap<FolderEntry, Integer>();
  		   fEntry.buildTotalReplyList(allReplies, fEntry);
  		   for (FolderEntry reply:  allReplies.keySet()) {
  	  		   indexEntry(reply);
  		   }
  	   }
    }
    
    //***********************************************************************************************************
    //no write transaction    
    @Override
	protected void deleteEntry_setCtx(Entry entry, Map ctx) {
    	//need context to pass replies
      	FolderEntry fEntry = (FolderEntry)entry;
      	//save top cause remove of reply sets it to null
       	ctx.put("this.topEntry", fEntry.getTopEntry());
    	//pass replies along as context so we can delete them all at once
     	//load in reverse hkey order so foreign keys constraints are handled correctly
     	List<FolderEntry> replies= getFolderDao().loadEntryDescendants((FolderEntry)fEntry);
        ctx.put("this.replies", replies);
        super.deleteEntry_setCtx(entry, ctx);
    }
    //no transaction
   	@Override
	protected void deleteEntry_processChangeLogs(Binder parentBinder, Entry entry, Map ctx, List changeLogs) {
   		super.deleteEntry_processChangeLogs(parentBinder, entry, ctx, changeLogs);
   		//create history prior to delete.
     	List<FolderEntry> replies= (List)ctx.get("this.replies");
     	for (FolderEntry reply:replies) {
     		super.deleteEntry_processChangeLogs(parentBinder, reply, ctx, changeLogs);
     	}
   	}
    //inside write transaction
    @Override
	protected void deleteEntry_preDelete(Binder parentBinder, Entry entry, Map ctx) {
        Statistics statistics = getFolderStatistics((Folder)parentBinder);        
        statistics.deleteStatistics(entry.getEntryDefId(), entry.getEntryDefDoc(), entry.getCustomAttributes());
    	super.deleteEntry_preDelete(parentBinder, entry, ctx);
       	FolderEntry fEntry = (FolderEntry)entry;
     	List<FolderEntry> replies= (List)ctx.get("this.replies");
      	//repeat pre-delete for each reply
      	for (int i=0; i<replies.size(); ++i) {
      		FolderEntry reply = (FolderEntry)replies.get(i);
            statistics.deleteStatistics(reply.getEntryDefId(), reply.getEntryDefDoc(), reply.getCustomAttributes());
    		super.deleteEntry_preDelete(parentBinder, reply, null);
    		reply.updateLastActivity(reply.getModification().getDate());
    	}
      	fEntry.updateLastActivity(fEntry.getModification().getDate());
        setFolderStatistics((Folder)parentBinder, statistics);
      	
    }
    //inside write transaction    
    @Override
	protected void deleteEntry_workflow(Binder parentBinder, Entry entry, Map ctx) {
    	//folder Dao will handle
    }
    //no write transaction    
    @Override
	protected void deleteEntry_processFiles(Binder parentBinder, Entry entry, boolean deleteMirroredSource, Map ctx) 
			throws WriteFilesException {
    	List<FolderEntry> replies = (List)ctx.get("this.replies");
     	for (FolderEntry reply: replies) {
     		try {
     			super.deleteEntry_processFiles(parentBinder, reply, deleteMirroredSource, ctx);
    		} catch(WriteFilesException e) {
    			//The files attached to this entry could not be deleted
    			//See if the error should be propagated
    			if (ctx.containsKey(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS) &&
    					(boolean)ctx.get(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS)) {
    				throw e;
    			}
    		}
    	}
     	try {
     		super.deleteEntry_processFiles(parentBinder, entry, deleteMirroredSource, ctx);
		} catch(WriteFilesException e) {
			//The files attached to this entry could not be deleted
			//See if the error should be propagated
			if (ctx.containsKey(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS) &&
					(boolean)ctx.get(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS)) {
				throw e;
			}
		}
    }
    
    //inside write transaction    
    @Override
	protected void deleteEntry_delete(Binder parentBinder, Entry entry, Map ctx) {
       	if (parentBinder.isDeleted()) return;  //will handle in bulk way
        //use the optimized deleteEntry or hibernate deletes each collection entry one at a time
       	List entries = new ArrayList((List)ctx.get("this.replies"));
       	entries.add(entry);
       	if (!entry.isTop()) {
       		//remove from list of children
       		FolderEntry fEntry = (FolderEntry)entry;
       		fEntry.getParentEntry().removeReply(fEntry);
       	}
       	getFolderDao().deleteEntries((Folder)parentBinder, entries);   
    }
    //inside write transaction
    @Override
	protected void deleteEntry_postDelete(Binder parentBinder, Entry entry, Map ctx) {
       	if (parentBinder.isDeleted()) return;  //will handle in bulk way
       	List<FolderEntry> replies = (List)ctx.get("this.replies");
      	for (FolderEntry reply: replies) {
       		super.deleteEntry_postDelete(parentBinder, reply, null);
    	}
       	Collection<Entry> entries = new ArrayList((List)ctx.get("this.replies"));
       	entries.add((FolderEntry)entry);
 		getRssModule().deleteRssFeed(parentBinder, entries);
		super.deleteEntry_postDelete(parentBinder, entry, null);
  }
    //no transaction    
    @Override
	protected void deleteEntry_indexDel(Binder parentBinder, Entry entry, Map ctx) {
       	if (parentBinder.isDeleted()) return;  //will handle in bulk way
        List<FolderEntry> replies = (List)ctx.get("this.replies");
      	for (FolderEntry reply: replies) {
      		super.deleteEntry_indexDel(parentBinder, reply, null);
    	}
		super.deleteEntry_indexDel(parentBinder, entry, null);
		FolderEntry top = (FolderEntry)ctx.get("this.topEntry");
		if (top != null) indexEntry(top);
		
   }
    //***********************************************************************************************************
    //inside write transaction
    @Override
	public void addEntryWorkflow(Binder binder, Entry entry, Definition definition, Map options) {
    	super.addEntryWorkflow(binder, entry, definition, options);
 		if (options != null && Boolean.TRUE.equals(options.get(ObjectKeys.INPUT_OPTION_NO_INDEX))) return;
     	
    	//reindex top whose lastActivity has changed
    	if (!entry.isTop()) {
 		   FolderEntry top = ((FolderEntry)entry).getTopEntry();
 		   indexEntry(top);
    	}
    }
    //***********************************************************************************************************
    //inside write transaction
    @Override
	public void deleteEntryWorkflow(Binder binder, Entry entry, Definition definition) {
    	super.deleteEntryWorkflow(binder, entry, definition);
    	//reindex top whose lastActivity has changed
    	if (!entry.isTop()) {
 		   FolderEntry top = ((FolderEntry)entry).getTopEntry();
 		   indexEntry(top);
    	}
    }
    //***********************************************************************************************************
    //inside write transaction
    @Override
	public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState) {
    	super.modifyWorkflowState(binder, entry, tokenId, toState);
    	if (!entry.isTop()) {
 		   FolderEntry top = ((FolderEntry)entry).getTopEntry();
 		   indexEntry(top);
    	}
       	getRssModule().updateRssFeed(entry);
           	
    }
    //***********************************************************************************************************
    //inside write transaction
   @Override
public void setWorkflowResponse(Binder binder, Entry entry, Long stateId, InputDataAccessor inputData, Boolean canModifyEntry)  {
	   Long version = entry.getLogVersion();
	   super.setWorkflowResponse(binder, entry, stateId, inputData, canModifyEntry);
	   if (version != entry.getLogVersion()) {
		   FolderEntry fEntry = (FolderEntry)entry;
		   fEntry.updateLastActivity(fEntry.getModification().getDate());
 		   if (!fEntry.isTop()) {
 			   FolderEntry top = fEntry.getTopEntry();
 			   indexEntry(top);
 		   }
	   }
   }
   //***********************************************************************************************************
   @Override
public Entry copyEntry(Binder binder, Entry source, Binder destination, String[] toFileNames, Map options) throws WriteFilesException {
   	 
	   if (destination.isZone() || 
			   ObjectKeys.PROFILE_ROOT_INTERNALID.equals(destination.getInternalId()) ||
			   !(destination instanceof Folder))
     		throw new NotSupportedException("errorcode.notsupported.copyEntryDestination", new String[] {destination.getPathName()});
	   if (!source.isTop()) throw new NotSupportedException("errorcode.notsupported.copyReply");
	   
	   BinderHelper.copyEntryCheckMirrored(binder, source, destination);

	   if (destination.isLibrary()) {
		   //Check that there wouldn't be any duplicate file names because of this move
		   try {
			   BinderHelper.copyOrMoveEntryCheckUniqueFileNames(destination, source, toFileNames);
		   } catch(Exception e) {
			   //Cannot copy this entry because it will violate the unique names requirement
			   FilesErrors errors = new FilesErrors();
			   errors.addProblem(new FilesErrors.Problem("",  source.getTitle(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, e));
			   throw new WriteFilesException(errors);
		   }
	   }

	   List<FolderEntry>children = getFolderDao().loadEntryDescendants((FolderEntry)source);
	   children.add(0, (FolderEntry)source);
	   getCoreDao().bulkLoadCollections(children);
	   List<EntityIdentifier> ids = new ArrayList();
	   for (FolderEntry e: children) {
		   ids.add(e.getEntityIdentifier());
	   }
	   Map<EntityIdentifier, List<Tag>> tags = getCoreDao().loadAllTagsByEntity(ids);
	   Map<FolderEntry, FolderEntry> sourceMap = new HashMap();
       getCoreDao().lock(destination);
       String newTitle = (options==null)?null:(String) options.get(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE);
	   //get ordered list of entries
	   for (FolderEntry child:children) {
		   FolderEntry entry = new FolderEntry(child);		
		   if(child.equals(source) && Validator.isNotNull(newTitle)) { 
			   // If the caller specified new title for the new entry, then we must set the title 
			   // of the top-level entry in the copied hierarchy to this title. The titles of the 
			   // children remain unchanged.
			   entry.setTitle(newTitle);
		   }
			if (child.isTop()) {
				//docnumber will be different
				((Folder)destination).addEntry(entry);
  			} else {
  				FolderEntry dParent = sourceMap.get(child.getParentEntry());
  				dParent.addReply(entry, child.getHKey().getLastNumber());
  			}
			getCoreDao().save(entry); //need to generate id; do after sortkey is set
			sourceMap.put(child, entry);
		    List<Tag> entryTags = tags.get(child.getEntityIdentifier());
			try{
				// file name change is possible only for the top-level entry being copied.
				FilesErrors errors = new FilesErrors();
				doCopy(child, entry, entryTags, child.equals(source)?toFileNames:null, options, errors);
				if (!errors.getProblems().isEmpty()) {
					throw new WriteFilesException(errors);
				}
			} catch(Exception e) {
				logger.error(e);
				//The copy failed, so delete the attempted copy
				sourceMap.remove(child);
				FolderModule folderModule = (FolderModule) SpringContextUtil.getBean("folderModule");
				folderModule.deleteEntry(entry.getParentBinder().getId(), entry.getId());
				if (e instanceof TitleException) {
					throw ((TitleException) e);
				}
				else if (e instanceof WriteFilesException) {
					throw ((WriteFilesException) e);
				}
				break;
			}
	   }
	   
	   FolderEntry top = sourceMap.get(source);
	   //the top folder was already index but prior to adding its children, so by adding this index on the top folder entry
	   //the additional information such as # of comments on a blog entry will show correctly
	   if (top != null) {
		   indexEntry(top.getParentBinder(), top, null, top.getFileAttachments(), true, tags.get(top.getEntityIdentifier()), false);
	   }
	   return top; 
   }
 
	protected void doCopy(FolderEntry source, FolderEntry entry, List<Tag> tags, String[] toFileNames, Map copyOptions, FilesErrors errors) {
		User user = RequestContextHolder.getRequestContext().getUser();
		getFileModule().copyFiles(source.getParentBinder(), source, entry.getParentBinder(), entry, toFileNames, errors);
		EntryBuilder.copyAttributes(source, entry);
 		//copy tags
 		List myTags = new ArrayList();
 		for (Tag t:tags) {
 			Tag tCopy = new Tag(t);
 			tCopy.setEntityIdentifier(entry.getEntityIdentifier());
 			if (source.getEntityIdentifier().equals(t.getOwnerIdentifier())) {
 				tCopy.setOwnerIdentifier(entry.getEntityIdentifier());
 			}
 			getCoreDao().save(tCopy);
 			myTags.add(tCopy);
 		}
    	if (entry.isTop()) {
    		if (entry.getParentFolder().isUniqueTitles()) 
    			getCoreDao().updateTitle(entry.getParentBinder(), entry, null, entry.getNormalTitle());
    		
    		if(!entry.getParentFolder().isMirrored()) {
	    		Statistics statistics = getFolderStatistics(entry.getParentFolder());
	    		statistics.addStatistics(entry.getEntryDefId(), entry.getEntryDefDoc(), entry.getCustomAttributes());
	    		setFolderStatistics(entry.getParentFolder(), statistics);
    		}
    		
    		//Check if this entry is being added to a Filr folder
    		if (entry.getParentBinder().isMirrored() && entry.getParentBinder().isAclExternallyControlled()) {
    			//In this case, we must change the entry definition type to be a Filr file
    			Document defDoc = entry.getEntryDefDoc();
    			String internalDefId = defDoc.getRootElement().attributeValue("internalId", "");
    			if (!internalDefId.equals(ObjectKeys.DEFAULT_MIRRORED_FILR_FILE_ENTRY_DEF)) {
    				//This is a bad definition type for a remote file. Change it
    				entry.setEntryDefId(ObjectKeys.DEFAULT_MIRRORED_FILR_FILE_ENTRY_DEF);
    			}
    		}
    	}
    	processChangeLog(entry, ChangeLog.ADDENTRY);
		getCoreDao().evict(tags);
		
		//If moving share items, do that now
		if (copyOptions != null && copyOptions.containsKey(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS) && 
				(Boolean)copyOptions.get(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS)) {
			List<Long> shareItemIds = getProfileDao().getShareItemIdsByEntity(source);
			if (!shareItemIds.isEmpty()) {
				getProfileDao().changeSharedEntityId(shareItemIds, entry);
			}
		}

		//Add any workflows
		if (copyOptions != null && copyOptions.containsKey(ObjectKeys.WORKFLOW_START_WORKFLOW) && 
				(ObjectKeys.WORKFLOW_START_WORKFLOW_START.equals(copyOptions.get(ObjectKeys.WORKFLOW_START_WORKFLOW)) ||
				 ObjectKeys.WORKFLOW_START_WORKFLOW_COPY.equals(copyOptions.get(ObjectKeys.WORKFLOW_START_WORKFLOW)))) {
			
			//We are supposed to start the workflows
			Set<WorkflowState> workflowStates = source.getWorkflowStates();
	
			for (WorkflowState state : workflowStates) {
				Definition def = state.getDefinition();
	
				EntityIdentifier entityIdentifier = new EntityIdentifier(entry.getId(), 
						EntityIdentifier.EntityType.folderEntry);
	
				Map options = new HashMap();
				if (ObjectKeys.WORKFLOW_START_WORKFLOW_COPY.equals(copyOptions.get(ObjectKeys.WORKFLOW_START_WORKFLOW))) {
					options.put(ObjectKeys.INPUT_OPTION_FORCE_WORKFLOW_STATE, state.getState());
				}
				
				//Workflow State needs a modification timestamp
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		    	Calendar current = Calendar.getInstance();
		    	current.setTime(new Date());
				
				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_NAME, user.getName());
				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, current);				
				
				getWorkflowModule().addEntryWorkflow((FolderEntry) entry,
					entityIdentifier, def, options);
					
			}
		} else if (copyOptions != null && copyOptions.containsKey(ObjectKeys.WORKFLOW_START_WORKFLOW) && 
				ObjectKeys.WORKFLOW_START_WORKFLOW_NO_START.equals(copyOptions.get(ObjectKeys.WORKFLOW_START_WORKFLOW))) {
			//Not starting the workflow that existed on the source entry, so check if one is configured for the folder
	    	Map workflowAssociations = (Map) entry.getParentBinder().getWorkflowAssociations();
	    	if (workflowAssociations != null) {
	    		//See if the entry definition type has an associated workflow
	    		if (entry.getEntryDefId() != null) {
	    			Definition wfDef = (Definition)workflowAssociations.get(entry.getEntryDefId());
	    			if (wfDef != null) {
	    				//Before starting this, make sure it isn't the same as one we aren't supposed to start
	    				boolean found = false;
	    				Set<WorkflowState> workflowStates = source.getWorkflowStates();
	    				for (WorkflowState state : workflowStates) {
	    					Definition def = state.getDefinition();
	    					if (def.getId().equals(wfDef.getId())) {
	    						found = true;
	    						break;
	    					}
	    				}
	    				if (!found) {
	    					//It is OK to start this workflow
	    					getWorkflowModule().addEntryWorkflow((WorkflowSupport)entry, entry.getEntityIdentifier(), wfDef, null);
	    				}
	    			}
	    		}
	    	}
		}

		indexEntry(entry.getParentBinder(), entry, null, entry.getFileAttachments(), true, myTags, false);
   	 
   }
 
   //***********************************************************************************************************
    //inside write transaction    
    @Override
	public void moveEntry(Binder binder, Entry entry, Binder destination, String[] toFileNames, Map options) {
       	if (destination == null || binder.equals(destination)) return;
    	Folder from = (Folder)binder;
    	if (!(destination instanceof Folder))
    		throw new NotSupportedException("errorcode.notsupported.moveEntryDestination", new String[] {destination.getPathName()});
    	Folder to = (Folder)destination;
    	FolderEntry fEntry = (FolderEntry)entry;
    	if (fEntry.getTopEntry() != null)
    		throw new NotSupportedException("errorcode.notsupported.moveReply");
    	BinderHelper.moveEntryCheckMirrored(binder, entry, destination);
    	
    	if (destination.isLibrary()) {
    		//Check that there wouldn't be any duplicate file names because of this move
    		BinderHelper.copyOrMoveEntryCheckUniqueFileNames(destination, entry, toFileNames);
    	}
    	
    	//Remove this entry from the RSS index
    	Set<Entry> entriesToDelete = new HashSet<Entry>();
    	entriesToDelete.add(entry);
    	getRssModule().deleteRssFeed(entry.getParentBinder(), entriesToDelete);
    	
    	HKey oldKey = fEntry.getHKey();
    	//get Children
    	List entries = getFolderDao().loadEntryDescendants(fEntry);
    	//only log top level titles
   		if (from.isUniqueTitles()) getCoreDao().updateTitle(from, entry, entry.getNormalTitle(), null);		
   	    from.removeEntry(fEntry);
    	to.addEntry(fEntry);
    	String newTitle = (options==null)?null:(String)options.get(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE);
    	if(Validator.isNotNull(newTitle)) {
		   // If the caller specified new title for the moved entry, then we need to change the title here.
    		entry.setTitle(newTitle);
    	}
   		if (to.isUniqueTitles()) getCoreDao().updateTitle(to, entry, null, entry.getNormalTitle());		
        Statistics statistics = getFolderStatistics(from);        
        statistics.deleteStatistics(entry.getEntryDefId(), entry.getEntryDefDoc(), entry.getCustomAttributes());
        setFolderStatistics(from, statistics);
        if(!destination.isMirrored()) {
	        statistics = getFolderStatistics((Folder)destination);
	        statistics.addStatistics(entry.getEntryDefId(), entry.getEntryDefDoc(), entry.getCustomAttributes());
	        setFolderStatistics((Folder)destination, statistics);
        }

    	User user = RequestContextHolder.getRequestContext().getUser();

        fEntry.setModification(new HistoryStamp(user));
        fEntry.incrLogVersion();
        //just log new location
    	ChangeLog changes = ChangeLogUtils.create(fEntry, ChangeLog.MOVEENTRY);
    	ChangeLogUtils.save(changes);

    	List ids = new ArrayList();
    	for (int i=0; i<entries.size(); ++i) {
    		FolderEntry e = (FolderEntry)entries.get(i);
     	   	String childSort = e.getHKey().getSortKey();
     	   	//need these changes for index and change log
          	e.setHKey(new HKey(childSort.replaceFirst(oldKey.getSortKey(), fEntry.getHKey().getSortKey())));
          	e.setParentFolder(to);
          	e.setOwningBinderKey(to.getBinderKey().getSortKey());
            //just log new location
          	e.setModification(fEntry.getModification());
          	e.incrLogVersion();
        	changes = ChangeLogUtils.create(e, ChangeLog.MOVEENTRY);
        	ChangeLogUtils.save(changes);
         	ids.add(e.getId());
    	}
    	//add top entry to list of entries
    	ids.add(fEntry.getId());
    	//write out changes before bulk updates
    	getCoreDao().flush();
    	getFolderDao().moveEntries(to,ids);
    	entries.add(fEntry);
    	// Move files in the entries
   		for (Iterator iter=entries.iterator(); iter.hasNext();) {
   			FolderEntry e = (FolderEntry)iter.next();
   			// file name change is possible only for the top-level entry being moved
   			getFileModule().moveFiles(binder, e, destination, e, e.equals(entry)?toFileNames:null);
   		}
    	//finally remove from index and reAdd.
    	indexEntries(entries);
   		getRssModule().updateRssFeed(fEntry); 
    }
    
    //***********************************************************************************************************
       
 	@Override
	protected void indexEntries_postIndex(Binder binder, Entry entry) {
 		super.indexEntries_postIndex(binder, entry);
	}
 
    //***********************************************************************************************************
    //inside write transaction    
   @Override
public void deleteBinder(Binder binder, boolean deleteMirroredSource, Map options, final boolean skipDbLog) {
    	if(logger.isDebugEnabled())
    		logger.debug("Deleting binder [" + binder.getPathName() + "]");
    	//mark deleted first, saving real work for later
    	if (!binder.isDeleted()) 
    		super.deleteBinder(binder, deleteMirroredSource, options, skipDbLog);
    	else {
    		//if binder is marked deleted, we are called from cleanup code without a transaction 
    		final Folder folder = (Folder)binder;
    		final FilterControls filter = new FilterControls(
    				new String[] {ObjectKeys.FIELD_ENTITY_DELETED},
					new Object[] {Boolean.FALSE});

    		
    		//loop through all entries and record delete
			Boolean done=Boolean.FALSE;
			while (!done) {
				done = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus status) {
						SFQuery query = getFolderDao().queryEntries(folder, filter); 
						try {
							int count = 0;
							List entries = new ArrayList();
							while (query.hasNext()) {
			      				Object obj = query.next();
			       				if (obj instanceof Object[])
			       					obj = ((Object [])obj)[0];
			 					FolderEntry entry = (FolderEntry)obj;
			 					if(logger.isDebugEnabled())
			 						logger.debug("Deleting entry [" + entry.getTitle() + "], id=" + entry.getId());
					    		//create history - using timestamp and version from folder delete
								if(!skipDbLog) {
									try {
										entry.setModification(folder.getModification());
										entry.incrLogVersion();
										processChangeLog(entry, ChangeLog.DELETEENTRY);
									} catch (Exception ex) {
										logger.warn("Error logging entry " + entry.toString(), ex);
									}
								}
								
								getFileModule().deleteFiles(folder, entry, false, null, skipDbLog);

								entries.add(entry);
								++count;
								
								//commit after 100
								if (count == 100) {
									//mark processed entries as deleted, so not read again
									//evict entries so not updated
									getFolderDao().markEntriesDeleted(folder, entries);
									return Boolean.FALSE;
								}
							}
							//	finally delete the binder and its associations
							try {
								getFileModule().deleteFiles(folder, folder, false, null, skipDbLog);
							} catch (Exception ex) {
								logger.warn("Error delete files: " + folder.getPathName(), ex);
							}
							//mark delete and flush from cache, cause handled in bulk way
							getFolderDao().markEntriesDeleted(folder, entries);
							getCoreDao().flush();  //flush before bulk updates
							//finally remove folder and its entries
							getFolderDao().delete(folder);
							//delete binder
							return Boolean.TRUE;
						} catch (Exception ex) {
							//don't want the transaction to clear the session
							logger.warn("Error delete folder " + folder.getPathName(), ex);
							return Boolean.TRUE;
						} finally {
							query.close();
						}
					}
	        	});
			}
     	};
    }
    
   //inside write transaction    
    @Override
	protected void deleteBinder_processFiles(Binder binder, Map ctx) {
    	//save for background

    }
    //inside write transaction    
   @Override
public void deleteBinder_delete(Binder binder, boolean deleteMirroredSource, Map ctx) {
      	if (!binder.isRoot()) {
    		binder.getParentBinder().removeBinder(binder);
    	}
    	//mark for delete now and continue in the background
    	binder.setDeleted(true);
    	//release posting now so name is available
    	if (binder.getPosting() != null) {
    		getCoreDao().delete(binder.getPosting());
    		binder.setPosting(null);
    	}
    }

   //inside write transaction    
   @Override
protected void deleteBinder_postDelete(Binder binder, Map ctx) {
	   //remove notification status so mail isn't sent
	   getCoreDao().executeUpdate("delete from org.kablink.teaming.domain.NotifyStatus where owningBinderId=" + binder.getId());
	   getRssModule().deleteRssFeed(binder);
    }

    //***********************************************************************************************************
   //inside write transaction    
    @Override
	public void moveBinder(Binder source, Binder destination, Map options) {
    	if ((destination instanceof Folder) || (destination instanceof Workspace)) 
    		super.moveBinder(source, destination, options);
    	else throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
   	 
    }
    //inside write transaction    
	@Override
	public void moveBinderFixup(Binder binder) {
		//Some parentage has changed.
		Folder folder = (Folder)binder;
		if (!folder.isTop()) {  //may have had topFolder changed
			if (folder.getParentFolder().isTop()) folder.setTopFolder(folder.getParentFolder());
			else folder.setTopFolder(folder.getParentFolder().getTopFolder());
		}
		getFolderDao().move(folder);
    	
	}
    //***********************************************************************************************************
    //no transaction
    @Override
	public Binder copyBinder(Binder source, Binder destination, Map options) {
    	if ((destination instanceof Folder) || (destination instanceof Workspace)) {
    		return super.copyBinder(source, destination, options);
    	} else {
    		throw new NotSupportedException("errorcode.notsupported.copyBinderDestination", new String[] {destination.getPathName()});
    	}
   	 
    }
    //***********************************************************************************************************
   //no transaction
    
    //If the destination binder is a mirrored folder and the source binder is not a mirrored folder,
    // then the callers to this routine should have validated that every entry is valid to copy.
    //This routine will skip over any entry that is not valid, possibly making a partial copy
    @Override
	public void copyEntries(final Binder source, Binder binder, final Map options) { 
		//now copy entries
		final Folder folder = (Folder)binder;
		Map serializationMap = ((Map) folder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE));
		final boolean hasTaskLinkage  = ((null != serializationMap) && (!(serializationMap.isEmpty())));
		final List<Long>      sIds    = (hasTaskLinkage ? new ArrayList<Long>()     : null);
		final Map<Long, Long> eIdsMap = (hasTaskLinkage ? new HashMap<Long, Long>() : null);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
		    	Boolean preserverDocNum = null;
		    	if (options != null) preserverDocNum = (Boolean)options.get(ObjectKeys.INPUT_OPTION_PRESERVE_DOCNUMBER);
		    	if (preserverDocNum == null) preserverDocNum=Boolean.FALSE;
				getCoreDao().lock(folder);
				FilterControls filter = new FilterControls();
				filter.setOrderBy(new OrderBy("HKey.sortKey"));
				SFQuery query = getFolderDao().queryEntries((Folder)source, filter);
				FolderModule fm = ((FolderModule) SpringContextUtil.getBean("folderModule"));
		      	try {       
		  			List<FolderEntry> batch = new ArrayList();
		  			@SuppressWarnings("unused")
					int total=0;
		  			Map<FolderEntry, FolderEntry> sourceMap = new HashMap();
		      		while (query.hasNext()) {
		       			int count=0;
		       			batch.clear();
		       			// get 1000 entries, then build collections by hand 
		       			//for performance
		       			while (query.hasNext() && (count < 1000)) {
		       				Object obj = query.next();
		       				if (obj instanceof Object[])
		       					obj = ((Object [])obj)[0];
		       				batch.add((FolderEntry)obj);
		       				++count;
		       			}
		       			total += count;
		       			//have 1000 entries, manually load their collections
		       			getCoreDao().bulkLoadCollections(batch);
		       			List<EntityIdentifier> ids = new ArrayList();
		       			for (Entry e: batch) {
		       				ids.add(e.getEntityIdentifier());
		       			}
		       			Map<EntityIdentifier, List<Tag>> tags = getCoreDao().loadAllTagsByEntity(ids);
		       			for (int i=0; i<batch.size(); ++i) {
		       				// If the entry is in the trash...
		       				FolderEntry sEntry = (FolderEntry)batch.get(i);
		       				if (sEntry.isPreDeleted()) {
		       					// ...skip it.
		       					continue;
		       				}
		       				// If the user doens't have rights to copy the entry...
		       				if (!(fm.testAccess(sEntry, FolderOperation.copyEntry))) {
		       					// ...skip it.
		       					continue;
		       				}
		       				boolean okToCopy = false;
		       				try {
		       					//See if copying from this source binder to this destination folder is a legal operation to do
		       					BinderHelper.copyEntryCheckMirrored(source, sEntry, folder);
		       					okToCopy = true;
		       				} catch(Exception e) {}
		       				
		       				//Inside this routine, we will skip any invalid entries.
		       				//Callers of this routine should validate the whole binder before calling this routine
		       				//  if it is not desirable to have an incomplete copy.
		       				if (okToCopy) {
			       				FolderEntry dEntry = new FolderEntry(sEntry);
			       				
			       				if (sEntry.isTop()) {
			       					sourceMap.clear();
			       					if (preserverDocNum) folder.addEntry(dEntry, sEntry.getHKey().getLastNumber());
			       					else folder.addEntry(dEntry);
			          			} else {
			          				FolderEntry dParent = sourceMap.get(sEntry.getParentEntry());
			          				dParent.addReply(dEntry, sEntry.getHKey().getLastNumber());
			          			}
			       				getCoreDao().save(dEntry); //need to generate id; do after sortkey is set
			       				sourceMap.put(sEntry, dEntry);
			      		    	List<Tag> entryTags = tags.get(sEntry.getEntityIdentifier());
			       				doCopy(sEntry, dEntry, entryTags, null, options, null);
	
			       				// Does the folder we're copy entries from
			       				// contain task linkage information?
			       				if (hasTaskLinkage) {
			       					// Yes!  Then we need to track the IDs
			       					// of the source entries and a mapping
			       					// between the source and destination
			       					// entries.
				       				Long sId = sEntry.getId();
				       				sIds.add(sId);
				       				eIdsMap.put(sId, dEntry.getId());
			       				}
		       				}
		       			}
		       			getCoreDao().flush();
		       			//get rid of entries no longer needed
		       			getCoreDao().evict(CollectionUtil.differences(batch, sourceMap.values()));
		       	 	            	                  		
		        	}

	       			//index the folder to fix issues with this binder not knowing about its children
	       			indexEntries(folder, false, false);
		      		
		      		getCoreDao().flush();
		      		getCoreDao().evict(sourceMap.values());
		        } finally {
		        	//clear out anything remaining
		        	query.close();
		        }
		        return null;
		}});

		// Does the folder we're copy entries from contain task linkage
		// information?
		if (hasTaskLinkage) {
			// Yes!  Validate the linkage information based on the
			// entries we copied...
			ServerTaskLinkage tl = ServerTaskLinkage.loadSerializationMap(serializationMap);
			tl.validateTaskLinkage(sIds);
			
			// ...map the source IDs to their target IDs...
			Long destId = folder.getId();
			ServerTaskLinkage.fixupAndStoreTaskLinkages(
				((BinderModule) SpringContextUtil.getBean("binderModule")),
				destId,
				tl,
				eIdsMap);

			// ...and preserve the current user's sort information.
			User user = RequestContextHolder.getRequestContext().getUser();
			if (null != user) {
			   Long userId = user.getId();
			   ProfileModule pm = ((ProfileModule) SpringContextUtil.getBean("profileModule"));
			   UserProperties ufp = pm.getUserProperties(userId, source.getId());
			   if (null != ufp) {
				   Object sort = ufp.getProperty(ObjectKeys.SEARCH_SORT_BY);
				   if (null != sort) pm.setUserProperty(userId, destId, ObjectKeys.SEARCH_SORT_BY, sort);
				   
				   sort = ufp.getProperty(ObjectKeys.SEARCH_SORT_DESCEND);
				   if (null != sort) pm.setUserProperty(userId, destId, ObjectKeys.SEARCH_SORT_DESCEND, sort);
			   }
		   }
		}
    }

    //***********************************************************************************************************
    //no transaction
	@Override
	public void setFileAgingDates(final Binder binder) { 
		if (binder instanceof Folder) {
	 		//now update the entries in the binder
	 		@SuppressWarnings("unused")
			final Folder folder = (Folder)binder;
	 		getTransactionTemplate().execute(new TransactionCallback() {
	 			@Override
				public Object doInTransaction(TransactionStatus status) {
	 				FilterControls filter = new FilterControls();
	 				filter.setOrderBy(new OrderBy("HKey.sortKey"));
	 				SFQuery query = getFolderDao().queryEntries((Folder)binder, filter);
	 				List<FolderEntry> batch = new ArrayList();
	 		      	try {       
	 		      		while (query.hasNext()) {
		       				Object obj = query.next();
		       				if (obj instanceof Object[]) {
		       					obj = ((Object [])obj)[0];
		       					batch.add((FolderEntry)obj);
		       				}
	 		      		}
	 		      		for (FolderEntry entry : batch) {
 		       				FileUtils.setFileVersionAging(entry);
	 		        	}
	 		      		getCoreDao().flush();
	 		        } finally {
	 		        	//clear out anything remaining
	 		        	query.close();
	 		        }
	 		        return null;
	 		}});
		}
	}
	
    //***********************************************************************************************************
    //no transaction
	@Override
	public boolean isFolderEmpty(final Binder binder) { 
		Boolean result = true;
		if (binder instanceof Folder) {
	 		//now see if there are entries in the binder
	 		@SuppressWarnings("unused")
			final Folder folder = (Folder)binder;
	 		result = (Boolean) getTransactionTemplate().execute(new TransactionCallback() {
	 			@Override
				public Object doInTransaction(TransactionStatus status) {
	 				FilterControls filter = new FilterControls();
	 				filter.setOrderBy(new OrderBy("HKey.sortKey"));
	 				SFQuery query = getFolderDao().queryEntries((Folder)binder, filter);
	 		      	if (query.hasNext()) {
	 		      		return Boolean.FALSE;
	 		      	} else {
	 		      		return Boolean.TRUE;
	 		      	}
	 		}});
		}
		return result;
	}
	
    //***********************************************************************************************************
    @Override
	public Set getPrincipalIds(DefinableEntity entity) {
    	Set ids = super.getPrincipalIds(entity);
    	if (entity instanceof FolderEntry) {
    		FolderEntry fEntry = (FolderEntry)entity;
    		if (fEntry.getReservation() != null) 
    			ids.add(fEntry.getReservation().getPrincipal().getId());
    	}
    	return ids;
    } 

    @Override
	public Set getPrincipalIds(List<DefinableEntity> results) {
    	Set ids = super.getPrincipalIds(results);
    	for (DefinableEntity entity: results) {
        	if (entity instanceof FolderEntry) {
        		FolderEntry fEntry = (FolderEntry)entity;
        		if (fEntry.getReservation() != null) 
        			ids.add(fEntry.getReservation().getPrincipal().getId());
        	}
       }
    	return ids;
     }     

    @Override
	public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, Collection tags) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry, tags);
    	FolderEntry fEntry = (FolderEntry)entry;        
        // Add Doc number
        IndexUtils.addDocNumber(indexDoc, fEntry, false);

        // Add sortable Doc number
        IndexUtils.addSortNumber(indexDoc, fEntry, false);
        // Add ReservedBy Principal Id
       	IndexUtils.addReservedByPrincipalId(indexDoc, fEntry, false);
         // Add the folder Id
        IndexUtils.addFolderId(indexDoc, (Folder)binder, false);
        //add last activity for top entries
      	//add total reply count for top entries
        if (fEntry.isTop()) {
        	IndexUtils.addLastActivityDate(indexDoc, fEntry, false);
           	IndexUtils.addTotalReplyCount(indexDoc, fEntry, false);
        }
        
        return indexDoc;
    }
       
 
    //***********************************************************************************************************
          

    @Override
	public Map getEntryTree(Folder parentFolder, FolderEntry entry) {
    	return getEntryTree(parentFolder, entry, false);
    }
    @Override
	public Map getEntryTree(Folder parentFolder, FolderEntry entry, boolean includePreDeleted) {
    	int entryLevel;
    	List<FolderEntry> lineage;
    	Map model = new HashMap();   	
        //load tree including parent chain and all replies and entry
        lineage = getFolderDao().loadEntryTree(entry);
        //split the tree
        entryLevel = entry.getDocLevel();
        if (entryLevel > lineage.size()) {
            throw new FolderHierarchyException(entry.getId(), "Parent entries are missing");
        }
        model.put(ObjectKeys.FOLDER_ENTRY, entry);
        //remove self from list
        lineage.remove(entry);
        List<FolderEntry> aList = lineage.subList(0,entryLevel-1);
        List<FolderEntry> dList = lineage.subList(entryLevel-1,lineage.size());
        if (!includePreDeleted) {
        	aList = removePreDeletedFromList(aList);
        	dList = removePreDeletedFromList(dList);
        }
        model.put(ObjectKeys.FOLDER_ENTRY_ANCESTORS, aList);
        model.put(ObjectKeys.FOLDER_ENTRY_DESCENDANTS, dList);
        //Initialize users
        List<DefinableEntity> allE = new ArrayList(lineage);
        allE.add(entry);
    	getProfileDao().loadUserPrincipals(getPrincipalIds(allE), RequestContextHolder.getRequestContext().getZoneId(), false);
        return model;
    }
    private static List<FolderEntry> removePreDeletedFromList(List<FolderEntry> list) {
    	ArrayList<FolderEntry> reply = new ArrayList<FolderEntry>();
    	int count = ((null == list) ? 0 : list.size());
    	for (int i = 0; i < count; i += 1) {
    		FolderEntry fe = list.get(i);
    		if (!fe.isPreDeleted()) {
    			reply.add(fe);
    		}
    	}
    	return reply;
    }
         
    //***********************************************************************************************************   

	@Override
	public ChangeLog processChangeLog(DefinableEntity entry, String operation) {
		return processChangeLog(entry, operation, false, false);
	}

	@Override
	public ChangeLog processChangeLog(DefinableEntity entry, String operation, boolean skipDbLog, boolean skipNotifyStatus) {
		// Take care of ChangeLog
		ChangeLog changes = null;
		
		if (entry instanceof Binder) {
			changes = processChangeLog((Binder)entry, operation, skipDbLog);
		}
		else {
			if(!skipDbLog) {
				changes = ChangeLogUtils.createAndBuild(entry, operation);
				Element element = changes.getEntityRoot();
				//add folderEntry fields
				if (entry instanceof FolderEntry) {
					FolderEntry fEntry = (FolderEntry)entry;
					XmlUtils.addProperty(element, ObjectKeys.XTAG_FOLDERENTRY_DOCNUMBER, fEntry.getDocNumber());
					if (fEntry.getTopEntry() != null) XmlUtils.addProperty(element, ObjectKeys.XTAG_FOLDERENTRY_TOPENTRY, fEntry.getTopEntry().getId());
					if (fEntry.getParentEntry() != null) XmlUtils.addProperty(element, ObjectKeys.XTAG_FOLDERENTRY_PARENTENTRY, fEntry.getParentEntry().getId());
					if (!Validator.isNull(fEntry.getPostedBy())) XmlUtils.addProperty(element, ObjectKeys.XTAG_FOLDERENTRY_POSTEDBY, fEntry.getPostedBy());
				} else if (entry instanceof Binder) {
					if (operation.equals(ChangeLog.DELETEBINDER) || operation.equals(ChangeLog.PREDELETEBINDER)) {
						//Add the path so it can be shown in the activity reports
						XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_PATH, ((Binder)entry).getPathName());
					}
				}
				ChangeLogUtils.save(changes);
			}
		}
		
		// Take care of NotifyStatus
		if(!skipNotifyStatus) {
			if (entry instanceof FolderEntry) {
				FolderEntry fEntry = (FolderEntry)entry;
				if (!ChangeLog.DELETEENTRY.equals(operation)) {
					NotifyStatus status = getCoreDao().loadNotifyStatus(fEntry.getParentFolder(), fEntry);
					status.setLastModified(fEntry.getModification().getDate());
					logger.debug("AbstractFolderCoreProcessor.processChangeLog( Operation:  " + operation + " ): NotifyStatus modified: "+ ", Entity: " + fEntry.getId() + " (" + fEntry.getTitle() + ")");
					status.traceStatus(logger);
				}
			}
		}

		return changes;
	}
	//***********************************************************************************************************
    private Statistics getFolderStatistics(Folder folder) {
        CustomAttribute statisticsAttribute = folder.getCustomAttribute(Statistics.ATTRIBUTE_NAME);
        Statistics statistics = null;
        if (statisticsAttribute == null) {
        	statistics = new Statistics();
        } else {
        	 statistics = (Statistics)statisticsAttribute.getValue();
        }
        return statistics;
	}
    
    private void setFolderStatistics(Folder folder, Statistics statistics) {
        CustomAttribute statisticsAttribute = folder.getCustomAttribute(Statistics.ATTRIBUTE_NAME);
        if (statisticsAttribute != null) {
        	statisticsAttribute.setValue(null);
        	statisticsAttribute.setValue(statistics);
        } else {
        	folder.addCustomAttribute(Statistics.ATTRIBUTE_NAME, statistics);
        }
	}
    
    @Override
	protected List<Long> indexEntries_getEntryIds(Binder binder) {
		return getCoreDao().getFolderEntryIds(binder);
   	}

    @Override
    protected List<Entry> indexEntries_loadEntries(List<Long> ids, Long zoneId) {
    	return getCoreDao().loadObjects(ids, FolderEntry.class, zoneId);
    }
}
