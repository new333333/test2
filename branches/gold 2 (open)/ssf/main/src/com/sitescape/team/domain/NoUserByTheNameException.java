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
public class NoUserByTheNameException extends NoObjectByTheNameException {
    private static final String NoUserByTheNameException_ErrorCode = "errorcode.no.user.by.the.name";
    
    public NoUserByTheNameException(String userName) {
        super(NoUserByTheNameException_ErrorCode, userName);
    }
    public NoUserByTheNameException(String userName, String message) {
        super(NoUserByTheNameException_ErrorCode, userName, message);
    }
    public NoUserByTheNameException(String userName, String message, Throwable cause) {
        super(NoUserByTheNameException_ErrorCode,userName, message, cause);
    }
    public NoUserByTheNameException(String userName, Throwable cause) {
        super(NoUserByTheNameException_ErrorCode, userName, cause);
    }
}
