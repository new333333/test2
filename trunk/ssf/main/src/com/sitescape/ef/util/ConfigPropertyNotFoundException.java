package com.sitescape.ef.util;

import com.sitescape.ef.exception.UncheckedException;

public class ConfigPropertyNotFoundException extends UncheckedException {
    public ConfigPropertyNotFoundException() {
        super();
    }
    public ConfigPropertyNotFoundException(String message) {
        super(message);
    }
    public ConfigPropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigPropertyNotFoundException(Throwable cause) {
        super(cause);
    }

}
