package com.sitescape.ef.security.authentication;

public class UserDoesNotExistException extends AuthenticationException {
    public UserDoesNotExistException() {
        super();
    }
    public UserDoesNotExistException(String message) {
        super(message);
    }
    public UserDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserDoesNotExistException(Throwable cause) {
        super(cause);
    }

}
