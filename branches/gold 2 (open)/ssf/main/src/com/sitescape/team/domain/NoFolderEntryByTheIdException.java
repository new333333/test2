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
/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import com.sitescape.team.NoObjectByTheIdException;


/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NoFolderEntryByTheIdException extends NoObjectByTheIdException {
    private static final String NoDocshareEntryByTheIdException_ErrorCode = "errorcode.no.folder.entry.by.the.id";
    
    public NoFolderEntryByTheIdException(Long entryId) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryId);
    }
    public NoFolderEntryByTheIdException(Long entryId, String message) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryId, message);
    }
    public NoFolderEntryByTheIdException(Long entryId, String message, Throwable cause) {
        super(NoDocshareEntryByTheIdException_ErrorCode,entryId, message, cause);
    }
    public NoFolderEntryByTheIdException(Long entryId, Throwable cause) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryId, cause);
    }
}
