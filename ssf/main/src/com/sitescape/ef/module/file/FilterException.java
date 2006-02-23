package com.sitescape.ef.module.file;

import com.sitescape.ef.exception.UncheckedException;

public class FilterException extends UncheckedException {

	public FilterException() {
        super();
    }
    public FilterException(String message) {
        super(message);
    }
    public FilterException(String message, Throwable cause) {
        super(message, cause);
    }
    public FilterException(Throwable cause) {
        super(cause);
    }
}
