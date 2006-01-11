package com.sitescape.ef.util;

import com.sitescape.ef.exception.UncheckedException;

public class ThumbnailException extends UncheckedException {
	
	public ThumbnailException() {
        super();
    }
    public ThumbnailException(String message) {
        super(message);
    }
    public ThumbnailException(String message, Throwable cause) {
        super(message, cause);
    }
    public ThumbnailException(Throwable cause) {
        super(cause);
    }

}
