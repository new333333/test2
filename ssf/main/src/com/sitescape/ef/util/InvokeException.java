package com.sitescape.ef.util;

import com.sitescape.ef.exception.UncheckedException;

/**
 *
 * @author Jong Kim
 */
public class InvokeException extends UncheckedException {
    public InvokeException() {
        super();
    }
    public InvokeException(String message) {
        super(message);
    }
    public InvokeException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvokeException(Throwable cause) {
        super(cause);
    }
}
