/*
 * $Header$
 * $Revision: 208500 $
 * $Date: 2005-02-09 14:16:28 -0500 (Wed, 09 Feb 2005) $
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

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.transaction.util.LoggerFacade;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.UnauthenticatedException;
import org.apache.slide.simple.reference.WebdavFileStore;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNotFoundException;

/**
 * Basic interface for stores that are being fed by the WebDAV layer.
 * 
 * <p>
 * For each WebDAV request an object of this interface is being created. Each
 * request will call the
 * {@link #begin(Service, Principal, Object, LoggerFacade, Hashtable)}method
 * and will either be ended with a call to the {@link #commit()}or the
 * {@link #rollback()}method. For the whole duration of such a request it will
 * be this only and only this object that is being called back. This means you
 * can store all kinds of object variables in your implementation and be very
 * sure they are still accessible when the next callback comes.
 * </p>
 * 
 * <p>
 * Parameters set for the adapter are being passed to this interface literally
 * and unchanged.
 * </p>
 * 
 * <p>
 * To indicate your system denies permission to perform certain requests on
 * certain Uris you should throw a
 * {@link org.apache.slide.security.AccessDeniedException}. This will be caught
 * by the {@link org.apache.slide.simple.store.WebdavStoreAdapter adapter}code
 * and transformed into a <em>FORBIDDEN</em> response. Any fatal stuff that
 * goes wrong in your code should issue a
 * {@link org.apache.slide.common.ServiceAccessException}. Finally, if the
 * adapter asks for any kind of object that is not there throw a
 * {@link org.apache.slide.structure.ObjectNotFoundException}or a
 * {@link org.apache.slide.structure.ObjectAlreadyExistsException}if the
 * adapter tries to create anything that is already there.
 * </p>
 * 
 * <p>
 * These method are all you need to make your system run with the main methods
 * of the Slide's WebDAV layer. This means if you implement these methods
 * meaningful your system will be able to work with MS Windows Explorer, MS
 * Office, Netdrive and the Mac Finder. You will however need additional stores
 * to take care of locking and security informations or implement the extended
 * interfaces that have additional locking and property methods. Implementing
 * this interface will not store any custom properties. Have a look at the
 * {@link WebdavFileStore}reference implementation to the file system and the
 * configuration given there.
 * </p>
 * 
 * <p>
 * <em>Caution: It is most important to understand that this is no general purpose store. 
 * It has been designed to solely work with access to Slide via WebDAV with general methods.
 * It relies on certain sequences of calls that are done when the Slide core is being accessed through
 * the WebDAV layer. Other sequences are likely to make this store fail.</em>
 * </p>
 * 
 * @see WebdavStoreLockExtension
 * @see WebdavStoreBulkPropertyExtension
 * @see WebdavStoreSinglePropertyExtension
 * @see WebdavStoreAdapter
 * @see WebdavFileStore
 * 
 * @version $Revision: 208500 $
 */
public interface BasicWebdavStore {

    /**
     * Indicates that a new request or transaction with this store involved has
     * been started. The request will be terminated by either {@link #commit()}
     * or {@link #rollback()}. If only non-read methods have been called, the
     * request will be terminated by a {@link #commit()}. This method will be
     * called by (@link WebdavStoreAdapter} at the beginning of each request.
     * 
     * 
     * @param service
     *            the Slide service that feeds this store, to be used in
     *            ServiceAccessException
     * @param principal
     *            the principal that started this request or <code>null</code>
     *            if there is non available
     * @param connection
     *            connection to the underlying persistence storage or
     *            <code>null</code> if either not applicable or not available
     * @param parameters
     *            Hashtable containing the parameters' names and associated
     *            values as configured with this store
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @exception ServiceParameterErrorException
     *                Incorrect service parameter
     * @exception ServiceParameterMissingException
     *                Service parameter missing
     */
    void begin(Service service, Principal principal, Object connection, LoggerFacade logger, Hashtable parameters)
            throws ServiceAccessException, ServiceParameterErrorException, ServiceParameterMissingException;

    /**
     * Checks if authentication information passed in {@link #begin(Service, Principal, Object, LoggerFacade, Hashtable)}
     * is valid. If not throws an exception.
     * 
     * @throws UnauthenticatedException if authentication is not valid
     */
    void checkAuthentication() throws UnauthenticatedException;
    
    /**
     * Indicates that all changes done inside this request shall be made
     * permanent and any transactions, connections and other temporary resources
     * shall be terminated.
     * 
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     */
    void commit() throws ServiceAccessException;

