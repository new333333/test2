package com.sitescape.ef.repository;

import java.io.OutputStream;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;
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

	public static String create(Binder binder, Entry entry,
			FileUploadItem fui) throws RepositoryServiceException {
		String repositoryServiceName = fui.getRepositoryServiceName();

		RepositoryService service = lookupRepositoryService(repositoryServiceName);

		Object session = service.openRepositorySession();
		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			return service.create(session, binder, entry, fui
					.getMultipartFile().getOriginalFilename(), fui
					.getMultipartFile());
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void update(Binder binder, Entry entry,
			FileUploadItem fui) throws RepositoryServiceException {
		String repositoryServiceName = fui.getRepositoryServiceName();

		RepositoryService service = lookupRepositoryService(repositoryServiceName);

		Object session = service.openRepositorySession();
		try {
			service.update(session, binder, entry, fui.getMultipartFile()
					.getOriginalFilename(), fui.getMultipartFile());
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void read(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName, OutputStream out)
			throws RepositoryServiceException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.read(session, binder, entry, fileName, out);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void checkout(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName)
			throws RepositoryServiceException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.checkout(session, binder, entry, fileName);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void uncheckout(String repositoryServiceName, Binder binder, 
			Entry entry,String fileName)
			throws RepositoryServiceException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.uncheckout(session, binder, entry, fileName);
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
