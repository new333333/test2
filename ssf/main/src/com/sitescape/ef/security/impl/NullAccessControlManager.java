package com.sitescape.ef.security.impl;

import java.util.List;
import java.util.ArrayList;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.dao.util.FilterControls;
/**
 *
 * @author Jong Kim
 */
public class NullAccessControlManager implements AccessControlManager {
	private static List groups;
	private Boolean lock = new Boolean(true);
	
	public List getWorkAreaAccessControl(WorkArea workArea, WorkAreaOperation workAreaOperation) {
		if (groups == null) {
			synchronized (lock) {
				if (groups == null) {
					CoreDao coreDao = (CoreDao)SpringContextUtil.getBean("coreDao");
					List result = coreDao.loadGroups(new FilterControls(), RequestContextHolder.getRequestContext().getZoneName());
					groups = new ArrayList();
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

	public void checkAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeCreator, boolean includeParentAcl) throws AccessControlException {

	}

	public boolean testAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeCreator, boolean includeParentAcl) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeCreator, boolean includeParentAcl) throws AccessControlException {
	}

	public boolean testAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeCreator, boolean includeParentAcl) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(AclContainer aclContainer, AccessType accessType, boolean includeCreator, boolean includeParentAcl) throws AccessControlException {		// TODO Auto-generated method stub
		
	}

	public boolean testAcl(AclContainer aclContainer, AccessType accessType, boolean includeCreator, boolean includeParentAcl) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeCreator, boolean includeParentAcl) throws AccessControlException {

	}

	public boolean testAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeCreator, boolean includeParentAcl) {
        return true; // Permission granted with no checking. 

	}
}
