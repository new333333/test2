package com.sitescape.ef.pipeline.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.DocSink;

public abstract class AbstractConduit implements Conduit {

	protected static final Log logger = LogFactory.getLog(AbstractConduit.class);
	
	private boolean sinkCalled = false;
	private boolean sourceCalled = false;
	
	protected DocSink sink;
	protected DocSource source;
	
	public DocSink getSink() throws IllegalStateException {
		sinkCalled = true;
		if(!sourceCalled) {
			if(sink == null)
				sink = sinkInternal();
			return sink;
		}
		else {
			throw new IllegalStateException("sink cannot be called once source is called");
		}
	}

	public DocSource getSource() throws IllegalStateException {
		sourceCalled = true;
		if(sinkCalled) {
			if(source == null)
				source = sourceInternal();
			return source;
		}
		else {
			throw new IllegalStateException("source cannot be called until sink is called");
		}
	}
	
	protected abstract DocSink sinkInternal();
	
	protected abstract DocSource sourceInternal();

}
