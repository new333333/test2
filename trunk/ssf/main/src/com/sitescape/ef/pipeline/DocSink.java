package com.sitescape.ef.pipeline;

import java.io.File;
import java.io.OutputStream;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.UncheckedIOException;

public interface DocSink {

	/**
	 * Indicates to the framework that a backing file should be used.
	 * In order to use a backing file, this method must be called BEFORE
	 * <code>getOutputStream</code> or <code>getFile</code> is called.  
	 * If the method is called afterward, it throws IllegalStateException. 
	 * It is also illegal to call this method more than once. 
	 *
	 * @throws ConfigurationException This sink does not support backing files.
	 * This is caused by misconfiguration of the pipeline 
	 * @throws IllegalStateException thrown if called after getOutputStream or
	 * getFile is called or called more than once
	 * @throws UncheckedIOException if I/O error occurs.
	 */
	public void useFile() throws ConfigurationException, IllegalStateException, UncheckedIOException;
	
	/**
	 * Returns <code>OutputStream</code> into which to write.
	 * Multple invocations of this method return the same 
	 * <code>OutputStream</code> instance.
	 *  
	 * @return
	 * @throws UncheckedIOException
	 */
	public OutputStream getOutputStream() throws UncheckedIOException;
	
	/**
	 * Returns the file that backs this sink. 
	 * 
	 * @return
	 * @throws ConfigurationException This sink does not support backing files.
	 * This is caused by misconfiguration of the pipeline 
	 * @throws IllegalStateException thrown if this method is called before
	 * <code>useFile</code> is called. 
	 * @throws UncheckedIOException
	 */
	public File getFile() throws ConfigurationException, IllegalStateException, UncheckedIOException;

}
