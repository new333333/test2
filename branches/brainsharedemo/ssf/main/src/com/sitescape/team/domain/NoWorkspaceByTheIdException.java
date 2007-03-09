package com.sitescape.team.domain;

import com.sitescape.team.NoObjectByTheIdException;

/**
 * @author Jong Kim
 *
 */
public class NoWorkspaceByTheIdException extends NoObjectByTheIdException {
    private static final String NoWorkspaceByTheIdException_ErrorCode = "errorcode.no.workspace.by.the.id";
    
    public NoWorkspaceByTheIdException(Long workspaceId) {
        super(NoWorkspaceByTheIdException_ErrorCode, workspaceId);
    }
    public NoWorkspaceByTheIdException(Long workspaceId, String message) {
        super(NoWorkspaceByTheIdException_ErrorCode, workspaceId, message);
    }
    public NoWorkspaceByTheIdException(Long workspaceId, String message, Throwable cause) {
        super(NoWorkspaceByTheIdException_ErrorCode,workspaceId, message, cause);
    }
    public NoWorkspaceByTheIdException(Long workspaceId, Throwable cause) {
        super(NoWorkspaceByTheIdException_ErrorCode, workspaceId, cause);
    }
}
