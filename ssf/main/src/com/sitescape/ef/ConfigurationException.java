package com.sitescape.ef;

import com.sitescape.ef.exception.UncheckedException;

/**
 * @author Jong Kim
 *
 */
public class ConfigurationException extends UncheckedException {
    public ConfigurationException() {
        super();
    }
    public ConfigurationException(String message) {
        super(message);
    }
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
