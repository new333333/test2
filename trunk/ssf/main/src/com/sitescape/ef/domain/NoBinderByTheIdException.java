package com.sitescape.ef.domain;


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
