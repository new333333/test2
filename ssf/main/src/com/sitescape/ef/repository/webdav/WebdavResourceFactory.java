package com.sitescape.ef.repository.webdav;

import java.io.IOException;

import org.apache.commons.httpclient.URIException;

/**
 * Defines interface for obtaining connection to Webdav resource.
 * 
 * @author jong
 *
 */
public interface WebdavResourceFactory {
	
	public String getHttpUrl();

	public void setHttpUrl(String httpUrl) throws URIException;
	
	public void setUsername(String username);
	
	public void setPassword(String password);

	public SWebdavResource openResource() throws IOException;
	
	public void closeResource(SWebdavResource resource) throws IOException;
}
