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
