package com.sitescape.team.repository.file;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.RepositorySessionFactory;
import com.sitescape.team.util.Constants;

public class FileRepositorySessionFactory implements RepositorySessionFactory {

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
		return new FileRepositorySession(repositoryRootDir);
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean isVersionDeletionAllowed() {
		return true;
	}
}
