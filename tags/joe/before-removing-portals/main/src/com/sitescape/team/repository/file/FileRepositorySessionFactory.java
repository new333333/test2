/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.repository.file;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.impl.AbstractExclusiveRepositorySessionFactory;
import com.sitescape.team.util.Constants;

public class FileRepositorySessionFactory extends AbstractExclusiveRepositorySessionFactory 
implements FileRepositorySessionFactoryMBean {

	private String repositoryRootDir;

	public String getRepositoryRootDir() {
		return repositoryRootDir;
	}

	public void setRepositoryRootDir(String repositoryRootDir) {
		if(repositoryRootDir.endsWith(Constants.SLASH))
			this.repositoryRootDir = repositoryRootDir;
		else
			this.repositoryRootDir = repositoryRootDir + Constants.SLASH;
	}

	public void shutdown() {
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		return new FileRepositorySession(this, repositoryRootDir);
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean isVersionDeletionAllowed() {
		return true;
	}

	public boolean supportSmartCheckin() {
		return true;
	}

	public DataSource getDataSourceVersioned(Binder binder,
			DefinableEntity entity, String relativeFilePath,
			String versionName, FileTypeMap fileTypeMap)
			throws RepositoryServiceException, UncheckedIOException {
		return new FileRepositoryDataSource(binder, entity, relativeFilePath, 
				versionName, fileTypeMap);
	}
	
	public class FileRepositoryDataSource extends AbstractExclusiveRepositoryDataSource {
		public FileRepositoryDataSource(Binder binder, DefinableEntity entity, 
				String relativeFilePath, String versionName, FileTypeMap fileMap) {
			super(binder, entity, relativeFilePath, versionName, fileMap);
		}
	}
}
