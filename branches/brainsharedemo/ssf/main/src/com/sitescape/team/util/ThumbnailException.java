package com.sitescape.team.util;

import com.sitescape.team.exception.UncheckedException;

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
