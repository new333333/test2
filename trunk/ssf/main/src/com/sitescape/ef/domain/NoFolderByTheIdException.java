package com.sitescape.ef.domain;


/**
 * @author Jong Kim
 *
 */
public class NoFolderByTheIdException extends NoObjectByTheIdException {
    private static final String NoFolderByTheIdException_ErrorCode = "errorcode.no.folder.by.the.id";
    
    public NoFolderByTheIdException(Long folderId) {
        super(NoFolderByTheIdException_ErrorCode, folderId);
    }
    public NoFolderByTheIdException(Long folderId, String message) {
        super(NoFolderByTheIdException_ErrorCode, folderId, message);
    }
    public NoFolderByTheIdException(Long folderId, String message, Throwable cause) {
        super(NoFolderByTheIdException_ErrorCode,folderId, message, cause);
    }
    public NoFolderByTheIdException(Long folderId, Throwable cause) {
        super(NoFolderByTheIdException_ErrorCode, folderId, cause);
    }
}
