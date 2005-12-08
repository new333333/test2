package com.sitescape.ef.search.docbuilder.mapped;

import com.sitescape.ef.exception.UncheckedException;

/**
 * @author Jong Kim
 *
 */
public class ClassNotMappedException extends UncheckedException {
    public ClassNotMappedException() {
        super();
    }
    public ClassNotMappedException(String message) {
        super(message);
    }
    public ClassNotMappedException(String message, Throwable cause) {
        super(message, cause);
    }
    public ClassNotMappedException(Throwable cause) {
        super(cause);
    }
}
