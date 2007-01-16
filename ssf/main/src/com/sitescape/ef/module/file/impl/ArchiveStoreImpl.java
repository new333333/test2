package com.sitescape.ef.module.file.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.module.file.ArchiveStore;
import com.sitescape.ef.repository.RepositoryUtil;
import com.sitescape.ef.util.FileStore;

public class ArchiveStoreImpl implements ArchiveStore {

	private FileStore fileStore;

	public void setRootPath(String rootPath) {
		this.fileStore = new FileStore(rootPath);		
	}
	
	public String write(Binder binder, DefinableEntity entity, 
			VersionAttachment v) throws UncheckedIOException {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();

		String path = getPath(zoneName, binder.getId(), entity.getTypedId(),
				v.getRepositoryName(), v.getVersionNumber(), v.getFileItem().getName());
		
		
		InputStream content = RepositoryUtil.readVersion(v.getRepositoryName(),
				binder, entity, v.getFileItem().getName(), v.getVersionName());

		try {
			fileStore.writeFile(path, content);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
		finally {
			try {
				content.close();
			} catch (IOException ignore) {}
		}
		
		return path;
	}

	private String getPath(String zoneName, Long binderId, String entityId, 
			String repositoryName, int versionNo, String fileName) {
		return new StringBuilder()
		.append(zoneName)
		.append(File.separator)
		.append(binderId)
		.append(File.separator)
		.append(entityId)
		.append(File.separator)
		.append(repositoryName)
		.append(File.separator)
		.append(versionNo)
		.append(File.separator)
		.append(fileName)
		.toString();
	}
}
