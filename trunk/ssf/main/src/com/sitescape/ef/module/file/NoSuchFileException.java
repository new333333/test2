package com.sitescape.ef.module.file;

import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.exception.UncheckedCodedException;

public class NoSuchFileException extends UncheckedCodedException {

	private static final String NoSuchFileException_ErrorCode = "errorcode.no.such.file";

	public NoSuchFileException(DefinableEntity entity, String fileName) {
		super(NoSuchFileException_ErrorCode, new Object[] { entity.getId(), fileName });
	}

}
