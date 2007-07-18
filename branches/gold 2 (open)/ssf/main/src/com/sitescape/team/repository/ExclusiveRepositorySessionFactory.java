package com.sitescape.team.repository;

import com.sitescape.team.UncheckedIOException;

public interface ExclusiveRepositorySessionFactory extends RepositorySessionFactory {

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException;
	
}
