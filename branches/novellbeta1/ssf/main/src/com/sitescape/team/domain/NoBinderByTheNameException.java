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
package com.sitescape.team.domain;

import com.sitescape.team.NoObjectByTheNameException;

/**
 * @author Jong Kim
 *
 */
public class NoBinderByTheNameException extends NoObjectByTheNameException {
    private static final String NoBinderByTheNameException_ErrorCode = "errorcode.no.binder.by.the.name";
    
    public NoBinderByTheNameException(String userName) {
        super(NoBinderByTheNameException_ErrorCode, userName);
    }
    public NoBinderByTheNameException(String userName, String message) {
        super(NoBinderByTheNameException_ErrorCode, userName, message);
    }
    public NoBinderByTheNameException(String userName, String message, Throwable cause) {
        super(NoBinderByTheNameException_ErrorCode,userName, message, cause);
    }
    public NoBinderByTheNameException(String userName, Throwable cause) {
        super(NoBinderByTheNameException_ErrorCode, userName, cause);
    }
}
