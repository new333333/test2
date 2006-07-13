
package com.sitescape.ef.domain;

import com.sitescape.ef.exception.UncheckedCodedException;

/**
 * @author Janet McCann
 *
 */
public class LibraryTitleException extends UncheckedCodedException {
	private static final String FileExistsException_ErrorCode = "errorcode.library.title";
    public LibraryTitleException(String title) {
        super(FileExistsException_ErrorCode, new Object[]{title});
    }
    public LibraryTitleException(String title, String message) {
        super(FileExistsException_ErrorCode,  new Object[]{title}, message);
    }
    public LibraryTitleException(String title, String message, Throwable cause) {
        super(FileExistsException_ErrorCode, new Object[]{title}, message, cause);
    }
    public LibraryTitleException(String title, Throwable cause) {
        super(FileExistsException_ErrorCode,  new Object[]{title}, cause);
    }
}
