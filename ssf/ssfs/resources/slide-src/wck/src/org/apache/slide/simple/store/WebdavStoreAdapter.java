/*
 * $Header$
 * $Revision: 226342 $
 * $Date: 2005-07-29 06:30:52 -0400 (Fri, 29 Jul 2005) $
 *
 * ====================================================================
 *
 * Copyright 2004 The Apache Software Foundation
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

package org.apache.slide.simple.store;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

import javax.transaction.xa.*;

import org.apache.commons.transaction.util.xa.AbstractTransactionalResource;
import org.apache.commons.transaction.util.xa.TransactionalResource;
import org.apache.slide.authenticate.CredentialsToken;
import org.apache.slide.common.*;
import org.apache.slide.lock.LockTokenNotFoundException;
import org.apache.slide.lock.NodeLock;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.UnauthenticatedException;
import org.apache.slide.simple.authentication.JAASLoginModule;
import org.apache.slide.simple.reference.WebdavFileStore;
import org.apache.slide.store.*;
import org.apache.slide.content.*;
import org.apache.slide.structure.*;
import org.apache.slide.util.logger.*;

/**
 * Driving adapter for call back interfaces {@link BasicWebdavStore}and
 * extensions.
 * 
 * @see BasicWebdavStore
 * @see WebdavStoreLockExtension
 * @see WebdavStoreBulkPropertyExtension
 * @see WebdavStoreSinglePropertyExtension
 * @see WebdavFileStore
 * @version $Revision: 226342 $
 */
