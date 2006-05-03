package com.sitescape.ef.module.file;

import com.sitescape.ef.exception.UncheckedCodedException;

public class LockIdMismatchException extends UncheckedCodedException {

	private static final String LockIdMismatchException_ErrorCode = "errorcode.lock.id.mismatch";

	public LockIdMismatchException() {
		super(LockIdMismatchException_ErrorCode, new Object[] {});
	}
}
