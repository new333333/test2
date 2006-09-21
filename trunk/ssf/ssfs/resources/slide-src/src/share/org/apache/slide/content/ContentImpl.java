/*
 * $Header$
 * $Revision: 356411 $
 * $Date: 2005-12-12 18:19:32 -0500 (Mon, 12 Dec 2005) $
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

package org.apache.slide.content;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.slide.common.Domain;
import org.apache.slide.common.Namespace;
import org.apache.slide.common.NamespaceConfig;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideToken;
import org.apache.slide.common.Uri;
import org.apache.slide.common.UriPath;
import org.apache.slide.lock.Lock;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.Security;
import org.apache.slide.structure.ActionNode;
import org.apache.slide.structure.LinkedObjectNotFoundException;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.Structure;
import org.apache.slide.structure.SubjectNode;
import org.apache.slide.util.Configuration;
import org.apache.slide.event.EventDispatcher;
import org.apache.slide.event.ContentEvent;
import org.apache.slide.event.VetoException;

/**
 * Implementation of the content interface.
 *
 * @version $Revision: 356411 $
 */
public class ContentImpl implements Content {
    
    
    // -------------------------------------------------------------- Constants
    protected static final String I_URIREDIRECTORCLASS         = "uriRedirectorClass";
    protected static final String I_URIREDIRECTORCLASS_DEFAULT = "org.apache.slide.webdav.util.DeltavUriRedirector";
    
    
    protected static final int PRE_STORE = 0;
    protected static final int POST_STORE = 1;
    protected static final int POST_RETRIEVE = 2;
    protected static final int PRE_REMOVE = 3;
    protected static final int POST_REMOVE = 4;
    
    protected static Class uriRedirectorClass;
    static {
        try {
            String uriRedirectorClassName = Domain.getParameter(I_URIREDIRECTORCLASS, I_URIREDIRECTORCLASS_DEFAULT);
            uriRedirectorClass = Class.forName( uriRedirectorClassName );
        }
        catch( Exception x ) {
            Domain.warn( "Loading of redirector class failed: "+x.getMessage() );
        }
    }
    
    // ----------------------------------------------------------- Constructors
    
    
    /**
     * Constructor.
     *
     * @param namespace Namespace
     * @param namespaceConfig Namespace configuration
     * @param securityHelper Security helper
     * @param structureHelper Structure helper
     * @param lockHelper lockHelper
     */
    public ContentImpl(Namespace namespace, NamespaceConfig namespaceConfig,
                       Security securityHelper, Structure structureHelper,
                       Lock lockHelper) {
        this.namespace = namespace;
        this.namespaceConfig = namespaceConfig;
        this.securityHelper = securityHelper;
        this.structureHelper = structureHelper;
        this.lockHelper = lockHelper;
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
    
    
    /**
     * Structure helper.
     */
    private Structure structureHelper;
    
    
    /**
     * Lock helper.
     */
    private Lock lockHelper;
    
    
    // -------------------------------------------------------- Content Methods
    
    
    /**
     * Retrieve revision descriptors.
     *
     * @param strUri Uri
     * @return NodeRevisionDescriptors
     */
    public NodeRevisionDescriptors retrieve(SlideToken token, String strUri)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException, ObjectLockedException, VetoException {
        
        String originalUri = strUri;
        strUri = redirectUri( originalUri ); // security token null - is ignored anyway
        
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Checking security and locking
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getReadRevisionMetadataAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getReadRevisionMetadataAction());
        
        Uri objectUri = namespace.getUri(token, associatedObject.getUri());
        NodeRevisionDescriptors revisionDescriptors = null;
        try {
            revisionDescriptors = objectUri.getStore()
                .retrieveRevisionDescriptors(objectUri);
        } catch (RevisionDescriptorNotFoundException e) {
            // No revision descriptors. We have to create some.
            revisionDescriptors = new NodeRevisionDescriptors();
            revisionDescriptors.setUri(objectUri.toString());
            // FIXME: createRevisionDescriptors shouldn't be done in this read-only method
            objectUri.getStore()
                .createRevisionDescriptors(objectUri, revisionDescriptors);
        }
        
        revisionDescriptors.setOriginalUri( originalUri );

        return revisionDescriptors;
    }
    
    
    /**
     * Retrieve revision descriptor of the latest revision
     * from a branch.
     *
     * @param revisionDescriptors Node revision descriptors
     * @param branch String branch
     */
    public NodeRevisionDescriptor retrieve
        (SlideToken token, NodeRevisionDescriptors revisionDescriptors,
         String branch)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException,
        BranchNotFoundException, NodeNotVersionedException, VetoException {
        
        NodeRevisionDescriptor result;
        Uri objectUri = namespace.getUri(token, revisionDescriptors.getUri());
        NodeRevisionNumber latestNrn =
            redirectLatestRevisionNumber( revisionDescriptors.getOriginalUri() );
        
        NodeRevisionDescriptors realRevisionDescriptors = objectUri.getStore()
            .retrieveRevisionDescriptors(objectUri);
        
        if (!realRevisionDescriptors.isVersioned()) {
            // Invalid function call : we try to create a revision, but the
            // descriptors won't allow it
            throw new NodeNotVersionedException(realRevisionDescriptors.getUri());
        }
        
        if( latestNrn == null ) {
            // Retrieving latest revision numbers
            NodeRevisionNumber branchLatestRevisionNumber =
                realRevisionDescriptors.getLatestRevision(branch);
            
            if (branchLatestRevisionNumber == null) {
                throw new BranchNotFoundException
                    (realRevisionDescriptors.getUri().toString(), branch);
            }
            result = retrieve(token, realRevisionDescriptors,
                              branchLatestRevisionNumber);
        }
        else {
            result = retrieve( token, revisionDescriptors,
                              latestNrn );
        }

        return result;
    }
    
    
    /**
     * Retrieve revision descriptor.
     *
     * @param revisionDescriptors Node revision descriptors
     * @param revisionNumber Node revision number
     */
    public NodeRevisionDescriptor retrieve
        (SlideToken token, NodeRevisionDescriptors revisionDescriptors,
         NodeRevisionNumber revisionNumber)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException, VetoException {
        
        ObjectNode associatedObject = structureHelper.retrieve
            (token, revisionDescriptors.getUri(), false);
        
        // Checking security and locking
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getReadRevisionMetadataAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getReadRevisionMetadataAction());
        
        Uri objectUri = namespace.getUri(token, revisionDescriptors.getUri());
        
