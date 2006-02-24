package com.sitescape.ef.module.file;

import com.sitescape.ef.exception.CheckedException;

public class WriteFilesException extends CheckedException {
	
	private FilesErrors errors;
	
	public WriteFilesException(FilesErrors errors) {
		this.errors = errors;
	}
	
	public FilesErrors getErrors() {
		return errors;
	}
}