public class WebdavStoreAdapter extends AbstractXAServiceBase implements Service, ContentStore, NodeStore, LockStore,
        RevisionDescriptorStore, RevisionDescriptorsStore {

    protected static final String LOG_CHANNEL = WebdavStoreAdapter.class.getName();

    protected static final String CALLBACK_PARAMETER = "callback-store";
    protected static final String CALLBACK_FACTORY_PARAMETER = "callback-factory";

    protected static final String PROPERTIES_PARAMETER = "store-properties";

    protected static ThreadLocal credentials = new ThreadLocal();

    protected static String getNamespacedPropertyName(String namespace, String propertyName) {
        String result;
        if (namespace == null) {
            result = propertyName;
        } else {
            if (namespace.endsWith(":")) {
                result = namespace + propertyName;
            } else {
                result = namespace + ":" + propertyName;
            }
        }
        return result;
    }

    protected Hashtable parameters;

    protected String callBackFactoryClassName;
    
    protected BasicWebdavStoreFactory storeFactory;
    
    protected boolean isCopySupported, isMoveSupported, isDeleteSupported;

    // ==== Service Methods ================================

    public void setParameters(Hashtable parameters) throws ServiceParameterErrorException,
            ServiceParameterMissingException {
        log("setParameters(" + parameters + ")");
        this.parameters = parameters;

        Class factoryClass;

        String callBackClassName = (String) parameters.get(CALLBACK_PARAMETER);
        if (callBackClassName != null) {
            try {
                final Class storeClass = Class.forName(callBackClassName);
                storeFactory = new BasicWebdavStoreFactory() {

                    public BasicWebdavStore newInstance() throws Exception {
                        return (BasicWebdavStore) storeClass.newInstance();
                    }

                };
            } catch (Exception e) {
                getLogger().log("Initialize call back store " + callBackClassName, e,
                        LOG_CHANNEL, Logger.CRITICAL);
                throw new ServiceParameterErrorException(this, CALLBACK_FACTORY_PARAMETER);
            }

        } else {
            callBackFactoryClassName = (String) parameters.get(CALLBACK_FACTORY_PARAMETER);
            if (callBackFactoryClassName == null) {
                throw new ServiceParameterMissingException(this, CALLBACK_FACTORY_PARAMETER);
            }
            try {
                factoryClass = Class.forName(callBackFactoryClassName);
                storeFactory = (BasicWebdavStoreFactory) factoryClass.newInstance();
            } catch (Exception e) {
                getLogger().log("Initialize call back store " + callBackFactoryClassName, e,
                        LOG_CHANNEL, Logger.CRITICAL);
                throw new ServiceParameterErrorException(this, CALLBACK_FACTORY_PARAMETER);
            }
        }
    }

    public void connect(CredentialsToken crdtoken) throws ServiceConnectionFailedException {
        WebdavStoreAdapter.credentials.set(crdtoken);
    }

    public void disconnect() throws ServiceDisconnectionFailedException {
        WebdavStoreAdapter.credentials.set(null);
    }

    public void connect() throws ServiceConnectionFailedException {
    }

    public void reset() throws ServiceResetFailedException {
        credentials.set(null);
    }

    public boolean isConnected() throws ServiceAccessException {
        return credentials.get() != null;
    }

    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }

    public boolean isSameRM(XAResource rm) throws XAException {
        return false;
    }

    // ==== ContentStore Methods ================================

    public NodeRevisionContent retrieveRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor)
            throws ServiceAccessException, RevisionNotFoundException {
        log("retrieveRevisionContent(" + uri + ")");
        // needed for DirectoryIndexGenerator
        TransactionId id = ((TransactionId) getCurrentlyActiveTransactionalResource());
        if (id == null) {
            id = createTransactionResource(uri);
            try {
                return id.retrieveRevisionContent(uri, revisionDescriptor);
            } finally {
                try {
                    id.commit();
                } catch (XAException e) {
                    throw new ServiceAccessException(this, e);
                }
            }
        }
        return id.retrieveRevisionContent(uri, revisionDescriptor);
    }

    public void createRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor,
            NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionAlreadyExistException {
        log("createRevisionContent(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).createRevisionContent(uri, revisionDescriptor,
                revisionContent);
    }

    public void storeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor,
            NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionNotFoundException {
        log("storeRevisionContent(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).storeRevisionContent(uri, revisionDescriptor,
                revisionContent);
    }

    public void removeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor) throws ServiceAccessException {
        log("removeRevisionContent(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).removeRevisionContent(uri, revisionDescriptor);
    }

    // ==== NodeStore Methods ================================

    public void storeObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectNotFoundException {
        log("storeObject(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).storeObject(uri, object);
    }

    public void createObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectAlreadyExistsException {
        log("createObject(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).createObject(uri, object);
    }

    public void removeObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectNotFoundException {
        log("removeObject(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).removeObject(uri, object);
    }

    public ObjectNode retrieveObject(Uri uri) throws ServiceAccessException, ObjectNotFoundException {
        log("retrieveObject(" + uri + ")");
        // needed for request in WebdavServlet that checks if we have a
        // directory here
        TransactionId id = ((TransactionId) getCurrentlyActiveTransactionalResource());
        if (id == null) {
            id = createTransactionResource(uri);
            try {
                return id.retrieveObject(uri);
            } finally {
                try {
                    id.commit();
                } catch (XAException e) {
                    throw new ServiceAccessException(this, e);
                }
            }
        }
        return id.retrieveObject(uri);
    }

    // ==== RevisionDescriptorsStore Methods ================================

    public NodeRevisionDescriptors retrieveRevisionDescriptors(Uri uri) throws ServiceAccessException,
            RevisionDescriptorNotFoundException {
        log("retrieveRevisionDescriptors(" + uri + ")");
        // needed for DirectoryIndexGenerator
        TransactionId id = ((TransactionId) getCurrentlyActiveTransactionalResource());
        if (id == null) {
            id = createTransactionResource(uri);
            try {
                return id.retrieveRevisionDescriptors(uri);
            } finally {
                try {
                    id.commit();
                } catch (XAException e) {
                    throw new ServiceAccessException(this, e);
                }
            }
        }
        return id.retrieveRevisionDescriptors(uri);
    }

    public void createRevisionDescriptors(Uri uri, NodeRevisionDescriptors revisionDescriptors)
            throws ServiceAccessException {
        log("createRevisionDescriptors(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).createRevisionDescriptors(uri, revisionDescriptors);
    }

    public void storeRevisionDescriptors(Uri uri, NodeRevisionDescriptors revisionDescriptors)
            throws ServiceAccessException, RevisionDescriptorNotFoundException {
        log("storeRevisionDescriptors(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).storeRevisionDescriptors(uri, revisionDescriptors);
    }

    public void removeRevisionDescriptors(Uri uri) throws ServiceAccessException {
        log("removeRevisionDescriptors(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).removeRevisionDescriptors(uri);
    }

    // ==== RevisionDescriptorStore Methods ================================

    public void createRevisionDescriptor(Uri uri, NodeRevisionDescriptor revisionDescriptor)
            throws ServiceAccessException {
        log("createRevisionDescriptor(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).createRevisionDescriptor(uri, revisionDescriptor);
    }

    public void storeRevisionDescriptor(Uri uri, NodeRevisionDescriptor revisionDescriptor)
            throws ServiceAccessException, RevisionDescriptorNotFoundException {
        log("storeRevisionDescriptor(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).storeRevisionDescriptor(uri, revisionDescriptor);
    }

    public void removeRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber) throws ServiceAccessException {
        log("removeRevisionDescriptor(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).removeRevisionDescriptor(uri, revisionNumber);
    }

    public NodeRevisionDescriptor retrieveRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber)
            throws ServiceAccessException, RevisionDescriptorNotFoundException {
        log("retrieveRevisionDescriptor(" + uri + ")");
        // needed for DirectoryIndexGenerator
        TransactionId id = ((TransactionId) getCurrentlyActiveTransactionalResource());
        if (id == null) {
            id = createTransactionResource(uri);
            try {
                return id.retrieveRevisionDescriptor(uri, revisionNumber);
            } finally {
                try {
                    id.commit();
                } catch (XAException e) {
                    throw new ServiceAccessException(this, e);
                }
            }
        }
        return id.retrieveRevisionDescriptor(uri, revisionNumber);
    }

    // ==== LockStore Methods ================================

    public void putLock(Uri uri, NodeLock lock) throws ServiceAccessException {
        log("putLock(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).putLock(uri, lock);
    }

    public void renewLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
        log("renewLock(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).renewLock(uri, lock);
    }

    public void removeLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
        log("removeLock(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).removeLock(uri, lock);
    }

    public void killLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
        log("killLock(" + uri + ")");
        ((TransactionId) getCurrentlyActiveTransactionalResource()).killLock(uri, lock);
    }

    public Enumeration enumerateLocks(Uri uri) throws ServiceAccessException {
        log("enumerateLocks(" + uri + ")");
        TransactionId id = ((TransactionId) getCurrentlyActiveTransactionalResource());
        if (id == null) {
            id = createTransactionResource(uri);
            try {
                return id.enumerateLocks(uri);
            } finally {
                try {
                    id.commit();
                } catch (XAException e) {
                    throw new ServiceAccessException(this, e);
                }
            }
        }
        return id.enumerateLocks(uri);
    }

    protected void log(String msg) {
        getLogger().log(msg, this.getClass().getName(), Logger.DEBUG);
    }

    public Xid[] recover(int arg0) throws XAException {
        return null;
    }

    protected boolean includeBranchInXid() {
        return false;
    }

    protected TransactionalResource createTransactionResource(Xid xid) throws ServiceAccessException {
        CredentialsToken token = (CredentialsToken) WebdavStoreAdapter.credentials.get();
        Principal principal = null;
        if (token != null)
            principal = token.getPrincipal();
        return new TransactionId(xid, this, principal, storeFactory, parameters);
    }

    protected TransactionId createTransactionResource(Uri uri) throws ServiceAccessException {
        return new TransactionId(null, this, uri.getToken().getCredentialsToken().getPrincipal(), storeFactory,
                parameters);
    }

    protected static class TransactionId extends AbstractTransactionalResource {
        protected boolean readOnly;

        protected BasicWebdavStore store;

        protected WebdavStoreLockExtension lockStore = null;

        protected WebdavStoreBulkPropertyExtension bulkPropStore = null;

        protected WebdavStoreSinglePropertyExtension singlePropStore = null;

        protected Set toBeCreated;

        protected Set tentativeResourceCreated;

        protected Service service;

        protected Principal principal;

        protected Hashtable parameters;

        protected Object connection;

        protected boolean authenticated = false;
        
        TransactionId(Xid xid, Service service, Principal principal, BasicWebdavStoreFactory storeFactory, Hashtable parameters)
                throws ServiceAccessException {
            super(xid);
            this.service = service;
            this.principal = principal;
            readOnly = false;
            try {
                store = storeFactory.newInstance();
                if (store instanceof WebdavStoreLockExtension) {
                    lockStore = (WebdavStoreLockExtension) store;
                }
                if (store instanceof WebdavStoreBulkPropertyExtension) {
                    bulkPropStore = (WebdavStoreBulkPropertyExtension) store;
                }
                if (store instanceof WebdavStoreSinglePropertyExtension) {
                    singlePropStore = (WebdavStoreSinglePropertyExtension) store;
                }
                this.parameters = parameters;
            } catch (Exception e) {
                throw new ServiceAccessException(service, e);
            }

            openConnection();

            toBeCreated = new HashSet();
            tentativeResourceCreated = new HashSet();

            try {
                store.begin(service, principal, connection, new TxLogger(service.getLogger(), "WebDAV store"),
                        parameters);
            } catch (ServiceParameterErrorException e) {
                throw new ServiceAccessException(service, e);
            } catch (ServiceParameterMissingException e) {
                throw new ServiceAccessException(service, e);
            }
        }

        public void commit() throws XAException {
            try {
                store.commit();
            } catch (ServiceAccessException e) {
                getLogger().log("Could not commit store " + store, e, LOG_CHANNEL, Logger.ERROR);
                throw new XAException(XAException.XA_RBCOMMFAIL);
            } finally {
                closeConnection();
            }
        }

        public void rollback() throws XAException {
            try {
                store.rollback();
            } catch (ServiceAccessException e) {
                getLogger().log("Could not rollback store " + store, e, LOG_CHANNEL, Logger.ERROR);
                throw new XAException(XAException.XA_RBCOMMFAIL);
            } finally {
                closeConnection();
            }
        }

        protected void closeConnection() {
            WebdavStoreAdapter.credentials.set(null);
            if (connection != null)
                try {
                    JAASLoginModule.closeConnection(connection);
                } catch (Exception e) {
                    getLogger().log("Could not properly close connection", e, LOG_CHANNEL, Logger.WARNING);
                }
        }

        protected void openConnection() {
            if (JAASLoginModule.isInitialized() && connection == null && principal != null) {
                try {
                    connection = JAASLoginModule.getConnectionForUser(principal);
                } catch (Exception e) {
                    getLogger().log("Could not get connection for user " + principal.getName(), e, LOG_CHANNEL,
                            Logger.WARNING);
                }
            } else {
                connection = null;
            }
        }

        public int prepare() throws XAException {
            return (readOnly ? XA_RDONLY : XA_OK);
        }

        public void begin() throws XAException {
        }

        public void suspend() throws XAException {
        }

        public void resume() throws XAException {
        }

        public NodeRevisionContent retrieveRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor)
                throws ServiceAccessException, RevisionNotFoundException {
            checkAuthentication();
            if (!objectExists(uri)) {
                throw new RevisionNotFoundException(uri.toString(), revisionDescriptor.getRevisionNumber());
            } else {
                try {
                    NodeRevisionContent nrc = new NodeRevisionContent();
                    if (!objectExistsScheduled(uri)) {
                        InputStream in = store.getResourceContent(uri.toString());
                        nrc.setContent(in);
                    } else {
                        nrc.setContent(new byte[0]);
                    }
                    return nrc;
                } catch (ObjectNotFoundException e) {
                    throw new RevisionNotFoundException(uri.toString(), revisionDescriptor.getRevisionNumber());
                } catch (AccessDeniedException e) {
                    throw new ServiceAccessException(service, e);
                } catch (ObjectLockedException e) {
                    throw new ServiceAccessException(service, e);
                }
            }
        }

        public void createRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor,
                NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionAlreadyExistException {
            checkAuthentication();
            try {
                store.createResource(uri.toString());
                storeRevisionContent(uri, revisionDescriptor, revisionContent);
                tentativeResourceCreated.add(uri.toString());
                toBeCreated.remove(uri.toString());
            } catch (ObjectAlreadyExistsException e) {
                throw new RevisionAlreadyExistException(uri.toString(), revisionDescriptor.getRevisionNumber());
            } catch (AccessDeniedException e) {
                throw new ServiceAccessException(service, e);
            } catch (RevisionNotFoundException e) {
                throw new ServiceAccessException(service, e);
            } catch (ObjectLockedException e) {
                throw new ServiceAccessException(service, e);
            }
        }

        public void storeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor,
                NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionNotFoundException {
            checkAuthentication();
            try {
                String contentType = null;
                String characterEncoding = null;
                if (revisionDescriptor != null) {
                    NodeProperty property = revisionDescriptor.getProperty("characterEncoding");
                    if (property != null) {
                        characterEncoding = property.getValue().toString();
                    }
                    contentType = revisionDescriptor.getContentType();
                }
                store.setResourceContent(uri.toString(), revisionContent.streamContent(), contentType,
                        characterEncoding);
            } catch (ObjectNotFoundException e) {
                throw new RevisionNotFoundException(uri.toString(), revisionDescriptor.getRevisionNumber());
            } catch (IOException e) {
                throw new RevisionNotFoundException(uri.toString(), revisionDescriptor.getRevisionNumber());
            } catch (AccessDeniedException e) {
                throw new ServiceAccessException(service, e);
            } catch (ObjectLockedException e) {
                throw new ServiceAccessException(service, e);
            }

        }

        public void removeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor)
                throws ServiceAccessException {
            checkAuthentication();

            // already done in removeObject
        }

        public ObjectNode retrieveObject(Uri uri) throws ServiceAccessException, ObjectNotFoundException {
            checkAuthentication();
            if (!objectExists(uri)) {
                throw new ObjectNotFoundException(uri.toString());
            } else {
                try {

                    SubjectNode subject = new SubjectNode(uri.toString());
                    if (!objectExistsScheduled(uri) && store.isFolder(uri.toString())) {
                        String[] children = store.getChildrenNames(uri.toString());
                        for (int i = 0; i < children.length; i++) {
                            subject.addChild(new SubjectNode(children[i]));
                        }
                    }
                    return subject;
                } catch (AccessDeniedException e) {
                    throw new ServiceAccessException(service, e);
                } catch (ObjectLockedException e) {
                    throw new ServiceAccessException(service, e);
                }
            }
        }

        public void storeObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectNotFoundException {
            checkAuthentication();
            if (!objectExists(uri))
                throw new ObjectNotFoundException(uri);
        }

        public void createObject(Uri uri, ObjectNode object) throws ServiceAccessException,
                ObjectAlreadyExistsException {
            checkAuthentication();
            try {
                if (store.objectExists(uri.toString()))
                    throw new ObjectAlreadyExistsException(uri.toString());
            } catch (AccessDeniedException e) {
                throw new ServiceAccessException(service, e);
            } catch (ObjectLockedException e) {
                throw new ServiceAccessException(service, e);
            }
            // now, we do not have enough information, let's wait until we have
            // it...
            toBeCreated.add(uri.toString());
        }

        public void removeObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectNotFoundException {
            checkAuthentication();
            try {
                store.removeObject(uri.toString());
            } catch (AccessDeniedException e) {
                throw new ServiceAccessException(service, e);
            } catch (ObjectLockedException e) {
                throw new ServiceAccessException(service, e);
            }
            toBeCreated.remove(uri.toString());
        }

        public NodeRevisionDescriptor retrieveRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber)
                throws ServiceAccessException, RevisionDescriptorNotFoundException {
            checkAuthentication();
            if (!objectExists(uri)) {
                throw new RevisionDescriptorNotFoundException(uri.toString());
            } else {
                NodeRevisionDescriptor descriptor = new NodeRevisionDescriptor(new NodeRevisionNumber(1, 0),
                        NodeRevisionDescriptors.MAIN_BRANCH, new Vector(), new ArrayList());
                if (objectExistsInStore(uri)) {
                    try {

                        Map properties = null;
                        if (bulkPropStore != null) {
                            properties = bulkPropStore.getProperties(uri.toString());
                        } else if (singlePropStore != null) {
                            properties = singlePropStore.getProperties(uri.toString());
                        }
                        // maybe this is not supported by implementing
                        // store, so check it:
                        if (properties != null) {
                            for (Iterator it = properties.entrySet().iterator(); it.hasNext();) {
                                Map.Entry entry = (Map.Entry) it.next();
                                String effectiveName = (String) entry.getKey();
                                String name = effectiveName;
                                String ns = null;
                                int colonPos = effectiveName.lastIndexOf(':');
                                if (colonPos > 0 && colonPos < effectiveName.length()) {
                                    name = effectiveName.substring(colonPos + 1);
                                    ns = effectiveName.substring(0, colonPos);
                                }

                                String value = (String) entry.getValue();
                                NodeProperty property;
                                if (ns != null) {
                                    property = new NodeProperty(name, value, ns);
                                } else {
                                    property = new NodeProperty(name, value);
                                }
                                descriptor.setProperty(property);
                            }
                        }

                        // this specific information overrides all explicit
                        // properties
                        if (store.isFolder(uri.toString())) {
                            descriptor.setResourceType(NodeRevisionDescriptor.COLLECTION_TYPE);
                            descriptor.setContentLength(0);
                        } else {
                            descriptor.removeProperty(NodeRevisionDescriptor.RESOURCE_TYPE);
                            long length = store.getResourceLength(uri.toString());
                            if (length != -1) {
                                descriptor.setContentLength(length);
                            }
                        }

                        Date creationDate = store.getCreationDate(uri.toString());
                        if (creationDate != null) {
                            descriptor.setCreationDate(creationDate);
                        }

                        Date lastModified = store.getLastModified(uri.toString());
                        if (lastModified != null) {
                            descriptor.setLastModified(lastModified);
                        }
                        descriptor.resetRemovedProperties();
                        descriptor.resetUpdatedProperties();
                    } catch (ObjectNotFoundException e) {
                        throw new RevisionDescriptorNotFoundException(uri.toString());
                    } catch (AccessDeniedException e) {
                        throw new ServiceAccessException(service, e);
                    } catch (ObjectLockedException e) {
                        throw new ServiceAccessException(service, e);
                    }
                }
                return descriptor;
            }
        }

        public void createRevisionDescriptor(Uri uri, NodeRevisionDescriptor revisionDescriptor)
                throws ServiceAccessException {
            checkAuthentication();
            // already done in createObject
        }

        public void storeRevisionDescriptor(Uri uri, NodeRevisionDescriptor revisionDescriptor)
                throws ServiceAccessException, RevisionDescriptorNotFoundException {
            checkAuthentication();
            try {
                if (toBeCreated.remove(uri.toString())) {
                    if (revisionDescriptor.getResourceType().equals(NodeRevisionDescriptor.COLLECTION_TYPE)) {
                        store.createFolder(uri.toString());
                    } else {
                        store.createResource(uri.toString());
                    }
                }

                
                // in initialzation phase there might be no other way to tell
                // this actually is a collection
                // if it turns out to be so we need to revoke our decission and
                // remove the resource and create a folder
                // instead
                if (tentativeResourceCreated.remove(uri.toString())
                        && revisionDescriptor.getResourceType().equals(NodeRevisionDescriptor.COLLECTION_TYPE)) {
                    store.removeObject(uri.toString());
                    store.createFolder(uri.toString());
                }

                if (singlePropStore != null) {
                    Enumeration updated = revisionDescriptor.enumerateUpdatedProperties();
                    while (updated.hasMoreElements()) {
                        NodeProperty property = (NodeProperty) updated.nextElement();
                        String name = property.getName();
                        String ns = property.getNamespace();
                        String effectiveName = getNamespacedPropertyName(ns, name);
                        String value = property.getValue().toString();
                        String type = property.getType();
                        // XXX we do not use the type as it rarely contains
                        // anything sensible
                        String effectiveValue = value;
                        singlePropStore.addOrUpdateProperty(uri.toString(), effectiveName, effectiveValue);
                    }
                    Enumeration removed = revisionDescriptor.enumerateRemovedProperties();
                    while (removed.hasMoreElements()) {
                        NodeProperty property = (NodeProperty) removed.nextElement();
                        String name = property.getName();
                        
                        // we might have set that before
                        if (name.equals(NodeRevisionDescriptor.RESOURCE_TYPE)) continue;

                        String ns = property.getNamespace();
                        String effectiveName = getNamespacedPropertyName(ns, name);
                        singlePropStore.removeProperty(uri.toString(), name);
                    }
                } else if (bulkPropStore != null) {
                    Map properties = new HashMap();
                    Enumeration enumeration = revisionDescriptor.enumerateProperties();
                    while (enumeration.hasMoreElements()) {
                        NodeProperty property = (NodeProperty) enumeration.nextElement();
                        String name = property.getName();
                        String ns = property.getNamespace();
                        String effectiveName = getNamespacedPropertyName(ns, name);
                        String value = property.getValue().toString();
                        String type = property.getType();
                        // XXX we do not use the type as it rarely contains
                        // anything sensible
                        String effectiveValue = value;
                        properties.put(effectiveName, effectiveValue);
                    }
                    bulkPropStore.setProperties(uri.toString(), properties);
                }

            } catch (ObjectAlreadyExistsException e) {
                throw new ServiceAccessException(service, e);
            } catch (ObjectNotFoundException e) {
                throw new RevisionDescriptorNotFoundException(uri.toString());
            } catch (AccessDeniedException e) {
                throw new ServiceAccessException(service, e);
            } catch (ObjectLockedException e) {
                throw new ServiceAccessException(service, e);
            }
        }

        public void removeRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber) throws ServiceAccessException {
            checkAuthentication();
            // already done in removeObject
        }

        public NodeRevisionDescriptors retrieveRevisionDescriptors(Uri uri) throws ServiceAccessException,
                RevisionDescriptorNotFoundException {
            checkAuthentication();
            if (!objectExists(uri)) {
                throw new RevisionDescriptorNotFoundException(uri.toString());
            } else {
                NodeRevisionNumber rev = new NodeRevisionNumber(1, 0);

                Hashtable workingRevisions = new Hashtable();
                workingRevisions.put("main", rev);

                Hashtable latestRevisionNumbers = new Hashtable();
                latestRevisionNumbers.put("main", rev);

                Hashtable branches = new Hashtable();
                branches.put(rev, new Vector());

                return new NodeRevisionDescriptors(uri.toString(), rev, workingRevisions, latestRevisionNumbers,
                        branches, false);
            }
        }

        public void createRevisionDescriptors(Uri uri, NodeRevisionDescriptors revisionDescriptors)
                throws ServiceAccessException {
            checkAuthentication();
        }

        public void storeRevisionDescriptors(Uri uri, NodeRevisionDescriptors revisionDescriptors)
                throws ServiceAccessException, RevisionDescriptorNotFoundException {
            checkAuthentication();
        }

        public void removeRevisionDescriptors(Uri uri) throws ServiceAccessException {
            checkAuthentication();
        }

        public void putLock(Uri uri, NodeLock lock) throws ServiceAccessException {
            checkAuthentication();
            if (lockStore != null) {
                String lockId = lock.getLockId();
                Date expiration = lock.getExpirationDate();
                boolean exclusive = lock.isExclusive();
                boolean inheritable = lock.isInheritable();
                String subject = lock.getSubjectUri();
                try {
                    lockStore.lockObject(uri.toString(), lockId, subject, expiration, exclusive, inheritable);
                } catch (AccessDeniedException e) {
                    throw new ServiceAccessException(service, e);
                }
            }

        }

        public void renewLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
            checkAuthentication();
            if (lockStore != null) {
                removeLock(uri, lock);
                putLock(uri, lock);
            }
        }

        public void removeLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
            checkAuthentication();
            if (lockStore != null) {
                try {
                    lockStore.unlockObject(uri.toString(), lock.getLockId());
                } catch (AccessDeniedException e) {
                    throw new ServiceAccessException(service, e);
                }
            }
        }

        public void killLock(Uri uri, NodeLock lock) throws ServiceAccessException, LockTokenNotFoundException {
            checkAuthentication();
            removeLock(uri, lock);
        }

        public Enumeration enumerateLocks(Uri uri) throws ServiceAccessException {
            checkAuthentication();
            if (lockStore != null) {
                try {
                    WebdavStoreLockExtension.Lock[] ids = lockStore.getLockInfo(uri.toString());
                    if (ids == null)
                        return new Vector().elements();
                    Vector locks = new Vector(ids.length);
                    for (int i = 0; i < ids.length; i++) {
                        WebdavStoreLockExtension.Lock lockId = ids[i];
                        NodeLock lock = new NodeLock(lockId.getId(), uri.toString(), lockId.getSubject(),
                                "/actions/write", lockId.getExpirationDate(), lockId
                                .isInheritable(), lockId.isExclusive());
                        locks.add(lock);
                    }
                    return locks.elements();
                } catch (AccessDeniedException e) {
                    throw new ServiceAccessException(service, e);
                }
            } else {
                return new Vector().elements();
            }
        }

        protected boolean objectExists(Uri uri) throws ServiceAccessException {
            try {
                return (toBeCreated.contains(uri.toString()) || store.objectExists(uri.toString()));
            } catch (AccessDeniedException e) {
                throw new ServiceAccessException(service, e);
            } catch (ObjectLockedException e) {
                throw new ServiceAccessException(service, e);
            }
        }

        protected boolean objectExistsScheduled(Uri uri) throws ServiceAccessException {
            return toBeCreated.contains(uri.toString());
        }

        protected boolean objectExistsInStore(Uri uri) throws ServiceAccessException {
            try {
                return store.objectExists(uri.toString());
            } catch (AccessDeniedException e) {
                throw new ServiceAccessException(service, e);
            } catch (ObjectLockedException e) {
                throw new ServiceAccessException(service, e);
            }
        }

        protected void checkAuthentication() throws ServiceAccessException {
            if (!authenticated) {
                try {
                    store.checkAuthentication();
                    authenticated = true;
                } catch (UnauthenticatedException e) {
                    throw new ServiceAccessException(service, e);
                }
            }
        }
        
        protected Logger getLogger() {
            return service.getLogger();

        }
    }
}