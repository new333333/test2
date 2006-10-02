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

package org.apache.slide.simple.reference;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.transaction.util.LoggerFacade;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.UnauthenticatedException;
import org.apache.slide.simple.store.BasicWebdavStore;
import org.apache.slide.simple.store.WebdavStoreAdapter;
import org.apache.slide.simple.store.WebdavStoreBulkPropertyExtension;
import org.apache.slide.simple.store.WebdavStoreLockExtension;
import org.apache.slide.simple.store.WebdavStoreMacroCopyExtension;
import org.apache.slide.simple.store.WebdavStoreMacroDeleteExtension;
import org.apache.slide.simple.store.WebdavStoreMacroMoveExtension;
import org.apache.slide.store.util.FileHelper;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNotFoundException;

/**
 * Reference implementation for the
 * {@link org.apache.slide.simple.store.BasicWebdavStore} and extension
 * interfaces. It
 * stores content into the file system with its root configured with the
 * "rootpath" parameter. Additional properties and locks are stored in separate
 * files and are in both cases optional.
 * 
 * <p>
 * WebdavFileStore needs to be deployed with implementations of at a least a
 * SecurityStore and optionally with a LockStore. A sample Domain.xml entry
 * looks like: <br>
 * 
 * <pre>
 * 
 *  
 *   
 *         &lt;store name=&quot;simple&quot;&gt;
 *             &lt;parameter name=&quot;cache-mode&quot;&gt;cluster&lt;/parameter&gt;
 *             &lt;nodestore classname=&quot;org.apache.slide.store.simple.WebdavStoreAdapter&quot;&gt;
 *                &lt;parameter name=&quot;callback-store&quot;&gt;org.apache.slide.store.simple.WebdavFileStore&lt;/parameter&gt;
 *                &lt;parameter name=&quot;rootpath&quot;&gt;c:/tmp&lt;/parameter&gt;
 *             &lt;/nodestore&gt;
 *             &lt;contentstore&gt;
 *               &lt;reference store=&quot;nodestore&quot;/&gt;
 *             &lt;/contentstore&gt;
 *             &lt;revisiondescriptorsstore&gt;
 *               &lt;reference store=&quot;nodestore&quot;/&gt;
 *             &lt;/revisiondescriptorsstore&gt;
 *             &lt;revisiondescriptorstore&gt;
 *               &lt;reference store=&quot;nodestore&quot;/&gt;
 *             &lt;/revisiondescriptorstore&gt;
 *             &lt;!-- comment this out when you want to use the locking from the memory store --&gt; 
 *             &lt;lockstore&gt;
 *               &lt;reference store=&quot;nodestore&quot;/&gt;
 *             &lt;/lockstore&gt;
 *             &lt;securitystore classname=&quot;org.apache.slide.store.mem.TransientSecurityStore&quot;/&gt;
 *             &lt;!-- uncomment this when you want to use the the locking from the memory store --&gt; 
 *             &lt;!--lockstore classname=&quot;org.apache.slide.store.mem.TransientLockStore&quot;/--&gt;
 *         &lt;/store&gt;
 *         &lt;scope match=&quot;/files&quot; store=&quot;simple&quot;/&gt;
 *    
 *   
 *  
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Caching mode should be set to "cluster" when you want every change in the
 * file system to be immedeately displayed in Slide. This is practical as this
 * store tends to be rather fast.
 * </p>
 * 
 * <p>
 * When you import data from Domain.xml like the file folder Slide gives you no
 * hint upon what kind of object it will be. Even more it stores content even
 * for folders pointing you in the wrong direction. As this implementation
 * relies on information if the stored object will be a folder or resource with
 * content imported data from Domain.xml must be augmented with properties that
 * indicate the type. E.g. for correct creation of the files object as a folder
 * the above configuration would require an entry like
 * 
 * <pre>
 * 
 *  
 *   
 *                    &lt;objectnode classname=&quot;org.apache.slide.structure.SubjectNode&quot; uri=&quot;/files&quot;&gt;
 *                           &lt;revision&gt;
 *                              &lt;property name=&quot;resourcetype&quot;&gt;&lt;![CDATA[&lt;collection/&gt;]]&gt;&lt;/property&gt;
 *                          &lt;/revision&gt;
 *                          ....
 *    
 *   
 *  
 * </pre>
 * 
 * instead of
 * 
 * <pre>
 * 
 *  
 *   
 *                    &lt;objectnode classname=&quot;org.apache.slide.structure.SubjectNode&quot; uri=&quot;/files&quot;&gt;
 *                          ....
 *    
 *   
 *  
 * </pre>
 * 
 * </p>
 * 
 * @see BasicWebdavStore
 * @see WebdavStoreLockExtension
 * @see WebdavStoreBulkPropertyExtension
 * @see WebdavStoreAdapter
 * @version $Revision: 226342 $
 */
