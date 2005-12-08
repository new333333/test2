package com.sitescape.ef.repository.webdav;

import java.io.IOException;

/**
 * Defines interface for obtaining connection to Webdav resource.
 * 
 * @author jong
 *
 */
public interface WebdavResourceFactory {
	
	public SWebdavResource openResource() throws IOException;
	
	public void closeResource(SWebdavResource resource) throws IOException;
}
