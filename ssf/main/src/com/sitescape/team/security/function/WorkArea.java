package com.sitescape.team.security.function;
import com.sitescape.team.domain.User;
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
    public User getOwner();
    public void setOwner(User owner);

}
