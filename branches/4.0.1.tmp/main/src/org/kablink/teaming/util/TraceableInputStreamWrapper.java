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
package org.kablink.teaming.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TraceableInputStreamWrapper extends InputStream implements Comparable<TraceableInputStreamWrapper> {

	private static final Log logger = LogFactory.getLog(TraceableInputStreamWrapper.class);
	
	private static final TreeSet<TraceableInputStreamWrapper> openStreamHandles = new TreeSet<TraceableInputStreamWrapper>();
	private static long createdStreamHandlesCount = 0;
	private static long openStreamHandlesPeak = 0;
	
	// Original input stream that this object wraps and delegates to.
	private InputStream original;
	private String path; // Some sort of path representing the original resource.
	
	public TraceableInputStreamWrapper(InputStream original, String path) {
		if(original == null)
			throw new IllegalArgumentException("Input stream must be specified");
		
		logger.debug("calling constructor() on [" + path + "]");
		
		this.original = original;
		this.path = path;
		
		synchronized(openStreamHandles) {
			openStreamHandles.add(this);
			createdStreamHandlesCount++;
			if(openStreamHandles.size() > openStreamHandlesPeak)
				openStreamHandlesPeak = openStreamHandles.size();
		}
	}
	
	@Override
	public int read() throws IOException {	
		logger.debug("read() on [" + path + "]");	
		int result = original.read();
		logger.debug("Returning [" + result + "]");
		return result;
	}
	
	@Override
    public int read(byte b[]) throws IOException {
		logger.debug("calling read(byte[" + b.length + "]) on [" + path + "]");
		int result = original.read(b);
		logger.debug("Returning [" + result + "]");
		return result;
    }

	@Override
    public int read(byte b[], int off, int len) throws IOException {
		logger.debug("calling read(byte[" + b.length + "]," + off + "," + len + ") on [" + path + "]");
		int result = original.read(b, off, len);
		logger.debug("Returning [" + result + "]");
		return result;
    }

	@Override
    public long skip(long n) throws IOException {
		logger.debug("calling skip(" + n + ") on [" + path + "]");
		long result = original.skip(n);
		logger.debug("Returning [" + result + "]");
		return result;
    }

	@Override
    public int available() throws IOException {
		logger.debug("calling available() on [" + path + "]");	
		int result = original.available();
		logger.debug("Returning [" + result + "]");
		return result;
    }

	@Override
    public void close() throws IOException {
		logger.debug("calling close() on [" + path + "]");
		
		try {
			original.close();
		}
		finally {
			synchronized(openStreamHandles) {
				openStreamHandles.remove(this);
			}
		}
    }

	@Override
    public synchronized void mark(int readlimit) {
		logger.debug("calling mark(" + readlimit + ") on [" + path + "]");
		
		original.mark(readlimit);
	}

	@Override
    public synchronized void reset() throws IOException {
		logger.debug("calling reset() on [" + path + "]");
		
		original.reset();
    }

	@Override
    public boolean markSupported() {
		logger.debug("calling markSupported() on [" + path + "]");
        boolean result = original.markSupported();
        logger.debug("Returning [" + result + "]");
        return result;
    }

	public static String getOpenStreamHandlesAsString() {
		StringBuilder sb = new StringBuilder();
		synchronized(openStreamHandles) {
			sb.append("Input stream: created=")
			.append(createdStreamHandlesCount)
			.append(" still open=")
			.append(openStreamHandles.size())
			.append(" peak open=")
			.append(openStreamHandlesPeak);
			for(TraceableInputStreamWrapper handle : openStreamHandles) {
				sb.append(Constants.NEWLINE)
				.append(handle.path);
			}
		}
		return sb.toString();
	}

	@Override
	public int compareTo(TraceableInputStreamWrapper o) {
		return this.path.compareTo(o.path);
	}
}
