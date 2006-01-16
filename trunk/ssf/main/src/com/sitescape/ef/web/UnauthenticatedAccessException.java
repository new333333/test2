package com.sitescape.ef.web;

import com.sitescape.ef.exception.UncheckedCodedException;

public class UnauthenticatedAccessException extends UncheckedCodedException {

	private static final String UnauthenticatedAccessException_ErrorCode = "errorcode.unauthenticated.access";

	public UnauthenticatedAccessException() {
		super(UnauthenticatedAccessException_ErrorCode, new Object[] {});
	}

	public UnauthenticatedAccessException(Throwable cause) {
		super(UnauthenticatedAccessException_ErrorCode, new Object[] {}, cause);
	}

}
