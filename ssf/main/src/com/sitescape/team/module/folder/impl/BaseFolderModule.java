package com.sitescape.team.module.folder.impl;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;

public class BaseFolderModule extends AbstractFolderModule implements BaseFolderModuleMBean {

	public boolean synchronize(Long folderId) throws FIException, UncheckedIOException {
		throw new UnsupportedOperationException("synchronize operation is not supported in the base edition");
	}
}
