/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.repository.impl.SessionWrappedInputStream;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.Utils;

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
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);
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

		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

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
					logger.error(e.getLocalizedMessage(), e);
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
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

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

		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			InputStream is = fui.getInputStream();
			
			try {
				session.update(binder, entry, fui.getOriginalFilename(), is);
			}
			finally {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage(), e);
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
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			session.delete(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}		
	}

	public static void delete(String repositoryName, Binder binder) 
		throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			session.delete(binder);
		} finally {
			session.close();
		}		
	}

	public static void deleteVersion(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, String versionName)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			session.deleteVersion(binder, entry, relativeFilePath, versionName);
		} finally {
			session.close();
		}
	}

	public static void readVersioned(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, String versionName, 
			String latestVersionName, OutputStream out)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			session.readVersioned(binder, entry, relativeFilePath, versionName, latestVersionName, out);
		} finally {
			session.close();
		}
	}

	public static InputStream readVersioned(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, String versionName, String latestVersionName)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		InputStream in = null;
		try {
			in = session.readVersioned(binder, entry, relativeFilePath, versionName, latestVersionName);
			return new SessionWrappedInputStream(in, session);
		} finally {
			if(in == null)
				session.close();
		}
	}

	public static void readUnversioned(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, OutputStream out)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			session.readUnversioned(binder, entry, relativeFilePath, out);
		} finally {
			session.close();
		}
	}

	public static InputStream readUnversioned(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		InputStream in = null;
		try {
			in = session.readUnversioned(binder, entry, relativeFilePath);
			return new SessionWrappedInputStream(in, session);
		} finally {
			if(in == null)
				session.close();
		}
	}
	
	public static DataSource getDataSourceVersioned(String repositoryName, Binder binder, 
			DefinableEntity entity, String relativeFilePath, String versionName,
			FileTypeMap fileTypeMap) throws RepositoryServiceException,
			UncheckedIOException {
		RepositorySessionFactory factory = RepositorySessionFactoryUtil.getRepositorySessionFactory(repositoryName);
		
		return factory.getDataSourceVersioned(binder, entity, relativeFilePath, versionName, fileTypeMap);
	}
	
	public static void checkout(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			session.checkout(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void checkin(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			session.checkin(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void uncheckout(String repositoryName, Binder binder, 
			DefinableEntity entry,String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

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
	 * Returns a logical binder path. The returned path does not contain root 
	 * path and it always ends with a separator character.  
	 * 
	 * @param binder
	 * @param separator
	 * @return
	 */
	public static String getBinderPath(Binder binder, String separator) {
		return new StringBuffer(Utils.getZoneKey()).
			append(separator).
			append(binder.getId()).
			append(separator).toString();
	}
	
	/**
	 * Returns a logical entity path. The returned path does not contain root 
	 * path and it always ends with a separator character.  
	 * 
	 * @param binder
	 * @param entry
	 * @param separator
	 * @return
	 */
	public static String getEntityPath(Binder binder, DefinableEntity entry, String separator) {
		return new StringBuffer(Utils.getZoneKey()).
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
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			session.move(binder, entry, relativeFilePath, destBinder, destEntry, destRelativeFilePath);
		} finally {
			session.close();
		}
	}

	public static String generateRandomVersionName() {
		// Generate a random version name using UUID generator. Since the
		// only requirement is that version names must be unique within a
		// particular file, this exceeds the requirement by being unique
		// VM wide.
		return UUID.randomUUID().toString();		
	}
	
	public static long getContentLengthUnversioned(String repositoryName, Binder binder, 
				DefinableEntity entity, String relativeFilePath) 
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

		try {
			return session.getContentLengthUnversioned(binder, entity, relativeFilePath);
		} finally {
			session.close();
		}
	}
}
