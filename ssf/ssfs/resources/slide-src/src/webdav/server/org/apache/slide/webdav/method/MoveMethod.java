/*
 * $Header$
 * $Revision: 208545 $
 * $Date: 2005-02-25 12:07:59 -0500 (Fri, 25 Feb 2005) $
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
import java.util.Iterator;

import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideException;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.content.RevisionNotFoundException;
import org.apache.slide.content.NodeProperty.NamespaceCache;
import org.apache.slide.event.EventDispatcher;
import org.apache.slide.macro.CopyListener;
import org.apache.slide.macro.DeleteListener;
import org.apache.slide.macro.Macro;
import org.apache.slide.macro.MacroException;
import org.apache.slide.macro.MacroParameters;
import org.apache.slide.search.RequestedResource;
import org.apache.slide.search.Search;
import org.apache.slide.search.SearchQuery;
import org.apache.slide.search.SearchQueryResult;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.util.Configuration;
import org.apache.slide.webdav.WebdavException;
import org.apache.slide.webdav.WebdavServletConfig;
import org.apache.slide.webdav.event.DetailedWebdavEvent;
import org.apache.slide.webdav.event.WebdavEvent;
import org.apache.slide.webdav.util.DaslConstants;
import org.apache.slide.webdav.util.DeltavConstants;
import org.apache.slide.webdav.util.PreconditionViolationException;
import org.apache.slide.webdav.util.PropertyHelper;
import org.apache.slide.webdav.util.UriHandler;
import org.apache.slide.webdav.util.VersioningHelper;
import org.apache.slide.webdav.util.ViolatedPrecondition;
import org.apache.slide.webdav.util.WebdavStatus;
import org.apache.slide.webdav.util.resourcekind.AbstractResourceKind;
import org.apache.slide.webdav.util.resourcekind.CheckedOutVersionControlled;
import org.apache.slide.webdav.util.resourcekind.DeltavCompliantUnmappedUrl;
import org.apache.slide.webdav.util.resourcekind.ResourceKind;
import org.apache.slide.webdav.util.resourcekind.VersionControlled;
import org.apache.slide.webdav.util.resourcekind.Working;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * MOVE Method.
 *
 */
public class MoveMethod extends AbstractMultistatusResponseMethod implements DeltavConstants, DeleteListener, CopyListener, WriteMethod {

    /**
     * The PropertyHelper used by this instance.
     */
    protected PropertyHelper propertyHelper = null;

    /**
     * Indicates if the source of the MOVE request is a workspace.
     */
    protected boolean isRequestSourceWorkspace = false;

    /**
     * Indicates if the source of the current copy operation is a VCR.
     * Used by {@link #beforeCopy beforeCopy()} and {@link #afterCopy afterCopy()}.
     */
    protected boolean isCopySourceVCR = false;

    /**
     * The Element returned by {@link #getWorkingResourceSearchElement
     * getWorkingResourceSearchElement()}.
     */
    protected Element basicSearch = null;

    /**
     * The &lt;literal&gt; Element used in the basic search query returned by
     * {@link #getWorkingResourceSearchElement getWorkingResourceSearchElement()}.
     */
    protected Element literal = null;

    private boolean destinationExistsBefore = false;

    // ----------------------------------------------------------- Constructors


    /**
     * Constructor.
     *
     * @param token     the token for accessing the namespace
     * @param config    configuration of the WebDAV servlet
     */
    public MoveMethod(NamespaceAccessToken token, WebdavServletConfig config) {
        super(token, config);
    }

    /**
     * @see org.apache.slide.webdav.method.FineGrainedLockingMethod#acquireFineGrainLocks()
     */
    public void acquireFineGrainLocks() {
        acquireStandardLocks(destinationUri);
        acquireStandardLocks(sourceUri);
        // changes source and destination plus parents
        acquireLock(destinationUri, WRITE_LOCK); // effectively means recursive write locks 
        acquireParentLock(destinationUri, WRITE_LOCK);
        acquireLock(sourceUri, WRITE_LOCK);
        acquireParentLock(sourceUri, WRITE_LOCK);
    }

