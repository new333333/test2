package com.sitescape.ef.module.folder.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
//import org.dom4j.Document;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.FolderHierarchyException;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.module.binder.BinderComparator;
import com.sitescape.ef.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderCoreProcessor extends AbstractEntryProcessor 
	implements FolderCoreProcessor {
    
    //***********************************************************************************************************	
    protected void addEntry_fillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {  
    	Folder folder = (Folder)binder;
    	getCoreDao().refresh(folder);
    	folder.addEntry((FolderEntry)entry);         
    	super.addEntry_fillIn(folder, entry, inputData, entryData);
   }
 
    protected void addEntry_postSave(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {
		getCoreDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).
							setSeen(entry);
    }

	 protected void modifyEntry_postFillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {
		   getCoreDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).setSeen(entry);
     }

 	protected SFQuery indexBinder_getQuery(Binder binder) {
        //do actual db query 
    	FilterControls filter = new FilterControls("parentBinder", binder);
        return (SFQuery)getFolderDao().queryEntries(filter);
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
       	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.ENTRY_TYPE_FIELD);
       	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
       	child.setText(EntryIndexUtils.ENTRY_TYPE_ENTRY);
 
       	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);

    	//Look only for binderId=binder
    	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.BINDER_ID_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(binder.getId().toString());
    	return qTree;
 
    }
          
    protected  WorkflowControlledEntry entry_load(Binder parentBinder, Long entryId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        return folderDao.loadFolderEntry(parentBinder.getId(), entryId, user.getZoneName()); 
    }
         
    protected  WorkflowControlledEntry entry_loadFull(Binder parentBinder, Long entryId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        return folderDao.loadFullFolderEntry(parentBinder.getId(), entryId, user.getZoneName()); 
   }
    protected void deleteEntry_preDelete(Binder parentBinder, WorkflowControlledEntry entry) {
    	FolderEntry fEntry = (FolderEntry)entry;
    	List replies = new ArrayList(fEntry.getReplies());
    	for (int i=0; i<replies.size(); ++i) {
    		deleteEntry(parentBinder, ((FolderEntry)replies.get(i)).getId());
    	}
        FolderEntry parent= fEntry.getParentEntry();
        if (parent != null) {
            parent.removeReply(fEntry);
        } else {
            ((Folder)parentBinder).removeEntry(fEntry);
        }
    }
  
     protected void deleteEntry_delete(Binder parentBinder, WorkflowControlledEntry entry) {
    	//use the optimized deleteEntry or hibernate deletes each collection entry one at a time
    	folderDao.deleteEntry((FolderEntry)entry);   
    }
    protected void loadEntryHistory(WorkflowControlledEntry entry) {
    	FolderEntry fEntry = (FolderEntry)entry;
        Set ids = new HashSet();
        if (fEntry.getCreation() != null)
            ids.add(fEntry.getCreation().getPrincipal().getId());
        if (fEntry.getModification() != null)
            ids.add(fEntry.getModification().getPrincipal().getId());
        if (fEntry.getReservedDoc() != null) 
            ids.add(fEntry.getReservedDoc().getPrincipal().getId());
        getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     } 

    protected void loadEntryHistory(HashMap entry) {
        Set ids = new HashSet();
        if (entry.get(EntryIndexUtils.CREATORID_FIELD) != null)
    	    try {ids.add(new Long((String)entry.get(EntryIndexUtils.CREATORID_FIELD)));
    	    } catch (Exception ex) {};
    	if (entry.get(EntryIndexUtils.MODIFICATIONID_FIELD) != null) 
    		try {ids.add(new Long((String)entry.get(EntryIndexUtils.MODIFICATIONID_FIELD)));
    		} catch (Exception ex) {};
        if (entry.get(IndexUtils.RESERVEDBYID_FIELD) != null) 
    		try {ids.add(new Long((String)entry.get(IndexUtils.RESERVEDBYID_FIELD)));
    	    } catch (Exception ex) {};
        getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     } 
    protected List loadEntryHistoryLuc(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        HashMap entry;
        while (iter.hasNext()) {
            entry = (HashMap)iter.next();
            if (entry.get(EntryIndexUtils.CREATORID_FIELD) != null)
        	    try {ids.add(new Long((String)entry.get(EntryIndexUtils.CREATORID_FIELD)));
           	    } catch (Exception ex) {};
           if (entry.get(EntryIndexUtils.MODIFICATIONID_FIELD) != null) 
        		try {ids.add(new Long((String)entry.get(EntryIndexUtils.MODIFICATIONID_FIELD)));
        		} catch (Exception ex) {};
   	       if (entry.get(IndexUtils.RESERVEDBYID_FIELD) != null) 
        		try {ids.add(new Long((String)entry.get(IndexUtils.RESERVEDBYID_FIELD)));
	    	} catch (Exception ex) {};
        }
        return getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
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
            if (entry.getReservedDoc() != null) 
                ids.add(entry.getReservedDoc().getPrincipal().getId());
        }
        getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     }     
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, WorkflowControlledEntry entry) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry);
    	               
        // Add Doc number
        IndexUtils.addDocNumber(indexDoc, (FolderEntry)entry);

        // Add the folder Id
        IndexUtils.addFolderId(indexDoc, (Folder)binder);
               
        return indexDoc;
    }
       
    //***********************************************************************************************************
   public Long addReply(final FolderEntry parent, Definition def, final InputDataAccessor inputData, Map fileItems) 
   	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        
        addReply_accessControl(parent.getParentFolder(), parent);
        
        Map entryDataAll = addReply_toEntryData(parent, def, inputData, fileItems);
        final Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        // Before doing anything else (especially writing anything to the 
        // database), make sure to run the filter on the uploaded files. 
        FilesErrors filesErrors = addReply_filterFiles(parent.getParentFolder(), fileData);
        
        final FolderEntry entry = addReply_create();
        entry.setEntryDef(def);
        
        filesErrors = addReply_processFiles(parent, entry, fileData, filesErrors);
        
        // The following part requires update database transaction.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		addReply_fillIn(parent, entry, inputData, entryData);
        
        		addReply_preSave(parent, entry, inputData, entryData);
        		
        		addReply_save(entry);
        
        		addReply_postSave(parent, entry, inputData, entryData);
        		return null;
        	}});
        
        addReply_indexAdd(parent, entry, inputData, entryData, fileData);
        
        addReply_startWorkflow(entry);
        
        
        cleanupFiles(fileData);
        
    	if(filesErrors.getProblems().size() > 0) {
    		// At least one error occured during the operation. 
    		throw new WriteFilesException(filesErrors);
    	}
    	else {
    		return entry.getId();
    	}
    }
    
    public void addReply_accessControl(Folder folder, FolderEntry parent) throws AccessControlException {
    	//TODO : check entry acl?        
   		getAccessControlManager().checkOperation(folder, WorkAreaOperation.ADD_REPLIES);
    }
    
    protected Map addReply_toEntryData(FolderEntry parent, Definition def, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
    /**
     * Subclass must implement this.
     * @return
     */
    protected FolderEntry addReply_create() {
        return new FolderEntry();
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
        getCoreDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).setSeen(entry);
    }
    
    protected void addReply_indexAdd(FolderEntry parent, FolderEntry entry, 
    		InputDataAccessor inputData, Map entryData, List fileData) {
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(entry.getParentFolder(), entry);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);     
        
        // Take care of attached files - to be completed by Roy.
    }
    
    protected void addReply_startWorkflow(FolderEntry entry) {
    	//Starting a workflow on a reply works the same as for the entry
    	addEntry_startWorkflow(entry);
    }
    
 
 
    //***********************************************************************************************************
    public org.dom4j.Document getDomFolderTree(Folder top, DomTreeBuilder domTreeHelper) {
    	getBinder_accessControl(top);
        User user = RequestContextHolder.getRequestContext().getUser();
    	Comparator c = new BinderComparator(user.getLocale());
    	    	
    	org.dom4j.Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	      	
  	    buildFolderDomTree(rootElement, (Folder)top, c, domTreeHelper);
  	    return wsTree;
  	}
    
    protected void buildFolderDomTree(Element current, Folder top, Comparator c, DomTreeBuilder domTreeHelper) {
       	Element next; 
       	Folder f;
    	   	
       	//callback to setup tree
    	domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_FOLDER, top, current);
     	TreeSet folders = new TreeSet(c);
    	folders.addAll(top.getFolders());
       	for (Iterator iter=folders.iterator(); iter.hasNext();) {
       		f = (Folder)iter.next();
      	    // Check if the user has the privilege to view the folder 
            try {
            	getBinder_accessControl(f);
            } catch (AccessControlException ac) {
               	continue;
            }
       		next = current.addElement(DomTreeBuilder.NODE_CHILD);
       		buildFolderDomTree(next, f, c, domTreeHelper);
       	}
    }
 
 
 


    //***********************************************************************************************************
          

    public Map getEntryTree(Folder parentFolder, Long entryId, int type) {
    	int entryLevel;
    	List lineage;
    	Map model = new HashMap();
    	
    	//get the entry
        FolderEntry entry = (FolderEntry)entry_loadFull(parentFolder, entryId);
        //check access
        getEntry_accessControl(parentFolder, entry);
 
        //load tree including parent chain and all replies
        lineage = getFolderDao().loadEntryTree(entry);
        //TODO: what about access control here?
        //split the tree
        entryLevel = entry.getDocLevel();
        if (entryLevel-1 > lineage.size()) {
            throw new FolderHierarchyException(entry.getId(), "Parent entries are missing");
        }
        model.put(ObjectKeys.FOLDER_ENTRY, entry);
        model.put(ObjectKeys.FOLDER_ENTRY_ANCESTORS, lineage.subList(0,entryLevel-1));
        model.put(ObjectKeys.FOLDER_ENTRY_DESCENDANTS, lineage.subList(entryLevel-1,lineage.size()));
        //Initialize users
        List allE = new ArrayList(lineage);
        allE.add(entry);
        loadEntryHistory(allE);
        return model;
    }
         
    //***********************************************************************************************************   


}
