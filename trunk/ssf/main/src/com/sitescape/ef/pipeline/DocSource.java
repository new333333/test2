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
	 */
	public byte[] getByteArray();
	
	/**
	 * Returns input data as string if it was stored that way or 
	 * <code>null</code> otherwise. 
	 * 
	 * @return
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
	 * Returns input data as InputStream if the resource was specified
	 * via setOutputStream method on the corresponding DocSink object,
	 * or <code>null</code> otherwise,  
	 * 
	 * @return
	 */
	public InputStream getInputStream();
	
	/**
	 * Returns <code>InputStream</code> from which to read. This is meaningful
	 * only if corresponding <code>setBuiltinOutputStream</code> method was
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
	public InputStream getBuiltinInputStream() throws UncheckedIOException;
	
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
	 * Returns whether or not the data is text type. 
	 * 
	 * @return
	 */
	public boolean isTextData();
	
	/**
	 * Returns the name of the charset specified for the data.
	 * This method is meaningful only when the data is text type.
	 * If not, it returns <code>null</code>. 
	 * 
	 * @return charset name if text data or <code>null</code>
	 */
	public String getCharsetName();
	
	/**
	 * Returns input data as <code>InputStream</code> REGARDLESS OF
	 * what form it was originally stored in. In some cases character
	 * conversion may be necessary (eg. from String to byte stream). 
	 * This is a catch-all convenience routine that allows application to
	 * treat any data as InputStream uniformly. 
	 * Important: Not to be confused with <code>getBuiltinInputStream</code>.
	 *  
	 * @return
	 * @throws UncheckedIOException if I/O error occurs
	 */
	public InputStream getDataAsInputStream() throws UncheckedIOException;
	
	/**
	 * Returns input data as byte array REGARDLESS OF what form it was
	 * originally stored in. In some cases character decoding may be
	 * necessary (ie. from String to bytes). 
	 * Note: This method does not cache the result of conversion (when
	 * it takes place), and therefore calling this method multiple
	 * times incur unnecessary overhead.  
	 * 
	 * @return byte array or <code>null</code> if data doesn't exist.
	 * @throws UncheckedIOException
	 */
	public byte[] getDataAsByteArray() throws UncheckedIOException;
	
	/**
	 * Returns input data as String REGARDLESS OF what form it was
	 * originally stored in. In some cases character encoding may be
	 * necessary (eg. from bytes to string). 
	 * Note: This method does not cache the result of conversion (when
	 * it takes place), and therefore calling this method multiple
	 * times incur unnecessary overhead.  
	 * 
	 * @return string or <code>null</code> if data doesn't exist.
	 * @throws IllegalStateException the object is in a state where
	 * data conversion to string is not possible due to missing
	 * information, specifically, charset. 
	 * @throws UncheckedIOException
	 */
	public String getDataAsString() throws IllegalStateException, UncheckedIOException;
}
