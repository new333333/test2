package com.sitescape.ef.module.file;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.VersionAttachment;

public interface ArchiveStore {
	
	/**
	 * Archive the file version and return its unique URI or <code>null</code>
	 * if archiving is not supported.
	 * 
	 * @param binder
	 * @param entity
	 * @param v
	 * @return	archive URI or <code>null</code>
	 */
	public String write(Binder binder, DefinableEntity entity, 
			VersionAttachment v) throws UncheckedIOException; 
}
