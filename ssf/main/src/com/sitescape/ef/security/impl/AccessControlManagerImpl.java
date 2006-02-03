package com.sitescape.ef.security.impl;

import java.util.Iterator;
import java.util.Set;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclAccessControlException;
import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.FunctionManager;
import com.sitescape.ef.security.function.OperationAccessControlException;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaFunctionMembershipManager;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;

/**
 *
 * @author Jong Kim
 */
public class AccessControlManagerImpl implements AccessControlManager {
    
    private FunctionManager functionManager;
    private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
    
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
        	        workArea, workAreaOperation);
    }
    
	public boolean testOperation(Long additionalPrincipalId, WorkArea workArea, 
			WorkAreaOperation workAreaOperation) throws AccessControlException {
        return testOperation
    	(RequestContextHolder.getRequestContext().getUser(), additionalPrincipalId, 
    	        workArea, workAreaOperation);		
	}

	public boolean testOperation(User user, WorkArea workArea,
			WorkAreaOperation workAreaOperation) throws AccessControlException {
		return testOperation(user, null, workArea, workAreaOperation);
	}
	
	public boolean testOperation(User user, Long additionalPrincipalId, 
			WorkArea workArea, WorkAreaOperation workAreaOperation) 
		throws AccessControlException {
		if (workArea.isFunctionMembershipInherited()) {
			WorkArea parentWorkArea = workArea.getParentWorkArea();
			if (parentWorkArea == null)
				throw new InternalException(
						"Cannot inherit function membership when it has no parent");
			else
				return testOperation(user, parentWorkArea, workAreaOperation);
		} else {
			Set membersToLookup = user.computePrincipalIds();
			
			if(additionalPrincipalId != null)
				membersToLookup.add(additionalPrincipalId);

			return getWorkAreaFunctionMembershipManager()
					.checkWorkAreaFunctionMembership(user.getZoneName(),
							workArea, workAreaOperation, membersToLookup);
		}

	}
	
    public void checkOperation(WorkArea workArea, 
            WorkAreaOperation workAreaOperation) throws AccessControlException {
        checkOperation(RequestContextHolder.getRequestContext().getUser(),
                workArea, workAreaOperation);
    }
    
	public void checkOperation(Long additionalPrincipalId, WorkArea workArea, 
			WorkAreaOperation workAreaOperation) throws AccessControlException {
        checkOperation(RequestContextHolder.getRequestContext().getUser(),
        		additionalPrincipalId, workArea, workAreaOperation);		
	}

	public void checkOperation(User user, WorkArea workArea, 
			WorkAreaOperation workAreaOperation) 
    	throws AccessControlException {
        if(!testOperation(user, workArea, workAreaOperation))
        	throw new OperationAccessControlException(user.getName(), 
        			workAreaOperation.toString(), workArea.getWorkAreaId());
    }
    
	public void checkOperation(User user, Long additionalPrincipalId, 
			WorkArea workArea, WorkAreaOperation workAreaOperation) 
		throws AccessControlException {
        if(!testOperation(user, additionalPrincipalId, workArea, workAreaOperation))
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
        if(!testAcl(user, parent, aclControlledObj, accessType))     	
            throw new AclAccessControlException(user.getName(), accessType.toString()); 
    }
    
    public boolean testAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType) {
    	return testAcl(user, parent, aclControlledObj, accessType, false, false);        
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
    		boolean includeCreator, boolean includeParentAcl) throws AccessControlException {
        checkAcl(RequestContextHolder.getRequestContext().getUser(), parent,
        	        aclControlledObj, accessType, includeCreator, includeParentAcl);
    }

    public boolean testAcl(AclContainer parent, AclControlled aclControlledObj, AccessType accessType,
    		boolean includeCreator, boolean includeParentAcl) {
        return testAcl(RequestContextHolder.getRequestContext().getUser(), parent,
                aclControlledObj, accessType, includeCreator, includeParentAcl);
    }
    
    public void checkAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType,
    		boolean includeCreator, boolean includeParentAcl) throws AccessControlException {
        if(!testAcl(user, parent, aclControlledObj, accessType, includeCreator, includeParentAcl))     	
            throw new AclAccessControlException(user.getName(), accessType.toString()); 
    }
    
    public boolean testAcl(User user, AclContainer parent, AclControlled aclControlledObj, AccessType accessType,
    		boolean includeCreator, boolean includeParentAcl) {
        if(aclControlledObj.getInheritAclFromParent()) {
            // This object inherits ACLs from the parent for all access types. 
        	// In this case, we ignore includeCreator and includeParentAcl
        	// arguments, and simply perform access control against the parent 
        	// object. 
            return testAcl(user, parent.getParentAclContainer(), parent, accessType, includeCreator, includeParentAcl);
        }
        else {
            // This object does not inherit ACLs from the parent. It is expected
            // that this object has its own set(s) of ACLs associated with it.

            if(includeCreator && user.getId().equals(aclControlledObj.getCreatorId())) {
            	// The application desires to grant the creator of the acl-controlled
            	// object an access (of the specified type) to the object, AND the 
            	// specified user happens to be the creator. Grant it. 
            	return true;
            }
            else {
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
            	Set principalIds = user.computePrincipalIds();
            	Set memberIds = aclControlledObj.getAclSet().getMemberIds(accessType);
            
            	return intersectedSets(principalIds, memberIds);
            }
        }        
    }
    
    public void checkAcl(AclContainer aclContainer, AccessType accessType, boolean includeCreator, boolean includeParentAcl) throws AccessControlException {
        checkAcl(aclContainer.getParentAclContainer(), aclContainer, accessType, includeCreator, includeParentAcl);
    }
    public boolean testAcl(AclContainer aclContainer, AccessType accessType, boolean includeCreator, boolean includeParentAcl) {
        return testAcl(aclContainer.getParentAclContainer(), aclContainer, accessType, includeCreator, includeParentAcl);
    }
    public void checkAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeCreator, boolean includeParentAcl) throws AccessControlException {
        checkAcl(user, aclContainer.getParentAclContainer(), aclContainer, accessType, includeCreator, includeParentAcl);
    }
    public boolean testAcl(User user, AclContainer aclContainer, AccessType accessType, boolean includeCreator, boolean includeParentAcl) {
        return testAcl(user, aclContainer.getParentAclContainer(), aclContainer, accessType, includeCreator, includeParentAcl);
    }    
    
    private boolean intersectedSets(Set set1, Set set2) {
        for(Iterator i = set1.iterator(); i.hasNext();) {
            if(set2.contains(i.next()))
                return true;
        }
        return false;
    }
}
