package com.sitescape.ef.security.authentication;

import com.sitescape.ef.exception.CheckedException;

public class AuthenticationException extends CheckedException {
    public AuthenticationException() {
        super();
    }
    public AuthenticationException(String message) {
        super(message);
    }
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
