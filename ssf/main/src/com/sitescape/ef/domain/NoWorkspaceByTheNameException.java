package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 *
 */
public class NoWorkspaceByTheNameException extends NoObjectByTheNameException {
    private static final String NoWorkspaceByTheNameException_ErrorCode = "errorcode.no.workspace.by.the.name";
    
    public NoWorkspaceByTheNameException(String workspaceName) {
        super(NoWorkspaceByTheNameException_ErrorCode, workspaceName);
    }
    public NoWorkspaceByTheNameException(String workspaceName, String message) {
        super(NoWorkspaceByTheNameException_ErrorCode, workspaceName, message);
    }
    public NoWorkspaceByTheNameException(String workspaceName, String message, Throwable cause) {
        super(NoWorkspaceByTheNameException_ErrorCode,workspaceName, message, cause);
    }
    public NoWorkspaceByTheNameException(String workspaceName, Throwable cause) {
        super(NoWorkspaceByTheNameException_ErrorCode, workspaceName, cause);
    }
}
