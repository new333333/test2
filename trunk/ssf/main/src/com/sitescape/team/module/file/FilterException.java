package com.sitescape.team.module.file;

import com.sitescape.team.exception.UncheckedCodedException;

public class FilterException extends UncheckedCodedException {

	private static final String FilterException_ErrorCode = "errorcode.content.filter";
	 
	public FilterException(String fileName) {
        super(FilterException_ErrorCode, new Object[]{fileName});
    }

}