    // ------------------------------------------------------ Protected Methods



    /**
     * Execute request.
     *
     * @exception WebdavException Unrecoverable error while moving the files
     */
    protected void executeRequest()
        throws WebdavException, IOException {

        propertyHelper = PropertyHelper.getPropertyHelper(slideToken, token, getConfig());

        // Prevent dirty reads
        slideToken.setForceStoreEnlistment(true);

        // check lock-null resources
        try {
            if (isLockNull(sourceUri)) {
                int statusCode = WebdavStatus.SC_NOT_FOUND;
                sendError( statusCode, "lock-null resource", new Object[]{sourceUri} );
                throw new WebdavException( statusCode );
            } else if (isLocked(sourceUri)) {
                int statusCode = WebdavStatus.SC_LOCKED;
                sendError( statusCode, getClass().getName()+".noLocked", new Object[]{sourceUri} );
                throw new WebdavException( statusCode );
            }
        }
        catch (ServiceAccessException e) {
            int statusCode = getErrorCode((Exception)e);
            sendError( statusCode, e );
            throw new WebdavException( statusCode );
        }

        int depth = requestHeaders.getDepth(INFINITY);
        if (depth < INFINITY) {
            int sc = WebdavStatus.SC_PRECONDITION_FAILED;
            sendError( sc, "Invalid header Depth: "+depth );
            throw new WebdavException( sc );
        }
        
        MacroParameters macroParameters = null;
        boolean isCollection = isCollection(sourceUri);

        if (overwrite) {
            macroParameters = Macro.RECURSIVE_OVERWRITE_PARAMETERS;
        } else {
            macroParameters = Macro.DEFAULT_PARAMETERS;
        }

        // check destination URI
        UriHandler destinationUriHandler = UriHandler.getUriHandler(destinationUri);
        if (destinationUriHandler.isRestrictedUri()) {
            int statusCode = WebdavStatus.SC_FORBIDDEN;
            sendError( statusCode, getClass().getName()+".restrictedDestinationUri", new Object[]{destinationUri} );
            throw new WebdavException( statusCode );
        }

        UriHandler sourceUriHandler = UriHandler.getUriHandler(sourceUri);
        isRequestSourceWorkspace = sourceUriHandler.isWorkspaceUri();

        try {
            // check preconditions
        	// 9/21/06 JK - Since SSFS does not support MOVE operation (except for
        	// renaming which is a special case of MOVE) within Aspen data hierarchy
        	// for both entries and binders, we do not have to check for precondition
        	// violation (eg. seeing whether the target's parent is a descendent of
        	// the source which results in cyclic reference, etc.). 
        	/*
            ViolatedPrecondition violatedPrecondition = getPreconditionViolation(sourceUri, destinationUri);
            if (violatedPrecondition != null) {
                PreconditionViolationException e =
                    new PreconditionViolationException(violatedPrecondition, sourceUri);
                sendPreconditionViolation(e);
                throw e;
            }
            */

            destinationExistsBefore = exists(destinationUri);
            if (!overwrite && destinationExistsBefore) {
                int statusCode = WebdavStatus.SC_PRECONDITION_FAILED;
                sendError( statusCode, getClass().getName()+".noOverwrite", new Object[]{destinationUri} );
                throw new WebdavException( statusCode );
            }
            if ( WebdavEvent.MOVE.isEnabled() ) EventDispatcher.getInstance().fireVetoableEvent(WebdavEvent.MOVE, new WebdavEvent(this));

            macro.move(slideToken, sourceUri, destinationUri, macroParameters, null, this, null, this);

            if (overwrite && destinationExistsBefore) {
                resp.setStatus(WebdavStatus.SC_NO_CONTENT);
            } else {
                resp.setStatus(WebdavStatus.SC_CREATED);
            }
        } catch (MacroException e) {
            if(generateMultiStatusResponse(isCollection, e, requestUri)) {
                String errorMessage = generateErrorMessage(e);
                // Write it on the servlet writer
                resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
                try {
                    resp.setContentType(TEXT_XML_UTF_8);
                    resp.getWriter().write(errorMessage);
                } catch(IOException ex) {
                    // Critical error ... Servlet container is dead or something
                    int statusCode = WebdavStatus.SC_INTERNAL_SERVER_ERROR;
                    sendError( statusCode, e );
                    throw new WebdavException( statusCode );
                }
            } else {
                // Returning 207 on non-collection requests is generally
                // considered bad. So let's not do it, since this way
                // makes clients generally behave better.
                SlideException exception = (SlideException)e.enumerateExceptions().nextElement();
                if (exception instanceof PreconditionViolationException) {
                    try {
                        sendPreconditionViolation((PreconditionViolationException)exception);
                    } catch(IOException ex) {
                        // Critical error ... Servlet container is dead or something
                        int statusCode = WebdavStatus.SC_INTERNAL_SERVER_ERROR;
                        sendError( statusCode, e );
                        throw new WebdavException( statusCode );
                    }
                }
                else {
                    int statusCode = getErrorCode( exception );
                    sendError( statusCode, exception );
                    throw new WebdavException( statusCode );
                }
            }
            //
            // make sure the transaction is aborted
            // throw any WebDAV exception to indicate the transaction wants to be aborted
            //
            throw new WebdavException(WebdavStatus.SC_ACCEPTED, false);
        }
        catch (SlideException e) {
            int statusCode = getErrorCode( e );
            sendError( statusCode, e );
            throw new WebdavException( statusCode );
        }
    }


