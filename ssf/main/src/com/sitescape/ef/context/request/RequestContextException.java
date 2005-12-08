package com.sitescape.ef.context.request;

import com.sitescape.ef.exception.UncheckedException;

/**
 *
 * @author Jong Kim
 */
public class RequestContextException extends UncheckedException {
    public RequestContextException() {
        super();
    }
    public RequestContextException(String message) {
        super(message);
    }
    public RequestContextException(String message, Throwable cause) {
        super(message, cause);
    }
    public RequestContextException(Throwable cause) {
        super(cause);
    }
}
