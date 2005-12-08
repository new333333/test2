package com.sitescape.ef.security;

import com.sitescape.ef.exception.UncheckedException;

/**
 * Same as <code>AccessControlException</code> except that this is not a
 * <i>coded</i> exception.
 * 
 * @author Jong Kim
 */
public class AccessControlNonCodedException extends UncheckedException {
    
    public AccessControlNonCodedException() {
        super();
    }
    public AccessControlNonCodedException(String message) {
        super(message);
    }
    public AccessControlNonCodedException(String message, Throwable cause) {
        super(message, cause);
    }
    public AccessControlNonCodedException(Throwable cause) {
        super(cause);
    }
}
