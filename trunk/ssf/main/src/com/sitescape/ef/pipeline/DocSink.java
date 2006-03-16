package com.sitescape.ef.pipeline;

import java.io.File;
import java.io.InputStream;
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
	 * Use the byte array as resource.
	 * 
	 * @param data
	 * @param isTextData if <code>true</code> the byte array represents
	 * text data and can be converted to charcters using charset. 
	 * if <code>false</code> the data is either non-text type (ie., binary)
	 * or its type is unknown.
	 * @param charsetName the name of a supported charset or <code>null</code>.
	 * if <code>isTextData</code> is <code>true</code>, this value must be
	 * specified; if <code>isTextData</code> is <code>false</code>, this 
	 * value is irrelevent therefore unused. 
	 */
	public void setByteArray(byte[] data, boolean isTextData, String charsetName);
	
	/**
	 * Use the string as resource.
	 * 
	 * @param data
	 * @param charsetName charset name used for encoding/decoding
	 */
	public void setString(String data, String charsetName);
	
	/**
	 * Use the file as resource. The value of transferOwnership specifies 
	 * whether it is the caller or the sink object that is responsible for the 
	 * disposal of the file resource after use.  
	 * 
	 * @param data
	 * @param transferOwnership if <code>true</code> the sink is responsible
	 * for deleting the file after processing is done. If <code>false</code>
	 * the caller is reponsible for the lifecycle of the file. 
	 * @param isTextData if <code>true</code> the file content represents
	 * text data and can be converted to charcters using charset. 
	 * if <code>false</code> the data is either non-text type (ie., binary)
	 * or its type is unknown.  
	 * @param charsetName the name of a supported charset or <code>null</code>.
	 * if <code>isTextData</code> is <code>true</code>, this value must be
	 * specified; if <code>isTextData</code> is <code>false</code>, this 
	 * value is irrelevent therefore unused. 
	 */
	public void setFile(File data, boolean transferOwnership, 
			boolean isTextData, String charsetName);
	
	/**
	 * Use the InputStream as resource. The caller is responsible for the 
	 * lifecycle of the resource the input stream is attached to.  
	 * 
	 * @param is
	 * @param isTextData
	 * @param charsetName
	 */
	public void setInputStream(InputStream is, boolean isTextData, String charsetName);
	
	/**
	 * Returns <code>OutputStream</code> into which to write.
	 * It is caller's responsibility to close the stream properly. 
	 * <p>
	 * The type of the backing resource (eg. RAM, file, socket, db, etc.) 
	 * associated with the returned output stream is implementation specific
	 * and the sink implementation is responsible for the lifecycle of 
	 * the backing resource.  
	 * <p>
	 * Important!: Everytime this method is called, the corresponding backing
	 * resource is reinitialized and new instance of <code>OutputStream</code>
	 * is returned. In other words, do not expect identical <code>OutputStream</code>
	 * with state preserved across calls when calling this method multiple times.  
	 * That's not how it works. 
	 *  
	 * @param isTextData if <code>true</code> the caller is indicating to the
	 * framework that the data it is about to put into the returned 
	 * <code>OutputStream</code> is text type data. if <code>false</code>,
	 * the caller is saying that the data to be streamed in is either 
	 * non-text type (i.e., binary) or its type is unknown.  
	 * @param charsetName the name of a supported charset or <code>null</code>.
	 * if <code>isTextData</code> is <code>true</code>, this value must be
	 * specified; if <code>isTextData</code> is <code>false</code>, this 
	 * value is irrelevent therefore unused. 
	 * @return
	 * @throws UncheckedIOException
	 */
	public OutputStream getBuiltinOutputStream(boolean isTextData, String charsetName) throws UncheckedIOException;
}
