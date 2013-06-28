/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.repository.webdav;

import java.io.IOException;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.repository.RepositorySession;
import org.kablink.teaming.repository.impl.AbstractExclusiveRepositorySessionFactory;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.SWebdavResource;
import org.kablink.teaming.util.WebdavUtil;


public class WebdavRepositorySessionFactory extends AbstractExclusiveRepositorySessionFactory
implements WebdavRepositorySessionFactoryMBean {

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
		super.initialize();
		
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
			HttpURL hrl = WebdavUtil.getHttpUrl(getHttpUrlStr());
			hrl.setUserinfo(username, password);
			SWebdavResource wdr = new SWebdavResource(hrl);
			
			//WebdavUtil.dump(wdr);
			
			return new WebdavRepositorySession(this, wdr, contextPath + docRootPath);
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

	protected String getHttpUrlStr() {
		return hostUrl + contextPath;
	}
	public boolean supportSmartCheckin() {
		return true;
	}	
	
	public DataSource getDataSourceVersioned(Binder binder,
			DefinableEntity entity, String relativeFilePath,
			String versionName, Boolean isEncrypted, byte[] encryptionKey, FileTypeMap fileTypeMap)
			throws RepositoryServiceException, UncheckedIOException {
		return new WebdavRepositoryDataSource(binder, entity, relativeFilePath, 
				versionName, isEncrypted, encryptionKey, fileTypeMap);
	}

	public class WebdavRepositoryDataSource extends AbstractExclusiveRepositoryDataSource {
		public WebdavRepositoryDataSource(Binder binder, DefinableEntity entity, 
				String relativeFilePath, String versionName, 
				Boolean isEncrypted, byte[] encryptionKey, FileTypeMap fileMap) {
			super(binder, entity, relativeFilePath, versionName, isEncrypted, encryptionKey, fileMap);
		}
	}

}
