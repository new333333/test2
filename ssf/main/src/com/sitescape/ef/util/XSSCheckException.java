package com.sitescape.ef.util;

import com.sitescape.ef.exception.CheckedCodedException;

public class XSSCheckException extends CheckedCodedException {

	private static final long serialVersionUID = 1L;
	
	private static final String XSSCheckException_ErrorCode = "errorcode.xss.check";

	public XSSCheckException() {
		super(XSSCheckException_ErrorCode);
	}
}
