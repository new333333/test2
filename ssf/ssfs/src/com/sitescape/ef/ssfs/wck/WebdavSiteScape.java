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
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNotFoundException;

import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;

import static com.sitescape.ef.web.crosscontext.ssfs.CrossContextConstants.*;

public class WebdavSiteScape implements BasicWebdavStore {

	private Service service;
	private LoggerFacade logger;
	private String zoneName;
	private String userName;
	
	public void begin(Service service, Principal principal, Object connection, 
			LoggerFacade logger, Hashtable parameters) 
	throws ServiceAccessException, ServiceParameterErrorException, 
	ServiceParameterMissingException {
		this.service = service;
		this.logger = logger;
		if(connection != null) {
			String[] v = ((String) connection).split(Constants.USERNAME_DELIM);
			this.zoneName = v[0].trim();
			this.userName = v[1].trim();
		}
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
		
			return objectExists(m);
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
	
	public boolean isFolder(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			return (representsFolder(m) && objectExists(m));
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

	public boolean isResource(String uri) throws ServiceAccessException, 
		AccessDeniedException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			return (!representsFolder(m) && objectExists(m));
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

	public void createFolder(String folderUri) throws ServiceAccessException, 
	AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException {
		throw new AccessDeniedException(folderUri, "Creating folder is not supported", "create");	
	}

	public void createResource(String resourceUri) throws ServiceAccessException, 
	AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException {
		try {
			Map m = parseUri(resourceUri);
		
			if(representsFolder(m))
				throw new ServiceAccessException(service, "The position refers to a folder");
			else
				CCClient.createResource(zoneName, userName, m);
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(resourceUri, e.getMessage(), "create");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(resourceUri, e.getMessage(), "create");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (AlreadyExistsException e) {
			throw new ObjectAlreadyExistsException(resourceUri);
		}		
	}

	public void setResourceContent(String resourceUri, InputStream content, 
			String contentType, String characterEncoding) throws ServiceAccessException, 
			AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(resourceUri);
		
			if(representsFolder(m))
				throw new ObjectNotFoundException(resourceUri);
			else
				CCClient.setResource(zoneName, userName, m, content); // we don't use contentType and characterEncoding
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(resourceUri, e.getMessage(), "store");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(resourceUri, e.getMessage(), "store");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(resourceUri);
		}				
	}

	public Date getLastModified(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);

			if(representsAbstractFolder(m))
				return new Date(0); // There's no good answer for this - Will this work?
			else
				return CCClient.getLastModified(zoneName, userName, m);
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
				return CCClient.getCreationDate(zoneName, userName, m);
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

			if(!representsFolder(m)) {
				return null; 
				// Not very consistent with the way we handled this condition. 
			    // In other places we throw ObjectNotFoundException when a
			    // folder uri refers to a non-folder resource. In this case,
				// we return null. This is simply to follow the convention
				// shown in the WebdavFileStore reference implementation.
			}
			else if(filesOnly(m)) { // /files
				if(zoneName != null)
					return new String[] {zoneName};
				else
					return new String[0];
			}
			else if(uptoZoneOnly(m)) { // /files/<zonename>
				return new String[] {URI_TYPE_INTERNAL, URI_TYPE_LIBRARY};
			}
			else {
				return CCClient.getChildrenNames(zoneName, userName, m);
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
	}

	public InputStream getResourceContent(String resourceUri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(resourceUri);
		
			if(representsFolder(m))
				throw new ObjectNotFoundException(resourceUri);
			else
				return CCClient.getResource(zoneName, userName, m); 
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(resourceUri, e.getMessage(), "read");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(resourceUri, e.getMessage(), "read");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(resourceUri);
		}				
	}

