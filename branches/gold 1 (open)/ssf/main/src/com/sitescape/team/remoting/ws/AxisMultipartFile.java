/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.remoting.ws;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.util.FileHelper;

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
		return null;
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
