package com.sitescape.ef.file;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.exception.UncheckedCodedException;

public class NoSuchFileException extends UncheckedCodedException {

	private static final String NoSuchFileException_ErrorCode = "errorcode.no.such.file";

	public NoSuchFileException(Entry entry, String fileName) {
		super(NoSuchFileException_ErrorCode, new Object[] { entry.getId(), fileName });
	}

}
