package com.sitescape.ef;

import com.sitescape.team.exception.UncheckedCodedException;

/**
 * @author Jong Kim
 *
 */
public abstract class NoObjectByTheNameException extends UncheckedCodedException {
    public NoObjectByTheNameException(String errorCode, String objName) {
        super(errorCode, new Object[] {objName});
    }
    public NoObjectByTheNameException(String errorCode, String objName, String message) {
        super(errorCode, new Object[] {objName}, message);
    }
    public NoObjectByTheNameException(String errorCode, String objName, String message, Throwable cause) {
        super(errorCode, new Object[] {objName}, message, cause);
    }
    public NoObjectByTheNameException(String errorCode, String objName, Throwable cause) {
        super(errorCode, new Object[] {objName}, cause);
    }
}
