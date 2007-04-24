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

import com.sitescape.team.pipeline.Conduit;
import com.sitescape.team.pipeline.DocHandler;
import com.sitescape.team.pipeline.DocSink;
import com.sitescape.team.pipeline.DocSource;
import com.sitescape.team.pipeline.PipelineInvocation;

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
			sink = conduits[curr].getSink();
		
		docHandlers[curr].doHandle(source, sink, this);
	}

	public void cleanup() {
		// initialIn and finalOut must not be closed/released here, since they 
		// belong to the caller of the pipeline rather than the pipeline itself.
		for(int i = 0; i < conduits.length; i++)
			conduits[i].close();
	}
}
