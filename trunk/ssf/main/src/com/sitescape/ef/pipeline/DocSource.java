package com.sitescape.ef.pipeline;

import java.io.File;
import java.io.InputStream;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.UncheckedIOException;

public interface DocSource {

	/**
	 * Returns <code>InputStream</code> from which to read.
	 * Multple invocations of this method return the same 
	 * <code>InputStream</code> instance.
	 *  
	 * @return
	 * @throws UncheckedIOException if I/O error occurs
	 */
	public InputStream getInputStream() throws UncheckedIOException;
	
	/**
	 * Returns length of the input data (in byte) if the information is
	 * available. If the information is unavailable (that is, it can not
	 * be computed without actually reading the data from the stream),
	 * it returns -1. 
	 * 
	 * @return
	 * @throws UncheckedIOException if I/O error occurs
	 */
	public long getLength() throws UncheckedIOException;
	
	
	/**
	 * Returns the file that backs this source if it exists. The returned file
	 * must not be modified by the caller. If backing file doesn't exist, it
	 * returns <code>null</code>.
	 * @return
	 */
	//public File getFile();
	
	/**
	 * Returns the file that backs this source if it exists. The returned file 
	 * must not be modified. If backing file doesn't exist, it returns 
	 * <code>null</code>.
	 * 
	 * @return
	 * @throws UncheckedIOException if I/O error occurs
	 */
	public File getFileIfExists() throws UncheckedIOException;
	
	/**
	 * Returns the file that backs this source. The returned file must not be 
	 * modified.
	 * <p>
	 * If the source is configured to allow for access through backing file
	 * AND the file already exists, the file is returned. Otherwise, it
	 * throws an exception. 
	 * 
	 * @return
	 * @throws ConfigurationException This source does not support backing files.
	 * This is caused by misconfiguration of the pipeline 
	 * @throws NoFileException	No backing file is present for the source.
	 * @throws UncheckedIOException if I/O error occurs
	 */
	public File getFileRequired() throws ConfigurationException, NoFileException, UncheckedIOException;

}
