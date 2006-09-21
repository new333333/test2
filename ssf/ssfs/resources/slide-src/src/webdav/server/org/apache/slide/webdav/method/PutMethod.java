/*
 * $Header$
 * $Revision: 208584 $
 * $Date: 2005-03-21 13:05:00 -0500 (Mon, 21 Mar 2005) $
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

package org.apache.slide.webdav.method;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideException;
import org.apache.slide.content.ContentImpl;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionContent;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.content.RevisionAlreadyExistException;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.event.EventDispatcher;
import org.apache.slide.structure.LinkedObjectNotFoundException;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.StructureImpl;
import org.apache.slide.structure.SubjectNode;
import org.apache.slide.util.Configuration;
import org.apache.slide.webdav.WebdavException;
import org.apache.slide.webdav.WebdavServletConfig;
import org.apache.slide.webdav.event.WebdavEvent;
import org.apache.slide.webdav.util.DeltavConstants;
import org.apache.slide.webdav.util.PreconditionViolationException;
import org.apache.slide.webdav.util.PropertyHelper;
import org.apache.slide.webdav.util.UriHandler;
import org.apache.slide.webdav.util.ViolatedPrecondition;
import org.apache.slide.webdav.util.WebdavStatus;
import org.apache.slide.webdav.util.WebdavUtils;
import org.apache.slide.webdav.util.resourcekind.AbstractResourceKind;
import org.apache.slide.webdav.util.resourcekind.CheckedInVersionControlled;
import org.apache.slide.webdav.util.resourcekind.ResourceKind;
import org.apache.slide.webdav.util.resourcekind.ResourceKindManager;

/**
 * PUT method.
 *
 */