    /**
     * Get return status based on exception type.
     */
    protected int getErrorCode(SlideException ex) {
        try {
            throw ex;
        } catch(RevisionNotFoundException e) {
            return WebdavStatus.SC_NOT_FOUND;
        } catch (SlideException e) {
            return super.getErrorCode(e);
        }
    }

    /**
     * Checks the (DeltaV) preconditions
     * <ul>
     * <li>&lt;DAV:resource-must-be-null&gt;</li>
     * <li>&lt;DAV:workspace-location-ok&gt;</li>
     * </ul>
     *
     * @param      sourceUri       the URI of the resource.
     * @param      destinationUri  the URI of the resource.
     *
     * @return     the precondition that has been violated (if any).
     *
     * @throws     SlideException
     */
    private ViolatedPrecondition getPreconditionViolation(String sourceUri, String destinationUri) throws SlideException {

        ViolatedPrecondition violatedPrecondition = null;
        if( Configuration.useVersionControl() ) {

            if (isRequestSourceWorkspace) {

                UriHandler destinationUriHandler = UriHandler.getUriHandler(destinationUri);
                NodeRevisionDescriptors destinationRevisionDescriptors = null;
                NodeRevisionDescriptor destinationRevisionDescriptor = null;
                try {
                    destinationRevisionDescriptors = content.retrieve( slideToken, destinationUri);
                    destinationRevisionDescriptor = content.retrieve( slideToken, destinationRevisionDescriptors);
                }
                catch( ObjectNotFoundException e ) {}; // can be ignored here!

                ResourceKind destinationResourceKind =
                    AbstractResourceKind.determineResourceKind( token, destinationUri, destinationRevisionDescriptor );

                if( !(destinationResourceKind instanceof DeltavCompliantUnmappedUrl) ) {
                    return new ViolatedPrecondition(C_RESOURCE_MUST_BE_NULL,
                                                    WebdavStatus.SC_CONFLICT);
                }
                if( !destinationUriHandler.isWorkspaceUri() ) {
                    return new ViolatedPrecondition(C_WORKSPACE_LOCATION_OK,
                                                    WebdavStatus.SC_CONFLICT);
                }
            }
        }
        if (isCollection(sourceUri)) {
            UriHandler destinationUriHandler = UriHandler.getUriHandler(destinationUri);
            ObjectNode destinationParentNode =
                structure.retrieve(slideToken, destinationUriHandler.getParentUriHandler().toString());
            ObjectNode sourceNode =
                structure.retrieve(slideToken, sourceUri);
            if (isDescendant(destinationParentNode, sourceNode)) {
                return new ViolatedPrecondition(C_CYCLE_ALLOWED, WebdavStatus.SC_FORBIDDEN);
            }
        }

        return violatedPrecondition;
    }

