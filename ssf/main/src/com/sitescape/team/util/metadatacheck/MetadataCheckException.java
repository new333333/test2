package com.sitescape.team.util.metadatacheck;

import com.sitescape.team.exception.UncheckedCodedException;

public class MetadataCheckException extends UncheckedCodedException {

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
