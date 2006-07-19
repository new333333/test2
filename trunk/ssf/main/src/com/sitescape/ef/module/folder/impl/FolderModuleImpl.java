package com.sitescape.ef.module.folder.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Collection;

import org.apache.lucene.document.DateField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.Rating;
import com.sitescape.ef.domain.ReservedByAnotherUserException;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Visits;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.binder.AccessUtils;
import com.sitescape.ef.module.binder.BinderComparator;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.folder.FileLockInfo;
import com.sitescape.ef.module.folder.FilesLockedByOtherUsersException;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.search.LuceneSession;
//import com.sitescape.ef.search.remote.RemoteInStreamSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.module.workflow.WorkflowUtils;
import com.sitescape.ef.util.NLT;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public class FolderModuleImpl extends CommonDependencyInjection implements FolderModule {
   	private String[] ratingAttrs = new String[]{"id.entityId", "id.entityType"};
    private String[] entryTypes = {EntryIndexUtils.ENTRY_TYPE_ENTRY};
    protected DefinitionModule definitionModule;
    protected FileModule fileModule;
    protected ProfileModule profileModule;
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	protected FileModule getFileModule() {
		return fileModule;
	}
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}
	public ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	private Folder loadFolder(Long folderId)  {
        String companyId = RequestContextHolder.getRequestContext().getZoneName();
        return  getFolderDao().loadFolder(folderId, companyId);
		
	}
	private FolderCoreProcessor loadProcessor(Folder folder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.

		return (FolderCoreProcessor)getProcessorManager().getProcessor(folder, folder.getProcessorKey(FolderCoreProcessor.PROCESSOR_KEY));	
	}

	public Folder getFolder(Long folderId)
		throws NoFolderByTheIdException, AccessControlException {
		Folder folder = loadFolder(folderId);
	
		// Check if the user has "read" access to the folder.
		getAccessControlManager().checkOperation(folder, WorkAreaOperation.READ_ENTRIES);		
		return folder;        
	} 
	public Collection getFolders(List folderIds) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new BinderComparator(user.getLocale());
       	TreeSet<Binder> result = new TreeSet<Binder>(c);
		for (int i=0; i<folderIds.size(); ++i) {
			try {
				result.add(getFolder((Long)folderIds.get(i)));
			} catch (NoFolderByTheIdException ex) {
			} catch (AccessControlException ax) {
			}
			
		}
		return result;
	}
   

    public Long addFolder(Long parentFolderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
        Folder parentFolder = loadFolder(parentFolderId);
        checkAddFolderAllowed(parentFolder);
        Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, parentFolder.getZoneName());
        } else {
        	def = parentFolder.getEntryDef();
        }
        
        return loadProcessor(parentFolder).addBinder(parentFolder, def, Folder.class, inputData, fileItems);

    }
 
    public void checkAddFolderAllowed(Folder parentFolder) {
        getAccessControlManager().checkOperation(parentFolder, WorkAreaOperation.CREATE_BINDERS);        
    }
    public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
        Folder folder = loadFolder(folderId);
        checkAddEntryAllowed(folder);
        Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, folder.getZoneName());
        } else {
        	def = folder.getDefaultEntryDef();
        }
        
        return loadProcessor(folder).addEntry(folder, def, FolderEntry.class, inputData, fileItems);
    }
    public void checkAddEntryAllowed(Folder folder) {
        getAccessControlManager().checkOperation(folder, WorkAreaOperation.CREATE_ENTRIES);        
    }

    public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems) throws AccessControlException, WriteFilesException {
        Folder folder = loadFolder(folderId);
        Definition def = getCoreDao().loadDefinition(definitionId, folder.getZoneName());
        FolderCoreProcessor processor = loadProcessor(folder);
        //load parent entry
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, parentId);
        checkAddReplyAllowed(entry);
        return processor.addReply(entry, def, inputData, fileItems);
    }
    public void checkAddReplyAllowed(FolderEntry entry) throws AccessControlException {
    	//TODO: this check is missing workflow checks??
    	getAccessControlManager().checkOperation(entry.getParentBinder(), WorkAreaOperation.ADD_REPLIES);
    }
    public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
			throws AccessControlException, WriteFilesException {
    	modifyEntry(binderId, id, inputData, new HashMap(), null);
    }
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkModifyEntryAllowed(entry);
    	List atts = new ArrayList();
    	if (deleteAttachments != null) {
    		for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    			String id = (String)iter.next();
    			Attachment a = entry.getAttachment(id);
    			if (a != null) atts.add(a);
    		}
    	}
        processor.modifyEntry(folder, entry, inputData, fileItems, atts);
    }

    public void checkModifyEntryAllowed(FolderEntry entry) {
		AccessUtils.modifyCheck(entry);   		
    }
    
    public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) throws AccessControlException {
        Folder folder = loadFolder(folderId);       
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
		checkTransitionOutStateAllowed(entry, stateId);
		checkTransitionInStateAllowed(entry, stateId, toState);
        processor.modifyWorkflowState(folder, entry, stateId, toState);
    }

    public void checkTransitionOutStateAllowed(FolderEntry entry, Long stateId) {
		WorkflowState ws = entry.getWorkflowState(stateId);
		AccessUtils.checkTransitionOut(entry.getParentBinder(), entry, ws.getDefinition(), ws.getState());   		
    }
	
    public void checkTransitionInStateAllowed(FolderEntry entry, Long stateId, String toState) {
		WorkflowState ws = entry.getWorkflowState(stateId);
		AccessUtils.checkTransitionIn(entry.getParentBinder(), entry, ws.getDefinition(), toState);   		
    }
	public Map getManualTransitions(FolderEntry entry, Long stateId) {
		WorkflowState ws = entry.getWorkflowState(stateId);
		Map result = WorkflowUtils.getManualTransitions(ws.getDefinition(), ws.getState());
		Map transitionData = new LinkedHashMap();
		for (Iterator iter=result.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			try {
				AccessUtils.checkTransitionIn(entry.getParentBinder(), entry, ws.getDefinition(), (String)me.getKey());  
				transitionData.put(me.getKey(), me.getValue());
			} catch (AccessControlException ac) {};
		}
		return transitionData;
    }		

	public Map getWorkflowQuestions(FolderEntry entry, Long stateId) {
		WorkflowState ws = entry.getWorkflowState(stateId);
		Map questions = WorkflowUtils.getQuestions(ws.getDefinition(), ws.getState());
		//TODO - Check if user is allowed to respond (add a user list property to the workflowQuestion item)
		return questions;
    }		

    public List applyEntryFilter(Definition entryFilter) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    
    public void indexFolderTree(Long folderId) {
		Folder folder = loadFolder(folderId);
		getAccessControlManager().checkOperation(folder,  WorkAreaOperation.BINDER_ADMINISTRATION);
    	//get sub-folders and index them all
		indexFolder(folder);
    }
    private void indexFolder(Folder folder) {
    	List folders = folder.getFolders();
		for (int i=0; i<folders.size(); ++i) {
	    	Folder f = (Folder)folders.get(i);
	    	indexFolder(f);
		}
	    loadProcessor(folder).indexEntries(folder);
    }
    
    public void indexEntries(Long folderId) {
		Folder folder = loadFolder(folderId);
		getAccessControlManager().checkOperation(folder,  WorkAreaOperation.BINDER_ADMINISTRATION);
        loadProcessor(folder).indexEntries(folder);
    }

    public Map getCommonEntryElements() {
    	Map entryElements = new HashMap();
    	Map itemData;
    	//Build a map of common elements for use in search filters
    	//  Each map has a "type" and a "caption". Types can be: title, text, user_list, or date.
    	
    	//title
    	itemData = new HashMap();
    	itemData.put("type", "title");
    	itemData.put("caption", NLT.get("filter.title"));
    	entryElements.put("title", itemData);
    	
    	//author
    	itemData = new HashMap();
    	itemData.put("type", "user_list");
    	itemData.put("caption", NLT.get("filter.author"));
    	entryElements.put("owner", itemData);
    	
    	//creation date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.creationDate"));
    	entryElements.put("creation", itemData);
    	
    	//modification date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.modificationDate"));
    	entryElements.put("modification", itemData);
    	
    	return entryElements;
    }
    
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper) {
        Folder top = loadFolder(folderId);
        getAccessControlManager().checkOperation(top, WorkAreaOperation.READ_ENTRIES);
        
        User user = RequestContextHolder.getRequestContext().getUser();
    	Comparator c = new BinderComparator(user.getLocale());
    	    	
    	org.dom4j.Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	      	
  	    buildFolderDomTree(rootElement, top, c, domTreeHelper);
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
       		if(!getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
       			continue;
       		next = current.addElement(DomTreeBuilder.NODE_CHILD);
       		buildFolderDomTree(next, f, c, domTreeHelper);
       	}
    }
 
 
 

   
    public Map getFolderEntries(Long folderId) {
        return getFolderEntries(folderId, 0);
    }

    public Map getFolderEntries(Long folderId, int maxChildEntries) {
    	return getFolderEntries(folderId, 0, null);
    }
    
    public Map getFolderEntries(Long folderId, int maxChildEntries, Document searchFilter) {
        Folder folder = loadFolder(folderId);
        //search query does access checks
        return loadProcessor(folder).getBinderEntries(folder, entryTypes, maxChildEntries, searchFilter);
    }
    

    public Map getUnseenCounts(List folderIds) {
    	//search engine will do acl checks
        User user = RequestContextHolder.getRequestContext().getUser();
        SeenMap seenMap = getProfileDao().loadSeenMap(user.getId());
        Map results = new HashMap();
        List folders = new ArrayList();
        for (int i=0; i<folderIds.size(); ++i) {
        	try {
        		folders.add(loadFolder((Long)folderIds.get(i)));
        	} catch (NoFolderByTheIdException nf) {} 
        }
        if (folders.size() > 0) {
	        Hits hits = getRecentEntries(folders);
	        Map unseenCounts = new HashMap();
	        for (int i = 0; i < hits.length(); i++) {
				String folderIdString = hits.doc(i).getField(IndexUtils.TOP_FOLDERID_FIELD).stringValue();
				String entryIdString = hits.doc(i).getField(EntryIndexUtils.DOCID_FIELD).stringValue();
				Long entryId = null;
				if (entryIdString != null && !entryIdString.equals("")) {
					entryId = new Long(entryIdString);
				}
				Date modifyDate = DateField.stringToDate(hits.doc(i).getField(EntryIndexUtils.MODIFICATION_DATE_FIELD).stringValue());
				Counter cnt = (Counter)unseenCounts.get(folderIdString);
				if (cnt == null) {
					cnt = new Counter();
					unseenCounts.put(folderIdString, cnt);
				}
				if (entryId != null && (!seenMap.checkAndSetSeen(entryId, modifyDate, false))) {
					cnt.increment();
				}
			}
	        for (int i=0; i<folders.size(); ++i) {
	        	Folder f = (Folder)folders.get(i);
	        	Counter cnt = (Counter)unseenCounts.get(f.getId().toString());
	        	if (cnt == null) cnt = new Counter();
	        	results.put(f, cnt);
	        }
        }
        return results;
    }
 
    public Hits getRecentEntries(List folders) {
    	Hits results = null;
       	// Build the query
    	org.dom4j.Document qTree = DocumentHelper.createDocument();
    	Element rootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
    	Element andElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
    	andElement.addElement(QueryBuilder.USERACL_ELEMENT);
    	Element rangeElement = andElement.addElement(QueryBuilder.RANGE_ELEMENT);
    	rangeElement.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntryIndexUtils.MODIFICATION_DAY_FIELD);
    	rangeElement.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, QueryBuilder.INCLUSIVE_TRUE);
    	Element startRange = rangeElement.addElement(QueryBuilder.RANGE_START);
    	Date now = new Date();
    	Date startDate = new Date(now.getTime() - ObjectKeys.SEEN_MAP_TIMEOUT);
    	startRange.addText(EntryIndexUtils.formatDayString(startDate));
    	Element finishRange = rangeElement.addElement(QueryBuilder.RANGE_FINISH);
    	finishRange.addText(EntryIndexUtils.formatDayString(now));
    	Element orElement = andElement.addElement(QueryBuilder.OR_ELEMENT);
    	Iterator itFolders = folders.iterator();
    	while (itFolders.hasNext()) {
    		Folder folder = (Folder) itFolders.next();
        	Element field = orElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,IndexUtils.TOP_FOLDERID_FIELD);
        	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	Folder topFolder = folder.getTopFolder();
        	if (topFolder == null) topFolder = folder;
    		child.setText(topFolder.getId().toString());
    	}
    	
    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder();
    	SearchObject so = qb.buildQuery(qTree);
    	
    	System.out.println("Query is: " + so.getQueryString());
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
    	//RemoteInStreamSession instreamSession = getInstreamSessionFactory().openSession();
        
        try {
        	results = luceneSession.search(so.getQuery(),so.getSortBy(),0,0);
        	//results = instreamSession.search(so.getQueryString(),so.getSortBy(),0,0);
        } catch (Exception e) {
        	logger.warn("Exception throw while searching in getRecentEntries: " + e.toString());
        }
        finally {
            luceneSession.close();
        }
        return results;
    }

           
     public FolderEntry getEntry(Long parentFolderId, Long entryId) {
        Folder folder = loadFolder(parentFolderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        AccessUtils.readCheck(entry);
        return entry;
    }
    public Map getEntryTree(Long parentFolderId, Long entryId) {
        Folder folder = loadFolder(parentFolderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        AccessUtils.readCheck(entry);
        return processor.getEntryTree(folder, entry);   	
    }
    
    public void deleteEntry(Long parentFolderId, Long entryId) {
        Folder folder = loadFolder(parentFolderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkDeleteEntryAllowed(entry);
        processor.deleteEntry(folder, entry);
    }
    public void checkDeleteEntryAllowed(FolderEntry entry) {
        AccessUtils.deleteCheck(entry);    	
    }
    public void moveEntry(Long folderId, Long entryId, Long destinationId) {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkModifyEntryAllowed(entry);
        Folder destination =  loadFolder(destinationId);
        checkAddEntryAllowed(destination);
        processor.moveEntry(folder, entry, destination);
    }
	public List getTags(Long binderId, Long entryId) {
		FolderEntry entry = getEntry(binderId, entryId);
		List tags = new ArrayList<Tag>();
		tags = getCoreDao().loadTagsByEntity(entry.getEntityIdentifier());
		tags = uniqueTags(tags);
		return tags;		
	}
	public void modifyTag(Long binderId, Long entryId, String tagId, String newtag) {
		FolderEntry entry = getEntry(binderId, entryId);
	   	Tag tag = coreDao.loadTagById(tagId);
	   	tag.setName(newtag);
	   	coreDao.update(tag);
	}
	public void setTag(Long binderId, Long entryId, String newtag) {
		FolderEntry entry = getEntry(binderId, entryId);
	   	Tag tag = new Tag();
	   	User user = RequestContextHolder.getRequestContext().getUser();
	   	tag.setOwnerIdentifier(user.getEntityIdentifier());
	   	tag.setEntityIdentifier(entry.getEntityIdentifier());
	  	tag.setName(newtag);
	  	coreDao.save(tag);
	}
	public void deleteTag(Long binderId, Long entryId, String tagId) {
		FolderEntry entry = getEntry(binderId, entryId);
	   	Tag tag = coreDao.loadTagById(tagId);
	   	getCoreDao().delete(tag);
	}
	public void setUserRating(Long folderId, Long entryId, long value) {
		FolderEntry entry = getEntry(folderId, entryId);
		EntityIdentifier id = entry.getEntityIdentifier();
		//set user rating
       	getProfileModule().setRating(id, value);
       	//update entry average
     	Object[] cfValues = new Object[]{id.getEntityId(), id.getEntityType().getValue()};
    	// see if title exists for this folder
     	float result = getCoreDao().averageColumn(Rating.class, "rating", new FilterControls(ratingAttrs, cfValues));
     	entry.setRating(Float.valueOf(result));
		
	}
	public void setUserRating(Long folderId, long value) {
		Folder folder = loadFolder(folderId);
		EntityIdentifier id = folder.getEntityIdentifier();
		//set user rating
       	getProfileModule().setRating(id, value);
       	//update folder average
     	Object[] cfValues = new Object[]{id.getEntityId(), id.getEntityType().getValue()};
    	// see if title exists for this folder
     	float result = getCoreDao().averageColumn(Rating.class, "rating", new FilterControls(ratingAttrs, cfValues));
     	folder.setRating(Float.valueOf(result));
		
	}    
	public void setUserVisit(Long folderId, Long entryId) {
		FolderEntry entry = getEntry(folderId, entryId);
		setUserVisit(entry);
	}
	public void setUserVisit(FolderEntry entry) {
		EntityIdentifier id = entry.getEntityIdentifier();
		//set user rating
       	getProfileModule().setVisit(id);
       	//update entry average
     	Object[] cfValues = new Object[]{id.getEntityId(), id.getEntityType().getValue()};
    	// see if title exists for this folder
     	long result = getCoreDao().sumColumn(Visits.class, "reads", new FilterControls(ratingAttrs, cfValues));
     	entry.setPopularity(Long.valueOf(result));		
	}
	
	public void setUserVisit(Long folderId) {
		Folder folder = loadFolder(folderId);
		setUserVisit(folder); 
	}
	public void setUserVisit(Folder folder) {
		EntityIdentifier id = folder.getEntityIdentifier();
		//set user rating
       	getProfileModule().setVisit(id);
       	//update entry average
     	Object[] cfValues = new Object[]{id.getEntityId(), id.getEntityType().getValue()};
    	// see if title exists for this folder
     	long result = getCoreDao().sumColumn(Visits.class, "reads", new FilterControls(ratingAttrs, cfValues));
     	folder.setPopularity(Long.valueOf(result));
	}   
	
	public List<String> getFolderIds(Integer type) {
    	// TODO 
    	// NOTE: This implementation utilizes database lookup to fetch the
    	// entire list of folders in the system and then test each one against
    	// access control. This is unacceptably inefficient especially when
    	// there are large number of folders in the system. This MUST be
    	// re-implemented to use search engine index in the same way that
    	// the index is used for querying for a list of entries. When this
    	// reimplementation is done, the type of the return object will need
    	// to change from the simple List<String> to something more involving
    	// (eg. Map) to accomodate the various pieces of information returned 
    	// from the search index lookup (similar to getBinderEntries method
    	// in AbstractEntryProcessor class).  
    	
    	String zoneName = RequestContextHolder.getRequestContext().getZoneName();
    	
    	FilterControls filter = null;
    	
    	if(type != null) {
    		filter = new FilterControls(new String[]{"zoneName", "definitionType"}, new Object[]{zoneName, Integer.valueOf(type)});
    	}
    	else {
    		filter = new FilterControls("zoneName", zoneName);
    	}
    	
    	List folders = getCoreDao().loadObjects(new ObjectControls(Folder.class), filter);
    	
    	List<String> result = new ArrayList<String>(folders.size());
    	for(int i = 0; i < folders.size(); i++) {
    		Folder folder = (Folder) folders.get(i);
    		// Check if the user has "read" access to the folder.
    		if(getAccessControlManager().testOperation(folder, WorkAreaOperation.READ_ENTRIES))
    			result.add(folder.getId().toString());
    		else
    			continue;
    	}
    	
    	return result;
    }

    public void reserveEntry(Long folderId, Long entryId)
	throws AccessControlException, ReservedByAnotherUserException,
	FilesLockedByOtherUsersException {
    	// Because I don't expect customers to override or extend this 
    	// functionality, I don't delegate its implementation to a
    	// processor (Am I wrong about this?)
    	
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);

        // For now, check against the same access right needed for modifying
        // entry. We might want to have a separate right for reserving entry...
    	checkModifyEntryAllowed(entry);

        User user = RequestContextHolder.getRequestContext().getUser();
    	
    	HistoryStamp reservation = entry.getReservation();
    	if(reservation == null) { // The entry is not currently reserved. 
    		// We must check if any of the files in the entry is locked
    		// by another user. 
    		
    		// Make sure that the lock states are current before examining them.
    		getFileModule().RefreshLocks(folder, entry);
    		
    		// Now that lock states are up-to-date, we can examine them.
    		
    		if(entry.getLockedFileCount() > 0) { // At least one effective lock
	    		boolean allOwnedBySameUser = true;
	    		List fAtts = entry.getFileAttachments();
	    		for(int i = 0; i < fAtts.size(); i++) {
	    			FileAttachment fa = (FileAttachment) fAtts.get(i);
	    			if(fa.getFileLock() != null && !fa.getFileLock().getOwner().equals(user)) {
	    				allOwnedBySameUser = false;
	    				break;
	    			}
	    		}	
	    		if(allOwnedBySameUser) {
	    			// All remaining effective locks are owned by the same user.
	    			// Proceed and reserve the entry.
	    			entry.setReservation(user);
	    		}
	    		else { // One or more lock is held by someone else.
	    			// Build error information.
	    			List<FileLockInfo> info = new ArrayList<FileLockInfo>();
		    		for(int i = 0; i < fAtts.size(); i++) {
		    			FileAttachment fa = (FileAttachment) fAtts.get(i);
		    			if(fa.getFileLock() != null) {
		    				info.add(new FileLockInfo
		    						(fa.getRepositoryServiceName(), 
		    								fa.getFileItem().getName(), 
		    								fa.getFileLock().getOwner()));
		    			}
		    		}		    			
		    		throw new FilesLockedByOtherUsersException(info);
	    		}
    		}
    		else { // No effective lock
    			// Proceed and reserve the entry.
    			entry.setReservation(user);
    		}
    	}
    	else {	
    		// The entry is currently reserved. 
    		if(reservation.getPrincipal().equals(user)) {
    			// The entry is reserved by the same user. Noop.
    		}
    		else {
    			// The entry is reserved by another user.
    			throw new ReservedByAnotherUserException(entry);
    		}
    	}
    }

    /**
     * Convenience method to find all the unique tags 
     * in a set to return to the user.
     * 
     * @param allTags
     * @return
     */
    private List uniqueTags(List allTags) {
    	List newTags = new ArrayList<Tag>();
    	HashMap tagMap = new HashMap();
    	for (Iterator iter=allTags.iterator(); iter.hasNext();) {
			Tag thisTag = (Tag)iter.next();
			if (tagMap.containsKey(thisTag.getName())) continue;
			tagMap.put(thisTag.getName(),thisTag);
			newTags.add(thisTag);
    	}
    	return newTags;    	
    }
    /**
     * Helper classs to return folder unseen counts as an objects
     * @author Janet McCann
     *
     */
    public class Counter {
    	long count=0;
    	protected Counter() {	
    	}
    	public void increment() {
    		++count;
    	}
    	public long getCount() {
    		return count;
    	}
    	public String toString() {
    		return String.valueOf(count);
    	}
    	
    }
 }
