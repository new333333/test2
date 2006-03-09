package com.sitescape.ef.pipeline.impl;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.ef.pipeline.DocHandler;

public abstract class AbstractDocHandler implements DocHandler, 
	InitializingBean, DisposableBean {

	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void afterPropertiesSet() throws Exception {}

	public void destroy() throws Exception {}
}
