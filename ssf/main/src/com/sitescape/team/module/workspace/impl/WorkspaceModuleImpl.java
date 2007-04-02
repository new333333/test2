package com.sitescape.team.module.workspace.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.InternalException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoWorkspaceByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.binder.BinderComparator;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.util.Validator;

/**
 * @author Jong Kim
 *
 */
public class WorkspaceModuleImpl extends CommonDependencyInjection implements WorkspaceModule {

    protected DefinitionModule definitionModule;
	/*
	 * Check access to folder.  If operation not listed, assume read_entries needed
   	 * Use method names as operation so we can keep the logic out of application
	 * and easisly change the required rights
	 * @see com.sitescape.team.module.binder.BinderModule#checkAccess(com.sitescape.team.domain.Binder, java.lang.String)
	 */
	public boolean testAccess(Workspace workspace, String operation) {
		try {
			checkAccess(workspace, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	protected void checkAccess(Workspace workspace, String operation) throws AccessControlException {
		if ("getWorkspace".equals(operation)) {
			getAccessControlManager().checkOperation(workspace, WorkAreaOperation.READ_ENTRIES);
		} else if ("addFolder".equals(operation)) {
	    	getAccessControlManager().checkOperation(workspace, WorkAreaOperation.CREATE_BINDERS);
		} else if ("addWorkspace".equals(operation)) { 	
	    	getAccessControlManager().checkOperation(workspace, WorkAreaOperation.CREATE_BINDERS);
		} else {
	    	getAccessControlManager().checkOperation(workspace, WorkAreaOperation.READ_ENTRIES);
		}
	}

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
	private Workspace loadWorkspace(Long workspaceId)  {
        Workspace workspace = (Workspace)getCoreDao().loadBinder(workspaceId, RequestContextHolder.getRequestContext().getZoneId());
        if (workspace.isDeleted()) throw new NoBinderByTheIdException(workspace.getId());
        return workspace;
		
	}
	private BinderProcessor loadProcessor(Workspace workspace) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.team.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
		return (BinderProcessor)getProcessorManager().getProcessor(workspace, BinderProcessor.PROCESSOR_KEY);
	}

	public Workspace getWorkspace() 
   		throws NoWorkspaceByTheIdException, AccessControlException {
    	return getWorkspace(null);
    }
    public Workspace getWorkspace(Long workspaceId) 
    	throws NoWorkspaceByTheIdException, AccessControlException {
        Workspace workspace=null;        
         
        if (workspaceId == null) {
        	workspace = getCoreDao().findTopWorkspace(RequestContextHolder.getRequestContext().getZoneName());
        } else {
        	workspace = (Workspace)getCoreDao().loadBinder(workspaceId, RequestContextHolder.getRequestContext().getZoneId());  
        }
        if (workspace.isDeleted()) throw new NoBinderByTheIdException(workspace.getId());
		// Check if the user has "read" access to the workspace.
        checkAccess(workspace, "getWorkspace");
 
       return workspace;
    }
    public Workspace getTopWorkspace() {
		Workspace top = getCoreDao().findTopWorkspace(RequestContextHolder.getRequestContext().getZoneName());
		return top;
    }
   	public Collection getWorkspaceTree(Long id) throws AccessControlException {
    	Workspace top = getWorkspace(id);
        return getWorkspaceTree(top);
    }
   	
   	public Collection getWorkspaceTree(Workspace top) {
        User user = RequestContextHolder.getRequestContext().getUser();
      	//order result
        Comparator c = new BinderComparator(user.getLocale());
       	TreeSet<Binder> tree = new TreeSet<Binder>(c);
     	for (Iterator iter=top.getBinders().iterator(); iter.hasNext();) {
    		Binder b = (Binder)iter.next();
    		if (b.isDeleted()) continue;
    		// To make this method consistent with the Dom construction counterpart
    		// (ie, getDomWorkspaceTree), the following additional check is necessary 
    		// before testing its access control.
    		if ((b instanceof Folder) || (b instanceof Workspace)) {
    			// Check if the user has "read" access to the binder.
    			if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES))
    				tree.add(b);
    		}
        }
     	return tree;
   	}
    	 
