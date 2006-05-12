package com.sitescape.ef.module.folder;

import com.sitescape.ef.domain.Principal;

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
