package com.sitescape.ef;

import java.io.IOException;

import com.sitescape.ef.exception.UncheckedException;

/**
 * Unchecked version of IOException.
 * 
 * @author jong
 *
 */
public class UncheckedIOException extends UncheckedException {

    public UncheckedIOException(IOException cause) {
        super(cause);
    }
}
