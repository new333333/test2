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
import com.sitescape.ef.domain.FolderCounts;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.module.binder.BinderComparator;
import com.sitescape.ef.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.folder.InputDataAccessor;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.WriteFilesException;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderCoreProcessor extends AbstractEntryProcessor 
	implements FolderCoreProcessor {
    
    //***********************************************************************************************************	
    protected void addEntry_fillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {  
    	Folder folder = (Folder)binder;
    	folder.addEntry((FolderEntry)entry, getFolderDao().allocateEntryNumbers(folder, 1));         
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
    protected void deleteEntry_preDelete(Folder parentFolder, FolderEntry entry) {
    	List replies = new ArrayList(entry.getReplies());
    	for (int i=0; i<replies.size(); ++i) {
    		deleteEntry(parentFolder, ((FolderEntry)replies.get(i)).getId());
    	}
        FolderEntry parent= entry.getParentEntry();
        if (parent != null) {
            parent.removeReply(entry);
        } else {
            parentFolder.removeEntry(entry);
        }
    }
  
     protected void deleteEntry_delete(Binder parentBinder, WorkflowControlledEntry entry) {
    	//use the optimized deleteEntry or hibernate deletes each collection entry one at a time
    	folderDao.deleteEntry((FolderEntry)entry);   
    }
    protected void loadEntryHistory(FolderEntry entry) {
        Set ids = new HashSet();
        if (entry.getCreation() != null)
            ids.add(entry.getCreation().getPrincipal().getId());
        if (entry.getModification() != null)
            ids.add(entry.getModification().getPrincipal().getId());
        if (entry.getReservedDoc() != null) 
            ids.add(entry.getReservedDoc().getPrincipal().getId());
        getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     } 

    protected void loadEntryHistory(HashMap entry) {
        Set ids = new HashSet();
        if (entry.get(EntryIndexUtils.CREATORID_FIELD) != null)
    	    ids.add(entry.get(EntryIndexUtils.CREATORID_FIELD));
        if (entry.get(EntryIndexUtils.MODIFICATIONID_FIELD) != null) 
    		ids.add(entry.get(EntryIndexUtils.MODIFICATIONID_FIELD));
        if (entry.get(IndexUtils.RESERVEDBYID_FIELD) != null) 
    		ids.add(entry.get(IndexUtils.RESERVEDBYID_FIELD));
        getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     } 
    protected List loadEntryHistoryLuc(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        HashMap entry;
        while (iter.hasNext()) {
            entry = (HashMap)iter.next();
            if (entry.get(EntryIndexUtils.CREATORID_FIELD) != null)
        	    ids.add(entry.get(EntryIndexUtils.CREATORID_FIELD));
            if (entry.get(EntryIndexUtils.MODIFICATIONID_FIELD) != null) 
        		ids.add(entry.get(EntryIndexUtils.MODIFICATIONID_FIELD));
            if (entry.get(IndexUtils.RESERVEDBYID_FIELD) != null) 
        		ids.add(entry.get(IndexUtils.RESERVEDBYID_FIELD));
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
   public Long addReply(FolderEntry parent, Definition def, InputDataAccessor inputData, Map fileItems) 
   	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        
        addReply_accessControl(parent);
        
        Map entryDataAll = addReply_toEntryData(parent, def, inputData, fileItems);
        Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        FolderEntry entry = addReply_create();
        entry.setEntryDef(def);
        
        addReply_processFiles(parent, entry, fileData);
        
        addReply_fillIn(parent, entry, inputData, entryData);
        
        addReply_preSave(parent, entry, inputData, entryData);
        
        addReply_save(entry);
        
        addReply_postSave(parent, entry, inputData, entryData);
        
        addReply_indexAdd(parent, entry, inputData, entryData);
        
        addReply_startWorkflow(entry);
        
        getCoreDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).setSeen(entry);
       
        return entry.getId();
    }
    
    protected void addReply_accessControl(FolderEntry parent) throws AccessControlException {
    	//TODO : check entry acl?        
   		getAccessControlManager().checkOperation(parent.getParentFolder(), WorkAreaOperation.ADD_REPLIES);
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
    
    protected void addReply_processFiles(FolderEntry parent, FolderEntry entry, List fileData) 
    	throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileModule(), parent.getParentFolder(), entry, fileData);
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
    }
    
    protected void addReply_indexAdd(FolderEntry parent, FolderEntry entry, InputDataAccessor inputData, Map entryData) {
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(entry.getParentFolder(), entry);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }
    
    protected void addReply_startWorkflow(FolderEntry entry) {
    	//Starting a workflow on a reply works the same as for the entry
    	addEntry_startWorkflow(entry);
    }
    
 
 
    //***********************************************************************************************************
    public org.dom4j.Document getDomFolderTree(Folder top, DomTreeBuilder domTreeHelper) {
    	getBinderEntries_accessControl(top);
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
            	getBinderEntries_accessControl(f);
            } catch (AccessControlException ac) {
               	continue;
            }
       		next = current.addElement(DomTreeBuilder.NODE_CHILD);
       		buildFolderDomTree(next, f, c, domTreeHelper);
       	}
    }
 
 
 

//***********************************************************************************************************
    public Long addFolder(Folder parentFolder, Folder folder) {
        addFolder_accessControl(parentFolder);
        
        addFolder_preSave(parentFolder, folder);
        
        addFolder_save(folder);
        
        addFolder_postSave(folder);
        
        return folder.getId();
    }
    protected void addFolder_accessControl(Folder parentFolder) {
    	getAccessControlManager().checkOperation(parentFolder, WorkAreaOperation.CREATE_FOLDERS);
    }
    protected void addFolder_preSave(Folder parentFolder, Folder folder) {
        parentFolder.addFolder(folder, getFolderDao().allocateFolderNumbers(folder, 1));
        // The sub-folder inherits the default ACLs of the parent folder.
        // The default ACLs of the sub-folder can be changed subsequently. 
        getAclManager().doInherit(folder);
        User user = RequestContextHolder.getRequestContext().getUser();
              
        folder.setCreation(new HistoryStamp(user));
        folder.setModification(folder.getCreation());
    }
    protected void addFolder_save(Folder folder) {
        getCoreDao().save(folder);
        //Save record of last docNumber as separate object
        FolderCounts fCounts = new FolderCounts(folder.getId());
        getCoreDao().save(fCounts);
    }
    protected void addFolder_postSave(Folder folder) {        
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
