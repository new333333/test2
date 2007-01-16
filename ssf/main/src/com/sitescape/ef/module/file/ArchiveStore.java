package com.sitescape.ef.module.file;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.domain.DefinableEntity;

public interface ArchiveStore {

	/*
	public String write(String zoneName, Long binderId, String entityId, 
			String repositoryName, int versionNo, String fileName,
			InputStream content) throws UncheckedIOException;
			*/
	
	public String write(Binder binder, DefinableEntity entity, 
			VersionAttachment v) throws UncheckedIOException; 
}
