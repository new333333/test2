package com.sitescape.ef.repository.file;

import java.io.IOException;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.repository.RepositoryException;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositorySessionFactory;
import com.sitescape.ef.util.ConfigPropertyNotFoundException;
import com.sitescape.ef.util.SPropsUtil;

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

	public void initialize() throws RepositoryException, UncheckedIOException {
	}

	public void shutdown() throws RepositoryException, UncheckedIOException {
	}

	public RepositorySession openSession() throws RepositoryException, UncheckedIOException {
		return new FileRepositorySession(repositoryRootDir);
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean supportVersionDeletion() {
		return false;
	}
}
