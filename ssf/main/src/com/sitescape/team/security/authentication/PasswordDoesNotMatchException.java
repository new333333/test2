package com.sitescape.team.security.authentication;

public class PasswordDoesNotMatchException extends AuthenticationException {
    public PasswordDoesNotMatchException() {
        super();
    }
    public PasswordDoesNotMatchException(String message) {
        super(message);
    }
    public PasswordDoesNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
    public PasswordDoesNotMatchException(Throwable cause) {
        super(cause);
    }

}
