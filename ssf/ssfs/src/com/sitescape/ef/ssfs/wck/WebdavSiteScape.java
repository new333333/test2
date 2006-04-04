package com.sitescape.ef.ssfs.wck;

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
import org.apache.slide.simple.store.BasicWebdavStore;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNotFoundException;

public class WebdavSiteScape implements BasicWebdavStore {

	public void begin(Service service, Principal principal, Object connection, LoggerFacade logger, Hashtable parameters) throws ServiceAccessException, ServiceParameterErrorException, ServiceParameterMissingException {
		// TODO Auto-generated method stub
		
	}

	public void checkAuthentication() throws UnauthenticatedException {
		// TODO Auto-generated method stub
		
	}

	public void commit() throws ServiceAccessException {
		// TODO Auto-generated method stub
		
	}

	public void rollback() throws ServiceAccessException {
		// TODO Auto-generated method stub
		
	}

	public boolean objectExists(String uri) throws ServiceAccessException, AccessDeniedException, ObjectLockedException {
		// TODO Auto-generated method stub
		if("/files".equals(uri))
			return true;
		else
			return false;
	}

	public boolean isFolder(String uri) throws ServiceAccessException, AccessDeniedException, ObjectLockedException {
		// TODO Auto-generated method stub
		if("/files".equals(uri))
			return true;
		else
			return false;
	}

	public boolean isResource(String uri) throws ServiceAccessException, AccessDeniedException, ObjectLockedException {
		// TODO Auto-generated method stub
		return false;
	}

	public void createFolder(String folderUri) throws ServiceAccessException, AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException {
		// TODO Auto-generated method stub
		
	}

	public void createResource(String resourceUri) throws ServiceAccessException, AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException {
		// TODO Auto-generated method stub
		
	}

	public void setResourceContent(String resourceUri, InputStream content, String contentType, String characterEncoding) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		// TODO Auto-generated method stub
		
	}

	public Date getLastModified(String uri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getCreationDate(String uri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getChildrenNames(String folderUri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		// TODO Auto-generated method stub
		return new String[0];
	}

	public InputStream getResourceContent(String resourceUri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		throw new ObjectNotFoundException(resourceUri);
	}

	public long getResourceLength(String resourceUri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void removeObject(String uri) throws ServiceAccessException, AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		// TODO Auto-generated method stub
		
	}
	
}
