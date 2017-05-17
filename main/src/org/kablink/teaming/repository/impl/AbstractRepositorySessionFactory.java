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
package org.kablink.teaming.repository.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.fi.connection.ResourceDriverManager.FileOperation;
import org.kablink.teaming.module.file.impl.CryptoFileEncryption;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.repository.RepositorySession;
import org.kablink.teaming.repository.RepositorySessionFactory;
import org.kablink.teaming.repository.archive.ArchiveStore;
import org.kablink.teaming.security.function.WorkAreaOperation;


public abstract class AbstractRepositorySessionFactory implements RepositorySessionFactory {

	private ArchiveStore archiveStore;
	
	public void setArchiveStore(ArchiveStore archiveStore) {
		this.archiveStore = archiveStore;
	}
	public ArchiveStore getArchiveStore() {
		return archiveStore;
	}

	public void initialize() throws RepositoryServiceException, UncheckedIOException {
		if(archiveStore == null)
			throw new ConfigurationException("errorcode.no.archive.store", (Object[])null);
	}

	public abstract class AbstractRepositoryDataSource implements DataSource {
		protected Binder _binder;
		protected DefinableEntity _entity;
		protected String _relativeFilePath;
		protected String _versionName;
		protected FileTypeMap _fileMap;
		protected Boolean _isEncrypted;
		protected byte[] _encryptionKey;

		public AbstractRepositoryDataSource(Binder binder, DefinableEntity entity, 
				String relativeFilePath, String versionName, 
				Boolean isEncrypted, byte[] encryptionKey, FileTypeMap fileMap) {
			this._binder = binder;
			this._entity = entity;
			this._relativeFilePath = relativeFilePath;
			this._versionName = versionName;
			this._fileMap = fileMap;
			this._isEncrypted = isEncrypted;
			this._encryptionKey = encryptionKey;
		}
		
		public String getContentType() {
			return _fileMap.getContentType(_relativeFilePath);
		}

		public InputStream getInputStream() throws IOException {
			RepositorySession session = createReadSessionForDataSource();
			InputStream in = null;
			try {
				in = session.readVersioned(_binder, _entity, _relativeFilePath, _versionName, null);
				if (this._isEncrypted) {
					CryptoFileEncryption cfe = new CryptoFileEncryption(this._encryptionKey);
					in = cfe.getEncryptionInputDecryptedStream(in);
				}
				return new SessionWrappedInputStream(in, session);
			} finally {
				if(in == null)
					session.close();
			}		
		}

		public String getName() {
			return _relativeFilePath;
		}

		public OutputStream getOutputStream() throws IOException {
			return null;
		}
		
		protected abstract RepositorySession createReadSessionForDataSource();
	}
}
