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
package com.sitescape.team.security.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sitescape.team.InternalException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.User;
import com.sitescape.team.license.LicenseManager;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.acl.AccessType;
import com.sitescape.team.security.acl.AclAccessControlException;
import com.sitescape.team.security.acl.AclContainer;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.security.function.FunctionManager;
import com.sitescape.team.security.function.OperationAccessControlException;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaFunctionMembershipManager;
import com.sitescape.team.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public class AccessControlManagerImpl implements AccessControlManager {
    
    private FunctionManager functionManager;
    private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
    private ProfileDao profileDao;
    private LicenseManager licenseManager;
    public FunctionManager getFunctionManager() {
        return functionManager;
    }
    public void setFunctionManager(FunctionManager functionManager) {
        this.functionManager = functionManager;
    }
    public WorkAreaFunctionMembershipManager getWorkAreaFunctionMembershipManager() {
        return workAreaFunctionMembershipManager;
    }
    public void setWorkAreaFunctionMembershipManager(
            WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager) {
        this.workAreaFunctionMembershipManager = workAreaFunctionMembershipManager;
    }
	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}
	public void setLicenseManager(LicenseManager licenseManager) {
		this.licenseManager = licenseManager;
	}
	protected LicenseManager getLicenseManager() {
		return licenseManager;
	}
    public Set getWorkAreaAccessControl(WorkArea workArea, WorkAreaOperation workAreaOperation) {
         if(workArea.isFunctionMembershipInherited()) {
            WorkArea parentWorkArea = workArea.getParentWorkArea();
            if(parentWorkArea == null)
                return new HashSet();  //possible for templates
            else
                return getWorkAreaAccessControl(parentWorkArea, workAreaOperation);
        }
        else {
	        Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
        	List<WorkAreaFunctionMembership>wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, workAreaOperation);
          	Set ids = new HashSet();
            for (WorkAreaFunctionMembership wfm:wfms) {
            	ids.addAll(wfm.getMemberIds());
        	}
	        return ids;
	        
        }    	
    }
    /*
    public void checkWorkAreaAccessControl(WorkArea workArea, 
            WorkAreaOperation workAreaOperation) throws AccessControlException {
        if(workArea.isFunctionMembershipInherited()) {
            WorkArea parentWorkArea = workArea.getParentWorkArea();
            if(parentWorkArea == null)
                throw new InternalException("Cannot inherit function membership when it has no parent");
            else
                checkWorkAreaAccessControl(parentWorkArea, workAreaOperation);
        }
        else {
	        Long zoneName = RequestContextHolder.getRequestContext().getZoneName();
	        
	        Set membersToLookup = RequestContextHolder.getRequestContext().getPrincipalIds();
	        
	        List functions = getFunctionManager().getFunctions(zoneName, workAreaOperation);
	        
	        boolean match = getWorkAreaFunctionMembershipManager().checkWorkAreaFunctionMembership(zoneName, workArea, membersToLookup, functions);
	        
	        if(!match)
	            throw new AccessControlException("The user is not authorized to perform the operation '" + 
	                    workAreaOperation.toString() + "' to the work area '" + workArea.getId() + "'");
        }
    }*/
    
    public boolean testOperation(WorkArea workArea, WorkAreaOperation workAreaOperation) 
    	throws AccessControlException {
        return testOperation
        	(RequestContextHolder.getRequestContext().getUser(), 
        	        workArea, workArea, workAreaOperation);
    }
	
	public boolean testOperation(User user,
			WorkArea workArea, WorkAreaOperation workAreaOperation) {
		return testOperation(user, workArea, workArea, workAreaOperation);
		
	}
	//pass the original ownerId in.  Recursive calls need the original
	private boolean testOperation(User user, WorkArea workAreaStart, WorkArea workArea, WorkAreaOperation workAreaOperation) {

		if (user.isSuper()) return true;
		if (!workAreaOperation.equals(WorkAreaOperation.READ_ENTRIES) && !getLicenseManager().validLicense())return false;
		if (workArea.isFunctionMembershipInherited()) {
			WorkArea parentWorkArea = workArea.getParentWorkArea();
			if (parentWorkArea == null)
				throw new InternalException(
						"Cannot inherit function membership when it has no parent");
			else
				// use the original workArea owner
				return testOperation(user, workAreaStart, parentWorkArea, workAreaOperation);
		} else {
			Set membersToLookup = getProfileDao().getPrincipalIds(user);
			//if current user is the workArea owner, add special Id to is membership
			if (user.getId().equals(workAreaStart.getOwnerId())) membersToLookup.add(ObjectKeys.OWNER_USER_ID);
			if (!Collections.disjoint(workAreaStart.getTeamMemberIds(), membersToLookup)) membersToLookup.add(ObjectKeys.TEAM_MEMBER_ID);
			return getWorkAreaFunctionMembershipManager()
					.checkWorkAreaFunctionMembership(user.getZoneId(),
							workArea, workAreaOperation, membersToLookup);
		}

	}
	
    public void checkOperation(WorkArea workArea, 
            WorkAreaOperation workAreaOperation) throws AccessControlException {
        checkOperation(RequestContextHolder.getRequestContext().getUser(),
                workArea, workAreaOperation);
    }

	public void checkOperation(User user, WorkArea workArea, 
			WorkAreaOperation workAreaOperation) 
    	throws AccessControlException {
        if (!testOperation(user, workArea, workArea, workAreaOperation))
        	throw new OperationAccessControlException(user.getName(), 
        			workAreaOperation.toString(), workArea.toString());
    }

	public void checkAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType) throws AccessControlException {
        checkAcl
        	(RequestContextHolder.getRequestContext().getUser(), parent,
        	        aclControlledObj, accessType);
    }

    public boolean testAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType) {
        return testAcl(RequestContextHolder.getRequestContext().getUser(), parent,
                aclControlledObj, accessType);
    }
    
    public void checkAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType) throws AccessControlException {
        if (!testAcl(user, parent, aclControlledObj, accessType))
            throw new AclAccessControlException(user.getName(), accessType.toString()); 
    }
    
    public boolean testAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType) {
    	return testAcl(user, parent, aclControlledObj, accessType, false);        
    }
    
    public void checkAcl(AclContainer aclContainer, AccessType accessType) throws AccessControlException {
        checkAcl(aclContainer.getParentAclContainer(), aclContainer, accessType);
    }
    public boolean testAcl(AclContainer aclContainer, AccessType accessType) {
        return testAcl(aclContainer.getParentAclContainer(), aclContainer, accessType);
    }
    public void checkAcl(User user, AclContainer aclContainer, AccessType accessType) throws AccessControlException {
        checkAcl(user, aclContainer.getParentAclContainer(), aclContainer, accessType);
    }
    public boolean testAcl(User user, AclContainer aclContainer, AccessType accessType) {
        return testAcl(user, aclContainer.getParentAclContainer(), aclContainer, accessType);
    }    

    public void checkAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType, 
    		boolean includeParentAcl) throws AccessControlException {
        checkAcl(RequestContextHolder.getRequestContext().getUser(), parent,
        	        aclControlledObj, accessType, includeParentAcl);
    }

    public boolean testAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType,
    		boolean includeParentAcl) {
        return testAcl(RequestContextHolder.getRequestContext().getUser(), parent,
                aclControlledObj, accessType, includeParentAcl);
    }
    
    public void checkAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType,
    		boolean includeParentAcl) throws AccessControlException {
        if(!testAcl(user, parent, aclControlledObj, accessType, includeParentAcl))     	
            throw new AclAccessControlException(user.getName(), accessType.toString()); 
    }
    
    public boolean testAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType,
    		boolean includeParentAcl) {
        if (user.isSuper()) return true;
         if(aclControlledObj.getInheritAclFromParent()) {
            // This object inherits ACLs from the parent for all access types. 
        	// In this case, we ignore includeCreator and includeParentAcl
        	// arguments, and simply perform access control against the parent 
        	// object. 
            return testAcl(user, parent.getParentAclContainer(), parent, accessType, false);
        }  else {
            // This object does not inherit ACLs from the parent. It is expected
            // that this object has its own set(s) of ACLs associated with it.
       		if(includeParentAcl) {
           		// The acl set of the specified access type for the object must
           		// include the default acl set associated with its parent. 
           		// Let's check against the parent first. 
           		// Note: We must NOT pass through the includeCreator and
           		// includeParentAcl arguments to the acl checking call against
           		// the parent, because they are NOT meant to be applied recursively.
           		// 
           		if(testAcl(user, parent.getParentAclContainer(), parent, accessType))
           			return true;
           	}
            	
           	// We have to check against the explicit set associated with this object.
           	Set principalIds = getProfileDao().getPrincipalIds(user);
            if (user.getId().equals(aclControlledObj.getOwnerId()))  principalIds.add(ObjectKeys.OWNER_USER_ID);
            //don't know if team members makes sense
           	Set memberIds = aclControlledObj.getAclSet().getMemberIds(accessType);
           	return !Collections.disjoint(principalIds, memberIds);
        }        
    }
    public void checkAcl(AclContainer aclContainer, AccessType accessType, boolean includeParentAcl) throws AccessControlException {
        checkAcl(aclContainer.getParentAclContainer(), aclContainer, accessType, includeParentAcl);
    }
    public boolean testAcl(AclContainer aclContainer, AccessType accessType, boolean includeParentAcl) {
        return testAcl(aclContainer.getParentAclContainer(), aclContainer, accessType, includeParentAcl);
    }
    public void checkAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeParentAcl) throws AccessControlException {
        checkAcl(user, aclContainer.getParentAclContainer(), aclContainer, accessType, includeParentAcl);
    }
    public boolean testAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeParentAcl) {
        return testAcl(user, aclContainer.getParentAclContainer(), aclContainer, accessType, includeParentAcl);
    }    
    

}
