/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.repository.webdav;

import java.io.IOException;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.RepositorySessionFactory;
import com.sitescape.team.util.Constants;

public class WebdavRepositorySessionFactory implements RepositorySessionFactory,
WebdavRepositorySessionFactoryMBean {

	protected Log logger = LogFactory.getLog(getClass());

	protected String hostUrl;
	protected String contextPath;
	protected String docRootPath; // This does not include context path
	protected String username;
	protected String password;
	protected boolean versionDeletionAllowed = false;

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	public String getHostUrl() {
		return hostUrl;
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
	public String getContextPath() {
		return contextPath;
	}

	public void setDocRootPath(String docRootPath) {
		if(docRootPath.startsWith(Constants.SLASH))
			docRootPath = docRootPath.substring(1);
		
		if(docRootPath.endsWith(Constants.SLASH))
			this.docRootPath = docRootPath;
		else
			this.docRootPath = docRootPath + Constants.SLASH;
	}
	public String getDocRootPath() {
		return docRootPath;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	
	public void initialize() throws RepositoryServiceException, UncheckedIOException {
		// Test if we can make a connection. This will check whether the webdav 
		// server is properly set up or not. Better find problem at startup time.
		
		try {
			RepositorySession ses = openSession();
			ses.close();
		}
		catch(RepositoryServiceException e) {
			logger.error("Failed to initialize. It appears that the webdav repository is not configured properly");
			throw e;
		}
		catch(UncheckedIOException e) {
			logger.error("Failed to initialize. It appears that the webdav repository is not configured properly"); 
			throw e;
		}
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

	public boolean isVersionDeletionAllowed() {
		// It appears that the Slide server we use does not allows this.
		// It doesn't appear to me to be a restriction by the DeltaV spec
		// itself, but some Slide specific misbehavior (or mis-configuration).
		// It requires more investigation...
		// Addendum (4/6/06) - It is observed that WebDAV based SCM tool
		// called Subversion does not support version deletion either,
		// which sort of indicates that now allowing version deletion 
		// is a general practice in WebDAV world. Just observation...

		return versionDeletionAllowed;
	}
	
	public void setVersionDeletionAllowed(boolean versionDeletionAllowed) {
		this.versionDeletionAllowed = versionDeletionAllowed;
	}

	protected String getHttpUrl() {
		return hostUrl + contextPath;
	}	
}
