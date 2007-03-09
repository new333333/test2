package com.sitescape.team;

import com.sitescape.team.exception.UncheckedException;

/**
 * @author Jong Kim
 *
 */
public class InternalException extends UncheckedException {
    public InternalException() {
        super();
    }
    public InternalException(String message) {
        super(message);
    }
    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
    public InternalException(Throwable cause) {
        super(cause);
    }
}
