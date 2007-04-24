package com.sitescape.team.repository.fi;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.RepositorySessionFactory;

public interface FIRepositorySessionFactoryAdapter extends RepositorySessionFactory {

	public RepositorySession openSession(String resourceDriverName) 
	throws FIException, UncheckedIOException;
}
