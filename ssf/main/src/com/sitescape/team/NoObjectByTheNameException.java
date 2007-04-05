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

/**
 * @author Jong Kim
 *
 */
public class NoObjectByTheNameException extends UncheckedCodedException {
    public NoObjectByTheNameException(String errorCode, String objName) {
        super(errorCode, new Object[] {objName});
    }
    public NoObjectByTheNameException(String errorCode, String objName, String message) {
        super(errorCode, new Object[] {objName}, message);
    }
    public NoObjectByTheNameException(String errorCode, String objName, String message, Throwable cause) {
        super(errorCode, new Object[] {objName}, message, cause);
    }
    public NoObjectByTheNameException(String errorCode, String objName, Throwable cause) {
        super(errorCode, new Object[] {objName}, cause);
    }
}
