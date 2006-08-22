package com.sitescape.ef.ssfs.wck;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.transaction.util.LoggerFacade;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.UnauthenticatedException;
import org.apache.slide.simple.store.BasicWebdavStore;
import org.apache.slide.simple.store.WebdavStoreBulkPropertyExtension;
import org.apache.slide.simple.store.WebdavStoreLockExtension;
import org.apache.slide.simple.store.WebdavStoreMacroCopyExtension;
import org.apache.slide.simple.store.WebdavStoreMacroMoveExtension;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNotFoundException;

import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.LockException;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.TypeMismatchException;

import static com.sitescape.ef.ssfs.CrossContextConstants.*;

public class WebdavSiteScape implements BasicWebdavStore, 
	WebdavStoreBulkPropertyExtension, WebdavStoreLockExtension,
	WebdavStoreMacroCopyExtension, WebdavStoreMacroMoveExtension{
	
	private static final String URI_SYNTACTIC_TYPE = "synType";
	// Syntactically the URI refers to a folder
	private static final Integer URI_SYNTACTIC_TYPE_FOLDER 	= new Integer(1);
	// Syntactically the URI refers to a file
	private static final Integer URI_SYNTACTIC_TYPE_FILE 	= new Integer(2);
	// Syntactically the URI could refer to either a folder or a file.
	// In other words, information about whether it is a folder or a file can
	// not be derived from the structural/syntactic analysis of the URI alone
	// since it could be either. 
	private static final Integer URI_SYNTACTIC_TYPE_EITHER 	= new Integer(3);
	
	private Service service;
	private LoggerFacade logger;
	private String zoneName;
	private String userName;
	private CCClient client;
	
	public void begin(Service service, Principal principal, Object connection, 
			LoggerFacade logger, Hashtable parameters) 
	throws ServiceAccessException, ServiceParameterErrorException, 
	ServiceParameterMissingException {
		this.service = service;
		this.logger = logger;
		if(connection != null) {
			String[] id = Util.parseUserIdInput((String) connection);			
			this.zoneName = id[0];
			this.userName = id[1];
		}
		this.client = new CCClient(zoneName, userName);
	}

	public void checkAuthentication() throws UnauthenticatedException {		
	}

	public void commit() throws ServiceAccessException {
		// Nothing to do
	}

	public void rollback() throws ServiceAccessException {
		// Don't support this (for now)
	}
	
	public boolean objectExists(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			return(!objectInfo(uri, m).equals(OBJECT_INFO_NON_EXISTING));
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		} 
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		} 
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		}
	}
	
	// Returns true ONLY IF the uri represents a folder AND the resource
	// it refers to actually exists. 
	public boolean isFolder(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			return(objectInfo(uri, m).equals(OBJECT_INFO_FOLDER));
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		} 
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		}
	}

	// Returns true ONLY IF the uri represents a file (non-folder) AND
	// the resource it refers to actually exists. 
	public boolean isResource(String uri) throws ServiceAccessException, 
		AccessDeniedException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			return(objectInfo(uri, m).equals(OBJECT_INFO_FILE));
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		}
	}

	public void createFolder(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			if(representsAbstractFolder(m))
				throw new ObjectAlreadyExistsException(uri); // Abstract folders always exist
			else if(URI_TYPE_INTERNAL.equals(m.get(URI_TYPE)))
				throw new AccessDeniedException(uri, "Creating folder is not supported for internal uri", "create");
			else
				client.createFolder(uri, m);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "create");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "create");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch(AlreadyExistsException e) {
			throw new ObjectAlreadyExistsException(uri);
		}
		catch(TypeMismatchException e) {
			// This indicates that the folder uri is already used in the system
			// to refer to a file (ie, non-folder type object). 
			throw new ServiceAccessException(service, e.getMessage());
		}
	}

	public void createResource(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FOLDER)
				throw new ServiceAccessException(service, "The position refers to a folder");
			else
				client.createResource(uri, m);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "create");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "create");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (AlreadyExistsException e) {
			throw new ObjectAlreadyExistsException(uri);
		}		
		catch(TypeMismatchException e) {
			// The position refers to a folder
			throw new ServiceAccessException(service, e.getMessage());
		}
	}

	public void setResourceContent(String uri, InputStream content, 
			String contentType, String characterEncoding) throws ServiceAccessException, 
			AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FOLDER)
				throw new ObjectNotFoundException(uri);
			else
				client.setResource(uri, m, content); // we don't use contentType and characterEncoding
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "store");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "store");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(uri);
		}	
		catch(TypeMismatchException e) {
			throw new ObjectNotFoundException(uri);
		}
	}

	public void createAndSetResource(String uri, InputStream content, 
			String contentType, String characterEncoding) throws ServiceAccessException, 
			AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FOLDER)
				throw new ServiceAccessException(service, "The position refers to a folder");
			else
				client.createAndSetResource(uri, m, content); // Discard contentType and characterEncoding
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "create");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "create");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (AlreadyExistsException e) {
			throw new ObjectAlreadyExistsException(uri);
		}		
		catch(TypeMismatchException e) {
			// The position refers to a folder
			throw new ServiceAccessException(service, e.getMessage());
		}
	}

	public Date getLastModified(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);

			if(representsAbstractFolder(m))
				return new Date(0); // There's no good answer for this - Will this work?
			else
				return client.getLastModified(uri, m);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		} 
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}		
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(uri);
		}				
	}

	public Date getCreationDate(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);

			if(representsAbstractFolder(m))
				return new Date(0); // There's no good answer for this - Will this work?
			else
				return client.getCreationDate(uri, m);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		} 
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}		
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(uri);
		}				
	}

	public String[] getChildrenNames(String folderUri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(folderUri);

			if(filesOnly(m)) { // /files
				return new String[] {URI_TYPE_INTERNAL, URI_TYPE_LIBRARY};				
			}
			else if(uptoUriTypeOnly(m)) { // /files/{internal or library}
				if(zoneName != null)
					return new String[] {zoneName};
				else
					return new String[0];				
			}
			else if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FILE ) {
				return null; 
				// Not very consistent with the way we handled this condition. 
			    // In other places we throw ObjectNotFoundException when a
			    // folder uri refers to a non-folder resource. In this case,
				// we return null. This is simply to follow the convention
				// shown in the WebdavFileStore reference implementation
				// and make WCK framework happy. 
			}
			else {
				return client.getChildrenNames(folderUri, m);
			}
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(folderUri, e.getMessage(), "read");
		} 
		catch (NoAccessException e) {
			throw new AccessDeniedException(folderUri, e.getMessage(), "read");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(folderUri);
		}	
		catch(TypeMismatchException e) {
			// For the exact same reason described above, this return null
			// as opposed to throwing an exception.
			return null;
		}
	}

	public InputStream getResourceContent(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FOLDER)
				throw new ObjectNotFoundException(uri);
			else
				return client.getResource(uri, m); 
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(uri);
		}		
		catch(TypeMismatchException e) {
			throw new ObjectNotFoundException(uri);
		}
	}

	public long getResourceLength(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FOLDER)
				throw new ObjectNotFoundException(uri);
			else
				return client.getResourceLength(uri, m); 
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(uri);
		}				
		catch(TypeMismatchException e) {
			throw new ObjectNotFoundException(uri);
		}
	}

	public void removeObject(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			if(representsAbstractFolder(m)) { // abstract folder
				throw new AccessDeniedException(uri, "Can not remove the folder", "delete");
			}
			else { // concrete folder or file
				if(URI_TYPE_INTERNAL.equals(m.get(URI_TYPE))) { // internal uri
					if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FOLDER)
						throw new AccessDeniedException(uri, "Removing folder is not supported for internal uri", "delete");
					else
						client.removeObject(uri, m); // remove file
				}
				else { // library uri
					client.removeObject(uri, m); // remove folder or file
				}
			}			
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "delete");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "delete");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(uri);
		}				
	}

	public Map getProperties(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
					
			if(representsAbstractFolder(m)) {
				// Implementation Note:
				// Do not return non-String type properties. So for now, I will
				// comment this out.
				/*
				Map<String,Object> props = new HashMap<String,Object>();
				props.put(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE, new Date(0));
				props.put(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED, new Date(0));
				return props;
				*/
				return null;
			}
			else {
				return client.getDAVProperties(uri, m);
			}
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "read");
		} 
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		}	
	}

	public void setProperties(String uri, Map properties) 
	throws ServiceAccessException, AccessDeniedException, 
	ObjectNotFoundException, ObjectLockedException {
		// We do not allow write access to the properties of any resource.
		// All properties are generated or computed internally as side
		// effect of some other operation such as creating or modifying
		// a resource. The properties can be read via getProperties, but
		// can not be modifed through WebDAV. This may not be consistent
		// with the typical manner in which "pure" WebDAV repository
		// operates. However, since our system needs to allow for both
		// WebDAV-based and web-based accesses to the same core, we can
		// not rely on the properties values handed over by the WebDAV
		// framework. If we did, it would cause inconsistency. Practically
		// most of the information in the input map are already available
		// in the core system, so ditching the map shouldn't cause any
		// loss of significant information. 
	}
	
	public void lockObject(String uri, String lockId, String subject, 
			Date expiration, boolean exclusive, boolean inheritable) 
	throws ServiceAccessException, AccessDeniedException {
		if(!exclusive)
			throw new AccessDeniedException(uri, "Shared lock is not supported", "lock");
		
		if(inheritable)
			throw new AccessDeniedException(uri, "Recursive locking is not supported", "lock");
		
		// Make sure that the subject passed in matches the credential of
		// the currently executing user. We do NOT allow users to obtain locks
		// on behalf of another user (Although WCK doesn't appear to allow
		// the described situation to occur, I'm doing additional check here - 
		// just to make sure). Essentially we use subject only for validation
		// purpose and do not actually store the string along with the lock. 
		String[] id = Util.parseSubject(subject);

		if(!id[0].equals(this.zoneName) || !id[1].equals(this.userName))
			throw new AccessDeniedException(uri, "Cannot obtain lock on behalf of another user", "lock");
		
		try {
			Map m = parseUri(uri);
		
			if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FOLDER)
				throw new AccessDeniedException(uri, "Locking of folder is not supported", "lock");
			else
				client.lockResource(uri, m, new SimpleLock(lockId, subject, expiration)); 
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "lock");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "lock");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		}
		catch (NoSuchObjectException e) {
			// The specified object does not exist. Although WebDAV specification
			// allows locking of object that does not yet fully exist (called
			// "Null Resource" - see WebDAV spec and/or the corresponding method 
			// description in WebdavStoreLockExtension for details), SSFS does 
			// not support this capability. Therefore, we throw an exception 
			// indicating that the store denies locking of this object. 
			throw new AccessDeniedException(uri, "Null-resource locking (locking of non-existing object) is not supported", "lock");
		}	
		catch(LockException e) {
			// If someone else (or even another program run by the same user)
			// locks the resource between the time this thread reads the lock 
			// and the time it attempts to obtain it, I guess this condition 
			// can occur. But this should be extremely unlikely except when 
			// system load is unusually high with many concurrent users
			// trying to edit the same files... (again, very unlikely). 
			throw new AccessDeniedException(uri, "Failed to lock the resource", "lock");
		}
		catch(TypeMismatchException e) {
			// The object is not a file but a folder. 
			throw new AccessDeniedException(uri, "Locking of folder is not supported", "lock");
		}
	}

	public void unlockObject(String uri, String lockId) 
	throws ServiceAccessException, AccessDeniedException {
		try {
			Map m = parseUri(uri);
		
			// If the uri represents a folder, we haven't locked the object since
			// we don't support locking of folder. Silently return in that case.
			if(getUriSyntacticType(m) != URI_SYNTACTIC_TYPE_FOLDER)
				client.unlockResource(uri, m, lockId); 
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "lock");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "lock");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		}
		catch (NoSuchObjectException e) {
			// The specified object does not exist. This means nothing to 
			// unlock, so return silently.
		}		
		catch(TypeMismatchException e) {
			// The object is not a file but a folder. Return silently. 
		}
	}

	public Lock[] getLockInfo(String uri) throws ServiceAccessException, 
	AccessDeniedException {
		try {
			Map m = parseUri(uri);
		
			// If the uri represents a folder, we haven't locked the object since
			// we don't support locking of folder. Return null in that case.
			if(getUriSyntacticType(m) == URI_SYNTACTIC_TYPE_FOLDER)
				return null;
			else
				return client.getLockInfo(uri, m); 
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "lock");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(uri, e.getMessage(), "lock");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		}
		catch (NoSuchObjectException e) {
			// The specified object does not exist. 
			return null;
		}	
		catch(TypeMismatchException e) {
			// The object is not a file but a folder. return null. 
			return null;
		}
	}

	public void macroCopy(String sourceUri, String targetUri, 
			boolean overwrite, boolean recursive) 
	throws ServiceAccessException, AccessDeniedException, 
	ObjectNotFoundException, ObjectAlreadyExistsException, 
	ObjectLockedException {
		Map sm = null;
		Map tm = null;
		
		try {
			sm = parseUri(sourceUri);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(sourceUri, e.getMessage(), "/actions/write");
		}
		
		try {
			tm = parseUri(targetUri);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(targetUri, e.getMessage(), "/actions/write");
		}		
		
		try {
			client.copyObject(sourceUri, sm, targetUri, tm, overwrite, recursive);
		}
		catch (NoAccessException e) {
			throw new AccessDeniedException(targetUri, e.getMessage(), "/actions/write");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(sourceUri);
		}	
		catch (AlreadyExistsException e) {
			throw new ObjectAlreadyExistsException(targetUri);
		}		
		catch(TypeMismatchException e) {
			// Perhaps we should throw AccessDeniedException instead?
			throw new ServiceAccessException(service, e.getMessage());
		}
	}

	public void macroMove(String sourceUri, String targetUri, boolean overwrite) 
	throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, 
	ObjectAlreadyExistsException, ObjectLockedException {
		Map sm = null;
		Map tm = null;
		
		try {
			sm = parseUri(sourceUri);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(sourceUri, e.getMessage(), "/actions/write");
		}
		
		try {
			tm = parseUri(targetUri);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(targetUri, e.getMessage(), "/actions/write");
		}		
		
		try {
			client.moveObject(sourceUri, sm, targetUri, tm, overwrite);
		}
		catch (NoAccessException e) {
			throw new AccessDeniedException(targetUri, e.getMessage(), "/actions/write");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(sourceUri);
		}	
		catch (AlreadyExistsException e) {
			throw new ObjectAlreadyExistsException(targetUri);
		}		
		catch(TypeMismatchException e) {
			// Perhaps we should throw AccessDeniedException instead?
			throw new ServiceAccessException(service, e.getMessage());
		}
	}

	private Map returnMap(Map map, Integer uriSyntacticType) {
		map.put(URI_SYNTACTIC_TYPE, uriSyntacticType);
		return map;
	}
	
	/**
	 * Returns a map containing the result of parsing the uri. 
	 * If uri structural validation fails, it returns <code>null</code>.
	 * If uri's zone validation fails against user credential, it throws 
	 * <code>ZoneMismatchException</code>.
	 * 
	 * @param uri
	 * @return
	 */
	private Map parseUri(String uri) throws ZoneMismatchException {
		if(uri.startsWith(Util.URI_DELIM))
			uri = uri.substring(1);
		
		// Break uri into pieces
		String[] u = uri.split(Util.URI_DELIM);
		
		if(!u[0].equals("files"))
			return null;
		
		Map map = new HashMap();
		
		map.put(URI_ORIGINAL, uri);
		
		if(u.length == 1)
			return returnMap(map, URI_SYNTACTIC_TYPE_FOLDER);
		
		String type = u[1];
		
		if(!type.equals(URI_TYPE_INTERNAL) && !type.equals(URI_TYPE_LIBRARY))
			return null;
		
		map.put(URI_TYPE, type);

		if(u.length == 2)
			return returnMap(map, URI_SYNTACTIC_TYPE_FOLDER);
		
		String zname = u[2];
		
		if(!zname.equals(this.zoneName))
			throw new ZoneMismatchException("No access to the specified zone");
		
		map.put(URI_ZONENAME, zname);
		
		if(u.length == 3)
			return returnMap(map, URI_SYNTACTIC_TYPE_FOLDER);
		
		if(type.equals(URI_TYPE_INTERNAL)) { // internal
			try {
				map.put(URI_BINDER_ID, Long.valueOf(u[3]));
			}
			catch(NumberFormatException e) {
				return null;
			}
			
			if(u.length == 4)
				return returnMap(map, URI_SYNTACTIC_TYPE_FOLDER);
			
			map.put(URI_ENTRY_ID, Long.valueOf(u[4]));
			
			if(u.length == 5)
				return returnMap(map, URI_SYNTACTIC_TYPE_FOLDER);
			
			String itemType = u[5];
			
			if(!itemType.equals(URI_ITEM_TYPE_LIBRARY) &&
					!itemType.equals(URI_ITEM_TYPE_FILE) &&
					!itemType.equals(URI_ITEM_TYPE_GRAPHIC) &&
					!itemType.equals(URI_ITEM_TYPE_ATTACH))
				return null;
			
			map.put(URI_ITEM_TYPE, itemType);
			
			if(u.length == 6)
				return returnMap(map, URI_SYNTACTIC_TYPE_FOLDER);
			
			if(itemType.equals(URI_ITEM_TYPE_LIBRARY)) {
				map.put(URI_FILEPATH, makeFilepath(u, 6));
				
				return returnMap(map, URI_SYNTACTIC_TYPE_FILE);
			}
			else if(itemType.equals(URI_ITEM_TYPE_ATTACH)) {
				map.put(URI_REPOS_NAME, u[6]);
				
				if(u.length == 7)
					return returnMap(map, URI_SYNTACTIC_TYPE_FOLDER);
				
				map.put(URI_FILEPATH, makeFilepath(u, 7));
				
				return returnMap(map, URI_SYNTACTIC_TYPE_FILE);			
			}
			else { // file or graphic
				map.put(URI_ELEMNAME, u[6]);
				
				if(u.length == 7)
					return returnMap(map, URI_SYNTACTIC_TYPE_FOLDER);
				
				map.put(URI_FILEPATH, makeFilepath(u, 7));
				
				return returnMap(map, URI_SYNTACTIC_TYPE_FILE);
			}
		}
		else { // library
			String libpath = makeLibpath(u, 3);
			
			map.put(URI_LIBPATH, libpath);
			
			return returnMap(map, URI_SYNTACTIC_TYPE_EITHER);
		}
	}
	
	private String makeLibpath(String[] sa, int startIndex) {
		// Not particularly efficient...
		StringBuffer sb  = new StringBuffer();
		for(int i = startIndex; i < sa.length; i++) {
			sb.append(Util.URI_DELIM).
			append(sa[i]);
		}
		String s = sb.toString();
		if(s.endsWith(Util.URI_DELIM))
			s = s.substring(0, s.length()-1);
		return s;
	}
	
	private String objectInfo(String uri, Map m) throws NoAccessException, CCClientException {
		if(m == null)
			return OBJECT_INFO_NON_EXISTING;
		else if(representsAbstractFolder(m))
			return OBJECT_INFO_FOLDER;
		else
			return client.objectInfo(uri, m);
	}

	/**
	 * Returns whether the URI represents an abstract folder or not.
	 * Abstract folder is one of the following:
	 * <p>
	 *	/files
	 *  /files/internal
	 *  /files/internal/<zonename>
	 *  /files/library
	 *  /files/library/<zonename> 
	 * 
	 * @param m
	 * @return
	 */
	private boolean representsAbstractFolder(Map m) {
		// Map does not contain 'files'. So including RESOURCE_TYPE and
		// URI_ORIGINAL entries, maximum of four entries indicates an 
		// abstract folder.
		return (m.size() <= 4);
	}
	
	private boolean filesOnly(Map m) {
		return (m.size() == 2); // contains RESOURCE_TYPE and URI_ORIGINAL only
	}
	
	private boolean uptoUriTypeOnly(Map m) {
		return (m.size() == 3); // contains RESOURCE_TYPE, URI_ORIGINAL and URI_TYPE
	}
	
	private Integer getUriSyntacticType(Map m) {
		return (Integer) m.get(URI_SYNTACTIC_TYPE);
	}

	private String makeFilepath(String[] input, int startIndex) {
		StringBuffer sb = new StringBuffer();
		for(int i = startIndex; i < input.length; i++) {
			if(i > startIndex)
				sb.append("/");
			sb.append(input[i]);
		}
		return sb.toString();
	}
}
