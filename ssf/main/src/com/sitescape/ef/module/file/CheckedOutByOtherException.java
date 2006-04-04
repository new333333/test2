package com.sitescape.ef.module.file;

import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.exception.UncheckedCodedException;

public class CheckedOutByOtherException extends UncheckedCodedException {
	
	private static final String CheckedOutByOtherException_ErrorCode = "errorcode.checked.out.by.other";
	 
		public CheckedOutByOtherException(DefinableEntity entity, String fileName, User user) {
	        super(CheckedOutByOtherException_ErrorCode, new Object[]{entity.getId(), fileName, user.getName()});
	    }
}
