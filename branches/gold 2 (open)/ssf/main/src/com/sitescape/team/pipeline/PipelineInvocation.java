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
package com.sitescape.team.pipeline;

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
