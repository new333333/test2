package com.sitescape.ef.util;

import com.sitescape.ef.ConfigurationException;

public class ConfigPropertyNotFoundException extends ConfigurationException {
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
