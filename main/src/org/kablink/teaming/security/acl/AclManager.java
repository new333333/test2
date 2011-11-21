/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.security.acl;

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
