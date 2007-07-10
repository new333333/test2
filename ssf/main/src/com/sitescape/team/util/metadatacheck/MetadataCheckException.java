package com.sitescape.team.util.metadatacheck;

import com.sitescape.team.exception.CheckedCodedException;

public class MetadataCheckException extends CheckedCodedException {

	private static final long serialVersionUID = 1L;
	
	private static final String MetadataCheckException_ErrorCode = "errorcode.metadatacheck.validation.failed";

	public MetadataCheckException() {
		super(MetadataCheckException_ErrorCode);
	}
	
	public MetadataCheckException(String errorCode) {
		super(errorCode);
	}
	
	public MetadataCheckException(String errorCode, Object[] args) {
		super(errorCode, args);
	}
}
