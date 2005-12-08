/*
 * Created on Jun 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.docconverter;


import com.sitescape.ef.exception.UncheckedException;

/**
 * @author Roy Klein
 *
 */

public class DocConverterException extends UncheckedException {
    public DocConverterException() {
        super();
    }
    public DocConverterException(String message) {
        super(message);
    }
    public DocConverterException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocConverterException(Throwable cause) {
        super(cause);
    }
}
