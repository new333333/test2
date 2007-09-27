/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.repository.jcr.jackrabbit;

import java.io.File;
import java.io.IOException;

import javax.activation.FileTypeMap;
import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.TransientRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.repository.AbstractRepositorySessionFactory;
import com.sitescape.team.repository.ExclusiveRepositorySessionFactory;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.jcr.JCRRepositorySession;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.SPropsUtil;

public class JCRRepositorySessionFactory extends AbstractRepositorySessionFactory
implements ExclusiveRepositorySessionFactory, JCRRepositorySessionFactoryMBean {

	protected Log logger = LogFactory.getLog(getClass());

	protected FileTypeMap mimeTypes;
	protected String repositoryRootDir;
	protected String homeSubdirName;
	protected String configFileName;
	protected String username;
	protected char[] password;
	protected boolean initializeOnStartup;
	protected boolean versionDeletionAllowed = false;

	private String repositoryHomeDir;
	private boolean initialized; 
	private Repository repository;
	private String workspaceName;
	
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
	public String getConfigFileName() {
		return configFileName;
	}

	public void setHomeSubdirName(String homeSubdirName) {
		this.homeSubdirName = homeSubdirName;
	}
	public String getHomeSubdirName() {
		return homeSubdirName;
	}

	public void setPassword(String password) {
		this.password = password.toCharArray();
	}
	public String getPassword() {
		return new String(password);
	}

	public void setInitializeOnStartup(boolean initializeOnStartup) {
		this.initializeOnStartup = initializeOnStartup;
	}

	public void setRepositoryRootDir(String repositoryRootDir) {
		if(repositoryRootDir.endsWith(Constants.SLASH))
			this.repositoryRootDir = repositoryRootDir;
		else
			this.repositoryRootDir = repositoryRootDir + Constants.SLASH;
	}
	public String getRepositoryRootDir() {
		return repositoryRootDir;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}

	public void initialize() throws RepositoryServiceException, UncheckedIOException {
		super.initialize();
		
		repositoryHomeDir = repositoryRootDir + homeSubdirName;
		workspaceName = SPropsUtil.getString("jcr.workspace.name", "sitescape");
		
		try {
			repository = getRepository();
			
			if(initializeOnStartup)
				initializeRepository(repository);
		}
		catch(IOException e) {
			logger.error("Could not initialize Jackrabbit JCR", e);
			throw new UncheckedIOException(e);
		}
		catch(RepositoryException e) {
			logger.error("Could not initialize Jackrabbit JCR", e);
			throw new RepositoryServiceException(e);
		}
	}

	public void shutdown() {
		/*
		if(initialized) {
			try {
				Session session = createSession(null);
				JackrabbitRepository repository = (JackrabbitRepository) session.getRepository();
				repository.shutdown();
			}
			catch(RepositoryException e) {
				logger.error("Could not shutdown Jackrabbit properly");
			}
			finally {
				initialized = false;
			}
		}*/
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		try {
			return new JCRRepositorySession(this, workspaceName, createSession(workspaceName), mimeTypes);
		} catch (RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean isVersionDeletionAllowed() {
		return versionDeletionAllowed;
	}
	
	public void setVersionDeletionAllowed(boolean versionDeletionAllowed) {
		this.versionDeletionAllowed = versionDeletionAllowed;
	}

	private Session createSession(String workspaceName)
	throws RepositoryException {
		Credentials credentials = new SimpleCredentials(username, password);
	
		try {
			return repository.login(credentials, workspaceName);
		}
		catch (RepositoryException e) {
			logger.error("Failed to login to the workspace " + workspaceName);
	
			throw e;
		}
	}

	private Repository getRepository() throws IOException {
		File repositoryFile;
		
		File repositoryFileInRootDir = new File(repositoryRootDir + configFileName);
		if(repositoryFileInRootDir.exists()) {
			// Repository configuration file is found in the repository root
			// directory. This most likely indicates that the user has 
			// customized the repository configuration. 
			repositoryFile = repositoryFileInRootDir;
		}
		else {
			// In this case we use factory-shipped default configuration file.
			Resource configFileResource = new ClassPathResource("config/" + configFileName);
			try {
				repositoryFile = configFileResource.getFile();
			} catch (IOException e) {
				logger.error("Could not find factory shipped Jackrabbit repository configuration file", e);
				throw e;
			}
		}

		return new TransientRepository(repositoryFile.getAbsolutePath(), repositoryHomeDir);	
	}
	
	private void initializeRepository(Repository repository) throws RepositoryException {
		Session session = createSession(null);
		
		session.logout();
		
		initialized = true;		
	}

	public boolean supportSmartCheckin() {
		return true;
	}
	
}
