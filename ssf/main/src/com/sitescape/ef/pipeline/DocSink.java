package com.sitescape.ef.pipeline;

import java.io.File;
import java.io.OutputStream;

import com.sitescape.ef.UncheckedIOException;

/**
 * Interface for specifying or transfering output data.
 * Only one mechanism (last specified one) can be be in effect.
 * Calling one mechanism effectively throws away the state caused by
 * a previous mechanism used. So, it is highly recommended that the
 * application utilizes only one mechanism and calls it only once.  
 * 
 * @author jong
 *
 */
public interface DocSink {

	/**
	 * Use the byte array as output data.
	 * 
	 * @param data
	 * @throws IllegalStateException
	 */
	public void setByteArray(byte[] data) throws IllegalStateException;
	
	/**
	 * Provides the name of a supported charset. This also indicates to the
	 * framework that the output data associated with this sink is character
	 * based data. The supplied information is used for conversion between
	 * characters and bytes if necessary.
	 * <p>
	 * If charset is not specified, UTF-8 is assumed by default.
	 * 
	 * @param charsetName
	 * @throws IllegalStateException
	 */
	public void setCharsetName(String charsetName) throws IllegalStateException;
	
	/**
	 * Use the string as output data.
	 * 
	 * @param data
	 * @throws IllegalStateException
	 */
	public void setString(String data) throws IllegalStateException;
	
	/**
	 * Use the file as output data.
	 * 
	 * @param data
	 * @throws IllegalStateException
	 */
	public void setFile(File data) throws IllegalStateException;
	
	/**
	 * Returns <code>OutputStream</code> into which to write.
	 * It is caller's responsibility to close the stream properly. 
	 * <p>
	 * The type of the backing storage (eg. RAM, file, socket, db, etc.) 
	 * associated with the returned output stream is implementation specific.
	 * <p>
	 * Important!: Everytime this method is called, the corresponding backing
	 * data is reinitialized and new instance of <code>OutputStream</code>
	 * is returned. In other words, do not expect identical <code>OutputStream</code>
	 * with state preserved across calls when calling this method multiple times.  
	 * That's not how it works. 
	 *  
	 * @return
	 * @throws UncheckedIOException
	 */
	public OutputStream getDefaultOutputStream() throws UncheckedIOException;
}
