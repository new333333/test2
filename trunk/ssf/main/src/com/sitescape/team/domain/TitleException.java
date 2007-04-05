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

import com.sitescape.team.exception.UncheckedCodedException;

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
