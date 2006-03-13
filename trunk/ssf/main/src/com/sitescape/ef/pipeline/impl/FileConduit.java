package com.sitescape.ef.pipeline.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.util.TempFileUtil;
import com.sitescape.ef.util.FileHelper;

public class FileConduit extends AbstractConduit {

	private File fileDir;
	
	private File dataFile; // Don't call this 'file'. The name's already taken.
	private String fileNamePrefix;
	
	public FileConduit(File fileDir, String fileNamePrefix) {
		this.fileDir = fileDir;
		this.fileNamePrefix = fileNamePrefix;		
	}

	protected void reset() {
		super.reset();
		clearFile();
	}
	
	@Override
	protected DocSink sinkOnce() {
		return new FileDocSink();
	}

	@Override
	protected DocSource sourceOnce() {
		return new FileDocSource();
	}
	
	private void clearFile() {
		if(dataFile != null) {
			try {
				FileHelper.delete(dataFile);
			} catch (IOException e) {
				logger.warn(e.getMessage(), e);
			}
			dataFile = null;
		}
	}
	
	private String getPrefix() {
		long currTime = System.currentTimeMillis();
		
		if(fileNamePrefix != null) {
			return new StringBuffer(fileNamePrefix).append("_").append(currTime).append("_").toString();
		}
		else {
			return new StringBuffer().append(currTime).append("_").toString();
		}
	}
	
	protected class FileDocSink extends AbstractDocSink {

		public OutputStream getDefaultOutputStream() throws UncheckedIOException {
			reset(); // important
			dataFile = TempFileUtil.createTempFile(getPrefix(), fileDir);
			try {
				return new FileOutputStream(dataFile);
			} catch (FileNotFoundException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
	
	protected class FileDocSource extends AbstractDocSource {

		public InputStream getDefaultInputStream() throws UncheckedIOException {
			if(dataFile != null) {
				try {
					return new FileInputStream(dataFile);
				} catch (FileNotFoundException e) {
					throw new UncheckedIOException(e);
				}
			}
			else {
				return null;
			}
		}
		
	}
}
