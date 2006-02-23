package com.sitescape.ef.module.file;

import com.sitescape.ef.exception.UncheckedException;

public class WriteFilesException extends UncheckedException {
	
	private FileErrors errors;
	
	public WriteFilesException(FileErrors errors) {
		this.errors = errors;
	}
	
	public FileErrors getErrors() {
		return errors;
	}
}
