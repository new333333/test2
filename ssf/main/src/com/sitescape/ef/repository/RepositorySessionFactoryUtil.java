package com.sitescape.ef.repository;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.util.SpringContextUtil;

public class RepositorySessionFactoryUtil {

	public static RepositorySessionFactory getRepositorySessionFactory
		(String repositoryName) throws RepositoryException {
		RepositorySessionFactory factory = (RepositorySessionFactory)
			SpringContextUtil.getBean(repositoryName);
		
		if(factory == null)
			throw new RepositoryException("Repository with name '" +
					repositoryName + "' is not found");
		
		return factory;
	}
	
	public static RepositorySession openSession(String repositoryName) 
	throws RepositoryException, UncheckedIOException {
		RepositorySessionFactory factory = getRepositorySessionFactory(repositoryName);
		return factory.openSession();
	}
}
