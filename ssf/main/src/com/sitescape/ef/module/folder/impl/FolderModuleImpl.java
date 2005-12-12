package com.sitescape.ef.module.folder.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.lucene.document.DateField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.web.WebKeys;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
/**
 *
 * @author Jong Kim
 */
public class FolderModuleImpl extends CommonDependencyInjection implements FolderModule {
    
    protected DefinitionModule definitionModule;
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}

	public List getFolders(List folderIds) {
		List result = new ArrayList();
		for (int i=0; i<folderIds.size(); ++i) {
			try {
				Folder f = getFolder((Long)folderIds.get(i));
				result.add(f);
			} catch (NoFolderByTheIdException ex) {
			} catch (AccessControlException ax) {
			}
			
		}
		return result;
	}
	public Folder getFolder(Long folderId)
		throws NoFolderByTheIdException, AccessControlException {
		Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneName());
		
		// Check if the user has "read" access to the folder.
        getAccessControlManager().checkAcl(folder, AccessType.READ);
		
        return folder;        
	}    
	public List getSortedFolderList(List folderIds) {
		Map forumIdMap = new TreeMap();
		List foldersUnsorted = getFolders(folderIds);
		for (int i = 0; i < foldersUnsorted.size(); i++) {
			if (!forumIdMap.containsKey(((Folder)foldersUnsorted.get(i)).getTitle())) {
				forumIdMap.put(((Folder)foldersUnsorted.get(i)).getTitle(), new ArrayList());
			}
			List vl = (List) forumIdMap.get(((Folder)foldersUnsorted.get(i)).getTitle());
			vl.add(((Folder)foldersUnsorted.get(i)));
		}
		List forumIdList = new ArrayList();
		Iterator itForums = forumIdMap.entrySet().iterator();
		while (itForums.hasNext()) {
			Map.Entry me = (Map.Entry) itForums.next();
			List meValue = (List)me.getValue();
			for (int i = 0; i < meValue.size(); i++) {
				forumIdList.add(meValue.get(i));
			}
		}
		return forumIdList;
	}

    public Long addEntry(Long folderId, String definitionId, Map inputData, Map fileItems) throws AccessControlException {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder folder = folderDao.loadFolder(folderId, user.getZoneName());
        Definition def = getCoreDao().loadDefinition(definitionId, user.getZoneName());
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
        	(folder, FolderCoreProcessor.PROCESSOR_KEY);
        
        return processor.addEntry(folder, def, inputData, fileItems);
    }

    public Long addReply(Long folderId, Long parentId, String definitionId, Map inputData, Map fileItems) throws AccessControlException {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder folder = folderDao.loadFolder(folderId, user.getZoneName());
        Definition def = getCoreDao().loadDefinition(definitionId, user.getZoneName());
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
        	(folder, FolderCoreProcessor.PROCESSOR_KEY);
        //load parent entry
        FolderEntry entry = processor.getEntry(folder, parentId, CURRENT_ENTRY);
        return processor.addReply(entry, def, inputData, fileItems);
    }
    public void modifyEntry(Long folderId, Long entryId, Map inputData, Map fileItems) throws AccessControlException {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder folder = folderDao.loadFolder(folderId, user.getZoneName());
       // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor(
        	folder, FolderCoreProcessor.PROCESSOR_KEY);
        
        processor.modifyEntry(folder, entryId, inputData, fileItems);
    }

    public List applyEntryFilter(Definition entryFilter) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void modifyFolderConfiguration(Long folderId, List definitionIds, Map workflowAssociations) 
    		throws AccessControlException {
    	modifyFolderConfiguration(folderId, definitionIds);
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
 		Folder folder = getFolderDao().loadFolder(folderId, companyId);
        getAccessControlManager().checkAcl(folder, AccessType.WRITE);    	
        folder.setProperty(ObjectKeys.FOLDER_WORKFLOW_ASSOCIATIONS, workflowAssociations);
    }
    public void modifyFolderConfiguration(Long folderId, List definitionIds) throws AccessControlException {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
    	List definitions = new ArrayList(); 
		Definition def;
		Folder folder = getFolderDao().loadFolder(folderId, companyId);
        getAccessControlManager().checkAcl(folder, AccessType.WRITE);    	
		//Build up new set - domain object will handle associations
    	if (definitionIds != null) {
    		for (int i=0; i<definitionIds.size(); ++i) {
    			def = getCoreDao().loadDefinition((String)definitionIds.get(i), companyId);
    			//	TODO:	getAccessControlManager().checkAcl(def, AccessType.READ);
    			definitions.add(def);
    		}
    	}
     	
		folder.setDefinitions(definitions);
    }
    
    public void indexFolderTree(Long folderId) {
		Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneName());

    	//get sub-folders and index them all
		List folders = getFolderDao().loadFolderTree(folder);
		folders.add(folder);
		for (int i=0; i<folders.size(); ++i) {
	    	folder = (Folder) folders.get(i);
	    	try {
	    		FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor(
		            	folder, FolderCoreProcessor.PROCESSOR_KEY);
		    	processor.indexFolder(folder);
	    	}
	    	catch(AccessControlException e) {
	    		//Skip folders to which access is denied
	    		continue;
	    	}
		}
    }
    
    public void indexFolder(Long folderId) {
		Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneName());

        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor(
            	folder, FolderCoreProcessor.PROCESSOR_KEY);
        processor.indexFolder(folder);
    }

 
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder folder = folderDao.loadFolder(folderId, user.getZoneName());
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
    	(folder, FolderCoreProcessor.PROCESSOR_KEY);
    
        return processor.getDomFolderTree(folder, domTreeHelper);
    }
    
    public Map getFolderEntries(Long folderId) {
        return getFolderEntries(folderId, 0);
    }

    public Map getFolderEntries(Long folderId, int maxChildEntries) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder folder = folderDao.loadFolder(folderId, user.getZoneName());
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
    	(folder, FolderCoreProcessor.PROCESSOR_KEY);
    
        return processor.getFolderEntries(folder, maxChildEntries);
    }
    

    public Map getUnseenCounts(List folderIds) {
    	//search engine will do acl checks
        User user = RequestContextHolder.getRequestContext().getUser();
        SeenMap seenMap = coreDao.loadSeenMap(user.getId());
        Map results = new HashMap();
        List folders = new ArrayList();
        for (int i=0; i<folderIds.size(); ++i) {
        	try {
        		folders.add(folderDao.loadFolder((Long)folderIds.get(i), user.getZoneName()));
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
        
        try {
	        results = luceneSession.search(so.getQuery(),so.getSortBy(),0,0);
        }
        finally {
            luceneSession.close();
        }
        return results;
    }
    public Long addFolder(Long folderId, Folder folder) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder parentFolder = folderDao.loadFolder(folderId, user.getZoneName());
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
    	(parentFolder, FolderCoreProcessor.PROCESSOR_KEY);

        return processor.addFolder(parentFolder, folder);
    }
 
           
    public FolderEntry getEntry(Long parentFolderId, Long entryId) {
        return getEntry(parentFolderId, entryId, CURRENT_ENTRY);
    }
    public FolderEntry getEntry(Long parentFolderId, Long entryId, int type) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder parentFolder = folderDao.loadFolder(parentFolderId, user.getZoneName());
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
    	(parentFolder, FolderCoreProcessor.PROCESSOR_KEY);
        return processor.getEntry(parentFolder, entryId, type);
    }
     public Map getEntryTree(Long parentFolderId, Long entryId) {
    	return getEntryTree(parentFolderId, entryId, CURRENT_ENTRY);
    }
    public Map getEntryTree(Long parentFolderId, Long entryId, int type) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder parentFolder = folderDao.loadFolder(parentFolderId, user.getZoneName());
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
    	(parentFolder, FolderCoreProcessor.PROCESSOR_KEY);
        return processor.getEntryTree(parentFolder, entryId, type);   	
    }
    
    public void deleteEntry(Long parentFolderId, Long entryId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder parentFolder = folderDao.loadFolder(parentFolderId, user.getZoneName());
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
    	(parentFolder, FolderCoreProcessor.PROCESSOR_KEY);
        processor.deleteEntry(parentFolder, entryId);
    }
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
