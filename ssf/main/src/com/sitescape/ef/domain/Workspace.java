package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.sitescape.ef.module.ldap.LdapInfo;

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
    protected Set dataForums;
    protected boolean forumsParsed;
    protected List forums;
    protected LdapInfo ldapInfo;
    /**
     * @hibernate.bag lazy="true"  cascade="all" inverse="true" optimistic-lock="false"
	 * @hibernate.key column="owningWorkspace" 
	 * @hibernate.one-to-many class="com.sitescape.ef.domain.Binder"
     * @hibernate.cache usage="read-write"
     * @return
     */
    private List getHForums() {return forums;}
    private void setHForums(List forums) {this.forums = forums;} 
    
    public List getForums() {
        if (forums == null) forums = new ArrayList();
    	return forums;
    }
    public Set getFolders() {
      	if (!forumsParsed) {
    		parseForums();
    	}
         return dataForums;
    }
 
    public Set getWorkspaces() {
      	if (!forumsParsed) {
    		parseForums();
    	}
      	return workspaces;
    }
    public void addForum(Binder child) {
     	if (!forumsParsed) parseForums();
		if (child instanceof Workspace) {
    		workspaces.add(child);
    	} else {
    		dataForums.add(child);
    	}
		child.setOwningWorkspace(this);
	}
    public void removeForum(Binder child) {
     	if (!forumsParsed) parseForums();
 		if (child instanceof Workspace) {
    		workspaces.remove(child);
    	} else {
    		dataForums.remove(child);
    	}
		child.setOwningWorkspace(null);
 		
	}
    protected void parseForums() {
     	dataForums = new HashSet();
    	workspaces = new HashSet();
    	Iterator iter = getForums().iterator();
    	Binder f,w;
    	while (iter.hasNext()) {
    		f = (Binder)iter.next();
    		if (f==null) continue;
    		w = f.getOwningWorkspace();
    		if (w==null) continue;
    		if (w.getId().equals(getId())) {
    			if (f instanceof Workspace) {
    				workspaces.add(f);
    			} else {
    				dataForums.add(f);
    			}
    		}
    	}
    	forumsParsed=true;
    }

    public List getChildAclContainers() {
        return new ArrayList(this.getForums());
    }
    
    public List getChildAclControlled() {
        return new ArrayList(); // empty
    }
    //content is persisted in zone properties
    //keep here so we have a cached pointer to the object
    public LdapInfo getLdapInfo() {
    	if (ldapInfo == null) ldapInfo = new LdapInfo(this);
    	return ldapInfo;
    }
    
}
