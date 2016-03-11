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

import org.kablink.teaming.repository.RepositorySession;


public class SessionWrappedInputStream extends InputStream {

	private InputStream target;
	private RepositorySession session;

	public SessionWrappedInputStream(InputStream in, RepositorySession session) {
		if(in == null)
			throw new IllegalArgumentException("Input stream must be specified");

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
