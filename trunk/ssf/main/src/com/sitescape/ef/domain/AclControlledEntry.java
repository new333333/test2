package com.sitescape.ef.domain;

import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AclSet;

/**
 *
 * @author Jong Kim
 */
public abstract class AclControlledEntry extends Entry implements AclControlled {

    private AclSet aclSet;
    private boolean inheritAclFromParent = true;
    
    /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly. 
     * 
     * @hibernate.component prefix="acl_"
     */
    public AclSet getAclSet() {
        return aclSet;
    }

    /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly. 
     */
    public void setAclSet(AclSet aclSet) {
        this.aclSet = aclSet;
    }

    /**
     * @hibernate.property column="acl_inheritFromParent" not-null="true"
     */
    public boolean getInheritAclFromParent() {
        return inheritAclFromParent;
    }

    public void setInheritAclFromParent(boolean inherit) {
        this.inheritAclFromParent = inherit;
    }
    
    public Long getCreatorId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }
}
