package com.sitescape.ef.repository;

import java.io.OutputStream;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.util.FileUploadItem;

/**
 * Convenience methods for repository service. 
 * Each of the method in this class opens and closes a new connection for
 * repository access, hence not efficient for an operation involving 
 * multiple interactions with the repository system. To reuse the same
 * connection for multiple requests, do not use this convenience methods 
 * but instead use RepositoryService interface directly. 
 * 
 * @author jong
 *
 */
public class RepositoryServiceUtil {

	public static String create(Folder folder, FolderEntry entry,
			FileUploadItem fui) throws RepositoryServiceException {
		String repositoryServiceName = fui.getRepositoryServiceName();

		RepositoryService service = lookupRepositoryService(repositoryServiceName);

		Object session = service.openRepositorySession();
		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			return service.create(session, folder, entry, fui
					.getMultipartFile().getOriginalFilename(), fui
					.getMultipartFile());
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void update(Folder folder, FolderEntry entry,
			FileUploadItem fui) throws RepositoryServiceException {
		String repositoryServiceName = fui.getRepositoryServiceName();

		RepositoryService service = lookupRepositoryService(repositoryServiceName);

		Object session = service.openRepositorySession();
		try {
			service.update(session, folder, entry, fui.getMultipartFile()
					.getOriginalFilename(), fui.getMultipartFile());
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void read(String repositoryServiceName, Folder folder, 
			FolderEntry entry, String fileName, OutputStream out)
			throws RepositoryServiceException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.read(session, folder, entry, fileName, out);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void checkout(String repositoryServiceName, Folder folder, 
			FolderEntry entry, String fileName)
			throws RepositoryServiceException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.checkout(session, folder, entry, fileName);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void uncheckout(String repositoryServiceName, Folder folder, 
			FolderEntry entry,String fileName)
			throws RepositoryServiceException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.uncheckout(session, folder, entry, fileName);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static RepositoryService lookupRepositoryService(
			String repositoryServiceName) throws ConfigurationException {
		RepositoryService service = (RepositoryService) SpringContextUtil
				.getBean(repositoryServiceName);
		if (service == null)
			throw new ConfigurationException("Repository service '"
					+ repositoryServiceName + "' not found");
		return service;
	}

	public static String getDefaultRepositoryServiceName() {
		return SPropsUtil.getString("default.repository.service",
				"internalWebdavRepositoryService");
	}
}
