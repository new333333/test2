package com.sitescape.ef.pipeline.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.util.FileHelper;

public abstract class AbstractConduit implements Conduit {

	private static final String DEFAULT_CHARSET = "UTF-8";
	
	protected static final Log logger = LogFactory.getLog(AbstractConduit.class);
	
	private boolean sinkCalled = false;
	private boolean sourceCalled = false;
	
	protected DocSink sink;
	protected DocSource source;
	
	/*
	 * Only at most of the following three can be non-null at a time.
	 */
	private byte[] bytes;
	private String string;
	private File file;
	
	private String charset;
	
	protected AbstractConduit() {}
	
	public DocSink getSink() throws IllegalStateException {
		sinkCalled = true;
		if(!sourceCalled) {
			if(sink == null)
				sink = sinkOnce();
			return sink;
		}
		else {
			throw new IllegalStateException("sink cannot be called once source is called");
		}
	}

	public DocSource getSource() throws IllegalStateException {
		sourceCalled = true;
		if(sinkCalled) {
			if(source == null)
				source = sourceOnce();
			return source;
		}
		else {
			throw new IllegalStateException("source cannot be called until sink is called");
		}
	}
	
	public void close() {
		reset();
	}
	
	/**
	 * This is guaranteed to be called only once on the same conduit. 
	 * 
	 * @return
	 */
	protected abstract DocSink sinkOnce();
	
	/**
	 * This is guaranteed to be called only once on the same conduit. 
	 * 
	 * @return
	 */
	protected abstract DocSource sourceOnce();

	protected void reset() {
		bytes = null;
		string = null;
		if(file != null) {
			try {
				FileHelper.delete(file);
			} catch (IOException e) {
				logger.warn(e.getMessage(), e);
			}
			file = null;
		}
		charset = DEFAULT_CHARSET;
	}
	
	public abstract class AbstractDocSink implements DocSink {
		
		public void setByteArray(byte[] data) throws IllegalStateException {
			reset();
			bytes = data;
		}

		public void setCharsetName(String charsetName) throws IllegalStateException {
			reset();
			charset = charsetName;
		}

		public void setString(String data) throws IllegalStateException {
			reset();
			string = data;
		}

		public void setFile(File data) throws IllegalStateException {
			reset();
			file = data;
		}
	}
	
	public abstract class AbstractDocSource implements DocSource {

		public byte[] getByteArray() {
			return bytes;
		}

		public String getString() {
			return string;
		}

		public File getFile() {
			return file;
		}

		public long getLength() throws UncheckedIOException {
			if(bytes != null)
				return bytes.length;
			else if(file != null)
				return file.length();
			else
				return -1;
		}

		public InputStream getDataAsInputStream() throws UncheckedIOException {
			try {
				if(bytes != null) {
					return new ByteArrayInputStream(bytes);
				}
				else if(string != null) {
					return new ByteArrayInputStream(string.getBytes(charset));
				}
				else if(file != null) {
					return new FileInputStream(file);
				}
				else {
					return getDefaultInputStream();
				}
			}
			catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
