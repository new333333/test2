package com.sitescape.ef.module.file;

import com.sitescape.ef.exception.UncheckedException;

public class WriteFilesException extends UncheckedException {
	
	private FilesErrors errors;
	
	public WriteFilesException(FilesErrors errors) {
		this.errors = errors;
	}
	
	public FilesErrors getErrors() {
		return errors;
	}
}
