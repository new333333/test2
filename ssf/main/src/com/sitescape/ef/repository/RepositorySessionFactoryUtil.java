package com.sitescape.ef.repository;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.util.SpringContextUtil;

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
