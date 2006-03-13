package com.sitescape.ef.pipeline;

import java.io.File;
import java.io.InputStream;

import com.sitescape.ef.UncheckedIOException;

public interface DocSource {

	/**
	 * Returns input data as byte array if it was stored that way or 
	 * <code>null</code> otherwise. 
	 * 
	 * @return
	 * @throws UncheckedIOException
	 */
	public byte[] getByteArray();
	
	/**
	 * Returns input data as string if it was stored that way or 
	 * <code>null</code> otherwise. 
	 * 
	 * @return
	 * @throws UncheckedIOException
	 */
	public String getString();
	
	/**
	 * Returns input data as file if it was stored that way or 
	 * <code>null</code> otherwise. 
	 * 
	 * @return
	 */
	public File getFile(); 
	
	/**
	 * Returns <code>InputStream</code> from which to read. This is meaningful
	 * only if corresponding <code>setDefaultOutputStream</code> method was
	 * used on the <code>DocSink</code> object associated with this source 
	 * object. Otherwise, it returns <code>null</code>.
	 * <p>
	 * The type of the backing storage (eg. RAM, file, socket, db, etc.) 
	 * associated with the returned input stream is implementation specific.
	 * Imporant: Not to be confused with <code>getDataAsInputStream</code>.
	 * 
	 * @return
	 * @throws UncheckedIOException
	 */
	public InputStream getDefaultInputStream() throws UncheckedIOException;
	
	/**
	 * Returns length of the input data (in byte) if the information is
	 * readily available.  If the information is unavailable (for example,
	 * the data has to be actually read or converted in order to compute
	 * the length), it returns -1. 
	 * 
	 * @return
	 * @throws UncheckedIOException if I/O error occurs
	 */
	public long getLength() throws UncheckedIOException;
	
	/**
	 * Returns input data as <code>InputStream</code> REGARDLESS OF
	 * what form it was originally stored in. In some cases character
	 * conversion may be necessary (eg. from String to byte stream). 
	 * This is a catch-all convenience routine that allows application to
	 * treat any data as InputStream uniformly. 
	 * Important: Not to be confused with <code>getDefaultInputStream</code>.
	 *  
	 * @return
	 * @throws UncheckedIOException if I/O error occurs
	 */
	public InputStream getDataAsInputStream() throws UncheckedIOException;
}
