package com.sitescape.ef.pipeline.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.impl.AbstractConduit;
import com.sitescape.ef.pipeline.impl.AbstractConduit.AbstractDocSink;
import com.sitescape.ef.pipeline.impl.AbstractConduit.AbstractDocSource;

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
