package com.sitescape.ef.repository;

import java.io.OutputStream;

import com.sitescape.ef.util.SPropsUtil;
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
		Object session = service.openRepositorySession();
		try {
			String filepath = fui.getMultipartFile().getOriginalFilename();
			
			boolean existingResource = service.exists(session, folder, entry, filepath);
			
			if(existingResource) {
				// It is an existing resource. We should make sure that the
				// resource is checked out. 
				// TODO Once we have a UI that allows users to manage checkout/
				// checkin procedures explicitly, then we should remove this
				// piece of code. 
				service.checkout(session, folder, entry, filepath);
			}
			
			service.write(session, folder, entry, filepath, fui.getMultipartFile());
			
			// TODO Until we have a UI that users can use to checkout and 
			// checkin file element explicitly, we have no choice but automate
			// the checkout/checkin process behind the scene. 
			service.checkin(session, folder, entry, filepath);
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
