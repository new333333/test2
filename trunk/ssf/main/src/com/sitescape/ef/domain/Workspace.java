package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @hibernate.subclass discriminator-value="WORKSPACE" dynamic-update="true"
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
	protected Set workspaces; 
    protected Set folders;
    protected boolean bindersParsed;
    protected List binders;
   /**
     * @hibernate.bag lazy="true"  cascade="all" inverse="true" optimistic-lock="false"
	 * @hibernate.key column="parentBinder" 
	 * @hibernate.one-to-many class="com.sitescape.ef.domain.Binder"
     * @hibernate.cache usage="read-write"
     * @return
     */
    private List getHBinders() {return binders;}
    private void setHBinders(List binders) {this.binders = binders;} 
    public List getBinders() {
    	if (binders == null) binders = new ArrayList();
    	return binders;
    }
    public Set getFolders() {
      	if (!bindersParsed) {
    		parseBinders();
    	}
         return folders;
    }
 
    public Set getWorkspaces() {
      	if (!bindersParsed) {
      		parseBinders();
    	}
      	return workspaces;
    }
    public void addChild(Binder child) {
     	if (!bindersParsed) parseBinders();
		if (child instanceof Workspace) {
    		workspaces.add(child);
    	} else if (child instanceof Folder){
    		folders.add(child);
    	}
		binders.add(child);
		child.setParentBinder(this);
	}
    public void removeChild(Binder child) {
     	if (!bindersParsed) parseBinders();
 		if (child instanceof Workspace) {
    		workspaces.remove(child);
    	} else {
    		folders.remove(child);
    	}
 		binders.remove(child);
		child.setParentBinder(null);
 		
	}
    protected void parseBinders() {
     	folders = new HashSet();
    	workspaces = new HashSet();
    	Iterator iter = getBinders().iterator();
    	Binder f,w;
    	while (iter.hasNext()) {
    		f = (Binder)iter.next();
   			if (f instanceof Workspace) {
   				workspaces.add(f);
   			} else if (f instanceof Folder){
   				folders.add(f);
   			}
    	}
    	bindersParsed=true;
    }
    /**
     * Overload so we can return parents definition if not set for this folder
     */
    public Definition getDefaultPostingDef() {
    	Definition def = super.getDefaultPostingDef();
    	if (def != null) return def;
    	if (getParentBinder() != null) return getParentBinder().getDefaultPostingDef();
    	return null;
    }
    
    public List getChildAclContainers() {
        return new ArrayList(this.getBinders());
    }
    
    public List getChildAclControlled() {
        return new ArrayList(); // empty
    }
    public List getEntryDefs() {return new ArrayList();}
    public List getBinderViewDefs() {return new ArrayList();}

}