    /**
     * Indicates that all changes done inside this request shall be undone and
     * any transactions, connections and other temporary resources shall be
     * terminated.
     * 
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     */
    void rollback() throws ServiceAccessException;

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code>.
     * 
     * @param uri
     *            URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies read access to this object
     * @throws ObjectLockedException if the object has been locked internally
     */
    boolean objectExists(String uri) throws ServiceAccessException, AccessDeniedException, ObjectLockedException;

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code> and if so if it is a folder.
     * 
     * @param uri
     *            URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     *         and is a folder
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies read access to this object
     * @throws ObjectLockedException if the object has been locked internally
     */
    boolean isFolder(String uri) throws ServiceAccessException, AccessDeniedException, ObjectLockedException;

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code> and if so if it is a content resource.
     * 
     * @param uri
     *            URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     *         and is a content resource
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies read access to this object
     * @throws ObjectLockedException if the object has been locked internally
     */
    boolean isResource(String uri) throws ServiceAccessException, AccessDeniedException, ObjectLockedException;

    /**
     * Creates a folder at the position specified by <code>folderUri</code>.
     * 
     * @param folderUri
     *            URI of the folder
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies write access to this folder
     * @throws ObjectAlreadyExistsException
     *             if there already is an object at <code>folderUri</code>
     * @throws ObjectLockedException if the object has been locked internally
     */
    void createFolder(String folderUri) throws ServiceAccessException, AccessDeniedException,
            ObjectAlreadyExistsException, ObjectLockedException;

    /**
     * Creates a content resource at the position specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *            URI of the content resource
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies write access to this content resource
     * @throws ObjectAlreadyExistsException
     *             if there already is an object at <code>resourceUri</code>
     * @throws ObjectLockedException if the object has been locked internally
     */
    void createResource(String resourceUri) throws ServiceAccessException, AccessDeniedException,
            ObjectAlreadyExistsException, ObjectLockedException;

    /**
     * Sets / stores the content of the resource specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *            URI of the content resource
     * @param content
     *            input stream the content of the resource can be read from
     * @param contentType
     *            content type of the resource or <code>null</code> if unknown
     * @param characterEncoding
     *            character encoding of the resource or <code>null</code> if
     *            unknown or not applicable
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies write access to this content resource
     * @throws ObjectNotFoundException
     *             if there is no object at <code>resourceUri</code>
     * @throws ObjectLockedException if the object has been locked internally
     */
    void setResourceContent(String resourceUri, InputStream content, String contentType, String characterEncoding)
            throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException;

    /**
     * Gets the date of the last modiciation of the object specified by
     * <code>uri</code>.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder
     * @return date of last modification, <code>null</code> declares this
     *         value as invalid and asks the adapter to try to set it from the
     *         properties if possible
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies read access to this object or property
     * @throws ObjectNotFoundException
     *             if there is no object at <code>uri</code>
     * @throws ObjectLockedException
     *             if the object has been locked internally
     */
    Date getLastModified(String uri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException,
            ObjectLockedException;

    /**
     * Gets the date of the creation of the object specified by <code>uri</code>.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder
     * @return date of creation, <code>null</code> declares this value as
     *         invalid and asks the adapter to try to set it from the properties
     *         if possible
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies read access to this object or property
     * @throws ObjectNotFoundException
     *             if there is no object at <code>uri</code>
     * @throws ObjectLockedException
     *             if the object has been locked internally
     */
    Date getCreationDate(String uri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException,
            ObjectLockedException;

    /**
     * Gets the names of the children of the folder specified by
     * <code>folderUri</code>.
     * 
     * @param folderUri
     *            URI of the folder
     * @return array containing names of the children
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies read access to this object or property
     * @throws ObjectNotFoundException
     *             if there is no object at <code>folderUri</code>
     * @throws ObjectLockedException if the object has been locked internally
     */
    String[] getChildrenNames(String folderUri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException, ObjectLockedException;

    /**
     * Gets the content of the resource specified by <code>resourceUri</code>.
     * 
     * @param resourceUri
     *            URI of the content resource
     * @return input stream you can read the content of the resource from
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies read access to this content resource
     * @throws ObjectNotFoundException
     *             if there is no object at <code>resourceUri</code>
     * @throws ObjectLockedException if the object has been locked internally
     */
    InputStream getResourceContent(String resourceUri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException, ObjectLockedException;

    /**
     * Gets the length of the content resource specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *            URI of the content resource
     * @return length of the resource in bytes,
     *         <code>-1</code> declares this value as invalid and asks the
     *         adapter to try to set it from the properties if possible
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies read access to this content resource
     * @throws ObjectNotFoundException
     *             if there is no object at <code>resourceUri</code>
     * @throws ObjectLockedException if the object has been locked internally
     */
    long getResourceLength(String resourceUri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException, ObjectLockedException;

    /**
     * Removes the object specified by <code>uri</code>.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies write resp. delete access to this object
     *             or property
     * @throws ObjectNotFoundException
     *             if there is no object at <code>uri</code>
     * @throws ObjectLockedException
     *             if the object already has been locked internally
     * @throws ObjectLockedException if the object has been locked internally
     */
    void removeObject(String uri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException,
            ObjectLockedException;
}