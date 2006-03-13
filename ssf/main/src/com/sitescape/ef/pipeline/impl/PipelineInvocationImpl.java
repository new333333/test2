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
		curr++;
		if(curr == 0) {
			docHandlers[curr].doHandle(initialIn, conduits[curr].getSink(), this);
		}
		else if(curr == docHandlers.length - 1) {
			docHandlers[curr].doHandle(conduits[curr-1].getSource(), finalOut, this);
		}
		else {
			docHandlers[curr].doHandle(conduits[curr-1].getSource(), conduits[curr].getSink(), this);
		}
	}

	public void cleanup() {
		// initialIn and finalOut must not be closed/released here, since they 
		// belong to the caller of the pipeline rather than the pipeline itself.
		for(int i = 0; i < conduits.length; i++)
			conduits[i].close();
	}
}
