package com.sitescape.ef.pipeline.impl;

import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.PipelineException;

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
