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
