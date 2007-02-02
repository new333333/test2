package com.sitescape.team.module.folder.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentHelper;
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
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.team.module.file.FilesErrors;
import com.sitescape.team.module.file.FilterException;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderCoreProcessor;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.util.FilterHelper;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderCoreProcessor extends AbstractEntryProcessor 
	implements FolderCoreProcessor {
  //***********************************************************************************************************	
    protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {  
    	Folder folder = (Folder)binder;
    	FolderEntry fEntry = (FolderEntry)entry;
    	getCoreDao().refresh(folder);
    	folder.addEntry((FolderEntry)entry);
    	if (inputData.exists(ObjectKeys.INPUT_FIELD_POSTING_FROM)) {
    		fEntry.setPostedBy(inputData.getSingleValue(ObjectKeys.INPUT_FIELD_POSTING_FROM)); 
    	}
    	super.addEntry_fillIn(folder, entry, inputData, entryData);
    	fEntry.updateLastActivity(fEntry.getModification().getDate());
   }
 
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {
    	super.addEntry_postSave(binder, entry, inputData, entryData);
    	getProfileDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).
							setSeen(entry);
    }
    protected void addEntry_done(Binder binder, Entry entry, InputDataAccessor inputData) {
       	super.addEntry_done(binder, entry, inputData);
       	if (entry instanceof AclControlled)
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(entry)); // Just for testing
    	else
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(binder)); // Just for testing
    }

    //***********************************************************************************************************
    public FolderEntry addReply(final FolderEntry parent, Definition def, final InputDataAccessor inputData, Map fileItems) 
   		throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
               
 
    	Map entryDataAll = addReply_toEntryData(parent, def, inputData, fileItems);
        final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
        List fileData = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
        try {
         	final FolderEntry entry = addReply_create(def);
         	        
         	Long lastParentVersion = parent.getLogVersion();
        	// The following part requires update database transaction.
        	getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		addReply_fillIn(parent, entry, inputData, entryData);
        		addReply_preSave(parent, entry, inputData, entryData);
        		addReply_save(parent, entry, inputData, entryData);
               	addReply_startWorkflow(entry);
           		addReply_postSave(parent, entry, inputData, entryData);
           	return null;
        	}});
        	//assume parent has been updated, index now
        	if (!lastParentVersion.equals(parent.getLogVersion())) {
    	     	// Do NOT use reindexEntry(entry) since it reindexes attached
    			// files as well. We want workflow state change to be lightweight
    			// and reindexing all attachments will be unacceptably costly.
    			// TODO (Roy, I believe this was your design idea, so please 
    			// verify that this strategy will indeed work). 

    			indexEntry(parent.getParentBinder(), parent, new ArrayList(), null, false);
        		
        	}
           	// Need entry id before filtering 
            FilesErrors filesErrors = addReply_filterFiles(parent.getParentFolder(), entry, entryData, fileData);
        	filesErrors = addReply_processFiles(parent, entry, fileData, filesErrors);
                 
        	addReply_indexAdd(parent, entry, inputData, fileData);
                
        	addReply_done(parent, entry, inputData);

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
        
    protected Map addReply_toEntryData(FolderEntry parent, Definition def, InputDataAccessor inputData, Map fileItems) {
     	return addEntry_toEntryData(parent.getParentBinder(), def, inputData, fileItems);
    }
    
    /**
     * Subclass must implement this.
     * @return
     */
    protected FolderEntry addReply_create(Definition def) {
    	try {
    		FolderEntry entry =  new FolderEntry();
           	entry.setEntryDef(def);
           	if (def != null) entry.setDefinitionType(new Integer(def.getType()));
           	return entry;
    	} catch (Exception ex) {
    		return null;
    	}
    	
    }

    protected FilesErrors addReply_filterFiles(Binder binder, Entry reply, 
    		Map entryData, List fileUploadItems) throws FilterException, TitleException {
    	return addEntry_filterFiles(binder, reply, entryData, fileUploadItems);
    }

    protected FilesErrors addReply_processFiles(FolderEntry parent, FolderEntry entry, 
    		List fileData, FilesErrors filesErrors) {
    	return addEntry_processFiles(parent.getParentBinder(), entry, fileData, filesErrors);
    }
    //inside write transaction
    protected void addReply_fillIn(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData) {  
        parent.addReply(entry);         
    	if (inputData.exists(ObjectKeys.INPUT_FIELD_POSTING_FROM)) {
    		entry.setPostedBy(inputData.getSingleValue(ObjectKeys.INPUT_FIELD_POSTING_FROM)); 
    	}
    	super.addEntry_fillIn(entry.getParentBinder(), entry, inputData, entryData);
    	entry.updateLastActivity(entry.getModification().getDate());
    }
    
    //inside write transaction
    protected void addReply_preSave(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData) {
    	addEntry_preSave(parent.getParentBinder(), entry, inputData, entryData);
    }
    
    //inside write transaction
    protected void addReply_save(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData) {
    	addEntry_save(parent.getParentBinder(), entry, inputData, entryData);
    }
    //inside write transaction
    protected void addReply_startWorkflow(FolderEntry entry) {
    	FolderEntry parent = entry.getParentEntry();
   		if (getWorkflowModule().modifyWorkflowStateOnReply(parent)) {
   	   		parent.incrLogVersion();
   			processChangeLog(parent, ChangeLog.MODIFYWORKFLOWSTATEONREPLY);
   		}
    	//Starting a workflow on a reply works the same as for the entry
    	addEntry_startWorkflow(entry);
    }
    
    //inside write transaction
    protected void addReply_postSave(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData) {
    	//will log addEntry
    	addEntry_postSave(parent.getParentBinder(), entry, inputData, entryData);

    }
    
    protected void addReply_done(Entry parent, Entry entry, InputDataAccessor inputData) {
    	addEntry_done(parent.getParentBinder(), entry, inputData);
    }

    protected void addReply_indexAdd(FolderEntry parent, FolderEntry entry, 
    		InputDataAccessor inputData, List fileData) {
    	addEntry_indexAdd(entry.getParentFolder(), entry, inputData, fileData);
    }
    
     //***********************************************************************************************************
   
 	protected void modifyEntry_postFillIn(Binder binder, Entry entry, 
 			InputDataAccessor inputData, Map entryData, Map<FileAttachment,String> fileRenamesTo) {
 		super.modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo);
		getProfileDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).setSeen(entry);
    	FolderEntry fEntry = (FolderEntry)entry;
		fEntry.updateLastActivity(fEntry.getModification().getDate());
   }
	protected void modifyEntry_done(Binder binder, Entry entry, InputDataAccessor inputData) { 
       	if (entry instanceof AclControlled)
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(entry)); // Just for testing
    	else
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(binder)); // Just for testing
 	}
    //***********************************************************************************************************

    protected Object deleteEntry_preDelete(Binder parentBinder, Entry entry, Object ctx) {
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
      	return replies;
    }
        
    protected Object deleteEntry_workflow(Binder parentBinder, Entry entry, Object ctx) {
       	List replies = (List)ctx;
    	List ids = new ArrayList();
      	for (int i=0; i<replies.size(); ++i) {
    		ids.add(((FolderEntry)replies.get(i)).getId());
    	}
      	ids.add(entry.getId());
      	//use optimized bulk delete
   		getFolderDao().deleteEntryWorkflows((Folder)parentBinder, ids);
        return ctx;
    }
    
    protected Object deleteEntry_processFiles(Binder parentBinder, Entry entry, Object ctx) {
    	List replies = (List)ctx;
       	for (int i=0; i<replies.size(); ++i) {
    		super.deleteEntry_processFiles(parentBinder, (FolderEntry)replies.get(i), null);
    	}
       	super.deleteEntry_processFiles(parentBinder, entry, null);
        return ctx;
    }
    
    protected Object deleteEntry_delete(Binder parentBinder, Entry entry, Object ctx) {
 
    	//use the optimized deleteEntry or hibernate deletes each collection entry one at a time
    	getFolderDao().deleteEntries((FolderEntry)entry, (List)ctx);   
      	return ctx;
    }
    protected Object deleteEntry_indexDel(Entry entry, Object ctx) {
    	List replies = (List)ctx;
      	for (int i=0; i<replies.size(); ++i) {
    		super.deleteEntry_indexDel((FolderEntry)replies.get(i), null);
    	}
		super.deleteEntry_indexDel(entry, null);
		return ctx;
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
    	from.removeEntry(fEntry);
    	to.addEntry(fEntry);
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
          	e.setOwningFolderSortKey(to.getFolderHKey().getSortKey());
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
    	reindexEntries(entries);
    }
    
    //***********************************************************************************************************
       
    protected SFQuery indexEntries_getQuery(Binder binder) {
        //do actual db query 
    	FilterControls filter = new FilterControls(ObjectKeys.FIELD_ENTITY_PARENTBINDER, binder);
        return (SFQuery)getFolderDao().queryEntries(filter);
   	}
 	protected void indexEntries_preIndex(Binder binder) {
 		super.indexEntries_preIndex(binder);
		getRssGenerator().deleteRssFile(binder); 		
 	}
 	protected void indexEntries_postIndex(Binder binder, Entry entry) {
 		super.indexEntries_postIndex(binder, entry);
 		if (entry instanceof AclControlled)
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(entry)); // Just for testing
 		else
 			getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(binder)); // Just for testing
	}
 	protected org.dom4j.Document getBinderEntries_getSearchDocument(Binder binder, 
    		String [] entryTypes, org.dom4j.Document searchFilter) {
    	  
    	if (searchFilter == null) {
    		//Build a null search filter
    		searchFilter = DocumentHelper.createDocument();
    		Element rootElement = searchFilter.addElement(FilterHelper.FilterRootName);
        	rootElement.addElement(FilterHelper.FilterTerms);
    	}
    	org.dom4j.Document qTree = FilterHelper.convertSearchFilterToSearchBoolean(searchFilter);
    	Element rootElement = qTree.getRootElement();
    	if (rootElement == null) return qTree;
    	//Find the first "and" element and add to it
    	Element boolElement = (Element) rootElement.selectSingleNode(QueryBuilder.AND_ELEMENT);
    	if (boolElement == null) {
    		//If there isn't one, then create one.
    		boolElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
    	}
    	boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
 
    	//Look only for entryType=entry
       	Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
       	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
       	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
       	child.setText(EntityIndexUtils.ENTRY_TYPE_ENTRY);
 
       	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);

    	//Look only for binderId=binder
    	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(binder.getId().toString());
    	return qTree;
 
    }
    //***********************************************************************************************************
          
    protected Entry entry_load(Binder parentBinder, Long entryId) {
        return folderDao.loadFolderEntry(parentBinder.getId(), entryId, parentBinder.getZoneId()); 
    }
         
    //***********************************************************************************************************
   
    protected Object deleteBinder_delete(Binder binder, Object ctx) {
    	//remove folder contents
    	getFolderDao().deleteEntryWorkflows((Folder)binder);
		getFolderDao().deleteEntries((Folder)binder);
		//finally delete the binder and its associations
		return super.deleteBinder_delete(binder, ctx);
    }

    protected Object deleteBinder_indexDel(Binder binder, Object ctx) {
    	indexEntries_deleteEntries(binder);
    	return super.deleteBinder_indexDel(binder, ctx);
    }

    //***********************************************************************************************************
    public void moveBinder(Binder source, Binder destination) {
    	if (destination instanceof Folder) 
    		moveFolderToFolder((Folder)source, (Folder)destination);
    	else if (destination instanceof Workspace) 
    		moveFolderToWorkspace((Folder)source, (Workspace)destination);
    	else throw new NotSupportedException(NLT.get("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()}));

    	 
      }
    public void moveFolderToFolder(Folder source, Folder destination) {
    	HKey oldKey = source.getFolderHKey();
    	//first remove name
    	getCoreDao().updateLibraryName(source.getParentBinder(), source, source.getTitle(), null);
    	source.getParentBinder().removeBinder(source);
    	destination.addFolder(source);
    	//now add name
    	getCoreDao().updateLibraryName(source.getParentBinder(), source, null, source.getTitle());
		// The path changes since its parent changed.    	
 		source.setPathName(destination.getPathName() + "/" + source.getTitle());
     	//create history - using timestamp and version from fillIn
        User user = RequestContextHolder.getRequestContext().getUser();
        moveLog(source, new HistoryStamp(user));

    	HKey newKey = source.getFolderHKey();
    	getFolderDao().moveEntries(source);
    	//fixup all children
    	List binders = source.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Folder child = (Folder)binders.get(i);
    		child.setTopFolder(source.getTopFolder());
     		child.setPathName(source.getPathName() + "/" + child.getTitle());
     		moveLog(child, source.getModification());
    		fixupMovedChild(child, oldKey, newKey);
    	}
    	indexTree(source, null);
    	
    }
    protected void fixupMovedChild(Folder child, HKey oldParent, HKey newParent) {
    	HKey oldKey = child.getFolderHKey();
    	String childSort = oldKey.getSortKey();
    	HKey newKey = new HKey(childSort.replaceFirst(oldParent.getSortKey(), newParent.getSortKey()));
    	child.setFolderHKey(newKey);
    	getFolderDao().moveEntries(child);
    	List binders = child.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Folder c = (Folder)binders.get(i);
    		c.setTopFolder(child.getTopFolder());
     		c.setPathName(child.getPathName() + "/" + child.getTitle());
     		moveLog(c, child.getModification());
    		fixupMovedChild(c, oldKey, newKey);
    	}
   	
    }
    private void moveLog(Binder binder, HistoryStamp stamp) {
    	binder.setModification(stamp);
 		binder.incrLogVersion();
 		ChangeLog changes = new ChangeLog(binder, ChangeLog.MOVEBINDER);
    	changes.getEntityRoot();
    	getCoreDao().save(changes);

    }
    public void moveFolderToWorkspace(Folder source, Workspace destination) {
      	if (destination.isZone())
      		throw new NotSupportedException(NLT.get("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()}));
      	HKey oldKey = source.getFolderHKey();
       	//first remove name
    	getCoreDao().updateLibraryName(source.getParentBinder(), source, source.getTitle(), null);
     	source.getParentBinder().removeBinder(source);
    	destination.addFolder(source);
    	//now add name
    	getCoreDao().updateLibraryName(source.getParentBinder(), source, null, source.getTitle());
		// The path changes since its parent changed.    	
 		source.setPathName(destination.getPathName() + "/" + source.getTitle());
     	//create history - using timestamp and version from fillIn
        User user = RequestContextHolder.getRequestContext().getUser();
        moveLog(source, new HistoryStamp(user));
 
    	source.setTopFolder(null);
    	HKey newKey = source.getFolderHKey();
    	getFolderDao().moveEntries(source);
    	//fixup all children
    	List binders = source.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Folder child = (Folder)binders.get(i);
    		child.setTopFolder(source);
     		child.setPathName(source.getPathName() + "/" + child.getTitle());
    		moveLog(child, source.getModification());
    		fixupMovedChild(child, oldKey, newKey);
    	}
    	indexTree(source, null);
   	
    }
    //***********************************************************************************************************
    protected void loadEntryHistory(Entry entry) {
    	FolderEntry fEntry = (FolderEntry)entry;
        Set ids = new HashSet();
        if (fEntry.getCreation() != null)
            ids.add(fEntry.getCreation().getPrincipal().getId());
        if (fEntry.getModification() != null)
            ids.add(fEntry.getModification().getPrincipal().getId());
        if (fEntry.getReservation() != null) 
            ids.add(fEntry.getReservation().getPrincipal().getId());
        getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId());
     } 

    protected void loadEntryHistory(HashMap entry) {
        Set ids = new HashSet();
        if (entry.get(EntityIndexUtils.CREATORID_FIELD) != null)
    	    try {ids.add(new Long((String)entry.get(EntityIndexUtils.CREATORID_FIELD)));
    	    } catch (Exception ex) {};
    	if (entry.get(EntityIndexUtils.MODIFICATIONID_FIELD) != null) 
    		try {ids.add(new Long((String)entry.get(EntityIndexUtils.MODIFICATIONID_FIELD)));
    		} catch (Exception ex) {};
        if (entry.get(IndexUtils.RESERVEDBYID_FIELD) != null) 
    		try {ids.add(new Long((String)entry.get(IndexUtils.RESERVEDBYID_FIELD)));
    	    } catch (Exception ex) {};
        getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId());
     } 
    protected List loadEntryHistoryLuc(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        HashMap entry;
        while (iter.hasNext()) {
            entry = (HashMap)iter.next();
            if (entry.get(EntityIndexUtils.CREATORID_FIELD) != null)
        	    try {ids.add(new Long((String)entry.get(EntityIndexUtils.CREATORID_FIELD)));
           	    } catch (Exception ex) {};
           if (entry.get(EntityIndexUtils.MODIFICATIONID_FIELD) != null) 
        		try {ids.add(new Long((String)entry.get(EntityIndexUtils.MODIFICATIONID_FIELD)));
        		} catch (Exception ex) {};
   	       if (entry.get(IndexUtils.RESERVEDBYID_FIELD) != null) 
        		try {ids.add(new Long((String)entry.get(IndexUtils.RESERVEDBYID_FIELD)));
	    	} catch (Exception ex) {};
        }
        return getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId());
     }   

    protected void loadEntryHistory(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        FolderEntry entry;
        while (iter.hasNext()) {
            entry = (FolderEntry)iter.next();
            if (entry.getCreation() != null)
                ids.add(entry.getCreation().getPrincipal().getId());
            if (entry.getModification() != null)
                ids.add(entry.getModification().getPrincipal().getId());
            if (entry.getReservation() != null) 
                ids.add(entry.getReservation().getPrincipal().getId());
        }
        getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId());
     }     
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, List tags) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry, tags);
    	FolderEntry fEntry = (FolderEntry)entry;        
        // Add Doc number
        IndexUtils.addDocNumber(indexDoc, fEntry);

        // Add sortable Doc number
        IndexUtils.addSortNumber(indexDoc, fEntry);

        // Add the folder Id
        IndexUtils.addFolderId(indexDoc, (Folder)binder);
        //add last activity for top entries
        if (fEntry.isTop()) IndexUtils.addLastActivityDate(indexDoc, fEntry);

        return indexDoc;
    }
       
 
    //***********************************************************************************************************
          

    public Map getEntryTree(Folder parentFolder, FolderEntry entry) {
    	int entryLevel;
    	List lineage;
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
        List allE = new ArrayList(lineage);
        allE.add(entry);
        loadEntryHistory(allE);
        return model;
    }
         
    //***********************************************************************************************************   

    protected String getEntryPrincipalField() {
    	return EntityIndexUtils.CREATORID_FIELD;
    }

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
