package com.sitescape.team.ssfs.server;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import com.sitescape.team.ssfs.AlreadyExistsException;
import com.sitescape.team.ssfs.LockException;
import com.sitescape.team.ssfs.NoAccessException;
import com.sitescape.team.ssfs.NoSuchObjectException;
import com.sitescape.team.ssfs.TypeMismatchException;

public interface SiteScapeFileSystem {

	public void createResource(Map uri) throws 
	NoAccessException, AlreadyExistsException, TypeMismatchException;
	
	public void setResource(Map uri, InputStream content) 
	throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException;
	
	public void createAndSetResource(Map uri, InputStream content) 
	throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException;

	public void createDirectory(Map uri) throws  
	NoAccessException, AlreadyExistsException, TypeMismatchException;
	
	public InputStream getResource(Map uri) throws  
	NoAccessException, NoSuchObjectException, TypeMismatchException;
	
	/*
	public long getResourceLength(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException;
	*/
	
	public void removeObject(Map uri) throws  
	NoAccessException, NoSuchObjectException;
	
	/*
	public Date getLastModified(Map uri) throws NoAccessException, 
	NoSuchObjectException;

	public Date getCreationDate(Map uri) throws NoAccessException, 
	NoSuchObjectException;
	*/

	public String[] getChildrenNames(Map uri) throws  
	NoAccessException, NoSuchObjectException;
	
	public Map getProperties(Map uri) throws  
	NoAccessException, NoSuchObjectException;
	
	public void lockResource(Map uri, String lockId, String lockSubject, 
			Date lockExpirationDate, String lockOwnerInfo)
	throws NoAccessException, NoSuchObjectException, 
	LockException, TypeMismatchException;
	
	public void unlockResource(Map uri, String lockId) throws  
	NoAccessException, NoSuchObjectException, TypeMismatchException;
	
	public void copyObject(Map sourceUri, Map targetUri, boolean overwrite, boolean recursive)
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException;
	
	public void moveObject(Map sourceUri, Map targetUri, boolean overwrite)
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException;
}
