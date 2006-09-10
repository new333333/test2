package com.sitescape.ef.repository.file;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositorySessionFactory;
import com.sitescape.ef.util.Constants;

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
