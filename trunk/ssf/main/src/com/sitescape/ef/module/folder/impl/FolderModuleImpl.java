package com.sitescape.ef.module.folder.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.AverageRating;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
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
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Visits;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.jobs.FillEmailSubscription;
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
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.module.workflow.WorkflowUtils;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.TagUtil;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public class FolderModuleImpl extends CommonDependencyInjection implements FolderModule {
   	private String[] ratingAttrs = new String[]{"id.entityId", "id.entityType"};
    private String[] entryTypes = {EntityIndexUtils.ENTRY_TYPE_ENTRY};
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
        
        return loadProcessor(parentFolder).addBinder(parentFolder, def, Folder.class, inputData, fileItems).getId();

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
        
        return loadProcessor(folder).addEntry(folder, def, FolderEntry.class, inputData, fileItems).getId();
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
        FolderEntry reply = processor.addReply(entry, def, inputData, fileItems);
        Date stamp = reply.getCreation().getDate();
        scheduleSubscription(folder, reply, new Date(stamp.getTime()-1));
        
        return reply.getId();
    }
    public void checkAddReplyAllowed(FolderEntry entry) throws AccessControlException {
    	//TODO: this check is missing workflow checks??
    	getAccessControlManager().checkOperation(entry.getParentBinder(), WorkAreaOperation.ADD_REPLIES);
    }
    public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
			throws AccessControlException, WriteFilesException, ReservedByAnotherUserException {
    	modifyEntry(binderId, id, inputData, new HashMap(), null);
    }
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, 
    		WriteFilesException, ReservedByAnotherUserException {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkModifyEntryAllowed(entry);
        
        User user = RequestContextHolder.getRequestContext().getUser();
        HistoryStamp reservation = entry.getReservation();
        if(reservation != null && !reservation.getPrincipal().equals(user))
        	throw new ReservedByAnotherUserException(entry);
        
    	List<Attachment> atts = new ArrayList<Attachment>();
    	if (deleteAttachments != null) {
    		for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    			Object v = iter.next();
    			if(v instanceof String) {
    				String id = (String)v;
    				Attachment a = entry.getAttachment(id);
    				if (a != null) atts.add(a);
    			}
    			else if(v instanceof Attachment) {
    				atts.add((Attachment) v);
    			}
    			else {
    				throw new InternalException();
    			}
    		}
    	}
    	Date stamp = entry.getModification().getDate();
        processor.modifyEntry(folder, entry, inputData, fileItems, atts);
        if (!stamp.equals(entry.getModification().getDate())) scheduleSubscription(folder, entry, stamp);
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
    	Date stamp = entry.getWorkflowChange().getDate();
        processor.modifyWorkflowState(folder, entry, stateId, toState);
        if (!stamp.equals(entry.getWorkflowChange().getDate())) scheduleSubscription(folder, entry, stamp);
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

    public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData) {
        Folder folder = loadFolder(folderId);       
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        //TODO - Check some access
    	Date stamp = entry.getWorkflowChange().getDate();
        processor.setWorkflowResponse(folder, entry, stateId, inputData);
        if (!stamp.equals(entry.getWorkflowChange().getDate())) scheduleSubscription(folder, entry, stamp);
        
    }

    public List applyEntryFilter(Definition entryFilter) {
        // TODO Auto-generated method stub
        return null;
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
    	return getDomFolderTree(folderId, domTreeHelper, -1);
    }
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper, int levels) {
        Folder top = loadFolder(folderId);
        getAccessControlManager().checkOperation(top, WorkAreaOperation.READ_ENTRIES);
        
        User user = RequestContextHolder.getRequestContext().getUser();
    	Comparator c = new BinderComparator(user.getLocale());
    	    	
    	org.dom4j.Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	      	
  	    buildFolderDomTree(rootElement, top, c, domTreeHelper, levels);
  	    return wsTree;
  	}
    
    protected void buildFolderDomTree(Element current, Folder top, Comparator c, DomTreeBuilder domTreeHelper, int levels) {
       	Element next; 
       	Folder f;
    	   	
       	//callback to setup tree
    	domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_FOLDER, top, current);
 		if (levels == 0) return;
    	--levels;
    	
     	TreeSet folders = new TreeSet(c);
    	folders.addAll(top.getFolders());
       	for (Iterator iter=folders.iterator(); iter.hasNext();) {
       		f = (Folder)iter.next();
      	    // Check if the user has the privilege to view the folder 
       		if(!getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
       			continue;
       		next = current.addElement(DomTreeBuilder.NODE_CHILD);
       		buildFolderDomTree(next, f, c, domTreeHelper, levels);
       	}
    }
 
 
 
  
    public Map getEntries(Long folderId) {
        return getEntries(folderId, new HashMap());
    }

    public Map getEntries(Long folderId, Map options) {
        Folder folder = loadFolder(folderId);
        //search query does access checks
        return loadProcessor(folder).getBinderEntries(folder, entryTypes, options);
    }
    
    public Map getFullEntries(Long folderId) {
    	return getFullEntries(folderId, new HashMap());
    }
    
    public Map getFullEntries(Long folderId, Map options) {
        //search query does access checks
        Map result =  getEntries(folderId, options);
        //now load the full database object
        List childEntries = (List)result.get(ObjectKeys.SEARCH_ENTRIES);
        ArrayList ids = new ArrayList();
        for (int i=0; i<childEntries.size();) {
        	Map searchEntry = (Map)childEntries.get(i);
        	String docId = (String)searchEntry.get(EntityIndexUtils.DOCID_FIELD);
        	try {
        		Long id = Long.valueOf(docId);
        		ids.add(id);
        		++i;
        	} catch (Exception ex) {
        		childEntries.remove(i);
        	}
        }
        List entries = getCoreDao().loadObjects(ids, FolderEntry.class, null);
        //return them in the same order
        List fullEntries = new ArrayList(entries.size());
        for (int i=0; i<childEntries.size(); ++i) {
        	Map searchEntry = (Map)childEntries.get(i);
        	String docId = (String)searchEntry.get(EntityIndexUtils.DOCID_FIELD);
       		Long id = Long.valueOf(docId);
       		for (int j=0; j<entries.size(); ++j) {
       			FolderEntry fe = (FolderEntry)entries.get(j);
       			if (id.equals(fe.getId())) {
       				fullEntries.add(fe);
       				entries.remove(j);
       				break;
       			}
       		}
        }
        	
        result.put(ObjectKeys.FULL_ENTRIES, fullEntries);
        return result;
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
	        Date modifyDate = new Date();
	        for (int i = 0; i < hits.length(); i++) {
				String folderIdString = hits.doc(i).getField(IndexUtils.TOP_FOLDERID_FIELD).stringValue();
				String entryIdString = hits.doc(i).getField(EntityIndexUtils.DOCID_FIELD).stringValue();
				Long entryId = null;
				if (entryIdString != null && !entryIdString.equals("")) {
					entryId = new Long(entryIdString);
				}
				try {
					modifyDate = DateTools.stringToDate(hits.doc(i).getField(EntityIndexUtils.MODIFICATION_DATE_FIELD).stringValue());
				} catch (ParseException pe) {} // no need to do anything
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
    	rangeElement.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.MODIFICATION_DAY_FIELD);
    	rangeElement.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, QueryBuilder.INCLUSIVE_TRUE);
    	Element startRange = rangeElement.addElement(QueryBuilder.RANGE_START);
    	Date now = new Date();
    	Date startDate = new Date(now.getTime() - ObjectKeys.SEEN_MAP_TIMEOUT);
    	startRange.addText(EntityIndexUtils.formatDayString(startDate));
    	Element finishRange = rangeElement.addElement(QueryBuilder.RANGE_FINISH);
    	finishRange.addText(EntityIndexUtils.formatDayString(now));
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
    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
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
    public void addSubscription(Long folderId, Long entryId, int style) {
    	//getEntry check read access
		FolderEntry entry = getEntry(folderId, entryId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		//digest doesn't make sense here - only individual messages are sent 
		if (s == null) {
			s = new Subscription(user.getId(), entry.getEntityIdentifier());
			s.setStyle(style);
			getCoreDao().save(s);
		} else 	s.setStyle(style);
  	
    }
    public Subscription getSubscription(Long folderId, Long entryId) {
    	//getEntry check read access
		FolderEntry entry = getEntry(folderId, entryId);
		User user = RequestContextHolder.getRequestContext().getUser();
		return getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
    }
    public void deleteSubscription(Long folderId, Long entryId) {
    	//getEntry check read access
		FolderEntry entry = getEntry(folderId, entryId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		if (s != null) getCoreDao().delete(s);
    }
/* not needed
    public void modifySubscription(Long folderId, Long entryId, Map updates) {
		FolderEntry entry = getEntry(folderId, entryId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		if (s == null) {
			s = new Subscription(user.getId(), entry.getEntityIdentifier());
			getCoreDao().save(s);		
		}
    	ObjectBuilder.updateObject(s, updates);
    }
*/
    public List getCommunityTags(Long binderId, Long entryId) {
		FolderEntry entry = getEntry(binderId, entryId);
		List tags = new ArrayList<Tag>();
		tags = getCoreDao().loadCommunityTagsByEntity(entry.getEntityIdentifier());
		return TagUtil.uniqueTags(tags);		
	}
	public List getPersonalTags(Long binderId, Long entryId) {
		FolderEntry entry = getEntry(binderId, entryId);
		List tags = new ArrayList<Tag>();
		User user = RequestContextHolder.getRequestContext().getUser();
		tags = getCoreDao().loadPersonalEntityTags(entry.getEntityIdentifier(),user.getEntityIdentifier());
		return TagUtil.uniqueTags(tags);		
	}
	public void modifyTag(Long binderId, Long entryId, String tagId, String newtag) {
		FolderEntry entry = getEntry(binderId, entryId);
	   	Tag tag = coreDao.loadTagById(tagId);
	   	tag.setName(newtag);
	}
	public void setTag(Long binderId, Long entryId, String newtag, boolean community) {
		FolderEntry entry = getEntry(binderId, entryId);
	   	Tag tag = new Tag();
	   	User user = RequestContextHolder.getRequestContext().getUser();
	   	tag.setOwnerIdentifier(user.getEntityIdentifier());
	   	tag.setEntityIdentifier(entry.getEntityIdentifier());
	    tag.setPublic(community);
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
		setRating(entry, value);
	}
	public void setUserRating(Long folderId, long value) {
		Folder folder = loadFolder(folderId);
		setRating(folder, value);
	} 
	private void setRating(DefinableEntity entity, long value) {
		EntityIdentifier id = entity.getEntityIdentifier();
		//update entity average
     	Object[] cfValues = new Object[]{id.getEntityId(), id.getEntityType().getValue()};
		Rating rating = getProfileModule().getRating(id);
		if (rating == null) {
		   	User user = RequestContextHolder.getRequestContext().getUser();
      		rating = new Rating(user.getId(), id);
			getCoreDao().save(rating);
		} 
		//set user rating
		rating.setRating(value);
    	// see if title exists for this folder
		FilterControls filter = new FilterControls(ratingAttrs, cfValues);
     	float result = getCoreDao().averageColumn(Rating.class, "rating", filter);
     	int count = getCoreDao().countObjects(Rating.class,filter);
     	AverageRating avg = entity.getAverageRating();
     	if (avg == null) {
     		avg = new AverageRating();
     		entity.setAverageRating(avg);
     	}
     	avg.setAverage(result);
   		avg.setCount(count);
 			
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
     	long result = getCoreDao().sumColumn(Visits.class, "readCount", new FilterControls(ratingAttrs, cfValues));
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
     	long result = getCoreDao().sumColumn(Visits.class, "readCount", new FilterControls(ratingAttrs, cfValues));
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
    	
    	List folders = getCoreDao().loadObjects(Folder.class, filter);
    	
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
    		
    		// Make sure that the file lock states are current before examining them.
    		getFileModule().RefreshLocks(folder, entry);
    		
    		// Now that lock states are up-to-date, we can examine them.
    		
    		boolean atLeastOneFileLockedByAnotherUser = false;
    		List fAtts = entry.getFileAttachments();
    		for(int i = 0; i < fAtts.size(); i++) {
    			FileAttachment fa = (FileAttachment) fAtts.get(i);
    			if(fa.getFileLock() != null && !fa.getFileLock().getOwner().equals(user)) {
    				atLeastOneFileLockedByAnotherUser = true;
    				break;
    			}
    		}	
    		
    		if(!atLeastOneFileLockedByAnotherUser) {
    			// All remaining effective locks are owned by the same user
    			// or there are no effective locks at all.
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
	    						(fa.getRepositoryName(), 
	    								fa.getFileItem().getName(), 
	    								fa.getFileLock().getOwner()));
	    			}
	    		}		    			
	    		throw new FilesLockedByOtherUsersException(info);
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
    
    public void unreserveEntry(Long folderId, Long entryId)
	throws AccessControlException, ReservedByAnotherUserException {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);

        // I will skip checking the user's access right for this operation.
        // If the user previously reserved the entry successfully, it is
        // inconceivable that the user no longer has the right to unreserve
        // the entry (although it is possible in theory...). If the user
        // hasn't been able to reserve it previously, unreserve won' work
        // anyway. So either way, we can skip the access checking. 
    	//checkModifyEntryAllowed(entry);

        User user = RequestContextHolder.getRequestContext().getUser();
    	
    	HistoryStamp reservation = entry.getReservation();
    	if(reservation == null) { 
    		// The entry is not currently reserved by anyone. 
    		// Nothing to do. 
    	}
    	else {
    		if(reservation.getPrincipal().equals(user)) {
    			// The entry is currently reserved by the same user. 
    			// Cancel the reservation.
    			entry.clearReservation();
    		}
    		else {
    			// The entry is currently reserved by another user. 
    			throw new ReservedByAnotherUserException(entry);
    		}
    	}
    }

    public FolderEntry getFileFolderEntryByTitle(Folder fileFolder, String title)
	throws AccessControlException {
    	FolderEntry entry = getFileModule().findFileFolderEntry(fileFolder, title);

    	if(entry == null)
    		return null;
    	
        AccessUtils.readCheck(entry);

    	return entry;
    }
 
    public Set<String> getSubfoldersTitles(Folder folder) {
        User user = RequestContextHolder.getRequestContext().getUser();

    	TreeSet<String> titles = new TreeSet<String>();
   		
    	for(Object o : folder.getFolders()) {
    		Folder f = (Folder) o;
    		if(getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
    			titles.add(f.getTitle());
    	}
    	
    	return titles;    	
    }
    
    public Set<Folder> getSubfolders(Folder folder) {
        User user = RequestContextHolder.getRequestContext().getUser();

        Set<Folder> subFolders = new HashSet<Folder>();
   		
    	for(Object o : folder.getFolders()) {
    		Folder f = (Folder) o;
    		if(getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
    			subFolders.add(f);
    	}
    	
    	return subFolders;    	
    }
    
    /*
    public Collection getFolderTree(Long folderId) throws AccessControlException {
   		Folder top = loadFolder(folderId);
        getAccessControlManager().checkOperation(top, WorkAreaOperation.READ_ENTRIES);
        return getFolderTree(top);
   	}
   	
   	public Collection getFolderTree(Folder folder) {        
        User user = RequestContextHolder.getRequestContext().getUser();
    	Comparator c = new BinderComparator(user.getLocale());

    	TreeSet<Folder> folders = new TreeSet<Folder>(c);
   		
    	for(Object o : folder.getFolders()) {
    		Folder f = (Folder) o;
    		if(getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
    			folders.add(f);
    	}
    	
    	return folders;
   	}*/
    private void scheduleSubscription(Folder folder, FolderEntry entry, Date when) {
  		FillEmailSubscription process = (FillEmailSubscription)processorManager.getProcessor(folder, FillEmailSubscription.PROCESSOR_KEY);
  		//if anyone subscribed to the topLevel entry, notify them of a change
  		FolderEntry parent = entry.getTopEntry();
  		if (parent == null) parent = entry;
  		if (!getCoreDao().loadSubscriptionByEntity(parent.getEntityIdentifier()).isEmpty())
  			process.schedule(folder.getId(), entry.getId(), when);
    	
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
