package com.sitescape.ef.pipeline.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.ConduitFactory;

public class BasicConduitFactory implements ConduitFactory {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private File fileDir;
	
	public void setFileDir(Resource fileDir) throws IOException {
		this.fileDir = fileDir.getFile();
	}
	
	public Conduit open(String producerName) throws UncheckedIOException {
		return new BasicConduit(producerName, fileDir);
	}

}
