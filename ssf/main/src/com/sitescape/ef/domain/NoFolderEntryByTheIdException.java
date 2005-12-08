/*
 * Created on Oct 26, 2004
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
