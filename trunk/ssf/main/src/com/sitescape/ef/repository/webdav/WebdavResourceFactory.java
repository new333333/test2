package com.sitescape.ef.webdav.client;

import java.io.IOException;

import org.apache.commons.httpclient.URIException;
import org.apache.webdav.lib.WebdavResource;

public interface WebdavResourceFactory {
	
	public String getHttpUrl();

	public void setHttpUrl(String httpUrl) throws URIException;

	public String getDocRootDir();

	public void setDocRootDir(String docRootDir);

	public WebdavResource openSession(String userName, String password) 
		throws IOException;
}
