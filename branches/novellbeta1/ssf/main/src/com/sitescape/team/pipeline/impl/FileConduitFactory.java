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

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.pipeline.Conduit;
import com.sitescape.team.pipeline.ConduitFactory;

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
