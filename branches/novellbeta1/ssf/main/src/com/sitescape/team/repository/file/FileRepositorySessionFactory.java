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
package com.sitescape.team.repository.file;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.repository.AbstractRepositorySessionFactory;
import com.sitescape.team.repository.ExclusiveRepositorySessionFactory;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.util.Constants;

public class FileRepositorySessionFactory extends AbstractRepositorySessionFactory 
implements ExclusiveRepositorySessionFactory, FileRepositorySessionFactoryMBean {

	private String repositoryRootDir;

	public String getRepositoryRootDir() {
		return repositoryRootDir;
	}

	public void setRepositoryRootDir(String repositoryRootDir) {
		if(repositoryRootDir.endsWith(Constants.SLASH))
			this.repositoryRootDir = repositoryRootDir;
		else
			this.repositoryRootDir = repositoryRootDir + Constants.SLASH;
	}

	public void initialize() throws RepositoryServiceException, UncheckedIOException {
	}

	public void shutdown() {
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		return new FileRepositorySession(this, repositoryRootDir);
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean isVersionDeletionAllowed() {
		return true;
	}

	public boolean supportSmartCheckin() {
		return true;
	}
}
