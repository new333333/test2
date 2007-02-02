package com.sitescape.team.module.file.impl;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.module.file.ArchiveStore;

public class NullArchiveStore implements ArchiveStore {

	public String write(Binder binder, DefinableEntity entity, VersionAttachment v) throws UncheckedIOException {
		return null;
	}

}
