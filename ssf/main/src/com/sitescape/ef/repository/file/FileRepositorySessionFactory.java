package com.sitescape.ef.repository.file;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositorySessionFactory;

public class FileRepositorySessionFactory implements RepositorySessionFactory {

	private String repositoryRootDir;

	public String getRepositoryRootDir() {
		return repositoryRootDir;
	}

	public void setRepositoryRootDir(String repositoryRootDir) {
		if(repositoryRootDir.endsWith("/"))
			this.repositoryRootDir = repositoryRootDir;
		else
			this.repositoryRootDir = repositoryRootDir + "/";
	}

	public void initialize() throws RepositoryServiceException, UncheckedIOException {
	}

	public void shutdown() throws RepositoryServiceException, UncheckedIOException {
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		return new FileRepositorySession(repositoryRootDir);
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean supportVersionDeletion() {
		return false;
	}
}
