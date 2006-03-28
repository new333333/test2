package com.sitescape.ef.module.workspace.impl;


import java.util.Iterator;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Collection;
import java.util.List;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.NoWorkspaceByTheIdException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.module.binder.BinderComparator;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.ObjectBuilder;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author Jong Kim
 *
 */
public class WorkspaceModuleImpl extends CommonDependencyInjection implements WorkspaceModule {

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
        getAccessControlManager().checkAcl(workspace, AccessType.READ);
 
       return workspace;
    }
   	public Collection getWorkspaceTree(Long id) throws AccessControlException {
    	Workspace top;
        User user = RequestContextHolder.getRequestContext().getUser();
        if (id == null) top =  getCoreDao().findTopWorkspace(user.getZoneName());
        else top = (Workspace)getCoreDao().loadBinder(id, user.getZoneName());
		// Check if the user has "read" access to the workspace
        getAccessControlManager().checkAcl(top, AccessType.READ);
       	//order result
        Comparator c = new BinderComparator(user.getLocale());
       	TreeSet<Binder> tree = new TreeSet<Binder>(c);
     	for (Iterator iter=top.getBinders().iterator(); iter.hasNext();) {
    		Binder b = (Binder)iter.next();
       	    // Check if the user has the privilege to view the binder 
            try {
        		// Check if the user has "read" access to the binder.
                getAccessControlManager().checkAcl(b, AccessType.READ);
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
        getAccessControlManager().checkAcl(top, AccessType.READ);

        Comparator c = new BinderComparator(user.getLocale());
    	Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	buildWorkspaceDomTree(rootElement, top, c, domTreeHelper, levels);
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
                getAccessControlManager().checkAcl(f, AccessType.READ);
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
                getAccessControlManager().checkAcl(w, AccessType.READ);
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

    public Long addFolder(Long parentWorkspaceId, Map input) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Workspace parentWorkspace = (Workspace)getCoreDao().loadBinder(parentWorkspaceId, user.getZoneName());
    	getAccessControlManager().checkOperation(parentWorkspace, WorkAreaOperation.CREATE_FOLDERS);
 
    	Folder folder = new Folder();
    	folder.setZoneName(user.getZoneName());
        folder.setCreation(new HistoryStamp(user));
        folder.setModification(folder.getCreation());
        ObjectBuilder.updateObject(folder, input);
        parentWorkspace.addChild(folder);
        // The sub-folder inherits the default ACLs of the parent folder.
        // The default ACLs of the sub-folder can be changed subsequently. 
        getAclManager().doInherit(folder);
        getCoreDao().save(folder);
                  
        return folder.getId();
    }
 
    public void checkAddFolderAllowed(Workspace parentWorkspace) {
    	getAccessControlManager().checkOperation(parentWorkspace, WorkAreaOperation.CREATE_FOLDERS);    	
    }
    public Long addWorkspace(Long parentWorkspaceId, Map input) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Workspace parentWorkspace = (Workspace)getCoreDao().loadBinder(parentWorkspaceId, user.getZoneName());
    	getAccessControlManager().checkOperation(parentWorkspace, WorkAreaOperation.CREATE_FOLDERS);
 
    	Workspace ws = new Workspace();
    	ws.setZoneName(user.getZoneName());
    	ws.setCreation(new HistoryStamp(user));
    	ws.setModification(ws.getCreation());
        ObjectBuilder.updateObject(ws, input);
        parentWorkspace.addChild(ws);
        // The sub-folder inherits the default ACLs of the parent folder.
        // The default ACLs of the sub-folder can be changed subsequently. 
        getAclManager().doInherit(ws);
        getCoreDao().save(ws);
                  
        return ws.getId();
    }
 
    public void checkAddWorkspaceAllowed(Workspace parentWorkspace) {
    	getAccessControlManager().checkOperation(parentWorkspace, WorkAreaOperation.CREATE_FOLDERS);    	
    }

}