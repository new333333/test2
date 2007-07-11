package com.sitescape.team.util.stringcheck;

import com.sitescape.team.exception.UncheckedCodedException;

public class StringCheckException extends UncheckedCodedException {

	private static final long serialVersionUID = 1L;
	
	private static final String StringCheckException_ErrorCode = "errorcode.string.check.failed";

	public StringCheckException() {
		super(StringCheckException_ErrorCode);
	}
	
	public StringCheckException(String errorCode) {
		super(errorCode);
	}
	
	public StringCheckException(String errorCode, Object[] args) {
		super(errorCode, args);
	}
}
