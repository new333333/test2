/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.module.file.impl.CryptoFileEncryption;
import org.kablink.teaming.repository.impl.SessionWrappedInputStream;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.FilePathUtil;
import org.kablink.teaming.util.FileUploadItem;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;


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
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);
		try {
			return session.fileInfo(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}		
	}
	
	public static String createVersionedFile(Binder binder, DefinableEntity entry,
			FileUploadItem fui) throws RepositoryServiceException,
			UncheckedIOException {
		String repositoryName = fui.getRepositoryName();

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.CREATE_FILE, binder);

		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			InputStream is = fui.getInputStream();
			try {
				long size = fui.makeReentrant().getSize();
				return session.createVersioned(binder, entry, fui
					.getOriginalFilename(), is, size, fui.getModTime());
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

	public static void createUnversionedFile(String repositoryName,
			Binder binder, DefinableEntity entry, String relativeFilePath, InputStream in, long size, Long lastModTime) 
		throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.CREATE_FILE, binder);

		try {
			// TODO For now we ignore file path relative to the owning entry.
			// We simply treat that the file path is identical to the file name.
			session.createUnversioned(binder, entry, relativeFilePath, in, size, lastModTime);
		} finally {
			session.close();
		}
	}

	public static void updateFile(Binder binder, DefinableEntity entry,
			FileUploadItem fui) throws RepositoryServiceException,
			UncheckedIOException {
		String repositoryName = fui.getRepositoryName();

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.UPDATE, entry);

		try {
			InputStream is = fui.getInputStream();
			try {
				long size = fui.makeReentrant().getSize();
				
				session.update(binder, entry, fui.getOriginalFilename(), is, size, fui.getModTime());
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
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.DELETE, binder);

		try {
			session.delete(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}		
	}

	public static void deleteVersion(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, String versionName)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.DELETE, binder);

		try {
			session.deleteVersion(binder, entry, relativeFilePath, versionName);
		} finally {
			session.close();
		}
	}

	public static void readVersionedFile(FileAttachment fa, Binder binder, 
			DefinableEntity entry, String versionName, 
			String latestVersionName, OutputStream out)
			throws RepositoryServiceException, UncheckedIOException {
		String repositoryName = fa.getRepositoryName();
		String relativeFilePath = fa.getFileItem().getName();
		if (fa.isEncrypted()) {
			CryptoFileEncryption cfe = new CryptoFileEncryption(fa.getEncryptionKey());
			out = cfe.getEncryptionOutputDecryptedStream(out);
		}

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);

		try {
			session.readVersioned(binder, entry, relativeFilePath, versionName, latestVersionName, out);
		} finally {
			session.close();
		}
	}

    public static RepositorySession openSession(Binder binder, DefinableEntity entry, FileAttachment fa) {
        String repositoryName = fa.getRepositoryName();
        return RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);
    }

	public static InputStream readVersionedFile(FileAttachment fa, Binder binder, 
			DefinableEntity entry, String versionName, String latestVersionName)
			throws RepositoryServiceException, UncheckedIOException {
		return readVersionedFile(fa, binder, entry, versionName, latestVersionName, false);
	}
	
	public static InputStream readVersionedFile(FileAttachment fa, Binder binder, 
			DefinableEntity entry, String versionName, String latestVersionName, boolean readRawFile)
			throws RepositoryServiceException, UncheckedIOException {
		String repositoryName = fa.getRepositoryName();
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);

		InputStream in = null;
		try {
            in = getVersionedInputStream(session, binder, entry, fa, versionName, latestVersionName, readRawFile);
		} finally {
			if(in == null)
				session.close();
		}
        return in;
	}

    public static InputStream getVersionedInputStream(RepositorySession session, Binder binder, DefinableEntity entry, FileAttachment fa, String versionName, String latestVersionName, boolean readRawFile) {
        String relativeFilePath = fa.getFileItem().getName();
        InputStream in;
        in = session.readVersioned(binder, entry, relativeFilePath, versionName, latestVersionName);
        if (!readRawFile && fa.isEncrypted()) {
            CryptoFileEncryption cfe = new CryptoFileEncryption(fa.getEncryptionKey());
            in = cfe.getEncryptionInputDecryptedStream(in);
        }
        return new SessionWrappedInputStream(in, session);
    }

    public static InputStream getUnversionedInputStream(RepositorySession session, Binder binder, DefinableEntity entry, FileAttachment fa) {
        String relativeFilePath = fa.getFileItem().getName();
        InputStream in;
        in = session.readUnversioned(binder, entry, relativeFilePath);
        return new SessionWrappedInputStream(in, session);
    }

    public static void readUnversionedFile(String repositoryName, Binder binder,
			DefinableEntity entry, String relativeFilePath, OutputStream out)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);

		try {
			session.readUnversioned(binder, entry, relativeFilePath, out);
		} finally {
			session.close();
		}
	}

	public static InputStream readUnversionedFile(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);

		InputStream in = null;
		try {
			in = session.readUnversioned(binder, entry, relativeFilePath);
			return new SessionWrappedInputStream(in, session);
		} finally {
			if(in == null)
				session.close();
		}
	}
	
	public static DataSource getDataSourceVersioned(FileAttachment fa, Binder binder, 
			DefinableEntity entity, FileTypeMap fileTypeMap) throws RepositoryServiceException,
			UncheckedIOException {
		String repositoryName = fa.getRepositoryName();
		String relativeFilePath = fa.getFileItem().getName();
		String versionName = fa.getHighestVersion().getVersionName();
		Boolean isEncrypted = fa.isEncrypted();
		byte[] encryptionKey = fa.getEncryptionKey();
		RepositorySessionFactory factory = RepositorySessionFactoryUtil.getRepositorySessionFactory(repositoryName);
		
		return factory.getDataSourceVersioned(binder, entity, relativeFilePath, versionName, 
				isEncrypted, encryptionKey, fileTypeMap);
	}
	
	public static void checkout(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);

		try {
			session.checkout(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void checkin(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);

		try {
			session.checkin(binder, entry, relativeFilePath);
		} finally {
			session.close();
		}
	}

	public static void uncheckout(String repositoryName, Binder binder, 
			DefinableEntity entry,String relativeFilePath)
			throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entry);

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
			append(entry.getEntityTypedId()).
			append(separator).toString();
	}
	
	public static void moveFile(String repositoryName, Binder binder, 
			DefinableEntity entry, String relativeFilePath, Binder destBinder, 
			DefinableEntity destEntry, String destRelativeFilePath)
	throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.MOVE_FILE, binder, destBinder);

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
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName, binder.getResourceDriverName(), ResourceDriverManager.FileOperation.READ, entity);

		try {
			return session.getContentLengthUnversioned(binder, entity, relativeFilePath);
		} finally {
			session.close();
		}
	}
}
