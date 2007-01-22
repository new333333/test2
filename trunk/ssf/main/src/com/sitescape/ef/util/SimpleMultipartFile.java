package com.sitescape.ef.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

public class SimpleMultipartFile implements MultipartFile {

	protected String fileName;
	// Only one of the following two is set per instance.
	protected File file;
	protected InputStream content;

	public SimpleMultipartFile(String fileName, InputStream content) {
		this.fileName = fileName;
		this.content = content;
	}
	
	public SimpleMultipartFile(String fileName, File file) {
		this.fileName = fileName;
		this.file = file;
	}
	
	public String getName() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public String getOriginalFilename() {
		return fileName;
	}

	public String getContentType() {
		return null;
	}

	/**
	 * Return the size of the file in bytes.
	 * <p>
	 * Note: This method overrides/extends the semantics of the interface 
	 * it is implementing. The interface does not specify what to return
	 * when the size is unknown. In other word, it assumes that the size
	 * is always computable. This particular implementation extends the
	 * semantics by allowing for return value of -1 (or any negative 
	 * long value) to indicate to the caller that the size is unknown.
	 * In purely OO sense, this is a violation of the contract defined
	 * by the interface, hence very bad programming practice that should be
	 * avoided whenever possible. However, the cost of avoiding this
	 * hack is too high for our project. So I think it justifies this
	 * small wrinkle (we are not living in an ideal world). 
	 */
	public long getSize() {
		if(file != null)
			return file.length();
		else
			return -1;
	}

	public byte[] getBytes() throws IOException {
		if(file != null)
			return FileCopyUtils.copyToByteArray(file);
		else
			return FileCopyUtils.copyToByteArray(content);
	}

	public InputStream getInputStream() throws IOException {
		if(file != null)
			return new BufferedInputStream(new FileInputStream(file));
		else 
			return content;
	}

	public void transferTo(File dest) throws IOException, IllegalStateException {
		if (dest.exists() && !dest.delete()) {
			throw new IOException(
					"Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
		}
		
		if(file != null) {
			FileCopyUtils.copy(file, dest);
		}
		else {
			FileCopyUtils.copy(content, new BufferedOutputStream(new FileOutputStream(dest)));
		}
	}
}
