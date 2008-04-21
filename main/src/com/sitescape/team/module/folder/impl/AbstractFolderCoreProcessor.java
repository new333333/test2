/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.folder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.FolderHierarchyException;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.Statistics;
import com.sitescape.team.domain.TitleException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.team.module.file.FilesErrors;
import com.sitescape.team.module.file.FilterException;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderCoreProcessor;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderCoreProcessor extends AbstractEntryProcessor 
	implements FolderCoreProcessor {
  //***********************************************************************************************************	
    //inside write transaction
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
    	if (fEntry.isTop()) {
    		Statistics statistics = getFolderStatistics(folder);
    		statistics.addStatistics(entry.getEntryDef(), entry.getCustomAttributes());
    		setFolderStatistics(folder, statistics);
    	}
    }
    //inside write transaction
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	super.addEntry_postSave(binder, entry, inputData, entryData, ctx);
    }
    //no transaction
    protected void addEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
       	super.addEntry_done(binder, entry, inputData, ctx);
   		getRssModule().updateRssFeed(entry); 
     }
 
    //***********************************************************************************************************
    //no transaction    
    public FolderEntry addReply(final FolderEntry parent, Definition def, final InputDataAccessor inputData, Map fileItems, Map options) 
   		throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
               
    	final Map ctx = new HashMap();
    	if (options != null) ctx.putAll(options);

    	Map entryDataAll = addReply_toEntryData(parent, def, inputData, fileItems, ctx);
        final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
        List fileData = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
        try {
         	final FolderEntry entry = addReply_create(def, ctx);
        	// The following part requires update database transaction.
        	getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		addReply_fillIn(parent, entry, inputData, entryData, ctx);
        		addReply_preSave(parent, entry, inputData, entryData, ctx);
        		addReply_save(parent, entry, inputData, entryData, ctx);
               	addReply_startWorkflow(entry, ctx);
           		addReply_postSave(parent, entry, inputData, entryData, ctx);
           	return null;
        	}});
           	// Need entry id before filtering 
            FilesErrors filesErrors = addReply_filterFiles(parent.getParentFolder(), entry, entryData, fileData, ctx);
        	filesErrors = addReply_processFiles(parent, entry, fileData, filesErrors, ctx);
                 
        	addReply_indexAdd(parent, entry, inputData, fileData, ctx);
                
        	addReply_done(parent, entry, inputData, ctx);

        	if(filesErrors.getProblems().size() > 0) {
        		// 	At least one error occured during the operation. 
        		throw new WriteFilesException(filesErrors, entry.getId());
        	}
        	else {
        		return entry;
        	}
    	} finally {
        	cleanupFiles(fileData);
    		
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
    protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
    	Statistics statistics = getFolderStatistics((Folder)binder);
    	statistics.deleteStatistics(entry.getEntryDef(), entry.getCustomAttributes());
    	super.modifyEntry_fillIn(binder, entry, inputData, entryData, ctx);
    }
    //inside write transaction
	protected void modifyEntry_postFillIn(Binder binder, Entry entry, 
 			InputDataAccessor inputData, Map entryData, Map<FileAttachment,String> fileRenamesTo, Map ctx) {
 		super.modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo, ctx);
    	FolderEntry fEntry = (FolderEntry)entry;
		fEntry.updateLastActivity(fEntry.getModification().getDate());
    	Statistics statistics = getFolderStatistics((Folder)binder);
        statistics.addStatistics(entry.getEntryDef(), entry.getCustomAttributes());
        setFolderStatistics((Folder)binder, statistics);
  }
    //no transaction
	protected void modifyEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
    	getRssModule().updateRssFeed(entry);
 	}
    protected void modifyEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems, 
    		Collection<FileAttachment> filesToIndex, Map ctx) {
 
  	   if (ctx != null && Boolean.TRUE.equals(ctx.get(ObjectKeys.INPUT_OPTION_NO_INDEX))) return;
  	   super.modifyEntry_indexAdd(binder, entry, inputData, fileUploadItems, filesToIndex, ctx);
       	//Also re-index the top entry (to catch the change in lastActivity)
    	FolderEntry fEntry = (FolderEntry)entry;
    	if (!fEntry.isTop()) indexEntry(fEntry.getTopEntry());
    }

    //***********************************************************************************************************
    //inside write transaction    
    protected Map deleteEntry_setCtx(Entry entry, Map ctx) {
    	//need context to pass replies
    	if (ctx == null) ctx = new HashMap();
    	return super.deleteEntry_setCtx(entry, ctx);
    }
    //inside write transaction
    protected void deleteEntry_preDelete(Binder parentBinder, Entry entry, Map ctx) {
        Statistics statistics = getFolderStatistics((Folder)parentBinder);        
        statistics.deleteStatistics(entry.getEntryDef(), entry.getCustomAttributes());
    	super.deleteEntry_preDelete(parentBinder, entry, ctx);
      	//pass replies along as context so we can delete them all at once
     	//load in reverse hkey order so foreign keys constraints are handled correctly
       	FolderEntry fEntry = (FolderEntry)entry;
       	FolderEntry top = fEntry.getTopEntry();
       	ctx.put("this.topEntry", top);
     	List<FolderEntry> replies= getFolderDao().loadEntryDescendants(fEntry);
      	//repeat pre-delete for each reply
      	for (int i=0; i<replies.size(); ++i) {
      		FolderEntry reply = (FolderEntry)replies.get(i);
            statistics.deleteStatistics(reply.getEntryDef(), reply.getCustomAttributes());
    		super.deleteEntry_preDelete(parentBinder, reply, null);
    		reply.updateLastActivity(reply.getModification().getDate());
    	}
      	fEntry.updateLastActivity(fEntry.getModification().getDate());
        setFolderStatistics((Folder)parentBinder, statistics);
        ctx.put("this.replies", replies);
      	
    }
    //inside write transaction    
    protected void deleteEntry_workflow(Binder parentBinder, Entry entry, Map ctx) {
    	//folder Dao will handle
    }
    //inside write transaction    
    protected void deleteEntry_processFiles(Binder parentBinder, Entry entry, boolean deleteMirroredSource, Map ctx) {
    	List replies = (List)ctx.get("this.replies");
       	for (int i=0; i<replies.size(); ++i) {
    		super.deleteEntry_processFiles(parentBinder, (FolderEntry)replies.get(i), deleteMirroredSource, null);
    	}
       	super.deleteEntry_processFiles(parentBinder, entry, deleteMirroredSource, null);
    }
    
    //inside write transaction    
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
    protected void deleteEntry_indexDel(Binder parentBinder, Entry entry, Map ctx) {
       	if (parentBinder.isDeleted());  //will handle in bulk way
        List replies = (List)ctx.get("this.replies");
      	if (replies != null) {
      		for (int i=0; i<replies.size(); ++i) {
      			super.deleteEntry_indexDel(parentBinder, (FolderEntry)replies.get(i), null);
      		}
    	}
		super.deleteEntry_indexDel(parentBinder, entry, null);
		FolderEntry top = (FolderEntry)ctx.get("this.topEntry");
		if (top != null) indexEntry(top);
		
   }
    //***********************************************************************************************************
    //inside write transaction
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
   public void setWorkflowResponse(Binder binder, Entry entry, Long stateId, InputDataAccessor inputData)  {
	   Long version = entry.getLogVersion();
	   super.setWorkflowResponse(binder, entry, stateId, inputData);
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
    //inside write transaction    
    public void moveEntry(Binder binder, Entry entry, Binder destination) {
       	if (binder.equals(destination)) return;
       	Folder from = (Folder)binder;
    	if (!(destination instanceof Folder))
    		throw new NotSupportedException("errorcode.notsupported.moveEntryDestination", new String[] {destination.getPathName()});
    	Folder to = (Folder)destination;
    	FolderEntry fEntry = (FolderEntry)entry;
    	if (fEntry.getTopEntry() != null)
    		throw new NotSupportedException("errorcode.notsupported.moveReply");
    	HKey oldKey = fEntry.getHKey();
    	//get Children
    	List entries = getFolderDao().loadEntryDescendants(fEntry);
    	//only log top leve titles
   		if (from.isUniqueTitles()) getCoreDao().updateTitle(from, entry, entry.getNormalTitle(), null);		
   	    from.removeEntry(fEntry);
    	to.addEntry(fEntry);
   		if (to.isUniqueTitles()) getCoreDao().updateTitle(to, entry, null, entry.getNormalTitle());		
        Statistics statistics = getFolderStatistics(from);        
        statistics.deleteStatistics(entry.getEntryDef(), entry.getCustomAttributes());
        setFolderStatistics(from, statistics);
        statistics = getFolderStatistics((Folder)destination);
        statistics.addStatistics(entry.getEntryDef(), entry.getCustomAttributes());
        setFolderStatistics((Folder)destination, statistics);

    	User user = RequestContextHolder.getRequestContext().getUser();

        fEntry.setModification(new HistoryStamp(user));
        fEntry.incrLogVersion();
        //just log new location
    	ChangeLog changes = new ChangeLog(fEntry, ChangeLog.MOVEENTRY);
    	changes.getEntityRoot();
    	getCoreDao().save(changes);

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
        	changes = new ChangeLog(e, ChangeLog.MOVEENTRY);
        	changes.getEntityRoot();
        	getCoreDao().save(changes);
         	ids.add(e.getId());
    	}
    	//add top entry to list of entries
    	ids.add(fEntry.getId());
    	//write out changes before bulk updates
    	getCoreDao().flush();
    	getFolderDao().moveEntries(to,ids);
    	entries.add(fEntry);
    	// Move files in the entries
    	moveFiles(binder, entries, destination);
    	//finally remove from index and reAdd.
    	indexEntries(entries);
    }
    
    //***********************************************************************************************************
       
    protected SFQuery indexEntries_getQuery(Binder binder) {
        //do actual db query 
    	FilterControls filter = new FilterControls(ObjectKeys.FIELD_ENTITY_PARENTBINDER, binder);
        return getFolderDao().queryEntries(filter);
   	}
 	protected void indexEntries_postIndex(Binder binder, Entry entry) {
 		super.indexEntries_postIndex(binder, entry);
	}
 
    //***********************************************************************************************************
          
    protected Entry entry_load(Binder parentBinder, Long entryId) {
        return folderDao.loadFolderEntry(parentBinder.getId(), entryId, parentBinder.getZoneId()); 
    }
         
    //***********************************************************************************************************
    //inside write transaction    
   public void deleteBinder(Binder binder, boolean deleteMirroredSource) {
    	if(logger.isDebugEnabled())
    		logger.debug("Deleting binder [" + binder.getPathName() + "]");
    	if (!binder.isDeleted()) super.deleteBinder(binder, deleteMirroredSource);
    	else {
    		//if binder is marked deleted, we are called from cleanup code without a transation 
    		final Folder folder = (Folder)binder;
    		final FilterControls filter = new FilterControls(
    				new String[] {ObjectKeys.FIELD_ENTITY_PARENTBINDER, ObjectKeys.FIELD_ENTITY_DELETED},
					new Object[] {binder, Boolean.FALSE});

    		
    		//loop through all entries and record delete
			Boolean done=Boolean.FALSE;
			while (!done) {
				done = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						SFQuery query = getFolderDao().queryEntries(filter); 
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
								try {
									entry.setModification(folder.getModification());
									entry.incrLogVersion();
									processChangeLog(entry, ChangeLog.DELETEENTRY);
								} catch (Exception ex) {
									logger.warn("Error logging entry " + entry.toString(), ex);
								}
								
								getFileModule().deleteFiles(folder, entry, false, null);

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
								getFileModule().deleteFiles(folder, folder, false, null);
							} catch (Exception ex) {
								logger.warn("Error delete files: " + folder.getPathName(), ex);
							}
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
    protected void deleteBinder_processFiles(Binder binder, Map ctx) {
    	//save for background

    }
    //inside write transaction    
   public void deleteBinder_delete(Binder binder, boolean deleteMirroredSource, Map ctx) {
      	if (!binder.isRoot()) {
    		binder.getParentBinder().removeBinder(binder);
    	}
    	//mark for delete now and continue in the background
    	binder.setDeleted(true);
    }

 
    //***********************************************************************************************************
   //inside write transaction    
    public void moveBinder(Binder source, Binder destination) {
    	if ((destination instanceof Folder) || (destination instanceof Workspace)) 
    		super.moveBinder(source, destination);
    	else throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
   	 
    }
    //inside write transaction    
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
    public Set getPrincipalIds(DefinableEntity entity) {
    	Set ids = super.getPrincipalIds(entity);
    	if (entity instanceof FolderEntry) {
    		FolderEntry fEntry = (FolderEntry)entity;
    		if (fEntry.getReservation() != null) 
    			ids.add(fEntry.getReservation().getPrincipal().getId());
    	}
    	return ids;
    } 

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

    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, List tags) {
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
          

    public Map getEntryTree(Folder parentFolder, FolderEntry entry) {
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
        model.put(ObjectKeys.FOLDER_ENTRY_ANCESTORS, lineage.subList(0,entryLevel-1));
        model.put(ObjectKeys.FOLDER_ENTRY_DESCENDANTS, lineage.subList(entryLevel-1,lineage.size()));
        //Initialize users
        List<DefinableEntity> allE = new ArrayList(lineage);
        allE.add(entry);
    	getProfileDao().loadPrincipals(getPrincipalIds(allE), RequestContextHolder.getRequestContext().getZoneId(), false);
        return model;
    }
         
    //***********************************************************************************************************   


	public ChangeLog processChangeLog(DefinableEntity entry, String operation) {
		if (entry instanceof Binder) return processChangeLog((Binder)entry, operation);
		ChangeLog changes = new ChangeLog(entry, operation);
		Element element = ChangeLogUtils.buildLog(changes, entry);
		//add folderEntry fields
		if (entry instanceof FolderEntry) {
			FolderEntry fEntry = (FolderEntry)entry;

			XmlUtils.addProperty(element, ObjectKeys.XTAG_FOLDERENTRY_DOCNUMBER, fEntry.getDocNumber());
			if (fEntry.getTopEntry() != null) XmlUtils.addProperty(element, ObjectKeys.XTAG_FOLDERENTRY_TOPENTRY, fEntry.getTopEntry().getId());
			if (fEntry.getParentEntry() != null) XmlUtils.addProperty(element, ObjectKeys.XTAG_FOLDERENTRY_PARENTENTRY, fEntry.getParentEntry().getId());
			if (!Validator.isNull(fEntry.getPostedBy())) XmlUtils.addProperty(element, ObjectKeys.XTAG_FOLDERENTRY_POSTEDBY, fEntry.getPostedBy());
		}
		getCoreDao().save(changes);
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
//        	statistics = new Statistics();
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
}
