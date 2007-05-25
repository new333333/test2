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
package com.sitescape.team.web;

import com.sitescape.team.exception.UncheckedCodedException;

public class UnauthenticatedAccessException extends UncheckedCodedException {

	private static final String UnauthenticatedAccessException_ErrorCode = "errorcode.unauthenticated.access";

	public UnauthenticatedAccessException() {
		super(UnauthenticatedAccessException_ErrorCode, new Object[] {});
	}

	public UnauthenticatedAccessException(Throwable cause) {
		super(UnauthenticatedAccessException_ErrorCode, new Object[] {}, cause);
	}

}
