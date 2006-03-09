package com.sitescape.ef.pipeline.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.NoFileException;
import com.sitescape.ef.util.FileHelper;

/**
 * <code>BasicConduit</code> supports simple RAM and File.
 * 
 * @author jong
 *
 */
public class BasicConduit extends AbstractConduit {
	
	private String producerName;
	private File fileDir; // If non-null, supports backing files

	private BasicDocSink sink;
	private BasicDocSource source;
	
	private OutputStream out;
	private File file;
	
	private byte[] data;
	private InputStream in;
	
	public BasicConduit(String producerName, File fileDir) {
		this.producerName = producerName;
		this.fileDir = fileDir;
	}

	@Override
	protected DocSink sinkInternal() {
		sink = new BasicDocSink();
		return sink;
	}

	@Override
	protected DocSource sourceInternal() {
		source = new BasicDocSource();
		return source;
	}
	
	protected class BasicDocSink implements DocSink {
				
		public void useFile() throws ConfigurationException, IllegalStateException, UncheckedIOException {
			if(fileDir == null)
				throw new ConfigurationException("This conduit does not support backing files");
			
			if(out != null || file != null)
				throw new IllegalStateException();
			
			try {
				if(!fileDir.exists())
					FileHelper.mkdirs(fileDir);
				file = File.createTempFile(producerName + "_" + System.currentTimeMillis() + "_", null, fileDir);
			}
			catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		public OutputStream getOutputStream() throws UncheckedIOException {
			if(out == null) {
				if(file == null) { // Use RAM
					out = new ByteArrayOutputStream();
				}
				else { // Use File
					try {
						out = new FileOutputStream(file);
					} catch (FileNotFoundException e) {
						throw new UncheckedIOException(e);
					}
				}
			}
			return out;
		}

		public File getFile() throws ConfigurationException, IllegalStateException, UncheckedIOException {
			if(fileDir == null)
				throw new ConfigurationException("This conduit does not support backing files");
			
			if(file == null)
				throw new IllegalStateException("Must call useFile method first");
			
			return file;
		}
		
	}
	
	protected class BasicDocSource implements DocSource {
		
		public InputStream getInputStream() throws UncheckedIOException {
			if(in == null) {
				if(file == null) { // RAM
					if(data == null)
						data = ((ByteArrayOutputStream) out).toByteArray();
					in = new ByteArrayInputStream(data);
				}
				else {	// File
					try {
						in = new FileInputStream(file);
					} catch (FileNotFoundException e) {
						throw new UncheckedIOException(e);
					}
				}
			}
			return in;
		}

		public long getLength() throws UncheckedIOException {
			if(file != null)
				return file.length();
			else if(data != null)
				return data.length;
			else
				return -1;
		}

		public File getFileIfExists() throws UncheckedIOException {
			return file;
		}

		public File getFileRequired() throws ConfigurationException, NoFileException, UncheckedIOException {
			if(fileDir == null)
				throw new ConfigurationException("This conduit does not support backing files");

			if(file == null)
				throw new NoFileException("Backing file does not exist");
			
			return file;
		}
		
	}

	public void close() {
		if(file != null) {
			// The file was created internally by this conduit, so it's this 
			// conduit's responsibility to delete it after use.  
			try {
				FileHelper.delete(file);
			} catch (IOException e) {
				logger.error("Error closing conduit", e);
			}
			finally {
				file = null;
			}
		}
	}
}
