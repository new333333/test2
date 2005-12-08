package com.sitescape.ef.module.folder.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.lang.Long;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.document.DateField;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
//import org.dom4j.Document;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileItem;
import com.sitescape.ef.domain.FolderHierarchyException;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderCounts;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Description;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.exception.UncheckedCodedContainerException;
import com.sitescape.ef.file.CheckedOutByOtherException;
import com.sitescape.ef.file.FileManager;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.binder.BinderComparator;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.LuceneSessionFactory;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.WriteFilesException;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderCoreProcessor extends CommonDependencyInjection 
	implements FolderCoreProcessor {
    
	private static final int DEFAULT_MAX_CHILD_ENTRIES = ObjectKeys.FOLDER_MAX_PAGE_SIZE;
    protected DefinitionModule definitionModule;
 
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}


    //***********************************************************************************************************	
    public Long addEntry(Folder folder, Definition def, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        
        addEntry_accessControl(folder);
        
        Map entryDataAll = addEntry_toEntryData(folder, def, inputData, fileItems);
        Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        FolderEntry entry = addEntry_create();
        entry.setEntryDef(def);
        
        //need to set entry/folder information before generating file attachments
        //Attachments need folder info for AnyOwner
        addEntry_fillIn(folder, entry, inputData, entryData);
        
        addEntry_processFiles(folder, entry, fileData);
        
        addEntry_preSave(folder, entry, inputData, entryData);
        
        addEntry_save(entry);
        
        addEntry_postSave(folder, entry, inputData, entryData);
        
        // This must be done in a separate step after persisting the entry,
        // because we need the entry's persistent ID for indexing. 
        addEntry_indexAdd(folder, entry, inputData);
		getCoreDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).
							setSeen(entry);
       
        return entry.getId();
    }

     
    protected void addEntry_accessControl(Folder folder) throws AccessControlException {
        accessControlManager.checkOperation(folder, WorkAreaOperation.CREATE_ENTRIES);        
    }
    
    protected void addEntry_processFiles(Folder folder, FolderEntry entry, List fileData) 
    	throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileManager(), folder, entry, fileData);
    }
    
    protected Map addEntry_toEntryData(Folder folder, Definition def, Map inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(def, inputData, fileItems);
    }
    
     protected FolderEntry addEntry_create() {
    	return new FolderEntry();
    }
    
    protected void addEntry_fillIn(Folder folder, FolderEntry entry, Map inputData, Map entryData) {  
    	folder.addEntry(entry, getFolderDao().allocateEntryNumbers(folder, 1));         
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(user));
        entry.setModification(entry.getCreation());
        
        
        // The entry inherits acls from the parent by default. 
        getAclManager().doInherit(folder, (AclControlledEntry) entry);
        
        EntryBuilder.buildEntry(entry, entryData);
    }
    
    protected void addEntry_preSave(Folder folder, FolderEntry entry, Map inputData, Map entryData) {
    }
    
    protected void addEntry_save(FolderEntry entry) {
        getCoreDao().save(entry);
    }
    
    protected void addEntry_postSave(Folder folder, FolderEntry entry, Map inputData, Map entryData) {
    }
    
    protected void addEntry_indexAdd(Folder folder, FolderEntry entry, Map inputData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(folder, entry);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }
 	

   //***********************************************************************************************************
    public void modifyEntry(Folder folder, Long entryId, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        FolderEntry entry = folderEntry_load(folder, entryId);
        modifyEntry_accessControl(folder, entry);
 
        Map entryDataAll = modifyEntry_toEntryData(entry, inputData, fileItems);
        Map entryData = (Map) entryDataAll.get("entryData");
        List fileData = (List) entryDataAll.get("fileData");
        
        modifyEntry_processFiles(folder, entry, fileData);
        
        modifyEntry_fillIn(folder, entry, inputData, entryData);
                    
        modifyEntry_postFillIn(folder, entry, inputData, entryData);
        
        modifyEntry_indexAdd(folder, entry, inputData);
          
     }
    protected void modifyEntry_accessControl(Folder folder, FolderEntry entry) throws AccessControlException {
        getAccessControlManager().checkOperation(folder, WorkAreaOperation.VIEW);
        
        // Check if the user has "write" access to the particular entry.
        getAccessControlManager().checkAcl(folder, entry, AccessType.WRITE);
    }
    protected void modifyEntry_processFiles(Folder folder, FolderEntry entry, List fileData) 
    throws WriteFilesException {
    	EntryBuilder.writeFiles(getFileManager(), folder, entry, fileData);
    }
    protected Map modifyEntry_toEntryData(FolderEntry entry, Map inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
        return getDefinitionModule().getEntryData(entry.getEntryDef(), inputData, fileItems);
    }
    protected void modifyEntry_fillIn(Folder folder, FolderEntry entry, Map inputData, Map entryData) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setModification(new HistoryStamp(user));
        EntryBuilder.updateEntry(entry, entryData);

    }

    protected void modifyEntry_postFillIn(Folder folder, FolderEntry entry, Map inputData, Map entryData) {
    }
    
    protected void modifyEntry_indexAdd(Folder folder, FolderEntry entry, Map inputData) {
        
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(folder, entry);
        
        // Delete the document that's currently in the index.
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }
 
   //***********************************************************************************************************
   public Long addReply(FolderEntry parent, Definition def, Map inputData, Map fileItems) 
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
        getCoreDao().loadSeenMap(RequestContextHolder.getRequestContext().getUser().getId()).setSeen(entry);
       
        return entry.getId();
    }
    
    protected void addReply_accessControl(FolderEntry parent) throws AccessControlException {
    	//TODO : check entry acl?        
   		getAccessControlManager().checkOperation(parent.getParentFolder(), WorkAreaOperation.ADD_REPLIES);
    }
    
    protected Map addReply_toEntryData(FolderEntry parent, Definition def, Map inputData, Map fileItems) {
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
    	EntryBuilder.writeFiles(getFileManager(), parent.getParentFolder(), entry, fileData);
    }
    
    protected void addReply_fillIn(FolderEntry parent, FolderEntry entry, Map inputData, Map entryData) {  
        parent.addReply(entry);         
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(user));
        entry.setModification(entry.getCreation());
        
            // The entry inherits acls from the parent by default. 
 //TODO::           getAclManager().doInherit(parent, (AclControlledEntry) entry);
        
        EntryBuilder.buildEntry(entry, entryData);
    }
    
    protected void addReply_preSave(FolderEntry parent, FolderEntry entry, Map inputData, Map entryData) {
    }
    
    protected void addReply_save(FolderEntry entry) {
        getCoreDao().save(entry);
    }
    
    protected void addReply_postSave(FolderEntry parent, FolderEntry entry, Map inputData, Map entryData) {
    }
    
    protected void addReply_indexAdd(FolderEntry parent, FolderEntry entry, Map inputData, Map entryData) {
        // Create an index document from the entry object.
        org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(entry.getParentFolder(), entry);
        
        // Register the index document for indexing.
        IndexSynchronizationManager.addDocument(indexDoc);        
    }
    //***********************************************************************************************************
    
    public void indexFolder(Folder folder) {
    	FolderEntry entry;
    	
    	indexFolder_accessControl(folder);
    	
        //do actual db query 
    	FilterControls filter = new FilterControls("parentFolder", folder);
        SFQuery query = (SFQuery)getFolderDao().queryEntries(filter);
        
        //iterate through results
        	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        try {
 	        while (query.hasNext()) {
	            Object obj = query.next();
	            if (obj instanceof Object[])
	                obj = ((Object [])obj)[0];
	            entry = (FolderEntry)obj;
	            // Create an index document from the entry object.
	            org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(folder, entry);
	            
	            logger.info("Indexing (" + folder.getId().toString() + ") " + entry.getDocNumber() + ": " + indexDoc.toString());
	            
	            // Delete the document that's currently in the index.
	            luceneSession.deleteDocument(entry.getIndexDocumentUid());
	            
	            // Register the index document for indexing.
	            luceneSession.addDocument(indexDoc);        
	        }
        } finally {
	        query.close();
	        luceneSession.close();
	    }
 
    }
    protected void indexFolder_accessControl(Folder folder) {
    	getAccessControlManager().checkAcl(folder, AccessType.READ);
    }

    //***********************************************************************************************************
    public org.dom4j.Document getDomFolderTree(Folder top, DomTreeBuilder domTreeHelper) {
       	getAccessControlManager().checkOperation(top, WorkAreaOperation.VIEW);
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
              	getAccessControlManager().checkOperation(f, WorkAreaOperation.VIEW);
            } catch (AccessControlException ac) {
               	continue;
            }
       		next = current.addElement(DomTreeBuilder.NODE_CHILD);
       		buildFolderDomTree(next, f, c, domTreeHelper);
       	}
    }
    //***********************************************************************************************************
    public Map getFolderEntriesOrig(Folder folder, int maxChildEntries) {
        int count=0;
        
        //check access to folder
        getFolderEntries_accessControl(folder);
        //validate entry count
        maxChildEntries = getFolderEntries_maxEntries(maxChildEntries); 
        //do actual db query
        SFQuery query =getFolderEntries_doQuery(folder);
        //iterate threw results
        ArrayList childEntries = new ArrayList(maxChildEntries);
        try {
 	        while ((count < maxChildEntries) && query.hasNext()) {
	            
	            Object obj = query.next();
	            if (obj instanceof Object[])
	                obj = ((Object [])obj)[0];
	            if (obj instanceof AclControlled) {
	                // This object requires access-control checking.
	                // TODO How do we handle workflow-controlled entries??
	                if (getFolderEntries_accessControl(folder, (AclControlled)obj)) {
	                    ++count;
	                    childEntries.add(obj);
	                }
	            }
	            else {
	                ++count;
	                childEntries.add(obj);
	            }
	        }
        } finally {
	        query.close();
        }
       	Map model = new HashMap();
        model.put(ObjectKeys.FOLDER, folder);      
        model.put(ObjectKeys.FOLDER_ENTRIES, childEntries);
        loadEntryHistory(childEntries);
        return model;
   }
    //***********************************************************************************************************
    public Map getFolderEntries(Folder folder, int maxChildEntries) {
        int count=0;
        Enumeration fields;
        Field fld;
        
        //check access to folder - might be able to get rid of this
        getFolderEntries_accessControl(folder);
        //validate entry count
        maxChildEntries = getFolderEntries_maxEntries(maxChildEntries); 
        //do actual search index query
        Hits hits = getFolderEntries_doSearch(folder,maxChildEntries);
        //iterate through results
        ArrayList childEntries = new ArrayList(hits.length());
        try {
 	        while (count < hits.length()) {
	            HashMap ent = new HashMap();
	            Document doc = hits.doc(count);
	            //enumerate thru all the returned fields, and add to the map object
	            Enumeration flds = doc.fields();
	            while (flds.hasMoreElements()) {
	            	fld = (Field)flds.nextElement();
	            	if (fld.name().toLowerCase().indexOf("date") > 0) 
	            		ent.put(fld.name(),DateField.stringToDate(fld.stringValue()));
	            	else
	            		ent.put(fld.name(),fld.stringValue());
	            }
	            childEntries.add(ent);
	            ++count;
	            
	        }
        } finally {
        }
        List users = loadEntryHistoryLuc(childEntries);
        // walk the entries, and stuff in the user object.
        for (int i = 0; i < childEntries.size(); i++) {
        	Principal p;
        	HashMap child = (HashMap)childEntries.get(i);
        	if (child.get("_creatorId") != null) {
        		child.put("_principal", getPrincipal(users,(String)child.get("_creatorId")));
        	}        	
        }
       	Map model = new HashMap();
        model.put(ObjectKeys.FOLDER, folder);      
        model.put(ObjectKeys.FOLDER_ENTRIES, childEntries);
        model.put(ObjectKeys.TOTAL_SEARCH_COUNT, new Integer(hits.length()));
        return model;
   }
 
    private Principal getPrincipal(List users, String userId) {
    	Principal p;
    	for (int i=0; i<users.size(); i++) {
    		p = (Principal)users.get(i);
    		if (p.getId().toString().equalsIgnoreCase(userId)) return p;
    	}
    	new Principal();
    	return null;
    }
    protected void getFolderEntries_accessControl(Folder folder) {
        getAccessControlManager().checkAcl(folder, AccessType.READ);    	
    }
    protected int getFolderEntries_maxEntries(int maxChildEntries) {
        if (maxChildEntries == 0) maxChildEntries = DEFAULT_MAX_CHILD_ENTRIES;
        return maxChildEntries;
    }
    protected SFQuery getFolderEntries_doQuery(Folder folder) {
    	return (SFQuery)getFolderDao().queryChildEntries(folder);
    }
    protected boolean getFolderEntries_accessControl(Folder folder, AclControlled obj) {
    	return getAccessControlManager().testAcl(folder, (AclControlled) obj, AccessType.READ);
    }
    
    protected Hits getFolderEntries_doSearch(Folder folder, int maxResults) {
       	Hits hits = null;
       	// Build the query
    	org.dom4j.Document qTree = DocumentHelper.createDocument();
    	Element rootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
    	Element boolElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
    	boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
    	
    	//Look only for entryType=entry
    	Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.ENTRY_TYPE_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(EntryIndexUtils.ENTRY_TYPE_ENTRY);
    	
    	//Limit results to entries in the current folder
    	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,IndexUtils.FOLDERID_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText((folder.getId()).toString());
    	
    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder();
    	SearchObject so = qb.buildQuery(qTree);
    	
    	System.out.println("Query is: " + qTree.asXML());
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        hits = luceneSession.search(so.getQuery(),so.getSortBy(),0,maxResults);
        }
        finally {
            luceneSession.close();
        }
    	
        return hits;
     
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
    public FolderEntry getEntry(Folder parentFolder, Long entryId, int type) {
    	//get the entry
        FolderEntry entry = folderEntry_load(parentFolder, entryId);
        //check access
        getEntry_accessControl(parentFolder, entry);
        //Initialize users
        loadEntryHistory(entry);
        return entry;
    }
          
    protected FolderEntry folderEntry_load(Folder parentFolder, Long entryId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        FolderEntry entry = folderDao.loadFolderEntry(parentFolder.getId(), entryId, user.getZoneName()); 
        return entry;
    }    
         
    protected void getEntry_accessControl(Folder parentFolder, FolderEntry entry) {
           
        // Check if the user has the privilege to view the entries in the 
        // work area, which is the docshare forum.
        getAccessControlManager().checkOperation(parentFolder, WorkAreaOperation.VIEW);
              
        // Check if the user has "read" access to the particular entry.
        getAccessControlManager().checkAcl(parentFolder, entry, AccessType.READ);
        
        // TODO If there is a workflow attached to the entry, we must perform
        // additional access check based on the state the entry is currently in.
    }
    public Map getEntryTree(Folder parentFolder, Long entryId, int type) {
    	int entryLevel;
    	List lineage;
    	Map model = new HashMap();
    	
    	//get the entry
        FolderEntry entry = folderEntry_load(parentFolder, entryId);
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
    public void deleteEntry(Folder parentFolder, Long entryId) {
        FolderEntry entry = folderEntry_load(parentFolder, entryId);
        deleteEntry(parentFolder, entry);
    }
    public void deleteEntry(Folder parentFolder, FolderEntry entry) {
        deleteEntry_accessControl(parentFolder, entry);
        deleteEntry_preDelete(parentFolder, entry);
        deleteEntry_delete(parentFolder, entry);
        deleteEntry_postDelete(parentFolder, entry);
        deleteEntry_indexDel(entry);
   	
    }
    protected void deleteEntry_accessControl(Folder parentFolder, FolderEntry entry) {
        getAccessControlManager().checkOperation(parentFolder, WorkAreaOperation.DELETE_ENTRIES);
        
        getAccessControlManager().checkAcl(parentFolder, entry, AccessType.DELETE);
    }
    protected void deleteEntry_preDelete(Folder parentFolder, FolderEntry entry) {
    	List replies = new ArrayList(entry.getReplies());
    	for (int i=0; i<replies.size(); ++i) {
    		deleteEntry(parentFolder, (FolderEntry)replies.get(i));
    	}
        FolderEntry parent= entry.getParentEntry();
        if (parent != null) {
            parent.removeReply(entry);
        } else {
            parentFolder.removeEntry(entry);
        }
    }
        
    protected void deleteEntry_delete(Folder parentFolder, FolderEntry entry) {
    	List atts = entry.getAttachments();
    	//need to get all attachments associated with replies
    	//need to delete workflow stuff
    	for (int i=0; i<atts.size(); ++i) {
    		Attachment a = (Attachment)atts.get(i);
    	}
        getCoreDao().delete(entry);   
    }
    protected void deleteEntry_postDelete(Folder parentFolder, FolderEntry entry) {
    }

    protected void deleteEntry_indexDel(FolderEntry entry) {
        // Delete the document that's currently in the index.
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
    }
    
    //***********************************************************************************************************
    /*
     * Load all principals assocated with an entry.  
     * This is a performance optimization for display.
     */
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
        if (entry.get("_creatorId") != null)
    	    ids.add(entry.get("_creatorId"));
        if (entry.get("_modificationId") != null) 
    		ids.add(entry.get("_modificationId"));
        getCoreDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
     } 
    protected List loadEntryHistoryLuc(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        HashMap entry;
        while (iter.hasNext()) {
            entry = (HashMap)iter.next();
            if (entry.get("_creatorId") != null)
        	    ids.add(entry.get("_creatorId"));
            if (entry.get("_modificationId") != null) 
        		ids.add(entry.get("_modificationId"));
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
    public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Folder folder, FolderEntry entry) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        
        // Add uid
        BasicIndexUtils.addUid(indexDoc, entry.getIndexDocumentUid());
        
        // Add doc type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.ef.search.BasicIndexUtils.DOC_TYPE_ENTRY);
               
        // Add the entry type (entry or reply)
        EntryIndexUtils.addEntryType(indexDoc, entry);
        
        // Add creation-date
        EntryIndexUtils.addCreationDate(indexDoc, entry);
        
        // Add modification-date
        EntryIndexUtils.addModificationDate(indexDoc,entry);
        
        // Add creator id
        EntryIndexUtils.addCreationPrincipleId(indexDoc,entry);
        
        // Add Modification Principle Id
        EntryIndexUtils.addModificationPrincipleId(indexDoc,entry);
        
        // Add Doc Id
        EntryIndexUtils.addDocId(indexDoc, entry);
        
        // Add Doc title
        EntryIndexUtils.addTitle(indexDoc, entry);
        
        // Add command definition
        EntryIndexUtils.addCommandDefinition(indexDoc, entry); 
        
        // Add Doc number
        IndexUtils.addDocNumber(indexDoc, entry);

        // Add the folder Id
        IndexUtils.addFolderId(indexDoc, folder);
        
        // Add data fields driven by the entry's definition object. 
        getDefinitionModule().addIndexFieldsForEntry(indexDoc, folder, entry);
        
        // Add ACL field. We only need to index ACLs for read access.
        IndexUtils.addReadAcls(indexDoc, folder, entry, getAclManager());
        
        // add the events
        EntryIndexUtils.addEvents(indexDoc, entry);
        
        return indexDoc;
    }
       
}
