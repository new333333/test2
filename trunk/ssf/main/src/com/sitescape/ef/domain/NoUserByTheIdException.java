/*
 * Created on Oct 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NoUserByTheIdException extends NoObjectByTheIdException {
    private static final String NoUserByTheIdException_ErrorCode = "error.no.user.by.the.id";
    
    public NoUserByTheIdException(Long userId) {
        super(NoUserByTheIdException_ErrorCode, userId);
    }
    public NoUserByTheIdException(Long userId, String message) {
        super(NoUserByTheIdException_ErrorCode, userId, message);
    }
    public NoUserByTheIdException(Long userId, String message, Throwable cause) {
        super(NoUserByTheIdException_ErrorCode,userId, message, cause);
    }
    public NoUserByTheIdException(Long userId, Throwable cause) {
        super(NoUserByTheIdException_ErrorCode, userId, cause);
    }

}

