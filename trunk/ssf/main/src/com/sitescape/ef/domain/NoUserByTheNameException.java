package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 *
 */
public class NoUserByTheNameException extends NoObjectByTheNameException {
    private static final String NoUserByTheNameException_ErrorCode = "errorcode.no.user.by.the.name";
    
    public NoUserByTheNameException(String userName) {
        super(NoUserByTheNameException_ErrorCode, userName);
    }
    public NoUserByTheNameException(String userName, String message) {
        super(NoUserByTheNameException_ErrorCode, userName, message);
    }
    public NoUserByTheNameException(String userName, String message, Throwable cause) {
        super(NoUserByTheNameException_ErrorCode,userName, message, cause);
    }
    public NoUserByTheNameException(String userName, Throwable cause) {
        super(NoUserByTheNameException_ErrorCode, userName, cause);
    }
}
