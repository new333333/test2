package com.sitescape.ef.pipeline.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.util.FileHelper;

public abstract class AbstractConduit implements Conduit {
	
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
	private boolean fileTransferOwnership;
	private InputStream inputstream;
	
	protected boolean isText;
	protected String charset;
	
	protected AbstractConduit() {
		reset();
	}
	
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
			if(fileTransferOwnership) {
				try {
					FileHelper.delete(file);
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
			file = null;
		}
		fileTransferOwnership = false;
		if(inputstream != null) {
			try {
				inputstream.close();
			}
			catch(IOException e) {
				logger.warn(e.getMessage(), e);
			}
			inputstream = null;
		}
		isText = false;
		charset = null;
	}
	
	public abstract class AbstractDocSink implements DocSink {
		
		public void setByteArray(byte[] data, boolean isTextData, String charsetName) {
			reset();
			bytes = data;
			isText = isTextData;
			if(isText)
				charset = charsetName;
		}

		/*
		public void setCharsetName(String charsetName) throws IllegalStateException {
			reset();
			charset = charsetName;
		}*/

		public void setString(String data, String charsetName) {
			reset();
			string = data;
			isText = true;
			charset = charsetName;
		}

		public void setFile(File data, boolean transferOwnership, boolean isTextData, String charsetName) {
			reset();
			file = data;
			fileTransferOwnership = transferOwnership;
			isText = isTextData;
			if(isText)
				charset = charsetName;
		}

		public void setInputStream(InputStream is, boolean isTextData, String charsetName) {
			reset();
			inputstream = is;
			isText = isTextData;
			if(isText)
				charset = charsetName;
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

		public InputStream getInputStream() {
			return inputstream;
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
					return new BufferedInputStream(new FileInputStream(file));
				}
				else if(inputstream != null) {
					return inputstream;
				}
				else {
					return getBuiltinInputStream();
				}
			}
			catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		public boolean isTextData() {
			return isText;
		}

		public String getCharsetName() {
			return charset;
		}

		public byte[] getDataAsByteArray() throws UncheckedIOException {
			try {
				if(bytes != null) {
					return bytes;
				}
				else if(string != null) {
					return string.getBytes(charset);
				}
				else if(file != null) {
					return FileCopyUtils.copyToByteArray(file);
				}
				else if(inputstream != null) {
					return FileCopyUtils.copyToByteArray(inputstream);
				}
				else {
					InputStream is = getBuiltinInputStream();
					if(is != null) {
						try {
							return FileCopyUtils.copyToByteArray(is);
						}
						finally {
							try {
								is.close();
							}
							catch(IOException e1) {
								logger.warn(e1.getMessage(), e1);
							}
						}
					}
					else {
						return null;
					}
				}
			}
			catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		public String getDataAsString() throws IllegalStateException, UncheckedIOException {
			if(!isText)
				throw new IllegalStateException("The data is not known to be text type");
			
			try {
				if(bytes != null) {				
					return new String(bytes, charset);
				}
				else if(string != null) {
					return string;
				}
				else if(file != null) {
					return FileCopyUtils.copyToString(new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), charset));
				}
				else if(inputstream != null) {
					return FileCopyUtils.copyToString(new InputStreamReader(new BufferedInputStream(inputstream), charset));
				}
				else {
					InputStream is = getBuiltinInputStream();
					if(is != null) {
						try {
							return FileCopyUtils.copyToString(new InputStreamReader(new BufferedInputStream(is), charset));
						}
						finally {
							try {
								is.close();
							}
							catch(IOException e1) {
								logger.warn(e1.getMessage(), e1);
							}
						}
					}
					else {
						return null;
					}
				}
			}
			catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		}

	}
}
