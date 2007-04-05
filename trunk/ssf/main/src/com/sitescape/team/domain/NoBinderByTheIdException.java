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

import com.sitescape.team.NoObjectByTheIdException;


/**
 * @author Jong Kim
 *
 */
public class NoBinderByTheIdException extends NoObjectByTheIdException {
    private static final String NoBinderByTheIdException_ErrorCode = "errorcode.no.binder.by.the.id";
    
    public NoBinderByTheIdException(Long folderId) {
        super(NoBinderByTheIdException_ErrorCode, folderId);
    }
    public NoBinderByTheIdException(Long folderId, String message) {
        super(NoBinderByTheIdException_ErrorCode, folderId, message);
    }
    public NoBinderByTheIdException(Long folderId, String message, Throwable cause) {
        super(NoBinderByTheIdException_ErrorCode,folderId, message, cause);
    }
    public NoBinderByTheIdException(Long folderId, Throwable cause) {
        super(NoBinderByTheIdException_ErrorCode, folderId, cause);
    }
}
