package com.sitescape.ef.module.file.impl;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.module.file.ArchiveStore;

public class NullArchiveStore implements ArchiveStore {

	public String write(Binder binder, DefinableEntity entity, VersionAttachment v) throws UncheckedIOException {
		return null;
	}

}