    // ------------------------------------------------------ Interface CopyListener

    /**
     * This method is called prior to copying the resource associated by
     * the given <code>sourceUri</code>. The copy can be prohibited by
     * throwing a SlideException.
     *
     * @param      sourceUri       the Uri of the resource that will be copied.
     * @param      destinationUri  the Uri of the copy.
     *
     * @throws     SlideException  this Exception will be passed to the caller
     *                             of the Macro helper (contained in the
     *                             MacroDeleteException.
     */
    public void beforeCopy(String sourceUri, String destinationUri, boolean isRootOfCopy) throws SlideException {

        if (DetailedWebdavEvent.MOVE_BEFORE_COPY.isEnabled()) {
            EventDispatcher.getInstance().fireVetoableEvent(
                    DetailedWebdavEvent.MOVE_BEFORE_COPY,
                    new DetailedWebdavEvent(this, destinationUri, sourceUri));
        }
        
        if( Configuration.useVersionControl() ) {

            UriHandler uriHandler = UriHandler.getUriHandler(sourceUri);
            if (uriHandler.isVersionUri()) {
                throw new PreconditionViolationException(
                        new ViolatedPrecondition(
                                DeltavConstants.C_CANNOT_RENAME_VERSION,
                                WebdavStatus.SC_FORBIDDEN), sourceUri);
            }
            if (uriHandler.isHistoryUri()) {
                throw new PreconditionViolationException(
                        new ViolatedPrecondition(
                                DeltavConstants.C_CANNOT_RENAME_HISTORY,
                                WebdavStatus.SC_FORBIDDEN), sourceUri);
            }
            if (uriHandler.isWorkingresourceUri()) {
                throw new PreconditionViolationException(
                        new ViolatedPrecondition(
                                DeltavConstants.C_CANNOT_RENAME_WORKING_RESOURCE,
                                WebdavStatus.SC_FORBIDDEN), sourceUri);
            }

            NodeRevisionDescriptors sourceNrds = content.retrieve(slideToken, sourceUri);
            NodeRevisionDescriptor sourceNrd = content.retrieve(slideToken, sourceNrds);
            ResourceKind resourceKind = AbstractResourceKind.determineResourceKind(
                    token, sourceNrds, sourceNrd);
            isCopySourceVCR = (resourceKind instanceof VersionControlled);

            // if resource being moved is a checked-out VCR or a WR,
            // update its URI in the DAV:checkout-set property of the VR
            if(resourceKind instanceof CheckedOutVersionControlled || resourceKind instanceof Working) {
                String vrUri = VersioningHelper.getUriOfAssociatedVR(token, 
                        slideToken, content, sourceUri);
                NodeRevisionDescriptors vrNrds = content.retrieve(slideToken, vrUri);
                NodeRevisionDescriptor vrNrd = content.retrieve(slideToken, vrNrds);
                try {
                    PropertyHelper.removeHrefFromProperty(vrNrd, P_CHECKOUT_SET, sourceUri);
                    PropertyHelper.addHrefToProperty(vrNrd, P_CHECKOUT_SET, destinationUri);
                    content.store(slideToken, vrNrds.getUri(), vrNrd, null);
                }
                catch (JDOMException e) {
                    throw new SlideException(
                        "Unable to update DAV:checkout-set of "+vrUri+": "+e.getMessage() );
                }
            }
        }
    }

