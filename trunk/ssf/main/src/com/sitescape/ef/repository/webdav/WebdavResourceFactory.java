package com.sitescape.ef.repository.webdav;

import java.io.IOException;

import org.apache.commons.httpclient.URIException;
import org.apache.webdav.lib.WebdavResource;

public interface WebdavResourceFactory {
	
	public String getHttpUrl();

	public void setHttpUrl(String httpUrl) throws URIException;

	public String getDocRootDir();

	public void setDocRootDir(String docRootDir);

	public SWebdavResource openSession(String userName, String password) 
		throws IOException;
}
