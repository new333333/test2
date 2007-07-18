package com.sitescape.team.repository.fi.impl;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;
import com.sitescape.team.repository.AbstractRepositorySessionFactory;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.fi.FIRepositorySessionFactoryAdapter;

public class NullFIRepositorySessionFactoryAdapter extends AbstractRepositorySessionFactory
implements FIRepositorySessionFactoryAdapter {

	public RepositorySession openSession(String resourceDriverName) throws FIException, UncheckedIOException {
		return null;
	}

	public void initialize() throws RepositoryServiceException, UncheckedIOException {
	}

	public boolean isVersionDeletionAllowed() {
		return false;
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		return null;
	}

	public void shutdown() {
	}

	public boolean supportVersioning() {
		return false;
	}

	public boolean supportSmartCheckin() {
		return false;
	}

}
