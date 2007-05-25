/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.util.NLT;
import com.sitescape.util.Validator;
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
    public Workspace() {
    	setType(EntityIdentifier.EntityType.workspace.name());
    }
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.workspace;
	}
 
	/**
	 * @hibernate.property length=255
	 */
	public String getSearchTitle() {
	   	Integer type = getDefinitionType();
	   	if ((type != null) && (type.intValue() == Definition.USER_WORKSPACE_VIEW)) {
    		if (Validator.isNotNull(searchTitle)) return searchTitle;
    	}
		return super.getSearchTitle();
    }
	public void setSearchTitle(String searchTitle) {
	   	Integer type = getDefinitionType();
    	if ((type != null) && (type.intValue() == Definition.USER_WORKSPACE_VIEW)) {
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
}
