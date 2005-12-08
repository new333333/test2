package com.sitescape.ef.module.workspace.impl;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Locale;
import java.util.Comparator;
import java.util.TreeSet;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.module.binder.BinderComparator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author Jong Kim
 *
 */
public class WorkspaceModuleImpl implements WorkspaceModule {
	protected CoreDao coreDao;
    protected AccessControlManager accessControlManager;
     
    public void setCoreDao(CoreDao coreDao) {
        this.coreDao = coreDao;
    }
    protected CoreDao getCoreDao() {
        return this.coreDao;
  
    }
    protected AccessControlManager getAccessControlManager() {
        return accessControlManager;
    }
    public void setAccessControlManager(
            AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }
    public Map showWorkspace() {
    	return showWorkspace(null);
    }
    public Map showWorkspace(Long workspaceId) {
        Map model = new HashMap();
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
        model.put(ObjectKeys.WORKSPACE,workspace);
 
        return model;
    }
    public org.dom4j.Document getDomWorkspaceTree() throws AccessControlException {
    	return getDomWorkspaceTree(null);
    }
    	 
    public org.dom4j.Document getDomWorkspaceTree(Long id) throws AccessControlException {
    	Workspace top;
        User user = RequestContextHolder.getRequestContext().getUser();
        if (id == null) top =  getCoreDao().findTopWorkspace(user.getZoneName());
        else top = (Workspace)getCoreDao().loadBinder(id, user.getZoneName());
      	getAccessControlManager().checkOperation(top, WorkAreaOperation.VIEW);
        Comparator c = new BinderComparator(user.getLocale());
    	Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement("root");
    	buildWorkspaceDomTree(rootElement, top, c);
    	return wsTree;
    }
    
    protected void buildWorkspaceDomTree(Element current, Workspace top, Comparator c) {
    	Element next; 
    	Folder f;
    	Workspace w;
    	Element url;
    	
		current.addAttribute("type", "workspace");
		current.addAttribute("title", top.getTitle());
		current.addAttribute("id", top.getId().toString());
    	current.addAttribute("image", "workspace");
    	url = current.addElement("url");
    	url.addAttribute("struts_action", "/forum/view_forum");
     	url.addAttribute(ObjectKeys.FORUM_URL_FORUM_ID, top.getId().toString());
    	url.addAttribute(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_VIEW_FORUM);

       	TreeSet ws = new TreeSet(c);
    	ws.addAll(top.getFolders());
      	for (Iterator iter=ws.iterator(); iter.hasNext();) {
    		f = (Folder)iter.next();
      	    // Check if the user has the privilege to view the folder 
            try {
              	getAccessControlManager().checkOperation(f, WorkAreaOperation.VIEW);
            } catch (AccessControlException ac) {
               	continue;
            }
            next = current.addElement("child");
    		next.addAttribute("type", "forum");
    	   	next.addAttribute("title", f.getTitle());
    	   	next.addAttribute("id", f.getId().toString());
    		next.addAttribute("image", "forum");
        	url = next.addElement("url");
        	url.addAttribute(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/view_forum");

        	url.addAttribute(ObjectKeys.FORUM_URL_FORUM_ID, f.getId().toString());
        	url.addAttribute(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_VIEW_FORUM);
        }
    	ws.clear();
    	ws.addAll(top.getWorkspaces());
     	for (Iterator iter=ws.iterator(); iter.hasNext();) {
     		w = (Workspace)iter.next();
       	    // Check if the user has the privilege to view the folder 
            try {
               	getAccessControlManager().checkOperation(w, WorkAreaOperation.VIEW);
            } catch (AccessControlException ac) {
                	continue;
            }
     		next = current.addElement("child");
     		buildWorkspaceDomTree(next, w, c);
     	}    	
    }

}