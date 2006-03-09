package com.sitescape.ef.pipeline.impl;

import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;

/**
 * <code>Conduit</code> wrapper around <code>DocSink</code>.
 * This is a adapter class.
 * 
 * @author jong
 *
 */
public class SinkOnlyConduit implements Conduit {

	private DocSink sink;
	
	public SinkOnlyConduit(DocSink sink) {
		this.sink = sink;
	}
	
	public DocSink getSink() throws IllegalStateException {
		return sink;
	}

	public DocSource getSource() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	public void close() {
		// This "fake" conduit does not own the DocSink passed in.
		// So it's not this conduit's responsibility to do anything about it.
	}

}
