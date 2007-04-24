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
package com.sitescape.team.repository;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.repository.archive.ArchiveStore;

public interface RepositorySessionFactory {

	public void initialize() throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Shuts down the factory. 
	 * <p>
	 * Possible errors are logged rather than thrown as exceptions as there is
	 * little that a client application could do in such a case. 
	 *
	 */
	public void shutdown();
	
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
	public boolean isVersionDeletionAllowed();
	
	/**
	 * Returns whether the repository supports smart checkin or not.
	 * Smart checkin means that, when checkin() is invoked, it will create
	 * a new version only if there has been any modification to the file
	 * since the last checkout. If the file has not been checked out or
	 * it has but there has been no modifications to it, then it will simply
	 * return the name of the latest existing version. 
	 * If smart checkin is not supported, the repository is not capable of
	 * keeping track of such state. Consequently, there is a danger that
	 * each invocation of checkin() may create a new version even when it
	 * is not necessary or desirable. In such case, it becomes the client's
	 * responsibility/burden to keep track of such state information and 
	 * decide whether it must call checkin() or not based on that information. 
	 * @return
	 */
	public boolean supportSmartCheckin();
	
	public ArchiveStore getArchiveStore();
}
