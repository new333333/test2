package com.sitescape.ef.module.file;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.exception.CheckedCodedException;

public class CheckedOutByOtherException extends CheckedCodedException {
	
	private static final String CheckedOutByOtherException_ErrorCode = "errorcode.checked.out.by.other";
	 
		public CheckedOutByOtherException(Entry entry, String fileName, User user) {
	        super(CheckedOutByOtherException_ErrorCode, new Object[]{entry.getId(), fileName, user.getName()});
	    }
}
