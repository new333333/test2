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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.pipeline.ConduitFactory;
import com.sitescape.team.pipeline.DocHandler;
import com.sitescape.team.pipeline.Pipeline;

public abstract class AbstractPipeline implements Pipeline, InitializingBean, 
	DisposableBean {

	// Conduit factories and doc handlers are created and initialized only
	// once while each invocation on the pipeline creates new conduit 
	// instances and a pipeline invocation instance. 
	
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
