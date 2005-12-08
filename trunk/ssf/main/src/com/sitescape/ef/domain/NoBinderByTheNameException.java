package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 *
 */
public class NoBinderByTheNameException extends NoObjectByTheNameException {
    private static final String NoBinderByTheNameException_ErrorCode = "errorcode.no.binder.by.the.name";
    
    public NoBinderByTheNameException(String userName) {
        super(NoBinderByTheNameException_ErrorCode, userName);
    }
    public NoBinderByTheNameException(String userName, String message) {
        super(NoBinderByTheNameException_ErrorCode, userName, message);
    }
    public NoBinderByTheNameException(String userName, String message, Throwable cause) {
        super(NoBinderByTheNameException_ErrorCode,userName, message, cause);
    }
    public NoBinderByTheNameException(String userName, Throwable cause) {
        super(NoBinderByTheNameException_ErrorCode, userName, cause);
    }
}
