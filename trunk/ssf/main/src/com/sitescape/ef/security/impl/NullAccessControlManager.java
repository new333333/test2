package com.sitescape.ef.security.impl;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public class NullAccessControlManager implements AccessControlManager {

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

	public boolean testOperation(Long additionalPrincipalId, WorkArea workArea, WorkAreaOperation workAreaOperation) throws AccessControlException {
        return true; // Permission granted with no checking. 
	}

	public boolean testOperation(User user, Long additionalPrincipalId, WorkArea workArea, WorkAreaOperation workAreaOperation) throws AccessControlException {
        return true; // Permission granted with no checking. 
	}

	public void checkOperation(Long additionalPrincipalId, WorkArea workArea, WorkAreaOperation workAreaOperation) throws AccessControlException {
	}

	public void checkOperation(User user, Long additionalPrincipalId, WorkArea workArea, WorkAreaOperation workAreaOperation) throws AccessControlException {
	}

	public void checkAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeEntryCreator, boolean includeForumDefault) throws AccessControlException {

	}

	public boolean testAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeEntryCreator, boolean includeForumDefault) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeEntryCreator, boolean includeForumDefault) throws AccessControlException {
	}

	public boolean testAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType, boolean includeEntryCreator, boolean includeForumDefault) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(AclContainer aclContainer, AccessType accessType, boolean includeEntryCreator, boolean includeForumDefault) throws AccessControlException {		// TODO Auto-generated method stub
		
	}

	public boolean testAcl(AclContainer aclContainer, AccessType accessType, boolean includeEntryCreator, boolean includeForumDefault) {
        return true; // Permission granted with no checking. 

	}

	public void checkAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeEntryCreator, boolean includeForumDefault) throws AccessControlException {

	}

	public boolean testAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeEntryCreator, boolean includeForumDefault) {
        return true; // Permission granted with no checking. 

	}
}
