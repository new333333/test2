package com.sitescape.ef.pipeline.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.ConduitFactory;

public class FileConduitFactory implements ConduitFactory, InitializingBean {

	//protected final Log logger = LogFactory.getLog(getClass());
	
	private File fileDir;
	private String fileNamePrefix;
	
	public void setFileDir(Resource fileDir) throws IOException {
		this.fileDir = fileDir.getFile();
	}
	
	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}
	
	public Conduit open() throws UncheckedIOException {
		return new FileConduit(fileDir, fileNamePrefix);
	}

	public void afterPropertiesSet() throws Exception {
		if(fileDir == null)
			throw new ConfigurationException("fileDir must be specified");
		
		if(fileNamePrefix == null)
			throw new ConfigurationException("fileNamePrefix must be specified");
	}

}
