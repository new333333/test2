package com.sitescape.ef.pipeline;

import com.sitescape.ef.exception.UncheckedException;

public class NoFileException extends UncheckedException {

	public NoFileException() {
        super();
    }
    public NoFileException(String message) {
        super(message);
    }
    public NoFileException(String message, Throwable cause) {
        super(message, cause);
    }
    public NoFileException(Throwable cause) {
        super(cause);
    }
}
