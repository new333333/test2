/*
 * $Header$
 * $Revision: 208313 $
 * $Date: 2004-12-15 11:30:19 -0500 (Wed, 15 Dec 2004) $
 *
 * ====================================================================
 *
 * Copyright 1999-2002 The Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.slide.lock;

import java.util.Date;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import org.apache.slide.common.Namespace;
import org.apache.slide.common.NamespaceConfig;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideException;
import org.apache.slide.common.SlideToken;
import org.apache.slide.common.Uri;
import org.apache.slide.event.EventDispatcher;
import org.apache.slide.event.LockEvent;
import org.apache.slide.event.VetoException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.Security;
import org.apache.slide.structure.ActionNode;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.SubjectNode;
import org.apache.slide.util.Configuration;

/**
 * Lock helper class.
 *
 * @version $Revision: 208313 $
 */
public class LockImpl implements Lock {
    
    // ----------------------------------------------------------- Constructors
    
    
    /**
     * Constructor.
     *
     * @param namespace Associated namespace
     * @param namespaceConfig Namespace configuration
     * @param securityHelper Security helper
     */
    public LockImpl(Namespace namespace, NamespaceConfig namespaceConfig,
                    Security securityHelper) {
        this.namespace = namespace;
        this.namespaceConfig = namespaceConfig;
        this.securityHelper = securityHelper;
    }
    
    
    // ----------------------------------------------------- Instance Variables
    
    
    /**
     * Namespace.
     */
    private Namespace namespace;
    
    
    /**
     * Namespace configuration.
     */
    private NamespaceConfig namespaceConfig;
    
    
    /**
     * Security helper.
     */
    private Security securityHelper;
    
    
    // ----------------------------------------------------------- Lock Methods
        
