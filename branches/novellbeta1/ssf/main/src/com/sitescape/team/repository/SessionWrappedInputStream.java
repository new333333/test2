/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.repository;

import java.io.IOException;
import java.io.InputStream;

public class SessionWrappedInputStream extends InputStream {

	private InputStream target;
	private RepositorySession session;

	public SessionWrappedInputStream(InputStream in, RepositorySession session) {
		this.target = in;
		this.session = session;
	}

	@Override
	public int read() throws IOException {
		return target.read();
	}

	public int read(byte b[]) throws IOException {
		return target.read(b);
	}

	public int read(byte b[], int off, int len) throws IOException {
		return target.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return target.skip(n);
	}

	public int available() throws IOException {
		return target.available();
	}

	public void close() throws IOException {
		try {
			target.close();
		}
		finally {
			closeSession();
		}
	}

	public synchronized void mark(int readlimit) {
		target.mark(readlimit);
	}

	public synchronized void reset() throws IOException {
		target.reset();
	}

	public boolean markSupported() {
		return target.markSupported();
	}

	protected void finalize() throws Throwable {
		try {
			super.finalize();
		}
		finally {
			closeSession();
		}
	}

	private void closeSession() {
		if(session != null) {
			try {
				session.close();
			}
			finally {
				session = null;
			}
		}
	}
}
