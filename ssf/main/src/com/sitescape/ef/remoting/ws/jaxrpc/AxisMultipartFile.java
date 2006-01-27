package com.sitescape.ef.remoting.ws.jaxrpc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.util.FileHelper;

/**
 * <code>MultipartFile</code> implementation for Apache Axis attachment support.
 * <p>
 * To be precise, this implemmentation doesn't actually represent an uploaded
 * file received in a multipart request. Instead, it acts as an adaptor so
 * as to provide application with the convenience of accessing uploaded files
 * through an uniform interface no matter how the files were uploaded.
 * Only a small subset of the methods in the interface are actually implemented
 * and the rest will throw an <code>UnsupportedOperationException</code>
 * exception if invoked. 
 * 
 * @author jong
 *
 */
public class AxisMultipartFile implements MultipartFile {

	private String fileName;
	private DataHandler dataHandler;
	private File file;
	private long size;
	
	public AxisMultipartFile(String fileName, DataHandler dataHandler) {
		this.fileName = fileName;
		this.dataHandler = dataHandler;
		this.file = new File(dataHandler.getName());
		this.size = file.length();
	}
	
	public String getName() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return (this.size == 0);
	}

	public String getOriginalFilename() {
		return fileName;
	}

	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	public long getSize() {
		return this.size;
	}

	// Only one of the following three methods can be invoked and at most once.
	
	public byte[] getBytes() throws IOException {
		return FileCopyUtils.copyToByteArray(getInputStream());
	}

	public InputStream getInputStream() throws IOException {
		return dataHandler.getInputStream();
	}

	public void transferTo(File dest) throws IOException, IllegalStateException {
		FileHelper.move(this.file, dest);
	}

}
