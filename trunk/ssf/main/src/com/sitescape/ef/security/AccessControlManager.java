package com.sitescape.ef.security;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public interface AccessControlManager {

    /**
     * Same as {@link #checkOperation(WorkArea, WorkAreaOperation)} except 
     * that this returns <code>boolean</code> flag rather than throwing an 
     * exception.
     * 
     * @param workArea
     * @param workAreaOperation
     * @return
     */
    public boolean testOperation(WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException;
    
    /**
     * Same as {@link #checkOperation(Long, WorkArea, WorkAreaOperation)} except 
     * that this returns <code>boolean</code> flag rather than throwing an 
     * exception. 
     * 
     * @param additionalPrincipalId
     * @param workArea
     * @param workAreaOperation
     * @return
     * @throws AccessControlException
     */
    public boolean testOperation(Long additionalPrincipalId, WorkArea workArea,
    		WorkAreaOperation workAreaOperation) throws AccessControlException;
    
    /**
     * Same as {@link #checkOperation(User, WorkArea, WorkAreaOperation)} except
     * that this returns <code>boolean</code> flag rather than throwing an 
     * exception. 
     * 
     * @param user
     * @param workArea
     * @param workAreaOperation
     * @return
     */
    public boolean testOperation(User user, WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException;

    /**
     * Same as {@link #checkOperation(User, Long, WorkArea, WorkAreaOperation)} 
     * except that this returns <code>boolean</code> flag rather than throwing 
     * an exception. 
     * 
     * @param user
     * @param additionalPrincipalId
     * @param workArea
     * @param workAreaOperation
     * @return
     * @throws AccessControlException
     */
    public boolean testOperation(User user, Long additionalPrincipalId, 
    		WorkArea workArea, WorkAreaOperation workAreaOperation)
    	throws AccessControlException;
    
    /**
     * Check if the user associated with the current request context has the 
     * privilege to run the operation against the work area. 
     * 
     * @param workArea
     * @param workAreaOperation
     * @throws AccessControlException
     */
    public void checkOperation(WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException;
    
    /**
     * Check if the user associated with the current request context or the
     * principal identified by the specified id has the privilege to run the
     * operation against the work area. 
     * 
     * @param additionalPrincipalId
     * @param workArea
     * @param workAreaOperation
     * @throws AccessControlException
     */
    public void checkOperation(Long additionalPrincipalId, WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException;
    
    /**
     * Check if the specified user has the privilege to run the operation
     * against the work area. 
     * <p>
     * Use this method if one of the following conditions is true.
     * <p>
     * 1) There is no <code>RequestContext</code> set up for the calling thread.<br>
     * 2) The user associated with the current request context is not the same
     * as the user against which this access check is being performed. 
     * 
     * @param user
     * @param workArea
     * @param workAreaOperation
     * @throws AccessControlException
     */
    public void checkOperation(User user, WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException;
    
    /**
     * Check if the specified user or the principal identified by the specified
     * id has the privilege to run the operation against the work area. 
     * 
     * @param user
     * @param additionalPrincipalId
     * @param workArea
     * @param workAreaOperation
     * @throws AccessControlException
     */
    public void checkOperation(User user, Long additionalPrincipalId, 
    		WorkArea workArea, WorkAreaOperation workAreaOperation) 
    	throws AccessControlException;
    
    /**
     * Check if the user associated with the current request context has the
     * specified type of access to the object. 
     * 
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @throws AccessControlException
     */
    public void checkAcl(AclContainer parent, AclControlled aclControlledObj,
            AccessType accessType) throws AccessControlException;
    
    /**
     * Same as <code>checkObjectAccessControl</code> except that this returns
     * <code>boolean</code> flag rather than throwing an exception.
     * 
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @return
     */
    public boolean testAcl(AclContainer parent, AclControlled aclControlledObj,
            AccessType accessType);
    
    /**
     * Check if the specified user has the specified type of access to the object.
     * <p>
     * Use this method if one of the following conditions is true.
     * <p>
     * 1) There is no <code>RequestContext</code> set up for the calling thread.<br>
     * 2) The user associated with the current request context is not the same
     * as the user against which this access check is being performed. 
     * 
     * @param user
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @throws AccessControlException
     */
    public void checkAcl(User user, AclContainer parent, 
            AclControlled aclControlledObj, AccessType accessType) 
    	throws AccessControlException;
    
    /**
     * Same as <code>checkObjectAccessControl</code> except that this returns
     * <code>boolean</code> flag rather than throwing an exception.
     * 
     * @param user
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @return
     */
    public boolean testAcl(User user, AclContainer parent, 
            AclControlled aclControlledObj, AccessType accessType);
    
    public void checkAcl(AclContainer aclContainer, AccessType accessType) 
    	throws AccessControlException;
    
    public boolean testAcl(AclContainer aclContainer, AccessType accessType);
    
    public void checkAcl(User user, AclContainer aclContainer, 
            AccessType accessType) throws AccessControlException;
    
    public boolean testAcl(User user, AclContainer aclContainer,
            AccessType accessType);
    
    /**
     * Check if the user associated with the current request context has the
     * specified type of access to the object. 
     * 
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @throws AccessControlException
     */
    public void checkAcl(AclContainer parent, AclControlled aclControlledObj,
            AccessType accessType, boolean includeEntryCreator, 
            boolean includeForumDefault) throws AccessControlException;
    
    /**
     * Same as <code>checkObjectAccessControl</code> except that this returns
     * <code>boolean</code> flag rather than throwing an exception.
     * 
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @return
     */
    public boolean testAcl(AclContainer parent, AclControlled aclControlledObj,
            AccessType accessType, boolean includeEntryCreator, 
            boolean includeForumDefault);
    
    /**
     * Check if the specified user has the specified type of access to the object.
     * <p>
     * Use this method if one of the following conditions is true.
     * <p>
     * 1) There is no <code>RequestContext</code> set up for the calling thread.<br>
     * 2) The user associated with the current request context is not the same
     * as the user against which this access check is being performed. 
     * 
     * @param user
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @throws AccessControlException
     */
    public void checkAcl(User user, AclContainer parent, 
            AclControlled aclControlledObj, AccessType accessType,
            boolean includeEntryCreator, boolean includeForumDefault) 
    	throws AccessControlException;
    
    /**
     * Same as <code>checkObjectAccessControl</code> except that this returns
     * <code>boolean</code> flag rather than throwing an exception.
     * 
     * @param user
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @return
     */
    public boolean testAcl(User user, AclContainer parent, 
            AclControlled aclControlledObj, AccessType accessType,
            boolean includeEntryCreator, boolean includeForumDefault);
    
    public void checkAcl(AclContainer aclContainer, AccessType accessType,
    		boolean includeEntryCreator, boolean includeForumDefault) 
    	throws AccessControlException;
    
    public boolean testAcl(AclContainer aclContainer, AccessType accessType,
    		boolean includeEntryCreator, boolean includeForumDefault);
    
    public void checkAcl(User user, AclContainer aclContainer, 
            AccessType accessType, boolean includeEntryCreator, 
            boolean includeForumDefault) throws AccessControlException;
    
    public boolean testAcl(User user, AclContainer aclContainer,
            AccessType accessType, boolean includeEntryCreator, 
            boolean includeForumDefault);

}
