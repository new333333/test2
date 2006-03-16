package com.sitescape.ef.pipeline.impl;

import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocHandler;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.PipelineInvocation;

public class PipelineInvocationImpl implements PipelineInvocation {

	private DocSource initialIn;
	private DocSink finalOut;
	private Conduit[] conduits;
	private DocHandler[] docHandlers;
	private int curr = -1;
	
	public PipelineInvocationImpl(DocSource initialIn, DocSink finalOut, 
			Conduit[] conduits, DocHandler[] docHandlers) {
		if(conduits.length + 1 != docHandlers.length)
			throw new IllegalArgumentException("Number of conduits and doc handlers passed in are wrong");
		
		this.initialIn = initialIn;
		this.finalOut = finalOut;
		this.conduits = conduits;
		this.docHandlers = docHandlers;
	}
	
	public void proceed() throws Throwable {
		if(++curr >= docHandlers.length)
			return; // No more handler to execute
		
		DocSource source = null;
		DocSink sink = null;
		
		if(curr == 0)
			source = initialIn;
		else
			source = conduits[curr-1].getSource();
		
		if(curr == docHandlers.length - 1)
			sink = finalOut;
		else
			conduits[curr].getSink();
		
		docHandlers[curr].doHandle(source, sink, this);
	}

	public void cleanup() {
		// initialIn and finalOut must not be closed/released here, since they 
		// belong to the caller of the pipeline rather than the pipeline itself.
		for(int i = 0; i < conduits.length; i++)
			conduits[i].close();
	}
}
