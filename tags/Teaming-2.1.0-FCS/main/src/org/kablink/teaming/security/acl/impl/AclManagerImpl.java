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
package org.kablink.teaming.security.acl.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.security.acl.AccessType;
import org.kablink.teaming.security.acl.AclContainer;
import org.kablink.teaming.security.acl.AclControlled;
import org.kablink.teaming.security.acl.AclManager;
import org.kablink.teaming.security.acl.AclSet;


/**
 *
 * @author Jong Kim
 */
public class AclManagerImpl implements AclManager {

    public void propagate(AclContainer container, boolean recursive) {
        // Set the acls of the non-container type child objects to that of the 
        // parent. This is done by making them inherit their acls from the
        // parent. 
        List objects = container.getChildAclControlled();
        for(Iterator i = objects.iterator(); i.hasNext();) {
            AclControlled obj = (AclControlled) i.next();
            // TODO We much exclude those entries that are under control by a
            // workflow process...
            doInherit(container, obj);
        }
        
        if(recursive) {
            // Apply it to child containers too.
            List childContainers = container.getChildAclContainers();
            for(Iterator i = childContainers.iterator(); i.hasNext();) {
                AclContainer childContainer = (AclContainer) i.next();
                doInherit(container, childContainer);
                // Proceed recursively.
                propagate(childContainer, recursive);
            }
        }
    }
    
    public Set getMembers(AclContainer parent, AclControlled obj, AccessType accessType) {
        return getAclSet(parent, obj).getMemberIds(accessType);
    }

    public void addMember(AclContainer parent, AclControlled obj, AccessType accessType, Long memberId) {
        initOwnAclSet(parent, obj);
        obj.getAclSet().addMemberId(accessType, memberId);
    }

    public void removeMember(AclContainer parent, AclControlled obj, AccessType accessType, Long memberId) {
        initOwnAclSet(parent, obj);
        obj.getAclSet().removeMemberId(accessType, memberId);
    }

    public void doInherit(AclContainer parent, AclControlled obj) {
        if(obj.getInheritAclFromParent()) {
            // It is already inheriting from the parent. No op. 
        }
        else {
            // The object has its own set of acls.
            obj.getAclSet().clear();
            obj.setInheritAclFromParent(true);
        }
    }

    public void doNotInherit(AclContainer parent, AclControlled obj) {
        initOwnAclSet(parent, obj);
    }

    public Set getMembers(AclContainer obj, AccessType accessType) {
        return getMembers(obj.getParentAclContainer(), obj, accessType);
    }

    public void addMember(AclContainer obj, AccessType accessType, Long memberId) {
        addMember(obj.getParentAclContainer(), obj, accessType, memberId);
    }

    public void removeMember(AclContainer obj, AccessType accessType, Long memberId) {
        removeMember(obj.getParentAclContainer(), obj, accessType, memberId);
    }

    public void doInherit(AclContainer obj) {
        doInherit(obj.getParentAclContainer(), obj);
    }

    public void doNotInherit(AclContainer obj) {
        doNotInherit(obj.getParentAclContainer(), obj);
    }
    
    private AclSet getAclSet(AclContainer parent, AclControlled obj) {
        if(obj.getInheritAclFromParent()) {
            return getAclSet(parent.getParentAclContainer(), parent);
        }
        else {
            return obj.getAclSet();
        }
    }
    
    private AclSet getAclSet(AclContainer obj) {
        return getAclSet(obj.getParentAclContainer(), obj);
    }
    
    private void initOwnAclSet(AclContainer parent, AclControlled obj) {
        if(obj.getInheritAclFromParent()) {
            // The object is currently inheriting its acls from the parent. 
            // Make a local copy. 
            obj.setAclSet((AclSet) getAclSet(parent).clone());
            obj.setInheritAclFromParent(false);
        }
        else {
            // The object already has its own set of acls. In this case,
            // do not reset or reinitialize them. 
        }
    }
}