    /**
     * Put a lock on a subject.
     * 
     * @param slideToken The token to access slide.
     * @param lockToken Object containing all the lock information
     * @exception ServiceAccessException Low level service access exception
     * @exception ObjectNotFoundException One of the objects referenced 
     * in the lock token were not found
     * @exception ObjectIsAlreadyLockedException Object is already locked 
     * with an incompatible lock token
     * @exception AccessDeniedException Insufficient credentials to allow 
     * object locking
     */
    public void lock(SlideToken slideToken, NodeLock lockToken)
        throws ServiceAccessException, ObjectIsAlreadyLockedException,
        AccessDeniedException, ObjectNotFoundException, VetoException {
        
        ObjectIsAlreadyLockedException nestedException =
            new ObjectIsAlreadyLockedException(lockToken.getObjectUri());
        Uri objectUri = namespace.getUri(slideToken, lockToken.getObjectUri(), true);
        boolean canLock =
            !isLockedInternal(slideToken, lockToken, true, nestedException);
        
        // Then we try to lock the subject.
        // If the User doesn't have enough priviledges to accomplish this
        // action, we will get a SecurityException which will in turn be
        // thrown by this function.
        if (canLock) {
            ObjectNode lockedObject = objectUri.getStore()
                .retrieveObject(objectUri);
            securityHelper
                .checkCredentials(slideToken, lockedObject,
                                  namespaceConfig.getLockObjectAction());
            objectUri.getStore().putLock(objectUri, lockToken);
        } else {
            throw nestedException;
        }
        
        // Fire event
        if ( LockEvent.LOCK.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(LockEvent.LOCK, new LockEvent(this, slideToken, namespace, objectUri));
    }
    
    
    /**
     * Removes a lock.
     *
     * @param slideToken The token to access slide.
     * @param lockToken Object containing all the lock information
     *
     * @return <code>true</code> if the lock could be removed
     *
     * @exception ServiceAccessException Low level service access exception
     * @exception LockTokenNotFoundException Cannot find the Lock in the
     * Lock Store service
     */
    public boolean unlock(SlideToken slideToken, NodeLock lockToken)
        throws ServiceAccessException, LockTokenNotFoundException, VetoException {
        
        try {
            if (!checkLockOwner(slideToken, lockToken)) {
                return false;
            }
            
            // check the lockId
            if (slideToken.isEnforceLockTokens() && !checkLockToken(slideToken, lockToken)) {
                return false;
            }
        } catch (ObjectNotFoundException e) {
            return false;
        }
        
        // all checks successful, so try to actually remove the lock
        Uri lockedUri = namespace.getUri(slideToken, lockToken.getObjectUri(),
                                         true);
        lockedUri.getStore().removeLock(lockedUri, lockToken);

        // Fire event
        if ( LockEvent.UNLOCK.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(LockEvent.UNLOCK, new LockEvent(this, slideToken, namespace, lockedUri));

        return true;
    }
    
    
    /**
     * Removes a set of linked locks.
     *
     * @param slideToken Slide token
     * @param objectUri Uri of the locked object.
     * @param lockId The id of the locks, which will be removed.
     * @exception ServiceAccessException Low level service access exception
     * @exception LockTokenNotFoundException Cannot find the Lock in the
     * Lock Store service
     * @exception ObjectNotFoundException One of the objects referenced
     * in the lock token were not found
     */
    public void unlock(SlideToken slideToken, String objectUri,
                       String lockId)
        throws ServiceAccessException, LockTokenNotFoundException,
        ObjectNotFoundException, VetoException {
        
        Enumeration locksList = enumerateLocks(slideToken, objectUri, false);
        while (locksList.hasMoreElements()) {
            NodeLock currentLock = (NodeLock) locksList.nextElement();
            if (currentLock.getLockId().equals(lockId)) {
                if (slideToken.isEnforceLockTokens())
                    slideToken.addLockToken(lockId);
                unlock(slideToken, currentLock);
            }
        }
        
    }
    
    
    /**
     * Renew a lock.
     *
     * @param slideToken Slide token
     * @param lockToken Token containing the lock info.
     * @param newExpirationDate the desired expiration date
     * @exception ServiceAccessException Low level service access exception
     * @exception LockTokenNotFoundException Cannot find the Lock in
     * the Lock Store service
     */
    public void renew(SlideToken slideToken, NodeLock lockToken,
                      Date newExpirationDate)
        throws ServiceAccessException, LockTokenNotFoundException, VetoException {
        lockToken.setExpirationDate(newExpirationDate);
        Uri lockedUri = namespace.getUri(slideToken, lockToken.getObjectUri());
        lockedUri.getStore().renewLock(lockedUri, lockToken);

        // Fire event
        if ( LockEvent.RENEW.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(LockEvent.RENEW, new LockEvent(this, slideToken, namespace, lockedUri));
    }
    
    
    /**
     * Renew a set of linked locks.
     *
     * @param slideToken Slide token
     * @param objectUri Uri of the locked object
     * @param lockId Id of the locks, which will be renewed.
     * @param newExpirationDate The new expiration date of the locks
     * @exception ServiceAccessException Low level service access exception
     * @exception LockTokenNotFoundException Cannot find the Lock in the
     * Lock Store service
     * @exception ObjectNotFoundException One of the objects referenced
     * in the lock token were not found
     */
    public void renew(SlideToken slideToken, String objectUri,
                      String lockId, Date newExpirationDate)
        throws ServiceAccessException, LockTokenNotFoundException,
        ObjectNotFoundException, VetoException {
        
        Enumeration locksList = enumerateLocks(slideToken, objectUri, false);
        while (locksList.hasMoreElements()) {
            NodeLock currentLock = (NodeLock) locksList.nextElement();
            if (currentLock.getLockId().equals(lockId)) {
                renew(slideToken, currentLock, newExpirationDate);
            }
        }
        
    }
    
    
    /**
     * Kills locks.
     *
     * @param slideToken Slide token
     * @param subject Subject on which locks are to be removed
     * @exception ServiceAccessException Low level service access exception
     * @exception ObjectNotFoundException One of the objects referenced in
     * the lock token were not found
     * @exception LockTokenNotFoundException Cannot find the Lock in the
     * Lock Store service
     * @exception AccessDeniedException Insufficient credentials to allow
     * forced removal of locks
     */
    public void kill(SlideToken slideToken, SubjectNode subject)
        throws ServiceAccessException, AccessDeniedException,
        LockTokenNotFoundException, ObjectNotFoundException, VetoException {
        
        // We retrieve the enumeration of locks which have been put on the
        // subject.
        Uri subjectUri = namespace.getUri(slideToken, subject.getUri());
        Enumeration locks = subjectUri.getStore()
            .enumerateLocks(subjectUri);
        // Then, try to kill each individual lock.
        while (locks.hasMoreElements()) {
            securityHelper
                .checkCredentials(slideToken, subject,
                                  namespaceConfig.getKillLockAction());
            subjectUri.getStore()
                .killLock(subjectUri, (NodeLock) locks.nextElement());
        }
        
        // Fire event
        if ( LockEvent.KILL.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(LockEvent.KILL, new LockEvent(this, slideToken, namespace, subjectUri));
    }
    
    public Enumeration enumerateLocks(SlideToken slideToken, String objectUri)
        throws ServiceAccessException, ObjectNotFoundException,
        LockTokenNotFoundException {
        
        return enumerateLocks(slideToken, objectUri, true);
    }
    
    public Enumeration enumerateLocks(SlideToken slideToken, String objectUri,
                                      boolean inherited)
        throws ServiceAccessException, ObjectNotFoundException,
        LockTokenNotFoundException {
        
        // We retrieve the LockStore service from the namespace.
        Uri subjectUri = namespace.getUri(slideToken, objectUri);
        
        Enumeration scopes = null;
        if (inherited) {
            // traverse the namespace up to the root node, and add any locks
            // found in the process
            scopes = subjectUri.getScopes();
        } else {
            // only return the locks that explicitly and directly lock the
            // given subject
            Vector scopeVector = new Vector();
            scopeVector.add(subjectUri.toString());
            scopes = scopeVector.elements();
        }
        Vector locksVector = new Vector();
        while (scopes.hasMoreElements()) {
            String currentScope = (String) scopes.nextElement();
            Uri currentScopeUri =
                namespace.getUri(slideToken, currentScope);
            Enumeration currentLocks =
                currentScopeUri.getStore().enumerateLocks(currentScopeUri);
            while (currentLocks.hasMoreElements()) {
                NodeLock currentLockToken =
                    (NodeLock) currentLocks.nextElement();
                if (currentLockToken.hasExpired()) {
                    // FIXME: do cleanup of locks someplace else
                    try {
                        currentScopeUri.getStore()
                            .removeLock(currentScopeUri, currentLockToken);
                    }
                    catch (LockTokenNotFoundException ex) {
                        // ignore
                    }
                } else {
                    locksVector.addElement(currentLockToken);
                }
            }
        }
        return locksVector.elements();
    }
    
    
    /**
     * Utility function for lock checking. Mirrors Security.checkCredentials.
     *
     * @param token Slide token
     * @param object Object on which the action is performed
     * @param action Action to test
     * @exception ObjectLockedException Can't perform specified action
     * on object
     * @exception ServiceAccessException Low level service access exception
     * @exception ObjectNotFoundException One of the objects referenced
     * in the lock token were not found
     */
    public void checkLock(SlideToken token,
                          ObjectNode object, ActionNode action)
        throws ServiceAccessException, ObjectNotFoundException,
        ObjectLockedException {
        
        if (!token.isForceLock()) {
            return;
        }
        
        if (Configuration.useIntegratedLocking()) {
            
            Boolean locked = token.checkLockCache(object, action);
            if (locked != null) {
                if (locked.booleanValue()) {
                    throw new ObjectLockedException(object.getUri());
                }
                else {
                    return;
                }
            }
            
            Uri objectUri = namespace.getUri(token, object.getUri());
            ObjectNode realObject = objectUri.getStore()
                .retrieveObject(objectUri);
            try {
                checkLock(token, realObject, (SubjectNode)securityHelper.getPrincipal(token), action);
                token.cacheLock(object, action, false);
            }
            catch (ObjectLockedException e) {
                token.cacheLock(object, action, true);
                throw e;
            }
        }
    }
    
    
    /**
     * Check locking for a specific action and credential.
     *
     * @param slideToken Credetials token
     * @param subject Subject to test
     * @param user User to test
     * @param action Action to test
     * @exception ObjectLockedException Can't perform specified action
     * on object
     * @exception ServiceAccessException Low level service access exception
     * @exception ObjectNotFoundException One of the objects referenced
     * in the lock token were not found
     */
    public void checkLock(SlideToken slideToken,
                          ObjectNode subject, SubjectNode user,
                          ActionNode action)
        throws ServiceAccessException, ObjectNotFoundException,
        ObjectLockedException {
        
        // no check for default action (server intitialization)
        if (action.equals(ActionNode.DEFAULT)) {
            return;
        }
        
        if (Configuration.useIntegratedLocking()) {
            if (isLocked(slideToken, subject, user, action, false)) {
                throw new ObjectLockedException(subject.getUri());
            }
        }
    }
    
    
    /**
     * Tests if an element is locked.
     *
     * @param slideToken Slide token
     * @param subject Subject to test
     * @param user User to test
     * @param action Locked for action
     * @return boolean True if the subject is locked
     * @exception ServiceAccessException Low level service access exception
     * @exception ObjectNotFoundException One of the objects referenced
     * in the lock token were not found
     */
    public boolean isLocked(SlideToken slideToken,
                            ObjectNode subject, SubjectNode user,
                            ActionNode action, boolean tryToLock)
        throws ServiceAccessException, ObjectNotFoundException {
        NodeLock token = new NodeLock(subject, user, action,
                                      new Date(), false);
        return isLocked(slideToken, token, tryToLock);
    }
    
    
    /**
     * Tests if an element is locked.
     *
     * @param slideToken Slide token
     * @param subject Subject to test
     * @param user User to test
     * @param action Locked for action
     * @param inheritance Set to true if we want to check if any children
     * is locked with an incompatible lock
     * @param tryToLock True if the check is intended to check whether
     * or not we can put a new lock
     * @return boolean True if the subject is locked
     * @exception ServiceAccessException Low level service access exception
     * @exception ObjectNotFoundException One of the objects referenced
     * in the lock token were not found
     */
    public boolean isLocked(SlideToken slideToken,
                            ObjectNode subject, SubjectNode user,
                            ActionNode action, boolean inheritance,
                            boolean tryToLock)
        throws ServiceAccessException, ObjectNotFoundException {
        NodeLock token = new NodeLock(subject, user, action, new Date(),
                                      inheritance);
        return isLocked(slideToken, token, tryToLock);
    }
    
    
    /**
     * Tests if an element is locked.
     *
     * @param token Lock token to test
     * @return boolean True if locked
     * @exception ServiceAccessException Low level service access exception
     * @exception ObjectNotFoundException One of the objects referenced
     * in the lock token were not found
     */
    public boolean isLocked(SlideToken slideToken, NodeLock token,
                            boolean tryToLock)
        throws ServiceAccessException, ObjectNotFoundException {
        
        return isLockedInternal(slideToken, token, tryToLock, null);
        
    }
    
    /**
     * Clears the expired locks from the specified resource.
     *
     * @param    slideToken          a  SlideToken
     * @param    objectUri           a  String
     * @param    listener            an UnlockListener
     * @throws   SlideException
     */
    public void clearExpiredLocks( SlideToken slideToken, String objectUri, UnlockListener listener ) throws SlideException {
        
        Uri uri =
            namespace.getUri(slideToken, objectUri);
        Enumeration currentLocks =
            uri.getStore().enumerateLocks(uri);
        while (currentLocks.hasMoreElements()) {
            NodeLock currentLockToken =
                (NodeLock) currentLocks.nextElement();
            if (currentLockToken.hasExpired()) {
                try {
                    uri.getStore().removeLock(uri, currentLockToken);
                    if( listener != null )
                        listener.afterUnlock( objectUri );
                }
                catch (LockTokenNotFoundException ex) {
                    // ignore
                }
            }
        }
    }
    
    /**
     * Return true if a lock token for this lock has been given in the
     * credentials token.
     *
     * @param slideToken Current credentials token
     * @param token Lock token
     */
    public boolean checkLockToken(SlideToken slideToken, NodeLock token) {
        if (!slideToken.isEnforceLockTokens())
            return true;
        
        // if org.apache.slide.principalIdentifiedLocks is true the lock-owner
        // must not provide the lock token (i.e. it must not be present in the
        // slide token)
        if (Configuration.usePrincipalIdentifiedLocks()) {
            SubjectNode principalNode = null;
            SubjectNode ownerNode = SubjectNode.getSubjectNode(token.getSubjectUri());
            try {
                principalNode = (SubjectNode)securityHelper.getPrincipal(slideToken);
            }
            catch (SlideException e) {}
            
            if (ownerNode != null &&
                !ownerNode.equals(SubjectNode.UNAUTHENTICATED) &&
                ownerNode.equals(principalNode)) 
            {
                return true;
            }
            return (slideToken.checkLockToken(token.getLockId()));
        } else {
            return (slideToken.checkLockToken(token.getLockId()));
        }
    }
    
    /**
     * Returns true, if the principal from the credentials token is either the
     * lock-owner of has kill-lock privileges
     *
     * @param    slideToken          a  SlideToken
     * @param    token               a  NodeLock
     * @return   a boolean
     *
     * @throws   ObjectNotFoundException
     * @throws   ServiceAccessException
     */
    public boolean checkLockOwner(SlideToken slideToken, NodeLock token) throws ObjectNotFoundException, ServiceAccessException {
        boolean canUnlock = true;
        SubjectNode principal = (SubjectNode)securityHelper.getPrincipal(slideToken);
        SubjectNode lockOwner = SubjectNode.getSubjectNode(token.getSubjectUri());
        if (!securityHelper.matchPrincipal(slideToken, principal, lockOwner)) {
            // it is not the lock owner ... now check the kill-lock privilege
            try {
                securityHelper.checkCredentials(slideToken,
                                                SubjectNode.getSubjectNode(token.getObjectUri()),
                                                namespaceConfig.getKillLockAction());
            }
            catch (AccessDeniedException e) {
                canUnlock = false;
            }
        }
        return canUnlock;
    }
    
    // -------------------------------------------------------- Private Methods
    
    
    /**
     * Tests if an element is locked.
     *
     * @param token Lock token to test
     * @return boolean True if locked
     * @exception ServiceAccessException Low level service access exception
     * @exception ObjectNotFoundException One of the objects referenced
     * in the lock token were not found
     */
    private boolean isLockedInternal
        (SlideToken slideToken, NodeLock token,
         boolean tryToLock, ObjectIsAlreadyLockedException nestedException)
        throws ServiceAccessException, ObjectNotFoundException {
        
        Uri objectUri = namespace.getUri(slideToken, token.getObjectUri());
        ObjectNode initialObject = objectUri.getStore()
            .retrieveObject(objectUri);
        Enumeration scopes = objectUri.getScopes();
        
        // At the end of the test, this boolean's value is true if we can
        // actually put the lock on the desired subject.
        boolean isLocked = false;
        
        // We parse all of the scopes which encompass the subject we want
        // to lock.
        // First, we parse all the parents of the subject.
        
        // 9/20/06 JK - Since SSFS supports the concept of lock only on the
        // file folder entries (but not on binders), there is no point in 
        // checking lock for the parents. This way, we will waste at most
        // one lock check (if the uri represents a binder) but no more. 
        if(!isLocked && scopes.hasMoreElements()) {
            String currentScope = (String) scopes.nextElement();
            Uri currentScopeUri = namespace.getUri(slideToken, currentScope);
            Enumeration locks = currentScopeUri.getStore()
                .enumerateLocks(currentScopeUri);
            
            while (locks.hasMoreElements()) {
                NodeLock currentLockToken = (NodeLock) locks.nextElement();
                if (!isCompatible(slideToken, token, currentLockToken,
                                  tryToLock)) {
                    isLocked = true;
                    if (nestedException != null) {
                        nestedException.addException
                            (new ObjectLockedException
                                 (currentScopeUri.toString()));
                    }
                }
            }
        }
        
        // Then, if the desired scope is inheritable, we parse the
        // locked subject's children to see if any of them has been
        // locked with an incompatible lock.
        if (token.isInheritable()) {
            Stack childrenStack = new Stack();
            childrenStack.push(initialObject);
            while (!isLocked && !childrenStack.empty()) {
                ObjectNode currentObject = (ObjectNode) childrenStack.pop();
                Uri currentObjectUri =
                    namespace.getUri(slideToken, currentObject.getUri());
                // We test the compatibility of the child
                Enumeration locks = currentObjectUri.getStore()
                    .enumerateLocks(currentObjectUri);
                
                while (locks.hasMoreElements()) {
                    NodeLock currentLockToken = (NodeLock) locks.nextElement();
                    if (!isCompatible(slideToken, token,
                                      currentLockToken, tryToLock)) {
                        isLocked = true;
                        if (nestedException != null) {
                            nestedException.addException
                                (new ObjectLockedException
                                     (currentObjectUri.toString()));
                        }
                    }
                }
                
                // We get the children and add them to the Stack.
                
                Vector childrenVector = new Vector();
                Enumeration childrenUri = currentObject.enumerateChildren();
                while (childrenUri.hasMoreElements()) {
                    String childUri = (String) childrenUri.nextElement();
                    Uri tempUri = namespace.getUri(slideToken, childUri);
                    ObjectNode child = tempUri.getStore()
                        .retrieveObject(tempUri);
                    childrenVector.addElement(child);
                }
                
                Enumeration children = childrenVector.elements();
                while (children.hasMoreElements()) {
                    ObjectNode tempObject =
                        (ObjectNode) children.nextElement();
                    childrenStack.push(tempObject);
                }
            }
        }
        
        return isLocked;
        
    }
    
    
    /**
     * Method isCompatibe
     *
     * @param    slideToken          a  SlideToken
     * @param    checkToken          the "current" token
     * @param    matchToken          the token to check against
     *                               (from a resource)
     * @param    tryToLock           a  boolean
     *
     * @return   a boolean
     *
     */
    private boolean isCompatible(SlideToken slideToken,
                                 NodeLock checkToken,
                                 NodeLock matchToken,
                                 boolean tryToLock)
        throws ServiceAccessException {
        
        boolean compatible = true;
        
        if (matchToken.hasExpired()) {
            // Since the lock has expired, it is removed
            try {
                if (slideToken.isForceStoreEnlistment()) {
                    Uri token2Uri = namespace.getUri(slideToken,
                                                     matchToken.getObjectUri());
                    token2Uri.getStore().removeLock(token2Uri, matchToken);
                }
            }
            catch (LockTokenNotFoundException e) {} // ignore silently
            return true;
        }
        
        // same-object-uri-OR-matchToken-is-inheritable
        boolean condition0 = matchToken.getObjectUri().equals(checkToken.getObjectUri()) || matchToken.isInheritable();
        if (!condition0) {
            return true;
        }
        
        // lock-types-are-equal
        boolean condition1 = matchToken.getTypeUri().equals(checkToken.getTypeUri());
        
        // user-of-tokens-are-equal OR user-of-matchToken-is-parent
        SubjectNode checkSubject = SubjectNode.getSubjectNode(checkToken.getSubjectUri());
        SubjectNode matchSubject = SubjectNode.getSubjectNode(matchToken.getSubjectUri());
        boolean condition2 =
            securityHelper.matchPrincipal(slideToken, checkSubject, matchSubject);
        
        // lock-types-are-equal OR lock-type-of-matchToken-is-parent
        ActionNode checkAction = ActionNode.getActionNode(checkToken.getTypeUri());
        ActionNode matchAction = ActionNode.getActionNode(matchToken.getTypeUri());
        boolean condition3 =
            securityHelper.matchAction(slideToken, checkAction, matchAction);
        
        // checkToken-is-exclusive
        boolean condition4 = checkToken.isExclusive();
        
        // check-matchToken-against-slidetoken
        boolean condition5 = checkLockToken(slideToken, matchToken);
        
        // enforced-locktokens
        boolean condition6 = slideToken.isEnforceLockTokens();
        
        if ((tryToLock && condition1 && condition4)
            || (condition3 && !condition2 && !condition6)
            || (condition3 && !condition5 && condition6)
            || (condition5 && !condition2)  // FIXES 30982
           ){
            compatible = false;
        }
        
        // sharing the lock is OK
        if (checkToken.isShared() && matchToken.isShared() && tryToLock && condition1 ) {
            compatible = true;
        }
        
        //            System.out.println();
        //            System.out.println("SlideToken:");
        //            System.out.println("   .showLockTokens      = " + slideToken.showLockTokens());
        //            System.out.println("   .isEnforceLockTokens = " + slideToken.isEnforceLockTokens());
        //            System.out.println("Token1 = " + checkToken);
        //            System.out.println("   .getLockId       = " + checkToken.getLockId());
        //            System.out.println("   .getTypeUri      = " + checkToken.getTypeUri());
        //            System.out.println("   .getSubjectUri   = " + checkToken.getSubjectUri());
        //            System.out.println("   .getObjectUri    = " + checkToken.getObjectUri());
        //            System.out.println("   .isExclusive     = " + checkToken.isExclusive());
        //            System.out.println("   .isInheritable() = " + checkToken.isInheritable());
        //            System.out.println("Token2 = " + matchToken);
        //            System.out.println("   .getLockId       = " + matchToken.getLockId());
        //            System.out.println("   .getTypeUri      = " + matchToken.getTypeUri());
        //            System.out.println("   .getSubjectUri   = " + matchToken.getSubjectUri());
        //            System.out.println("   .getObjectUri    = " + matchToken.getObjectUri());
        //            System.out.println("   .isExclusive     = " + matchToken.isExclusive());
        //            System.out.println("   .isInheritable() = " + matchToken.isInheritable());
        //            System.out.println("Parameter tryToLock: "+tryToLock);
        //            System.out.println("C1 (lock-types-are-equal).................................: " + condition1);
        //            System.out.println("C2 (user-of-tokens-are-equal OR user-of-matchToken-is-parent).: " + condition2);
        //            System.out.println("C3 (lock-types-are-equal OR lock-type-of-checkToken-is-parent): " + condition3);
        //            System.out.println("C4 (checkToken-is-exclusive)..................................: " + condition4);
        //            System.out.println("C5 (check-matchToken-against-slidetoken)......................: " + condition5);
        //            System.out.println("C6 (enforced-locktokens)..................................: " + condition6);
        //            System.out.println("C7 (same-object-uri-OR-matchToken-is-inheritable).............: " + condition7);
        //            System.out.println("Compatible (tryToLock && C1 && C4 && C7 || C3 && !C2 && !C6 && C7 || C3 && !C5 && C6 && C7): " + compatible);
        
        return compatible;
    }
    
    /**
     * Tells whether or not two locks are compatible.
     *
     * @param token1 First token
     * @param token2 Second token : object's lock
     * @param tryToLock True if we want to check for a lock creation
     * @return boolean True if the locks are compatible
     */
    private boolean isCompatible_OLD(SlideToken slideToken,
                                     NodeLock token1, NodeLock token2,
                                     boolean tryToLock) {
        /*
         System.out.println("**** Check lock ****");
         System.out.println("Lock 1 : " + token1.getSubjectUri() + " action "
         + token1.getTypeUri());
         System.out.println("Lock 2 : " + token2.getSubjectUri() + " on "
         + token2.getObjectUri() + " action "
         + token2.getTypeUri());
         */
        
        boolean compatible = true;
        
        // We first check whether or not the lock is still valid
        if (token2.hasExpired()) {
            // Since the lock has expired, it is removed
            try {
                if (slideToken.isForceStoreEnlistment()) {
                    Uri token2Uri = namespace.getUri(slideToken,
                                                     token2.getObjectUri());
                    token2Uri.getStore().removeLock(token2Uri, token2);
                }
            } catch (SlideException e) {
                e.printStackTrace();
            }
        } else {
            // lock-types-are-equal
            boolean condition1 = token2.getTypeUri().equals(token1.getTypeUri());
            // user-of-tokens-are-equal OR user-of-token2-is-parent
            boolean condition2 =
                (token1.getSubjectUri().startsWith(token2.getSubjectUri()));
            // lock-types-are-equal OR lock-type-of-token1-is-parent
            boolean condition3 = token2.getTypeUri()
                .startsWith(token1.getTypeUri());
            // token1-is-exclusive
            boolean condition4 = token1.isExclusive();
            // check-token2-against-slidetoken
            boolean condition5 = checkLockToken(slideToken, token2);
            // enforced-locktokens
            boolean condition6 = slideToken.isEnforceLockTokens();
            // same-object-uri-OR-token2-is-inheritable
            boolean condition7 = token2.getObjectUri().equals(token1.getObjectUri()) || token2.isInheritable();
            
            if ((tryToLock && condition1 && condition4 && condition7)
                || (condition3 && !condition2 && !condition6 && condition7)
                || (condition3 && !condition5 && condition6 && condition7)
               ){
                compatible = false;
            }
            
            // sharing the lock is OK
            if (token1.isShared() && token2.isShared() && tryToLock && condition1 ) {
                compatible = true;
            }
            
            //          System.out.println();
            //          System.out.println("SlideToken:");
            //          System.out.println("   .showLockTokens      = " + slideToken.showLockTokens());
            //          System.out.println("   .isEnforceLockTokens = " + slideToken.isEnforceLockTokens());
            //          System.out.println("Token1 = " + token1);
            //          System.out.println("   .getLockId       = " + token1.getLockId());
            //          System.out.println("   .getTypeUri    = " + token1.getTypeUri());
            //          System.out.println("   .getSubjectUri = " + token1.getSubjectUri());
            //          System.out.println("   .getObjectUri  = " + token1.getObjectUri());
            //          System.out.println("   .isExclusive   = " + token1.isExclusive());
            //          System.out.println("   .isInheritable() = " + token1.isInheritable());
            //          System.out.println("Token2 = " + token2);
            //          System.out.println("   .getLockId       = " + token2.getLockId());
            //          System.out.println("   .getTypeUri    = " + token2.getTypeUri());
            //          System.out.println("   .getSubjectUri = " + token2.getSubjectUri());
            //          System.out.println("   .getObjectUri  = " + token2.getObjectUri());
            //          System.out.println("   .isExclusive   = " + token2.isExclusive());
            //          System.out.println("   .isInheritable() = " + token2.isInheritable());
            //          System.out.println("Parameter tryToLock: "+tryToLock);
            //          System.out.println("C1 (lock-types-are-equal).................................: " + condition1);
            //          System.out.println("C2 (user-of-tokens-are-equal OR user-of-token2-is-parent).: " + condition2);
            //          System.out.println("C3 (lock-types-are-equal OR lock-type-of-token1-is-parent): " + condition3);
            //          System.out.println("C4 (token1-is-exclusive)..................................: " + condition4);
            //          System.out.println("C5 (check-token2-against-slidetoken)......................: " + condition5);
            //          System.out.println("C6 (enforced-locktokens)..................................: " + condition6);
            //          System.out.println("C7 (same-object-uri-OR-token2-is-inheritable).............: " + condition7);
            //          System.out.println("Compatible (tryToLock && C1 && C4 && C7 || C3 && !C2 && !C6 && C7 || C3 && !C5 && C6 && C7): " + compatible);
            
        }
        return compatible;
    }
}




