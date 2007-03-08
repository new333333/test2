package com.sitescape.team;
import com.sitescape.team.exception.UncheckedCodedException;

public class InvalidArgumentException extends UncheckedCodedException {
    private static final String InvalidArgumentException_ErrorCode = "errorcode.invalid.argument";

    public InvalidArgumentException() {
        super(InvalidArgumentException_ErrorCode);
    }
    public InvalidArgumentException(String message) {
        super(InvalidArgumentException_ErrorCode,  new Object[0], message);
    }
    public InvalidArgumentException(String message, Throwable cause) {
        super(InvalidArgumentException_ErrorCode, new Object[0], message, cause);
    }
    public InvalidArgumentException(Throwable cause) {
        super(InvalidArgumentException_ErrorCode, new Object[0], cause);
    }
}