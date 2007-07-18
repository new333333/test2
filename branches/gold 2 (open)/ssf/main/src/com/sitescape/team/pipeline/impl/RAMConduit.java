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
package com.sitescape.team.pipeline.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.pipeline.DocSink;
import com.sitescape.team.pipeline.DocSource;

public class RAMConduit extends AbstractConduit {
	
	private ByteArrayOutputStream baos;

	public RAMConduit() {
		super();
	}
	
	protected void reset() {
		super.reset();
		baos = null;
	}

	@Override
	protected DocSink sinkOnce() {
		return new RAMDocSink();
	}

	@Override
	protected DocSource sourceOnce() {
		return new RAMDocSource();
	}

	protected class RAMDocSink extends AbstractDocSink {
		
		public OutputStream getBuiltinOutputStream(boolean isTextData, String charsetName) throws UncheckedIOException {
			reset(); // important
			isText = isTextData;
			if(isText)
				charset = charsetName;
			baos = new ByteArrayOutputStream();
			return baos;
		}
	}
	
	protected class RAMDocSource extends AbstractDocSource {

		public InputStream getBuiltinInputStream() throws UncheckedIOException {
			if(baos != null)
				return new ByteArrayInputStream(baos.toByteArray());
			else
				return null;
		}
	}
}
