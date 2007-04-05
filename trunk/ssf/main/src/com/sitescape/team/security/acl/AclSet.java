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
