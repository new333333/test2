package com.sitescape.ef.repository;

import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;

public interface RepositoryService {

	public void write(Folder folder, FolderEntry entry, MultipartFile mf) 
		throws RepositoryServiceException;
	
	public void read(Folder folder, FolderEntry entry, String fileName, OutputStream out) 
		throws RepositoryServiceException;
}
