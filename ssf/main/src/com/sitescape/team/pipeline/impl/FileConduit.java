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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.pipeline.DocSink;
import com.sitescape.team.pipeline.DocSource;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.TempFileUtil;

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
		
	protected class FileDocSink extends AbstractDocSink {

		public OutputStream getBuiltinOutputStream(boolean isTextData, String charsetName) throws UncheckedIOException {
			reset(); // important
			isText = isTextData;
			if(isText)
				charset = charsetName;
			dataFile = TempFileUtil.createTempFile(fileNamePrefix, fileDir);
			try {
				return new FileOutputStream(dataFile);
			} catch (FileNotFoundException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
	
	protected class FileDocSource extends AbstractDocSource {

		public InputStream getBuiltinInputStream() throws UncheckedIOException {
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
