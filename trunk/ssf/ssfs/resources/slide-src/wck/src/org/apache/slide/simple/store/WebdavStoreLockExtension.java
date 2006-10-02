/*
 * $Header$
 * $Revision: 208281 $
 * $Date: 2004-12-09 07:17:09 -0500 (Thu, 09 Dec 2004) $
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

import java.util.Date;

import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.security.AccessDeniedException;

/**
 * Optional extension to the 
 * {@link org.apache.slide.simple.store.BasicWebdavStore basic store} with
 * locking call backs.
 * 
 * <p>
 * It can be fed by the same adapter as the
 * {@link org.apache.slide.simple.store.WebdavStoreAdapter adapter}!
 * </p>
 * 
 * <p>
 * Be sure to read the Javadocs of the
 * {@link org.apache.slide.simple.store.BasicWebdavStore basic one} first!
 * </p>
 * 
 * <p>
 * Each lock in Slide has an id which is used to unqiuely identify the lock. It
 * will be passed along with the
 * {@link #lockObject(String, String, String, Date, boolean, boolean) locking} and
 * {@link #unlockObject(String, String) unlocking} methods. Note that folders
 * can be locked as well. Information about existing locks has to be passed back
 * with the {@link #getLockInfo(String)} method as an array of
 * {@link WebdavStoreLockExtension.Lock}objects. This means you will also have to
 * implement this interface, which should be very simple, though.
 * </p>
 * 
 * <p>
 * <em>Caution: It is most important to understand that this is no general purpose store. 
 * It has been designed to solely work with access to Slide via WebDAV with general methods.
 * It relies on certain sequences of calls that are done when the Slide core is being accessed through
 * the WebDAV layer. Other sequences are likely to make this store fail.</em>
 * </p>
 * 
 * @see BasicWebdavStore
 * @see org.apache.slide.simple.reference.WebdavFileStore
 * @see WebdavStoreAdapter
 * @version $Revision: 208281 $
 */
public interface WebdavStoreLockExtension {

    /**
     * Locks an object specified by <code>uri</code>. The object is not
     * reqired to exist as locks can be set to protect the creation of objects
     * as well.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder even an
     *            object that does not exist, yet
     * @param lockId
     *            unique identifier of this lock
     * @param subject
     *            owner of this lock
     * @param expiration
     *            date when this lock expires
     * @param exclusive
     *            if set to <code>true</code> this lock is exclusive,
     *            otherwise it is shared
     * @param inheritable
     *            if the object is a folder and this is set to <code>true</code>
     *            the children and all descendants of this folder are locked as
     *            well
     * @param owner 10/2/06 JK - We need to pass down the client-supplied owner info
     *              to the server along with other fields so that it can be retrieved
     *              later. Xythos Drive counts on this value for important functionality.
     *              Specifically, it seems to use this  value to locate and manage 
     *              cache entries. 
     *
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies locking of this object or property
     */
    void lockObject(String uri, String lockId, String subject, Date expiration, boolean exclusive, boolean inheritable, String owner)
            throws ServiceAccessException, AccessDeniedException;

    /**
     * Unlocks an object specified by <code>uri</code>. The object is not
     * reqired to exist.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder even an
     *            object that does not exist
     * @param lockId
     *            unique identifier of this lock
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies locking of this object or property
     */
    void unlockObject(String uri, String lockId) throws ServiceAccessException, AccessDeniedException;

    /**
     * Gets all locks set on an object specified by <code>uri</code>. The
     * object is not reqired to exist.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder even an
     *            object that does not exist
     * @return an array of {@link WebdavStoreLockExtension.Lock}objects
     * @throws ServiceAccessException
     *             if any kind of internal error or any unexpected state occurs
     * @throws AccessDeniedException
     *             if the store denies getting locking information on this
     *             object or property
     */
    Lock[] getLockInfo(String uri) throws ServiceAccessException, AccessDeniedException;

    /**
     * A lock as returned by {@link WebdavStoreLockExtension#getLockInfo(String)}and
     * set by
     * {@link WebdavStoreLockExtension#lockObject(String, String, String, Date, boolean, boolean)}.
     */
    interface Lock {
        String getId();

        boolean isExclusive();

        boolean isInheritable();

        Date getExpirationDate();

        String getSubject();
        
        // 10/2/06 JK - We need to preserve and pass owner info as well 
        // (which is supplied by WebDAV client in the first place).
        // Xythos Drive counts on this value for important functionality.
        // Specifically, it seems to use this  value to locate and manage 
        // cache entries. 
        String getOwner();
    }
}