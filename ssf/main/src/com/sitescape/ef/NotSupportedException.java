package com.sitescape.ef;
import com.sitescape.team.exception.UncheckedCodedException;

public class NotSupportedException extends UncheckedCodedException {
 	private static final long serialVersionUID = 1L;
	private static final String NotSupportedException_ErrorCode = "errorcode.not.supported";

    public NotSupportedException() {
        super(NotSupportedException_ErrorCode);
    }
    public NotSupportedException(String message) {
        super(NotSupportedException_ErrorCode,  new Object[] {message});
    }
    public NotSupportedException(String message, Throwable cause) {
        super(NotSupportedException_ErrorCode, new Object[] {message}, cause);
    }
    public NotSupportedException(Throwable cause) {
        super(NotSupportedException_ErrorCode, new Object[0], cause);
    }
}