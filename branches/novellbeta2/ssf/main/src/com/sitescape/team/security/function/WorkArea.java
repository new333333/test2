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
package com.sitescape.team.security.function;
import com.sitescape.team.domain.Principal;
/**
 * 
 * @author Jong Kim
 */
public interface WorkArea {
    
    public Long getWorkAreaId();
    
    /**
     * The type of the work area. 
     * The value must be between 1 and 16 characters long.
     * 
     * @return
     */
    public String getWorkAreaType();
    
    public WorkArea getParentWorkArea();
    public boolean isFunctionMembershipInheritanceSupported();

    public boolean isFunctionMembershipInherited();
    
    public void setFunctionMembershipInherited(boolean functionMembershipInherited);

    public Long getOwnerId();
    public Principal getOwner();
    public void setOwner(Principal owner);

}
