/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.workspace.impl;


import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.comparator.BinderComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoWorkspaceByTheIdException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;


/**
 * @author Jong Kim
 *
 */
public class WorkspaceModuleImpl extends CommonDependencyInjection implements WorkspaceModule {

	protected Log logger = LogFactory.getLog(getClass());
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

   public Workspace getWorkspace(Long workspaceId) 
    	throws NoWorkspaceByTheIdException, AccessControlException {
        Workspace workspace=null;        
         
        if (workspaceId == null) {
        	workspace = RequestContextHolder.getRequestContext().getZone();
        } else {
        	workspace = (Workspace)getCoreDao().loadBinder(workspaceId, RequestContextHolder.getRequestContext().getZoneId());  
        }
        if (workspace.isDeleted()) throw new NoBinderByTheIdException(workspace.getId());
		// Check if the user has "read" access to the workspace.
		try {
			getAccessControlManager().checkOperation(workspace, WorkAreaOperation.READ_ENTRIES);
		} catch(AccessControlException ace) {
			try {
				getAccessControlManager().checkOperation(workspace, WorkAreaOperation.VIEW_BINDER_TITLE);
			} catch(AccessControlException ace2) {
				throw ace;
			}
		}
 
       return workspace;
    }
   public Workspace getTopWorkspace() {
		Workspace top = RequestContextHolder.getRequestContext().getZone();
		// Check if the user has "read" access to the workspace.
		try {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.READ_ENTRIES);
		} catch(AccessControlException ace) {
			try {
				getAccessControlManager().checkOperation(top, WorkAreaOperation.VIEW_BINDER_TITLE);
			} catch(AccessControlException ace2) {
				throw ace;
			}
		}
		return top;
   }
   public Long getTopWorkspaceId() {
		Workspace top = RequestContextHolder.getRequestContext().getZone();
		return top.getId();
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
    			if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES) ||
    					getAccessControlManager().testOperation(b, WorkAreaOperation.VIEW_BINDER_TITLE))
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
       			if(b instanceof Folder) {
       				if(((Folder) b).isPreDeleted()) continue;
       			}
       			if(b instanceof Workspace) {
       				if(((Workspace) b).isPreDeleted()) continue;
       			}
       		    // Check if the user has "read" access to the binder.
       			if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES) ||
       					getAccessControlManager().testOperation(b, WorkAreaOperation.VIEW_BINDER_TITLE))
       				titles.add(b.getTitle());
       		}
        }
     	return titles;
   		
   	}
 
}