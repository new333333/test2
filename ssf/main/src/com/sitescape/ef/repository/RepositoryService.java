package com.sitescape.ef.repository;

import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;

public interface RepositoryService {

	//public static final String DEFAULT_REPOSITORY_SERVICE = "defaultWebdavRepositoryService";
	public static final String DEFAULT_REPOSITORY_SERVICE = "fileRepositoryService";
	
	/**
	 * Writes the file resource to the repository system. 
	 * <p>
	 * If the underlying repository system supports versioning and the specified
	 * file resource already exists, it creates a new version. If the repository
	 * does not support versioning, it overrides the existing resource. 
	 * <p>
	 * When the specified file resource already exists, it is expected that the
	 * caller has 
	 * 
	 * @param folder
	 * @param entry
	 * @param mf
	 * @throws RepositoryServiceException
	 */
	public void write(Folder folder, FolderEntry entry, MultipartFile mf) 
		throws RepositoryServiceException;
	
	public void read(Folder folder, FolderEntry entry, String fileName, OutputStream out) 
		throws RepositoryServiceException;
	
	/**
	 * Returns whether the repository service 
	 * @return
	 */
	//public boolean supportVersioning();
}
