package com.sitescape.ef.module.workspace.impl;


import java.util.Iterator;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.InternalException;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.NoWorkspaceByTheIdException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.module.binder.BinderComparator;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.binder.BinderProcessor;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.util.Validator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author Jong Kim
 *
 */
public class WorkspaceModuleImpl extends CommonDependencyInjection implements WorkspaceModule {

    protected DefinitionModule definitionModule;
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
        String companyId = RequestContextHolder.getRequestContext().getZoneName();
        return  (Workspace)getCoreDao().loadBinder(workspaceId, companyId);
		
	}
	private BinderProcessor loadProcessor(Workspace workspace) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
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
         
        User user = RequestContextHolder.getRequestContext().getUser();
        if (workspaceId == null) {
        	workspaceId  = user.getPreferredWorkspaceId();
        	if (workspaceId != null) {
            	workspace = (Workspace)getCoreDao().loadBinder(workspaceId, user.getZoneName());          		
        	} 
        	if (workspace == null) workspace = getCoreDao().findTopWorkspace(user.getZoneName());
        } else {
        	workspace = (Workspace)getCoreDao().loadBinder(workspaceId, user.getZoneName());  
        }
		// Check if the user has "read" access to the workspace.
        getAccessControlManager().checkOperation(workspace, WorkAreaOperation.READ_ENTRIES);
 
       return workspace;
    }
   	public Collection getWorkspaceTree(Long id) throws AccessControlException {
    	Workspace top;
        User user = RequestContextHolder.getRequestContext().getUser();
        if (id == null) top =  getCoreDao().findTopWorkspace(user.getZoneName());
        else top = (Workspace)getCoreDao().loadBinder(id, user.getZoneName());
		// Check if the user has "read" access to the workspace
        getAccessControlManager().checkOperation(top, WorkAreaOperation.READ_ENTRIES);
      	//order result
        Comparator c = new BinderComparator(user.getLocale());
       	TreeSet<Binder> tree = new TreeSet<Binder>(c);
     	for (Iterator iter=top.getBinders().iterator(); iter.hasNext();) {
    		Binder b = (Binder)iter.next();
       	    // Check if the user has the privilege to view the binder 
            try {
        		// Check if the user has "read" access to the binder.
            	   getAccessControlManager().checkOperation(b, WorkAreaOperation.READ_ENTRIES);
                tree.add(b);
            } catch (AccessControlException ac) {
               	continue;
            }
          }
     	return tree;
    }
    	    	     	    	 
    public org.dom4j.Document getDomWorkspaceTree(DomTreeBuilder domTreeHelper) throws AccessControlException {
       	return getDomWorkspaceTree(null, domTreeHelper, -1);
    }
    public org.dom4j.Document getDomWorkspaceTree(Long id, DomTreeBuilder domTreeHelper, int levels) throws AccessControlException {
    	Workspace top;
        User user = RequestContextHolder.getRequestContext().getUser();
        if (id == null) top =  getCoreDao().findTopWorkspace(user.getZoneName());
        else top = (Workspace)getCoreDao().loadBinder(id, user.getZoneName());
		
		// Check if the user has "read" access to the top folder.
        getAccessControlManager().checkOperation(top, WorkAreaOperation.READ_ENTRIES);

        Comparator c = new BinderComparator(user.getLocale());
    	Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	buildWorkspaceDomTree(rootElement, top, c, domTreeHelper, levels);
    	return wsTree;
    }
    
    public org.dom4j.Document getDomWorkspaceTree(Long topId, Long bottomId, DomTreeBuilder domTreeHelper) throws AccessControlException {
        User user = RequestContextHolder.getRequestContext().getUser();
        Workspace top = (Workspace)getCoreDao().loadBinder(topId, user.getZoneName());
		Workspace bottom = (Workspace)getCoreDao().loadBinder(bottomId, user.getZoneName());
		// Check if the user has "read" access to the top folder.
        getAccessControlManager().checkOperation(top, WorkAreaOperation.READ_ENTRIES);
        
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
    	Folder f;
    	Workspace w;
    	
  		--levels;
		//callback to setup tree
    	domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_WORKSPACE, top, current);
    	//order result
       	TreeSet ws = new TreeSet(c);
    	ws.addAll(top.getFolders());
      	for (Iterator iter=ws.iterator(); iter.hasNext();) {
    		f = (Folder)iter.next();
    		if (f.getTopFolder() != null) continue;
      	    // Check if the user has the privilege to view the folder 
            try {
        		// Check if the user has "read" access to the folder.
                getAccessControlManager().checkOperation(f, WorkAreaOperation.READ_ENTRIES);
            } catch (AccessControlException ac) {
               	continue;
            }
            next = current.addElement(DomTreeBuilder.NODE_CHILD);
           	domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_FOLDER, f, next);
         }
    	ws.clear();
    	ws.addAll(top.getWorkspaces());
     	for (Iterator iter=ws.iterator(); iter.hasNext();) {
     		w = (Workspace)iter.next();
       	    // Check if the user has the privilege to view the folder 
            try {
        		// Check if the user has "read" access to the folder.
                getAccessControlManager().checkOperation(w, WorkAreaOperation.READ_ENTRIES);
            } catch (AccessControlException ac) {
                	continue;
            }
     		next = current.addElement(DomTreeBuilder.NODE_CHILD);
     		if (levels != 0)
     			buildWorkspaceDomTree(next, w, c, domTreeHelper, levels);
     		else
     		   	domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_WORKSPACE, w, next);
       	}    	
    }
 
    public Long addFolder(Long parentWorkspaceId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
    	Workspace parentWorkspace = loadWorkspace(parentWorkspaceId);
        Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, parentWorkspace.getZoneName());
//        } else {
//        	def = parentWorkspace.getFolderDef();
        }
        
        return loadProcessor(parentWorkspace).addBinder(parentWorkspace, def, Folder.class, inputData, fileItems);

    }
 
    public void checkAddFolderAllowed(Workspace parentWorkspace) {
    	loadProcessor(parentWorkspace).addBinder_accessControl(parentWorkspace);    	
    }
    public Long addWorkspace(Long parentWorkspaceId,String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException {
    	Workspace parentWorkspace = loadWorkspace(parentWorkspaceId);
        Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, parentWorkspace.getZoneName());
//        } else {
//        	def = parentWorkspace.getDefaultWorkspaceDef();
        }
        
        return loadProcessor(parentWorkspace).addBinder(parentWorkspace, def, Workspace.class, inputData, fileItems);
    }
 
    public void checkAddWorkspaceAllowed(Workspace parentWorkspace) {
       	loadProcessor(parentWorkspace).addBinder_accessControl(parentWorkspace);    	
    }

 
}