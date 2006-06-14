package com.sitescape.ef.repository.webdav;

import java.io.IOException;

import org.apache.commons.httpclient.HttpURL;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositorySessionFactory;
import com.sitescape.ef.util.Constants;

public class WebdavRepositorySessionFactory implements RepositorySessionFactory {

	protected String hostUrl;
	protected String contextPath;
	protected String docRootPath; // This does not include context path
	protected String username;
	protected String password;

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	public void setContextPath(String contextPath) {
		// The context path must end with '/'. Otherwise it appears that
		// connection request to WebDAV server (Slide in particular) 
		// does not work. 
		if(contextPath.endsWith(Constants.SLASH))
			this.contextPath = contextPath;
		else
			this.contextPath = contextPath + Constants.SLASH;
	}

	public void setDocRootPath(String docRootPath) {
		if(docRootPath.startsWith(Constants.SLASH))
			docRootPath = docRootPath.substring(1);
		
		if(docRootPath.endsWith(Constants.SLASH))
			this.docRootPath = docRootPath;
		else
			this.docRootPath = docRootPath + Constants.SLASH;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void initialize() throws RepositoryServiceException, UncheckedIOException {
	}

	public void shutdown() {
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		try { 
			HttpURL hrl = new HttpURL(getHttpUrl());
			hrl.setUserinfo(username, password);
			SWebdavResource wdr = new SWebdavResource(hrl);
			
			//WebdavUtil.dump(wdr);
			
			return new WebdavRepositorySession(wdr, contextPath + docRootPath);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean supportVersionDeletion() {
		// It appears that the Slide server we use does not allows this.
		// It doesn't appear to me to be a restriction by the DeltaV spec
		// itself, but some Slide specific misbehavior (or mis-configuration).
		// It requires more investigation...
		// Addendum (4/6/06) - It is observed that WebDAV based SCM tool
		// called Subversion does not support version deletion either,
		// which sort of indicates that now allowing version deletion 
		// is a general practice in WebDAV world. Just observation...
		return false; // for now
	}

	protected String getHttpUrl() {
		return hostUrl + contextPath;
	}	
}
