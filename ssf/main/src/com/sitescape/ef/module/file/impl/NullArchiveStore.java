package com.sitescape.ef.module.file.impl;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.module.file.ArchiveStore;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.VersionAttachment;

public class NullArchiveStore implements ArchiveStore {

	public String write(Binder binder, DefinableEntity entity, VersionAttachment v) throws UncheckedIOException {
		return null;
	}

}
