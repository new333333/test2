package com.sitescape.ef.remoting.impl;

public class RemotingException extends RuntimeException {
    public RemotingException() {
        super();
    }
    public RemotingException(String message) {
        super(message);
    }
    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
    public RemotingException(Throwable cause) {
        super(cause);
    }
}