public class PutMethod
    extends AbstractWebdavMethod
    implements DeltavConstants, WriteMethod, FineGrainedLockingMethod {
    

    // ----------------------------------------------------- Instance Variables
    
    protected PropertyHelper propertyHelper = null;
    
    /**
     * Resource to be written.
     */
    protected String resourcePath;
    
    private NodeRevisionContent revisionContent = null;
    
    // ----------------------------------------------------------- Constructors
    
    
    /**
     * Constructor.
     *
     * @param token     the token for accessing the namespace
     * @param config    configuration of the WebDAV servlet
     */
    public PutMethod(NamespaceAccessToken token, WebdavServletConfig config) {
        super(token, config);
    }
    
    /**
     * @see org.apache.slide.webdav.method.FineGrainedLockingMethod#acquireFineGrainLocks()
     */
    public void acquireFineGrainLocks() {
        acquireStandardLocks(resourcePath);
        // lock history folder in case we have auto versioning turned on
        acquireHistoryLocks(resourcePath);
        // changes this and parent
        acquireLock(resourcePath, WRITE_LOCK);
        acquireParentLock(resourcePath, WRITE_LOCK);
    }

    // ------------------------------------------------------ Protected Methods
    
    
    /**
     * Parse XML request.
     *
     * @exception WebdavException Does not happen
     */
    protected void parseRequest()
        throws WebdavException {
        propertyHelper = PropertyHelper.getPropertyHelper(slideToken, token, 
                getConfig());
        
        resourcePath = requestUri;
        if (resourcePath == null) {
            resourcePath = "/";
        }
    }
    
    /**
     * Execute request.
     *
     * @exception WebdavException Bad request
     */
    protected void executeRequest()
        throws WebdavException, IOException {
        
        // Prevent dirty reads
        slideToken.setForceStoreEnlistment(true);

        // check destination URI
        UriHandler destUh = UriHandler.getUriHandler(resourcePath);
        
        if (destUh.isRestrictedUri()) {
            boolean sendError = true;
            if( destUh.isWorkingresourceUri() ) {
                // PUT on existing WRs is *not* restricted !!!
                try {
                    content.retrieve(slideToken, resourcePath);
                    sendError = false;
                }
                catch( Exception x ) {};
            }
            if( sendError ) {
                int statusCode = WebdavStatus.SC_FORBIDDEN;
                sendError( statusCode, getClass().getName()+".restrictedDestinationUri", new Object[]{resourcePath} );
                throw new WebdavException( statusCode );
            }
        }
        
        try {
            // fire put event
            if ( WebdavEvent.PUT.isEnabled() ) {
                EventDispatcher.getInstance().fireVetoableEvent(WebdavEvent.PUT,
                        new WebdavEvent(this));
            }

            try {

                boolean isLockedNullResource = false;
                
                NodeRevisionDescriptors revisionDescriptors =
                    content.retrieve(slideToken, resourcePath);
                
                NodeRevisionNumber revisionNumber =
                    revisionDescriptors.getLatestRevision();
                NodeRevisionDescriptor oldRevisionDescriptor = null;
                if (revisionNumber != null) {
                    try {
                        oldRevisionDescriptor = content.retrieve
                            (slideToken, revisionDescriptors);
                    } catch (RevisionDescriptorNotFoundException e) {
                    }
                }
                if (WebdavUtils.isCollection(oldRevisionDescriptor)) {
                    int statusCode = WebdavStatus.SC_METHOD_NOT_ALLOWED;
                    sendError( statusCode, getClass().getName()+".mustNotBeCollection" );
                    throw new WebdavException( statusCode );
                }

                // Check if this is a redirect reference.
                if (WebdavUtils.isRedirectref(oldRevisionDescriptor)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Redirect references can not have bodies");
                    return;
                }

                NodeRevisionDescriptor revisionDescriptor = null;
                if (oldRevisionDescriptor == null) {
                    revisionDescriptor = new NodeRevisionDescriptor();
                } else {
                    revisionDescriptor = oldRevisionDescriptor;
                    revisionDescriptor.setContentLength(-1);
                }
                
                ResourceInfo resourceInfo =
                    new ResourceInfo(resourcePath, revisionDescriptor);
                
                // Checking If headers
                if (!checkIfHeaders(req, resp, resourceInfo)) {
                    return;
                }
                
                ResourceKind resourceKind = AbstractResourceKind.determineResourceKind(token, resourcePath, revisionDescriptor);
                
                versioningHelper.isWriteLocked(slideToken, revisionDescriptors);
                
                // check preconditions
                ViolatedPrecondition violatedPrecondition = getPreconditionViolation(revisionDescriptors, revisionDescriptor, resourceKind);
                if (violatedPrecondition != null) {
                    throw new PreconditionViolationException(violatedPrecondition, resourcePath);
                }
                
                // Changed for DeltaV --start--
                boolean mustCheckIn = false;
                if( Configuration.useVersionControl() &&
                       (resourceKind instanceof CheckedInVersionControlled) &&
                   versioningHelper.mustCheckoutAutoVersionedVCR(revisionDescriptors, revisionDescriptor) ) {
                    
                    versioningHelper.checkout(revisionDescriptors, revisionDescriptor, false, false, true );
                    mustCheckIn = versioningHelper.mustCheckinAutoVersionedVCR(slideToken, revisionDescriptors, revisionDescriptor);
                }
                // Changed for DeltaV --end--
                
                // Creating revisionDescriptor associated with the object
                if (revisionContent == null) {
                    revisionContent = new NodeRevisionContent();
                    revisionContent.setContent(req.getInputStream());
                    
                    // Get content length
                    int contentLength = req.getContentLength();
                    // we need to buffer the content to find out the content length
                    // and to save it for a retry
                    if (contentLength == -1 || retryUponConflict) {
                        long longContentLength = revisionContent.bufferContent(revisionContent
                                .streamContent());
                        revisionDescriptor.setContentLength(longContentLength);
                    } else {
                        revisionDescriptor.setContentLength(contentLength);
                    }
                }

                // Last modification date
                revisionDescriptor.setLastModified(new Date());
                
                // Etag generation
                revisionDescriptor.setETag(PropertyHelper.computeEtag(resourcePath, revisionDescriptor) );
                
                // Get content type (allow content-type to be updated here)
                String contentType = req.getContentType();
                if (contentType == null) {
                    contentType = getConfig().getServletContext()
                        .getMimeType(resourcePath);
                }
                if (contentType == null) {
                    contentType = getConfig().getDefaultMimeType();
                }
                revisionDescriptor.setContentType(contentType);
                
                // Normally assume the 'getcontentlanguage'
                // is set, however, before we clear the
                // 'resourcetype' need to check for the case when a
                // 'lock-null' is created just before the initial PUT. In
                // that case need to add the missing properties.
                if (isLockNull(revisionDescriptor)) {
                    //              if (revisionDescriptor.getResourceType().equals("<lock-null/>")) {
                    isLockedNullResource = true;
                    revisionDescriptor.setContentLanguage("en");
                    
                    // Changed for DeltaV --start--
                    if( Configuration.useVersionControl() ) {
                        // Workspace
                        versioningHelper.setWorkspaceProperty( resourcePath, revisionDescriptor );
                    }
                    // Changed for DeltaV --end--
                }
                
                // Resource type
                revisionDescriptor.setResourceType("");
                
                // Owner
                if ( isLockedNullResource ) {
                    // set the owner when updating a lock-null resource
                    String creator = ((SubjectNode)security.getPrincipal(slideToken)).getPath().lastSegment();
                    revisionDescriptor.setCreationUser(creator);
                    revisionDescriptor.setOwner(creator);
                }
                
                content.store(slideToken, resourcePath, revisionDescriptor,
                              revisionContent);
                
                // Changed for DeltaV --start--
                // check if the resource should be put under version-control
                if ( isLockedNullResource ) {
                    if ( Configuration.useVersionControl() && isAutoVersionControl(resourcePath) && !isExcludedForVersionControl(resourcePath) ) {
                        versioningHelper.versionControl(resourcePath);
                    }
                }
                if( Configuration.useVersionControl() && mustCheckIn) {
                    versioningHelper.checkin(revisionDescriptors, revisionDescriptor, false, false, true ); //forkOk=false, keepCheckedOut=false
                }
                // Changed for DeltaV --end--
                
                if (WebdavEvent.PUT_AFTER.isEnabled()) {
                    EventDispatcher.getInstance().fireVetoableEvent(WebdavEvent.PUT_AFTER,
                            new WebdavEvent(this));
                }
                
                // ETag header
                resp.setHeader("ETag", revisionDescriptor.getETag() );
                
                resp.setStatus(WebdavStatus.SC_NO_CONTENT);
                
            } catch (LinkedObjectNotFoundException e) {
                int statusCode = getErrorCode( e );
                sendError( statusCode, e );
                throw new WebdavException( statusCode );
            } catch (ObjectNotFoundException e) {
                SubjectNode subject = new SubjectNode();
                // Creating an object
                // 9/20/06 JK - Call the simpler (hence more efficient) version of the method.
                ((StructureImpl) structure).createSimple(slideToken, subject, resourcePath);
                
                NodeRevisionDescriptor revisionDescriptor =
                    new NodeRevisionDescriptor(req.getContentLength());
                
                ResourceInfo resourceInfo =
                    new ResourceInfo(resourcePath, revisionDescriptor);
                resourceInfo.exists = false;
                
                // Checking If headers
                if (!checkIfHeaders(req, resp, resourceInfo)) {
                    int statusCode = WebdavStatus.SC_PRECONDITION_FAILED;
                    sendError( statusCode, "Check If Header failed" );
                    throw new WebdavException( statusCode );
                }
                
                // Resource type
                revisionDescriptor.setResourceType("");
                
                // Get content type
                String contentType = req.getContentType();
                if (contentType == null) {
                    contentType = getConfig().getServletContext()
                        .getMimeType(resourcePath);
                }
                if (contentType == null) {
                    contentType = getConfig().getDefaultMimeType();
                }
                revisionDescriptor.setContentType(contentType);
                
                // Last modification date
                revisionDescriptor.setLastModified(new Date());

                // Etag generation
                revisionDescriptor.setETag(PropertyHelper.computeEtag(resourcePath, revisionDescriptor));
                
                // Creation date
                revisionDescriptor.setCreationDate(new Date());
                
                // Owner
                String creator = ((SubjectNode)security.getPrincipal(slideToken)).getPath().lastSegment();
                revisionDescriptor.setCreationUser(creator);
                revisionDescriptor.setOwner(creator);
                
                // Added for DeltaV --start--
                if( Configuration.useVersionControl() ) {
                    // Workspace
                    versioningHelper.setWorkspaceProperty( resourcePath, revisionDescriptor );
                }
                // Added for DeltaV --end--
                
                // create default property values of the new resource 
                List defaultValues = propertyHelper.createInitialProperties(
                        ResourceKindManager.determineResourceKind(
                                this.token, resourcePath, revisionDescriptor),
                                resourcePath);
                for(int i = 0, l = defaultValues.size(); i < l; i++) {
                    NodeProperty property = (NodeProperty)defaultValues.get(i);
                    revisionDescriptor.setProperty(property);
                }
                
                // Creating revisionDescriptor associated with the object
                if (revisionContent == null) {
                    revisionContent = new NodeRevisionContent();
                    revisionContent.setContent(req.getInputStream());
                    
                    // Get content length
                    int contentLength = req.getContentLength();
                    // we need to buffer the content to find out the content length
                    // and to save it for a retry
                    if (contentLength == -1 || retryUponConflict) {
                        long longContentLength = revisionContent.bufferContent(revisionContent
                                .streamContent());
                        revisionDescriptor.setContentLength(longContentLength);
                    } else {
                        revisionDescriptor.setContentLength(contentLength);
                    }
                }
                
                // 9/20/06 JK - Call the simpler (hence more efficient) version of the method.
                ((ContentImpl) content).createSimple(slideToken, resourcePath, revisionDescriptor,
                               revisionContent);
                
                // check if the resource should be put under version-control
                if ( Configuration.useVersionControl() && isAutoVersionControl(resourcePath) && !isExcludedForVersionControl(resourcePath) ) {
                    versioningHelper.versionControl(resourcePath);
                }
                
                if (WebdavEvent.PUT_AFTER.isEnabled()) {
                    EventDispatcher.getInstance().fireVetoableEvent(WebdavEvent.PUT_AFTER,
                            new WebdavEvent(this));
                }
                
                // ETag header
                resp.setHeader("ETag", revisionDescriptor.getETag() );
                
                resp.setStatus(WebdavStatus.SC_CREATED);
                
            }
            // clean up
            revisionContent = null;
        }
        catch (PreconditionViolationException e) {
            sendPreconditionViolation(e);
            throw e;
        }
        catch (SlideException e) {
            int statusCode = getErrorCode( e );
            sendError( statusCode, e );
            throw new WebdavException( statusCode );
        }
        catch (Exception e) {
            int statusCode = getErrorCode( e );
            sendError( statusCode, e );
            throw new WebdavException( statusCode );
        }
        
    }
    
    /**
     * Checks the (DeltaV) preconditions
     * <ul>
     * <li>&lt;DAV:cannot-modify-version-controlled-content&gt;</li>
     * <li>&lt;DAV:cannot-modify-version&gt;</li>
     * </ul>
     *
     * @param      revisionDescriptors  the NodeRevisionDescriptors of the resource
     *                                  to perform the <code>PUT</code> on.
     * @param      revisionDescriptor  the NodeRevisionDescriptor of the resource
     *                                 to perform the <code>PUT</code> on.
     * @param      resourceKind         the ResourceKind of the resource.
     *
     * @return     the precondition that has been violated (if any).
     */
    private ViolatedPrecondition getPreconditionViolation(NodeRevisionDescriptors revisionDescriptors, NodeRevisionDescriptor revisionDescriptor, ResourceKind resourceKind)
        throws ServiceAccessException {
        
        if( Configuration.useVersionControl() ) {
            
            if (resourceKind instanceof CheckedInVersionControlled) {
                
                // check precondition DAV:cannot-modify-version-controlled-content
                String autoVersion = versioningHelper.getAutoVersionElementName(revisionDescriptor);
                if (autoVersion == null) {
                    autoVersion = "";
                }
                if ( !E_CHECKOUT_CHECKIN.equals(autoVersion) &&
                    !E_CHECKOUT_UNLOCKED_CHECKIN.equals(autoVersion) &&
                    !E_CHECKOUT.equals(autoVersion) &&
                    !E_CHECKOUT_IGNORE_UNLOCK.equals(autoVersion) &&
                    !E_LOCKED_CHECKOUT.equals(autoVersion) ) {
                    return new ViolatedPrecondition(C_CANNOT_MODIFY_VERSION_CONTROLLED_CONTENT,
                                                    WebdavStatus.SC_FORBIDDEN);
                }
                if ( E_LOCKED_CHECKOUT.equals(autoVersion) &&
                        ( !versioningHelper.isWriteLocked(slideToken, revisionDescriptors) ) ) {
                    return new ViolatedPrecondition(C_CANNOT_MODIFY_VERSION_CONTROLLED_CONTENT,
                                                    WebdavStatus.SC_FORBIDDEN);
                }
            }
            
            // check precondition DAV:cannot-modify-version
            UriHandler uriHandler = UriHandler.getUriHandler(resourcePath);
            if (uriHandler.isVersionUri()) {
                return new ViolatedPrecondition(C_CANNOT_MODIFY_VERSION,
                                                WebdavStatus.SC_FORBIDDEN);
            }
        }
        return null;
    }
    
    
    
    
    
    
    /**
     * Get return status based on exception type.
     */
    protected int getErrorCode(SlideException ex) {
        try {
            throw ex;
        } catch (RevisionAlreadyExistException e) {
            return WebdavStatus.SC_CONFLICT;
        } catch (ObjectAlreadyExistsException e) {
            return WebdavStatus.SC_CONFLICT;
        } catch (ObjectNotFoundException e) {
            return WebdavStatus.SC_CONFLICT;
        } catch (LinkedObjectNotFoundException e) {
            return WebdavStatus.SC_NOT_FOUND;
        } catch (SlideException e) {
            return super.getErrorCode(e);
        } catch (Exception e) {
            return super.getErrorCode(e);
        }
    }
}




