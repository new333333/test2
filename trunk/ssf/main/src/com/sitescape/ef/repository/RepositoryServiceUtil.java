package com.sitescape.ef.repository;

import java.io.OutputStream;

import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.util.FileUploadItem;

public class RepositoryServiceUtil {
	
	public static String create(Folder folder, FolderEntry entry, FileUploadItem fui) 
	throws RepositoryServiceException {
	String repositoryServiceName = fui.getRepositoryServiceName();
	RepositoryService service = (RepositoryService) SpringContextUtil.getBean(repositoryServiceName);
	if(service == null)
		throw new RepositoryServiceException("Repository service '" + repositoryServiceName + "' not found");
	Object session = service.openRepositorySession();
	try {
		// TODO For now we ignore file path relative to the owning entry. 
		// We simply treat that the file path is identical to the file name. 
		return service.create(session, folder, entry, fui.getMultipartFile().getOriginalFilename(), fui.getMultipartFile());
	}
	finally {
		service.closeRepositorySession(session);
	}
}

	public static void update(Folder folder, FolderEntry entry, FileUploadItem fui) 
	throws RepositoryServiceException {
	String repositoryServiceName = fui.getRepositoryServiceName();
	RepositoryService service = (RepositoryService) SpringContextUtil.getBean(repositoryServiceName);
	if(service == null)
		throw new RepositoryServiceException("Repository service '" + repositoryServiceName + "' not found");
	Object session = service.openRepositorySession();
	try {
		service.update(session, folder, entry, fui.getMultipartFile().getOriginalFilename(), fui.getMultipartFile());
	}
	finally {
		service.closeRepositorySession(session);
	}
}

	public static void read(Folder folder, FolderEntry entry, 
			String repositoryServiceName, String fileName, OutputStream out) 
		throws RepositoryServiceException {
		RepositoryService service = (RepositoryService) SpringContextUtil.getBean(repositoryServiceName);
		if(service == null)
			throw new RepositoryServiceException("Repository service '" + repositoryServiceName + "' not found");
		Object session = service.openRepositorySession();
		try {
			service.read(session, folder, entry, fileName, out);
		}
		finally {
			service.closeRepositorySession(session);
		}		
	}
	
	public static String getDefaultRepositoryServiceName() {
		return SPropsUtil.getString("default.repository.service", "internalWebdavRepositoryService");
	}
}