	public long getResourceLength(String resourceUri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(resourceUri);
		
			if(representsFolder(m))
				throw new ObjectNotFoundException(resourceUri);
			else
				return CCClient.getResourceLength(zoneName, userName, m); 
		}
		catch(ZoneMismatchException e) {
			throw new AccessDeniedException(resourceUri, e.getMessage(), "read");
		}		
		catch (NoAccessException e) {
			throw new AccessDeniedException(resourceUri, e.getMessage(), "read");
		}
		catch (CCClientException e) {
			throw new ServiceAccessException(service, e.getMessage());
		} 
		catch (NoSuchObjectException e) {
			throw new ObjectNotFoundException(resourceUri);
		}				
	}

	public void removeObject(String uri) throws ServiceAccessException, 
	AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		try {
			Map m = parseUri(uri);
		
			if(representsFolder(m))
				throw new AccessDeniedException(uri, "Removing folder is not supported", "create");
			else
				CCClient.removeResource(zoneName, userName, m); 
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
	
	private Map returnMap(Map map, boolean isFolder) {
		if(isFolder)
			map.put(URI_IS_FOLDER, Boolean.TRUE);
		else
			map.put(URI_IS_FOLDER, Boolean.FALSE);
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
		if(uri.startsWith(Constants.URI_DELIM))
			uri = uri.substring(1);
		
		// Break uri into pieces
		String[] u = uri.split(Constants.URI_DELIM);
		
		if(!u[0].equals("files"))
			return null;
		
		Map map = new HashMap();
		
		if(u.length == 1)
			return returnMap(map, true);
		
		String zname = u[1];
		
		if(!zname.equals(this.zoneName))
			throw new ZoneMismatchException("No access to the specified zone");
		
		map.put(URI_ZONENAME, zname);
		
		if(u.length == 2)
			return returnMap(map, true);
		
		if(!u[2].equals(URI_TYPE_INTERNAL) && !u[2].equals(URI_TYPE_LIBRARY))
			return null;
		
		map.put(URI_TYPE, u[2]);
		
		if(u.length == 3)
			return returnMap(map, true);
		
		if(u[2].equals(URI_TYPE_INTERNAL)) {
			try {
				map.put(URI_BINDER_ID, Long.valueOf(u[3]));
			}
			catch(NumberFormatException e) {
				return null;
			}
			
			if(u.length == 4)
				return returnMap(map, true);
			
			map.put(URI_ENTITY_TYPED_ID, u[4]);
			
			if(u.length == 5)
				return returnMap(map, true);
			
			String itemType = u[5];
			
			if(!itemType.equals(URI_ITEM_TYPE_PRIMARY) &&
					!itemType.equals(URI_ITEM_TYPE_FILE) &&
					!itemType.equals(URI_ITEM_TYPE_GRAPHIC) &&
					!itemType.equals(URI_ITEM_TYPE_ATTACH))
				return null;
			
			map.put(URI_ITEM_TYPE, itemType);
			
			if(u.length == 6)
				return returnMap(map, true);
			
			if(itemType.equals(URI_ITEM_TYPE_PRIMARY)) {
				map.put(URI_FILENAME, u[6]);
				
				if(u.length == 7)
					return returnMap(map, false);
				else
					return null;
			}
			else {
				map.put(URI_ELEMNAME, u[6]);
				
				if(u.length == 7)
					return returnMap(map, true);
				
				map.put(URI_FILENAME, u[7]);
				
				if(u.length == 8)
					return returnMap(map, false);
				else
					return null;
			}
		}
		else {
			try {
				map.put(URI_BINDER_ID, Long.valueOf(u[3]));
			}
			catch(NumberFormatException e) {
				return null;
			}
			
			if(u.length == 4)
				return returnMap(map, true);
			
			map.put(URI_FILENAME, u[4]);
			
			if(u.length == 5)
				return returnMap(map, false);
			else
				return null;
		}
	}
	
	private boolean objectExists(Map m) throws NoAccessException, CCClientException {
		// 1. /files always exist
		// 2. /files/<zonename> always exist AS LONG AS the zonename matches that of the user
		// 3. /files/<zonename>/internal always exist AS LONG AS the zonename matches that of the user
		// 4. /files/<zonename>/library always exist AS LONG AS the zonename matches that of the user
		
		if(representsAbstractFolder(m))
			return true;  // /files/<zonename>/<internal or library>
		else
			return CCClient.objectExists(zoneName, userName, m);		
	}

	/**
	 * Returns whether the URI represents an abstract folder or not.
	 * Abstract folder is one of the following:
	 * <p>
	 *	/files
	 *  /files/<zonename>
	 *  /files/<zonename>/internal
	 *  /files/<zonename>/library  
	 * 
	 * @param m
	 * @return
	 */
	private boolean representsAbstractFolder(Map m) {
		// Map does not contain 'files'. So including URI_IS_FOLDER
		// entry, maximum of three entries indicates an abstract folder.
		return (m.size() <= 3);
	}
	
	private boolean filesOnly(Map m) {
		return (m.size() == 1); // contains URI_IS_FOLDER only
	}
	
	private boolean uptoZoneOnly(Map m) {
		return (m.size() == 2); // contains URI_IS_FOLDER and <zonename>
	}
	
	private boolean representsFolder(Map m) {
		return ((Boolean) m.get(URI_IS_FOLDER)).booleanValue();
	}
}
