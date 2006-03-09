package com.sitescape.ef.pipeline.impl;

import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.PipelineInvocation;

/**
 * This class implements a most basic pipeline that executes each pipeline
 * invocation in a single thread in synchronous manner. 
 * 
 * @author jong
 *
 */
public class SynchronousPipeline extends AbstractPipeline {

	public void invoke(DocSource initialIn, DocSink finalOut) throws Throwable {
		PipelineInvocation invocation = setupPipelineInvocation(initialIn, finalOut);
		
		invocation.proceed();
	}

	private PipelineInvocation setupPipelineInvocation(DocSource initialIn, DocSink finalOut) {
		Conduit[] conduits = new Conduit[conduitFactories.length+2];
		conduits[0] = new SourceOnlyConduit(initialIn);
		for(int i = 0; i < conduitFactories.length; i++) {
			conduits[i+1] = conduitFactories[i].open(docHandlers[i].getName());
		}
		conduits[conduits.length-1] = new SinkOnlyConduit(finalOut);
		
		return new PipelineInvocationImpl(conduits, docHandlers);
	}

}
