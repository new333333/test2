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
package org.kablink.teaming.security.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.acl.AccessType;
import org.kablink.teaming.security.acl.AclAccessControlException;
import org.kablink.teaming.security.acl.AclContainer;
import org.kablink.teaming.security.acl.AclControlled;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public class NullAccessControlManager implements AccessControlManager {
	private static Set groups;
	private Boolean lock = new Boolean(true);
	private ProfileDao profileDao;
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}
	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	
	public Set getWorkAreaAccessControl(WorkArea workArea, WorkAreaOperation workAreaOperation) {
		if (groups == null) {
			synchronized (lock) {
				if (groups == null) {
					List result = profileDao.loadGroups(new FilterControls(), RequestContextHolder.getRequestContext().getZoneId());
					groups = new HashSet();
					for (int i=0; i<result.size(); ++i) {
						groups.add(((Group)result.get(i)).getId());
					}
				}
			}
		}
		return groups;
	}
    public void checkOperation(WorkArea workArea, WorkAreaOperation workAreaOperation) throws AccessControlException {
    }

    public boolean testOperation(WorkArea workArea, WorkAreaOperation workAreaOperation) {
        return true; // Permission granted with no checking. 
    }
    
    public boolean testOperation(User user, WorkArea workArea, WorkAreaOperation workAreaOperation, boolean checkSharing) {
    	return true;
    }

    public void checkOperation(User user, WorkArea workArea, WorkAreaOperation workAreaOperation) throws AccessControlException {
    }

    public boolean testOperation(User user, WorkArea workArea, WorkAreaOperation workAreaOperation) {
        return true; // Permission granted with no checking. 
    }

	public void checkFunction(WorkArea workArea, Function function) 
		throws AccessControlException {
	}
	public void checkFunction(User user, WorkArea workArea, Function function) 
		throws AccessControlException {
	}
     
	public boolean testFunction(WorkArea workArea, Function function) {
		return true;
	}
	public boolean testFunction(User user,	WorkArea workArea, Function function) {
		return true;
	}   
	public void checkAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType) throws AccessControlException {
    }

    public boolean testAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType) {
        return true; // Permission granted with no checking. 
    }

    public void checkAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType) throws AccessControlException {
    }

    public boolean testAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType) {
        return true; // Permission granted with no checking. 
    }

    public void checkAcl(AclContainer aclContainer, AccessType accessType) throws AccessControlException {
    }

    public boolean testAcl(AclContainer aclContainer, AccessType accessType) {
        return true; // Permission granted with no checking. 
    }

    public void checkAcl(User user, AclContainer aclContainer, AccessType accessType) throws AccessControlException {
    }

    public boolean testAcl(User user, AclContainer aclContainer, AccessType accessType) {
        return true; // Permission granted with no checking. 
    }

	public void checkAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType,  boolean includeParentAcl) throws AccessControlException {

	}

	public boolean testAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeParentAcl) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeParentAcl) throws AccessControlException {
	}

	public boolean testAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeParentAcl) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(AclContainer aclContainer, AccessType accessType, boolean includeParentAcl) throws AccessControlException {		// TODO Auto-generated method stub
		
	}

	public boolean testAcl(AclContainer aclContainer, AccessType accessType, boolean includeParentAcl) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeParentAcl) throws AccessControlException {

	}

	public boolean testAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeParentAcl) {
        return true; // Permission granted with no checking. 

	}
    public void checkAcl(User user, AclControlled aclControlledObj, Set memberIds ) throws AccessControlException {
 
    }
    public boolean testAcl(User user, AclControlled aclControlledObj, Set memberIds) {
    	return true;
    }
    
	@Override
    public boolean testRightGrantedBySharing(User user, WorkArea workArea, WorkAreaOperation workAreaOperation) {
		return false; // Assume there's no sharing
	}
}
