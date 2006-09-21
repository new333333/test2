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

import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideException;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.event.EventDispatcher;
import org.apache.slide.structure.LinkedObjectNotFoundException;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.SubjectNode;
import org.apache.slide.util.Configuration;
import org.apache.slide.webdav.WebdavException;
import org.apache.slide.webdav.WebdavServletConfig;
import org.apache.slide.webdav.event.WebdavEvent;
import org.apache.slide.webdav.util.DeltavConstants;
import org.apache.slide.webdav.util.PropertyHelper;
import org.apache.slide.webdav.util.UriHandler;
import org.apache.slide.webdav.util.WebdavStatus;
import org.apache.slide.webdav.util.resourcekind.ResourceKindManager;

/**
 * MKCOL method.
 *
 */
public class MkcolMethod
    extends AbstractWebdavMethod
    implements DeltavConstants, WriteMethod, FineGrainedLockingMethod {
    
    
    // ----------------------------------------------------- Instance Variables
    
    
    /**
     * Collection name.
     */
    protected String colName;
    
    protected PropertyHelper propertyHelper = null;
    
    /**
     * Constructor.
     *
     * @param token     the token for accessing the namespace
     * @param config    configuration of the WebDAV servlet
     */
    public MkcolMethod(NamespaceAccessToken token,
                       WebdavServletConfig config) {
        super(token, config);
    }
    
    
    /**
     * @see org.apache.slide.webdav.method.FineGrainedLockingMethod#acquireFineGrainLocks()
     */
    public void acquireFineGrainLocks() {
        acquireStandardLocks(colName);
        // changes this and parent
        acquireLock(colName, WRITE_LOCK);
        acquireParentLock(colName, WRITE_LOCK);
    }

    // ------------------------------------------------------ Protected Methods
    
    
    /**
     * Parse XML request.
     */
    protected void parseRequest()
        throws WebdavException {
        
        propertyHelper = PropertyHelper.getPropertyHelper(slideToken, token, 
                getConfig());
        
        if (req.getContentLength() > 0) {
            int statusCode = WebdavStatus.SC_UNSUPPORTED_MEDIA_TYPE;
            sendError( statusCode, getClass().getName()+".requestBodyMustBeEmpty" );
            throw new WebdavException( statusCode );
        }
        
        colName = requestUri;
        if (colName == null) {
            colName = "/";
        }
    }
    
    
    /**
     * Execute request.
     *
     * @exception WebdavException Bad request
     */
    protected void executeRequest() throws WebdavException, IOException {
        
        // Prevent dirty reads
        slideToken.setForceStoreEnlistment(true);
        NodeRevisionDescriptor revisionDescriptor = null;
        
        // check lock-null resources
        boolean isLockNull = false;
        try {
            revisionDescriptor =
                content.retrieve(slideToken, content.retrieve(slideToken, colName));
            isLockNull = isLockNull( revisionDescriptor );
        }
        catch (ServiceAccessException e) {
            int statusCode = getErrorCode((Exception)e);
            sendError( statusCode, e );
            throw new WebdavException( statusCode );
        }
        catch (SlideException e) {
            // ignore silently
        }
        
        if (revisionDescriptor == null) {
            revisionDescriptor =
                new NodeRevisionDescriptor(0);
        }
        
        // check destination URI
        UriHandler destinationUriHandler = UriHandler.getUriHandler(colName);
        if (destinationUriHandler.isRestrictedUri()) {
            int statusCode = WebdavStatus.SC_FORBIDDEN;
            sendError( statusCode, getClass().getName()+".restrictedDestinationUri", new Object[]{colName} );
            throw new WebdavException( statusCode );
        }
        
        // Resource type
        revisionDescriptor.setResourceType(NodeRevisionDescriptor.COLLECTION_TYPE);
        
        // Creation date
        revisionDescriptor.setCreationDate(new Date());
        
        // Last modification date
        revisionDescriptor.setLastModified(new Date());
        
        // Content length name
        revisionDescriptor.setContentLength(0);
        
        // Owner
        try {
            String creator = ((SubjectNode)security.getPrincipal(slideToken)).getPath().lastSegment();
            revisionDescriptor.setCreationUser(creator);
            revisionDescriptor.setOwner(creator);
        } catch (Exception e) {
            int statusCode = getErrorCode( e );
            sendError( statusCode, e );
            throw new WebdavException( statusCode );
        }
        
        // Added for DeltaV --start--
        if( Configuration.useVersionControl() ) {
            // Workspace
            versioningHelper.setWorkspaceProperty(colName, revisionDescriptor);
        }
        // Added for DeltaV --end--
        
        // create default property values of the new collection
        List defaultValues = propertyHelper.createInitialProperties(
                ResourceKindManager.determineResourceKind(
                        this.token, colName, revisionDescriptor),
                        colName);
        for(int i = 0, l = defaultValues.size(); i < l; i++) {
            NodeProperty property = (NodeProperty)defaultValues.get(i);
            revisionDescriptor.setProperty(property);
        }
        
        // If everything is ok : 201 - Created / OK
        resp.setStatus(WebdavStatus.SC_CREATED);
        
        try {
            if ( WebdavEvent.MKCOL.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(WebdavEvent.MKCOL, new WebdavEvent(this));

            if (!isLockNull) {
                SubjectNode collection = new SubjectNode();
                structure.create(slideToken, collection, colName);
                content.create(slideToken, colName, revisionDescriptor, null);
            }
            else {
                content.store(slideToken, colName, revisionDescriptor, null);
            }
            
            if (WebdavEvent.MKCOL_AFTER.isEnabled()) {
                EventDispatcher.getInstance().fireVetoableEvent(WebdavEvent.MKCOL_AFTER,
                        new WebdavEvent(this));
            }
        } catch (Exception e) {
            int statusCode = getErrorCode( e );
            sendError( statusCode, e );
            throw new WebdavException( statusCode );
        }
        
        // 415 - Unsupported Media Type
        // TODO : Has something to do with the body of the request ...
        // WebDAV RFC is vague on the subject.
        
        // 507 - Insufficient storage
        // Would be returned as a ServiceAccessException, so it would
        // return an Internal Server Error, which is probably acceptable.
        
        // TODO : Initialize and create collection's properties.
        
    }
    
    
    /**
     * Get return status based on exception type.
     */
    protected int getErrorCode(Exception ex) {
        try {
            throw ex;
        } catch (ObjectNotFoundException e) {
            return WebdavStatus.SC_CONFLICT;
        } catch (LinkedObjectNotFoundException e) {
            return WebdavStatus.SC_NOT_FOUND;
        } catch (ObjectAlreadyExistsException e) {
            return WebdavStatus.SC_METHOD_NOT_ALLOWED;
        } catch (Exception e) {
            return super.getErrorCode(e);
        }
        
    }
    
}

