package com.sitescape.ef.search;

import com.sitescape.ef.exception.UncheckedException;

/**
 * @author Jong Kim
 *
 */
public class LuceneException extends UncheckedException {
    public LuceneException() {
        super();
    }
    public LuceneException(String message) {
        super(message);
    }
    public LuceneException(String message, Throwable cause) {
        super(message, cause);
    }
    public LuceneException(Throwable cause) {
        super(cause);
    }
}
