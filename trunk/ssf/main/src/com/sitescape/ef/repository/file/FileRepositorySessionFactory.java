package com.sitescape.ef.repository.file;

import java.io.IOException;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositorySessionFactory;
import com.sitescape.ef.util.ConfigPropertyNotFoundException;
import com.sitescape.ef.util.SPropsUtil;

public class FileRepositorySessionFactory implements RepositorySessionFactory {

	private String dataRootDir;
	private String subDirName;

	public void setDataRootDirProperty(String dataRootDirProperty)
			throws ConfigPropertyNotFoundException, IOException {
		this.dataRootDir = SPropsUtil.getDirPath(dataRootDirProperty);
	}

	public void setSubDirName(String subDirName) {
		this.subDirName = subDirName;
	}

	public void initialize() throws RepositoryServiceException, UncheckedIOException {
		if(dataRootDir == null || dataRootDir.length() == 0)
			throw new RepositoryServiceException("Data root directory must be specified");
		
		if(subDirName == null || subDirName.length() == 0)
			throw new RepositoryServiceException("Sub directory name must be specified");
	}

	public void shutdown() throws RepositoryServiceException, UncheckedIOException {
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		return new FileRepositorySession(dataRootDir, subDirName);
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean supportVersionDeletion() {
		return false;
	}
}
