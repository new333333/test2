package com.sitescape.ef.search.docbuilder.mapped;

import com.sitescape.ef.exception.UncheckedException;

/**
 * @author Jong Kim
 *
 */
public class MappingException extends UncheckedException {
    public MappingException() {
        super();
    }
    public MappingException(String message) {
        super(message);
    }
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
    public MappingException(Throwable cause) {
        super(cause);
    }
}
