package com.sitescape.ef.module.folder.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.dom4j.Document;
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
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.DomTreeBuilder;
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
		accessControlManager.checkOperation(folder, WorkAreaOperation.VIEW);
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
    
    public Hits getRecentEntries(List folders, Map seenMaps) {
    	Hits hits = null;
        FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
    	  (folders.get(0), FolderCoreProcessor.PROCESSOR_KEY);
        hits = processor.getUnseenEntries(folders, seenMaps);
    	return hits;
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
    
 }
