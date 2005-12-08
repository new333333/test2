package com.sitescape.ef.security.authentication;

import com.sitescape.ef.exception.UncheckedException;

public class AuthenticationException extends UncheckedException {
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
