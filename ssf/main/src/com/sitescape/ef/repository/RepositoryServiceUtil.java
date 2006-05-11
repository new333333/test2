package com.sitescape.ef.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.DefinableEntity;
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

	private static final Log logger = LogFactory.getLog(RepositoryServiceUtil.class);
	
	public static int fileInfo(String repositoryServiceName,
			Binder binder, DefinableEntity entry, String relativeFilePath)
		throws RepositoryServiceException, UncheckedIOException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);

		Object session = service.openRepositorySession();
		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			return service.fileInfo(session, binder, entry, relativeFilePath);
		} finally {
			service.closeRepositorySession(session);
		}		
	}
	
	public static String createVersioned(Binder binder, DefinableEntity entry,
			FileUploadItem fui) throws RepositoryServiceException,
			UncheckedIOException {
		String repositoryServiceName = fui.getRepositoryServiceName();

		RepositoryService service = lookupRepositoryService(repositoryServiceName);

		Object session = service.openRepositorySession();
		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			InputStream is = fui.getInputStream();
			try {
				return service.createVersioned(session, binder, entry, fui
					.getOriginalFilename(), is);
			}
			finally {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void createUnversioned(String repositoryServiceName,
			Binder binder, DefinableEntity entry, String relativeFilePath, InputStream in) 
		throws RepositoryServiceException, UncheckedIOException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);

		Object session = service.openRepositorySession();
		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			service.createUnversioned(session, binder, entry, relativeFilePath, in);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void update(Binder binder, DefinableEntity entry,
			FileUploadItem fui) throws RepositoryServiceException,
			UncheckedIOException {
		String repositoryServiceName = fui.getRepositoryServiceName();

		RepositoryService service = lookupRepositoryService(repositoryServiceName);

		Object session = service.openRepositorySession();
		try {
			InputStream is = fui.getInputStream();
			
			try {
				service.update(session, binder, entry, fui.getOriginalFilename(), is);
			}
			finally {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		} finally {
			service.closeRepositorySession(session);
		}
	}
	
	public static void delete(String repositoryServiceName, Binder binder,
			DefinableEntity entry, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.delete(session, binder, entry, relativeFilePath);
		} finally {
			service.closeRepositorySession(session);
		}		
	}

	public static void read(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, OutputStream out)
			throws RepositoryServiceException, UncheckedIOException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.read(session, binder, entry, relativeFilePath, out);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static InputStream read(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			return service.read(session, binder, entry, relativeFilePath);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void checkout(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.checkout(session, binder, entry, relativeFilePath);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void checkin(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.checkin(session, binder, entry, relativeFilePath);
		} finally {
			service.closeRepositorySession(session);
		}
	}

	public static void uncheckout(String repositoryServiceName, Binder binder, 
			DefinableEntity entry,String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositoryService service = lookupRepositoryService(repositoryServiceName);
		Object session = service.openRepositorySession();
		try {
			service.uncheckout(session, binder, entry, relativeFilePath);
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
				"fileRepositoryService");
	}
}
