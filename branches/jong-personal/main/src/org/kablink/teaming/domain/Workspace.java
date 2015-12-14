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
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.NotSupportedException;
import org.kablink.util.Validator;

/**
 * @hibernate.subclass discriminator-value="workspace" dynamic-update="true"
 * 
 * Manage child forums/workspaces ourselves.  The zone already contains all the
 * forums and each forum contains the information necessary to build the workspace
 * tree ourselves.  Zone and forums are cached in the secondary cache, so we want to 
 * use that.
 * 
 * @author Jong Kim
 *
 */
public class Workspace extends Binder  {
	protected String searchTitle; //set by hibernate acccess=field
	protected Boolean preDeleted;
	protected Long preDeletedWhen;
	protected Long preDeletedBy;
	
    public Workspace() {
    }
    public Workspace(Workspace workspace) {
       	super(workspace);
       	searchTitle = workspace.searchTitle;
    }
    public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.workspace;
	}
 
	/**
	 * @hibernate.property length=255
	 */
	public String getSearchTitle() {
	   	Integer type = getDefinitionType();
	   	if ((type != null) && ((type.intValue() == Definition.USER_WORKSPACE_VIEW) ||
	   			(type.intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW))) {
    		if (Validator.isNotNull(searchTitle)) return searchTitle;
    	}
		return super.getSearchTitle();
    }
	public void setSearchTitle(String searchTitle) {
	   	Integer type = getDefinitionType();
    	if ((type != null) && ((type.intValue() == Definition.USER_WORKSPACE_VIEW) ||
    			(type.intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW))) {
    		this.searchTitle = searchTitle;
    	} else throw new NotSupportedException("errorcode.notsupported.setTitle");
	}

	public List getBinders() {
    	if (binders == null) binders = new ArrayList();
    	return binders;
    }
    public Set getFolders() {
     	Set folders = new HashSet();
    	Binder f;
    	for (Iterator iter=getBinders().iterator(); iter.hasNext();) {
    		f = (Binder)iter.next();
   			if (f instanceof Folder) {
   				folders.add(f);
   			} 
    	}
      	return folders;
    }
 
    public Set getWorkspaces() {
     	Set workspaces = new HashSet();
    	Binder w;
    	for (Iterator iter=getBinders().iterator(); iter.hasNext();) {
    		w = (Binder)iter.next();
   			if (w instanceof Workspace) {
   				workspaces.add(w);
   			} 
    	}
      	return workspaces;
    }
    public void addFolder(Folder folder) {
		super.addBinder(folder);
	}
    public void removeFolder(Folder folder) {
 		super.removeBinder(folder);
 		
	}
    public void addWorkspace(Workspace workspace) {
		super.addBinder(workspace);
	}
    public void removeWorkspace(Workspace workspace) {
 		super.removeBinder(workspace);
 		
	}
 
    
    public List getChildAclContainers() {
        return new ArrayList(this.getBinders());
    }
    
    public List getChildAclControlled() {
        return new ArrayList(); // empty
    }
    public List getEntryDefinitions() {return new ArrayList();}
    public List getViewDefinitions() {
    	if (definitionType != null) 
    		return getDefs(definitionType);
    	else
    		return getDefs(Definition.WORKSPACE_VIEW);
    }

    @Override
    protected Binder newInstance() {
        return new Workspace();
    }

    /**
     * @hibernate.property
     * @return
     */
    public Boolean isPreDeleted() {
    	return ((null != preDeleted) && preDeleted);
    	
    }
    public void setPreDeleted(Boolean preDeleted) {
    	this.preDeleted = preDeleted;
    }
    
	/**
	 * @hibernate.property 
	 * @return
	 */
	public Long getPreDeletedWhen() {
		return preDeletedWhen;
	}
	public void setPreDeletedWhen(Long preDeletedWhen) {
		this.preDeletedWhen = preDeletedWhen;
	}
	
	/**
     * @hibernate.property
	 * @return
     */
	public Long getPreDeletedBy() {
		return preDeletedBy;
	}
	public void setPreDeletedBy(Long preDeletedBy) {
		this.preDeletedBy = preDeletedBy;
	}
}
