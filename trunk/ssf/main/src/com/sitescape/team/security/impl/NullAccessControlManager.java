package com.sitescape.team.security.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.User;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.acl.AccessType;
import com.sitescape.team.security.acl.AclAccessControlException;
import com.sitescape.team.security.acl.AclContainer;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.SpringContextUtil;
/**
 *
 * @author Jong Kim
 */
public class NullAccessControlManager implements AccessControlManager {
	private static Set groups;
	private Boolean lock = new Boolean(true);
	
	public Set getWorkAreaAccessControl(WorkArea workArea, WorkAreaOperation workAreaOperation) {
		if (groups == null) {
			synchronized (lock) {
				if (groups == null) {
					ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
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
}
