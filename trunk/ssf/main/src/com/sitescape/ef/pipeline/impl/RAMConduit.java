package com.sitescape.ef.pipeline.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;

public class RAMConduit extends AbstractConduit {
	
	private ByteArrayOutputStream baos;

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
		
		public OutputStream getDefaultOutputStream() throws UncheckedIOException {
			reset(); // important
			baos = new ByteArrayOutputStream();
			return baos;
		}
	}
	
	protected class RAMDocSource extends AbstractDocSource {

		public InputStream getDefaultInputStream() throws UncheckedIOException {
			if(baos != null)
				return new ByteArrayInputStream(baos.toByteArray());
			else
				return null;
		}
	}
}
