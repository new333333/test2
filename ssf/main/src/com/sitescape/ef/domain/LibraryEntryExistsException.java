
package com.sitescape.ef.domain;

import com.sitescape.ef.exception.UncheckedCodedException;

/**
 * @author Janet McCann
 *
 */
public class LibraryEntryExistsException extends UncheckedCodedException {
	private static final String FolderExistsException_ErrorCode = "errorcode.library.folderentry.exists";
    public LibraryEntryExistsException(String title) {
        super(FolderExistsException_ErrorCode, new Object[]{title});
    }
    public LibraryEntryExistsException(String title, String message) {
        super(FolderExistsException_ErrorCode,  new Object[]{title}, message);
    }
    public LibraryEntryExistsException(String title, String message, Throwable cause) {
        super(FolderExistsException_ErrorCode, new Object[]{title}, message, cause);
    }
    public LibraryEntryExistsException(String title, Throwable cause) {
        super(FolderExistsException_ErrorCode,  new Object[]{title}, cause);
    }
}
