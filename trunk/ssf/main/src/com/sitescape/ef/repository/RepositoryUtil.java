package com.sitescape.ef.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Binder;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.SPropsUtil;

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
public class RepositoryUtil {

	private static final Log logger = LogFactory.getLog(RepositoryUtil.class);
	
	public static int fileInfo(String repositoryName,
			Binder binder, DefinableEntity entry, String relativeFilePath)
		throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);
		try {
			return session.fileInfo(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}		
	}
	
	public static String createVersioned(Binder binder, DefinableEntity entry,
			FileUploadItem fui) throws RepositoryServiceException,
			UncheckedIOException {
		String repositoryName = fui.getRepositoryName();

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

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
			throw new RepositoryServiceException(e);
		} finally {
			session.close();
		}
	}

	public static void createUnversioned(String repositoryName,
			Binder binder, DefinableEntity entry, String relativeFilePath, InputStream in) 
		throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			session.createUnversioned(binder, entry, relativeFilePath, in);
		} finally {
			session.close();
		}
	}

	public static void update(Binder binder, DefinableEntity entry,
			FileUploadItem fui) throws RepositoryServiceException,
			UncheckedIOException {
		String repositoryName = fui.getRepositoryName();

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

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
			throw new RepositoryServiceException(e);
		} finally {
			session.close();
		}
	}
	
	public static void delete(String repositoryName, Binder binder,
			DefinableEntity entry, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			session.delete(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}		
	}

	public static void read(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, OutputStream out)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			session.read(binder, entry, relativeFilePath, out);
		} finally {
			session.close();
		}
	}

	public static void readVersion(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, String versionName, OutputStream out)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			session.readVersion(binder, entry, relativeFilePath, versionName, out);
		} finally {
			session.close();
		}
	}

	public static InputStream read(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		InputStream in = null;
		try {
			in = session.read(binder, entry, relativeFilePath);
			return new SessionWrappedInputStream(in, session);
		} finally {
			if(in == null)
				session.close();
		}
	}
	
	public static InputStream readVersion(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, String versionName)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		InputStream in = null;
		try {
			in = session.readVersion(binder, entry, relativeFilePath, versionName);
			return new SessionWrappedInputStream(in, session);
		} finally {
			if(in == null)
				session.close();
		}
	}

	public static DataSource getDataSource(String repositoryName, Binder binder, 
			DefinableEntity entity, String relativeFilePath, 
			FileTypeMap fileTypeMap) throws RepositoryServiceException,
			UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			return session.getDataSource(binder, entity, relativeFilePath, fileTypeMap);
		} finally {
			session.close();
		}	
	}
	
	public static void checkout(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			session.checkout(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void checkin(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			session.checkin(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void uncheckout(String repositoryName, Binder binder, 
			DefinableEntity entry,String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			session.uncheckout(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static String getDefaultRepositoryName() {
		return SPropsUtil.getString("repository.default",
				"simpleFileRepository");
	}
	
	/**
	 * Returns entity path. The returned path does not contain root path and
	 * it always ends with a separator character.  
	 * 
	 * @param binder
	 * @param entry
	 * @param separator
	 * @return
	 */
	public static String getEntityPath(Binder binder, DefinableEntity entry, String separator) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(zoneName).
			append(separator).
			append(binder.getId()).
			append(separator).
			append(entry.getTypedId()).
			append(separator).toString();
	}
	
	public static void move(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, Binder destBinder, 
			DefinableEntity destEntry, String destRelativeFilePath)
	throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			session.move(binder, entry, relativeFilePath, destBinder, destEntry, destRelativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void deleteVersion(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, String versionName)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			session.deleteVersion(binder, entry, relativeFilePath, versionName);
		} finally {
			session.close();
		}
	}

	public static List<String> getVersionNames(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			return session.getVersionNames(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

}
