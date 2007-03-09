package com.sitescape.team.security.acl;

/**
 * Warning: The methods defined in this class are meant to be used by security 
 * manager only. Application should never invoke these methods directly. 
 * 
 * @author Jong Kim
 */
public interface AclControlled {
       
    public AclSet getAclSet();

    public void setAclSet(AclSet aclSet);
    
    public boolean getInheritAclFromParent();
    
    public void setInheritAclFromParent(boolean inherit);
    
    public Long getOwnerId();
}
