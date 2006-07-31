
package com.sitescape.ef.domain;

import com.sitescape.ef.exception.UncheckedCodedException;

/**
 * @author Janet McCann
 *
 */
public class TitleException extends UncheckedCodedException {
	private static final String TitleExistsException_ErrorCode = "errorcode.title";
    public TitleException(String title) {
        super(TitleExistsException_ErrorCode, new Object[]{title});
    }
    public TitleException(String title, String message) {
        super(TitleExistsException_ErrorCode,  new Object[]{title}, message);
    }
    public TitleException(String title, String message, Throwable cause) {
        super(TitleExistsException_ErrorCode, new Object[]{title}, message, cause);
    }
    public TitleException(String title, Throwable cause) {
        super(TitleExistsException_ErrorCode,  new Object[]{title}, cause);
    }
}
