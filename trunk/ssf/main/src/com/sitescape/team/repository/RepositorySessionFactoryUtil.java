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

import com.sitescape.team.InternalException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.repository.archive.ArchiveStore;
import com.sitescape.team.repository.fi.FIRepositorySessionFactoryAdapter;
import com.sitescape.team.util.SpringContextUtil;

public class RepositorySessionFactoryUtil {

	public static RepositorySessionFactory getRepositorySessionFactory
		(String repositoryName) throws RepositoryServiceException {
		RepositorySessionFactory factory = (RepositorySessionFactory)
			SpringContextUtil.getBean(repositoryName);
		
		if(factory == null)
			throw new RepositoryServiceException("Repository with name '" +
					repositoryName + "' is not found");
		
		return factory;
	}
	
	public static RepositorySession openSession(Binder binder, String repositoryName) 
	throws RepositoryServiceException, UncheckedIOException {
		RepositorySessionFactory factory = getRepositorySessionFactory(repositoryName);
		
		if(factory instanceof FIRepositorySessionFactoryAdapter)
			return ((FIRepositorySessionFactoryAdapter) factory).openSession(binder.getResourceDriverName());
		else if(factory instanceof ExclusiveRepositorySessionFactory)
			return ((ExclusiveRepositorySessionFactory) factory).openSession();
		else
			throw new InternalException("This should not occur");
	}
	
	public static ArchiveStore getArchiveStore(String repositoryName) {
		if(ObjectKeys.FI_ADAPTER.equals(repositoryName)) {
			return null;
		}
		else {
			return getRepositorySessionFactory(repositoryName).getArchiveStore();
		}
	}
}