    /**
     * This method is called after copying the resource to
     * the given <code>destinationUri</code>.
     *
     * @param      sourceUri       the Uri of the resource that has been copied.
     * @param      destinationUri  the Uri of the copy.
     *
     * @throws     SlideException  this Exception will be passed to the caller
     *                             of the Macro helper (contained in the
     *                             MacroDeleteException.
     */
    public void afterCopy(String sourceUri, String destinationUri, boolean isRootOfCopy, boolean destinationExists) throws SlideException {

        if( Configuration.useVersionControl() ) {

            NodeRevisionDescriptors sourceNrds =
                content.retrieve( slideToken, sourceUri);
            NodeRevisionDescriptors destinationNrds =
                content.retrieve( slideToken, destinationUri);
            NodeRevisionDescriptor destinationNrd =
                content.retrieve( slideToken, destinationNrds );

            // copy DeltaV-specific "0.0" revision if exists
            try {
                NodeRevisionDescriptor sourceNrd00 =
                    content.retrieve( slideToken, sourceNrds, NodeRevisionNumber.HIDDEN_0_0 );
                NodeRevisionDescriptor destinationNrd00 = sourceNrd00.cloneObject();

                try {
                    content.retrieve( slideToken, destinationNrds, NodeRevisionNumber.HIDDEN_0_0 );
                    content.store( slideToken, destinationUri, destinationNrd00, null ); // revisionContent=null
                }
                catch( RevisionDescriptorNotFoundException x ) {
                    content.create( slideToken, destinationUri, null, destinationNrd00, null ); // branch=null, revisionContent=null
                }
            }
            catch (ServiceAccessException e) {
                throw e;
            }
            catch (SlideException e) {}

            handleWorkspacePostconditions(destinationNrd, destinationUri);
            handleWorkingResourcePostconditions(sourceUri, destinationUri);
            
        }
        
        if (DetailedWebdavEvent.MOVE_AFTER_COPY.isEnabled()) {
            EventDispatcher.getInstance().fireVetoableEvent(
                    DetailedWebdavEvent.MOVE_AFTER_COPY,
                    new DetailedWebdavEvent(this, destinationUri, sourceUri));
        }
    }

    /**
     * Handles the working resource postconditions.
     * <ul>
     * <li>DAV:update-auto-update</li>
     * </ul>
     *
     * @param      sourceUri       the Uri of the resource that has been copied.
     * @param      destinationUri  the Uri of the copy.
     *
     * @throws     SlideException
     */
    private void handleWorkingResourcePostconditions(String sourceUri, String destinationUri) throws SlideException {

        if (isCopySourceVCR) {

            Element basicSearch = getWorkingResourceSearchElement(sourceUri);
            String grammarNamespace = basicSearch.getNamespaceURI();
            Search searchHelper = token.getSearchHelper();
            
            SearchQuery searchQuery = searchHelper.createSearchQuery(
                    grammarNamespace,
                    basicSearch,
                    slideToken,
                    Integer.MAX_VALUE,
                    getSlideContextPath() + sourceUri);

            SearchQueryResult queryResult = searchHelper.search(slideToken, searchQuery);

            Iterator queryResultIterator = queryResult.iterator();
            RequestedResource requestedResource = null;
            NodeRevisionDescriptors workingResourceRevisionDescriptors = null;
            NodeRevisionDescriptor workingResourceRevisionDescriptor = null;
            NodeProperty autoUpdateProperty = null;
            String workingResourceUri = null;

            while (queryResultIterator.hasNext()) {

                requestedResource = (RequestedResource)queryResultIterator.next();
                workingResourceUri = requestedResource.getUri();
                workingResourceRevisionDescriptors =
                    content.retrieve( slideToken, workingResourceUri);
                workingResourceRevisionDescriptor =
                    content.retrieve( slideToken, workingResourceRevisionDescriptors);
                autoUpdateProperty = new NodeProperty(PN_AUTO_UPDATE,
                                                      propertyHelper.createHrefValue(destinationUri));
                workingResourceRevisionDescriptor.setProperty(autoUpdateProperty);
                content.store(slideToken,
                              workingResourceRevisionDescriptors.getUri(),
                              workingResourceRevisionDescriptor,
                              null);
            }
        }
    }

