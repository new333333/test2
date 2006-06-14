package com.sitescape.ef.repository;

import com.sitescape.ef.UncheckedIOException;

public interface RepositorySessionFactory {

	public void initialize() throws RepositoryServiceException, UncheckedIOException;
	
	public void shutdown() throws RepositoryServiceException, UncheckedIOException;
	
	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns whether or not the repository supports versioning.
	 * 
	 * @return
	 */
	public boolean supportVersioning();
	
	/**
	 * Returns whether the repository allows users to delete individual
	 * versions of a resource without deleting the entire resource. In other
	 * words, for repository system that does not support this, the only way
	 * to remove a particular resource is to delete it in its entirety which
	 * deletes all of its versions as well. For repository that does not
	 * support versioning, this method return <code>false</code>.
	 * 
	 * @return
	 */
	public boolean supportVersionDeletion();
}
