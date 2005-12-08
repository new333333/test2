/*
 * Created on Jul 14, 2005
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.web.util;
import com.sitescape.ef.exception.UncheckedException;

/**
 * @author billmers
 *
 */

public class DatepickerException extends UncheckedException {
    public DatepickerException() {
        super();
    }
    public DatepickerException(String message) {
        super(message);
    }
    public DatepickerException(String message, Throwable cause) {
        super(message, cause);
    }
    public DatepickerException(Throwable cause) {
        super(cause);
    }
}
