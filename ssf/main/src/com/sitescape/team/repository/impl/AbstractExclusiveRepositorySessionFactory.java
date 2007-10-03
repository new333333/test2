package com.sitescape.team.repository.impl;

import javax.activation.FileTypeMap;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.repository.ExclusiveRepositorySessionFactory;
import com.sitescape.team.repository.RepositorySession;

public abstract class AbstractExclusiveRepositorySessionFactory 
	extends AbstractRepositorySessionFactory implements ExclusiveRepositorySessionFactory {

	public abstract class AbstractExclusiveRepositoryDataSource extends AbstractRepositoryDataSource {
		public AbstractExclusiveRepositoryDataSource(Binder binder, DefinableEntity entity, 
				String relativeFilePath, String versionName, FileTypeMap fileMap) {
			super(binder, entity, relativeFilePath, versionName, fileMap);
		}
		
		protected RepositorySession createSessionForDataSource() {
			return openSession();
		}
	}
}
