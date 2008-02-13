/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.workspace.impl;


import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.BinderComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoWorkspaceByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.binder.processor.BinderProcessor;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.util.Validator;

/**
 * @author Jong Kim
 *
 */
public class WorkspaceModuleImpl extends CommonDependencyInjection implements WorkspaceModule {

	protected Log logger = LogFactory.getLog(getClass());
    protected DefinitionModule definitionModule;

	public boolean testAccess(Workspace workspace, WorkspaceOperation operation) {
		try {
			checkAccess(workspace, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	public void checkAccess(Workspace workspace, WorkspaceOperation operation) throws AccessControlException {
		switch (operation) {
		case addFolder:
	    	getAccessControlManager().checkOperation(workspace, WorkAreaOperation.CREATE_FOLDERS);
	    	break;
		case addWorkspace:
	    	getAccessControlManager().checkOperation(workspace, WorkAreaOperation.CREATE_WORKSPACES);
	    	break;
	    default:
	    	throw new NotSupportedException(operation.toString(), "checkAccess");
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
		getAccessControlManager().checkOperation(workspace, WorkAreaOperation.READ_ENTRIES);
 
       return workspace;
    }
    public Workspace getTopWorkspace() {
		Workspace top = getCoreDao().findTopWorkspace(RequestContextHolder.getRequestContext().getZoneName());
		// Check if the user has "read" access to the workspace.
		getAccessControlManager().checkOperation(top, WorkAreaOperation.READ_ENTRIES);
		return top;
    }
   	public SortedSet<Binder> getWorkspaceTree(Long id) throws AccessControlException {
    	Workspace top = getWorkspace(id);
        User user = RequestContextHolder.getRequestContext().getUser();
      	//order result
        Comparator c = new BinderComparator(user.getLocale(), BinderComparator.SortByField.title);
       	TreeSet<Binder> tree = new TreeSet<Binder>(c);
     	for (Iterator iter=top.getBinders().iterator(); iter.hasNext();) {
    		Binder b = (Binder)iter.next();
    		if (b.isDeleted()) continue;
    		// To make this method consistent with the Dom construction counterpart
    		// (ie, getDomBinderTree), the following additional check is necessary 
    		// before testing its access control.
    		if ((b instanceof Folder) || (b instanceof Workspace)) {
    			// Check if the user has "read" access to the binder.
    			if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES))
    				tree.add(b);
    		}
        }
     	return tree;
   	}
    	 
   	public SortedSet<String> getChildrenTitles(Workspace top) {
       	TreeSet<String> titles = new TreeSet<String>();
     	for (Iterator iter=top.getBinders().iterator(); iter.hasNext();) {
    		Binder b = (Binder)iter.next();
       		if (b.isDeleted()) continue;
       	 	// To make this method consistent with the Dom construction counterpart
    		// (ie, getDomBinderTree), the following additional check is necessary 
    		// before testing its access control.
       		if ((b instanceof Folder) || (b instanceof Workspace)) {
       		     	// Check if the user has "read" access to the binder.
       			if(getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES))
       				titles.add(b.getTitle());
       		}
        }
     	return titles;
   		
   	}
   	
    //no transaction by default     
    public Long addFolder(Long parentWorkspaceId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options) throws AccessControlException, WriteFilesException {
 
    	Workspace parentWorkspace = loadWorkspace(parentWorkspaceId);
    	checkAccess(parentWorkspace, WorkspaceOperation.addFolder);
    	Definition def = null;
    	if (Validator.isNotNull(definitionId)) { 
    		def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
    	}
    	        
    	Binder binder = loadProcessor(parentWorkspace).addBinder(parentWorkspace, def, Folder.class, inputData, fileItems, options);
    	return binder.getId();
   }
 
    //no transaction by default
    public Long addWorkspace(Long parentWorkspaceId, String definitionId, InputDataAccessor inputData,
       		Map fileItems, Map options) throws AccessControlException, WriteFilesException {
    	Workspace parentWorkspace = loadWorkspace(parentWorkspaceId);
   		    
    	Definition def = null;
    	if (Validator.isNotNull(definitionId)) { 
    		def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
    	}
    	//allow users workspaces to be created for all users
    	if (parentWorkspace.isReserved() && ObjectKeys.PROFILE_ROOT_INTERNALID.equals(parentWorkspace.getInternalId())) { 
    		if ((def == null) || (def.getType() != Definition.USER_WORKSPACE_VIEW)) {
    			checkAccess(parentWorkspace, WorkspaceOperation.addWorkspace);
    		}
    	} else {
    		checkAccess(parentWorkspace, WorkspaceOperation.addWorkspace);
    	}
    	
    	return loadProcessor(parentWorkspace).addBinder(parentWorkspace, def, Workspace.class, inputData, fileItems, options).getId();
    }
 
}