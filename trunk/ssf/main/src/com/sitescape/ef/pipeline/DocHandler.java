package com.sitescape.ef.pipeline;

public interface DocHandler {

	public String getName();
	
	public void doHandle(DocSource source, DocSink sink,
		PipelineInvocation invocation) throws Throwable;
}
