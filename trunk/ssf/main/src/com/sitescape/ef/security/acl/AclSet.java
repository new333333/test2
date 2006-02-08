package com.sitescape.ef.security.acl;


import java.util.Set;


/**
 * <code>AclSet</code> maintains a set of principal ids for each access type.
 * 
 * @author Jong Kim
 */
public interface AclSet extends Cloneable {
        
    public Set getMemberIds(AccessType accessType);
    public void addMemberId(AccessType accessType, Long memberId);
    public boolean removeMemberId(AccessType accessType, Long memberId);
    public void clear();
    public Object clone();
}
