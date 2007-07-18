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
