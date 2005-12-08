package com.sitescape.ef;

import com.sitescape.ef.exception.UncheckedException;

/**
 *
 * @author Jong Kim
 */
public class PropertyNotFoundException extends UncheckedException {
    public PropertyNotFoundException() {
        super();
    }
    public PropertyNotFoundException(String message) {
        super(message);
    }
    public PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public PropertyNotFoundException(Throwable cause) {
        super(cause);
    }
}
