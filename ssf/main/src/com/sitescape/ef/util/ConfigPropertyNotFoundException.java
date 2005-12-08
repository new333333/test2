package com.sitescape.ef.util;

import com.sitescape.ef.exception.UncheckedException;

public class ConfigPropsUtilException extends UncheckedException {
    public ConfigPropsUtilException() {
        super();
    }
    public ConfigPropsUtilException(String message) {
        super(message);
    }
    public ConfigPropsUtilException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigPropsUtilException(Throwable cause) {
        super(cause);
    }

}
