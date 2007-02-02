package com.sitescape.team.pipeline.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.pipeline.DocHandler;

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
