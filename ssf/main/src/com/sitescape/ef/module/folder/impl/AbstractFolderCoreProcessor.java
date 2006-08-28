package com.sitescape.ef.module.folder.impl;

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
//import org.dom4j.Document;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.FolderHierarchyException;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.HKey;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.InternalException;
import com.sitescape.ef.NotSupportedException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.module.binder.AccessUtils;
import com.sitescape.ef.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderCoreProcessor extends AbstractEntryProcessor 
	implements FolderCoreProcessor {
    
    //***********************************************************************************************************	
    protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {  
    	Folder folder = (Folder)binder;
    	getCoreDao().refresh(folder);
    	folder.addEntry((FolderEntry)entry);         
    	super.addEntry_fillIn(folder, entry, inputData, entryData);
   }
 
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {
    	getProfileDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).
							setSeen(entry);
    	if (entry instanceof AclControlled)
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(entry)); // Just for testing
    	else
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(binder)); // Just for testing
     }

	 protected void modifyEntry_postFillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {
		 getProfileDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).setSeen(entry);
     }

 	protected SFQuery indexEntries_getQuery(Binder binder) {
        //do actual db query 
    	FilterControls filter = new FilterControls("parentBinder", binder);
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
          
    protected Entry entry_load(Binder parentBinder, Long entryId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        return folderDao.loadFolderEntry(parentBinder.getId(), entryId, user.getZoneName()); 
    }
         
     protected Object deleteEntry_preDelete(Binder parentBinder, Entry entry) {
       	//pass replies along as context so we can delete them all at once
       	return getFolderDao().loadEntryDescendants((FolderEntry)entry); 
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
    	List entries = new ArrayList((List)ctx);
    	entries.add(entry);
    	getFolderDao().deleteEntries(entries);   
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
    public void moveBinder(Binder source, Binder destination) {
    	if (destination instanceof Folder) 
    		moveFolderToFolder((Folder)source, (Folder)destination);
    	else if (destination instanceof Workspace) 
    		moveFolderToWorkspace((Folder)source, (Workspace)destination);
    	else throw new InternalException("Cannot move folder");
    	 
      }
    public void moveFolderToFolder(Folder source, Folder destination) {
    	checkFolderMoveType(source.getDefinitionType(), destination.getDefinitionType());
    	HKey oldKey = source.getFolderHKey();
    	source.getParentBinder().removeBinder(source);
    	destination.addFolder(source);
    	HKey newKey = source.getFolderHKey();
    	getFolderDao().moveEntries(source);
    	//fixup all children
    	List binders = source.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Folder child = (Folder)binders.get(i);
    		child.setTopFolder(source.getTopFolder());
    		fixupMovedChild(child, oldKey, newKey);
    	}
    	//TODO: need to reindex binder only??  its parent will be different.
    	
    }
    protected void checkFolderMoveType(Integer source, Integer destination) {
    	if (source == null) source=Integer.valueOf(Definition.FOLDER_VIEW);
    	if (destination == null) destination=Integer.valueOf(Definition.FOLDER_VIEW);
    	
   		if (source.equals(destination)) return;
   		if (source.intValue() == Definition.FILE_FOLDER_VIEW) return;
   		throw new NotSupportedException("Cannot move discussion folder to a library folder");    	
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
    		fixupMovedChild(c, oldKey, newKey);
    	}
   	
    }
    public void moveFolderToWorkspace(Folder source, Workspace destination) {
       	HKey oldKey = source.getFolderHKey();
    	source.getParentBinder().removeBinder(source);
    	destination.addFolder(source);
    	source.setTopFolder(null);
    	HKey newKey = source.getFolderHKey();
    	getFolderDao().moveEntries(source);
    	//fixup all children
    	List binders = source.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Folder child = (Folder)binders.get(i);
    		child.setTopFolder(source);
    		fixupMovedChild(child, oldKey, newKey);
    	}
    	//TODO: need to reindex binder only??  its parent will be different.
   	
    }
    //***********************************************************************************************************
    public void moveEntry(Binder binder, Entry entry, Binder destination) {
    	checkEntryMoveType(binder.getDefinitionType(), destination.getDefinitionType());
    	Folder from = (Folder)binder;
    	if (!(destination instanceof Folder))
    		throw new NotSupportedException("Must move folderEntry to another folder");
    	Folder to = (Folder)destination;
    	FolderEntry fEntry = (FolderEntry)entry;
    	if (fEntry.getTopEntry() != null)
    		throw new NotSupportedException("Cannot move a reply");
    	HKey oldKey = fEntry.getHKey();
    	//get Children
    	List entries = getFolderDao().loadEntryDescendants(fEntry);
    	from.removeEntry(fEntry);
    	to.addEntry(fEntry);
    	//TODO: need to remove entries from index. add to new index for parentBinder changes
    	List ids = new ArrayList();
    	for (int i=0; i<entries.size(); ++i) {
    		FolderEntry e = (FolderEntry)entries.get(i);
     	   	String childSort = e.getHKey().getSortKey();
          	e.setHKey(new HKey(childSort.replaceFirst(oldKey.getSortKey(), fEntry.getHKey().getSortKey())));
        	ids.add(e.getId());
    	}
    	//add top entry to list of entries
    	ids.add(fEntry.getId());
    	//write out changes before bulk updates
    	getCoreDao().flush();
    	getFolderDao().moveEntries(to,ids);
    	//finally remove from index
    	entries.add(fEntry);
    	reindexEntries(entries);
    }
    protected void checkEntryMoveType(Integer source, Integer destination) {
       	if (source == null) source=Integer.valueOf(Definition.FOLDER_VIEW);
    	if (destination == null) destination=Integer.valueOf(Definition.FOLDER_VIEW);
    	
   		if (source.equals(destination)) return;
   		throw new NotSupportedException("Cannot move folderEntry to another folder type");    	    	
    }
    protected void loadEntryHistory(Entry entry) {
    	FolderEntry fEntry = (FolderEntry)entry;
        Set ids = new HashSet();
        if (fEntry.getCreation() != null)
            ids.add(fEntry.getCreation().getPrincipal().getId());
        if (fEntry.getModification() != null)
            ids.add(fEntry.getModification().getPrincipal().getId());
        if (fEntry.getReservation() != null) 
            ids.add(fEntry.getReservation().getPrincipal().getId());
        getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
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
        getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
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
        return getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
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
        getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     }     
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry);
    	               
        // Add Doc number
        IndexUtils.addDocNumber(indexDoc, (FolderEntry)entry);

        // Add sortable Doc number
        IndexUtils.addSortNumber(indexDoc, (FolderEntry)entry);

        // Add the folder Id
        IndexUtils.addFolderId(indexDoc, (Folder)binder);
               
        return indexDoc;
    }
       
    //***********************************************************************************************************
   public FolderEntry addReply(final FolderEntry parent, Definition def, final InputDataAccessor inputData, Map fileItems) 
   	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
               
        Map entryDataAll = addReply_toEntryData(parent, def, inputData, fileItems);
        final Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        // Before doing anything else (especially writing anything to the 
        // database), make sure to run the filter on the uploaded files. 
        FilesErrors filesErrors = addReply_filterFiles(parent.getParentFolder(), fileData);
        
        final FolderEntry entry = addReply_create(def);
        
        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		addReply_fillIn(parent, entry, inputData, entryData);
        
        		addReply_preSave(parent, entry, inputData, entryData);
        		
        		addReply_save(entry);
        
        		addReply_postSave(parent, entry, inputData, entryData);
        		return null;
        	}});
        
        filesErrors = addReply_processFiles(parent, entry, fileData, filesErrors);
        
        addReply_startWorkflow(entry);
         
        addReply_indexAdd(parent, entry, inputData, entryData, fileData);
                
        cleanupFiles(fileData);
        
    	if(filesErrors.getProblems().size() > 0) {
    		// At least one error occured during the operation. 
    		throw new WriteFilesException(filesErrors);
    	}
    	else {
    		return entry;
    	}
    }
        
    protected Map addReply_toEntryData(FolderEntry parent, Definition def, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        if (def != null) {
        	return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
        } else {
        	return new HashMap();
        }
    }
    
    /**
     * Subclass must implement this.
     * @return
     */
    protected FolderEntry addReply_create(Definition def) {
    	try {
    		FolderEntry entry =  new FolderEntry();
           	entry.setEntryDef(def);
       	return entry;
    	} catch (Exception ex) {
    		return null;
    	}
    	
    }

    protected FilesErrors addReply_filterFiles(Binder binder, List fileData) throws FilterException {
    	return getFileModule().filterFiles(binder, fileData);
    }

    protected FilesErrors addReply_processFiles(FolderEntry parent, FolderEntry entry, 
    		List fileData, FilesErrors filesErrors) {
    	return getFileModule().writeFiles(parent.getParentFolder(), entry, fileData, filesErrors);
    }
    
    protected void addReply_fillIn(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData) {  
        parent.addReply(entry);         
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(user));
        entry.setModification(entry.getCreation());
        
            // The entry inherits acls from the parent by default. 
 //TODO::           getAclManager().doInherit(parent, (AclControlledEntry) entry);
        
        for (Iterator iter=entryData.values().iterator(); iter.hasNext();) {
        	Object obj = iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (obj instanceof Event)
        		getCoreDao().save(obj);
        }
        EntryBuilder.buildEntry(entry, entryData);
    }
    
    protected void addReply_preSave(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData) {
    }
    
    protected void addReply_save(FolderEntry entry) {
        getCoreDao().save(entry);
    }
    
    protected void addReply_postSave(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData) {
    	getProfileDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).setSeen(entry);
    	if (entry instanceof AclControlled)
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(entry)); // Just for testing
    	else
    		getRssGenerator().updateRssFeed(entry, AccessUtils.getReadAclIds(parent.getParentBinder())); // Just for testing
    	if (parent instanceof WorkflowSupport)
    		if (getWorkflowModule().modifyWorkflowStateOnReply(parent)) {
    			indexEntry(parent.getParentBinder(), parent, new ArrayList(), null, false);    			
    		}

    }
    
    protected void addReply_indexAdd(FolderEntry parent, FolderEntry entry, 
    		InputDataAccessor inputData, Map entryData, List fileData) {
    	indexEntry(entry.getParentFolder(), entry, fileData, true);
    }
    
    protected void addReply_startWorkflow(FolderEntry entry) {
    	//Starting a workflow on a reply works the same as for the entry
    	addEntry_startWorkflow(entry);
    }
    
 
    //***********************************************************************************************************
          

    public Map getEntryTree(Folder parentFolder, FolderEntry entry) {
    	int entryLevel;
    	List lineage;
    	Map model = new HashMap();
    	
        //load tree including parent chain and all replies and entry
        lineage = getFolderDao().loadEntryTree(entry);
        //TODO: what about access control here?
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
}
