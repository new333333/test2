package com.sitescape.ef.security.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclAccessControlException;
import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.FunctionManager;
import com.sitescape.ef.security.function.OperationAccessControlException;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;
import com.sitescape.ef.security.function.WorkAreaFunctionMembershipManager;
import com.sitescape.ef.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public class AccessControlManagerImpl implements AccessControlManager {
    
    private FunctionManager functionManager;
    private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
    private ProfileDao profileDao;
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
    public Set getWorkAreaAccessControl(WorkArea workArea, WorkAreaOperation workAreaOperation) {
        if(workArea.isFunctionMembershipInherited()) {
            WorkArea parentWorkArea = workArea.getParentWorkArea();
            if(parentWorkArea == null)
                throw new InternalException("Cannot inherit function membership when it has no parent");
            else
                return getWorkAreaAccessControl(parentWorkArea, workAreaOperation);
        }
        else {
	        Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
	        //Get list of functions that allow the operation
	        List functions = getFunctionManager().findFunctions(zoneId, workAreaOperation);
	        //get all function memberships for this workarea
	        List memberships = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(zoneId, workArea);
	        //build list of users by merging  
	        Set result = new HashSet();
	        for (int i=0; i<memberships.size(); ++i) {
	        	WorkAreaFunctionMembership m = (WorkAreaFunctionMembership)memberships.get(i);
	        	for (int j=0; j<functions.size(); ++j) {
	        		Function f = (Function)functions.get(j);
	        		if (f.getId().equals(m.getFunctionId())) {
	        			result.addAll(m.getMemberIds());
	        			break;
	        		}
	        	}
	        }
	        //replaces reserved ownerId with workArea owner
	        if (result.remove(ObjectKeys.OWNER_USER_ID)) result.add(workArea.getOwnerId());
	        return result;
	        
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
        	        workArea, workArea.getOwnerId(), workAreaOperation);
    }
	
	public boolean testOperation(User user,
			WorkArea workArea, WorkAreaOperation workAreaOperation) {
		return testOperation(user, workArea, workArea.getOwnerId(), workAreaOperation);
		
	}
	//pass the original ownerId in.  Recursive calls need the original
	private boolean testOperation(User user, WorkArea workArea, Long ownerId, WorkAreaOperation workAreaOperation) {

		if (ObjectKeys.SUPER_USER_ID.equals(user.getInternalId())) return true;
		if (workArea.isFunctionMembershipInherited()) {
			WorkArea parentWorkArea = workArea.getParentWorkArea();
			if (parentWorkArea == null)
				throw new InternalException(
						"Cannot inherit function membership when it has no parent");
			else
				// use the original workArea owner
				return testOperation(user, parentWorkArea, ownerId, workAreaOperation);
		} else {
			Set membersToLookup = getProfileDao().getPrincipalIds(user);
			//if current user is the workArea owner, add special Id to is membership
			if (user.getId().equals(ownerId)) membersToLookup.add(ObjectKeys.OWNER_USER_ID);
			return getWorkAreaFunctionMembershipManager()
					.checkWorkAreaFunctionMembership(user.getParentBinder().getParentBinder().getId(),
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
        if (!testOperation(user, workArea, workArea.getOwnerId(), workAreaOperation))
        	throw new OperationAccessControlException(user.getName(), 
        			workAreaOperation.toString(), workArea.getWorkAreaId());
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
        if (ObjectKeys.SUPER_USER_ID.equals(user.getInternalId())) return true;
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
           	Set memberIds = aclControlledObj.getAclSet().getMemberIds(accessType);
           	return intersectedSets(principalIds, memberIds);
        }        
    }
    public void checkAcl(User user, AclControlled aclControlledObj, Set memberIds)
    	throws AccessControlException {
    	if (!testAcl(user, aclControlledObj, memberIds))
    		throw new AclAccessControlException(user.getName(), ""); 

    }
    public boolean testAcl(User user, AclControlled aclControlledObj, Set memberIds) {
        if (ObjectKeys.SUPER_USER_ID.equals(user.getInternalId())) return true;
       	Set principalIds = getProfileDao().getPrincipalIds(user);
       	//if owner, add special id
        if (user.getId().equals(aclControlledObj.getOwnerId()))  principalIds.add(ObjectKeys.OWNER_USER_ID);
     	return intersectedSets(principalIds, memberIds);      	
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
    
    private boolean intersectedSets(Set set1, Set set2) {
        for(Iterator i = set1.iterator(); i.hasNext();) {
            if(set2.contains(i.next()))
                return true;
        }
        return false;
    }
}
