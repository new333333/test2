/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
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