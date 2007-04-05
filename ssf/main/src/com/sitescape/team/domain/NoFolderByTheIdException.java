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
