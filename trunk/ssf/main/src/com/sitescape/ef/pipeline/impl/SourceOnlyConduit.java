package com.sitescape.ef.pipeline.impl;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;

public class SourceOnlyConduit implements Conduit {

	private DocSource source;
	
	public SourceOnlyConduit(DocSource source) {
		this.source = source;
	}
	
	public DocSink getSink() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	public DocSource getSource() throws IllegalStateException {
		return source;
	}

	public void close() throws UncheckedIOException {
		// This "fake" conduit does not own the DocSource passed in.
		// So it's not this conduit's responsibility to do anything about it.
	}

}
