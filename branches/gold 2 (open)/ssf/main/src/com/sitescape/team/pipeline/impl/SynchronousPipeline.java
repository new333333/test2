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
import com.sitescape.team.pipeline.DocSink;
import com.sitescape.team.pipeline.DocSource;
import com.sitescape.team.pipeline.PipelineException;

/**
 * This class implements a most basic pipeline that executes each pipeline
 * invocation in a single thread in synchronous manner. 
 * 
 * @author jong
 *
 */
public class SynchronousPipeline extends AbstractPipeline {

	public void invoke(DocSource initialIn, DocSink finalOut) throws PipelineException {
		// Setup invocation instance. 
		PipelineInvocationImpl invocation = setupPipelineInvocation(initialIn, finalOut);
		
		// Invoke the handler chain. 
		try {
			invocation.proceed();
		}
		catch(Throwable t) {
			throw new PipelineException(t);
		}
		finally {
			// Cleanup
			invocation.cleanup();
		}
	}

	private PipelineInvocationImpl setupPipelineInvocation(DocSource initialIn, DocSink finalOut) {
		Conduit[] conduits = new Conduit[conduitFactories.length];
		for(int i = 0; i < conduitFactories.length; i++) {
			conduits[i] = conduitFactories[i].open();
		}
		
		return new PipelineInvocationImpl(initialIn, finalOut, conduits, docHandlers);
	}

}
