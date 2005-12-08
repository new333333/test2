package com.sitescape.ef.security;

import com.sitescape.ef.exception.UncheckedCodedException;

/**
 * Thrown to indicate that access is denied.
 * 
 * @author Jong Kim
 */
public abstract class AccessControlException extends UncheckedCodedException {
    public AccessControlException(String errorCode, Object[] errorArgs) {
    	super(errorCode, errorArgs);
    }
}
