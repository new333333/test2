package com.sitescape.team.util;

import com.sitescape.team.exception.UncheckedException;

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
