package com.sitescape.ef.web;

import com.sitescape.ef.exception.UncheckedCodedException;

public class NoValidUserSessionException extends UncheckedCodedException {

	private static final String NoValidUserSessionException_ErrorCode = "errorcode.no.valid.user.session";

	public NoValidUserSessionException() {
		super(NoValidUserSessionException_ErrorCode, new Object[] {});
	}

	public NoValidUserSessionException(Throwable cause) {
		super(NoValidUserSessionException_ErrorCode, new Object[] {}, cause);
	}

}
