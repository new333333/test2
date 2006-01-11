package com.sitescape.ef.module.file;

import com.sitescape.ef.exception.UncheckedException;

public class FileException extends UncheckedException {
	
	public FileException() {
        super();
    }
    public FileException(String message) {
        super(message);
    }
    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
    public FileException(Throwable cause) {
        super(cause);
    }
}