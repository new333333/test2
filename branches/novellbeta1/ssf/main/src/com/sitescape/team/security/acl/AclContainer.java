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

import java.util.List;

/**
 * <code>AclContainer</code> contains other objects and is used to specify
 * a default ACL for the objects contained in the container.
 * 
 * Warning: The methods defined in this class are meant to be used by security 
 * manager only. Application should never invoke these methods directly. 
 * 
 * @author Jong Kim
 */
public interface AclContainer extends AclControlled {
    
    /**
     * Returns the id of the acl container. 
     * 
     * @return
     */
    public Long getAclContainerId();
    
    /**
     * Return parent acl container. 
     * 
     * @return
     */
    public AclContainer getParentAclContainer();
    
    /**
     * Return a list of child acl containers. 
     * 
     * @return a list of <code>AclContainer</code>s
     */
    public List getChildAclContainers();
    
    /**
     * Returns a list of child acl-controlled objects that are not themselves
     * acl containers. 
     * <p>
     * This only returns immediate children and does not work recursively. 
     * 
     * @return a list of <code>AclControlled</code>s
     */
    public List getChildAclControlled();
}