        NodeRevisionDescriptor revisionDescriptor =
            objectUri.getStore().retrieveRevisionDescriptor
            (objectUri, revisionNumber);
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                           null, POST_RETRIEVE);
        
        // Fire event
        if ( ContentEvent.RETRIEVE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.RETRIEVE, new ContentEvent(this, token, namespace,  objectUri.toString(), revisionDescriptors, revisionDescriptor));

        return revisionDescriptor;
    }
    
    
    /**
     * Retrieve revision descriptor from the latest revision
     * in the main branch.
     *
     * @param revisionDescriptors Node revision descriptors
     */
    public NodeRevisionDescriptor retrieve
        (SlideToken token, NodeRevisionDescriptors revisionDescriptors)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException, VetoException {
        
        NodeRevisionDescriptor result;
        Uri objectUri = namespace.getUri(token, revisionDescriptors.getUri());
        NodeRevisionNumber latestNrn =
            redirectLatestRevisionNumber( revisionDescriptors.getOriginalUri() );
        
        NodeRevisionDescriptors realRevisionDescriptors = objectUri.getStore()
            .retrieveRevisionDescriptors(objectUri);
        
        if( latestNrn == null ) {
            result = retrieve( token, revisionDescriptors,
                              realRevisionDescriptors.getLatestRevision());
        }
        else {
            result = retrieve( token, revisionDescriptors,
                              latestNrn );
        }

        return result;
    }
    
    
    /**
     * Retrieve revision content.
     *
     * @param revisionDescriptors Node revision descriptors
     * @param revisionDescriptor Node revision descriptor
     */
    public NodeRevisionContent retrieve
        (SlideToken token, NodeRevisionDescriptors revisionDescriptors,
         NodeRevisionDescriptor revisionDescriptor)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionNotFoundException, RevisionContentNotFoundException,
        ObjectLockedException, VetoException {
        return retrieve(token, revisionDescriptors.getUri(),
                        revisionDescriptor);
    }
    
    
    /**
     * Retrieve revision content.
     *
     * @param strUri Uri
     * @param revisionDescriptor Node revision descriptor
     */
    public NodeRevisionContent retrieve
        (SlideToken token, String strUri,
         NodeRevisionDescriptor revisionDescriptor)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionNotFoundException, RevisionContentNotFoundException,
        ObjectLockedException, VetoException {
        
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Checking security and locking
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getReadRevisionContentAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getReadRevisionContentAction());
        
        Uri objectUri = namespace.getUri(token, strUri);
        NodeRevisionContent revisionContent =
            objectUri.getStore().retrieveRevisionContent(objectUri,
                                                         revisionDescriptor);
        
        // Invoke interceptors
        invokeInterceptors(token, null, revisionDescriptor,
                           revisionContent, POST_RETRIEVE);
        
        // Fire event
        if ( ContentEvent.RETRIEVE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.RETRIEVE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptor, revisionContent));

        return revisionContent;
    }
    
    
    /**
     * Create new revision descriptors.
     *
     * @param strUri Uri
     * @param isVersioned true is the resource is versioned
     */
    public void create(SlideToken token, String strUri,
                       boolean isVersioned)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        ObjectLockedException, VetoException {
        
        // Check parent exists and is not lock-null
        checkParentExists(strUri, token);
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        if (namespaceConfig.getCreateRevisionMetadataAction() !=
            namespaceConfig.getCreateRevisionContentAction()) {
            securityHelper.checkCredentials
                (token, associatedObject,
                 namespaceConfig.getCreateRevisionContentAction());
            lockHelper.checkLock
                (token, associatedObject,
                 namespaceConfig.getCreateRevisionContentAction());
        }
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        NodeRevisionDescriptors revisionDescriptors =
            new NodeRevisionDescriptors(isVersioned);
        revisionDescriptors.setUri(strUri);

        objectUri.getStore()
            .createRevisionDescriptors(objectUri, revisionDescriptors);
    }
    
    
    /**
     * Create new revision in main branch.
     *
     * @param strUri Uri
     * @param revisionDescriptor New Node revision descriptor
     * @param revisionContent New Node revision content
     */
    public void create(SlideToken token, String strUri,
                       NodeRevisionDescriptor revisionDescriptor,
                       NodeRevisionContent revisionContent)
        throws ObjectNotFoundException, AccessDeniedException,
        RevisionAlreadyExistException, LinkedObjectNotFoundException,
        ServiceAccessException, ObjectLockedException, VetoException {
        
        // Check parent exists and is not lock-null
        checkParentExists(strUri, token);
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        if (namespaceConfig.getCreateRevisionMetadataAction() !=
            namespaceConfig.getCreateRevisionContentAction()) {
            securityHelper.checkCredentials
                (token, associatedObject,
                 namespaceConfig.getCreateRevisionContentAction());
            lockHelper.checkLock
                (token, associatedObject,
                 namespaceConfig.getCreateRevisionContentAction());
        }
        
        setDefaultProperties(associatedObject, revisionDescriptor);
        // set the creation date if not already set
        if (revisionDescriptor.getCreationDate() == null) {
            revisionDescriptor.setCreationDate(new Date());
            
            // Set the creation user
            setCreationUser(token, revisionDescriptor);
        }
        // set the display name (in case of copy)
        if (!Configuration.useBinding(namespace.getUri(token, strUri).getStore())) {
            if (revisionDescriptor.getName() == null || revisionDescriptor.getName().length() == 0) {
                revisionDescriptor.setName(UriPath.getLastSegment(strUri));
            }
        }
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        NodeRevisionDescriptors revisionDescriptors = null;
        try {
            revisionDescriptors = objectUri.getStore()
                .retrieveRevisionDescriptors(objectUri);
        } catch (RevisionDescriptorNotFoundException e) {
            // No revision descriptors. We have to create some.
            revisionDescriptors = new NodeRevisionDescriptors();
            revisionDescriptors.setUri(objectUri.toString());
            objectUri.getStore()
                .createRevisionDescriptors(objectUri, revisionDescriptors);
        }
        
        // Retrieve the latest revision from the descriptor,
        // unless there is no revisions. We generate a new revision number,
        // basing on an existing revision, if any.
        NodeRevisionNumber newRevisionNumber = null;
        if (revisionDescriptors.isVersioned()) {
            
            if (revisionDescriptors.hasRevisions()) {
                newRevisionNumber = new NodeRevisionNumber
                    (revisionDescriptors.getLatestRevision());
                revisionDescriptors
                    .addSuccessor(revisionDescriptors.getLatestRevision(),
                                  newRevisionNumber);
                revisionDescriptors
                    .setSuccessors(newRevisionNumber, new Vector());
            } else {
                newRevisionNumber = new NodeRevisionNumber();
                revisionDescriptors
                    .setSuccessors(newRevisionNumber, new Vector());
            }
            // We now set the newly created revision as the latest revison
            revisionDescriptors.setLatestRevision(newRevisionNumber);
            
            // We update the descriptor
            revisionDescriptor.setRevisionNumber(newRevisionNumber);

            // Fire event
            if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

            // Invoke interceptors
            invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                               revisionContent, PRE_STORE);
            
            if (revisionContent != null) {
                // Storing the new revision contents
                objectUri.getStore()
                    .createRevisionContent(objectUri, revisionDescriptor,
                                           revisionContent);
            }
            // Now creating the revision desriptor in the store
            revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
            revisionDescriptor.setModificationUser(
                securityHelper.getPrincipal(token).getPath().lastSegment());
            objectUri.getStore()
                .createRevisionDescriptor(objectUri, revisionDescriptor);
            
        } else {
            // We don't use versioning for this object.
            // Two options :
            // - The object already has one (and only one) revision,
            //   so we update it
            // - The object dooesn't have any revisions right now, so we create
            //   the initial revision
            newRevisionNumber = new NodeRevisionNumber();
            revisionDescriptor.setRevisionNumber(newRevisionNumber);
            
            if (!revisionDescriptors.hasRevisions()) {

                // Fire event
                if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

                // Invoke interceptors
                invokeInterceptors(token, revisionDescriptors,
                                   revisionDescriptor,
                                   revisionContent, PRE_STORE);
                
                if (revisionContent != null) {
                    // Storing the new revision contents
                    objectUri.getStore()
                        .createRevisionContent(objectUri, revisionDescriptor,
                                               revisionContent);
                }
                // Now creating the revision desriptor in the store
                revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
                revisionDescriptor.setModificationUser(
                    securityHelper.getPrincipal(token).getPath().lastSegment());
                objectUri.getStore()
                    .createRevisionDescriptor(objectUri, revisionDescriptor);
                
            } else {
                
                try { {
                        // merge the new received properties into the
                        // revisionDescriptor
                        
                        // We update the descriptor's properties
                        NodeRevisionDescriptor oldRevisionDescriptor =
                            objectUri.getStore()
                            .retrieveRevisionDescriptor
                            (objectUri, newRevisionNumber);
                        Enumeration newPropertiesList =
                            revisionDescriptor.enumerateProperties();
                        while (newPropertiesList.hasMoreElements()) {
                            oldRevisionDescriptor
                                .setProperty((NodeProperty) newPropertiesList
                                                 .nextElement() );
                        }
                        
                        // now use the merged revision descriptor
                        revisionDescriptor = oldRevisionDescriptor;
                    } // end of merge

                    // Fire event
                    if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

                    // Invoke interceptors
                    invokeInterceptors(token, revisionDescriptors,
                                       revisionDescriptor,
                                       revisionContent, PRE_STORE);
                    
                    if (revisionContent != null) {
                        // Storing the new revision contents
                        try {
                            objectUri.getStore()
                                .storeRevisionContent(objectUri,
                                                      revisionDescriptor,
                                                      revisionContent);
                        } catch (RevisionNotFoundException e) {
                            objectUri.getStore()
                                .createRevisionContent(objectUri,
                                                       revisionDescriptor,
                                                       revisionContent);
                        }
                    }
                    
                    revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
                    revisionDescriptor.setModificationUser(
                        securityHelper.getPrincipal(token).getPath().lastSegment());
                    objectUri.getStore()
                        .storeRevisionDescriptor
                        (objectUri, revisionDescriptor);
                    
                } catch (RevisionDescriptorNotFoundException e) {
                    // Should NEVER happen.
                    // Basically, it would mean that there is no initial
                    // revision, which is incorrect since the object
                    // HAS revisions.

                    // Fire event
                    if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

                    // Invoke interceptors
                    invokeInterceptors(token, revisionDescriptors,
                                       revisionDescriptor,
                                       revisionContent, PRE_STORE);
                    
                    revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
                    revisionDescriptor.setModificationUser(
                        securityHelper.getPrincipal(token).getPath().lastSegment());
                    objectUri.getStore()
                        .createRevisionDescriptor(objectUri,
                                                  revisionDescriptor);
                }
            }
            // Updating the descriptors object
            revisionDescriptors
                .setSuccessors(newRevisionNumber, new Vector());
            revisionDescriptors.setLatestRevision(newRevisionNumber);
        }
        
        // We now store the updated revision descriptors
        try {
            objectUri.getStore()
                .storeRevisionDescriptors(objectUri, revisionDescriptors);
        } catch (RevisionDescriptorNotFoundException e) {
            // Problem ...
            e.printStackTrace();
        }
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                           revisionContent, POST_STORE);
    }
    
	// 9/20/06 JK - Similar to create() method, except that this method
    // skips the checking for parents. 
    public void createSimple(SlideToken token, String strUri,
                       NodeRevisionDescriptor revisionDescriptor,
                       NodeRevisionContent revisionContent)
        throws ObjectNotFoundException, AccessDeniedException,
        RevisionAlreadyExistException, LinkedObjectNotFoundException,
        ServiceAccessException, ObjectLockedException, VetoException {
        
        // Check parent exists and is not lock-null
    	// 9/20/06 JK - commented out
        // checkParentExists(strUri, token); 
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        // 9/20/06 JK - Skip lock check. This is expensive operation that
        // we don't really need/care. 
        //lockHelper.checkLock
        //    (token, associatedObject,
        //     namespaceConfig.getCreateRevisionMetadataAction());
        if (namespaceConfig.getCreateRevisionMetadataAction() !=
            namespaceConfig.getCreateRevisionContentAction()) {
            securityHelper.checkCredentials
                (token, associatedObject,
                 namespaceConfig.getCreateRevisionContentAction());
            lockHelper.checkLock
                (token, associatedObject,
                 namespaceConfig.getCreateRevisionContentAction());
        }
        
        setDefaultProperties(associatedObject, revisionDescriptor);
        // set the creation date if not already set
        if (revisionDescriptor.getCreationDate() == null) {
            revisionDescriptor.setCreationDate(new Date());
            
            // Set the creation user
            setCreationUser(token, revisionDescriptor);
        }
        // set the display name (in case of copy)
        if (!Configuration.useBinding(namespace.getUri(token, strUri).getStore())) {
            if (revisionDescriptor.getName() == null || revisionDescriptor.getName().length() == 0) {
                revisionDescriptor.setName(UriPath.getLastSegment(strUri));
            }
        }
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        NodeRevisionDescriptors revisionDescriptors = null;
        try {
            revisionDescriptors = objectUri.getStore()
                .retrieveRevisionDescriptors(objectUri);
        } catch (RevisionDescriptorNotFoundException e) {
            // No revision descriptors. We have to create some.
            revisionDescriptors = new NodeRevisionDescriptors();
            revisionDescriptors.setUri(objectUri.toString());
            objectUri.getStore()
                .createRevisionDescriptors(objectUri, revisionDescriptors);
        }
        
        // Retrieve the latest revision from the descriptor,
        // unless there is no revisions. We generate a new revision number,
        // basing on an existing revision, if any.
        NodeRevisionNumber newRevisionNumber = null;
        if (revisionDescriptors.isVersioned()) {
            
            if (revisionDescriptors.hasRevisions()) {
                newRevisionNumber = new NodeRevisionNumber
                    (revisionDescriptors.getLatestRevision());
                revisionDescriptors
                    .addSuccessor(revisionDescriptors.getLatestRevision(),
                                  newRevisionNumber);
                revisionDescriptors
                    .setSuccessors(newRevisionNumber, new Vector());
            } else {
                newRevisionNumber = new NodeRevisionNumber();
                revisionDescriptors
                    .setSuccessors(newRevisionNumber, new Vector());
            }
            // We now set the newly created revision as the latest revison
            revisionDescriptors.setLatestRevision(newRevisionNumber);
            
            // We update the descriptor
            revisionDescriptor.setRevisionNumber(newRevisionNumber);

            // Fire event
            if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

            // Invoke interceptors
            invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                               revisionContent, PRE_STORE);
            
            if (revisionContent != null) {
                // Storing the new revision contents
                objectUri.getStore()
                    .createRevisionContent(objectUri, revisionDescriptor,
                                           revisionContent);
            }
            // Now creating the revision desriptor in the store
            revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
            revisionDescriptor.setModificationUser(
                securityHelper.getPrincipal(token).getPath().lastSegment());
            objectUri.getStore()
                .createRevisionDescriptor(objectUri, revisionDescriptor);
            
        } else {
            // We don't use versioning for this object.
            // Two options :
            // - The object already has one (and only one) revision,
            //   so we update it
            // - The object dooesn't have any revisions right now, so we create
            //   the initial revision
            newRevisionNumber = new NodeRevisionNumber();
            revisionDescriptor.setRevisionNumber(newRevisionNumber);
            
            if (!revisionDescriptors.hasRevisions()) {

                // Fire event
                if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

                // Invoke interceptors
                invokeInterceptors(token, revisionDescriptors,
                                   revisionDescriptor,
                                   revisionContent, PRE_STORE);
                
                if (revisionContent != null) {
                    // Storing the new revision contents
                    objectUri.getStore()
                        .createRevisionContent(objectUri, revisionDescriptor,
                                               revisionContent);
                }
                // Now creating the revision desriptor in the store
                revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
                revisionDescriptor.setModificationUser(
                    securityHelper.getPrincipal(token).getPath().lastSegment());
                objectUri.getStore()
                    .createRevisionDescriptor(objectUri, revisionDescriptor);
                
            } else {
                
                try { {
                        // merge the new received properties into the
                        // revisionDescriptor
                        
                        // We update the descriptor's properties
                        NodeRevisionDescriptor oldRevisionDescriptor =
                            objectUri.getStore()
                            .retrieveRevisionDescriptor
                            (objectUri, newRevisionNumber);
                        Enumeration newPropertiesList =
                            revisionDescriptor.enumerateProperties();
                        while (newPropertiesList.hasMoreElements()) {
                            oldRevisionDescriptor
                                .setProperty((NodeProperty) newPropertiesList
                                                 .nextElement() );
                        }
                        
                        // now use the merged revision descriptor
                        revisionDescriptor = oldRevisionDescriptor;
                    } // end of merge

                    // Fire event
                    if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

                    // Invoke interceptors
                    invokeInterceptors(token, revisionDescriptors,
                                       revisionDescriptor,
                                       revisionContent, PRE_STORE);
                    
                    if (revisionContent != null) {
                        // Storing the new revision contents
                    	// 9/20/06 JK - Create new resource without first attempting to update existing one. 
                        objectUri.getStore()
                            .createRevisionContent(objectUri,
                                                   revisionDescriptor,
                                                   revisionContent);
                    }
                    
                    revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
                    revisionDescriptor.setModificationUser(
                        securityHelper.getPrincipal(token).getPath().lastSegment());
                    objectUri.getStore()
                        .storeRevisionDescriptor
                        (objectUri, revisionDescriptor);
                    
                } catch (RevisionDescriptorNotFoundException e) {
                    // Should NEVER happen.
                    // Basically, it would mean that there is no initial
                    // revision, which is incorrect since the object
                    // HAS revisions.

                    // Fire event
                    if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

                    // Invoke interceptors
                    invokeInterceptors(token, revisionDescriptors,
                                       revisionDescriptor,
                                       revisionContent, PRE_STORE);
                    
                    revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
                    revisionDescriptor.setModificationUser(
                        securityHelper.getPrincipal(token).getPath().lastSegment());
                    objectUri.getStore()
                        .createRevisionDescriptor(objectUri,
                                                  revisionDescriptor);
                }
            }
            // Updating the descriptors object
            revisionDescriptors
                .setSuccessors(newRevisionNumber, new Vector());
            revisionDescriptors.setLatestRevision(newRevisionNumber);
        }
        
        // We now store the updated revision descriptors
        try {
            objectUri.getStore()
                .storeRevisionDescriptors(objectUri, revisionDescriptors);
        } catch (RevisionDescriptorNotFoundException e) {
            // Problem ...
            e.printStackTrace();
        }
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                           revisionContent, POST_STORE);
    }
        
    /**
     * Create new revision based on a previous revision.
     *
     * @param strUri Uri
     * @param branch Branch in which to create the revision
     * @param newRevisionDescriptor New revision descriptor
     * @param revisionContent Node revision content
     */
    public void create(SlideToken token, String strUri, String branch,
                       NodeRevisionDescriptor newRevisionDescriptor,
                       NodeRevisionContent revisionContent)
        throws ObjectNotFoundException, AccessDeniedException,
        RevisionAlreadyExistException, LinkedObjectNotFoundException,
        ServiceAccessException, RevisionDescriptorNotFoundException,
        ObjectLockedException, NodeNotVersionedException,
        BranchNotFoundException, VetoException {
        
        // Check parent exists and is not lock-null
        checkParentExists(strUri, token);
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        NodeRevisionDescriptors revisionDescriptors = objectUri.getStore()
            .retrieveRevisionDescriptors(objectUri);
        
        if( branch != null ) {
            // Retrieving latest revision numbers
            NodeRevisionNumber branchLatestRevisionNumber =
                revisionDescriptors.getLatestRevision(branch);
            
            if (branchLatestRevisionNumber == null) {
                throw new BranchNotFoundException(strUri, branch);
            }
            
            create(token, strUri, branchLatestRevisionNumber,
                   newRevisionDescriptor, revisionContent);
        }
        else {
            // special handling for DeltaV used for the creation of the
            // branch-less VHR descriptor at version 0.0
            create( token, strUri, newRevisionDescriptor );
        }
        
        
    }
    
    
    /**
     * Create a branch based on specified revision.
     *
     * @param strUri Uri
     * @param branchName Name of the new branch
     * @param basedOnRevisionDescriptor Node revision descriptor of
     *                                  the revision on which the new branch
     *                                  is based on.
     *
     * @return the NodeRevisionNumber of the created revision.
     */
    public NodeRevisionNumber fork(SlideToken token, String strUri, String branchName,
                                   NodeRevisionDescriptor basedOnRevisionDescriptor)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException,
        NodeNotVersionedException, RevisionAlreadyExistException, VetoException {
        
        return fork(token, strUri, branchName,
                    basedOnRevisionDescriptor.getRevisionNumber());
        
    }
    
    
    /**
     * Create a branch based on specified revision.
     *
     * @param strUri Uri
     * @param branchName Name of the new branch
     * @param basedOnRevisionNumber NodeRevisionNumber revision descriptor of
     *                                  the revision on which the new branch
     *                                  is based on.
     *
     * @return the NodeRevisionNumber of the created revision.
     */
    public NodeRevisionNumber fork(SlideToken token, String strUri, String branchName,
                                   NodeRevisionNumber basedOnRevisionNumber)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException,
        NodeNotVersionedException, RevisionAlreadyExistException, VetoException {
        
        if (branchName.equals(NodeRevisionDescriptors.MAIN_BRANCH))
            return null;
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionContentAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getCreateRevisionContentAction());
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        // Retrieve the revision table
        NodeRevisionDescriptors revisionDescriptors =
            objectUri.getStore()
            .retrieveRevisionDescriptors(objectUri);
        
        if (!revisionDescriptors.isVersioned()) {
            // Invalid function call : we try to create a revision, but the
            // descriptors won't allow it
            throw new NodeNotVersionedException(strUri);
        }
        
        // Retrieving the revision on which the new branch is based.
        NodeRevisionDescriptor basedOnRevisionDescriptor =
            objectUri.getStore().retrieveRevisionDescriptor
            (objectUri, basedOnRevisionNumber);
        NodeRevisionContent basedOnRevisionContent = null;
        try {
            basedOnRevisionContent =
                objectUri.getStore().retrieveRevisionContent
                (objectUri,
                 basedOnRevisionDescriptor);
        } catch (RevisionNotFoundException e) {
        }
        
        // Create a revision number suited for the new branch
        NodeRevisionNumber branchedRevisionNumber =
            new NodeRevisionNumber(basedOnRevisionNumber, true);
        
        basedOnRevisionDescriptor.setRevisionNumber(branchedRevisionNumber);
        basedOnRevisionDescriptor.setBranchName(branchName);
        
        revisionDescriptors.setUri(strUri);
        revisionDescriptors.setLatestRevision
            (branchName, branchedRevisionNumber);
        revisionDescriptors.addSuccessor
            (basedOnRevisionNumber, branchedRevisionNumber);
        
        // Fire event
        if ( ContentEvent.FORK.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.FORK, new ContentEvent(this, token, namespace, strUri, revisionDescriptors));

        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors,
                           basedOnRevisionDescriptor,
                           basedOnRevisionContent, PRE_STORE);
        
        // Storing back everything
        // TODO: setModificationDate
        //       clone of NRD required??
        if (basedOnRevisionContent != null) {
            objectUri.getStore().createRevisionContent
                (objectUri, basedOnRevisionDescriptor, basedOnRevisionContent);
        }
        objectUri.getStore().createRevisionDescriptor
            (objectUri, basedOnRevisionDescriptor);
        objectUri.getStore().storeRevisionDescriptors
            (objectUri, revisionDescriptors);
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors,
                           basedOnRevisionDescriptor,
                           basedOnRevisionContent, POST_STORE);

        return branchedRevisionNumber;
    }
    
    
    /**
     * Merge specified branches into a single branch.
     *
     * @param strUri Uri
     * @param mainBranch Branch into which the other branch will be merged
     * @param branch Branch to merge into main branch
     * @param newRevisionDescriptor New revision descriptor
     * @param revisionContent Node revision content
     */
    public void merge(SlideToken token, String strUri,
                      NodeRevisionDescriptor mainBranch,
                      NodeRevisionDescriptor branch,
                      NodeRevisionDescriptor newRevisionDescriptor,
                      NodeRevisionContent revisionContent)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException,
        NodeNotVersionedException, BranchNotFoundException,
        RevisionAlreadyExistException, VetoException {
        
        merge(token, strUri, mainBranch.getBranchName(),
              branch.getBranchName(), newRevisionDescriptor, revisionContent);
        
    }
    
    
    /**
     * Merge specified branches into a single branch.
     *
     * @param strUri Uri
     * @param mainBranch Branch into which the other branch will be merged
     * @param branch Branch to merge into main branch
     * @param newRevisionDescriptor New revision descriptor
     * @param revisionContent Node revision content
     */
    public void merge(SlideToken token, String strUri,
                      String mainBranch, String branch,
                      NodeRevisionDescriptor newRevisionDescriptor,
                      NodeRevisionContent revisionContent)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException,
        NodeNotVersionedException, BranchNotFoundException,
        RevisionAlreadyExistException, VetoException {
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionContentAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getCreateRevisionContentAction());
        
        setDefaultProperties(associatedObject, newRevisionDescriptor);
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        // Retrieve the revision table
        NodeRevisionDescriptors revisionDescriptors =
            objectUri.getStore().retrieveRevisionDescriptors(objectUri);
        
        if (!revisionDescriptors.isVersioned()) {
            // Invalid function call : we try to create a revision, but the
            // descriptors won't allow it
            throw new NodeNotVersionedException(strUri);
        }
        
        // Retrieving latest revision numbers
        NodeRevisionNumber mainBranchLatestRevisionNumber =
            revisionDescriptors.getLatestRevision(mainBranch);
        NodeRevisionNumber branchLatestRevisionNumber =
            revisionDescriptors.getLatestRevision(branch);
        
        if (mainBranchLatestRevisionNumber == null) {
            throw new BranchNotFoundException(strUri, mainBranch);
        }
        if (branchLatestRevisionNumber == null) {
            throw new BranchNotFoundException(strUri, branch);
        }
        
        NodeRevisionNumber newRevisionNumber =
            new NodeRevisionNumber(mainBranchLatestRevisionNumber);
        
        newRevisionDescriptor.setRevisionNumber(newRevisionNumber);
        newRevisionDescriptor.setBranchName(branch);
        
        revisionDescriptors.addSuccessor
            (mainBranchLatestRevisionNumber, newRevisionNumber);
        revisionDescriptors.addSuccessor
            (branchLatestRevisionNumber, newRevisionNumber);
        revisionDescriptors.setLatestRevision(mainBranch, newRevisionNumber);
        
        // Fire event
        if ( ContentEvent.MERGE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.MERGE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, newRevisionDescriptor, revisionContent));

        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, newRevisionDescriptor,
                           revisionContent, PRE_STORE);
        
        // Storing back everything
        if (revisionContent != null) {
            objectUri.getStore().createRevisionContent
                (objectUri, newRevisionDescriptor, revisionContent);
        }
        newRevisionDescriptor.setModificationDate(newRevisionDescriptor.getCreationDate());
        newRevisionDescriptor.setModificationUser(
            securityHelper.getPrincipal(token).getPath().lastSegment());
        objectUri.getStore().createRevisionDescriptor
            (objectUri, newRevisionDescriptor);
        objectUri.getStore().storeRevisionDescriptors
            (objectUri, revisionDescriptors);
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, newRevisionDescriptor,
                           revisionContent, POST_STORE);
    }
    
    
    /**
     * Update contents of an existing revision.
     *
     * @param strUri Uri
     * @param revisionDescriptor Revision descriptor
     * @param revisionContent Revision content
     */
    public void store(SlideToken token, String strUri,
                      NodeRevisionDescriptor revisionDescriptor,
                      NodeRevisionContent revisionContent)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException,
        RevisionNotFoundException, VetoException {
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getModifyRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getModifyRevisionMetadataAction());
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getModifyRevisionContentAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getModifyRevisionContentAction());
        
        setDefaultProperties(associatedObject, revisionDescriptor);
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        // Retrieve the revision table
        NodeRevisionDescriptors revisionDescriptors =
            objectUri.getStore().retrieveRevisionDescriptors(objectUri);

        // Fire event
        if ( revisionDescriptor.getRevisionNumber() != NodeRevisionNumber.HIDDEN_0_0 && ContentEvent.STORE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.STORE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor, revisionContent));

        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                           revisionContent, PRE_STORE);
        
        if (revisionContent != null) {
            try {
                // Removed retrieveContent call [pnever, 25-APR-2003].
                // Reasons:
                // - it is not necessary to check existence because storeRevisionContent also throws RevisionNotFoundException
                // - it is harmful for filesystem-based stores, as the input streams created by retrieveRevisionContent never are closed
                // Simple scenario:
                // Use the FileContentStore, create a file, update it and then try to delete it.
                // It will not be deleted from the filesystem.
                //
                //                objectUri.getStore().retrieveRevisionContent
                //                    (objectUri, revisionDescriptor);
                objectUri.getStore().storeRevisionContent
                    (objectUri, revisionDescriptor, revisionContent);
            } catch (RevisionNotFoundException e) {
                try {
                    objectUri.getStore().createRevisionContent
                        (objectUri, revisionDescriptor, revisionContent);
                } catch (RevisionAlreadyExistException ex) {
                    // Should never happen
                    ex.printStackTrace();
                }
            }
        }
        revisionDescriptor.setModificationDate(new Date());
        revisionDescriptor.setModificationUser(
            securityHelper.getPrincipal(token).getPath().lastSegment());
        objectUri.getStore().storeRevisionDescriptor
            (objectUri, revisionDescriptor);
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                           revisionContent, POST_STORE);
    }
    
    
    /**
     * Remove all revisions at this Uri.
     *
     * @param revisionDescriptors Node revision descriptors
     */
    public void remove(SlideToken token,
                       NodeRevisionDescriptors revisionDescriptors)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException, VetoException {
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, null, null, PRE_REMOVE);

        // Retrieve the associated object
        ObjectNode associatedObject = structureHelper.retrieve
            (token, revisionDescriptors.getUri(), false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getRemoveRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getRemoveRevisionMetadataAction());
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getRemoveRevisionContentAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getRemoveRevisionContentAction());
        
        Uri objectUri = namespace.getUri(token, revisionDescriptors.getUri());

        // Fire event
        if ( ContentEvent.REMOVE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.REMOVE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors));

        objectUri.getStore().removeRevisionDescriptors(objectUri);
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, null, null,
                           POST_REMOVE);
    }
    
    
    /**
     * Remove specified revision.
     *
     * @param strUri Uri
     * @param revisionDescriptor Node revision descriptor
     */
    public void remove(SlideToken token, String strUri,
                       NodeRevisionDescriptor revisionDescriptor)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException, VetoException {
        
        remove(token, strUri, revisionDescriptor.getRevisionNumber());
        
    }
    
    
    /**
     * Remove specified revision.
     *
     * @param strUri Uri
     * @param revisionNumber Revision number
     */
    public void remove(SlideToken token, String strUri,
                       NodeRevisionNumber revisionNumber)
        throws ObjectNotFoundException, AccessDeniedException,
        LinkedObjectNotFoundException, ServiceAccessException,
        RevisionDescriptorNotFoundException, ObjectLockedException, VetoException {
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getRemoveRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getRemoveRevisionMetadataAction());
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getRemoveRevisionContentAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getRemoveRevisionContentAction());
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        NodeRevisionDescriptor revisionDescriptor =
            objectUri.getStore().retrieveRevisionDescriptor
            (objectUri, revisionNumber);

        // Fire event
        if ( revisionNumber != NodeRevisionNumber.HIDDEN_0_0 && ContentEvent.REMOVE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.REMOVE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptor));

        // Invoke interceptors
        NodeRevisionDescriptors revisionDescriptors = new NodeRevisionDescriptors();
        revisionDescriptors.setUri(strUri);
        invokeInterceptors(token, revisionDescriptors, revisionDescriptor, null, PRE_REMOVE);
        
        objectUri.getStore().removeRevisionContent
            (objectUri, revisionDescriptor);
        objectUri.getStore()
            .removeRevisionDescriptor(objectUri, revisionNumber);
        
        // Invoke interceptors
        invokeInterceptors(token, null, revisionDescriptor, null, POST_REMOVE);
    }
    
    
    // ------------------------------------------------------ Protected Methods
    
    
    /**
     * Create new revision based on a previous revision.
     *
     * @param strUri Uri
     * @param basedOnRevisionNumber Number of revision on which the
     * new revision is based
     * @param newRevisionDescriptor New revision descriptor
     * @param revisionContent Node revision content
     */
    protected void create(SlideToken token, String strUri,
                          NodeRevisionNumber basedOnRevisionNumber,
                          NodeRevisionDescriptor newRevisionDescriptor,
                          NodeRevisionContent revisionContent)
        throws ObjectNotFoundException, AccessDeniedException,
        RevisionAlreadyExistException, LinkedObjectNotFoundException,
        ServiceAccessException, RevisionDescriptorNotFoundException,
        ObjectLockedException, NodeNotVersionedException, VetoException {
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionContentAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getCreateRevisionContentAction());
        
        setDefaultProperties(associatedObject, newRevisionDescriptor);
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        // Retrieve the revision table
        NodeRevisionDescriptors revisionDescriptors =
            objectUri.getStore()
            .retrieveRevisionDescriptors(objectUri);
        
        if (!revisionDescriptors.isVersioned()) {
            // Invalid function call : we try to create a revision, but the
            // descriptors won't allow it
            throw new NodeNotVersionedException(strUri);
        }
        
        // Retrieve the old revision descriptor, just to make sure that the old
        //  revision we base the new one upon really exists
        NodeRevisionDescriptor realOldRevisionDescriptor =
            objectUri.getStore().retrieveRevisionDescriptor
            (objectUri, basedOnRevisionNumber);
        
        // We check that the old revision doesn't have successors, that is we :
        // - check to see if it's the latest revision in a branch
        // - store that information for later use
        NodeRevisionNumber latestNumberInBranch =
            revisionDescriptors.getLatestRevision
            (realOldRevisionDescriptor.getBranchName());
        if (!realOldRevisionDescriptor.getRevisionNumber()
            .equals(latestNumberInBranch)) {
            throw new RevisionAlreadyExistException
                (objectUri.toString(), new NodeRevisionNumber
                     (basedOnRevisionNumber));
        }
        
        // Next, generate the new revision's number
        newRevisionDescriptor.setRevisionNumber
            (new NodeRevisionNumber(basedOnRevisionNumber));
        // Set the creation date
        newRevisionDescriptor.setCreationDate(new Date());
        
        // Set the creation user
        setCreationUser(token, newRevisionDescriptor);
        
        // Initialize the branch name in the new descriptor
        newRevisionDescriptor.setBranchName
            (realOldRevisionDescriptor.getBranchName());

        // Fire event
        if ( ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, newRevisionDescriptor, revisionContent));

        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, newRevisionDescriptor,
                           revisionContent, PRE_STORE);
        
        // Update the revision graph in the revision descriptors
        revisionDescriptors
            .addSuccessor(revisionDescriptors.getLatestRevision(newRevisionDescriptor.getBranchName()),
                          newRevisionDescriptor.getRevisionNumber());
        revisionDescriptors
            .setSuccessors(newRevisionDescriptor.getRevisionNumber(), new Vector());
        revisionDescriptors
            .setLatestRevision(newRevisionDescriptor.getBranchName(),
                               newRevisionDescriptor.getRevisionNumber());
        if (revisionContent != null) {
            // Storing the new revision contents
            objectUri.getStore()
                .createRevisionContent(objectUri, newRevisionDescriptor,
                                       revisionContent);
        }
        // Now creating the revision desriptor in the store
        newRevisionDescriptor.setModificationDate(newRevisionDescriptor.getCreationDate());
        newRevisionDescriptor.setModificationUser(
            securityHelper.getPrincipal(token).getPath().lastSegment());
        objectUri.getStore()
            .createRevisionDescriptor(objectUri, newRevisionDescriptor);
        
        // We now store the updated revision descriptors
        try {
            objectUri.getStore()
                .storeRevisionDescriptors(objectUri, revisionDescriptors);
        } catch (RevisionDescriptorNotFoundException e) {
            // Problem ...
            e.printStackTrace();
        }
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, newRevisionDescriptor,
                           revisionContent, POST_STORE);
    }
    
    
    /**
     * Create new branch-less revision descriptor.
     * This is used only by DeltaV to store the VHR-specific descriptor at
     * revision 0.0, or to backup the properties of a VCR for checkout/uncheckout
     * at revision 0.0.
     */
    protected void create( SlideToken token, String strUri,
                          NodeRevisionDescriptor revisionDescriptor )
        throws ObjectNotFoundException, AccessDeniedException,
        RevisionAlreadyExistException, LinkedObjectNotFoundException,
        ServiceAccessException, RevisionDescriptorNotFoundException,
        ObjectLockedException, NodeNotVersionedException, VetoException {
        
        // Retrieve the associated object
        ObjectNode associatedObject =
            structureHelper.retrieve(token, strUri, false);
        
        // Next we do a security check and a locking check for modifyRevisions
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        lockHelper.checkLock
            (token, associatedObject,
             namespaceConfig.getCreateRevisionMetadataAction());
        securityHelper.checkCredentials
            (token, associatedObject,
             namespaceConfig.getCreateRevisionContentAction());
        lockHelper.checkLock(token, associatedObject,
                             namespaceConfig.getCreateRevisionContentAction());
        
        setDefaultProperties(associatedObject, revisionDescriptor);
        
        Uri objectUri = namespace.getUri(token, strUri);
        
        // Retrieve the revision table
        NodeRevisionDescriptors revisionDescriptors =
            objectUri.getStore()
            .retrieveRevisionDescriptors(objectUri);
        
        // Set the creation date
        revisionDescriptor.setCreationDate(new Date());
        
        // Set the creation user
        setCreationUser(token, revisionDescriptor);
        
        // Initialize the branch name in the new descriptor
        String branchName = "backup";
        NodeProperty rootVersionProperty = revisionDescriptor.getProperty(
                "version-set");
        if (rootVersionProperty != null) {
            branchName = "version-history";
        }
        revisionDescriptor.setBranchName(branchName);

        // Fire event
        if ( revisionDescriptor.getRevisionNumber() != NodeRevisionNumber.HIDDEN_0_0 && ContentEvent.CREATE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(ContentEvent.CREATE, new ContentEvent(this, token, namespace, objectUri.toString(), revisionDescriptors, revisionDescriptor));

        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                           null, PRE_STORE);
        
        // Now creating the revision desriptor in the store
        revisionDescriptor.setModificationDate(revisionDescriptor.getCreationDate());
        revisionDescriptor.setModificationUser(
            securityHelper.getPrincipal(token).getPath().lastSegment());
        objectUri.getStore()
            .createRevisionDescriptor(objectUri, revisionDescriptor);
        
        // We now store the updated revision descriptors
        try {
            objectUri.getStore()
                .storeRevisionDescriptors(objectUri, revisionDescriptors);
        } catch (RevisionDescriptorNotFoundException e) {
            // Problem ...
            e.printStackTrace();
        }
        
        // Invoke interceptors
        invokeInterceptors(token, revisionDescriptors, revisionDescriptor,
                           null, POST_STORE);
    }
    
    private void setCreationUser(SlideToken token, NodeRevisionDescriptor revisionDescriptor) throws ServiceAccessException, ObjectNotFoundException {
        String creationUser = ((SubjectNode)securityHelper.getPrincipal(token)).getPath().lastSegment();
        revisionDescriptor.setCreationUser(creationUser);
        revisionDescriptor.setOwner(creationUser);
    }
    
    
    /**
     * Set default properties for a revision descriptors.
     */
    protected void setDefaultProperties
        (ObjectNode associatedObject,
         NodeRevisionDescriptor revisionDescriptor) {
        // Retrieving the roles of the associated object
        Enumeration roles = securityHelper.getRoles(associatedObject);
        while (roles.hasMoreElements()) {
            String role = (String) roles.nextElement();
            Enumeration defaultProperties =
                namespaceConfig.getDefaultProperties(role);
            revisionDescriptor.setDefaultProperties(defaultProperties);
        }
        if (namespaceConfig.isPrincipal(associatedObject.getUri())) {
            // principals must have DAV:displayname
            if (revisionDescriptor.getName() == null || revisionDescriptor.getName().length() == 0) {
                revisionDescriptor.setName(
                        UriPath.getLastSegment(associatedObject.getUri()));
            }
            // principals must have DAV:principal in resourcetype
            String rt = revisionDescriptor.getResourceType();
            if (rt.indexOf("principal") < 0) {
                revisionDescriptor.setResourceType(rt+"<principal/>");
            }
        }
    }
    
    
    /**
     * Invoke content interceptors.
     */
    protected void invokeInterceptors
        (SlideToken token, NodeRevisionDescriptors revisionDescriptors,
         NodeRevisionDescriptor revisionDescriptor,
         NodeRevisionContent revisionContent, int type)
        throws AccessDeniedException, ObjectNotFoundException,
        LinkedObjectNotFoundException, ObjectLockedException,
        ServiceAccessException {
        ContentInterceptor[] contentInterceptors =
            namespace.getContentInterceptors();
        for (int i = 0; i < contentInterceptors.length; i++) {
            switch (type) {
                case PRE_STORE:
                    contentInterceptors[i].preStoreContent
                        (token, revisionDescriptors,
                         revisionDescriptor, revisionContent);
                    break;
                case POST_STORE:
                    contentInterceptors[i].postStoreContent
                        (token, revisionDescriptors,
                         revisionDescriptor, revisionContent);
                    break;
                case POST_RETRIEVE:
                    contentInterceptors[i].postRetrieveContent
                        (token, revisionDescriptors,
                         revisionDescriptor, revisionContent);
                    break;
                case PRE_REMOVE:
                    contentInterceptors[i].preRemoveContent
                        (token, revisionDescriptors, revisionDescriptor);
                    break;
                case POST_REMOVE:
                    contentInterceptors[i].postRemoveContent
                        (token, revisionDescriptors, revisionDescriptor);
                    break;
            }
        }
    }
    
    /**
     *
     */
    protected String redirectUri( String uri ) {
        String result = uri;
        
        if( uriRedirectorClass != null ) {
            try {
                Method ru = uriRedirectorClass.getMethod(
                    "redirectUri", new Class[]{String.class} );
                result = (String)ru.invoke( null, new Object[]{uri} ); // obj=null since method is static
            }
            catch( Exception x ) {
                Domain.warn( "Redirecting of URI "+uri+" failed: "+x.getMessage() );
            }
        }
        
        return result;
    }
    
    /**
     *
     */
    protected NodeRevisionNumber redirectLatestRevisionNumber( String uri ) {
        NodeRevisionNumber result = null;
        
        if( uriRedirectorClass != null ) {
            try {
                Method ru = uriRedirectorClass.getMethod(
                    "redirectLatestRevisionNumber", new Class[]{String.class} );
                result = (NodeRevisionNumber)ru.invoke( null, new Object[]{uri} ); // obj=null since method is static
            }
            catch( Exception x ) {
                Domain.warn( "Redirecting of latest revision number for "+uri+" failed: "+x.getMessage() );
            }
        }
        
        return result;
    }
    
    private boolean isLockNull( NodeRevisionDescriptor nrd ) {
        return nrd.propertyValueContains("resourcetype", "lock-null");
    }
    
    private void checkParentExists(String strUri, SlideToken token)
        throws ServiceAccessException, ObjectLockedException, AccessDeniedException,
        LinkedObjectNotFoundException, ObjectNotFoundException, VetoException {
        
        if (namespaceConfig.getCreateObjectAction().equals(ActionNode.DEFAULT)) {
            // do not check during start-up
            return;
        }
        
        String parentUri = UriPath.getParentUri(strUri);
        try {
            NodeRevisionDescriptor parentNrd =
                retrieve(token, retrieve(token, parentUri));
            if (isLockNull(parentNrd)) {
                throw new ObjectNotFoundException(parentUri);
            }
        }
        catch (RevisionDescriptorNotFoundException e) {
            throw new ObjectNotFoundException(parentUri);
        }
    }
}


