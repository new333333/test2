package com.sitescape.ef.repository;

import java.io.OutputStream;

import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.util.FileUploadItem;

public class RepositoryServiceUtil {
	
	public static void write(Folder folder, FolderEntry entry, FileUploadItem fui) 
		throws RepositoryServiceException {
		String repositoryServiceName = fui.getRepositoryServiceName();
		RepositoryService service = (RepositoryService) SpringContextUtil.getBean(repositoryServiceName);
		if(service == null)
			throw new RepositoryServiceException("Repository service '" + repositoryServiceName + "' not found");
		// Currently we use the original file name (which comes from the client/browser)
		// as the pathname of the file. In other words, we do not distinguish them. 
		// However, the underlying repository API is designed to handle the file
		// pathname fully. 
		service.write(folder, entry, fui.getMultipartFile().getOriginalFilename(), fui.getMultipartFile());
	}

	public static void read(Folder folder, FolderEntry entry, 
			String repositoryServiceName, String fileName, OutputStream out) 
		throws RepositoryServiceException {
		RepositoryService service = (RepositoryService) SpringContextUtil.getBean(repositoryServiceName);
		if(service == null)
			throw new RepositoryServiceException("Repository service '" + repositoryServiceName + "' not found");
		service.read(folder, entry, fileName, out);
	}
}
