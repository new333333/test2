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
package com.sitescape.team.module.folder;

import com.sitescape.team.domain.Principal;

public class FileLockInfo {

	private String repositoryName;

	private String relativeFilePath;

	private Principal lockOwner;

	public FileLockInfo(String repositoryName, String relativeFilePath,
			Principal lockOwner) {
		this.repositoryName = repositoryName;
		this.relativeFilePath = relativeFilePath;
		this.lockOwner = lockOwner;
	}

	public Principal getLockOwner() {
		return lockOwner;
	}

	public String getRelativeFilePath() {
		return relativeFilePath;
	}

	public String getRepositoryName() {
		return repositoryName;
	}
}
