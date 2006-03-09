package com.sitescape.ef.pipeline.impl;

import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocHandler;
import com.sitescape.ef.pipeline.PipelineInvocation;

public class PipelineInvocationImpl implements PipelineInvocation {

	private Conduit[] conduits;
	private DocHandler[] docHandlers;
	private int curr = -1;
	
	public PipelineInvocationImpl(Conduit[] conduits, DocHandler[] docHandlers) {
		if(conduits.length != docHandlers.length + 1)
			throw new IllegalArgumentException("Number of conduits and doc handlers passed in are wrong");
		
		this.conduits = conduits;
		this.docHandlers = docHandlers;
	}
	
	public void proceed() throws Throwable {
		curr++;
		docHandlers[curr].doHandle(conduits[curr].getSource(), conduits[curr+1].getSink(), this);
	}

}
