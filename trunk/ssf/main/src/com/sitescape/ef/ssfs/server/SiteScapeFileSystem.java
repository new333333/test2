package com.sitescape.ef.ssfs.server;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;

public interface SiteScapeFileSystem {

	public boolean objectExists(Map uri) throws NoAccessException;
	
	public void createResource(Map uri) throws NoAccessException, AlreadyExistsException;
	
	public void setResource(Map uri, InputStream content) 
	throws NoAccessException, AlreadyExistsException;
	
	public InputStream getResource(Map uri) throws NoAccessException,
	NoSuchObjectException;
	
	public long getResourceLength(Map uri) throws NoAccessException, 
	NoSuchObjectException;
	
	public void removeResource(Map uri) throws NoAccessException,
	NoSuchObjectException;
	
	public Date getLastModified(Map uri) throws NoAccessException, 
	NoSuchObjectException;

	public Date getCreationDate(Map uri) throws NoAccessException, 
	NoSuchObjectException;

	public String[] getChildrenNames(Map uri) throws NoAccessException,
	NoSuchObjectException;
	
	public Map getProperties(Map uri) throws NoAccessException,
	NoSuchObjectException;
}
