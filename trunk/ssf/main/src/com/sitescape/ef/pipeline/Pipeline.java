package com.sitescape.ef.pipeline;

public interface Pipeline {
	
	public void invoke(DocSource initialIn, DocSink finalOut) throws Throwable;
}