   	public Set<String> getChildrenTitles(Workspace top) {
       	TreeSet<String> titles = new TreeSet<String>();
     	for (Iterator iter=top.getBinders().iterator(); iter.hasNext();) {
    		Binder b = (Binder)iter.next();
       		if (b.isDeleted()) continue;
       	 	// To make this method consistent with the Dom construction counterpart
    		// (ie, getDomWorkspaceTree), the following additional check is necessary 
    		// before testing its access control.
       		if ((b instanceof Folder) || (b instanceof Workspace)) {
       		     	// Check if the user has "read" access to the binder.
       			if(getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES))
       				titles.add(b.getTitle());
       		}
        }
     	return titles;
   		
   	}
   	
    public org.dom4j.Document getDomWorkspaceTree(DomTreeBuilder domTreeHelper) throws AccessControlException {
       	return getDomWorkspaceTree(null, domTreeHelper, -1);
    }
    public org.dom4j.Document getDomWorkspaceTree(Long id, DomTreeBuilder domTreeHelper, int levels) throws AccessControlException {
    	//getWorkspace does access check
    	Workspace top = getWorkspace(id);
 
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new BinderComparator(user.getLocale());
    	Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	buildWorkspaceDomTree(rootElement, top, c, domTreeHelper, levels);
    	return wsTree;
    }
    
    public org.dom4j.Document getDomWorkspaceTree(Long topId, Long bottomId, DomTreeBuilder domTreeHelper) throws AccessControlException {
        User user = RequestContextHolder.getRequestContext().getUser();
       	//getWorkspace does access check
    	Workspace top = getWorkspace(topId);
 		Workspace bottom = (Workspace)getCoreDao().loadBinder(bottomId, user.getZoneId());
        
        List<Workspace> ancestors = new ArrayList<Workspace>();
        Workspace parent = bottom;
        //build inverted list of parents
        while ((parent != null) && !parent.equals(top)) {
        	ancestors.add(parent);
        	parent = (Workspace)parent.getParentBinder();
        }
        if (parent == null) throw new InternalException("Top is not a parent"); 
        ancestors.add(parent);
        Comparator c = new BinderComparator(user.getLocale());
    	Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	for (int i=ancestors.size()-1; i>=0; --i) {
    		buildWorkspaceDomTree(rootElement, (Workspace)ancestors.get(i), c, domTreeHelper, 1);
    		if (i != 0) {
    			parent = ancestors.get(i-1);
    			rootElement = (Element)rootElement.selectSingleNode("./" + DomTreeBuilder.NODE_CHILD + "[@id='" + parent.getId() + "']");
    		}
    	}
    	return wsTree;
    }
 
    protected void buildWorkspaceDomTree(Element current, Workspace top, Comparator c, DomTreeBuilder domTreeHelper, int levels) {
    	Element next; 
    	
 		//callback to setup tree
    	domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_WORKSPACE, top, current);
 		if (levels == 0) return;
    	--levels;
		TreeSet ws = new TreeSet(c);
		List binders = top.getBinders();
		List searchBinders = null;
		if (binders.size() > 10) {  //what is the best number to avoid search??
			BinderProcessor processor = loadProcessor(top);
			Map searchResults = processor.getBinders(top, null);
			searchBinders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		}
		if (domTreeHelper.supportsType(DomTreeBuilder.TYPE_FOLDER, null)) {
			//get folders
			if (searchBinders != null) {
				for (int i=0; i<searchBinders.size(); ++i) {
					Map search = (Map)searchBinders.get(i);
					String entityType = (String)search.get(EntityIndexUtils.ENTITY_FIELD);
					if (EntityType.folder.name().equals(entityType)) {
						String sId = (String)search.get(EntityIndexUtils.DOCID_FIELD);
						try {
							Long id = Long.valueOf(sId);
							ws.add(getCoreDao().load(Folder.class, id));
						} catch (Exception ex) {continue;}					
					}				
				}
			} else {
				Set<Folder> folders = top.getFolders();
				for (Folder f: folders) {
					if(!getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
		 				continue;
					 ws.add(f);
				}
			}
			for (Iterator iter=ws.iterator(); iter.hasNext();) {
 				Folder f = (Folder)iter.next();
 	      		if (f.isDeleted()) continue;
 				// 	Check if the user has "read" access to the folder.
 				next = current.addElement(DomTreeBuilder.NODE_CHILD);
 				if (domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_FOLDER, f, next) == null) 
 					current.remove(next);
 			}
        }
    	ws.clear();
		//get workspaces
		if (searchBinders != null) {
			for (int i=0; i<searchBinders.size(); ++i) {
				Map search = (Map)searchBinders.get(i);
				String entityType = (String)search.get(EntityIndexUtils.ENTITY_FIELD);
				if (!EntityType.folder.name().equals(entityType)) {
					String sId = (String)search.get(EntityIndexUtils.DOCID_FIELD);
					try {
						Long id = Long.valueOf(sId);
						ws.add(getCoreDao().load(Folder.class, id));
					} catch (Exception ex) {continue;}					
				}				
			}
		} else {
			Set<Workspace> workspaces = top.getWorkspaces();
			for (Workspace w: workspaces) {
	     		// Check if the user has "read" access to the workspace.
				if(!getAccessControlManager().testOperation(w, WorkAreaOperation.READ_ENTRIES))
	 				continue;
				 ws.add(w);
			}
			
		}
    	
      	for (Iterator iter=ws.iterator(); iter.hasNext();) {
     		Workspace w = (Workspace)iter.next();
      		if (w.isDeleted()) continue;
      		next = current.addElement(DomTreeBuilder.NODE_CHILD);
   			buildWorkspaceDomTree(next, w, c, domTreeHelper, levels);
       	}    	
    }
 
    public Long addFolder(Long parentWorkspaceId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
    	Workspace parentWorkspace = loadWorkspace(parentWorkspaceId);
        checkAccess(parentWorkspace, "addFolder");
        Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        }
        
        Binder binder = loadProcessor(parentWorkspace).addBinder(parentWorkspace, def, Folder.class, inputData, fileItems);
        return binder.getId();
    }
 
     public Long addWorkspace(Long parentWorkspaceId,String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException {
    	Workspace parentWorkspace = loadWorkspace(parentWorkspaceId);
 
    	Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        }
        //allow users workspaces to be created for all users
    	if (parentWorkspace.isReserved() && ObjectKeys.PROFILE_ROOT_INTERNALID.equals(parentWorkspace.getInternalId())) { 
    		if ((def == null) || (def.getType() != Definition.USER_WORKSPACE_VIEW)) {
        		checkAccess(parentWorkspace, "addWorkspace");
    		}
    	} else {
    		checkAccess(parentWorkspace, "addWorkspace");
    	}
        
        return loadProcessor(parentWorkspace).addBinder(parentWorkspace, def, Workspace.class, inputData, fileItems).getId();
    }
 
}