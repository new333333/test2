package com.sitescape.ef.repository.webdav;

import java.io.IOException;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;


public abstract class AbstractWebdavResourceFactory implements WebdavResourceFactory {

	protected String httpUrl;
	protected String username;
	protected String password;
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) throws URIException {
		this.httpUrl = httpUrl;
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
		HttpURL hrl = new HttpURL(httpUrl);
		hrl.setUserinfo(userName, password);
		SWebdavResource wdr = new SWebdavResource(hrl);
		
		//WebdavUtil.dump(wdr);
		
		return wdr;
	}
}
