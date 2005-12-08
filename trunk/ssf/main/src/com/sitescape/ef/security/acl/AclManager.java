package com.sitescape.ef.security.acl;

import java.util.Set;

/**
 *
 * @author Jong Kim
 */
public interface AclManager {
    
    public Set getMembers(AclContainer parent, AclControlled obj, AccessType accessType);
    
    public void addMember(AclContainer parent, AclControlled obj, AccessType accessType, Long memberId);
    
    public void removeMember(AclContainer parent, AclControlled obj, AccessType accessType, Long memberId);
    
    /**
     * Make the object inherit its acls from the parent. If the object
     * is already inheriting from the parent, this has no effect.
     * Otherwise, the acls currently associated with the object are
     * discarded.   
     * 
     * @param parent
     * @param obj
     */
    public void doInherit(AclContainer parent, AclControlled obj);
    
    /**
     * Make the object not inherit its acls from the parent. If the object
     * already maintains its own set of acls, this has no effect (i.e., it
     * does not change its acl set in that case). If it is currently
     * inheriting its acls from the parent, it makes its own copy of the acl
     * set and maintains it separately. That is, the object's acl set is
     * initialized to the same value of the parent.   
     * 
     * @param parent
     * @param obj
     */
    public void doNotInherit(AclContainer parent, AclControlled obj);
    
    public Set getMembers(AclContainer obj, AccessType accessType);
    
    public void addMember(AclContainer obj, AccessType accessType, Long memberId);
    
    public void removeMember(AclContainer obj, AccessType accessType, Long memberId);
    
    public void doInherit(AclContainer obj);
    
    public void doNotInherit(AclContainer obj);
    
    /**
     * Apply the container's acls to the objects in the container by making
     * the children inherit their acl sets from their parents. 
     * <p> 
     * If <code>recursive</code> is <code>true</code>, the propagation works
     * recursively to both containers and non-containers starting rom the 
     * specified container all the way down to the leaves. 
     * 
     * If <code>false</code>, it affects only those non-container objects
     * within the specified container. 
     * 
     * @param container
     * @param recursive
     */
    public void propagate(AclContainer container, boolean recursive);
}