    /**
     * Returns the query document used to search all resources that have
     * a &lt;auto-update&gt; property with a &lt;href&gt; value containing
     * the given <code>resourcePath</code>.
     *
     * @param      resourcePath  the Uri to search for.
     *
     * @return     the query document.
     */
    protected Element getWorkingResourceSearchElement(String resourcePath) {

        if (basicSearch == null) {
            basicSearch = new Element(DaslConstants.E_BASICSEARCH,
                                      DNSP);

            Element select = new Element(DaslConstants.E_SELECT,
                                         DNSP);
            basicSearch.addContent(select);
            Element prop = new Element(E_PROP,
                                       DNSP);
            select.addContent(prop);
            Element autoUpdate = new Element(P_AUTO_UPDATE,
                                             DNSP);
            prop.addContent(autoUpdate);

            Element from = new Element(DaslConstants.E_FROM,
                                       DNSP);
            basicSearch.addContent(from);
            Element scope = new Element(DaslConstants.E_SCOPE,
                                        DNSP);
            from.addContent(scope);
            Element href = new Element(E_HREF,
                                       DNSP);
            scope.addContent(href);
            href.setText("");

            Iterator excludeIterator = propertyHelper.getNonVcrPathExcludeList().iterator();
            while (excludeIterator.hasNext()) {
                scope.addContent((Element)excludeIterator.next());
            }

            Element where = new Element(DaslConstants.E_WHERE,
                                        DNSP);
            basicSearch.addContent(where);
            Element propcontains = new Element(DaslConstants.E_PROPCONTAINS,
                                               NamespaceCache.SLIDE_NAMESPACE);
            where.addContent(propcontains);
            propcontains.addContent((Element)prop.clone());
            literal = new Element(DaslConstants.E_LITERAL,
                                  DNSP);
            propcontains.addContent(literal);
        }
        literal.setText(resourcePath);

        return basicSearch;
    }

    /**
     * Handles the workspace postconditions
     * <ul>
     * <li>DAV:workspace-moved</li>
     * <li>DAV:workspace-member-moved</li>
     * </ul>
     *
     * @param      revisionDescriptor  the NodeRevisionDescriptor of the resource.
     * @param      resourceUri         the URI of the resource.
     *
     * @throws     SlideException
     */
    protected void handleWorkspacePostconditions(NodeRevisionDescriptor revisionDescriptor, String resourceUri) throws SlideException {

        if( Configuration.useVersionControl() ) {

            if (isRequestSourceWorkspace) {
                // DAV:workspace-moved
                revisionDescriptor.setProperty(
                    new NodeProperty( PN_WORKSPACE, propertyHelper.createHrefValue(this.destinationUri)) );
            }
            else {
                // DAV:workspace-member-moved
                versioningHelper.setWorkspaceProperty(resourceUri, revisionDescriptor);
            }
            content.store(slideToken, resourceUri, revisionDescriptor, null);
        }
    }


    // ------------------------------------------------------ Interface DeleteListener

    /**
     * This method is called prior to deleting the resource associated by
     * the given <code>targetUri</code>. The deletion can be prohibited by
     * throwing a SlideException.
     *
     * @param      targetUri       the Uri of the resource that will be deleted.
     *
     * @throws     SlideException  this Exception will be passed to the caller
     *                             of the Macro helper (contained in the
     *                             MacroDeleteException.
     */
    public void beforeDelete(String targetUri) throws SlideException {
        if (DetailedWebdavEvent.MOVE_BEFORE_DELETE.isEnabled()) {
            EventDispatcher.getInstance().fireVetoableEvent(
                    DetailedWebdavEvent.MOVE_BEFORE_DELETE,
                    new DetailedWebdavEvent(this, targetUri));
        }
    }

    /**
     * This method is called after deleting the resource associated by
     * the given <code>targetUri</code>.
     *
     * @param      targetUri       the Uri of the resource that will be deleted.
     *
     * @throws     SlideException  this Exception will be passed to the caller
     *                             of the Macro helper (contained in the
     *                             MacroDeleteException.
     */
    public void afterDelete(String targetUri) throws SlideException {
        if (DetailedWebdavEvent.MOVE_AFTER_DELETE.isEnabled()) {
            EventDispatcher.getInstance().fireVetoableEvent(
                    DetailedWebdavEvent.MOVE_AFTER_DELETE,
                    new DetailedWebdavEvent(this, targetUri));
        }
    }
}


