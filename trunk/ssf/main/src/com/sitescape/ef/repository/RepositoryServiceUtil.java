package com.sitescape.ef.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

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
		throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);
		try {
			return session.fileInfo(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}		
	}
	
	public static String createVersioned(Binder binder, DefinableEntity entry,
			FileUploadItem fui) throws RepositoryException,
			UncheckedIOException {
		String repositoryServiceName = fui.getRepositoryServiceName();

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			InputStream is = fui.getInputStream();
			try {
				return session.createVersioned(binder, entry, fui
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
			throw new RepositoryException(e);
		} finally {
			session.close();
		}
	}

	public static void createUnversioned(String repositoryServiceName,
			Binder binder, DefinableEntity entry, String relativeFilePath, InputStream in) 
		throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			session.createUnversioned(binder, entry, relativeFilePath, in);
		} finally {
			session.close();
		}
	}

	public static void update(Binder binder, DefinableEntity entry,
			FileUploadItem fui) throws RepositoryException,
			UncheckedIOException {
		String repositoryServiceName = fui.getRepositoryServiceName();

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			InputStream is = fui.getInputStream();
			
			try {
				session.update(binder, entry, fui.getOriginalFilename(), is);
			}
			finally {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (IOException e) {
			throw new RepositoryException(e);
		} finally {
			session.close();
		}
	}
	
	public static void delete(String repositoryServiceName, Binder binder,
			DefinableEntity entry, String relativeFilePath) 
		throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			session.delete(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}		
	}

	public static void read(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, OutputStream out)
			throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			session.read(binder, entry, relativeFilePath, out);
		} finally {
			session.close();
		}
	}

	public static InputStream read(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			return session.read(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static DataSource getDataSource(String repositoryServiceName, Binder binder, 
			DefinableEntity entity, String relativeFilePath, 
			FileTypeMap fileTypeMap) throws RepositoryException,
			UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			return session.getDataSource(binder, entity, relativeFilePath, fileTypeMap);
		} finally {
			session.close();
		}	
	}
	
	public static void checkout(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			session.checkout(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void checkin(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			session.checkin(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void uncheckout(String repositoryServiceName, Binder binder, 
			DefinableEntity entry,String relativeFilePath)
			throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			session.uncheckout(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static String getDefaultRepositoryServiceName() {
		return SPropsUtil.getString("default.repository.service",
				"fileRepositoryService");
	}
}
