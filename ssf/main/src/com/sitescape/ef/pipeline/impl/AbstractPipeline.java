package com.sitescape.ef.pipeline.impl;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.pipeline.ConduitFactory;
import com.sitescape.ef.pipeline.DocHandler;
import com.sitescape.ef.pipeline.Pipeline;

public abstract class AbstractPipeline implements Pipeline, InitializingBean, 
	DisposableBean {

	protected ConduitFactory[] conduitFactories;	// N
	protected DocHandler[] docHandlers;			// N+1
	
	public void setConduitFactories(ConduitFactory[] conduitFactories) {
		this.conduitFactories = conduitFactories;
	}
	/*
	public Object[] getConduitFactories() {
		return conduitFactories;
	}*/

	public void setDocHandlers(DocHandler[] docHandlers) {
		this.docHandlers = docHandlers;
	}
	/*
	public Object[] getDocHandlers() {
		return docHandlers;
	}*/

	public void afterPropertiesSet() throws Exception {
		if(conduitFactories.length + 1 != docHandlers.length)
			throw new ConfigurationException("Number of doc handlers must be equal to number of conduit factories plus 1");
	}

	public void destroy() throws Exception {
	}

}
