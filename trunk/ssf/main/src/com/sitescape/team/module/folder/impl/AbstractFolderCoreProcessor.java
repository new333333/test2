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
package com.sitescape.team.module.folder.impl;

import java.util.ArrayList;
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
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.FolderHierarchyException;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.HistoryStamp;
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
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.NLT;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderCoreProcessor extends AbstractEntryProcessor 
	implements FolderCoreProcessor {
  //***********************************************************************************************************	
    protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
    	Folder folder = (Folder)binder;
    	FolderEntry fEntry = (FolderEntry)entry;
    	getCoreDao().refresh(folder);
    	folder.addEntry((FolderEntry)entry);
    	if (inputData.exists(ObjectKeys.INPUT_FIELD_POSTING_FROM)) {
    		fEntry.setPostedBy(inputData.getSingleValue(ObjectKeys.INPUT_FIELD_POSTING_FROM)); 
    	}
    	super.addEntry_fillIn(folder, entry, inputData, entryData, ctx);
    	fEntry.updateLastActivity(fEntry.getModification().getDate());
   }
 
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	super.addEntry_postSave(binder, entry, inputData, entryData, ctx);
    	getProfileDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).
							setSeen(entry);
    }
    protected void addEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
       	super.addEntry_done(binder, entry, inputData, ctx);
   		getRssGenerator().updateRssFeed(entry); 
     }

    //***********************************************************************************************************
    public FolderEntry addReply(final FolderEntry parent, Definition def, final InputDataAccessor inputData, Map fileItems) 
   		throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
               
    	final Map ctx = new HashMap();

    	Map entryDataAll = addReply_toEntryData(parent, def, inputData, fileItems, ctx);
        final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
        List fileData = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
        try {
         	final FolderEntry entry = addReply_create(def, ctx);
          	Long lastParentVersion = parent.getLogVersion();
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
        	//assume parent has been updated, index now
        	if (!lastParentVersion.equals(parent.getLogVersion())) {
    			indexEntry(parent);
        		
        	}
           	// Need entry id before filtering 
            FilesErrors filesErrors = addReply_filterFiles(parent.getParentFolder(), entry, entryData, fileData, ctx);
        	filesErrors = addReply_processFiles(parent, entry, fileData, filesErrors, ctx);
                 
        	addReply_indexAdd(parent, entry, inputData, fileData, ctx);
                
        	addReply_done(parent, entry, inputData, ctx);

        	if(filesErrors.getProblems().size() > 0) {
        		// 	At least one error occured during the operation. 
        		throw new WriteFilesException(filesErrors);
        	}
        	else {
        		return entry;
        	}
    	} finally {
        	cleanupFiles(fileData);
    		
    	}
    }
        
    protected Map addReply_toEntryData(FolderEntry parent, Definition def, InputDataAccessor inputData, Map fileItems, Map ctx) {
     	return addEntry_toEntryData(parent.getParentBinder(), def, inputData, fileItems, ctx);
    }
    
    /**
     * Subclass must implement this.
     * @return
     */
    protected FolderEntry addReply_create(Definition def, Map ctx) {
    	return (FolderEntry)addEntry_create(def, FolderEntry.class, ctx);    	
    }

    protected FilesErrors addReply_filterFiles(Binder binder, Entry reply, 
    		Map entryData, List fileUploadItems, Map ctx) throws FilterException, TitleException {
    	return addEntry_filterFiles(binder, reply, entryData, fileUploadItems, ctx);
    }

    protected FilesErrors addReply_processFiles(FolderEntry parent, FolderEntry entry, 
    		List fileData, FilesErrors filesErrors, Map ctx) {
    	return addEntry_processFiles(parent.getParentBinder(), entry, fileData, filesErrors, ctx);
    }
    //inside write transaction
    protected void addReply_fillIn(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
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
    
    protected void addReply_done(Entry parent, Entry entry, InputDataAccessor inputData, Map ctx) {
    	addEntry_done(parent.getParentBinder(), entry, inputData, ctx);
    }

    protected void addReply_indexAdd(FolderEntry parent, FolderEntry entry, 
    		InputDataAccessor inputData, List fileData, Map ctx) {
    	addEntry_indexAdd(entry.getParentFolder(), entry, inputData, fileData, ctx);
    	//Also re-index the top entry (to catch the change in lastActivity)
    	indexEntry(entry.getTopEntry());
    }
    
     //***********************************************************************************************************

	protected void modifyEntry_postFillIn(Binder binder, Entry entry, 
 			InputDataAccessor inputData, Map entryData, Map<FileAttachment,String> fileRenamesTo, Map ctx) {
 		super.modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo, ctx);
		getProfileDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).setSeen(entry);
    	FolderEntry fEntry = (FolderEntry)entry;
		fEntry.updateLastActivity(fEntry.getModification().getDate());
   }
	protected void modifyEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
    	getRssGenerator().updateRssFeed(entry);
 	}
    //***********************************************************************************************************
    public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState) {
    	super.modifyWorkflowState(binder, entry, tokenId, toState);
       	getRssGenerator().updateRssFeed(entry);
           	
    }
    protected Map deleteEntry_setCtx(Entry entry, Map ctx) {
    	//need context to pass replies
    	if (ctx == null) ctx = new HashMap();
    	return super.deleteEntry_setCtx(entry, ctx);
    }
    protected void deleteEntry_preDelete(Binder parentBinder, Entry entry, Map ctx) {
    	super.deleteEntry_preDelete(parentBinder, entry, ctx);
      	//pass replies along as context so we can delete them all at once
     	//load in reverse hkey order so foreign keys constraints are handled correctly
       	FolderEntry fEntry = (FolderEntry)entry;
     	List<FolderEntry> replies= getFolderDao().loadEntryDescendants(fEntry);
      	//repeat pre-delete for each reply
      	for (int i=0; i<replies.size(); ++i) {
      		FolderEntry reply = (FolderEntry)replies.get(i);
    		super.deleteEntry_preDelete(parentBinder, reply, null);
    		reply.updateLastActivity(reply.getModification().getDate());
    	}
      	fEntry.updateLastActivity(fEntry.getModification().getDate());
      	ctx.put(this.getClass().getName(), replies);
    }
        
    protected void deleteEntry_workflow(Binder parentBinder, Entry entry, Map ctx) {
    	if (parentBinder.isDeleted()) return;  //will handle in bulk way
    	List replies = (List)ctx.get(this.getClass().getName());
    	List ids = new ArrayList();
      	for (int i=0; i<replies.size(); ++i) {
    		ids.add(((FolderEntry)replies.get(i)).getId());
    	}
      	ids.add(entry.getId());
      	//use optimized bulk delete
   		getFolderDao().deleteEntryWorkflows((Folder)parentBinder, ids);
    }
    
    protected void deleteEntry_processFiles(Binder parentBinder, Entry entry, boolean deleteMirroredSource, Map ctx) {
    	List replies = (List)ctx.get(this.getClass().getName());
       	for (int i=0; i<replies.size(); ++i) {
    		super.deleteEntry_processFiles(parentBinder, (FolderEntry)replies.get(i), deleteMirroredSource, null);
    	}
       	super.deleteEntry_processFiles(parentBinder, entry, deleteMirroredSource, null);
    }
    
    protected void deleteEntry_delete(Binder parentBinder, Entry entry, Map ctx) {
       	if (parentBinder.isDeleted()) return;  //will handle in bulk way
        //use the optimized deleteEntry or hibernate deletes each collection entry one at a time
       	List entries = new ArrayList((List)ctx.get(this.getClass().getName()));
       	entries.add(entry);
       	if (!entry.isTop()) {
       		//remove from list of children
       		FolderEntry fEntry = (FolderEntry)entry;
       		fEntry.getParentEntry().removeReply(fEntry);
       	}
       	getFolderDao().deleteEntries((Folder)parentBinder, entries);   
    }
    protected void deleteEntry_indexDel(Binder parentBinder, Entry entry, Map ctx) {
       	if (parentBinder.isDeleted());  //will handle in bulk way
        List replies = (List)ctx.get(this.getClass().getName());
      	for (int i=0; i<replies.size(); ++i) {
    		super.deleteEntry_indexDel(parentBinder, (FolderEntry)replies.get(i), null);
    	}
		super.deleteEntry_indexDel(parentBinder, entry, null);
   }
    
    //***********************************************************************************************************
    public void moveEntry(Binder binder, Entry entry, Binder destination) {
    	Folder from = (Folder)binder;
    	if (!(destination instanceof Folder))
    		throw new NotSupportedException(NLT.get("errorcode.notsupported.moveEntryDestination", new String[] {destination.getPathName()}));
    	Folder to = (Folder)destination;
    	FolderEntry fEntry = (FolderEntry)entry;
    	if (fEntry.getTopEntry() != null)
    		throw new NotSupportedException(NLT.get("errorcode.notsupported.moveReply"));
    	HKey oldKey = fEntry.getHKey();
    	//get Children
    	List entries = getFolderDao().loadEntryDescendants(fEntry);
    	//only log top leve titles
   		if (from.isUniqueTitles()) getCoreDao().updateTitle(from, entry, entry.getNormalTitle(), null);		
   	    from.removeEntry(fEntry);
    	to.addEntry(fEntry);
   		if (to.isUniqueTitles()) getCoreDao().updateTitle(to, entry, null, entry.getNormalTitle());		

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
    public void deleteBinder(Binder binder, boolean deleteMirroredSource) {
    	if (!binder.isDeleted()) super.deleteBinder(binder, deleteMirroredSource);
    	else {
    		final Folder folder = (Folder)binder;
    		final FilterControls fc = new FilterControls(ObjectKeys.FIELD_ENTITY_PARENTBINDER, folder);
    		
    		//loop through all entries and record delete
			Boolean done=Boolean.FALSE;
			while (!done) {
				done = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						SFQuery query = getFolderDao().queryEntries(fc); 
						try {
							int count = 0;
							while (query.hasNext()) {
			      				Object obj = query.next();
			       				if (obj instanceof Object[])
			       					obj = ((Object [])obj)[0];
			 					FolderEntry entry = (FolderEntry)obj;
					    		//create history - using timestamp and version from folder delete
								try {
									entry.setModification(folder.getModification());
									entry.incrLogVersion();
									processChangeLog(entry, ChangeLog.DELETEENTRY);
								} catch (Exception ex) {
									logger.warn("Error logging entry " + entry.toString(), ex);
								}
								try {
									getFileModule().deleteFiles(folder, entry, false, null);
								} catch (Exception ex) {
									// but keep going
									logger.warn("Error delete files for entry " + entry.toString() , ex);
								}
								//we handle the entry in a bulk delete with the folder, don't let hibernate 
								getCoreDao().evict(entry);
								++count;
								//commit after 100
								if (count == 100) return Boolean.FALSE;
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
							getFolderDao().deleteEntryWorkflows(folder);
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
    
    protected void deleteBinder_processFiles(Binder binder, Map ctx) {
    	//save for background

    }
    public void deleteBinder_delete(Binder binder, boolean deleteMirroredSource, Map ctx) {
      	if (!binder.isRoot()) {
    		binder.getParentBinder().removeBinder(binder);
    	}
    	//mark for delete now and continue in the background
    	binder.setDeleted(true);
    }

 
    //***********************************************************************************************************
    public void moveBinder(Binder source, Binder destination) {
    	if ((destination instanceof Folder) || (destination instanceof Workspace)) 
    		super.moveBinder(source, destination);
    	else throw new NotSupportedException(NLT.get("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()}));
   	 
    }
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
        IndexUtils.addDocNumber(indexDoc, fEntry);

        // Add sortable Doc number
        IndexUtils.addSortNumber(indexDoc, fEntry);
        // Add ReservedBy Principal Id
       	IndexUtils.addReservedByPrincipalId(indexDoc, fEntry);

        // Add the folder Id
        IndexUtils.addFolderId(indexDoc, (Folder)binder);
        //add last activity for top entries
        if (fEntry.isTop()) IndexUtils.addLastActivityDate(indexDoc, fEntry);

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

			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FOLDERENTRY_DOCNUMBER, fEntry.getDocNumber());
			if (fEntry.getTopEntry() != null) ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FOLDERENTRY_TOPENTRY, fEntry.getTopEntry().getId());
			if (fEntry.getParentEntry() != null) ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FOLDERENTRY_PARENTENTRY, fEntry.getParentEntry().getId());
			if (!Validator.isNull(fEntry.getPostedBy())) ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FOLDERENTRY_POSTEDBY, fEntry.getPostedBy());
		}
		getCoreDao().save(changes);
		return changes;
	}
}
