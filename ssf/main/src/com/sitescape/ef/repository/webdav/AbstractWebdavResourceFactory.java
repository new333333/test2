package com.sitescape.ef.repository.webdav;

import java.io.IOException;

import org.apache.commons.httpclient.HttpURL;

public abstract class AbstractWebdavResourceFactory implements WebdavResourceFactory {

	protected String hostUrl;
	protected String contextPath;
	protected String username;
	protected String password;
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	private String getHttpUrl() {
		return hostUrl + contextPath;
	}

	public SWebdavResource openResource() throws IOException {
		return openResource(username, password);
	}
	
	public void closeResource(SWebdavResource resource) throws IOException {
		resource.close();
	}

	/**
	 * Default implementation of opening a resource. 
	 * Subclass can override this method to deal with server-specific requirement.
	 *  
	 * @param userName
	 * @param password
	 * @return
	 * @throws IOException
	 */
	protected SWebdavResource openResource(String userName, String password) 
		throws IOException {
		HttpURL hrl = new HttpURL(getHttpUrl());
		hrl.setUserinfo(userName, password);
		SWebdavResource wdr = new SWebdavResource(hrl);
		
		//WebdavUtil.dump(wdr);
		
		return wdr;
	}
}
