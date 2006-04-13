package com.sitescape.ef;
import com.sitescape.ef.exception.UncheckedCodedException;

public class NotSupportedException extends UncheckedCodedException {
    private static final String NotSupportedException_ErrorCode = "errorcode.not.supported";

    public NotSupportedException() {
        super(NotSupportedException_ErrorCode);
    }
    public NotSupportedException(String message) {
        super(NotSupportedException_ErrorCode,  new Object[0], message);
    }
    public NotSupportedException(String message, Throwable cause) {
        super(NotSupportedException_ErrorCode, new Object[0], message, cause);
    }
    public NotSupportedException(Throwable cause) {
        super(NotSupportedException_ErrorCode, new Object[0], cause);
    }
}