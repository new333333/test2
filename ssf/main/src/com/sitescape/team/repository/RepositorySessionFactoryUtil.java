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
	
	public static RepositorySession openSession(String repositoryName) 
	throws RepositoryServiceException, UncheckedIOException {
		RepositorySessionFactory factory = getRepositorySessionFactory(repositoryName);
		return factory.openSession();
	}
}
