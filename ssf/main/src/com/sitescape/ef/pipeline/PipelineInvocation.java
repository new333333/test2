package com.sitescape.ef.pipeline;

/**
 * This class represents an invocation of a pipeline. An invocation
 * provides context for sequential execution of handlers defined in the 
 * pipeline. Multiple invocations can be in progress simultaneously on a
 * pipeline. Invocation instance encapsulates and maintains necessary
 * state information needed for such execution and allows for concurrent
 * executions of the pipeline for multiple clients. 
 * 
 * @author jong
 *
 */
public interface PipelineInvocation {

	public void proceed() throws Throwable;
}
