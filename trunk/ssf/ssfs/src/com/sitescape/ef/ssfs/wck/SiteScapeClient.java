package com.sitescape.ef.ssfs.wck;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

public class SiteScapeClient {

	public static boolean objectExists(Map uri) throws SiteScapeClientException,
	NoAccessException {
		return false;
	}
	
	public static void createResource(Map uri) throws SiteScapeClientException,
	NoAccessException, AlreadyExistsException {
	}
	
	public static void setResource(Map uri, InputStream content) 
	throws SiteScapeClientException, NoAccessException, NoSuchObjectException {	
	}
	
	public static InputStream getResource(Map uri) throws SiteScapeClientException,
	NoAccessException, NoSuchObjectException {
		// TODO
		return null;
	}
	
	public static long getResourceLength(Map uri) throws SiteScapeClientException,
	NoAccessException, NoSuchObjectException {
		// TODO
		return 0;
	}
	
	public static void removeResource(Map uri) throws SiteScapeClientException,
	NoAccessException, NoSuchObjectException {
		
	}
	
	public static Date getLastModified(Map uri) throws SiteScapeClientException,
	NoAccessException, NoSuchObjectException {
		// TODO
		return null;
	}
	public static Date getCreationDate(Map uri) throws SiteScapeClientException,
	NoAccessException, NoSuchObjectException {
		// TODO
		return null;
	}
	
	public static String[] getChildrenNames(Map uri) throws SiteScapeClientException,
	NoAccessException, NoSuchObjectException {
		// TODO
		return new String[0];
	}
}