public class WebdavFileStore implements BasicWebdavStore, WebdavStoreLockExtension, WebdavStoreBulkPropertyExtension,
        WebdavStoreMacroCopyExtension, WebdavStoreMacroMoveExtension, WebdavStoreMacroDeleteExtension {

    private static final String ROOTPATH_PARAMETER = "rootpath";

    private static final String LOCK_FILE_EXTENSION = ".lck";

    private static final String PROPERTY_FILE_PREFIX = ".";

    private static void save(InputStream is, File file) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        try {
            FileHelper.copy(is, os);
        } finally {
            try {
                is.close();
            } finally {
                os.close();
            }
        }
    }

    private static File root = null;

    private static Service service = null;

    private static LoggerFacade logger = null;

    public synchronized void begin(Service service, Principal principal, Object connection, LoggerFacade logger,
            Hashtable parameters) throws ServiceAccessException, ServiceParameterErrorException,
            ServiceParameterMissingException {
        // set parameters only once...
        if (WebdavFileStore.root == null) {
            WebdavFileStore.service = service;
            WebdavFileStore.logger = logger;
            String rootPath = (String) parameters.get(ROOTPATH_PARAMETER);
            if (rootPath == null)
                throw new ServiceParameterMissingException(service, ROOTPATH_PARAMETER);

            WebdavFileStore.root = new File(rootPath);
            if (!WebdavFileStore.root.exists()) {
                if (!WebdavFileStore.root.mkdirs()) {
                    throw new ServiceParameterErrorException(service, ROOTPATH_PARAMETER + ": " + WebdavFileStore.root
                            + " does not exist and could not be created");
                } else {
                    logger.logInfo("Created root folder at: " + rootPath);
                }
            }
        }
    }

    public void checkAuthentication() throws UnauthenticatedException {
    }
    
    public void commit() throws ServiceAccessException {
    }

    public void rollback() throws ServiceAccessException {
    }

    public void macroCopy(String sourceUri, String targetUri, boolean overwrite, boolean recursive) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException, ObjectAlreadyExistsException {
        try {
            File fromFile = getFile(sourceUri);
            File toFile = getFile(targetUri);
            if (toFile.exists() && !overwrite) {
                throw new ObjectAlreadyExistsException(targetUri);
            }
            if (!toFile.getParentFile().exists()) {
                throw new ObjectNotFoundException(toFile.getParentFile().toString());
            }
            if (fromFile.isDirectory() && !recursive) {
                //  copy directory only, which means create dir and copy properties
                if (!toFile.exists()) {
                    toFile.mkdirs();
                }
            } else {
                FileHelper.copyRec(fromFile, toFile);
            }
            File propertyFile = getPropertyFile(sourceUri);
            File destPropertyFile = getPropertyFile(targetUri);
            if (propertyFile.exists()) FileHelper.copy(propertyFile, destPropertyFile);
            // XXX is it correct not to take over locking information on copy? I guess so (OZ)
//            File lockFile = getLockFile(sourceUri);
//            File destLockFile = getLockFile(targetUri);
//            if (lockFile.exists()) FileHelper.copy(propertyFile, destPropertyFile);
        } catch (FileNotFoundException e) {
            throw new ObjectNotFoundException(targetUri);
        } catch (IOException e) {
            throw new ServiceAccessException(service, e);
        } catch (SecurityException e) {
            throw new AccessDeniedException(targetUri, e.getMessage(), "/actions/write");
        }
    }

    public void macroMove(String sourceUri, String targetUri, boolean overwrite) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException, ObjectAlreadyExistsException {
        try {
            File fromFile = getFile(sourceUri);
            File toFile = getFile(targetUri);
            if (toFile.exists() && !overwrite) {
                throw new ObjectAlreadyExistsException(targetUri);
            }
            if (!toFile.getParentFile().exists()) {
                throw new ObjectNotFoundException(toFile.getParentFile().toString());
            }
            renameOrMove(fromFile, toFile);
            File propertyFile = getPropertyFile(sourceUri);
            File destPropertyFile = getPropertyFile(targetUri);
            renameOrMove(propertyFile, destPropertyFile);
            File lockFile = getLockFile(sourceUri);
            File destLockFile = getLockFile(targetUri);
            renameOrMove(lockFile, destLockFile);
        } catch (FileNotFoundException e) {
            throw new ObjectNotFoundException(targetUri);
        } catch (IOException e) {
            throw new ServiceAccessException(service, e);
        } catch (SecurityException e) {
            throw new AccessDeniedException(targetUri, e.getMessage(), "/actions/write");
        }
    }

    protected void renameOrMove(File from, File to) throws IOException,
            ObjectAlreadyExistsException {
        if (from.exists()) {
            if (to.exists()) {
                boolean success = to.delete();
                if (!success) {
                    new ObjectAlreadyExistsException(to.toString());
                }
            }
            boolean success = from.renameTo(to);
            if (!success) {
                FileHelper.moveRec(from, to);
            }
        }
    }
    
    public void macroDelete(String targetUri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException {
        try {
            File file = getFile(targetUri);
            FileHelper.removeRec(file);
            File propertyFile = getPropertyFile(targetUri);
            if (propertyFile.exists()) propertyFile.delete();    
            File lockFile = getLockFile(targetUri);
            if (lockFile.exists()) lockFile.delete();    
        } catch (SecurityException e) {
            throw new AccessDeniedException(targetUri, e.getMessage(), "/actions/write");
        }
    }

    public boolean objectExists(String uri) throws ServiceAccessException, AccessDeniedException {
        try {
            return getFile(uri).exists();
        } catch (SecurityException e) {
            throw new AccessDeniedException(uri, e.getMessage(), "read");
        }
    }

    public boolean isFolder(String uri) throws ServiceAccessException, AccessDeniedException {
        try {
            return (getFile(uri).exists() && getFile(uri).isDirectory());
        } catch (SecurityException e) {
            throw new AccessDeniedException(uri, e.getMessage(), "read");
        }

    }

    public boolean isResource(String uri) throws ServiceAccessException, AccessDeniedException {
        try {
            return (getFile(uri).exists() && !getFile(uri).isDirectory());
        } catch (SecurityException e) {
            throw new AccessDeniedException(uri, e.getMessage(), "read");
        }
    }

    public void createFolder(String folderUri) throws ServiceAccessException, AccessDeniedException,
            ObjectAlreadyExistsException {
        try {
            if (!getFile(folderUri).mkdir())
                throw new ServiceAccessException(service, "Can not create directory " + folderUri);

        } catch (SecurityException e) {
            throw new AccessDeniedException(folderUri, e.getMessage(), "create");
        }
    }

    public String[] getChildrenNames(String folderUri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException {
        try {

            File file = getFile(folderUri);
            if (file.isDirectory()) {

                File[] children = file.listFiles();
                List childList = new ArrayList();
                for (int i = 0; i < children.length; i++) {
                    String name = children[i].getName();
                    // locking and property information should not be displayed
                    // as a resource
                    if (!isLockFilename(name) && !isPropertyFilename(name)) {
                        childList.add(name);
                    }
                }
                String[] childrenNames = new String[childList.size()];
                childrenNames = (String[]) childList.toArray(childrenNames);
                return childrenNames;
            } else {
                return null;
            }
        } catch (SecurityException e) {
            throw new AccessDeniedException(folderUri, e.getMessage(), "read");
        }
    }

    public void createResource(String resourceUri) throws ServiceAccessException, AccessDeniedException,
            ObjectAlreadyExistsException {
        try {
            File file = getFile(resourceUri);
            if (file.exists())
                throw new ObjectAlreadyExistsException(resourceUri);
            if (!file.createNewFile())
                throw new ServiceAccessException(service, "Can not create file " + resourceUri);
        } catch (IOException e) {
            throw new ServiceAccessException(service, e);
        } catch (SecurityException e) {
            throw new AccessDeniedException(resourceUri, e.getMessage(), "create");
        }
    }

    public void setResourceContent(String resourceUri, InputStream content, String contentType, String characterEncoding) throws ServiceAccessException,
            AccessDeniedException, ObjectNotFoundException {
        try {

            File file = getFile(resourceUri);
            if (!file.exists())
                throw new ObjectNotFoundException(resourceUri);
            try {
                save(content, file);
            } catch (FileNotFoundException e) {
                // XXX this really indicates a denied access
                throw new AccessDeniedException(resourceUri, e.getMessage(), "store");
            } catch (IOException e) {
                throw new ServiceAccessException(service, e);
            }
        } catch (SecurityException e) {
            throw new AccessDeniedException(resourceUri, e.getMessage(), "store");
        }
    }

    // 4/25/06 JK - Due to changes to the superclass, I had to add implementation
    // of this additional method.
    public void createAndSetResource(String resourceUri, InputStream content, String contentType, String characterEncoding)
    throws ServiceAccessException, AccessDeniedException, ObjectAlreadyExistsException {
    	createResource(resourceUri);
    	
    	try {
    		setResourceContent(resourceUri, content, contentType, characterEncoding);
    	}
    	catch(ObjectNotFoundException e) {
    		// This should never occur. 
    		throw new ServiceAccessException(service, e);
    	}
    }

    public long getResourceLength(String resourceUri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException {
        try {
            File file = getFile(resourceUri);
            if (!file.exists())
                throw new ObjectNotFoundException(resourceUri);
            return file.length();
        } catch (SecurityException e) {
            throw new AccessDeniedException(resourceUri, e.getMessage(), "read");
        }

    }

    public InputStream getResourceContent(String resourceUri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException {
        try {
            File file = getFile(resourceUri);
            if (!file.exists())
                throw new ObjectNotFoundException(resourceUri);
            InputStream in;
            try {
                in = new BufferedInputStream(new FileInputStream(file));
                return in;
            } catch (FileNotFoundException e) {
                throw new ObjectNotFoundException(resourceUri);
            }
        } catch (SecurityException e) {
            throw new AccessDeniedException(resourceUri, e.getMessage(), "read");
        }
    }

    public void removeObject(String uri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException {
        try {
            File file = getFile(uri);
            if (!file.exists())
                throw new ObjectNotFoundException(uri);

            if (!file.delete())
                throw new ServiceAccessException(service, "Unable to delete " + uri);
            getPropertyFile(uri).delete();
            getLockFile(uri).delete();
        } catch (SecurityException e) {
            throw new AccessDeniedException(uri, e.getMessage(), "delete");
        }
    }

    public Date getLastModified(String uri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException {
        try {
            File file = getFile(uri);
            if (!file.exists())
                throw new ObjectNotFoundException(uri);
            long lastModified = file.lastModified();
            return new Date(lastModified);
        } catch (SecurityException e) {
            throw new AccessDeniedException(uri, e.getMessage(), "read");
        }
    }

    public Date getCreationDate(String uri) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException {
        // XXX we do not have this information
        return getLastModified(uri);
    }

    public Map getProperties(String uri) throws ServiceAccessException, AccessDeniedException {
        File file = getPropertyFile(uri);
        if (!file.exists()) {
            return null;
        }
        return readProperties(file);
    }

    public void setProperties(String uri, Map properties) throws ServiceAccessException, AccessDeniedException {
        File file = getPropertyFile(uri);
        assureCreated(file, uri);
        Properties props = new Properties();
        props.putAll(properties);
        saveProperties(file, props, "WebDAV properties");
    }

    public void addOrUpdateProperty(String uri, String name, String value) throws ServiceAccessException,
            AccessDeniedException, ObjectNotFoundException {
    }

    public void removeProperty(String uri, String name) throws ServiceAccessException, AccessDeniedException,
            ObjectNotFoundException {
    }

    public void lockObject(String uri, String lockId, String subject, Date expiration, boolean exclusive,
            boolean inheritable, String owner) throws ServiceAccessException, AccessDeniedException {
        // 10/2/06 JK - We needed to change the signature of lockObject method in
    	// WebdavStoreLockExtension class so that we can preserve owner info field
    	// supplied by WebDAV client. It is a necessary for proper operation of
    	// Xythos Drive. This class ignores the extra field value though since
    	// we don't care about this class (other than it has to compile).
        File file = getLockFile(uri);
        assureCreated(file, uri);
        Properties properties = readProperties(file);
        String lockString = expiration.getTime() + "|" + String.valueOf(exclusive) + "|" + String.valueOf(inheritable)
                + "|" + subject;
        properties.setProperty(lockId, lockString);
        saveProperties(file, properties, "WebDAV locks");
    }

    public void unlockObject(String uri, String lockId) throws ServiceAccessException, AccessDeniedException {
        File file = getLockFile(uri);
        if (!file.exists()) {
            throw new ServiceAccessException(service, "There nothing to unlock for " + uri);
        }
        Properties properties = readProperties(file);
        properties.remove(lockId);
        // do a special trick and remove the lock file when there are no more
        // locks
        if (properties.size() != 0) {
            saveProperties(file, properties, "WebDAV locks");
        } else {
            if (!file.delete())
                throw new ServiceAccessException(service, "Could not delete lock file for " + uri);
        }
    }

    public Lock[] getLockInfo(String uri) throws ServiceAccessException, AccessDeniedException {
        File file = getLockFile(uri);
        if (!file.exists()) {
            return null;
        }
        Properties properties = readProperties(file);
        List locks = new ArrayList();
        Enumeration enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String id = (String) enumeration.nextElement();
            String value = properties.getProperty(id);
            if (value == null) {
                throw new ServiceAccessException(service, "Invalid lockId " + id);
            }
            StringTokenizer tokenizer = new StringTokenizer(value, "|");
            int tokens = tokenizer.countTokens();
            if (tokens != 4) {
                throw new ServiceAccessException(service, "Invalid lock information for lockId " + id);
            }
            String dateString = tokenizer.nextToken();
            String exclusiveString = tokenizer.nextToken();
            String inheritableString = tokenizer.nextToken();
            String subject = tokenizer.nextToken();
            Date date = new Date(Long.valueOf(dateString).longValue());
            boolean exclusive = Boolean.valueOf(exclusiveString).booleanValue();
            boolean inheritable = Boolean.valueOf(inheritableString).booleanValue();
            Lock lock = new SimpleLock(id, exclusive, inheritable, date, subject);
            locks.add(lock);
        }
        Lock[] lockArray = new Lock[locks.size()];
        lockArray = (Lock[]) locks.toArray(lockArray);
        return lockArray;
    }

    protected File getFile(String uri) {
        File file = new File(root, uri);
        return file;
    }

    protected File getPropertyFile(String uri) {
        String dir;
        String name;
        int lastSlash = uri.lastIndexOf('/');
        if (lastSlash != -1) {
            dir = uri.substring(0, lastSlash + 1);
            name = uri.substring(lastSlash + 1);
        } else {
            dir = "";
            name = uri;
        }
        String path = dir + PROPERTY_FILE_PREFIX + name;
        File file = new File(root, path);
        return file;
    }

    protected boolean isPropertyFilename(String uri) {
        return uri.startsWith(PROPERTY_FILE_PREFIX);
    }

    protected File getLockFile(String uri) {
        File file = new File(root, uri + LOCK_FILE_EXTENSION);
        return file;
    }

    protected boolean isLockFilename(String uri) {
        return uri.endsWith(LOCK_FILE_EXTENSION);
    }

    protected void assureCreated(File file, String uri) throws ServiceAccessException {
        if (!file.exists()) {
            try {
                if (!file.createNewFile())
                    throw new ServiceAccessException(service, "Can not create file " + uri);
            } catch (IOException e) {
                throw new ServiceAccessException(service, e);
            }
        }
    }

    protected String getLockEntry(String uri, String lockId) throws ServiceAccessException, ObjectNotFoundException {
        File file = getLockFile(uri);
        if (!file.exists()) {
            throw new ObjectNotFoundException(uri);
        }
        Properties properties = readProperties(file);
        String value = properties.getProperty(lockId);
        if (value == null) {
            throw new ServiceAccessException(service, "Invalid lockId " + lockId);
        }
        return value;
    }

    protected void saveProperties(File file, Properties properties, String header) throws ServiceAccessException {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            properties.store(os, header);
        } catch (FileNotFoundException e) {
            throw new ServiceAccessException(service, e);
        } catch (IOException e) {
            throw new ServiceAccessException(service, e);
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch (IOException e) {
                }
        }
    }

    protected Properties readProperties(File file) throws ServiceAccessException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (FileNotFoundException e) {
            throw new ServiceAccessException(service, e);
        } catch (IOException e) {
            throw new ServiceAccessException(service, e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                }
        }
    }

    /**
     * Straight forward reference implemenation of a lock.
     */
    public static class SimpleLock implements Lock {

        public String id;

        public boolean exclusive;

        public boolean inheritable;

        public Date expirationDate;

        public String subject;

        public SimpleLock(String id, boolean exclusive, boolean inheritable, Date expirationDate, String subject) {
            this.id = id;
            this.exclusive = exclusive;
            this.inheritable = inheritable;
            this.expirationDate = expirationDate;
            this.subject = subject;
        }

        public boolean isExclusive() {
            return exclusive;
        }

        public Date getExpirationDate() {
            return expirationDate;
        }

        public String getId() {
            return id;
        }

        public boolean isInheritable() {
            return inheritable;
        }

        public String getSubject() {
            return subject;
        }

        // 10/2/06 JK - This implementation simply ignores owner field.
        public String getOwner() {
        	return "";
        }
    }
}
