package com.sitescape.ef.security;

import com.sitescape.ef.exception.UncheckedException;

/**
 *
 * @author Jong Kim
 */
public class AccessControlException extends UncheckedException {
    
    public AccessControlException() {
        super();
    }
    public AccessControlException(String message) {
        super(message);
    }
    public AccessControlException(String message, Throwable cause) {
        super(message, cause);
    }
    public AccessControlException(Throwable cause) {
        super(cause);
    }
}
