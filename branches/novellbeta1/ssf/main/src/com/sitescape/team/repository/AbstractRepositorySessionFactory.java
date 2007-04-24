package com.sitescape.team.repository;

import com.sitescape.team.repository.archive.ArchiveStore;

public class AbstractRepositorySessionFactory {

	private ArchiveStore archiveStore;
	
	public void setArchiveStore(ArchiveStore archiveStore) {
		this.archiveStore = archiveStore;
	}
	public ArchiveStore getArchiveStore() {
		return archiveStore;
	}

}
