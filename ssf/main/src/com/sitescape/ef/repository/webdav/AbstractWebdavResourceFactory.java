package com.sitescape.ef.webdav.client;

import org.apache.commons.httpclient.URIException;


public abstract class AbstractWebdavResourceFactory implements WebdavResourceFactory {

	protected String httpUrl;
	protected String docRootDir;
	
	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) throws URIException {
		this.httpUrl = httpUrl;
	}

	public String getDocRootDir() {
		return docRootDir;
	}

	public void setDocRootDir(String docRootDir) {
		this.docRootDir = docRootDir;
	}

}
