package com.sitescape.team.repository;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.repository.archive.ArchiveStore;

public class AbstractRepositorySessionFactory {

	private ArchiveStore archiveStore;
	
	public void setArchiveStore(ArchiveStore archiveStore) {
		this.archiveStore = archiveStore;
	}
	public ArchiveStore getArchiveStore() {
		return archiveStore;
	}

	public void initialize() throws RepositoryServiceException, UncheckedIOException {
		if(archiveStore == null)
			throw new ConfigurationException("Archive store must be specified");
	}
}
