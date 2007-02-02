package com.sitescape.team.pipeline;

public interface Pipeline {
	
	public void invoke(DocSource initialIn, DocSink finalOut) throws PipelineException;
}
