package com.sitescape.ef.util;

import com.sitescape.ef.exception.UncheckedException;

/**
 *
 * @author Jong Kim
 */
public class ObjectPropertyNotFoundException extends UncheckedException {
    public ObjectPropertyNotFoundException() {
        super();
    }
    public ObjectPropertyNotFoundException(String message) {
        super(message);
    }
    public ObjectPropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ObjectPropertyNotFoundException(Throwable cause) {
        super(cause);
    }
}
