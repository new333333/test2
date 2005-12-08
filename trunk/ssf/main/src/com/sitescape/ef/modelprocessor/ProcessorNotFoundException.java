package com.sitescape.ef.modelprocessor;

import com.sitescape.ef.ConfigurationException;

/**
 *
 * @author Jong Kim
 */
public class ProcessorNotFoundException extends ConfigurationException {
    public ProcessorNotFoundException() {
        super();
    }
    public ProcessorNotFoundException(String message) {
        super(message);
    }
    public ProcessorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ProcessorNotFoundException(Throwable cause) {
        super(cause);
    }
}
