package com.sitescape.team.repository.fi.impl;

import javax.activation.FileTypeMap;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.fi.FIRepositorySessionFactoryAdapter;
import com.sitescape.team.repository.impl.AbstractRepositorySessionFactory;

public abstract class AbstractFIRepositorySessionFactoryAdapter 
	extends AbstractRepositorySessionFactory implements FIRepositorySessionFactoryAdapter {
	
	public abstract class AbstractFIRepositoryDataSource extends AbstractRepositoryDataSource {
		public AbstractFIRepositoryDataSource(Binder binder, DefinableEntity entity, 
				String relativeFilePath, String versionName, FileTypeMap fileMap) {
			super(binder, entity, relativeFilePath, versionName, fileMap);
		}
		
		protected RepositorySession createSessionForDataSource() {
			return openSession(_binder.getResourceDriverName());
		}
	}

}
