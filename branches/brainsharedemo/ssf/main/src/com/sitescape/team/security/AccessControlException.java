package com.sitescape.team.security;

import com.sitescape.team.exception.UncheckedCodedException;

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
